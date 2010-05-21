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
package org.talend.cwm.management.i18n;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author scorreia
 * 
 * This class must be run as a plugin junit. In the launch configuration, remove the .nl in order to test with the
 * messages.properties file and not with a message_XX.properties file.
 */
public class MessagesTest {

    private static final String ARG = "salut";

    private static String[][] keyToExpected = {
            { "DefinitionHandler.IndicatorsDefinition", "Indicators' definition not loaded!" },
            { "AbstractTableBuilder.NoTypeGiven", "No type given. Type must be set to either tables, views, system tables..." },
            { "EIndicatorChartType.PatternFrequencyStatistics", "Pattern Frequency Statistics" },
            { "Evaluator.NoConnectionFoundInMetadata", "DB Connection \"{0}\" is not available in Meatdata." },
            { "JavaSqlFactory.UnableDecryptPassword",
                    "Unable to decrypt given password correctly. It's probably due to a change in the passphrase used in encryption." }

    };

    private static String[][] keyToExpectedWithArgs = {
            { "Evaluator.NoConnectionFoundInMetadata", "DB Connection \"" + ARG + "\" is not available in Meatdata." },
            { "DefinitionHandler.IndicatorsDefinition", "Indicators' definition not loaded!" },
            { "AbstractTableBuilder.NoTypeGiven", "No type given. Type must be set to either tables, views, system tables..." },
            { "EIndicatorChartType.PatternFrequencyStatistics", "Pattern Frequency Statistics" },
            { "JavaSqlFactory.UnableDecryptPassword",
                    "Unable to decrypt given password correctly. It's probably due to a change in the passphrase used in encryption." }

    };

    /**
     * Test method for {@link org.talend.cwm.management.i18n.Messages#getString(java.lang.String)}.
     */
    @Test
    public void testGetStringString() {
        for (int i = 0; i < keyToExpected.length; i++) {
            String key = keyToExpected[i][0];
            String expected = keyToExpected[i][1];
            String mess = Messages.getString(key);
            System.out.println(mess);
            Assert.assertEquals("Failed to get the correct result for key " + key, expected, mess);
        }
    }

    /**
     * Test method for {@link org.talend.cwm.management.i18n.Messages#getString(java.lang.String, java.lang.Object[])}.
     */
    @Test
    public void testGetStringStringObjectArray() {
        for (int i = 0; i < keyToExpectedWithArgs.length; i++) {

            String key = keyToExpectedWithArgs[i][0];
            String arg = ARG;
            String expected = keyToExpectedWithArgs[i][1];
            String mess = Messages.getString(key, arg);
            System.out.println(mess);
            Assert.assertEquals("Failed to get the correct result for key " + key, expected, mess);
        }

    }

}
