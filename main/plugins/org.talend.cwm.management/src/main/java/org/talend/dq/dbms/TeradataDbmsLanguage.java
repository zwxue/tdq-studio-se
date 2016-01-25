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
package org.talend.dq.dbms;

import java.util.regex.Matcher;

import org.talend.core.model.metadata.builder.connection.DatabaseConnection;
import org.talend.dataquality.PluginConstant;
import org.talend.utils.ProductVersion;

/**
 * DOC scorreia class global comment. Detailled comment
 */
public class TeradataDbmsLanguage extends DbmsLanguage {

    /**
     * DOC scorreia TeradataDbmsLanguage constructor comment.
     */
    TeradataDbmsLanguage() {
        super(DbmsLanguage.TERADATA);
    }

    /**
     * DOC scorreia TeradataDbmsLanguage constructor comment.
     * 
     * @param dbmsType
     * @param majorVersion
     * @param minorVersion
     */
    TeradataDbmsLanguage(String dbmsType, ProductVersion dbVersion) {
        super(dbmsType, dbVersion);
    }

    /**
     * DOC yyi 2011-07-07 22246:view rows for average length for Oracle
     * 
     * @return average length sql statement
     */
    @Override
    public String getAverageLengthRows() {
        return "SELECT t.* FROM(" + "SELECT "
                + "CAST(SUM(CHARACTER_LENGTH(<%=__COLUMN_NAMES__%>)) / (COUNT(<%=__COLUMN_NAMES__%>)*1.00)+0.99 as int) c, "
                + "CAST(SUM(CHARACTER_LENGTH(<%=__COLUMN_NAMES__%>)) / (COUNT(<%=__COLUMN_NAMES__%>)*1.00) as int) f "
                + "FROM <%=__TABLE_NAME__%>) e, <%=__TABLE_NAME__%> t "
                + "where character_length(<%=__COLUMN_NAMES__%>) between f and c";
    }

    /*
     * (non-Jsdoc)
     * 
     * @see org.talend.dq.dbms.DbmsLanguage#getAverageLengthWithBlankRows()
     */
    @Override
    public String getAverageLengthWithBlankRows() {
        String sql = "SELECT t.* FROM(SELECT CAST(SUM(" + charLength(trimIfBlank("<%=__COLUMN_NAMES__%>"))
                + ") / (COUNT(<%=__COLUMN_NAMES__%> )*1.00)+0.99 as int) c," + "CAST(SUM("
                + charLength(trimIfBlank("<%=__COLUMN_NAMES__%>")) + ") / (COUNT(<%=__COLUMN_NAMES__%>)*1.00) as int) f "
                + "FROM <%=__TABLE_NAME__%> WHERE(<%=__COLUMN_NAMES__%> IS NOT NULL)) e, <%=__TABLE_NAME__%> t " + "WHERE "
                + charLength(trimIfBlank("<%=__COLUMN_NAMES__%>")) + " BETWEEN f AND c";
        return sql;
    }

    /*
     * (non-Jsdoc)
     * 
     * @see org.talend.dq.dbms.DbmsLanguage#getAverageLengthWithNullBlankRows()
     */
    @Override
    public String getAverageLengthWithNullBlankRows() {
        String sql = "SELECT t.* FROM(SELECT CAST(SUM(" + charLength(trimIfBlank("<%=__COLUMN_NAMES__%>"))
                + ") / (COUNT(*)*1.00)+0.99 as int) c," + "CAST(SUM(" + charLength(trimIfBlank("<%=__COLUMN_NAMES__%>"))
                + ") / (COUNT(*)*1.00) as int) f " + "FROM <%=__TABLE_NAME__%> ) e, <%=__TABLE_NAME__%> t " + "WHERE "
                + charLength(trimIfBlank("<%=__COLUMN_NAMES__%>")) + " BETWEEN f AND c";
        return sql;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.dbms.DbmsLanguage#getAverageLengthWithNullRows()
     */
    @Override
    public String getAverageLengthWithNullRows() {
        String whereExp = "WHERE(<%=__COLUMN_NAMES__%> IS NULL OR " + isNotBlank("<%=__COLUMN_NAMES__%>") + ")";
        String sql = "SELECT t.* FROM(SELECT " + "CAST(SUM(" + charLength("<%=__COLUMN_NAMES__%>")
                + ") / (COUNT(<%=__COLUMN_NAMES__%> )*1.00)+0.99 as int) c," + "CAST(SUM(" + charLength("<%=__COLUMN_NAMES__%>")
                + ") / (COUNT(<%=__COLUMN_NAMES__%>)*1.00) as int) f " + "FROM <%=__TABLE_NAME__%> " + whereExp
                + ") e, <%=__TABLE_NAME__%> t " + whereExp + "AND " + charLength("<%=__COLUMN_NAMES__%>") + " BETWEEN f AND c";
        return sql;
    }

    @Override
    protected String getPatternFinderFunction(String expression, String charsToReplace, String replacementChars) {
        assert charsToReplace != null && replacementChars != null && charsToReplace.length() == replacementChars.length();
        return expression;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.dbms.DbmsLanguage#getTopNQuery(java.lang.String, int)
     */
    @Override
    public String getTopNQuery(String query, int n) {
        Matcher m = SELECT_PATTERN.matcher(query);
        return m.replaceFirst("SELECT TOP " + n + PluginConstant.SPACE_STRING); //$NON-NLS-1$ 
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.dbms.DbmsLanguage#getCatalogNameFromContext(org.talend.core.model.metadata.builder.connection.
     * DatabaseConnection)
     */
    @Override
    public String getCatalogNameFromContext(DatabaseConnection dbConn) {
        return null;
    }
}
