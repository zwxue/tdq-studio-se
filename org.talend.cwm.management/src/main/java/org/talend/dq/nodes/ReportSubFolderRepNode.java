// ============================================================================
//
// Copyright (C) 2006-2010 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dq.nodes;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.properties.TDQReportItem;
import org.talend.dataquality.reports.AnalysisMap;
import org.talend.dataquality.reports.TdReport;
import org.talend.dq.helper.RepositoryNodeHelper;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.RepositoryNode;
import orgomg.cwm.foundation.businessinformation.Document;

/**
 * DOC klliu class global comment. Detailled comment
 */
public class ReportSubFolderRepNode extends RepositoryNode {

    private List<IRepositoryNode> anaElement;

    private EList<AnalysisMap> analysisMaps;

    /**
     * DOC klliu ReportSubFolderRepNode constructor comment.
     * 
     * @param object
     * @param parent
     * @param type
     */
    public ReportSubFolderRepNode(IRepositoryViewObject object, RepositoryNode parent, ENodeType type) {
        super(object, parent, type);
    }

    /*
     * (non-Jsdoc)
     * 
     * @see org.talend.repository.model.RepositoryNode#getChildren()
     */
    @Override
    public List<IRepositoryNode> getChildren() {
        anaElement = new ArrayList<IRepositoryNode>();
        IRepositoryViewObject reportViewObject = this.getObject();
        if (reportViewObject == null) {
            ReportRepNode parent = (ReportRepNode) this.getParent();
            TDQReportItem reportItem = (TDQReportItem) parent.getObject().getProperty().getItem();
            TdReport report = (TdReport) reportItem.getReport();
            if (this.getProperties(EProperties.LABEL).equals(parent.ANA_FLODER)) {
                // EList<AnalysisMap> analysisMaps = report.getAnalysisMap();
                for (AnalysisMap analysisMap : analysisMaps) {
                    Analysis analysis = analysisMap.getAnalysis();
                    RepositoryNode recursiveFind = RepositoryNodeHelper.recursiveFind(analysis);
                    IRepositoryViewObject viewObject = recursiveFind.getObject();
                    AnalysisRepNode anaNode = new AnalysisRepNode(viewObject, this, ENodeType.REPOSITORY_ELEMENT);
                    anaNode.setProperties(EProperties.LABEL, ERepositoryObjectType.TDQ_ANALYSIS_ELEMENT);
                    anaNode.setProperties(EProperties.CONTENT_TYPE, ERepositoryObjectType.TDQ_ANALYSIS_ELEMENT);
                    viewObject.setRepositoryNode(anaNode);
                    anaElement.add(anaNode);
                }
            } else {
                EList<Document> documents = report.getDocument();
                for (Document document : documents) {
                    // FIXME need to add document node
                }
            }
            return anaElement;

        }
        return super.getChildren();
    }

    public String getCount() {
        IRepositoryViewObject reportViewObject = this.getObject();
        if (reportViewObject == null) {
            ReportRepNode parent = (ReportRepNode) this.getParent();
            TDQReportItem reportItem = (TDQReportItem) parent.getObject().getProperty().getItem();
            TdReport report = (TdReport) reportItem.getReport();
            if (this.getProperties(EProperties.LABEL).equals(parent.ANA_FLODER)) {
                analysisMaps = report.getAnalysisMap();
            } else {
                return "(" + 0 + ")";
            }
        }
        return "(" + analysisMaps.size() + ")";
    }

    @Override
    public String getLabel() {
        if (this.getObject() != null) {
            this.getObject().getLabel();
        }
        return super.getLabel();
    }
}
