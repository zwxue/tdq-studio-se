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

import org.talend.dataquality.analysis.ExecutionLanguage;

/**
 * DOC qiongli class global comment. Detailled comment
 */
public class PhoneNumbStatisticsExplorer extends DataExplorer {

    public PhoneNumbStatisticsExplorer() {
    }

    /* (non-Jsdoc)
     * @see org.talend.dq.analysis.explore.IDataExplorer#getQueryMap()
     */
    public Map<String, String> getQueryMap() {
        Map<String, String> map = new HashMap<String, String>();
        boolean isSqlEngine = ExecutionLanguage.SQL.equals(this.analysis.getParameters().getExecutionLanguage());
        if (!isXml() && !isSqlEngine) {
            switch (this.indicatorEnum) {
            case ValidPhoneCountIndicatorEnum:
            case PossiblePhoneCountIndicatorEnum:
            case ValidRegCodeCountIndicatorEnum:
            case InvalidRegCodeCountIndicatorEnum:
            case WellFormE164PhoneCountIndicatorEnum:
            case WellFormIntePhoneCountIndicatorEnum:
            case WellFormNationalPhoneCountIndicatorEnum:
                map.put(MENU_VIEW_ROWS, null);
                map.put(MENU_VIEW_VALUES, null);
                break;

            default:
            }
        }

        return map;
    }

}
