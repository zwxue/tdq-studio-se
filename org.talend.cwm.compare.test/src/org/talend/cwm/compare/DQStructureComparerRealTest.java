// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
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

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.compare.diff.metamodel.ComparisonResourceSnapshot;
import org.eclipse.emf.compare.diff.metamodel.DiffElement;
import org.eclipse.emf.compare.diff.metamodel.DiffFactory;
import org.eclipse.emf.compare.diff.metamodel.DiffModel;
import org.eclipse.emf.compare.diff.metamodel.ModelElementChangeRightTarget;
import org.eclipse.emf.compare.diff.service.DiffService;
import org.eclipse.emf.compare.match.MatchOptions;
import org.eclipse.emf.compare.match.metamodel.MatchModel;
import org.eclipse.emf.compare.match.service.MatchService;
import org.eclipse.emf.compare.util.ModelUtils;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.talend.commons.exception.PersistenceException;
import org.talend.core.database.EDatabaseTypeName;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.metadata.builder.connection.ConnectionFactory;
import org.talend.core.model.metadata.builder.connection.DatabaseConnection;
import org.talend.core.model.properties.DatabaseConnectionItem;
import org.talend.core.model.properties.PropertiesFactory;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.cwm.compare.helper.UnitTestBuildHelper;
import org.talend.cwm.helper.CatalogHelper;
import org.talend.cwm.helper.ConnectionHelper;
import org.talend.dq.writer.EMFSharedResources;
import orgomg.cwm.resource.relational.Catalog;

/**
 * created by talend on Dec 11, 2012 Detailled comment
 * 
 */
public class DQStructureComparerRealTest {

    private final String PROJECTNAME = "testForDQStructureComparerTDQ"; //$NON-NLS-1$

    private String connectionNameLeft = "conn1"; //$NON-NLS-1$

    private String connectionNameRight = "conn2"; //$NON-NLS-1$

    private DatabaseConnectionItem createDatabaseConnectionLeftItem = null;

    private DatabaseConnectionItem createDatabaseConnectionRightItem = null;

    private Resource leftResource = null;

    private Resource rightResource = null;

    private Resource diffResource = null;

    ProxyRepositoryFactory factory = ProxyRepositoryFactory.getInstance();

    Logger log = Logger.getLogger(DQStructureComparerRealTest.class);

    Map<String, Object> options = null;

    /**
     * DOC talend Comment method "setUp".
     * 
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        UnitTestBuildHelper.deleteCurrentProject(PROJECTNAME);
        UnitTestBuildHelper.createRealProject(PROJECTNAME);
        createLeftResource();
        createRightResource();
        createDiffResource();
        options = new HashMap<String, Object>();
        options.put(MatchOptions.OPTION_IGNORE_XMI_ID, true);
        options.put(MatchOptions.OPTION_IGNORE_ID, true);

    }

    /**
     * DOC talend Comment method "createDiffResource".
     */
    private void createDiffResource() {

        MatchModel match = null;
        try {
            match = MatchService.doResourceMatch(leftResource, rightResource, options);
        } catch (InterruptedException e) {
            log.error(e, e);
            Assert.fail(e.getMessage());
        }
        final DiffModel diff = DiffService.doDiff(match);
        EList<DiffElement> ownedElements = diff.getOwnedElements();
        for (DiffElement de : ownedElements) {
            EList<DiffElement> subDiffElements = de.getSubDiffElements();
            for (DiffElement difElement : subDiffElements) {
                if (difElement instanceof ModelElementChangeRightTarget) {
                    ((ModelElementChangeRightTarget) difElement).setLeftParent(leftResource.getContents().get(0));

                }
            }
        }

        final ComparisonResourceSnapshot snapshot = DiffFactory.eINSTANCE.createComparisonResourceSnapshot();
        snapshot.setDate(Calendar.getInstance().getTime());
        snapshot.setMatch(match);
        snapshot.setDiff(diff);
        IFile createDiffResourceFile = DQStructureComparer.getDiffResourceFile();
        final String fullPath = createDiffResourceFile.getLocation().toOSString();
        try {
            ModelUtils.save(snapshot, fullPath);
            diffResource = diff.eResource();
        } catch (IOException e) {
            log.error(e, e);
            Assert.fail(e.getMessage());
        }
    }

    /**
     * DOC talend Comment method "createRightResource".
     */
    private void createRightResource() {
        createDatabaseConnectionRightItem = createConnectionItem(connectionNameRight, null, false);
        createCatalog("catalog2", createDatabaseConnectionRightItem); //$NON-NLS-1$
        rightResource = createDatabaseConnectionRightItem.getConnection().eResource();

    }

    /**
     * DOC talend Comment method "createLeftResource".
     */
    private void createLeftResource() {
        createDatabaseConnectionLeftItem = createConnectionItem(connectionNameLeft, null, false);
        createCatalog("catalog1", createDatabaseConnectionLeftItem); //$NON-NLS-1$
        leftResource = createDatabaseConnectionLeftItem.getConnection().eResource();
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
        createConnection.setDatabaseType(EDatabaseTypeName.MSSQL.getXmlName());

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
            return createDatabaseConnectionItem;
        } catch (PersistenceException e) {
            log.error(e, e);
            Assert.fail(e.getMessage());
        }
        return null;
    }

    /**
     * DOC talend Comment method "createCatalog".
     * 
     * @param catalogName
     */
    private void createCatalog(String catalogName, DatabaseConnectionItem createDatabaseConnectionItem) {
        Connection connection = createDatabaseConnectionItem.getConnection();
        Catalog createCatalog = CatalogHelper.createCatalog(catalogName);
        ConnectionHelper.addCatalog(createCatalog, connection);
        try {
            factory.save(createDatabaseConnectionItem, false);
        } catch (PersistenceException e) {
            log.error(e, e);
            Assert.fail(e.getMessage());
        }
    }

    /**
     * DOC talend Comment method "tearDown".
     * 
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
        // UnitTestBuildHelper.deleteCurrentProject(PROJECTNAME);
    }

    /**
     * Test method for
     * {@link org.talend.cwm.compare.DQStructureComparer#removeResourceFromWorkspace(org.eclipse.emf.ecore.resource.Resource)}
     * .
     */
    @Test
    public void testRemoveResourceFromWorkspace() {
        boolean returnCode = false;

        URI resourceUri = leftResource.getURI();
        Resource resource = EMFSharedResources.getInstance().getResource(resourceUri, false);
        Assert.assertTrue(resource != null);// before Remove resurce don't should is null
        returnCode = DQStructureComparer.removeResourceFromWorkspace(leftResource);
        resource = EMFSharedResources.getInstance().getResource(resourceUri, false);
        Assert.assertTrue(returnCode);
        Assert.assertTrue(resource == null);// after Remove resurce should is null

        resourceUri = rightResource.getURI();
        resource = EMFSharedResources.getInstance().getResource(resourceUri, false);
        Assert.assertTrue(resource != null);// before Remove resurce don't should is null
        returnCode = DQStructureComparer.removeResourceFromWorkspace(rightResource);
        resource = EMFSharedResources.getInstance().getResource(resourceUri, false);
        Assert.assertTrue(returnCode);
        Assert.assertTrue(resource == null);// after Remove resurce should is null
        resourceUri = diffResource.getURI();

        resource = EMFSharedResources.getInstance().getResource(resourceUri, false);
        Assert.assertTrue(resource != null);// before Remove resurce don't should is null
        returnCode = DQStructureComparer.removeResourceFromWorkspace(diffResource);
        resource = EMFSharedResources.getInstance().getResource(resourceUri, false);
        Assert.assertTrue(returnCode);
        Assert.assertTrue(resource == null);// after Remove resurce should is null
        returnCode = DQStructureComparer.removeResourceFromWorkspace(null);
        Assert.assertFalse(returnCode);
    }
}
