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
import org.talend.utils.sugars.ReturnCode;
import orgomg.cwmx.analysis.informationreporting.Report;


/**
 * DOC klliu  class global comment. Detailled comment
 */
public final class TDQReportReposViewObjDelegator extends AbstractDQRepositoryViewObjectDelegator<Report> {

    private static TDQReportReposViewObjDelegator instance;
    @Override
    protected List<IRepositoryViewObject> fetchRepositoryViewObjectsLower(boolean withDelete) {
        List<IRepositoryViewObject> reportList = new ArrayList<IRepositoryViewObject>();
        List<IRepositoryViewObject> reportItemList = new ArrayList<IRepositoryViewObject>();
        try {
            List<IRepositoryViewObject> all = ProxyRepositoryFactory.getInstance().getAll(
                    ERepositoryObjectType.TDQ_REPORT_ELEMENT,
                    withDelete);
            reportList.addAll(all);
            RootContainer<String, IRepositoryViewObject> reports = ProxyRepositoryFactory.getInstance().getReport();
            clear();
            for (IRepositoryViewObject reposViewObj : reportList) {
                Item item = reposViewObj.getProperty().getItem();

                if ((item instanceof FolderItem)) {
                    // TODO AAAAAAAAAAAAAA
                } else {
                    reportItemList.add(reposViewObj);
                }
            }

        } catch (PersistenceException e) {
            log.error(e, e);
        }
        return reportList;
    }

    @Override
    protected ReturnCode save(Item item) {
        // TODO Auto-generated method stub
        return null;
    }



    /*
     * (non-Jsdoc)
     * 
     * @see
     * org.talend.dq.helper.AbstractDQRepositoryViewObjectDelegator#setActiveElement(org.talend.core.model.repository
     * .IRepositoryViewObject, orgomg.cwm.objectmodel.core.ModelElement)
     */
    @Override
    protected void setActiveElement(IRepositoryViewObject viewObject, Report element) {
        // TODO Auto-generated method stub

    }

    public static TDQReportReposViewObjDelegator getInstance() {
        if (instance == null) {
            instance = new TDQReportReposViewObjDelegator();
        }
        return instance;
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
