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
import org.eclipse.jface.viewers.TreeSelection;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.dataprofiler.core.ui.action.actions.RemoveAnalysisAction;
import org.talend.repository.model.RepositoryNode;
/**
 * DOC rli class global comment. Detailled comment
 */
public class RemoveAnalysisActionProvider extends AbstractCommonActionProvider {

    public RemoveAnalysisActionProvider() {
    }

    public void fillContextMenu(IMenuManager menu) {
        // MOD mzhao user readonly role on svn repository mode.
        if (!isShowMenu()) {
            return;
        }

        TreeSelection treeSelection = ((TreeSelection) this.getContext().getSelection());

        if (treeSelection.size() == 1) {
            Object obj = treeSelection.getFirstElement();
            if (obj instanceof RepositoryNode) {
                RepositoryNode node = (RepositoryNode) obj;
                if (ERepositoryObjectType.TDQ_ANALYSIS_ELEMENT.equals(node.getContentType())) {
                    menu.add(new RemoveAnalysisAction());
                }
            }
        }
    }
}
