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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.support.membermodification.MemberMatcher.method;
import static org.powermock.api.support.membermodification.MemberModifier.stub;
import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.talend.cwm.management.i18n.Messages;
import org.talend.cwm.relational.TdColumn;
import org.talend.cwm.relational.TdSqlDataType;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.SumIndicator;
import org.talend.dq.dbms.DbmsLanguage;
import org.talend.dq.dbms.DbmsLanguageFactory;
import org.talend.dq.indicators.preview.table.ChartDataEntity;
import org.talend.dq.nodes.indicator.type.IndicatorEnum;
import orgomg.cwm.foundation.softwaredeployment.DataManager;

/**
 * DOC yyin class global comment. Detailled comment
 */
@PrepareForTest({ DbmsLanguageFactory.class, Messages.class, IndicatorEnum.class })
public class SummaryStastictisExplorerTest {

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    DbmsLanguage mockDbLanguage;

    private SummaryStastictisExplorer sumExp;

    TdColumn column;

    /**
     * DOC yyin Comment method "setUp".
     * 
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        DataExplorerTestHelper.initDataExplorer();
        sumExp = new SummaryStastictisExplorer();

        mockDbLanguage = mock(DbmsLanguage.class);
        when(mockDbLanguage.like()).thenReturn(" like ");
        when(mockDbLanguage.getDbmsName()).thenReturn("Teradata");
        when(mockDbLanguage.quote("INTERVAL_MONTH")).thenReturn("\"INTERVAL_MONTH\"");
        stub(method(DbmsLanguageFactory.class, "createDbmsLanguage", DataManager.class)).toReturn(mockDbLanguage);
        SumIndicator indicator = mock(SumIndicator.class);
        when(indicator.eClass()).thenReturn(null);
        when(indicator.getAnalyzedElement()).thenReturn(column);
        Analysis analysis = DataExplorerTestHelper.getAnalysis(indicator, mockDbLanguage);
        sumExp.setAnalysis(analysis);

        ChartDataEntity entity = mock(ChartDataEntity.class);
        when(entity.getIndicator()).thenReturn(indicator);
        PowerMockito.mockStatic(IndicatorEnum.class);
        when(IndicatorEnum.findIndicatorEnum(indicator.eClass())).thenReturn(IndicatorEnum.MedianIndicatorEnum);

        column = mock(TdColumn.class);
        when(indicator.getAnalyzedElement()).thenReturn(column);
        TdSqlDataType tdsql = mock(TdSqlDataType.class);
        when(column.getSqlDataType()).thenReturn(tdsql);
        when(tdsql.getName()).thenReturn("INTERVAL MONTH"); //$NON-NLS-1$
        when(column.getName()).thenReturn("INTERVAL_MONTH");

        sumExp.setEnitty(entity);
        when(entity.getKey()).thenReturn("1");
        when(entity.isLabelNull()).thenReturn(false);
    }

    /**
     * DOC yyin Comment method "tearDown".
     * 
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test method for
     * {@link org.talend.dq.analysis.explore.SummaryStastictisExplorer#getAnalyzedElementName(org.talend.dataquality.indicators.Indicator)}
     * . test for teradata interval_xx type
     */
    @Test
    public void testGetAnalyzedElementName() {

        Indicator ind = mock(Indicator.class);
        when(ind.getAnalyzedElement()).thenReturn(column);
        when(column.getName()).thenReturn("INTERVAL_MONTH");
        String intervalColumn = this.sumExp.getAnalyzedElementName(ind);
        Assert.assertEquals("cast(\"INTERVAL_MONTH\" AS REAL)", intervalColumn);
    }

    /**
     * Test method for
     * {@link org.talend.dq.analysis.explore.SummaryStastictisExplorer#getAnalyzedElementName(org.talend.dataquality.indicators.Indicator)}
     * . test for normal type, not teradata interval_xx type
     */
    @Test
    public void testGetAnalyzedElementName_2() {
        TdSqlDataType tdsql = mock(TdSqlDataType.class);
        when(column.getSqlDataType()).thenReturn(tdsql);
        when(tdsql.getName()).thenReturn("VARCHAR"); //$NON-NLS-1$

        Indicator ind = mock(Indicator.class);
        when(ind.getAnalyzedElement()).thenReturn(column);

        String intervalColumn = this.sumExp.getAnalyzedElementName(ind);
        Assert.assertEquals("\"INTERVAL_MONTH\"", intervalColumn);
    }

    /**
     * Test method for {@link org.talend.dq.analysis.explore.SummaryStastictisExplorer#getQueryMap()}.
     */
    @Test
    public void testGetQueryMap() {
        // TODO
    }

}
