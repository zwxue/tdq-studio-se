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
package org.talend.dataprofiler.core.ui.action.provider;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;
import org.eclipse.ui.navigator.ICommonMenuConstants;
import org.eclipse.ui.navigator.ICommonViewerWorkbenchSite;
import org.talend.dataprofiler.core.ui.action.actions.AnalyzeColumnAction;
import org.talend.dataprofiler.core.ui.action.actions.AnalyzeColumnSetAction;
import org.talend.dq.nodes.DBColumnRepNode;
import org.talend.dq.nodes.DFColumnRepNode;
import org.talend.repository.model.RepositoryNode;

/**
 * DOC zqin class global comment. Detailled comment <br/>
 * 
 * $Id: talend.epf 1 2006-09-29 17:06:40Z zqin $
 * 
 */
public class AnalyzeColumnProvider extends AbstractCommonActionProvider {

    private AnalyzeColumnAction analyzeColumnAction;

    private AnalyzeColumnSetAction analyzeColumnSetAction;

    public AnalyzeColumnProvider() {

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.navigator.CommonActionProvider#init(org.eclipse.ui.navigator.ICommonActionExtensionSite)
     */
    @Override
    public void init(ICommonActionExtensionSite site) {

        if (site.getViewSite() instanceof ICommonViewerWorkbenchSite) {
            analyzeColumnAction = new AnalyzeColumnAction();
            analyzeColumnSetAction = new AnalyzeColumnSetAction();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.actions.ActionGroup#fillContextMenu(org.eclipse.jface.action.IMenuManager)
     */
    @SuppressWarnings("unchecked")
    @Override
    public void fillContextMenu(IMenuManager menu) {
        // MOD mzhao user readonly role on svn repository mode.
        if (!isShowMenu()) {
            return;
        }

        TreeSelection currentSelection = ((TreeSelection) this.getContext().getSelection());

        if (isSelectedColumnLevel(currentSelection)) {
            IMenuManager submenu = new MenuManager("Column Analysis", NEW_MENU_NAME);
            menu.insertAfter(ICommonMenuConstants.GROUP_NEW, submenu);
            analyzeColumnAction.setColumnSelection(currentSelection);
            submenu.add(analyzeColumnAction);
        }

        if (isSelectedTdColumn(currentSelection)) {
            analyzeColumnSetAction.setColumnSelection(currentSelection);
            menu.add(analyzeColumnSetAction);
        }
    }

    /**
     * DOC bZhou Comment method "isSelectedColumnLevel".
     * 
     * @param currentSelection
     * @return
     */
    private boolean isSelectedColumnLevel(TreeSelection currentSelection) {
        for (Object obj : currentSelection.toList()) {
            if (obj instanceof RepositoryNode) {
                RepositoryNode node = (RepositoryNode) obj;
                if (node.hasChildren()) {
                    return false;
                }
            } else {
                return false;
            }
        }

        return true;
    }

    /**
     * DOC bZhou Comment method "isSelectedTdColumn".
     * 
     * @param currentSelection
     * @return
     */
    private boolean isSelectedTdColumn(TreeSelection currentSelection) {
        for (Object obj : currentSelection.toList()) {
            if (!(obj instanceof DBColumnRepNode) && !(obj instanceof DFColumnRepNode)) {
                return false;
            }
        }

        return true;
    }

}
