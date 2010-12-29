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
package org.talend.dq.helper;

import java.util.ArrayList;
import java.util.List;

import org.talend.commons.exception.PersistenceException;
import org.talend.commons.utils.data.container.Container;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.Folder;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.dataquality.domain.pattern.Pattern;
import org.talend.dataquality.properties.TDQPatternItem;
import org.talend.repository.model.IRepositoryNode.ENodeType;
import org.talend.repository.model.RepositoryNode;
import org.talend.utils.sugars.ReturnCode;

/**
 * DOC klliu class global comment. Detailled comment
 */
public class TDQPatternsReposViewObjDelegator extends AbstractDQRepositoryViewObjectDelegator<Pattern> {

    private static TDQPatternsReposViewObjDelegator instance;

    @Override
    protected void setActiveElement(IRepositoryViewObject viewObject, Pattern element) {

    }

    @Override
    protected ReturnCode save(Item item) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<IRepositoryViewObject> fetchRepositoryViewObjectsLower(boolean withDelete) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<IRepositoryViewObject> fetchRepositoryViewObjectsLower(boolean withDelete, ERepositoryObjectType type) {
        List<IRepositoryViewObject> patternsList = new ArrayList<IRepositoryViewObject>();
        List<IRepositoryViewObject> patternsItemList = new ArrayList<IRepositoryViewObject>();

        try {

            List<IRepositoryViewObject> all = ProxyRepositoryFactory.getInstance().getAll(type, withDelete);
            patternsList.addAll(all);
            ProxyRepositoryFactory.getInstance().getPatterns(type);
            clear();
            for (IRepositoryViewObject reposViewObj : patternsList) {
                Item item = reposViewObj.getProperty().getItem();
                ((TDQPatternItem) item).getPattern();
                patternsItemList.add(reposViewObj);
            }
        } catch (PersistenceException e) {
            log.error(e, e);
        }
        return patternsList;
    }

    /**
     * DOC klliu Comment method "getInstance".
     * 
     * @return
     */
    public static TDQPatternsReposViewObjDelegator getInstance() {
        // TODO Auto-generated method stub
        instance = new TDQPatternsReposViewObjDelegator();
        return instance;
    }

    /**
     * DOC klliu Comment method "fetchRepositoryNodeByFolder".
     * 
     * @param patterns
     * @param itemType
     * @param node
     * @return
     */
    public RepositoryNode fetchRepositoryNodeByFolder(Container patterns, ERepositoryObjectType itemType, RepositoryNode node) {
        RepositoryNode parent = node;
        for (Object object : patterns.getSubContainer()) {
            Container container = (Container) object;
            Object property = container.getProperty();
            Folder folder = new Folder(((Property) property), itemType);
            RepositoryNode childNodeFolder = new RepositoryNode(folder, parent, ENodeType.SIMPLE_FOLDER);
            parent.getChildren().add(childNodeFolder);
            fetchRepositoryNodeByFolder(container, itemType, childNodeFolder);
        }
        // not folder or folders have no subFolder
        for (Object obj : patterns.getMembers()) {
            IRepositoryViewObject repositoryObject = (IRepositoryViewObject) obj;
            if (!repositoryObject.isDeleted()) {
                RepositoryNode elementNode = new RepositoryNode(repositoryObject, parent, ENodeType.REPOSITORY_ELEMENT);
                parent.getChildren().add(elementNode);
            }
        }
        return parent;
    }

}
