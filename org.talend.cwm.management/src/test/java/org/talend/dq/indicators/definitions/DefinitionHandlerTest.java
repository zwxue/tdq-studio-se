// ============================================================================
//
// Copyright (C) 2006-2007 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dq.indicators.definitions;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;
import org.talend.dataquality.indicators.definition.IndicatorsDefinitions;

/**
 * DOC scorreia class global comment. Detailled comment
 */
public class DefinitionHandlerTest {

    /**
     * Test method for {@link org.talend.dq.indicators.DefinitionHandler#getIndicatorsDefinitions()}.
     */
    @Test
    public void testGetIndicatorsDefinitions() {
        IndicatorsDefinitions indicatorsDefinitions = DefinitionHandler.getInstance().getIndicatorsDefinitions();
        Assert.assertNotNull(indicatorsDefinitions);
    }

    /**
     * Test method for {@link org.talend.dq.indicators.DefinitionHandler#copyDefinitionsIntoFolder(java.io.File)}.
     */
    @Test
    public void testCopyDefinitionsInto() {
        File folder = new File("TestCopyDefinitionsIntoTest");
        if (folder.exists()) {
            File[] files = folder.listFiles();
            for (File file : files) {
                Assert.assertTrue(file.delete());
            }
            folder.delete();
        }

        DefinitionHandler.getInstance().copyDefinitionsIntoFolder(folder);
        Assert.assertTrue(folder.exists());
    }
}
