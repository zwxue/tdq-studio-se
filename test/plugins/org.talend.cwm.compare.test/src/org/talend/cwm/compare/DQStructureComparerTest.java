// ============================================================================
//
// Copyright (C) 2006-2018 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.cwm.compare;

import static org.junit.Assert.fail;

import java.io.File;
import java.net.URI;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.talend.commons.utils.WorkspaceUtils;
import org.talend.commons.utils.database.DB2ForZosDataBaseMetadata;
import org.talend.core.database.EDatabaseTypeName;
import org.talend.core.model.metadata.IMetadataConnection;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.metadata.builder.connection.ConnectionFactory;
import org.talend.core.model.metadata.builder.connection.DatabaseConnection;
import org.talend.core.model.metadata.builder.connection.MetadataTable;
import org.talend.core.model.metadata.builder.database.ExtractMetaDataUtils;
import org.talend.core.model.metadata.builder.database.dburl.SupportDBUrlType;
import org.talend.core.runtime.CoreRuntimePlugin;
import org.talend.cwm.helper.ConnectionHelper;
import org.talend.metadata.managment.model.MetadataFillFactory;
import org.talend.metadata.managment.utils.MetadataConnectionUtils;
import org.talend.test.utils.SingletonUtil;
import org.talend.utils.sql.metadata.constants.MetaDataConstants;
import org.talend.utils.sugars.TypedReturnCode;
import orgomg.cwm.resource.relational.Catalog;
import orgomg.cwm.resource.relational.Schema;

/**
 * created by talend on 2015-07-28 Detailled comment.
 *
 */
@PrepareForTest({ MetadataFillFactory.class, ExtractMetaDataUtils.class, ConnectionHelper.class, CoreRuntimePlugin.class,
        WorkspaceUtils.class, URI.class })
public class DQStructureComparerTest {

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    @Test
    @Ignore
    public void testCopyedToDestinationFile() {
        fail("Not yet implemented");
    }

    @Test
    @Ignore
    public void testGetTempRefreshFile() {
        fail("Not yet implemented");
    }

    @Test
    @Ignore
    public void testGetNeedReloadElementsFile() {
        fail("Not yet implemented");
    }

    @Test
    @Ignore
    public void testGetFirstComparisonLocalFile() {
        fail("Not yet implemented");
    }

    @Test
    @Ignore
    public void testGetSecondComparisonLocalFile() {
        fail("Not yet implemented");
    }

    @Test
    @Ignore
    public void testDeleteFirstResourceFile() {
        fail("Not yet implemented");
    }

    @Test
    @Ignore
    public void testDeleteSecondResourceFile() {
        fail("Not yet implemented");
    }

    @Test
    @Ignore
    public void testGetDiffResourceFile() {
        fail("Not yet implemented");
    }

    @Test
    @Ignore
    public void testGetLocalDiffResourceFile() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetRefreshedDataProvider() throws Exception {
        DatabaseConnection dbProvider = ConnectionFactory.eINSTANCE.createDatabaseConnection();
        setJDBCMysqlConnection(dbProvider);
        List<Catalog> catalogPackageFilter = new ArrayList<Catalog>();
        List<orgomg.cwm.objectmodel.core.Package> schemaPackageFilter = new ArrayList<orgomg.cwm.objectmodel.core.Package>();
        // mock ReturnCode sql.Connection
        TypedReturnCode<java.sql.Connection> returnCode = new TypedReturnCode<java.sql.Connection>(true);
        java.sql.Connection mockSqlConn = Mockito.mock(java.sql.Connection.class);
        returnCode.setObject(mockSqlConn);
        // ~mock

        // mock DatabaseMetaData
        DatabaseMetaData mockDatabaseMetaData = Mockito.mock(DatabaseMetaData.class);
        Mockito.when(mockDatabaseMetaData.supportsCatalogsInIndexDefinitions()).thenReturn(true);

        // initial the data of catalogs
        List<String> catalogNames = new ArrayList<String>();
        List<String> packageFilter = MetadataConnectionUtils.getPackageFilter(dbProvider, mockDatabaseMetaData, true);
        boolean haveFilter = false;
        if (packageFilter.size() > 0) {
            catalogNames.addAll(packageFilter);
            haveFilter = true;
        } else {
            catalogNames.add("tbi"); //$NON-NLS-1$
            catalogNames.add("test"); //$NON-NLS-1$
            catalogNames.add("testtable"); //$NON-NLS-1$
        }
        // ~
        // mock ResultSet
        ResultSet mockCatalogResults = Mockito.mock(ResultSet.class);
        if (haveFilter) {
            Mockito.when(mockCatalogResults.next()).thenReturn(true, false);
        } else {
            Mockito.when(mockCatalogResults.next()).thenReturn(true, true, true, false);
        }
        Mockito.when(mockCatalogResults.getString(MetaDataConstants.TABLE_CAT.name())).thenReturn("tbi", "test", "testtable"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        // ~mock
        // mock ResultSet
        ResultSet mockSchemaResults = Mockito.mock(ResultSet.class);
        Mockito.when(mockSchemaResults.next()).thenReturn(false);
        // ~mock

        // getDatabaseProductName
        Mockito.when(mockDatabaseMetaData.getDatabaseProductName()).thenReturn(EDatabaseTypeName.MYSQL.getProduct());
        // getCatalogs

        Mockito.when(mockDatabaseMetaData.getCatalogs()).thenReturn(mockCatalogResults);
        Mockito.when(mockDatabaseMetaData.getDriverName()).thenReturn("don't match"); //$NON-NLS-1$
        Mockito.when(mockDatabaseMetaData.getSchemas()).thenReturn(mockSchemaResults);
        // ~mock

        // mock CoreRuntimePlugin
        CoreRuntimePlugin instanceMock = Mockito.mock(CoreRuntimePlugin.class);
        PowerMockito.mockStatic(CoreRuntimePlugin.class);
        Mockito.when(CoreRuntimePlugin.getInstance()).thenReturn(instanceMock);
        Mockito.when(instanceMock.getRepositoryService()).thenReturn(null);
        // ~CoreRuntimePlugin

        // mock ExtractMetaDataUtils
        ExtractMetaDataUtils extract = SingletonUtil.spySingleton(ExtractMetaDataUtils.class);
        PowerMockito.mockStatic(ExtractMetaDataUtils.class);
        Mockito.when(ExtractMetaDataUtils.getInstance()).thenReturn(extract);
        Mockito.when(extract.getConnectionMetadata(mockSqlConn)).thenReturn(mockDatabaseMetaData);
        Mockito.when(extract.getDatabaseMetaData(mockSqlConn, dbProvider)).thenReturn(mockDatabaseMetaData);

        // mock ConnectionHelper
        PowerMockito.mockStatic(ConnectionHelper.class);
        Set<MetadataTable> result = new HashSet<MetadataTable>();
        Mockito.when(ConnectionHelper.getTables(dbProvider)).thenReturn(result);
        Mockito.when(ConnectionHelper.addCatalogs((Collection<Catalog>) Mockito.any(), (Connection) Mockito.any()))
                .thenCallRealMethod();
        Mockito.when(ConnectionHelper.addPackages((Collection<Catalog>) Mockito.any(), (Connection) Mockito.any()))
                .thenCallRealMethod();
        // ~mock

        // mock MetadataFillFactory
        PowerMockito.mockStatic(MetadataFillFactory.class);
        MetadataFillFactory mockMetadataFillFactory = Mockito.mock(MetadataFillFactory.class);
        Mockito.when(MetadataFillFactory.getDBInstance(EDatabaseTypeName.MYSQL)).thenReturn(mockMetadataFillFactory);
        Mockito.when(mockMetadataFillFactory.createConnection((IMetadataConnection) Mockito.any())).thenReturn(returnCode);
        Mockito.when(mockMetadataFillFactory.fillUIConnParams((IMetadataConnection) Mockito.any(), (Connection) Mockito.isNull()))
                .thenReturn(dbProvider);
        Mockito.when(
                mockMetadataFillFactory.fillCatalogs((Connection) Mockito.any(), (DatabaseMetaData) Mockito.any(),
                        Mockito.anyList())).thenReturn(null);
        Mockito.when(
                mockMetadataFillFactory.fillSchemas((Connection) Mockito.any(), (DatabaseMetaData) Mockito.any(),
                        Mockito.anyList())).thenReturn(schemaPackageFilter);
        List<Schema> schemaList = new ArrayList<Schema>();
        Mockito.when(
                mockMetadataFillFactory.fillSchemaToCatalog((Connection) Mockito.any(), (DatabaseMetaData) Mockito.any(),
                        (Catalog) Mockito.any(), (List<String>) Mockito.any())).thenReturn(schemaList);
        // ~mock

        TypedReturnCode<Connection> refreshedDataProvider = DQStructureComparer.getRefreshedDataProvider(dbProvider);

        Assert.assertEquals(true, refreshedDataProvider.isOk());
        Assert.assertNotNull(refreshedDataProvider.getObject());

    }

    /**
     * Test method for {@link DQStructureComparer#getRefreshedDataProvider(Connection)}
     * 
     */
    @Test
    public void testGetRefreshedDataProviderContextMode() throws Exception {
        DatabaseConnection dbProvider = ConnectionFactory.eINSTANCE.createDatabaseConnection();
        setJDBCMysqlConnection(dbProvider);
        List<Catalog> catalogPackageFilter = new ArrayList<Catalog>();
        List<orgomg.cwm.objectmodel.core.Package> schemaPackageFilter = new ArrayList<orgomg.cwm.objectmodel.core.Package>();
        // mock ReturnCode sql.Connection
        TypedReturnCode<java.sql.Connection> returnCode = new TypedReturnCode<java.sql.Connection>(true);
        java.sql.Connection mockSqlConn = Mockito.mock(java.sql.Connection.class);
        returnCode.setObject(mockSqlConn);
        // ~mock

        // mock DatabaseMetaData
        DatabaseMetaData mockDatabaseMetaData = Mockito.mock(DatabaseMetaData.class);
        Mockito.when(mockDatabaseMetaData.supportsCatalogsInIndexDefinitions()).thenReturn(true);

        // initial the data of catalogs
        List<String> catalogNames = new ArrayList<String>();
        List<String> packageFilter = MetadataConnectionUtils.getPackageFilter(dbProvider, mockDatabaseMetaData, true);
        boolean haveFilter = false;
        if (packageFilter.size() > 0) {
            catalogNames.addAll(packageFilter);
            haveFilter = true;
        } else {
            catalogNames.add("tbi"); //$NON-NLS-1$
            catalogNames.add("test"); //$NON-NLS-1$
            catalogNames.add("testtable"); //$NON-NLS-1$
        }
        // ~
        // mock ResultSet
        ResultSet mockCatalogResults = Mockito.mock(ResultSet.class);
        if (haveFilter) {
            Mockito.when(mockCatalogResults.next()).thenReturn(true, false);
        } else {
            Mockito.when(mockCatalogResults.next()).thenReturn(true, true, true, false);
        }
        Mockito.when(mockCatalogResults.getString(MetaDataConstants.TABLE_CAT.name())).thenReturn("tbi", "test", "testtable"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        // ~mock
        // mock ResultSet
        ResultSet mockSchemaResults = Mockito.mock(ResultSet.class);
        Mockito.when(mockSchemaResults.next()).thenReturn(false);
        // ~mock

        // getDatabaseProductName
        Mockito.when(mockDatabaseMetaData.getDatabaseProductName()).thenReturn(EDatabaseTypeName.MYSQL.getProduct());
        // getCatalogs

        Mockito.when(mockDatabaseMetaData.getCatalogs()).thenReturn(mockCatalogResults);
        Mockito.when(mockDatabaseMetaData.getDriverName()).thenReturn("don't match"); //$NON-NLS-1$
        Mockito.when(mockDatabaseMetaData.getSchemas()).thenReturn(mockSchemaResults);
        // ~mock

        // mock CoreRuntimePlugin
        CoreRuntimePlugin instanceMock = Mockito.mock(CoreRuntimePlugin.class);
        PowerMockito.mockStatic(CoreRuntimePlugin.class);
        Mockito.when(CoreRuntimePlugin.getInstance()).thenReturn(instanceMock);
        Mockito.when(instanceMock.getRepositoryService()).thenReturn(null);
        // ~CoreRuntimePlugin

        // mock ExtractMetaDataUtils
        ExtractMetaDataUtils extract = SingletonUtil.spySingleton(ExtractMetaDataUtils.class);
        PowerMockito.mockStatic(ExtractMetaDataUtils.class);
        Mockito.when(ExtractMetaDataUtils.getInstance()).thenReturn(extract);
        Mockito.when(extract.getConnectionMetadata(mockSqlConn)).thenReturn(mockDatabaseMetaData);
        Mockito.when(extract.getDatabaseMetaData(mockSqlConn, dbProvider)).thenReturn(mockDatabaseMetaData);

        // mock ConnectionHelper
        PowerMockito.mockStatic(ConnectionHelper.class);
        Set<MetadataTable> result = new HashSet<MetadataTable>();
        Mockito.when(ConnectionHelper.getTables(dbProvider)).thenReturn(result);
        Mockito.when(ConnectionHelper.addCatalogs((Collection<Catalog>) Mockito.any(), (Connection) Mockito.any()))
                .thenCallRealMethod();
        Mockito.when(ConnectionHelper.addPackages((Collection<Catalog>) Mockito.any(), (Connection) Mockito.any()))
                .thenCallRealMethod();
        // ~mock

        // mock MetadataFillFactory
        PowerMockito.mockStatic(MetadataFillFactory.class);
        MetadataFillFactory mockMetadataFillFactory = Mockito.mock(MetadataFillFactory.class);
        Mockito.when(MetadataFillFactory.getDBInstance(EDatabaseTypeName.MYSQL)).thenReturn(mockMetadataFillFactory);
        Mockito.when(mockMetadataFillFactory.createConnection((IMetadataConnection) Mockito.any())).thenReturn(returnCode);
        Mockito.when(mockMetadataFillFactory.fillUIConnParams((IMetadataConnection) Mockito.any(), (Connection) Mockito.isNull()))
                .thenReturn(dbProvider);
        Mockito.when(
                mockMetadataFillFactory.fillCatalogs((Connection) Mockito.any(), (DatabaseMetaData) Mockito.any(),
                        Mockito.anyList())).thenReturn(null);
        Mockito.when(
                mockMetadataFillFactory.fillSchemas((Connection) Mockito.any(), (DatabaseMetaData) Mockito.any(),
                        Mockito.anyList())).thenReturn(schemaPackageFilter);
        List<Schema> schemaList = new ArrayList<Schema>();
        Mockito.when(
                mockMetadataFillFactory.fillSchemaToCatalog((Connection) Mockito.any(), (DatabaseMetaData) Mockito.any(),
                        (Catalog) Mockito.any(), (List<String>) Mockito.any())).thenReturn(schemaList);
        // ~mock

        TypedReturnCode<Connection> refreshedDataProvider = DQStructureComparer.getRefreshedDataProvider(dbProvider);

        Assert.assertEquals(true, refreshedDataProvider.isOk());
        Assert.assertNotNull(refreshedDataProvider.getObject());

    }

    @Test
    public void testGetRefreshedDataProviderForDB2ZOS() throws SQLException {
        DatabaseConnection dbProvider = ConnectionFactory.eINSTANCE.createDatabaseConnection();
        setJDBDB2ForZOSConnection(dbProvider);
        List<Catalog> catalogPackageFilter = new ArrayList<Catalog>();
        List<orgomg.cwm.objectmodel.core.Package> schemaPackageFilter = new ArrayList<orgomg.cwm.objectmodel.core.Package>();
        // mock ReturnCode sql.Connection
        TypedReturnCode<java.sql.Connection> returnCode = new TypedReturnCode<java.sql.Connection>(true);
        java.sql.Connection mockSqlConn = Mockito.mock(java.sql.Connection.class);
        returnCode.setObject(mockSqlConn);
        // ~mock

        // mock DatabaseMetaData
        DatabaseMetaData mockDatabaseMetaData = Mockito.mock(DB2ForZosDataBaseMetadata.class);
        Mockito.when(mockDatabaseMetaData.supportsCatalogsInIndexDefinitions()).thenReturn(true);
        Mockito.when(mockDatabaseMetaData.supportsSchemasInIndexDefinitions()).thenReturn(true);

        // initial the data of catalogs
        List<String> schenaNames = new ArrayList<String>();
        List<String> packageFilter = MetadataConnectionUtils.getPackageFilter(dbProvider, mockDatabaseMetaData, false);
        boolean haveFilter = false;
        if (packageFilter.size() > 0) {
            schenaNames.addAll(packageFilter);
            haveFilter = true;
        } else {
            schenaNames.add("tbi"); //$NON-NLS-1$
            schenaNames.add("test"); //$NON-NLS-1$
            schenaNames.add("testtable"); //$NON-NLS-1$
        }
        // ~
        // mock ResultSet
        ResultSet mockSchemaResults = Mockito.mock(ResultSet.class);
        if (haveFilter) {
            Mockito.when(mockSchemaResults.next()).thenReturn(true, false);
        } else {
            Mockito.when(mockSchemaResults.next()).thenReturn(true, true, true, false);
        }
        Mockito.when(mockSchemaResults.getString(MetaDataConstants.TABLE_SCHEM.name())).thenReturn("tbi", "test", "testtable"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        // ~mock
        // mock ResultSet
        ResultSet mockCatalogResults = Mockito.mock(ResultSet.class);
        Mockito.when(mockCatalogResults.next()).thenReturn(false);
        // ~mock

        // getCatalogs

        Mockito.when(mockDatabaseMetaData.getCatalogs()).thenReturn(mockCatalogResults);
        Mockito.when(mockDatabaseMetaData.getDriverName()).thenReturn("don't match");
        Mockito.when(mockDatabaseMetaData.getSchemas()).thenReturn(mockSchemaResults);
        // ~mock

        // mock CoreRuntimePlugin
        CoreRuntimePlugin instanceMock = Mockito.mock(CoreRuntimePlugin.class);
        PowerMockito.mockStatic(CoreRuntimePlugin.class);
        Mockito.when(CoreRuntimePlugin.getInstance()).thenReturn(instanceMock);
        Mockito.when(instanceMock.getRepositoryService()).thenReturn(null);
        // ~CoreRuntimePlugin

        // mock ExtractMetaDataUtils
        PowerMockito.mockStatic(ExtractMetaDataUtils.class);
        ExtractMetaDataUtils extract = Mockito.mock(ExtractMetaDataUtils.class);
        Mockito.when(ExtractMetaDataUtils.getInstance()).thenReturn(extract);
        Mockito.when(extract.getConnectionMetadata(mockSqlConn)).thenReturn(mockDatabaseMetaData);
        Mockito.when(extract.createDB2ForZosFakeDatabaseMetaData(mockSqlConn)).thenReturn(mockDatabaseMetaData);
        Mockito.when(extract.getDatabaseMetaData(mockSqlConn, dbProvider)).thenCallRealMethod();
        Mockito.when(extract.getDatabaseMetaData(mockSqlConn, dbProvider, false)).thenCallRealMethod();
        Mockito.when(extract.getDatabaseMetaData(mockSqlConn, EDatabaseTypeName.IBMDB2ZOS.getXmlName(), false, ""))
                .thenCallRealMethod();
        // ~mock

        // mock ConnectionHelper
        PowerMockito.mockStatic(ConnectionHelper.class);
        Set<MetadataTable> result = new HashSet<MetadataTable>();
        Mockito.when(ConnectionHelper.getTables(dbProvider)).thenReturn(result);
        Mockito.when(ConnectionHelper.addCatalogs((Collection<Catalog>) Mockito.any(), (Connection) Mockito.any()))
                .thenCallRealMethod();
        Mockito.when(ConnectionHelper.addPackages((Collection<Catalog>) Mockito.any(), (Connection) Mockito.any()))
                .thenCallRealMethod();
        Mockito.when(ConnectionHelper.addSchemas((Collection<Schema>) Mockito.any(), (Connection) Mockito.any()))
                .thenCallRealMethod();
        Mockito.when(ConnectionHelper.addPackages((Collection<Schema>) Mockito.any(), (Connection) Mockito.any()))
                .thenCallRealMethod();
        // ~mock

        // mock MetadataFillFactory
        MetadataFillFactory mockMetadataFillFactory = Mockito.mock(MetadataFillFactory.class);
        PowerMockito.mockStatic(MetadataFillFactory.class);
        Mockito.when(MetadataFillFactory.getDBInstance(EDatabaseTypeName.IBMDB2ZOS)).thenReturn(mockMetadataFillFactory);
        Mockito.when(mockMetadataFillFactory.createConnection((IMetadataConnection) Mockito.any())).thenReturn(returnCode);
        Mockito.when(mockMetadataFillFactory.fillUIConnParams((IMetadataConnection) Mockito.any(), (Connection) Mockito.isNull()))
                .thenReturn(dbProvider);
        Mockito.when(
                mockMetadataFillFactory.fillCatalogs((Connection) Mockito.any(), (DatabaseMetaData) Mockito.any(),
                        Mockito.anyList())).thenReturn(null);
        Mockito.when(
                mockMetadataFillFactory.fillSchemas((Connection) Mockito.any(), (DatabaseMetaData) Mockito.any(),
                        Mockito.anyList())).thenReturn(null);
        List<Schema> schemaList = new ArrayList<Schema>();
        Mockito.when(
                mockMetadataFillFactory.fillSchemaToCatalog((Connection) Mockito.any(), (DatabaseMetaData) Mockito.any(),
                        (Catalog) Mockito.any(), (List<String>) Mockito.any())).thenReturn(schemaList);
        // ~mock

        TypedReturnCode<Connection> refreshedDataProvider = DQStructureComparer.getRefreshedDataProvider(dbProvider);

        Assert.assertEquals(true, refreshedDataProvider.isOk());
        Assert.assertNotNull(refreshedDataProvider.getObject());

    }

    @Test
    public void testGetRefreshedDataProviderForDB2() throws SQLException {
        DatabaseConnection dbProvider = ConnectionFactory.eINSTANCE.createDatabaseConnection();
        setJDBDB2Connection(dbProvider);
        List<Catalog> catalogPackageFilter = new ArrayList<Catalog>();
        List<orgomg.cwm.objectmodel.core.Package> schemaPackageFilter = new ArrayList<orgomg.cwm.objectmodel.core.Package>();

        // mock DatabaseMetaData
        DatabaseMetaData mockDatabaseMetaData = Mockito.mock(DatabaseMetaData.class);
        Mockito.when(mockDatabaseMetaData.supportsCatalogsInIndexDefinitions()).thenReturn(true);
        Mockito.when(mockDatabaseMetaData.supportsSchemasInIndexDefinitions()).thenReturn(true);

        // mock ReturnCode sql.Connection
        TypedReturnCode<java.sql.Connection> returnCode = new TypedReturnCode<java.sql.Connection>(true);
        java.sql.Connection mockSqlConn = Mockito.mock(java.sql.Connection.class);
        Mockito.when(mockSqlConn.getMetaData()).thenReturn(mockDatabaseMetaData);

        returnCode.setObject(mockSqlConn);
        // ~mock

        // initial the data of catalogs
        List<String> schenaNames = new ArrayList<String>();
        List<String> packageFilter = MetadataConnectionUtils.getPackageFilter(dbProvider, mockDatabaseMetaData, false);
        boolean haveFilter = false;
        if (packageFilter.size() > 0) {
            schenaNames.addAll(packageFilter);
            haveFilter = true;
        } else {
            schenaNames.add("tbi"); //$NON-NLS-1$
            schenaNames.add("test"); //$NON-NLS-1$
            schenaNames.add("testtable"); //$NON-NLS-1$
        }
        // ~
        // mock ResultSet
        ResultSet mockSchemaResults = Mockito.mock(ResultSet.class);
        if (haveFilter) {
            Mockito.when(mockSchemaResults.next()).thenReturn(true, false);
        } else {
            Mockito.when(mockSchemaResults.next()).thenReturn(true, true, true, false);
        }
        Mockito.when(mockSchemaResults.getString(MetaDataConstants.TABLE_SCHEM.name())).thenReturn("tbi", "test", "testtable"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        // ~mock
        // mock ResultSet
        ResultSet mockCatalogResults = Mockito.mock(ResultSet.class);
        Mockito.when(mockCatalogResults.next()).thenReturn(false);
        // ~mock

        // getCatalogs

        Mockito.when(mockDatabaseMetaData.getCatalogs()).thenReturn(mockCatalogResults);
        Mockito.when(mockDatabaseMetaData.getDriverName()).thenReturn("don't match"); //$NON-NLS-1$
        Mockito.when(mockDatabaseMetaData.getSchemas()).thenReturn(mockSchemaResults);
        // ~mock

        // mock CoreRuntimePlugin
        CoreRuntimePlugin instanceMock = Mockito.mock(CoreRuntimePlugin.class);
        PowerMockito.mockStatic(CoreRuntimePlugin.class);
        Mockito.when(CoreRuntimePlugin.getInstance()).thenReturn(instanceMock);
        Mockito.when(instanceMock.getRepositoryService()).thenReturn(null);
        // ~CoreRuntimePlugin

        // mock ExtractMetaDataUtils
        PowerMockito.mockStatic(ExtractMetaDataUtils.class);
        ExtractMetaDataUtils extract = Mockito.mock(ExtractMetaDataUtils.class);
        Mockito.when(ExtractMetaDataUtils.getInstance()).thenReturn(extract);
        Mockito.when(extract.getConnectionMetadata(mockSqlConn)).thenReturn(mockDatabaseMetaData);
        Mockito.when(extract.createDB2ForZosFakeDatabaseMetaData(mockSqlConn)).thenReturn(mockDatabaseMetaData);
        Mockito.when(extract.getDatabaseMetaData(mockSqlConn, dbProvider)).thenCallRealMethod();
        Mockito.when(extract.getDatabaseMetaData(mockSqlConn, dbProvider, false)).thenCallRealMethod();
        Mockito.when(extract.getDatabaseMetaData(mockSqlConn, EDatabaseTypeName.IBMDB2.getXmlName(), false, ""))
                .thenCallRealMethod();
        // ~mock

        // mock ConnectionHelper
        PowerMockito.mockStatic(ConnectionHelper.class);
        Set<MetadataTable> result = new HashSet<MetadataTable>();
        Mockito.when(ConnectionHelper.getTables(dbProvider)).thenReturn(result);
        Mockito.when(ConnectionHelper.addCatalogs((Collection<Catalog>) Mockito.any(), (Connection) Mockito.any()))
                .thenCallRealMethod();
        Mockito.when(ConnectionHelper.addPackages((Collection<Catalog>) Mockito.any(), (Connection) Mockito.any()))
                .thenCallRealMethod();
        Mockito.when(ConnectionHelper.addSchemas((Collection<Schema>) Mockito.any(), (Connection) Mockito.any()))
                .thenCallRealMethod();
        Mockito.when(ConnectionHelper.addPackages((Collection<Schema>) Mockito.any(), (Connection) Mockito.any()))
                .thenCallRealMethod();
        // ~mock

        // mock MetadataFillFactory
        MetadataFillFactory mockMetadataFillFactory = Mockito.mock(MetadataFillFactory.class);
        PowerMockito.mockStatic(MetadataFillFactory.class);
        Mockito.when(MetadataFillFactory.getDBInstance(EDatabaseTypeName.IBMDB2)).thenReturn(mockMetadataFillFactory);
        Mockito.when(mockMetadataFillFactory.createConnection((IMetadataConnection) Mockito.any())).thenReturn(returnCode);
        Mockito.when(mockMetadataFillFactory.fillUIConnParams((IMetadataConnection) Mockito.any(), (Connection) Mockito.isNull()))
                .thenReturn(dbProvider);
        Mockito.when(
                mockMetadataFillFactory.fillCatalogs((Connection) Mockito.any(), (DatabaseMetaData) Mockito.any(),
                        Mockito.anyList())).thenReturn(null);
        Mockito.when(
                mockMetadataFillFactory.fillSchemas((Connection) Mockito.any(), (DatabaseMetaData) Mockito.any(),
                        Mockito.anyList())).thenReturn(null);
        List<Schema> schemaList = new ArrayList<Schema>();
        Mockito.when(
                mockMetadataFillFactory.fillSchemaToCatalog((Connection) Mockito.any(), (DatabaseMetaData) Mockito.any(),
                        (Catalog) Mockito.any(), (List<String>) Mockito.any())).thenReturn(schemaList);
        // ~mock

        TypedReturnCode<Connection> refreshedDataProvider = DQStructureComparer.getRefreshedDataProvider(dbProvider);

        Assert.assertEquals(true, refreshedDataProvider.isOk());
        Assert.assertNotNull(refreshedDataProvider.getObject());

    }

    private boolean setJDBCMysqlConnection(DatabaseConnection dbProvider) {
        // General JDBC case
        dbProvider.setComment("");
        dbProvider.setSID("tbi");
        dbProvider.setDatasourceName("");
        dbProvider.setDatabaseType("MySQL");
        dbProvider.setDbVersionString("MYSQL_5");
        dbProvider.setDriverClass("org.gjt.mm.mysql.Driver");
        dbProvider.setFileFieldName("");
        dbProvider.setId("_9bw28cccEeGQNaw_qcyMFw");
        dbProvider.setLabel("jdbcmysql1");
        dbProvider.setNullChar("");
        dbProvider.setRawPassword("shenze");
        dbProvider.setPort("3306");
        dbProvider.setServerName("");
        dbProvider.setSqlSynthax("SQL Syntax");
        dbProvider.setUiSchema("");
        dbProvider.setStringQuote("");
        dbProvider.setURL("jdbc:mysql://192.168.30.151:3306/tbi?noDatetimeStringSync=true");
        dbProvider.setAdditionalParams("");
        dbProvider.setUsername("shenze");
        dbProvider.setDbmsId("mysql_id");
        dbProvider.setProductId("MYSQL");
        dbProvider.setDBRootPath("");
        dbProvider.setSQLMode(false);
        dbProvider.setContextMode(false);
        dbProvider.setContextId("");
        dbProvider.setContextName("");
        dbProvider.setDatabaseType(SupportDBUrlType.MYSQLDEFAULTURL.getDBKey());
        return true;
    }

    private boolean setJDBDB2Connection(DatabaseConnection dbProvider) {
        // General JDBC case
        dbProvider.setComment("");
        dbProvider.setSID("");
        dbProvider.setDatasourceName("");
        dbProvider.setDatabaseType("IBM DB2");
        dbProvider.setDbVersionString("MYSQL_5");
        dbProvider.setDriverClass("org.gjt.mm.mysql.Driver");
        dbProvider.setFileFieldName("");
        dbProvider.setId("_9bw28cccEeGQNaw_qcyMFw");
        dbProvider.setLabel("jdbcmysql1");
        dbProvider.setNullChar("");
        dbProvider.setRawPassword("shenze");
        dbProvider.setPort("3306");
        dbProvider.setServerName("");
        dbProvider.setSqlSynthax("SQL Syntax");
        dbProvider.setUiSchema("");
        dbProvider.setStringQuote("");
        dbProvider.setURL("jdbc:mysql://192.168.30.151:3306/tbi?noDatetimeStringSync=true");
        dbProvider.setAdditionalParams("");
        dbProvider.setUsername("shenze");
        dbProvider.setDbmsId("mysql_id");
        dbProvider.setProductId("MYSQL");
        dbProvider.setDBRootPath("");
        dbProvider.setSQLMode(false);
        dbProvider.setContextMode(false);
        dbProvider.setContextId("");
        dbProvider.setContextName("");
        return true;
    }

    private boolean setJDBDB2ForZOSConnection(DatabaseConnection dbProvider) {
        // General JDBC case
        dbProvider.setComment("");
        dbProvider.setSID("");
        dbProvider.setDatasourceName("");
        dbProvider.setDatabaseType(EDatabaseTypeName.IBMDB2ZOS.getXmlName());
        dbProvider.setDbVersionString("MYSQL_5");
        dbProvider.setDriverClass("org.gjt.mm.mysql.Driver");
        dbProvider.setFileFieldName("");
        dbProvider.setId("_9bw28cccEeGQNaw_qcyMFw");
        dbProvider.setLabel("jdbcmysql1");
        dbProvider.setNullChar("");
        dbProvider.setRawPassword("shenze");
        dbProvider.setPort("3306");
        dbProvider.setServerName("");
        dbProvider.setSqlSynthax("SQL Syntax");
        dbProvider.setUiSchema("");
        dbProvider.setStringQuote("");
        dbProvider.setURL("jdbc:mysql://192.168.30.151:3306/tbi?noDatetimeStringSync=true");
        dbProvider.setAdditionalParams("");
        dbProvider.setUsername("shenze");
        dbProvider.setDbmsId("mysql_id");
        dbProvider.setProductId("MYSQL");
        dbProvider.setDBRootPath("");
        dbProvider.setSQLMode(false);
        dbProvider.setContextMode(false);
        dbProvider.setContextId("");
        dbProvider.setContextName("");
        return true;
    }

    @Test
    @Ignore
    public void testFindMatchedPackage() {
        fail("Not yet implemented");
    }

    @Test
    @Ignore
    public void testFindMatchedColumnSet() {
        fail("Not yet implemented");
    }

    @Test
    @Ignore
    public void testFindMatchedColumn() {
        fail("Not yet implemented");
    }

    @Test
    @Ignore
    public void testClearSubNode() {
        fail("Not yet implemented");
    }

    @Test
    @Ignore
    public void testOpenDiffCompareEditor() {
        fail("Not yet implemented");
    }

    /**
     * Test method for
     * {@link org.talend.cwm.compare.DQStructureComparer#removeResourceFromWorkspace(org.eclipse.emf.ecore.resource.Resource)}
     * . case 1: resource is null
     */
    @Test
    public void testRemoveResourceFromWorkspaceCase1() {
        org.eclipse.emf.ecore.resource.Resource resource1 = null;
        boolean removeResourceFromWorkspace = DQStructureComparer.removeResourceFromWorkspace(resource1);
        Assert.assertFalse(removeResourceFromWorkspace);
    }

    /**
     * Test method for
     * {@link org.talend.cwm.compare.DQStructureComparer#removeResourceFromWorkspace(org.eclipse.emf.ecore.resource.Resource)}
     * . case 2: the uri of resource is null
     */
    @Test
    public void testRemoveResourceFromWorkspaceCase2() {
        org.eclipse.emf.ecore.resource.Resource resource1 = Mockito.mock(org.eclipse.emf.ecore.resource.Resource.class);
        org.eclipse.emf.common.util.URI uri1 = null;
        Mockito.when(resource1.getURI()).thenReturn(uri1);
        boolean removeResourceFromWorkspace = DQStructureComparer.removeResourceFromWorkspace(resource1);
        Assert.assertFalse(removeResourceFromWorkspace);
    }

    /**
     * Test method for
     * {@link org.talend.cwm.compare.DQStructureComparer#removeResourceFromWorkspace(org.eclipse.emf.ecore.resource.Resource)}
     * . case 3: isPlatformResource case and file is null
     */
    @Test
    public void testRemoveResourceFromWorkspaceCase3() {
        // mock uri1
        org.eclipse.emf.common.util.URI uri1 = org.eclipse.emf.common.util.URI.createPlatformResourceURI("aa", false); //$NON-NLS-1$
        // org.eclipse.emf.common.util.URI uri1 = PowerMockito.mock(org.eclipse.emf.common.util.URI.class);
        // Mockito.when(uri1.isPlatformResource()).thenReturn(true);
        // ~uri1
        // mock resource1
        org.eclipse.emf.ecore.resource.Resource resource1 = Mockito.mock(org.eclipse.emf.ecore.resource.Resource.class);
        Mockito.when(resource1.getURI()).thenReturn(uri1);
        // ~resource1

        // mock file1
        IFile file1 = null;

        // ~file1

        // powerMock workspaceUtils
        PowerMockito.mockStatic(WorkspaceUtils.class);
        Mockito.when(WorkspaceUtils.getModelElementResource(uri1)).thenReturn(file1);
        // ~workspaceUtils

        boolean removeResourceFromWorkspace = DQStructureComparer.removeResourceFromWorkspace(resource1);
        Assert.assertFalse(removeResourceFromWorkspace);
    }

    /**
     * Test method for
     * {@link org.talend.cwm.compare.DQStructureComparer#removeResourceFromWorkspace(org.eclipse.emf.ecore.resource.Resource)}
     * . case 4: isPlatformResource case and file is not exist
     */
    @Test
    public void testRemoveResourceFromWorkspaceCase4() {
        // mock uri1
        org.eclipse.emf.common.util.URI uri1 = org.eclipse.emf.common.util.URI.createPlatformResourceURI("aa", false); //$NON-NLS-1$
        // ~uri1
        // mock resource1
        org.eclipse.emf.ecore.resource.Resource resource1 = Mockito.mock(org.eclipse.emf.ecore.resource.Resource.class);
        Mockito.when(resource1.getURI()).thenReturn(uri1);
        // ~resource1

        // mock file1
        IFile file1 = Mockito.mock(IFile.class);
        Mockito.when(file1.exists()).thenReturn(false);
        // ~file1

        // powerMock workspaceUtils
        PowerMockito.mockStatic(WorkspaceUtils.class);
        Mockito.when(WorkspaceUtils.getModelElementResource(uri1)).thenReturn(file1);
        // ~workspaceUtils

        boolean removeResourceFromWorkspace = DQStructureComparer.removeResourceFromWorkspace(resource1);
        Assert.assertFalse(removeResourceFromWorkspace);
    }

    /**
     * Test method for
     * {@link org.talend.cwm.compare.DQStructureComparer#removeResourceFromWorkspace(org.eclipse.emf.ecore.resource.Resource)}
     * . case 5: isPlatformResource case and file is exist and return true
     */
    @Test
    public void testRemoveResourceFromWorkspaceCase5() {
        // mock file1
        IFile file1 = Mockito.mock(IFile.class);
        Mockito.when(file1.exists()).thenReturn(true).thenReturn(false);
        // ~file1

        // mock uri1
        org.eclipse.emf.common.util.URI uri1 = org.eclipse.emf.common.util.URI.createPlatformResourceURI("aa", false); //$NON-NLS-1$
        // ~uri1
        // mock resource1
        org.eclipse.emf.ecore.resource.Resource resource1 = Mockito.mock(org.eclipse.emf.ecore.resource.Resource.class);
        Mockito.when(resource1.getURI()).thenReturn(uri1);
        // ~resource1

        // powerMock workspaceUtils
        PowerMockito.mockStatic(WorkspaceUtils.class);
        Mockito.when(WorkspaceUtils.getModelElementResource(uri1)).thenReturn(file1);
        // ~workspaceUtils

        boolean removeResourceFromWorkspace = DQStructureComparer.removeResourceFromWorkspace(resource1);
        Assert.assertTrue(removeResourceFromWorkspace);
    }

    /**
     * Test method for
     * {@link org.talend.cwm.compare.DQStructureComparer#removeResourceFromWorkspace(org.eclipse.emf.ecore.resource.Resource)}
     * . case 6: isFile case and file is exist and return true
     */
    @Test
    public void testRemoveResourceFromWorkspaceCase6() {
        // mock file1
        IFile file1 = Mockito.mock(IFile.class);
        Mockito.when(file1.exists()).thenReturn(true).thenReturn(false);
        // ~file1

        // mock uri1
        org.eclipse.emf.common.util.URI uri1 = org.eclipse.emf.common.util.URI.createFileURI("aa"); //$NON-NLS-1$
        // ~uri1
        // mock resource1
        org.eclipse.emf.ecore.resource.Resource resource1 = Mockito.mock(org.eclipse.emf.ecore.resource.Resource.class);
        Mockito.when(resource1.getURI()).thenReturn(uri1);
        // ~resource1

        // powerMock workspaceUtils
        PowerMockito.mockStatic(WorkspaceUtils.class);
        Mockito.when(WorkspaceUtils.getModelElementResource(uri1)).thenReturn(file1);
        Mockito.when(WorkspaceUtils.fileToIFile((File) Mockito.any())).thenReturn(file1);
        // ~workspaceUtils

        boolean removeResourceFromWorkspace = DQStructureComparer.removeResourceFromWorkspace(resource1);
        Assert.assertTrue(removeResourceFromWorkspace);
    }
}
