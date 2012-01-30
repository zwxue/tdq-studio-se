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
package org.talend.dataquality.record.linkage.attribute;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Unit tests for DoubleMetaphoneMatcher
 */
public class AbstractAttributeMatcherTest {

    private static final String[] record1 = { "kate", "unmottreslong", "steff", "notblank", "", "" };

    private static final String[] record2 = { "Cade", "unautremotlong", "stephanie", "", "notblank", "" };

    private static final double[] expectation_metaphone = { 1.0, 0.5, 0.75, 0.0, 0.0, 1.0 };

    private static final double[] expectation_jaro = { 0.66, 0.84, 0.64, 0.0, 0.0, 1.0 };

    /**
     * Test method for
     * {@link org.talend.dataquality.record.linkage.attribute.AbstractAttributeMatcher#getMatchingWeight(java.lang.String, java.lang.String)}
     * .
     */
    @Test
    public void testGetMatchingWeight() {
        for (int i = 0; i < record1.length; i++) {
            DoubleMetaphoneMatcher matcher = new DoubleMetaphoneMatcher();
            assertEquals("The score of record #" + i + " is unexpected.", expectation_metaphone[i],
                    matcher.getMatchingWeight(record1[i], record2[i]), 0.01);
        }

        for (int i = 0; i < record1.length; i++) {
            JaroMatcher matcher = new JaroMatcher();
            assertEquals("The score of record #" + i + " is unexpected.", expectation_jaro[i],
                    matcher.getMatchingWeight(record1[i], record2[i]), 0.01);
        }
    }
}
