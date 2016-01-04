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
package org.talend.cwm.dependencies;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.talend.commons.exception.PersistenceException;
import org.talend.core.GlobalServiceRegister;
import org.talend.core.ITDQItemService;
import org.talend.core.model.properties.DatabaseConnectionItem;
import org.talend.core.model.properties.ItemState;
import org.talend.core.model.properties.Property;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.cwm.helper.ConnectionHelper;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.analysis.AnalysisContext;
import org.talend.dataquality.analysis.AnalysisFactory;
import org.talend.dataquality.analysis.AnalysisResult;
import org.talend.dataquality.helpers.AnalysisHelper;
import org.talend.dataquality.properties.TDQAnalysisItem;
import org.talend.dataquality.properties.impl.PropertiesFactoryImpl;
import org.talend.dq.helper.PropertyHelper;
import org.talend.dq.helper.UnitTestBuildHelper;
import org.talend.dq.writer.impl.ElementWriterFactory;
import org.talend.utils.sugars.TypedReturnCode;
import orgomg.cwm.objectmodel.core.Dependency;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC zshen class global comment. Detailled comment
 */

public class DependenciesHandlerTest {

    @Before
    public void setUp() throws Exception {
        UnitTestBuildHelper.initProjectStructure();
        if (GlobalServiceRegister.getDefault().isServiceRegistered(ITDQItemService.class)) {
            ITDQItemService tdqService = (ITDQItemService) GlobalServiceRegister.getDefault().getService(ITDQItemService.class);
            tdqService.createDQStructor();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#tearDown()
     */
    @After
    public void tearDown() throws Exception {

    }

    @Test
    /**
     * Add by qiongli 2012-5-16,it is for TDQ-5259.
     */
    public void testGetClintDependencyProperty_2() {
        Property property = PropertyHelper.createFolderItemProperty();
        List<Property> clintDependency = DependenciesHandler.getInstance().getClintDependency(property);
        assertTrue(clintDependency.size() == 0);

    }

    /**
     * Test method for
     * {@link org.talend.cwm.dependencies.DependenciesHandler#removeDependenciesBetweenModel(orgomg.cwm.objectmodel.core.ModelElement, orgomg.cwm.objectmodel.core.ModelElement)}
     * .
     */
    @Test
    public void testRemoveDependenciesBetweenModel() {
        ModelElement ana = AnalysisHelper.createAnalysis("ana1"); //$NON-NLS-1$
        ModelElement conn = ConnectionHelper.createDatabaseConnection("conn1"); //$NON-NLS-1$
        EList<Dependency> clientDependencyFirst = ana.getClientDependency();
        assertEquals(0, clientDependencyFirst.size());

        TypedReturnCode<Dependency> setUsageDependencyOn = DependenciesHandler.getInstance().setUsageDependencyOn(ana, conn);
        assertEquals(1, clientDependencyFirst.size());
        EList<ModelElement> supplier = clientDependencyFirst.get(0).getSupplier();
        assertEquals(1, supplier.size());
        assertEquals(conn, supplier.get(0));
        if (setUsageDependencyOn.isOk()) {
            DependenciesHandler.getInstance().removeDependenciesBetweenModel(ana, conn);
        }
        assertEquals(0, clientDependencyFirst.size());
        EList<Dependency> clientDependencyTwo = conn.getClientDependency();
        assertEquals(0, clientDependencyTwo.size());
        setUsageDependencyOn = DependenciesHandler.getInstance().setUsageDependencyOn(conn, ana);
        assertEquals(1, clientDependencyTwo.size());
        supplier = clientDependencyTwo.get(0).getSupplier();
        assertEquals(1, supplier.size());
        assertEquals(ana, supplier.get(0));
        if (setUsageDependencyOn.isOk()) {
            DependenciesHandler.getInstance().removeDependenciesBetweenModel(ana, conn);
        }
        assertEquals(0, clientDependencyTwo.size());
    }

    @Test
    public void testRemoveConnDependencyAndSave() {
        TDQAnalysisItem analysisItem = PropertiesFactoryImpl.eINSTANCE.createTDQAnalysisItem();
        Analysis analysis = AnalysisFactory.eINSTANCE.createAnalysis();
        analysis.setName("testDependAnalysis"); //$NON-NLS-1$
        analysisItem.setAnalysis(analysis);
        Property propertyAna = org.talend.core.model.properties.PropertiesFactory.eINSTANCE.createProperty();
        propertyAna.setId(EcoreUtil.generateUUID());
        propertyAna.setLabel("ana"); //$NON-NLS-1$
        analysisItem.setProperty(propertyAna);
        ItemState itemState = org.talend.core.model.properties.PropertiesFactory.eINSTANCE.createItemState();
        itemState.setDeleted(false);
        analysisItem.setState(itemState);
        AnalysisResult analysisResult1 = AnalysisFactory.eINSTANCE.createAnalysisResult();
        analysis.setResults(analysisResult1);
        AnalysisContext analysisContext = AnalysisFactory.eINSTANCE.createAnalysisContext();
        analysis.setContext(analysisContext);

        try {
            ProxyRepositoryFactory.getInstance().create(analysisItem, Path.EMPTY, false);
        } catch (PersistenceException e) {
            Assert.fail(e.getMessage());
        }

        DatabaseConnectionItem connectionItem = UnitTestBuildHelper.createDatabaseConnectionItem(
                "testDependeceConnection", null, false); //$NON-NLS-1$

        DependenciesHandler.getInstance().setUsageDependencyOn(analysis, connectionItem.getConnection());
        analysisContext.setConnection(connectionItem.getConnection());
        ElementWriterFactory.getInstance().createDataProviderWriter().save(connectionItem, true);
        Assert.assertNotNull(analysis.getContext().getConnection());
        Assert.assertEquals(false, analysis.getClientDependency().isEmpty());
        DependenciesHandler.getInstance().removeConnDependencyAndSave(analysisItem);
        Assert.assertNull(analysis.getContext().getConnection());
        Assert.assertEquals(true, analysis.getClientDependency().isEmpty());

    }

}
