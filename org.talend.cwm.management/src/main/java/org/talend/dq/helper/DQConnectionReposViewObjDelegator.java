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
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.properties.ConnectionItem;
import org.talend.core.model.properties.Item;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.repository.ProjectManager;
import org.talend.repository.model.ProxyRepositoryFactory;

/**
 * DOC mzhao This class help store the needed save connection object.
 * 
 */
public final class DQConnectionReposViewObjDelegator extends ADQRepositoryViewObjectDelegator<Connection> {

    private static Logger log = Logger.getLogger(DQConnectionReposViewObjDelegator.class);

    private static DQConnectionReposViewObjDelegator instance = null;

    private DQConnectionReposViewObjDelegator() {
    }

    public static DQConnectionReposViewObjDelegator getInstance() {
        if (instance == null) {
            instance = new DQConnectionReposViewObjDelegator();
        }
        return instance;
    }

    protected List<IRepositoryViewObject> fetchRepositoryViewObjectsLower() {
        List<IRepositoryViewObject> connList = new ArrayList<IRepositoryViewObject>();
        try {
            // ProxyRepositoryFactory.getInstance().initialize();
            connList.addAll(ProxyRepositoryFactory.getInstance().getMetadataConnection(
                    ProjectManager.getInstance().getCurrentProject(), true).getMembers());
            connList.addAll(ProxyRepositoryFactory.getInstance().getMetadataMDM(ProjectManager.getInstance().getCurrentProject(),
                    true).getMembers());
            clear();
            for (IRepositoryViewObject reposViewObj : connList) {
                // Register the Repository view objects by connection to be able to grab the Repository view object
                // later.
                Item item = reposViewObj.getProperty().getItem();
                Connection connection = ((ConnectionItem) item).getConnection();
                register(connection, reposViewObj);
            }

        } catch (PersistenceException e) {
            log.error(e, e);
        }
        return connList;
    }

    @Override
    protected List<IRepositoryViewObject> filterArrays(int type) {
        List<IRepositoryViewObject> connList = new ArrayList<IRepositoryViewObject>();
        for (Connection conn : this.needSavedElements.keySet()) {
            if (conn.eClass().getClassifierID() == type) {
                connList.add(this.needSavedElements.get(conn));
            }
        }
        return connList;
    }

}
