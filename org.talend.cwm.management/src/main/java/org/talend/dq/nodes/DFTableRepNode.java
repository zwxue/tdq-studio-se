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

import org.talend.core.model.metadata.builder.connection.MetadataTable;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.repository.model.repositoryObject.MetadataTableRepositoryObject;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.RepositoryNode;

/**
 * DOC qiongli class global comment. Detailled comment
 */
public class DFTableRepNode extends DQRepositoryNode {

    private MetadataTableRepositoryObject mdTableRepositoryObject;

    private MetadataTable metadataTable;
    /**
     * DOC qiongli DFRepositoryNode constructor comment.
     * 
     * @param object
     * @param parent
     * @param type
     */
    public DFTableRepNode(IRepositoryViewObject object, RepositoryNode parent, ENodeType type) {
        super(object, parent, type);
        this.mdTableRepositoryObject = (MetadataTableRepositoryObject) object;
        this.metadataTable = this.mdTableRepositoryObject.getTable();
    }

    @Override
    public List<IRepositoryNode> getChildren() {
        // MOD gdbu 2011-7-1 bug : 22204
        if (!super.getChildren().isEmpty()) {
            return filterResultsIfAny(super.getChildren());
        }
        // ~22204
        MetadataTableRepositoryObject viewObject = (MetadataTableRepositoryObject) this.getObject();
        DFColumnFolderRepNode columnFolderNode = new DFColumnFolderRepNode(viewObject, this, ENodeType.TDQ_REPOSITORY_ELEMENT);
        super.getChildren().add(columnFolderNode);
        return super.getChildren();
    }

    public MetadataTable getMetadataTable() {
        return this.metadataTable;
    }

}
