// ============================================================================
//
// Copyright (C) 2006-2013 Talend Inc. - www.talend.com
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
import org.talend.cwm.relational.TdTable;
import org.talend.dataprofiler.core.ui.action.actions.predefined.SuggestAnalysisAction;
import org.talend.dataprofiler.core.ui.action.provider.AbstractCommonActionProvider;
import org.talend.dq.nodes.DBTableRepNode;

/**
 * Provider of the semantic analysis actions.
 */
public class SemanticAnalysisProvider extends AbstractCommonActionProvider {

    /**
     * SemanticAnalysisProvider constructor.
     */
    public SemanticAnalysisProvider() {
        // default CTOR
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
        // MOD msjian 2011-12-7 TDQ-4091: the tdTable info is not correct
        if (firstElement instanceof DBTableRepNode) {
            TdTable tdTable = ((DBTableRepNode) firstElement).getTdTable();
            if (tdTable != null) {
                menu.add(new SuggestAnalysisAction(tdTable));
            }
        }
        // TDQ-4091 ~
    }

}
