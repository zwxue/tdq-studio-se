// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.utils;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EObjectWithInverseResolvingEList;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.talend.core.model.metadata.builder.connection.DatabaseConnection;
import org.talend.cwm.dependencies.DependenciesHandler;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dq.helper.RepositoryNodeHelper;

import orgomg.cwm.foundation.softwaredeployment.DataProvider;
import orgomg.cwm.objectmodel.core.Dependency;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC yyin  class global comment. Detailled comment
 */
@PrepareForTest({ RepositoryNodeHelper.class })
public class WorkbenchUtilsTest {

    /**
     * DOC yyin Comment method "setUp".
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }

    /**
     * DOC yyin Comment method "tearDown".
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test method for {@link org.talend.dataprofiler.core.ui.utils.WorkbenchUtils#impactExistingAnalyses(orgomg.cwm.foundation.softwaredeployment.DataProvider)}.
     */
    @Test
    public void testImpactExistingAnalyses() {
        DatabaseConnection mock_data = mock(DatabaseConnection.class);
        Resource mock_Resource = mock(Resource.class);
        when(((ModelElement) mock_data).eResource()).thenReturn(mock_Resource);

        // EList<Dependency> clientDependencies = new
        // EObjectWithInverseResolvingEList.ManyInverse<Dependency>(Dependency.class,
        // null, CorePackage.MODEL_ELEMENT__CLIENT_DEPENDENCY, CorePackage.DEPENDENCY__CLIENT_DEPENDENCY);
        // Dependency mock_depend = mock(Dependency.class);
        // clientDependencies.add(mock_depend);
        // when(mock_data.getClientDependency()).thenReturn(clientDependencies);
        // when(mock_data.getSupplierDependency()).thenReturn(clientDependencies);
        //
        Analysis mock_ana = mock(Analysis.class);
        EList<Dependency> clients = mock(EObjectWithInverseResolvingEList.class);
        // new EObjectWithInverseResolvingEList.ManyInverse<ModelElement>(ModelElement.class, null,
        // CorePackage.MODEL_ELEMENT__CLIENT_DEPENDENCY, CorePackage.DEPENDENCY__CLIENT_DEPENDENCY);//
        // mock(EObjectWithInverseResolvingEList.ManyInverse<E>.class);
        Iterator<Dependency> mockIterator = mock(Iterator.class);
        when(mockIterator.hasNext()).thenReturn(true).thenReturn(false);
        Dependency dependency = mock(Dependency.class);
        when(mockIterator.next()).thenReturn(dependency);
        // Resource mockResource = mock(Resource.class);
        when(dependency.eResource()).thenReturn(mock_Resource);

        when(clients.iterator()).thenReturn(mockIterator);
        when(mock_ana.getClientDependency()).thenReturn(clients);
        // when(mock_depend.getClient()).thenReturn(clients);
        //
        // Resource eResource = mock(Resource.class);
        // when(mock_ana.eResource()).thenReturn(eResource);
        // RepositoryNode mockNode = mock(RepositoryNode.class);
        // IRepositoryViewObject mockVO = mock(IRepositoryViewObject.class);
        // try {
        // PowerMockito.doReturn(mockNode).when(RepositoryNodeHelper.class, "recursiveFind", mock_data);
        // when(mockNode.getObject()).thenReturn(mockVO);
        // } catch (Exception e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
        // Property mockP = mock(Property.class);
        // when(mockVO.getProperty()).thenReturn(mockP);
        // org.talend.core.model.properties.Item mockItem = mock(org.talend.core.model.properties.Item.class);
        // when(mockP.getItem()).thenReturn(mockItem);
        
        List<Resource> result = this.method_impactExistingAnalyses(mock_data, mock_ana);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    private List<Resource> method_impactExistingAnalyses(DataProvider oldDataProvider, Analysis analysis) {
        // MOD yyin 20120410, bug 4753
        List<ModelElement> tempList = new ArrayList<ModelElement>();
        tempList.add(oldDataProvider);
        return DependenciesHandler.getInstance().removeDependenciesBetweenModels(analysis, tempList);
        // DependenciesHandler.getInstance().removeSupplierDependenciesBetweenModels(analysis, tempList);

        // IRepositoryViewObject reposViewObject = RepositoryNodeHelper.recursiveFind(oldDataProvider).getObject();
        // ElementWriterFactory.getInstance().createDataProviderWriter().save(reposViewObject.getProperty().getItem(),
        // true);
    }

}
