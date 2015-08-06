// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
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

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.properties.ConnectionItem;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.dataprofiler.core.service.AbstractSvnRepositoryService;
import org.talend.dataprofiler.core.service.GlobalServiceRegister;
import org.talend.dq.helper.RepositoryNodeHelper;
import org.talend.dq.nodes.foldernode.IConnectionElementSubFolder;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.RepositoryNode;

/**
 * 
 * DOC mzhao Abstract common action provider.
 */
public class AbstractCommonActionProvider extends CommonActionProvider {

    protected static final String NEW_MENU_NAME = "column.analysis.menu"; //$NON-NLS-1$

    public boolean isShowMenu() {
        // MOD mzhao user readonly role on svn repository mode.
        AbstractSvnRepositoryService svnReposService = GlobalServiceRegister.getDefault().getSvnRepositoryService(
                AbstractSvnRepositoryService.class);
        if (svnReposService != null && svnReposService.isReadonly() || !isSelectionSameType()) {
            return false;
        }

        return true;
    }

    /**
     * DOC bZhou Comment method "getSubMenuManager".
     * 
     * @param subMenuId
     * @return
     */
    public IMenuManager getSubMenuManager(IMenuManager topMenu, String subMenuId) {
        if (topMenu != null) {
            for (IContributionItem item : topMenu.getItems()) {
                if (StringUtils.equals(item.getId(), subMenuId)) {
                    return (IMenuManager) item;
                }
            }
        }

        return null;
    }

    /**
     * 
     * MOD bzhou 2011-4-1 bug 20051
     * 
     * DOC bzhou Comment method "getContextObject".
     * 
     * @return
     */
    protected Object getContextObject() {
        return ((TreeSelection) this.getContext().getSelection()).getFirstElement();
    }

    /**
     * 
     * MOD bzhou 2011-4-1 bug 20051
     * 
     * DOC bzhou Comment method "getFistContextNode".
     * 
     * @return
     */
    protected IRepositoryNode getFistContextNode() {
        Object object = getContextObject();

        if (object instanceof IRepositoryNode) {
            return (IRepositoryNode) object;
        }

        return null;
    }

    /**
     * 
     * judge if all selections are the same type nodes.
     * 
     * @return
     */
    protected boolean isSelectionSameType() {
        TreeSelection currentSelections = ((TreeSelection) getContext().getSelection());
        Object[] selectionArrays = currentSelections.toArray();
        // TDQ-5186 MOD qiongli.pop the context menus just when these selections are the same type.
        if (selectionArrays.length > 1) {
            RepositoryNode firstNode = null;
            ERepositoryObjectType firstContentType = null;
            for (Object obj : selectionArrays) {
                if (obj instanceof RepositoryNode) {
                    if (firstNode == null) {
                        firstNode = (RepositoryNode) obj;
                        firstContentType = firstNode.getContentType();
                        continue;
                    }
                    RepositoryNode node = (RepositoryNode) obj;
                    ERepositoryObjectType contentType = node.getContentType();
                    // return false when the content type is different or a node is in recycle bin and another is not
                    // in.
                    if ((firstContentType != null && contentType != null && !contentType.getType().equals(
                            firstContentType.getType()))
                            || (RepositoryNodeHelper.isStateDeleted(node) != RepositoryNodeHelper.isStateDeleted(firstNode))) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * 
     * Get Connection which cotain by selectedNode(DBConnectionRepNode\DBCatalogRepNode\DBSchemaRepNode) If want to
     * support to DelimiteFileConnection node need to make DFColumnFolderRepNode implements IConnectionElementSubFolder
     * interface The case for MDMConnection is same to DelimiteFileConnection.
     * 
     * @param selectedNode
     * @return
     */
    protected Connection getConnection(Object selectedNode) {
        Connection conn = null;
        IRepositoryViewObject object = null;
        if (selectedNode == null) {
            return conn;
        }
        if (selectedNode instanceof RepositoryNode) {
            object = ((RepositoryNode) selectedNode).getObject();
        }
        if (object != null) {
            Property property = object.getProperty();
            if (property == null) {
                return conn;
            }
            Item item = property.getItem();
            if (item != null && item instanceof ConnectionItem) {
                conn = ((ConnectionItem) item).getConnection();
            }
        } else if (selectedNode instanceof IConnectionElementSubFolder) {
            conn = ((IConnectionElementSubFolder) selectedNode).getConnection();
        }
        return conn;
    }
}
