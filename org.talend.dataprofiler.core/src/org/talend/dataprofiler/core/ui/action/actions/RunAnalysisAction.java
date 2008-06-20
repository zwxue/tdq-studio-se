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

import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.cheatsheets.ICheatSheetAction;
import org.eclipse.ui.cheatsheets.ICheatSheetManager;
import org.eclipse.ui.part.FileEditorInput;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.exception.ExceptionHandler;
import org.talend.dataprofiler.core.helper.AnaResourceFileHelper;
import org.talend.dataprofiler.core.ui.editor.analysis.AnalysisEditor;
import org.talend.dataprofiler.core.ui.editor.analysis.ColumnMasterDetailsPage;
import org.talend.dataprofiler.core.ui.progress.ProgressUI;
import org.talend.dataprofiler.core.ui.views.DQRespositoryView;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.analysis.AnalysisType;
import org.talend.dataquality.helpers.AnalysisHelper;
import org.talend.dq.analysis.AnalysisExecutor;
import org.talend.dq.analysis.ColumnAnalysisExecutor;
import org.talend.dq.analysis.ColumnAnalysisSqlExecutor;
import org.talend.dq.analysis.ConnectionAnalysisExecutor;
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

        if (currentSelection == null) {
            AnalysisEditor editor = (AnalysisEditor) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
                    .getActiveEditor();
            if (editor != null) {
                ColumnMasterDetailsPage page = (ColumnMasterDetailsPage) editor.getMasterPage();
                FileEditorInput input = (FileEditorInput) page.getEditorInput();
                if (page.isDirty()) {
                    try {
                        page.saveAnalysis();
                    } catch (Exception e) {
                        ExceptionHandler.process(e);
                    }
                    
                }
                IFile file = input.getFile();
                if (file.getName().endsWith(PluginConstant.ANA_SUFFIX)) {
                    analysis = AnaResourceFileHelper.getInstance().findAnalysis(file);
                }
            }

        } else {

            if (currentSelection.getName().endsWith(PluginConstant.ANA_SUFFIX)) {
                analysis = AnaResourceFileHelper.getInstance().findAnalysis(currentSelection);
            }
        }

        AnalysisType analysisType = AnalysisHelper.getAnalysisType(analysis);
        AnalysisExecutor exec = null;
        switch (analysisType) {
        case MULTIPLE_COLUMN:
            exec = new ColumnAnalysisSqlExecutor();
            break;
        case CONNECTION:
            exec = new ConnectionAnalysisExecutor();
            break;
        default:
            exec = new ColumnAnalysisExecutor();
        }

        final Analysis finalAnalysis = analysis;
        final AnalysisExecutor finalExec = exec;
        final Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        IRunnableWithProgress op = new IRunnableWithProgress() {

            public void run(IProgressMonitor monitor) throws InvocationTargetException {
                final ReturnCode executed = finalExec.execute(finalAnalysis);
                if (executed.isOk()) {
                    if (log.isInfoEnabled()) {
                        int executionDuration = analysis.getResults().getResultMetadata().getExecutionDuration();
                        log.info("Analysis \"" + finalAnalysis.getName() + "\" execution code: " + executed + ". Duration: "
                                + FORMAT_SECONDS.format(Double.valueOf(executionDuration) / 1000) + " s.");
                    }
                    AnaResourceFileHelper.getInstance().save(finalAnalysis);
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
                }

            }
        };
        try {
            ProgressUI.popProgressDialog(op, shell);
            if (treeViewer == null) {
                DQRespositoryView view = (DQRespositoryView) CorePlugin.getDefault().findView(PluginConstant.DQ_VIEW_ID);
                view.getCommonViewer().refresh();
            } else {
                CorePlugin.getDefault().refreshWorkSpace();
                treeViewer.refresh();
            }
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
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
