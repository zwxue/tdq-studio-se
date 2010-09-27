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
package org.talend.dataprofiler.core.ui.wizard.database;

import java.util.LinkedList;

import net.sourceforge.sqlexplorer.dbproduct.ManagedDriver;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFolder;
import org.eclipse.jface.dialogs.MessageDialog;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.metadata.builder.connection.DatabaseConnection;
import org.talend.cwm.db.connection.ConnectionUtils;
import org.talend.cwm.helper.ConnectionHelper;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.cwm.management.api.FolderProvider;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.editor.connection.ConnectionEditor;
import org.talend.dataprofiler.core.ui.utils.MessageUI;
import org.talend.dataprofiler.core.ui.wizard.AbstractWizard;
import org.talend.dq.CWMPlugin;
import org.talend.dq.analysis.parameters.DBConnectionParameter;
import org.talend.dq.connection.DataProviderBuilder;
import org.talend.dq.helper.ProxyRepositoryViewObject;
import org.talend.dq.helper.resourcehelper.ResourceFileMap;
import org.talend.dq.writer.impl.ElementWriterFactory;
import org.talend.resource.ResourceManager;
import org.talend.resource.ResourceService;
import org.talend.utils.sugars.ReturnCode;
import org.talend.utils.sugars.TypedReturnCode;
import orgomg.cwm.foundation.softwaredeployment.DataProvider;
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
    public void addPages() {
        String winTitle = isMdmFlag() ? DefaultMessagesImpl.getString("DatabaseConnectionWizard.mdmConnection")
                : DefaultMessagesImpl.getString("DatabaseConnectionWizard.databaseConnection");

        setWindowTitle(winTitle); //$NON-NLS-1$
        setDefaultPageImageDescriptor(ImageLib.getImageDescriptor(ImageLib.REFRESH_IMAGE));

        propertiesWizardPage = new DatabaseMetadataWizardPage();
        databaseWizardPage = new DatabaseWizardPage(this);
        databaseWizardPage.setMdmFlag(isMdmFlag());

        String propTitle = isMdmFlag() ? DefaultMessagesImpl.getString("DatabaseConnectionWizard.newMdmConnection")
                : DefaultMessagesImpl.getString("DatabaseConnectionWizard.newDatabaseConnection");
        String propDesc = DefaultMessagesImpl.getString("DatabaseConnectionWizard.defineProperties");

        propertiesWizardPage.setTitle(propTitle); //$NON-NLS-1$
        propertiesWizardPage.setDescription(propDesc); //$NON-NLS-1$
        propertiesWizardPage.setPageComplete(false);

        String dataTitle = isMdmFlag() ? DefaultMessagesImpl.getString("DatabaseConnectionWizard.newMdmConnections")
                : DefaultMessagesImpl.getString("DatabaseConnectionWizard.newDatabaseConnections");
        String dataDesc = isMdmFlag() ? DefaultMessagesImpl.getString("DatabaseConnectionWizard.defineMdm") : DefaultMessagesImpl
                .getString("DatabaseConnectionWizard.defineDatabase");

        databaseWizardPage.setTitle(dataTitle); //$NON-NLS-1$
        databaseWizardPage.setDescription(dataDesc); //$NON-NLS-1$

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
        TypedReturnCode<Object> reposViewObjRC = new TypedReturnCode<Object>();
        ProxyRepositoryViewObject.fetchAllRepositoryViewObjects(Boolean.TRUE);
        reposViewObjRC.setObject(ProxyRepositoryViewObject.getRepositoryViewObject((Connection) cwmElement));
        return reposViewObjRC;
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

        DataProviderBuilder dpBuilder = new DataProviderBuilder();

        String driverPath = connectionParam.getDriverPath();
        if (driverPath != null) {
            LinkedList<String> jars = new LinkedList<String>();
            for (String driverpath : driverPath.split(";")) { //$NON-NLS-1$
                jars.add(driverpath);
            }

            // MOD xqliu 2009-12-03 bug 10247
            String jdbcUrl = connectionParam.getJdbcUrl();
            if (jdbcUrl != null && jdbcUrl.length() > 12) {
                String name = jdbcUrl.substring(0, 12);
                driver = dpBuilder.buildDriverForSQLExploer(name, connectionParam.getDriverClassName(), connectionParam
                        .getJdbcUrl(), jars);
            }
            // ~
        }

        ReturnCode rc = dpBuilder.initializeDataProvider(connectionParam);

        if (rc.isOk()) {
            return dpBuilder.getDataProvider();
        } else {
            MessageDialog
                    .openInformation(
                            getShell(),
                            DefaultMessagesImpl.getString("DatabaseWizardPage.checkConnectionss"), DefaultMessagesImpl.getString("DatabaseWizardPage.checkFailure") //$NON-NLS-1$ //$NON-NLS-2$
                                    + rc.getMessage());
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
        super.fillMetadataToCWMResource(cwmElement);
        if (cwmElement instanceof DataProvider) {
            Connection dataProvider = SwitchHelpers.CONNECTION_SWITCH.doSwitch(cwmElement);
            if (dataProvider != null) {
                ConnectionUtils.setServerName(dataProvider, getParameter().getHost());
                ConnectionUtils.setPort(dataProvider, getParameter().getPort());
                DatabaseConnection dbConnection = SwitchHelpers.DATABASECONNECTION_SWITCH.doSwitch(dataProvider);
                if (dbConnection != null) {
                    dbConnection.setDatabaseType(getParameter().getSqlTypeName());
                }
                ConnectionUtils.setSID(dataProvider, getParameter().getDbName());
                // ADD xqliu 2010-03-03 feature 11412
                ConnectionHelper.setRetrieveAllMetadata(getParameter().isRetrieveAllMetadata(), dataProvider);
            } else {
                MessageUI.openError("Connection is null!");
            }
        }
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
    public ReturnCode checkMetadata() {
        if (getParameter() != null) {
            String dbName = getParameter().getDbName();
            boolean retrieveAll = getParameter().isRetrieveAllMetadata();
            if (!retrieveAll && (dbName == null || "".equals(dbName.trim()))) {
                ReturnCode rc = new ReturnCode();
                rc.setMessage(DefaultMessagesImpl.getString("DatabaseConnectionWizard.dbnameEmpty")); //$NON-NLS-1$
                rc.setOk(false);
                return rc;
            }
        }
        return super.checkMetadata();
    }
}
