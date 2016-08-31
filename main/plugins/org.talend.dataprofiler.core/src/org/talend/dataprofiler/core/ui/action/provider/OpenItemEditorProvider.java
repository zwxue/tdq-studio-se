// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
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
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.dataprofiler.core.ui.action.actions.OpenItemEditorAction;
import org.talend.repository.model.IRepositoryNode;

/**
 * 
 * DOC mzhao Open TDQ item editor provider.
 */
public class OpenItemEditorProvider extends AbstractCommonActionProvider {

    public OpenItemEditorProvider() {
    }

    @Override
    public void fillContextMenu(IMenuManager menu) {

        if (!isSelectionSameType()) {
            return;
        }

        Object firstObject = getContextObject();

        // TDQ-12485: use this check to hidden the open menu especailly some node no need to show open menu for example
        if (firstObject instanceof IRepositoryViewObject) {
            if (firstObject instanceof IRepositoryNode) {
                OpenItemEditorAction openItemEditorAction = new OpenItemEditorAction((IRepositoryNode) firstObject);
                menu.add(openItemEditorAction);
            }
        }
    }
}
