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
package org.talend.dataprofiler.core.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Assert;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.talend.core.ITDQRepositoryService;
import org.talend.core.model.metadata.builder.connection.DatabaseConnection;
import org.talend.core.model.metadata.builder.connection.DelimitedFileConnection;
import org.talend.core.model.metadata.builder.connection.MDMConnection;
import org.talend.core.model.properties.ConnectionItem;
import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.dq.helper.EObjectHelper;
import org.talend.repository.model.IRepositoryNode;
import orgomg.cwm.objectmodel.core.ModelElement;


/**
 * DOC yyin  class global comment. Detailled comment
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ EObjectHelper.class })
public class TestTOPRepositoryServiceTest extends TestCase {

    private ITDQRepositoryService tdqRepService;
    /* (non-Jsdoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        tdqRepService = new TOPRepositoryService();// (ITDQRepositoryService)
                                                   // GlobalServiceRegister.getDefault().getService(ITDQRepositoryService.class);
    }

    /* (non-Jsdoc)
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test method for {@link org.talend.dataprofiler.core.service.TOPRepositoryService#removeAliasInSQLExplorer(org.talend.repository.model.IRepositoryNode)}.
     */
    public void testRemoveAliasInSQLExplorer() {
        IRepositoryNode child = mock(IRepositoryNode.class);
        IRepositoryViewObject obj = mock(IRepositoryViewObject.class);
        when(child.getObject()).thenReturn(obj);
        Property pro = mock(Property.class);
        when(obj.getProperty()).thenReturn(pro);
        ConnectionItem item = mock(ConnectionItem.class);
        when(pro.getItem()).thenReturn(item);

        MDMConnection mdm = mock(MDMConnection.class);
        DelimitedFileConnection dfile = mock(DelimitedFileConnection.class);
        DatabaseConnection dbc = mock(DatabaseConnection.class);
        when(item.getConnection()).thenReturn(dbc).thenReturn(dfile).thenReturn(mdm);
        
        List<ModelElement> dependencyClients = new ArrayList<ModelElement>();
        ModelElement m1 = mock(ModelElement.class);
        dependencyClients.add(m1);
        PowerMockito.mockStatic(EObjectHelper.class);
        when(EObjectHelper.getDependencyClients(dbc)).thenReturn(dependencyClients);
        when(EObjectHelper.getDependencyClients(mdm)).thenReturn(dependencyClients);
        when(EObjectHelper.getDependencyClients(dfile)).thenReturn(dependencyClients);

        Assert.assertFalse(tdqRepService.removeAliasInSQLExplorer(child));
        Assert.assertFalse(tdqRepService.removeAliasInSQLExplorer(child));
        Assert.assertFalse(tdqRepService.removeAliasInSQLExplorer(child));
    }

}
