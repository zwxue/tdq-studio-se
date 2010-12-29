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

import org.apache.log4j.Logger;
import org.talend.commons.exception.PersistenceException;
import org.talend.commons.utils.data.container.RootContainer;
import org.talend.core.model.properties.FolderItem;
import org.talend.core.model.properties.Item;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.properties.TDQAnalysisItem;
import org.talend.utils.sugars.ReturnCode;

/**
 * 
 * DOC mzhao Help to store analysis repository view object.
 */
public final class TDQAnalysisReposViewObjDelegator extends AbstractDQRepositoryViewObjectDelegator<Analysis> {

    private static Logger log = Logger.getLogger(TDQAnalysisReposViewObjDelegator.class);

    private static TDQAnalysisReposViewObjDelegator instance = null;

    private TDQAnalysisReposViewObjDelegator() {

    }

    public static TDQAnalysisReposViewObjDelegator getInstance() {
        if (instance == null) {
            instance = new TDQAnalysisReposViewObjDelegator();
        }
        return instance;
    }

    @Override
    protected List<IRepositoryViewObject> fetchRepositoryViewObjectsLower(boolean withDelete) {
        List<IRepositoryViewObject> anaList = new ArrayList<IRepositoryViewObject>();
        List<IRepositoryViewObject> anaItemList = new ArrayList<IRepositoryViewObject>();
        try {
            List<IRepositoryViewObject> all = ProxyRepositoryFactory.getInstance().getAll(ERepositoryObjectType.TDQ_ANALYSIS,
                    withDelete);
            anaList.addAll(all);
            RootContainer<String, IRepositoryViewObject> analysis1 = ProxyRepositoryFactory.getInstance().getAnalysis();
            clear();
            for (IRepositoryViewObject reposViewObj : anaList) {
                Item item = reposViewObj.getProperty().getItem();

                if ((item instanceof FolderItem)) {
                    // TODO AAAAAAAAAAAAAA
                } else {
                    Analysis analysis = (Analysis) ((TDQAnalysisItem) item).getAnalysis();
                    anaItemList.add(reposViewObj);
                    register(analysis, reposViewObj);
                }
            }

        } catch (PersistenceException e) {
            log.error(e, e);
        }
        return anaList;
    }

    @Override
    protected ReturnCode save(Item item) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected void setActiveElement(IRepositoryViewObject viewObject, Analysis element) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Jsdoc)
     * 
     * @see org.talend.dq.helper.AbstractDQRepositoryViewObjectDelegator#fetchRepositoryViewObjectsLower(boolean,
     * org.talend.core.model.repository.ERepositoryObjectType)
     */
    @Override
    protected List<IRepositoryViewObject> fetchRepositoryViewObjectsLower(boolean withDelete, ERepositoryObjectType type) {
        // TODO Auto-generated method stub
        return null;
    }

}
