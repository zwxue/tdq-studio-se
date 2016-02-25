// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
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
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.metadata.builder.database.DqRepositoryViewService;
import org.talend.core.model.properties.ConnectionItem;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.model.repository.ISubRepositoryObject;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.core.repository.model.repositoryObject.MetadataCatalogRepositoryObject;
import org.talend.core.repository.model.repositoryObject.MetadataSchemaRepositoryObject;
import org.talend.core.repository.model.repositoryObject.TdViewRepositoryObject;
import org.talend.cwm.db.connection.ConnectionUtils;
import org.talend.cwm.helper.PackageHelper;
import org.talend.cwm.relational.TdView;
import org.talend.dataquality.PluginConstant;
import org.talend.dq.helper.RepositoryNodeHelper;
import org.talend.dq.nodes.factory.DQRepNodeCreateFactory;
import org.talend.dq.nodes.foldernode.IConnectionElementSubFolder;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.RepositoryNode;
import org.talend.utils.exceptions.MissingDriverException;
import orgomg.cwm.objectmodel.core.Package;
import orgomg.cwm.resource.relational.Catalog;
import orgomg.cwm.resource.relational.Schema;

/**
 * DOC klliu class global comment. Detailled comment
 */
public class DBViewFolderRepNode extends DQDBFolderRepositoryNode implements IConnectionElementSubFolder {

    private static Logger log = Logger.getLogger(DBViewFolderRepNode.class);

    private IRepositoryViewObject viewObject;

    private Catalog catalog;

    private ConnectionItem item;

    private Connection connection;

    private Schema schema;

    public Catalog getCatalog() {
        return this.catalog;
    }

    public ConnectionItem getItem() {
        return this.item;
    }

    public Connection getConnection() {
        if (this.connection == null) {
            getConnectionFromViewObject();
        }
        return this.connection;
    }

    public Schema getSchema() {
        return this.schema;
    }

    /**
     * DOC klliu ViewFolderRepNode constructor comment.
     * 
     * @param object
     * @param parent if parent is null will try to create new one to insert of old parent.
     * @param type
     */
    public DBViewFolderRepNode(IRepositoryViewObject object, RepositoryNode parent, ENodeType type,
            org.talend.core.model.general.Project inWhichProject) {
        super(object, parent, type, inWhichProject);
        this.viewObject = object;
        if (parent == null) {
            RepositoryNode createParentNode = createParentNode();
            this.setParent(createParentNode);
        }

    }

    /**
     * DOC talend Comment method "setConnection".
     * 
     * @param object
     */
    private void getConnectionFromViewObject() {
        IRepositoryViewObject object = this.getObject() == null ? this.getParent().getObject() : this.getObject();
        if (object != null && object instanceof ISubRepositoryObject) {
            Property property = ((ISubRepositoryObject) object).getProperty();
            if (property == null) {
                return;
            }
            Item theItem = property.getItem();
            if (theItem != null && theItem instanceof ConnectionItem) {
                connection = ((ConnectionItem) theItem).getConnection();
            }
        }
    }

    /**
     * create the node of parent.
     * 
     * @param object
     * @return
     */
    private RepositoryNode createParentNode() {
        RepositoryNode dbParentRepNode = null;
        if (viewObject instanceof MetadataCatalogRepositoryObject) {
            dbParentRepNode = DQRepNodeCreateFactory.createDBCatalogRepNode(viewObject, null, ENodeType.TDQ_REPOSITORY_ELEMENT,
                    getProject());
        } else if (viewObject instanceof MetadataSchemaRepositoryObject) {
            dbParentRepNode = new DBSchemaRepNode(viewObject, null, ENodeType.TDQ_REPOSITORY_ELEMENT, getProject());
        }
        viewObject.setRepositoryNode(dbParentRepNode);
        return dbParentRepNode;
    }

    @Override
    public List<IRepositoryNode> getChildren() {
        // MOD gdbu 2011-7-1 bug : 22204
        List<IRepositoryNode> repsNodes = new ArrayList<IRepositoryNode>();
        IRepositoryViewObject object = this.getParent().getObject();
        createRepositoryNodeViewFolderNode(repsNodes, object);
        // ADD msjian 2011-7-22 22206: fix the note 93101
        if (DQRepositoryNode.isUntilSchema()) {
            return repsNodes;
        }
        // ~22206
        return filterResultsIfAny(repsNodes);
        // ~22204
    }

    /**
     * Create TableFolderNodeRepositoryNode.
     * 
     * @param node parent RepositoryNode
     * @param metadataObject parent CatalogViewObject or SchemaViewObject
     */
    private void createRepositoryNodeViewFolderNode(List<IRepositoryNode> node, IRepositoryViewObject metadataObject) {
        List<TdView> views = new ArrayList<TdView>();
        String filterCharacter = null;
        try {
            if (metadataObject instanceof MetadataCatalogRepositoryObject) {
                viewObject = ((MetadataCatalogRepositoryObject) metadataObject).getViewObject();
                if (((MetadataCatalogRepositoryObject) metadataObject).getCatalog().eIsProxy()) {
                    // reload the connection to make sure the connection(and all it's owned elements) is not proxy
                    reloadConnectionViewObject();
                }
                catalog = ((MetadataCatalogRepositoryObject) metadataObject).getCatalog();
                item = (ConnectionItem) viewObject.getProperty().getItem();
                views = PackageHelper.getViews(catalog);
                filterCharacter = RepositoryNodeHelper.getViewFilter(catalog, schema);

                // MOD gdbu 2011-6-29 bug : 22204
                // MOD TDQ-8718 20140505 yyin --the repository view cares about if use the filter or not, the column
                // select dialog cares about if connect to DB or not.
                if (views.isEmpty()) {
                    connection = item.getConnection();
                    if (isCallingFromColumnDialog()) {
                        views = DqRepositoryViewService.getViews(connection, catalog, null, isLoadDBFromDialog(), true);
                    } else if (!isOnFilterring()) {
                        // MOD gdbu 2011-7-21 bug 23220
                        views = DqRepositoryViewService.getViews(connection, catalog, null, true, true);
                    }
                    if (views != null && views.size() > 0) {
                        ProxyRepositoryFactory.getInstance().save(item, false);
                    }
                }

                ConnectionUtils.retrieveColumn(views);
                // ~22204
            } else if (metadataObject instanceof MetadataSchemaRepositoryObject) {
                viewObject = ((MetadataSchemaRepositoryObject) metadataObject).getViewObject();
                if (((MetadataSchemaRepositoryObject) metadataObject).getSchema().eIsProxy()) {
                    // reload the connection to make sure the connection(and all it's owned elements) is not proxy
                    reloadConnectionViewObject();
                }
                schema = ((MetadataSchemaRepositoryObject) metadataObject).getSchema();
                item = (ConnectionItem) viewObject.getProperty().getItem();
                views = PackageHelper.getViews(schema);
                filterCharacter = RepositoryNodeHelper.getViewFilter(catalog, schema);
                RepositoryNode parent = metadataObject.getRepositoryNode().getParent();
                IRepositoryViewObject object = parent.getObject();
                if (object instanceof MetadataCatalogRepositoryObject && filterCharacter.equals(PluginConstant.EMPTY_STRING)) {
                    filterCharacter = RepositoryNodeHelper.getViewFilter(((MetadataCatalogRepositoryObject) object).getCatalog(),
                            null);
                }
                // MOD gdbu 2011-6-29 bug : 22204
                if (views.isEmpty()) {
                    connection = item.getConnection();
                    if (isCallingFromColumnDialog()) {
                        views = DqRepositoryViewService.getViews(connection, schema, null, isLoadDBFromDialog(), true);
                    } else if (!isOnFilterring()) {
                        // MOD gdbu 2011-7-21 bug 23220
                        views = DqRepositoryViewService.getViews(connection, schema, null, true, true);
                    }
                    if (views != null && views.size() > 0) {
                        ProxyRepositoryFactory.getInstance().save(item, false);
                    }
                } else {
                    ConnectionUtils.retrieveColumn(views);
                }
                // ~22204
            }

        } catch (Exception e) {
            log.error(e, e);
        }
        if (filterCharacter != null && !filterCharacter.equals(PluginConstant.EMPTY_STRING)) {
            views = RepositoryNodeHelper.filterViews(views, filterCharacter);
        }
        createViewRepositoryNode(views, node);
    }

    /**
     * DOC klliu Comment method "createTableRepositoryNode".
     * 
     * @param tables
     */
    private void createViewRepositoryNode(List<TdView> views, List<IRepositoryNode> node) {
        if (views != null) {
            for (TdView view : views) {
                // create view object
                TdViewRepositoryObject metadataView = new TdViewRepositoryObject(viewObject, view);
                metadataView.setTableName(view.getName());
                metadataView.setLabel(view.getName());
                metadataView.setId(view.getName());
                // create a node for ui
                DBViewRepNode viewNode = new DBViewRepNode(metadataView, this, ENodeType.TDQ_REPOSITORY_ELEMENT, getProject());
                viewNode.setProperties(EProperties.LABEL, ERepositoryObjectType.METADATA_CON_TABLE);
                viewNode.setProperties(EProperties.CONTENT_TYPE, ERepositoryObjectType.METADATA_CON_VIEW);

                metadataView.setRepositoryNode(viewNode);
                node.add(viewNode);
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.repository.model.RepositoryNode#getLabel()
     */
    @Override
    public String getLabel() {
        return "Views (" + this.getChildrenCount() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
    }

    /*
     * ADD gdbu 2011-7-25 bug : 23220
     * 
     * children count : only read from the file
     */
    private int getChildrenCount() {
        List<TdView> tables = new ArrayList<TdView>();
        IRepositoryViewObject object = this.getParent().getObject();
        if (object instanceof MetadataCatalogRepositoryObject) {
            catalog = ((MetadataCatalogRepositoryObject) object).getCatalog();
            tables = PackageHelper.getViews(catalog);
        } else {
            schema = ((MetadataSchemaRepositoryObject) object).getSchema();
            tables = PackageHelper.getViews(schema);
        }
        return tables.size();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.repository.model.RepositoryNode#hasChildren()
     */
    @Override
    public boolean hasChildren() {
        // MOD gdbu 2011-9-1 TDQ-3457
        if (!hasChildrenInFile()) {
            if (isOnFilterring()) {
                return false;
            } else {
                if (!hasChildrenInDataBase()) {
                    return false;
                }
            }
        }
        return true;
        // ~ TDQ-3457
    }

    private boolean hasChildrenInFile() {
        IRepositoryViewObject object = this.getParent().getObject();
        if (object instanceof MetadataCatalogRepositoryObject) {
            Catalog catalogInFile = ((MetadataCatalogRepositoryObject) object).getCatalog();
            List<TdView> views = PackageHelper.getViews(catalogInFile);
            if (views.isEmpty()) {
                return false;
            } else {
                return true;
            }
        } else if (object instanceof MetadataSchemaRepositoryObject) {
            Schema schemaInFile = ((MetadataSchemaRepositoryObject) object).getSchema();
            List<TdView> views = PackageHelper.getViews(schemaInFile);
            if (views.isEmpty()) {
                return false;
            } else {
                return true;
            }
        }
        return false;
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
                hasChildrenInDB = DqRepositoryViewService.isCatalogHasChildren(connection, catalog, null,
                        DqRepositoryViewService.VIEW_TYPES);
            } catch (Exception e) {
                log.error(e.toString());
            }

        } else {
            viewObject = ((MetadataSchemaRepositoryObject) object).getViewObject();
            item = (ConnectionItem) viewObject.getProperty().getItem();
            connection = item.getConnection();
            schema = ((MetadataSchemaRepositoryObject) object).getSchema();
            try {
                hasChildrenInDB = DqRepositoryViewService.isSchemaHasChildren(connection, schema, null,
                        DqRepositoryViewService.VIEW_TYPES);
            } catch (MissingDriverException e) {
                throw e;
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
        Package result = null;

        if (this.getCatalog() != null) {
            result = this.getCatalog();
        } else if (this.getSchema() != null) {
            result = this.getSchema();
        } else {
            RepositoryNode parent = this.getParent();

            if (parent instanceof DBSchemaRepNode) {
                this.schema = ((DBSchemaRepNode) parent).getSchema();
                result = this.schema;
            } else if (parent instanceof DBCatalogRepNode) {
                this.catalog = ((DBCatalogRepNode) parent).getCatalog();
                result = this.catalog;
            }
        }

        return result;
    }
}
