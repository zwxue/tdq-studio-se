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
package org.talend.dq.analysis.explore;


import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.talend.cwm.management.i18n.Messages;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.indicators.PatternMatchingIndicator;
import org.talend.dq.dbms.DbmsLanguage;
import org.talend.dq.dbms.DbmsLanguageFactory;
import org.talend.dq.indicators.preview.table.ChartDataEntity;
import org.talend.dq.nodes.indicator.type.IndicatorEnum;
import orgomg.cwm.objectmodel.core.Expression;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC scorreia class global comment. Detailled comment
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ DbmsLanguageFactory.class, IndicatorEnum.class, Messages.class })
public class PatternExplorerTest {

    // @Rule
    // public PowerMockRule powerMockRule = new PowerMockRule();

    private static final String EMPTY_STRING = ""; //$NON-NLS-1$

    private static final String RES = "SELECT *  FROM `tbi`.`customer`  WHERE ((customer.lname = \"sunny\")) AND  `lname` REGEXP 'su.*'"; //$NON-NLS-1$

    private static final String RES_NEG = "SELECT *  FROM `tbi`.`customer`  WHERE (((customer.lname = \"sunny\")) AND  `lname` NOT REGEXP 'su.*' OR `lname` IS NULL )"; //$NON-NLS-1$

    private static final String RES_VAL = "SELECT `lname` FROM `tbi`.`customer`  WHERE ((customer.lname = \"sunny\")) AND  `lname` REGEXP 'su.*' "; //$NON-NLS-1$

    private static final String RES_VAL_NEG = "SELECT `lname` FROM `tbi`.`customer`  WHERE (((customer.lname = \"sunny\")) AND  `lname` NOT REGEXP 'su.*'  OR `lname` IS NULL )"; //$NON-NLS-1$

    private PatternExplorer patternExplorer;

    // used in almost every method in this class
    private DbmsLanguage dbmsLanguage;

    private PatternMatchingIndicator indicator;

    @Before
    public void setUp() throws Exception {
        DataExplorerTestHelper.initDataExplorer();
        patternExplorer = new PatternExplorer();

        // mock setEntity
        indicator = mock(PatternMatchingIndicator.class);
        when(indicator.eClass()).thenReturn(null);

        dbmsLanguage = mock(DbmsLanguage.class);
        when(dbmsLanguage.getRegexPatternString(indicator)).thenReturn(RES);
        when(dbmsLanguage.quote(anyString())).thenReturn("`lname`");
        when(dbmsLanguage.regexLike(anyString(), anyString())).thenReturn(
                "((customer.lname = \"sunny\")) AND  `lname` REGEXP 'su.*'");
        when(dbmsLanguage.regexNotLike(anyString(), anyString())).thenReturn(
                "((customer.lname = \"sunny\")) AND  `lname` NOT REGEXP 'su.*'");

        when(dbmsLanguage.where()).thenReturn(" WHERE ");
        when(dbmsLanguage.and()).thenReturn(" AND ");
        when(dbmsLanguage.from()).thenReturn(" FROM ");

        Analysis analysis = DataExplorerTestHelper.getAnalysis(indicator, dbmsLanguage);
        Assert.assertTrue(patternExplorer.setAnalysis(analysis));

        ChartDataEntity cdEntity = mock(ChartDataEntity.class);
        when(cdEntity.getIndicator()).thenReturn(indicator);

        // when(patternExplorer.getAnalyzedElementName(indicator)).thenReturn( "mocked_column");

        PowerMockito.mockStatic(IndicatorEnum.class);
        when(IndicatorEnum.findIndicatorEnum(indicator.eClass())).thenReturn(IndicatorEnum.RowCountIndicatorEnum);

        // verifyStatic();

        patternExplorer.setEnitty(cdEntity);

    }

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
     * Mock Test method for {@link org.talend.dq.analysis.explore.PatternExplorer#getInvalidRowsStatement()}.
     */
    @Test
    public void testGetInvalidRowsStatement() {
        ModelElement element = mock(ModelElement.class);
        when(indicator.getAnalyzedElement()).thenReturn(element);
        when(element.getName()).thenReturn("lname");
        indicator.setAnalyzedElement(element);

        when(dbmsLanguage.or()).thenReturn(" OR ");
        when(dbmsLanguage.isNull()).thenReturn(" IS NULL ");

        // for method: getFromClause()
        Expression instantiatedExpression = mock(Expression.class);
        when(dbmsLanguage.getInstantiatedExpression(indicator)).thenReturn(instantiatedExpression);
        when(instantiatedExpression.getBody()).thenReturn(" FROM `tbi`.`customer` ");

        String strStatement = patternExplorer.getInvalidRowsStatement();
        // System.out.println(strStatement);
        Assert.assertEquals(RES_NEG, strStatement);
        // TestAnalysisCreation creator = new TestAnalysisCreation();
        //
        // Analysis analysis;
        // try {
        // analysis = creator.createAndRunAnalysis();
        // PatternExplorer patternExplorer = new PatternExplorer();
        // Assert.assertTrue(patternExplorer.setAnalysis(analysis));
        // ChartDataEntity cdEntity = new ChartDataEntity(analysis.getResults().getIndicators().iterator().next(),
        // EMPTY_STRING,
        // EMPTY_STRING);
        // patternExplorer.setEnitty(cdEntity);
        // String strStatement = patternExplorer.getInvalidRowsStatement();
        // System.out.println(strStatement);
        // Assert.assertEquals(RES_NEG, strStatement);
        // } catch (TalendException e) {
        // fail(e.getMessage());
        // }
    }

    /**
     * Test method for {@link org.talend.dq.analysis.explore.PatternExplorer#getValidRowsStatement()}.
     */
    // @Test
    // public void testGetValidRowsStatement() {
    // TestAnalysisCreation creator = new TestAnalysisCreation();
    //
    // Analysis analysis;
    // try {
    // analysis = creator.createAndRunAnalysis();
    // PatternExplorer patternExplorer = new PatternExplorer();
    // Assert.assertTrue(patternExplorer.setAnalysis(analysis));
    // ChartDataEntity cdEntity = new ChartDataEntity(analysis.getResults().getIndicators().iterator().next(),
    // EMPTY_STRING,
    // EMPTY_STRING);
    // patternExplorer.setEnitty(cdEntity);
    // String strStatement = patternExplorer.getValidRowsStatement();
    // System.out.println(strStatement);
    // Assert.assertEquals(RES, strStatement);
    // } catch (TalendException e) {
    // fail(e.getMessage());
    // }
    // }

    /**
     * mock test method
     */
    @Test
    public void testGetValidRowsStatement() {
        ModelElement element = mock(ModelElement.class);
        when(indicator.getAnalyzedElement()).thenReturn(element);
        when(element.getName()).thenReturn("name");
        indicator.setAnalyzedElement(element);

        // for method: getFromClause()
        Expression instantiatedExpression = mock(Expression.class);
        when(dbmsLanguage.getInstantiatedExpression(indicator)).thenReturn(instantiatedExpression);
        when(instantiatedExpression.getBody()).thenReturn(" FROM `tbi`.`customer` ");

        String strStatement = patternExplorer.getValidRowsStatement();
        // System.out.println(strStatement);
        Assert.assertEquals(RES, strStatement);
    }

    /**
     * Test method for {@link org.talend.dq.analysis.explore.PatternExplorer#getQueryMap()}.
     */
    // @Test
    // public void testGetQueryMap() {
    // TestAnalysisCreation creator = new TestAnalysisCreation();
    //
    // Analysis analysis;
    // try {
    // analysis = creator.createAndRunAnalysis();
    // PatternExplorer patternExplorer = new PatternExplorer();
    // Assert.assertTrue(patternExplorer.setAnalysis(analysis));
    // ChartDataEntity cdEntity = new ChartDataEntity(analysis.getResults().getIndicators().iterator().next(),
    // EMPTY_STRING,
    // EMPTY_STRING);
    // patternExplorer.setEnitty(cdEntity);
    // Map<String, String> queryMap = patternExplorer.getQueryMap();
    // Assert.assertNotNull(queryMap);
    // Assert.assertTrue(queryMap.size() == 4);
    // } catch (TalendException e) {
    // fail(e.getMessage());
    // }
    // }

    /**
     * Test method for {@link org.talend.dq.analysis.explore.PatternExplorer#getInvalidValuesStatement()}.
     * 
     * @Test public void testGetInvalidValuesStatement() { TestAnalysisCreation creator = new TestAnalysisCreation();
     * 
     * Analysis analysis; try { analysis = creator.createAndRunAnalysis(); PatternExplorer patternExplorer = new
     * PatternExplorer(); Assert.assertTrue(patternExplorer.setAnalysis(analysis)); ChartDataEntity cdEntity = new
     * ChartDataEntity(analysis.getResults().getIndicators().iterator().next(), EMPTY_STRING, EMPTY_STRING);
     * patternExplorer.setEnitty(cdEntity); String strStatement = patternExplorer.getInvalidValuesStatement();
     * System.out.println(strStatement); Assert.assertEquals(RES_VAL_NEG, strStatement); } catch (TalendException e) {
     * fail(e.getMessage()); } }
     */

    /**
     * Test method for {@link org.talend.dq.analysis.explore.PatternExplorer#getValidValuesStatement()}.
     * 
     * @Test public void testGetValidValuesStatement() { TestAnalysisCreation creator = new TestAnalysisCreation();
     * 
     * Analysis analysis; try { analysis = creator.createAndRunAnalysis(); PatternExplorer patternExplorer = new
     * PatternExplorer(); Assert.assertTrue(patternExplorer.setAnalysis(analysis)); ChartDataEntity cdEntity = new
     * ChartDataEntity(analysis.getResults().getIndicators().iterator().next(), EMPTY_STRING, EMPTY_STRING);
     * patternExplorer.setEnitty(cdEntity); String strStatement = patternExplorer.getValidValuesStatement();
     * System.out.println(strStatement); Assert.assertEquals(RES_VAL, strStatement); } catch (TalendException e) {
     * fail(e.getMessage()); } }
     */

}
