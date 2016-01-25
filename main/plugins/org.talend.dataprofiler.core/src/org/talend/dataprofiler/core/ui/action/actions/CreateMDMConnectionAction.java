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

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.ui.PlatformUI;
import org.talend.core.GlobalServiceRegister;
import org.talend.core.PluginChecker;
import org.talend.core.ui.IMDMProviderService;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.action.AbstractMetadataCreationAction;
import org.talend.dq.helper.RepositoryNodeHelper;
import org.talend.repository.model.RepositoryNode;
import org.talend.resource.EResourceConstant;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public class CreateMDMConnectionAction extends AbstractMetadataCreationAction {

    /**
     * DOC bZhou CreateMDMConnectionAction constructor comment.
     * 
     * @param node
     */
    public CreateMDMConnectionAction(RepositoryNode node) {
        super(node);
    }

    public CreateMDMConnectionAction() {
        // MOD qiongli bug 18642,create mdm connection by cheat sheet
        RepositoryNode node = (RepositoryNode) RepositoryNodeHelper.getMetadataFolderNode(EResourceConstant.MDM_CONNECTIONS);
        super.node = node;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.action.AbstractMetadataCreationAction#createWizard()
     */
    @Override
    protected IWizard createWizard() {
        // DBConnectionParameter connectionParam = new DBConnectionParameter();
        // connectionParam.setParameters(new Properties());
        //
        // IFolder folder = WorkbenchUtils.getFolder(node);
        // if (folder != null) {
        // FolderProvider provider = new FolderProvider();
        // provider.setFolderResource(folder);
        // connectionParam.setFolderProvider(provider);
        // }
        //
        // return new DatabaseConnectionWizard(connectionParam);

        if (PluginChecker.isMDMPluginLoaded()
                && GlobalServiceRegister.getDefault().isServiceRegistered(IMDMProviderService.class)) {
            IMDMProviderService service = (IMDMProviderService) GlobalServiceRegister.getDefault().getService(
                    IMDMProviderService.class);
            if (service != null) {
                return service.newWizard(PlatformUI.getWorkbench(), true, node, getExistingNames());
            }
        }

        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.action.AbstractMetadataCreationAction#getActionImage()
     */
    @Override
    protected ImageDescriptor getActionImage() {
        return ImageLib.createAddedIcon(ImageLib.MDM_CONNECTION);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.action.AbstractMetadataCreationAction#getActionLabel()
     */
    @Override
    protected String getActionLabel() {
        return DefaultMessagesImpl.getString("CreateMDMConnection"); //$NON-NLS-1$
    }

}
