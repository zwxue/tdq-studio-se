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
package org.talend.dataprofiler.core.ui.utils;

import static org.mockito.Mockito.*;
import static org.powermock.api.support.membermodification.MemberMatcher.*;
import static org.powermock.api.support.membermodification.MemberModifier.*;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Path;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.talend.core.model.general.Project;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.cwm.helper.ModelElementHelper;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataquality.helpers.AnalysisHelper;
import org.talend.dataquality.helpers.ReportHelper;
import org.talend.dataquality.reports.AnalysisMap;
import org.talend.dataquality.reports.ReportsFactory;
import org.talend.dataquality.reports.TdReport;
import org.talend.dq.helper.RepositoryNodeHelper;
import org.talend.dq.nodes.ReportRepNode;
import org.talend.repository.ProjectManager;
import org.talend.repository.model.IRepositoryNode;
import org.talend.resource.EResourceConstant;
import org.talend.resource.ResourceManager;

/**
 * DOC yyin class global comment. Detailled comment
 */
@PrepareForTest({ ProjectManager.class, ResourceManager.class, RepositoryNodeHelper.class, CorePlugin.class,
        ProxyRepositoryFactory.class, ModelElementHelper.class })
public class RepNodeUtilsTest {

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    TdReport realReport;

    String oldJrxmlPath = "../../../TDQ_Libraries/JRXML Template/column/column_basic_0.1.jrxml"; //$NON-NLS-1$

    String newJrxmlPath = "TDQ_Libraries/JRXML Template/column/new_column_basic_new_0.1.jrxml"; //$NON-NLS-1$

    String moveJrxmlPath = "TDQ_Libraries/JRXML Template/column_move/new_column_basic_new_0.1.jrxml"; //$NON-NLS-1$

    String newJrxmlResource = "../../../TDQ_Libraries/JRXML Template/column/new_column_basic_new_0.1.jrxml"; //$NON-NLS-1$

    /**
     * can not use the real project to test, because the method RepositoryNodeHelper.getDataProfilingFolderNode will use
     * the UI view to fetch the node. so , can only mock for this test. The report and its analysismap with jrxml is
     * real created, not mocked object.
     * 
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {

        // create a report with user defined jrxml
        realReport = ReportHelper.createReport("report20130124"); //$NON-NLS-1$
        AnalysisMap analysisMap = ReportsFactory.eINSTANCE.createAnalysisMap();
        analysisMap.setReportType(ReportHelper.ReportType.USER_MADE.getLabel());
        analysisMap.setAnalysis(AnalysisHelper.createAnalysis("analysis20130124")); //$NON-NLS-1$
        analysisMap.setJrxmlSource(oldJrxmlPath);
        realReport.getAnalysisMap().add(analysisMap);

        List<ReportRepNode> repNodes = new ArrayList<ReportRepNode>();
        ReportRepNode repNode = mock(ReportRepNode.class);
        repNodes.add(repNode);
        IRepositoryViewObject view = mock(IRepositoryViewObject.class);
        Property prop = mock(Property.class);
        Item item = mock(Item.class);

        when(prop.getItem()).thenReturn(item);
        when(view.getProperty()).thenReturn(prop);
        when(repNode.getObject()).thenReturn(view);
        when(repNode.getReport()).thenReturn(realReport);

        ProjectManager manager = mock(ProjectManager.class);
        stub(method(ProjectManager.class, "getInstance")).toReturn(manager); //$NON-NLS-1$

        Project project = mock(Project.class);
        when(manager.getCurrentProject()).thenReturn(project);

        PowerMockito.mock(ResourceManager.class);
        IProject iproject = mock(IProject.class);
        when(iproject.getLocation()).thenReturn(new Path("/opt/runtime/tdqee/a1/")); //$NON-NLS-1$
        stub(method(ResourceManager.class, "getRootProject")).toReturn(iproject); //$NON-NLS-1$

        IFile ifile = mock(IFile.class);
        when(iproject.getFile(newJrxmlPath)).thenReturn(ifile);
        when(ifile.getLocation()).thenReturn(new Path("/opt/runtime/tdqee/a1/" + newJrxmlPath)); //$NON-NLS-1$

        PowerMockito.mockStatic(ModelElementHelper.class);
        IFile ifile2 = mock(IFile.class);
        when(ModelElementHelper.getIFile(realReport)).thenReturn(ifile2);
        when(ifile2.getLocation())
                .thenReturn(new Path("/opt/runtime/tdqee/a1/TDQ_Data Profiling/Reports/report20130124_0.1.rep")); //$NON-NLS-1$

        IFile ifile3 = mock(IFile.class);
        when(iproject.getFile(moveJrxmlPath)).thenReturn(ifile3);
        when(ifile3.getLocation()).thenReturn(new Path("/opt/runtime/tdqee/a1/" + moveJrxmlPath)); //$NON-NLS-1$

        PowerMockito.mock(RepositoryNodeHelper.class);
        IRepositoryNode ReportRootFolderNode = mock(IRepositoryNode.class);
        stub(method(RepositoryNodeHelper.class, "getDataProfilingFolderNode", EResourceConstant.class)).toReturn( //$NON-NLS-1$
                ReportRootFolderNode);
        stub(method(RepositoryNodeHelper.class, "getReportRepNodes", IRepositoryNode.class, boolean.class, boolean.class)) //$NON-NLS-1$
                .toReturn(repNodes);

        ProxyRepositoryFactory repFactory = mock(ProxyRepositoryFactory.class);
        PowerMockito.mockStatic(ProxyRepositoryFactory.class);
        when(ProxyRepositoryFactory.getInstance()).thenReturn(repFactory);
        stub(method(ProxyRepositoryFactory.class, "save", Project.class, Item.class, boolean.class)); //$NON-NLS-1$
    }

    /**
     * DOC yyin Comment method "tearDown".
     * 
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
        // do nothing until now
    }

    /**
     * Test for normal case: change the jrxml source in the report which use it. Test method for
     * {@link org.talend.dataprofiler.core.ui.utils.RepNodeUtils#updateJrxmlRelatedReport(org.eclipse.core.runtime.IPath, org.eclipse.core.runtime.IPath)}
     * .
     */
    @Test
    public void testUpdateJrxmlRelatedReportIPath() {

        // before update, the jrxml in the report is old.
        for (AnalysisMap anaMap : realReport.getAnalysisMap()) {
            Assert.assertTrue(anaMap.getJrxmlSource().equals(oldJrxmlPath));
        }
        RepNodeUtils.updateJrxmlRelatedReport(new Path(oldJrxmlPath), new Path(newJrxmlPath));
        // after update, the jrxml in the report should be changed to new
        for (AnalysisMap anaMap : realReport.getAnalysisMap()) {
            Assert.assertTrue(anaMap.getJrxmlSource().equals(this.newJrxmlResource));
        }
    }

    /**
     * Test for rename case: not change the jrxml source in the report which NOT use it. Test method for
     * {@link org.talend.dataprofiler.core.ui.utils.RepNodeUtils#updateJrxmlRelatedReport(org.eclipse.core.runtime.IPath, org.eclipse.core.runtime.IPath)}
     * .
     */
    @Test
    public void testUpdateJrxmlRelatedReportIPath_2() {
        String jrxmlPath = "TDQ_Libraries/JRXML Template/column/column_0.1.jrxml"; //$NON-NLS-1$
        // before update, the jrxml in the report
        for (AnalysisMap anaMap : realReport.getAnalysisMap()) {
            Assert.assertTrue(anaMap.getJrxmlSource().equals(oldJrxmlPath));
        }
        RepNodeUtils.updateJrxmlRelatedReport(new Path(jrxmlPath), new Path(newJrxmlPath));
        // after update, the jrxml in the report should NOT be changed, because this report didnot user the renamed
        // jrxml.
        for (AnalysisMap anaMap : realReport.getAnalysisMap()) {
            Assert.assertTrue(anaMap.getJrxmlSource().equals(oldJrxmlPath));
        }
    }

    /**
     * Test for move jrxml file case: move the jrxml source, the in the report which use it will also be updated. Test
     * method for
     * {@link org.talend.dataprofiler.core.ui.utils.RepNodeUtils#updateJrxmlRelatedReport(org.eclipse.core.runtime.IPath, org.eclipse.core.runtime.IPath)}
     * .
     */
    @Test
    public void testUpdateJrxmlRelatedReportIPath_move() {

        // before update, the jrxml in the report is old.
        for (AnalysisMap anaMap : realReport.getAnalysisMap()) {
            Assert.assertTrue(anaMap.getJrxmlSource().equals(oldJrxmlPath));
        }
        RepNodeUtils.updateJrxmlRelatedReport(new Path(oldJrxmlPath), new Path(this.moveJrxmlPath));
        // after update, the jrxml in the report should be changed to new
        for (AnalysisMap anaMap : realReport.getAnalysisMap()) {
            Assert.assertTrue(anaMap.getJrxmlSource().equals("../../../" + this.moveJrxmlPath)); //$NON-NLS-1$
        }
    }

}
