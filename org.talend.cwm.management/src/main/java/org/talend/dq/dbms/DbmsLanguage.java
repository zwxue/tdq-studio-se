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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import orgomg.cwm.objectmodel.core.Expression;

import Zql.ParseException;

/**
 * @author scorreia
 * 
 * This class handle DBMS specific SQL terms and functions.
 * 
 * TODO scorreia split this class into specific subclasses for each DBMS and let the factory choose the appropriate
 * instance.
 * 
 * TODO use SupportDBUrlType
 */
public class DbmsLanguage {

    private static Logger log = Logger.getLogger(DbmsLanguage.class);

    /** Functions of the system. [Function name, number of parameters] */
    private final Map<String, Integer> dbmsFunctions;

    private static final String AND_WHERE_CLAUSE = "<%=__AND_WHERE_CLAUSE__%>";

    private static final String WHERE_CLAUSE = "<%=__WHERE_CLAUSE__%>";

    private static final String TABLE_NAME = "<%=__TABLE_NAME__%>";

    private static final String TABLE_NAME2 = "<%=__TABLE_NAME_2__%>";

    private static final String COLUMN_NAMES = "<%=__COLUMN_NAMES__%>";

    private static final String GROUP_BY_ALIAS = "<%=__GROUP_BY_ALIAS__%>";

    private static final String LIMIT_ROW = "<%=__LIMIT_ROW__%>";

    private static final String PATTERN_EXPRESSION = "<%=__PATTERN_EXPR__%>";

    private static final String JOIN_CLAUSE = "<%=__JOIN_CLAUSE__%>";

    private static final String LIMIT_OFFSET = "<%=__LIMIT_OFFSET__%>";

    private static final String LIMIT_ROW_PLUS_OFFSET = "<%=__LIMIT_ROW_PLUS_OFFSET__%>";

    // --- add here other supported systems (always in uppercase) // DBMS_SUPPORT

    static final String ORACLE = "ORACLE";

    static final String MYSQL = "MYSQL";

    static final String POSTGRESQL = "POSTGRESQL";

    static final String MSSQL = "MICROSOFT SQL SERVER";

    static final String DB2 = "DB2";

    static final String SYBASE_ASE = "ADAPTIVE SERVER ENTERPRISE";

    /**
     * Ansi SQL.
     */
    static final String SQL = "SQL";

    private static final String DOT = ".";

    /**
     * End of Statement: ";".
     */
    protected static final String EOS = ";";

    /**
     * Temporary table name for replacement before ZQL parsing.
     */

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

    public String greater() {
        return surroundWithSpaces(SqlPredicate.GREATER.getLiteral());
    }

    public String greaterOrEqual() {
        return surroundWithSpaces(SqlPredicate.GREATER_EQUAL.getLiteral());
    }

    public String less() {
        return surroundWithSpaces(SqlPredicate.LESS.getLiteral());
    }

    public String lessOrEqual() {
        return surroundWithSpaces(SqlPredicate.LESS_EQUAL.getLiteral());
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
     * Method "addDatabaseFunction" adds or replaces the function.
     * 
     * @param functionName the function name
     * @param nbParameter the number of parameters of this function
     * @return true if the function is new, false if it already existed.
     */
    public boolean addDatabaseFunction(String functionName, Integer nbParameter) {
        return this.dbmsFunctions.put(functionName, nbParameter) != null;
    }

    /**
     * Method "getDefaultLanguage".
     * 
     * @return the default String to use when no dbms is defined.
     */
    public String getDefaultLanguage() {
        return SQL;
    }

    public String toQualifiedName(String catalog, String schema, String table) {
        StringBuffer qualName = new StringBuffer();
        if (catalog != null && catalog.length() > 0) {
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

    /**
     * Method "replaceUnsupportedQuotes".
     * 
     * Override this method when the identifier quotes are not supported by the ZQL Parser (e.g. MySQL `).
     * 
     * @param sqlString
     * @return the input without the quotes
     */
    protected String replaceUnsupportedQuotes(String sqlString) {
        return sqlString;
    }


    /**
     * Method "getPatternFinderDefaultFunction".
     * 
     * @param expression a column name or a string
     * @return a default SQL expression which can be used as pattern finder or null
     */
    public String getPatternFinderDefaultFunction(String expression) {
        return null;
    }

    public String replaceNullsWithString(String colName, String replacement) {
        return " CASE WHEN " + colName + isNull() + " THEN " + replacement + " ELSE " + colName + " END ";
    }

    /**
     * Method "isNotBlank".
     * 
     * @param colName a column name
     * @return the expression saying that the given column is not blank.
     */
    public String isNotBlank(String colName) {
        // default is OK for MySQL
        return trim(colName) + notEqual() + " '' ";
    }

    public String trim(String colName) {
        // default is OK for MySQL
        return " TRIM(" + colName + ") ";
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
        // ANSI SQL, MySQL
        return " SELECT COUNT(*) FROM (" + subquery + ") AS " + alias;
    }

    public String sumRowInSubquery(String colToSum, String subquery, String alias) {
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

    protected String extract(DateGrain dateGrain, String colName) {
        // ANSI SQL, MySQL, Oracle
        return " EXTRACT(" + dateGrain + from() + colName + ") ";
    }

    /**
     * Method "is".
     * 
     * @param dbName a DBMS name
     * @return true if this DBMS is given string
     */
    private boolean is(String dbName) {
        return DbmsLanguageFactory.compareDbmsLanguage(dbName, this.dbmsName);
    }

    /**
     * Method "initDbmsFunctions" initialize functions specific to DBMS. This is needed for ZQLParser which does not
     * know all available functions.
     * 
     * @param dbms
     * @return the initialized map of functions with their number of parameters.
     */
    protected Map<String, Integer> initDbmsFunctions(String dbms) {
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
        // if (is(SQL)) {
        // functions.put("TRIM", 1);
        // functions.put("CHAR_LENGTH", 1);
        // }

        return functions;
    }

    protected String surroundWithSpaces(String toSurround) {
        return surroundWith(' ', toSurround, ' ');
    }

    protected String surroundWith(char left, String toSurround, char right) {
        return left + toSurround + right;
    }

    public String addWhereToSqlStringStatement(String completedSqlString, List<String> whereExpressions) throws ParseException {
        String query = completedSqlString;
        String where = this.buildWhereExpression(whereExpressions);
        if (where != null) {
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
        // if (!isTooComplexForZql(statement)) {
        // try {
        // ZQuery query = this.parseQuery(statement);
        // if (whereClause != null) {
        // if (StringUtils.isNotBlank(whereClause)) {
        // String safeWhereClause = replaceUnsupportedQuotes(whereClause);
        // ZqlParser filterParser = getZqlParser();
        // filterParser.initParser(new ByteArrayInputStream(safeWhereClause.getBytes()));
        // ZExp currentWhere = query.getWhere();
        // ZExp whereExpression = filterParser.readExpression();
        // if (currentWhere != null && whereExpression != null) {
        // ZExpression finalWhereExpression = new ZExpression(and(), currentWhere, whereExpression);
        // query.addWhere(finalWhereExpression);
        // } else {
        // if (whereExpression != null) {
        // query.addWhere(whereExpression);
        // }
        // }
        // }
        // }
        // return query.toString();
        // } catch (ParseException e) {
        // log.warn("Given statement cannot be parsed: " + statement + ". Error message: " + e, e);
        // }
        // }
        // // else
        // // bug 4979 fix (add where to internal query for Oracle and DB2...)
        // if (statement.contains("OVER ( ORDER BY ") && statement.contains(") x")) { // awkward piece of code!!
        // int insertIdx = statement.indexOf(") x");
        // StringBuilder finalQuery = new StringBuilder().append(statement.substring(0,
        // insertIdx)).append(where()).append(
        // surroundWithSpaces(whereClause)).append(statement.substring(insertIdx));
        // return finalQuery.toString();
        // }

        if (statement.contains(WHERE_CLAUSE)) {
            whereClause = whereClause.length() != 0 ? where() + whereClause : whereClause;
            statement = statement.replaceAll(WHERE_CLAUSE, whereClause);
        }

        if (statement.contains(AND_WHERE_CLAUSE)) {
            whereClause = whereClause.length() != 0 ? and() + whereClause : whereClause;
            statement = statement.replaceAll(AND_WHERE_CLAUSE, whereClause);
        }
        return statement;
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
     * @return the expression for this database language or for the default SQL or null when not found
     */
    public Expression getSqlExpression(IndicatorDefinition indicatorDefinition) {
        Expression sqlGenExpr = getSqlExpression(indicatorDefinition, this.dbmsName);
        if (sqlGenExpr != null) {
            return sqlGenExpr; // language found
        }

        // else try with default language (ANSI SQL)
        if (log.isDebugEnabled()) {
            log.warn("The indicator SQL expression has not been found for the database type " + this.dbmsName
                    + " for the indicator" + indicatorDefinition.getName()
                    + ". This is not necessarily a problem since the default SQL expression will be used. "
                    + "Nevertheless, if an SQL error during the analysis, this could be the cause.");
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
            if (DbmsLanguageFactory.compareDbmsLanguage(language, sqlGenExpr.getLanguage())) {
                return sqlGenExpr; // language found
            }
        }
        return null;
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
        return getSelectRegexp(regexLikeExpression);
    }

    protected String getSelectRegexp(String regexLikeExpression) {
        return "SELECT " + regexLikeExpression + EOS;
    }

    /**
     * Method "regex".
     * 
     * @param element
     * @param regex
     * @return the regular expression according to the DBMS syntax or null if not supported.
     */
    public String regexLike(String element, String regex) {
        return null;
    }

    public String regexNotLike(String element, String regex) {
        return null;
    }

    /**
     * Method "getQuoteIdentifier" returns the hard coded quote identifier string. You should call
     * {@link #getDbQuoteString()} instead.
     * 
     * @return hard coded quote identifier string supported by the internal SQL parser (ZQL)
     */
    public String getSupportedQuoteIdentifier() {
        return "";
    }

    /**
     * Method "supportAliasesInGroupBy".
     * 
     * @return true when the DB supports aliases in group by clause
     */
    public boolean supportAliasesInGroupBy() {
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
        return null;
    }

    /**
     * Method "getSelectRemarkOnColumn".
     * 
     * @param columnName a column name
     * @return the select statement to execute to get the comment on the column, or null
     */
    public String getSelectRemarkOnColumn(String columnName) {
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

    /**
     * DOC scorreia Comment method "fillGenericQueryWithColumnTableAndAlias".
     * 
     * @param genericSQL
     * @param columns
     * @param table
     * @param groupByAliases
     */
    public String fillGenericQueryWithColumnTableAndAlias(String genericSQL, String columns, String table, String groupByAliases) {
        return genericSQL.replaceAll(TABLE_NAME, table).replaceAll(COLUMN_NAMES, columns).replaceAll(GROUP_BY_ALIAS,
                groupByAliases);

    }

    public String fillGenericQueryWithColumnsAndTable(String genericQuery, String columns, String table) {
        return this.fillGenericQueryWithColumnTableAndAlias(genericQuery, columns, table, "");
    }

    public String fillGenericQueryWithColumnTablePattern(String genericQuery, String columns, String table, String regexp) {
        return this.fillGenericQueryWithColumnsAndTable(genericQuery, columns, table).replaceAll(PATTERN_EXPRESSION, regexp);
    }

    /**
     * DOC scorreia Comment method "fillGenericQueryWithColumnTableLimitOffset".
     * 
     * @param genericQuery
     * @param colName
     * @param table
     * @param limitRow
     * @param offset
     * @param limitRowPlusOffset
     * @return
     */
    public String fillGenericQueryWithColumnTableLimitOffset(String genericQuery, String colName, String table, String limitRow,
            String offset, String limitRowPlusOffset) {
        return this.fillGenericQueryWithColumnsAndTable(genericQuery, colName, table).replaceAll(LIMIT_ROW, limitRow).replaceAll(
                LIMIT_OFFSET, offset).replaceAll(LIMIT_ROW_PLUS_OFFSET, limitRowPlusOffset);
    }

    public String fillGenericQueryWithJoin(String genericSQL, String tableNameA, String tableNameB, String joinClause,
            String whereClause) {
        return genericSQL.replaceAll(TABLE_NAME, tableNameA).replaceAll(TABLE_NAME2, tableNameB).replaceAll(JOIN_CLAUSE,
                joinClause).replaceAll(WHERE_CLAUSE, whereClause);
    }

}
