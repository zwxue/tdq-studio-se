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
package org.talend.dataprofiler.core.ui.action.provider.predefined;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.TreeSelection;
import org.talend.core.repository.model.repositoryObject.TdTableRepositoryObject;
import org.talend.dataprofiler.core.ui.action.actions.predefined.PreviewTableAction;
import org.talend.dataprofiler.core.ui.action.provider.AbstractCommonActionProvider;
import org.talend.repository.model.RepositoryNode;

/**
 * DOC qzhang class global comment. Detailled comment <br/>
 * 
 * $Id: talend.epf 1 2006-09-29 17:06:40Z nrousseau $
 * 
 */
public class PreviewTableProvider extends AbstractCommonActionProvider {

    /**
     * DOC qzhang PreviewTableProvider constructor comment.
     */
    public PreviewTableProvider() {
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
        if (firstElement instanceof RepositoryNode) {
            RepositoryNode node = (RepositoryNode) firstElement;
            if (node.getObject() instanceof TdTableRepositoryObject) {
                TdTableRepositoryObject tableObject = (TdTableRepositoryObject) node.getObject();
                PreviewTableAction action = new PreviewTableAction(tableObject.getTdTable());
                menu.add(action);
            }
        }
    }
}
