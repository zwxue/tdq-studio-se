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
 * DOC xqliu  class global comment. Detailled comment
 */
public class IngresDbmsLanguage extends DbmsLanguage {

    /**
     * DOC xqliu IngresDbmsLanguage constructor comment.
     */
    IngresDbmsLanguage() {
        super(DbmsLanguage.INGRES);
    }

    /**
     * DOC xqliu IngresDbmsLanguage constructor comment.
     * 
     * @param dbmsType
     * @param dbVersion
     */
    IngresDbmsLanguage(String dbmsType, ProductVersion dbVersion) {
        super(dbmsType, dbVersion);
    }

    public String toQualifiedName(String catalog, String schema, String table) {
        return super.toQualifiedName(null, null, table);
    }

    /**
     * DOC yyi 2011-07-07 22246:view rows for average length for Oracle
     * 
     * @return average length sql statement
     */
    public String getAverageLengthRows() {
        return "SELECT t.* FROM(" + "SELECT "
                + "CAST(SUM(LENGTH(<%=__COLUMN_NAMES__%>)) / (COUNT(<%=__COLUMN_NAMES__%>)*1.00)+0.99 as int) c, "
                + "CAST(SUM(LENGTH(<%=__COLUMN_NAMES__%>)) / (COUNT(<%=__COLUMN_NAMES__%>)*1.00) as int) f "
                + "FROM <%=__TABLE_NAME__%>) e, <%=__TABLE_NAME__%> t " + "where LENGTH(<%=__COLUMN_NAMES__%>) BETWEEN f AND c";
    }
}
