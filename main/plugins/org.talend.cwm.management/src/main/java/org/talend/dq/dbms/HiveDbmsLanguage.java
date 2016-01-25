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

import java.sql.SQLException;
import java.sql.Statement;

import org.talend.cwm.relational.TdColumn;
import org.talend.utils.ProductVersion;
import orgomg.cwm.objectmodel.core.Expression;

/**
 * DOC qiongli class global comment. Detailled comment <br/>
 * 
 * $Id: talend.epf 55206 2011-02-15 17:32:14Z mhirt $
 * 
 */
public class HiveDbmsLanguage extends DbmsLanguage {

    /**
     * DOC qiongli HiveDbmsLanguage constructor comment.
     */
    public HiveDbmsLanguage() {
        super(DbmsLanguage.HIVE);
    }

    /**
     * DOC qiongli HiveDbmsLanguage constructor comment.
     * 
     * @param dbmsType
     */
    public HiveDbmsLanguage(String dbmsType) {
        super(dbmsType);
    }

    /**
     * DOC qiongli HiveDbmsLanguage constructor comment.
     * 
     * @param dbmsType
     * @param dbVersion
     */
    public HiveDbmsLanguage(String dbmsType, ProductVersion dbVersion) {
        super(dbmsType, dbVersion);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.dbms.DbmsLanguage#charLength(java.lang.String)
     */
    @Override
    public String charLength(String columnName) {
        return " LENGTH(" + columnName + ") "; //$NON-NLS-1$ //$NON-NLS-2$
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.dbms.DbmsLanguage#getRegularExpressionFunction()
     */
    @Override
    public String getRegularExpressionFunction() {
        return "REGEXP"; //$NON-NLS-1$
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.dbms.DbmsLanguage#getRegularExpressionFunction()
     */
    @Override
    public String extractRegularExpressionFunction(Expression expression) {
        return getRegularExpressionFunction();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.cwm.management.api.DbmsLanguage#regexLike(java.lang.String, java.lang.String)
     */
    @Override
    public String regexLike(String element, String regex) {
        return surroundWithSpaces(element + surroundWithSpaces(getRegularExpressionFunction()) + regex);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.dbms.DbmsLanguage#regexNotLike(java.lang.String, java.lang.String)
     */
    @Override
    public String regexNotLike(String element, String regex) {
        return surroundWithSpaces(element + surroundWithSpaces(this.not() + getRegularExpressionFunction()) + regex);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.dbms.DbmsLanguage#isPkIndexSupported()
     */
    @Override
    public boolean isPkIndexSupported() {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.dbms.DbmsLanguage#supportCatalogSelection()
     */
    @Override
    public boolean supportCatalogSelection() {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.dbms.DbmsLanguage#createStatement(java.sql.Connection)
     */
    @Override
    public Statement createStatement(java.sql.Connection connection, int fetchSize) throws SQLException {
        // hive don't need to set fetch size
        return connection.createStatement();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.dbms.DbmsLanguage#getQueryColumnsWithPrefix(org.talend.cwm.relational.TdColumn[])
     */
    @Override
    public String getQueryColumnsWithPrefix(TdColumn[] columns) {
        return getQueryColumns(columns);
    }
}
