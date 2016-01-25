// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
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
public class AnalysisFolderRepNode extends DQRepositoryNode {

    private static Logger log = Logger.getLogger(AnalysisFolderRepNode.class);

    /**
     * DOC klliu AnalysisFolderRepNode constructor comment.
     * 
     * @param object
     * @param parent
     * @param type
     * @throws PersistenceException
     */
    public AnalysisFolderRepNode(IRepositoryViewObject object, RepositoryNode parent, ENodeType type) {
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
                Folder folder = new Folder((Property) container.getProperty(), ERepositoryObjectType.TDQ_ANALYSIS_ELEMENT);
                // MOD qiongli 2011-1-20.
                if (!withDeleted && folder.isDeleted()) {
                    continue;
                }
                AnalysisSubFolderRepNode childNodeFolder = new AnalysisSubFolderRepNode(folder, this, ENodeType.SIMPLE_FOLDER);
                childNodeFolder.setProperties(EProperties.LABEL, ERepositoryObjectType.TDQ_ANALYSIS_ELEMENT);
                childNodeFolder.setProperties(EProperties.CONTENT_TYPE, ERepositoryObjectType.TDQ_ANALYSIS_ELEMENT);
                folder.setRepositoryNode(childNodeFolder);
                super.getChildren().add(childNodeFolder);
            }

            // ana files
            for (IRepositoryViewObject viewObject : tdqViewObjects.getMembers()) {
                if (!withDeleted && viewObject.isDeleted()) {
                    continue;
                }
                AnalysisRepNode anaNode = new AnalysisRepNode(viewObject, this, ENodeType.REPOSITORY_ELEMENT);
                anaNode.setProperties(EProperties.LABEL, ERepositoryObjectType.TDQ_ANALYSIS_ELEMENT);
                anaNode.setProperties(EProperties.CONTENT_TYPE, ERepositoryObjectType.TDQ_ANALYSIS_ELEMENT);
                viewObject.setRepositoryNode(anaNode);
                super.getChildren().add(anaNode);
            }
        } catch (PersistenceException e) {
            log.error(e, e);
        }
        // MOD gdbu 2011-7-1 bug : 22204
        return filterResultsIfAny(super.getChildren());
        // ~22204
    }

    public String getLabelWithCount() {
        String label = "Analysis"; //$NON-NLS-1$
        if (this.getObject() != null) {
            label = this.getObject().getLabel();
        } else {
            label = super.getLabel();
        }
        return decorateCount(label);
    }

    private String decorateCount(String label) {
        return label + " (" + getChildrenAll(false).size() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * get all children under the folder.
     * 
     * @param withDelete include deleted ones
     * @return
     */
    public List<IRepositoryNode> getChildrenAll(boolean withDelete) {
        List<IRepositoryNode> nodes = new ArrayList<IRepositoryNode>();
        try {
            RootContainer<String, IRepositoryViewObject> tdqViewObjects = ProxyRepositoryFactory.getInstance()
                    .getTdqRepositoryViewObjects(getContentType(), RepositoryNodeHelper.getPath(this).toString());

            // sub folders
            for (Container<String, IRepositoryViewObject> container : tdqViewObjects.getSubContainer()) {
                Folder folder = new Folder((Property) container.getProperty(), ERepositoryObjectType.TDQ_ANALYSIS_ELEMENT);
                if (!withDelete && folder.isDeleted()) {
                    continue;
                }
                AnalysisSubFolderRepNode childNodeFolder = new AnalysisSubFolderRepNode(folder, this, ENodeType.SIMPLE_FOLDER);
                childNodeFolder.setProperties(EProperties.LABEL, ERepositoryObjectType.TDQ_ANALYSIS_ELEMENT);
                childNodeFolder.setProperties(EProperties.CONTENT_TYPE, ERepositoryObjectType.TDQ_ANALYSIS_ELEMENT);
                folder.setRepositoryNode(childNodeFolder);
                nodes.addAll(childNodeFolder.getChildrenAll(withDelete));
            }

            // ana files
            for (IRepositoryViewObject viewObject : tdqViewObjects.getMembers()) {
                if (!withDelete && viewObject.isDeleted()) {
                    continue;
                }
                AnalysisRepNode anaNode = new AnalysisRepNode(viewObject, this, ENodeType.REPOSITORY_ELEMENT);
                anaNode.setProperties(EProperties.LABEL, ERepositoryObjectType.TDQ_ANALYSIS_ELEMENT);
                anaNode.setProperties(EProperties.CONTENT_TYPE, ERepositoryObjectType.TDQ_ANALYSIS_ELEMENT);
                viewObject.setRepositoryNode(anaNode);
                nodes.add(anaNode);
            }
        } catch (PersistenceException e) {
            log.error(e, e);
        }
        return nodes;
    }

    public boolean isVirtualFolder() {
        return false;
    }
}
