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
package org.talend.dataprofiler.core.ui.action.provider;

import org.eclipse.core.resources.IFolder;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.TreeSelection;
import org.talend.core.model.properties.Item;
import org.talend.dataprofiler.core.ui.action.actions.DeleteObjectsAction;
import org.talend.dataprofiler.core.ui.utils.WorkbenchUtils;
import org.talend.dataquality.properties.TDQIndicatorDefinitionItem;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.IRepositoryNode.ENodeType;
import org.talend.repository.model.RepositoryNode;
import org.talend.resource.EResourceConstant;

/**
 * DOC rli class global comment. Detailled comment
 */
public class DeleteResourceProvider extends AbstractCommonActionProvider {

    /**
     * Adds a submenu to the given menu with the name "New Component".
     */
    public void fillContextMenu(IMenuManager menu) {
        // MOD mzhao user readonly role on svn repository mode.
        if (!isShowMenu()) {
            return;
        }
        Object obj = ((TreeSelection) this.getContext().getSelection()).getFirstElement();
        if (obj instanceof IRepositoryNode) {
            RepositoryNode node = (RepositoryNode) obj;
            if (!node.getType().equals(ENodeType.SYSTEM_FOLDER)) {
                if (!isSystemIndicator(node)) {
                    menu.add(new DeleteObjectsAction());
                }
            }
        }
    }

    private boolean isSystemIndicator(RepositoryNode node) {
        switch (node.getType()) {
        case SYSTEM_FOLDER:
        case SIMPLE_FOLDER:
            IFolder ifolder = WorkbenchUtils.getFolder(node);
            return ifolder.getFullPath().toOSString().contains(EResourceConstant.SYSTEM_INDICATORS.getName());
        case TDQ_REPOSITORY_ELEMENT:
        case REPOSITORY_ELEMENT:
            Item item = node.getObject().getProperty().getItem();
            return item instanceof TDQIndicatorDefinitionItem;

        }
        return false;
    }

}
