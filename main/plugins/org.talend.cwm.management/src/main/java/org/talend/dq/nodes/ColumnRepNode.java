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

import org.talend.core.model.metadata.MetadataColumnRepositoryObject;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.repository.model.RepositoryNode;

/**
 * The parent for the current DBColumnRepNode, and DFColumnRepNode, and other possible column type in the future.
 */
public class ColumnRepNode extends DQRepositoryNode {

    protected MetadataColumnRepositoryObject metadataColumnRepositoryObject;

    /**
     * ColumnRepNode constructor
     * 
     * @param object
     * @param parent
     * @param type
     */
    public ColumnRepNode(IRepositoryViewObject object, RepositoryNode parent, ENodeType type) {
        super(object, parent, type);
    }

    public MetadataColumnRepositoryObject getMetadataColumnRepositoryObject() {
        return this.metadataColumnRepositoryObject;
    }

}
