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
import org.talend.commons.utils.data.container.RootContainer;
import org.talend.core.model.properties.FolderItem;
import org.talend.core.model.properties.Item;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.dataquality.indicators.definition.IndicatorDefinition;
import org.talend.dataquality.properties.TDQIndicatorDefinitionItem;
import org.talend.utils.sugars.ReturnCode;

/**
 * DOC klliu class global comment. Detailled comment
 */
public class TDQIndicatorsReposViewObjDelegator extends AbstractDQRepositoryViewObjectDelegator<IndicatorDefinition> {

    private static TDQIndicatorsReposViewObjDelegator instance;

    @Override
    protected void setActiveElement(IRepositoryViewObject viewObject, IndicatorDefinition element) {
        // TODO Auto-generated method stub

    }

    @Override
    protected ReturnCode save(Item item) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * DOC klliu Comment method "getInstance".
     * 
     * @return
     */
    public static TDQIndicatorsReposViewObjDelegator getInstance() {
        instance = new TDQIndicatorsReposViewObjDelegator();
        return instance;
    }

    @Override
    protected List<IRepositoryViewObject> fetchRepositoryViewObjectsLower(boolean withDelete, ERepositoryObjectType type) {
        List<IRepositoryViewObject> indicatorDefinitiontList = new ArrayList<IRepositoryViewObject>();
        List<IRepositoryViewObject> indicatorDefinitionItemList = new ArrayList<IRepositoryViewObject>();
        try {
            // Add indicator folder type for check
            List<IRepositoryViewObject> all = ProxyRepositoryFactory.getInstance().getAll(type,
                    withDelete);
            indicatorDefinitiontList.addAll(all);
            RootContainer<String, IRepositoryViewObject> indicatorDefinitions = ProxyRepositoryFactory.getInstance()
                    .getIndicatorDefinitions(type);
            clear();
            for (IRepositoryViewObject reposViewObj : indicatorDefinitiontList) {
                Item item = reposViewObj.getProperty().getItem();

                if ((item instanceof FolderItem)) {
                    // TODO AAAAAAAAAAAAAA
                } else {
                    IndicatorDefinition indicatorDefinition = ((TDQIndicatorDefinitionItem) item).getIndicatorDefinition();
                    indicatorDefinitionItemList.add(reposViewObj);
                    register(indicatorDefinition, reposViewObj);
                }
            }

        } catch (PersistenceException e) {
            log.error(e, e);
        }
        return indicatorDefinitiontList;
    }

    @Override
    protected List<IRepositoryViewObject> fetchRepositoryViewObjectsLower(boolean withDelete) {
        // TODO Auto-generated method stub
        return null;
    }
}
