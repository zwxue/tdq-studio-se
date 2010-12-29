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

import org.eclipse.core.runtime.IPath;
import org.talend.commons.exception.PersistenceException;
import org.talend.core.model.properties.Item;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.repository.model.RepositoryNode;
import org.talend.utils.sugars.ReturnCode;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC klliu  class global comment. Detailled comment
 */
public class TDQJrxmlTemplateReposViewObjDelegator extends AbstractDQRepositoryViewObjectDelegator<ModelElement> {

    private static TDQJrxmlTemplateReposViewObjDelegator instance;

    /*
     * (non-Jsdoc)
     * 
     * @see
     * org.talend.dq.helper.AbstractDQRepositoryViewObjectDelegator#setActiveElement(org.talend.core.model.repository
     * .IRepositoryViewObject, orgomg.cwm.objectmodel.core.ModelElement)
     */
    @Override
    protected void setActiveElement(IRepositoryViewObject viewObject, ModelElement element) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Jsdoc)
     * 
     * @see org.talend.dq.helper.AbstractDQRepositoryViewObjectDelegator#save(org.talend.core.model.properties.Item)
     */
    @Override
    protected ReturnCode save(Item item) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Jsdoc)
     * 
     * @see org.talend.dq.helper.AbstractDQRepositoryViewObjectDelegator#fetchRepositoryViewObjectsLower(boolean)
     */
    @Override
    protected List<IRepositoryViewObject> fetchRepositoryViewObjectsLower(boolean withDelete) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Jsdoc)
     * 
     * @see org.talend.dq.helper.AbstractDQRepositoryViewObjectDelegator#fetchRepositoryViewObjectsLower(boolean,
     * org.talend.core.model.repository.ERepositoryObjectType)
     */
    @Override
    protected List<IRepositoryViewObject> fetchRepositoryViewObjectsLower(boolean withDelete, ERepositoryObjectType type) {
        List<IRepositoryViewObject> jrxmlTemplateList = new ArrayList<IRepositoryViewObject>();
        List<IRepositoryViewObject> patternsItemList = new ArrayList<IRepositoryViewObject>();

        try {
            // Add patterns folder type for check
            List<IRepositoryViewObject> all = ProxyRepositoryFactory.getInstance().getAll(type, withDelete);
            jrxmlTemplateList.addAll(all);
            ProxyRepositoryFactory.getInstance().getJrxmlTemplates(type);
            clear();
            for (IRepositoryViewObject reposViewObj : jrxmlTemplateList) {
                Item item = reposViewObj.getProperty().getItem();
                patternsItemList.add(reposViewObj);
            }
        } catch (PersistenceException e) {
            log.error(e, e);
        }
        return jrxmlTemplateList;
    }

    /**
     * DOC klliu Comment method "getInstance".
     * 
     * @return
     */
    public static TDQJrxmlTemplateReposViewObjDelegator getInstance() {
        if (instance == null) {
            instance = new TDQJrxmlTemplateReposViewObjDelegator();
        }
        return instance;
    }

    /**
     * DOC klliu Comment method "fetchRepositoryNodeByFolder".
     * 
     * @param reload
     * @param itemType
     * @param path
     * @param withDelete
     * @param node
     * @return
     */
    public RepositoryNode fetchRepositoryNodeByFolder(boolean reload, ERepositoryObjectType itemType, IPath path,
            boolean withDelete, RepositoryNode node) {
        // TODO Auto-generated method stub
        return null;
    }

}
