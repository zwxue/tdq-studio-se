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
package org.talend.dataprofiler.core.ui.action.actions.handle;

import junit.framework.Assert;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.jfree.util.Log;
import org.junit.Before;
import org.junit.Test;
import org.talend.commons.exception.BusinessException;
import org.talend.commons.exception.PersistenceException;
import org.talend.core.database.EDatabaseTypeName;
import org.talend.core.model.general.Project;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.metadata.builder.connection.ConnectionFactory;
import org.talend.core.model.metadata.builder.connection.DatabaseConnection;
import org.talend.core.model.properties.DatabaseConnectionItem;
import org.talend.core.model.properties.PropertiesFactory;
import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.cwm.helper.CatalogHelper;
import org.talend.cwm.helper.ConnectionHelper;
import org.talend.cwm.helper.ResourceHelper;
import org.talend.dq.helper.PropertyHelper;
import org.talend.dq.nodes.DBConnectionRepNode;
import org.talend.repository.localprovider.model.LocalRepositoryFactory;
import org.talend.repository.model.IRepositoryNode.ENodeType;
import org.talend.repository.model.RepositoryNode;
import orgomg.cwm.resource.relational.Catalog;

/**
 * created by zshen on Apr 18, 2013 Detailled comment
 * 
 */
public class EMFResourceHandleTest {

    /**
     * DOC zshen Comment method "setUpBeforeClass".
     * 
     * @throws java.lang.Exception
     */

    private static LocalRepositoryFactory repositoryFactory;

    ProxyRepositoryFactory factory = ProxyRepositoryFactory.getInstance();

    private static Project originalProject;

    /**
     * DOC zshen Comment method "setUp".
     * 
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        //        UnitTestBuildHelper.initProjectStructure("testForEMFSharedResourcesDuplicate"); //$NON-NLS-1$
    }

    /**
     * Test method for
     * {@link org.talend.dataprofiler.core.ui.action.actions.handle.EMFResourceHandle#duplicate(java.lang.String)}.
     * 
     * @throws BusinessException
     * @throws PersistenceException
     */
    @Test
    public void testDuplicate() throws BusinessException, PersistenceException {
        // connectionNode
        DatabaseConnectionItem oldConnectionItem = createConnectionItem("EMFResourceHandleTest_conn1", null, false); //$NON-NLS-1$
        addDatapackage("catalog1", oldConnectionItem); //$NON-NLS-1$
        RepositoryNode dbParentRepNode = createConnectionNode(oldConnectionItem);

        // ~connectionNode

        IDuplicateHandle createDuplicateHandle = ActionHandleFactory.createDuplicateHandle(dbParentRepNode);

        IFile duplicate = createDuplicateHandle.duplicate("copy_of_EMFResourceHandleTest_conn1"); //$NON-NLS-1$
        Property newProperty = PropertyHelper.getProperty(duplicate);
        IRepositoryViewObject newViewObject = factory.getLastVersion(newProperty.getId());
        DatabaseConnectionItem newConnectionItem = (DatabaseConnectionItem) newViewObject.getProperty().getItem();
        compareResult(newConnectionItem, oldConnectionItem);

    }

    /**
     * DOC zshen Comment method "compareResult".
     * 
     * @param newConnectionItem
     * @param oldConnectionItem
     */
    private void compareResult(DatabaseConnectionItem newConnectionItem, DatabaseConnectionItem oldConnectionItem) {
        Connection newConnection = newConnectionItem.getConnection();
        Connection oldConnection = oldConnectionItem.getConnection();
        Assert.assertTrue(newConnection != null);
        Assert.assertTrue(newConnection.eResource() != null);
        Assert.assertTrue(!newConnection.eIsProxy());

        Assert.assertTrue(oldConnection != null);
        Assert.assertTrue(oldConnection.eResource() != null);
        Assert.assertTrue(!oldConnection.eIsProxy());

        boolean isConnUUIDSame = StringUtils.equals(ResourceHelper.getUUID(newConnection), ResourceHelper.getUUID(oldConnection));
        Catalog oldCatalog = CatalogHelper.getCatalog(oldConnection, "catalog1"); //$NON-NLS-1$
        Catalog newCatalog = CatalogHelper.getCatalog(newConnection, "catalog1"); //$NON-NLS-1$
        boolean isCatalogUUIDSame = StringUtils.equals(ResourceHelper.getUUID(oldCatalog), ResourceHelper.getUUID(newCatalog));
        Assert.assertFalse(isConnUUIDSame);
        Assert.assertFalse(isCatalogUUIDSame);

    }

    /**
     * DOC zshen Comment method "createConnectionNode".
     * 
     * @return
     */
    private RepositoryNode createConnectionNode(DatabaseConnectionItem createConnectionItem) {
        IRepositoryViewObject connectionViewObject = null;
        try {
            connectionViewObject = factory.getLastVersion(createConnectionItem.getProperty().getId());
        } catch (PersistenceException e) {
            Log.error(e, e);
            Assert.fail(e.getMessage());
        }

        RepositoryNode dbParentRepNode = new DBConnectionRepNode(connectionViewObject, null, ENodeType.REPOSITORY_ELEMENT);
        return dbParentRepNode;
    }

    private DatabaseConnectionItem createConnectionItem(String name, IFolder folder, Boolean isDelete) {
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
            factory.create(createDatabaseConnectionItem, createPath, false);
        } catch (PersistenceException e) {
            Log.error(e, e);
            Assert.fail(e.getMessage());
        }
        return createDatabaseConnectionItem;
    }

    private Catalog addDatapackage(String packageName, DatabaseConnectionItem connItem) throws PersistenceException {
        Connection connection = connItem.getConnection();
        Catalog createCatalog = CatalogHelper.createCatalog(packageName);
        ConnectionHelper.addCatalog(createCatalog, connection);
        factory.save(connItem, null);
        return createCatalog;
    }
}
