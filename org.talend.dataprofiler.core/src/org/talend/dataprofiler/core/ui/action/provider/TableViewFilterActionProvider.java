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
import org.talend.core.repository.model.repositoryObject.MetadataCatalogRepositoryObject;
import org.talend.core.repository.model.repositoryObject.MetadataSchemaRepositoryObject;
import org.talend.dataprofiler.core.ui.action.actions.TableViewFilterAction;
import org.talend.repository.model.IRepositoryNode.ENodeType;
import org.talend.repository.model.RepositoryNode;

/**
 * DOC xqliu class global comment. Detailled comment
 */
public class TableViewFilterActionProvider extends AbstractCommonActionProvider {

    public TableViewFilterActionProvider() {
    }

    @Override
    public void fillContextMenu(IMenuManager menu) {
        // MOD mzhao user readonly role on svn repository mode.
        if (!isShowMenu()) {
            return;
        }
        TreeSelection currentSelection = ((TreeSelection) this.getContext().getSelection());
        Object obj = currentSelection.getFirstElement();
        if (obj instanceof RepositoryNode) {
            RepositoryNode node = (RepositoryNode) obj;
            if (ENodeType.TDQ_REPOSITORY_ELEMENT.equals(node.getType())) {
                IRepositoryViewObject viewObject = node.getObject();
                if (viewObject instanceof MetadataSchemaRepositoryObject) {
                    MetadataSchemaRepositoryObject schemaObject = (MetadataSchemaRepositoryObject) viewObject;
                    TableViewFilterAction tvfAction = new TableViewFilterAction(schemaObject.getSchema());
                    menu.add(tvfAction);
                } else if (viewObject instanceof MetadataCatalogRepositoryObject) {
                    MetadataCatalogRepositoryObject catalogObject = (MetadataCatalogRepositoryObject) viewObject;
                    TableViewFilterAction tvfAction = new TableViewFilterAction(catalogObject.getCatalog());
                    menu.add(tvfAction);
                }
            }
        }
    }
}
