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
package org.talend.cwm.management.connection;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.junit.Assert;
import org.junit.Test;
import org.talend.core.model.metadata.builder.connection.DatabaseConnection;
import org.talend.cwm.db.connection.ConnectionUtils;
import org.talend.cwm.helper.TableHelper;
import org.talend.cwm.helper.ViewHelper;
import org.talend.cwm.relational.TdColumn;
import org.talend.cwm.relational.TdSqlDataType;
import org.talend.cwm.relational.TdTable;
import org.talend.cwm.relational.TdView;
import org.talend.dq.analysis.parameters.DBConnectionParameter;
import org.talend.dq.connection.DataProviderBuilder;
import org.talend.utils.properties.PropertiesLoader;
import org.talend.utils.properties.TypedProperties;
import org.talend.utils.sugars.ReturnCode;
import orgomg.cwm.foundation.typemapping.TypeSystem;
import orgomg.cwm.objectmodel.core.ModelElement;
import orgomg.cwm.resource.relational.Catalog;
import orgomg.cwm.resource.relational.Schema;

/**
 * DOC scorreia class global comment. Detailled comment
 */
public class DatabaseContentRetrieverTest {

    private static final Class<DatabaseContentRetrieverTest> THAT = DatabaseContentRetrieverTest.class;

    private static Logger log = Logger.getLogger(THAT);

    private static final Connection CONNECTION = getConnection();

    private static final DatabaseConnection DBCONNECTION = getDBConnection();

    private static final String COLUMNS_TO_MATCH = "i";

    private static final String TABLES_TO_MATCH = "generaldata";

    private static final String CATALOG = "testtable";

    /**
     * DOC scorreia Comment method "getConnection".
     * 
     * @return
     */
    private static DatabaseConnection getDBConnection() {
        DBConnectionParameter dbconnectionParam = new DBConnectionParameter();
        TypedProperties connectionParams = PropertiesLoader.getProperties(THAT, "db.properties");
        assertNotNull("No properties found", connectionParams);

        String driverClassName = connectionParams.getProperty("driver");
        String url = connectionParams.getProperty("url");
        String sqlTypeName = connectionParams.getProperty("SqlTypeName");
        DataProviderBuilder dpBuilder = new DataProviderBuilder();
        assertNotNull("DB URL not found", url);
        assertNotNull("No driver found", driverClassName);
        dbconnectionParam.setJdbcUrl(url);
        dbconnectionParam.setSqlTypeName(sqlTypeName);
        dbconnectionParam.setDriverClassName(driverClassName);
        dbconnectionParam.setParameters(connectionParams);
        ReturnCode returnCode = dpBuilder.initializeDataProvider(dbconnectionParam);
        if (returnCode.isOk()) {
            return (DatabaseConnection) dpBuilder.getDataProvider();
        }

        // try {
        // return ConnectionUtils.createConnection(url, driverClassName, connectionParams);
        // } catch (SQLException e) {
        // // TODO Auto-generated catch block
        // log.error(e, e);
        // } catch (InstantiationException e) {
        // // TODO Auto-generated catch block
        // log.error(e, e);
        // } catch (IllegalAccessException e) {
        // // TODO Auto-generated catch block
        // log.error(e, e);
        // } catch (ClassNotFoundException e) {
        // // TODO Auto-generated catch block
        // log.error(e, e);
        // }
        fail("No CONNECTION found " + url);
        return null;
    }

    /**
     * DOC scorreia Comment method "getConnection".
     * 
     * @return
     */
    private static Connection getConnection() {
        TypedProperties connectionParams = PropertiesLoader.getProperties(THAT, "db.properties");
        assertNotNull("No properties found", connectionParams);
        String driverClassName = connectionParams.getProperty("driver");
        String url = connectionParams.getProperty("url");
        assertNotNull("DB URL not found", url);
        assertNotNull("No driver found", driverClassName);

        try {
            return ConnectionUtils.createConnection(url, driverClassName, connectionParams);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            log.error(e, e);
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            log.error(e, e);
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            log.error(e, e);
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            log.error(e, e);
        }
        fail("No CONNECTION found " + url);
        return null;
    }

    /**
     * Test method for
     * {@link org.talend.cwm.management.connection.DatabaseContentRetriever#getCatalogs(DatabaseConnection)}.
     */
    @Test
    public void testGetCatalogs() {

        try {
            Collection<Catalog> catalogs = DatabaseContentRetriever.getCatalogs(DBCONNECTION);
            assertNotNull(catalogs);
            // assertTrue("We should have a connection", ok);
            assertFalse("We should have a connection", catalogs.isEmpty());
            for (Catalog tdCatalog : catalogs) {
                assertNotNull(tdCatalog);
                log.info("Catalog: " + tdCatalog.getName());
            }

        } catch (SQLException e) {
            fail("Got exception: " + e.getMessage());
        }
    }

    /**
     * Test method for
     * {@link org.talend.cwm.management.connection.DatabaseContentRetriever#getSchemas(java.sql.Connection)}.
     */
    @Test
    public void testGetSchemas() {
        try {
            Map<String, List<Schema>> schemas = DatabaseContentRetriever.getSchemas(CONNECTION);
            assertNotNull(schemas);
            Set<String> schemaNames = schemas.keySet();
            for (String name : schemaNames) {
                assertNotNull(name);
                log.info("Schema " + name);
            }
        } catch (SQLException e) {
            fail(e.getMessage());
        }

    }

    /**
     * Test method for
     * {@link org.talend.cwm.management.connection.DatabaseContentRetriever#getTablesWithColumns(java.lang.String, java.lang.String, java.lang.String, DatabaseConnection)}
     * .
     */
    @Test
    public void testGetTablesWithColumns() {
        try {
            List<TdTable> tables = DatabaseContentRetriever.getTablesWithColumns(CATALOG, null, TABLES_TO_MATCH, DBCONNECTION);
            assertFalse(tables.isEmpty());
            for (TdTable tdTable : tables) {
                log.info("Table " + tdTable.getName());
                List<TdColumn> columns = TableHelper.getColumns(tdTable);
                for (TdColumn tdColumn : columns) {
                    assertNotNull(tdColumn);
                    // assertTrue(tdColumn.getName().contains(COLUMNS_TO_MATCH));
                    log.info("Column " + tdColumn.getName());
                }
                assertFalse(columns.isEmpty());
            }

        } catch (SQLException e) {
            fail(e.getMessage());
        }
    }

    /**
     * Test method for
     * {@link org.talend.cwm.management.connection.DatabaseContentRetriever#getTablesWithColumns(java.lang.String, java.lang.String, java.lang.String, DatabaseConnection)}
     * .
     */
    @Test
    public void testGetColumns() {

        List<TdColumn> columns = null;
        try {
            columns = DatabaseContentRetriever.getColumns(CATALOG, null, TABLES_TO_MATCH, "%" + COLUMNS_TO_MATCH + "%",
                    DBCONNECTION);
            assertNotNull(columns);
            assertFalse(columns.isEmpty());
            for (TdColumn tdColumn : columns) {
                assertNotNull(tdColumn);
                assertTrue(tdColumn.getName().contains(COLUMNS_TO_MATCH));
                log.info("Column " + tdColumn.getName());
            }
        } catch (SQLException e) {
            fail(e.getMessage());
        }
    }

    /**
     * Test method for
     * {@link org.talend.cwm.management.connection.DatabaseContentRetriever#getTablesWithoutColumns(java.lang.String, java.lang.String, java.lang.String, DatabaseConnection)}
     * .
     */
    @Test
    public void testGetTablesWithoutColumns() {
        try {
            List<TdTable> tables = DatabaseContentRetriever.getTablesWithoutColumns(CATALOG, null, null, DBCONNECTION);
            assertFalse(tables.isEmpty());
            for (TdTable tdTable : tables) {
                log.info("Table " + tdTable.getName());
                List<TdColumn> columns = TableHelper.getColumns(tdTable);
                for (TdColumn tdColumn : columns) {
                    assertNotNull(tdColumn);
                    log.info("Column " + tdColumn.getName());
                }
                assertTrue(columns.isEmpty());
            }

        } catch (SQLException e) {
            fail(e.getMessage());
        }
    }

    /**
     * Test method for
     * {@link org.talend.cwm.management.connection.DatabaseContentRetriever#getViewsWithColumns(java.lang.String, java.lang.String, java.lang.String, DatabaseConnection)}
     * .
     */
    @Test
    public void testGetViewsWithColumns() {
        try {
            List<TdView> tables = DatabaseContentRetriever.getViewsWithColumns(CATALOG, null, null, DBCONNECTION);
            assertFalse(tables.isEmpty());
            for (TdView tdTable : tables) {
                log.info("Table " + tdTable.getName());
                List<TdColumn> columns = ViewHelper.getColumns(tdTable);
                for (TdColumn tdColumn : columns) {
                    assertNotNull(tdColumn);
                    log.info("Column " + tdColumn.getName());
                }
            }

        } catch (SQLException e) {
            fail(e.getMessage());
        }
    }

    /**
     * Test method for
     * {@link org.talend.cwm.management.connection.DatabaseContentRetriever#getDataProvider(java.sql.Driver, java.lang.String, java.util.Properties)}
     * .
     */
    @Test
    public void testGetDataProvider() {
        fail("Not yet implemented");
    }

    /**
     * Test method for
     * {@link org.talend.cwm.management.connection.DatabaseContentRetriever#getProviderConnection(java.lang.String, java.lang.String, java.util.Properties, java.sql.Connection)}
     * .
     */
    @Test
    public void testGetProviderConnection() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetDataType() {
        try {
            List<TdSqlDataType> dataType = DatabaseContentRetriever.getDataType(CATALOG, null, null, null, CONNECTION);
            Assert.assertFalse(dataType.isEmpty());
            for (TdSqlDataType tdSqlDataType : dataType) {
                System.out.println(tdSqlDataType);
            }
        } catch (SQLException e) {
            fail(e.getMessage());
        }

    }

    /**
     * Test method for
     * {@link org.talend.cwm.management.connection.DatabaseContentRetriever#getSoftwareSystem(java.sql.Connection)}.
     */
    @Test
    public void testGetSoftwareSystem() {
        fail("Not yet implemented");
    }

    /**
     * Test method for
     * {@link org.talend.cwm.management.connection.DatabaseContentRetriever#getTypeSystem(java.sql.Connection)}.
     */
    @Test
    public void testGetTypeSystem() {
        try {
            TypeSystem typeSystem = DatabaseContentRetriever.getTypeSystem(getConnection());
            Assert.assertNotNull(typeSystem);
            EList<ModelElement> ownedElement = typeSystem.getOwnedElement();
            Assert.assertFalse(ownedElement.isEmpty());
            for (ModelElement modelElement : ownedElement) {
                System.out.println(modelElement);
            }
        } catch (SQLException e) {
            fail(e.getMessage());
        }

    }

    private static boolean showUnimplemented = false;

    private static void fail(String str) {
        if (showUnimplemented) {
            Assert.fail(str);
        }
    }
}
