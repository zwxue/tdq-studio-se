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

import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.RepositoryNode;

/**
 * DOC klliu class global comment. Detailled comment
 */
public class ReportRepNode extends RepositoryNode {

    public static String anaFloder = "Analyzes";

    public static String genFloder = "Generated Documents";

    /**
     * DOC klliu ReportRepNode constructor comment.
     * 
     * @param object
     * @param parent
     * @param type
     */
    public ReportRepNode(IRepositoryViewObject object, RepositoryNode parent, ENodeType type) {
        super(object, parent, type);
    }

    @Override
    public List<IRepositoryNode> getChildren() {
        List<IRepositoryNode> anaElement = new ArrayList<IRepositoryNode>();
        ReportSubFolderRepNode anaNodeFolder = new ReportSubFolderRepNode(null, this, ENodeType.SIMPLE_FOLDER);
        anaNodeFolder.setProperties(EProperties.CONTENT_TYPE, ERepositoryObjectType.TDQ_REPORTS);
        anaNodeFolder.setProperties(EProperties.LABEL, anaFloder);
        anaElement.add(anaNodeFolder);
        ReportSubFolderRepNode grenNodeFolder = new ReportSubFolderRepNode(null, this, ENodeType.SIMPLE_FOLDER);
        grenNodeFolder.setProperties(EProperties.CONTENT_TYPE, ERepositoryObjectType.TDQ_REPORTS);
        grenNodeFolder.setProperties(EProperties.LABEL, genFloder);
        anaElement.add(grenNodeFolder);
        return anaElement;
        // return super.getChildren();
    }
}
