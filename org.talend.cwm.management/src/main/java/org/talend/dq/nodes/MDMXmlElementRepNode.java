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
import org.talend.core.repository.model.repositoryObject.MetadataXmlElementTypeRepositoryObject;
import org.talend.cwm.db.connection.ConnectionUtils;
import org.talend.cwm.xml.TdXmlElementType;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.RepositoryNode;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC klliu class global comment. Detailled comment
 */
public class MDMXmlElementRepNode extends RepositoryNode {

    private MetadataXmlElementTypeRepositoryObject metadataXmlElementTypeRepositoryObject;

    private TdXmlElementType tdXmlElementType;

    public MetadataXmlElementTypeRepositoryObject getMetadataXmlElementTypeRepositoryObject() {
        return this.metadataXmlElementTypeRepositoryObject;
    }

    public TdXmlElementType getTdXmlElementType() {
        return this.tdXmlElementType;
    }

    /**
     * DOC klliu MDMXmlElementRepNode constructor comment.
     * 
     * @param object
     * @param parent
     * @param type
     */
    public MDMXmlElementRepNode(IRepositoryViewObject object, RepositoryNode parent, ENodeType type) {
        super(object, parent, type);
        if (object instanceof MetadataXmlElementTypeRepositoryObject) {
            this.metadataXmlElementTypeRepositoryObject = (MetadataXmlElementTypeRepositoryObject) object;
            this.tdXmlElementType = this.metadataXmlElementTypeRepositoryObject.getTdXmlElementType();
        }
    }

    @Override
    public List<IRepositoryNode> getChildren() {
        IRepositoryViewObject object = this.getObject();
        List<IRepositoryNode> repsNodes = new ArrayList<IRepositoryNode>();
        TdXmlElementType xmlElementType = ((MetadataXmlElementTypeRepositoryObject) object).getTdXmlElementType();
        List<TdXmlElementType> xmlElements = null;
        if (xmlElementType != null) {

            xmlElements = ConnectionUtils.getXMLElements(xmlElementType);
        }
        if (xmlElements.size() > 0) {
            for (ModelElement mElement : xmlElements) {
                MetadataXmlElementTypeRepositoryObject metadataXmlElementType = new MetadataXmlElementTypeRepositoryObject(
                        this.getObject(), (TdXmlElementType) mElement);
                RepositoryNode xmlElementTypeNode = new MDMXmlElementRepNode((IRepositoryViewObject) metadataXmlElementType,
                        this, ENodeType.TDQ_REPOSITORY_ELEMENT);
                xmlElementTypeNode.setProperties(EProperties.LABEL, ERepositoryObjectType.MDM_ELEMENT_TYPE);
                xmlElementTypeNode.setProperties(EProperties.CONTENT_TYPE, ERepositoryObjectType.METADATA_CON_CATALOG);
                metadataXmlElementType.setRepositoryNode(xmlElementTypeNode);
                repsNodes.add(xmlElementTypeNode);
            }
            return repsNodes;
        } else {
            return new ArrayList<IRepositoryNode>();
        }
    }

    @Override
    public boolean hasChildren() {
        return getChildren().size() > 0;
    }

    @Override
    public String getLabel() {
        if (this.getTdXmlElementType() != null) {
            return this.getTdXmlElementType().getName();
        }
        return super.getLabel();
    }

    public String getNodeDataType() {
        if (!hasChildren()) {
            return tdXmlElementType.getJavaType();
        }
        return "";

    }
}
