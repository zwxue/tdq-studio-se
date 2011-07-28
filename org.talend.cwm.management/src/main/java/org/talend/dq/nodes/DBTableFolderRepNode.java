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
package org.talend.dq.nodes;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.talend.core.model.general.Project;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.metadata.builder.database.DqRepositoryViewService;
import org.talend.core.model.properties.ConnectionItem;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.core.repository.model.repositoryObject.MetadataCatalogRepositoryObject;
import org.talend.core.repository.model.repositoryObject.MetadataSchemaRepositoryObject;
import org.talend.core.repository.model.repositoryObject.TdTableRepositoryObject;
import org.talend.cwm.db.connection.ConnectionUtils;
import org.talend.cwm.helper.PackageHelper;
import org.talend.cwm.relational.TdTable;
import org.talend.dq.helper.RepositoryNodeHelper;
import org.talend.dq.writer.impl.ElementWriterFactory;
import org.talend.repository.ProjectManager;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.RepositoryNode;
import orgomg.cwm.objectmodel.core.Package;
import orgomg.cwm.resource.relational.Catalog;
import orgomg.cwm.resource.relational.Schema;

/**
 * DOC klliu Folder node node displayed on repository view (UI), knowing exact folder type by folder
 * object:TDQFolderObject.
 */
public class DBTableFolderRepNode extends DQRepositoryNode {

    private static Logger log = Logger.getLogger(DBTableFolderRepNode.class);

    private IRepositoryViewObject viewObject;

    private Catalog catalog;

    private ConnectionItem item;

    private Connection connection;

    private Schema schema;

    // private List<IRepositoryNode> children;

    public Catalog getCatalog() {
        return this.catalog;
    }

    public ConnectionItem getItem() {
        return this.item;
    }

    public Connection getConnection() {
        return this.connection;
    }

    public Schema getSchema() {
        return this.schema;
    }

    /**
     * DOC klliu FolderRepNode constructor comment.
     * 
     * @param object
     * @param parent
     * @param type
     */
    public DBTableFolderRepNode(IRepositoryViewObject object, RepositoryNode parent, ENodeType type) {
        super(object, parent, type);
        // children = new ArrayList<IRepositoryNode>();
    }

    @Override
    public List<IRepositoryNode> getChildren() {
        // MOD gdbu 2011-7-1 bug : 22204
        List<IRepositoryNode> children = new ArrayList<IRepositoryNode>();
        IRepositoryViewObject object = this.getParent().getObject();
        createRepositoryNodeTableFolderNode(children, object);
        // ADD msjian 2011-7-22 22206: fix the note 93101
        if (DQRepositoryNode.isUntilSchema()) {
            return children;
        }
        // ~22206
        return filterResultsIfAny(children);
        // ~22204
    }

    /**
     * Create TableFolderNodeRepositoryNode.
     * 
     * @param node parent RepositoryNode
     * @param metadataObject parent CatalogViewObject or SchemaViewObject
     */
    private void createRepositoryNodeTableFolderNode(List<IRepositoryNode> node, IRepositoryViewObject metadataObject) {
        List<TdTable> tables = new ArrayList<TdTable>();
        String filterCharacter = null;
        try {
            if (metadataObject instanceof MetadataCatalogRepositoryObject) {
                viewObject = ((MetadataCatalogRepositoryObject) metadataObject).getViewObject();
                item = (ConnectionItem) viewObject.getProperty().getItem();
                connection = item.getConnection();
                catalog = ((MetadataCatalogRepositoryObject) metadataObject).getCatalog();
                tables = PackageHelper.getTables(catalog);
                filterCharacter = RepositoryNodeHelper.getTableFilter(catalog, schema);
                // MOD mzhao 0022204 : when the tree is rendering with a filter, do not loading from db.
                if (tables.isEmpty()) {
                    if (isOnFilterring()) {
                        tables = DqRepositoryViewService.getTables(connection, catalog, null, false);
                    } else {
                        tables = DqRepositoryViewService.getTables(connection, catalog, null, true);
                    }
                    if (tables.size() > 0) {
                        ElementWriterFactory.getInstance().createDataProviderWriter().save(item);
                    }
                } else {
                    ConnectionUtils.retrieveColumn(tables);
                }

            } else {
                viewObject = ((MetadataSchemaRepositoryObject) metadataObject).getViewObject();
                item = (ConnectionItem) viewObject.getProperty().getItem();
                connection = item.getConnection();
                schema = ((MetadataSchemaRepositoryObject) metadataObject).getSchema();
                tables = PackageHelper.getTables(schema);
                filterCharacter = RepositoryNodeHelper.getTableFilter(catalog, schema);
                RepositoryNode parent = metadataObject.getRepositoryNode().getParent();
                IRepositoryViewObject object = parent.getObject();
                if (object instanceof MetadataCatalogRepositoryObject && filterCharacter.equals("")) {
                    filterCharacter = RepositoryNodeHelper.getTableFilter(
                            ((MetadataCatalogRepositoryObject) object).getCatalog(), null);
                }
                // MOD mzhao 0022204 : when the tree is rendering with a filter, do not loading from db.
                if (tables.isEmpty()) {
                    if (isOnFilterring()) {
                        tables = DqRepositoryViewService.getTables(connection, schema, null, false);
                    } else {
                        tables = DqRepositoryViewService.getTables(connection, schema, null, true);
                    }
                    if (tables.size() > 0) {
                        ElementWriterFactory.getInstance().createDataProviderWriter().save(item);
                    }
                }

                // -------------delete this code
                else {
                    ConnectionUtils.retrieveColumn(tables);
                }
                // -------------

            }

            if (tables.size() > 0) {
                // MOD qiongli 2011-6-28 bug 22019,only need to save connection in this place.
                Project currentProject = ProjectManager.getInstance().getCurrentProject();
                ProxyRepositoryFactory.getInstance().save(currentProject, item);
            } else {
                ConnectionUtils.retrieveColumn(tables);
            }
            if (filterCharacter != null && !filterCharacter.equals("")) {
                tables = RepositoryNodeHelper.filterTables(tables, filterCharacter);
            }

        } catch (Exception e) {
            log.error(e, e);
        }
        createTableRepositoryNode(tables, node);
    }

    /**
     * DOC klliu Comment method "createTableRepositoryNode".
     * 
     * @param tables
     */
    private void createTableRepositoryNode(List<TdTable> tables, List<IRepositoryNode> node) {
        for (TdTable table : tables) {
            table.setTableType("TABLE"); //$NON-NLS-1$
            TdTableRepositoryObject metadataTable = new TdTableRepositoryObject(viewObject, table);
            metadataTable.setTableName(table.getName());
            metadataTable.setLabel(table.getName());
            metadataTable.setId(table.getName());

            DBTableRepNode tableNode = new DBTableRepNode(metadataTable, this, ENodeType.TDQ_REPOSITORY_ELEMENT);
            tableNode.setProperties(EProperties.LABEL, ERepositoryObjectType.METADATA_CON_TABLE);
            tableNode.setProperties(EProperties.CONTENT_TYPE, ERepositoryObjectType.METADATA_CON_TABLE);

            metadataTable.setRepositoryNode(tableNode);
            node.add(tableNode);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.repository.model.RepositoryNode#getLabel()
     */
    @Override
    public String getLabel() {
        return "Tables (" + this.getChildrenCount() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
    }

    private int getChildrenCount() {
        List<TdTable> tables = new ArrayList<TdTable>();
        IRepositoryViewObject object = this.getParent().getObject();
        if (object instanceof MetadataCatalogRepositoryObject) {
            catalog = ((MetadataCatalogRepositoryObject) object).getCatalog();
            tables = PackageHelper.getTables(catalog);
        } else {
            schema = ((MetadataSchemaRepositoryObject) object).getSchema();
            tables = PackageHelper.getTables(schema);
        }
        return tables.size();
    }

    public boolean hasChildren() {
        return hasChildrenInDataBase();
    }

    private boolean hasChildrenInDataBase() {

        boolean hasChildrenInDB = false;

        IRepositoryViewObject object = this.getParent().getObject();
        if (object instanceof MetadataCatalogRepositoryObject) {
            viewObject = ((MetadataCatalogRepositoryObject) object).getViewObject();
            item = (ConnectionItem) viewObject.getProperty().getItem();
            connection = item.getConnection();
            catalog = ((MetadataCatalogRepositoryObject) object).getCatalog();
            try {
                hasChildrenInDB = DqRepositoryViewService.isContainsTable(connection, catalog, null);
            } catch (Exception e) {
                log.error(e.toString());
            }
        } else {
            viewObject = ((MetadataSchemaRepositoryObject) object).getViewObject();
            item = (ConnectionItem) viewObject.getProperty().getItem();
            connection = item.getConnection();
            schema = ((MetadataSchemaRepositoryObject) object).getSchema();
            try {
                hasChildrenInDB = DqRepositoryViewService.isContainsTable(connection, schema, null);
            } catch (Exception e) {
                log.error(e.toString());
            }
        }
        return hasChildrenInDB;
    }

    /**
     * return the Catalog or Schema, or null.
     * 
     * @return
     */
    public Package getPackage() {
        return this.getCatalog() != null ? this.getCatalog() : this.getSchema();
    }
}
