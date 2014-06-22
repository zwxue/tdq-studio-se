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

import org.eclipse.core.resources.IFolder;
import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.TreeSelection;
import org.talend.core.context.Context;
import org.talend.core.model.context.ContextUtils;
import org.talend.core.model.properties.ContextItem;
import org.talend.cwm.db.connection.ConnectionUtils;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.action.actions.SwitchContextAction;
import org.talend.dataprofiler.core.ui.utils.WorkbenchUtils;
import org.talend.dq.nodes.DBConnectionRepNode;
import org.talend.dq.nodes.DFConnectionRepNode;
import org.talend.repository.model.IRepositoryNode.ENodeType;
import org.talend.repository.model.RepositoryNode;
import org.talend.resource.ResourceManager;
import org.talend.resource.ResourceService;

/**
 * DOC msjian  class global comment. Detailled comment
 */
public class SwitchContextProvider extends AbstractCommonActionProvider {

    private static final String SWITCHCONTEXT = DefaultMessagesImpl.getString("SwitchContextProvider.SwitchContext");//$NON-NLS-1$

    /**
     * DOC msjian SwitchContextProvider constructor comment.
     */
    public SwitchContextProvider() {
    }

    @Override
    public void fillContextMenu(IMenuManager menu) {
        if (!isShowMenu()) {
            return;
        }

        Object obj = ((TreeSelection) this.getContext().getSelection()).getFirstElement();
        if (obj instanceof RepositoryNode) {
            RepositoryNode node = (RepositoryNode) obj;
            if (shouldShowReloadMenu(node)) {
                menu.add(new SwitchContextAction(node, SWITCHCONTEXT));
            }
        }
    }

    /**
     * DOC msjian Comment method "shouldShowReloadMenu".
     * 
     * @param node
     * @return
     */
    @SuppressWarnings("unchecked")
    private boolean shouldShowReloadMenu(RepositoryNode node) {
        ENodeType type = node.getType();
        IFolder folder = WorkbenchUtils.getFolder(node);
        IFolder connectionFolder = ResourceManager.getConnectionFolder();
        if ((ENodeType.REPOSITORY_ELEMENT.equals(type) || ENodeType.TDQ_REPOSITORY_ELEMENT.equals(type))
                && ResourceService.isSubFolder(connectionFolder, folder)) {
            if (ConnectionUtils.isMdmConnection(node.getObject())) {
                return false;
            }
            // MOD qiongli 2011-10-10
            String contextId = null;
            if (node instanceof DBConnectionRepNode) {
                contextId = ((DBConnectionRepNode) node).getDatabaseConnection().getContextId();
            } else if (node instanceof DFConnectionRepNode) {
                contextId = ((DFConnectionRepNode) node).getDfConnection().getContextId();
            }
            // This menu will be available only when a context is not null and has more than one group.
            if (contextId != null && !PluginConstant.EMPTY_STRING.equals(contextId.trim())) {
                ContextItem objContextItem = ContextUtils.getContextItemById2(contextId);
                EList<Context> contexts = objContextItem.getContext();
                if (contexts != null && contexts.size() > 1) {
                    return true;
                }
            }
        }
        return false;
    }
}
