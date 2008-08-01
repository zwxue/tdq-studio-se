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
package org.talend.dataprofiler.core.ui.editor.preview.model;

/**
 * DOC zqin class global comment. Detailled comment
 */
public class PatternChartDataEntity extends ChartDataEntity {

    private String numMatch;

    private String numNoMatch;

    public String getNumMatch() {
        return numMatch;
    }

    public void setNumMatch(String numMatch) {
        this.numMatch = numMatch;
    }

    public String getNumNoMatch() {
        return numNoMatch;
    }

    public void setNumNoMatch(String numNoMatch) {
        this.numNoMatch = numNoMatch;
    }

    public String getPerMatch() {
        Double match = Double.parseDouble(getNumMatch());
        return match * 100 / getSum() + "%";
    }

    public String getPerNoMatch() {
        Double nomatch = Double.parseDouble(getNumNoMatch());
        return nomatch * 100 / getSum() + "%";
    }

    private Double getSum() {
        return getIndicator().getCount().doubleValue();
    }
}
