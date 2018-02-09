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

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.powermock.api.support.membermodification.MemberMatcher.*;
import static org.powermock.api.support.membermodification.MemberModifier.*;

import java.util.Map;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.analysis.AnalysisContext;
import org.talend.dataquality.analysis.AnalysisParameters;
import org.talend.dataquality.analysis.ExecutionLanguage;
import org.talend.dataquality.indicators.AverageLengthIndicator;
import org.talend.dataquality.indicators.IndicatorsFactory;
import org.talend.dq.dbms.DbmsLanguageFactory;
import org.talend.dq.dbms.HiveDbmsLanguage;
import org.talend.dq.indicators.preview.table.ChartDataEntity;
import orgomg.cwm.foundation.softwaredeployment.DataManager;

/**
 * created by qiongli on Sep 5, 2012 Detailled comment
 * 
 */
// @RunWith(PowerMockRunner.class)
@PrepareForTest({ DbmsLanguageFactory.class, org.talend.cwm.management.i18n.Messages.class })
public class TextStatisticsExplorerTest {

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    /**
     * DOC qiongli Comment method "setUp".
     * 
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        DataExplorerTestHelper.initDataExplorer();
    }

    /**
     * Test method for Text indicators with Hive connection,should don't have any menu items.
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
        HiveDbmsLanguage hiveDBMS = mock(HiveDbmsLanguage.class);

        stub(method(DbmsLanguageFactory.class, "createDbmsLanguage", DataManager.class, ExecutionLanguage.class)).toReturn(hiveDBMS);//$NON-NLS-1$ 

        AverageLengthIndicator averageLengthIndicator = IndicatorsFactory.eINSTANCE.createAverageLengthIndicator();
        ChartDataEntity chartDataEntity = new ChartDataEntity(averageLengthIndicator, "", ""); //$NON-NLS-1$  //$NON-NLS-2$

        TextStatisticsExplorer textStatisticsExplorer = new TextStatisticsExplorer();
        textStatisticsExplorer.setEnitty(chartDataEntity);
        textStatisticsExplorer.setAnalysis(ana);
        Map<String, String> queryMap = textStatisticsExplorer.getQueryMap();
        assertTrue(queryMap.isEmpty());

    }

}
