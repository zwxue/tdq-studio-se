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
package org.talend.dq.nodes;

import java.util.ArrayList;
import java.util.List;

import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.RepositoryNode;

/**
 * DOC klliu Database view repository node displayed on repository view (UI).
 */
public class DBViewRepNode extends RepositoryNode {

    /**
     * DOC klliu DBViewRepNode constructor comment.
     * 
     * @param object
     * @param parent
     * @param type
     */
    public DBViewRepNode(IRepositoryViewObject object, RepositoryNode parent, ENodeType type) {
        super(object, parent, type);
    }

    /*
     * (non-Jsdoc)
     * 
     * @see org.talend.repository.model.RepositoryNode#getChildren()
     */
    @Override
    public List<IRepositoryNode> getChildren() {
        List<IRepositoryNode> nodes = new ArrayList<IRepositoryNode>();
        DBColumnFolderRepNode columnFolderNode = new DBColumnFolderRepNode(getObject(), this, ENodeType.TDQ_REPOSITORY_ELEMENT);
        nodes.add(columnFolderNode);
        return nodes;
    }
}
