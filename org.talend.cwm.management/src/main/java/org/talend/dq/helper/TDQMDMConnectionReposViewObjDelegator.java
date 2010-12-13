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
import org.talend.core.model.metadata.builder.connection.MDMConnection;
import org.talend.core.model.properties.FolderItem;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.MDMConnectionItem;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.repository.model.ProxyRepositoryFactory;

/**
 * DOC zshen MDM connection repository view object delegator. use TDQRepositoryViewObjectProxy for high level API calls
 */
public final class TDQMDMConnectionReposViewObjDelegator extends TDQConnectionReposViewObjDelegator<MDMConnection> {

    private static Logger log = Logger.getLogger(TDQMDMConnectionReposViewObjDelegator.class);

    private static TDQMDMConnectionReposViewObjDelegator instance = null;

    private TDQMDMConnectionReposViewObjDelegator() {

    }

    public static TDQMDMConnectionReposViewObjDelegator getInstance() {
        if (instance == null) {
            instance = new TDQMDMConnectionReposViewObjDelegator();
        }
        return instance;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.helper.ADQRepositoryViewObjectDelegator#fetchRepositoryViewObjectsLower()
     */
    @Override
    protected List<IRepositoryViewObject> fetchRepositoryViewObjectsLower(boolean withDelete) {
        List<IRepositoryViewObject> connList = new ArrayList<IRepositoryViewObject>();
        try {
            // ProxyRepositoryFactory.getInstance().initialize();
            // connList.addAll(ProxyRepositoryFactory.getInstance().getMetadataConnection(
            // ProjectManager.getInstance().getCurrentProject(), true).getMembers());
            // connList.addAll(ProxyRepositoryFactory.getInstance().getMetadataMDM(ProjectManager.getInstance().getCurrentProject(),
            // true).getMembers());
            connList
                    .addAll(ProxyRepositoryFactory.getInstance().getAll(ERepositoryObjectType.METADATA_MDMCONNECTION, withDelete));
            ProxyRepositoryFactory.getInstance().getMetadataMDM();
            clear();
            for (IRepositoryViewObject reposViewObj : connList) {
                // Register the Repository view objects by connection to be able to grab the Repository view object
                // later.
                Item item = reposViewObj.getProperty().getItem();
                if (!(item instanceof FolderItem)) {
                    MDMConnection connection = (MDMConnection) ((MDMConnectionItem) item).getConnection();
                    register(connection, reposViewObj);
                }
            }

        } catch (PersistenceException e) {
            log.error(e, e);
        }
        return connList;
    }

}
