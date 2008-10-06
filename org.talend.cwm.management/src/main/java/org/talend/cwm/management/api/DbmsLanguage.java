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
package org.talend.cwm.management.api;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.talend.dataquality.domain.Domain;
import org.talend.dataquality.domain.pattern.Pattern;
import org.talend.dataquality.domain.pattern.PatternComponent;
import org.talend.dataquality.domain.pattern.PatternPackage;
import org.talend.dataquality.domain.pattern.RegularExpression;
import org.talend.dataquality.domain.sql.SqlPredicate;
import org.talend.dataquality.indicators.DateGrain;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.IndicatorParameters;
import org.talend.dataquality.indicators.PatternMatchingIndicator;
import org.talend.dataquality.indicators.definition.IndicatorDefinition;
import org.talend.utils.sugars.TypedReturnCode;
import orgomg.cwm.objectmodel.core.Expression;

import Zql.ParseException;
import Zql.ZExp;
import Zql.ZExpression;
import Zql.ZQuery;
import Zql.ZqlParser;

/**
 * @author scorreia
 * 
 * This class handle DBMS specific SQL terms and functions.
 */
public class DbmsLanguage {

    private static Logger log = Logger.getLogger(DbmsLanguage.class);

    /** Functions of the system. [Function name, number of parameters] */
    private final Map<String, Integer> dbmsFunctions;

    // --- add here other supported systems (always in uppercase) // DBMS_SUPPORT

    private static final String ORACLE = "ORACLE";

    private static final String MYSQL = "MYSQL";

    private static final String POSTGRESQL = "POSTGRESQL";

    private static final String MSSQL = "MICROSOFT SQL SERVER";

    private static final String DB2 = "DB2";

    private static final String SYBASE_ASE = "ADAPTIVE SERVER ENTERPRISE";

    /**
     * Ansi SQL.
     */
    private static final String SQL = "SQL";

    private static final String DOT = ".";

    /**
     * End of Statement: ";".
     */
    private static final String EOS = ";";

    private static final String LIMIT_REGEXP = ".*(LIMIT){1}\\p{Blank}+\\p{Digit}+,?\\p{Digit}?.*";

    /**
     * Temporary table name for replacement before ZQL parsing.
     */
    private static final String TMP_TABLE_NAME = "from TMPTABLENAME_ZZ";

    private String invalidZqlQualifiedTableName;

    private String[] withoutLimit;

    private boolean containsLimitClause;

    /**
     * in upper case.
     */
    private final String dbmsName;

    /**
     * the quoting string or an empty string if quoting is not supported.
     */
    private String dbQuoteString = "";

    /**
     * DbmsLanguage constructor for generic ANSI SQL (independent of any DBMS).
     */
    DbmsLanguage() {
        this.dbmsName = SQL;
        this.dbmsFunctions = initDbmsFunctions(dbmsName);
    }

    /**
     * DbmsLanguage constructor.
     * 
     * @param dbmsType the name of the DBMS (MySQL, Oracle,...)
     */
    DbmsLanguage(String dbmsType) {
        assert dbmsType != null : "DBMS type must not be null!!";
        this.dbmsName = dbmsType;
        this.dbmsFunctions = initDbmsFunctions(dbmsName);
    }

    /**
     * DbmsLanguage constructor. Use this constructor when functions are specific to a given release of the DBMS.
     * 
     * @param dbmsType the name of the DBMS (MySQL, Oracle,...)
     * @param majorVersion the major version number
     * @param minorVersion the minor version number
     */
    DbmsLanguage(String dbmsType, int majorVersion, int minorVersion) {
        this.dbmsName = dbmsType;
        // PTODO scorreia handle dbms versions if needed
        this.dbmsFunctions = initDbmsFunctions(dbmsName);
    }

    /**
     * Method "quote".
     * 
     * @param sqlIdentifier the SQL identifier to quote
     * @return the sqlIdentifier quoted.
     */
    public String quote(String sqlIdentifier) {
        return dbQuoteString + sqlIdentifier + dbQuoteString;
    }

    public String and() {
        return surroundWithSpaces(SqlPredicate.AND.getLiteral());
    }

    public String or() {
        return surroundWithSpaces(SqlPredicate.OR.getLiteral());
    }

    public String between() {
        return surroundWithSpaces(SqlPredicate.BETWEEN.getLiteral());
    }

    /**
     * Method "isNull".
     * 
     * @return IS NULL surrounded with spaces.
     */
    public String isNull() {
        return surroundWithSpaces(SqlPredicate.IS_NULL.getLiteral());
    }

    /**
     * Method "isNotNull".
     * 
     * @return " IS NOT NULL " surrounded with spaces.
     */
    public String isNotNull() {
        return surroundWithSpaces(SqlPredicate.IS_NOT_NULL.getLiteral());
    }

    /**
     * Method "equal".
     * 
     * @return the = sign
     */
    public String equal() {
        return surroundWithSpaces(SqlPredicate.EQUAL.getLiteral());
    }

    /**
     * Method "notEqual".
     * 
     * @return "<>" by default or "!=" on some specific DBMS
     */
    public String notEqual() {
        // --- add the DBMS that do not allow <> operator

        // "!=" seem to be more performant on Oracle than "<>". See
        // http://www.freelists.org/archives/oracle-l/09-2006/msg01005.html
        if (is(ORACLE)) {
            return surroundWithSpaces(SqlPredicate.NOT_EQUAL2.getLiteral());
        }

        // ANSI SQL, MySQL,
        return surroundWithSpaces(SqlPredicate.NOT_EQUAL.getLiteral());

    }

    public String unionAll() {
        return union() + all();
    }

    public String all() {
        return surroundWithSpaces(SqlPredicate.ALL.getLiteral());
    }

    public String union() {
        return surroundWithSpaces(SqlPredicate.UNION.getLiteral());
    }

    public String from() {
        return surroundWithSpaces("FROM");
    }

    /**
     * Method "eos".
     * 
     * @return the end of SQL statement token (";")
     */
    public String eos() {
        return EOS;
    }

    // TODO scorreia implement other predicate methods

    public String getDbmsName() {
        return this.dbmsName;
    }

    /**
     * Method "getDefaultLanguage".
     * 
     * @return the default String to use when no dbms is defined.
     */
    public String getDefaultLanguage() {
        return SQL;
    }

    // TODO scorreia move this method in a utility class
    public String toQualifiedName(String catalog, String schema, String table) {
        if (is(MSSQL)) {
            schema = quote("dbo");
            // Bug fixed: 5118. ZQL parser does not understand statement like
            // select count(*) from Talend.dbo.departement
            // hence remove catalog and try statement like
            // select count(*) from dbo.departement
            // catalog = "dbo";
        }
        if (is(SYBASE_ASE)) {
            schema = quote("dbo");
            // Bug fixed: 5118. ZQL parser does not understand statement with full qualified name
            // catalog = "dbo";
        }

        StringBuffer qualName = new StringBuffer();
        if (catalog != null && catalog.length() > 0 && !is(POSTGRESQL)) {
            qualName.append(catalog);
            qualName.append(DOT);
        }
        if (schema != null && schema.length() > 0) {
            qualName.append(schema);
            qualName.append(DOT);
        }

        qualName.append(table);
        if (log.isDebugEnabled()) {
            log.debug(String.format("%s.%s.%s -> %s", catalog, schema, table, qualName));
        }
        return qualName.toString();
    }

    private String getFromQualifiedTablePattern() {
        final String dot = "\\.";
        final String alnum = quote("\\w[\\w\\d_-]*");
        return new StringBuilder().append("FROM ").append(alnum).append(dot).append(alnum).append(dot).append(alnum).toString();
    }

    /**
     * Method "parseQuery".
     * 
     * @param queryString
     * @return the parsed query. When boolean isOk() is true, this means that the finalizeQuery must called (for example
     * for handling the MySQL LIMIT clause). When false, calling finalizeQuery() is not needed (but it won't hurt to
     * call it).
     * @throws ParseException
     */
    private ZQuery parseQuery(final String queryString) throws ParseException {
        // check here whether the table name is of type Catalog.Schema.Table
        invalidZqlQualifiedTableName = get2dotTableName(queryString);

        // remove qualified names unsupported by ZQL
        String safeZqlString = invalidZqlQualifiedTableName != null ? queryString.replace(invalidZqlQualifiedTableName,
                TMP_TABLE_NAME) : queryString;

        safeZqlString = replaceUnsupportedQuotes(safeZqlString);

        // extractClause = removeExtractFromClause(safeZqlString);
        safeZqlString = closeStatement(safeZqlString);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(safeZqlString.getBytes());
        ZqlParser parser = getZqlParser();
        parser.initParser(byteArrayInputStream);
        if (log.isDebugEnabled()) {
            log.debug("Parsing query: " + safeZqlString);
        }
        ZQuery zQuery = (ZQuery) parser.readStatement();
        return zQuery;
    }

    /**
     * DOC scorreia Comment method "replaceUnsupportedQuotes".
     * 
     * @param safeZqlString
     * @return
     */
    private String replaceUnsupportedQuotes(String safeZqlString) {
        // ZQL does not support MySQL identifier quote string: `
        if (is(MYSQL)) {
            return safeZqlString.replace("`", "");
        }
        // for other DB, it's ok
        return safeZqlString;
    }

    /**
     * DOC scorreia Comment method "get2dotTableName".
     * 
     * @param queryString
     * @return
     */
    private String get2dotTableName(String queryString) {
        // queryString.matches(queryString)
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(this.getFromQualifiedTablePattern());
        Matcher matcher = p.matcher(queryString);
        if (!matcher.find()) {
            return null;
        }
        // else
        String matched = matcher.group();
        return matched;
    }

    /**
     * Method "prepareQuery" prepares the query for being parsed without exception (remove LIMIT clause...). It will
     * remove all elements that cannot be currently parsed by ZQL.
     * 
     * @param queryString
     * @return a query string safe to be parsed (whatever the boolean in returnCode is). When boolean isOk() is true,
     * this means that the finalizeQuery must called (for example for handling the MySQL LIMIT clause). When false,
     * calling finalizeQuery() is not needed (but it won't hurt to call it).
     * @throws ParseException
     */
    public TypedReturnCode<String> prepareQuery(final String queryString) throws ParseException {
        if (log.isDebugEnabled()) {
            log.debug("Preparing query: " + queryString);
        }
        // LIMIT clause is specific to MySQL, it is not understood by ZQL. remove it and add only at the end
        withoutLimit = removeLimitClause(queryString);
        containsLimitClause = (withoutLimit != null);
        String safeSqlString = containsLimitClause ? withoutLimit[0] : queryString;
        // extractClause = removeExtractFromClause(safeZqlString);

        TypedReturnCode<String> trc = new TypedReturnCode<String>();
        trc.setObject(safeSqlString);
        trc.setOk(mustCallFinalize());
        return trc;
    }

    /**
     * Method "finalizeQuery" must be called after prepareQuery().
     * 
     * @param query the query to finalize
     * @return the final query.
     */
    public String finalizeQuery(String query) {
        StringBuffer buf = new StringBuffer();
        buf.append(query.toString());
        if (containsLimitClause) {
            buf.append(" " + withoutLimit[1]);
        }
        containsLimitClause = false;
        String fquery = buf.toString();

        // replace tmp table name if needed
        if (invalidZqlQualifiedTableName != null) {
            return fquery.replace(TMP_TABLE_NAME, this.invalidZqlQualifiedTableName);
        }
        return fquery;

    }

    /**
     * Method "getZqlParser".
     * 
     * @return a new parser with predefined functions.
     */
    private ZqlParser getZqlParser() {
        ZqlParser parser = new ZqlParser();
        for (String fnct : this.dbmsFunctions.keySet()) {
            parser.addCustomFunction(fnct, this.dbmsFunctions.get(fnct));
        }
        return parser;
    }

    public String replaceNullsWithString(String colName, String replacement) {
        if (is(MYSQL)) {
            return " IFNULL(" + colName + "," + replacement + ")";
        }

        if (is(ORACLE)) {
            return " NVL(" + colName + "," + replacement + ")";
        }
        // default
        return " CASE WHEN " + colName + isNull() + " THEN " + replacement + " ELSE " + colName + " END ";
    }

    /**
     * Method "isNotBlank".
     * 
     * @param colName a column name
     * @return the expression saying that the given column is not blank.
     */
    public String isNotBlank(String colName) {
        if (is(MSSQL)) {
            return " LTRIM(RTRIM(" + colName + ")) " + notEqual() + " '' ";
        }
        // oracle does not currently distinguish between blank and null
        if (is(ORACLE)) {
            return " TRIM(" + colName + ") " + isNotNull();
        }
        // default is OK for MySQL
        return " TRIM(" + colName + ") " + notEqual() + " '' ";
    }

    public String toUpperCase(String colName) {
        // default is OK for MySQL, Oracle
        return " UPPER(" + colName + ")";
    }

    public String toLowerCase(String colName) {
        // default is OK for MySQL, Oracle
        return " LOWER(" + colName + ")";
    }

    public String extractYear(String colName) {
        return extract(DateGrain.YEAR, colName);
    }

    public String extractQuarter(String colName) {
        return extract(DateGrain.QUARTER, colName);
    }

    public String extractMonth(String colName) {
        return extract(DateGrain.MONTH, colName);
    }

    public String extractWeek(String colName) {
        return extract(DateGrain.WEEK, colName);
    }

    public String extractDay(String colName) {
        return extract(DateGrain.DAY, colName);
    }

    /**
     * Method "getTopNQuery".
     * 
     * @param query
     * @param n
     * @return the n first row of the given query
     */
    public String getTopNQuery(String query, int n) {
        if (is(ORACLE)) {
            return "SELECT * FROM (" + query + ") WHERE ROWNUM <= " + n;
        }
        if (is(MYSQL) || is(POSTGRESQL)) {
            return query + " LIMIT " + n;
        }
        if (is(DB2)) {
            return query + " FETCH FIRST " + n + " ROWS ONLY ";
        }
        // default: I don't know, simply return the query
        return query; // FIXME find how to get top n in generic SQL or maybe return null
    }

    /**
     * Method "countRowInSubquery".
     * 
     * @param subquery
     * @param alias the mandatory alias for the subquery
     * @return the select count(*) from aliased subquery
     */
    public String countRowInSubquery(String subquery, String alias) {
        if (is(ORACLE)) { // does not support "AS"
            return " SELECT COUNT(*) FROM (" + subquery + ") " + alias;
        }
        // ANSI SQL, MySQL
        return " SELECT COUNT(*) FROM (" + subquery + ") AS " + alias;
    }

    public String sumRowInSubquery(String colToSum, String subquery, String alias) {
        if (is(ORACLE)) { // does not support "AS"
            return " SELECT SUM(" + colToSum + ") FROM (" + subquery + ") " + alias;
        }
        // ANSI SQL, MySQL
        return " SELECT SUM(" + colToSum + ") FROM (" + subquery + ") AS " + alias;
    }

    /**
     * Method "getDbQuoteString".
     * 
     * @return the quote identifier string set in this object.
     */
    public String getDbQuoteString() {
        return this.dbQuoteString;
    }

    public void setDbQuoteString(String dbQuoteString) {
        if (log.isDebugEnabled()) {
            log.debug("Database SQL quote: " + dbQuoteString);
        }
        this.dbQuoteString = dbQuoteString;
    }

    private String extract(DateGrain dateGrain, String colName) {
        // DBMS_SUPPORT how to extract date parts
        if (is(MYSQL)) {
            return dateGrain.getName() + surroundWith('(', colName, ')');
        }

        if (is(ORACLE)) {
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
            }
        }

        if (is(MSSQL)) {
            return "DATEPART(" + dateGrain.getName() + " , " + colName + ") ";
        }

        // TODO sybase, DB2, Postgresql
        if (is(POSTGRESQL)) {
            return " CAST( EXTRACT(" + dateGrain + from() + colName + ") AS INTEGER )";
        }

        // ANSI SQL, MySQL, Oracle
        return " EXTRACT(" + dateGrain + from() + colName + ") ";
    }

    /**
     * DOC scorreia Comment method "removeLimitClause".
     * 
     * @param completedSqlString
     * @return
     */
    private String[] removeLimitClause(String completedSqlString) {
        if (completedSqlString == null) {
            return null;
        }
        String upperCased = completedSqlString.toUpperCase();
        if (upperCased.matches(LIMIT_REGEXP)) {
            String[] res = new String[2];
            int lastIndexOf = upperCased.lastIndexOf("LIMIT");
            res[0] = completedSqlString.substring(0, lastIndexOf);
            res[1] = completedSqlString.substring(lastIndexOf);
            return res;
        }
        return null;
    }

    /**
     * Method "closeStatement" coses the statement with ';' if needed.
     * 
     * @param safeZqlString
     * @return
     */
    private String closeStatement(String safeZqlString) {
        if (!safeZqlString.trim().endsWith(eos())) {
            safeZqlString += eos();
        }
        return safeZqlString;
    }

    /**
     * Method "is".
     * 
     * @param dbName a DBMS name
     * @return true if this DBMS is given string
     */
    private boolean is(String dbName) {
        // DBMS_SUPPORT check all caller methods to set database specific code.
        return compareDbmsLanguage(dbName, this.dbmsName);
    }

    /**
     * Method "initDbmsFunctions" initialize functions specific to DBMS. This is needed for ZQLParser which does not
     * know all available functions.
     * 
     * @param dbms
     * @return the initialized map of functions with their number of parameters.
     */
    private Map<String, Integer> initDbmsFunctions(String dbms) {
        Map<String, Integer> functions = new HashMap<String, Integer>();

        // --- functions common to all databases // DBMS_SUPPORT
        functions.put("SUM", 1);
        functions.put("MIN", 1);
        functions.put("MAX", 1);
        functions.put("UPPER", 1);
        functions.put("LOWER", 1);

        // --- set here functions specific to some databases // DBMS_SUPPORT
        // TODO as the user can write any function in the data filter, we must write all possible functions for all
        // systems.
        if (is(SQL)) {
            functions.put("TRIM", 1);
            functions.put("CHAR_LENGTH", 1);
        }

        if (is(MYSQL)) {
            functions.put("TRIM", 1);
            functions.put("RTRIM", 1);
            functions.put("LTRIM", 1);
            functions.put("LCASE", 1);
            functions.put("UCASE", 1);
            functions.put("REPLACE", 3);
            functions.put("CHAR_LENGTH", 1);
            functions.put("CHARACTER_LENGTH", 1);
            functions.put("BIT_LENGTH", 1);
            functions.put("IFNULL", 2);
            functions.put("LENGTH", 1);
            functions.put("SOUNDEX", 1);
            functions.put("SPACE", 1);
            functions.put("SUBSTRING", 2);
            functions.put("SUBSTRING", 3);
            functions.put("LEFT", 2);
            functions.put("OCTET_LENGTH", 1);
            functions.put("CONCAT", 2);
            for (DateGrain grain : DateGrain.values()) {
                functions.put(grain.getName(), 1);
            }

        }

        if (is(ORACLE)) {
            functions.put("TRIM", 1);
            functions.put("LENGTH", 1);
            functions.put("LENGTHB", 1);
            functions.put("TO_CHAR", 2);
            functions.put("TO_NUMBER", 1);
            functions.put("NVL", 2);
            functions.put("CEIL", 1);
            functions.put("FLOOR", 1);
            functions.put("ROUND", 2);
            functions.put("TRUNC", 2);
            functions.put("SIGN", 1);
            functions.put("CONVERT", 3);
            functions.put("REPLACE", 2);
            functions.put("CONCAT", 2);
            functions.put("PERCENTILE_DISC", 1);
            functions.put("PERCENTILE_CONT", 1);
            functions.put("REPLACE", 3);
        }

        if (is(POSTGRESQL)) {
            functions.put("TRIM", 1);
            functions.put("LTRIM", 1);
            functions.put("RTRIM", 1);
            functions.put("LENGTH", 1);
            functions.put("CHAR_LENGTH", 1);
            functions.put("CHARACTER_LENGTH", 1);
            functions.put("OCTET_LENGTH", 1);
            functions.put("CEIL", 1);
            functions.put("FLOOR", 1);
            functions.put("ROUND", 1);
            functions.put("ROUND", 2);
            functions.put("SIGN", 1);
            functions.put("SQRT", 1);
            functions.put("BIT_LENGTH", 1);
            functions.put("DECODE", 2);
        }

        if (is(MSSQL)) {
            functions.put("LTRIM", 1);
            functions.put("RTRIM", 1);
            // Note: datalength gives the length of any object, but in the case of unicode, this length should be
            // divided by 2 in order to get the actual number of characters.
            // Do not use "LEN" since it right-trims the strings.
            functions.put("DATALENGTH", 1);
        }

        if (is(DB2)) {
            functions.put("TRIM", 1);
            functions.put("LENGTH", 1);
        }

        if (is(SYBASE_ASE)) {
            // TODO
        }

        return functions;
    }

    private String surroundWithSpaces(String toSurround) {
        return surroundWith(' ', toSurround, ' ');
    }

    private String surroundWith(char left, String toSurround, char right) {
        return left + toSurround + right;
    }

    public String addWhereToSqlStringStatement(String completedSqlString, List<String> whereExpressions) throws ParseException {
        TypedReturnCode<String> trc = this.prepareQuery(completedSqlString);
        String query = trc.getObject();

        String where = this.buildWhereExpression(whereExpressions);
        if (where != null && where.trim().length() != 0) {
            query = this.addWhereToStatement(query, where);
        }
        return query;
    }

    /**
     * Method "addWhereToStatement".
     * 
     * @param statement a statement already prepared for parsing
     * @param whereClause
     * @return the new statement
     */
    public String addWhereToStatement(String statement, String whereClause) {
        if (!isTooComplexForZql(statement)) {
            try {
                ZQuery query = this.parseQuery(statement);
                if (whereClause != null) {
                    if (StringUtils.isNotBlank(whereClause)) {
                        ZqlParser filterParser = new ZqlParser();
                        filterParser.initParser(new ByteArrayInputStream(whereClause.getBytes()));
                        ZExp currentWhere = query.getWhere();
                        ZExp whereExpression = filterParser.readExpression();
                        if (currentWhere != null && whereExpression != null) {
                            ZExpression finalWhereExpression = new ZExpression(and(), currentWhere, whereExpression);
                            query.addWhere(finalWhereExpression);
                        } else {
                            if (whereExpression != null) {
                                query.addWhere(whereExpression);
                            }
                        }
                    }
                }
                return query.toString();
            } catch (ParseException e) {
                log.warn("Given statement cannot be parsed: " + statement + ". Error message: " + e, e);
            }
        }
        // else
        // bug 4979 fix (add where to internal query for Oracle and DB2...)
        if (statement.contains("OVER ( ORDER BY ") && statement.contains(") x")) { // awkward piece of code!!
            int insertIdx = statement.indexOf(") x");
            StringBuilder finalQuery = new StringBuilder().append(statement.substring(0, insertIdx)).append(where()).append(
                    surroundWithSpaces(whereClause)).append(statement.substring(insertIdx));
            return finalQuery.toString();
        }
        // else

        // FIXME scorreia need to parse statement here and then to add the where clause correctly
        String op = statement.toUpperCase().contains(where()) ? and() : where();
        String finalQuery = statement + op + whereClause;
        log.warn("Query parsing failed. Returning simple concatenated string: " + finalQuery);
        return finalQuery;
    }

    /**
     * DOC scorreia Comment method "isTooComplexForZql".
     * 
     * @param statement
     * @return
     */
    private boolean isTooComplexForZql(String statement) {
        return statement.contains("OVER ( ORDER BY ") && statement.contains(") x");
    }

    public String where() {
        return " WHERE ";
    }

    /**
     * Method "buildWhereExpression".
     * 
     * @param whereExpression a list of boolean clauses
     * @return clauses between parentheses and concatenated with "AND"
     */
    public String buildWhereExpression(List<String> whereExpression) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < whereExpression.size(); i++) {
            String exp = whereExpression.get(i);
            buf.append(this.surroundWith('(', exp, ')'));
            if (i != whereExpression.size() - 1) {
                buf.append(and());
            }
        }
        return buf.toString();
    }

    private boolean mustCallFinalize() {
        return containsLimitClause;
    }

    public String desc() {
        return " DESC ";
    }

    public String orderBy() {
        return " ORDER BY ";
    }

    public String groupBy() {
        return " GROUP BY ";
    }

    /**
     * Method "getSqlExpression".
     * 
     * @param indicatorDefinition contains a list of possible expression (one for each supported database)
     * @return the expression for this database language or null when not found
     */
    public Expression getSqlExpression(IndicatorDefinition indicatorDefinition) {
        Expression sqlGenExpr = getSqlExpression(indicatorDefinition, this.dbmsName);
        if (sqlGenExpr != null) {
            return sqlGenExpr; // language found
        }

        // else try with default language (ANSI SQL)
        log.warn("The indicator SQL expression has not been found for the database type " + this.dbmsName + " for the indicator"
                + indicatorDefinition.getName());
        if (log.isInfoEnabled()) {
            log.info("Trying to compute the indicator with the default language " + getDefaultLanguage());
        }
        return getSqlExpression(indicatorDefinition, getDefaultLanguage());
    }

    /**
     * Method "getRegexPatternString".
     * 
     * @param indicator
     * @return the regular expression or null if none was found
     */
    public String getRegexPatternString(PatternMatchingIndicator indicator) {
        IndicatorParameters parameters = indicator.getParameters();
        if (parameters == null) {
            return null;
        }
        Domain dataValidDomain = parameters.getDataValidDomain();
        if (dataValidDomain == null) {
            return null;
        }
        EList<Pattern> patterns = dataValidDomain.getPatterns();
        for (Pattern pattern : patterns) {
            return this.getRegexp(pattern);
        }
        return null;
    }

    /**
     * DOC scorreia Comment method "getSqlExpression".
     * 
     * @param indicatorDefinition
     * @param defaultLanguage
     * @return
     */
    private static Expression getSqlExpression(IndicatorDefinition indicatorDefinition, String language) {
        EList<Expression> sqlGenericExpression = indicatorDefinition.getSqlGenericExpression();
        for (Expression sqlGenExpr : sqlGenericExpression) {
            if (compareDbmsLanguage(language, sqlGenExpr.getLanguage())) {
                return sqlGenExpr; // language found
            }
        }
        return null;
    }

    private static boolean compareDbmsLanguage(String lang1, String lang2) {
        if (lang1 == null || lang2 == null) {
            return false;
        }
        // MOD 2008-08-04 scorreia: for DB2 database, dbName can be "DB2/NT" or "DB2/6000" or "DB2"...
        if (lang1.startsWith(DB2)) {
            return StringUtils.upperCase(lang1).startsWith(StringUtils.upperCase(lang2))
                    || StringUtils.upperCase(lang2).startsWith(StringUtils.upperCase(lang1));
        }
        return StringUtils.equalsIgnoreCase(lang1, lang2);
    }

    /**
     * Method "getRegexp".
     * 
     * @param pattern a pattern
     * @return the body of the regular expression applicable to this dbms or null
     */
    public String getRegexp(Pattern pattern) {
        EList<PatternComponent> components = pattern.getComponents();
        for (PatternComponent patternComponent : components) {
            if (patternComponent != null) {
                Expression expression = this.getExpression(patternComponent);
                if (expression != null) {
                    return expression.getBody();
                }
            }
        }
        return null;
    }

    /**
     * Method "getExpression".
     * 
     * @param patternComponent
     * @return the expression for the correct language or null
     */
    public Expression getExpression(PatternComponent patternComponent) {
        if (patternComponent != null && patternComponent.eClass().equals(PatternPackage.eINSTANCE.getRegularExpression())) {
            RegularExpression regExp = (RegularExpression) patternComponent;
            Expression expression = regExp.getExpression();
            if (expression != null) {
                return getApplicable(expression);
            }
        }
        // not a regular expression
        return null;
    }

    private Expression getApplicable(Expression expression) {
        return isApplicable(expression) ? expression : null;
    }

    /**
     * Method "getApplicableExpression".
     * 
     * @param expression an expression (not null)
     * @return
     */
    public boolean isApplicable(Expression expression) {
        // handle database specific patterns
        if (is(expression.getLanguage())) {
            return true;
        }
        // try generic language
        if (SQL.equals(expression.getLanguage())) {
            return true;
        }
        // else no correct language found
        return false;
    }

    /**
     * Method "getRegexpTestString" returns the SQL SELECT statement that can be used to check a string against a
     * regular expression.
     * 
     * @param stringToCheck a string to check (not a column name)
     * @param regularExpression
     * @return the appropriate SQL SELECT statement that can be used to check a string against a regular expression.
     */
    public String getSelectRegexpTestString(String stringToCheck, Expression regularExpression) {
        if (!isApplicable(regularExpression)) {
            return null;
        }
        String regex = regularExpression.getBody();
        if (regex == null) {
            return null;
        }
        return getSelectRegexpTestString(stringToCheck, regex);
    }

    /**
     * Method "getSelectRegexpTestString".
     * 
     * @param stringToCheck a string to check against the regular expression (can contain quotes at start and end)
     * @param regex a regular expression
     * @return the appropriate SQL SELECT statement that can be used to check a string against a regular expression.
     */
    public String getSelectRegexpTestString(String stringToCheck, String regex) {
        String surroundedTestString = (stringToCheck.startsWith("'") && stringToCheck.endsWith("'")) ? stringToCheck
                : surroundWith('\'', stringToCheck, '\'');
        String regexLikeExpression = regexLike(surroundedTestString, regex);
        // else
        if (regexLikeExpression == null) {
            return null;
        }
        if (is(MYSQL) || is(POSTGRESQL)) {
            return "SELECT " + regexLikeExpression + " AS OK" + EOS;
        }

        if (is(ORACLE)) {
            return "SELECT " + regexLikeExpression + EOS;
        }

        return null;
    }

    /**
     * Method "regex".
     * 
     * @param element
     * @param regex
     * @return the regular expression according to the DBMS syntax or null if not supported.
     */
    public String regexLike(String element, String regex) {
        if (is(MYSQL)) {
            return surroundWithSpaces(element + " REGEXP " + regex);
        }
        if (is(ORACLE)) {
            return surroundWithSpaces("REGEXP_LIKE(" + element + " , " + regex + " )");
        }
        if (is(POSTGRESQL)) {
            return surroundWithSpaces(element + " ~ " + regex);
        }
        return null;
    }

    public String regexNotLike(String element, String regex) {
        if (is(MYSQL)) {
            return surroundWithSpaces(element + " NOT REGEXP " + regex);
        }
        if (is(ORACLE)) {
            return surroundWithSpaces("NOT REGEXP_LIKE(" + element + " , " + regex + " )");
        }
        if (is(POSTGRESQL)) {
            return surroundWithSpaces(element + " !~ " + regex);
        }
        return null;
    }

    /**
     * Method "getQuoteIdentifier" returns the hard coded quote identifier string. You should call
     * {@link #getDbQuoteString()} instead.
     * 
     * @return hard coded quote identifier string.
     */
    public String getQuoteIdentifier() {
        if (is(MYSQL)) {
            return "`";
        }
        if (is(ORACLE)) {
            return "\"";
        }
        if (is(POSTGRESQL)) {
            return "\"";
        }
        return "";
    }

    /**
     * Method "supportAliasesInGroupBy".
     * 
     * @return true when the DB supports aliases in group by clause
     */
    public boolean supportAliasesInGroupBy() {
        // DBMS_SUPPORT
        if (is(MYSQL)) {
            return true;
        }
        // else Oracle, MSSQL, DB2, Postgresql
        return false;
    }

    /**
     * Method "supportNonIntegerConstantInGroupBy".
     * 
     * @return true if expression like "GROUP BY 'toto'" are supported
     */
    public boolean supportNonIntegerConstantInGroupBy() {
        // DBMS_SUPPORT
        if (is(POSTGRESQL) || is(MSSQL)) {
            return false;
        }
        // else MySQL, Oracle
        return true;
    }

    /**
     * Method "getSelectRemarkOnTable".
     * 
     * @param tableName a table name
     * @return the select statement to execute to get the comment on the table, or null
     */
    public String getSelectRemarkOnTable(String tableName) {
        if (is(ORACLE)) {
            return "SELECT COMMENTS FROM USER_TAB_COMMENTS WHERE TABLE_NAME='" + tableName + "'";
        }
        if (is(MYSQL)) {
            return "SELECT TABLE_COMMENT FROM information_schema.TABLES WHERE TABLE_NAME='" + tableName + "'";
        }
        return null;
    }

    /**
     * Method "getSelectRemarkOnColumn".
     * 
     * @param columnName a column name
     * @return the select statement to execute to get the comment on the column, or null
     */
    public String getSelectRemarkOnColumn(String columnName) {
        if (is(ORACLE)) {
            return "SELECT COMMENTS FROM USER_COL_COMMENTS WHERE COLUMN_NAME='" + columnName + "'";
        }
        return null;
    }

    /**
     * Method "getInstantiatedExpression".
     * 
     * @param indicator
     * @return the appropriate expression or the default one (or null when it does not exists)
     */
    public Expression getInstantiatedExpression(Indicator indicator) {
        Expression query = indicator.getInstantiatedExpressions(this.getDbmsName());
        if (query == null) {
            // try to get a default sql expression
            query = indicator.getInstantiatedExpressions(this.getDefaultLanguage());
        }
        return query;

    }

}
