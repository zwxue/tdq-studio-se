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
package org.talend.dq.helper;

import static org.easymock.EasyMock.expect;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.easymock.PowerMock.mockStatic;
import static org.powermock.api.easymock.PowerMock.replayAll;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.talend.core.model.properties.Property;
import org.talend.dataquality.helpers.ReportHelper;
import org.talend.dataquality.properties.TDQReportItem;

/**
 * DOC qiongli class global comment. Detailled comment <br/>
 * 
 * $Id: talend.epf 55206 2011-02-15 17:32:14Z mhirt $
 * 
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ DQDeleteHelper.class, PropertyHelper.class, ReportHelper.class })
public class DQDeleteHelperTest {

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
        mockStatic(PropertyHelper.class);
        expect(PropertyHelper.getItemFile(prop)).andReturn(file);
        mockStatic(ReportHelper.class);
        expect(ReportHelper.getOutputFolder(file)).andReturn(folder);
        replayAll();
        DQDeleteHelper.deleteRelations(item);

    }

}
