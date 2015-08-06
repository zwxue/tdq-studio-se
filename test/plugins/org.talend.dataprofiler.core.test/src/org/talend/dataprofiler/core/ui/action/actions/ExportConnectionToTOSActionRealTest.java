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
package org.talend.dataprofiler.core.ui.action.actions;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.jfree.util.Log;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.talend.commons.exception.PersistenceException;
import org.talend.core.context.Context;
import org.talend.core.context.RepositoryContext;
import org.talend.core.database.EDatabaseTypeName;
import org.talend.core.model.general.Project;
import org.talend.core.model.metadata.IMetadataConnection;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.metadata.builder.connection.ConnectionFactory;
import org.talend.core.model.metadata.builder.connection.DatabaseConnection;
import org.talend.core.model.properties.DatabaseConnectionItem;
import org.talend.core.model.properties.PropertiesFactory;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.core.runtime.CoreRuntimePlugin;
import org.talend.cwm.helper.CatalogHelper;
import org.talend.cwm.helper.ColumnSetHelper;
import org.talend.cwm.helper.ConnectionHelper;
import org.talend.cwm.helper.SchemaHelper;
import org.talend.dataprofiler.core.helper.UnitTestBuildHelper;
import org.talend.repository.localprovider.model.LocalRepositoryFactory;
import orgomg.cwm.objectmodel.core.Package;
import orgomg.cwm.resource.relational.Catalog;
import orgomg.cwm.resource.relational.Schema;

/**
 * created by zshen on Apr 23, 2013 Detailled comment
 * 
 */
public class ExportConnectionToTOSActionRealTest {

    private static LocalRepositoryFactory repositoryFactory;

    private static ProxyRepositoryFactory factory = ProxyRepositoryFactory.getInstance();

    private static Project originalProject;

    private static ExportConnectionToTOSAction createAction = null;

    private static Catalog catalog1 = null;

    private static Catalog catalog2 = null;

    private static Catalog catalog3 = null;

    private static Catalog catalog4 = null;

    private static Catalog catalog5 = null;

    private static Schema schema1 = null;

    private static Schema schema2 = null;

    /**
     * DOC zshen Comment method "setUpBeforeClass".
     * 
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        Context ctx = CoreRuntimePlugin.getInstance().getContext();
        RepositoryContext repositoryContext = (RepositoryContext) ctx.getProperty(Context.REPOSITORY_CONTEXT_KEY);
        if (repositoryContext != null) {
            originalProject = repositoryContext.getProject();
        }
        repositoryFactory = new LocalRepositoryFactory();

    }

    /**
     * DOC zshen Comment method "tearDownAfterClass".
     * 
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        repositoryFactory = null;
        if (originalProject != null) {
            Context ctx = CoreRuntimePlugin.getInstance().getContext();
            RepositoryContext repositoryContext = (RepositoryContext) ctx.getProperty(Context.REPOSITORY_CONTEXT_KEY);
            repositoryContext.setProject(originalProject);
        }
    }

    /**
     * DOC zshen Comment method "setUp".
     * 
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        UnitTestBuildHelper.deleteCurrentProject("testForExportConnectionToTOSAction"); //$NON-NLS-1$
        UnitTestBuildHelper.createRealProject("testForExportConnectionToTOSAction"); //$NON-NLS-1$

    }

    private static Catalog addCataPackage(String packageName, DatabaseConnectionItem connItem) throws PersistenceException {
        Connection connection = connItem.getConnection();
        Catalog createCatalog = CatalogHelper.createCatalog(packageName);
        ConnectionHelper.addCatalog(createCatalog, connection);

        return createCatalog;
    }

    private static DatabaseConnectionItem createConnectionItem(String name, IFolder folder, Boolean isDelete, boolean isSave) {
        IPath createPath = Path.EMPTY;
        if (folder != null) {
            createPath = new Path(folder.getFullPath().lastSegment());
        }
        // connection
        DatabaseConnection createConnection = ConnectionFactory.eINSTANCE.createDatabaseConnection();
        createConnection.setName(name);
        createConnection.setUsername("UserName"); //$NON-NLS-1$
        createConnection.setPassword("Password"); //$NON-NLS-1$
        createConnection.setURL("URL"); //$NON-NLS-1$
        createConnection.setDatabaseType(EDatabaseTypeName.MYSQL.getXmlName());
        createConnection.setContextMode(true);
        // ~connection
        DatabaseConnectionItem createDatabaseConnectionItem = PropertiesFactory.eINSTANCE.createDatabaseConnectionItem();

        org.talend.core.model.properties.Property createDatabaseConnectionProperty = PropertiesFactory.eINSTANCE.createProperty();
        createDatabaseConnectionProperty.setId(EcoreUtil.generateUUID());
        createDatabaseConnectionProperty.setItem(createDatabaseConnectionItem);
        createDatabaseConnectionProperty.setLabel(createConnection.getName());
        createDatabaseConnectionItem.setProperty(createDatabaseConnectionProperty);
        createDatabaseConnectionItem.setConnection(createConnection);
        try {
            if (isSave) {
                factory.create(createDatabaseConnectionItem, createPath, false);
            }
        } catch (PersistenceException e) {
            Log.error(e, e);
            Assert.fail(e.getMessage());
        }
        return createDatabaseConnectionItem;
    }

    /**
     * DOC zshen Comment method "initPackageList".
     * 
     * @param packageList
     * @throws PersistenceException
     */
    private static void initCatalogList(DatabaseConnectionItem createConnectionItem) throws PersistenceException {
        catalog1 = addCataPackage("catalog1", createConnectionItem);
        catalog2 = addCataPackage("catalog2", createConnectionItem);
        catalog3 = addCataPackage("catalog3", createConnectionItem);
        catalog4 = addCataPackage("catalog4", createConnectionItem);
        catalog5 = addCataPackage("catalog5", createConnectionItem);

        factory.save(createConnectionItem, null);
    }

    /**
     * DOC zshen Comment method "initPackageList".
     * 
     * @param packageList
     * @throws PersistenceException
     */
    private static void initSchemaOfCagalogList(DatabaseConnectionItem createConnectionItem) throws PersistenceException {
        schema1 = addSchePackage("schema1", catalog1);
        schema2 = addSchePackage("schema2", catalog2);
        factory.save(createConnectionItem, null);
    }

    /**
     * DOC zshen Comment method "initPackageList".
     * 
     * @param packageList
     * @throws PersistenceException
     */
    private static void initSchemaList(DatabaseConnectionItem createConnectionItem) throws PersistenceException {
        schema1 = addSchePackage("schema1", (DatabaseConnection) createConnectionItem.getConnection());
        schema2 = addSchePackage("schema2", (DatabaseConnection) createConnectionItem.getConnection());
        factory.save(createConnectionItem, null);
    }

    /**
     * DOC zshen Comment method "addSchePackage".
     * 
     * @param string
     * @param catalog1
     * @return
     */
    private static Schema addSchePackage(String schemaName, Catalog catalog1) {
        Schema createSchema = SchemaHelper.createSchema(schemaName);
        CatalogHelper.addSchemas(createSchema, catalog1);
        return createSchema;
    }

    /**
     * DOC zshen Comment method "addSchePackage".
     * 
     * @param string
     * @param catalog1
     * @return
     */
    private static Schema addSchePackage(String schemaName, DatabaseConnection conn) {
        Schema createSchema = SchemaHelper.createSchema(schemaName);
        ConnectionHelper.addSchema(createSchema, conn);
        return createSchema;
    }

    /**
     * DOC zshen Comment method "tearDown".
     * 
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
        UnitTestBuildHelper.deleteCurrentProject();
    }

    /**
     * Test method for {@link org.talend.dataprofiler.core.ui.action.actions.ExportConnectionToTOSAction#run()}.
     * 
     * @throws PersistenceException
     * 
     * case1: create new connection from catalog case
     */
    @Test
    public void testRuncase1() throws PersistenceException {
        // connectionNode
        DatabaseConnectionItem createOldConnectionItem = createConnectionItem("conn1", null, false, true);
        initCatalogList(createOldConnectionItem);
        initSchemaOfCagalogList(createOldConnectionItem);
        // ~connectionNode

        List<Package> packageList = new ArrayList<Package>();
        packageList.add(catalog1);
        createAction = PowerMockito.spy(new ExportConnectionToTOSAction(packageList));
        PowerMockito.doNothing().when(createAction).openSuccessInformation();
        PowerMockito.doNothing().when(createAction).refreshViewerAndNode();
        Connection oldConnection = ConnectionHelper.getConnection(catalog1);
        DatabaseConnectionItem createConnectionItem = createConnectionItem(oldConnection.getName() + "_" + catalog1.getName(),
                null, false, false);
        DatabaseConnection tempConnection = (DatabaseConnection) createConnectionItem.getConnection();
        tempConnection.setUiSchema(null);
        tempConnection.setSID(catalog1.getName());
        ConnectionHelper.addCatalog(EcoreUtil.copy(catalog1), tempConnection);

        PowerMockito.doReturn(tempConnection).when(createAction)
                .fillCatalogSchema(Mockito.argThat(new MetadataConnectionMatcher(catalog1)));
        createAction.run();
        List<IRepositoryViewObject> all = factory.getAll(ERepositoryObjectType.METADATA_CONNECTIONS, false);
        Assert.assertTrue(all.size() == 2);
        IRepositoryViewObject lastVersion = all.get(1);
        DatabaseConnectionItem item = (DatabaseConnectionItem) lastVersion.getProperty().getItem();
        Connection newConnection = item.getConnection();
        Assert.assertTrue(CatalogHelper.getCatalog(newConnection, catalog1.getName()) != null);
    }

    /**
     * Test method for {@link org.talend.dataprofiler.core.ui.action.actions.ExportConnectionToTOSAction#run()}.
     * 
     * @throws PersistenceException
     * 
     * case2: create new connection from schema case
     */
    @Test
    public void testRuncase2() throws PersistenceException {
        // connectionNode
        DatabaseConnectionItem createOldConnectionItem = createConnectionItem("conn1", null, false, true);
        initCatalogList(createOldConnectionItem);
        initSchemaOfCagalogList(createOldConnectionItem);
        // ~connectionNode
        List<Package> packageList = new ArrayList<Package>();
        packageList.add(schema1);
        createAction = PowerMockito.spy(new ExportConnectionToTOSAction(packageList));
        PowerMockito.doNothing().when(createAction).openSuccessInformation();
        PowerMockito.doNothing().when(createAction).refreshViewerAndNode();
        Connection oldConnection = ConnectionHelper.getConnection(schema1);
        DatabaseConnectionItem createConnectionItem = createConnectionItem(oldConnection.getName() + "_" + schema1.getName(),
                null, false, false);
        DatabaseConnection tempConnection = (DatabaseConnection) createConnectionItem.getConnection();
        tempConnection.setUiSchema(schema1.getName());
        tempConnection.setSID(catalog1.getName());
        ConnectionHelper.addCatalog(EcoreUtil.copy(catalog1), tempConnection);

        PowerMockito.doReturn(tempConnection).when(createAction)
                .fillCatalogSchema(Mockito.argThat(new MetadataConnectionMatcher(schema1)));
        createAction.run();
        List<IRepositoryViewObject> all = factory.getAll(ERepositoryObjectType.METADATA_CONNECTIONS, false);
        Assert.assertTrue(all.size() == 2);
        IRepositoryViewObject lastVersion = all.get(1);
        DatabaseConnectionItem item = (DatabaseConnectionItem) lastVersion.getProperty().getItem();
        Connection newConnection = item.getConnection();
        Assert.assertTrue(CatalogHelper.getCatalog(newConnection, catalog1.getName()) != null);
    }

    /**
     * Test method for {@link org.talend.dataprofiler.core.ui.action.actions.ExportConnectionToTOSAction#run()}.
     * 
     * @throws PersistenceException
     * 
     * case3: create new connection from only schema case
     */
    @Test
    public void testRuncase3() throws PersistenceException {
        // connectionNode
        DatabaseConnectionItem createOldConnectionItem = createConnectionItem("conn1", null, false, true);
        DatabaseConnection oldConnection = ((DatabaseConnection) createOldConnectionItem.getConnection());
        oldConnection.setSID("orcl"); //$NON-NLS-1$
        initSchemaList(createOldConnectionItem);

        // ~connectionNode
        List<Package> packageList = new ArrayList<Package>();
        packageList.add(schema1);
        createAction = PowerMockito.spy(new ExportConnectionToTOSAction(packageList));
        PowerMockito.doNothing().when(createAction).openSuccessInformation();
        PowerMockito.doNothing().when(createAction).refreshViewerAndNode();
        // Connection oldConnection = ConnectionHelper.getConnection(schema1);
        DatabaseConnectionItem createConnectionItem = createConnectionItem(oldConnection.getName() + "_" + schema1.getName(),
                null, false, false);
        DatabaseConnection tempConnection = (DatabaseConnection) createConnectionItem.getConnection();
        tempConnection.setUiSchema(schema1.getName());
        tempConnection.setSID("orcl"); //$NON-NLS-1$
        ConnectionHelper.addSchema(EcoreUtil.copy(schema1), tempConnection);

        PowerMockito.doReturn(tempConnection).when(createAction)
                .fillCatalogSchema(Mockito.argThat(new MetadataConnectionMatcher(schema1)));
        createAction.run();
        List<IRepositoryViewObject> all = factory.getAll(ERepositoryObjectType.METADATA_CONNECTIONS, false);
        Assert.assertTrue(all.size() == 2);
        IRepositoryViewObject lastVersion = all.get(1);
        DatabaseConnectionItem item = (DatabaseConnectionItem) lastVersion.getProperty().getItem();
        DatabaseConnection newConnection = (DatabaseConnection) item.getConnection();
        Assert.assertTrue(newConnection.getSID().equalsIgnoreCase(oldConnection.getSID()));
        Assert.assertTrue(newConnection.getUiSchema().equalsIgnoreCase(schema1.getName()));
        Assert.assertTrue(SchemaHelper.getSchema(newConnection, schema1.getName()) != null);
    }

    /**
     * Test method for {@link org.talend.dataprofiler.core.ui.action.actions.ExportConnectionToTOSAction#run()}.
     * 
     * @throws PersistenceException
     * 
     * case4: create new connection from only Catalog case
     */
    @Test
    public void testRuncase4() throws PersistenceException {
        // connectionNode
        DatabaseConnectionItem createOldConnectionItem = createConnectionItem("conn1", null, false, true);
        DatabaseConnection oldConnection = ((DatabaseConnection) createOldConnectionItem.getConnection());
        oldConnection.setSID("orcl"); //$NON-NLS-1$
        initCatalogList(createOldConnectionItem);

        // ~connectionNode
        List<Package> packageList = new ArrayList<Package>();
        packageList.add(catalog1);
        createAction = PowerMockito.spy(new ExportConnectionToTOSAction(packageList));
        PowerMockito.doNothing().when(createAction).openSuccessInformation();
        PowerMockito.doNothing().when(createAction).refreshViewerAndNode();
        // Connection oldConnection = ConnectionHelper.getConnection(schema1);
        DatabaseConnectionItem createConnectionItem = createConnectionItem(oldConnection.getName() + "_" + catalog1.getName(),
                null, false, false);
        DatabaseConnection tempConnection = (DatabaseConnection) createConnectionItem.getConnection();
        // tempConnection.setUiSchema(schema1.getName());
        tempConnection.setSID("orcl"); //$NON-NLS-1$
        ConnectionHelper.addCatalog(EcoreUtil.copy(catalog1), tempConnection);

        PowerMockito.doReturn(tempConnection).when(createAction)
                .fillCatalogSchema(Mockito.argThat(new MetadataConnectionMatcher(catalog1)));
        createAction.run();
        List<IRepositoryViewObject> all = factory.getAll(ERepositoryObjectType.METADATA_CONNECTIONS, false);
        Assert.assertTrue(all.size() == 2);
        IRepositoryViewObject lastVersion = all.get(1);
        DatabaseConnectionItem item = (DatabaseConnectionItem) lastVersion.getProperty().getItem();
        DatabaseConnection newConnection = (DatabaseConnection) item.getConnection();
        Assert.assertTrue(newConnection.getSID().equalsIgnoreCase(oldConnection.getSID()));
        Assert.assertTrue(newConnection.getUiSchema() == null);
        Assert.assertTrue(CatalogHelper.getCatalog(newConnection, catalog1.getName()) != null);
    }

    class MetadataConnectionMatcher extends BaseMatcher<IMetadataConnection> {

        private Package comparePackage = null;

        public MetadataConnectionMatcher(Package comparePackage) {
            this.comparePackage = comparePackage;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.hamcrest.Matcher#matches(java.lang.Object)
         */
        @Override
        public boolean matches(Object item) {
            if (comparePackage == null || !(item instanceof IMetadataConnection)) {
                return false;
            }
            DatabaseConnection connection = (DatabaseConnection) ConnectionHelper.getConnection(comparePackage);

            String compareLabel = connection.getName() + "_" + comparePackage.getName(); //$NON-NLS-1$
            String compareUiSchema = ""; //$NON-NLS-1$
            String compareDatabase = comparePackage.getName();
            if (comparePackage instanceof Schema) {
                Package parent = ColumnSetHelper.getParentCatalogOrSchema(comparePackage);
                if (parent != null) {
                    compareDatabase = parent.getName();
                } else {
                    compareDatabase = connection.getSID();
                }
                compareUiSchema = comparePackage.getName();
            }

            IMetadataConnection metadataConneciton = (IMetadataConnection) item;

            if (!compareLabel.equalsIgnoreCase(metadataConneciton.getLabel())) {
                return false;
            }
            if (!compareDatabase.equalsIgnoreCase(metadataConneciton.getDatabase())) {
                return false;
            }
            if (!compareUiSchema.equalsIgnoreCase(metadataConneciton.getUiSchema())) {
                return false;
            }
            return true;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.hamcrest.SelfDescribing#describeTo(org.hamcrest.Description)
         */
        @Override
        public void describeTo(Description description) {
            // TODO Auto-generated method stub

        }

    }

}
