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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.sql.SQLException;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.talend.cwm.softwaredeployment.TdSoftwareSystem;
import org.talend.utils.properties.PropertiesLoader;
import org.talend.utils.properties.TypedProperties;
import orgomg.cwm.resource.relational.Catalog;
import orgomg.cwm.resource.relational.Schema;

/**
 * DOC scorreia class global comment. Detailled comment
 */
public class DBConnectTest {

    protected static Logger log = Logger.getLogger(DBConnectTest.class);

    private static final Class<DBConnectTest> THAT = DBConnectTest.class;

    private static DBConnect initDBConnect() {
        // --- load connection parameters from properties file
        TypedProperties connectionParams = PropertiesLoader.getProperties(THAT, "db.properties");
        assertNotNull("No properties found", connectionParams);

        String driverClassName = connectionParams.getProperty("driver");
        String dbUrl = connectionParams.getProperty("url");
        assertNotNull("DB URL not found", dbUrl);
        assertNotNull("No driver found", driverClassName);
        return new DBConnect(dbUrl, driverClassName, connectionParams);
    }

    /**
     * Test method for {@link org.talend.cwm.db.connection.DBConnect#getCatalogs()}.
     */
    @Test
    public void testGetCatalogs() {
        DBConnect connect = initDBConnect();

        Collection<Catalog> cats = connect.getCatalogs();
        assertNotNull(cats);
        assertTrue(cats.isEmpty());

        try {
            boolean ok = connect.retrieveDatabaseStructure();
            assertFalse("Connection is not opened, we should not get the database structure.", ok);

            connect.connect();
            ok = connect.retrieveDatabaseStructure();
            assertTrue("We should have a connection", ok);

            cats = connect.getCatalogs();
            assertNotNull(cats);

            assertFalse(supportCatalogs(connect) && cats.isEmpty());
        } catch (SQLException e) {
            Assert.fail("Got an SQL exception, check your connection parameters. This should not happen." + e);
        }

        boolean closed = connect.closeConnection();
        assertTrue("Connection is not closed", closed);
    }

    /**
     * DOC scorreia Comment method "supportCatalogs".
     * 
     * @param connect
     * @return
     */
    private boolean supportCatalogs(DBConnect connect) {
        if (connect.getDatabaseUrl().toLowerCase().contains("oracle")) {
            return false;
        }
        return true;
    }

    /**
     * Test method for {@link org.talend.cwm.db.connection.DBConnect#getSchemata()}.
     */
    @Test
    public void testGetSchemata() {

        DBConnect connect = initDBConnect();

        Collection<Schema> schemata = connect.getSchemata();
        assertNotNull(schemata);
        assertTrue(schemata.isEmpty());

        try {
            boolean ok = connect.retrieveDatabaseStructure();
            assertFalse("Connection is not opened, we should not get the database structure.", ok);

            connect.connect();
            ok = connect.retrieveDatabaseStructure();
            assertTrue("We should have a connection", ok);

            schemata = connect.getSchemata();
            assertNotNull(schemata);

            assertTrue("Schema do not exist in MySQL => list should be empty", schemata.isEmpty());
        } catch (SQLException e) {
            Assert.fail("Got an SQL exception, check your connection parameters. This should not happen." + e);
        }

        boolean closed = connect.closeConnection();
        assertTrue("Connection is not closed", closed);

    }

    /**
     * Test method for {@link org.talend.cwm.db.connection.DBConnect#getSoftwareSystem()}.
     */
    @Test
    public void testGetSoftwareSystem() {
        DBConnect connect = initDBConnect();
        try {

            TdSoftwareSystem softwareSystem = connect.getSoftwareSystem();
            assertNull("Without connection, no software system should be retrieved", softwareSystem);

            boolean connected = connect.connect();
            assertTrue(connected);

            softwareSystem = connect.getSoftwareSystem();
            assertNull("Without retrieving informations, no software system should be retrieved", softwareSystem);

            boolean retrieved = connect.retrieveDeployedSystemInformations();
            assertTrue(retrieved);

            softwareSystem = connect.getSoftwareSystem();
            assertNotNull("should have got a software system", softwareSystem);

        } catch (SQLException e) {
            fail("Exception should not happen" + e);
        }

        boolean closed = connect.closeConnection();
        assertTrue("Connection is not closed", closed);
    }

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
    @Test
    public void testCloseConnection() {
        DBConnect dbConnect = initDBConnect();
        boolean closed = dbConnect.closeConnection();
        assertTrue(closed);

        boolean connected = false;
        try {
            connected = dbConnect.connect();
        } catch (SQLException e) {
            fail("Got an exception");
        }
        assertTrue(connected);

        closed = dbConnect.closeConnection();
        assertTrue(closed);
    }

    /**
     * Test method for {@link org.talend.cwm.db.connection.DBConnect#connect()}.
     */
    @Test
    public void testConnect() {
        DBConnect dbConnect = initDBConnect();
        try {
            boolean connected = dbConnect.connect();
            assertTrue("We should have a connection", connected);
        } catch (SQLException e) {
            Assert.fail("Got a exception during connection." + e);
        }

        boolean closed = dbConnect.closeConnection();
        assertTrue("Connection should be closed", closed);
    }

    /**
     * Test method for {@link org.talend.cwm.db.connection.DBConnect#connectAndRetrieveInformations()}.
     */
    @Test
    public void testConnectAndRetrieveInformations() {
        DBConnect connect = initDBConnect();
        boolean ok = connect.connectAndRetrieveInformations();
        assertTrue(ok);
        connect.closeConnection();
    }

    /**
     * Test method for {@link org.talend.cwm.db.connection.DBConnect#toString()}.
     */
    @Test
    public void testToString() {
        DBConnect connect = initDBConnect();
        System.out.println("Connection: " + connect);
    }

    /**
     * Test method for {@link org.talend.cwm.db.connection.DBConnect#retrieveDatabaseStructure()}.
     */
    @Test
    public void testRetrieveDatabaseStructure() {
        DBConnect connect = initDBConnect();
        boolean ok = false;
        try {
            ok = connect.connect();
            ok = ok && connect.retrieveDatabaseStructure();
        } catch (SQLException e) {
            log.error(e, e);
        }
        assertTrue(ok);
        connect.closeConnection();
    }

    /**
     * Test method for {@link org.talend.cwm.db.connection.DBConnect#retrieveDeployedSystemInformations()}.
     */
    @Test
    public void testRetrieveDeployedSystemInformations() {
        DBConnect connect = initDBConnect();
        boolean ok = false;
        try {
            ok = connect.connect();
            ok = ok && connect.retrieveDeployedSystemInformations();
        } catch (SQLException e) {
            log.error(e, e);
        }
        assertTrue(ok);
        connect.closeConnection();
    }

    private static boolean showUnimplemented = false;

    private void fail(String str) {
        if (showUnimplemented) {
            Assert.fail(str);
        }
    }
}
