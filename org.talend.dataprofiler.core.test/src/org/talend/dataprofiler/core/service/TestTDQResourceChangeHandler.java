// ============================================================================
//
// Copyright (C) 2006-2010 Talend Inc. - www.talend.com
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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashSet;
import java.util.List;

import org.junit.Test;
import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.dataprofiler.core.recycle.LogicalDeleteFileHandle;
import org.talend.dq.helper.ProxyRepositoryViewObject;

/**
 * DOC qiongli class global comment. Detailled comment
 */
public class TestTDQResourceChangeHandler {

    private List<IRepositoryViewObject> repsList = null;

    /**
     * Test method for {@link org.talend.dataprofiler.core.service.TDQResourceChangeHandler#handleLogicalDelete(org.talend.core.model.properties.Property)}.
     */
    @Test
    public void testHandleLogicalDelete() {
        Property prop = getProperty(true);
        if (prop == null) {
            fail("Can not find any aviable property of Connection in this System!");
        }
        TDQResourceChangeHandler tdqResourceChangeHandler = new TDQResourceChangeHandler();
        tdqResourceChangeHandler.handleLogicalDelete(prop);
        HashSet<Property> delLs = LogicalDeleteFileHandle.getDelPropertyLs();
        assertNotNull(delLs);
        assertTrue(delLs.contains(prop));
    }

    /**
     * Test method for {@link org.talend.dataprofiler.core.service.TDQResourceChangeHandler#handleRestore(org.talend.core.model.properties.Property)}.
     */
    @Test
    public void testHandleRestore() {
        Property prop = getProperty(false);
        if (prop == null) {
            fail("Can not find any aviable property of Connection in this System!");
        }
        TDQResourceChangeHandler tdqResourceChangeHandler = new TDQResourceChangeHandler();
        tdqResourceChangeHandler.handleRestore(prop);
        HashSet<Property> delLs = LogicalDeleteFileHandle.getDelPropertyLs();
        assertNotNull(delLs);
        assertFalse(delLs.contains(prop));
    }

    private Property getProperty(boolean isDeleted) {
        if (repsList == null) {
            repsList = ProxyRepositoryViewObject.fetchAllRepositoryViewObjects(true, true);
        }
        for (IRepositoryViewObject res : repsList) {
            Property prop = res.getProperty();
            assertNotNull(prop);
            if (isDeleted && prop.getItem().getState().isDeleted()) {
                return prop;
            }
            if (!isDeleted && !prop.getItem().getState().isDeleted()) {
                return prop;
            }
        }
        return null;
    }


}
