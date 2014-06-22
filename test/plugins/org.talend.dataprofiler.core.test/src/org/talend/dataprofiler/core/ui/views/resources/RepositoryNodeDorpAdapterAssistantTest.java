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
package org.talend.dataprofiler.core.ui.views.resources;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import static org.powermock.api.support.membermodification.MemberMatcher.*;
import static org.powermock.api.support.membermodification.MemberModifier.*;

import java.util.ResourceBundle;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.junit.Rule;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.helper.WorkspaceResourceHelper;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.utils.MessageUI;
import org.talend.dataquality.properties.TDQSourceFileItem;
import org.talend.repository.model.IProxyRepositoryFactory;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.IRepositoryNode.ENodeType;
import org.talend.resource.ResourceManager;

/**
 * DOC xqliu class global comment. Detailled comment
 */
@PrepareForTest({ MessageUI.class, DefaultMessagesImpl.class, CorePlugin.class, StringUtils.class, IProxyRepositoryFactory.class,
        ProxyRepositoryFactory.class, WorkspaceResourceHelper.class, ResourceManager.class })
public class RepositoryNodeDorpAdapterAssistantTest {

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    /**
     * Test method for
     * {@link org.talend.dataprofiler.core.ui.views.resources.RepositoryNodeDorpAdapterAssistant#moveJrxmlFileRepNode(org.talend.repository.model.IRepositoryNode, org.talend.repository.model.IRepositoryNode)}
     * .
     */
    @Test
    public void testMoveJrxmlFileRepNode() {
        // there just show a warning dialog now, we need to implement this test method again when the method be
        // implemented on the 5.2.0
        try {
            CorePlugin cpMock = mock(CorePlugin.class);
            PowerMockito.mockStatic(CorePlugin.class);
            when(CorePlugin.getDefault()).thenReturn(cpMock);

            ResourceBundle rb = mock(ResourceBundle.class);
            stub(method(ResourceBundle.class, "getBundle", String.class)).toReturn(rb); //$NON-NLS-1$

            PowerMockito.mockStatic(DefaultMessagesImpl.class);
            when(DefaultMessagesImpl.getString(anyString())).thenReturn("anyString()"); //$NON-NLS-1$

            PowerMockito.mockStatic(MessageUI.class);
            stub(method(MessageUI.class, "openWarning", String.class)); //$NON-NLS-1$

            ProxyRepositoryFactory proxFactoryMock = mock(ProxyRepositoryFactory.class);
            when(proxFactoryMock.getNextId()).thenReturn("abcd1").thenReturn("abcd2"); //$NON-NLS-1$ //$NON-NLS-2$
            stub(method(ProxyRepositoryFactory.class, "getInstance")).toReturn(proxFactoryMock); //$NON-NLS-1$

            LocalRepositoryObjectCRUD assistant = new LocalRepositoryObjectCRUD();
            IRepositoryNode sourceNodeMock = mock(IRepositoryNode.class);
            IRepositoryNode targetNodeMock = mock(IRepositoryNode.class);
            assistant.moveJrxmlFileRepNode(sourceNodeMock, targetNodeMock);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Test method for
     * {@link org.talend.dataprofiler.core.ui.views.resources.RepositoryNodeDorpAdapterAssistant#moveSourceFileRepNode(org.talend.repository.model.IRepositoryNode, org.talend.repository.model.IRepositoryNode)}
     * .
     */
    @Test
    public void testMoveSourceFileRepNode() {
        // test the source file node which has been opened
        try {
            CorePlugin cpMock = mock(CorePlugin.class);
            PowerMockito.mockStatic(CorePlugin.class);
            when(CorePlugin.getDefault()).thenReturn(cpMock);

            ResourceBundle rb = mock(ResourceBundle.class);
            stub(method(ResourceBundle.class, "getBundle", String.class)).toReturn(rb); //$NON-NLS-1$

            PowerMockito.mockStatic(DefaultMessagesImpl.class);
            when(DefaultMessagesImpl.getString(anyString())).thenReturn("anyString()"); //$NON-NLS-1$

            PowerMockito.mockStatic(MessageUI.class);
            stub(method(MessageUI.class, "openWarning", String.class)); //$NON-NLS-1$

            ProxyRepositoryFactory proxFactoryMock = mock(ProxyRepositoryFactory.class);
            when(proxFactoryMock.getNextId()).thenReturn("abcd1").thenReturn("abcd2"); //$NON-NLS-1$ //$NON-NLS-2$
            stub(method(ProxyRepositoryFactory.class, "getInstance")).toReturn(proxFactoryMock); //$NON-NLS-1$

            LocalRepositoryObjectCRUD assistant = new LocalRepositoryObjectCRUD();
            IRepositoryNode sourceNodeMock = mock(IRepositoryNode.class);
            IRepositoryNode targetNodeMock = mock(IRepositoryNode.class);

            PowerMockito.mockStatic(WorkspaceResourceHelper.class);
            when(WorkspaceResourceHelper.sourceFileHasBeenOpened(sourceNodeMock)).thenReturn(Boolean.TRUE);

            IPath ipath = mock(IPath.class);
            assistant.moveOthersNode(sourceNodeMock, targetNodeMock, ipath);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Test method for
     * {@link org.talend.dataprofiler.core.ui.views.resources.RepositoryNodeDorpAdapterAssistant#moveSourceFileRepNode(org.talend.repository.model.IRepositoryNode, org.talend.repository.model.IRepositoryNode)}
     * .
     */
    @Test
    public void testMoveSourceFileRepNode2() {
        CorePlugin cpMock = mock(CorePlugin.class);
        PowerMockito.mockStatic(CorePlugin.class);
        when(CorePlugin.getDefault()).thenReturn(cpMock);
        method(CorePlugin.class, "refreshDQView", Object.class); //$NON-NLS-1$

        ResourceBundle rb = mock(ResourceBundle.class);
        stub(method(ResourceBundle.class, "getBundle", String.class)).toReturn(rb); //$NON-NLS-1$

        PowerMockito.mockStatic(DefaultMessagesImpl.class);
        when(DefaultMessagesImpl.getString(anyString())).thenReturn("anyString()"); //$NON-NLS-1$

        ProxyRepositoryFactory proxFactoryMock = mock(ProxyRepositoryFactory.class);
        when(proxFactoryMock.getNextId()).thenReturn("abcd1").thenReturn("abcd2"); //$NON-NLS-1$ //$NON-NLS-2$
        stub(method(ProxyRepositoryFactory.class, "getInstance")).toReturn(proxFactoryMock); //$NON-NLS-1$

        IRepositoryNode sourceNodeMock = mock(IRepositoryNode.class);
        IRepositoryNode targetNodeMock = mock(IRepositoryNode.class);

        PowerMockito.mockStatic(WorkspaceResourceHelper.class);
        when(WorkspaceResourceHelper.sourceFileHasBeenOpened(sourceNodeMock)).thenReturn(Boolean.FALSE);

        IRepositoryViewObject repViewObjMock = mock(IRepositoryViewObject.class);
        when(sourceNodeMock.getObject()).thenReturn(repViewObjMock);

        when(targetNodeMock.getContentType()).thenReturn(ERepositoryObjectType.TDQ_SOURCE_FILE_ELEMENT);

        PowerMockito.mockStatic(ResourceManager.class);

        IFolder folerMock = mock(IFolder.class);
        when(ResourceManager.getSourceFileFolder()).thenReturn(folerMock);

        IPath folderPath = Path.fromOSString("/"); //$NON-NLS-1$
        when(folerMock.getFullPath()).thenReturn(folderPath);

        IProject projectMock = mock(IProject.class);
        when(ResourceManager.getRootProject()).thenReturn(projectMock);

        IPath projectPath = Path.fromOSString("/"); //$NON-NLS-1$
        when(projectMock.getFullPath()).thenReturn(projectPath);

        when(targetNodeMock.getType()).thenReturn(ENodeType.SYSTEM_FOLDER);

        LocalRepositoryObjectCRUD spyAssistant = spy(new LocalRepositoryObjectCRUD());
        doNothing().when(spyAssistant).moveObject((IRepositoryNode) any(), (IRepositoryNode) any(), (IPath) any());

        // added by yyin, 20120626, for TDQ-5468 modifications
        TDQSourceFileItem fileItem = mock(TDQSourceFileItem.class);
        Property propertyMock = mock(Property.class);
        when(repViewObjMock.getProperty()).thenReturn(propertyMock);
        when(propertyMock.getItem()).thenReturn(fileItem);

        Resource resourceMock = mock(Resource.class);
        when(fileItem.eResource()).thenReturn(resourceMock);
        URI uri = URI.createPlatformResourceURI("org.talend.dataprofiler.core.test/cc_0.1.properties", false); //$NON-NLS-1$
        when(resourceMock.getURI()).thenReturn(uri);
        when(ResourceManager.getRootFolderLocation()).thenReturn(folderPath);
        // ~
        IPath ipath = mock(IPath.class);
        spyAssistant.moveOthersNode(sourceNodeMock, targetNodeMock, ipath);
    }
}
