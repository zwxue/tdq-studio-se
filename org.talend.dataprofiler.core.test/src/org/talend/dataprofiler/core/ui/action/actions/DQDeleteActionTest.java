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
package org.talend.dataprofiler.core.ui.action.actions;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.powermock.api.support.membermodification.MemberMatcher.*;
import static org.powermock.api.support.membermodification.MemberModifier.*;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.navigator.CommonViewer;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.talend.core.model.properties.ConnectionItem;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.ItemState;
import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.IRepositoryObject;
import org.talend.core.runtime.CoreRuntimePlugin;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.dialog.message.DeleteModelElementConfirmDialog;
import org.talend.dataprofiler.core.ui.views.DQRespositoryView;
import org.talend.dq.helper.EObjectHelper;
import org.talend.dq.helper.PropertyHelper;
import org.talend.dq.helper.RepositoryNodeHelper;
import org.talend.dq.nodes.RecycleBinRepNode;
import org.talend.repository.model.IProxyRepositoryFactory;
import org.talend.repository.model.RepositoryNode;
import org.talend.repository.ui.actions.DeleteAction;
import orgomg.cwm.objectmodel.core.ModelElement;


/**
 * DOC qionli class global comment. Detailled comment <br/>
 * 
 * $Id: talend.epf 55206 2011-02-15 17:32:14Z mhirt $
 * 
 */
// @RunWith(PowerMockRunner.class)
@PrepareForTest({ CorePlugin.class, RepositoryNodeHelper.class, CoreRuntimePlugin.class, IProxyRepositoryFactory.class,
        PropertyHelper.class, DeleteModelElementConfirmDialog.class, EObjectHelper.class })
public class DQDeleteActionTest {

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    private DQDeleteAction dqDeleteAction_real;

    private DeleteAction deleteAction_mock;

    private CorePlugin corePlugin;

    /**
     * DOC qiongli Comment method "setUp".
     * 
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        corePlugin = mock(CorePlugin.class);
        PowerMockito.mockStatic(CorePlugin.class);
        when(CorePlugin.getDefault()).thenReturn(corePlugin);
        dqDeleteAction_real = new DQDeleteAction();
        deleteAction_mock = PowerMockito.spy(dqDeleteAction_real);
        PowerMockito.doReturn(null).when(deleteAction_mock).getActivePage();
        PowerMockito.doNothing().when(deleteAction_mock, "doRun");

        // mock/stub something for superclass AContextualAction.run.
        PowerMockito.mockStatic(CoreRuntimePlugin.class);
        CoreRuntimePlugin coreRuntPlugin = mock(CoreRuntimePlugin.class);
        when(CoreRuntimePlugin.getInstance()).thenReturn(coreRuntPlugin);
        IProxyRepositoryFactory proxFactory = mock(IProxyRepositoryFactory.class);
        when(coreRuntPlugin.getProxyRepositoryFactory()).thenReturn(proxFactory);
    }

    /**
     * DOC Administrator Comment method "tearDown".
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test method run for logical delete.
     * 
     * @throws Exception
     */
    @Test
    public void testRun_1() throws Exception {
         dqDeleteAction_real.setCurrentNode(null);
         DQRespositoryView dqView = mock(DQRespositoryView.class);
         when(corePlugin.getRepositoryView()).thenReturn(dqView);
         CommonViewer commonView = mock(CommonViewer.class);
         List<RepositoryNode> seleLs = new ArrayList<RepositoryNode>();
         RepositoryNode node1 = mock(RepositoryNode.class);
         IRepositoryObject object = mock(IRepositoryObject.class);
         Property prop = mock(Property.class);
         Item item = mock(Item.class);
         ItemState state = mock(ItemState.class);
         when(prop.getItem()).thenReturn(item);
         when(node1.getObject()).thenReturn(object);
         when(object.getProperty()).thenReturn(prop);
         when(item.getState()).thenReturn(state);
         when(state.isDeleted()).thenReturn(false);
         seleLs.add(node1);
        
         ISelection selecetion = new StructuredSelection(seleLs);
         when(dqView.getCommonViewer()).thenReturn(commonView);
         when(commonView.getSelection()).thenReturn(selecetion);
         assertNotNull(dqDeleteAction_real.getSelection());
        
         stub(method(RepositoryNodeHelper.class, "isStateDeleted")).toReturn(false);
         RecycleBinRepNode recyBinNode = mock(RecycleBinRepNode.class);
         stub(method(RepositoryNodeHelper.class, "getRecycleBinRepNode")).toReturn(recyBinNode);
        

         deleteAction_mock.run();

    }

    /**
     * Test method for {@link org.talend.dataprofiler.core.ui.action.actions.dqDeleteAction_real#run()}.
     * 
     * @throws Exception
     */
    @Test
    public void testRun_2() throws Exception {
        DQRespositoryView dqView = mock(DQRespositoryView.class);
        when(corePlugin.getRepositoryView()).thenReturn(dqView);
        CommonViewer commonView = mock(CommonViewer.class);
        List<RepositoryNode> seleLs = new ArrayList<RepositoryNode>();
        // mock three nodes(connection and analysis and another node)
        RepositoryNode nodeConn = mock(RepositoryNode.class);
        RepositoryNode nodeAna = mock(RepositoryNode.class);
        IRepositoryObject objectConn = mock(IRepositoryObject.class);
        Property propConn = mock(Property.class);
        ConnectionItem itemConn = mock(ConnectionItem.class);
        ItemState stateConn = mock(ItemState.class);
        when(propConn.getItem()).thenReturn(itemConn);
        when(nodeConn.getObject()).thenReturn(objectConn);
        when(objectConn.getProperty()).thenReturn(propConn);
        when(itemConn.getState()).thenReturn(stateConn);
        when(stateConn.isDeleted()).thenReturn(true);
        IRepositoryObject objectAna = mock(IRepositoryObject.class);
        Property propAna = mock(Property.class);
        Item itemAna = mock(Item.class);
        ItemState stateAna = mock(ItemState.class);
        when(propAna.getItem()).thenReturn(itemAna);
        when(nodeAna.getObject()).thenReturn(objectAna);
        when(objectAna.getProperty()).thenReturn(propAna);
        when(itemAna.getState()).thenReturn(stateAna);
        when(stateAna.isDeleted()).thenReturn(true);
        ModelElement modAna = mock(ModelElement.class);
        ModelElement modConn = mock(ModelElement.class);
        List<ModelElement> dependenceLs = new ArrayList<ModelElement>();
        dependenceLs.add(modAna);
        RepositoryNode nodeOther = mock(RepositoryNode.class);
        IRepositoryObject objectOther = mock(IRepositoryObject.class);
        Property propOther = mock(Property.class);
        ConnectionItem itemOther = mock(ConnectionItem.class);
        ItemState stateOther = mock(ItemState.class);
        when(propOther.getItem()).thenReturn(itemOther);
        when(nodeOther.getObject()).thenReturn(objectOther);
        when(objectOther.getProperty()).thenReturn(propOther);
        when(itemOther.getState()).thenReturn(stateOther);
        when(stateOther.isDeleted()).thenReturn(true);

        seleLs.add(nodeAna);
        seleLs.add(nodeConn);
        seleLs.add(nodeOther);
        ISelection selecetion = new StructuredSelection(seleLs);
        // mock private method showDialog
        PowerMockito.mockStatic(DeleteModelElementConfirmDialog.class);
        when(
                DeleteModelElementConfirmDialog.showDialog(null, modConn,
                        dependenceLs.toArray(new ModelElement[dependenceLs.size()]),
                        DefaultMessagesImpl.getString("DQDeleteAction.dependencyByOther", ""), true)).thenReturn(true);
        // mock/stub something for private method physicalDeleteDependencies
        PowerMockito.mockStatic(RepositoryNodeHelper.class);
        RecycleBinRepNode recyBinNode = mock(RecycleBinRepNode.class);
        when(RepositoryNodeHelper.getRecycleBinRepNode()).thenReturn(recyBinNode);
        when(RepositoryNodeHelper.getResourceModelElement(nodeAna)).thenReturn(modAna);
        when(RepositoryNodeHelper.recursiveFind(modAna)).thenReturn(nodeAna);
        when(RepositoryNodeHelper.recursiveFindRecycleBin(modAna)).thenReturn(nodeAna);
        when(RepositoryNodeHelper.getModelElementFromRepositoryNode(nodeConn)).thenReturn(modConn);
        when(RepositoryNodeHelper.isStateDeleted(nodeConn)).thenReturn(true);
        when(RepositoryNodeHelper.isStateDeleted(nodeAna)).thenReturn(true);
        when(RepositoryNodeHelper.isStateDeleted(nodeOther)).thenReturn(true);
        when(RepositoryNodeHelper.hasDependencyClients(nodeConn)).thenReturn(true);

        PowerMockito.mockStatic(EObjectHelper.class);
        when(EObjectHelper.getDependencyClients(nodeConn)).thenReturn(dependenceLs);
        PowerMockito.mockStatic(PropertyHelper.class);
        when(PropertyHelper.getPropertyFile(modAna)).thenReturn(null);

        when(dqView.getCommonViewer()).thenReturn(commonView);
        when(commonView.getSelection()).thenReturn(selecetion);
        assertNotNull(dqDeleteAction_real.getSelection());

        deleteAction_mock.run();

    }

    /**
     * dqDeleteAction_real.currentNode==null means logical delete.
     */
    @Test
    public void testGetSelection_1() {
        dqDeleteAction_real.setCurrentNode(null);
        PowerMockito.mockStatic(CorePlugin.class);
        when(CorePlugin.getDefault()).thenReturn(corePlugin);
        DQRespositoryView dqView = mock(DQRespositoryView.class);
        when(corePlugin.getRepositoryView()).thenReturn(dqView);
        CommonViewer commonView = mock(CommonViewer.class);
        List<RepositoryNode> seleLs = new ArrayList<RepositoryNode>();
        seleLs.add(mock(RepositoryNode.class));
        seleLs.add(mock(RepositoryNode.class));
        ISelection selecetion = new StructuredSelection(seleLs);
        when(dqView.getCommonViewer()).thenReturn(commonView);
        when(commonView.getSelection()).thenReturn(selecetion);
        assertNotNull(dqDeleteAction_real.getSelection());

    }

    /**
     * 
     * dqDeleteAction_real.currentNode!=null means physical delete.
     */
    @Test
    public void testGetSelection_2() {
        // dqDeleteAction_real.currentNode==null means logical delete.
        RepositoryNode repNode = mock(RepositoryNode.class);
        dqDeleteAction_real.setCurrentNode(repNode);
        assertNotNull(dqDeleteAction_real.getSelection());

    }

    /**
     * Test method for
     * {@link org.talend.dataprofiler.core.ui.action.actions.dqDeleteAction_real#getRepositorySelection()}.
     */
    @Test
    public void testGetRepositorySelection() {
        // fail("Not yet implemented");
    }

    /**
     * Test method for
     * {@link org.talend.dataprofiler.core.ui.action.actions.dqDeleteAction_real#init(org.eclipse.jface.viewers.TreeViewer, org.eclipse.jface.viewers.IStructuredSelection)}
     * .
     */
    @Test
    public void testInit() {
        // fail("Not yet implemented");
    }

}
