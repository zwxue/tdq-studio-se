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
import org.talend.cwm.compare.ui.actions.ReloadDatabaseAction;
import org.talend.dataprofiler.core.ui.action.provider.AbstractCommonActionProvider;
import org.talend.dataprofiler.core.ui.utils.WorkbenchUtils;
import org.talend.repository.model.IRepositoryNode.ENodeType;
import org.talend.repository.model.RepositoryNode;
import org.talend.resource.ResourceManager;
import org.talend.resource.ResourceService;


/**
 * DOC rli class global comment. Detailled comment
 */
public class ReloadDatabaseProvider extends AbstractCommonActionProvider {

    private static final String RELOADDATABASE_MENUTEXT = Messages.getString("ReloadDatabaseProvider.reloadDBList"); //$NON-NLS-1$

    private static final String RELOADTABLES_MENUTEXT = Messages.getString("ReloadDatabaseProvider.reloadTableList"); //$NON-NLS-1$

    private static final String RELOADVIEWS_MENUTEXT = Messages.getString("ReloadDatabaseProvider.reloadViewList"); //$NON-NLS-1$

    private static final String RELOADCOLUMNS_MENUTEXT = Messages.getString("ReloadDatabaseProvider.reloadColumnList"); //$NON-NLS-1$

    public ReloadDatabaseProvider() {
    }

    public void fillContextMenu(IMenuManager menu) {
        // MOD mzhao user readonly role on svn repository mode.
        if (!isShowMenu()) {
            return;
        }
        // MOD bug 16532 scorreia 2010-10-19 hide menu when not in TOP standalone
        // if (!PluginChecker.isOnlyTopLoaded()) {
        // return;
        // }
        // ~
        Object obj = ((TreeSelection) this.getContext().getSelection()).getFirstElement();
        if (obj instanceof RepositoryNode) {
            RepositoryNode node = (RepositoryNode) obj;
            ENodeType type = node.getType();
            IRepositoryViewObject object = node.getObject();

            IFolder folder = WorkbenchUtils.getFolder(node);
            IFolder connectionFolder = ResourceManager.getConnectionFolder();

            if ((ENodeType.REPOSITORY_ELEMENT.equals(type) || ENodeType.TDQ_REPOSITORY_ELEMENT.equals(type))
                    && ResourceService.isSubFolder(connectionFolder, folder)) {
                if (object instanceof MetadataCatalogRepositoryObject || object instanceof MetadataSchemaRepositoryObject
                        || object instanceof TdTableRepositoryObject || object instanceof TdViewRepositoryObject
                        || object instanceof MetadataColumnRepositoryObject) {
                    return;
                }
                String menuText = RELOADDATABASE_MENUTEXT;
                // if (object instanceof ITDQFolderObject) {
                // ITDQFolderObject tdqFolder = (ITDQFolderObject) object;
                // IFolderNode folderNode = tdqFolder.getFolderNode();
                // switch (folderNode.getFolderNodeType()) {
                // case IFolderNode.TABLEFOLDER_NODE_TYPE:
                // menuText = RELOADTABLES_MENUTEXT;
                // break;
                // case IFolderNode.VIEWFOLDER_NODE_TYPE:
                // menuText = RELOADVIEWS_MENUTEXT;
                // break;
                // case IFolderNode.COLUMNFOLDER_NODE_TYPE:
                // menuText = RELOADCOLUMNS_MENUTEXT;
                // break;
                // default:
                // }
                // }
                menu.add(new ReloadDatabaseAction(object, menuText));
            }
        }
    }
}
