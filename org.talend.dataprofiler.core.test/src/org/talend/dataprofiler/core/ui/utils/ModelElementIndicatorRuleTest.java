// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.utils;

import static org.mockito.Mockito.*;

import java.sql.Types;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.talend.cwm.relational.TdColumn;
import org.talend.cwm.relational.TdSqlDataType;
import org.talend.dataquality.analysis.ExecutionLanguage;
import org.talend.dq.nodes.indicator.type.IndicatorEnum;



/**
 * DOC yyin  class global comment. Detailled comment
 */
public class ModelElementIndicatorRuleTest {

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    /**
     * DOC yyin Comment method "setUp".
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }

    /**
     * DOC yyin Comment method "tearDown".
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test method for {@link org.talend.dataprofiler.core.ui.utils.ModelElementIndicatorRule#match(org.talend.dq.nodes.indicator.IIndicatorNode, org.talend.dataprofiler.core.model.ModelElementIndicator, org.talend.dataquality.analysis.ExecutionLanguage)}.
     */
    @Test
    public void testMatch() {
        // fail("Not yet implemented");
    }

    /**
     * TDQ-5241 Test method for
     * {@link org.talend.dataprofiler.core.ui.utils.ModelElementIndicatorRule#patternRule(org.talend.dq.nodes.indicator.type.IndicatorEnum, orgomg.cwm.objectmodel.core.ModelElement, org.talend.dataquality.analysis.ExecutionLanguage)}
     * .
     */
    @Test
    public void testPatternRule() {
        // modified: when the column is TIME type, any indicator using year(),month(),week() ,quarter(),day() should be
        // disabled
        TdSqlDataType tdsql = mock(TdSqlDataType.class);
        TdColumn me = mock(TdColumn.class);
        when(me.getSqlDataType()).thenReturn(tdsql);
        when(tdsql.getJavaDataType()).thenReturn(Types.TIME);
        when(me.getContentType()).thenReturn("type");
        // ExecutionLanguage el = mock(ExecutionLanguage.class);
        // can not mock final class
        // PowerMockito.mockStatic(MetadataHelper.class);
        // when(MetadataHelper.getDataminingType(me)).thenReturn(DataminingType.OTHER);
        
        Assert.assertTrue(ModelElementIndicatorRule.patternRule(IndicatorEnum.ModeIndicatorEnum, me, ExecutionLanguage.JAVA));
        Assert.assertFalse(ModelElementIndicatorRule
                .patternRule(IndicatorEnum.FrequencyIndicatorEnum, me, ExecutionLanguage.JAVA));
        // Assert.assertFalse(ModelElementIndicatorRule.patternRule(IndicatorEnum.LowFrequencyIndicatorEnum, me,
        // ExecutionLanguage.JAVA));
        Assert.assertFalse(ModelElementIndicatorRule.patternRule(IndicatorEnum.DateFrequencyIndicatorEnum, me,
                ExecutionLanguage.JAVA));
        Assert.assertFalse(ModelElementIndicatorRule.patternRule(IndicatorEnum.WeekFrequencyIndicatorEnum, me,
                ExecutionLanguage.JAVA));
        Assert.assertFalse(ModelElementIndicatorRule.patternRule(IndicatorEnum.MonthFrequencyIndicatorEnum, me,
                ExecutionLanguage.JAVA));
        Assert.assertFalse(ModelElementIndicatorRule.patternRule(IndicatorEnum.QuarterFrequencyIndicatorEnum, me,
                ExecutionLanguage.JAVA));
        Assert.assertFalse(ModelElementIndicatorRule.patternRule(IndicatorEnum.YearFrequencyIndicatorEnum, me,
                ExecutionLanguage.JAVA));
        Assert.assertFalse(ModelElementIndicatorRule.patternRule(IndicatorEnum.DateLowFrequencyIndicatorEnum, me,
                ExecutionLanguage.JAVA));
        Assert.assertFalse(ModelElementIndicatorRule.patternRule(IndicatorEnum.WeekLowFrequencyIndicatorEnum, me,
                ExecutionLanguage.JAVA));
        Assert.assertFalse(ModelElementIndicatorRule.patternRule(IndicatorEnum.MonthLowFrequencyIndicatorEnum, me,
                ExecutionLanguage.JAVA));
        Assert.assertFalse(ModelElementIndicatorRule.patternRule(IndicatorEnum.QuarterLowFrequencyIndicatorEnum, me,
                ExecutionLanguage.JAVA));
        Assert.assertFalse(ModelElementIndicatorRule.patternRule(IndicatorEnum.YearLowFrequencyIndicatorEnum, me,
                ExecutionLanguage.JAVA));

    }

}
