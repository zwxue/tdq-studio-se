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
import org.talend.dataquality.domain.Domain;
import org.talend.dataquality.helpers.DomainHelper;
import org.talend.dataquality.indicators.IndicatorParameters;

/**
 * DOC Administrator class global comment. Detailled comment
 */
public class SummaryStastictisExplorer extends DataExplorer {

    private String getSummaryRowsStatement() {
        double value = Double.valueOf(entity.getValue());
        String clause = dbmsLanguage.where() + this.columnName + dbmsLanguage.equal() + value;

        TdColumn column = (TdColumn) indicator.getAnalyzedElement();
        IndicatorParameters parameters = indicator.getParameters();
        if (parameters != null) {
            Domain domain = parameters.getIndicatorValidDomain();
            double max = Double.valueOf(DomainHelper.getMaxValue(domain.getRanges().get(0)));
            double min = Double.valueOf(DomainHelper.getMinValue(domain.getRanges().get(0)));
            if (value < min || value > max) {
                clause = dbmsLanguage.where() + this.columnName + "<" + min + dbmsLanguage.and() + this.columnName + ">" + max;
            }
        }

        return "select * from " + ColumnHelper.getColumnSetFullName(column) + clause;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.analysis.explore.IDataExplorer#getQueryMap()
     */
    public Map<String, String> getQueryMap() {
        Map<String, String> map = new HashMap<String, String>();

        switch (indicatorEnum) {
        case MeanIndicatorEnum:
            break;
        default:
            map.put("View rows", getSummaryRowsStatement());
        }

        return map;
    }

}
