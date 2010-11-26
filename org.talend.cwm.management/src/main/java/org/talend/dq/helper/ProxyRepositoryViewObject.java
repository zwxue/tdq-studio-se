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
import org.talend.core.model.properties.ConnectionItem;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.cwm.db.connection.ConnectionUtils;
import org.talend.utils.sugars.ReturnCode;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC mzhao Proxy of repository view object.
 */
public final class ProxyRepositoryViewObject {

    private ProxyRepositoryViewObject() {

    }

    public static List<IRepositoryViewObject> fetchRepositoryViewObjectsByFolder(boolean reload, ERepositoryObjectType itemType,
            IPath path, boolean withDelete) {

        if (itemType == ERepositoryObjectType.METADATA_CONNECTIONS) {
            return getDBConnectionInstance().fetchRepositoryViewObjectsByFolder(reload, itemType, path, withDelete);
        } else if (itemType == ERepositoryObjectType.METADATA_MDMCONNECTION) {
            return getMDMConnectionInstance().fetchRepositoryViewObjectsByFolder(reload, itemType, path, withDelete);
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
        return ConnectionUtils.fillConnectionInformation(metadataConns);
    }

    /**
     * 
     * DOC yyi Get all metadata connections.modEleToReposObjMap.
     * 
     * @return
     */
    public static List<Connection> getAllMetadataConnections(boolean excludeRecycleBin) {
        List<Connection> connections = new ArrayList<Connection>();
        if (excludeRecycleBin) {
            for (Connection con : getAllMetadataConnections()) {
                IRepositoryViewObject repositoryViewObject = getRepositoryViewObject(con);
                if (repositoryViewObject == null) {
                    continue;
                }
                Property property = repositoryViewObject.getProperty();
                if (property == null) {
                    continue;
                }
                Item propItem = property.getItem();
                if (propItem == null || !(propItem instanceof ConnectionItem)) {
                    continue;
                }
                ConnectionItem item = (ConnectionItem) propItem;
                if (null != item && !item.getState().isDeleted()) {
                    connections.add(con);
                }
            }
        }
        return connections;
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
     * DOC yyi Comment method "getAllDatabaseConnections".
     * 
     * @param excludeRecycleBin
     * @return
     */
    public static List<Connection> getAllDatabaseConnections(boolean excludeRecycleBin) {
        List<Connection> connections = new ArrayList<Connection>();
        if (excludeRecycleBin) {
            for (Connection con : getAllDatabaseConnections()) {
                if (null == con.eResource()) {
                    con = (Connection) EObjectHelper.resolveObject(con);
                }

                if (null != con.eResource() && !PropertyHelper.getProperty(con).getItem().getState().isDeleted()) {
                    connections.add(con);
                }
            }
        }
        return connections;
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
     * DOC yyi Comment method "getAllDatabaseConnections".
     * 
     * @param excludeRecycleBin
     * @return
     */
    public static List<Connection> getAllMDMConnections(boolean excludeRecycleBin) {
        List<Connection> connections = new ArrayList<Connection>();
        if (excludeRecycleBin) {
            for (Connection con : getAllMDMConnections()) {
                if (null == con.eResource()) {
                    con = (Connection) EObjectHelper.resolveObject(con);
                }
                if (null != con.eResource() && !PropertyHelper.getProperty(con).getItem().getState().isDeleted()) {
                    connections.add(con);
                }
            }
        }
        return connections;
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
        // MOD qiongli bug 14469
        Item item = property.getItem();
        if (item instanceof ConnectionItem) {
            Connection connection = ((ConnectionItem) item).getConnection();
            if (connection != null && connection.eIsProxy()) {
                connection = (Connection) EObjectHelper.resolveObject(connection);
            }
            return getRepositoryViewObject(connection);
        }
        return null;
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

    /**
     * 
     * DOC zshen Comment method "areSame".
     * 
     * @param m1 the one of need to compare element
     * @param m2 another of need to compare element
     * @return
     */
    public static boolean areSame(ModelElement m1, ModelElement m2) {
        if (m1 instanceof DatabaseConnection) {
            return getDBConnectionInstance().areSame((Connection) m1, (Connection) m2);
        } else if (m1 instanceof MDMConnection) {
            return getMDMConnectionInstance().areSame((Connection) m1, (Connection) m2);
        }
        // File connection ...
        return false;
    }

    public static List<IRepositoryViewObject> fetchAllDBRepositoryViewObjects(Boolean reload, boolean withDelete) {
        List<IRepositoryViewObject> dbReposViewObjs = new ArrayList<IRepositoryViewObject>();
        dbReposViewObjs.addAll(getDBConnectionInstance().fetchRepositoryViewObjects(reload, withDelete));
        return dbReposViewObjs;
    }

    public static List<IRepositoryViewObject> fetchAllMDMRepositoryViewObjects(Boolean reload, boolean withDelete) {
        List<IRepositoryViewObject> mdmReposViewObjs = new ArrayList<IRepositoryViewObject>();
        mdmReposViewObjs.addAll(getMDMConnectionInstance().fetchRepositoryViewObjects(reload, withDelete));
        return mdmReposViewObjs;
    }

    /**
     * 
     * DOC mzhao Fetch all repository view objects, database, mdm, file etc.
     * 
     * @param true1
     */
    public static List<IRepositoryViewObject> fetchAllRepositoryViewObjects(Boolean reload, boolean withDelete) {
        List<IRepositoryViewObject> reposViewObjs = new ArrayList<IRepositoryViewObject>();
        reposViewObjs.addAll(getDBConnectionInstance().fetchRepositoryViewObjects(reload, withDelete));
        reposViewObjs.addAll(getMDMConnectionInstance().fetchRepositoryViewObjects(reload, withDelete));
        return reposViewObjs;
    }

    public static List<IRepositoryViewObject> fetchAllRepositoryViewObjects(Boolean paramBoolean) {
        ArrayList localArrayList = new ArrayList();
        localArrayList.addAll(getDBConnectionInstance().fetchRepositoryViewObjects(paramBoolean.booleanValue(), true));
        localArrayList.addAll(getMDMConnectionInstance().fetchRepositoryViewObjects(paramBoolean.booleanValue(), true));
        return localArrayList;
    }

    public static List<IRepositoryViewObject> fetchRepositoryViewObjectsByFolder(boolean paramBoolean,
            ERepositoryObjectType paramERepositoryObjectType, IPath paramIPath) {
        if (paramERepositoryObjectType == ERepositoryObjectType.METADATA_CONNECTIONS)
            return getDBConnectionInstance().fetchRepositoryViewObjectsByFolder(paramBoolean, paramERepositoryObjectType,
                    paramIPath, true);
        if (paramERepositoryObjectType == ERepositoryObjectType.METADATA_MDMCONNECTION)
            return getMDMConnectionInstance().fetchRepositoryViewObjectsByFolder(paramBoolean, paramERepositoryObjectType,
                    paramIPath, true);
        return new ArrayList();
    }

    public static List<IRepositoryViewObject> fetchAllDBRepositoryViewObjects(Boolean paramBoolean) {
        ArrayList localArrayList = new ArrayList();
        localArrayList.addAll(getDBConnectionInstance().fetchRepositoryViewObjects(paramBoolean.booleanValue(), true));
        return localArrayList;
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
