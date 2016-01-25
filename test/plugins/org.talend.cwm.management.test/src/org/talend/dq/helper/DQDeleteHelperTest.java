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
package org.talend.dq.helper;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.powermock.api.support.membermodification.MemberMatcher.*;
import static org.powermock.api.support.membermodification.MemberModifier.*;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.junit.Rule;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.talend.commons.utils.WorkspaceUtils;
import org.talend.core.model.general.Project;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.ItemState;
import org.talend.core.model.properties.Property;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.dataquality.properties.TDQReportItem;
import org.talend.repository.ProjectManager;
import org.talend.repository.RepositoryWorkUnit;
import org.talend.repository.model.IRepositoryNode;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC qiongli class global comment. Detailled comment <br/>
 * 
 * $Id: talend.epf 55206 2011-02-15 17:32:14Z mhirt $
 * 
 */
// @RunWith(PowerMockRunner.class)
@PrepareForTest({ DQDeleteHelper.class, PropertyHelper.class, ReportFileHelper.class, EObjectHelper.class, ProjectManager.class,
        WorkspaceUtils.class, ProxyRepositoryFactory.class })
public class DQDeleteHelperTest {

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    /**
     * Test method for
     * {@link org.talend.dq.helper.DQDeleteHelper#deleteRelations(org.talend.core.model.properties.Item)}.
     */
    @Test
    public void testDeleteRelations() {
        TDQReportItem item = mock(TDQReportItem.class);
        IFile file = mock(IFile.class);
        when(file.exists()).thenReturn(false);
        IFolder folder = mock(IFolder.class);
        when(folder.exists()).thenReturn(true);
        Property prop = mock(Property.class);
        when(item.getProperty()).thenReturn(prop);
        PowerMockito.mockStatic(PropertyHelper.class);
        when(PropertyHelper.getItemFile(prop)).thenReturn(file);
        stub(method(ReportFileHelper.class, "getOutputFolder", IFile.class)).toReturn(folder); //$NON-NLS-1$
        PowerMockito.mockStatic(ProjectManager.class);
        ProjectManager mockProjectManager = PowerMockito.mock(ProjectManager.class);
        when(ProjectManager.getInstance()).thenReturn(mockProjectManager);
        Project mockProject = PowerMockito.mock(Project.class);
        when(mockProjectManager.getCurrentProject()).thenReturn(mockProject);
        when(folder.getParent()).thenReturn(null);
        PowerMockito.mockStatic(WorkspaceUtils.class);
        when(WorkspaceUtils.ifolderToFile(folder)).thenReturn(null);
        PowerMockito.mockStatic(ProxyRepositoryFactory.class);
        ProxyRepositoryFactory mockProxyRepositoryFactory = PowerMockito.mock(ProxyRepositoryFactory.class);
        when(ProxyRepositoryFactory.getInstance()).thenReturn(mockProxyRepositoryFactory);
        stub(method(ProxyRepositoryFactory.class, "executeRepositoryWorkUnit", RepositoryWorkUnit.class)); //$NON-NLS-1$
    }

    @Test
    /**
     * have a dependence not in recyclebin
     */
    public void testgetCanNotDeletedNodes_1() {
        List<IRepositoryNode> recybinLs = new ArrayList<IRepositoryNode>();
        ModelElement mod = mock(ModelElement.class);
        List<ModelElement> dependceLs = new ArrayList<ModelElement>();
        dependceLs.add(mod);
        IRepositoryNode nodeAna = mock(IRepositoryNode.class);
        IRepositoryNode nodeRep = mock(IRepositoryNode.class);
        recybinLs.add(nodeAna);
        recybinLs.add(nodeRep);
        PowerMockito.mockStatic(EObjectHelper.class);
        when(EObjectHelper.getDependencyClients(nodeAna)).thenReturn(dependceLs);
        when(EObjectHelper.getDependencyClients(nodeRep)).thenReturn(null);
        Item item = mock(Item.class);
        ItemState state = mock(ItemState.class);
        when(item.getState()).thenReturn(state);
        when(state.isDeleted()).thenReturn(false);
        Property prop = mock(Property.class);
        when(prop.getItem()).thenReturn(item);
        PowerMockito.mockStatic(PropertyHelper.class);
        when(PropertyHelper.getProperty(mod)).thenReturn(prop);
        // replayAll();
        List<IRepositoryNode> canNotDeletedNodes = DQDeleteHelper.getCanNotDeletedNodes(recybinLs, true);
        assertFalse(canNotDeletedNodes.isEmpty());
    }

    @Test
    /**
     * dosen't have dependences or all dependences are in recyclebin.
     */
    public void testcanEmptyRecyBin_2() {
        List<IRepositoryNode> recybinLs = new ArrayList<IRepositoryNode>();
        List<IRepositoryNode> canNotDeletedNodes = DQDeleteHelper.getCanNotDeletedNodes(recybinLs, true);
        assertTrue(canNotDeletedNodes.isEmpty());
        ModelElement mod = mock(ModelElement.class);
        List<ModelElement> dependceLs = new ArrayList<ModelElement>();
        dependceLs.add(mod);
        IRepositoryNode nodeAna = mock(IRepositoryNode.class);
        IRepositoryNode nodeRep = mock(IRepositoryNode.class);
        recybinLs.add(nodeAna);
        recybinLs.add(nodeRep);
        PowerMockito.mockStatic(EObjectHelper.class);
        when(EObjectHelper.getDependencyClients(nodeAna)).thenReturn(dependceLs);
        when(EObjectHelper.getDependencyClients(nodeRep)).thenReturn(null);
        Item item = mock(Item.class);
        ItemState state = mock(ItemState.class);
        when(item.getState()).thenReturn(state);
        when(state.isDeleted()).thenReturn(true);
        Property prop = mock(Property.class);
        when(prop.getItem()).thenReturn(item);
        PowerMockito.mockStatic(PropertyHelper.class);
        when(PropertyHelper.getProperty(mod)).thenReturn(prop);
        canNotDeletedNodes = DQDeleteHelper.getCanNotDeletedNodes(canNotDeletedNodes, true);
        assertTrue(canNotDeletedNodes.isEmpty());
    }
}
