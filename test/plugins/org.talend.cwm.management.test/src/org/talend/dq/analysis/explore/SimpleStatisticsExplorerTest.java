// ============================================================================
//
// Copyright (C) 2006-2017 Talend Inc. - www.talend.com
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

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import static org.powermock.api.support.membermodification.MemberMatcher.*;
import static org.powermock.api.support.membermodification.MemberModifier.*;

import java.sql.Types;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.talend.cwm.relational.TdColumn;
import org.talend.cwm.relational.TdExpression;
import org.talend.cwm.relational.TdSqlDataType;
import org.talend.cwm.relational.TdTable;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.analysis.AnalysisContext;
import org.talend.dataquality.analysis.AnalysisParameters;
import org.talend.dataquality.analysis.ExecutionLanguage;
import org.talend.dataquality.helpers.AnalysisHelper;
import org.talend.dataquality.helpers.BooleanExpressionHelper;
import org.talend.dataquality.indicators.definition.userdefine.UDIndicatorDefinition;
import org.talend.dataquality.indicators.definition.userdefine.UserdefineFactory;
import org.talend.dataquality.indicators.sql.IndicatorSqlFactory;
import org.talend.dataquality.indicators.sql.UserDefIndicator;
import org.talend.dq.dbms.DbmsLanguageFactory;
import org.talend.dq.dbms.MySQLDbmsLanguage;
import org.talend.dq.indicators.preview.table.ChartDataEntity;
import org.talend.utils.dates.DateUtils;
import orgomg.cwm.foundation.softwaredeployment.DataManager;

/**
 * created by msjian on 2013-3-2 Detailled comment
 * 
 */
@PrepareForTest({ DbmsLanguageFactory.class, org.talend.cwm.management.i18n.Messages.class, AnalysisHelper.class })
public class SimpleStatisticsExplorerTest {

    private static final String body = "SELECT * FROM <%=__TABLE_NAME__%> <%=__WHERE_CLAUSE__%>"; //$NON-NLS-1$

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    /**
     * DOC msjian Comment method "setUp".
     * 
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        DataExplorerTestHelper.initDataExplorer();
    }

    /**
     * Test method for {@link org.talend.dq.analysis.explore.SimpleStatisticsExplorer#getQueryMap()}.
     */
    @Test
    public void testGetQueryMap() {
        // mock an analysis for the super class.
        Analysis ana = mock(Analysis.class);
        AnalysisParameters parameters = mock(AnalysisParameters.class);
        when(parameters.getExecutionLanguage()).thenReturn(ExecutionLanguage.SQL);
        when(ana.getParameters()).thenReturn(parameters);
        AnalysisContext context = mock(AnalysisContext.class);
        when(ana.getContext()).thenReturn(context);
        DataManager dataManager = mock(DataManager.class);
        when(context.getConnection()).thenReturn(dataManager);

        MySQLDbmsLanguage dbmsLanguage = mock(MySQLDbmsLanguage.class);
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

        when(dbmsLanguage.where()).thenReturn(" WHERE "); //$NON-NLS-1$
        when(dbmsLanguage.and()).thenReturn(" AND "); //$NON-NLS-1$
        when(dbmsLanguage.quote(anyString())).thenReturn("CAL_DATE"); //$NON-NLS-1$
        stub(method(DbmsLanguageFactory.class, "createDbmsLanguage", DataManager.class, ExecutionLanguage.class)).toReturn(dbmsLanguage);//$NON-NLS-1$ 

        // create user define indicator
        UserDefIndicator userDefIndicator = IndicatorSqlFactory.eINSTANCE.createUserDefIndicator();
        UDIndicatorDefinition indicatorDefinition = UserdefineFactory.eINSTANCE.createUDIndicatorDefinition();
        indicatorDefinition.setName("user define"); //$NON-NLS-1$
        userDefIndicator.setName(indicatorDefinition.getName());
        userDefIndicator.setIndicatorDefinition(indicatorDefinition);

        TdExpression newTdExp = BooleanExpressionHelper.createTdExpression("MySQL", body, null);//$NON-NLS-1$
        newTdExp.setModificationDate(DateUtils.getCurrentDate(DateUtils.PATTERN_5));
        indicatorDefinition.getViewRowsExpression().add(newTdExp);

        ChartDataEntity chartDataEntity = new ChartDataEntity(userDefIndicator, "2012-06-05", "1"); //$NON-NLS-1$  //$NON-NLS-2$
        chartDataEntity.setLabelNull(false);
        chartDataEntity.setKey("2012-06-05"); //$NON-NLS-1$

        userDefIndicator.setAnalyzedElement(column);

        PowerMockito.mockStatic(DbmsLanguageFactory.class);
        when(DbmsLanguageFactory.createDbmsLanguage(dataManager)).thenReturn(dbmsLanguage);
        when(DbmsLanguageFactory.compareDbmsLanguage("MySQL", "MySQL")).thenReturn(true); //$NON-NLS-1$ //$NON-NLS-2$

        when(dbmsLanguage.toQualifiedName(null, null, "TDQ_CALENDAR")).thenReturn("TDQ_CALENDAR"); //$NON-NLS-1$ //$NON-NLS-2$

        SimpleStatisticsExplorer simpleStatisticsExplorer = new SimpleStatisticsExplorer();
        Assert.assertTrue(simpleStatisticsExplorer.setAnalysis(ana));
        simpleStatisticsExplorer.setEnitty(chartDataEntity);

        PowerMockito.mockStatic(AnalysisHelper.class);
        when(AnalysisHelper.getAnalysisType(ana)).thenReturn(null);
        when(AnalysisHelper.getPurpose(ana)).thenReturn("Purpose"); //$NON-NLS-1$
        when(AnalysisHelper.getDescription(ana)).thenReturn("Description"); //$NON-NLS-1$

        Map<String, String> queryMap = simpleStatisticsExplorer.getQueryMap();
        assertFalse(queryMap.isEmpty());
        assertEquals(1, queryMap.size());
        assertEquals("-- unit test  ;\n" + "-- unit test  ;\n" + "-- unit test Purpose ;\n" + "-- unit test Description ;\n" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                + "-- unit test CAL_DATE ;\n" + "-- unit test user define ;\n" + "-- unit test unit test ;\n" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                + "SELECT * FROM TDQ_CALENDAR ", queryMap.get("unit test")); //$NON-NLS-1$ //$NON-NLS-2$

        // test when is not sql engine
        when(parameters.getExecutionLanguage()).thenReturn(ExecutionLanguage.JAVA);
        Map<String, String> queryMap_java = simpleStatisticsExplorer.getQueryMap();
        assertFalse(queryMap_java.isEmpty());
        assertEquals(1, queryMap_java.size());
        assertEquals(null, queryMap_java.get("unit test")); //$NON-NLS-1$

    }

}
