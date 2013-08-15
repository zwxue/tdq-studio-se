// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.wizard.analysis.connection;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;
import org.talend.core.GlobalServiceRegister;
import org.talend.core.ITDQRepositoryService;
import org.talend.core.PluginChecker;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.properties.ConnectionItem;
import org.talend.core.ui.IMDMProviderService;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.events.EventEnum;
import org.talend.dataprofiler.core.ui.events.EventManager;
import org.talend.dq.helper.RepositoryNodeHelper;
import org.talend.repository.model.RepositoryNode;
import org.talend.repository.ui.wizards.metadata.connection.database.DatabaseWizard;
import org.talend.repository.ui.wizards.metadata.connection.files.delimited.DelimitedFileWizard;
import org.talend.resource.EResourceConstant;


/**
 * DOC yyin  class global comment. Detailled comment
 */
public class ConnectionTypePage extends WizardPage {

    private String defaultInfor = DefaultMessagesImpl.getString("ConnnectionTypePage.defaultInfor"); //$NON-NLS-1$

    private CCombo connectionType;

    private Composite relatedComposite;

    /**
     * DOC yyin ConnectionTypePage constructor comment.
     * 
     * @param pageName
     */
    protected ConnectionTypePage(String pageName) {
        super(pageName);
    }

    public ConnectionTypePage(Composite relatedComposite) {
        super("");
        this.relatedComposite = relatedComposite;
    }

    /*
     * Create the composites, initialize it and add controls.
     */
    public void createControl(Composite parent) {
        // create the dropdown list to select the connection type
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new GridLayout());

        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.heightHint = 70;
        Label inforLabel = new Label(container, SWT.BORDER);
        inforLabel.setText(defaultInfor);
        inforLabel.setLayoutData(gd);

        // COnnection type
        Label statusLab = new Label(container, SWT.NONE);
        statusLab.setText("Status"); //$NON-NLS-1$

        connectionType = new CCombo(container, SWT.BORDER);

        connectionType.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        connectionType.setEditable(false);

        // String[] connectionTypeList = { EResourceConstant.DB_CONNECTIONS.getName(),
        // EResourceConstant.MDM_CONNECTIONS.getName(),
        // EResourceConstant.FILEDELIMITED.getName() };

        connectionType.add(EResourceConstant.DB_CONNECTIONS.getName(), 0);
        connectionType.add(EResourceConstant.MDM_CONNECTIONS.getName(), 1);
        connectionType.add(EResourceConstant.FILEDELIMITED.getName(), 2);
        connectionType.select(0);
        // connectionType.setItems(connectionTypeList);
        // connectionType.setText(connectionTypeList[0]);
        // connectionType.addModifyListener(new ModifyListener() {
        //
        // public void modifyText(ModifyEvent e) {
        // Object data = e.data;
        //
        // }
        //
        // });

        setControl(container);
        super.setPageComplete(true);
    }

    public int getConnectionType() {
        connectionType.getSelectionIndex();
        if (EResourceConstant.DB_CONNECTIONS.getName().equals(connectionType.getText())) {
            return 0;
        } else if (EResourceConstant.MDM_CONNECTIONS.getName().equals(connectionType.getText())) {
            return 1;
        } else {
            return 2;
        }
    }

    @Override
    public boolean canFlipToNextPage() {
        return true;
    }

    @Override
    public IWizardPage getNextPage() {
        RepositoryNode node = null;
        IWizard nextWizard = null;
        // make the next wizard do not open the created connection
        ITDQRepositoryService tdqRepService = (ITDQRepositoryService) GlobalServiceRegister.getDefault().getService(
                ITDQRepositoryService.class);
        tdqRepService.setNoNeedToOpenConnectionEditor(Boolean.TRUE);

        switch (getConnectionType()) {
        case 0:// db
            node = (RepositoryNode) RepositoryNodeHelper.getMetadataFolderNode(EResourceConstant.DB_CONNECTIONS);
            nextWizard = new DatabaseWizard(PlatformUI.getWorkbench(), true, node, null);
            break;
        case 1:// MDM
            node = (RepositoryNode) RepositoryNodeHelper.getMetadataFolderNode(EResourceConstant.MDM_CONNECTIONS);
            if (PluginChecker.isMDMPluginLoaded()
                    && GlobalServiceRegister.getDefault().isServiceRegistered(IMDMProviderService.class)) {
                IMDMProviderService service = (IMDMProviderService) GlobalServiceRegister.getDefault().getService(
                        IMDMProviderService.class);
                if (service != null) {
                    nextWizard = service.newWizard(PlatformUI.getWorkbench(), true, node, null);
                }
            }
            break;
        case 2: // file
            node = (RepositoryNode) RepositoryNodeHelper.getMetadataFolderNode(EResourceConstant.FILEDELIMITED);
            nextWizard = new DelimitedFileWizard(PlatformUI.getWorkbench(), true, node, null);
            break;
        }
        WizardDialog dialog = new WizardDialog(null, nextWizard);
        dialog.setPageSize(550, 550);
        if (WizardDialog.OK == dialog.open()) {
            publishSelectDataEvent(nextWizard, tdqRepService);
        }
        return null;
    }

    private void publishSelectDataEvent(IWizard nextWizard, ITDQRepositoryService tdqRepService) {
        ConnectionItem connectionItem = ((DatabaseWizard) nextWizard).getConnectionItem();
        Connection connection = connectionItem.getConnection();
        ((WizardDialog) getWizard().getContainer()).close();
        // make it back to initial value after finish creating
        tdqRepService.setNoNeedToOpenConnectionEditor(Boolean.FALSE);

        EventManager.getInstance().publish(relatedComposite, EventEnum.DQ_MATCH_ANALYSIS_AFTER_CREATE_CONNECTION, connection);
    }

    public void checkForCompletion() {
        setPageComplete(false);

        getWizard().getContainer().updateButtons();

    }
}
