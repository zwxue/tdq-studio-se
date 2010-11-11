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

import static org.junit.Assert.assertNotNull;

import java.sql.SQLException;

import org.junit.Assert;
import org.junit.Test;
import org.talend.core.database.EDatabaseTypeName;
import org.talend.core.model.metadata.builder.connection.DatabaseConnection;
import org.talend.cwm.exception.TalendException;
import org.talend.dq.analysis.parameters.DBConnectionParameter;
import org.talend.utils.properties.PropertiesLoader;
import org.talend.utils.properties.TypedProperties;
import orgomg.cwm.resource.relational.Catalog;


/**
 * DOC zshen  class global comment. Detailled comment
 */
public class TalendCwmFactoryTest {

    private static DBConnect initDBConnect() {
        // --- load connection parameters from properties file
        TypedProperties connectionParams = PropertiesLoader.getProperties(TalendCwmFactoryTest.class, "db.properties");
        assertNotNull("No properties found", connectionParams);

        String driverClassName = connectionParams.getProperty("driver");
        String dbUrl = connectionParams.getProperty("url");
        assertNotNull("DB URL not found", dbUrl);
        assertNotNull("No driver found", driverClassName);

        return new DBConnect(initDbParameter());
    }

    private static DBConnectionParameter initDbParameter() {
        DBConnectionParameter dbParameter = new DBConnectionParameter();
        TypedProperties connectionParams = PropertiesLoader.getProperties(TalendCwmFactoryTest.class, "db.properties");
        dbParameter.setaDDParameter(connectionParams.getProperty("aDDParameter"));
        dbParameter.setAuthor(connectionParams.getProperty("author"));
        dbParameter.setDbName("");
        dbParameter.setDriverClassName(connectionParams.getProperty("driver"));
        dbParameter.setHost(connectionParams.getProperty("host"));
        dbParameter.setParameters(connectionParams);
        dbParameter.setJdbcUrl(connectionParams.getProperty("url"));
        dbParameter.setName(connectionParams.getProperty("name"));
        dbParameter.setPort(connectionParams.getProperty("port"));
        dbParameter.setSqlTypeName(connectionParams.getProperty("SqlTypeName"));
        dbParameter.setStatus(connectionParams.getProperty("status"));
        return dbParameter;
    }
    /**
     * Test method for {@link org.talend.cwm.db.connection.TalendCwmFactory#createDataProvider(org.talend.cwm.db.connection.DBConnect)}.
     */
    @Test
    public void testCreateDataProvider() {
        DBConnect dbConnect = initDBConnect();
        DatabaseConnection connection = null;
        try {
            connection = (DatabaseConnection)TalendCwmFactory.createDataProvider(dbConnect);
        } catch (SQLException e) {
            Assert.fail("Got an SQL exception, check your connection parameters. This should not happen." + e);
        } catch (TalendException e) {
            Assert.fail("Got an TalendException, check your connection parameters. This should not happen." + e);
        }
        String dbVersion = connection.getDbVersionString();
        assertNotNull("DBVersion is not null", dbVersion);
        assertNotNull("AdditionalParams is not null", connection.getAdditionalParams());
        assertNotNull("DatabaseType is not null", connection.getDatabaseType());
        if (EDatabaseTypeName.getTypeFromDisplayName(connection.getDatabaseType()).isNeedSchema()) {
        assertNotNull("UiSchema is not null", connection.getUiSchema());
        }
        if (connection.getDataPackage().get(0) instanceof Catalog) {
            assertNotNull("SID is not null", connection.getSID());
        }
        assertNotNull("ProductId is not null", connection.getProductId());
        assertNotNull("DbmsId is not null", connection.getDbmsId());
        // fail("Not yet implemented");
    }

}
