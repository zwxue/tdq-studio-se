// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.editor.connection;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.Properties;

import net.sourceforge.sqlexplorer.dbproduct.ManagedDriver;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.part.FileEditorInput;
import org.talend.core.GlobalServiceRegister;
import org.talend.core.PluginChecker;
import org.talend.core.model.metadata.IMetadataConnection;
import org.talend.core.model.metadata.MetadataFillFactory;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.metadata.builder.connection.MDMConnection;
import org.talend.core.model.metadata.builder.database.JavaSqlFactory;
import org.talend.core.model.properties.ConnectionItem;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.Property;
import org.talend.core.ui.IMDMProviderService;
import org.talend.cwm.compare.exception.ReloadCompareException;
import org.talend.cwm.compare.factory.ComparisonLevelFactory;
import org.talend.cwm.compare.factory.IComparisonLevel;
import org.talend.cwm.db.connection.ConnectionUtils;
import org.talend.cwm.db.connection.MdmWebserviceConnection;
import org.talend.cwm.helper.ConnectionHelper;
import org.talend.cwm.helper.TaggedValueHelper;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.exception.ExceptionHandler;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.dialog.message.DeleteModelElementConfirmDialog;
import org.talend.dataprofiler.core.ui.editor.AbstractMetadataFormPage;
import org.talend.dataprofiler.core.ui.progress.ProgressUI;
import org.talend.dataprofiler.core.ui.utils.MessageUI;
import org.talend.dataquality.exception.DataprofilerCoreException;
import org.talend.dq.analysis.parameters.DBConnectionParameter;
import org.talend.dq.connection.DataProviderBuilder;
import org.talend.dq.helper.EObjectHelper;
import org.talend.dq.helper.ParameterUtil;
import org.talend.dq.helper.PropertyHelper;
import org.talend.dq.helper.RepositoryNodeHelper;
import org.talend.dq.nodes.ConnectionRepNode;
import org.talend.dq.nodes.DBConnectionRepNode;
import org.talend.dq.nodes.MDMConnectionRepNode;
import org.talend.dq.writer.impl.ElementWriterFactory;
import org.talend.i18n.Messages;
import org.talend.repository.model.RepositoryNode;
import org.talend.repository.ui.wizards.metadata.connection.database.DatabaseWizard;
import org.talend.utils.sugars.ReturnCode;
import org.talend.utils.sugars.TypedReturnCode;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC rli class global comment. Detailled comment
 */
public class ConnectionInfoPage extends AbstractMetadataFormPage {

    private static Logger log = Logger.getLogger(ConnectionInfoPage.class);

    private Connection connection;

    protected ConnectionRepNode connectionRepNode;

    public ConnectionRepNode getConnectionRepNode() {
        return this.connectionRepNode;
    }

    private void initConnectionRepNode(Connection conn) {
        RepositoryNode recursiveFind = RepositoryNodeHelper.recursiveFind(conn);
        if (recursiveFind != null && recursiveFind instanceof ConnectionRepNode) {
            this.connectionRepNode = (ConnectionRepNode) recursiveFind;
        }
    }

    private ConnectionItem connectionItem;

    private DBConnectionParameter tmpParam;

    private Text loginText;

    private Text passwordText;

    private Text urlText;

    private Section infomatioinSection = null;

    private boolean isUrlChanged = false;

    private boolean isPassWordChanged = false;

    private boolean isLoginChanged = false;

    public ConnectionInfoPage(FormEditor editor, String id, String title) {
        super(editor, id, title);
    }

    @Override
    protected ModelElement getCurrentModelElement(FormEditor editor) {
        IEditorInput editorInput = editor.getEditorInput();
        if (editorInput instanceof ConnectionItemEditorInput) {
            ConnectionItemEditorInput input = (ConnectionItemEditorInput) editorInput;
            connectionItem = input.getConnectionItem();
            connection = input.getConnectionItem().getConnection();
        } else if (editorInput instanceof FileEditorInput) {
            Property proty = PropertyHelper.getProperty(((FileEditorInput) editorInput).getFile());
            // String fileLabel = proty.getLabel();
            Item item = proty.getItem();
            if (item instanceof ConnectionItem) {
                connectionItem = (ConnectionItem) item;
                connection = ((ConnectionItem) item).getConnection();
            }
        }
        initConnectionRepNode(connection);
        return ConnectionUtils.fillConnectionInformation(connection);
    }

    @Override
    protected void createFormContent(IManagedForm managedForm) {
        super.createFormContent(managedForm);
        final ScrolledForm form = managedForm.getForm();
        form.setText(DefaultMessagesImpl.getString("ConnectionInfoPage.connectionSettings")); //$NON-NLS-1$
        this.metadataSection.setText(DefaultMessagesImpl.getString("ConnectionInfoPage.connectionMetadata")); //$NON-NLS-1$
        this.metadataSection.setDescription(DefaultMessagesImpl.getString("ConnectionInfoPage.propertiesOConnnection")); //$NON-NLS-1$
        createInformationSection(form, topComp);

        Button checkBtn = toolkit.createButton(topComp, DefaultMessagesImpl.getString("ConnectionInfoPage.check"), SWT.NONE); //$NON-NLS-1$
        GridData gd = new GridData();
        gd.verticalSpan = 20;
        gd.horizontalAlignment = SWT.CENTER;
        checkBtn.setLayoutData(gd);

        checkBtn.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                ReturnCode code = checkDBConnection();
                if (code.isOk()) {
                    MessageDialog.openInformation(
                            null,
                            DefaultMessagesImpl.getString("ConnectionInfoPage.checkConnections"), DefaultMessagesImpl.getString("ConnectionInfoPage.checkConnectionSuccessful")); //$NON-NLS-1$ //$NON-NLS-2$
                } else {
                    MessageDialog.openInformation(
                            null,
                            DefaultMessagesImpl.getString("ConnectionInfoPage.checkConnection"), DefaultMessagesImpl.getString("ConnectionInfoPage.CheckConnectionFailure", code.getMessage())); //$NON-NLS-1$ //$NON-NLS-2$ 
                }
            }

        });

    }

    /**
     * @param form
     * @param toolkit
     * @param topComp
     */
    void createInformationSection(final ScrolledForm form, Composite topComp) {
        infomatioinSection = createSection(
                form,
                topComp,
                DefaultMessagesImpl.getString("ConnectionInfoPage.connectionInformations"), DefaultMessagesImpl.getString("ConnectionInfoPage.informationsOfConnection")); //$NON-NLS-1$ //$NON-NLS-2$

        Composite sectionClient = toolkit.createComposite(infomatioinSection);
        sectionClient.setLayout(new GridLayout(2, false));
        Label loginLabel = new Label(sectionClient, SWT.NONE);
        loginLabel.setText(DefaultMessagesImpl.getString("ConnectionInfoPage.Login")); //$NON-NLS-1$

        loginText = new Text(sectionClient, SWT.BORDER);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(loginText);
        Label passwordLabel = new Label(sectionClient, SWT.NONE);
        passwordLabel.setText(DefaultMessagesImpl.getString("ConnectionInfoPage.Password")); //$NON-NLS-1$
        passwordText = new Text(sectionClient, SWT.BORDER | SWT.PASSWORD);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(passwordText);

        String loginValue = JavaSqlFactory.getUsername(connection);
        loginText.setText(loginValue == null ? PluginConstant.EMPTY_STRING : loginValue);

        // MOD scorreia 2009-01-09 handle encrypted password
        String passwordValue = JavaSqlFactory.getPassword(connection);
        passwordText.setText(passwordValue == null ? PluginConstant.EMPTY_STRING : passwordValue);

        Label urlLabel = new Label(sectionClient, SWT.NONE);
        urlLabel.setText(DefaultMessagesImpl.getString("ConnectionInfoPage.Url")); //$NON-NLS-1$

        Composite urlComp = new Composite(sectionClient, SWT.NONE);
        GridLayout gridLayout = new GridLayout(2, false);
        gridLayout.marginWidth = 0;
        gridLayout.marginHeight = 0;
        urlComp.setLayout(gridLayout);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(urlComp);
        urlText = new Text(urlComp, SWT.BORDER | SWT.READ_ONLY);
        GridDataFactory.fillDefaults().hint(100, -1).grab(true, true).applyTo(urlText);
        String urlValue = JavaSqlFactory.getURL(connection);
        urlText.setText(urlValue == null ? PluginConstant.EMPTY_STRING : urlValue);
        // urlText.setEnabled(false);

        Button editButton = new Button(urlComp, SWT.PUSH);
        editButton.setText(DefaultMessagesImpl.getString("IndicatorDefinitionMaterPage.editExpression")); //$NON-NLS-1$
        editButton.setToolTipText(DefaultMessagesImpl.getString("IndicatorDefinitionMaterPage.editExpression")); //$NON-NLS-1$
        editButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                changeConnectionInformations();
            }
        });

        String driverClass = JavaSqlFactory.getDriverClass(connection);
        if (driverClass != null && driverClass.startsWith("org.sqlite")) { //$NON-NLS-1$
            loginText.setEnabled(false);
            passwordText.setEnabled(false);
        }
        ModifyListener listener = new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                setDirty(true);
                // saveTextChange();
            }

        };

        // MOD klliu 2010-07-06 bug 14095
        loginText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                setDirty(true);
                isLoginChanged = true;
            }

        });
        passwordText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                setDirty(true);
                isPassWordChanged = true;
            }

        });
        urlText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                setDirty(true);
                isUrlChanged = true;
                // saveTextChange();
            }

        });
        infomatioinSection.setClient(sectionClient);
    }

    /**
     * Change connection informations with server, port etc., and update related analyses.
     * 
     * MOD yyi 9082 2010-02-25
     */
    protected void changeConnectionInformations() {
        // UrlEditDialog urlDlg = new UrlEditDialog(null, connection);
        //
        // if (urlDlg.open() == Window.OK) {
        // tmpParam = urlDlg.getResult();
        //
        // if (!tmpParam.getJdbcUrl().equals(urlText.getText())) {
        // urlText.setText(tmpParam.getJdbcUrl());
        // isUrlChanged = true;
        // }
        // // has new jar file for generic jdbc
        // if (!"".equals(tmpParam.getDriverPath())) {
        // setDirty(true);
        // isUrlChanged = true;
        // }
        // }

        if (connectionItem != null) {
            // MOD mzhao bug:19288
            if (connectionItem.eIsProxy()) {
                connectionItem = (ConnectionItem) EObjectHelper.resolveObject(connectionItem);
            }
            RepositoryNode node = RepositoryNodeHelper.recursiveFind(connectionItem.getConnection());

            IWizard wizard = null;
            if (node != null) {
                if (node instanceof DBConnectionRepNode) {
                    wizard = new DatabaseWizard(PlatformUI.getWorkbench(), false, node, null);
                } else if (node instanceof MDMConnectionRepNode) {
                    if (PluginChecker.isMDMPluginLoaded()
                            && GlobalServiceRegister.getDefault().isServiceRegistered(IMDMProviderService.class)) {
                        IMDMProviderService service = (IMDMProviderService) GlobalServiceRegister.getDefault().getService(
                                IMDMProviderService.class);
                        if (service != null) {
                            wizard = service.newMDMWizard(PlatformUI.getWorkbench(), false, node, null);
                        }
                    }
                }
            }

            if (wizard != null) {
                WizardDialog dialog = new WizardDialog(null, wizard);
                dialog.setPageSize(550, 550);
                wizard.setContainer(dialog);
                dialog.open();
            }
        }
    }

    /**
     * DOC yyi Comment method "storeDriverInfo".
     * 
     * @param dbConnectionParam
     */
    private void storeDriveInfoToPerference(DBConnectionParameter connectionParam) {
        String driverPath = connectionParam.getDriverPath();
        if (driverPath != null && !PluginConstant.EMPTY_STRING.equals(driverPath)) {
            LinkedList<String> jars = new LinkedList<String>();
            for (String driverpath : driverPath.split(";")) { //$NON-NLS-1$
                jars.add(driverpath);
            }

            String jdbcUrl = connectionParam.getJdbcUrl();
            if (jdbcUrl != null && jdbcUrl.length() > 12) {
                String name = jdbcUrl.substring(0, 12);
                ManagedDriver driver = new DataProviderBuilder().buildDriverForSQLExploer(name,
                        connectionParam.getDriverClassName(), connectionParam.getJdbcUrl(), jars);
                if (connectionParam == null || driver == null || connection == null) {
                    return;
                }

                StringBuilder driverPara = new StringBuilder();
                if (CorePlugin.getDefault().getPreferenceStore().getString("JDBC_CONN_DRIVER") != null //$NON-NLS-1$
                        && !CorePlugin.getDefault().getPreferenceStore().getString("JDBC_CONN_DRIVER").equals("")) { //$NON-NLS-1$ //$NON-NLS-2$
                    driverPara.append(CorePlugin.getDefault().getPreferenceStore().getString("JDBC_CONN_DRIVER") + ";{" //$NON-NLS-1$ //$NON-NLS-2$
                            + connectionParam.getDriverPath().substring(0, connectionParam.getDriverPath().length() - 1) + "," //$NON-NLS-1$
                            + connectionParam.getDriverClassName() + "," + connectionParam.getJdbcUrl() + "," //$NON-NLS-1$ //$NON-NLS-2$
                            + connection.eResource().getURI().toString() + "," + driver.getId() + "};"); //$NON-NLS-1$ //$NON-NLS-2$
                } else {
                    driverPara.append("{" //$NON-NLS-1$
                            + connectionParam.getDriverPath().substring(0, connectionParam.getDriverPath().length() - 1) + "," //$NON-NLS-1$
                            + connectionParam.getDriverClassName() + "," + connectionParam.getJdbcUrl() + "," //$NON-NLS-1$ //$NON-NLS-2$
                            + connection.eResource().getURI().toString() + "," + driver.getId() + "};"); //$NON-NLS-1$ //$NON-NLS-2$
                }
                CorePlugin.getDefault().getPreferenceStore().putValue("JDBC_CONN_DRIVER", driverPara.toString()); //$NON-NLS-1$

            }
        }

    }

    /**
     * DOC yyi Comment method "updateConnection".
     * 
     * @param dbConnectionParameter
     */
    private void updateConnection(DBConnectionParameter targetParam) {
        if (connection != null) {
            ConnectionUtils.setServerName(connection, targetParam.getHost());
            ConnectionUtils.setPort(connection, targetParam.getPort());
            ConnectionUtils.setSID(connection, targetParam.getDbName());
        }
    }

    private ReturnCode checkDBConnection() {
        Properties props = new Properties();
        props.put(TaggedValueHelper.USER, loginText.getText());
        props.put(TaggedValueHelper.PASSWORD, passwordText.getText());
        // MOD xqliu 2009-12-17 bug 10238
        Connection tdDataProvider2;
        if (null == tmpParam) {
            tdDataProvider2 = connection;
        } else {
            // MOD xqliu 2010-08-04 bug 13406
            if (ConnectionUtils.isMdmConnection(connection)) {
                tdDataProvider2 = connection;
                JavaSqlFactory.setURL(tdDataProvider2, this.urlText.getText());
            } else {

                IMetadataConnection metadataConnection = MetadataFillFactory.getDBInstance().fillUIParams(
                        ParameterUtil.toMap(tmpParam));
                TypedReturnCode<?> typedRC = org.talend.core.model.metadata.builder.util.MetadataConnectionUtils
                        .checkConnection(metadataConnection);
                if (!typedRC.isOk()) {
                    return typedRC;
                } else {
                    tdDataProvider2 = MetadataFillFactory.getDBInstance().fillUIConnParams(metadataConnection, null);
                }
            }
            // ~ 13406
        }
        if (tdDataProvider2 instanceof MDMConnection) {
            props.put(TaggedValueHelper.UNIVERSE, ConnectionHelper.getUniverse((MDMConnection) tdDataProvider2));
            props.put(TaggedValueHelper.DATA_FILTER, ConnectionHelper.getDataFilter((MDMConnection) tdDataProvider2));
        }
        ReturnCode returnCode = ConnectionUtils.isMdmConnection(tdDataProvider2) ? new MdmWebserviceConnection(
                JavaSqlFactory.getURL(tdDataProvider2), props).checkDatabaseConnection() : ConnectionUtils.checkConnection(
                this.urlText.getText(), JavaSqlFactory.getDriverClass(tdDataProvider2), props);
        // ~
        return returnCode;
    }

    private ReturnCode checkDBConnectionWithProgress() {
        final ReturnCode rc = new ReturnCode();
        IRunnableWithProgress op = new IRunnableWithProgress() {

            public void run(IProgressMonitor monitor) throws InvocationTargetException {
                Display.getDefault().asyncExec(new Runnable() {

                    public void run() {
                        rc.setOk(checkDBConnection().isOk());
                    }
                });
            }
        };
        try {
            ProgressUI.popProgressDialog(op);
        } catch (InvocationTargetException e) {
            log.error(e, e);
        } catch (InterruptedException e) {
            log.error(e, e);
        }
        return rc;
    }

    @Override
    public void setDirty(boolean isDirty) {
        if (this.isDirty != isDirty) {
            this.isDirty = isDirty;
            ((ConnectionEditor) this.getEditor()).firePropertyChange(IEditorPart.PROP_DIRTY);
        }

    }

    @Override
    public void doSave(IProgressMonitor monitor) {
        boolean checkDBConnection = checkDBConnectionWithProgress().isOk();
        if (!checkDBConnection) {
            String dialogMessage = DefaultMessagesImpl.getString("ConnectionInfoPage.checkDBConnection");
            String dialogTitle = DefaultMessagesImpl.getString("ConnectionInfoPage.urlChanged");
            if (Window.CANCEL == DeleteModelElementConfirmDialog.showElementImpactConfirmDialog(null,
                    new ModelElement[] { connection }, dialogTitle, dialogMessage)) {
                return;
            }
        } else {
            if (!impactAnalyses().isOk()) {
                return;
            }
        }

        saveTextChange();

        if (isUrlChanged) {
            updateConnection(tmpParam);
            storeDriveInfoToPerference(tmpParam);
        }

        super.doSave(monitor);
        try {
            saveConnectionInfo();

            this.initialize(this.getEditor());

            if (checkDBConnection)
                reloadDataProvider();

            this.isUrlChanged = false;
            this.isDirty = false;
        } catch (DataprofilerCoreException e) {
            ExceptionHandler.process(e, Level.ERROR);
            log.error(e, e);
        }
    }

    /**
     * DOC yyi Comment method "impactAnalyses".
     */
    private ReturnCode impactAnalyses() {

        ReturnCode rc = new ReturnCode();
        String dialogMessage = DefaultMessagesImpl.getString("ConnectionInfoPage.impactAnalyses");
        String dialogTitle = DefaultMessagesImpl.getString("ConnectionInfoPage.urlChanged");
        // MOD klliu 2010-07-06 bug 14095: unnecessary wizard
        if (this.isUrlChanged || this.isLoginChanged || this.isPassWordChanged) {
            rc.setOk(Window.OK == DeleteModelElementConfirmDialog.showElementImpactConfirmDialog(null,
                    new ModelElement[] { connection }, dialogTitle, dialogMessage));
        }

        return rc;
    }

    private void reloadDataProvider() {
        // MOD klliu 2011-02-24 bug 19015
        final RepositoryNode connRecursiveFind = RepositoryNodeHelper.recursiveFind(connection);
        IRunnableWithProgress op = new IRunnableWithProgress() {

            public void run(IProgressMonitor monitor) throws InvocationTargetException {
                final IComparisonLevel creatComparisonLevel = ComparisonLevelFactory.creatComparisonLevel(connRecursiveFind);
                Display.getDefault().asyncExec(new Runnable() {

                    public void run() {
                        try {
                            creatComparisonLevel.reloadCurrentLevelElement();
                        } catch (ReloadCompareException e) {
                            log.error(e, e);
                        }
                    }
                });
            }
        };
        try {
            ProgressUI.popProgressDialog(op);
            CorePlugin.getDefault().refreshDQView();
            this.initialize(this.getEditor());
            // MOD klliu this don't need to close the Editor
            // this.getEditor().close(false);
        } catch (InvocationTargetException e) {
            MessageUI.openError(Messages.getString("ReloadDatabaseAction.checkConnectionFailured", e.getCause().getMessage())); //$NON-NLS-1$
            log.error(e, e);
        } catch (InterruptedException e) {
            log.error(e, e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.editor.AbstractMetadataFormPage#saveTextChange()
     */
    @Override
    protected void saveTextChange() {
        if (connection != null && connection.eIsProxy()) {
            connection = (Connection) EObjectHelper.resolveObject(connection);
        }
        super.saveTextChange();
        ConnectionUtils.setName(connection, nameText.getText());
        PropertyHelper.getProperty(connection).setLabel(nameText.getText());
        JavaSqlFactory.setUsername(connection, loginText.getText());
        JavaSqlFactory.setPassword(connection, passwordText.getText());
        JavaSqlFactory.setURL(connection, urlText.getText());
        // MOD zshen for bug 12327:to save driverClassName.
        if (tmpParam != null && tmpParam.getDriverClassName() != null && !"".equals(tmpParam.getDriverClassName())) {
            ConnectionUtils.setDriverClass(connection, tmpParam.getDriverClassName());
        }
        // ~12327
    }

    private void saveConnectionInfo() throws DataprofilerCoreException {
        if (connection != null && connection.eIsProxy()) {
            connection = (Connection) EObjectHelper.resolveObject(connection);
        }
        ReturnCode returnCode = ElementWriterFactory.getInstance().createDataProviderWriter().save(connection);
        if (returnCode.isOk()) {
            if (log.isDebugEnabled()) {
                log.debug("Saved in  " + connection.eResource().getURI().toFileString() + " successful"); //$NON-NLS-1$ //$NON-NLS-2$
            }
        } else {
            throw new DataprofilerCoreException(
                    DefaultMessagesImpl
                            .getString(
                                    "ConnectionInfoPage.ProblemSavingFile", connection.eResource().getURI().toFileString(), returnCode.getMessage())); //$NON-NLS-1$
        }
    }

}
