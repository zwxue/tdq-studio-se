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

import org.apache.commons.lang.StringUtils;
import org.talend.commons.exception.PersistenceException;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.metadata.builder.connection.DatabaseConnection;
import org.talend.core.model.metadata.builder.database.dburl.SupportDBUrlType;
import org.talend.core.model.properties.DatabaseConnectionItem;
import org.talend.core.model.properties.Item;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.cwm.db.connection.ConnectionUtils;
import org.talend.cwm.helper.CatalogHelper;
import org.talend.cwm.helper.ConnectionHelper;
import orgomg.cwm.resource.relational.Catalog;
import orgomg.cwm.resource.relational.Schema;

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

    protected List<IRepositoryViewObject> fetchRepositoryViewObjectsLower(boolean withDelete) {
        List<IRepositoryViewObject> connList = new ArrayList<IRepositoryViewObject>();
        List<IRepositoryViewObject> returnconnList = new ArrayList<IRepositoryViewObject>();
        try {
            connList.addAll(ProxyRepositoryFactory.getInstance().getAll(ERepositoryObjectType.METADATA_CONNECTIONS, withDelete));
            ProxyRepositoryFactory.getInstance().getMetadataConnection();
            clear();
            for (IRepositoryViewObject reposViewObj : connList) {
                // Register the Repository view objects by connection to be able to grab the Repository view object
                // later.

                Item item = reposViewObj.getProperty().getItem();

                // MOD scorreia 2010-10-20 avoid several class cast exceptions here
                if (item instanceof DatabaseConnectionItem) {
                    DatabaseConnectionItem dbConnectionItem = (DatabaseConnectionItem) item;
                    Connection dbConn = dbConnectionItem.getConnection();
                    DatabaseConnection connection = dbConn instanceof DatabaseConnection ? (DatabaseConnection) dbConn : null;
                    // Judge the structure of database connection, some of them which comes from TOS may be brocken.
                    if (connection == null || !isDBStructureCorrect(connection)) {
                        continue;
                    }
                    String connectionType = connection.getDatabaseType();
                    for (SupportDBUrlType dbType : SupportDBUrlType.values()) {
                        if (dbType.getDBKey().equals(connectionType)
                                || connectionType.contains(SupportDBUrlType.SYBASEDEFAULTURL.getDBKey())) {
                            register(connection, reposViewObj);
                            returnconnList.add(reposViewObj);
                            break;
                        }
                    }

                }
            }

        } catch (PersistenceException e) {
            log.error(e, e);
        }
        return returnconnList;
    }

    /**
     * DOC mzhao Judge the structure of database connection, some of them which comes from TOS may be brocken.
     */
    private boolean isDBStructureCorrect(DatabaseConnection connection) {
        List<Catalog> catalogs = ConnectionHelper.getCatalogs(connection);
        List<Schema> schemas = ConnectionHelper.getSchema(connection);
        // If the database has both catalog and schema (SQL Server).
        if ((ConnectionHelper.getAllSchemas(connection).isEmpty() && (ConnectionUtils.isMssql(connection)
                || ConnectionUtils.isPostgresql(connection) || ConnectionUtils.isAs400(connection)))) {
            if (catalogs != null && catalogs.size() > 0) {
                List<Schema> catSchemas = CatalogHelper.getSchemas(catalogs.get(0));
                if (catSchemas != null && catSchemas.size() > 0) {
                    if (StringUtils.isEmpty(catSchemas.get(0).getName())) {
                        return Boolean.FALSE;
                    }
                }
            }
        }
        // If the database has only catalogs (e.g MYSQL).
        if (catalogs != null && catalogs.size() > 0) {
            if (catalogs != null && catalogs.size() > 0) {
                if (StringUtils.isEmpty(catalogs.get(0).getName())) {
                    return Boolean.FALSE;
                }
            }
        }
        // If the database has only schema(Oracle).
        if (schemas != null && schemas.size() > 0) {
            if (schemas != null && schemas.size() > 0) {
                if (StringUtils.isEmpty(schemas.get(0).getName())) {
                    return Boolean.FALSE;
                }
            }
        }
        return Boolean.TRUE;

    }
}
