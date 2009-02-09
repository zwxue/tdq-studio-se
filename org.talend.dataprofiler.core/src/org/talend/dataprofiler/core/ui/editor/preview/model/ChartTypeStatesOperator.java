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
package org.talend.dataprofiler.core.ui.editor.preview.model;

import java.util.List;

import org.talend.dataprofiler.core.ui.editor.preview.IndicatorUnit;
import org.talend.dataprofiler.core.ui.editor.preview.model.states.IChartTypeStates;
import org.talend.dataprofiler.core.ui.editor.preview.model.states.ModeStatisticsState;
import org.talend.dataprofiler.core.ui.editor.preview.model.states.SimpleStatisticsState;
import org.talend.dataprofiler.core.ui.editor.preview.model.states.SummaryStatisticsState;
import org.talend.dataprofiler.core.ui.editor.preview.model.states.TextStatisticsState;
import org.talend.dataprofiler.core.ui.editor.preview.model.states.freq.FrequencyStatisticsState;
import org.talend.dataprofiler.core.ui.editor.preview.model.states.freq.LowFrequencyStatisticsState;
import org.talend.dataprofiler.core.ui.editor.preview.model.states.freq.PatternFrequencyStatisticsState;
import org.talend.dataprofiler.core.ui.editor.preview.model.states.freq.PatternLowFrequencyStatisticsState;
import org.talend.dataprofiler.core.ui.editor.preview.model.states.pattern.REGEXPatternStatisticsState;
import org.talend.dataprofiler.core.ui.editor.preview.model.states.pattern.SQLPatternStatisticsState;
import org.talend.dq.indicators.preview.EIndicatorChartType;

/**
 * DOC Zqin class global comment. Detailled comment
 */
public final class ChartTypeStatesOperator {

    private ChartTypeStatesOperator() {

    }

    public static IChartTypeStates getChartState(EIndicatorChartType type, List<IndicatorUnit> units) {
        switch (type) {
        case SIMPLE_STATISTICS:
            return new SimpleStatisticsState(units);

        case TEXT_STATISTICS:
            return new TextStatisticsState(units);

        case MODE_INDICATOR:
            return new ModeStatisticsState(units);

        case FREQUENCE_STATISTICS:
            return new FrequencyStatisticsState(units);

        case LOW_FREQUENCE_STATISTICS:
            return new LowFrequencyStatisticsState(units);

        case PATTERN_FREQUENCE_STATISTICS:
            return new PatternFrequencyStatisticsState(units);

        case PATTERN_LOW_FREQUENCE_STATISTICS:
            return new PatternLowFrequencyStatisticsState(units);

        case PATTERN_MATCHING:
            return new REGEXPatternStatisticsState(units);

        case SQL_PATTERN_MATCHING:
            return new SQLPatternStatisticsState(units);

        case SUMMARY_STATISTICS:
            return new SummaryStatisticsState(units);

        default:
            return null;
        }
    }
}
