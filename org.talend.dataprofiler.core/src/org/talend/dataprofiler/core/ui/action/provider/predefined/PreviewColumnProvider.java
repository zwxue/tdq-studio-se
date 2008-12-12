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
package org.talend.dataprofiler.core.ui.action.provider.predefined;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.talend.cwm.relational.TdColumn;
import org.talend.dataprofiler.core.ui.action.actions.predefined.PreviewColumnAction;

/**
 * DOC Zqin class global comment. Detailled comment
 */
public class PreviewColumnProvider extends CommonActionProvider {

    @Override
    public void fillContextMenu(IMenuManager menu) {
        TreeSelection treeSelection = ((TreeSelection) this.getContext().getSelection());
        TdColumn column = (TdColumn) treeSelection.getFirstElement();
        PreviewColumnAction action = new PreviewColumnAction(column);
        menu.add(action);
    }
}
