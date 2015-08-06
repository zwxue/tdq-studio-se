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
package org.talend.dataprofiler.core.ui.utils;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EObjectWithInverseResolvingEList;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.talend.commons.utils.WorkspaceUtils;
import org.talend.core.model.metadata.builder.connection.DatabaseConnection;
import org.talend.cwm.dependencies.DependenciesHandler;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dq.helper.RepositoryNodeHelper;
import orgomg.cwm.foundation.softwaredeployment.DataProvider;
import orgomg.cwm.objectmodel.core.Dependency;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC yyin class global comment. Detailled comment
 */
@PrepareForTest({ RepositoryNodeHelper.class })
public class WorkbenchUtilsTest {

    /**
     * Test method for
     * {@link org.talend.dataprofiler.core.ui.utils.WorkbenchUtils#impactExistingAnalyses(orgomg.cwm.foundation.softwaredeployment.DataProvider)}
     * .
     */
    @Test
    public void testImpactExistingAnalyses_1() {
        DatabaseConnection mock_data = mock(DatabaseConnection.class);
        Resource mock_Resource = mock(Resource.class);
        when(((ModelElement) mock_data).eResource()).thenReturn(mock_Resource);

        Analysis mock_ana = mock(Analysis.class);
        EList<Dependency> clients = mock(EObjectWithInverseResolvingEList.class);
        Iterator<Dependency> mockIterator = mock(Iterator.class);
        when(mockIterator.hasNext()).thenReturn(true).thenReturn(false);
        Dependency dependency = mock(Dependency.class);
        when(mockIterator.next()).thenReturn(dependency);
        when(dependency.eResource()).thenReturn(mock_Resource);

        when(clients.iterator()).thenReturn(mockIterator);
        when(mock_ana.getClientDependency()).thenReturn(clients);

        List<Resource> result = this.method_impactExistingAnalyses(mock_data, mock_ana);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    /**
     * Test method for
     * {@link org.talend.dataprofiler.core.ui.utils.WorkbenchUtils#impactExistingAnalyses(orgomg.cwm.foundation.softwaredeployment.DataProvider)}
     * .
     */
    // when getClientDependency return null
    @Test
    public void testImpactExistingAnalyses_2() {
        DatabaseConnection mock_data = mock(DatabaseConnection.class);
        Resource mock_Resource = mock(Resource.class);
        when(((ModelElement) mock_data).eResource()).thenReturn(mock_Resource);

        Analysis mock_ana = mock(Analysis.class);

        when(mock_ana.getClientDependency()).thenReturn(null);

        List<Resource> result = this.method_impactExistingAnalyses(mock_data, mock_ana);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    private List<Resource> method_impactExistingAnalyses(DataProvider oldDataProvider, Analysis analysis) {
        // MOD yyin 20120410, bug 4753
        List<ModelElement> tempList = new ArrayList<ModelElement>();
        tempList.add(oldDataProvider);
        return DependenciesHandler.getInstance().removeDependenciesBetweenModels(analysis, tempList);
    }

    /**
     * Test method for {@link org.talend.commons.utils.WorkspaceUtils#normalize(String)}.
     */
    @Test
    public void testNormalize() {
        assertEquals(null, WorkspaceUtils.normalize(null));
        assertEquals("_", WorkspaceUtils.normalize("?")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("_test", WorkspaceUtils.normalize("*test")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("_test_", WorkspaceUtils.normalize("<test>")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("test_", WorkspaceUtils.normalize("test?")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("test__", WorkspaceUtils.normalize("test'\"")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("_test_", WorkspaceUtils.normalize("(test)")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("___________", WorkspaceUtils.normalize("#^&/:;\\~.! ")); //$NON-NLS-1$ //$NON-NLS-2$
    }
}
