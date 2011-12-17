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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.TreeSelection;
import org.talend.dataprofiler.core.ui.action.actions.OverviewAnalysisAction;
import org.talend.dq.nodes.DBCatalogRepNode;
import org.talend.dq.nodes.DBConnectionRepNode;
import org.talend.dq.nodes.DBSchemaRepNode;
import org.talend.dq.nodes.DBTableRepNode;
import org.talend.repository.model.IRepositoryNode;

/**
 * DOC rli class global comment. Detailled comment
 */
public class OverViewAnalysisActionProvider extends AbstractCommonActionProvider {

    /**
     * DOC rli OverViewAnalysisActionProvider constructor comment.
     */
    public OverViewAnalysisActionProvider() {
    }

    @Override
    public void fillContextMenu(IMenuManager menu) {
        // MOD mzhao user readonly role on svn repository mode.
        if (!isShowMenu()) {
            return;
        }
        TreeSelection currentSelection = ((TreeSelection) this.getContext().getSelection());
        List list = currentSelection.toList();
        // DOC MOD klliu 2010-12-14 feature 15750 for overview catalog or schema analysis.
        List<IRepositoryNode> packageList = new ArrayList<IRepositoryNode>();
        boolean showMenu = false;
        for (Object obj : list) {
            if (obj instanceof DBConnectionRepNode || obj instanceof DBCatalogRepNode || obj instanceof DBSchemaRepNode
                    || obj instanceof DBTableRepNode) {
                IRepositoryNode node = (IRepositoryNode) obj;
                packageList.add(node);
                showMenu = true;
            }
        }
        if (showMenu) {
            OverviewAnalysisAction overviewAnalysisAction = new OverviewAnalysisAction(packageList);
            menu.add(overviewAnalysisAction);
        }
    }
}
