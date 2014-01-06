// ============================================================================
//
// Copyright (C) 2006-2013 Talend Inc. - www.talend.com
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
import org.talend.repository.model.RepositoryNode;

/**
 * The parent for: DBTableRepNode,DFTableRepNode,DBViewRepNode,and other table/view level node in the future. Which can provide a easy way to judge them , and handle
 * them together.
 * 
 */
public class ColumnSetRepNode extends DQRepositoryNode {

    /**
     * ColumnSetRepNode constructor.
     * 
     * @param object
     * @param parent
     * @param type
     */
    public ColumnSetRepNode(IRepositoryViewObject object, RepositoryNode parent, ENodeType type) {
        super(object, parent, type);
    }

}
