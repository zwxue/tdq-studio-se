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

import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.repository.model.repositoryObject.MetadataCatalogRepositoryObject;
import org.talend.core.repository.model.repositoryObject.MetadataSchemaRepositoryObject;
import org.talend.cwm.helper.CatalogHelper;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.RepositoryNode;
import orgomg.cwm.resource.relational.Catalog;
import orgomg.cwm.resource.relational.Schema;

/**
 * DOC klliu Database catalog repository node displayed on repository view (UI).
 */
public class DBCatalogRepNode extends DQRepositoryNode {

    private IRepositoryViewObject object;

    private List<IRepositoryNode> schemaChildren;

    private Catalog catalog;

    public Catalog getCatalog() {
        return this.catalog;
    }

    /**
     * DOC klliu DBCatalogRepNode constructor comment.
     * 
     * @param viewObject
     * @param parent
     * @param type
     */
    public DBCatalogRepNode(IRepositoryViewObject viewObject, RepositoryNode parent, ENodeType type) {
        super(viewObject, parent, type);
        object = viewObject;
        schemaChildren = new ArrayList<IRepositoryNode>();
        if (object instanceof MetadataCatalogRepositoryObject) {
            MetadataCatalogRepositoryObject metadataCatalogObject = (MetadataCatalogRepositoryObject) object;
            this.catalog = metadataCatalogObject.getCatalog();
        }
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
        // MOD gdbu 2011-7-1 bug : 22204
        if (schemas != null && schemas.size() > 0) {
            return filterResultsIfAny(createRepositoryNodeSchema(schemas));
        } else {
            // feature 22206 2011-7-12 msjian: fixed its note 91852 issue2
            if (DQRepositoryNode.isUntilSchema()) {
                return createTableViewFolder(metadataCatalog);
            }
            return filterResultsIfAny(createTableViewFolder(metadataCatalog));
        }
        // ~22204
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
        if (!schemaChildren.isEmpty()) {
            return schemaChildren;
        }
        for (Schema schema : schemas) {
            MetadataSchemaRepositoryObject metadataSchema = new MetadataSchemaRepositoryObject(
                    ((MetadataCatalogRepositoryObject) getObject()).getViewObject(), schema);
            RepositoryNode schemaNode = new DBSchemaRepNode((IRepositoryViewObject) metadataSchema, this,
                    ENodeType.TDQ_REPOSITORY_ELEMENT);
            schemaNode.setProperties(EProperties.LABEL, ERepositoryObjectType.METADATA_CON_SCHEMA);
            schemaNode.setProperties(EProperties.CONTENT_TYPE, ERepositoryObjectType.METADATA_CON_SCHEMA);
            metadataSchema.setRepositoryNode(schemaNode);
            schemaChildren.add(schemaNode);
        }
        return schemaChildren;
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
        return this.getObject().getLabel();
    }

}
