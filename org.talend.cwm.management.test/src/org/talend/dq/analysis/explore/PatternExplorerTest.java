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
package org.talend.dq.analysis.explore;

import static org.junit.Assert.fail;

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.talend.cwm.exception.TalendException;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dq.analysis.TestAnalysisCreation;
import org.talend.dq.indicators.preview.table.ChartDataEntity;

/**
 * DOC scorreia class global comment. Detailled comment
 */
public class PatternExplorerTest {

    private static final String EMPTY_STRING = ""; //$NON-NLS-1$

    private static final String RES = "SELECT *  FROM `tbi`.`customer`  WHERE ((customer.lname = \"sunny\")) AND  `lname` REGEXP 'su.*' "; //$NON-NLS-1$

    private static final String RES_NEG = "SELECT *  FROM `tbi`.`customer`  WHERE ((customer.lname = \"sunny\")) AND  `lname` NOT REGEXP 'su.*'  OR `lname` IS NULL "; //$NON-NLS-1$

    private static final String RES_VAL = "SELECT `lname` FROM `tbi`.`customer`  WHERE ((customer.lname = \"sunny\")) AND  `lname` REGEXP 'su.*' "; //$NON-NLS-1$

    private static final String RES_VAL_NEG = "SELECT `lname` FROM `tbi`.`customer`  WHERE ((customer.lname = \"sunny\")) AND  `lname` NOT REGEXP 'su.*'  OR `lname` IS NULL "; //$NON-NLS-1$

    /**
     * Test method for {@link org.talend.dq.analysis.explore.PatternExplorer#PatternExplorer()}.
     */
    @Test
    public void testPatternExplorer() {
        try {
            PatternExplorer pe = new PatternExplorer();
            Assert.assertNotNull(pe);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Test method for {@link org.talend.dq.analysis.explore.PatternExplorer#getInvalidRowsStatement()}.
     */
    @Test
    public void testGetInvalidRowsStatement() {
        TestAnalysisCreation creator = new TestAnalysisCreation();

        Analysis analysis;
        try {
            analysis = creator.createAndRunAnalysis();
            PatternExplorer patternExplorer = new PatternExplorer();
            Assert.assertTrue(patternExplorer.setAnalysis(analysis));
            ChartDataEntity cdEntity = new ChartDataEntity(analysis.getResults().getIndicators().iterator().next(), EMPTY_STRING,
                    EMPTY_STRING);
            patternExplorer.setEnitty(cdEntity);
            String strStatement = patternExplorer.getInvalidRowsStatement();
            System.out.println(strStatement);
            Assert.assertEquals(RES_NEG, strStatement);
        } catch (TalendException e) {
            fail(e.getMessage());
        }
    }

    /**
     * Test method for {@link org.talend.dq.analysis.explore.PatternExplorer#getValidRowsStatement()}.
     */
    @Test
    public void testGetValidRowsStatement() {
        TestAnalysisCreation creator = new TestAnalysisCreation();

        Analysis analysis;
        try {
            analysis = creator.createAndRunAnalysis();
            PatternExplorer patternExplorer = new PatternExplorer();
            Assert.assertTrue(patternExplorer.setAnalysis(analysis));
            ChartDataEntity cdEntity = new ChartDataEntity(analysis.getResults().getIndicators().iterator().next(), EMPTY_STRING,
                    EMPTY_STRING);
            patternExplorer.setEnitty(cdEntity);
            String strStatement = patternExplorer.getValidRowsStatement();
            System.out.println(strStatement);
            Assert.assertEquals(RES, strStatement);
        } catch (TalendException e) {
            fail(e.getMessage());
        }
    }

    /**
     * Test method for {@link org.talend.dq.analysis.explore.PatternExplorer#getQueryMap()}.
     */
    @Test
    public void testGetQueryMap() {
        TestAnalysisCreation creator = new TestAnalysisCreation();

        Analysis analysis;
        try {
            analysis = creator.createAndRunAnalysis();
            PatternExplorer patternExplorer = new PatternExplorer();
            Assert.assertTrue(patternExplorer.setAnalysis(analysis));
            ChartDataEntity cdEntity = new ChartDataEntity(analysis.getResults().getIndicators().iterator().next(), EMPTY_STRING,
                    EMPTY_STRING);
            patternExplorer.setEnitty(cdEntity);
            Map<String, String> queryMap = patternExplorer.getQueryMap();
            Assert.assertNotNull(queryMap);
            Assert.assertTrue(queryMap.size() == 4);
        } catch (TalendException e) {
            fail(e.getMessage());
        }
    }

    /**
     * Test method for {@link org.talend.dq.analysis.explore.PatternExplorer#getInvalidValuesStatement()}.
     */
    @Test
    public void testGetInvalidValuesStatement() {
        TestAnalysisCreation creator = new TestAnalysisCreation();

        Analysis analysis;
        try {
            analysis = creator.createAndRunAnalysis();
            PatternExplorer patternExplorer = new PatternExplorer();
            Assert.assertTrue(patternExplorer.setAnalysis(analysis));
            ChartDataEntity cdEntity = new ChartDataEntity(analysis.getResults().getIndicators().iterator().next(), EMPTY_STRING,
                    EMPTY_STRING);
            patternExplorer.setEnitty(cdEntity);
            String strStatement = patternExplorer.getInvalidValuesStatement();
            System.out.println(strStatement);
            Assert.assertEquals(RES_VAL_NEG, strStatement);
        } catch (TalendException e) {
            fail(e.getMessage());
        }
    }

    /**
     * Test method for {@link org.talend.dq.analysis.explore.PatternExplorer#getValidValuesStatement()}.
     */
    @Test
    public void testGetValidValuesStatement() {
        TestAnalysisCreation creator = new TestAnalysisCreation();

        Analysis analysis;
        try {
            analysis = creator.createAndRunAnalysis();
            PatternExplorer patternExplorer = new PatternExplorer();
            Assert.assertTrue(patternExplorer.setAnalysis(analysis));
            ChartDataEntity cdEntity = new ChartDataEntity(analysis.getResults().getIndicators().iterator().next(), EMPTY_STRING,
                    EMPTY_STRING);
            patternExplorer.setEnitty(cdEntity);
            String strStatement = patternExplorer.getValidValuesStatement();
            System.out.println(strStatement);
            Assert.assertEquals(RES_VAL, strStatement);
        } catch (TalendException e) {
            fail(e.getMessage());
        }
    }
}
