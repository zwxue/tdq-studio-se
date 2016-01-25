// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
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
import org.talend.dq.dbms.HiveDbmsLanguage;
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
        // MOD qiongli 2012-8-29 hive don't support the complex sql.
        boolean isHive = dbmsLanguage instanceof HiveDbmsLanguage;
        if (!isHive) {
            switch (this.indicatorEnum) {
            case AverageLengthIndicatorEnum:
                // MOD msjian 2011-7-1 22549:change Sql for average length indicator
                // MOD qiongli 2011-8-10 TDQ-2474:change Sql for kinds of average length indicator
            case AverageLengthWithBlankIndicatorEnum:
                map.put(MENU_VIEW_ROWS, isSqlEngine ? getComment(MENU_VIEW_ROWS) + getAverageLengthWithBlankRowsStatement()
                        : null);
                break;
            case AverageLengthWithNullBlankIndicatorEnum:
                map.put(MENU_VIEW_ROWS, isSqlEngine ? getComment(MENU_VIEW_ROWS) + getAverageLengthWithNullBlankRowsStatement()
                        : null);
                break;
            case AverageLengthWithNullIndicatorEnum:
                map.put(MENU_VIEW_ROWS, isSqlEngine ? getComment(MENU_VIEW_ROWS) + getAverageLengthWithNullRowsStatement() : null);
                break;
            case MinLengthIndicatorEnum:
            case MaxLengthIndicatorEnum:
            case MinLengthWithBlankIndicatorEnum:
            case MinLengthWithBlankNullIndicatorEnum:
            case MinLengthWithNullIndicatorEnum:
            case MaxLengthWithBlankIndicatorEnum:
            case MaxLengthWithBlankNullIndicatorEnum:
            case MaxLengthWithNullIndicatorEnum:
                map.put(MENU_VIEW_ROWS, isSqlEngine ? getComment(MENU_VIEW_ROWS) + getTextRowsStatement() : null);
                break;
            default:
            }
        } else {
            if (!isSqlEngine) {
                map.put(MENU_VIEW_ROWS, null);
            }
        }
        return map;
    }

    /**
     * DOC qiongli 2011-09-8 TDQ-2474:view rows for average length with blank.
     * 
     * @return
     */
    private String getAverageLengthWithBlankRowsStatement() {
        String tableName = getFullyQualifiedTableName(this.indicator.getAnalyzedElement());
        return dbmsLanguage.fillGenericQueryWithColumnsAndTable(dbmsLanguage.getAverageLengthWithBlankRows(), this.columnName,
                tableName);
    }

    /**
     * DOC qiongli 2011-07-8 TDQ-2474:view rows for average length with null blank.
     * 
     * @return
     */
    private String getAverageLengthWithNullBlankRowsStatement() {
        String tableName = getFullyQualifiedTableName(this.indicator.getAnalyzedElement());
        return dbmsLanguage.fillGenericQueryWithColumnsAndTable(dbmsLanguage.getAverageLengthWithNullBlankRows(),
                this.columnName, tableName);
    }

    /**
     * DOC qiongli 2011-09-8 TDQ-2474:view rows for average length with null.
     * 
     * @return
     */
    private String getAverageLengthWithNullRowsStatement() {
        String tableName = getFullyQualifiedTableName(this.indicator.getAnalyzedElement());
        return dbmsLanguage.fillGenericQueryWithColumnsAndTable(dbmsLanguage.getAverageLengthWithNullRows(), this.columnName,
                tableName);
    }

}
