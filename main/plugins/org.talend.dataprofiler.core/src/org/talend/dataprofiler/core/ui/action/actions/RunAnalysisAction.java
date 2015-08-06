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
package org.talend.dataprofiler.core.ui.action.actions;

import java.text.DecimalFormat;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.cheatsheets.ICheatSheetAction;
import org.eclipse.ui.cheatsheets.ICheatSheetManager;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.part.FileEditorInput;
import org.talend.commons.exception.BusinessException;
import org.talend.commons.exception.LoginException;
import org.talend.commons.exception.PersistenceException;
import org.talend.core.model.metadata.IMetadataConnection;
import org.talend.core.model.metadata.builder.ConvertionHelper;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.metadata.builder.database.HotClassLoader;
import org.talend.core.model.metadata.builder.database.JDBCDriverLoader;
import org.talend.core.model.properties.Item;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.cwm.compare.exception.ReloadCompareException;
import org.talend.cwm.compare.factory.ComparisonLevelFactory;
import org.talend.cwm.db.connection.ConnectionUtils;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.exception.ExceptionFactory;
import org.talend.dataprofiler.core.exception.ExceptionHandler;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.IRuningStatusListener;
import org.talend.dataprofiler.core.ui.editor.CommonFormEditor;
import org.talend.dataprofiler.core.ui.editor.analysis.AbstractAnalysisMetadataPage;
import org.talend.dataprofiler.core.ui.editor.analysis.AnalysisEditor;
import org.talend.dataprofiler.core.ui.editor.analysis.AnalysisItemEditorInput;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.analysis.AnalysisType;
import org.talend.dataquality.helpers.AnalysisHelper;
import org.talend.dataquality.properties.TDQAnalysisItem;
import org.talend.dq.analysis.connpool.TdqAnalysisConnectionPool;
import org.talend.dq.helper.ProxyRepositoryManager;
import org.talend.dq.helper.RepositoryNodeHelper;
import org.talend.dq.helper.resourcehelper.AnaResourceFileHelper;
import org.talend.dq.nodes.AnalysisRepNode;
import org.talend.repository.model.RepositoryNode;
import org.talend.repository.ui.utils.ManagerConnection;
import org.talend.utils.sugars.ReturnCode;
import orgomg.cwm.foundation.softwaredeployment.DataManager;

/**
 * DOC zqin class global comment. Detailled comment <br/>
 * 
 * $Id: talend.epf 1 2006-09-29 17:06:40Z zqin $
 */
public class RunAnalysisAction extends Action implements ICheatSheetAction {

    private static Logger log = Logger.getLogger(RunAnalysisAction.class);

    public static final String ID = "org.talend.common.runTalendElement"; //$NON-NLS-1$

    private static final DecimalFormat FORMAT_SECONDS = new DecimalFormat("0.00"); //$NON-NLS-1$

    private Analysis analysis = null;

    private IRuningStatusListener listener;

    private IFile selectionFile;

    private AnalysisRepNode node;

    // Added TDQ-7551 20130704: for lock/unlock in SVN ask user mode
    private boolean editable = true;

    private Boolean lockByUserOwn = false;

    private Item item = null;

    private IEditorPart editor = null;

    // ~
    private boolean isLocalProject = ProxyRepositoryManager.getInstance().isLocalProject();

    public IFile getSelectionFile() {
        return selectionFile;
    }

    public void setSelectionFile(IFile selectionFile) {
        this.selectionFile = selectionFile;
    }

    public void setSelectionNode(AnalysisRepNode node) {
        this.node = node;
    }

    public RunAnalysisAction() {
        super(DefaultMessagesImpl.getString("RunAnalysisAction.Run")); //$NON-NLS-1$
        setImageDescriptor(ImageLib.getImageDescriptor(ImageLib.REFRESH_IMAGE));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.action.Action#run()
     */
    @Override
    public void run() {
        try {
            editor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();

            // MOD klliu bug 19244 2011-03-10
            if (node != null) {
                // MOD sizhaoliu TDQ-5452 verify the lock status before running an analysis
                item = node.getObject().getProperty().getItem();
                if (item instanceof TDQAnalysisItem) {
                    if (((TDQAnalysisItem) item).getAnalysis() == null
                            || ((TDQAnalysisItem) item).getAnalysis().getParameters() == null) {
                        BusinessException createBusinessException = ExceptionFactory.getInstance().createBusinessException(
                                ((TDQAnalysisItem) item).getFilename());
                        throw createBusinessException;
                    }
                }
                if (ProxyRepositoryManager.getInstance().isLockByOthers(item)) {
                    CorePlugin.getDefault().refreshDQView(node.getParent());
                    MessageDialog.openError(PlatformUI.getWorkbench().getDisplay().getActiveShell(),
                            DefaultMessagesImpl.getString("RunAnalysisAction.runAnalysis"), //$NON-NLS-1$
                            DefaultMessagesImpl.getString("RunAnalysisAction.error.lockByOthers")); //$NON-NLS-1$
                    return;
                } // ~ TDQ-5452

                // Added TDQ-7551 0704 yyin
                lockByUserOwn = ProxyRepositoryManager.getInstance().isLockByUserOwn(item);
                // if the analysis is not locked, lock it
                if (!lockByUserOwn) {
                    editable = ProxyRepositoryFactory.getInstance().isEditableAndLockIfPossible(item);
                    if (editable) {
                        CorePlugin.getDefault().refreshDQView(node);
                    }
                }

                if (!editable) {
                    // when the item is not editable, and the user select not lock the item
                    // under ask user mode(remote). and in this case, we will not continue the run action.
                    return;
                }
                // ~ TDQ-7551
                editor = CorePlugin.getDefault().openEditor(
                        new AnalysisItemEditorInput(node.getObject().getProperty().getItem()), AnalysisEditor.class.getName());
                // // in this running, the editor should be editable if before is not editable
            } else {
                // means that the user select "run" inside the analysis editor, it is locked already before running
                lockByUserOwn = true;
            }
            // MOD qiongli bug 13880,2010-7-6,avoid 'ClassCastException'
            if (selectionFile != null) {
                analysis = AnaResourceFileHelper.getInstance().findAnalysis(selectionFile);
                RepositoryNode recursiveFind = RepositoryNodeHelper.recursiveFind(analysis);
                if (recursiveFind != null) {
                    editor = CorePlugin.getDefault().openEditor(
                            new AnalysisItemEditorInput(recursiveFind.getObject().getProperty().getItem()),
                            AnalysisEditor.class.getName());
                }
            }
            // ~
            if (editor == null) {
                analysis = this.node.getAnalysis();
            } else {
                AnalysisEditor anaEditor = (AnalysisEditor) editor;
                if (editor.isDirty()) {
                    // MOD qiongli 2011-6-20 bug 21533,can save should before method doSave()
                    // MOD klliu bug 19991 3td 2011-03-29
                    ReturnCode canSave = anaEditor.getMasterPage().canSave();
                    if (!canSave.isOk()) {
                        MessageDialog.openError(editor.getSite().getShell(),
                                DefaultMessagesImpl.getString("RunAnalysisAction.runAnalysis"), //$NON-NLS-1$
                                canSave.getMessage());
                        return;
                    }
                    // ~
                    anaEditor.doSave(null);
                }

                ReturnCode canRun = anaEditor.canRun();
                if (!canRun.isOk()) {
                    MessageDialogWithToggle.openError(null,
                            DefaultMessagesImpl.getString("RunAnalysisAction.runAnalysis"), canRun//$NON-NLS-1$
                                    .getMessage());
                    return;
                }

                if (selectionFile != null) {
                    analysis = AnaResourceFileHelper.getInstance().findAnalysis(selectionFile);

                    IEditorReference[] editorReferences = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
                            .getEditorReferences();

                    for (IEditorReference reference : editorReferences) {
                        AnalysisItemEditorInput analysisItemEditorInput;
                        try {
                            // MOD qiongli bug 16505.
                            IEditorInput editorInput = reference.getEditorInput();
                            if (editorInput instanceof AnalysisItemEditorInput) {
                                analysisItemEditorInput = (AnalysisItemEditorInput) editorInput;
                                Analysis ana = ((TDQAnalysisItem) analysisItemEditorInput.getItem()).getAnalysis();
                                if (analysis.equals(ana)) {
                                    IFormPage activePageInstance = ((AnalysisEditor) reference.getEditor(true))
                                            .getActivePageInstance();
                                    // MOD qiongli bug 13880
                                    // if (reference instanceof IRuningStatusListener) {
                                    if (activePageInstance instanceof IRuningStatusListener) {
                                        listener = (IRuningStatusListener) activePageInstance;
                                    }
                                }
                            }
                        } catch (PartInitException e) {
                            log.error(e, e);
                        }
                    }
                } else {
                    IEditorInput editorInput = anaEditor.getEditorInput();
                    if (editorInput instanceof FileEditorInput) {
                        IFile afile = ((FileEditorInput) editorInput).getFile();
                        analysis = AnaResourceFileHelper.getInstance().findAnalysis(afile);
                    } else if (editorInput instanceof AnalysisItemEditorInput) {
                        analysis = ((TDQAnalysisItem) ((AnalysisItemEditorInput) editorInput).getItem()).getAnalysis();
                    }
                    IFormPage activePageInstance = anaEditor.getActivePageInstance();
                    if (activePageInstance instanceof IRuningStatusListener) {
                        listener = (IRuningStatusListener) activePageInstance;
                    }
                }
            }

            if (analysis == null) {
                return;
            }
            // MOD klliu bug 4546 check connectiong is connected well.
            DataManager datamanager = analysis.getContext().getConnection();
            Connection analysisDataProvider = ConnectionUtils.getConnectionFromDatamanager(datamanager);

            // MOD klliu bug 4584 Filtering the file connection when checking connection is successful,before real
            // running
            // analysis.
            ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
            // MOD 20130313 TDQ-6524 avoid popup context select dialog when running analysis,yyin
            IMetadataConnection metadataConnection = ConvertionHelper.convert(analysisDataProvider, false,
                    analysisDataProvider.getContextName());
            // ~
            boolean isHiveEmbedded = ConnectionUtils.isHiveEmbedded(metadataConnection);
            if (isHiveEmbedded) {
                ManagerConnection managerConnection = new ManagerConnection();
                managerConnection.checkForHive(metadataConnection);
                JDBCDriverLoader jdbcDriverLoader = new JDBCDriverLoader();
                HotClassLoader hotClassLoaderFromCache = jdbcDriverLoader.getHotClassLoaderFromCache(
                        metadataConnection.getDbType(), metadataConnection.getDbVersionString());
                Thread.currentThread().setContextClassLoader(hotClassLoaderFromCache);
            }

            ReturnCode connectionAvailable = ConnectionUtils.isConnectionAvailable(analysisDataProvider);

            if (!connectionAvailable.isOk()) {
                MessageDialogWithToggle.openWarning(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                        DefaultMessagesImpl.getString("RunAnalysisAction.checkConnFailTitle"),//$NON-NLS-1$
                        DefaultMessagesImpl.getString("RunAnalysisAction.checkConnFailMsg", connectionAvailable.getMessage()));//$NON-NLS-1$
                return;
            }

            AnalysisType analysisType = analysis.getParameters().getAnalysisType();

            if (AnalysisType.COLUMNS_COMPARISON.equals(analysisType)) {
                if (!MessageDialogWithToggle.openConfirm(null, DefaultMessagesImpl.getString("RunAnalysisAction.confirmTitle"), //$NON-NLS-1$
                        DefaultMessagesImpl.getString("RunAnalysisAction.confirmMSG"))) { //$NON-NLS-1$
                    return;
                }
            } else if (AnalysisType.CONNECTION.equals(analysisType)) {
                if (AnalysisHelper.getReloadDatabases(analysis)) {
                    Connection conntion = (Connection) analysis.getContext().getConnection();
                    if (conntion != null) {
                        try {
                            RepositoryNode connectionNode = RepositoryNodeHelper.recursiveFind(conntion);
                            ComparisonLevelFactory.creatComparisonLevel(connectionNode).reloadCurrentLevelElement();
                        } catch (ReloadCompareException e) {
                            log.error(e, e);
                        }
                    }
                }
            }

            final WorkspaceJob job = new WorkspaceJob("Run Analysis") { //$NON-NLS-1$

                @Override
                public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {

                    monitor.beginTask(
                            DefaultMessagesImpl.getString("RunAnalysisAction.running", analysis.getName()), IProgressMonitor.UNKNOWN); //$NON-NLS-1$ 

                    Display.getDefault().asyncExec(new Runnable() {

                        public void run() {
                            if (listener != null) {
                                listener.fireRuningItemChanged(false);
                            }
                        }

                    });

                    ReturnCode executed = null;
                    AnalysisExecutorThread aet = new AnalysisExecutorThread(analysis, monitor);

                    // MOD sizhaoliu TDQ-6421 use eclipse monitor instead of starting a new thread
                    aet.run();

                    if (monitor.isCanceled()) {
                        TdqAnalysisConnectionPool.closeConnectionPool(analysis);
                        executed = new ReturnCode(DefaultMessagesImpl.getString("RunAnalysisAction.TaskCancel"), false); //$NON-NLS-1$
                    } else {
                        if (aet.getExecuted() != null) {
                            executed = aet.getExecuted();
                        }
                    }
                    monitor.done();

                    if (!isLocalProject && !lockByUserOwn && item != null && editable) {
                        try {
                            ProxyRepositoryFactory.getInstance().unlock(item);
                        } catch (PersistenceException e) {
                            log.error(e, e);
                        } catch (LoginException e) {
                            log.error(e, e);
                        }
                    }// ~

                    Display.getDefault().asyncExec(new Runnable() {

                        public void run() {
                            if (listener != null) {
                                listener.fireRuningItemChanged(true);
                            }
                            // Added TDQ-7551 0704 yyin
                            // unlock the current item if it is locked/opened in this run,
                            // else, do not unlock it
                            if (!isLocalProject && !lockByUserOwn) {
                                disableEditorWhenUnlock();
                            }

                            // CorePlugin.getDefault().refreshDQView();
                        }

                    });
                    displayResultStatus(executed);

                    return Status.OK_STATUS;
                }

            };

            job.setUser(true);
            job.schedule();
            if (isHiveEmbedded) {
                Thread.currentThread().setContextClassLoader(contextClassLoader);
            }

        } catch (BusinessException e) {
            ExceptionHandler.process(e, Level.FATAL);
        }
    }

    // if the user select unlock after running, unable the editor
    private void disableEditorWhenUnlock() {
        if (!ProxyRepositoryFactory.getInstance().getStatus(item).isEditable()) {
            ((CommonFormEditor) editor).lockFormEditor(true);
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
        AbstractAnalysisMetadataPage masterPage = anaEditor.getMasterPage();
        listener = masterPage;
        // ~
        run();

    }

    private void displayResultStatus(final ReturnCode executed) {
        if (log.isInfoEnabled()) {
            int executionDuration = analysis.getResults().getResultMetadata().getExecutionDuration();
            log.info(DefaultMessagesImpl
                    .getString(
                            "RunAnalysisAction.displayInformation", new Object[] { analysis.getName(), executed, FORMAT_SECONDS.format(Double.valueOf(executionDuration) / 1000) })); //$NON-NLS-1$ 

        }

        if (executed.getMessage() != null) {
            Display.getDefault().asyncExec(new Runnable() {

                public void run() {
                    // use the ActiveWorkbenchWindow's shell, don't use the default shell, otherwize the error dialog
                    // will be cloed when the "Run Analysis" dialog (if user don't check "Always run in background", it
                    // will always show this dialog when run an analysis) close, just like don't show the error dialog
                    Shell shell = null;
                    if (PlatformUI.getWorkbench().getActiveWorkbenchWindow() != null) {
                        shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
                    }
                    MessageDialogWithToggle.openError(
                            shell,
                            DefaultMessagesImpl.getString("RunAnalysisAction.runAnalysis"), DefaultMessagesImpl.getString("RunAnalysisAction.failRunAnalysis", analysis.getName(), executed.getMessage())); //$NON-NLS-1$ //$NON-NLS-2$
                }
            });
        }
    }
}
