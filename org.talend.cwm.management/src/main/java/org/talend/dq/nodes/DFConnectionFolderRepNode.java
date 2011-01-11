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

import org.apache.log4j.Logger;
import org.talend.commons.exception.PersistenceException;
import org.talend.commons.utils.data.container.Container;
import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.Folder;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.model.repository.RepositoryViewObject;
import org.talend.core.runtime.CoreRuntimePlugin;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.RepositoryNode;


/**
 * DOC qiongli  class global comment. Detailled comment
 */
public class DFConnectionFolderRepNode extends RepositoryNode {

    private static Logger log = Logger.getLogger(DFConnectionFolderRepNode.class);
    /**
     * DOC qiongli FileDelimitedFolderRepNode constructor comment.
     * 
     * @param object
     * @param parent
     * @param type
     */
    public DFConnectionFolderRepNode(IRepositoryViewObject object, RepositoryNode parent, ENodeType type) {
        super(object, parent, type);
        // TODO Auto-generated constructor stub
    }

    @Override
    public List<IRepositoryNode> getChildren() {
        RepositoryNode fetchNodeByFolder = new RepositoryNode(this.getObject(), this.getParent(), this.getType());
        ERepositoryObjectType contentType = this.getContentType();
        Container<String, IRepositoryViewObject> container = null;
        try {
            container = CoreRuntimePlugin.getInstance().getProxyRepositoryFactory().getMetadataFileDelimited();
            fetchRepositoryNodeByFolder(container, contentType, fetchNodeByFolder);
        } catch (PersistenceException e) {
            log.error(e, e);
        }

        return fetchNodeByFolder.getChildren();
    }

    public RepositoryNode fetchRepositoryNodeByFolder(Container<String, IRepositoryViewObject> patterns,
            ERepositoryObjectType parentItemType, RepositoryNode node) {
        RepositoryNode parent = node;
        for (Container<String, IRepositoryViewObject> container : patterns.getSubContainer()) {

            Property property = (Property) container.getProperty();
            ERepositoryObjectType itemType = ERepositoryObjectType.getTypeFromKey(property.getLabel());

            if (itemType == null) {
                itemType = parentItemType;
            }
            Folder folder = new Folder(((Property) property), itemType);
            DFConnectionSubFolderRepNode childNodeFolder = new DFConnectionSubFolderRepNode(folder, parent,
                    ENodeType.SIMPLE_FOLDER);
            parent.getChildren().add(childNodeFolder);
            fetchRepositoryNodeByFolder(container, itemType, childNodeFolder);
        }
        // not folder or folders have no subFolder
        for (Object obj : patterns.getMembers()) {
            RepositoryViewObject viewObject = new RepositoryViewObject(((IRepositoryViewObject) obj).getProperty());
            if (!viewObject.isDeleted()) {
                DFConnectionRepNode repNode = new DFConnectionRepNode(viewObject, node, ENodeType.REPOSITORY_ELEMENT);
                viewObject.setRepositoryNode(repNode);
                parent.getChildren().add(repNode);

            }
        }
        return parent;
    }


}
