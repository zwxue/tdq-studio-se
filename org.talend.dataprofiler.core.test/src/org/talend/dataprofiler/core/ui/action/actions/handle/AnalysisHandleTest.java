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
package org.talend.dataprofiler.core.ui.action.actions.handle;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import junit.framework.Assert;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.common.util.EList;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.dataprofiler.core.ui.utils.WorkbenchUtils;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.analysis.AnalysisFactory;
import org.talend.dataquality.analysis.AnalysisParameters;
import org.talend.dataquality.domain.Domain;
import org.talend.dataquality.helpers.AnalysisHelper;
import org.talend.dataquality.helpers.DomainHelper;
import org.talend.dq.helper.RepositoryNodeHelper;
import org.talend.repository.model.IRepositoryNode;
import org.talend.resource.ResourceManager;
import orgomg.cwm.objectmodel.core.Expression;
import orgomg.cwm.objectmodel.core.ModelElement;


/**
 * DOC yyin  class global comment. Detailled comment
 */
@PrepareForTest({ WorkbenchUtils.class, ResourceManager.class, RepositoryNodeHelper.class })
public class AnalysisHandleTest {

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    Analysis analysis1;

    AnalysisHandle handle;
    /**
     * DOC yyin Comment method "setUp".
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        // init the analysis
        analysis1 = AnalysisHelper.createAnalysis("ana");
        AnalysisParameters parameters = AnalysisFactory.eINSTANCE.createAnalysisParameters();
        analysis1.setParameters(parameters);
        EList<Domain> dataFilters = analysis1.getParameters().getDataFilter();
        Domain domain = DomainHelper.createDomain(analysis1.getName());
        AnalysisHelper.setStringDataFilter(analysis1, "table.column is null");
        dataFilters.add(domain);

        // create a handle
        IRepositoryNode node = mock(IRepositoryNode.class);
        IRepositoryViewObject object = mock(IRepositoryViewObject.class);
        when(node.getObject()).thenReturn(object);
        Property pro = mock(Property.class);
        when(object.getProperty()).thenReturn(pro);

        IPath itemPath = mock(IPath.class);
        PowerMockito.mockStatic(WorkbenchUtils.class);
        when(WorkbenchUtils.getFilePath(node)).thenReturn(itemPath); //$NON-NLS-1$

        PowerMockito.mockStatic(ResourceManager.class);
        IWorkspaceRoot root = mock(IWorkspaceRoot.class);
        when(ResourceManager.getRoot()).thenReturn(root);
        when(root.getFile(itemPath)).thenReturn(null);

        PowerMockito.mockStatic(RepositoryNodeHelper.class);
        when(RepositoryNodeHelper.getModelElementFromRepositoryNode(node)).thenReturn(analysis1);

        handle = new AnalysisHandle(node);
    }

    /**
     * DOC yyin Comment method "tearDown".
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test method for {@link org.talend.dataprofiler.core.ui.action.actions.handle.AnalysisHandle#update(orgomg.cwm.objectmodel.core.ModelElement, orgomg.cwm.objectmodel.core.ModelElement)}.
     */
    @Test
    public void testUpdate() {
        Analysis newAnalysis = AnalysisHelper.createAnalysis("new ana");
        AnalysisParameters parameters = AnalysisFactory.eINSTANCE.createAnalysisParameters();
        newAnalysis.setParameters(parameters);

        // make the new analysis use the old domain
        EList<Domain> dataFilters = analysis1.getParameters().getDataFilter();
        EList<Domain> newFilter = parameters.getDataFilter();
        newFilter.add(dataFilters.get(0));

        ModelElement copiedAna = handle.update(analysis1, newAnalysis);

        // after update, the old domain should be replaced by a new one, but with same body& language
        Domain domain = ((Analysis) copiedAna).getParameters().getDataFilter().get(0);
        assertTrue(domain != null);
        Assert.assertNotSame(dataFilters.get(0), domain);
        assertFalse(domain.getName().equals("ana"));

        // the expressions are different instances, with same boby&language.
        Expression ex = domain.getRanges().get(0).getExpressions().getExpression();
        Expression oldex = dataFilters.get(0).getRanges().get(0).getExpressions().getExpression();
        Assert.assertNotSame(oldex, ex);
        assertTrue(ex.getBody().equals(oldex.getBody()));
        assertTrue(ex.getLanguage().equals(oldex.getLanguage()));
    }

}
