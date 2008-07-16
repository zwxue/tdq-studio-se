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
package org.talend.dataprofiler.core.ui.action.provider;

import org.eclipse.core.resources.IFolder;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.talend.dataprofiler.core.ui.action.actions.DeleteFolderAction;

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
        if (obj instanceof IFolder) {
            currentSelection = (IFolder) obj;
            if (currentSelection.getResourceAttributes().isReadOnly()) {
                return;
            }
        }
        DeleteFolderAction createSubFolderAction = new DeleteFolderAction(currentSelection);
        menu.add(createSubFolderAction);
    }
}
