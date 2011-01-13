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

import org.eclipse.core.resources.IFolder;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.TreeSelection;
import org.talend.core.model.properties.Item;
import org.talend.dataprofiler.core.ui.action.actions.DeleteObjectsAction;
import org.talend.dataprofiler.core.ui.exchange.ExchangeCategoryRepNode;
import org.talend.dataprofiler.core.ui.exchange.ExchangeComponentRepNode;
import org.talend.dataprofiler.core.ui.utils.WorkbenchUtils;
import org.talend.dataquality.properties.TDQIndicatorDefinitionItem;
import org.talend.dq.nodes.DBCatalogRepNode;
import org.talend.dq.nodes.DBColumnFolderRepNode;
import org.talend.dq.nodes.DBColumnRepNode;
import org.talend.dq.nodes.DBSchemaRepNode;
import org.talend.dq.nodes.DBTableFolderRepNode;
import org.talend.dq.nodes.DBTableRepNode;
import org.talend.dq.nodes.DBViewFolderRepNode;
import org.talend.dq.nodes.DBViewRepNode;
import org.talend.dq.nodes.DFColumnFolderRepNode;
import org.talend.dq.nodes.DFColumnRepNode;
import org.talend.dq.nodes.DFConnectionRepNode;
import org.talend.dq.nodes.DFTableRepNode;
import org.talend.dq.nodes.MDMSchemaRepNode;
import org.talend.dq.nodes.MDMXmlElementRepNode;
import org.talend.repository.model.IRepositoryNode.ENodeType;
import org.talend.repository.model.RepositoryNode;
import org.talend.resource.ResourceManager;
import org.talend.resource.ResourceService;

/**
 * DOC rli class global comment. Detailled comment
 */
public class DeleteResourceProvider extends AbstractCommonActionProvider {

    /**
     * Adds a submenu to the given menu with the name "New Component".
     */
    public void fillContextMenu(IMenuManager menu) {
        // MOD mzhao user readonly role on svn repository mode.
        if (!isShowMenu()) {
            return;
        }
        Object obj = ((TreeSelection) this.getContext().getSelection()).getFirstElement();
        if (obj instanceof RepositoryNode) {
            RepositoryNode node = (RepositoryNode) obj;
            if (shouldShowDeleteMenu(node)) {
                menu.add(new DeleteObjectsAction());
            }
        }
    }

    /**
     * DOC xqliu Comment method "shouldShowDeleteMenu".
     * 
     * @param node
     * @return
     */
    private boolean shouldShowDeleteMenu(RepositoryNode node) {
        return !isSystemFolder(node) && !isVirturalNode(node) && !isSystemIndicator(node) && !isDelimitedFile(node);
    }

    /**
     * DOC xqliu Comment method "isSystemFolder".
     * 
     * @param node
     * @return
     */
    private boolean isSystemFolder(RepositoryNode node) {
        return ENodeType.SYSTEM_FOLDER.equals(node.getType());
    }

    /**
     * DOC xqliu Comment method "isVirturalNode".
     * 
     * @param node
     * @return
     */
    private boolean isVirturalNode(RepositoryNode node) {
        return node instanceof DBCatalogRepNode || node instanceof DBSchemaRepNode || node instanceof DBTableFolderRepNode
                || node instanceof DBViewFolderRepNode || node instanceof DBTableRepNode || node instanceof DBViewRepNode
                || node instanceof DBColumnFolderRepNode || node instanceof DBColumnRepNode || node instanceof MDMSchemaRepNode
                || node instanceof MDMXmlElementRepNode || node instanceof DFTableRepNode
                || node instanceof DFColumnFolderRepNode || node instanceof DFColumnRepNode
                || node instanceof ExchangeCategoryRepNode || node instanceof ExchangeComponentRepNode;
    }

    /**
     * DOC xqliu Comment method "isSystemIndicator".
     * 
     * @param node
     * @return
     */
    private boolean isSystemIndicator(RepositoryNode node) {
        switch (node.getType()) {
        case TDQ_REPOSITORY_ELEMENT:
        case REPOSITORY_ELEMENT:
            if (node.getObject() != null) {
                Item item = node.getObject().getProperty().getItem();
                IFolder folder = WorkbenchUtils.getFolder(node);
                return item instanceof TDQIndicatorDefinitionItem
                        && ResourceService.isSubFolder(ResourceManager.getSystemIndicatorFolder(), folder);
            }
        default:

        }
        return false;
    }

    /**
     * 
     * DOC qiongli Comment method "isDelimitedFile".
     * 
     * @param node
     * @return
     */
    private boolean isDelimitedFile(RepositoryNode node) {
        if (node instanceof DFConnectionRepNode) {
            return true;
        }
        return false;
    }

}
