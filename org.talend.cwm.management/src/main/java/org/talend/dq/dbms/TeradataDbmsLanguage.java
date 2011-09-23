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
package org.talend.dq.dbms;

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
        // String whereExpression = "WHERE <%=__COLUMN_NAMES__%> IS NOT NULL ";
        //        return "SELECT * FROM <%=__TABLE_NAME__%> WHERE CHAR_LENGTH(" + trimIfBlank("<%=__COLUMN_NAMES__%>") + ") BETWEEN (SELECT FLOOR(SUM(CHAR_LENGTH(" + trimIfBlank("<%=__COLUMN_NAMES__%>") + ")) / COUNT(*)) FROM <%=__TABLE_NAME__%> " + whereExpression + ") AND (SELECT CEIL(SUM(LENGTH(" + trimIfBlank("<%=__COLUMN_NAMES__%>") + " )) / COUNT(* )) FROM <%=__TABLE_NAME__%> " + whereExpression + ")"; //$NON-NLS-1$
        String sql = "SELECT t.* FROM(SELECT CAST(SUM(" + charLength(trimIfBlank("<%=__COLUMN_NAMES__%>"))
                + ") / (COUNT(<%=__COLUMN_NAMES__%> )*1.00)+0.99 as int) c," + "CAST(SUM("
                + charLength(trimIfBlank("<%=__COLUMN_NAMES__%>")) + ") / (COUNT(<%=__COLUMN_NAMES__%>)*1.00) as int) f "
                + "FROM <%=__TABLE_NAME__%> WHERE(<%=__COLUMN_NAMES__%> IS NOT NULL)) e, <%=__TABLE_NAME__%> t "
 + "WHERE "
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
        //        return "SELECT * FROM <%=__TABLE_NAME__%> WHERE CHAR_LENGTH(" + trimIfBlank("<%=__COLUMN_NAMES__%>") + ") BETWEEN (SELECT FLOOR(SUM(CHAR_LENGTH(" + trimIfBlank("<%=__COLUMN_NAMES__%>") + ")) / COUNT(*)) FROM <%=__TABLE_NAME__%>) AND (SELECT CEIL(SUM(LENGTH(" + trimIfBlank("<%=__COLUMN_NAMES__%>") + " )) / COUNT(* )) FROM <%=__TABLE_NAME__%>)"; //$NON-NLS-1$
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
}
