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
package org.talend.dq.indicators.preview;

import org.talend.dq.analysis.explore.FrequencyStatisticsExplorer;
import org.talend.dq.analysis.explore.FunctionFrequencyStatExplorer;
import org.talend.dq.analysis.explore.IDataExplorer;
import org.talend.dq.analysis.explore.PatternExplorer;
import org.talend.dq.analysis.explore.SimpleStatisticsExplorer;
import org.talend.dq.analysis.explore.SoundexFrequencyExplorer;
import org.talend.dq.analysis.explore.SummaryStastictisExplorer;
import org.talend.dq.analysis.explore.TextStatisticsExplorer;
import org.talend.i18n.Messages;

/**
 * DOC zqin class global comment. Detailled comment
 */
public enum EIndicatorChartType {

    SIMPLE_STATISTICS(Messages.getString("EIndicatorChartType.SimpleStatistics"), new SimpleStatisticsExplorer()), //$NON-NLS-1$
    TEXT_STATISTICS(Messages.getString("EIndicatorChartType.TextStatistics"), new TextStatisticsExplorer()), //$NON-NLS-1$
    FREQUENCE_STATISTICS(Messages.getString("EIndicatorChartType.FrequencyStatistics"), new FrequencyStatisticsExplorer()), //$NON-NLS-1$
    LOW_FREQUENCE_STATISTICS(Messages.getString("EIndicatorChartType.LowFrequencyStatistics"), new FrequencyStatisticsExplorer()), //$NON-NLS-1$
    PATTERN_FREQUENCE_STATISTICS(
                                 Messages.getString("EIndicatorChartType.PatternFrequencyStatistics"), new FunctionFrequencyStatExplorer()), //$NON-NLS-1$
    PATTERN_LOW_FREQUENCE_STATISTICS(
                                     Messages.getString("EIndicatorChartType.PatternLowFrequencyStatistics"), new FunctionFrequencyStatExplorer()), //$NON-NLS-1$
    SUMMARY_STATISTICS(Messages.getString("EIndicatorChartType.SummaryStatistics"), new SummaryStastictisExplorer()), //$NON-NLS-1$
    PATTERN_MATCHING(Messages.getString("EIndicatorChartType.PatternMatching"), new PatternExplorer()), //$NON-NLS-1$
    SQL_PATTERN_MATCHING(Messages.getString("EIndicatorChartType.SQLPatternMatching"), new PatternExplorer()), //$NON-NLS-1$
    MODE_INDICATOR(Messages.getString("EIndicatorChartType.ModeIndicator"), null), //$NON-NLS-1$
    WHERERULE_INDICATOR(Messages.getString("EIndicatorChartType.WhereRuleIndicator"), null), //$NON-NLS-1$
    // MOD mzhao 2009-03-23,Soundex frequency.
    SOUNDEX_FREQUENCY_TABLE(Messages.getString("EIndicatorChartType.SoundexFrequencyTable"), new SoundexFrequencyExplorer()), //$NON-NLS-1$
    SOUNDEX_LOW_FREQUENCY_TABLE(
                                Messages.getString("EIndicatorChartType.SoundexLowFrequencyTable"), new SoundexFrequencyExplorer());//$NON-NLS-1$

    private String literal;

    private IDataExplorer explorer;

    public IDataExplorer getExplorer() {
        return explorer;
    }

    public String getLiteral() {
        return literal;
    }

    EIndicatorChartType(String literal, IDataExplorer explorer) {
        this.literal = literal;
        this.explorer = explorer;
    }
}
