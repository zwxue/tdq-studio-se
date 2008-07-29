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
import java.util.Vector;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.talend.dataquality.domain.pattern.PatternComponent;
import org.talend.dataquality.domain.pattern.PatternPackage;
import org.talend.dataquality.domain.pattern.RegularExpression;
import org.talend.dataquality.domain.sql.SqlPredicate;
import org.talend.dataquality.indicators.DateGrain;
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

    // --- add here other supported systems (always in uppercase)

    private static final String ORACLE = "ORACLE";

    private static final String MYSQL = "MYSQL";

    private static final String POSTGRESQL = "POSTGRESQL";

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

    // private static final String EXTRACT_REGEXP =
    // ".*\\(\\p{Blank}(EXTRACT){1}\\p{Blank}+(FROM){1}\\p{BLANK}+\\)?\\p{Blank}?.*";

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
        // do not quote SQL identifier on MySQL database because ZQLParser does not understand "`"
        if (is(MYSQL)) {
            return sqlIdentifier;
        }
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
    public String toQualifiedName(String catalog, String schema, String name) {
        StringBuffer qualName = new StringBuffer();
        if (catalog != null && catalog.length() > 0) {
            qualName.append(catalog);
            qualName.append(DOT);
        }
        if (schema != null && schema.length() > 0) {
            qualName.append(schema);
            qualName.append(DOT);
        }

        qualName.append(name);
        if (log.isDebugEnabled()) {
            log.debug(String.format("%s.%s.%s -> %s", catalog, schema, name, qualName));
        }
        return qualName.toString();
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
        // String query = removeSemiColumn(queryString);
        String safeZqlString = queryString;
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
     * DOC scorreia Comment method "removeSemiColumn".
     * 
     * @param queryString
     * @return
     */
    private String removeSemiColumn(String queryString) {
        if (queryString.trim().endsWith(eos())) {
            int endIndex = queryString.lastIndexOf(eos());
            return queryString.substring(0, endIndex);
        }
        return queryString;
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
        return buf.toString();
        // return closeStatement(buf.toString());
    }

    /**
     * Method "finalizeQuery" must be called after parseQuery().
     * 
     * @param zQuery
     * @return the final query string
     * @deprecated use this{@link #finalizeQuery(String)} instead
     */
    private String finalizeQuery(ZQuery zQuery) {
        StringBuffer buf = new StringBuffer();
        buf.append(zQuery.toString());
        if (containsLimitClause) {
            buf.append(" " + withoutLimit[1]);
        }
        return buf.toString();
    }

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

    public String isNotBlank(String colName) {
        // default is OK for MySQL, Oracle
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
        if (is(MYSQL)) {
            return query + " LIMIT " + n;
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
        return StringUtils.equalsIgnoreCase(this.dbmsName, dbName);
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

        // --- functions common to all databases
        functions.put("TRIM", 1);
        functions.put("SUM", 1);
        functions.put("MIN", 1);
        functions.put("MAX", 1);
        functions.put("UPPER", 1);
        functions.put("LOWER", 1);

        // --- set here functions specific to some databases
        if (is(SQL)) {
            functions.put("CHAR_LENGTH", 1);
        }

        if (is(MYSQL)) {
            functions.put("CHAR_LENGTH", 1);
            functions.put("IFNULL", 2);
            for (DateGrain grain : DateGrain.values()) {
                functions.put(grain.getName(), 1);
            }

        }

        if (is(ORACLE)) {
            functions.put("LENGTH", 1);
            functions.put("TO_CHAR", 2);
            functions.put("TO_NUMBER", 1);
            functions.put("NVL", 2);
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
            log.error(e, e);
        }
        // FIXME scorreia need to parse statement here and then to add the where clause correctly
        String finalQuery = statement + where() + whereClause;
        log.error("Query parsing failed. Returning simple concatenated string: " + finalQuery);
        return finalQuery;
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

    /**
     * DOC scorreia Comment method "getOrderBy".
     * 
     * @param sqlStatement
     * @return
     */
    public String getOrderBy(String sqlStatement) {
        ZqlParser zqlParser = getZqlParser();
        zqlParser.initParser(new ByteArrayInputStream(sqlStatement.getBytes()));
        try {
            ZQuery zQuery = (ZQuery) zqlParser.readStatement();
            if (zQuery == null) {
                return null;
            }
            Vector orderBy = zQuery.getOrderBy();
            if (orderBy == null || orderBy.size() != 1) {
                return null;
            }
            return orderBy.get(0).toString();
        } catch (ParseException e) {
            log.error(e, e);
        }
        // TODO Auto-generated method stub
        return null;
    }

    public String orderBy() {
        return " ORDER BY ";
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
     * DOC scorreia Comment method "getRegexpTestString".
     * 
     * @param stringToCheck
     * @param regularExpression
     * @return
     */
    public String getRegexpTestString(String stringToCheck, Expression regularExpression) {
        if (!isApplicable(regularExpression)) {
            return null;
        }
        // else
        if (is(MYSQL)) {
            return "SELECT " + surroundWith('\'', stringToCheck, '\'') + " REGEXP " + regularExpression.getBody() + EOS;
        }

        if (is(ORACLE)) {
            return "SELECT REGEXP_LIKE(" + surroundWith('\'', stringToCheck, '\'') + " , " + regularExpression.getBody() + ")"
                    + EOS;
        }
        return null;
    }

    /**
     * Method "getQuoteIdentifier".
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
}
