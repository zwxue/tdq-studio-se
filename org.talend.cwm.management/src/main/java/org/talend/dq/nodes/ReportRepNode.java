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

import java.util.List;

import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.RepositoryNode;

/**
 * DOC klliu class global comment. Detailled comment
 */
public class ReportRepNode extends RepositoryNode {

    /**
     * DOC klliu ReportRepNode constructor comment.
     * 
     * @param object
     * @param parent
     * @param type
     */
    public ReportRepNode(IRepositoryViewObject object, RepositoryNode parent, ENodeType type) {
        super(object, parent, type);
        // TODO Auto-generated constructor stub
    }

    @Override
    public List<IRepositoryNode> getChildren() {
        // RepositoryNode parent = node;
        // for (Object object : patterns.getSubContainer()) {
        // Container container = (Container) object;
        // Object property = container.getProperty();
        // Folder folder = new Folder(((Property) property), itemType);
        // RepositoryNode childNodeFolder = new RepositoryNode(folder, parent, ENodeType.SIMPLE_FOLDER);
        // parent.getChildren().add(childNodeFolder);
        // fetchRepositoryNodeByFolder(container, itemType, childNodeFolder);
        // }
        // // not folder or folders have no subFolder
        // for (Object obj : patterns.getMembers()) {
        // RepositoryViewObject viewObject = new RepositoryViewObject(((IRepositoryViewObject) obj).getProperty());
        // if (!viewObject.isDeleted()) {
        // if (node instanceof ReportFolderRepNode) {
        // ReportRepNode repNode = new ReportRepNode(viewObject, node, ENodeType.REPOSITORY_ELEMENT);
        // viewObject.setRepositoryNode(repNode);
        // parent.getChildren().add(repNode);
        //
        // } else {
        // RepositoryNode elementNode = new RepositoryNode(viewObject, parent, ENodeType.REPOSITORY_ELEMENT);
        // parent.getChildren().add(elementNode);
        // }
        // }
        // }
        // return parent;
        return super.getChildren();
    }
}
