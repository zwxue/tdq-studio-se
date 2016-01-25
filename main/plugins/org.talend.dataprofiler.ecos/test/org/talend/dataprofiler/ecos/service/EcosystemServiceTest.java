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
package org.talend.dataprofiler.ecos.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Iterator;
import java.util.List;

import org.junit.Test;
import org.talend.dataprofiler.ecos.model.IEcosCategory;
import org.talend.dataprofiler.ecos.model.impl.EcosCategory;

/**
 * 
 * DOC mzhao class global comment. Detailled comment
 */
public class EcosystemServiceTest {

    @Test
    public void testGetCategoryList() {
        List<IEcosCategory> list;
        try {
            list = EcosystemService.getCategoryList("3.1.2");//$NON-NLS-1$
            for (Iterator<IEcosCategory> iterator = list.iterator(); iterator.hasNext();) {
                EcosCategory object = (EcosCategory) iterator.next();
                System.out.println(object.getVersion());

            }
            assertNotNull(list);
        } catch (Exception e) {
            fail(e.getMessage());
        }

    }

}
