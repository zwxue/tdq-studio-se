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
package org.talend.dq.indicators.preview.table;

import org.talend.utils.format.StringFormatUtil;

/**
 * DOC xqliu class global comment. Detailled comment
 */
public class WhereRuleChartDataEntity extends PatternChartDataEntity {

    private double rowCount;

    public double getRowCount() {
        return rowCount;
    }

    public void setRowCount(double rowCount) {
        this.rowCount = rowCount;
    }

    @Override
    public String getPerMatch() {
        Double match = Double.parseDouble(getNumMatch());
        return StringFormatUtil.format(match / getRowCount(), StringFormatUtil.PERCENT).toString();
    }

    @Override
    public String getPerNoMatch() {
        Double nomatch = Double.parseDouble(getNumNoMatch());
        return StringFormatUtil.format(nomatch / getRowCount(), StringFormatUtil.PERCENT).toString();
    }

}
