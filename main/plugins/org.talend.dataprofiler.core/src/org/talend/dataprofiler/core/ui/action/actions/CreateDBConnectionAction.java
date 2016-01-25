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

import org.apache.commons.lang.math.NumberUtils;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.cheatsheets.ICheatSheetManager;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.action.AbstractMetadataCreationAction;
import org.talend.dq.helper.RepositoryNodeHelper;
import org.talend.repository.model.RepositoryNode;
import org.talend.repository.ui.wizards.metadata.connection.database.DatabaseWizard;
import org.talend.resource.EResourceConstant;

/**
 * DOC zqin class global comment. Detailled comment <br/>
 * 
 * $Id: talend.epf 1 2006-09-29 17:06:40Z zqin $
 * 
 */
public class CreateDBConnectionAction extends AbstractMetadataCreationAction {

    private static final int MDM_CONNECTION_TYPE_VALUE = 1;

    public CreateDBConnectionAction(RepositoryNode node) {
        super(node);
    }

    public CreateDBConnectionAction() {
        // MOD by zshen for bug 15750 TODO 39(9) create connection exception by cheat sheet
        RepositoryNode node = (RepositoryNode) RepositoryNodeHelper.getMetadataFolderNode(EResourceConstant.DB_CONNECTIONS);
        super.node = node;
    }

    @Override
    protected void initCheateSheetRun(String[] params, ICheatSheetManager manager) {
        if (params == null || params.length == 0) {
            return;
        }
        Integer connectionType = null;
        if (NumberUtils.isNumber(params[0])) {
            connectionType = NumberUtils.toInt(params[0]);
        }
        switch (connectionType) {
        case MDM_CONNECTION_TYPE_VALUE:
            // this.node = ResourceManager.getMDMConnectionFolder();
            break;
        default:
            // this.node = ResourceManager.getConnectionFolder();
        }
    }

    @Override
    protected IWizard createWizard() {
        return new DatabaseWizard(PlatformUI.getWorkbench(), true, node, getExistingNames());
    }

    @Override
    protected String getActionLabel() {
        return DefaultMessagesImpl.getString("CreateDBConnectionAction.createConnection"); //$NON-NLS-1$
    }

    @Override
    protected ImageDescriptor getActionImage() {
        return ImageLib.getImageDescriptor(ImageLib.NEW_CONNECTION);
    }
}
