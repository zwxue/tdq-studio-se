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

import java.util.List;

import org.apache.log4j.Logger;
import org.talend.commons.exception.PersistenceException;
import org.talend.commons.utils.data.container.Container;
import org.talend.commons.utils.data.container.RootContainer;
import org.talend.commons.utils.platform.PluginChecker;
import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.Folder;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.dq.helper.RepositoryNodeHelper;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.RepositoryNode;

/**
 * DOC gdbu class global comment. Detailled comment
 */
public class RulesFolderRepNode extends DQRepositoryNode {

    private static Logger log = Logger.getLogger(RulesFolderRepNode.class);

    /**
     * DOC gdbu RulesFolderRepNode constructor comment.
     * 
     * @param object
     * @param parent
     * @param type
     */
    public RulesFolderRepNode(IRepositoryViewObject object, RepositoryNode parent, ENodeType type) {
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
            // create child node for folder of rules
            createRulesChildFolderNode(withDeleted, tdqViewObjects);

        } catch (PersistenceException e) {
            log.error(e, e);
        }
        // MOD gdbu 2011-6-29 bug : 22204
        return filterResultsIfAny(super.getChildren());
        // ~22204
    }

    private void createRulesChildFolderNode(boolean withDeleted, RootContainer<String, IRepositoryViewObject> tdqViewObjects) {
        for (Container<String, IRepositoryViewObject> container : tdqViewObjects.getSubContainer()) {
            Folder folder = null;
            boolean isSql = container.getLabel().equals("SQL"); //$NON-NLS-1$
            boolean isParser = container.getLabel().equals("Parser"); //$NON-NLS-1$
            if (isSql) {
                folder = new Folder((Property) container.getProperty(), ERepositoryObjectType.TDQ_RULES_SQL);
                if (!withDeleted && folder.isDeleted()) {
                    continue;
                }
                RulesSQLFolderRepNode systemIndicatorFolderNode = new RulesSQLFolderRepNode(folder, this, ENodeType.SYSTEM_FOLDER);
                folder.setRepositoryNode(systemIndicatorFolderNode);
                systemIndicatorFolderNode.setProperties(EProperties.CONTENT_TYPE, ERepositoryObjectType.TDQ_RULES_SQL);
                systemIndicatorFolderNode.setProperties(EProperties.LABEL, ERepositoryObjectType.TDQ_RULES_SQL);
                super.getChildren().add(systemIndicatorFolderNode);

            }
            if (isParser && PluginChecker.isTDQLoaded()) {
                folder = new Folder((Property) container.getProperty(), ERepositoryObjectType.TDQ_RULES_PARSER);
                if (!withDeleted && folder.isDeleted()) {
                    continue;
                }
                RulesParserFolderRepNode systemIndicatorFolderNode = new RulesParserFolderRepNode(folder, this,
                        ENodeType.SYSTEM_FOLDER);
                folder.setRepositoryNode(systemIndicatorFolderNode);
                systemIndicatorFolderNode.setProperties(EProperties.CONTENT_TYPE, ERepositoryObjectType.TDQ_RULES_PARSER);
                systemIndicatorFolderNode.setProperties(EProperties.LABEL, ERepositoryObjectType.TDQ_RULES_PARSER);
                super.getChildren().add(systemIndicatorFolderNode);
            }

        }
    }

}
