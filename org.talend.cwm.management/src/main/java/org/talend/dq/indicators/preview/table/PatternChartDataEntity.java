// ============================================================================
//
// Copyright (C) 2006-2010 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dq.indicators.preview.table;

import org.talend.utils.format.StringFormatUtil;

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
        return StringFormatUtil.format(match / getSum(), StringFormatUtil.PERCENT).toString();
    }

    public String getPerNoMatch() {
        Double nomatch = Double.parseDouble(getNumNoMatch());
        return StringFormatUtil.format(nomatch / getSum(), StringFormatUtil.PERCENT).toString();
    }

    private Double getSum() {
        return getIndicator().getCount().doubleValue();
    }

    @Override
    public String getRangeAsString() {
        StringBuilder msg = new StringBuilder();
        if (isOutOfRange(getNumMatch())) {
            msg.append("This value is outside the expected indicator's thresholds: " + range); //$NON-NLS-1$
            msg.append("\n"); //$NON-NLS-1$
        }
        if (isOutOfRange(getPerMatch())) {
            msg.append("This value is outside the expected indicator's thresholds in percent: " + range); //$NON-NLS-1$
        }
        return msg.length() == 0 ? null : msg.toString();
    }
}
