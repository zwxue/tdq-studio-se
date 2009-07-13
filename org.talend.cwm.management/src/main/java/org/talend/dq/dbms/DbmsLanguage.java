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
package org.talend.dq.dbms;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.talend.cwm.dburl.SupportDBUrlType;
import org.talend.cwm.helper.ColumnHelper;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.cwm.relational.TdColumn;
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
import org.talend.dataquality.rules.JoinElement;
import org.talend.utils.ProductVersion;
import orgomg.cwm.objectmodel.core.Expression;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * @author scorreia
 * 
 * This class handle DBMS specific SQL terms and functions. It provides methods to handle SQL clause and evrything
 * related to a specific DBMS.
 * 
 * this class provides a default implementation. Subclasses exist for each DBMS so that specific implementation can be
 * written.
 */
public class DbmsLanguage {

    private static Logger log = Logger.getLogger(DbmsLanguage.class);

    // TODO scorreia put this into its own class and offer simple methods to replace tokens.

    // --- add here other supported systems (always in uppercase) // DBMS_SUPPORT

    static final String ORACLE = SupportDBUrlType.ORACLEWITHSIDDEFAULTURL.getLanguage();

    static final String MYSQL = SupportDBUrlType.MYSQLDEFAULTURL.getLanguage();

    static final String POSTGRESQL = SupportDBUrlType.POSTGRESQLEFAULTURL.getLanguage();

    static final String MSSQL = SupportDBUrlType.MSSQLDEFAULTURL.getLanguage();

    static final String DB2 = SupportDBUrlType.DB2DEFAULTURL.getLanguage();

    static final String DB2ZOS = SupportDBUrlType.DB2ZOSDEFAULTURL.getLanguage();

    static final String SYBASE_ASE = SupportDBUrlType.SYBASEDEFAULTURL.getLanguage();

    static final String SQLITE3 = SupportDBUrlType.SQLITE3DEFAULTURL.getLanguage();

    static final String TERADATA = SupportDBUrlType.TERADATADEFAULTURL.getLanguage();

    /**
     * Ansi SQL.
     */
    static final String SQL = "SQL"; //$NON-NLS-1$

    private static final String DOT = "."; //$NON-NLS-1$

    /**
     * End of Statement: ";".
     */
    protected static final String EOS = ";"; //$NON-NLS-1$

    /**
     * in upper case.
     */
    private final String dbmsName;

    /**
     * version for current database.
     */
    private final ProductVersion dbVersion;

    /**
     * the quoting string or an empty string if quoting is not supported.
     */
    private String dbQuoteString = ""; //$NON-NLS-1$

    /**
     * DbmsLanguage constructor for generic ANSI SQL (independent of any DBMS).
     */
    DbmsLanguage() {
        this(SQL);
    }

    /**
     * DbmsLanguage constructor.
     * 
     * @param dbmsType the name of the DBMS (MySQL, Oracle,...)
     */
    DbmsLanguage(String dbmsType) {
        assert dbmsType != null : "DBMS type must not be null!!"; //$NON-NLS-1$
        this.dbmsName = dbmsType;
        this.dbVersion = null;
    }

    /**
     * DbmsLanguage constructor. Use this constructor when functions are specific to a given release of the DBMS.
     * 
     * @param dbmsType the database name
     * @param dbVersion the database version number
     */
    DbmsLanguage(String dbmsType, ProductVersion dbVersion) {
        this.dbmsName = dbmsType;
        this.dbVersion = dbVersion;
    }

    /**
     * Method "quote".
     * 
     * @param sqlIdentifier the SQL identifier to quote
     * @return the sqlIdentifier quoted.
     */
    public String quote(String sqlIdentifier) {
        String quotedSqlIdentifier = sqlIdentifier;
        if (!quotedSqlIdentifier.startsWith(dbQuoteString)) {
            quotedSqlIdentifier = dbQuoteString + quotedSqlIdentifier;
        }
        if (!quotedSqlIdentifier.endsWith(dbQuoteString)) {
            quotedSqlIdentifier = quotedSqlIdentifier + dbQuoteString;
        }
        return quotedSqlIdentifier;
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
        return surroundWithSpaces("FROM"); //$NON-NLS-1$
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

    public ProductVersion getDbVersion() {
        return dbVersion;
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
        if (catalog != null && catalog.trim().length() > 0) {
            qualName.append(this.quote(catalog));
            qualName.append(DOT);
        }
        if (schema != null && schema.trim().length() > 0) {
            qualName.append(this.quote(schema));
            qualName.append(DOT);
        }

        qualName.append(this.quote(table));
        if (log.isDebugEnabled()) {
            log.debug(String.format("%s.%s.%s -> %s", catalog, schema, table, qualName)); //$NON-NLS-1$
        }
        return qualName.toString();
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
        return " CASE WHEN " + colName + isNull() + " THEN " + replacement + " ELSE " + colName + " END "; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
    }

    /**
     * Method "isNotBlank".
     * 
     * @param colName a column name
     * @return the expression saying that the given column is not blank.
     */
    public String isNotBlank(String colName) {
        // default is OK for MySQL
        return trim(colName) + notEqual() + " '' "; //$NON-NLS-1$
    }

    public String trim(String colName) {
        // default is OK for MySQL
        return " TRIM(" + colName + ") "; //$NON-NLS-1$ //$NON-NLS-2$
    }

    public String toUpperCase(String colName) {
        // default is OK for MySQL, Oracle
        return " UPPER(" + colName + ")"; //$NON-NLS-1$ //$NON-NLS-2$
    }

    public String toLowerCase(String colName) {
        // default is OK for MySQL, Oracle
        return " LOWER(" + colName + ")"; //$NON-NLS-1$ //$NON-NLS-2$
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
        return " SELECT COUNT(*) FROM (" + subquery + ") AS " + alias; //$NON-NLS-1$ //$NON-NLS-2$
    }

    public String sumRowInSubquery(String colToSum, String subquery, String alias) {
        // ANSI SQL, MySQL
        return " SELECT SUM(" + colToSum + ") FROM (" + subquery + ") AS " + alias; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }

    public String selectAllRowsWhereColumnIn(String column, String table, String subquery) {
        return " SELECT * FROM " + table + where() + column + in() + "( SELECT " + column + from() + "(" + subquery //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                + ") AS mysubquery )"; //$NON-NLS-1$
    }

    public String in() {
        return " IN "; //$NON-NLS-1$
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
        return " EXTRACT(" + dateGrain + from() + colName + ") "; //$NON-NLS-1$ //$NON-NLS-2$
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

    protected String surroundWithSpaces(String toSurround) {
        return surroundWith(' ', toSurround, ' ');
    }

    protected String surroundWith(char left, String toSurround, char right) {
        return left + toSurround + right;
    }

    public String addWhereToSqlStringStatement(String completedSqlString, List<String> whereExpressions) {
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
     * @param whereClause (must not be null, but can be empty when there is no where clause)
     * @return the new statement
     */
    public String addWhereToStatement(String statement, String whereClause) {
        final GenericSQLHandler genericSQLHandler = new GenericSQLHandler(statement);
        if (genericSQLHandler.containsWhereClause()) {
            whereClause = whereClause.length() != 0 ? where() + whereClause : whereClause;
            statement = genericSQLHandler.replaceWhere(whereClause).getSqlString();
        }

        if (genericSQLHandler.containsAndClause()) {
            whereClause = whereClause.length() != 0 ? and() + whereClause : whereClause;
            statement = genericSQLHandler.replaceAndClause(whereClause).getSqlString();
        }
        return statement;
    }

    public String where() {
        return " WHERE "; //$NON-NLS-1$
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
        return " DESC "; //$NON-NLS-1$
    }

    public String orderBy() {
        return " ORDER BY "; //$NON-NLS-1$
    }

    public String groupBy() {
        return " GROUP BY "; //$NON-NLS-1$
    }

    /**
     * Method "getSqlExpression".
     * 
     * @param indicatorDefinition contains a list of possible expression (one for each supported database)
     * @return the expression for this database language or for the default SQL or null when not found
     */
    public Expression getSqlExpression(IndicatorDefinition indicatorDefinition) {
        EList<Expression> sqlGenericExpression = indicatorDefinition.getSqlGenericExpression();

        Expression sqlGenExpr = getSqlExpression(indicatorDefinition, this.dbmsName, sqlGenericExpression);
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
        return getSqlExpression(indicatorDefinition, getDefaultLanguage(), sqlGenericExpression);
    }

    /**
     * Method "getAggregate1argFunctions".
     * 
     * @param indicatorDefinition
     * @return the ordered list of aggregate functions
     */
    public List<String> getAggregate1argFunctions(IndicatorDefinition indicatorDefinition) {
        final EList<Expression> aggregate1argFunctions = indicatorDefinition.getAggregate1argFunctions();
        return getFunctions(indicatorDefinition, aggregate1argFunctions);
    }

    /**
     * Method "getDate1argFunctions".
     * 
     * @param indicatorDefinition
     * @return the ordered list of functions applicable to date columns
     */
    public List<String> getDate1argFunctions(IndicatorDefinition indicatorDefinition) {
        final EList<Expression> date1argFunctions = indicatorDefinition.getDate1argFunctions();
        return getFunctions(indicatorDefinition, date1argFunctions);
    }

    private List<String> getFunctions(IndicatorDefinition indicatorDefinition, final EList<Expression> functions) {
        Expression sqlGenExpr = getSqlExpression(indicatorDefinition, this.dbmsName, functions);
        if (sqlGenExpr != null) {
            final String body = sqlGenExpr.getBody();
            final String[] fonc = body.split(";"); //$NON-NLS-1$
            return Arrays.asList(fonc); // language found
        }

        // else try with default language (ANSI SQL)
        sqlGenExpr = getSqlExpression(indicatorDefinition, getDefaultLanguage(), functions);
        if (sqlGenExpr != null) {
            final String body = sqlGenExpr.getBody();
            final String[] fonc = body.split(";"); //$NON-NLS-1$
            return Arrays.asList(fonc); // language found
        }
        return Collections.emptyList();
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
    private static Expression getSqlExpression(IndicatorDefinition indicatorDefinition, String language,
            EList<Expression> sqlGenericExpression) {
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

    public String getBackSlashForRegex() {
        return "\\"; //$NON-NLS-1$
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
        String surroundedTestString = (stringToCheck.startsWith("'") && stringToCheck.endsWith("'")) ? stringToCheck //$NON-NLS-1$ //$NON-NLS-2$
                : surroundWith('\'', stringToCheck, '\'');
        String regexLikeExpression = regexLike(surroundedTestString, regex);
        // else
        if (regexLikeExpression == null) {
            return null;
        }
        return getSelectRegexp(regexLikeExpression);
    }

    protected String getSelectRegexp(String regexLikeExpression) {
        return "SELECT " + regexLikeExpression + EOS; //$NON-NLS-1$
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
     * Method "getHardCodedQuoteIdentifier" returns the hard coded quote identifier string. You should call
     * {@link #getDbQuoteString()} instead.
     * 
     * @return hard coded quote identifier string
     */
    public String getHardCodedQuoteIdentifier() {
        return ""; //$NON-NLS-1$
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
        // MySQL, Oracle
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
     * @param genericQuery
     * @param columns
     * @param table
     * @param groupByAliases
     */
    public String fillGenericQueryWithColumnTableAndAlias(String genericQuery, String columns, String table, String groupByAliases) {
        return new GenericSQLHandler(genericQuery).replaceColumnTableAlias(columns, table, groupByAliases).getSqlString();

    }

    public String fillGenericQueryWithColumnsAndTable(String genericQuery, String columns, String table) {
        return new GenericSQLHandler(genericQuery).replaceColumnTable(columns, table).getSqlString();
    }

    public String fillGenericQueryWithColumnTablePattern(String genericQuery, String columns, String table, String regexp) {
        return new GenericSQLHandler(genericQuery).replaceColumnTablePattern(columns, table, regexp).getSqlString();
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
        return new GenericSQLHandler(genericQuery).replaceLimitOffset(colName, table, limitRow, offset, limitRowPlusOffset)
                .getSqlString();
    }

    public String fillGenericQueryWithJoin(String genericSQL, String tableNameA, String tableNameB, String joinClause,
            String whereClause) {
        return new GenericSQLHandler(genericSQL).replaceWithJoin(tableNameA, tableNameB, joinClause, whereClause).getSqlString();
    }

    /**
     * DOC scorreia Comment method "fillGenericQueryWithJoin".
     * 
     * @param body
     * @param tableName
     * @param joinclause
     * @return
     */
    public String fillGenericQueryWithJoin(String genericSQL, String tableName, String joinclause) {
        return new GenericSQLHandler(genericSQL).replaceTable(tableName).replaceJoinClause(joinclause).getSqlString();
    }

    /**
     * Method "charLength".
     * 
     * @param columnName
     * @return CHAR_LENGTH(columnName)
     */
    public String charLength(String columnName) {
        return " CHAR_LENGTH(" + columnName + ") "; //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * DOC bzhou Comment method "supportRegexp".
     * 
     * check if the database surpport the Regexp.
     * 
     * @return false by default.
     */
    public boolean supportRegexp() {
        return false;
    }
    
    public String createJoinConditionAsString(List<JoinElement> joinElements) {
        if (joinElements.isEmpty()) {
            return "";
        }
        // else
        StringBuilder builder = new StringBuilder();
        for (JoinElement joinElement : joinElements) {
            ModelElement colA = joinElement.getColA();
            String tableA = getTableName(colA);
            String tableAliasA = joinElement.getTableAliasA();
            
            String columnAName = getColumnName(colA);
            String columnAliasA = joinElement.getColumnAliasA();

            boolean hasTableAliasA = !StringUtils.isEmpty(tableAliasA);
            boolean hasColumnAliasA = !StringUtils.isEmpty(columnAliasA);

            
            ModelElement colB = joinElement.getColB();
            String tableB = getTableName(colB);
            String tableAliasB = joinElement.getTableAliasB();

            String columnBName = getColumnName(colB);            
            String columnAliasB = joinElement.getColumnAliasB();

            boolean hasTableAliasB = !StringUtils.isEmpty(tableAliasB);
            boolean hasColumnAliasB = !StringUtils.isEmpty(columnAliasB);

            String operator = joinElement.getOperator();

            // tableA tableAliasA JOIN
            // builder.append(surroundWithSpaces(quote(tableA)));
            if (hasTableAliasA) {
                builder.append(surroundWithSpaces(tableAliasA));
            }
            builder.append(" JOIN ");

            // tableB tableAliasB ON
            builder.append(surroundWithSpaces(quote(tableB)));
            if (hasTableAliasB) {
                builder.append(surroundWithSpaces(tableAliasB));
            }
            builder.append(" ON ");

            String tA = hasTableAliasA ? null : quote(tableA);
            String tB = hasTableAliasB ? null : quote(tableB);
            String cA = hasColumnAliasA ? columnAliasA : quote(columnAName);
            String cB = hasColumnAliasB ? columnAliasB : quote(columnBName);
            createJoinClause(builder, tA, cA, tB, cB, operator);
        }
        return builder.toString();
    }

    /**
     * Method "createJoinClause" appends a join condition to the builder.
     * 
     * @param builder
     * @param tableA the name of the table or null
     * @param columnAName a column name (or an alias)
     * @param tableB the name of the second table or null
     * @param columnBName the column name (or an alias)
     * @param operator the operator used in the join
     * 
     * When using a column alias instead of a name, the table name must be set to null (because it's not required)
     */
    private void createJoinClause(StringBuilder builder, String tableA, String columnAName, String tableB, String columnBName,
            String operator) {
        // (columaliasA = columnaliasB)
        builder.append('(');
        if (tableA != null) {
            builder.append(surroundWithSpaces(tableA + "."));
        }
        builder.append(surroundWithSpaces(columnAName));

        builder.append(operator);
        if (tableB != null) {
            builder.append(surroundWithSpaces(tableB + "."));
        }
        builder.append(surroundWithSpaces(columnBName));
        builder.append(')');
    }
    
    private String getColumnName(ModelElement colA) {
        TdColumn columnA = colA != null ? SwitchHelpers.COLUMN_SWITCH.doSwitch(colA) : null;
        return columnA != null ? columnA.getName() : null;
    }

    private String getTableName(ModelElement colA) {
        TdColumn columnA = colA != null ? SwitchHelpers.COLUMN_SWITCH.doSwitch(colA) : null;
        return (columnA != null) ? ColumnHelper.getColumnSetFullName(columnA) : null;
    }

}
