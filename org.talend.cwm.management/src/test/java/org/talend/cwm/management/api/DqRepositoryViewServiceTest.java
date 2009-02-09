// ============================================================================
//
// Copyright (C) 2006-2009 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.cwm.management.api;

import static org.junit.Assert.fail;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

/**
 * DOC scorreia class global comment. Detailled comment
 */
public class DqRepositoryViewServiceTest {

    private static Logger log = Logger.getLogger(DqRepositoryViewServiceTest.class);

    private static final File FOLDER = new File("out");

    private static final String[] FUNC_NAMES = new String[] { "abc", "salut", "tu vas bien", "etage", "tage", "étage",
            "ça promet", "&%ùöôk~n@^aâ", "fdg " };

    /**
     * Test method for {@link org.talend.cwm.management.api.DqRepositoryViewService#listTdDataProviders(java.io.File)}.
     */
    @Test
    public void testListTdDataProviders() {

        // List<TdDataProvider> dataProviders = DqRepositoryViewService.listTdDataProviders(FOLDER);
        // assertNotNull(dataProviders);
        // assertFalse(dataProviders.isEmpty());
        // for (TdDataProvider tdDataProvider : dataProviders) {
        // log.info("tdDataProvider name = " + tdDataProvider.getName());
        // }

    }

    @Test
    public void testCreateTechnicalName() {
        List<String> technames = new ArrayList<String>();

        for (String functionalName : FUNC_NAMES) {
            String technicalName = DqRepositoryViewService.createTechnicalName(functionalName);
            // System.out.println(functionalName + " -> " + technicalName);
            technames.add(technicalName);
        }
        for (int i = 0; i < FUNC_NAMES.length; i++) {
            String functionalName = FUNC_NAMES[i];
            Assert.assertEquals(technames.get(i), DqRepositoryViewService.createTechnicalName(functionalName));
        }

    }

    /**
     * Test method for
     * {@link org.talend.cwm.management.api.DqRepositoryViewService#getTables(orgomg.cwm.resource.relational.Catalog, boolean)}.
     */
    @Test
    public void testGetTables() {
        fail("Not yet implemented");
    }

}
