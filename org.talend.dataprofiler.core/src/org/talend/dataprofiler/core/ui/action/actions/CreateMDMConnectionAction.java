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
package org.talend.dataprofiler.core.ui.action.actions;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.ui.PlatformUI;
import org.talend.core.GlobalServiceRegister;
import org.talend.core.PluginChecker;
import org.talend.core.ui.IMDMProviderService;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.ui.action.AbstractMetadataCreationAction;
import org.talend.repository.model.RepositoryNode;


/**
 * DOC bZhou  class global comment. Detailled comment
 */
public class CreateMDMConnectionAction extends AbstractMetadataCreationAction {

    /**
     * DOC bZhou CreateMDMConnectionAction constructor comment.
     * @param node
     */
    public CreateMDMConnectionAction(RepositoryNode node) {
        super(node);
    }

    /* (non-Javadoc)
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

        if (PluginChecker.isMDMPluginLoaded() && GlobalServiceRegister.getDefault().isServiceRegistered(IMDMProviderService.class)) {
            IMDMProviderService service = (IMDMProviderService) GlobalServiceRegister.getDefault().getService(IMDMProviderService.class);
            if (service != null) {
                return service.newMDMWizard(PlatformUI.getWorkbench(), true, node, getExistingNames());
            }
        }

        return null;
    }

    /* (non-Javadoc)
     * @see org.talend.dataprofiler.core.ui.action.AbstractMetadataCreationAction#getActionImage()
     */
    @Override
    protected ImageDescriptor getActionImage() {
        return ImageLib.createAddedIcon(ImageLib.MDM_CONNECTION);
    }

    /* (non-Javadoc)
     * @see org.talend.dataprofiler.core.ui.action.AbstractMetadataCreationAction#getActionLabel()
     */
    @Override
    protected String getActionLabel() {
        return "Create MDM Conntion";
    }

}
