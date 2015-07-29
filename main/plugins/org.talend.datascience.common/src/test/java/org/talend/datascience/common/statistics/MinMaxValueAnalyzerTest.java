package org.talend.datascience.common.statistics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MinMaxValueAnalyzerTest {

    MinValueAnalyzer analyzerMin = null;
    MaxValueAnalyzer analyzerMax = null;

    @Before
    public void setUp() throws Exception {
        analyzerMin = new MinValueAnalyzer();
        analyzerMax = new MaxValueAnalyzer();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testAnalyze() {
        // 1. assert case of all double values.
        String[] pureDouble = new String[] { "20", "0.3", "3", "4.5", "8" };
        for (String strValue : pureDouble) {
            analyzerMin.analyze(strValue);
            analyzerMax.analyze(strValue);
        }
        assertEquals(0.3, analyzerMin.getResult().get(0).getMin(), 0);
        assertEquals(20, analyzerMax.getResult().get(0).getMax(), 0);

        // 2. assert case of double values with one str
        pureDouble = new String[] { "20", "a str", "3", "4.5", "8" };
        for (String strValue : pureDouble) {
            analyzerMin.analyze(strValue);
            analyzerMax.analyze(strValue);
        }
        assertEquals(0.3, analyzerMin.getResult().get(0).getMin(), 0);
        assertEquals(20, analyzerMax.getResult().get(0).getMax(), 0);

    }

    @Test
    public void testAnalyzeStr() {
        // 2. assert case of double values with one str
        String[] doubleWithStr = new String[] { "20", "a str", "3", "4.5", "8" };
        for (String strValue : doubleWithStr) {
            analyzerMin.analyze(strValue);
            analyzerMax.analyze(strValue);
        }
        assertEquals(3, analyzerMin.getResult().get(0).getMin(), 0);
        assertEquals(20, analyzerMax.getResult().get(0).getMax(), 0);

    }

    @Test
    public void testAnalyzeEmptyArray() {
        // 2. assert case of double values with empty
        String[] doubleWithEmpty = new String[] { "" };
        for (String strValue : doubleWithEmpty) {
            analyzerMin.analyze(strValue);
            analyzerMax.analyze(strValue);
        }
        assertTrue(Double.isNaN(analyzerMin.getResult().get(0).getMin()));
        assertTrue(Double.isNaN(analyzerMax.getResult().get(0).getMax()));
    }
}
