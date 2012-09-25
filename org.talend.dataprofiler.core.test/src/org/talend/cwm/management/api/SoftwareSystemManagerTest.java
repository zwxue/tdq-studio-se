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
package org.talend.cwm.management.api;

import static org.junit.Assert.*;

import java.util.List;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.jfree.util.Log;
import org.junit.Before;
import org.junit.Test;
import org.talend.commons.exception.PersistenceException;
import org.talend.core.model.metadata.builder.connection.ConnectionFactory;
import org.talend.core.model.metadata.builder.connection.DatabaseConnection;
import org.talend.core.model.properties.DatabaseConnectionItem;
import org.talend.core.model.properties.PropertiesFactory;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.cwm.helper.ConnectionHelper;
import org.talend.cwm.softwaredeployment.SoftwaredeploymentFactory;
import org.talend.cwm.softwaredeployment.TdSoftwareSystem;
import org.talend.dataprofiler.core.helper.UnitTestBuildHelper;
import org.talend.dq.writer.EMFSharedResources;
import orgomg.cwm.foundation.softwaredeployment.Component;

/**
 * created by talend on Sep 19, 2012 Detailled comment
 * 
 */
public class SoftwareSystemManagerTest {

    Logger log = Logger.getLogger(SoftwareSystemManagerTest.class);

    DatabaseConnection createConnection = null;

    /**
     * DOC talend Comment method "setUp".
     * 
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        UnitTestBuildHelper.deleteCurrentProject();
        UnitTestBuildHelper.createRealProject("testForSoftWareTDQ"); //$NON-NLS-1$
        createConnection("conn1", null, false); //$NON-NLS-1$
        EMFSharedResources.getInstance().getSoftwareDeploymentResource().unload();

    }

    /**
     * Test method for
     * {@link org.talend.cwm.management.api.SoftwareSystemManager#getSoftwareSystemFromModel(org.talend.core.model.metadata.builder.connection.Connection)}
     * . fail case : connection is null
     */
    @Test
    public void testGetSoftwareSystemFromModelfail1() {

        TdSoftwareSystem softwareSystemFromModel = SoftwareSystemManager.getInstance().getSoftwareSystemFromModel(null);
        assert (softwareSystemFromModel == null);
    }

    /**
     * Test method for
     * {@link org.talend.cwm.management.api.SoftwareSystemManager#getSoftwareSystemFromModel(org.talend.core.model.metadata.builder.connection.Connection)}
     * . fail case : resource of the Contents is not TdSoftwareSystem
     */
    @Test
    public void testGetSoftwareSystemFromModelfail2() {
        EMFSharedResources.getInstance().getSoftwareDeploymentResource().getContents().add(createConnection);
        EMFSharedResources.getInstance().saveSoftwareDeploymentResource();

        TdSoftwareSystem softwareSystemFromModel = SoftwareSystemManager.getInstance().getSoftwareSystemFromModel(
                createConnection);
        assert (softwareSystemFromModel == null);
    }

    /**
     * Test method for
     * {@link org.talend.cwm.management.api.SoftwareSystemManager#getSoftwareSystemFromModel(org.talend.core.model.metadata.builder.connection.Connection)}
     * . fail case : the size of OwnedElement is zero
     */
    @Test
    public void testGetSoftwareSystemFromModelfail3() {
        // TdSoftwareSystem
        TdSoftwareSystem createTdSoftwareSystem = SoftwaredeploymentFactory.eINSTANCE.createTdSoftwareSystem();
        // ~TdSoftwareSystem
        SoftwareSystemManager.saveSoftwareSystem(createTdSoftwareSystem);

        TdSoftwareSystem softwareSystemFromModel = SoftwareSystemManager.getInstance().getSoftwareSystemFromModel(
                createConnection);
        assert (softwareSystemFromModel == null);
    }

    /**
     * Test method for
     * {@link org.talend.cwm.management.api.SoftwareSystemManager#getSoftwareSystemFromModel(org.talend.core.model.metadata.builder.connection.Connection)}
     * . fail case : the size of Deployment is zero
     */
    @Test
    public void testGetSoftwareSystemFromModelfail4() {
        // Component
        Component createComponent = orgomg.cwm.foundation.softwaredeployment.SoftwaredeploymentFactory.eINSTANCE
                .createComponent();

        // ~Component
        // TdSoftwareSystem
        TdSoftwareSystem createTdSoftwareSystem = SoftwaredeploymentFactory.eINSTANCE.createTdSoftwareSystem();
        createTdSoftwareSystem.getOwnedElement().add(createComponent);
        // ~TdSoftwareSystem
        SoftwareSystemManager.saveSoftwareSystem(createTdSoftwareSystem);

        TdSoftwareSystem softwareSystemFromModel = SoftwareSystemManager.getInstance().getSoftwareSystemFromModel(
                createConnection);
        assert (softwareSystemFromModel == null);
    }

    /**
     * Test method for
     * {@link org.talend.cwm.management.api.SoftwareSystemManager#getSoftwareSystemFromModel(org.talend.core.model.metadata.builder.connection.Connection)}
     * . fail case : connection is exist but don't same
     */
    @Test
    public void testGetSoftwareSystemFromModelfail5() {
        // Component
        Component createComponent = orgomg.cwm.foundation.softwaredeployment.SoftwaredeploymentFactory.eINSTANCE
                .createComponent();
        createComponent.getDeployment().add(createConnection);
        // ~Component
        // TdSoftwareSystem
        TdSoftwareSystem createTdSoftwareSystem = SoftwaredeploymentFactory.eINSTANCE.createTdSoftwareSystem();
        createTdSoftwareSystem.getOwnedElement().add(createComponent);
        // ~TdSoftwareSystem
        SoftwareSystemManager.saveSoftwareSystem(createTdSoftwareSystem);

        createConnection = ConnectionFactory.eINSTANCE.createDatabaseConnection();
        TdSoftwareSystem softwareSystemFromModel = SoftwareSystemManager.getInstance().getSoftwareSystemFromModel(
                createConnection);
        assert (softwareSystemFromModel == null);
    }

    /**
     * Test method for
     * {@link org.talend.cwm.management.api.SoftwareSystemManager#getSoftwareSystemFromModel(org.talend.core.model.metadata.builder.connection.Connection)}
     * .
     */
    @Test
    public void testGetSoftwareSystemFromModelsuccess() {
        // Component
        Component createComponent = orgomg.cwm.foundation.softwaredeployment.SoftwaredeploymentFactory.eINSTANCE
                .createComponent();
        createComponent.getDeployment().add(createConnection);
        // ~Component

        // TdSoftwareSystem
        TdSoftwareSystem createTdSoftwareSystem = SoftwaredeploymentFactory.eINSTANCE.createTdSoftwareSystem();
        createTdSoftwareSystem.getOwnedElement().add(createComponent);
        // ~TdSoftwareSystem

        ConnectionHelper.setSoftwareSystem(createConnection, createTdSoftwareSystem);

        SoftwareSystemManager.saveSoftwareSystem(createTdSoftwareSystem);
        TdSoftwareSystem softwareSystemFromModel = SoftwareSystemManager.getInstance().getSoftwareSystemFromModel(
                createConnection);
        assert (softwareSystemFromModel != null);
        assert (softwareSystemFromModel.equals(createTdSoftwareSystem));
    }

    /**
     * Test method for
     * {@link org.talend.cwm.management.api.SoftwareSystemManager#cleanSoftWareSystem(org.talend.core.model.metadata.builder.connection.Connection)}
     * .
     */
    @Test
    public void testCleanSoftWareSystemSuccess() {
        // Component
        Component createComponent = orgomg.cwm.foundation.softwaredeployment.SoftwaredeploymentFactory.eINSTANCE
                .createComponent();
        createComponent.getDeployment().add(createConnection);
        // ~Component

        // TdSoftwareSystem
        TdSoftwareSystem createTdSoftwareSystem = SoftwaredeploymentFactory.eINSTANCE.createTdSoftwareSystem();
        TdSoftwareSystem createTdSoftwareSystem1 = SoftwaredeploymentFactory.eINSTANCE.createTdSoftwareSystem();
        createTdSoftwareSystem.getOwnedElement().add(createComponent);
        // ~TdSoftwareSystem

        ConnectionHelper.setSoftwareSystem(createConnection, createTdSoftwareSystem);

        SoftwareSystemManager.saveSoftwareSystem(createTdSoftwareSystem);
        SoftwareSystemManager.saveSoftwareSystem(createTdSoftwareSystem1);
        Resource softwareSystemResource = EMFSharedResources.getInstance().getSoftwareDeploymentResource();
        List<EObject> softwareSystems = softwareSystemResource.getContents();
        assert (softwareSystems.size() == 2);
        boolean isclean = SoftwareSystemManager.getInstance().cleanSoftWareSystem(createConnection);

        assert (softwareSystems.size() == 0);
        assertTrue(isclean);
    }

    /**
     * Test method for
     * {@link org.talend.cwm.management.api.SoftwareSystemManager#cleanSoftWareSystem(org.talend.core.model.metadata.builder.connection.Connection)}
     * case 1: faile because connection is null .
     */
    @Test
    public void testCleanSoftWareSystemFaile1() {

        boolean isclean = SoftwareSystemManager.getInstance().cleanSoftWareSystem(null);

        Resource softwareSystemResource = EMFSharedResources.getInstance().getSoftwareDeploymentResource();
        List<EObject> softwareSystems = softwareSystemResource.getContents();

        assert (softwareSystems.size() == 0);
        assertFalse(isclean);
    }

    /**
     * Test method for
     * {@link org.talend.cwm.management.api.SoftwareSystemManager#cleanSoftWareSystem(org.talend.core.model.metadata.builder.connection.Connection)}
     * case 2: the size of softwareSystems is zero
     */
    @Test
    public void testCleanSoftWareSystemFaile2() {

        boolean isclean = SoftwareSystemManager.getInstance().cleanSoftWareSystem(createConnection);

        Resource softwareSystemResource = EMFSharedResources.getInstance().getSoftwareDeploymentResource();
        List<EObject> softwareSystems = softwareSystemResource.getContents();

        assert (softwareSystems.size() == 0);
        assertFalse(isclean);
    }

    /**
     * Test method for
     * {@link org.talend.cwm.management.api.SoftwareSystemManager#cleanSoftWareSystem(org.talend.core.model.metadata.builder.connection.Connection)}
     * case 3: resource of the Contents is not a TdSoftwareSystem type
     */
    @Test
    public void testCleanSoftWareSystemFaile3() {
        EMFSharedResources.getInstance().getSoftwareDeploymentResource().getContents().add(createConnection);
        EMFSharedResources.getInstance().saveSoftwareDeploymentResource();
        boolean isclean = SoftwareSystemManager.getInstance().cleanSoftWareSystem(createConnection);

        Resource softwareSystemResource = EMFSharedResources.getInstance().getSoftwareDeploymentResource();
        List<EObject> softwareSystems = softwareSystemResource.getContents();

        assert (softwareSystems.size() == 0);
        assertFalse(isclean);
    }

    /**
     * Test method for
     * {@link org.talend.cwm.management.api.SoftwareSystemManager#cleanSoftWareSystem(org.talend.core.model.metadata.builder.connection.Connection)}
     * case 4: the size of OwnedElement is zero
     */
    @Test
    public void testCleanSoftWareSystemFaile4() {

        // TdSoftwareSystem
        TdSoftwareSystem createTdSoftwareSystem = SoftwaredeploymentFactory.eINSTANCE.createTdSoftwareSystem();
        // ~TdSoftwareSystem

        SoftwareSystemManager.saveSoftwareSystem(createTdSoftwareSystem);

        boolean isclean = SoftwareSystemManager.getInstance().cleanSoftWareSystem(createConnection);

        Resource softwareSystemResource = EMFSharedResources.getInstance().getSoftwareDeploymentResource();
        List<EObject> softwareSystems = softwareSystemResource.getContents();

        assert (softwareSystems.size() == 0);
        assertTrue(isclean);
    }

    /**
     * Test method for
     * {@link org.talend.cwm.management.api.SoftwareSystemManager#cleanSoftWareSystem(org.talend.core.model.metadata.builder.connection.Connection)}
     * case 3: the size of Deployment is zero
     */
    @Test
    public void testCleanSoftWareSystemFaile5() {
        // Component
        Component createComponent = orgomg.cwm.foundation.softwaredeployment.SoftwaredeploymentFactory.eINSTANCE
                .createComponent();
        // ~Component
        // TdSoftwareSystem
        TdSoftwareSystem createTdSoftwareSystem = SoftwaredeploymentFactory.eINSTANCE.createTdSoftwareSystem();
        createTdSoftwareSystem.getOwnedElement().add(createComponent);
        // ~TdSoftwareSystem

        // ConnectionHelper.setSoftwareSystem(createConnection, createTdSoftwareSystem);

        SoftwareSystemManager.saveSoftwareSystem(createTdSoftwareSystem);

        boolean isclean = SoftwareSystemManager.getInstance().cleanSoftWareSystem(createConnection);

        Resource softwareSystemResource = EMFSharedResources.getInstance().getSoftwareDeploymentResource();
        List<EObject> softwareSystems = softwareSystemResource.getContents();

        assert (softwareSystems.size() == 0);
        assertTrue(isclean);
    }

    /**
     * Test method for
     * {@link org.talend.cwm.management.api.SoftwareSystemManager#cleanSoftWareSystem(org.talend.core.model.metadata.builder.connection.Connection)}
     * case 3: connection is exist but don't same
     */
    @Test
    public void testCleanSoftWareSystemFaile6() {
        // Component
        Component createComponent = orgomg.cwm.foundation.softwaredeployment.SoftwaredeploymentFactory.eINSTANCE
                .createComponent();
        // ~Component
        // TdSoftwareSystem
        TdSoftwareSystem createTdSoftwareSystem = SoftwaredeploymentFactory.eINSTANCE.createTdSoftwareSystem();
        createTdSoftwareSystem.getOwnedElement().add(createComponent);
        // ~TdSoftwareSystem

        ConnectionHelper.setSoftwareSystem(createConnection, createTdSoftwareSystem);

        SoftwareSystemManager.saveSoftwareSystem(createTdSoftwareSystem);
        createConnection = ConnectionFactory.eINSTANCE.createDatabaseConnection();
        boolean isclean = SoftwareSystemManager.getInstance().cleanSoftWareSystem(createConnection);

        Resource softwareSystemResource = EMFSharedResources.getInstance().getSoftwareDeploymentResource();
        List<EObject> softwareSystems = softwareSystemResource.getContents();

        assert (softwareSystems.size() == 0);
        assertFalse(isclean);
    }

    private void createConnection(String name, IFolder folder, Boolean isDelete) {
        IPath createPath = Path.EMPTY;
        if (folder != null) {
            createPath = new Path(folder.getFullPath().lastSegment());
        }
        // connection
        createConnection = ConnectionFactory.eINSTANCE.createDatabaseConnection();
        createConnection.setName(name);
        createConnection.setUsername("UserName"); //$NON-NLS-1$
        createConnection.setPassword("Password"); //$NON-NLS-1$
        createConnection.setURL("URL"); //$NON-NLS-1$
        // ~connection
        DatabaseConnectionItem createDatabaseConnectionItem = PropertiesFactory.eINSTANCE.createDatabaseConnectionItem();

        org.talend.core.model.properties.Property createDatabaseConnectionProperty = PropertiesFactory.eINSTANCE.createProperty();
        createDatabaseConnectionProperty.setId(EcoreUtil.generateUUID());
        createDatabaseConnectionProperty.setItem(createDatabaseConnectionItem);
        createDatabaseConnectionProperty.setLabel(createConnection.getName());
        createDatabaseConnectionItem.setProperty(createDatabaseConnectionProperty);
        createDatabaseConnectionItem.setConnection(createConnection);
        try {
            ProxyRepositoryFactory.getInstance().create(createDatabaseConnectionItem, createPath, false);
        } catch (PersistenceException e) {
            Log.error(e, e);
            Assert.fail(e.getMessage());
        }
    }
}
