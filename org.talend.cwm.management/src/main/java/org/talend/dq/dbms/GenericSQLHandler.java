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

/**
 * DOC scorreia class global comment. Detailled comment
 */
public class GenericSQLHandler {

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

    private final String originalSQL;

    private String sqlString;

    /**
     * GenericSQLHandler constructor comment.
     * 
     * @param genericString a generic string
     */
    public GenericSQLHandler(final String genericString) {
        assert genericString != null;
        this.sqlString = new String(genericString);
        this.originalSQL = genericString;
    }

    /**
     * Getter for originalSQL.
     * 
     * @return the originalSQL
     */
    public String getOriginalSQL() {
        return this.originalSQL;
    }

    public GenericSQLHandler replaceTable(String table) {
        sqlString = sqlString.replaceAll(TABLE_NAME, table);
        return this;
    }

    public GenericSQLHandler replaceColumn(String columns) {
        sqlString = sqlString.replaceAll(COLUMN_NAMES, columns);
        return this;
    }

    public GenericSQLHandler replaceGroupByAlias(String groupByAliases) {
        sqlString = sqlString.replaceAll(GROUP_BY_ALIAS, groupByAliases);
        return this;
    }

    public GenericSQLHandler replaceColumnTableAlias(String columns, String table, String groupByAliases) {
        return this.replaceColumnTable(columns, table).replaceGroupByAlias(groupByAliases);
    }

    public GenericSQLHandler replaceColumnTable(String columns, String table) {
        return this.replaceColumn(columns).replaceTable(table);
    }

    public boolean containsAndClause() {
        return sqlString.contains(AND_WHERE_CLAUSE);
    }

    public boolean containsWhereClause() {
        return sqlString.contains(WHERE_CLAUSE);
    }

    public GenericSQLHandler replaceWhere(String whereClause) {
        sqlString = sqlString.replaceAll(WHERE_CLAUSE, whereClause);
        return this;
    }

    public GenericSQLHandler replaceAndClause(String whereClause) {
        sqlString = sqlString.replaceAll(AND_WHERE_CLAUSE, whereClause);
        return this;
    }

    public GenericSQLHandler replaceWithJoin(String tableNameA, String tableNameB, String joinClause, String whereClause) {
        sqlString = sqlString.replaceAll(TABLE_NAME, tableNameA).replaceAll(TABLE_NAME2, tableNameB).replaceAll(JOIN_CLAUSE,
                joinClause).replaceAll(WHERE_CLAUSE, whereClause);
        return this;
    }

    public GenericSQLHandler replaceLimitOffset(String colName, String table, String limitRow, String offset,
            String limitRowPlusOffset) {
        this.replaceColumnTable(colName, table);
        sqlString = this.sqlString.replaceAll(LIMIT_ROW, limitRow).replaceAll(LIMIT_OFFSET, offset).replaceAll(
                LIMIT_ROW_PLUS_OFFSET, limitRowPlusOffset);
        return this;
    }

    public GenericSQLHandler replacePattern(String regexp) {
        sqlString = sqlString.replaceAll(PATTERN_EXPRESSION, regexp);
        return this;
    }

    public GenericSQLHandler replaceColumnTablePattern(String columns, String table, String regexp) {
        return this.replaceColumnTable(columns, table).replacePattern(regexp);
    }

    public String getSqlString() {
        return this.sqlString;
    }
    
    /**
     *Method "createGenericSqlWithRegexFunction".
     * 
     * @param function a two arguments function.
     * @return the full generic statement
     */
    public String createGenericSqlWithRegexFunction(String function) {
        return "SELECT COUNT(CASE WHEN " + function + "(" + COLUMN_NAMES + "," + PATTERN_EXPRESSION
                + ") THEN 1 END), COUNT(*) FROM " + TABLE_NAME + " " + WHERE_CLAUSE;
    }
}
