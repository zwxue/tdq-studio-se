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
package org.talend.dq.helper;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.EList;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.navigator.CommonNavigator;
import org.eclipse.ui.navigator.CommonViewer;
import org.talend.core.model.metadata.MetadataColumnRepositoryObject;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.metadata.builder.connection.MetadataColumn;
import org.talend.core.model.metadata.builder.connection.MetadataTable;
import org.talend.core.model.properties.ConnectionItem;
import org.talend.core.model.properties.FolderItem;
import org.talend.core.model.properties.Item;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.repository.model.ISubRepositoryObject;
import org.talend.core.repository.model.repositoryObject.MetadataCatalogRepositoryObject;
import org.talend.core.repository.model.repositoryObject.MetadataSchemaRepositoryObject;
import org.talend.core.repository.model.repositoryObject.MetadataTableRepositoryObject;
import org.talend.core.repository.model.repositoryObject.TdTableRepositoryObject;
import org.talend.core.repository.model.repositoryObject.TdViewRepositoryObject;
import org.talend.cwm.helper.CatalogHelper;
import org.talend.cwm.helper.ColumnHelper;
import org.talend.cwm.helper.ColumnSetHelper;
import org.talend.cwm.helper.ConnectionHelper;
import org.talend.cwm.helper.ResourceHelper;
import org.talend.cwm.relational.TdColumn;
import org.talend.cwm.relational.TdTable;
import org.talend.cwm.relational.TdView;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.reports.TdReport;
import org.talend.dq.nodes.AnalysisRepNode;
import org.talend.dq.nodes.DBCatalogRepNode;
import org.talend.dq.nodes.DBColumnFolderRepNode;
import org.talend.dq.nodes.DBColumnRepNode;
import org.talend.dq.nodes.DBConnectionFolderRepNode;
import org.talend.dq.nodes.DBConnectionRepNode;
import org.talend.dq.nodes.DBSchemaRepNode;
import org.talend.dq.nodes.DBTableFolderRepNode;
import org.talend.dq.nodes.DBTableRepNode;
import org.talend.dq.nodes.DBViewFolderRepNode;
import org.talend.dq.nodes.DBViewRepNode;
import org.talend.dq.nodes.DFConnectionFolderRepNode;
import org.talend.dq.nodes.JrxmlTempleteRepNode;
import org.talend.dq.nodes.MDMConnectionRepNode;
import org.talend.dq.nodes.PatternRepNode;
import org.talend.dq.nodes.RuleRepNode;
import org.talend.dq.nodes.SourceFileRepNode;
import org.talend.dq.nodes.SysIndicatorDefinitionRepNode;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.RepositoryNode;
import org.talend.resource.EResourceConstant;
import orgomg.cwm.objectmodel.core.ModelElement;
import orgomg.cwm.resource.record.RecordFile;
import orgomg.cwm.resource.relational.Catalog;
import orgomg.cwm.resource.relational.Schema;

/**
 * Helper class for RepositoryNode.
 */
public final class RepositoryNodeHelper {

    public static final String DQRESPOSITORYVIEW = "org.talend.dataprofiler.core.ui.views.DQRespositoryView"; //$NON-NLS-1$
    private RepositoryNodeHelper() {
    }

    public static IPath getPath(RepositoryNode node) {
        if (node == null) {
            return null;
        }
        if (node.isBin()) {
            return new Path(""); //$NON-NLS-1$
        }
        if (node.getType() == null) {
            return null;
        }
        switch (node.getType()) {
        case SYSTEM_FOLDER:
            ERepositoryObjectType contentType = node.getContentType();
            if (contentType == null) {
                Item item = node.getObject().getProperty().getItem();
                contentType = ERepositoryObjectType.getItemType(item);
            }
            return new Path(ERepositoryObjectType.getFolderName(contentType)); //$NON-NLS-1$
        case SIMPLE_FOLDER:
            String label = node.getObject().getProperty().getLabel();
            return getPath(node.getParent()).append(label);
        default:
        }
        return getPath(node.getParent());
    }

    public static ERepositoryObjectType retrieveRepObjectTypeByPath(String path) {
        if (EResourceConstant.DATA_PROFILING.getPath().equals(path)) {
            return ERepositoryObjectType.TDQ_DATA_PROFILING;
        } else if (EResourceConstant.ANALYSIS.getPath().equals(path)) {
            return ERepositoryObjectType.TDQ_ANALYSIS;
        } else if (EResourceConstant.REPORTS.getPath().equals(path)) {
            return ERepositoryObjectType.TDQ_REPORTS;
        } else if (EResourceConstant.LIBRARIES.getPath().equals(path)) {
            return ERepositoryObjectType.TDQ_LIBRARIES;
        } else if (EResourceConstant.EXCHANGE.getPath().equals(path)) {
            return ERepositoryObjectType.TDQ_EXCHANGE;
        } else if (EResourceConstant.INDICATORS.getPath().equals(path)) {
            return ERepositoryObjectType.TDQ_INDICATORS;
        } else if (EResourceConstant.SYSTEM_INDICATORS.getPath().equals(path)) {
            return ERepositoryObjectType.TDQ_SYSTEM_INDICATORS;
        } else if (EResourceConstant.SYSTEM_INDICATORS_ADVANCED_STATISTICS.getPath().equals(path)) {
            return ERepositoryObjectType.SYSTEM_INDICATORS_ADVANCED_STATISTICS;
        } else if (EResourceConstant.SYSTEM_INDICATORS_BUSINESS_RULES.getPath().equals(path)) {
            return ERepositoryObjectType.SYSTEM_INDICATORS_BUSINESS_RULES;
        } else if (EResourceConstant.SYSTEM_INDICATORS_CORRELATION.getPath().equals(path)) {
            return ERepositoryObjectType.SYSTEM_INDICATORS_CORRELATION;
        } else if (EResourceConstant.SYSTEM_INDICATORS_FUNCTIONAL_DEPENDENCY.getPath().equals(path)) {
            return ERepositoryObjectType.SYSTEM_INDICATORS_FUNCTIONAL_DEPENDENCY;
        } else if (EResourceConstant.SYSTEM_INDICATORS_OVERVIEW.getPath().equals(path)) {
            return ERepositoryObjectType.SYSTEM_INDICATORS_OVERVIEW;
        } else if (EResourceConstant.SYSTEM_INDICATORS_PATTERN_FINDER.getPath().equals(path)) {
            return ERepositoryObjectType.SYSTEM_INDICATORS_PATTERN_FINDER;
        } else if (EResourceConstant.SYSTEM_INDICATORS_PATTERN_MATCHING.getPath().equals(path)) {
            return ERepositoryObjectType.SYSTEM_INDICATORS_PATTERN_MATCHING;
        } else if (EResourceConstant.SYSTEM_INDICATORS_ROW_COMPARISON.getPath().equals(path)) {
            return ERepositoryObjectType.SYSTEM_INDICATORS_ROW_COMPARISON;
        } else if (EResourceConstant.SYSTEM_INDICATORS_SIMPLE_STATISTICS.getPath().equals(path)) {
            return ERepositoryObjectType.SYSTEM_INDICATORS_SIMPLE_STATISTICS;
        } else if (EResourceConstant.SYSTEM_INDICATORS_SOUNDEX.getPath().equals(path)) {
            return ERepositoryObjectType.SYSTEM_INDICATORS_SOUNDEX;
        } else if (EResourceConstant.SYSTEM_INDICATORS_SUMMARY_STATISTICS.getPath().equals(path)) {
            return ERepositoryObjectType.SYSTEM_INDICATORS_SUMMARY_STATISTICS;
        } else if (EResourceConstant.SYSTEM_INDICATORS_TEXT_STATISTICS.getPath().equals(path)) {
            return ERepositoryObjectType.SYSTEM_INDICATORS_TEXT_STATISTICS;
        } else if (EResourceConstant.USER_DEFINED_INDICATORS.getPath().equals(path)) {
            return ERepositoryObjectType.TDQ_USERDEFINE_INDICATORS;
        } else if (EResourceConstant.JRXML_TEMPLATE.getPath().equals(path)) {
            return ERepositoryObjectType.TDQ_JRXMLTEMPLATE;
        } else if (EResourceConstant.PATTERNS.getPath().equals(path)) {
            return ERepositoryObjectType.TDQ_PATTERNS;
        } else if (EResourceConstant.PATTERN_REGEX.getPath().equals(path)) {
            return ERepositoryObjectType.TDQ_PATTERN_REGEX;
        } else if (EResourceConstant.PATTERN_SQL.getPath().equals(path)) {
            return ERepositoryObjectType.TDQ_PATTERN_SQL;
        } else if (EResourceConstant.RULES.getPath().equals(path)) {
            return ERepositoryObjectType.TDQ_RULES;
        } else if (EResourceConstant.RULES_SQL.getPath().equals(path)) {
            return ERepositoryObjectType.TDQ_RULES_SQL;
        } else if (EResourceConstant.SOURCE_FILES.getPath().equals(path)) {
            return ERepositoryObjectType.TDQ_SOURCE_FILES;
        } else if (EResourceConstant.METADATA.getPath().equals(path)) {
            return ERepositoryObjectType.METADATA;
        } else if (EResourceConstant.DB_CONNECTIONS.getPath().equals(path)) {
            return ERepositoryObjectType.METADATA_CONNECTIONS;
        } else if (EResourceConstant.MDM_CONNECTIONS.getPath().equals(path)) {
            return ERepositoryObjectType.METADATA_MDMCONNECTION;
        } else if (EResourceConstant.FILEDELIMITED.getPath().equals(path)) {
            return ERepositoryObjectType.METADATA_FILE_DELIMITED;
        }
        return null;
    }

    public static List<DBColumnRepNode> getColumnNodeList(Object[] objs) {
        List<DBColumnRepNode> nodeList = new ArrayList<DBColumnRepNode>();
        for (Object obj : objs) {
            if (obj != null && obj instanceof DBColumnRepNode) {
                nodeList.add((DBColumnRepNode) obj);
            }
        }
        return nodeList;
    }

    public static boolean hasColumnNode(Object[] objs) {
        if (objs != null && objs.length > 0) {
            for (Object obj : objs) {
                if (obj != null && obj instanceof DBColumnRepNode) {
                    return true;
                }
            }
        }
        return false;
    }

    public static List<DBTableRepNode> getTableNodeList(Object[] objs) {
        List<DBTableRepNode> nodeList = new ArrayList<DBTableRepNode>();
        for (Object obj : objs) {
            if (obj != null && obj instanceof DBTableRepNode) {
                nodeList.add((DBTableRepNode) obj);
            }
        }
        return nodeList;
    }

    public static boolean hasTableNode(Object[] objs) {
        if (objs != null && objs.length > 0) {
            for (Object obj : objs) {
                if (obj != null && obj instanceof DBTableRepNode) {
                    return true;
                }
            }
        }
        return false;
    }

    public static List<DBViewRepNode> getViewNodeList(Object[] objs) {
        List<DBViewRepNode> nodeList = new ArrayList<DBViewRepNode>();
        for (Object obj : objs) {
            if (obj != null && obj instanceof DBViewRepNode) {
                nodeList.add((DBViewRepNode) obj);
            }
        }
        return nodeList;
    }

    public static boolean hasViewNode(Object[] objs) {
        if (objs != null && objs.length > 0) {
            for (Object obj : objs) {
                if (obj != null && obj instanceof DBViewRepNode) {
                    return true;
                }
            }
        }
        return false;
    }

    public static List<DBSchemaRepNode> getSchemaNodeList(Object[] objs) {
        List<DBSchemaRepNode> nodeList = new ArrayList<DBSchemaRepNode>();
        for (Object obj : objs) {
            if (obj != null && obj instanceof DBSchemaRepNode) {
                nodeList.add((DBSchemaRepNode) obj);
            }
        }
        return nodeList;
    }

    public static boolean hasSchemaNode(Object[] objs) {
        if (objs != null && objs.length > 0) {
            for (Object obj : objs) {
                if (obj != null && obj instanceof DBSchemaRepNode) {
                    return true;
                }
            }
        }
        return false;
    }

    public static List<DBCatalogRepNode> getCatalogNodeList(Object[] objs) {
        List<DBCatalogRepNode> nodeList = new ArrayList<DBCatalogRepNode>();
        for (Object obj : objs) {
            if (obj != null && obj instanceof DBCatalogRepNode) {
                nodeList.add((DBCatalogRepNode) obj);
            }
        }
        return nodeList;
    }

    public static boolean hasCatalogNode(Object[] objs) {
        if (objs != null && objs.length > 0) {
            for (Object obj : objs) {
                if (obj != null && obj instanceof DBCatalogRepNode) {
                    return true;
                }
            }
        }
        return false;
    }

    public static List<DBConnectionRepNode> getDbConnectionNodeList(Object[] objs) {
        List<DBConnectionRepNode> nodeList = new ArrayList<DBConnectionRepNode>();
        for (Object obj : objs) {
            if (obj != null && obj instanceof DBConnectionRepNode) {
                nodeList.add((DBConnectionRepNode) obj);
            }
        }
        return nodeList;
    }

    public static boolean hasDbConnectionNode(Object[] objs) {
        if (objs != null && objs.length > 0) {
            for (Object obj : objs) {
                if (obj != null && obj instanceof DBConnectionRepNode) {
                    return true;
                }
            }
        }
        return false;
    }

    public static List<MDMConnectionRepNode> getMdmConnectionNodeList(Object[] objs) {
        List<MDMConnectionRepNode> nodeList = new ArrayList<MDMConnectionRepNode>();
        for (Object obj : objs) {
            if (obj != null && obj instanceof MDMConnectionRepNode) {
                nodeList.add((MDMConnectionRepNode) obj);
            }
        }
        return nodeList;
    }

    public static boolean hasMdmConnectionNode(Object[] objs) {
        if (objs != null && objs.length > 0) {
            for (Object obj : objs) {
                if (obj != null && obj instanceof MDMConnectionRepNode) {
                    return true;
                }
            }
        }
        return false;
    }

    public static List<TdColumn> getTdColumnList(Object[] objs) {
        List<TdColumn> list = new ArrayList<TdColumn>();
        for (Object obj : objs) {
            if (obj != null && obj instanceof TdColumn) {
                list.add((TdColumn) obj);
            }
        }
        return list;
    }

    public static boolean hasTdColumn(Object[] objs) {
        if (objs != null && objs.length > 0) {
            for (Object obj : objs) {
                if (obj != null && obj instanceof TdColumn) {
                    return true;
                }
            }
        }
        return false;
    }


    public static RepositoryNode recursiveFind(ModelElement modelElement) {
        if (modelElement instanceof Analysis) {

        } else if (modelElement instanceof TdReport) {

        } else if (modelElement instanceof TdColumn) {
            TdColumn column = (TdColumn) modelElement;
            IRepositoryNode columnSetNode = recursiveFind(ColumnHelper.getColumnOwnerAsColumnSet(column));
            for (IRepositoryNode columnNode : columnSetNode.getChildren().get(0).getChildren()) {
                TdColumn columnOnUI = (TdColumn) ((MetadataColumnRepositoryObject) columnNode.getObject()).getTdColumn();
                if (ResourceHelper.getUUID(column).equals(ResourceHelper.getUUID(columnOnUI))) {
                    return (RepositoryNode) columnNode;
                }
            }

        } else if (modelElement instanceof TdTable) {
            TdTable table = (TdTable) modelElement;
            IRepositoryNode schemaOrCatalogNode = recursiveFind(ColumnSetHelper.getParentCatalogOrSchema(modelElement));
            for (IRepositoryNode tableNode : schemaOrCatalogNode.getChildren().get(0).getChildren()) {
                TdTable tableOnUI = (TdTable) ((TdTableRepositoryObject) tableNode.getObject()).getTdTable();
                if (ResourceHelper.getUUID(table).equals(ResourceHelper.getUUID(tableOnUI))) {
                    return (RepositoryNode) tableNode;
                }
            }

        } else if (modelElement instanceof TdView) {
            TdView view = (TdView) modelElement;
            IRepositoryNode schemaOrCatalogNode = recursiveFind(ColumnSetHelper.getParentCatalogOrSchema(modelElement));
            for (IRepositoryNode viewNode : schemaOrCatalogNode.getChildren().get(1).getChildren()) {
                TdView viewOnUI = (TdView) ((TdViewRepositoryObject) viewNode.getObject()).getTdView();
                if (ResourceHelper.getUUID(view).equals(ResourceHelper.getUUID(viewOnUI))) {
                    return (RepositoryNode) viewNode;
                }
            }
        } else if (modelElement instanceof MetadataColumn) {
            // MOD qiongli 2011-1-12 for delimted file
            MetadataColumn column = (MetadataColumn) modelElement;
            IRepositoryNode columnSetNode = recursiveFind(ColumnHelper.getColumnOwnerAsMetadataTable(column));
            for (IRepositoryNode columnNode : columnSetNode.getChildren().get(0).getChildren()) {
                MetadataColumn columnOnUI = ((MetadataColumnRepositoryObject) columnNode.getObject()).getTdColumn();
                if (ResourceHelper.getUUID(column).equals(ResourceHelper.getUUID(columnOnUI))) {
                    return (RepositoryNode) columnNode;
                }
            }

        } else if (modelElement instanceof MetadataTable) {
            // MOD qiongli 2011-1-12 for delimted file
            MetadataTable table = (MetadataTable) modelElement;
            if (table.getNamespace() instanceof RecordFile) {
                IRepositoryNode connNode = recursiveFind(ConnectionHelper.getTdDataProvider(table));
                for (IRepositoryNode tableNode : connNode.getChildren()) {
                    MetadataTable tableOnUI = (MetadataTable) ((MetadataTableRepositoryObject) tableNode.getObject()).getTable();
                    if (ResourceHelper.getUUID(table).equals(ResourceHelper.getUUID(tableOnUI))) {
                        return (RepositoryNode) tableNode;
                    }
                }
            }

        } else if (modelElement instanceof Catalog) {
            Catalog catalog = (Catalog) modelElement;
            IRepositoryNode connNode = recursiveFind(ConnectionHelper.getTdDataProvider(catalog));
            for (IRepositoryNode catalogNode : connNode.getChildren()) {
                Catalog catalogOnUI = ((MetadataCatalogRepositoryObject) catalogNode.getObject()).getCatalog();
                if (ResourceHelper.getUUID(catalog).equals(ResourceHelper.getUUID(catalogOnUI))) {
                    return (RepositoryNode) catalogNode;
                }
            }

        } else if (modelElement instanceof Schema) {
            Schema schema = (Schema) modelElement;
            Catalog catalog = CatalogHelper.getParentCatalog(schema);
            // Schema's parent is catalog (MS SQL Server)
            if (catalog != null) {
                IRepositoryNode catalogNode = recursiveFind(catalog);
                for (IRepositoryNode schemaNode : catalogNode.getChildren()) {
                    Schema schemaOnUI = ((MetadataSchemaRepositoryObject) schemaNode.getObject()).getSchema();
                    if (ResourceHelper.getUUID(schema).equals(ResourceHelper.getUUID(schemaOnUI))) {
                        return (RepositoryNode) schemaNode;
                    }
                }

            }
            // schema's parent is connection (e.g Oracle)
            IRepositoryNode connNode = recursiveFind(ConnectionHelper.getTdDataProvider(schema));
            for (IRepositoryNode schemaNode : connNode.getChildren()) {
                Schema schemaOnUI = ((MetadataSchemaRepositoryObject) schemaNode.getObject()).getSchema();
                if (ResourceHelper.getUUID(schema).equals(ResourceHelper.getUUID(schemaOnUI))) {
                    return (RepositoryNode) schemaNode;
                }
            }

        } else if (modelElement instanceof Connection) {
            Connection connection = (Connection) modelElement;
            List<IRepositoryNode> connsNode = getConnectionRepositoryNodes();
            for (IRepositoryNode connNode : connsNode) {
                Item itemTemp = ((IRepositoryViewObject) connNode.getObject()).getProperty().getItem();
                if (itemTemp instanceof ConnectionItem) {
                    ConnectionItem item = (ConnectionItem) itemTemp;
                    if (ResourceHelper.getUUID(connection).equals(ResourceHelper.getUUID(item.getConnection()))) {
                        return (RepositoryNode) connNode;
                    }
                } else if (itemTemp instanceof FolderItem) {
                    List<ConnectionItem> connItems = getConnectionItemsFromFolderItem((FolderItem) itemTemp);
                    for (ConnectionItem connItem : connItems) {
                        if (ResourceHelper.getUUID(connection).equals(ResourceHelper.getUUID(connItem.getConnection()))) {
                            return (RepositoryNode) connNode;
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * get all the ConnectionItems from FolderItem (recursive).
     * 
     * @param folderItem
     * @return
     */
    private static List<ConnectionItem> getConnectionItemsFromFolderItem(FolderItem folderItem) {
        List<ConnectionItem> list = new ArrayList<ConnectionItem>();
        EList objs = folderItem.getChildren();
        for (Object obj : objs) {
            if (obj instanceof FolderItem) {
                list.addAll(getConnectionItemsFromFolderItem((FolderItem) obj));
            } else if (obj instanceof ConnectionItem) {
                list.add((ConnectionItem) obj);
            }
        }
        return list;
    }

    /**
     * ADD mzhao 15750 , build dq metadata tree, get connection root node.
     */

    public static List<IRepositoryNode> getConnectionRepositoryNodes() {
        RepositoryNode node = getRootNode(EResourceConstant.METADATA.getName());
        List<IRepositoryNode> connNodes = new ArrayList<IRepositoryNode>();
        if (node != null) {
            List<IRepositoryNode> childrens = node.getChildren();
            for (IRepositoryNode subNode : childrens) {
                if (subNode instanceof DBConnectionFolderRepNode || subNode instanceof DFConnectionFolderRepNode) {
                    // don't add mdm connections
                    connNodes.addAll(subNode.getChildren());
                }
            }
        }
        return connNodes;
    }

    public static ModelElement getMetadataElement(RepositoryNode repositoryNode) {
        ISubRepositoryObject metadataObject = null;
        if (repositoryNode instanceof DBTableFolderRepNode || repositoryNode instanceof DBViewFolderRepNode
                || repositoryNode instanceof DBColumnFolderRepNode) {
                metadataObject = (ISubRepositoryObject) repositoryNode.getParent().getObject();
        } else if (repositoryNode.getObject() instanceof ISubRepositoryObject) {
            metadataObject = (ISubRepositoryObject) repositoryNode.getObject();
        }
        if (metadataObject != null) {
            return metadataObject.getModelElement();
        }
        return null;
    }

    /**
     * DOC klliu Comment method "getRootNode".
     * 
     * @return
     */
    private static RepositoryNode getRootNode(String nodeName) {
        RepositoryNode node = null;
        CommonViewer commonViewer = getDQCommonViewer();
        if (commonViewer != null) {
            TreeItem[] items = commonViewer.getTree().getItems();
            for (TreeItem item : items) {
                String text = item.getText();
                if (text.equals(nodeName)) {
                    node = (RepositoryNode) item.getData();
                }
            }

        }
        return node;
    }

    /**
     * DOC klliu 15750 Comment method "getDQRespositoryView".
     * 
     * @return
     */
    private static CommonViewer getDQCommonViewer() {
        IViewPart part = null;
        CommonViewer commonViewer = null;
        IWorkbenchWindow activeWorkbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (activeWorkbenchWindow != null) {
            IWorkbenchPage activePage = activeWorkbenchWindow.getActivePage();
            if (activePage != null) {
                part = activePage.findView(DQRESPOSITORYVIEW);
                if (part == null) {
                    return null;
                }
                CommonNavigator dqView = (CommonNavigator) part;
                commonViewer = dqView.getCommonViewer();
            }
        }
        return commonViewer;
    }


    public static boolean canOpenEditor(RepositoryNode node) {
        return node instanceof AnalysisRepNode || node instanceof SysIndicatorDefinitionRepNode || node instanceof PatternRepNode
                || node instanceof JrxmlTempleteRepNode || node instanceof SourceFileRepNode || node instanceof RuleRepNode
                || node instanceof DBConnectionRepNode || node instanceof MDMConnectionRepNode;
    }

}
