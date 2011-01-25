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
package org.talend.cwm.db.connection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.talend.core.model.metadata.IMetadataConnection;
import org.talend.core.model.metadata.MetadataFillFactory;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.metadata.builder.connection.MetadataTable;
import org.talend.core.model.metadata.builder.util.MetadataConnectionUtils;
import org.talend.cwm.relational.TdColumn;
import org.talend.cwm.relational.TdTable;
import org.talend.utils.properties.PropertiesLoader;
import org.talend.utils.properties.TypedProperties;
import org.talend.utils.sql.metadata.constants.TableType;
import orgomg.cwm.resource.relational.Catalog;

/**
 * DOC scorreia class global comment. Detailled comment
 */
public class DBConnectTest {

    protected static Logger log = Logger.getLogger(DBConnectTest.class);

    private static final Class<DBConnectTest> THAT = DBConnectTest.class;

    // private static DBConnect initDBConnect() {
    // // --- load connection parameters from properties file
    // TypedProperties connectionParams = PropertiesLoader.getProperties(THAT, "db.properties");
    // assertNotNull("No properties found", connectionParams);
    //
    // String driverClassName = connectionParams.getProperty("driver");
    // String dbUrl = connectionParams.getProperty("url");
    // assertNotNull("DB URL not found", dbUrl);
    // assertNotNull("No driver found", driverClassName);
    // return new DBConnect(dbUrl, driverClassName, connectionParams);
    // }

    /**
     * Test method for {@link org.talend.cwm.db.connection.DBConnect#getCatalogs()}.
     */
    // @Test
    // public void testGetCatalogs() {
    // DBConnect connect = initDBConnect();
    //
    // Collection<Catalog> cats = connect.getCatalogs();
    // assertNotNull(cats);
    // assertTrue(cats.isEmpty());
    //
    // try {
    // boolean ok = connect.retrieveDatabaseStructure();
    // assertFalse("Connection is not opened, we should not get the database structure.", ok);
    //
    // connect.connect();
    // ok = connect.retrieveDatabaseStructure();
    // assertTrue("We should have a connection", ok);
    //
    // cats = connect.getCatalogs();
    // assertNotNull(cats);
    //
    // assertFalse(supportCatalogs(connect) && cats.isEmpty());
    // } catch (SQLException e) {
    // Assert.fail("Got an SQL exception, check your connection parameters. This should not happen." + e);
    // }
    //
    // boolean closed = connect.closeConnection();
    // assertTrue("Connection is not closed", closed);
    // }
    //
    // /**
    // * DOC scorreia Comment method "supportCatalogs".
    // *
    // * @param connect
    // * @return
    // */
    // private boolean supportCatalogs(DBConnect connect) {
    // if (connect.getDatabaseUrl().toLowerCase().contains("oracle")) {
    // return false;
    // }
    // return true;
    // }

    /**
     * Test method for {@link org.talend.cwm.db.connection.DBConnect#getSchemata()}.
     */
    // @Test
    // public void testGetSchemata() {
    //
    // DBConnect connect = initDBConnect();
    //
    // Collection<Schema> schemata = connect.getSchemata();
    // assertNotNull(schemata);
    // assertTrue(schemata.isEmpty());
    //
    // try {
    // boolean ok = connect.retrieveDatabaseStructure();
    // assertFalse("Connection is not opened, we should not get the database structure.", ok);
    //
    // connect.connect();
    // ok = connect.retrieveDatabaseStructure();
    // assertTrue("We should have a connection", ok);
    //
    // schemata = connect.getSchemata();
    // assertNotNull(schemata);
    //
    // assertTrue("Schema do not exist in MySQL => list should be empty", schemata.isEmpty());
    // } catch (SQLException e) {
    // Assert.fail("Got an SQL exception, check your connection parameters. This should not happen." + e);
    // }
    //
    // boolean closed = connect.closeConnection();
    // assertTrue("Connection is not closed", closed);
    //
    // }

    /**
     * Test method for {@link org.talend.cwm.db.connection.DBConnect#getSoftwareSystem()}.
     */
    // @Test
    // public void testGetSoftwareSystem() {
    // DBConnect connect = initDBConnect();
    // try {
    //
    // TdSoftwareSystem softwareSystem = connect.getSoftwareSystem();
    // assertNull("Without connection, no software system should be retrieved", softwareSystem);
    //
    // boolean connected = connect.connect();
    // assertTrue(connected);
    //
    // softwareSystem = connect.getSoftwareSystem();
    // assertNull("Without retrieving informations, no software system should be retrieved", softwareSystem);
    //
    // boolean retrieved = connect.retrieveDeployedSystemInformations();
    // assertTrue(retrieved);
    //
    // softwareSystem = connect.getSoftwareSystem();
    // assertNotNull("should have got a software system", softwareSystem);
    //
    // } catch (SQLException e) {
    // fail("Exception should not happen" + e);
    // }
    //
    // boolean closed = connect.closeConnection();
    // assertTrue("Connection is not closed", closed);
    // }

    /**
     * Test method for {@link org.talend.cwm.db.connection.DBConnect#getTypeSystem()}.
     */
    @Test
    public void testGetTypeSystem() {
        fail("Not yet implemented");
    }

    /**
     * Test method for {@link org.talend.cwm.db.connection.DBConnect#saveInFiles()}.
     */
    @Test
    public void testSaveInFiles() {
        fail("Not yet implemented");
    }

    /**
     * Test method for
     * {@link org.talend.cwm.db.connection.DBConnect#storeInResourceSet(org.eclipse.emf.ecore.EObject, java.lang.String)}
     * .
     */
    @Test
    public void testStoreInResourceSet() {
        fail("Not yet implemented");
    }

    /**
     * Test method for {@link org.talend.cwm.db.connection.DBConnect#closeConnection()}.
     */
    // @Test
    // public void testCloseConnection() {
    // DBConnect dbConnect = initDBConnect();
    // boolean closed = dbConnect.closeConnection();
    // assertTrue(closed);
    //
    // boolean connected = false;
    // try {
    // connected = dbConnect.connect();
    // } catch (SQLException e) {
    // fail("Got an exception");
    // }
    // assertTrue(connected);
    //
    // closed = dbConnect.closeConnection();
    // assertTrue(closed);
    // }

    /**
     * Test method for {@link org.talend.cwm.db.connection.DBConnect#connect()}.
     */
    // @Test
    // public void testConnect() {
    // DBConnect dbConnect = initDBConnect();
    // try {
    // boolean connected = dbConnect.connect();
    // assertTrue("We should have a connection", connected);
    // } catch (SQLException e) {
    // Assert.fail("Got a exception during connection." + e);
    // }
    //
    // boolean closed = dbConnect.closeConnection();
    // assertTrue("Connection should be closed", closed);
    // }

    /**
     * Test method for {@link org.talend.cwm.db.connection.DBConnect#connectAndRetrieveInformations()}.
     */
    // @Test
    // public void testConnectAndRetrieveInformations() {
    // DBConnect connect = initDBConnect();
    // boolean ok = connect.connectAndRetrieveInformations();
    // assertTrue(ok);
    // connect.closeConnection();
    // }

    /**
     * Test method for {@link org.talend.cwm.db.connection.DBConnect#toString()}.
     */
    // @Test
    // public void testToString() {
    // DBConnect connect = initDBConnect();
    // System.out.println("Connection: " + connect);
    // }

    /**
     * Test method for {@link org.talend.cwm.db.connection.DBConnect#retrieveDatabaseStructure()}.
     */
    // @Test
    // public void testRetrieveDatabaseStructure() {
    // DBConnect connect = initDBConnect();
    // boolean ok = false;
    // try {
    // ok = connect.connect();
    // ok = ok && connect.retrieveDatabaseStructure();
    // } catch (SQLException e) {
    // log.error(e, e);
    // }
    // assertTrue(ok);
    // connect.closeConnection();
    // }

    /**
     * Test method for {@link org.talend.cwm.db.connection.DBConnect#retrieveDeployedSystemInformations()}.
     */
    // @Test
    // public void testRetrieveDeployedSystemInformations() {
    // DBConnect connect = initDBConnect();
    // boolean ok = false;
    // try {
    // ok = connect.connect();
    // ok = ok && connect.retrieveDeployedSystemInformations();
    // } catch (SQLException e) {
    // log.error(e, e);
    // }
    // assertTrue(ok);
    // connect.closeConnection();
    // }

    /**
     * Test method for
     * {@link org.talend.cwm.db.connection.ConnectionUtils#retrieveColumn(org.talend.core.model.metadata.builder.connection.MetadataTable)}
     * .
     */
    @Test
    public void testRetrieveColumn() {
        TypedProperties connectionParams = PropertiesLoader.getProperties(THAT, "db.properties");
        java.util.Iterator<Object> iter = connectionParams.keySet().iterator();
        Map<String, String> parameterMap = new HashMap<String, String>();
        while (iter.hasNext()) {
            String key = (String) iter.next();
            String value = connectionParams.get(key) == null ? null : connectionParams.get(key).toString();
            parameterMap.put(key, value);
        }

        List<String> sqlDataTypeList = new ArrayList<String>();
        IMetadataConnection metadataConn = MetadataFillFactory.getDBInstance().fillUIParams(parameterMap);
        java.sql.Connection sqlconn = MetadataConnectionUtils.checkConnection(metadataConn).getObject();
        Connection dbconn=MetadataFillFactory.getDBInstance().fillUIConnParams(metadataConn,null);
        DatabaseMetaData databaseMetaData=null;
        try {
            databaseMetaData = sqlconn.getMetaData();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        List<Catalog> catalogList=MetadataFillFactory.getDBInstance().fillCatalogs(dbconn, databaseMetaData, null);
        List<TdTable> tableList = MetadataFillFactory.getDBInstance().fillTables(catalogList.get(13), databaseMetaData, null,
                null,
                new String[] { TableType.TABLE.toString() });
        List<TdColumn> columnList = MetadataFillFactory.getDBInstance().fillColumns(tableList.get(2), databaseMetaData, null);

        if (tableList.size() <= 0) {
            fail("The table of db should have more than one.");
        }

        for (TdColumn tdColumn : columnList) {
            sqlDataTypeList.add(tdColumn.getSqlDataType().getName());
            tdColumn.setSqlDataType(null);
        }
        ConnectionUtils.retrieveColumn((MetadataTable) tableList.get(2));
        int i = 0;
        for (TdColumn tdColumn : columnList) {
            assertNotNull(tdColumn.getSqlDataType());
            assertEquals(sqlDataTypeList.get(i), tdColumn.getSqlDataType().getName());
            i++;
        }
    }

    private static boolean showUnimplemented = false;

    private void fail(String str) {
        if (showUnimplemented) {
            Assert.fail(str);
        }
    }
}
