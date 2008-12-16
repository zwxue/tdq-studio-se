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
package org.talend.dataprofiler.core.ui.wizard.database;

import java.util.LinkedList;

import net.sourceforge.sqlexplorer.dbproduct.ManagedDriver;
import net.sourceforge.sqlexplorer.plugin.SQLExplorerPlugin;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.dialogs.MessageDialog;
import org.talend.cwm.management.api.ConnectionService;
import org.talend.cwm.management.api.DqRepositoryViewService;
import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.editor.connection.ConnectionEditor;
import org.talend.dataprofiler.core.ui.views.DQRespositoryView;
import org.talend.dataprofiler.core.ui.wizard.AbstractWizard;
import org.talend.dq.analysis.parameters.ConnectionParameter;
import org.talend.dq.analysis.parameters.DBConnectionParameter;
import org.talend.dq.helper.resourcehelper.PrvResourceFileHelper;
import org.talend.utils.sugars.TypedReturnCode;

/**
 * DatabaseWizard present the DatabaseForm. Use to manage the metadata connection.
 */
public class DatabaseConnectionWizard extends AbstractWizard {

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
            e.printStackTrace();
        }

    }

    /**
     * This method is called when 'Finish' button is pressed in the wizard. Save metadata close Lock Strategy and close
     * wizard.
     */
    public boolean performFinish() {
        if (connectionParam.getDriverPath() != null) {
            int repeatDriverCount = 0;
            driver = new ManagedDriver(SQLExplorerPlugin.getDefault().getDriverModel().createUniqueId());

            // CorePlugin.getDefault().getPreferenceStore().putValue("DRIVERPATHS", connectionParam.getDriverPath());
            // CorePlugin.getDefault().getPreferenceStore().putValue("DRIVERNAME",
            // connectionParam.getDriverClassName());
            // CorePlugin.getDefault().getPreferenceStore().putValue("DRIVERURL", connectionParam.getJdbcUrl());
            LinkedList<String> driverFile = new LinkedList<String>();
            for (String driverpath : connectionParam.getDriverPath().split(";")) {
                driverFile.add(driverpath);
            }
            String newDriverName = connectionParam.getJdbcUrl().substring(0, 10);
            for (ManagedDriver checkedDriver : SQLExplorerPlugin.getDefault().getDriverModel().getDrivers()) {
                String driverName = checkedDriver.getName();
                if (driverName != null && driverName.trim().equals(newDriverName.trim())) {
                    newDriverName += repeatDriverCount++;
                }
            }
            driver.setName(connectionParam.getJdbcUrl().substring(0, 12));
            driver.setJars(driverFile);
            driver.setDriverClassName(connectionParam.getDriverClassName());
            driver.setUrl(connectionParam.getJdbcUrl());
            SQLExplorerPlugin.getDefault().getDriverModel().addDriver(driver);

        }
        TypedReturnCode<TdDataProvider> rc = ConnectionService.createConnection(this.connectionParam);
        if (!rc.isOk()) {
            SQLExplorerPlugin.getDefault().getDriverModel().removeDriver(driver);
            MessageDialog
                    .openInformation(
                            getShell(),
                            DefaultMessagesImpl.getString("DatabaseConnectionWizard.createConnections"), DefaultMessagesImpl.getString("DatabaseConnectionWizard.createConnectionFailure") + rc.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$
            return false;
        }
        TdDataProvider dataProvider = rc.getObject();

        // MODSCA 2008-03-10 save the provider
        IFile returnFile = DqRepositoryViewService.saveDataProviderAndStructure(dataProvider, this.connectionParam
                .getFolderProvider());
        PrvResourceFileHelper.getInstance().register(returnFile, dataProvider.eResource());
        if (returnFile != null) {
            CorePlugin.getDefault().refreshWorkSpace();
            ((DQRespositoryView) CorePlugin.getDefault().findView(DQRespositoryView.ID)).getCommonViewer().refresh();
            CorePlugin.getDefault().openEditor(returnFile, ConnectionEditor.class.getName());
        }
        if (connectionParam.getDriverPath() != null) {
            StringBuilder driverPara = new StringBuilder();
            if (CorePlugin.getDefault().getPreferenceStore().getString("JDBC_CONN_DRIVER") != null
                    && !CorePlugin.getDefault().getPreferenceStore().getString("JDBC_CONN_DRIVER").equals("")) {
                driverPara.append(CorePlugin.getDefault().getPreferenceStore().getString("JDBC_CONN_DRIVER") + ";{"
                        + connectionParam.getDriverPath().substring(0, connectionParam.getDriverPath().length() - 1) + ","
                        + connectionParam.getDriverClassName() + "," + connectionParam.getJdbcUrl() + ","
                        + dataProvider.eResource().getURI().toString() + "," + driver.getId() + "};");
            } else {
                driverPara.append("{"
                        + connectionParam.getDriverPath().substring(0, connectionParam.getDriverPath().length() - 1) + ","
                        + connectionParam.getDriverClassName() + "," + connectionParam.getJdbcUrl() + ","
                        + dataProvider.eResource().getURI().toString() + "," + driver.getId() + "};");
            }
            CorePlugin.getDefault().getPreferenceStore().putValue("JDBC_CONN_DRIVER", driverPara.toString());
        }

        return true;
    }

    @Override
    protected ConnectionParameter getConnectionParameter() {

        return this.connectionParam;
    }
}
