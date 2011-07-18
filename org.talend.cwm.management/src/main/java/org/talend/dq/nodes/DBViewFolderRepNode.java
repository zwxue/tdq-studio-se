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
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.metadata.builder.database.DqRepositoryViewService;
import org.talend.core.model.properties.ConnectionItem;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.repository.model.repositoryObject.MetadataCatalogRepositoryObject;
import org.talend.core.repository.model.repositoryObject.MetadataSchemaRepositoryObject;
import org.talend.core.repository.model.repositoryObject.TdViewRepositoryObject;
import org.talend.cwm.db.connection.ConnectionUtils;
import org.talend.cwm.helper.PackageHelper;
import org.talend.cwm.relational.TdView;
import org.talend.dq.helper.RepositoryNodeHelper;
import org.talend.dq.writer.impl.ElementWriterFactory;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.RepositoryNode;
import orgomg.cwm.objectmodel.core.Package;
import orgomg.cwm.resource.relational.Catalog;
import orgomg.cwm.resource.relational.Schema;

/**
 * DOC klliu class global comment. Detailled comment
 */
public class DBViewFolderRepNode extends DQRepositoryNode {

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
        return this.connection;
    }

    public Schema getSchema() {
        return this.schema;
    }

    /**
     * DOC klliu ViewFolderRepNode constructor comment.
     * 
     * @param object
     * @param parent
     * @param type
     */
    public DBViewFolderRepNode(IRepositoryViewObject object, RepositoryNode parent, ENodeType type) {
        super(object, parent, type);
        
    }

    @Override
    public List<IRepositoryNode> getChildren() {
        // MOD gdbu 2011-7-1 bug : 22204
        List<IRepositoryNode> repsNodes = new ArrayList<IRepositoryNode>();
        IRepositoryViewObject object = this.getParent().getObject();
        createRepositoryNodeViewFolderNode(repsNodes, object);
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
                catalog = ((MetadataCatalogRepositoryObject) metadataObject).getCatalog();
                item = (ConnectionItem) viewObject.getProperty().getItem();
                connection = item.getConnection();
                views = PackageHelper.getViews(catalog);
                filterCharacter = RepositoryNodeHelper.getViewFilter(catalog, schema);
                // MOD gdbu 2011-6-29 bug : 22204
                if (views.isEmpty() && !isOnFilterring()) {
                    // ~22204
                    views = DqRepositoryViewService.getViews(connection, catalog, null, true);
                    if (views.size() > 0) {
                        ElementWriterFactory.getInstance().createDataProviderWriter().save(item);
                    }
                } else {
                    ConnectionUtils.retrieveColumn(views);
                }
            } else if (metadataObject instanceof MetadataSchemaRepositoryObject) {
                viewObject = ((MetadataSchemaRepositoryObject) metadataObject).getViewObject();
                schema = ((MetadataSchemaRepositoryObject) metadataObject).getSchema();
                item = (ConnectionItem) viewObject.getProperty().getItem();
                connection = item.getConnection();
                views = PackageHelper.getViews(schema);
                filterCharacter = RepositoryNodeHelper.getViewFilter(catalog, schema);
                RepositoryNode parent = metadataObject.getRepositoryNode().getParent();
                IRepositoryViewObject object = parent.getObject();
                if (object instanceof MetadataCatalogRepositoryObject && filterCharacter.equals("")) {
                    filterCharacter = RepositoryNodeHelper.getViewFilter(((MetadataCatalogRepositoryObject) object).getCatalog(),
                            null);
                }
                // MOD gdbu 2011-6-29 bug : 22204
                if (views.isEmpty() && !isOnFilterring()) {
                    // ~22204
                    views = DqRepositoryViewService.getViews(connection, schema, null, true);
                    if (views.size() > 0) {
                        ElementWriterFactory.getInstance().createDataProviderWriter().save(item);
                    }
                } else {
                    ConnectionUtils.retrieveColumn(views);
                }
            }

        } catch (Exception e) {
            log.error(e, e);
        }
        if (filterCharacter != null && !filterCharacter.equals("")) {
            views = RepositoryNodeHelper.filterViews(views, filterCharacter);
        }
        createTableRepositoryNode(views, node);
    }

    /**
     * DOC klliu Comment method "createTableRepositoryNode".
     * 
     * @param tables
     */
    private void createTableRepositoryNode(List<TdView> views, List<IRepositoryNode> node) {
        for (TdView view : views) {
            // create view object
            TdViewRepositoryObject metadataView = new TdViewRepositoryObject(viewObject, view);
            metadataView.setTableName(view.getName());
            metadataView.setLabel(view.getName());
            metadataView.setId(view.getName());
            // create a node for ui
            DBViewRepNode viewNode = new DBViewRepNode(metadataView, this, ENodeType.TDQ_REPOSITORY_ELEMENT);
            viewNode.setProperties(EProperties.LABEL, ERepositoryObjectType.METADATA_CON_TABLE);
            viewNode.setProperties(EProperties.CONTENT_TYPE, ERepositoryObjectType.METADATA_CON_VIEW);

            metadataView.setRepositoryNode(viewNode);
            node.add(viewNode);
        }
    }

    public String getNodeName() {
        return "Views (" + this.getChildrenCount() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
    }

    public int getChildrenCount() {
        List<IRepositoryNode> children2 = this.getChildren();
        if (children2 != null) {
            return children2.size();
        }
        return 0;
    }

    /**
     * return the Catalog or Schema, or null.
     * 
     * @return
     */
    public Package getPackage() {
        return this.getCatalog() != null ? this.getCatalog() : this.getSchema();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.repository.model.RepositoryNode#getLabel()
     */
    @Override
    public String getLabel() {
        if (getObject() == null) {
            return this.getProperties(EProperties.LABEL).toString();
        }
        return this.getNodeName();
    }
}
