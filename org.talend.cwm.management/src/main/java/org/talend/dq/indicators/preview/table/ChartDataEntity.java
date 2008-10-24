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
package org.talend.dq.indicators.preview.table;

import org.talend.dataquality.indicators.Indicator;
import org.talend.utils.format.StringFormatUtil;

/**
 * DOC zqin class global comment. Detailled comment
 */
public class ChartDataEntity {

    private String label;

    private String value;

    private Indicator indicator;

    private String percent;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getValue() {
        return StringFormatUtil.format(value, StringFormatUtil.NUMBER).toString();
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getPersent() {
        return StringFormatUtil.format(percent, StringFormatUtil.PERCENT).toString();
    }

    public void setPercent(String percent) {
        this.percent = percent;
    }

    public Indicator getIndicator() {
        return indicator;
    }

    public void setIndicator(Indicator indicator) {
        this.indicator = indicator;
    }
}
