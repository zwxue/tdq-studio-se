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
package org.talend.dq.analysis.explore;

import java.util.HashMap;
import java.util.Map;

import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.definition.IndicatorDefinition;
import org.talend.dataquality.rules.WhereRule;
import org.talend.dq.analysis.TableAnalysisSqlExecutor;
import orgomg.cwm.resource.relational.NamedColumnSet;

/**
 * DOC xqliu class global comment. Detailled comment
 */
public class DQRuleExplorer extends DataExplorer {

    public Map<String, String> getQueryMap() {
        Map<String, String> map = new HashMap<String, String>();

        switch (this.indicatorEnum) {
        case WhereRuleIndicatorEnum:
            // MOD xqliu 2009-10-30 bug 9702
            if (!includeJoinCondition(this.indicator)) {
                map.put(MENU_VIEW_INVALID_ROWS, getRowsStatement(false));
            }
            // ~
            map.put(MENU_VIEW_VALID_ROWS, getRowsStatement(true));
            break;
        default:
        }

        return map;
    }

    /**
     * DOC xqliu Comment method "getRowsStatement".
     * 
     * @param valid
     * @return
     */
    private String getRowsStatement(boolean valid) {
        // MOD xqliu 2009-10-29 bug 9702
        String dataFilterClause = this.getDataFilterClause();
        Indicator indicator2 = this.indicator;

        TableAnalysisSqlExecutor tasExecutor = new TableAnalysisSqlExecutor();
        tasExecutor.setCachedAnalysis(this.analysis);

        if (valid) {
            return tasExecutor.getValidStatement(dataFilterClause, indicator2);
        } else {
            // MOD zshen 2010-01-13 bug 10913
            NamedColumnSet set = (NamedColumnSet) indicator2.getAnalyzedElement();
            String whereClause = ((WhereRule) indicator2.getIndicatorDefinition()).getWhereExpression();
            return "SELECT * FROM " + getFullyQualifiedTableName(set) + dbmsLanguage.where() + dbmsLanguage.not() + "("
                    + whereClause + ")" + andDataFilterClause();
            // ~10913
        }
        // ~
    }

    /**
     * DOC xqliu Comment method "includeJoinCondition". 2009-10-30 bug 9702
     * 
     * @param indicator
     * @return
     */
    private boolean includeJoinCondition(Indicator indicator) {
        IndicatorDefinition indicatorDefinition = indicator.getIndicatorDefinition();
        WhereRule wr = (WhereRule) indicatorDefinition;
        return !wr.getJoins().isEmpty();
    }
}
