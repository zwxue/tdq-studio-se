// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dq.indicators;

import java.sql.SQLException;

import junit.framework.Assert;

import org.junit.Test;
import org.mockito.Mockito;
import org.talend.core.model.metadata.builder.connection.ConnectionFactory;
import org.talend.core.model.metadata.builder.connection.DatabaseConnection;
import org.talend.core.model.metadata.builder.database.dburl.SupportDBUrlType;
import org.talend.cwm.management.i18n.Messages;
import org.talend.dataquality.indicators.schema.CatalogIndicator;
import org.talend.dataquality.indicators.schema.SchemaFactory;
import org.talend.dataquality.indicators.schema.SchemaIndicator;
import org.talend.dq.dbms.DbmsLanguageFactory;
import org.talend.utils.sugars.ReturnCode;
import orgomg.cwm.resource.relational.Catalog;
import orgomg.cwm.resource.relational.RelationalFactory;
import orgomg.cwm.resource.relational.Schema;

import com.mysql.jdbc.DatabaseMetaData;

/**
 * created by talend on Apr 8, 2014 Detailled comment
 * 
 */
public class SchemaEvaluatorTest {

    /**
     * Test method for {@link org.talend.dq.indicators.SchemaEvaluator#executeSqlQuery(java.lang.String)}.
     * 
     * case 1: normal case
     * 
     * @throws SQLException
     */
    @Test
    public void testExecuteSqlQueryCase1() throws SQLException {
        // database connection
        DatabaseConnection createDatabaseConnection = ConnectionFactory.eINSTANCE.createDatabaseConnection();

        // schema
        Schema createSchema = RelationalFactory.eINSTANCE.createSchema();
        createSchema.setName("schema1"); //$NON-NLS-1$

        // schema indicator
        SchemaIndicator createSchemaIndicator = SchemaFactory.eINSTANCE.createSchemaIndicator();
        createSchemaIndicator.setAnalyzedElement(createSchema);

        // mock DatabaseMetaData
        DatabaseMetaData databaseMetadata = Mockito.mock(DatabaseMetaData.class);
        Mockito.when(databaseMetadata.getDatabaseProductName()).thenReturn(SupportDBUrlType.MYSQLDEFAULTURL.getDBKey());
        Mockito.when(databaseMetadata.getDriverName()).thenReturn(SupportDBUrlType.MYSQLDEFAULTURL.getDbDriver());

        // mock java.sql.connection
        java.sql.Connection connection = Mockito.mock(java.sql.Connection.class);
        Mockito.when(connection.getMetaData()).thenReturn(databaseMetadata);
        Mockito.doNothing().when(connection).setCatalog(Mockito.anyString());

        SchemaEvaluator realSchemaEval = new SchemaEvaluator();
        SchemaEvaluator mockSchemaEval = Mockito.spy(realSchemaEval);
        mockSchemaEval.setConnection(connection);
        Mockito.doNothing()
                .when(mockSchemaEval)
                .evalSchemaIndicLow((CatalogIndicator) Mockito.any(), (SchemaIndicator) Mockito.any(), (Catalog) Mockito.any(),
                        (Schema) Mockito.any(), (ReturnCode) Mockito.any());

        Mockito.doReturn(createDatabaseConnection).when(mockSchemaEval).getDataManager();
        Mockito.doReturn(true).when(mockSchemaEval).checkSchema(createSchema);
        Mockito.doReturn(DbmsLanguageFactory.createDbmsLanguage(SupportDBUrlType.MYSQLDEFAULTURL)).when(mockSchemaEval).dbms();

        mockSchemaEval.storeIndicator(createSchema, createSchemaIndicator);
        ReturnCode executeSqlReturn = mockSchemaEval.executeSqlQuery(""); //$NON-NLS-1$
        Assert.assertTrue("execute sql query fail", executeSqlReturn.isOk()); //$NON-NLS-1$
        Assert.assertNull(executeSqlReturn.getMessage());

        // dbms is mysql database() so that connection.setCatalog method will be call at last once
        Mockito.verify(connection, Mockito.atLeastOnce()).setCatalog(Mockito.anyString());
    }

    /**
     * Test method for {@link org.talend.dq.indicators.SchemaEvaluator#executeSqlQuery(java.lang.String)}.
     * 
     * case 2: elementToIndicators is empty
     * 
     * @throws SQLException
     */
    @Test
    public void testExecuteSqlQueryCase2() throws SQLException {
        // database connection
        DatabaseConnection createDatabaseConnection = ConnectionFactory.eINSTANCE.createDatabaseConnection();

        // schema
        Schema createSchema = RelationalFactory.eINSTANCE.createSchema();
        createSchema.setName("schema1"); //$NON-NLS-1$

        // schema indicator
        SchemaIndicator createSchemaIndicator = SchemaFactory.eINSTANCE.createSchemaIndicator();
        createSchemaIndicator.setAnalyzedElement(createSchema);

        // mock DatabaseMetaData
        DatabaseMetaData databaseMetadata = Mockito.mock(DatabaseMetaData.class);
        Mockito.when(databaseMetadata.getDatabaseProductName()).thenReturn(SupportDBUrlType.MYSQLDEFAULTURL.getDBKey());
        Mockito.when(databaseMetadata.getDriverName()).thenReturn(SupportDBUrlType.MYSQLDEFAULTURL.getDbDriver());

        // mock java.sql.connection
        java.sql.Connection connection = Mockito.mock(java.sql.Connection.class);
        Mockito.when(connection.getMetaData()).thenReturn(databaseMetadata);
        Mockito.doNothing().when(connection).setCatalog(Mockito.anyString());

        SchemaEvaluator realSchemaEval = new SchemaEvaluator();
        SchemaEvaluator mockSchemaEval = Mockito.spy(realSchemaEval);
        mockSchemaEval.setConnection(connection);
        Mockito.doNothing()
                .when(mockSchemaEval)
                .evalSchemaIndicLow((CatalogIndicator) Mockito.any(), (SchemaIndicator) Mockito.any(), (Catalog) Mockito.any(),
                        (Schema) Mockito.any(), (ReturnCode) Mockito.any());

        Mockito.doReturn(createDatabaseConnection).when(mockSchemaEval).getDataManager();
        Mockito.doReturn(true).when(mockSchemaEval).checkSchema(createSchema);
        Mockito.doReturn(DbmsLanguageFactory.createDbmsLanguage(SupportDBUrlType.MYSQLDEFAULTURL)).when(mockSchemaEval).dbms();

        ReturnCode executeSqlReturn = mockSchemaEval.executeSqlQuery(""); //$NON-NLS-1$
        Assert.assertFalse("execute sql query success", executeSqlReturn.isOk()); //$NON-NLS-1$
        Assert.assertEquals(Messages.getString("Evaluator.NoInidcator1"), executeSqlReturn.getMessage()); //$NON-NLS-1$

    }

    /**
     * Test method for {@link org.talend.dq.indicators.SchemaEvaluator#executeSqlQuery(java.lang.String)}.
     * 
     * case 3: allIndicators is empty
     * 
     * @throws SQLException
     */
    @Test
    public void testExecuteSqlQueryCase3() throws SQLException {
        // database connection
        DatabaseConnection createDatabaseConnection = ConnectionFactory.eINSTANCE.createDatabaseConnection();

        // schema
        Schema createSchema = RelationalFactory.eINSTANCE.createSchema();
        createSchema.setName("schema1"); //$NON-NLS-1$

        // schema indicator
        SchemaIndicator createSchemaIndicator = SchemaFactory.eINSTANCE.createSchemaIndicator();
        createSchemaIndicator.setAnalyzedElement(createSchema);

        // mock DatabaseMetaData
        DatabaseMetaData databaseMetadata = Mockito.mock(DatabaseMetaData.class);
        Mockito.when(databaseMetadata.getDatabaseProductName()).thenReturn(SupportDBUrlType.MYSQLDEFAULTURL.getDBKey());
        Mockito.when(databaseMetadata.getDriverName()).thenReturn(SupportDBUrlType.MYSQLDEFAULTURL.getDbDriver());

        // mock java.sql.connection
        java.sql.Connection connection = Mockito.mock(java.sql.Connection.class);
        Mockito.when(connection.getMetaData()).thenReturn(databaseMetadata);
        Mockito.doNothing().when(connection).setCatalog(Mockito.anyString());

        SchemaEvaluator realSchemaEval = new SchemaEvaluator();
        SchemaEvaluator mockSchemaEval = Mockito.spy(realSchemaEval);
        mockSchemaEval.setConnection(connection);
        Mockito.doNothing()
                .when(mockSchemaEval)
                .evalSchemaIndicLow((CatalogIndicator) Mockito.any(), (SchemaIndicator) Mockito.any(), (Catalog) Mockito.any(),
                        (Schema) Mockito.any(), (ReturnCode) Mockito.any());

        Mockito.doReturn(createDatabaseConnection).when(mockSchemaEval).getDataManager();
        Mockito.doReturn(true).when(mockSchemaEval).checkSchema(createSchema);
        Mockito.doReturn(DbmsLanguageFactory.createDbmsLanguage(SupportDBUrlType.MYSQLDEFAULTURL)).when(mockSchemaEval).dbms();

        mockSchemaEval.storeIndicator(createSchema, createSchemaIndicator);
        mockSchemaEval.allIndicators.clear();
        ReturnCode executeSqlReturn = mockSchemaEval.executeSqlQuery(""); //$NON-NLS-1$
        Assert.assertFalse("execute sql query success", executeSqlReturn.isOk()); //$NON-NLS-1$
        Assert.assertEquals(Messages.getString("Evaluator.NoInidcator2", createDatabaseConnection), executeSqlReturn.getMessage()); //$NON-NLS-1$

    }

    /**
     * Test method for {@link org.talend.dq.indicators.SchemaEvaluator#executeSqlQuery(java.lang.String)}.
     * 
     * case 4: checkSchema is false
     * 
     * @throws SQLException
     */
    @Test
    public void testExecuteSqlQueryCase4() throws SQLException {
        // database connection
        DatabaseConnection createDatabaseConnection = ConnectionFactory.eINSTANCE.createDatabaseConnection();

        // schema
        Schema createSchema = RelationalFactory.eINSTANCE.createSchema();
        createSchema.setName("schema1"); //$NON-NLS-1$

        // schema indicator
        SchemaIndicator createSchemaIndicator = SchemaFactory.eINSTANCE.createSchemaIndicator();
        createSchemaIndicator.setAnalyzedElement(createSchema);

        // mock DatabaseMetaData
        DatabaseMetaData databaseMetadata = Mockito.mock(DatabaseMetaData.class);
        Mockito.when(databaseMetadata.getDatabaseProductName()).thenReturn(SupportDBUrlType.MYSQLDEFAULTURL.getDBKey());
        Mockito.when(databaseMetadata.getDriverName()).thenReturn(SupportDBUrlType.MYSQLDEFAULTURL.getDbDriver());

        // mock java.sql.connection
        java.sql.Connection connection = Mockito.mock(java.sql.Connection.class);
        Mockito.when(connection.getMetaData()).thenReturn(databaseMetadata);
        Mockito.doNothing().when(connection).setCatalog(Mockito.anyString());

        SchemaEvaluator realSchemaEval = new SchemaEvaluator();
        SchemaEvaluator mockSchemaEval = Mockito.spy(realSchemaEval);
        mockSchemaEval.setConnection(connection);
        Mockito.doNothing()
                .when(mockSchemaEval)
                .evalSchemaIndicLow((CatalogIndicator) Mockito.any(), (SchemaIndicator) Mockito.any(), (Catalog) Mockito.any(),
                        (Schema) Mockito.any(), (ReturnCode) Mockito.any());

        Mockito.doReturn(createDatabaseConnection).when(mockSchemaEval).getDataManager();
        Mockito.doReturn(false).when(mockSchemaEval).checkSchema(createSchema);
        Mockito.doReturn(DbmsLanguageFactory.createDbmsLanguage(SupportDBUrlType.MYSQLDEFAULTURL)).when(mockSchemaEval).dbms();

        mockSchemaEval.storeIndicator(createSchema, createSchemaIndicator);
        ReturnCode executeSqlReturn = mockSchemaEval.executeSqlQuery(""); //$NON-NLS-1$
        Assert.assertFalse("execute sql query success", executeSqlReturn.isOk()); //$NON-NLS-1$
        Assert.assertEquals(Messages.getString("Evaluator.schemaNotExist", createSchema.getName()), executeSqlReturn.getMessage()); //$NON-NLS-1$

    }

    /**
     * Test method for {@link org.talend.dq.indicators.SchemaEvaluator#executeSqlQuery(java.lang.String)}.
     * 
     * case 5: current dbms support Catalog Selection
     * 
     * @throws SQLException
     */
    @Test
    public void testExecuteSqlQueryCase5() throws SQLException {
        // database connection
        DatabaseConnection createDatabaseConnection = ConnectionFactory.eINSTANCE.createDatabaseConnection();

        // schema
        Schema createSchema = RelationalFactory.eINSTANCE.createSchema();
        createSchema.setName("schema1"); //$NON-NLS-1$

        // schema indicator
        SchemaIndicator createSchemaIndicator = SchemaFactory.eINSTANCE.createSchemaIndicator();
        createSchemaIndicator.setAnalyzedElement(createSchema);

        // mock DatabaseMetaData
        DatabaseMetaData databaseMetadata = Mockito.mock(DatabaseMetaData.class);
        Mockito.when(databaseMetadata.getDatabaseProductName()).thenReturn(SupportDBUrlType.HIVEDEFAULTURL.getDBKey());
        Mockito.when(databaseMetadata.getDriverName()).thenReturn(SupportDBUrlType.HIVEDEFAULTURL.getDbDriver());

        // mock java.sql.connection
        java.sql.Connection connection = Mockito.mock(java.sql.Connection.class);
        Mockito.when(connection.getMetaData()).thenReturn(databaseMetadata);
        Mockito.doNothing().when(connection).setCatalog(Mockito.anyString());

        SchemaEvaluator realSchemaEval = new SchemaEvaluator();
        SchemaEvaluator mockSchemaEval = Mockito.spy(realSchemaEval);
        mockSchemaEval.setConnection(connection);
        Mockito.doNothing()
                .when(mockSchemaEval)
                .evalSchemaIndicLow((CatalogIndicator) Mockito.any(), (SchemaIndicator) Mockito.any(), (Catalog) Mockito.any(),
                        (Schema) Mockito.any(), (ReturnCode) Mockito.any());

        Mockito.doReturn(createDatabaseConnection).when(mockSchemaEval).getDataManager();
        Mockito.doReturn(true).when(mockSchemaEval).checkSchema(createSchema);
        Mockito.doReturn(DbmsLanguageFactory.createDbmsLanguage(SupportDBUrlType.HIVEDEFAULTURL)).when(mockSchemaEval).dbms();

        mockSchemaEval.storeIndicator(createSchema, createSchemaIndicator);
        ReturnCode executeSqlReturn = mockSchemaEval.executeSqlQuery(""); //$NON-NLS-1$
        Assert.assertTrue("execute sql query fail", executeSqlReturn.isOk()); //$NON-NLS-1$
        Assert.assertNull(executeSqlReturn.getMessage());

        // dbms is hive database() so that connection.setCatalog method will not be call
        Mockito.verify(connection, Mockito.never()).setCatalog(Mockito.anyString());
    }
}
