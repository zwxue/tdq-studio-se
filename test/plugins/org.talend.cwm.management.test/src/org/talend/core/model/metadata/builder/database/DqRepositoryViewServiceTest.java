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
package org.talend.core.model.metadata.builder.database;

import static org.junit.Assert.fail;

import org.junit.Assert;
import org.junit.Test;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.properties.PropertiesFactory;
import org.talend.core.model.properties.Property;
import org.talend.cwm.xml.TdXmlElementType;
import org.talend.cwm.xml.TdXmlSchema;
import org.talend.cwm.xml.XmlFactory;
import orgomg.cwm.objectmodel.core.Package;
import orgomg.cwm.resource.relational.ColumnSet;

/**
 * DOC xqliu class global comment. Detailled comment
 */
public class DqRepositoryViewServiceTest {

    private static final String BASE_NAME = "base_name"; //$NON-NLS-1$

    private static final String EXT_NAME = "ext"; //$NON-NLS-1$

    private static final String CATALOG_PATTERN = "catalog_pattern"; //$NON-NLS-1$

    private static final String SCHEMA_PATTERN = "schema_pattern"; //$NON-NLS-1$

    private static final String TABLE_PATTERN = "table_pattern"; //$NON-NLS-1$

    private static final String VIEW_PATTERN = "view_pattern"; //$NON-NLS-1$

    private static final String COLUMN_PATTERN = "column_pattern"; //$NON-NLS-1$

    /**
     * Test method for
     * {@link org.talend.core.model.metadata.builder.database.DqRepositoryViewService#createTechnicalName(java.lang.String)}
     * .
     */
    @Test
    public void testCreateTechnicalName() {
        try {
            Assert.assertNotNull(DqRepositoryViewService.createTechnicalName(BASE_NAME));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Test method for
     * {@link org.talend.core.model.metadata.builder.database.DqRepositoryViewService#refreshDataProvider(org.talend.core.model.metadata.builder.connection.Connection, java.lang.String, java.lang.String)}
     * .
     */
    @Test
    public void testRefreshDataProvider() {
        try {
            Connection dataProvider = null; // need to create a real Connection object if the method
                                            // refreshDataProvider() implement
            Assert.assertFalse(DqRepositoryViewService.refreshDataProvider(dataProvider, CATALOG_PATTERN, SCHEMA_PATTERN));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Test method for
     * {@link org.talend.core.model.metadata.builder.database.DqRepositoryViewService#refreshTables(orgomg.cwm.objectmodel.core.Package, java.lang.String)}
     * .
     */
    @Test
    public void testRefreshTables() {
        try {
            Package schema = null; // need to create a real Package object if the method
                                   // refreshTables() implement
            Assert.assertFalse(DqRepositoryViewService.refreshTables(schema, TABLE_PATTERN));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Test method for
     * {@link org.talend.core.model.metadata.builder.database.DqRepositoryViewService#refreshViews(orgomg.cwm.objectmodel.core.Package, java.lang.String)}
     * .
     */
    @Test
    public void testRefreshViews() {
        try {
            Package schema = null; // need to create a real Package object if the method
                                   // refreshViews() implement
            Assert.assertFalse(DqRepositoryViewService.refreshViews(schema, VIEW_PATTERN));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Test method for
     * {@link org.talend.core.model.metadata.builder.database.DqRepositoryViewService#refreshColumns(orgomg.cwm.resource.relational.ColumnSet, java.lang.String)}
     * .
     */
    @Test
    public void testRefreshColumns() {
        try {
            ColumnSet table = null; // need to create a real Package object if the method
                                    // refreshColumns() implement
            Assert.assertFalse(DqRepositoryViewService.refreshColumns(table, COLUMN_PATTERN));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Test method for
     * {@link org.talend.core.model.metadata.builder.database.DqRepositoryViewService#getTables(org.talend.core.model.metadata.builder.connection.Connection, orgomg.cwm.resource.relational.Catalog, java.lang.String, boolean)}
     * .
     * 
     * Has been done in the org.talend.metadata.management.test
     */

    // @Test
    // public void testGetTablesConnectionCatalogStringBoolean() {
    // try {
    // TestAnalysisCreation helper = new TestAnalysisCreation();
    //
    // // create a mysql connection
    // Connection connection = helper.getDataManager();
    //
    // // get the tbi catalog or default catalog
    //            String tbi = "tbi"; //$NON-NLS-1$
    // List<Catalog> tdCatalogs = CatalogHelper.getCatalogs(connection.getDataPackage());
    // Assert.assertFalse(tdCatalogs.isEmpty());
    // Catalog catalog = tdCatalogs.get(0);
    // for (Catalog tdCatalog : tdCatalogs) {
    // if (tbi.equals(tdCatalog.getName())) {
    // catalog = tdCatalog;
    // break;
    // }
    // }
    //
    // List<TdTable> tables = DqRepositoryViewService.getTables(connection, catalog, null, true);
    // Assert.assertFalse(tables.isEmpty());
    // } catch (Exception e) {
    // fail(e.getMessage());
    // }
    // }


    /**
     * Test method for
     * {@link org.talend.core.model.metadata.builder.database.DqRepositoryViewService#getTables(org.talend.core.model.metadata.builder.connection.Connection, orgomg.cwm.resource.relational.Schema, java.lang.String, boolean)}
     * .
     * 
     * Has been done in the org.talend.metadata.management.test
     */

    // @Test
    // public void testGetTablesConnectionSchemaStringBoolean() {
    // try {
    // TestAnalysisCreation helper = new TestAnalysisCreation();
    //
    // // create a postgresql connection
    // Connection connection = helper.getDataManagerPostgresql();
    //
    // // get the tdq_db catalog
    //            String tbi = "tdq_db"; //$NON-NLS-1$
    // List<Catalog> tdCatalogs = CatalogHelper.getCatalogs(connection.getDataPackage());
    // Assert.assertFalse(tdCatalogs.isEmpty());
    // Catalog catalog = null;
    // for (Catalog tdCatalog : tdCatalogs) {
    // if (tbi.equals(tdCatalog.getName())) {
    // catalog = tdCatalog;
    // break;
    // }
    // }
    //
    // // get the talend schema
    // Schema schema = null;
    //            String talend = "talend"; //$NON-NLS-1$
    // if (catalog != null) {
    // List<Schema> tdSchemas = CatalogHelper.getSchemas(catalog);
    // Assert.assertFalse(tdSchemas.isEmpty());
    // for (Schema tdSchema : tdSchemas) {
    // if (talend.equals(tdSchema.getName())) {
    // schema = tdSchema;
    // break;
    // }
    // }
    // }
    //
    // List<TdTable> tables = DqRepositoryViewService.getTables(connection, schema, null, true);
    // Assert.assertFalse(tables.isEmpty());
    // } catch (Exception e) {
    // fail(e.getMessage());
    // }
    // }


    /**
     * Test method for
     * {@link org.talend.core.model.metadata.builder.database.DqRepositoryViewService#getTables(org.talend.core.model.metadata.builder.connection.Connection, orgomg.cwm.objectmodel.core.Package, java.lang.String, boolean)}
     * .
     */
    @Test
    public void testGetTablesConnectionPackageStringBoolean() {
        // if testGetTablesConnectionCatalogStringBoolean() and testGetTablesConnectionSchemaStringBoolean() ok then
        // this method ok also
    }

    /**
     * Test method for
     * {@link org.talend.core.model.metadata.builder.database.DqRepositoryViewService#getViews(org.talend.core.model.metadata.builder.connection.Connection, orgomg.cwm.resource.relational.Catalog, java.lang.String, boolean)}
     * . Has been done in the org.talend.metadata.management.test
     */
    // @Test
    // public void testGetViewsConnectionCatalogStringBoolean() {
    // try {
    // TestAnalysisCreation helper = new TestAnalysisCreation();
    //
    // // create a mysql connection
    // Connection connection = helper.getDataManager();
    //
    // // get the tbi catalog or default catalog
    //            String tbi = "tbi"; //$NON-NLS-1$
    // List<Catalog> tdCatalogs = CatalogHelper.getCatalogs(connection.getDataPackage());
    // Assert.assertFalse(tdCatalogs.isEmpty());
    // Catalog catalog = tdCatalogs.get(0);
    // for (Catalog tdCatalog : tdCatalogs) {
    // if (tbi.equals(tdCatalog.getName())) {
    // catalog = tdCatalog;
    // break;
    // }
    // }
    //
    // List<TdView> views = DqRepositoryViewService.getViews(connection, catalog, null, true);
    // Assert.assertFalse(views.isEmpty());
    // } catch (Exception e) {
    // fail(e.getMessage());
    // }
    // }

    /**
     * Test method for
     * {@link org.talend.core.model.metadata.builder.database.DqRepositoryViewService#getViews(org.talend.core.model.metadata.builder.connection.Connection, orgomg.cwm.resource.relational.Schema, java.lang.String, boolean)}
     * . Has been done in the org.talend.metadata.management.test
     */
    // @Test
    // public void testGetViewsConnectionSchemaStringBoolean() {
    // try {
    // TestAnalysisCreation helper = new TestAnalysisCreation();
    //
    // // create a postgresql connection
    // Connection connection = helper.getDataManagerPostgresql();
    //
    // // get the tdq_db catalog
    //            String tbi = "tdq_db"; //$NON-NLS-1$
    // List<Catalog> tdCatalogs = CatalogHelper.getCatalogs(connection.getDataPackage());
    // Assert.assertFalse(tdCatalogs.isEmpty());
    // Catalog catalog = null;
    // for (Catalog tdCatalog : tdCatalogs) {
    // if (tbi.equals(tdCatalog.getName())) {
    // catalog = tdCatalog;
    // break;
    // }
    // }
    //
    // // get the talend schema
    // Schema schema = null;
    //            String talend = "talend"; //$NON-NLS-1$
    // if (catalog != null) {
    // List<Schema> tdSchemas = CatalogHelper.getSchemas(catalog);
    // Assert.assertFalse(tdSchemas.isEmpty());
    // for (Schema tdSchema : tdSchemas) {
    // if (talend.equals(tdSchema.getName())) {
    // schema = tdSchema;
    // break;
    // }
    // }
    // }
    //
    // List<TdView> views = DqRepositoryViewService.getViews(connection, schema, null, true);
    // Assert.assertFalse(views.isEmpty());
    // } catch (Exception e) {
    // fail(e.getMessage());
    // }
    // }

    /**
     * Test method for
     * {@link org.talend.core.model.metadata.builder.database.DqRepositoryViewService#getViews(org.talend.core.model.metadata.builder.connection.Connection, orgomg.cwm.objectmodel.core.Package, java.lang.String, boolean)}
     * .
     */
    @Test
    public void testGetViewsConnectionPackageStringBoolean() {
        // if testGetViewsConnectionCatalogStringBoolean() and testGetViewsConnectionSchemaStringBoolean() ok then this
        // method ok also
    }

    /**
     * Test method for
     * {@link org.talend.core.model.metadata.builder.database.DqRepositoryViewService#getColumns(org.talend.core.model.metadata.builder.connection.Connection, orgomg.cwm.resource.relational.ColumnSet, java.lang.String, boolean)}
     * .
     * 
     * Has been done in the org.talend.metadata.management.test
     */
    // @Test
    // public void testGetColumns() {
    // try {
    // TestAnalysisCreation helper = new TestAnalysisCreation();
    //
    // // create a mysql connection
    // Connection connection = helper.getDataManager();
    //
    // // get the tbi catalog
    //            String tbi = "tbi"; //$NON-NLS-1$
    // List<Catalog> tdCatalogs = CatalogHelper.getCatalogs(connection.getDataPackage());
    // Assert.assertFalse(tdCatalogs.isEmpty());
    // Catalog catalog = null;
    // for (Catalog tdCatalog : tdCatalogs) {
    // if (tbi.equals(tdCatalog.getName())) {
    // catalog = tdCatalog;
    // break;
    // }
    // }
    //
    // // get the customer table
    //            String customer = "customer"; //$NON-NLS-1$
    // ColumnSet columnSet = null;
    // if (catalog != null) {
    // List<TdTable> tables = DqRepositoryViewService.getTables(connection, catalog, null, true);
    // CatalogHelper.addTables(tables, catalog);
    // Assert.assertFalse(tables.isEmpty());
    // for (TdTable table : tables) {
    // if (customer.equals(table.getName())) {
    // columnSet = table;
    // break;
    // }
    // }
    // }
    //
    // List<TdColumn> columns = DqRepositoryViewService.getColumns(connection, columnSet, null, true);
    // Assert.assertFalse(columns.isEmpty());
    // } catch (Exception e) {
    // fail(e.getMessage());
    // }
    // }

    /**
     * Test method for
     * {@link org.talend.core.model.metadata.builder.database.DqRepositoryViewService#createFilename(java.lang.String, java.lang.String)}
     * .
     */
    @Test
    public void testCreateFilename() {
        try {
            Assert.assertNotNull(DqRepositoryViewService.createFilename(BASE_NAME, EXT_NAME));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Test method for
     * {@link org.talend.core.model.metadata.builder.database.DqRepositoryViewService#readFromFile(org.eclipse.core.resources.IFile)}
     * .
     */
    @Test
    public void testReadFromFile() {
        // this method has been deprecated!!!
    }

    /**
     * Test method for
     * {@link org.talend.core.model.metadata.builder.database.DqRepositoryViewService#getAllRepositoryResourceObjects(boolean)}
     * .
     */
    @Test
    public void testGetAllRepositoryResourceObjects() {
        // need a project to test this method!!!
    }

    /**
     * Test method for
     * {@link org.talend.core.model.metadata.builder.database.DqRepositoryViewService#buildElementName(org.talend.core.model.properties.Property)}
     * .
     */
    @Test
    public void testBuildElementName() {
        try {
            String displayName = "displayName"; //$NON-NLS-1$
            String version = "version"; //$NON-NLS-1$
            String elementName = displayName + " " + version; //$NON-NLS-1$

            Property createProperty = PropertiesFactory.eINSTANCE.createProperty();
            createProperty.setDisplayName(displayName);
            createProperty.setVersion(version);

            Assert.assertTrue(elementName.equals(DqRepositoryViewService.buildElementName(createProperty)));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Test method for
     * {@link org.talend.core.model.metadata.builder.database.DqRepositoryViewService#hasChildren(org.talend.cwm.xml.TdXmlElementType)}
     * .
     */
    @Test
    public void testHasChildren() {

        try {
            String schemaName = "XmlSchemaName"; //$NON-NLS-1$
            TdXmlSchema createTdXmlSchema = XmlFactory.eINSTANCE.createTdXmlSchema();
            createTdXmlSchema.setName(schemaName);

            String elementTypeName = "XmlElementTypeName"; //$NON-NLS-1$
            TdXmlElementType createTdXmlElementType = XmlFactory.eINSTANCE.createTdXmlElementType();
            createTdXmlElementType.setName(elementTypeName);
            createTdXmlElementType.setOwnedDocument(createTdXmlSchema);

            Assert.assertTrue(DqRepositoryViewService.hasChildren(createTdXmlElementType));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Test method for
     * {@link org.talend.core.model.metadata.builder.database.DqRepositoryViewService#isContainsTable(org.talend.core.model.metadata.builder.connection.Connection, orgomg.cwm.resource.relational.Catalog, java.lang.String)}
     * .
     * 
     * Has been done in the org.talend.metadata.management.test
     */
    // @Test
    // public void testIsContainsTableConnectionCatalogString() {
    // try {
    // TestAnalysisCreation helper = new TestAnalysisCreation();
    //
    // // create a mysql connection
    // Connection connection = helper.getDataManager();
    //
    // // get the tbi catalog or default catalog
    //            String tbi = "tbi"; //$NON-NLS-1$
    // List<Catalog> tdCatalogs = CatalogHelper.getCatalogs(connection.getDataPackage());
    // Assert.assertFalse(tdCatalogs.isEmpty());
    // Catalog catalog = tdCatalogs.get(0);
    // for (Catalog tdCatalog : tdCatalogs) {
    // if (tbi.equals(tdCatalog.getName())) {
    // catalog = tdCatalog;
    // break;
    // }
    // }
    //
    // Assert.assertTrue(DqRepositoryViewService.isContainsTable(connection, catalog, null));
    // } catch (Exception e) {
    // fail(e.getMessage());
    // }
    // }

    /**
     * Test method for
     * {@link org.talend.core.model.metadata.builder.database.DqRepositoryViewService#isContainsTable(org.talend.core.model.metadata.builder.connection.Connection, orgomg.cwm.resource.relational.Schema, java.lang.String)}
     * .
     * 
     * Has been done in the org.talend.metadata.management.test
     */
    // @Test
    // public void testIsContainsTableConnectionSchemaString() {
    //
    // try {
    // TestAnalysisCreation helper = new TestAnalysisCreation();
    //
    // // create a postgresql connection
    // Connection connection = helper.getDataManagerPostgresql();
    //
    // // get the tdq_db catalog
    //            String tbi = "tdq_db"; //$NON-NLS-1$
    // List<Catalog> tdCatalogs = CatalogHelper.getCatalogs(connection.getDataPackage());
    // Assert.assertFalse(tdCatalogs.isEmpty());
    // Catalog catalog = null;
    // for (Catalog tdCatalog : tdCatalogs) {
    // if (tbi.equals(tdCatalog.getName())) {
    // catalog = tdCatalog;
    // break;
    // }
    // }
    //
    // // get the talend schema
    // Schema schema = null;
    //            String talend = "talend"; //$NON-NLS-1$
    // if (catalog != null) {
    // List<Schema> tdSchemas = CatalogHelper.getSchemas(catalog);
    // Assert.assertFalse(tdSchemas.isEmpty());
    // for (Schema tdSchema : tdSchemas) {
    // if (talend.equals(tdSchema.getName())) {
    // schema = tdSchema;
    // break;
    // }
    // }
    // }
    //
    // Assert.assertTrue(DqRepositoryViewService.isContainsTable(connection, schema, null));
    // } catch (Exception e) {
    // fail(e.getMessage());
    // }
    // }

    /**
     * Test method for
     * {@link org.talend.core.model.metadata.builder.database.DqRepositoryViewService#isContainsView(org.talend.core.model.metadata.builder.connection.Connection, orgomg.cwm.resource.relational.Catalog, java.lang.String)}
     * .
     * 
     * Has been done in the org.talend.metadata.management.test
     */
    // @Test
    // public void testIsContainsViewConnectionCatalogString() {
    // try {
    // TestAnalysisCreation helper = new TestAnalysisCreation();
    //
    // // create a mysql connection
    // Connection connection = helper.getDataManager();
    //
    // // get the tbi catalog or default catalog
    //            String tbi = "tbi"; //$NON-NLS-1$
    // List<Catalog> tdCatalogs = CatalogHelper.getCatalogs(connection.getDataPackage());
    // Assert.assertFalse(tdCatalogs.isEmpty());
    // Catalog catalog = tdCatalogs.get(0);
    // for (Catalog tdCatalog : tdCatalogs) {
    // if (tbi.equals(tdCatalog.getName())) {
    // catalog = tdCatalog;
    // break;
    // }
    // }
    //
    // Assert.assertTrue(DqRepositoryViewService.isContainsView(connection, catalog, null));
    // } catch (Exception e) {
    // fail(e.getMessage());
    // }
    // }

    /**
     * Test method for
     * {@link org.talend.core.model.metadata.builder.database.DqRepositoryViewService#isContainsView(org.talend.core.model.metadata.builder.connection.Connection, orgomg.cwm.resource.relational.Schema, java.lang.String)}
     * . Has been done in the org.talend.metadata.management.test
     */
    // @Test
    // public void testIsContainsViewConnectionSchemaString() {
    //
    // try {
    // TestAnalysisCreation helper = new TestAnalysisCreation();
    //
    // // create a postgresql connection
    // Connection connection = helper.getDataManagerPostgresql();
    //
    // // get the tdq_db catalog
    //            String tbi = "tdq_db"; //$NON-NLS-1$
    // List<Catalog> tdCatalogs = CatalogHelper.getCatalogs(connection.getDataPackage());
    // Assert.assertFalse(tdCatalogs.isEmpty());
    // Catalog catalog = null;
    // for (Catalog tdCatalog : tdCatalogs) {
    // if (tbi.equals(tdCatalog.getName())) {
    // catalog = tdCatalog;
    // break;
    // }
    // }
    //
    // // get the talend schema
    // Schema schema = null;
    //            String talend = "talend"; //$NON-NLS-1$
    // if (catalog != null) {
    // List<Schema> tdSchemas = CatalogHelper.getSchemas(catalog);
    // Assert.assertFalse(tdSchemas.isEmpty());
    // for (Schema tdSchema : tdSchemas) {
    // if (talend.equals(tdSchema.getName())) {
    // schema = tdSchema;
    // break;
    // }
    // }
    // }
    //
    // Assert.assertTrue(DqRepositoryViewService.isContainsView(connection, schema, null));
    // } catch (Exception e) {
    // fail(e.getMessage());
    // }
    // }

}
