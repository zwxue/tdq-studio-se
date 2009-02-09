// ============================================================================
//
// Copyright (C) 2006-2009 Talend Inc. - www.talend.com
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

import org.junit.Assert;
import org.junit.Test;
import org.talend.cwm.exception.TalendException;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dq.analysis.TestAnalysisCreation;

/**
 * DOC scorreia class global comment. Detailled comment
 */
public class PatternExplorerTest {

    private static final String RES = "SELECT *  FROM weka.arff WHERE ((arff.outlook = \"sunny\")) AND  outlook REGEXP 'su.*' ";

    private static final String RES_NEG = "SELECT *  FROM weka.arff WHERE ((arff.outlook = \"sunny\")) AND  outlook NOT REGEXP 'su.*' ";

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
            // Assert.assertTrue(patternExplorer.setIndicator(analysis.getResults().getIndicators().iterator().next()));
            Assert.assertTrue(patternExplorer.setAnalysis(analysis));
            String validRowsStatement = patternExplorer.getInvalidRowsStatement();
            System.out.println(validRowsStatement);
            Assert.assertEquals(RES_NEG, validRowsStatement);
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
            // Assert.assertTrue(patternExplorer.setIndicator(analysis.getResults().getIndicators().iterator().next()));
            Assert.assertTrue(patternExplorer.setAnalysis(analysis));
            String validRowsStatement = patternExplorer.getValidRowsStatement();
            System.out.println(validRowsStatement);
            Assert.assertEquals(RES, validRowsStatement);
        } catch (TalendException e) {
            fail(e.getMessage());
        }
    }
}
