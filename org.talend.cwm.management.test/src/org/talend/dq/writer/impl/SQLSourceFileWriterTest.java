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
package org.talend.dq.writer.impl;

import static org.mockito.Mockito.*;
import static org.powermock.api.support.membermodification.MemberMatcher.*;
import static org.powermock.api.support.membermodification.MemberModifier.*;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.IPath;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.talend.core.model.properties.ByteArray;
import org.talend.core.repository.model.IRepositoryFactory;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.core.repository.utils.XmiResourceManager;
import org.talend.dataquality.properties.TDQSourceFileItem;
import org.talend.dq.writer.EMFSharedResources;
import org.talend.utils.sugars.ReturnCode;


/**
 * DOC yyin  class global comment. Detailled comment
 */
@PrepareForTest({ ProxyRepositoryFactory.class, EMFSharedResources.class })
public class SQLSourceFileWriterTest {

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    /**
     * Test method for
     * {@link org.talend.dq.writer.impl.SQLSourceFileWriter#loadFileContentInItem(org.talend.core.model.properties.Item, boolean)}
     * .
     * 
     * @throws IOException
     */
    @Test
    public void testLoadFileContentInItem() throws IOException {
        // EMFSharedResources cpMock = mock(EMFSharedResources.class);
        // PowerMockito.mockStatic(EMFSharedResources.class);
        // when(EMFSharedResources.getInstance()).thenReturn(cpMock);

        // PowerMockito.mockStatic(ProxyRepositoryFactory.class);
        ProxyRepositoryFactory proxFactoryMock = mock(ProxyRepositoryFactory.class);
        IRepositoryFactory factory = mock(IRepositoryFactory.class);
        XmiResourceManager resourceManager = mock(XmiResourceManager.class);
        stub(method(ProxyRepositoryFactory.class, "getInstance")).toReturn(proxFactoryMock); //$NON-NLS-1$
        when(proxFactoryMock.getRepositoryFactoryFromProvider()).thenReturn(factory);
        when(factory.getResourceManager()).thenReturn(resourceManager);

        SQLSourceFileWriter sqlWriter = new SQLSourceFileWriter();
        TDQSourceFileItem item = mock(TDQSourceFileItem.class);

        SQLSourceFileWriter spyWriter = spy(sqlWriter);
        IPath path = mock(IPath.class);
        doReturn(path).when(spyWriter).getItemFullPath(item);

        File file = mock(File.class);
        when(path.toFile()).thenReturn(file);

        ByteArray bytearray = mock(ByteArray.class);
        when(item.getContent()).thenReturn(bytearray);
        doNothing().when(bytearray).setInnerContentFromFile(file);

        ReturnCode rc = spyWriter.loadFileContentInItem(item, false);
        Assert.assertTrue(rc.isOk());
    }

}