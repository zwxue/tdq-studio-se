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
package org.talend.dq.nodes;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.metadata.builder.database.DqRepositoryViewService;
import org.talend.core.model.properties.ConnectionItem;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.repository.model.repositoryObject.MetadataCatalogRepositoryObject;
import org.talend.core.repository.model.repositoryObject.MetadataSchemaRepositoryObject;
import org.talend.core.repository.model.repositoryObject.TdTableRepositoryObject;
import org.talend.cwm.relational.TdTable;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.RepositoryNode;
import orgomg.cwm.objectmodel.core.ModelElement;
import orgomg.cwm.resource.relational.Catalog;
import orgomg.cwm.resource.relational.Schema;

/**
 * DOC klliu Folder node node displayed on repository view (UI), knowing exact folder type by folder
 * object:TDQFolderObject
 */
public class DBTableFolderRepNode extends RepositoryNode {

    private static Logger log = Logger.getLogger(DBTableFolderRepNode.class);

    private IRepositoryViewObject viewObject;

    private Catalog catalog;

    private ConnectionItem item;

    private Connection connection;

    private Schema schema;

    /**
     * DOC klliu FolderRepNode constructor comment.
     * 
     * @param object
     * @param parent
     * @param type
     */
    public DBTableFolderRepNode(IRepositoryViewObject object, RepositoryNode parent, ENodeType type) {
        super(object, parent, type);
        // TODO Auto-generated constructor stub
    }

    @Override
    public List<IRepositoryNode> getChildren() {
        List<IRepositoryNode> repsNodes = new ArrayList<IRepositoryNode>();
        IRepositoryViewObject object = this.getParent().getObject();
        createRepositoryNodeTableFolderNode(repsNodes, object);
        return repsNodes;
    }

    /**
     * Create TableFolderNodeRepositoryNode.
     * 
     * @param node parent RepositoryNode
     * @param metadataObject parent CatalogViewObject or SchemaViewObject
     */
    private void createRepositoryNodeTableFolderNode(List<IRepositoryNode> node, IRepositoryViewObject metadataObject) {
        List<TdTable> tables = new ArrayList<TdTable>();
        if (metadataObject instanceof MetadataCatalogRepositoryObject) {
            viewObject = ((MetadataCatalogRepositoryObject) metadataObject).getViewObject();
            catalog = ((MetadataCatalogRepositoryObject) metadataObject).getCatalog();
            item = (ConnectionItem) viewObject.getProperty().getItem();
            connection = item.getConnection();
        } else if (metadataObject instanceof MetadataSchemaRepositoryObject) {
            viewObject = ((MetadataSchemaRepositoryObject) metadataObject).getViewObject();
            schema = ((MetadataSchemaRepositoryObject) metadataObject).getSchema();
            item = (ConnectionItem) viewObject.getProperty().getItem();
            connection = item.getConnection();
        }
        EList<ModelElement> ownedElement = null;
        if (catalog != null) {
            ownedElement = catalog.getOwnedElement();
        }

        if (ownedElement != null && ownedElement.size() <= 0) {
            try {
                if (catalog != null) {
                    tables = DqRepositoryViewService.getTables(connection, catalog, null, true);
                } else if (schema != null) {
                    tables = DqRepositoryViewService.getTables(connection, schema, null, true);
                }
                for (ModelElement element : ownedElement) {
                    if (element instanceof TdTable) {
                        TdTable table = (TdTable) element;
                        tables.add(table);
                    }
                }
            } catch (Exception e) {
                log.error(e, e);
            }
        } else {
            if (tables.size() <= 0) {
                try {
                    if (catalog != null) {
                        tables = DqRepositoryViewService.getTables(connection, catalog, null, true);
                    } else if (schema != null) {
                        tables = DqRepositoryViewService.getTables(connection, schema, null, true);
                    }
                } catch (Exception e) {
                    log.error(e, e);
                }
            }

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
            table.setTableType("TABLE");
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

    public String getNodeName() {
        return "Tables";
    }
}
