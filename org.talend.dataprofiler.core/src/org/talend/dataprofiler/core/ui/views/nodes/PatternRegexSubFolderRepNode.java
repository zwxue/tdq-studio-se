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
package org.talend.dataprofiler.core.ui.views.nodes;

import org.apache.log4j.Logger;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.repository.model.RepositoryNode;


/**
 * DOC klliu  class global comment. Detailled comment
 */
public class PatternRegexSubFolderRepNode extends RepositoryNode {

    private static Logger log = Logger.getLogger(PatternRegexSubFolderRepNode.class);
    /**
     * DOC klliu PatternSubFolderRepNode constructor comment.
     * @param object
     * @param parent
     * @param type
     */
    public PatternRegexSubFolderRepNode(IRepositoryViewObject object, RepositoryNode parent, ENodeType type) {
        super(object, parent, type);
    }

}
