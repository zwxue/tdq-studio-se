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
import org.talend.dataquality.properties.TDQBusinessRuleItem;
import org.talend.dataquality.rules.DQRule;
import org.talend.repository.model.RepositoryNode;
import org.talend.utils.sugars.ReturnCode;


/**
 * DOC klliu  class global comment. Detailled comment
 */
public class TDQRulessReposViewObjDelegator extends AbstractDQRepositoryViewObjectDelegator<DQRule> {

    private static TDQRulessReposViewObjDelegator instance;


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
            // Add patterns folder type for check
            List<IRepositoryViewObject> all = ProxyRepositoryFactory.getInstance().getAll(type, withDelete);
            patternsList.addAll(all);
            ProxyRepositoryFactory.getInstance().getRules(type);
            clear();
            for (IRepositoryViewObject reposViewObj : patternsList) {
                Item item = reposViewObj.getProperty().getItem();
                DQRule rule = ((TDQBusinessRuleItem) item).getDqrule();
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
    public static TDQRulessReposViewObjDelegator getInstance() {
        // TODO Auto-generated method stub
        instance = new TDQRulessReposViewObjDelegator();
        return instance;
    }

    /* (non-Jsdoc)
     * @see org.talend.dq.helper.AbstractDQRepositoryViewObjectDelegator#setActiveElement(org.talend.core.model.repository.IRepositoryViewObject, orgomg.cwm.objectmodel.core.ModelElement)
     */
    @Override
    protected void setActiveElement(IRepositoryViewObject viewObject, DQRule element) {
        // TODO Auto-generated method stub
        
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

