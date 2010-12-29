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
package org.talend.dataprofiler.core.ui.views.nodes;

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
import org.talend.core.repository.model.repositoryObject.TdViewRepositoryObject;
import org.talend.cwm.relational.TdView;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.RepositoryNode;
import orgomg.cwm.objectmodel.core.ModelElement;
import orgomg.cwm.resource.relational.Catalog;
import orgomg.cwm.resource.relational.Schema;

/**
 * DOC klliu class global comment. Detailled comment
 */
public class DBViewFolderRepNode extends RepositoryNode {

    private static Logger log = Logger.getLogger(DBViewFolderRepNode.class);

    private IRepositoryViewObject viewObject;

    private Catalog catalog;

    private ConnectionItem item;

    private Connection connection;

    private Schema schema;

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
        List<IRepositoryNode> repsNodes = new ArrayList<IRepositoryNode>();
        IRepositoryViewObject object = this.getParent().getObject();
        createRepositoryNodeViewFolderNode(repsNodes, object);
        return repsNodes;
    }

    /**
     * Create TableFolderNodeRepositoryNode.
     * 
     * @param node parent RepositoryNode
     * @param metadataObject parent CatalogViewObject or SchemaViewObject
     */
    private void createRepositoryNodeViewFolderNode(List<IRepositoryNode> node, IRepositoryViewObject metadataObject) {
        List<TdView> views = new ArrayList<TdView>();
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
        EList<ModelElement> ownedElement = catalog.getOwnedElement();
        if (ownedElement.size() <= 0) {
            views = getViews(new ArrayList<TdView>());
        } else {
            for (ModelElement element : ownedElement) {
                if (element instanceof TdView) {
                    TdView view = (TdView) element;
                    views.add(view);
                }
            }
            if (views.size() <= 0) {
                views = getViews(new ArrayList<TdView>());
            }
        }
        createTableRepositoryNode(views, node);
    }

    /**
     * DOC klliu Comment method "getViews".
     * 
     * @param catalog2
     * @param schema2
     * @return
     */
    private List<TdView> getViews(List<TdView> tdviews) {
        try {

            if (catalog != null) {
                tdviews = DqRepositoryViewService.getViews(connection, catalog, null, true);
            } else if (schema != null) {
                tdviews = DqRepositoryViewService.getViews(connection, schema, null, true);
            }
        } catch (Exception e) {
            log.error(e, e);
        }
        return tdviews;
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
            viewNode.setProperties(EProperties.CONTENT_TYPE, ERepositoryObjectType.METADATA_CON_TABLE);

            metadataView.setRepositoryNode(viewNode);
            node.add(viewNode);
        }
    }

    public String getNodeName() {
        return "Views";
    }
}
