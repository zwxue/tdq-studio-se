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

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;
import org.talend.dataprofiler.core.ui.action.actions.TdAddTaskAction;

/**
 * 
 * DOC mzhao class global comment. Detailled comment
 */
public class ModelElementTaskProvider extends CommonActionProvider {

    private TdAddTaskAction addTaskAction = null;

    private ICommonActionExtensionSite site = null;

    public ModelElementTaskProvider() {

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.navigator.CommonActionProvider#init(org.eclipse.ui.navigator.ICommonActionExtensionSite)
     */
    @Override
    public void init(ICommonActionExtensionSite site) {
        this.site = site;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.actions.ActionGroup#fillContextMenu(org.eclipse.jface.action.IMenuManager)
     */
    @Override
    public void fillContextMenu(IMenuManager menu) {
        TreeSelection currentSelection = ((TreeSelection) this.getContext().getSelection());
        // MOD by hcheng 07-28-2009,for 8273,Remove the "add task" menu on the system indicators.
        boolean showMenu = true;
        TreePath[] treePath = currentSelection.getPaths();
        for (TreePath paths : treePath) {
            Object path = paths.getSegment(1);
            if (path.toString().endsWith("Indicators")) { //$NON-NLS-1$
                showMenu = false;
            }
        }
        if (showMenu) {
            addTaskAction = new TdAddTaskAction(site.getViewSite().getShell(), currentSelection.getFirstElement());
            menu.add(addTaskAction);
        }

    }
}
