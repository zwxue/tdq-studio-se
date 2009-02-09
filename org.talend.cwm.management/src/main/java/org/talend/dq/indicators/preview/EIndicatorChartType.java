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
import org.talend.dq.analysis.explore.SummaryStastictisExplorer;
import org.talend.dq.analysis.explore.TextStatisticsExplorer;

/**
 * DOC zqin class global comment. Detailled comment
 */
public enum EIndicatorChartType {

    SIMPLE_STATISTICS("Simple Statistics", new SimpleStatisticsExplorer()),
    TEXT_STATISTICS("Text Statistics", new TextStatisticsExplorer()),
    FREQUENCE_STATISTICS("Frequency Statistics", new FrequencyStatisticsExplorer()),
    LOW_FREQUENCE_STATISTICS("Low Frequency Statistics", new FrequencyStatisticsExplorer()),
    PATTERN_FREQUENCE_STATISTICS("Pattern Frequency Statistics", new FunctionFrequencyStatExplorer()),
    PATTERN_LOW_FREQUENCE_STATISTICS("Pattern Low Frequency Statistics", new FunctionFrequencyStatExplorer()),
    SUMMARY_STATISTICS("Summary Statistics", new SummaryStastictisExplorer()),
    PATTERN_MATCHING("Pattern Matching", new PatternExplorer()),
    SQL_PATTERN_MATCHING("SQL Pattern Matching", new PatternExplorer()),
    MODE_INDICATOR("Mode Indicator", null);

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
