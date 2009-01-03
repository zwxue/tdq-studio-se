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

import org.talend.cwm.relational.TdColumn;
import org.talend.dataquality.domain.Domain;
import org.talend.dataquality.helpers.DomainHelper;
import org.talend.dataquality.indicators.IndicatorParameters;

/**
 * DOC Administrator class global comment. Detailled comment
 */
public class SummaryStastictisExplorer extends DataExplorer {

    /**
     * Method "getMatchingRowsStatement".
     * 
     * @return the query to get the rows with a matching value
     */
    private String getMatchingRowsStatement() {
        double value = Double.valueOf(entity.getValue());
        String whereClause = dbmsLanguage.where() + this.columnName + dbmsLanguage.equal() + value;
        TdColumn column = (TdColumn) indicator.getAnalyzedElement();
        return "select * from " + getFullyQualifiedTableName(column) + whereClause;
    }

    /**
     * TODO zqin use this method in a menu ".
     * 
     * View invalid rows" Method "getInvalidRowsStatement".
     * 
     * @return the query to get the invalid rows (or null when all rows are valid)
     */
    private String getInvalidRowsStatement() {
        double value = Double.valueOf(entity.getValue());
        String whereClause = null;

        TdColumn column = (TdColumn) indicator.getAnalyzedElement();
        IndicatorParameters parameters = indicator.getParameters();
        if (parameters != null) {
            String where1 = null;
            Domain domain = parameters.getIndicatorValidDomain();
            if (domain != null) {
                where1 = getWhereInvalidClause(value, domain);
            }
            String where2 = null;
            domain = parameters.getDataValidDomain();
            if (domain != null) {
                where2 = getWhereInvalidClause(value, domain);
            }

            if (where1 != null) {
                whereClause = where1;
                if (where2 != null) {
                    whereClause += dbmsLanguage.or() + where2;
                }
            } else if (where2 != null) {
                whereClause = where2;
            }
        }

        // add the data filter where clause
        return whereClause != null ? "SELECT * FROM " + getFullyQualifiedTableName(column) + dbmsLanguage.where()
                + inBrackets(whereClause) + andDataFilterClause() : null;
    }

    /**
     * DOC scorreia Comment method "getWhereInvalidClause".
     * 
     * @param value
     * @param whereClause
     * @param domain
     * @return
     */
    private String getWhereInvalidClause(double value, Domain domain) {
        double max = Double.valueOf(DomainHelper.getMaxValue(domain.getRanges().get(0)));
        double min = Double.valueOf(DomainHelper.getMinValue(domain.getRanges().get(0)));
        String whereClause = (value < min || value > max) ? columnName + dbmsLanguage.less() + min + dbmsLanguage.or()
                + columnName + dbmsLanguage.greater() + max : null;
        return whereClause;
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
            if (entity.isOutOfRange(entity.getValue())) {
                map.put(MENU_VIEW_INVALID_ROWS, getInvalidRowsStatement());
            }
            map.put(MENU_VIEW_ROWS, getMatchingRowsStatement());
        }

        return map;
    }

}
