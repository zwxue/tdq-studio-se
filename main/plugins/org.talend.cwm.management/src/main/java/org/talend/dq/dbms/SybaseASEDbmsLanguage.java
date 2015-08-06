// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
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
public class SybaseASEDbmsLanguage extends DbmsLanguage {

    /**
     * DOC scorreia SybaseASEDbmsLanguage constructor comment.
     */
    SybaseASEDbmsLanguage() {
        super(DbmsLanguage.SYBASE);
    }

    /**
     * DOC scorreia SybaseASEDbmsLanguage constructor comment.
     * 
     * @param dbmsType
     * @param majorVersion
     * @param minorVersion
     */
    SybaseASEDbmsLanguage(ProductVersion dbVersion) {
        super(DbmsLanguage.SYBASE, dbVersion);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.cwm.management.api.DbmsLanguage#toQualifiedName(java.lang.String, java.lang.String,
     * java.lang.String)
     */
    @Override
    public String toQualifiedName(String catalog, String schema, String table) {

        return super.toQualifiedName(catalog, schema, table);
    }

    /**
     * DOC yyi 2011-08-10 22246:view rows for aveagge length
     * 
     * @return average length sql statement
     */
    public String getAverageLengthRows() {
        return "SELECT * FROM <%=__TABLE_NAME__%> WHERE CHAR_LENGTH(<%=__COLUMN_NAMES__%>) BETWEEN (SELECT FLOOR(SUM(CHAR_LENGTH(<%=__COLUMN_NAMES__%>)) / COUNT(<%=__COLUMN_NAMES__%>)) FROM <%=__TABLE_NAME__%>) AND (SELECT CEILING(SUM(CHAR_LENGTH(<%=__COLUMN_NAMES__%>)) / COUNT(<%=__COLUMN_NAMES__%>)) FROM <%=__TABLE_NAME__%>)"; //$NON-NLS-1$
    }

    /*
     * (non-Jsdoc)
     * 
     * @see org.talend.dq.dbms.DbmsLanguage#trim(java.lang.String)
     */
    @Override
    public String trim(String colName) {
        return " LTRIM(RTRIM(" + colName + ")) "; //$NON-NLS-1$ //$NON-NLS-2$
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.dbms.DbmsLanguage#getAverageLengthWithBlankRows()
     */
    @Override
    public String getAverageLengthWithBlankRows() {
        String whereExpression = "WHERE <%=__COLUMN_NAMES__%> IS NOT NULL ";
        return "SELECT * FROM <%=__TABLE_NAME__%> WHERE " + charLength(trimIfBlank("<%=__COLUMN_NAMES__%>")) + " BETWEEN (SELECT FLOOR(SUM(" + charLength(trimIfBlank("<%=__COLUMN_NAMES__%>")) + ") / COUNT(*)) FROM <%=__TABLE_NAME__%> " + whereExpression + ") AND (SELECT CEILING(SUM(" + charLength(trimIfBlank("<%=__COLUMN_NAMES__%>")) + " ) / COUNT(* )) FROM <%=__TABLE_NAME__%> " + whereExpression + ")"; //$NON-NLS-1$
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.dbms.DbmsLanguage#getAverageLengthWithNullBlankRows()
     */
    @Override
    public String getAverageLengthWithNullBlankRows() {
        return "SELECT * FROM <%=__TABLE_NAME__%> WHERE " + charLength(trimIfBlank("<%=__COLUMN_NAMES__%>")) + " BETWEEN (SELECT FLOOR(SUM(" + charLength(trimIfBlank("<%=__COLUMN_NAMES__%>")) + ") / COUNT(*)) FROM <%=__TABLE_NAME__%>) AND (SELECT CEILING(SUM(" + charLength(trimIfBlank("<%=__COLUMN_NAMES__%>")) + " ) / COUNT(* )) FROM <%=__TABLE_NAME__%>)"; //$NON-NLS-1$
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.dbms.DbmsLanguage#getAverageLengthWithNullRows()
     */
    @Override
    public String getAverageLengthWithNullRows() {
        String whereExpression = "WHERE(<%=__COLUMN_NAMES__%> IS NULL OR " + isNotBlank("<%=__COLUMN_NAMES__%>") + ")";
        return "SELECT * FROM <%=__TABLE_NAME__%> " + whereExpression + "AND " + charLength("<%=__COLUMN_NAMES__%>") + " BETWEEN (SELECT FLOOR(SUM(" + charLength("<%=__COLUMN_NAMES__%>") + ") / COUNT( * )) FROM <%=__TABLE_NAME__%> " + whereExpression + ") AND (SELECT CEILING(SUM(" + charLength("<%=__COLUMN_NAMES__%>") + ") / COUNT(*)) FROM <%=__TABLE_NAME__%>  " + whereExpression + ")"; //$NON-NLS-1$
    }
}
