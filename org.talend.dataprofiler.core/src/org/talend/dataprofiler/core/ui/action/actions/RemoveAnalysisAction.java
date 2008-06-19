// ============================================================================
//
// Copyright (C) 2006-2007 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.action.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.talend.commons.emf.EMFUtil;
import org.talend.cwm.dependencies.DependenciesHandler;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.helper.RepResourceFileHelper;
import org.talend.dataprofiler.core.manager.DQStructureManager;
import org.talend.dataprofiler.core.ui.views.DQRespositoryView;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.helpers.ReportHelper;
import org.talend.dataquality.reports.TdReport;

/**
 * DOC rli RemoveAnalysisActionProvider class global comment. Detailled comment
 */
public class RemoveAnalysisAction extends Action {

    private static Logger log = Logger.getLogger(RemoveAnalysisAction.class);

    public RemoveAnalysisAction() {
        super("Remove Analysis");
        setImageDescriptor(ImageLib.getImageDescriptor(ImageLib.ACTION_DELETE));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.action.Action#run()
     */
    @Override
    public void run() {
        DQRespositoryView findView = (DQRespositoryView) CorePlugin.getDefault().findView(DQRespositoryView.ID);
        TreeSelection treeSelection = (TreeSelection) findView.getCommonViewer().getSelection();
        TreePath[] paths = treeSelection.getPaths();
        TdReport parentReport;
        List<Analysis> analysisList;
        Analysis analysisObj = null;
        Map<TdReport, List<Analysis>> removeMap = new HashMap<TdReport, List<Analysis>>();
        for (int i = 0; i < paths.length; i++) {
            Object lastSegment = paths[i].getLastSegment();
            if (!(lastSegment instanceof Analysis)) {
                return;
            }
            analysisObj = (Analysis) lastSegment;
            IFile fileSegment = (IFile) paths[i].getSegment(paths[i].getSegmentCount() - 2);
            parentReport = RepResourceFileHelper.getInstance().findReport(fileSegment);
            analysisList = removeMap.get(parentReport);
            if (analysisList == null) {
                analysisList = new ArrayList<Analysis>();
                analysisList.add(analysisObj);
                removeMap.put(parentReport, analysisList);
            } else {
                analysisList.add(analysisObj);
            }
        }
        if (analysisObj == null) {
            return;
        }
        String message = paths.length > 1 ? "Are you sure you want to delete these " + paths.length + " elements?"
                : "Are you sure you want to remove analysis '" + analysisObj.getName() + "' from this report?";
        boolean openConfirm = MessageDialog.openConfirm(null, "Confirm Resource Delete", message);

        if (openConfirm) {
            Iterator<TdReport> iterator = removeMap.keySet().iterator();
            while (iterator.hasNext()) {
                TdReport report = iterator.next();
                ReportHelper.removeAnalyses(report, removeMap.get(report));
                RepResourceFileHelper.getInstance().save(report);

                // save now modified resources (that contain the Dependency objects)
                List<Resource> modifiedResources = DependenciesHandler.getInstance().clearDependencies(report);
                EMFUtil util = EMFUtil.getInstance();
                for (int i = 0; i < modifiedResources.size(); i++) {
                    util.saveSingleResource(modifiedResources.get(i));
                }
            }

            IFolder reportsFolder = ResourcesPlugin.getWorkspace().getRoot().getProject(DQStructureManager.DATA_PROFILING)
                    .getFolder(DQStructureManager.REPORTS);
            try {
                reportsFolder.refreshLocal(IResource.DEPTH_INFINITE, null);
            } catch (CoreException e) {
                e.printStackTrace();
            }
            findView.getCommonViewer().refresh();
        }
    }
}
