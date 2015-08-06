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

import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.repository.model.ISubRepositoryObject;
import org.talend.repository.model.RepositoryNode;

/**
 * created by xqliu on Apr 15, 2013 Detailled comment
 * 
 */
public class DQDBFolderRepositoryNode extends DQRepositoryNode {

    /**
     * DOC xqliu DQConnectionRepositoryNode constructor comment.
     * 
     * @param object
     * @param parent
     * @param type
     */
    public DQDBFolderRepositoryNode(IRepositoryViewObject object, RepositoryNode parent, ENodeType type) {
        super(object, parent, type);
    }

    /**
     * call getProperty() to reload the connection if it has been unloaded.
     * 
     * @param connectionViewObject
     */
    protected void reloadConnectionViewObject() {
        IRepositoryViewObject subRepositoryObject = this.getObject() == null ? this.getParent().getObject() : this.getObject();
        if (subRepositoryObject != null && subRepositoryObject instanceof ISubRepositoryObject) {
            // call getProperty() to reload the connection if it has been unloaded
            ((ISubRepositoryObject) subRepositoryObject).getProperty();
        }
    }
}
