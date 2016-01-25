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
package org.talend.dataprofiler.core.migration.impl;

import static org.junit.Assert.*;
import junit.framework.Assert;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.jfree.util.Log;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.talend.commons.exception.PersistenceException;
import org.talend.core.context.Context;
import org.talend.core.context.RepositoryContext;
import org.talend.core.model.general.Project;
import org.talend.core.model.metadata.builder.connection.ConnectionFactory;
import org.talend.core.model.metadata.builder.connection.DatabaseConnection;
import org.talend.core.model.properties.DatabaseConnectionItem;
import org.talend.core.model.properties.PropertiesFactory;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.core.runtime.CoreRuntimePlugin;
import org.talend.dataprofiler.core.helper.UnitTestBuildHelper;
import org.talend.repository.localprovider.model.LocalRepositoryFactory;

/**
 * DOC qiongli Test case for split system indicators.
 */
public class UpdateMsSqlToJdbcTaskTest {

    private static Project originalProject;

    private static LocalRepositoryFactory repositoryFactory;

    ProxyRepositoryFactory factory = ProxyRepositoryFactory.getInstance();

    @BeforeClass
    public static void beforeAllTests() {
        Context ctx = CoreRuntimePlugin.getInstance().getContext();
        RepositoryContext repositoryContext = (RepositoryContext) ctx.getProperty(Context.REPOSITORY_CONTEXT_KEY);
        if (repositoryContext != null) {
            originalProject = repositoryContext.getProject();
        }
        repositoryFactory = new LocalRepositoryFactory();
    }

    @Before
    public void setUp() throws Exception {
        UnitTestBuildHelper.deleteCurrentProject("testForDeleteActionTDQ"); //$NON-NLS-1$
        UnitTestBuildHelper.createRealProject("testForDeleteActionTDQ"); //$NON-NLS-1$
    }

    @AfterClass
    public static void afterAllTests() {
        repositoryFactory = null;
        if (originalProject != null) {
            Context ctx = CoreRuntimePlugin.getInstance().getContext();
            RepositoryContext repositoryContext = (RepositoryContext) ctx.getProperty(Context.REPOSITORY_CONTEXT_KEY);
            repositoryContext.setProject(originalProject);
        }
    }

    @Test
    public void testDoExecute() throws Exception {
        DatabaseConnection dbConn = createConnectionItem("conn1", null); //$NON-NLS-1$
        UpdateMsSqlToJdbcTask updateMsSqlToJdbcTask = new UpdateMsSqlToJdbcTask();
        updateMsSqlToJdbcTask.doExecute();
        assertTrue("General JDBC".equals(dbConn.getDatabaseType())); //$NON-NLS-1$
        assertTrue(dbConn.getDriverJarPath() == null);
        assertTrue("".equals(dbConn.getDriverClass())); //$NON-NLS-1$
    }

    private DatabaseConnection createConnectionItem(String name, IFolder folder) {
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
        createConnection.setDatabaseType("Microsoft SQL Server 2005/2008"); //$NON-NLS-1$
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
            factory.create(createDatabaseConnectionItem, createPath, false);
        } catch (PersistenceException e) {
            Log.error(e, e);
            Assert.fail(e.getMessage());
        }
        return createConnection;
    }

}
