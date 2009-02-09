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
package org.talend.dataprofiler.core.ui.action.provider.predefined;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.talend.cwm.relational.TdView;
import org.talend.dataprofiler.core.ui.action.actions.predefined.PreviewViewAction;

/**
 * DOC Zqin class global comment. Detailled comment
 */
public class PreviewViewProvider extends CommonActionProvider {

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
        TreeSelection treeSelection = ((TreeSelection) this.getContext().getSelection());
        TdView view = (TdView) treeSelection.getFirstElement();
        PreviewViewAction action = new PreviewViewAction(view);
        menu.add(action);
    }
}
