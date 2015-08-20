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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.Types;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.talend.cwm.management.i18n.Messages;
import org.talend.cwm.relational.TdColumn;
import org.talend.cwm.relational.TdExpression;
import org.talend.cwm.relational.TdSqlDataType;
import org.talend.cwm.relational.TdTable;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.analysis.AnalysisContext;
import org.talend.dataquality.analysis.AnalysisParameters;
import org.talend.dataquality.analysis.ExecutionLanguage;
import org.talend.dataquality.helpers.BooleanExpressionHelper;
import org.talend.dataquality.indicators.IndicatorParameters;
import org.talend.dataquality.indicators.IndicatorsFactory;
import org.talend.dataquality.indicators.PatternMatchingIndicator;
import org.talend.dataquality.indicators.definition.userdefine.UDIndicatorDefinition;
import org.talend.dataquality.indicators.definition.userdefine.UserdefineFactory;
import org.talend.dataquality.indicators.sql.IndicatorSqlFactory;
import org.talend.dataquality.indicators.sql.UserDefIndicator;
import org.talend.dq.dbms.DbmsLanguage;
import org.talend.dq.dbms.DbmsLanguageFactory;
import org.talend.dq.indicators.preview.table.ChartDataEntity;
import org.talend.dq.nodes.indicator.type.IndicatorEnum;
import org.talend.utils.dates.DateUtils;
import orgomg.cwm.foundation.softwaredeployment.DataManager;
import orgomg.cwm.objectmodel.core.Expression;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC scorreia class global comment. Detailled comment
 */
@PrepareForTest({ DbmsLanguageFactory.class, IndicatorEnum.class, Messages.class })
public class PatternExplorerTest {

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    private static final String ViewValidValues = "SELECT <%=__COLUMN_NAMES__%> FROM <%=__TABLE_NAME__%> <%=__WHERE_CLAUSE__%>"; //$NON-NLS-1$

    private static final String ViewInvalidValues = "SELECT <%=__COLUMN_NAMES__%> FROM <%=__TABLE_NAME__%> WHERE NOT (id>=1) <%=__AND_WHERE_CLAUSE__%>"; //$NON-NLS-1$

    private static final String ViewValidRows = "SELECT * FROM <%=__TABLE_NAME__%> <%=__WHERE_CLAUSE__%>"; //$NON-NLS-1$

    private static final String ViewInvalidRows = "SELECT * FROM <%=__TABLE_NAME__%> WHERE NOT (id>=1) <%=__AND_WHERE_CLAUSE__%>"; //$NON-NLS-1$

    private static final String RES_VALIED_ROWS = "SELECT *  FROM `tbi`.`customer`  WHERE ((customer.lname = \"sunny\")) AND  `lname` REGEXP 'su.*'"; //$NON-NLS-1$

    private static final String RES_INVALIED_ROWS = "SELECT *  FROM `tbi`.`customer`  WHERE (((customer.lname = \"sunny\")) AND  `lname` NOT REGEXP 'su.*' OR `lname` IS NULL )"; //$NON-NLS-1$

    private static final String RES_VALIED_VALUES = "SELECT `lname` FROM `tbi`.`customer`  WHERE ((customer.lname = \"sunny\")) AND  `lname` REGEXP 'su.*'"; //$NON-NLS-1$

    private static final String RES_INVALIED_VALUES = "SELECT `lname` FROM `tbi`.`customer`  WHERE (((customer.lname = \"sunny\")) AND  `lname` NOT REGEXP 'su.*' OR `lname` IS NULL )"; //$NON-NLS-1$

    private PatternExplorer patternExplorer;

    /**
     * DOC msjian Comment method "setUp".
     * 
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        DataExplorerTestHelper.initDataExplorer();

        patternExplorer = new RegexPatternExplorer();

        // mock setEntity
        PatternMatchingIndicator indicator = mock(PatternMatchingIndicator.class);
        when(indicator.eClass()).thenReturn(null);

        ModelElement element = mock(ModelElement.class);
        when(element.getName()).thenReturn("lname"); //$NON-NLS-1$

        when(indicator.getAnalyzedElement()).thenReturn(element);
        indicator.setAnalyzedElement(element);

        DbmsLanguage dbmsLanguage = mock(DbmsLanguage.class);
        when(dbmsLanguage.getRegexPatternString(indicator)).thenReturn(RES_VALIED_ROWS);
        when(dbmsLanguage.quote(anyString())).thenReturn("`lname`"); //$NON-NLS-1$
        when(dbmsLanguage.regexLike(anyString(), anyString())).thenReturn(
                "((customer.lname = \"sunny\")) AND  `lname` REGEXP 'su.*'"); //$NON-NLS-1$
        when(dbmsLanguage.regexNotLike(anyString(), anyString())).thenReturn(
                "((customer.lname = \"sunny\")) AND  `lname` NOT REGEXP 'su.*'"); //$NON-NLS-1$
        when(dbmsLanguage.getFunctionReturnValue()).thenReturn(""); //$NON-NLS-1$ 

        when(dbmsLanguage.where()).thenReturn(" WHERE "); //$NON-NLS-1$
        when(dbmsLanguage.and()).thenReturn(" AND "); //$NON-NLS-1$
        when(dbmsLanguage.from()).thenReturn(" FROM "); //$NON-NLS-1$
        when(dbmsLanguage.or()).thenReturn(" OR "); //$NON-NLS-1$
        when(dbmsLanguage.isNull()).thenReturn(" IS NULL "); //$NON-NLS-1$

        Analysis analysis = DataExplorerTestHelper.getAnalysis(indicator, dbmsLanguage);
        patternExplorer.setAnalysis(analysis);

        ChartDataEntity cdEntity = mock(ChartDataEntity.class);
        when(cdEntity.getIndicator()).thenReturn(indicator);

        PowerMockito.mockStatic(IndicatorEnum.class);
        when(IndicatorEnum.findIndicatorEnum(indicator.eClass())).thenReturn(IndicatorEnum.RowCountIndicatorEnum);

        patternExplorer.setEnitty(cdEntity);

        // for method: getFromClause()
        Expression instantiatedExpression = mock(Expression.class);
        when(dbmsLanguage.getInstantiatedExpression(indicator)).thenReturn(instantiatedExpression);
        when(instantiatedExpression.getBody()).thenReturn(" FROM `tbi`.`customer` "); //$NON-NLS-1$
    }

    /**
     * Test method for {@link org.talend.dq.analysis.explore.PatternExplorer#PatternExplorer()}.
     */
    @Test
    public void testPatternExplorer() {
        try {
            RegexPatternExplorer pe = new RegexPatternExplorer();
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
        String strStatement = patternExplorer.getInvalidRowsStatement();
        Assert.assertEquals(RES_INVALIED_ROWS, strStatement);
    }

    /**
     * mock test method
     */
    @Test
    public void testGetValidRowsStatement() {
        String strStatement = patternExplorer.getValidRowsStatement();
        Assert.assertEquals(RES_VALIED_ROWS, strStatement);
    }

    /**
     * Mock Test method for {@link org.talend.dq.analysis.explore.PatternExplorer#getInvalidValuesStatement()}.
     */
    @Test
    public void testGetInvalidValuesStatement() {
        String strStatement = patternExplorer.getInvalidValuesStatement();
        Assert.assertEquals(RES_INVALIED_VALUES, strStatement);
    }

    /**
     * Mock Test method for {@link org.talend.dq.analysis.explore.PatternExplorer#getValidValuesStatement()}.
     */
    @Test
    public void testGetValidValuesStatement() {
        String strStatement = patternExplorer.getValidValuesStatement();
        Assert.assertEquals(RES_VALIED_VALUES, strStatement);
    }

    /**
     * Mock Test method for {@link org.talend.dq.analysis.explore.PatternExplorer#getValidValuesStatement()}. when the
     * test for indicator is user define indicator
     */
    @Test
    public void testGetValidValuesStatement_2() {
        // mock an analysis for the super class.
        Analysis analysis = mock(Analysis.class);
        AnalysisParameters parameters = mock(AnalysisParameters.class);
        when(parameters.getExecutionLanguage()).thenReturn(ExecutionLanguage.SQL);
        when(analysis.getParameters()).thenReturn(parameters);
        AnalysisContext context = mock(AnalysisContext.class);
        when(analysis.getContext()).thenReturn(context);
        DataManager dataManager = mock(DataManager.class);
        when(context.getConnection()).thenReturn(dataManager);

        DbmsLanguage dbmsLanguage = mock(DbmsLanguage.class);
        when(dbmsLanguage.getDbmsName()).thenReturn("MySQL"); //$NON-NLS-1$
        when(dbmsLanguage.getDbVersion()).thenReturn(null);

        TdTable table = org.talend.cwm.relational.RelationalFactory.eINSTANCE.createTdTable();
        table.setName("TDQ_CALENDAR"); //$NON-NLS-1$
        TdColumn column = org.talend.cwm.relational.RelationalFactory.eINSTANCE.createTdColumn();
        column.setName("CAL_DATE"); //$NON-NLS-1$
        TdSqlDataType tdsql = org.talend.cwm.relational.RelationalFactory.eINSTANCE.createTdSqlDataType();
        tdsql.setName("DATE"); //$NON-NLS-1$
        tdsql.setJavaDataType(Types.DATE);
        column.setSqlDataType(tdsql);
        table.getOwnedElement().add(column);
        column.setOwner(table);

        // create indicator
        UserDefIndicator userDefIndicator = IndicatorSqlFactory.eINSTANCE.createUserDefIndicator();
        UDIndicatorDefinition indicatorDefinition = UserdefineFactory.eINSTANCE.createUDIndicatorDefinition();
        indicatorDefinition.setName("user define"); //$NON-NLS-1$
        userDefIndicator.setName(indicatorDefinition.getName());
        userDefIndicator.setIndicatorDefinition(indicatorDefinition);

        TdExpression newTdExp = BooleanExpressionHelper.createTdExpression("MySQL", //$NON-NLS-1$
                ViewValidValues, null);
        newTdExp.setModificationDate(DateUtils.getCurrentDate(DateUtils.PATTERN_5));
        indicatorDefinition.getViewValidValuesExpression().add(newTdExp);

        ChartDataEntity chartDataEntity = new ChartDataEntity(userDefIndicator, "2012-06-05", "1"); //$NON-NLS-1$  //$NON-NLS-2$
        chartDataEntity.setLabelNull(false);
        chartDataEntity.setKey("2012-06-05"); //$NON-NLS-1$
        assertFalse(chartDataEntity.isLabelNull());

        userDefIndicator.setAnalyzedElement(column);
        IndicatorParameters indicatorParameters = IndicatorsFactory.eINSTANCE.createIndicatorParameters();
        indicatorParameters.setDateParameters(null);
        userDefIndicator.setParameters(indicatorParameters);
        assertNull(indicatorParameters.getDateParameters());

        PowerMockito.mockStatic(DbmsLanguageFactory.class);
        when(DbmsLanguageFactory.createDbmsLanguage(dataManager)).thenReturn(dbmsLanguage);
        when(DbmsLanguageFactory.compareDbmsLanguage("MySQL", "MySQL")).thenReturn(true); //$NON-NLS-1$ //$NON-NLS-2$

        when(dbmsLanguage.toQualifiedName(null, null, "TDQ_CALENDAR")).thenReturn("TDQ_CALENDAR"); //$NON-NLS-1$ //$NON-NLS-2$
        when(
                dbmsLanguage.fillGenericQueryWithColumnsAndTable(
                        "SELECT <%=__COLUMN_NAMES__%> FROM <%=__TABLE_NAME__%> ", "CAL_DATE", "TDQ_CALENDAR")) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                .thenReturn("SELECT CAL_DATE FROM TDQ_CALENDAR"); //$NON-NLS-1$

        RegexPatternExplorer freqExp = new RegexPatternExplorer();
        Assert.assertTrue(freqExp.setAnalysis(analysis));
        freqExp.setEnitty(chartDataEntity);

        String clause = freqExp.getValidValuesStatement();

        assertEquals("SELECT CAL_DATE FROM TDQ_CALENDAR", clause); //$NON-NLS-1$
    }

    /**
     * Mock Test method for {@link org.talend.dq.analysis.explore.PatternExplorer#getInvalidValuesStatement()}. when the
     * test for indicator is user define indicator
     */
    @Test
    public void testGetInvalidValuesStatement_2() {
        // mock an analysis for the super class.
        Analysis analysis = mock(Analysis.class);
        AnalysisParameters parameters = mock(AnalysisParameters.class);
        when(parameters.getExecutionLanguage()).thenReturn(ExecutionLanguage.SQL);
        when(analysis.getParameters()).thenReturn(parameters);
        AnalysisContext context = mock(AnalysisContext.class);
        when(analysis.getContext()).thenReturn(context);
        DataManager dataManager = mock(DataManager.class);
        when(context.getConnection()).thenReturn(dataManager);

        DbmsLanguage dbmsLanguage = mock(DbmsLanguage.class);
        when(dbmsLanguage.getDbmsName()).thenReturn("MySQL"); //$NON-NLS-1$
        when(dbmsLanguage.getDbVersion()).thenReturn(null);

        TdTable table = org.talend.cwm.relational.RelationalFactory.eINSTANCE.createTdTable();
        table.setName("TDQ_CALENDAR"); //$NON-NLS-1$
        TdColumn column = org.talend.cwm.relational.RelationalFactory.eINSTANCE.createTdColumn();
        column.setName("CAL_DATE"); //$NON-NLS-1$
        TdSqlDataType tdsql = org.talend.cwm.relational.RelationalFactory.eINSTANCE.createTdSqlDataType();
        tdsql.setName("DATE"); //$NON-NLS-1$
        tdsql.setJavaDataType(Types.DATE);
        column.setSqlDataType(tdsql);
        table.getOwnedElement().add(column);
        column.setOwner(table);

        // create indicator
        UserDefIndicator userDefIndicator = IndicatorSqlFactory.eINSTANCE.createUserDefIndicator();
        UDIndicatorDefinition indicatorDefinition = UserdefineFactory.eINSTANCE.createUDIndicatorDefinition();
        indicatorDefinition.setName("user define"); //$NON-NLS-1$
        userDefIndicator.setName(indicatorDefinition.getName());
        userDefIndicator.setIndicatorDefinition(indicatorDefinition);

        TdExpression newTdExp = BooleanExpressionHelper.createTdExpression("MySQL", //$NON-NLS-1$
                ViewInvalidValues, null);
        newTdExp.setModificationDate(DateUtils.getCurrentDate(DateUtils.PATTERN_5));
        indicatorDefinition.getViewInvalidValuesExpression().add(newTdExp);

        ChartDataEntity chartDataEntity = new ChartDataEntity(userDefIndicator, "2012-06-05", "1"); //$NON-NLS-1$  //$NON-NLS-2$
        chartDataEntity.setLabelNull(false);
        chartDataEntity.setKey("2012-06-05"); //$NON-NLS-1$
        assertFalse(chartDataEntity.isLabelNull());

        userDefIndicator.setAnalyzedElement(column);
        IndicatorParameters indicatorParameters = IndicatorsFactory.eINSTANCE.createIndicatorParameters();
        indicatorParameters.setDateParameters(null);
        userDefIndicator.setParameters(indicatorParameters);
        assertNull(indicatorParameters.getDateParameters());

        PowerMockito.mockStatic(DbmsLanguageFactory.class);
        when(DbmsLanguageFactory.createDbmsLanguage(dataManager)).thenReturn(dbmsLanguage);
        when(DbmsLanguageFactory.compareDbmsLanguage("MySQL", "MySQL")).thenReturn(true); //$NON-NLS-1$ //$NON-NLS-2$
        when(dbmsLanguage.toQualifiedName(null, null, "TDQ_CALENDAR")).thenReturn("TDQ_CALENDAR"); //$NON-NLS-1$ //$NON-NLS-2$
        when(
                dbmsLanguage.fillGenericQueryWithColumnsAndTable(
                        "SELECT <%=__COLUMN_NAMES__%> FROM <%=__TABLE_NAME__%> WHERE NOT (id>=1) ", "CAL_DATE", "TDQ_CALENDAR")) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                .thenReturn("SELECT CAL_DATE FROM TDQ_CALENDAR WHERE NOT (id>=1)"); //$NON-NLS-1$

        RegexPatternExplorer freqExp = new RegexPatternExplorer();
        Assert.assertTrue(freqExp.setAnalysis(analysis));
        freqExp.setEnitty(chartDataEntity);

        String clause = freqExp.getInvalidValuesStatement();

        assertEquals("SELECT CAL_DATE FROM TDQ_CALENDAR WHERE NOT (id>=1)", clause); //$NON-NLS-1$
    }

    /**
     * Mock Test method for {@link org.talend.dq.analysis.explore.PatternExplorer#getInvalidRowsStatement()}. when the
     * test for indicator is user define indicator
     */
    @Test
    public void testGetInvalidRowsStatement_2() {
        // mock an analysis for the super class.
        Analysis analysis = mock(Analysis.class);
        AnalysisParameters parameters = mock(AnalysisParameters.class);
        when(parameters.getExecutionLanguage()).thenReturn(ExecutionLanguage.SQL);
        when(analysis.getParameters()).thenReturn(parameters);
        AnalysisContext context = mock(AnalysisContext.class);
        when(analysis.getContext()).thenReturn(context);
        DataManager dataManager = mock(DataManager.class);
        when(context.getConnection()).thenReturn(dataManager);

        DbmsLanguage dbmsLanguage = mock(DbmsLanguage.class);
        when(dbmsLanguage.getDbmsName()).thenReturn("MySQL"); //$NON-NLS-1$
        when(dbmsLanguage.getDbVersion()).thenReturn(null);

        TdTable table = org.talend.cwm.relational.RelationalFactory.eINSTANCE.createTdTable();
        table.setName("TDQ_CALENDAR"); //$NON-NLS-1$
        TdColumn column = org.talend.cwm.relational.RelationalFactory.eINSTANCE.createTdColumn();
        column.setName("CAL_DATE"); //$NON-NLS-1$
        TdSqlDataType tdsql = org.talend.cwm.relational.RelationalFactory.eINSTANCE.createTdSqlDataType();
        tdsql.setName("DATE"); //$NON-NLS-1$
        tdsql.setJavaDataType(Types.DATE);
        column.setSqlDataType(tdsql);
        table.getOwnedElement().add(column);
        column.setOwner(table);

        // create indicator
        UserDefIndicator userDefIndicator = IndicatorSqlFactory.eINSTANCE.createUserDefIndicator();
        UDIndicatorDefinition indicatorDefinition = UserdefineFactory.eINSTANCE.createUDIndicatorDefinition();
        indicatorDefinition.setName("user define"); //$NON-NLS-1$
        userDefIndicator.setName(indicatorDefinition.getName());
        userDefIndicator.setIndicatorDefinition(indicatorDefinition);

        TdExpression newTdExp = BooleanExpressionHelper.createTdExpression("MySQL", //$NON-NLS-1$
                ViewInvalidRows, null);
        newTdExp.setModificationDate(DateUtils.getCurrentDate(DateUtils.PATTERN_5));
        indicatorDefinition.getViewInvalidRowsExpression().add(newTdExp);

        ChartDataEntity chartDataEntity = new ChartDataEntity(userDefIndicator, "2012-06-05", "1"); //$NON-NLS-1$  //$NON-NLS-2$
        chartDataEntity.setLabelNull(false);
        chartDataEntity.setKey("2012-06-05"); //$NON-NLS-1$
        assertFalse(chartDataEntity.isLabelNull());

        userDefIndicator.setAnalyzedElement(column);
        IndicatorParameters indicatorParameters = IndicatorsFactory.eINSTANCE.createIndicatorParameters();
        indicatorParameters.setDateParameters(null);
        userDefIndicator.setParameters(indicatorParameters);
        assertNull(indicatorParameters.getDateParameters());

        PowerMockito.mockStatic(DbmsLanguageFactory.class);
        when(DbmsLanguageFactory.createDbmsLanguage(dataManager)).thenReturn(dbmsLanguage);
        when(DbmsLanguageFactory.compareDbmsLanguage("MySQL", "MySQL")).thenReturn(true); //$NON-NLS-1$ //$NON-NLS-2$
        when(dbmsLanguage.toQualifiedName(null, null, "TDQ_CALENDAR")).thenReturn("TDQ_CALENDAR"); //$NON-NLS-1$ //$NON-NLS-2$
        when(
                dbmsLanguage.fillGenericQueryWithColumnsAndTable(
                        "SELECT * FROM <%=__TABLE_NAME__%> WHERE NOT (id>=1) ", "CAL_DATE", "TDQ_CALENDAR")) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                .thenReturn("SELECT * FROM TDQ_CALENDAR WHERE NOT (id>=1)"); //$NON-NLS-1$

        RegexPatternExplorer freqExp = new RegexPatternExplorer();
        Assert.assertTrue(freqExp.setAnalysis(analysis));
        freqExp.setEnitty(chartDataEntity);

        String clause = freqExp.getInvalidRowsStatement();

        assertEquals("SELECT * FROM TDQ_CALENDAR WHERE NOT (id>=1)", clause); //$NON-NLS-1$
    }

    /**
     * Mock Test method for {@link org.talend.dq.analysis.explore.PatternExplorer#getValidRowsStatement()}. when the
     * test for indicator is user define indicator
     */
    @Test
    public void testGetValidRowsStatement_2() {
        // mock an analysis for the super class.
        Analysis analysis = mock(Analysis.class);
        AnalysisParameters parameters = mock(AnalysisParameters.class);
        when(parameters.getExecutionLanguage()).thenReturn(ExecutionLanguage.SQL);
        when(analysis.getParameters()).thenReturn(parameters);
        AnalysisContext context = mock(AnalysisContext.class);
        when(analysis.getContext()).thenReturn(context);
        DataManager dataManager = mock(DataManager.class);
        when(context.getConnection()).thenReturn(dataManager);

        DbmsLanguage dbmsLanguage = mock(DbmsLanguage.class);
        when(dbmsLanguage.getDbmsName()).thenReturn("MySQL"); //$NON-NLS-1$
        when(dbmsLanguage.getDbVersion()).thenReturn(null);

        TdTable table = org.talend.cwm.relational.RelationalFactory.eINSTANCE.createTdTable();
        table.setName("TDQ_CALENDAR"); //$NON-NLS-1$
        TdColumn column = org.talend.cwm.relational.RelationalFactory.eINSTANCE.createTdColumn();
        column.setName("CAL_DATE"); //$NON-NLS-1$
        TdSqlDataType tdsql = org.talend.cwm.relational.RelationalFactory.eINSTANCE.createTdSqlDataType();
        tdsql.setName("DATE"); //$NON-NLS-1$
        tdsql.setJavaDataType(Types.DATE);
        column.setSqlDataType(tdsql);
        table.getOwnedElement().add(column);
        column.setOwner(table);

        // create indicator
        UserDefIndicator userDefIndicator = IndicatorSqlFactory.eINSTANCE.createUserDefIndicator();
        UDIndicatorDefinition indicatorDefinition = UserdefineFactory.eINSTANCE.createUDIndicatorDefinition();
        indicatorDefinition.setName("user define"); //$NON-NLS-1$
        userDefIndicator.setName(indicatorDefinition.getName());
        userDefIndicator.setIndicatorDefinition(indicatorDefinition);

        TdExpression newTdExp = BooleanExpressionHelper.createTdExpression("MySQL", //$NON-NLS-1$
                ViewValidRows, null);
        newTdExp.setModificationDate(DateUtils.getCurrentDate(DateUtils.PATTERN_5));
        indicatorDefinition.getViewValidRowsExpression().add(newTdExp);

        ChartDataEntity chartDataEntity = new ChartDataEntity(userDefIndicator, "2012-06-05", "1"); //$NON-NLS-1$  //$NON-NLS-2$
        chartDataEntity.setLabelNull(false);
        chartDataEntity.setKey("2012-06-05"); //$NON-NLS-1$
        assertFalse(chartDataEntity.isLabelNull());

        userDefIndicator.setAnalyzedElement(column);
        IndicatorParameters indicatorParameters = IndicatorsFactory.eINSTANCE.createIndicatorParameters();
        indicatorParameters.setDateParameters(null);
        userDefIndicator.setParameters(indicatorParameters);
        assertNull(indicatorParameters.getDateParameters());

        PowerMockito.mockStatic(DbmsLanguageFactory.class);
        when(DbmsLanguageFactory.createDbmsLanguage(dataManager)).thenReturn(dbmsLanguage);
        when(DbmsLanguageFactory.compareDbmsLanguage("MySQL", "MySQL")).thenReturn(true); //$NON-NLS-1$ //$NON-NLS-2$

        when(dbmsLanguage.toQualifiedName(null, null, "TDQ_CALENDAR")).thenReturn("TDQ_CALENDAR"); //$NON-NLS-1$ //$NON-NLS-2$
        when(dbmsLanguage.fillGenericQueryWithColumnsAndTable("SELECT * FROM <%=__TABLE_NAME__%> ", "CAL_DATE", "TDQ_CALENDAR")) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                .thenReturn("SELECT * FROM TDQ_CALENDAR"); //$NON-NLS-1$

        RegexPatternExplorer freqExp = new RegexPatternExplorer();
        Assert.assertTrue(freqExp.setAnalysis(analysis));
        freqExp.setEnitty(chartDataEntity);

        String clause = freqExp.getValidRowsStatement();

        assertEquals("SELECT * FROM TDQ_CALENDAR", clause); //$NON-NLS-1$
    }

    /**
     * 
     * Mock Test method for {@link org.talend.dq.analysis.explore.PatternExplorer#isImplementRegexFunction()}.test
     * return true;
     */
    @Test
    public void testIsImplementRegexFunction_True() {
        String label = DataExplorer.MENU_VIEW_VALID_ROWS;
        boolean implementRegexFunction = patternExplorer.isImplementRegexFunction(label);
        assertTrue(implementRegexFunction);
        label = DataExplorer.MENU_VIEW_VALID_VALUES;
        implementRegexFunction = patternExplorer.isImplementRegexFunction(label);
        assertTrue(implementRegexFunction);
        label = DataExplorer.MENU_VIEW_INVALID_VALUES;
        implementRegexFunction = patternExplorer.isImplementRegexFunction(label);
        assertTrue(implementRegexFunction);
        label = DataExplorer.MENU_VIEW_INVALID_ROWS;
        assertTrue(implementRegexFunction);
    }

    /**
     * 
     * Mock Test method for {@link org.talend.dq.analysis.explore.PatternExplorer#isImplementRegexFunction()}.test
     * return false;
     */
    @Test
    public void testIsImplementRegexFunction_False() {
        String label = null;
        when(patternExplorer.dbmsLanguage.regexLike(anyString(), anyString())).thenReturn(null);
        when(patternExplorer.dbmsLanguage.regexNotLike(anyString(), anyString())).thenReturn(null);

        boolean implementRegexFunction = this.patternExplorer.isImplementRegexFunction(label);
        assertFalse(implementRegexFunction);

        label = "ab";//$NON-NLS-1$
        implementRegexFunction = this.patternExplorer.isImplementRegexFunction(label);
        assertFalse(implementRegexFunction);

        label = DataExplorer.MENU_VIEW_VALID_ROWS;
        implementRegexFunction = patternExplorer.isImplementRegexFunction(label);
        assertFalse(implementRegexFunction);
        label = DataExplorer.MENU_VIEW_VALID_VALUES;
        implementRegexFunction = patternExplorer.isImplementRegexFunction(label);
        assertFalse(implementRegexFunction);
        label = DataExplorer.MENU_VIEW_INVALID_VALUES;
        implementRegexFunction = patternExplorer.isImplementRegexFunction(label);
        assertFalse(implementRegexFunction);
        label = DataExplorer.MENU_VIEW_INVALID_ROWS;
        implementRegexFunction = patternExplorer.isImplementRegexFunction(label);
        assertFalse(implementRegexFunction);

    }

}
