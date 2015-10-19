// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataquality.indicators.impl;

import java.util.Random;

import org.junit.Assert;
import org.junit.Test;
import org.talend.dataquality.indicators.util.ChainResponsibilityHandler;
import org.talend.dataquality.indicators.util.HandlerFactory;
import org.talend.utils.dates.DateUtils;

/**
 * created by zhao on 2015年10月15日 Detailled comment
 *
 */
public class PatternFreqIndicatorImplTest {

    private final static String LATIN_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZàáâãäåæçèéêëìíîïðñòóôõöøùúûüýþÿÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏÐÑÒÓÔÕÖØÙÚÛÜÝÞß0123456789"; //$NON-NLS-1$

    private final static String LATIN_CHARS_PATT = "aaaaaaaaaaaaaaaaaaaaaaaaaaAAAAAAAAAAAAAAAAAAAAAAAAAAaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA9999999999"; //$NON-NLS-1$

    @Test
    public void testLatinPatternReplacement() {
        PatternFreqIndicatorImpl matchIndicator = new PatternFreqIndicatorImpl();
        Assert.assertEquals(LATIN_CHARS_PATT, matchIndicator.getFrequencyLabel(LATIN_CHARS));
    }

    @Test
    public void testLatin1Perf() {
        PatternFreqIndicatorImpl matchIndicator = new PatternFreqIndicatorImpl();

        // Character replace
        Random r = new Random();
        int count = 100000000;
        int length = count / 10000000;
        StringBuffer bf = new StringBuffer();
        long crStart = System.currentTimeMillis();
        System.out.println("Char replacement start at: " + DateUtils.getCurrentDate(DateUtils.PATTERN_2)); //$NON-NLS-1$
        for (int i = 0; i < count; i++) {
            if (i % length == 0) {
                if (i != 0) {
                    matchIndicator.getFrequencyLabel(bf.toString());
                }
                bf = new StringBuffer();
            }
            bf.append(LATIN_CHARS.charAt(r.nextInt(LATIN_CHARS.length())));
        }
        System.out.println("End at: " + DateUtils.getCurrentDate(DateUtils.PATTERN_2)); //$NON-NLS-1$
        long crEnd = System.currentTimeMillis();

        // Regex
        ChainResponsibilityHandler createEastAsiaPatternHandler = HandlerFactory.createLatinPatternHandler();
        long reStart = System.currentTimeMillis();
        System.out.println("Regex replacement start at: " + DateUtils.getCurrentDate(DateUtils.PATTERN_2)); //$NON-NLS-1$
        for (int i = 0; i < count; i++) {
            if (i % length == 0) {
                if (i != 0) {
                    createEastAsiaPatternHandler.handleRequest(bf.toString());
                }
                bf = new StringBuffer();
            }
            bf.append(LATIN_CHARS.charAt(r.nextInt(LATIN_CHARS.length())));
        }
        System.out.println("End at: " + DateUtils.getCurrentDate(DateUtils.PATTERN_2)); //$NON-NLS-1$
        long reEnd = System.currentTimeMillis();
        // Assert that the time spend by char replacement is 3 times less than that of regex.
        Assert.assertTrue(reEnd - reStart > 2 * (crEnd - crStart));
    }
}
