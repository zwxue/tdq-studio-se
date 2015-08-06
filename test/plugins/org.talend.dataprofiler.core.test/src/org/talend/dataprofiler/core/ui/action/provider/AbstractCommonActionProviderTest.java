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
package org.talend.dataprofiler.core.ui.action.provider;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.actions.ActionContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.dq.helper.RepositoryNodeHelper;
import org.talend.dq.nodes.AnalysisRepNode;
import org.talend.dq.nodes.DQRepositoryNode;
import org.talend.dq.nodes.ReportRepNode;

/**
 * created by qiongli on 2012-9-18 Detailled comment
 * 
 */
@PrepareForTest({ RepositoryNodeHelper.class })
public class AbstractCommonActionProviderTest {

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    private AbstractCommonActionProvider absCommonActionProvider = null;

    TreeSelection treeSel = null;

    /**
     * DOC qiongli Comment method "setUp".
     * 
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        absCommonActionProvider = new AbstractCommonActionProvider();

        ActionContext context = mock(ActionContext.class);
        treeSel = mock(TreeSelection.class);
        when(context.getSelection()).thenReturn(treeSel);
        absCommonActionProvider.setContext(context);
        PowerMockito.mockStatic(RepositoryNodeHelper.class);
    }

    /**
     * Test method for different type nodes(analysis node and report node) .
     */
    @Test
    public void testisSelectionSameType_1() {

        AnalysisRepNode anaNode = mock(AnalysisRepNode.class);
        when(anaNode.getContentType()).thenReturn(ERepositoryObjectType.TDQ_ANALYSIS_ELEMENT);
        ReportRepNode repNode = mock(ReportRepNode.class);
        when(repNode.getContentType()).thenReturn(ERepositoryObjectType.TDQ_REPORT_ELEMENT);
        Object objs[] = new Object[2];
        objs[0] = anaNode;
        objs[1] = repNode;
        when(treeSel.toArray()).thenReturn(objs);
        boolean flag = absCommonActionProvider.isSelectionSameType();
        assertFalse(flag);
    }

    /**
     * Test method for the same type nodes(analysis nodes) .
     */
    @Test
    public void testisSelectionSameType_2() {

        AnalysisRepNode anaNode = mock(AnalysisRepNode.class);
        when(anaNode.getContentType()).thenReturn(ERepositoryObjectType.TDQ_ANALYSIS_ELEMENT);
        AnalysisRepNode anaNode2 = mock(AnalysisRepNode.class);
        when(anaNode2.getContentType()).thenReturn(ERepositoryObjectType.TDQ_ANALYSIS_ELEMENT);
        Object objs[] = new Object[2];
        objs[0] = anaNode;
        objs[1] = anaNode2;
        when(treeSel.toArray()).thenReturn(objs);
        boolean flag = absCommonActionProvider.isSelectionSameType();
        assertTrue(flag);
        // there are two nodes in recycle bin
        DQRepositoryNode node1 = mock(DQRepositoryNode.class);
        when(node1.getContentType()).thenReturn(null);
        DQRepositoryNode node2 = mock(DQRepositoryNode.class);
        when(node2.getContentType()).thenReturn(null);
        objs[0] = node1;
        objs[1] = node2;
        when(treeSel.toArray()).thenReturn(objs);

        when(RepositoryNodeHelper.isStateDeleted(node1)).thenReturn(true);
        when(RepositoryNodeHelper.isStateDeleted(node2)).thenReturn(true);
        flag = absCommonActionProvider.isSelectionSameType();
        assertTrue(flag);
    }

    /**
     * Test method for a node is in recycle bin,another one is not in recycle bin .
     */
    @Test
    public void testisSelectionSameType_3() {

        DQRepositoryNode node1 = mock(DQRepositoryNode.class);
        when(node1.getContentType()).thenReturn(null);
        when(RepositoryNodeHelper.isStateDeleted(node1)).thenReturn(true);

        ReportRepNode repNode = mock(ReportRepNode.class);
        when(repNode.getContentType()).thenReturn(ERepositoryObjectType.TDQ_REPORT_ELEMENT);
        when(RepositoryNodeHelper.isStateDeleted(repNode)).thenReturn(false);
        Object objs[] = new Object[2];
        objs[0] = node1;
        objs[1] = repNode;
        when(treeSel.toArray()).thenReturn(objs);
        boolean flag = absCommonActionProvider.isSelectionSameType();
        assertFalse(flag);
    }
}
