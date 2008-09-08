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

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.dialogs.MessageDialog;
import org.talend.cwm.management.api.ConnectionService;
import org.talend.cwm.management.api.DqRepositoryViewService;
import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.helper.PrvResourceFileHelper;
import org.talend.dataprofiler.core.ui.editor.connection.ConnectionEditor;
import org.talend.dataprofiler.core.ui.views.DQRespositoryView;
import org.talend.dataprofiler.core.ui.wizard.AbstractWizard;
import org.talend.dq.analysis.parameters.ConnectionParameter;
import org.talend.dq.analysis.parameters.DBConnectionParameter;
import org.talend.utils.sugars.TypedReturnCode;

/**
 * DatabaseWizard present the DatabaseForm. Use to manage the metadata connection.
 */
public class DatabaseConnectionWizard extends AbstractWizard {

    private DatabaseMetadataWizardPage propertiesWizardPage;

    private DBConnectionParameter connectionParam;

    private DatabaseWizardPage databaseWizardPage;

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
        setWindowTitle("Database Connection");
        setDefaultPageImageDescriptor(ImageLib.getImageDescriptor(ImageLib.REFRESH_IMAGE));

        propertiesWizardPage = new DatabaseMetadataWizardPage();
        databaseWizardPage = new DatabaseWizardPage();

        propertiesWizardPage.setTitle("New Database Connection on repository - Step 1/2");
        propertiesWizardPage.setDescription("Define the properties");
        propertiesWizardPage.setPageComplete(false);

        databaseWizardPage.setTitle("Database connection");
        databaseWizardPage.setDescription("New database connection on repository - step 2/2");

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
        TypedReturnCode<TdDataProvider> rc = ConnectionService.createConnection(this.connectionParam);
        if (!rc.isOk()) {
            MessageDialog.openInformation(getShell(), "Create  connections", "Create connection failure:" + rc.getMessage());
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
        return true;
    }

    @Override
    protected ConnectionParameter getConnectionParameter() {

        return this.connectionParam;
    }
}
