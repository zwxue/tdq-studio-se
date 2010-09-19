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

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.TreeSelection;
import org.talend.cwm.compare.i18n.Messages;
import org.talend.cwm.compare.ui.actions.PopComparisonUIAction;
import org.talend.cwm.db.connection.ConnectionUtils;
import org.talend.dataprofiler.core.ui.action.provider.AbstractCommonActionProvider;
import org.talend.dq.nodes.foldernode.IFolderNode;

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
        // ADD xqliu 2010-03-30 bug 11951
        // remove the "Database Compare" menu when the object is a mdm connection
        if (ConnectionUtils.isMdmConnection(obj)) {
            return;
        }
        // ~11951
        String menuText = COMPAREDATABASE_MENUTEXT;
        if (obj instanceof IFolderNode) {
            IFolderNode folderNode = (IFolderNode) obj;
            switch (folderNode.getFolderNodeType()) {
            case IFolderNode.TABLEFOLDER_NODE_TYPE:
                menuText = COMPARETABLES_MENUTEXT;
                break;
            case IFolderNode.VIEWFOLDER_NODE_TYPE:
                menuText = COMPAREVIEWS_MENUTEXT;
                break;
            case IFolderNode.COLUMNFOLDER_NODE_TYPE:
                menuText = COMPARECOLUMNS_MENUTEXT;
                break;
            default:
            }
        }
        menu.add(new PopComparisonUIAction(obj, menuText));
    }
}
