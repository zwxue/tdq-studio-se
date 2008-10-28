// ============================================================================
//
// Copyright (C) 2006-2007 Talend Inc. - www.talend.com
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

/**
 * DOC zqin class global comment. Detailled comment
 */
public enum EIndicatorChartType {

    SIMPLE_STATISTICS("Simple Statistics"),
    TEXT_STATISTICS("Text Statistics"),
    FREQUENCE_STATISTICS("Frequency Statistics"),
    LOW_FREQUENCE_STATISTICS("Low Frequency Statistics"),
    SUMMARY_STATISTICS("Summary Statistics"),
    PATTERN_MATCHING("Pattern Matching"),
    SQL_PATTERN_MATCHING("SQL Pattern Matching"),
    MODE_INDICATOR("Mode Indicator");

    private String literal;

    public String getLiteral() {
        return literal;
    }

    EIndicatorChartType(String literal) {
        this.literal = literal;
    }
}
