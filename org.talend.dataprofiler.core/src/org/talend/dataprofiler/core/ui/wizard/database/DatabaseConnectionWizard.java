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
package org.talend.dataprofiler.core.ui.wizard.database;

import java.util.LinkedList;

import net.sourceforge.sqlexplorer.dbproduct.ManagedDriver;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.jface.dialogs.MessageDialog;
import org.talend.cwm.helper.DataProviderHelper;
import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.cwm.softwaredeployment.TdProviderConnection;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.editor.connection.ConnectionEditor;
import org.talend.dataprofiler.core.ui.utils.MessageUI;
import org.talend.dataprofiler.core.ui.wizard.AbstractWizard;
import org.talend.dq.analysis.parameters.DBConnectionParameter;
import org.talend.dq.connection.DataProviderBuilder;
import org.talend.dq.helper.resourcehelper.PrvResourceFileHelper;
import org.talend.dq.helper.resourcehelper.ResourceFileMap;
import org.talend.dq.writer.impl.ElementWriterFactory;
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
        setWindowTitle(DefaultMessagesImpl.getString("DatabaseConnectionWizard.databaseConnection")); //$NON-NLS-1$
        setDefaultPageImageDescriptor(ImageLib.getImageDescriptor(ImageLib.REFRESH_IMAGE));

        propertiesWizardPage = new DatabaseMetadataWizardPage();
        databaseWizardPage = new DatabaseWizardPage();

        propertiesWizardPage.setTitle(DefaultMessagesImpl.getString("DatabaseConnectionWizard.newDatabaseConnection")); //$NON-NLS-1$
        propertiesWizardPage.setDescription(DefaultMessagesImpl.getString("DatabaseConnectionWizard.defineProperties")); //$NON-NLS-1$
        propertiesWizardPage.setPageComplete(false);

        databaseWizardPage.setTitle(DefaultMessagesImpl.getString("DatabaseConnectionWizard.databaseConnections")); //$NON-NLS-1$
        databaseWizardPage.setDescription(DefaultMessagesImpl.getString("DatabaseConnectionWizard.newDatabaseConnections")); //$NON-NLS-1$

        try {
            addPage(propertiesWizardPage);
            addPage(databaseWizardPage);
        } catch (Exception e) {
            log.error(e, e);
        }

    }

    public TypedReturnCode<IFile> createAndSaveCWMFile(ModelElement cwmElement) {
        TdDataProvider dataProvider = (TdDataProvider) cwmElement;

        IFolder folder = connectionParam.getFolderProvider().getFolderResource();
        TypedReturnCode<IFile> save = ElementWriterFactory.getInstance().createDataProviderWriter().create(dataProvider, folder);
        if (save.isOk()) {
            if (driver != null) {
                storeInfoToPerference(dataProvider);
            }
            CorePlugin.getDefault().addConnetionAliasToSQLPlugin(dataProvider);
        }

        return save;
    }

    public ModelElement initCWMResourceBuilder() {

        DataProviderBuilder dpBuilder = new DataProviderBuilder();

        String driverPath = connectionParam.getDriverPath();
        if (driverPath != null) {
            LinkedList<String> jars = new LinkedList<String>();
            for (String driverpath : driverPath.split(";")) { //$NON-NLS-1$
                jars.add(driverpath);
            }

            String name = connectionParam.getJdbcUrl().substring(0, 12);
            driver = dpBuilder.buildDriverForSQLExploer(name, connectionParam.getDriverClassName(), connectionParam.getJdbcUrl(),
                    jars);
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
        return PrvResourceFileHelper.getInstance();
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

            DataProvider dataProvider = (DataProvider) cwmElement;
            TypedReturnCode<TdProviderConnection> rc = DataProviderHelper.getTdProviderConnection(dataProvider);
            if (rc.getObject() != null) {
                TdProviderConnection connection = rc.getObject();
                DataProviderHelper.setHost(getParameter().getHost(), connection);
                DataProviderHelper.setPort(getParameter().getPort(), connection);
                DataProviderHelper.setDBType(getParameter().getSqlTypeName(), connection);
                DataProviderHelper.setDBName(getParameter().getDbName(), connection);
            } else {
                MessageUI.openError(rc.getMessage());
            }
        }
    }

    private void storeInfoToPerference(TdDataProvider dataProvider) {
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
}
