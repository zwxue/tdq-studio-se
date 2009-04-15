// ============================================================================
//
// Copyright (C) 2006-2009 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.cwm.db.connection;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EObject;
import org.talend.commons.emf.EMFSharedResources;
import org.talend.commons.emf.EMFUtil;
import org.talend.cwm.builders.CatalogBuilder;
import org.talend.cwm.builders.DataProviderBuilder;
import org.talend.cwm.builders.SoftwareSystemBuilder;
import org.talend.cwm.management.connection.DatabaseContentRetriever;
import org.talend.cwm.relational.TdCatalog;
import org.talend.cwm.relational.TdSchema;
import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.cwm.softwaredeployment.TdProviderConnection;
import org.talend.cwm.softwaredeployment.TdSoftwareSystem;
import org.talend.dq.analysis.parameters.DBConnectionParameter;
import org.talend.utils.sugars.ReturnCode;
import orgomg.cwm.foundation.typemapping.TypeSystem;

/**
 * @author scorreia
 * 
 * This class builds CWM classes from a database connection and offers facilities for serializing data.
 */
public class DBConnect {

    private static final Class<DBConnect> THAT = DBConnect.class;

    private static Logger log = Logger.getLogger(THAT);

    private final EMFUtil emfUtil = EMFSharedResources.getSharedEmfUtil();

    private final String databaseUrl;

    private final Properties connectionProperties;

    private final String driverClass;

    private Driver driver;

    private Connection connection;

    private SoftwareSystemBuilder softwareSystemBuilder;

    private CatalogBuilder catalogBuilder;

    private TdProviderConnection providerConnection;

    private DataProviderBuilder dataProvBuilder;

    // TODO scorreia errorMessage;

    /**
     * DBConnect constructor.
     * 
     * @param connParams the connection parameters (must not be null). The connection parameters must have at least a
     * JDBC url, a driver classname and a user password (when needed) in its properties.
     */
    public DBConnect(DBConnectionParameter connParams) {
        this(connParams.getJdbcUrl(), connParams.getDriverClassName(), connParams.getParameters());
        this.emfUtil.setUsePlatformRelativePath(true);
    }

    // public DBConnect(TdDataProvider dataprovider) {
    // // TODO scorreia
    // }

    /**
     * DBConnect constructor. Path will not be platform relative with this constructor.
     * 
     * @param dbUrl the database URL (must not be null)
     * @param driverClassName the driver class name (must not be null)
     * @param props properties passed to the connection
     */
    protected DBConnect(final String dbUrl, final String driverClassName, final Properties props) {
        assert dbUrl != null && driverClassName != null;

        this.databaseUrl = dbUrl;
        this.driverClass = driverClassName;
        this.connectionProperties = props;
        this.emfUtil.setUsePlatformRelativePath(false); // use file paths for tests
    }

    /**
     * Method "getCatalogs". Method this.{@link #retrieveDatabaseStructure()} should be called before getting the
     * catalogs.
     * 
     * @return the catalogs built from the connection, or an empty list.
     */
    public Collection<TdCatalog> getCatalogs() {
        if (catalogBuilder == null) {
            return Collections.emptyList();
        }
        return catalogBuilder.getCatalogs();
    }

    /**
     * Method "getSchemata". Method this.{@link #retrieveDatabaseStructure()} should be called before getting the
     * schematas.
     * 
     * @return the schemata built from the connection, or an empty list.
     */
    public Collection<TdSchema> getSchemata() {
        if (catalogBuilder == null) {
            return Collections.emptyList();
        }
        return catalogBuilder.getSchemata();
    }

    /**
     * Method "getSoftwareSystem". Method this.{@link #retrieveDeployedSystemInformations()} should be called before
     * getting the software system.
     * 
     * @return the Software system or null.
     */
    public TdSoftwareSystem getSoftwareSystem() {
        if (softwareSystemBuilder == null) {
            return null;
        }
        return softwareSystemBuilder.getSoftwareSystem();
    }

    /**
     * Method "getTypeSystem". Method this.{@link #retrieveDeployedSystemInformations()} should be called before getting
     * the type system.
     * 
     * @return the TypeSystem that defines the datatypes supported by the software system.
     */
    public TypeSystem getTypeSystem() {
        if (softwareSystemBuilder == null) {
            return null;
        }
        return softwareSystemBuilder.getTypeSystem();
    }

    /**
     * Method "getProviderConnection". This method can be called right after {@link this#connect()}.
     * 
     * @return the connection provider (containing connection parameters given in CTOR)
     */
    public TdProviderConnection getProviderConnection() {
        return providerConnection;
    }

    /**
     * Method "saveInFiles". Calling this method without having called this.{@link #storeInResourceSet(EObject, String)}
     * will do nothing. If objects are dependent on each other, each object must be stored in a resource set first
     * before trying to save them in a file.
     * 
     * @return true if CWM objects have been saved in file.
     */
    boolean saveInFiles() {
        return emfUtil.save();
    }

    /**
     * Method "storeInResourceSet" prepares the given object to be saved in the given file. Then the method
     * {@link #saveInFiles()} must be called to write the files.
     * 
     * @param eObject the object to be saved
     * @param filename the file name to which the object must be saved
     * @return true (as per the general contract of the Collection.add method).
     */
    public boolean storeInResourceSet(EObject eObject, String path) {
        return emfUtil.addPoolToResourceSet(path, eObject);
    }

    /**
     * Method "closeConnection".
     * 
     * @param connection the connection to close
     * @return true if connection has been closed.
     */
    public boolean closeConnection() {
        if (connection == null) {
            return true;
        }
        try {
            connection.close();
            return true;
        } catch (SQLException e) {
            log.error("Problem when closing connection", e);
            return false;
        }
    }

    /**
     * Method "connect". Opens a connection by using the DriverManager class. It is of the responsability of the caller
     * to close it correctly. When calling this method, you must call individual retrieveXXX() methods in order to get
     * the desired informations of the database.
     * 
     * @param dbUrl the url
     * @param driverClassName
     * @param props
     * @return false if problem. true if the connection is opened.
     * @throws SQLException
     */
    public boolean connect() throws SQLException {
        // --- check preconditions
        boolean ok = StringUtils.isNotBlank(databaseUrl) && StringUtils.isNotBlank(driverClass);
        // --- connect
        ok = ok && connectLow(this.databaseUrl, this.driverClass, this.connectionProperties);
        return ok;
    }

    public boolean isConnected() {
        return ConnectionUtils.isValid(connection).isOk();
    }

    /**
     * Method "connectAndRetrieveInformations". Opens a connection by using the DriverManager class. It is of the
     * responsability of the caller to close it correctly. This method retrieve all available informations of the
     * connected database. These information can be then used by calling the getter methods.
     * 
     * @param dbUrl the url
     * @param driverClassName
     * @param props
     * @return false if problem. true if the connection is opened.
     */
    boolean connectAndRetrieveInformations() {
        boolean ok = true;
        try {
            ok = connect();
            // --- initialize fields
            ok = ok && retrieveDeployedSystemInformations();
            ok = ok && retrieveDatabaseStructure();
            ok = ok && retrieveDriverInformations();
        } catch (SQLException e) {
            log.error("Problem during connection to " + databaseUrl + " with " + driverClass, e);
            ok = false;
        }
        return ok;
    }

    @Override
    public String toString() {
        return "Connection " + databaseUrl; //$NON-NLS-1$
    }

    /**
     * Method "retrieveDeployedSystemInformations". This method fills in the Software deployment information like the
     * SoftwareSystem.
     * 
     * @return true if ok
     * @throws SQLException
     */
    public boolean retrieveDeployedSystemInformations() throws SQLException {
        boolean ok = checkConnection("Cannot retrieve deployed system informations");
        if (ok) {
            softwareSystemBuilder = new SoftwareSystemBuilder(connection);
        }
        return ok;
    }

    /**
     * Method "retrieveDatabaseStructure" retrieves the catalog and schemas list from the database. If it has already
     * been done once, then the structure is not requested again from the database.
     * 
     * @return ok if no problem
     * @throws SQLException
     */
    public boolean retrieveDatabaseStructure() throws SQLException {
        boolean ok = checkConnection("Cannot retrieve database structure. ");
        if (ok && catalogBuilder == null) {
            catalogBuilder = new CatalogBuilder(connection);
            // initialize database structure
            catalogBuilder.getCatalogs();
            catalogBuilder.getSchemata();
        }
        return ok;
    }

    /**
     * Method "retrieveDriverInformations".
     * 
     * @return ok if no problem
     * @throws SQLException
     */
    public boolean retrieveDriverInformations() throws SQLException {
        boolean ok = checkConnection("Cannot connect to database. "); // TODO scorreia verify if needed
        if (ok) {
            dataProvBuilder = new DataProviderBuilder(connection, driver, databaseUrl, connectionProperties);
        }
        return ok;
    }

    // /**
    // * Method "retrieveTables".
    // *
    // * @param catalogName the name of the catalog
    // * @param schemaName the name of the schema
    // * @return true if tables and columns have been loaded and stored into the corresponding catalog and/or schema.
    // * @throws SQLException
    // */
    // public boolean retrieveTables(String catalogName, String schemaName) throws SQLException {
    // boolean ok = checkConnection("Cannot connect to database. ");
    // if (catalogBuilder == null || !ok) {
    // return false;
    // }
    // // else
    // return catalogBuilder.setTablesIntoStructure(catalogName, schemaName);
    // }

    /**
     * Method "checkConnection" checks that the connection is not null and not closed.
     * 
     * @param message the error message to log in case of problem.
     * @return true if ok
     * @throws SQLException
     */
    private boolean checkConnection(String message) throws SQLException {
        ReturnCode valid = ConnectionUtils.isValid(connection);
        boolean isOk = valid.isOk();
        if (!isOk) {
            log.error(message + "\n" + valid.getMessage());
        }
        return isOk;
    }

    /**
     * Method "connect". Opens a connection by using the DriverManager class. It is of the responsability of the caller
     * to close it correctly.
     * 
     * @param dbUrl the url
     * @param driverClassName
     * @param props
     * @return true if the connection is opened.
     * @throws SQLException
     */
    private boolean connectLow(String dbUrl, String driverClassName, Properties props) throws SQLException {
        boolean ok = true;
        try {
            driver = ConnectionUtils.getClassDriver(driverClassName);
            // MOD xqliu 2009-02-03 bug 5261
            connection = ConnectionUtils.createConnectionWithTimeout(driver, dbUrl, props);
            // connection = DriverManager.getConnection(dbUrl, props);
            if (connection != null) {
                this.providerConnection = DatabaseContentRetriever.getProviderConnection(dbUrl, driverClassName, props,
                        connection);
            }
        } catch (InstantiationException e) {
            log.error(e, e);
            ok = false;
        } catch (IllegalAccessException e) {
            log.error(e, e);
            ok = false;
        } catch (ClassNotFoundException e) {
            log.error(e, e);
            ok = false;
        }
        return ok;
    }

    /**
     * Method "getDataProvider" should be called after {@link #retrieveDriverInformations()}. Note that the
     * ProviderConnection is already linked to the data provider when calling this method.
     * 
     * @return the data provider or null.
     */
    public TdDataProvider getDataProvider() {
        if (dataProvBuilder == null) {
            return null;
        }
        return this.dataProvBuilder.getDataProvider();
    }

    /**
     * Getter for databaseUrl.
     * 
     * @return the databaseUrl (not null)
     */
    public String getDatabaseUrl() {
        return this.databaseUrl;
    }
}
