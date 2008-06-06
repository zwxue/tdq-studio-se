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

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;
import org.eclipse.ui.navigator.ICommonViewerWorkbenchSite;
import org.talend.dataprofiler.core.manager.DQStructureManager;
import org.talend.dataprofiler.core.ui.action.actions.CreateConnectionAction;

/**
 * @author rli
 * 
 */
public class NewConnectionActionProvider extends CommonActionProvider {

    /**
     * 
     */
    public NewConnectionActionProvider() {
    }

    private IAction createConnectionAction;

    private String selectedFolderName;

    public void init(ICommonActionExtensionSite anExtensionSite) {

        if (anExtensionSite.getViewSite() instanceof ICommonViewerWorkbenchSite) {
            createConnectionAction = new CreateConnectionAction();
        }
    }

    private static final String[] ACTIONSETIDS = new String[] { "org.eclipse.ui.DeleteResourceAction", //$NON-NLS-1$ //$NON-NLS-2$
            "org.eclipse.ui.RenameResourceAction", "org.eclipse.ui.CopyAction", //$NON-NLS-1$
            "org.eclipse.ui.PasteAction", //$NON-NLS-1$ 
            "org.eclipse.ui.MoveResourceAction" }; //$NON-NLS-1$ 

    /**
     * Adds a submenu to the given menu with the name "New Component".
     */
    public void fillContextMenu(IMenuManager menu) {
        Object obj = ((TreeSelection) this.getContext().getSelection()).getFirstElement();
        List<String> removeActionList = Arrays.asList(ACTIONSETIDS);
        for (IContributionItem item : menu.getItems()) {
            if (item == null || item.getId() == null) {
                continue;
            }
            if (removeActionList.contains(item.getId())) {
                menu.remove(item);
            }
        }

        if (obj instanceof IFolder) {
            selectedFolderName = ((IFolder) obj).getName();
            if (selectedFolderName.equals(DQStructureManager.DB_CONNECTIONS)) {
                menu.add(createConnectionAction);
                // menu.insertBefore("group.edit", createConnectionAction);
            }
        }
    }
}
