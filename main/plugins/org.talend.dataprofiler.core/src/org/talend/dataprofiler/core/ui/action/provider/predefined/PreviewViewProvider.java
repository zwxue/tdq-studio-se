// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.action.provider.predefined;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.TreeSelection;
import org.talend.dataprofiler.core.ui.action.actions.predefined.PreviewViewAction;
import org.talend.dataprofiler.core.ui.action.provider.AbstractCommonActionProvider;
import org.talend.dq.nodes.DBViewRepNode;

/**
 * DOC Zqin class global comment. Detailled comment
 */
public class PreviewViewProvider extends AbstractCommonActionProvider {

    /**
     * DOC Zqin PreviewViewProvider constructor comment.
     */
    public PreviewViewProvider() {
        // TODO Auto-generated constructor stub
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.actions.ActionGroup#fillContextMenu(org.eclipse.jface.action.IMenuManager)
     */
    @Override
    public void fillContextMenu(IMenuManager menu) {
        // MOD mzhao user readonly role on svn repository mode.
        if (!isShowMenu()) {
            return;
        }
        TreeSelection treeSelection = ((TreeSelection) this.getContext().getSelection());
        Object firstElement = treeSelection.getFirstElement();
        // MOD msjian 2011-12-7 TDQ-4091: the tdView info is not correct
        if (firstElement instanceof DBViewRepNode) {
            PreviewViewAction action = new PreviewViewAction(((DBViewRepNode) firstElement).getTdView());
            menu.add(action);
        }
        // TDQ-4091 ~
    }
}
