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
package org.talend.dq.nodes;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Path;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.talend.core.runtime.CoreRuntimePlugin;
import org.talend.dq.helper.ReportUtils;
import org.talend.dq.helper.resourcehelper.ResourceFileMap;
import org.talend.dq.nodes.ReportSubFolderRepNode.ReportSubFolderType;
import org.talend.repository.ProjectManager;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.IRepositoryNode.ENodeType;
import orgomg.cwmx.analysis.informationreporting.Report;

/**
 * DOC qiongli class global comment. Detailled comment <br/>
 * 
 * $Id: talend.epf 55206 2011-02-15 17:32:14Z mhirt $
 * 
 */
@PrepareForTest({ ReportUtils.class, ResourceFileMap.class, IExtensionRegistry.class, IConfigurationElement.class,
        ProjectManager.class, CoreRuntimePlugin.class })
public class ReportSubFolderRepNodeTest {

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    private ReportSubFolderRepNode reportSubRepNode;

    private Report report;

    /**
     * DOC qiongli Comment method "setUp".
     * 
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        reportSubRepNode = new ReportSubFolderRepNode(null, null, ENodeType.REPOSITORY_ELEMENT);
        reportSubRepNode.setFiltering(false);
        report = mock(Report.class);
        reportSubRepNode.setReport(report);

    }

    /**
     * DOC qiongli Comment method "tearDown".
     * 
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test method for {@link org.talend.dq.nodes.ReportSubFolderRepNode#getChildren()}.
     * 
     * @throws Exception
     */
    @Test
    public void testGetChildren() throws Exception {
        reportSubRepNode.setReportSubFolderType(ReportSubFolderType.GENERATED_DOCS);
        mockForGetChildren();
        List<IRepositoryNode> children = reportSubRepNode.getChildren();
        assertFalse(children.isEmpty());
        assertTrue(children.size() == 3);
    }

    /**
     * Test method for {@link org.talend.dq.nodes.ReportSubFolderRepNode#getLabel()}.
     */
    @Test
    public void testGetLabel() {
        // fail("Not yet implemented");
    }

    /**
     * Test method for {@link org.talend.dq.nodes.ReportSubFolderRepNode#isVirtualFolder()}.
     */
    @Test
    public void testIsVirtualFolder() {
        // fail("Not yet implemented");
    }

    /**
     * Test method for {@link org.talend.dq.nodes.ReportSubFolderRepNode#getReportSubFolderType()}.
     */
    @Test
    public void testGetReportSubFolderType() {
        // fail("Not yet implemented");
    }

    /**
     * Test method for
     * {@link org.talend.dq.nodes.ReportSubFolderRepNode#setReportSubFolderType(org.talend.dq.nodes.ReportSubFolderRepNode.ReportSubFolderType)}
     * .
     */
    @Test
    public void testSetReportSubFolderType() {
        // fail("Not yet implemented");
    }

    /**
     * Test method for {@link org.talend.dq.nodes.ReportSubFolderRepNode#getReport()}.
     */
    @Test
    public void testGetReport() {
        // fail("Not yet implemented");
    }

    /**
     * Test method for
     * {@link org.talend.dq.nodes.ReportSubFolderRepNode#setReport(orgomg.cwmx.analysis.informationreporting.Report)}.
     */
    @Test
    public void testSetReport() {
        // fail("Not yet implemented");
    }

    /**
     * Test method for {@link org.talend.dq.nodes.ReportSubFolderRepNode#getReportSubFolderChildren()}.
     */
    @Test
    public void testGetReportSubFolderChildren() {
        // fail("Not yet implemented");
    }

    /**
     * Test method for
     * {@link org.talend.dq.nodes.ReportSubFolderRepNode#ReportSubFolderRepNode(org.talend.core.model.repository.IRepositoryViewObject, org.talend.repository.model.RepositoryNode, org.talend.repository.model.IRepositoryNode.ENodeType)}
     * .
     */
    @Test
    public void testReportSubFolderRepNode() {
        // fail("Not yet implemented");
    }

    /**
     * Test method for {@link org.talend.dq.nodes.ReportSubFolderRepNode#getCount()}.
     * 
     * @throws Exception
     */
    @Test
    public void testGetCount_1() throws Exception {
        // ReportSubFolderRepNode reportSubRepNode = mock(ReportSubFolderRepNode.class);
        reportSubRepNode.setReportSubFolderType(ReportSubFolderType.GENERATED_DOCS);
        mockForGetChildren();
        String count = reportSubRepNode.getCount();
        count = count.replace("(", "").replace(")", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        assertTrue(Integer.parseInt(count) == 3);

    }

    /**
     * Test method for {@link org.talend.dq.nodes.ReportSubFolderRepNode#getCount()}.
     * 
     * @throws Exception
     */
    @Test
    public void testGetCount_2() throws Exception {
        List<IRepositoryNode> ls = new ArrayList<IRepositoryNode>();
        IRepositoryNode repositoryNode = mock(IRepositoryNode.class);
        ls.add(repositoryNode);
        reportSubRepNode.getReportSubFolderChildren().addAll(ls);

        String count = reportSubRepNode.getCount();
        count = count.replace("(", "").replace(")", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        assertTrue(Integer.parseInt(count) == 1);

    }

    private void mockForGetChildren() throws Exception {
        IResource[] res = new IResource[3];
        for (int i = 0; i < 3; i++) {
            IFile fe = mock(IFile.class);
            when(fe.getFullPath()).thenReturn(new Path("")); //$NON-NLS-1$
            res[i] = fe;
        }
        PowerMockito.mockStatic(ReportUtils.class);
        IFile repFile = mock(IFile.class);
        when(ReportUtils.getReportGeneratedDocs(repFile)).thenReturn(res);
        PowerMockito.mockStatic(ResourceFileMap.class);
        when(ResourceFileMap.findCorrespondingFile(report)).thenReturn(repFile);

        PowerMockito.mockStatic(ProjectManager.class);
        ProjectManager projManager = mock(ProjectManager.class);
        //        when(projManager.getProjectNode("")).thenReturn(null); //$NON-NLS-1$
        PowerMockito.mockStatic(CoreRuntimePlugin.class);
        CoreRuntimePlugin coreRunPlugin = mock(CoreRuntimePlugin.class);
        when(CoreRuntimePlugin.getInstance()).thenReturn(coreRunPlugin).thenReturn(coreRunPlugin).thenReturn(coreRunPlugin);
        when(ProjectManager.getInstance()).thenReturn(projManager).thenReturn(projManager).thenReturn(projManager);
    }
}
