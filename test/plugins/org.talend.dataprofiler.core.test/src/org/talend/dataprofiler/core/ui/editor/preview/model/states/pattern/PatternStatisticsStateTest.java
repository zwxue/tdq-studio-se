// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.editor.preview.model.states.pattern;

import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.talend.dataprofiler.common.ui.editor.preview.ICustomerDataset;
import org.talend.dataprofiler.core.ui.editor.preview.IndicatorUnit;
import org.talend.dataquality.indicators.IndicatorsFactory;
import org.talend.dataquality.indicators.RegexpMatchingIndicator;
import org.talend.dq.indicators.ext.PatternMatchingExt;
import org.talend.dq.indicators.preview.table.ChartDataEntity;

/**
 * DOC msjian class global comment. Detailled comment
 */
public class PatternStatisticsStateTest {

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    private PatternStatisticsState patternStatisticsState;

    private PatternMatchingExt patternMatchingExt;

    /**
     * init the state.
     * 
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        List<IndicatorUnit> units = new ArrayList<IndicatorUnit>();

        IndicatorUnit unit = mock(IndicatorUnit.class);
        units.add(unit);

        patternMatchingExt = new PatternMatchingExt();
        patternMatchingExt.setMatchingValueCount(1);
        patternMatchingExt.setNotMatchingValueCount(9);
        when(unit.getValue()).thenReturn(patternMatchingExt);

        RegexpMatchingIndicator indicator = IndicatorsFactory.eINSTANCE.createRegexpMatchingIndicator();
        indicator.setName("Blank text"); //$NON-NLS-1$
        when(unit.getIndicator()).thenReturn(indicator);

        when(unit.isExcuted()).thenReturn(true);

        patternStatisticsState = new PatternStatisticsState(units);
    }

    /**
     * Test method for
     * {@link org.talend.dataprofiler.core.ui.editor.preview.model.states.pattern.PatternStatisticsState#getCustomerDataset()}
     * 
     */
    @Test
    public void testGetCustomerDataset() {
        ICustomerDataset customerDataset = patternStatisticsState.getCustomerDataset();
        ChartDataEntity[] dataEntities = customerDataset.getDataEntities();
        Assert.assertEquals(1, dataEntities.length);
        Assert.assertEquals("Blank text", dataEntities[0].getLabel()); //$NON-NLS-1$
    }
}
