// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
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

import java.sql.SQLException;
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
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.cheatsheets.ICheatSheetAction;
import org.eclipse.ui.cheatsheets.ICheatSheetManager;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.part.FileEditorInput;
import org.talend.commons.exception.BusinessException;
import org.talend.commons.exception.LoginException;
import org.talend.commons.exception.PersistenceException;
import org.talend.core.database.EDatabaseTypeName;
import org.talend.core.model.metadata.IMetadataConnection;
import org.talend.core.model.metadata.builder.ConvertionHelper;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.properties.Item;
import org.talend.core.repository.model.IRepositoryFactory;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.core.repository.model.RepositoryFactoryProvider;
import org.talend.cwm.compare.exception.ReloadCompareException;
import org.talend.cwm.compare.factory.ComparisonLevelFactory;
import org.talend.cwm.db.connection.ConnectionUtils;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.exception.ExceptionFactory;
import org.talend.dataprofiler.core.exception.ExceptionHandler;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.IRuningStatusListener;
import org.talend.dataprofiler.core.ui.editor.analysis.AbstractAnalysisMetadataPage;
import org.talend.dataprofiler.core.ui.editor.analysis.AnalysisEditor;
import org.talend.dataprofiler.core.ui.editor.analysis.AnalysisItemEditorInput;
import org.talend.dataprofiler.core.ui.events.EventEnum;
import org.talend.dataprofiler.core.ui.events.EventManager;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.analysis.AnalysisType;
import org.talend.dataquality.helpers.AnalysisHelper;
import org.talend.dataquality.properties.TDQAnalysisItem;
import org.talend.dq.analysis.connpool.TdqAnalysisConnectionPool;
import org.talend.dq.helper.ProxyRepositoryManager;
import org.talend.dq.helper.RepositoryNodeHelper;
import org.talend.dq.helper.resourcehelper.AnaResourceFileHelper;
import org.talend.dq.nodes.AnalysisRepNode;
import org.talend.metadata.managment.connection.manager.HiveConnectionManager;
import org.talend.repository.model.RepositoryConstants;
import org.talend.repository.model.RepositoryNode;
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

    @Deprecated
    private IFile selectionFile;// no one used

    private AnalysisRepNode node;

    // Added TDQ-7551 20130704: for lock/unlock in SVN ask user mode
    private IEditorPart editor = null;

    private boolean isNeedUnlock = false;

    // ~

    @Deprecated
    public IFile getSelectionFile() {
        return selectionFile;
    }

    @Deprecated
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
            Item item = null;

            // MOD klliu bug 19244 2011-03-10
            if (node != null) {
                // means it from the context menu "run" which need to select a node
                // then find the analysis from the node, and not fromt the editor(only one way)
                item = node.getObject().getProperty().getItem();
                if (item == null) {
                    log.error("Analysis item is null");
                    return;
                }
                validateAnalysis(item);
                if (ifLockByOthers(item)) {
                    return;
                }
                analysis = this.node.getAnalysis();

                // check if the analysis need to be saved or can run before real running by the event
                if (!EventManager.getInstance().publish(analysis, EventEnum.DQ_ANALYSIS_CHECK_BEFORERUN, null)) {
                    // if the analysis need save but can not be saved, return without continue;
                    // or the analysis can not run, return without continue
                    return;
                }

                // if not from the context menu, then find the analysis from the editor
            } else if (editor != null && editor instanceof AnalysisEditor) {
                // only when the current opened editor is the analysis editor type
                // editor already opened, the run comes from the run button in the editor
                AnalysisEditor anaEditor = (AnalysisEditor) editor;
                // check if the analysis editor is dirty or not, if dirty save it before continue running
                if (!saveAnalysisIfNeeded(anaEditor) || !isAnalysisCanRun(anaEditor)) {
                    return;
                }

                IEditorInput editorInput = anaEditor.getEditorInput();
                analysis = findAnalysisFromEditorInput(editorInput);
                // find the listener by the editor
                listener = findAnalysisListenerFromAnalysisEditor(anaEditor);
            }

            if (analysis == null || !isConnectedAvailable(analysis)) {
                return;
            }
            AnalysisType analysisType = analysis.getParameters().getAnalysisType();
            if (AnalysisType.COLUMNS_COMPARISON.equals(analysisType)) {
                // If the analysis type is column comparison, ask user to continue to run or not.
                if (!isContinueRun()) {
                    return;
                }
            }

            if (AnalysisType.CONNECTION.equals(analysisType)) {
                // If the analysis type is overview analysis, reload the database. TODO check here the needed of
                // reloading database
                reloadConnection(analysis);
            }

            // lock the analysis before running it if it is not locked yet.(whenever the editor is not opened)
            // when the run comes from the button in editor, the node is null;
            // when the run comes from context menu, the node is not null
            if (node != null && !ProxyRepositoryManager.getInstance().isLocked(item)) {
                if (!ProxyRepositoryFactory.getInstance().isEditableAndLockIfPossible(item)) {
                    // if the analysis is not editable , return without running.
                    isNeedUnlock = false;
                    return;
                } else {// it is locked here, means that it need the unlock too.
                    isNeedUnlock = true;
                }
            } else {
                isNeedUnlock = false;
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
                        monitor.done();
                        if (isNeedUnlock) {
                            unlockAnalysis();
                        }
                        return Status.CANCEL_STATUS;
                    }

                    if (aet.getExecuted() != null) {
                        executed = aet.getExecuted();
                    }
                    monitor.done();
                    if (isNeedUnlock) {
                        unlockAnalysis();
                    }

                    Display.getDefault().asyncExec(new Runnable() {

                        public void run() {
                            if (listener != null) {
                                listener.fireRuningItemChanged(true);
                            } else {
                                EventManager.getInstance().publish(analysis, EventEnum.DQ_ANALYSIS_RUN_FROM_MENU, null);
                            }
                        }

                    });

                    displayResultStatus(executed);

                    return Status.OK_STATUS;
                }

            };

            job.setUser(true);
            job.schedule();
        } catch (BusinessException e) {
            ExceptionHandler.process(e, Level.FATAL);
        }
    }

    private Analysis findAnalysisFromEditorInput(IEditorInput editorInput) {
        Analysis analysisToFind = null;
        if (editorInput instanceof FileEditorInput) {
            IFile afile = ((FileEditorInput) editorInput).getFile();
            analysisToFind = AnaResourceFileHelper.getInstance().findAnalysis(afile);
        } else if (editorInput instanceof AnalysisItemEditorInput) {
            analysisToFind = ((TDQAnalysisItem) ((AnalysisItemEditorInput) editorInput).getItem()).getAnalysis();
        }
        return analysisToFind;
    }

    private void unlockAnalysis() {
        try {
            ProxyRepositoryFactory.getInstance().unlock(node.getObject().getProperty().getItem());
        } catch (PersistenceException e) {
            log.error(e, e);
        } catch (LoginException e) {
            log.error(e, e);
        }
    }

    private Boolean isContinueRun() {
        boolean isContinueRun = Boolean.TRUE;
        if (!MessageDialogWithToggle.openConfirm(null, DefaultMessagesImpl.getString("RunAnalysisAction.confirmTitle"), //$NON-NLS-1$
                DefaultMessagesImpl.getString("RunAnalysisAction.confirmMSG"))) { //$NON-NLS-1$
            isContinueRun = Boolean.FALSE;
        }
        return isContinueRun;
    }

    private void reloadConnection(Analysis analysis) {
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

    // return true when the connection is well connected
    private boolean isConnectedAvailable(Analysis analysis) {
        // MOD klliu bug 4546 check connectiong is connected well.
        DataManager datamanager = analysis.getContext().getConnection();
        Connection analysisDataProvider = ConnectionUtils.getConnectionFromDatamanager(datamanager);

        // MOD klliu bug 4584 Filtering the file connection when checking connection is successful,before real
        // running analysis.
        // MOD 20130313 TDQ-6524 avoid popup context select dialog when running analysis,yyin
        IMetadataConnection metadataConnection = ConvertionHelper.convert(analysisDataProvider, false,
                analysisDataProvider.getContextName());
        // ~

        ReturnCode connectionAvailable = new ReturnCode(false);

        if (metadataConnection != null && EDatabaseTypeName.HIVE.getXmlName().equalsIgnoreCase(metadataConnection.getDbType())) {
            try {
                HiveConnectionManager.getInstance().checkConnection(metadataConnection);
                connectionAvailable.setOk(true);
            } catch (ClassNotFoundException e) {
                connectionAvailable.setOk(false);
                log.error(e);
            } catch (InstantiationException e) {
                connectionAvailable.setOk(false);
                log.error(e);
            } catch (IllegalAccessException e) {
                connectionAvailable.setOk(false);
                log.error(e);
            } catch (SQLException e) {
                connectionAvailable.setOk(false);
                log.error(e);
            }
        } else {
            connectionAvailable = ConnectionUtils.isConnectionAvailable(analysisDataProvider);
        }

        if (!connectionAvailable.isOk()) {
            MessageDialogWithToggle.openWarning(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                    DefaultMessagesImpl.getString("RunAnalysisAction.checkConnFailTitle"),//$NON-NLS-1$
                    DefaultMessagesImpl.getString("RunAnalysisAction.checkConnFailMsg", connectionAvailable.getMessage()));//$NON-NLS-1$
            return false;
        }
        return true;
    }

    private IRuningStatusListener findAnalysisListenerFromAnalysisEditor(AnalysisEditor anaEditor) {
        IRuningStatusListener listenerToFind = null;
        IFormPage activePageInstance = anaEditor.getActivePageInstance();
        if (activePageInstance instanceof IRuningStatusListener) {
            listenerToFind = (IRuningStatusListener) activePageInstance;
        }
        return listenerToFind;
    }

    /**
     * check if the analysis can run when editor is opened, if can not, popup dialog
     * 
     * @param anaEditor
     * @return
     */
    private boolean isAnalysisCanRun(AnalysisEditor anaEditor) {
        ReturnCode canRun = anaEditor.canRun();
        if (!canRun.isOk()) {
            MessageDialogWithToggle.openError(null, DefaultMessagesImpl.getString("RunAnalysisAction.runAnalysis"), canRun//$NON-NLS-1$
                    .getMessage());
            return false;
        }
        return true;
    }

    private boolean saveAnalysisIfNeeded(AnalysisEditor anaEditor) {
        if (anaEditor.isDirty()) {
            // MOD qiongli 2011-6-20 bug 21533,can save should before method doSave()
            // MOD klliu bug 19991 3td 2011-03-29
            ReturnCode canSave = anaEditor.getMasterPage().canSave();
            if (!canSave.isOk()) {
                MessageDialog.openError(editor.getSite().getShell(),
                        DefaultMessagesImpl.getString("RunAnalysisAction.runAnalysis"), //$NON-NLS-1$
                        canSave.getMessage());
                return false;
            }
            // ~
            try {
                saveBeforeRun(anaEditor);
            } catch (Exception e) {
                log.error(e, e);
                return false;
            }
        }
        return true;
    }

    /**
     * Save the analysis before run the analysis.
     * 
     * @param anaEditor
     * @throws Exception
     */
    private void saveBeforeRun(AnalysisEditor anaEditor) throws Exception {
        IRepositoryFactory localRepository = RepositoryFactoryProvider
                .getRepositoriyById(RepositoryConstants.REPOSITORY_LOCAL_ID);
        IRepositoryFactory oldRepository = ProxyRepositoryFactory.getInstance().getRepositoryFactoryFromProvider();
        ProxyRepositoryFactory.getInstance().setRepositoryFactoryFromProvider(localRepository);
        // This save action won't invoke any remote repository action such as svn commit. TDQ-7508
        try {
            anaEditor.doSave(null);
        } catch (Exception e) {
            throw e;
        } finally {
            ProxyRepositoryFactory.getInstance().setRepositoryFactoryFromProvider(oldRepository);
        }
    }
    private boolean ifLockByOthers(Item item) {
        // MOD sizhaoliu TDQ-5452 verify the lock status before running an analysis
        if (ProxyRepositoryManager.getInstance().isLockByOthers(item)) {
            CorePlugin.getDefault().refreshDQView(node.getParent());
            MessageDialog.openError(PlatformUI.getWorkbench().getDisplay().getActiveShell(),
                    DefaultMessagesImpl.getString("RunAnalysisAction.runAnalysis"), //$NON-NLS-1$
                    DefaultMessagesImpl.getString("RunAnalysisAction.error.lockByOthers")); //$NON-NLS-1$
            return true;
        } // ~ TDQ-5452
        return false;
    }

    private void validateAnalysis(Item item) throws BusinessException {
        if (item instanceof TDQAnalysisItem) {
            if (((TDQAnalysisItem) item).getAnalysis() == null || ((TDQAnalysisItem) item).getAnalysis().getParameters() == null) {
                BusinessException createBusinessException = ExceptionFactory.getInstance().createBusinessException(
                        ((TDQAnalysisItem) item).getFilename());
                throw createBusinessException;
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
