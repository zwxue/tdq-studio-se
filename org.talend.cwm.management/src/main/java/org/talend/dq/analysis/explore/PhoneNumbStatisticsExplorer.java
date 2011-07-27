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
 * DOC qiongli class global comment. Detailled comment
 */
public class PhoneNumbStatisticsExplorer extends DataExplorer {

    public PhoneNumbStatisticsExplorer() {
        // TODO Auto-generated constructor stub
    }

    /* (non-Jsdoc)
     * @see org.talend.dq.analysis.explore.IDataExplorer#getQueryMap()
     */
    public Map<String, String> getQueryMap() {
        Map<String, String> map = new HashMap<String, String>();
        boolean isSqlEngine = ExecutionLanguage.SQL.equals(this.analysis.getParameters().getExecutionLanguage());
        if (!isXml() || !isSqlEngine) {
            switch (this.indicatorEnum) {
            case ValidPhoneCountIndicatorEnum:
            case PossiblePhoneCountIndicatorEnum:
            case ValidRegCodeCountIndicatorEnum:
            case InvalidRegCodeCountIndicatorEnum:
            case WellFormE164PhoneCountIndicatorEnum:
            case WellFormIntePhoneCountIndicatorEnum:
            case WellFormNationalPhoneCountIndicatorEnum:
                map.put(MENU_VIEW_ROWS, isSqlEngine ? getComment(MENU_VIEW_ROWS) + getRowsStatementWithSubQuery() : null);
                map.put(MENU_VIEW_VALUES, isSqlEngine ? getComment(MENU_VIEW_VALUES) + getValuesStatement(this.columnName) : null);
                break;

            default:
            }
        }

        return map;
    }

}
