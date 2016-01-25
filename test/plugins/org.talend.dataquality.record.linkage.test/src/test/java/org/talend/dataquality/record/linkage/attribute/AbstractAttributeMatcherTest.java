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
package org.talend.dataquality.record.linkage.attribute;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;
import org.talend.dataquality.record.linkage.attribute.IAttributeMatcher.NullOption;
import org.talend.dataquality.record.linkage.constant.AttributeMatcherType;

/**
 * Unit tests for attribute matchers
 */
public class AbstractAttributeMatcherTest {

    private static final String[][] testcase = {
            // tests for Exact matcher
            { AttributeMatcherType.exact.toString(), "E", "E", "1.0" },
            { AttributeMatcherType.exact.toString(), "E", "e", "0.0" },

            // tests for ExactIgnoreCase matcher
            { AttributeMatcherType.exactIgnoreCase.toString(), "E", "e", "1.0" },
            { AttributeMatcherType.exactIgnoreCase.toString(), "Î", "î", "1.0" },

            // tests for Soundex matcher
            { AttributeMatcherType.soundex.toString(), "kate", "Cade", "0.75" },
            { AttributeMatcherType.soundex.toString(), "unmottreslong", "unautremotlong", "1.0" },
            { AttributeMatcherType.soundex.toString(), "steff", "stephanie", "0.75" },
            { AttributeMatcherType.soundex.toString(), "Sebastiao", "Sepastien", "1.0" },
            { AttributeMatcherType.soundex.toString(), "Sizhao", "sejao", "1.0" },

            // tests for SoundexFR matcher

            { AttributeMatcherType.soundexFR.toString(), "kate", "Cade", "0.75" },
            { AttributeMatcherType.soundexFR.toString(), "unmottreslong", "unautremotlong", "0.25" },
            { AttributeMatcherType.soundexFR.toString(), "steff", "stephanie", "0.75" },
            { AttributeMatcherType.soundexFR.toString(), "Sebastiao", "Sepastien", "0.75" },
            { AttributeMatcherType.soundexFR.toString(), "Sizhao", "sejao", "0.75" },

            // tests for Metaphone/DoubleMataphone matcher
            { AttributeMatcherType.doubleMetaphone.toString(), "kate", "Cade", "1.0" },
            { AttributeMatcherType.doubleMetaphone.toString(), "unmottreslong", "unautremotlong", "0.5" },
            { AttributeMatcherType.doubleMetaphone.toString(), "steff", "stephanie", "0.75" },
            { AttributeMatcherType.doubleMetaphone.toString(), "Sebastiao", "Sepastien", "0.75" },
            { AttributeMatcherType.doubleMetaphone.toString(), "Sizhao", "sejao", "1.0" },

            // tests for Levenshtein matcher
            { AttributeMatcherType.levenshtein.toString(), "kate", "Cade", "0.5" },
            { AttributeMatcherType.levenshtein.toString(), "unmottreslong", "unautremotlong", "0.57" },
            { AttributeMatcherType.levenshtein.toString(), "steff", "stephanie", "0.33" },
            { AttributeMatcherType.levenshtein.toString(), "Sebastiao", "Sepastien", "0.67" },
            { AttributeMatcherType.levenshtein.toString(), "Sizhao", "sejao", "0.33" },

            // tests for Jaro(-Winkler) matcher
            { AttributeMatcherType.jaro.toString(), "kate", "Cade", "0.66" },
            { AttributeMatcherType.jaro.toString(), "unmottreslong", "unautremotlong", "0.84" },
            { AttributeMatcherType.jaro.toString(), "steff", "stephanie", "0.64" },
            { AttributeMatcherType.jaro.toString(), "Sebastiao", "Sepastien", "0.78" },
            { AttributeMatcherType.jaro.toString(), "Sizhao", "sejao", "0.57" },

            // tests for Qgrams matcher
            { AttributeMatcherType.qgrams.toString(), "kate", "Cade", "0.16" },
            { AttributeMatcherType.qgrams.toString(), "unmottreslong", "unautremotlong", "0.51" },
            { AttributeMatcherType.qgrams.toString(), "steff", "stephanie", "0.33" },
            { AttributeMatcherType.qgrams.toString(), "Sebastiao", "Sepastien", "0.36" },
            { AttributeMatcherType.qgrams.toString(), "Sizhao", "sejao", "0.26" },

            // tests for blank fields
            { AttributeMatcherType.doubleMetaphone.toString(), "", "stephanie", "0.0" },
            { AttributeMatcherType.doubleMetaphone.toString(), "stephanie", "", "0.0" },
            { AttributeMatcherType.doubleMetaphone.toString(), "", "", "1.0" },

            // tests for null fields (default null option)
            { AttributeMatcherType.doubleMetaphone.toString(), null, "stephanie", "0.0" },
            { AttributeMatcherType.doubleMetaphone.toString(), "stephanie", null, "0.0" },
            { AttributeMatcherType.doubleMetaphone.toString(), null, null, "1.0" }, };

    /**
     * Test method for
     * {@link org.talend.dataquality.record.linkage.attribute.AbstractAttributeMatcher#getMatchingWeight(java.lang.String, java.lang.String)}
     * .
     */
    @Test
    public void testGetMatchingWeight() {
        for (String[] str : testcase) {
            IAttributeMatcher matcher = AttributeMatcherFactory.createMatcher(str[0]);
            assertEquals("The score of test case is unexpected.\n" + Arrays.asList(str), Double.valueOf(str[3]),
                    matcher.getMatchingWeight(str[1], str[2]), 0.01);
        }
    }

    @Test
    public void testNullOptions() {
        for (AttributeMatcherType type : AttributeMatcherType.values()) {
            if (type.equals(AttributeMatcherType.custom)) {
                continue; // do not handle this case.
            }
            IAttributeMatcher matcher = AttributeMatcherFactory.createMatcher(type);
            matcher.setNullOption(NullOption.nullMatchAll);
            Assert.assertEquals(1.0d, matcher.getMatchingWeight(null, null), 0);
            Assert.assertEquals(1.0d, matcher.getMatchingWeight(null, "toto"), 0);
            Assert.assertEquals(1.0d, matcher.getMatchingWeight("", "toto"), 0);
            Assert.assertEquals(1.0d, matcher.getMatchingWeight("", ""), 0);
            Assert.assertEquals(1.0d, matcher.getMatchingWeight("a", "a"), 0);

            // change option
            matcher.setNullOption(NullOption.nullMatchNone);
            Assert.assertEquals(0.0d, matcher.getMatchingWeight(null, null), 0);
            Assert.assertEquals(0.0d, matcher.getMatchingWeight(null, "toto"), 0);
            Assert.assertEquals(0.0d, matcher.getMatchingWeight("", "toto"), 0);
            Assert.assertEquals(0.0d, matcher.getMatchingWeight("", ""), 0);
            Assert.assertEquals(1.0d, matcher.getMatchingWeight("a", "a"), 0);

            // change option
            matcher.setNullOption(NullOption.nullMatchNull);
            Assert.assertEquals(1.0d, matcher.getMatchingWeight(null, null), 0);
            Assert.assertEquals(0.0d, matcher.getMatchingWeight(null, "toto"), 0);
            Assert.assertEquals(0.0d, matcher.getMatchingWeight("", "toto"), 0);
            Assert.assertEquals(1.0d, matcher.getMatchingWeight("", ""), 0);
            Assert.assertEquals(1.0d, matcher.getMatchingWeight("a", "a"), 0);

        }

    }
}
