// ============================================================================
//
// Copyright (C) 2006-2009 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.model;

import java.util.Collection;
import java.util.List;

import net.sourceforge.sqlexplorer.dbdetail.DetailTabManager;
import net.sourceforge.sqlexplorer.dbproduct.Alias;
import net.sourceforge.sqlexplorer.dbproduct.User;
import net.sourceforge.sqlexplorer.dbstructure.nodes.DatabaseNode;
import net.sourceforge.sqlexplorer.dbstructure.nodes.INode;
import net.sourceforge.sqlexplorer.dbstructure.nodes.TableFolderNode;
import net.sourceforge.sqlexplorer.dbstructure.nodes.TableNode;
import net.sourceforge.sqlexplorer.plugin.SQLExplorerPlugin;
import net.sourceforge.sqlexplorer.plugin.actions.OpenPasswordConnectDialogAction;
import net.sourceforge.sqlexplorer.plugin.views.DatabaseStructureView;

import org.eclipse.jface.viewers.StructuredSelection;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.cwm.relational.TdCatalog;
import org.talend.cwm.softwaredeployment.TdProviderConnection;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.perspective.ChangePerspectiveAction;
import org.talend.utils.sugars.TypedReturnCode;
import orgomg.cwm.objectmodel.core.Package;

/**
 * DOC rli class global comment. Detailled comment
 */
public final class SqlExplorerBridge {

    private SqlExplorerBridge() {

    }

    @SuppressWarnings("unchecked")
    public static TypedReturnCode<TableNode> findSqlExplorerTableNode(TdProviderConnection providerConnection,
            Package parentPackageElement, String tableName, String activeTabName) {
        // Open data explore perspective.
        new ChangePerspectiveAction(PluginConstant.SE_ID).run();
        SQLExplorerPlugin default1 = SQLExplorerPlugin.getDefault();
        Collection<Alias> aliases = default1.getAliasManager().getAliases();
        String url = providerConnection.getConnectionString();
        User currentUser = null;
        for (Alias alias : aliases) {
            if (alias.getUrl().equals(url)) {
                currentUser = alias.getDefaultUser();
                OpenPasswordConnectDialogAction openDlgAction = new OpenPasswordConnectDialogAction(alias,
                        alias.getDefaultUser(), false);
                openDlgAction.run();
                break;
            }
        }

        if (currentUser == null) {
            return new TypedReturnCode<TableNode>(DefaultMessagesImpl.getString(
                    "SqlExplorerBridge.NotFindCorrespondTable", tableName), //$NON-NLS-1$
                    false);
        }
        DatabaseNode root = currentUser.getMetaDataSession().getRoot();
        root.load();
        List<INode> catalogs = root.getCatalogs();
        List<INode> schemas = root.getSchemas();
        TdCatalog doSwitch = SwitchHelpers.CATALOG_SWITCH.doSwitch(parentPackageElement);
        INode catalogOrSchemaNode = null;
        if (doSwitch != null) {
            for (INode catalogNode : catalogs) {
                if (parentPackageElement.getName().equalsIgnoreCase(catalogNode.getName())) {
                    catalogOrSchemaNode = catalogNode;
                    // TODO if the schema is the child of catalog
                    break;
                }
            }
        } else {
            for (INode schemaNode : schemas) {
                if (parentPackageElement.getName().equalsIgnoreCase(schemaNode.getName())) {
                    catalogOrSchemaNode = schemaNode;
                }
            }
        }
        // find the table folder node.
        INode[] childNodes = catalogOrSchemaNode.getChildNodes();
        TableFolderNode tableFolderNode = null;
        for (INode node : childNodes) {
            if ("TABLE".equals(node.getQualifiedName())) { //$NON-NLS-1$
                tableFolderNode = (TableFolderNode) node;
                break;
            }
        }
        INode[] tableNodes = tableFolderNode.getChildNodes();
        for (INode node : tableNodes) {
            if (tableName.equalsIgnoreCase(node.getName())) {
                TypedReturnCode<TableNode> typedReturnCode = new TypedReturnCode<TableNode>(null, true);
                typedReturnCode.setObject((TableNode) node);
                DatabaseStructureView dsView = SQLExplorerPlugin.getDefault().getDatabaseStructureView();
                dsView.setSessionSelectionNode(currentUser.getMetaDataSession(), new StructuredSelection(node));
                DetailTabManager.setActiveTabName(activeTabName);
                return typedReturnCode;
            }
        }
        return new TypedReturnCode<TableNode>(DefaultMessagesImpl.getString(
                "SqlExplorerBridge.NotFindCorrespondTableObject", tableName), //$NON-NLS-1$
                false);
    }

}
