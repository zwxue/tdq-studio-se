package org.talend.datascience.common.statistics;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class MeanValueAnalyzerTest {

    private MeanValueAnalyzer meanAnalyzer = null;

    @Before
    public void setUp() throws Exception {
        meanAnalyzer = new MeanValueAnalyzer();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testAnalyze() {
        // 1. test with values of all double.
        String[] dValues = new String[] { "20", "0.3", "3", "4.5", "8" };
        meanAnalyzer.init();
        for (String strValue : dValues) {
            meanAnalyzer.analyze(strValue);
        }
        meanAnalyzer.end();
        Assert.assertEquals(7.159999999999999, meanAnalyzer.getResult().get(0).getMean(), 0);

        // 2. assert with only one digits
        dValues = new String[] { "10" };
        meanAnalyzer.init();
        for (String strValue : dValues) {
            meanAnalyzer.analyze(strValue);
        }
        meanAnalyzer.end();
        Assert.assertEquals(10, meanAnalyzer.getResult().get(0).getMean(), 0);

        // 3. assert with values contain a str
        meanAnalyzer.init();
        dValues = new String[] { "20", "a str", "3", "4.5", "8" };
        for (String strValue : dValues) {
            meanAnalyzer.analyze(strValue);
        }
        meanAnalyzer.end();
        Assert.assertEquals(8.875, meanAnalyzer.getResult().get(0).getMean(), 0);

        // 4. assert with empty
        meanAnalyzer.init();
        dValues = new String[] { "" };
        for (String strValue : dValues) {
            meanAnalyzer.analyze(strValue);
        }
        meanAnalyzer.end();
        Assert.assertTrue(Double.isNaN(meanAnalyzer.getResult().get(0).getMean()));

    }

}
