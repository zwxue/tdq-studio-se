// ============================================================================
//
// Copyright (C) 2006-2018 Talend Inc. - www.talend.com
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.support.membermodification.MemberMatcher.method;
import static org.powermock.api.support.membermodification.MemberModifier.stub;

import java.sql.Types;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
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
import org.talend.dataquality.indicators.LowFrequencyIndicator;
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

/**
 * test for class FrequencyStatisticsExplorer.
 */
@PrepareForTest({ DbmsLanguageFactory.class, Messages.class, IndicatorEnum.class })
public class FrequencyStatisticsExplorerTest {

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    FrequencyStatisticsExplorer freqExp;

    DbmsLanguage mockDbLanguage = DbmsLanguageFactory.createDbmsLanguage("MySQL", "5.0.2"); //$NON-NLS-1$ //$NON-NLS-2$

    /**
     * DOC msjian Comment method "setUp".
     * 
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        DataExplorerTestHelper.initDataExplorer();
        stub(method(DbmsLanguageFactory.class, "createDbmsLanguage", DataManager.class)).toReturn(mockDbLanguage); //$NON-NLS-1$
    }

    /**
     * DOC msjian Comment method "tearDown".
     * 
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test method for {@link org.talend.dq.analysis.explore.FrequencyStatisticsExplorer#getFreqRowsStatement()} case_1:
     * for the column javaType is Date
     */
    @Test
    public void testGetFreqRowsStatement_1() {

        // mock an analysis for the super class.
        Analysis analysis = mock(Analysis.class);
        AnalysisParameters parameters = mock(AnalysisParameters.class);
        when(parameters.getExecutionLanguage()).thenReturn(ExecutionLanguage.SQL);
        when(analysis.getParameters()).thenReturn(parameters);
        AnalysisContext context = mock(AnalysisContext.class);
        when(analysis.getContext()).thenReturn(context);
        DataManager dataManager = mock(DataManager.class);
        when(context.getConnection()).thenReturn(dataManager);

        // create database construction
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
        LowFrequencyIndicator indicator = IndicatorsFactory.eINSTANCE.createLowFrequencyIndicator();
        ChartDataEntity chartDataEntity = new ChartDataEntity(indicator, "2012-06-05", "1"); //$NON-NLS-1$  //$NON-NLS-2$
        chartDataEntity.setLabelNull(false);
        chartDataEntity.setKey("2012-06-05"); //$NON-NLS-1$
        assertFalse(chartDataEntity.isLabelNull());

        indicator.setAnalyzedElement(column);
        IndicatorParameters indicatorParameters = IndicatorsFactory.eINSTANCE.createIndicatorParameters();
        indicatorParameters.setDateParameters(null);
        indicator.setParameters(indicatorParameters);
        assertNull(indicatorParameters.getDateParameters());

        freqExp = new FrequencyStatisticsExplorer();
        freqExp.setAnalysis(analysis);
        freqExp.setEnitty(chartDataEntity);

        String clause = freqExp.getFreqRowsStatement();

        assertEquals("SELECT * FROM `TDQ_CALENDAR` WHERE  (`CAL_DATE` = '2012-06-05') ", clause); //$NON-NLS-1$
    }

    /**
     * Test method for {@link org.talend.dq.analysis.explore.FrequencyStatisticsExplorer#getFreqRowsStatement()} case_2:
     * for the column javaType is Text
     */
    @Test
    public void testGetFreqRowsStatement_2() {
        // TODO when the column javaType is Text
    }

    /**
     * Test method for {@link org.talend.dq.analysis.explore.FrequencyStatisticsExplorer#getFreqRowsStatement()} case_3:
     * for for the column javaType is Number
     */
    @Test
    public void testGetFreqRowsStatement_3() {
        // TODO when the column javaType is Number

    }

    /**
     * Test method for {@link org.talend.dq.analysis.explore.FrequencyStatisticsExplorer#getFreqRowsStatement()} case_3:
     * for UDIndicatorDefinition
     */
    @Test
    public void testGetFreqRowsStatement_4() {

        // mock an analysis for the super class.
        Analysis analysis = mock(Analysis.class);
        AnalysisParameters parameters = mock(AnalysisParameters.class);
        when(parameters.getExecutionLanguage()).thenReturn(ExecutionLanguage.SQL);
        when(analysis.getParameters()).thenReturn(parameters);
        AnalysisContext context = mock(AnalysisContext.class);
        when(analysis.getContext()).thenReturn(context);
        DataManager dataManager = mock(DataManager.class);
        when(context.getConnection()).thenReturn(dataManager);

        // create database construction
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
        indicatorDefinition.setName("user define count"); //$NON-NLS-1$
        userDefIndicator.setName(indicatorDefinition.getName());
        userDefIndicator.setIndicatorDefinition(indicatorDefinition);

        TdExpression newTdExp = BooleanExpressionHelper.createTdExpression("SQL", //$NON-NLS-1$
                "SELECT * FROM <%=__TABLE_NAME__%> <%=__WHERE_CLAUSE__%>", null); //$NON-NLS-1$
        newTdExp.setModificationDate(DateUtils.getCurrentDate(DateUtils.PATTERN_5));
        indicatorDefinition.getViewRowsExpression().add(newTdExp);

        ChartDataEntity chartDataEntity = new ChartDataEntity(userDefIndicator, "2012-06-05", "1"); //$NON-NLS-1$  //$NON-NLS-2$
        chartDataEntity.setLabelNull(false);
        chartDataEntity.setKey("2012-06-05"); //$NON-NLS-1$
        assertFalse(chartDataEntity.isLabelNull());

        userDefIndicator.setAnalyzedElement(column);
        IndicatorParameters indicatorParameters = IndicatorsFactory.eINSTANCE.createIndicatorParameters();
        indicatorParameters.setDateParameters(null);
        userDefIndicator.setParameters(indicatorParameters);
        assertNull(indicatorParameters.getDateParameters());

        freqExp = new FrequencyStatisticsExplorer();
        freqExp.setAnalysis(analysis);
        freqExp.setEnitty(chartDataEntity);

        String clause = freqExp.getFreqRowsStatement();

        assertEquals("SELECT * FROM `TDQ_CALENDAR` ", clause); //$NON-NLS-1$
    }
}
