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

import java.util.List;

import org.apache.log4j.Logger;
import org.talend.commons.exception.PersistenceException;
import org.talend.commons.utils.data.container.Container;
import org.talend.commons.utils.data.container.RootContainer;
import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.Folder;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.dq.helper.RepositoryNodeHelper;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.RepositoryNode;

/**
 * DOC klliu class global comment. Detailled comment
 */
public class JrxmlTempFolderRepNode extends DQRepositoryNode {

    private static Logger log = Logger.getLogger(JrxmlTempFolderRepNode.class);

    /**
     * DOC klliu JrxmlTempFolderRepNode constructor comment.
     * 
     * @param object
     * @param parent
     * @param type
     */
    public JrxmlTempFolderRepNode(IRepositoryViewObject object, RepositoryNode parent, ENodeType type) {
        super(object, parent, type);
    }

    @Override
    public List<IRepositoryNode> getChildren() {
        return getChildren(false);
    }

    @Override
    public List<IRepositoryNode> getChildren(boolean withDeleted) {
        try {
            super.getChildren().clear();
            RootContainer<String, IRepositoryViewObject> tdqViewObjects = ProxyRepositoryFactory.getInstance()
                    .getTdqRepositoryViewObjects(getContentType(), RepositoryNodeHelper.getPath(this).toString());

            // sub folders
            for (Container<String, IRepositoryViewObject> container : tdqViewObjects.getSubContainer()) {
                Folder folder = new Folder((Property) container.getProperty(), ERepositoryObjectType.TDQ_JRAXML_ELEMENT);
                if (!withDeleted && folder.isDeleted()) {
                    continue;
                }
                JrxmlTempSubFolderNode childNodeFolder = new JrxmlTempSubFolderNode(folder, this, ENodeType.SIMPLE_FOLDER);
                childNodeFolder.setProperties(EProperties.LABEL, ERepositoryObjectType.TDQ_JRAXML_ELEMENT);
                childNodeFolder.setProperties(EProperties.CONTENT_TYPE, ERepositoryObjectType.TDQ_JRAXML_ELEMENT);
                folder.setRepositoryNode(childNodeFolder);
                super.getChildren().add(childNodeFolder);
            }

            // jrxml templates
            for (IRepositoryViewObject viewObject : tdqViewObjects.getMembers()) {
                if (!withDeleted && viewObject.isDeleted()) {
                    continue;
                }
                JrxmlTempleteRepNode jrxmlNode = new JrxmlTempleteRepNode(viewObject, this, ENodeType.REPOSITORY_ELEMENT);
                jrxmlNode.setProperties(EProperties.LABEL, ERepositoryObjectType.TDQ_JRAXML_ELEMENT);
                jrxmlNode.setProperties(EProperties.CONTENT_TYPE, ERepositoryObjectType.TDQ_JRAXML_ELEMENT);
                viewObject.setRepositoryNode(jrxmlNode);
                // MOD yyi 2011-04-21 19977 duplicate template after replace
                if (!duplicateNode(super.getChildren(), jrxmlNode)) {
                    super.getChildren().add(jrxmlNode);
                }
            }
        } catch (PersistenceException e) {
            log.error(e, e);
        }
        // MOD gdbu 2011-6-29 bug : 22204
        return filterResultsIfAny(super.getChildren());
        // ~22204
    }

    private boolean duplicateNode(List<IRepositoryNode> children, JrxmlTempleteRepNode jrxmlNode) {
        for (IRepositoryNode node : children) {
            if (node.getLabel().equals(jrxmlNode.getLabel())) {
                return true;
            }
        }
        return false;
    }

}
