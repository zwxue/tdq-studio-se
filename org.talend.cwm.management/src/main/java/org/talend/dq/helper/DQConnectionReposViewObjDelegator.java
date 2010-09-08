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
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.metadata.builder.connection.DatabaseConnection;
import org.talend.core.model.metadata.builder.connection.MDMConnection;
import org.talend.core.model.properties.DatabaseConnectionItem;
import org.talend.core.model.properties.MDMConnectionItem;
import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.utils.sugars.ReturnCode;

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
        connList.addAll(DQMDMConnectionReposViewObjDelegator.getInstance().fetchRepositoryViewObjectsLower());
        connList.addAll(DQDBConnectionReposViewObjDelegator.getInstance().fetchRepositoryViewObjectsLower());

        // try {
        // // ProxyRepositoryFactory.getInstance().initialize();
        // connList.addAll(ProxyRepositoryFactory.getInstance().getMetadataConnection(
        // ProjectManager.getInstance().getCurrentProject(), true).getMembers());
        // connList.addAll(ProxyRepositoryFactory.getInstance().getMetadataMDM(ProjectManager.getInstance().getCurrentProject(),
        // true).getMembers());
        // clear();
        // for (IRepositoryViewObject reposViewObj : connList) {
        // // Register the Repository view objects by connection to be able to grab the Repository view object
        // // later.
        // Item item = reposViewObj.getProperty().getItem();
        // DatabaseConnection connection = (DatabaseConnection) ((DatabaseConnectionItem) item).getConnection();
        // register(connection, reposViewObj);
        // }
        //
        // } catch (PersistenceException e) {
        // log.error(e, e);
        // }
        return connList;
    }

    @Override
    public List<IRepositoryViewObject> fetchRepositoryViewObjects(boolean reload) {
        List<IRepositoryViewObject> returnList = new ArrayList<IRepositoryViewObject>();
        returnList.addAll(DQMDMConnectionReposViewObjDelegator.getInstance().fetchRepositoryViewObjects(reload));
        returnList.addAll(DQDBConnectionReposViewObjDelegator.getInstance().fetchRepositoryViewObjects(reload));
        return returnList;
    }

    @Override
    public Collection<Connection> getAllElements() {
        List<Connection> allElements = new ArrayList();
        allElements.addAll(DQMDMConnectionReposViewObjDelegator.getInstance().getAllElements());
        allElements.addAll(DQDBConnectionReposViewObjDelegator.getInstance().getAllElements());
        return allElements;
    }

    @Override
    public IRepositoryViewObject getRepositoryViewObject(Connection element) {
        if (element instanceof MDMConnection) {
            return DQMDMConnectionReposViewObjDelegator.getInstance().getRepositoryViewObject((MDMConnection) element);
        } else if (element instanceof DatabaseConnection) {
            return DQDBConnectionReposViewObjDelegator.getInstance().getRepositoryViewObject((DatabaseConnection) element);
        }
        return null;
    }

    @Override
    public IRepositoryViewObject getReposViewObjByProperty(Property property) {
        if (property.getItem() instanceof MDMConnectionItem) {
            return DQMDMConnectionReposViewObjDelegator.getInstance().getReposViewObjByProperty(property);
        } else if (property.getItem() instanceof DatabaseConnectionItem) {
            return DQDBConnectionReposViewObjDelegator.getInstance().getReposViewObjByProperty(property);
        }
        return null;
    }

    @Override
    protected void register(Connection modelElement, IRepositoryViewObject reposViewObj) {
        if (modelElement instanceof MDMConnection) {
            DQMDMConnectionReposViewObjDelegator.getInstance().register((MDMConnection) modelElement, reposViewObj);
        } else if (modelElement instanceof DatabaseConnection) {
            DQDBConnectionReposViewObjDelegator.getInstance().register((DatabaseConnection) modelElement, reposViewObj);
        }
    }

    @Override
    public void remove(Connection element) {
        if (element instanceof MDMConnection) {
            DQMDMConnectionReposViewObjDelegator.getInstance().remove((MDMConnection) element);
        } else if (element instanceof DatabaseConnection) {
            DQDBConnectionReposViewObjDelegator.getInstance().remove((DatabaseConnection) element);
        }
    }

    @Override
    public void saveAllElements() {
        DQMDMConnectionReposViewObjDelegator.getInstance().saveAllElements();
        DQDBConnectionReposViewObjDelegator.getInstance().saveAllElements();
    }

    @Override
    public void saveConnectionWithReloadPackage(Connection element) {
        if (element instanceof MDMConnection) {
            DQMDMConnectionReposViewObjDelegator.getInstance().saveConnectionWithReloadPackage((MDMConnection) element);
        } else if (element instanceof DatabaseConnection) {
            DQDBConnectionReposViewObjDelegator.getInstance().saveConnectionWithReloadPackage((DatabaseConnection) element);
        }
    }

    @Override
    public ReturnCode saveElement(Connection element) {

        if (element instanceof MDMConnection) {
            return DQMDMConnectionReposViewObjDelegator.getInstance().saveElement((MDMConnection) element);
        } else if (element instanceof DatabaseConnection) {
            return DQDBConnectionReposViewObjDelegator.getInstance().saveElement((DatabaseConnection) element);
        }
        return null;
    }

    @Override
    public void clear() {
        DQMDMConnectionReposViewObjDelegator.getInstance().clear();
        DQDBConnectionReposViewObjDelegator.getInstance().clear();
    }

}
