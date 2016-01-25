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

import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.repository.model.repositoryObject.MetadataXmlElementTypeRepositoryObject;
import org.talend.cwm.db.connection.ConnectionUtils;
import org.talend.cwm.xml.TdXmlElementType;
import org.talend.dataquality.PluginConstant;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.RepositoryNode;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC klliu class global comment. Detailled comment
 */
public class MDMXmlElementRepNode extends DQRepositoryNode {

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
        // MOD gdbu 2011-7-1 bug : 22204
        List<IRepositoryNode> childrenNode = new ArrayList<IRepositoryNode>();
        IRepositoryViewObject object = this.getObject();
        TdXmlElementType xmlElementType = ((MetadataXmlElementTypeRepositoryObject) object).getTdXmlElementType();
        List<TdXmlElementType> xmlElements = null;
        if (xmlElementType != null) {
            xmlElements = ConnectionUtils.getXMLElements(xmlElementType);
        }

        // FIXME xmlElements might be null.
        if (xmlElements == null || xmlElements.size() < 1) {
            return new ArrayList<IRepositoryNode>();
        } else { // xmlElements.size() > 0
            for (ModelElement mElement : xmlElements) {
                MetadataXmlElementTypeRepositoryObject metadataXmlElementType = new MetadataXmlElementTypeRepositoryObject(
                        this.getObject(), (TdXmlElementType) mElement);
                RepositoryNode xmlElementTypeNode = new MDMXmlElementRepNode(metadataXmlElementType, this,
                        ENodeType.TDQ_REPOSITORY_ELEMENT);
                xmlElementTypeNode.setProperties(EProperties.LABEL, ERepositoryObjectType.MDM_ELEMENT_TYPE);
                xmlElementTypeNode.setProperties(EProperties.CONTENT_TYPE, ERepositoryObjectType.METADATA_CON_CATALOG);
                metadataXmlElementType.setRepositoryNode(xmlElementTypeNode);
                // MOD gdbu 2011-7-1 bug : 22204
                childrenNode.add(xmlElementTypeNode);
            }
            return filterResultsIfAny(childrenNode);
            // ~22204
        }
    }

    @Override
    public boolean hasChildren() {
        IRepositoryViewObject object = this.getObject();
        TdXmlElementType xmlElementType = ((MetadataXmlElementTypeRepositoryObject) object).getTdXmlElementType();
        if (xmlElementType != null) {
            return ConnectionUtils.getXMLElementsWithOutSave(xmlElementType).size() > 0;
        }
        return false;
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
        return PluginConstant.EMPTY_STRING;

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.repository.model.RepositoryNode#getDisplayText()
     */
    @Override
    public String getDisplayText() {
        String nodeDataType = getNodeDataType();
        if (!PluginConstant.EMPTY_STRING.equals(nodeDataType)) {
            return getTdXmlElementType().getName() + "(" + nodeDataType + ")"; //$NON-NLS-1$ //$NON-NLS-2$
        }
        return getTdXmlElementType().getName();
    }
}
