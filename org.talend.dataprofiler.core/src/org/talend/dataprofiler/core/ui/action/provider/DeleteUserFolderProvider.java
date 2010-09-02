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
import org.eclipse.ui.navigator.CommonActionProvider;
import org.talend.dataprofiler.core.ui.action.actions.DeleteFolderAction;
import org.talend.resource.ResourceManager;
import org.talend.resource.ResourceService;

/**
 * This provider for deleting a user folder.
 */
public class DeleteUserFolderProvider extends CommonActionProvider {

    /**
     * DOC rli CreateSubFolderProvider constructor comment.
     */
    public DeleteUserFolderProvider() {
    }

    private IFolder currentSelection;

    // public void init(ICommonActionExtensionSite anExtensionSite) {
    //
    // if (anExtensionSite.getViewSite() instanceof ICommonViewerWorkbenchSite) {
    //           
    // }
    // }

    /**
     * Adds a submenu to the given menu with the name "New Component".
     */
    public void fillContextMenu(IMenuManager menu) {
        Object obj = ((TreeSelection) this.getContext().getSelection()).getFirstElement();
        // MOD qiongli feature 9486
        if (obj instanceof IFolder) {
            currentSelection = (IFolder) obj;
            if (!ResourceService.isReadOnlyFolder(currentSelection)) {
                DeleteFolderAction createSubFolderAction = null;
                IFolder parent = (IFolder) currentSelection.getParent();
                if (parent.equals(ResourceManager.getSourceFileFolder()) || (parent).equals(ResourceManager.getJRXMLFolder())) {
                    createSubFolderAction = new DeleteFolderAction(currentSelection, true);
                } else {
                    createSubFolderAction = new DeleteFolderAction(currentSelection, false);
                }
                menu.add(createSubFolderAction);
            }
        }
    }
}
