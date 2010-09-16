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
import orgomg.cwm.objectmodel.core.Expression;

/**
 * DOC zqin class global comment. Detailled comment
 */
public class TextStatisticsExplorer extends DataExplorer {

    private String getTextRowsStatement() {
        String lang = dbmsLanguage.getDbmsName();
        Expression instantiatedExpression = this.indicator.getInstantiatedExpressions(lang);
        String instantiatedSQL = instantiatedExpression.getBody();

        String clause = dbmsLanguage.charLength(this.columnName) + dbmsLanguage.equal() + "(" + instantiatedSQL + ")"; //$NON-NLS-1$ //$NON-NLS-2$
        return getRowsStatement(clause);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.analysis.explore.IDataExplorer#getQueryMap()
     */
    public Map<String, String> getQueryMap() {
        boolean isSqlEngine = ExecutionLanguage.SQL.equals(this.analysis.getParameters().getExecutionLanguage());
        Map<String, String> map = new HashMap<String, String>();

        switch (this.indicatorEnum) {
        case MinLengthIndicatorEnum:
        case MaxLengthIndicatorEnum:
        case AverageLengthIndicatorEnum:
        case MinLengthWithBlankIndicatorEnum:
        case MinLengthWithBlankNullIndicatorEnum:
        case MinLengthWithNullIndicatorEnum:
        case MaxLengthWithBlankIndicatorEnum:
        case MaxLengthWithBlankNullIndicatorEnum:
        case MaxLengthWithNullIndicatorEnum:
        case AverageLengthWithBlankIndicatorEnum:
        case AverageLengthWithNullBlankIndicatorEnum:
        case AverageLengthWithNullIndicatorEnum:
            map.put(MENU_VIEW_ROWS, isSqlEngine ? getTextRowsStatement() : null);
            break;
        default:
        }
        return map;
    }

}
