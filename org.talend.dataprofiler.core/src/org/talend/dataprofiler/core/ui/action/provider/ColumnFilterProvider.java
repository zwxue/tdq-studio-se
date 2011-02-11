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

import org.apache.log4j.Logger;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.TreeSelection;
import org.talend.core.repository.model.repositoryObject.TdTableRepositoryObject;
import org.talend.core.repository.model.repositoryObject.TdViewRepositoryObject;
import org.talend.dataprofiler.core.ui.action.actions.ColumnFilterAction;
import org.talend.repository.model.RepositoryNode;

/**
 * DOC xqliu class global comment. Detailled comment
 */
public class ColumnFilterProvider extends AbstractCommonActionProvider {

    protected static Logger log = Logger.getLogger(ColumnFilterProvider.class);

    public ColumnFilterProvider() {
    }

    @Override
    public void fillContextMenu(IMenuManager menu) {
        // MOD mzhao user readonly role on svn repository mode.
        if (!isShowMenu()) {
            return;
        }
        TreeSelection currentSelection = ((TreeSelection) this.getContext().getSelection());
        Object firstElement = currentSelection.getFirstElement();
        if (firstElement instanceof RepositoryNode) {
            RepositoryNode node = (RepositoryNode) firstElement;
            if (node.getObject() instanceof TdTableRepositoryObject) {
                TdTableRepositoryObject tableObject = (TdTableRepositoryObject) node.getObject();
                ColumnFilterAction ecfAction = new ColumnFilterAction(tableObject.getTdTable());
                menu.add(ecfAction);
            } else if (node.getObject() instanceof TdViewRepositoryObject) {
                TdViewRepositoryObject viewObject = (TdViewRepositoryObject) node.getObject();
                ColumnFilterAction ecfAction = new ColumnFilterAction(viewObject.getTdView());
                menu.add(ecfAction);
            }
        }
    }

}
