package org.talend.dataquality.indicators.impl;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * mzhao Junit test for mean indicator
 */
public class MeanIndicatorImplTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testGetMean() {
        MeanIndicatorImpl meanInd = new MeanIndicatorImpl();
        meanInd.setSumStr("18");
        meanInd.setCount(6L);
        meanInd.setNullCount(3L);
        assertTrue(meanInd.getMean().doubleValue() == 3);
        meanInd.setNullCount(null);
        assertTrue(meanInd.getMean().doubleValue() == 3);
        meanInd.setNullCount(0L);
        assertTrue(meanInd.getMean().doubleValue() == 3);
        meanInd.setCount(6L);
        meanInd.setNullCount(6L);
        assertTrue(meanInd.getMean().doubleValue() == 3);
    }

}
