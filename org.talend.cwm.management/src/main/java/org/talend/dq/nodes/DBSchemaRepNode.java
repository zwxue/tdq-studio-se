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
import org.talend.core.repository.model.repositoryObject.MetadataSchemaRepositoryObject;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.RepositoryNode;

/**
 * DOC klliu Database schema repository node displayed on repository view (UI).
 */
public class DBSchemaRepNode extends RepositoryNode {

    /**
     * DOC klliu DBSchemaRepNode constructor comment.
     * 
     * @param object
     * @param parent
     * @param type
     */
    public DBSchemaRepNode(IRepositoryViewObject object, RepositoryNode parent, ENodeType type) {
        super(object, parent, type);
        // TODO Auto-generated constructor stub
    }

    @Override
    public List<IRepositoryNode> getChildren() {
        // TODO Auto-generated method stub
        IRepositoryViewObject object = getObject();
        return createTableViewFolder((MetadataSchemaRepositoryObject) object);
    }

    private List<IRepositoryNode> createTableViewFolder(MetadataSchemaRepositoryObject metadataSchema) {
        IRepositoryViewObject viewObject = metadataSchema.getViewObject();
        List<IRepositoryNode> repsNodes = new ArrayList<IRepositoryNode>();
        // table folder node under catalog
        DBTableFolderRepNode tableFloderNode = new DBTableFolderRepNode(viewObject, this, ENodeType.TDQ_REPOSITORY_ELEMENT);
        repsNodes.add(tableFloderNode);
        // view folder node under catalog
        DBViewFolderRepNode viewFolderNode = new DBViewFolderRepNode(viewObject, this, ENodeType.TDQ_REPOSITORY_ELEMENT);
        repsNodes.add(viewFolderNode);
        return repsNodes;
    }

}
