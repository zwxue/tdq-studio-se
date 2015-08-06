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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.talend.dataquality.indicators.BenfordLawFrequencyIndicator;
import org.talend.dataquality.indicators.IndicatorsFactory;


/**
 * DOC yyin  class global comment. Detailled comment
 */
public class BenfordLawFrequencyIndicatorImplTest {

    private BenfordLawFrequencyIndicator benIndicator;

    private List<Object[]> values;
    /**
     * DOC yyin Comment method "setUp".
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        benIndicator = IndicatorsFactory.eINSTANCE.createBenfordLawFrequencyIndicator();

        values = new ArrayList<Object[]>();
        values.add(new Object[] { "1", 30L });
        values.add(new Object[] { "2", 20L });
        values.add(new Object[] { "3", 16L });
        values.add(new Object[] { "4", 12L });
        values.add(new Object[] { "5", 9L });
        values.add(new Object[] { "6", 7L });
        values.add(new Object[] { "7", 5L });
    }

    /**
     * DOC yyin Comment method "tearDown".
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
        benIndicator.getValueToFreq().clear();
    }

    /**
     * Test method for {@link org.talend.dataquality.indicators.impl.BenfordLawFrequencyIndicatorImpl#handle(java.lang.Object)}.
     */
    @Test
    public void testHandleObject() {

    }

    /**
     * Test method for {@link org.talend.dataquality.indicators.impl.BenfordLawFrequencyIndicatorImpl#checkValues()}.
     * test normal data
     */
    @Test
    public void testCheckValues_1() {
        values.add(new Object[] { "8", 3L });
        values.add(new Object[] { "9", 1L });

        benIndicator.storeSqlResults(values);
        HashMap<Object, Long> valueMap = benIndicator.getValueToFreq();
        Assert.assertEquals(valueMap.size(), 9);
        assertinternal(valueMap);
        Assert.assertTrue(valueMap.get("8") == 3L);
        Assert.assertTrue(valueMap.get("9") == 1L);
    }

    private void assertinternal(HashMap<Object, Long> valueMap) {
        Assert.assertTrue(valueMap.get("1") == 30L);
        Assert.assertTrue(valueMap.get("2") == 20L);
        Assert.assertTrue(valueMap.get("3") == 16L);
        Assert.assertTrue(valueMap.get("4") == 12L);
        Assert.assertTrue(valueMap.get("5") == 9L);
        Assert.assertTrue(valueMap.get("6") == 7L);
        Assert.assertTrue(valueMap.get("7") == 5L);
    }

    /**
     * test for miss numbers in 1~9
     */
    @Test
    public void testCheckValues_2() {
        // miss 8,9
        benIndicator.storeSqlResults(values);
        HashMap<Object, Long> valueMap = benIndicator.getValueToFreq();
        Assert.assertEquals(valueMap.size(), 9);
        assertinternal(valueMap);
        Assert.assertTrue(valueMap.get("8") == 0L);
        Assert.assertTrue(valueMap.get("9") == 0L);
    }

    /**
     * test for include "0" (double type)
     */
    @Test
    public void testCheckValues_3() {
        values.add(new Object[] { "8", 9L });
        values.add(new Object[] { "9", 6L });
        values.add(new Object[] { "0", 3L });

        benIndicator.storeSqlResults(values);
        HashMap<Object, Long> valueMap = benIndicator.getValueToFreq();
        Assert.assertEquals(valueMap.size(), 10);
        assertinternal(valueMap);
        Assert.assertTrue(valueMap.get("8") == 9L);
        Assert.assertTrue(valueMap.get("9") == 6L);
        Assert.assertTrue(valueMap.get("0") == 3L);
    }

    /**
     * test for null value
     */
    @Test
    public void testCheckValues_4() {
        values.add(new Object[] { "8", 3L });
        values.add(new Object[] { "9", 1L });
        values.add(new Object[] { null, 3L });

        benIndicator.storeSqlResults(values);
        HashMap<Object, Long> valueMap = benIndicator.getValueToFreq();
        Assert.assertEquals(valueMap.size(), 10);
        assertinternal(valueMap);
        Assert.assertTrue(valueMap.get("8") == 3L);
        Assert.assertTrue(valueMap.get("9") == 1L);
        Assert.assertTrue(valueMap.get("invalid") == 3L);

    }

    /**
     * test for string type invalid value= 3+3+3=9
     */
    @Test
    public void testCheckValues_5() {
        values.add(new Object[] { "8", 3L });
        values.add(new Object[] { "9", 1L });
        values.add(new Object[] { "a", 3L });
        values.add(new Object[] { "b", 3L });
        values.add(new Object[] { "c", 3L });

        benIndicator.storeSqlResults(values);
        HashMap<Object, Long> valueMap = benIndicator.getValueToFreq();
        Assert.assertEquals(valueMap.size(), 10);
        assertinternal(valueMap);
        Assert.assertTrue(valueMap.get("8") == 3L);
        Assert.assertTrue(valueMap.get("9") == 1L);
        Assert.assertTrue(valueMap.get("invalid") == 9L);
    }

    /**
     * test for key length>1
     */
    @Test
    public void testCheckValues_6() {
        values.add(new Object[] { "8     ", 3L });
        values.add(new Object[] { "9                     ", 1L });

        benIndicator.storeSqlResults(values);
        HashMap<Object, Long> valueMap = benIndicator.getValueToFreq();
        Assert.assertEquals(valueMap.size(), 9);
        assertinternal(valueMap);
        Assert.assertTrue(valueMap.get("8") == 3L);
        Assert.assertTrue(valueMap.get("9") == 1L);
        Assert.assertTrue(valueMap.get("8     ") == null);
    }

    /**
     * test for key length>1, with some invalid(length>1) which should not be effected
     */
    @Test
    public void testCheckValues_7() {
        values.add(new Object[] { "8     ", 3L });
        values.add(new Object[] { "9                     ", 1L });
        values.add(new Object[] { "null", 3L });
        values.add(new Object[] { "a", 3L });

        benIndicator.storeSqlResults(values);
        HashMap<Object, Long> valueMap = benIndicator.getValueToFreq();
        Assert.assertEquals(valueMap.size(), 10);
        assertinternal(valueMap);
        Assert.assertTrue(valueMap.get("8") == 3L);
        Assert.assertTrue(valueMap.get("9") == 1L);
        Assert.assertTrue(valueMap.get("8     ") == null);
        Assert.assertTrue(valueMap.get("invalid") == 6L);

    }
}
