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
package org.talend.dq.dbms;

import org.talend.dataquality.domain.sql.SqlPredicate;
import org.talend.dataquality.indicators.DateGrain;

/**
 * DOC scorreia class global comment. Detailled comment
 */
public class OracleDbmsLanguage extends DbmsLanguage {

    /**
     * DOC scorreia OracleDbmsLanguage constructor comment.
     */
    public OracleDbmsLanguage() {
        super(DbmsLanguage.ORACLE);
    }

    /**
     * DOC scorreia OracleDbmsLanguage constructor comment.
     * 
     * @param dbmsType
     * @param majorVersion
     * @param minorVersion
     */
    public OracleDbmsLanguage(String dbmsType, int majorVersion, int minorVersion) {
        super(dbmsType, majorVersion, minorVersion);
        // TODO Auto-generated constructor stub
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.cwm.management.api.DbmsLanguage#notEqual()
     */
    @Override
    public String notEqual() {
        // "!=" seem to be more performant on Oracle than "<>". See
        // http://www.freelists.org/archives/oracle-l/09-2006/msg01005.html
        return surroundWithSpaces(SqlPredicate.NOT_EQUAL2.getLiteral());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.cwm.management.api.DbmsLanguage#getPatternFinderDefaultFunction(java.lang.String)
     */
    @Override
    public String getPatternFinderDefaultFunction(String expression) {
        return "TRANSLATE(" + expression
                + " ,'1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZÂÊÎÔÛÄËÏÖÜabcdefghijklmnopqrstuvwxyzçâêîôûéèùïöü' "
                + ",RPAD('9',10,'9') || RPAD('A',36,'A')||RPAD('a',38,'a'))";
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.cwm.management.api.DbmsLanguage#replaceNullsWithString(java.lang.String, java.lang.String)
     */
    @Override
    public String replaceNullsWithString(String colName, String replacement) {
        if ("''".equals(replacement)) {
            return colName;
        }
        return " NVL(" + colName + "," + replacement + ")";
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.cwm.management.api.DbmsLanguage#isNotBlank(java.lang.String)
     */
    @Override
    public String isNotBlank(String colName) {
        // oracle does not currently distinguish between blank and null
        return trim(colName) + isNotNull();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.cwm.management.api.DbmsLanguage#getTopNQuery(java.lang.String, int)
     */
    @Override
    public String getTopNQuery(String query, int n) {
        return "SELECT * FROM (" + query + ") WHERE ROWNUM <= " + n;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.cwm.management.api.DbmsLanguage#countRowInSubquery(java.lang.String, java.lang.String)
     */
    @Override
    public String countRowInSubquery(String subquery, String alias) {
        // does not support "AS"
        return " SELECT COUNT(*) FROM (" + subquery + ") " + alias;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.cwm.management.api.DbmsLanguage#sumRowInSubquery(java.lang.String, java.lang.String,
     * java.lang.String)
     */
    @Override
    public String sumRowInSubquery(String colToSum, String subquery, String alias) {
        // does not support "AS"
        return " SELECT SUM(" + colToSum + ") FROM (" + subquery + ") " + alias;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.cwm.management.api.DbmsLanguage#extract(org.talend.dataquality.indicators.DateGrain,
     * java.lang.String)
     */
    @Override
    protected String extract(DateGrain dateGrain, String colName) {
        String toNumberToChar = "TO_NUMBER(TO_CHAR(";
        switch (dateGrain.getValue()) {
        case DateGrain.DAY_VALUE:
            return toNumberToChar + colName + ", 'DD'))";
        case DateGrain.WEEK_VALUE:
            return toNumberToChar + colName + ", 'IW'))";
        case DateGrain.MONTH_VALUE:
            return toNumberToChar + colName + ",'MM'))";
        case DateGrain.QUARTER_VALUE:
            return toNumberToChar + colName + ",'Q'))";
        case DateGrain.YEAR_VALUE:
            return toNumberToChar + colName + ", 'YYYY'))";
        default:
            return super.extract(dateGrain, colName);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.cwm.management.api.DbmsLanguage#regexLike(java.lang.String, java.lang.String)
     */
    @Override
    public String regexLike(String element, String regex) {
        return surroundWithSpaces("REGEXP_LIKE(" + element + " , " + regex + " )");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.cwm.management.api.DbmsLanguage#regexNotLike(java.lang.String, java.lang.String)
     */
    @Override
    public String regexNotLike(String element, String regex) {
        return surroundWithSpaces("NOT REGEXP_LIKE(" + element + " , " + regex + " )");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.cwm.management.api.DbmsLanguage#getQuoteIdentifier()
     */
    @Override
    public String getHardCodedQuoteIdentifier() {
        return "\"";
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.cwm.management.api.DbmsLanguage#getSelectRemarkOnTable(java.lang.String)
     */
    @Override
    public String getSelectRemarkOnTable(String tableName) {
        return "SELECT COMMENTS FROM USER_TAB_COMMENTS WHERE TABLE_NAME='" + tableName + "'";
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.cwm.management.api.DbmsLanguage#getSelectRemarkOnColumn(java.lang.String)
     */
    @Override
    public String getSelectRemarkOnColumn(String columnName) {
        return "SELECT COMMENTS FROM USER_COL_COMMENTS WHERE COLUMN_NAME='" + columnName + "'";
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.dbms.DbmsLanguage#charLength(java.lang.String)
     */
    @Override
    public String charLength(String columnName) {
        return " LENGTH(" + columnName + ") ";
    }

}
