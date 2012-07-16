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
package org.talend.dataprofiler.core.migration.impl;

import static org.mockito.Mockito.*;
import static org.powermock.api.support.membermodification.MemberMatcher.*;
import static org.powermock.api.support.membermodification.MemberModifier.*;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Path;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.talend.core.model.metadata.builder.connection.DatabaseConnection;
import org.talend.core.model.properties.ConnectionItem;
import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.resource.ResourceManager;



/**
 * DOC qiongli Test case for split system indicators.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ ProxyRepositoryFactory.class, ResourceManager.class })
public class UpdateMsSqlToJdbcTaskTest {

    @Test
    public void testDoExecute() throws Exception {
        ProxyRepositoryFactory proxFactory = mock(ProxyRepositoryFactory.class);
        stub(method(ProxyRepositoryFactory.class, "getInstance")).toReturn(proxFactory);
        IRepositoryViewObject repObject = mock(IRepositoryViewObject.class);
        List<IRepositoryViewObject> ls = new ArrayList<IRepositoryViewObject>();
        ls.add(repObject);
        when(proxFactory.getAll(ERepositoryObjectType.METADATA_CONNECTIONS)).thenReturn(ls);
        Property prop = mock(Property.class);
        ConnectionItem item = mock(ConnectionItem.class);
        when(repObject.getProperty()).thenReturn(prop);
        when(prop.getItem()).thenReturn(item);
        DatabaseConnection dbConn = mock(DatabaseConnection.class);
        when(item.getConnection()).thenReturn(dbConn);
        when(dbConn.getDatabaseType()).thenReturn("Microsoft SQL Server 2005/2008");

        // mock something for super-class AbstractWorksapceUpdateTask
        IProject proj = mock(IProject.class);
        when(proj.getLocation()).thenReturn(new Path(""));
        stub(method(ResourceManager.class, "getRootProject")).toReturn(proj);
        UpdateMsSqlToJdbcTask updateMsSqlToJdbcTask = new UpdateMsSqlToJdbcTask();
        updateMsSqlToJdbcTask.doExecute();
    }

}
