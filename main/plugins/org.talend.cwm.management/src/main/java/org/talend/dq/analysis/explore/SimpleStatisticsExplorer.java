// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
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

import org.talend.dataquality.analysis.AnalysisType;
import org.talend.dataquality.analysis.ExecutionLanguage;
import org.talend.dq.dbms.HiveDbmsLanguage;

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
        // MOD qiongli 2011-3-4,feature 19192:filter menue 'view rows' for columSet AnalysisType.
        AnalysisType analysisType = this.analysis.getParameters().getAnalysisType();
        // MOD qiongli 2012-8-29 hive don't support 'where in...'
        boolean isHive = dbmsLanguage instanceof HiveDbmsLanguage;
        if (!isXml() || !isSqlEngine) {

            switch (this.indicatorEnum) {
            case RowCountIndicatorEnum:
            case NullCountIndicatorEnum:
            case BlankCountIndicatorEnum:
            case DefValueCountIndicatorEnum:
                map.put(MENU_VIEW_ROWS, isSqlEngine ? getComment(MENU_VIEW_ROWS) + getRowsStatement() : null);
                break;

            case UniqueIndicatorEnum:
                if (analysisType != AnalysisType.COLUMN_SET) {
                    if (!isHive) {
                        map.put(MENU_VIEW_ROWS, isSqlEngine ? getComment(MENU_VIEW_ROWS) + getRowsStatementWithSubQuery() : null);
                    } else if (!isSqlEngine) {
                        map.put(MENU_VIEW_ROWS, null);
                    }
                }
                map.put(MENU_VIEW_VALUES, isSqlEngine ? getComment(MENU_VIEW_VALUES) + getValuesStatement(this.columnName) : null);
                break;
            case DistinctCountIndicatorEnum:
                map.put(MENU_VIEW_VALUES, isSqlEngine ? getComment(MENU_VIEW_VALUES)
                        + getDistinctValuesStatement(this.columnName) : null);
                break;

            case DuplicateCountIndicatorEnum:
                if (analysisType != AnalysisType.COLUMN_SET) {
                    if (!isHive) {
                        map.put(MENU_VIEW_ROWS, isSqlEngine ? getComment(MENU_VIEW_ROWS) + getRowsStatementWithSubQuery() : null);
                    } else if (!isSqlEngine) {
                        map.put(MENU_VIEW_ROWS, null);
                    }
                }
                map.put(MENU_VIEW_VALUES, isSqlEngine ? getComment(MENU_VIEW_VALUES) + getValuesStatement(this.columnName) : null);
                break;
            default:
            }
        }

        return map;
    }

}
