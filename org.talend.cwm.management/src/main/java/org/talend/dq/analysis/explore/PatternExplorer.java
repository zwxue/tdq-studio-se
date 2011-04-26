// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
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
import org.talend.dataquality.indicators.PatternMatchingIndicator;
import org.talend.dataquality.indicators.columnset.ColumnsetPackage;

/**
 * @author scorreia
 * 
 * This class explores the data that matched or did not match a pattern indicator.
 */
public class PatternExplorer extends DataExplorer {

    /**
     * DOC scorreia PatternExplorer constructor comment.
     * 
     * @param dbmsLang
     */
    public PatternExplorer() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.analysis.explore.IDataExplorer#getInvalidRowsStatement()
     */
    public String getInvalidRowsStatement() {
        String regexPatternString = dbmsLanguage.getRegexPatternString((PatternMatchingIndicator) this.indicator);
        String columnName = dbmsLanguage.quote(indicator.getAnalyzedElement().getName());
        String regexCmp = dbmsLanguage.regexNotLike(columnName, regexPatternString);
        // add null as invalid rows
        String nullClause = dbmsLanguage.or() + columnName + dbmsLanguage.isNull();
        return getRowsStatement(regexCmp + nullClause);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.analysis.explore.IDataExplorer#getValidRowsStatement()
     */
    public String getValidRowsStatement() {
        String regexPatternString = dbmsLanguage.getRegexPatternString((PatternMatchingIndicator) this.indicator);
        final String columnName = dbmsLanguage.quote(indicator.getAnalyzedElement().getName());
        String regexCmp = dbmsLanguage.regexLike(columnName, regexPatternString);
        return getRowsStatement(regexCmp);
    }

    public Map<String, String> getQueryMap() {
        Map<String, String> map = new HashMap<String, String>();// MOD zshen feature 12919 adapt to pop-menu for Jave
        // MOD qiongli 2011-3-30,feature 19192,filter this menu and query for ColumnSet AllMatchIndicator
        if (ColumnsetPackage.eINSTANCE.getAllMatchIndicator().isSuperTypeOf(indicator.eClass())) {
            return map;
        }
        // engin on result page
        boolean isSqlEngine = ExecutionLanguage.SQL.equals(this.analysis.getParameters().getExecutionLanguage());
        // MOD zshen 10448 Add menus "view invalid values" and "view valid values" on pattern matching indicator
        map.put(MENU_VIEW_INVALID_VALUES, isSqlEngine ? getComment(MENU_VIEW_INVALID_VALUES) + getInvalidValuesStatement() : null);
        map.put(MENU_VIEW_VALID_VALUES, isSqlEngine ? getComment(MENU_VIEW_VALID_VALUES) + getValidValuesStatement() : null);
        // ~10448
        map.put(MENU_VIEW_INVALID_ROWS, isSqlEngine ? getComment(MENU_VIEW_INVALID_ROWS) + getInvalidRowsStatement() : null);
        map.put(MENU_VIEW_VALID_ROWS, isSqlEngine ? getComment(MENU_VIEW_VALID_ROWS) + getValidRowsStatement() : null);

        return map;
    }

    /**
     * 
     * DOC zshen Comment method "getValidValuesStatement".
     * 
     * @return SELECT statement for the invalid Value of select column
     */
    public String getInvalidValuesStatement() {
        String regexPatternString = dbmsLanguage.getRegexPatternString((PatternMatchingIndicator) this.indicator);
        String columnName = dbmsLanguage.quote(indicator.getAnalyzedElement().getName());
        String regexCmp = dbmsLanguage.regexNotLike(columnName, regexPatternString);
        // add null as invalid rows
        String nullClause = dbmsLanguage.or() + columnName + dbmsLanguage.isNull();
        return getValuesStatement(columnName, regexCmp + nullClause);
    }

    /**
     * 
     * DOC zshen Comment method "getValidValuesStatement".
     * 
     * @return SELECT statement for the valid Value of select column
     */
    public String getValidValuesStatement() {
        String regexPatternString = dbmsLanguage.getRegexPatternString((PatternMatchingIndicator) this.indicator);
        final String columnName = dbmsLanguage.quote(indicator.getAnalyzedElement().getName());
        String regexCmp = dbmsLanguage.regexLike(columnName, regexPatternString);
        return getValuesStatement(columnName, regexCmp);
    }

}
