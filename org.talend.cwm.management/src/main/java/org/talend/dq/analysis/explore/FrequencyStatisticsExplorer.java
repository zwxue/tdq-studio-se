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
package org.talend.dq.analysis.explore;

import java.util.HashMap;
import java.util.Map;

import org.talend.cwm.helper.ColumnHelper;
import org.talend.cwm.relational.TdColumn;
import org.talend.dataquality.indicators.DateGrain;
import org.talend.dataquality.indicators.IndicatorParameters;
import org.talend.utils.sql.Java2SqlType;

/**
 * DOC Administrator class global comment. Detailled comment
 */
public class FrequencyStatisticsExplorer extends DataExplorer {

    private String getFreqRowsStatement() {

        String clause = "";

        TdColumn column = (TdColumn) indicator.getAnalyzedElement();
        int javaType = column.getJavaType();

        if (Java2SqlType.isTextInSQL(javaType)) {
            clause = this.columnName + dbmsLanguage.equal() + "'" + entity.getLabel() + "'";
        } else if (Java2SqlType.isDateInSQL(javaType)) {
            IndicatorParameters parameters = indicator.getParameters();
            DateGrain dateGrain = parameters.getDateParameters().getDateAggregationType();

            switch (dateGrain) {
            case QUARTER:
                clause = dbmsLanguage.extractQuarter(this.columnName) + dbmsLanguage.equal()
                        + dbmsLanguage.extractQuarter(formatDate(entity.getLabel()));
                break;
            case DAY:
                clause = dbmsLanguage.extractDay(this.columnName) + dbmsLanguage.equal()
                        + dbmsLanguage.extractDay(formatDate(entity.getLabel()));
                break;
            case WEEK:
                clause = dbmsLanguage.extractWeek(this.columnName) + dbmsLanguage.equal()
                        + dbmsLanguage.extractWeek(formatDate(entity.getLabel()));
                break;
            case MONTH:
                clause = dbmsLanguage.extractMonth(this.columnName) + dbmsLanguage.equal()
                        + dbmsLanguage.extractMonth(formatDate(entity.getLabel()));
                break;
            case YEAR:
                clause = dbmsLanguage.extractYear(this.columnName) + dbmsLanguage.equal()
                        + dbmsLanguage.extractYear(formatDate(entity.getLabel()));
            default:
            }

        } else {
            clause = this.columnName + dbmsLanguage.equal() + entity.getLabel();
        }

        return "select * from " + ColumnHelper.getColumnSetFullName(column) + " where " + clause;
    }

    private String formatDate(String date) {
        int length = date.trim().length();

        for (; length < 8; length++) {
            date = date + "0";
        }

        return "'" + date + "'";
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.analysis.explore.IDataExplorer#getQueryMap()
     */
    public Map<String, String> getQueryMap() {
        Map<String, String> map = new HashMap<String, String>();

        map.put("View rows", getFreqRowsStatement());

        return map;
    }

}
