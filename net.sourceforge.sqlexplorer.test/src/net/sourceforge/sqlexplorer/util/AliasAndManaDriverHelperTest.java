// ============================================================================
//
// Copyright (C) 2006-2014 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package net.sourceforge.sqlexplorer.util;

import static org.junit.Assert.*;
import net.sourceforge.sqlexplorer.dbproduct.ManagedDriver;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.talend.core.model.metadata.builder.connection.ConnectionFactory;
import org.talend.core.model.metadata.builder.connection.DatabaseConnection;

/**
 * created by talend2 on 2014-5-12 Detailled comment
 * 
 */
public class AliasAndManaDriverHelperTest {

    AliasAndManaDriverHelper aliasManaDriverHelper = AliasAndManaDriverHelper.getInstance();

    /**
     * DOC talend2 Comment method "setUp".
     * 
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }

    /**
     * DOC talend2 Comment method "tearDown".
     * 
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test method for
     * {@link net.sourceforge.sqlexplorer.util.AliasAndManaDriverHelper#createNewManagerDriver(org.talend.core.model.metadata.builder.connection.DatabaseConnection)}
     * .
     * 
     * @throws Exception
     */
    @Test
    public void testCreateNewManagerDriver() throws Exception {

        DatabaseConnection dbConn = ConnectionFactory.eINSTANCE.createDatabaseConnection();
        dbConn.setName("conn"); //$NON-NLS-1$
        dbConn.setDatabaseType("MySQL"); //$NON-NLS-1$
        dbConn.setDriverClass("org.gjt.mm.mysql.Driver"); //$NON-NLS-1$
        dbConn.setVersion("MYSQL_5"); //$NON-NLS-1$
        dbConn.setURL("jdbc:mysql://localhost:3306/?noDatetimeStringSync=true"); //$NON-NLS-1$
        ManagedDriver manaDriver = aliasManaDriverHelper.createNewManagerDriver(dbConn);
        assertNotNull(manaDriver);
    }

    /**
     * Test method for
     * {@link net.sourceforge.sqlexplorer.util.AliasAndManaDriverHelper#getManaDriverByDriverClass(org.talend.core.model.metadata.builder.connection.Connection)}
     * .
     * 
     * @throws Exception
     */
    @Test
    public void testGetManaDriverByConnection() throws Exception {
        DatabaseConnection dbConn = ConnectionFactory.eINSTANCE.createDatabaseConnection();
        dbConn.setName("dbOracle"); //$NON-NLS-1$
        dbConn.setDatabaseType("Oracle with SID"); //$NON-NLS-1$
        dbConn.setDriverClass("oracle.jdbc.OracleDriver"); //$NON-NLS-1$
        dbConn.setVersion("ORACLE_11"); //$NON-NLS-1$
        dbConn.setURL("jdbc:oracle:thin:@192.168.31.39:1521:root"); //$NON-NLS-1$
        ManagedDriver manaDriver = aliasManaDriverHelper.getManaDriverByConnection(dbConn);
        assertNull(manaDriver);
        manaDriver = aliasManaDriverHelper.createNewManagerDriver(dbConn);
        assertNotNull(manaDriver);
    }

    /**
     * Test method for
     * {@link net.sourceforge.sqlexplorer.util.AliasAndManaDriverHelper#joinManagedDriverId(org.talend.core.model.metadata.builder.connection.DatabaseConnection)}
     * .
     */
    @Test
    public void testJoinManagedDriverIdDatabaseConnection() {
        DatabaseConnection dbConn = ConnectionFactory.eINSTANCE.createDatabaseConnection();
        dbConn.setName("conn"); //$NON-NLS-1$
        dbConn.setDatabaseType("MySQL"); //$NON-NLS-1$
        dbConn.setDriverClass("org.gjt.mm.mysql.Driver"); //$NON-NLS-1$
        dbConn.setVersion("MYSQL_5"); //$NON-NLS-1$
        dbConn.setURL("jdbc:mysql://localhost:3306/?noDatetimeStringSync=true"); //$NON-NLS-1$
        String joinManagedDriverId = aliasManaDriverHelper.joinManagedDriverId(dbConn);
        assertEquals(joinManagedDriverId, "MySQL:org.gjt.mm.mysql.Driver:MYSQL_5"); //$NON-NLS-1$
    }

    /**
     * Test method for
     * {@link net.sourceforge.sqlexplorer.util.AliasAndManaDriverHelper#joinManagedDriverId(java.lang.String, java.lang.String, java.lang.String)}
     * .
     */
    @Test
    public void testJoinManagedDriverIdStringStringString() {
        String type = "MySQL"; //$NON-NLS-1$
        String driver = "org.gjt.mm.mysql.Driver"; //$NON-NLS-1$
        String version = "MYSQL_5"; //$NON-NLS-1$
        String joinManagedDriverId = aliasManaDriverHelper.joinManagedDriverId(type, driver, version);
        assertEquals(joinManagedDriverId, "MySQL:org.gjt.mm.mysql.Driver:MYSQL_5"); //$NON-NLS-1$
    }
}
