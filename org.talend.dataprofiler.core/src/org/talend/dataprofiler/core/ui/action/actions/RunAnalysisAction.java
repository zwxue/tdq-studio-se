// ============================================================================
//
// Copyright (C) 2006-2009 Talend Inc. - www.talend.com
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.cheatsheets.ICheatSheetAction;
import org.eclipse.ui.cheatsheets.ICheatSheetManager;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.part.FileEditorInput;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.IRuningStatusListener;
import org.talend.dataprofiler.core.ui.editor.analysis.AbstractAnalysisMetadataPage;
import org.talend.dataprofiler.core.ui.editor.analysis.AnalysisEditor;
import org.talend.dataprofiler.core.ui.views.DQRespositoryView;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dq.helper.resourcehelper.AnaResourceFileHelper;
import org.talend.utils.sugars.ReturnCode;

/**
 * DOC zqin class global comment. Detailled comment <br/>
 * 
 * $Id: talend.epf 1 2006-09-29 17:06:40Z zqin $
 * 
 */
public class RunAnalysisAction extends Action implements ICheatSheetAction {

    private static Logger log = Logger.getLogger(RunAnalysisAction.class);

    private static final DecimalFormat FORMAT_SECONDS = new DecimalFormat("0.00"); //$NON-NLS-1$

    private Analysis analysis = null;

    private IRuningStatusListener listener;

    private IFile selectionFile;

    public IFile getSelectionFile() {
        return selectionFile;
    }

    public void setSelectionFile(IFile selectionFile) {
        this.selectionFile = selectionFile;
    }

    public RunAnalysisAction() {
        super("run"); //$NON-NLS-1$
        setImageDescriptor(ImageLib.getImageDescriptor(ImageLib.REFRESH_IMAGE));
    }

    public RunAnalysisAction(IRuningStatusListener listener) {
        this();
        this.listener = listener;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.action.Action#run()
     */
    @Override
    public void run() {

        if (getSelectionFile() == null) {
            IEditorPart editor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
            if (editor != null && editor instanceof AnalysisEditor) {
                AnalysisEditor anaEditor = (AnalysisEditor) editor;
                IFormPage masterPage = anaEditor.getMasterPage();
                editor.doSave(null);
                IFile afile = ((FileEditorInput) masterPage.getEditorInput()).getFile();

                analysis = AnaResourceFileHelper.getInstance().findAnalysis(afile);
            }
        } else {
            analysis = AnaResourceFileHelper.getInstance().findAnalysis(getSelectionFile());
        }

        final WorkspaceJob job = new WorkspaceJob("Run Analysis") { //$NON-NLS-1$

            @Override
            public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {

                monitor.beginTask(
                        DefaultMessagesImpl.getString("RunAnalysisAction.running", analysis.getName()), IProgressMonitor.UNKNOWN); //$NON-NLS-1$ //$NON-NLS-2$

                Display.getDefault().asyncExec(new Runnable() {

                    public void run() {
                        if (listener != null) {
                            listener.fireRuningItemChanged(false);
                        }
                    }

                });
                // MOD xqliu 2009-02-09 bug 6237
                ReturnCode executed = null;
                AnalysisExecutorThread aet = new AnalysisExecutorThread(analysis, monitor);
                // aet.run();
                // executed = aet.getExecuted();
                new Thread(aet).start();
                while (true) {
                    if (aet.getExecuted() != null) {
                        executed = aet.getExecuted();
                        break;
                    }
                    if (monitor.isCanceled()) {
                        executed = new ReturnCode(DefaultMessagesImpl.getString("RunAnalysisAction.TaskCancel"), false); //$NON-NLS-1$
                        break;
                    }
                }
                aet = null;
                monitor.done();
                // ~
                Display.getDefault().asyncExec(new Runnable() {

                    public void run() {
                        if (listener != null) {
                            listener.fireRuningItemChanged(true);
                        }
                    }

                });

                return getResultStatus(executed);
            }

        };

        job.setUser(true);
        job.schedule();

        DQRespositoryView view = (DQRespositoryView) CorePlugin.getDefault().findView(PluginConstant.DQ_VIEW_ID);
        view.getCommonViewer().refresh();

        if (selectionFile != null) {
            IEditorPart ep = CorePlugin.getDefault().openEditor(selectionFile,
                    "org.talend.dataprofiler.core.ui.editor.analysis.AnalysisEditor"); //$NON-NLS-1$
            if (ep instanceof AnalysisEditor) {
                this.listener = ((AnalysisEditor) ep).getMasterPage();
            }
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.cheatsheets.ICheatSheetAction#run(java.lang.String[],
     * org.eclipse.ui.cheatsheets.ICheatSheetManager)
     */
    public void run(String[] params, ICheatSheetManager manager) {
        // ADD mzhao 2009-02-03 If there is no active editor opened, run
        // analysis action from cheat sheets will do nothing.
        IEditorPart editor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
        if (editor == null) {
            return;
        }
        AnalysisEditor anaEditor = (AnalysisEditor) editor;
        AbstractAnalysisMetadataPage masterPage = (AbstractAnalysisMetadataPage) anaEditor.getMasterPage();
        listener = masterPage;
        // ~
        run();
    }

    private IStatus getResultStatus(final ReturnCode executed) {
        if (executed.isOk()) {
            if (log.isInfoEnabled()) {
                int executionDuration = analysis.getResults().getResultMetadata().getExecutionDuration();
                log.info("Analysis \"" + analysis.getName() + "\" execution code: " + executed + ". Duration: " //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                        + FORMAT_SECONDS.format(Double.valueOf(executionDuration) / 1000) + " s."); //$NON-NLS-1$
            }
            return Status.OK_STATUS;
        } else {
            int executionDuration = analysis.getResults().getResultMetadata().getExecutionDuration();
            log
                    .warn(DefaultMessagesImpl
                            .getString(
                                    "RunAnalysisAction.analysis", analysis.getName(), executed, FORMAT_SECONDS.format(Double.valueOf(executionDuration) / 1000))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            // open error dialog
            Display.getDefault().syncExec(new Runnable() {

                public void run() {
                    MessageDialogWithToggle
                            .openError(
                                    null,
                                    DefaultMessagesImpl.getString("RunAnalysisAction.runAnalysis"), DefaultMessagesImpl.getString("RunAnalysisAction.failRunAnalysis", analysis.getName(), executed.getMessage())); //$NON-NLS-1$ //$NON-NLS-2$
                }
            });

            return Status.CANCEL_STATUS;
        }
    }
}
