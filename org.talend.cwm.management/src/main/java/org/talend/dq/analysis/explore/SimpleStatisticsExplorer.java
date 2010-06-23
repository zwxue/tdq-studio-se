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
package org.talend.dq.analysis.explore;

import java.util.HashMap;
import java.util.Map;

import org.talend.dataquality.analysis.ExecutionLanguage;

/**
 * DOC zqin class global comment. Detailled comment
 */
public class SimpleStatisticsExplorer extends DataExplorer {

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.analysis.explore.IDataExplorer#getQueryMap()
     */
    public Map<String, String> getQueryMap() {
        Map<String, String> map = new HashMap<String, String>();
        // MOD zshen feature 12919 adapt to pop-menu for Jave engin on result page
        boolean isSqlEngine = ExecutionLanguage.SQL.equals(this.analysis.getParameters().getExecutionLanguage());
        if (!isXml() || !isSqlEngine) {

            switch (this.indicatorEnum) {
            case RowCountIndicatorEnum:
            case NullCountIndicatorEnum:
            case BlankCountIndicatorEnum:
            case DefValueCountIndicatorEnum:
                map.put(MENU_VIEW_ROWS, isSqlEngine ? getRowsStatement() : null);
                break;

            case UniqueIndicatorEnum:
                map.put(MENU_VIEW_ROWS, isSqlEngine ? getRowsStatementWithSubQuery() : null);
                map.put(MENU_VIEW_VALUES, isSqlEngine ? getValuesStatement(this.columnName) : null);
                break;
            case DistinctCountIndicatorEnum:
                map.put(MENU_VIEW_VALUES, isSqlEngine ? getDistinctValuesStatement(this.columnName) : null);
                break;

            case DuplicateCountIndicatorEnum:
                map.put(MENU_VIEW_ROWS, isSqlEngine ? getRowsStatementWithSubQuery() : null);
                map.put(MENU_VIEW_VALUES, isSqlEngine ? getValuesStatement(this.columnName) : null);
                break;
            default:
            }
        }

        return map;
    }

}
