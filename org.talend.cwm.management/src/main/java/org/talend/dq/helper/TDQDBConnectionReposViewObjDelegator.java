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

import org.eclipse.core.resources.ResourcesPlugin;
import org.talend.commons.exception.PersistenceException;
import org.talend.core.model.metadata.builder.connection.DatabaseConnection;
import org.talend.core.model.properties.DatabaseConnectionItem;
import org.talend.core.model.properties.FolderItem;
import org.talend.core.model.properties.Item;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.repository.model.ProxyRepositoryFactory;

/**
 * DOC mzhao DB connection repository view object delegator.
 */
public final class TDQDBConnectionReposViewObjDelegator extends TDQConnectionReposViewObjDelegator<DatabaseConnection> {

    private static TDQDBConnectionReposViewObjDelegator instance = null;

    private TDQDBConnectionReposViewObjDelegator() {
    }

    public static TDQDBConnectionReposViewObjDelegator getInstance() {
        if (instance == null) {
            instance = new TDQDBConnectionReposViewObjDelegator();
        }
        return instance;
    }

    protected List<IRepositoryViewObject> fetchRepositoryViewObjectsLower() {
        List<IRepositoryViewObject> connList = new ArrayList<IRepositoryViewObject>();
        if (ResourcesPlugin.getWorkspace().isTreeLocked()) {
            return connList;
        }
        try {
            // ProxyRepositoryFactory.getInstance().initialize();
            // connList.addAll(ProxyRepositoryFactory.getInstance().getAllVersion("_Qhb-I7p0Ed-F2eBqMU9vww", "",
            // ERepositoryObjectType.METADATA_CONNECTIONS));
            // connList.addAll(ProxyRepositoryFactory.getInstance().getMetadataConnection(
            // ProjectManager.getInstance().getCurrentProject(), true).getMembers());
            connList.addAll(ProxyRepositoryFactory.getInstance().getAll(ERepositoryObjectType.METADATA_CONNECTIONS, true));
            clear();
            for (IRepositoryViewObject reposViewObj : connList) {
                // Register the Repository view objects by connection to be able to grab the Repository view object
                // later.
                Item item = reposViewObj.getProperty().getItem();
                if (!(item instanceof FolderItem)) {
                    DatabaseConnection connection = (DatabaseConnection) ((DatabaseConnectionItem) item).getConnection();
                    register(connection, reposViewObj);
                }
            }

        } catch (PersistenceException e) {
            log.error(e, e);
        }
        return connList;
    }

}
