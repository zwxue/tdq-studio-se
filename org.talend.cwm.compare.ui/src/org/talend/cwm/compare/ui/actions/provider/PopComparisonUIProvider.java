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
package org.talend.cwm.compare.ui.actions.provider;

import org.eclipse.core.resources.IFolder;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.TreeSelection;
import org.talend.core.model.metadata.MetadataColumnRepositoryObject;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.repository.model.repositoryObject.MetadataCatalogRepositoryObject;
import org.talend.core.repository.model.repositoryObject.MetadataSchemaRepositoryObject;
import org.talend.core.repository.model.repositoryObject.TdTableRepositoryObject;
import org.talend.core.repository.model.repositoryObject.TdViewRepositoryObject;
import org.talend.cwm.compare.i18n.Messages;
import org.talend.cwm.compare.ui.actions.PopComparisonUIAction;
import org.talend.cwm.db.connection.ConnectionUtils;
import org.talend.dataprofiler.core.ui.action.provider.AbstractCommonActionProvider;
import org.talend.dataprofiler.core.ui.utils.WorkbenchUtils;
import org.talend.repository.model.IRepositoryNode.ENodeType;
import org.talend.repository.model.RepositoryNode;
import org.talend.resource.ResourceManager;
import org.talend.resource.ResourceService;


/**
 * DOC rli class global comment. Detailled comment
 */
public class PopComparisonUIProvider extends AbstractCommonActionProvider {

    private static final String COMPAREDATABASE_MENUTEXT = Messages.getString("PopComparisonUIProvider.dbCompare"); //$NON-NLS-1$

    private static final String COMPARETABLES_MENUTEXT = Messages.getString("PopComparisonUIProvider.tableCompare"); //$NON-NLS-1$

    private static final String COMPAREVIEWS_MENUTEXT = Messages.getString("PopComparisonUIProvider.viewCompare"); //$NON-NLS-1$

    private static final String COMPARECOLUMNS_MENUTEXT = Messages.getString("PopComparisonUIProvider.columnCompare"); //$NON-NLS-1$

    public PopComparisonUIProvider() {
    }

    public void fillContextMenu(IMenuManager menu) {
        // MOD mzhao user readonly role on svn repository mode.
        if (!isShowMenu()) {
            return;
        }
        Object obj = ((TreeSelection) this.getContext().getSelection()).getFirstElement();
        if (obj instanceof RepositoryNode) {
            RepositoryNode node = (RepositoryNode) obj;
            ENodeType type = node.getType();
            IRepositoryViewObject object = node.getObject();

            IFolder folder = WorkbenchUtils.getFolder(node);
            IFolder connectionFolder = ResourceManager.getConnectionFolder();

            if ((ENodeType.REPOSITORY_ELEMENT.equals(type) || ENodeType.TDQ_REPOSITORY_ELEMENT.equals(type))
                    && ResourceService.isSubFolder(connectionFolder, folder)) {
                // ADD xqliu 2010-03-30 bug 11951
                // remove the "Database Compare" menu when the object is a mdm connection
                if (ConnectionUtils.isMdmConnection(object)) {
                    return;
                }
                // ~11951

                if (object instanceof MetadataCatalogRepositoryObject || object instanceof MetadataSchemaRepositoryObject
                        || object instanceof TdTableRepositoryObject || object instanceof TdViewRepositoryObject
                        || object instanceof MetadataColumnRepositoryObject) {
                    return;
                }

                String menuText = COMPAREDATABASE_MENUTEXT;
                // if (object instanceof ITDQFolderObject) {
                // ITDQFolderObject tdqFolder = (ITDQFolderObject) object;
                // IFolderNode folderNode = tdqFolder.getFolderNode();
                // switch (folderNode.getFolderNodeType()) {
                // case IFolderNode.TABLEFOLDER_NODE_TYPE:
                // menuText = COMPARETABLES_MENUTEXT;
                // break;
                // case IFolderNode.VIEWFOLDER_NODE_TYPE:
                // menuText = COMPAREVIEWS_MENUTEXT;
                // break;
                // case IFolderNode.COLUMNFOLDER_NODE_TYPE:
                // menuText = COMPARECOLUMNS_MENUTEXT;
                // break;
                // default:
                // }
                // }
                menu.add(new PopComparisonUIAction(obj, menuText));

            }
        }
    }
}
