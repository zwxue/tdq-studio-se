// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
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

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import static org.powermock.api.support.membermodification.MemberMatcher.*;
import static org.powermock.api.support.membermodification.MemberModifier.*;

import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.talend.core.database.EDatabaseTypeName;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.metadata.builder.connection.ConnectionFactory;
import org.talend.core.model.metadata.builder.connection.DatabaseConnection;
import org.talend.core.model.metadata.builder.connection.MetadataTable;
import org.talend.core.model.metadata.builder.database.ExtractMetaDataUtils;
import org.talend.core.model.metadata.builder.database.JavaSqlFactory;
import org.talend.core.model.metadata.builder.database.dburl.SupportDBUrlType;
import org.talend.core.model.metadata.builder.util.MetadataConnectionUtils;
import org.talend.cwm.helper.CatalogHelper;
import org.talend.cwm.helper.ColumnSetHelper;
import org.talend.cwm.helper.ConnectionHelper;
import org.talend.cwm.helper.SchemaHelper;
import org.talend.cwm.management.i18n.Messages;
import org.talend.cwm.relational.TdColumn;
import org.talend.cwm.relational.TdSqlDataType;
import org.talend.cwm.relational.TdTable;
import org.talend.cwm.relational.impl.TdColumnImpl;
import org.talend.dq.writer.impl.DataProviderWriter;
import org.talend.dq.writer.impl.ElementWriterFactory;
import org.talend.utils.sugars.ReturnCode;
import org.talend.utils.sugars.TypedReturnCode;
import orgomg.cwm.resource.relational.Catalog;
import orgomg.cwm.resource.relational.ColumnSet;
import orgomg.cwm.resource.relational.Schema;
import orgomg.cwm.resource.relational.impl.CatalogImpl;
import orgomg.cwm.resource.relational.impl.SchemaImpl;

/**
 * DOC msjian class global comment. Detailled comment
 */
@PrepareForTest({ ConnectionUtils.class, ColumnSetHelper.class, ConnectionHelper.class, JavaSqlFactory.class,
        CatalogHelper.class, SchemaHelper.class, org.talend.utils.sql.ConnectionUtils.class, ElementWriterFactory.class,
        Messages.class, ExtractMetaDataUtils.class, MetadataConnectionUtils.class })
public class ConnectionUtilsTest {

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    /**
     * Test method for
     * {@link org.talend.cwm.db.connection.ConnectionUtils#retrieveColumn(org.talend.core.model.metadata.builder.connection.MetadataTable)}
     * .
     */
    @Test
    public void testRetrieveColumn() {
        ColumnSet tdTable = mock(TdTable.class);
        when(tdTable.getName()).thenReturn("testTableName"); //$NON-NLS-1$

        PowerMockito.mockStatic(ColumnSetHelper.class);
        List<TdColumn> columnList = new ArrayList<TdColumn>();
        TdColumnImpl testColumn = mock(TdColumnImpl.class);
        when(testColumn.getName()).thenReturn("testColumnName"); //$NON-NLS-1$
        columnList.add(testColumn);
        when(ColumnSetHelper.getColumns(tdTable)).thenReturn(columnList);

        Connection tempConnection = mock(Connection.class);
        PowerMockito.mockStatic(ConnectionHelper.class);
        when(ConnectionHelper.getConnection(testColumn)).thenReturn(tempConnection);

        java.sql.Connection connection = mock(java.sql.Connection.class);

        PowerMockito.mockStatic(JavaSqlFactory.class);
        TypedReturnCode<java.sql.Connection> rc = mock(TypedReturnCode.class);

        when(JavaSqlFactory.createConnection(tempConnection)).thenReturn(rc);
        when(rc.getObject()).thenReturn(connection);

        Catalog catalog = mock(CatalogImpl.class);
        PowerMockito.mockStatic(CatalogHelper.class);
        when(CatalogHelper.getParentCatalog(tdTable)).thenReturn(catalog);

        Schema schema = mock(SchemaImpl.class);
        PowerMockito.mockStatic(SchemaHelper.class);
        when(SchemaHelper.getParentSchema(tdTable)).thenReturn(schema);

        PowerMockito.mockStatic(ConnectionUtils.class);
        when(ConnectionUtils.getName(catalog)).thenReturn("testCatalogName"); //$NON-NLS-1$
        when(ConnectionUtils.getName(schema)).thenReturn("testSchemaName"); //$NON-NLS-1$

        List<TdSqlDataType> list = mock(List.class);
        try {
            when(ConnectionUtils.getDataType("testCatalogName", "testSchemaName", "testTableName", "testColumnName", connection)) //$NON-NLS-1$ //$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$
                    .thenReturn(list);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        when(list.size()).thenReturn(0);

        ReturnCode returnCode = new ReturnCode();
        returnCode.setReturnCode("ok", true); //$NON-NLS-1$
        PowerMockito.mockStatic(org.talend.utils.sql.ConnectionUtils.class);
        when(org.talend.utils.sql.ConnectionUtils.closeConnection(connection)).thenReturn(returnCode);

        PowerMockito.mockStatic(ElementWriterFactory.class);

        ElementWriterFactory instance = mock(ElementWriterFactory.class);
        when(ElementWriterFactory.getInstance()).thenReturn(instance);

        DataProviderWriter dataProviderWriter = mock(DataProviderWriter.class);
        when(instance.createDataProviderWriter()).thenReturn(dataProviderWriter);

        when(dataProviderWriter.save(tempConnection)).thenReturn(returnCode);

        ConnectionUtils.retrieveColumn((MetadataTable) tdTable);
        Assert.assertTrue(returnCode.isOk());
    }

    /**
     * Test method for
     * {@link org.talend.cwm.db.connection.ConnectionUtils#isGeneralJdbc(org.talend.core.model.metadata.builder.connection.Connection)}
     * .
     */
    @Test
    public void testIsGeneralJdbc() {
        DatabaseConnection dbConnMock = mock(DatabaseConnection.class);
        EDatabaseTypeName generalJdbcType = EDatabaseTypeName.GENERAL_JDBC;
        when(dbConnMock.getDatabaseType()).thenReturn(generalJdbcType.getDisplayName());
        assertTrue(ConnectionUtils.isGeneralJdbc(dbConnMock));
    }

    /**
     * Test method for
     * {@link org.talend.cwm.db.connection.ConnectionUtils#checkGeneralJdbcJarFilePathDriverClassName(org.talend.core.model.metadata.builder.connection.DatabaseConnection)}
     * .
     */
    @Test
    public void testCheckGeneralJdbcJarFilePathDriverClassName() {
        String driverClass = "DriverClassName"; //$NON-NLS-1$
        String driverJarPath = "DriverJarFilePath"; //$NON-NLS-1$
        String msg = "msg"; //$NON-NLS-1$

        ResourceBundle rb = mock(ResourceBundle.class);
        stub(method(ResourceBundle.class, "getBundle", String.class)).toReturn(rb); //$NON-NLS-1$

        PowerMockito.mockStatic(Messages.class);
        when(Messages.getString(anyString())).thenReturn(msg);

        PowerMockito.mockStatic(JavaSqlFactory.class);
        DatabaseConnection dbConnMock = mock(DatabaseConnection.class);
        when(JavaSqlFactory.getDriverClass(dbConnMock)).thenReturn(driverClass);
        when(JavaSqlFactory.getDriverJarPath(dbConnMock)).thenReturn(driverJarPath);

        try {
            ReturnCode rc = ConnectionUtils.checkGeneralJdbcJarFilePathDriverClassName(dbConnMock);

            assertFalse(rc.isOk());
            assertEquals(msg, rc.getMessage());
        } catch (MalformedURLException e) {
            Assert.fail(e.getMessage());
        }
    }

    /**
     * Test method for
     * {@link org.talend.cwm.db.connection.ConnectionUtils#checkUsernameBeforeSaveConnection4Sqlite(org.talend.core.model.metadata.builder.connection.Connection)}
     * .
     */
    @Test
    public void testCheckUsernameBeforeSaveConnection4Sqlite() {
        DatabaseConnection sqliteConn = ConnectionFactory.eINSTANCE.createDatabaseConnection();
        sqliteConn.setDatabaseType(SupportDBUrlType.SQLITE3DEFAULTURL.getDBKey());
        sqliteConn.setUsername(""); //$NON-NLS-1$
        sqliteConn.setContextMode(false);
        ConnectionUtils.checkUsernameBeforeSaveConnection4Sqlite(sqliteConn);
        assertTrue(JavaSqlFactory.DEFAULT_USERNAME.equals(sqliteConn.getUsername()));

        String username = "abc"; //$NON-NLS-1$
        sqliteConn.setUsername(username);
        ConnectionUtils.checkUsernameBeforeSaveConnection4Sqlite(sqliteConn);
        assertFalse(JavaSqlFactory.DEFAULT_USERNAME.equals(sqliteConn.getUsername()));
        assertTrue(username.equals(sqliteConn.getUsername()));
    }
}
