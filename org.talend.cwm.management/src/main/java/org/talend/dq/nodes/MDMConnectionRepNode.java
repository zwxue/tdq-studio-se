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

import org.eclipse.emf.common.util.EList;
import org.talend.core.model.metadata.builder.connection.MDMConnection;
import org.talend.core.model.properties.ConnectionItem;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.MDMConnectionItem;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.repository.model.repositoryObject.MetadataXmlSchemaRepositoryObject;
import org.talend.cwm.xml.impl.TdXmlSchemaImpl;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.RepositoryNode;
import orgomg.cwm.objectmodel.core.Package;

/**
 * DOC klliu class global comment. Detailled comment
 */
public class MDMConnectionRepNode extends ConnectionRepNode {

    private MDMConnection mdmConnection;

    public MDMConnection getMdmConnection() {
        return this.mdmConnection;
    }

    /**
     * DOC klliu MDMConnectionRepNode constructor comment.
     * 
     * @param object
     * @param parent
     * @param type
     */
    public MDMConnectionRepNode(IRepositoryViewObject object, RepositoryNode parent, ENodeType type) {
        super(object, parent, type);
        if (object != null && object.getProperty() != null) {
            Item item = object.getProperty().getItem();
            if (item != null && item instanceof MDMConnectionItem) {
                this.mdmConnection = (MDMConnection) ((MDMConnectionItem) item).getConnection();
            }
        }
    }

    @Override
    public List<IRepositoryNode> getChildren() {
        // Retrieve catalogs/schemes.
        EList<Package> dataPackage = ((ConnectionItem) getObject().getProperty().getItem()).getConnection().getDataPackage();
        if (dataPackage != null && dataPackage.size() > 0) {
            Package pack = dataPackage.get(0);
            if (pack instanceof TdXmlSchemaImpl) {
                return createRepositoryNodeSchema(dataPackage);
            }
        }
        return new ArrayList<IRepositoryNode>();

    }

    private List<IRepositoryNode> createRepositoryNodeSchema(EList<Package> dataPackage) {
        IRepositoryViewObject object = this.getObject();
        List<IRepositoryNode> nodes = new ArrayList<IRepositoryNode>();
        for (Package pack : dataPackage) {
            MetadataXmlSchemaRepositoryObject metadataXmlSchema = new MetadataXmlSchemaRepositoryObject(object,
                    (TdXmlSchemaImpl) pack);
            MDMSchemaRepNode xmlSchemaNode = new MDMSchemaRepNode(metadataXmlSchema, this,
                    ENodeType.TDQ_REPOSITORY_ELEMENT);
            xmlSchemaNode.setProperties(EProperties.LABEL, ERepositoryObjectType.MDM_SCHEMA);
            xmlSchemaNode.setProperties(EProperties.CONTENT_TYPE, ERepositoryObjectType.METADATA_CON_CATALOG);
            metadataXmlSchema.setRepositoryNode(xmlSchemaNode);
            nodes.add(xmlSchemaNode);
        }
        return nodes;
    }

    @Override
    public String getLabel() {
        if (this.getMdmConnection() != null) {
            return this.getMdmConnection().getName();
        }
        return super.getLabel();
    }

}
