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

        switch (this.indicatorEnum) {
        case RowCountIndicatorEnum:
        case NullCountIndicatorEnum:
        case BlankCountIndicatorEnum:
            map.put(MENU_VIEW_ROWS, getRowsStatement());
            break;

        case UniqueIndicatorEnum:
            map.put(MENU_VIEW_ROWS, getRowsStatementWithSubQuery());
            map.put(MENU_VIEW_VALUES, getValuesStatement(this.columnName));
            break;
        case DistinctCountIndicatorEnum:
            map.put(MENU_VIEW_VALUES, getDistinctValuesStatement(this.columnName));
            break;

        case DuplicateCountIndicatorEnum:
            map.put(MENU_VIEW_ROWS, getRowsStatementWithSubQuery());
            map.put(MENU_VIEW_VALUES, getValuesStatement("col"));
            break;
        default:
        }

        return map;
    }


}
