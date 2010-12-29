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

import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.repository.model.repositoryObject.MetadataCatalogRepositoryObject;
import org.talend.core.repository.model.repositoryObject.MetadataSchemaRepositoryObject;
import org.talend.cwm.helper.CatalogHelper;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.RepositoryNode;
import orgomg.cwm.resource.relational.Schema;

/**
 * DOC klliu Database catalog repository node displayed on repository view (UI).
 */
public class DBCatalogRepNode extends RepositoryNode {

    private IRepositoryViewObject parentObject;

    /**
     * DOC klliu DBCatalogRepNode constructor comment.
     * 
     * @param object
     * @param parent
     * @param type
     */
    public DBCatalogRepNode(IRepositoryViewObject object, RepositoryNode parent, ENodeType type) {
        super(object, parent, type);
        parentObject = object;
    }

    /*
     * (non-Jsdoc)
     * 
     * @see org.talend.repository.model.RepositoryNode#getChildren()
     */
    @Override
    public List<IRepositoryNode> getChildren() {
        MetadataCatalogRepositoryObject metadataCatalog = (MetadataCatalogRepositoryObject) getObject();
        List<Schema> schemas = CatalogHelper.getSchemas(metadataCatalog.getCatalog());
        if (schemas != null && schemas.size() > 0) {
            return createRepositoryNodeSchema(schemas);
        } else {
            return createTableViewFolder(metadataCatalog);
        }
    }

    /**
     * DOC klliu Comment method "createTableViewFolder".
     * 
     * @param metadataCatalog
     * @return
     */
    private List<IRepositoryNode> createTableViewFolder(MetadataCatalogRepositoryObject metadataCatalog) {
        IRepositoryViewObject viewObject = metadataCatalog.getViewObject();
        List<IRepositoryNode> repsNodes = new ArrayList<IRepositoryNode>();
        // table folder node under catalog
        DBTableFolderRepNode tableFloderNode = new DBTableFolderRepNode(viewObject, this, ENodeType.TDQ_REPOSITORY_ELEMENT);
        repsNodes.add(tableFloderNode);
        // view folder node under catalog
        DBViewFolderRepNode viewFolderNode = new DBViewFolderRepNode(viewObject, this, ENodeType.TDQ_REPOSITORY_ELEMENT);
        repsNodes.add(viewFolderNode);
        return repsNodes;
    }

    /**
     * Create SchemaRepositoryNode under CatalogRepositoryNode.
     * 
     * @param node parent CatalogRepositoryNode
     * @param metadataCatalog parent CatalogViewObject
     * @param schema the schema should to be added under the catalog
     */
    private List<IRepositoryNode> createRepositoryNodeSchema(List<Schema> schemas) {
        List<IRepositoryNode> repsNodes = new ArrayList<IRepositoryNode>();
        for (Schema schema : schemas) {
            MetadataSchemaRepositoryObject metadataSchema = new MetadataSchemaRepositoryObject(
                    ((MetadataCatalogRepositoryObject) getObject()).getViewObject(), schema);
            RepositoryNode schemaNode = new DBSchemaRepNode((IRepositoryViewObject) metadataSchema, this,
                    ENodeType.TDQ_REPOSITORY_ELEMENT);
            schemaNode.setProperties(EProperties.LABEL, ERepositoryObjectType.METADATA_CON_SCHEMA);
            schemaNode.setProperties(EProperties.CONTENT_TYPE, ERepositoryObjectType.METADATA_CON_SCHEMA);
            metadataSchema.setRepositoryNode(schemaNode);
            repsNodes.add(schemaNode);
        }
        return repsNodes;
    }

}
