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

import java.text.DecimalFormat;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.cheatsheets.ICheatSheetAction;
import org.eclipse.ui.cheatsheets.ICheatSheetManager;
import org.eclipse.ui.part.FileEditorInput;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.helper.resourcehelper.AnaResourceFileHelper;
import org.talend.dataprofiler.core.ui.editor.AbstractMetadataFormPage;
import org.talend.dataprofiler.core.ui.editor.analysis.AnalysisEditor;
import org.talend.dataprofiler.core.ui.editor.analysis.ColumnMasterDetailsPage;
import org.talend.dataprofiler.core.ui.editor.analysis.ConnectionMasterDetailsPage;
import org.talend.dataprofiler.core.ui.views.DQRespositoryView;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dq.analysis.AnalysisExecutorSelector;
import org.talend.utils.sugars.ReturnCode;

/**
 * DOC zqin class global comment. Detailled comment <br/>
 * 
 * $Id: talend.epf 1 2006-09-29 17:06:40Z zqin $
 * 
 */
public class RunAnalysisAction extends Action implements ICheatSheetAction {

    private static Logger log = Logger.getLogger(RunAnalysisAction.class);

    private static final DecimalFormat FORMAT_SECONDS = new DecimalFormat("0.00");

    private TreeViewer treeViewer;

    private IFile currentSelection;

    private Analysis analysis = null;

    private AbstractMetadataFormPage page;

    public RunAnalysisAction() {
        super("Run");
        setImageDescriptor(ImageLib.getImageDescriptor(ImageLib.REFRESH_IMAGE));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.action.Action#run()
     */
    @Override
    public void run() {

        IEditorPart activeEditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
        if (activeEditor instanceof AnalysisEditor) {
            AnalysisEditor editor = (AnalysisEditor) activeEditor;

            if (editor != null) {
                switch (editor.getAnalysisType()) {
                case MULTIPLE_COLUMN:
                    page = (ColumnMasterDetailsPage) editor.getMasterPage();
                    break;
                case CONNECTION:
                    page = (ConnectionMasterDetailsPage) editor.getMasterPage();
                    break;
                default:
                }
            }

            if (page != null && page.isDirty()) {
                page.doSave(null);
            }

            editor.setRefreshResultPage(true);
        }

        if (currentSelection == null) {

            if (page == null) {
                return;
            }

            IFile file = ((FileEditorInput) page.getEditorInput()).getFile();
            if (file.getName().endsWith(PluginConstant.ANA_SUFFIX)) {
                analysis = AnaResourceFileHelper.getInstance().findAnalysis(file);
            }
        } else {

            if (currentSelection.getName().endsWith(PluginConstant.ANA_SUFFIX)) {
                analysis = AnaResourceFileHelper.getInstance().findAnalysis(currentSelection);
            }
        }

        if (analysis == null) {
            return;
        }

        final Analysis finalAnalysis = analysis;

        final WorkspaceJob job = new WorkspaceJob("Run Analysis") {

            @Override
            public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {

                monitor.beginTask("Running the [" + analysis.getName() + "].....", IProgressMonitor.UNKNOWN);

                // MOD scorreia 2008-08-19 factorized code with AnalysisExecutorSelector
                final ReturnCode executed = AnalysisExecutorSelector.executeAnalysis(finalAnalysis);
                monitor.done();
                AnaResourceFileHelper.getInstance().save(finalAnalysis);

                if (page != null) {
                    Display.getDefault().asyncExec(new Runnable() {

                        public void run() {
                            if (page instanceof ColumnMasterDetailsPage && page.isActive()) {
                                ColumnMasterDetailsPage columnMasterPage = (ColumnMasterDetailsPage) page;
                                columnMasterPage.refreshChart();
                            } else if (page instanceof ConnectionMasterDetailsPage) {
                                ConnectionMasterDetailsPage connDetailsPage = (ConnectionMasterDetailsPage) page;
                                connDetailsPage.doSetInput();
                            }
                        }
                    });
                }
                if (executed.isOk()) {
                    if (log.isInfoEnabled()) {
                        int executionDuration = analysis.getResults().getResultMetadata().getExecutionDuration();
                        log.info("Analysis \"" + finalAnalysis.getName() + "\" execution code: " + executed + ". Duration: "
                                + FORMAT_SECONDS.format(Double.valueOf(executionDuration) / 1000) + " s.");
                    }
                    return Status.OK_STATUS;
                } else {
                    int executionDuration = analysis.getResults().getResultMetadata().getExecutionDuration();
                    log.warn("Analysis \"" + finalAnalysis.getName() + "\" execution code: " + executed + ". Duration: "
                            + FORMAT_SECONDS.format(Double.valueOf(executionDuration) / 1000) + " s.");
                    // open error dialog
                    Display.getDefault().asyncExec(new Runnable() {

                        public void run() {
                            MessageDialogWithToggle.openError(null, "Run analysis error", "Fail to run this analysis: \""
                                    + finalAnalysis.getName() + "\". Error message:" + executed.getMessage());
                        }
                    });
                    return Status.CANCEL_STATUS;
                }
            }

        };

        job.setUser(true);
        job.schedule();

        if (treeViewer == null) {
            DQRespositoryView view = (DQRespositoryView) CorePlugin.getDefault().findView(PluginConstant.DQ_VIEW_ID);
            view.getCommonViewer().refresh();
        } else {
            CorePlugin.getDefault().refreshWorkSpace();
            treeViewer.refresh();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.cheatsheets.ICheatSheetAction#run(java.lang.String[],
     * org.eclipse.ui.cheatsheets.ICheatSheetManager)
     */
    public void run(String[] params, ICheatSheetManager manager) {
        run();
    }

    /**
     * Sets the currentSelection.
     * 
     * @param currentSelection the currentSelection to set
     */
    public void setCurrentSelection(IFile currentSelection) {
        this.currentSelection = currentSelection;
    }

    /**
     * Sets the treeViewer.
     * 
     * @param treeViewer the treeViewer to set
     */
    public void setTreeViewer(TreeViewer treeViewer) {
        this.treeViewer = treeViewer;
    }

}
