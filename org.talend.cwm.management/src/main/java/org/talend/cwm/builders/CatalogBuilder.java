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
package org.talend.cwm.builders;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.talend.cwm.db.connection.ConnectionUtils;
import org.talend.cwm.helper.CatalogHelper;
import org.talend.cwm.management.connection.DatabaseConstant;
import org.talend.cwm.management.connection.DatabaseContentRetriever;
import org.talend.cwm.management.i18n.Messages;
import org.talend.dq.analysis.parameters.DBConnectionParameter;
import org.talend.utils.sql.metadata.constants.MetaDataConstants;
import orgomg.cwm.resource.relational.Catalog;
import orgomg.cwm.resource.relational.Schema;

/**
 * @author scorreia
 * 
 * This class builds relational CWM objects from a connection.
 */
public class CatalogBuilder extends CwmBuilder {

    private static Logger log = Logger.getLogger(CatalogBuilder.class);

    private final Map<String, Catalog> name2catalog = new HashMap<String, Catalog>();

    private final Set<Schema> schemata = new HashSet<Schema>();

    private boolean catalogsInitialized = false;

    private boolean schemaInitialized = false;

    private Map<String, Catalog> catalogsToUpdate = new HashMap<String, Catalog>();

    // ADD xqliu 2010-03-03 feature 11412
    protected DBConnectionParameter dbConnectionParameter;

    public DBConnectionParameter getDbConnectionParameter() {
        return dbConnectionParameter;
    }
    // ~11412

    /**
     * CatalogBuilder constructor. Catalogs and/or schemata are initialized, but not the lower structure such as the
     * table, trigger, procedures...
     * 
     * @param connection2 the sql connection
     * @throws SQLException
     */
    public CatalogBuilder(Connection conn) {
        super(conn);
        try {
            if (log.isDebugEnabled()) {
                printMetadata();
            }
        } catch (RuntimeException e) {
            log.error(e, e);
        } catch (SQLException e) {
            log.error(e, e);
        }

    }

    /**
     * DOC xqliu CatalogBuilder constructor comment. ADD xqliu 2010-03-03 feature 11412
     * 
     * @param conn
     * @param dbParam
     */
    public CatalogBuilder(Connection conn, DBConnectionParameter dbParam) {
        this(conn);
        this.dbConnectionParameter = dbParam;
    }

    public boolean refreshCatalogs(final Collection<Catalog> catalogs) {
        for (Catalog tdCatalog : catalogs) {
            if (tdCatalog == null) {
                continue;
            }
            this.catalogsToUpdate.put(tdCatalog.getName(), tdCatalog);
        }
        initializeCatalog();
        // remove all catalogs
        this.catalogsToUpdate.clear();
        return true;
    }

    /**
     * Method "printMetadata" for debug or test purpose only.
     * 
     * @throws SQLException
     */
    private void printMetadata() throws SQLException {
        DatabaseMetaData databaseMetadata = getConnectionMetadata(connection);
        int databaseMinorVersion = databaseMetadata.getDatabaseMinorVersion();
        int databaseMajorVersion = databaseMetadata.getDatabaseMajorVersion();
        String databaseProductName = databaseMetadata.getDatabaseProductName();
        String databaseProductVersion = databaseMetadata.getDatabaseProductVersion();
        print("Database=", databaseProductName + " " + databaseProductVersion + " " + databaseMajorVersion + "." //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                + databaseMinorVersion);

        String driverName = databaseMetadata.getDriverName();
        String driverVersion = databaseMetadata.getDriverVersion();
        int driverMajorVersion = databaseMetadata.getDriverMajorVersion();
        int driverMinorVersion = databaseMetadata.getDriverMinorVersion();
        print("Driver=", driverName + " " + driverVersion + " " + driverMajorVersion + "." + driverMinorVersion); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

        int majorVersion = databaseMetadata.getJDBCMajorVersion();
        int minorVersion = databaseMetadata.getJDBCMinorVersion();
        print("JDBC=", "" + majorVersion + "." + minorVersion); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        String identifierQuoteString = databaseMetadata.getIdentifierQuoteString();
        print("quote=", identifierQuoteString); //$NON-NLS-1$

        String extraNameCharacters = databaseMetadata.getExtraNameCharacters();
        print("extra=", extraNameCharacters); //$NON-NLS-1$

        String procedureTerm = databaseMetadata.getProcedureTerm();
        print("proc=", procedureTerm); //$NON-NLS-1$

        String url = databaseMetadata.getURL();
        print("DB url=", url); //$NON-NLS-1$
        String userName = databaseMetadata.getUserName();
        print("user=", userName); //$NON-NLS-1$

        String catalogTerm = databaseMetadata.getCatalogTerm();
        print("Catalog term=", catalogTerm); //$NON-NLS-1$

        String catalogSeparator = databaseMetadata.getCatalogSeparator();
        print("Catalog sep=", catalogSeparator); //$NON-NLS-1$
        String keywords = databaseMetadata.getSQLKeywords();
        print("keywords=", keywords); //$NON-NLS-1$

        String schemaTerm = databaseMetadata.getSchemaTerm();
        print("Schema term=", schemaTerm); //$NON-NLS-1$

    }

    /**
     * Method "getCatalogs".
     * 
     * @return the catalogs initialized with the metadata (The list could be empty). If the method
     * {@link CatalogBuilder#getSchemata()} has been called before, then the catalogs will contain schemata. if not,
     * then the catalogs are empty.
     */
    public Collection<Catalog> getCatalogs() {
        if (!catalogsInitialized) {
            initializeCatalog();
        }
        return this.name2catalog.values();
    }

    /**
     * Method "getSchemata".
     * 
     * @return the schemata initialized with the metadata. Catalogs will be created.
     */
    public Collection<Schema> getSchemata() {
        if (!schemaInitialized) {
            initializeSchema();
        }
        return this.schemata;
    }

    public Catalog getCatalog(String catalogName) {
        if (!catalogsInitialized) {
            initializeCatalog();
        }
        return this.name2catalog.get(catalogName);
    }

    private void initializeCatalog() {
        try {
            initializeCatalogLow();
        } catch (SQLException e) {
            log.error(e, e);
        }
    }

    private void initializeCatalogLow() throws SQLException {
        // ADD xqliu 2010-03-03 feature 11412
        String dbName = getDbConnectionParameter() == null ? null : getDbConnectionParameter().getDbName();
        // ~11412
        // MOD xqliu 2009-10-29 bug 9838
        DatabaseMetaData connectionMetadata = getConnectionMetadata(connection);
        if (connectionMetadata.getDatabaseProductName() != null
                && connectionMetadata.getDatabaseProductName().toLowerCase().indexOf(DatabaseConstant.ODBC_ORACLE_PRODUCT_NAME) > -1) {
            catalogsInitialized = true;
            return;
        }
        ResultSet catalogNames = null;
        try {
            catalogNames = connectionMetadata.getCatalogs();
        } catch (Exception e) {
            log.warn("JDBC getCatalogs() method is not available with this driver.", e);
        }
        // ~
        if (catalogNames == null) {
            String currentCatalogName = null;
            try {
                currentCatalogName = connection.getCatalog();
            } catch (Exception e) {
                log.warn("JDBC error: no current catalog can be found", e);
            }
            if (currentCatalogName == null) {
                if (log.isInfoEnabled()) {
                    log.info("No catalog found in connection " + getConnectionInformations(connection));
                }
                // -- continue without creating any catalog
            } else { // got the current catalog name, create a Catalog
                Catalog catalog = createOrUpdateCatalog(currentCatalogName);
                name2catalog.put(currentCatalogName, catalog);
            }
        } else {
            // else DB support getCatalogs() method
            while (catalogNames.next()) {
                // MOD xqliu 2009-11-03 bug 9841
                String catalogName = null;
                try {
                    String temp = ConnectionUtils.isOdbcPostgresql(connection) ? DatabaseConstant.ODBC_POSTGRESQL_CATALOG_NAME
                            : MetaDataConstants.TABLE_CAT.name();
                    catalogName = catalogNames.getString(temp);
                } catch (Exception e) {
                    log.warn(e, e);
                    if (connectionMetadata.getDatabaseProductName() != null
                            && connectionMetadata.getDatabaseProductName().toLowerCase().indexOf(
                                    DatabaseConstant.POSTGRESQL_PRODUCT_NAME) > -1) {
                        catalogName = "";
                    }
                }
                // ~
                assert catalogName != null : Messages.getString("CatalogBuilder.CatalogNameNull",//$NON-NLS-1$
                        getConnectionInformations(connection));
                // MOD xqliu 2010-03-03 feature 11412
                if (retrieveCatalogSchema(dbName, catalogName)) {
                    Catalog catalog = createOrUpdateCatalog(catalogName);
                    name2catalog.put(catalogName, catalog);
                }
                // ~11412
            }
            // --- release the result set.
            catalogNames.close();
        }
        catalogsInitialized = true;
    }

    /**
     * DOC xqliu Comment method "retrieveCatalogSchema".
     * 
     * @param dbName
     * @param catalogSchemaName
     * @return
     */
    private boolean retrieveCatalogSchema(String dbName, String catalogSchemaName) {
        if (getDbConnectionParameter() == null || getDbConnectionParameter().isRetrieveAllMetadata() || dbName == null
                || dbName.equals(catalogSchemaName)) {
            return true;
        }
        return false;
    }

    private void initializeSchema() {
        try {
            initializeSchemaLow();
        } catch (SQLException e) {
            log.error(e, e);
        }
    }

    private void initializeSchemaLow() throws SQLException {
        // ADD xqliu 2010-03-03 feature 11412
        String dbName = getDbConnectionParameter() == null ? null : getDbConnectionParameter().getDbName();
        // ~11412
        // initialize the catalog if not already done
        if (!this.catalogsInitialized) {
            initializeCatalog();
        }

        // MOD mzhao bug 8502 2009-10-29
        Map<String, List<Schema>> catalog2schemas = null;
        if (this.connection.getMetaData().getDriverName().equals(DatabaseConstant.MSSQL_DRIVER_NAME_JDBC2_0)) {
            catalog2schemas = DatabaseContentRetriever.getMSSQLSchemas(this.connection);
        } else {
            catalog2schemas = DatabaseContentRetriever.getSchemas(this.connection);
        }

        // ADD xqliu 2010-05-07 bug 9840
        boolean odbcIngres = this.connection.getMetaData().getDriverName().startsWith(DatabaseConstant.ODBC_INGRES_DRIVER_NAME);
        // ~9840

        // store schemas in catalogs
        Set<String> catNames = catalog2schemas.keySet();
        for (String catName : catNames) {
            List<Schema> schemas = catalog2schemas.get(catName);
            if (catName != null) { // a mapping between catalog and schema exist
                if (schemas != null) {
                    Catalog catalog = this.name2catalog.get(catName);
                    // MOD mzhao bug 8502 2009-10-28, filter user for MSSQL 2005 and 2008.
                    if (catalog != null && schemas != null) {
                        if (!(schemas.size() == 1 && schemas.get(0) == null)) {
                            CatalogHelper.addSchemas(schemas, catalog);
                        }
                    }
                }
            } else {
                // ADD xqliu 2010-04-21 bug 12452
                List<Schema> retrievedSchemas = new ArrayList<Schema>();
                for (Schema schema : schemas) {
                	// MOD xqliu 2010-05-07 bug 9840
                    if (retrieveCatalogSchema(dbName, schema.getName()) || odbcIngres) {
                        retrievedSchemas.add(schema);
                    }
                }
                // this.schemata.addAll(schemas);
                this.schemata.addAll(retrievedSchemas);
                // ~12452
                // MOD xqliu 2010-03-04 feature 11412
                // handle case when one catalog exist but no mapping between catalog and schemas exist (PostgreSQL)
                // MOD xqliu 2010-05-07 bug 9840
                if ((ConnectionUtils.isPostgresql(this.connection) && catNames.size() == 1 && this.name2catalog.size() == 1)
                        || odbcIngres) {
                    Catalog cat = this.name2catalog.values().iterator().next();
                    // MOD xqliu 2010-04-21 bug 12452
                    // CatalogHelper.addSchemas(schemas, cat);
                    CatalogHelper.addSchemas(retrievedSchemas, cat);
                    // ~12452
                }
                // ~11412
                // PTODO scorreia handle MS SQL schemata (dbo, root, guest) not related to catalogs.
            }
        }

        // set the flag to initialized and return the created catalog
        schemaInitialized = true;

    }

    /**
     * Method "createCatalog" creates a Catalog with the given name.
     * 
     * @param name the name of the catalog
     * @return the newly created catalog
     */
    private Catalog createOrUpdateCatalog(String name) {
        if (name == null) {
            return null;
        }
        Catalog cat = getOrCreateCatalog(name);

        // --- TODO set attributes of catalog
        return cat;
    }

    private Catalog getOrCreateCatalog(String name) {
        Catalog cat = (!this.catalogsToUpdate.isEmpty()) ? this.catalogsToUpdate.get(name) : null;
        if (cat != null) {
            return cat;
        }
        // else
        cat = CatalogHelper.createCatalog(name);
        return cat;
    }
}
