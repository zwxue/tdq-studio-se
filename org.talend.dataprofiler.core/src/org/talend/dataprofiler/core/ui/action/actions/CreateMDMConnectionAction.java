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
package org.talend.dataprofiler.core.ui.action.actions;

import java.util.Properties;

import org.eclipse.core.resources.IFolder;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.IWizard;
import org.talend.cwm.management.api.FolderProvider;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.ui.action.AbstractMetadataCreationAction;
import org.talend.dataprofiler.core.ui.utils.WorkbenchUtils;
import org.talend.dataprofiler.core.ui.wizard.database.DatabaseConnectionWizard;
import org.talend.dq.analysis.parameters.DBConnectionParameter;
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
        DBConnectionParameter connectionParam = new DBConnectionParameter();
        connectionParam.setParameters(new Properties());

        IFolder folder = WorkbenchUtils.getFolder(node);
        if (folder != null) {
            FolderProvider provider = new FolderProvider();
            provider.setFolderResource(folder);
            connectionParam.setFolderProvider(provider);
        }

        return new DatabaseConnectionWizard(connectionParam);
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
