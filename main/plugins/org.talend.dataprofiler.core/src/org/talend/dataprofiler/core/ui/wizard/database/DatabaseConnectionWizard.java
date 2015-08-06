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
package org.talend.dataprofiler.core.ui.wizard.database;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.List;

import net.sourceforge.sqlexplorer.dbproduct.ManagedDriver;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFolder;
import org.talend.core.model.metadata.IMetadataConnection;
import org.talend.core.model.metadata.MetadataFillFactory;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.metadata.builder.database.dburl.SupportDBUrlType;
import org.talend.core.model.properties.Item;
import org.talend.cwm.db.connection.ConnectionUtils;
import org.talend.cwm.management.api.FolderProvider;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.editor.connection.ConnectionEditor;
import org.talend.dataprofiler.core.ui.editor.connection.ConnectionItemEditorInput;
import org.talend.dataprofiler.core.ui.wizard.AbstractWizard;
import org.talend.dq.CWMPlugin;
import org.talend.dq.analysis.parameters.DBConnectionParameter;
import org.talend.dq.helper.ParameterUtil;
import org.talend.dq.helper.resourcehelper.ResourceFileMap;
import org.talend.dq.writer.impl.ElementWriterFactory;
import org.talend.resource.ResourceManager;
import org.talend.resource.ResourceService;
import org.talend.utils.sugars.ReturnCode;
import org.talend.utils.sugars.TypedReturnCode;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DatabaseWizard present the DatabaseForm. Use to manage the metadata connection.
 */
public class DatabaseConnectionWizard extends AbstractWizard {

    protected static Logger log = Logger.getLogger(DatabaseConnectionWizard.class);

    private DatabaseMetadataWizardPage propertiesWizardPage;

    private DBConnectionParameter connectionParam;

    private DatabaseWizardPage databaseWizardPage;

    private ManagedDriver driver;

    private boolean canFinishFlag = true;

    /**
     * Constructor for DatabaseWizard. Analyse Iselection to extract DatabaseConnection and the pathToSave. Start the
     * Lock Strategy.
     * 
     * @param selection
     * @param existingNames
     */
    public DatabaseConnectionWizard(DBConnectionParameter connectionParam) {
        this.connectionParam = connectionParam;
    }

    /**
     * Adding the page to the wizard and set Title, Description and PageComplete.
     */
    @Override
    public void addPages() {
        String winTitle = isMdmFlag() ? DefaultMessagesImpl.getString("DatabaseConnectionWizard.mdmConnection") //$NON-NLS-1$
                : DefaultMessagesImpl.getString("DatabaseConnectionWizard.databaseConnection"); //$NON-NLS-1$

        setWindowTitle(winTitle);
        setDefaultPageImageDescriptor(ImageLib.getImageDescriptor(ImageLib.REFRESH_IMAGE));

        propertiesWizardPage = new DatabaseMetadataWizardPage();
        databaseWizardPage = new DatabaseWizardPage(this);
        databaseWizardPage.setMdmFlag(isMdmFlag());

        String propTitle = isMdmFlag() ? DefaultMessagesImpl.getString("DatabaseConnectionWizard.newMdmConnection") //$NON-NLS-1$
                : DefaultMessagesImpl.getString("DatabaseConnectionWizard.newDatabaseConnection"); //$NON-NLS-1$
        String propDesc = DefaultMessagesImpl.getString("DatabaseConnectionWizard.defineProperties"); //$NON-NLS-1$

        propertiesWizardPage.setTitle(propTitle);
        propertiesWizardPage.setDescription(propDesc);
        propertiesWizardPage.setPageComplete(false);

        String dataTitle = isMdmFlag() ? DefaultMessagesImpl.getString("DatabaseConnectionWizard.newMdmConnections") //$NON-NLS-1$
                : DefaultMessagesImpl.getString("DatabaseConnectionWizard.newDatabaseConnections"); //$NON-NLS-1$
        String dataDesc = isMdmFlag() ? DefaultMessagesImpl.getString("DatabaseConnectionWizard.defineMdm") : DefaultMessagesImpl //$NON-NLS-1$
                .getString("DatabaseConnectionWizard.defineDatabase"); //$NON-NLS-1$

        databaseWizardPage.setTitle(dataTitle);
        databaseWizardPage.setDescription(dataDesc);

        try {
            addPage(propertiesWizardPage);
            addPage(databaseWizardPage);
        } catch (Exception e) {
            log.error(e, e);
        }

    }

    public TypedReturnCode<Object> createAndSaveCWMFile(ModelElement cwmElement) {
        IFolder folder = connectionParam.getFolderProvider().getFolderResource();
        TypedReturnCode<Object> save = ElementWriterFactory.getInstance().createDataProviderWriter().create(cwmElement, folder);
        if (save.isOk()) {
            if (driver != null) {
                storeInfoToPerference(cwmElement);
            }
            CWMPlugin.getDefault().addConnetionAliasToSQLPlugin(cwmElement);
        }
        return save;
    }

    @Override
    public boolean performFinish() {
        this.canFinishFlag = false;
        boolean result = super.performFinish();
        this.canFinishFlag = true;
        return result;
    }

    @Override
    public boolean canFinish() {
        return databaseWizardPage == null ? false : databaseWizardPage.isPageComplete() && canFinishFlag;
    }

    public ModelElement initCWMResourceBuilder() {

        // DataProviderBuilder dpBuilder = new DataProviderBuilder();
        //
        // String driverPath = connectionParam.getDriverPath();
        // if (driverPath != null) {
        // LinkedList<String> jars = new LinkedList<String>();
        //            for (String driverpath : driverPath.split(";")) { //$NON-NLS-1$
        // jars.add(driverpath);
        // }
        //
        // // MOD xqliu 2009-12-03 bug 10247
        // String jdbcUrl = connectionParam.getJdbcUrl();
        // if (jdbcUrl != null && jdbcUrl.length() > 12) {
        // String name = jdbcUrl.substring(0, 12);
        // driver = dpBuilder.buildDriverForSQLExploer(name, connectionParam.getDriverClassName(), connectionParam
        // .getJdbcUrl(), jars);
        // }
        // // ~
        // }
        //
        // ReturnCode rc = dpBuilder.initializeDataProvider(connectionParam);
        //
        // MetadataFillFactory instance;
        // IMetadataConnection metaConnection = instance.fillUIParams(ParameterUtil.toMap(connectionParam));
        // ReturnCode rc1 = instance.checkConnection(metaConnection);
        // if (rc.isOk()) {
        //
        // // MOD xqliu 2010-10-13 bug 15756
        // DataProvider dataProvider = dpBuilder.getDataProvider();
        // try {DatabaseConnection dbConn = SwitchHelpers.DATABASECONNECTION_SWITCH.doSwitch(dataProvider);
        //
        // if (dbConn != null && dbConn.getDbVersionString() == null) {
        // dbConn.setDbVersionString(ConnectionUtils.createDatabaseVersionString(dbConn));
        // return dbConn;
        // }
        // } finally {
        // // if (sqlConn != null) {
        // // ConnectionUtils.closeConnection(sqlConn);
        // // }
        //
        // }
        // return dataProvider;

        // // 15756
        // } else {
        // // Need to add a dialog for report the reson of error
        // MessageDialog
        // .openInformation(
        // getShell(),
        //                            DefaultMessagesImpl.getString("DatabaseWizardPage.checkConnectionss"), DefaultMessagesImpl.getString("DatabaseWizardPage.checkFailure") //$NON-NLS-1$ //$NON-NLS-2$
        // + rc.getMessage());
        // }

        // MOD by zshen use new API to fill connetion and it's construct
        MetadataFillFactory instance = null;
        if (connectionParam.getSqlTypeName().equals(SupportDBUrlType.MDM.getDBKey())) {
            instance = MetadataFillFactory.getMDMInstance();
        } else {
            instance = MetadataFillFactory.getDBInstance();
        }

        IMetadataConnection metaConnection = instance.fillUIParams(ParameterUtil.toMap(connectionParam));
        ReturnCode rc = instance.createConnection(metaConnection);
        if (rc.isOk()) {
            Connection dbConn = instance.fillUIConnParams(metaConnection, null);
            DatabaseMetaData dbMetadata = null;
            List<String> packageFilter = ConnectionUtils.getPackageFilter(connectionParam);
            java.sql.Connection sqlConn = null;
            try {
                if (rc instanceof TypedReturnCode) {
                    Object sqlConnObject = ((TypedReturnCode) rc).getObject();
                    if (sqlConnObject instanceof java.sql.Connection) {
                        sqlConn = (java.sql.Connection) sqlConnObject;
                        dbMetadata = org.talend.utils.sql.ConnectionUtils.getConnectionMetadata(sqlConn);
                    }
                }

                instance.fillCatalogs(dbConn, dbMetadata, packageFilter);
                instance.fillSchemas(dbConn, dbMetadata, packageFilter);
                return dbConn;
            } catch (SQLException e) {
                log.error(e, e);
                // Need to add a dialog for report the reson of error
            } finally {
                if (sqlConn != null) {
                    ConnectionUtils.closeConnection(sqlConn);
                }

            }
        }
        return null;
    }

    @Override
    protected DBConnectionParameter getParameter() {
        return this.connectionParam;
    }

    @Override
    protected String getEditorName() {
        return ConnectionEditor.class.getName();
    }

    @Override
    protected ResourceFileMap getResourceFileMap() {
        // return PrvResourceFileHelper.getInstance();
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.talend.dataprofiler.core.ui.wizard.AbstractWizard#fillMetadataToCWMResource(orgomg.cwm.objectmodel.core.
     * ModelElement)
     */
    @Override
    public void fillMetadataToCWMResource(ModelElement cwmElement) {
        // MOD by zshen new API have been fill all of those attribute.
        // super.fillMetadataToCWMResource(cwmElement);
        // if (cwmElement instanceof DataProvider) {
        // Connection dataProvider = SwitchHelpers.CONNECTION_SWITCH.doSwitch(cwmElement);
        // if (dataProvider != null) {
        // ConnectionUtils.setServerName(dataProvider, getParameter().getHost());
        // ConnectionUtils.setPort(dataProvider, getParameter().getPort());
        // DatabaseConnection dbConnection = SwitchHelpers.DATABASECONNECTION_SWITCH.doSwitch(dataProvider);
        // if (dbConnection != null) {
        // dbConnection.setDatabaseType(getParameter().getSqlTypeName());
        // }
        // ConnectionUtils.setSID(dataProvider, getParameter().getDbName());
        // // ADD xqliu 2010-03-03 feature 11412
        // ConnectionHelper.setRetrieveAllMetadata(getParameter().isRetrieveAllMetadata(), dataProvider);
        // // ADD klliu 2010-10-11 feature 15821
        // ConnectionHelper.setOtherParameter(getParameter().getOtherParameter(), dataProvider);
        // } else {
        // MessageUI.openError("Connection is null!");
        // }
        // }
        // ~
    }

    private void storeInfoToPerference(ModelElement dataProvider) {
        if (connectionParam == null || driver == null || dataProvider == null) {
            return;
        }
        StringBuilder driverPara = new StringBuilder();
        if (CorePlugin.getDefault().getPreferenceStore().getString("JDBC_CONN_DRIVER") != null //$NON-NLS-1$
                && !CorePlugin.getDefault().getPreferenceStore().getString("JDBC_CONN_DRIVER").equals("")) { //$NON-NLS-1$ //$NON-NLS-2$
            driverPara.append(CorePlugin.getDefault().getPreferenceStore().getString("JDBC_CONN_DRIVER") + ";{" //$NON-NLS-1$ //$NON-NLS-2$
                    + connectionParam.getDriverPath().substring(0, connectionParam.getDriverPath().length() - 1) + "," //$NON-NLS-1$
                    + connectionParam.getDriverClassName() + "," + connectionParam.getJdbcUrl() + "," //$NON-NLS-1$ //$NON-NLS-2$
                    + dataProvider.eResource().getURI().toString() + "," + driver.getId() + "};"); //$NON-NLS-1$ //$NON-NLS-2$
        } else {
            driverPara.append("{" //$NON-NLS-1$
                    + connectionParam.getDriverPath().substring(0, connectionParam.getDriverPath().length() - 1) + "," //$NON-NLS-1$
                    + connectionParam.getDriverClassName() + "," + connectionParam.getJdbcUrl() + "," //$NON-NLS-1$ //$NON-NLS-2$
                    + dataProvider.eResource().getURI().toString() + "," + driver.getId() + "};"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        CorePlugin.getDefault().getPreferenceStore().putValue("JDBC_CONN_DRIVER", driverPara.toString()); //$NON-NLS-1$
    }

    private boolean isMdmFlag() {
        FolderProvider folderProvider = connectionParam.getFolderProvider();
        return folderProvider != null
                && ResourceService.isSubFolder(ResourceManager.getMDMConnectionFolder(), folderProvider.getFolderResource());
    }

    /**
     * ADD xqliu 2010-04-01 bug 12379.
     */
    @Override
    public ReturnCode checkMetadata() {
        if (getParameter() != null) {
            String dbName = getParameter().getDbName();
            boolean retrieveAll = getParameter().isRetrieveAllMetadata();
            if (!retrieveAll && (dbName == null || "".equals(dbName.trim()))) { //$NON-NLS-1$
                ReturnCode rc = new ReturnCode();
                rc.setMessage(DefaultMessagesImpl.getString("DatabaseConnectionWizard.dbnameEmpty")); //$NON-NLS-1$
                rc.setOk(false);
                return rc;
            }
        }
        return super.checkMetadata();
    }

    @Override
    public void openEditor(Item item) {
        ConnectionItemEditorInput connItemEditorInput = new ConnectionItemEditorInput(item);
        CorePlugin.getDefault().openEditor(connItemEditorInput, ConnectionEditor.class.getName());

    }
}
