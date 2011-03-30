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

import java.util.List;

import org.talend.core.model.metadata.MetadataColumnRepositoryObject;
import org.talend.core.model.metadata.builder.connection.MetadataColumn;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.RepositoryNode;
import org.talend.utils.sql.TalendTypeConvert;

/**
 * DOC qiongli class global comment. Detailled comment
 */
public class DFColumnRepNode extends RepositoryNode {

    private MetadataColumnRepositoryObject metadataColumnRepositoryObject;

    private MetadataColumn metadataColumn;
    /**
     * DOC qiongli DFColumnRepNode constructor comment.
     * 
     * @param object
     * @param parent
     * @param type
     */
    public DFColumnRepNode(IRepositoryViewObject object, RepositoryNode parent, ENodeType type) {
        super(object, parent, type);
        metadataColumnRepositoryObject = (MetadataColumnRepositoryObject) object;
        metadataColumn = (MetadataColumn) metadataColumnRepositoryObject.getTdColumn();
    }

    @Override
    public List<IRepositoryNode> getChildren() {
        return super.getChildren();
    }

    public MetadataColumn getMetadataColumn() {
        return this.metadataColumn;
    }

    @Override
    public String getLabel() {
        if (this.getMetadataColumn() != null) {
            return this.getMetadataColumn().getName();
        }
        return super.getLabel();
    }

    public String getNodeDataType() {
        String convertToJavaType = TalendTypeConvert.convertToJavaType(this.getMetadataColumn().getTalendType());
        return convertToJavaType;

    }
}
