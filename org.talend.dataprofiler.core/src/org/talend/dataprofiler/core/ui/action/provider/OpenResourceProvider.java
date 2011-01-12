// ============================================================================
//
// Copyright (C) 2006-2010 Talend Inc. - www.talend.com
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
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.dataprofiler.core.ui.action.actions.OpenItemEditorAction;
import org.talend.dq.helper.RepositoryNodeHelper;
import org.talend.repository.model.RepositoryNode;

/**
 * DOC rli class global comment. Detailled comment
 */
public class OpenResourceProvider extends AbstractCommonActionProvider {

    private OpenItemEditorAction openFileAction;

    public void fillContextMenu(IMenuManager aMenu) {
        // DOC MOD klliu 2010-12-09 feature15750
        Object obj = ((TreeSelection) this.getContext().getSelection()).getFirstElement();
        RepositoryNode node = (RepositoryNode) obj;
        if (RepositoryNodeHelper.canOpenEditor(node)) {
            IRepositoryViewObject object = node.getObject();
            openFileAction = new OpenItemEditorAction(object);
            aMenu.add(openFileAction);
        }
    }
}
