// ============================================================================
//
// Copyright (C) 2006-2009 Talend Inc. - www.talend.com
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.ui.action.actions.DeleteCWMResourceAction;
import org.talend.dataprofiler.core.ui.views.DQRespositoryView;

/**
 * DOC rli class global comment. Detailled comment
 */
public class DeleteCWMResourceProvider extends CommonActionProvider {

    // private IFile selectedFile;

    public DeleteCWMResourceProvider() {
    }

    private DeleteCWMResourceAction deleteResourceAction;

    /**
     * Adds a submenu to the given menu with the name "New Component".
     */
    public void fillContextMenu(IMenuManager menu) {
        deleteResourceAction = new DeleteCWMResourceAction(getSelectedResourcesArray());
        menu.add(deleteResourceAction);
    }

    /**
     * Return an array of the currently selected resources.
     * 
     * @return the selected resources
     */
    @SuppressWarnings("unchecked")
    private IFile[] getSelectedResourcesArray() {
        DQRespositoryView findView = (DQRespositoryView) CorePlugin.getDefault().findView(DQRespositoryView.ID);
        TreeSelection treeSelection = (TreeSelection) findView.getCommonViewer().getSelection();
        List<IFile> selectedFiles = new ArrayList<IFile>();
        Iterator iterator = treeSelection.iterator();
        while (iterator.hasNext()) {
            Object obj = iterator.next();
            if (obj instanceof IFile) {
                IFile file = (IFile) obj;
                selectedFiles.add(file);
            } else {
                return new IFile[0];
            }
        }
        return selectedFiles.toArray(new IFile[selectedFiles.size()]);
    }
}
