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

import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.talend.cwm.management.api.ConnectionService;
import org.talend.cwm.management.api.DqRepositoryViewService;
import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.ui.editor.connection.ConnectionEditor;
import org.talend.dataprofiler.core.ui.views.DQRespositoryView;
import org.talend.dataprofiler.core.ui.wizard.AbstractWizardPage;
import org.talend.dq.analysis.parameters.DBConnectionParameter;
import org.talend.utils.sugars.TypedReturnCode;

/**
 * DatabaseWizard present the DatabaseForm. Use to manage the metadata connection.
 */
public class DatabaseConnectionWizard extends Wizard implements INewWizard {

    private boolean creation;

    private DatabaseMetadataWizardPage propertiesWizardPage;

    private DBConnectionParameter connectionProperty;

    private DatabaseWizardPage databaseWizardPage;

    /**
     * Constructor for DatabaseWizard. Analyse Iselection to extract DatabaseConnection and the pathToSave. Start the
     * Lock Strategy.
     * 
     * @param selection
     * @param existingNames
     */
    public DatabaseConnectionWizard(IWorkbench workbench, boolean creation, IStructuredSelection selection, String[] existingNames) {
        super();
        this.init(workbench, selection);
        this.creation = creation;
    }

    public void init(IWorkbench workbench, IStructuredSelection selection) {

        connectionProperty = new DBConnectionParameter();
        connectionProperty.setParameters(new Properties());
        AbstractWizardPage.setConnectionParams(connectionProperty);
    }

    /**
     * Adding the page to the wizard and set Title, Description and PageComplete.
     */
    public void addPages() {
        setWindowTitle("Database Connection");
        setDefaultPageImageDescriptor(ImageLib.getImageDescriptor(ImageLib.REFRESH_IMAGE));

        propertiesWizardPage = new DatabaseMetadataWizardPage();
        databaseWizardPage = new DatabaseWizardPage();

        if (creation) {
            propertiesWizardPage.setTitle("New Database Connection on repository - Step 1/2");
            propertiesWizardPage.setDescription("Define the properties");
            propertiesWizardPage.setPageComplete(false);

            databaseWizardPage.setTitle("Database connection");
            databaseWizardPage.setDescription("New database connection on repository - step 2/2"); //$NON-NLS-1$
            // //$NON-NLS-1$
            // databaseWizardPage.setPageComplete(false);
        } else {
            propertiesWizardPage.setTitle("Update Database Connection - Step 1/2");
            propertiesWizardPage.setDescription("Update properties");
            // propertiesWizardPage.setPageComplete(isRepositoryObjectEditable());

            // databaseWizardPage.setTitle(Messages.getString("DatabaseWizardPage.titleUpdate.Step2")); //$NON-NLS-1$
            // databaseWizardPage.setDescription(Messages.getString("DatabaseWizardPage.descriptionUpdate.Step2"));
            // //$NON-NLS-1$
            // databaseWizardPage.setPageComplete(isRepositoryObjectEditable());
        }
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
        TypedReturnCode<TdDataProvider> rc = ConnectionService.createConnection(this.connectionProperty);
        if (!rc.isOk()) {
            MessageDialog.openInformation(getShell(), "Create  connections", "Create connection failure:" + rc.getMessage());
            return false;
        }
        TdDataProvider dataProvider = rc.getObject();
        // MODSCA 2008-03-10 save the provider
        IFile returnFile = DqRepositoryViewService.saveDataProviderAndStructure(dataProvider, this.connectionProperty
                .getFolderProvider());
        if (returnFile != null) {
            CorePlugin.getDefault().refreshWorkSpace();
            ((DQRespositoryView) CorePlugin.getDefault().findView(DQRespositoryView.ID)).getCommonViewer().refresh();
            CorePlugin.getDefault().openEditor(returnFile, ConnectionEditor.class.getName());
        }
        return true;
    }
}
