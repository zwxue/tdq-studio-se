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
package org.talend.dataprofiler.core.ui.action.provider.predefined;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.TreeSelection;
import org.talend.core.model.metadata.MetadataColumnRepositoryObject;
import org.talend.cwm.relational.TdColumn;
import org.talend.dataprofiler.core.ui.action.actions.predefined.PreviewColumnAction;
import org.talend.dataprofiler.core.ui.action.provider.AbstractCommonActionProvider;
import org.talend.repository.model.RepositoryNode;

/**
 * DOC Zqin class global comment. Detailled comment
 */
public class PreviewColumnProvider extends AbstractCommonActionProvider {

    @Override
    public void fillContextMenu(IMenuManager menu) {
        // MOD mzhao user readonly role on svn repository mode.
        if (!isShowMenu()) {
            return;
        }

        TreeSelection treeSelection = ((TreeSelection) this.getContext().getSelection());
        List<TdColumn> list = new ArrayList<TdColumn>();
        Object[] selectedObjs = treeSelection.toArray();
        for (Object obj : selectedObjs) {
            if (obj instanceof RepositoryNode) {
                RepositoryNode node = (RepositoryNode) obj;
                if (node.getObject() instanceof MetadataColumnRepositoryObject) {
                    MetadataColumnRepositoryObject columnObject = (MetadataColumnRepositoryObject) node.getObject();
                    list.add((TdColumn) columnObject.getTdColumn());
                }
            }
        }

        if (!list.isEmpty()) {
            PreviewColumnAction action = new PreviewColumnAction(list.toArray(new TdColumn[list.size()]));
            menu.add(action);
        }
    }
}
