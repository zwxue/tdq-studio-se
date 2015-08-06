// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dq.helper;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.common.util.EList;
import org.talend.core.model.properties.FolderItem;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.dataquality.helpers.ReportHelper;
import org.talend.dataquality.helpers.ReportHelper.ReportType;
import org.talend.dataquality.properties.TDQReportItem;
import org.talend.dataquality.reports.AnalysisMap;
import org.talend.dataquality.reports.TdReport;
import org.talend.dq.nodes.ReportRepNode;
import org.talend.repository.model.IRepositoryNode;
import org.talend.resource.EResourceConstant;
import org.talend.utils.sugars.ReturnCode;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC qiongli class global comment. Detailled comment <br/>
 * 
 * $Id: talend.epf 55206 2011-02-15 17:32:14Z mhirt $
 * 
 */
public final class DQDeleteHelper {

    private static Logger log = Logger.getLogger(DQDeleteHelper.class);

    private DQDeleteHelper() {
    }

    /**
     * physical delete related.
     * 
     * @param item
     */
    public static ReturnCode deleteRelations(Item item) {
        ReturnCode rc = new ReturnCode(Boolean.TRUE);
        if (item == null || item.getProperty() == null || item instanceof FolderItem) {
            rc.setOk(Boolean.FALSE);
            return rc;
        }

        IFile itemFile = PropertyHelper.getItemFile(item.getProperty());
        // if file is null or this file is not physical deleted,do nothing.
        if (itemFile == null || itemFile.exists()) {
            rc.setOk(Boolean.FALSE);
            return rc;
        }
        if (item instanceof TDQReportItem) {
            try {
                rc = ReportUtils.deleteRepOutputFolder(itemFile);
            } catch (Exception e) {
                log.error(e);
                rc.setMessage(e.getMessage());
                rc.setOk(false);
            }
            return rc;
        }
        return rc;
    }

    /**
     * 
     * if these items in recycle bin are depended by others which is not in recycle bin,show a warning and return.
     * 
     * @param allNodes
     * @param isCurrentPerspectiveDQ
     * @return these list will be used to pop a dialog and display the detail nodes which are depended by others.
     */
    public static List<IRepositoryNode> getCanNotDeletedNodes(List<IRepositoryNode> allNodes, boolean isCurrentPerspectiveDQ) {
        List<IRepositoryNode> canNotDeletedNodes = new ArrayList<IRepositoryNode>();
        if (allNodes == null) {
            return canNotDeletedNodes;
        }

        for (IRepositoryNode node : allNodes) {
            List<ModelElement> dependencies = EObjectHelper.getDependencyClients(node);
            if (ERepositoryObjectType.METADATA_CON_TABLE.equals(node.getObjectType())
                    || ERepositoryObjectType.METADATA_CON_VIEW.equals(node.getObjectType())) {
                dependencies = EObjectHelper.getFirstDependency(node);
            }

            if (dependencies == null || dependencies.isEmpty()) {
                continue;
            }
            // if the current perspective is not DQ,no need to judge its client dependences are in recycle bin.
            if (!isCurrentPerspectiveDQ) {
                canNotDeletedNodes.add(node);
                continue;
            }
            for (ModelElement mod : dependencies) {
                Property property = PropertyHelper.getProperty(mod);
                if (property == null) {
                    continue;
                }
                Item item = property.getItem();
                if (item != null && !item.getState().isDeleted()) {
                    canNotDeletedNodes.add(node);
                }
            }
        }
        return canNotDeletedNodes;
    }

    /**
     * Go throught all reports in the project and return all which used the current jrxml.
     * 
     * @param node the Jrxml node
     * @return list of reports who used this jrxml as user defined template
     */
    public static List<ModelElement> getDependedReportOfJrxml(IRepositoryNode node) {
        if (node.getObject().getProperty() == null) {
            return null;
        }
        IPath path = PropertyHelper.getItemPath(node.getObject().getProperty());
        // check if it has depended Report
        List<ModelElement> dependedReport = new ArrayList<ModelElement>();
        // get all reports
        List<ReportRepNode> repNodes = RepositoryNodeHelper.getReportRepNodes(
                RepositoryNodeHelper.getDataProfilingFolderNode(EResourceConstant.REPORTS), true, true);
        // go through every report to find if any one used current jrxml
        for (ReportRepNode report : repNodes) {
            EList<AnalysisMap> analysisMap = ((TdReport) report.getReport()).getAnalysisMap();
            for (AnalysisMap anaMap : analysisMap) {
                if (isUsedByDeletedJrxml(path, anaMap)) {
                    dependedReport.add(report.getReport());
                    break;
                }
            }
        }
        return dependedReport;
    }

    /**
     * check if the anaMap comtains the Jrxml or not, by compare the jrxml's path with anaMap's jrxml source(when user
     * mode)
     * 
     * @param path the path of the jrxml saved in the analysis map
     * @param anaMap the analysis map in the report.
     * @return the analysis map used the current jrxml or not.
     */
    private static boolean isUsedByDeletedJrxml(IPath path, AnalysisMap anaMap) {
        ReportType reportType = ReportHelper.ReportType.getReportType(anaMap.getAnalysis(), anaMap.getReportType());
        // compare the Jrxml path if the report has the user defined one.
        if (ReportHelper.ReportType.USER_MADE.equals(reportType)) {
            String jrxmlPath = anaMap.getJrxmlSource();
            String deletedpath = path.removeFirstSegments(2).toString();
            if (jrxmlPath.contains(deletedpath)) {
                return true;
            }
        }
        return false;
    }
}
