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
package org.talend.dataprofiler.core.ui.utils;

import org.talend.cwm.relational.TdTable;
import org.talend.dataprofiler.core.model.TableIndicator;
import org.talend.dq.nodes.indicator.IIndicatorNode;
import org.talend.dq.nodes.indicator.type.IndicatorEnum;

/**
 * DOC xqliu class global comment. Detailled comment
 */
public class TableIndicatorRule {

    private TableIndicatorRule() {
    }

    public static boolean match(IIndicatorNode node, TableIndicator tableIndicator) {

        IndicatorEnum indicatorType = node.getIndicatorEnum();
        TdTable table = tableIndicator.getTdTable();

        if (indicatorType == null) {

            for (IIndicatorNode one : node.getChildren()) {
                if (match(one, tableIndicator)) {
                    return true;
                }
            }

            return false;
        }

        return patternRule(indicatorType, table);
    }

    public static boolean patternRule(IndicatorEnum indicatorType, TdTable table) {
        switch (indicatorType) {
        case RowCountIndicatorEnum:
            return true;
        default:
            return false;
        }
    }
}
