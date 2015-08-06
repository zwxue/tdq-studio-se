// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
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
import org.talend.core.repository.model.repositoryObject.MetadataXmlElementTypeRepositoryObject;
import org.talend.core.repository.model.repositoryObject.MetadataXmlSchemaRepositoryObject;
import org.talend.cwm.db.connection.ConnectionUtils;
import org.talend.cwm.xml.TdXmlElementType;
import org.talend.cwm.xml.TdXmlSchema;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.RepositoryNode;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC klliu class global comment. Detailled comment
 */
public class MDMSchemaRepNode extends DQRepositoryNode {

    private MetadataXmlSchemaRepositoryObject metadataXmlSchemaRepositoryObject;

    private TdXmlSchema tdXmlSchema;

    public MetadataXmlSchemaRepositoryObject getMetadataXmlSchemaRepositoryObject() {
        return this.metadataXmlSchemaRepositoryObject;
    }

    public TdXmlSchema getTdXmlSchema() {
        return this.tdXmlSchema;
    }

    /**
     * DOC klliu MDMSchemaRepNode constructor comment.
     * 
     * @param object
     * @param parent
     * @param type
     */
    public MDMSchemaRepNode(IRepositoryViewObject object, RepositoryNode parent, ENodeType type) {
        super(object, parent, type);
        if (object instanceof MetadataXmlSchemaRepositoryObject) {
            this.metadataXmlSchemaRepositoryObject = (MetadataXmlSchemaRepositoryObject) object;
            this.tdXmlSchema = this.metadataXmlSchemaRepositoryObject.getTdXmlSchema();
        }
    }

    @Override
    public List<IRepositoryNode> getChildren() {
        // MOD gdbu 2011-7-1 bug : 22204

        List<IRepositoryNode> children = new ArrayList<IRepositoryNode>();

        MetadataXmlSchemaRepositoryObject metadataXmlSchema = (MetadataXmlSchemaRepositoryObject) this.getObject();

        TdXmlSchema xmlSchema = metadataXmlSchema.getTdXmlSchema();
        List<ModelElement> xmlElements = ConnectionUtils.getXMLElements(xmlSchema);
        for (ModelElement mElement : xmlElements) {
            if (mElement instanceof TdXmlElementType) {
                MetadataXmlElementTypeRepositoryObject metadataXmlElementType = new MetadataXmlElementTypeRepositoryObject(
                        metadataXmlSchema.getViewObject(), (TdXmlElementType) mElement);
                metadataXmlSchema.getChildren().add((IRepositoryViewObject) metadataXmlElementType);
                MDMXmlElementRepNode xmlElementTypeNode = new MDMXmlElementRepNode(
                        (IRepositoryViewObject) metadataXmlElementType, this, ENodeType.TDQ_REPOSITORY_ELEMENT);
                xmlElementTypeNode.setProperties(EProperties.LABEL, ERepositoryObjectType.MDM_ELEMENT_TYPE);
                xmlElementTypeNode.setProperties(EProperties.CONTENT_TYPE, ERepositoryObjectType.METADATA_CON_CATALOG);
                metadataXmlElementType.setRepositoryNode(xmlElementTypeNode);
                children.add(xmlElementTypeNode);
            }
        }
        return filterResultsIfAny(children);
        // ~22204
    }

    @Override
    public boolean hasChildren() {
        MetadataXmlSchemaRepositoryObject metadataXmlSchema = (MetadataXmlSchemaRepositoryObject) this.getObject();
        TdXmlSchema xmlSchema = metadataXmlSchema.getTdXmlSchema();
        return ConnectionUtils.getXMLElementsWithOutSave(xmlSchema).size() > 0;
    }

    @Override
    public String getLabel() {
        if (this.getTdXmlSchema() != null) {
            return this.getTdXmlSchema().getName();
        }
        return super.getLabel();
    }

}
