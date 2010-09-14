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
import org.eclipse.emf.common.util.URI;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.metadata.builder.connection.DatabaseConnection;
import org.talend.core.model.metadata.builder.connection.MDMConnection;
import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.utils.sugars.ReturnCode;

/**
 * DOC mzhao Proxy of repository view object.
 */
public final class ProxyRepositoryViewObject {

    private ProxyRepositoryViewObject() {

    }

    public static List<IRepositoryViewObject> fetchRepositoryViewObjectsByFolder(boolean reload, ERepositoryObjectType itemType,
            IPath path) {

        if (itemType == ERepositoryObjectType.METADATA_CONNECTIONS) {
            return getDBConnectionInstance().fetchRepositoryViewObjectsByFolder(reload, itemType, path);
        } else if (itemType == ERepositoryObjectType.METADATA_MDMCONNECTION) {
            return getMDMConnectionInstance().fetchRepositoryViewObjectsByFolder(reload, itemType, path);
        }
        return new ArrayList<IRepositoryViewObject>();

    }

    private static TDQDBConnectionReposViewObjDelegator getDBConnectionInstance() {
        return TDQDBConnectionReposViewObjDelegator.getInstance();
    }

    private static TDQMDMConnectionReposViewObjDelegator getMDMConnectionInstance() {
        return TDQMDMConnectionReposViewObjDelegator.getInstance();
    }

    /**
     * 
     * DOC mzhao Get all metadata connections.
     * 
     * @return
     */
    public static List<Connection> getAllMetadataConnections() {
        List<Connection> metadataConns = new ArrayList<Connection>();
        // Database connections
        metadataConns.addAll(getDBConnectionInstance().getAllElements());
        // MDM Connections
        metadataConns.addAll(getMDMConnectionInstance().getAllElements());
        // File connections ...
        return metadataConns;
    }

    /**
     * 
     * DOC mzhao Get all database connections.
     * 
     * @return
     */
    public static List<Connection> getAllDatabaseConnections() {
        List<Connection> metadataConns = new ArrayList<Connection>();
        // Database connections
        metadataConns.addAll(getDBConnectionInstance().getAllElements());
        return metadataConns;
    }

    /**
     * 
     * DOC mzhao Get all mdm connections.
     * 
     * @return
     */
    public static List<Connection> getAllMDMConnections() {
        List<Connection> metadataConns = new ArrayList<Connection>();
        // Database connections
        metadataConns.addAll(getMDMConnectionInstance().getAllElements());
        return metadataConns;
    }

    /**
     * 
     * DOC mzhao Save connection by proper delegator class.
     * 
     * @param connection
     * @return
     */
    public static ReturnCode save(Connection connection) {
        ReturnCode rc = new ReturnCode(Boolean.TRUE);
        if (connection instanceof DatabaseConnection) {
            rc = getDBConnectionInstance().saveElement(connection);
        } else if (connection instanceof MDMConnection) {
            rc = getMDMConnectionInstance().saveElement(connection);
        }
        return rc;
    }

    /**
     * 
     * DOC mzhao Get repository view object with property param by iterate all the metadata cache.
     * 
     * @param property
     * @return
     */
    public static IRepositoryViewObject getRepositoryViewObjectByProperty(Property property) {
        IRepositoryViewObject connReposViewObj = getDBConnectionInstance().getReposViewObjByProperty(property);
        if (connReposViewObj == null) {
            connReposViewObj = getMDMConnectionInstance().getReposViewObjByProperty(property);
        }
        return connReposViewObj;
    }

    /**
     * 
     * DOC mzhao Get repository view object by connection.
     * 
     * @param connection
     * @return
     */
    public static IRepositoryViewObject getRepositoryViewObject(Connection connection) {
        if (connection instanceof DatabaseConnection) {
            return getDBConnectionInstance().getRepositoryViewObject(connection);
        } else if (connection instanceof MDMConnection) {
            return getMDMConnectionInstance().getRepositoryViewObject(connection);
        }
        // File connection ...
        return null;
    }

    public static List<IRepositoryViewObject> fetchAllDBRepositoryViewObjects(Boolean reload) {
        List<IRepositoryViewObject> dbReposViewObjs = new ArrayList<IRepositoryViewObject>();
        dbReposViewObjs.addAll(getDBConnectionInstance().fetchRepositoryViewObjects(reload));
        return dbReposViewObjs;
    }

    public static List<IRepositoryViewObject> fetchAllMDMRepositoryViewObjects(Boolean reload) {
        List<IRepositoryViewObject> mdmReposViewObjs = new ArrayList<IRepositoryViewObject>();
        mdmReposViewObjs.addAll(getMDMConnectionInstance().fetchRepositoryViewObjects(reload));
        return mdmReposViewObjs;
    }

    /**
     * 
     * DOC mzhao Fetch all repository view objects, database, mdm, file etc.
     * 
     * @param true1
     */
    public static List<IRepositoryViewObject> fetchAllRepositoryViewObjects(Boolean reload) {
        List<IRepositoryViewObject> reposViewObjs = new ArrayList<IRepositoryViewObject>();
        reposViewObjs.addAll(getDBConnectionInstance().fetchRepositoryViewObjects(reload));
        reposViewObjs.addAll(getMDMConnectionInstance().fetchRepositoryViewObjects(reload));
        return reposViewObjs;
    }

    /**
     * 
     * DOC mzhao Put the connection-uri to map cache so that the resource can be reload by URI.
     * 
     * @param connection
     * @param uri
     */
    public static void registerURI(Connection connection, URI uri) {
        if (connection instanceof DatabaseConnection) {
            getDBConnectionInstance().register(connection, uri);
        } else if (connection instanceof MDMConnection) {
            getMDMConnectionInstance().register(connection, uri);
        }
    }

    /**
     * 
     * DOC mzhao Put connection-repositoryViewObject to map cache so that the view object can easily be obtained by
     * connection instance.
     * 
     * @param connection
     * @param reposViewObj
     */
    public static void registerReposViewObj(Connection connection, IRepositoryViewObject reposViewObj) {
        if (connection instanceof DatabaseConnection) {
            getDBConnectionInstance().register(connection, reposViewObj);
        } else if (connection instanceof MDMConnection) {
            getMDMConnectionInstance().register(connection, reposViewObj);
        }
    }
}
