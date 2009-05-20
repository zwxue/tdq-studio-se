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
package org.talend.dataprofiler.core.pattern;

/**
 * DOC zqin class global comment. Detailled comment
 */
public enum PatternToExcelEnum {

    Label(0, "Label"), //$NON-NLS-1$
    Purpose(1, "Purpose"), //$NON-NLS-1$
    Description(2, "Description"), //$NON-NLS-1$
    Author(3, "Author"), //$NON-NLS-1$
    RelativePath(4, "Relative Path"), //$NON-NLS-1$
    AllDBRegexp(5, "All DB Regexp"), //$NON-NLS-1$
    DB2Regexp(6, "DB2 Regexp"), //$NON-NLS-1$
    MySQLRegexp(7, "MySQL Regexp"), //$NON-NLS-1$
    OracleRegexp(8, "Oracle Regexp"), //$NON-NLS-1$
    PostgreSQLRegexp(9, "PostgreSQL Regexp"), //$NON-NLS-1$
    SQLServerRegexp(10, "SQL Server Regexp"), //$NON-NLS-1$
    SybaseRegexp(11, "Sybase Regexp"), //$NON-NLS-1$
    IngresRegexp(12, "Ingres Regexp"), //$NON-NLS-1$
    InformixRegexp(13, "Informix Regexp"), //$NON-NLS-1$
    SQLite3Regexp(14, "SQLite3 Regexp"), //$NON-NLS-1$
    Teradata(15, "Teradata Regexp"); //$NON-NLS-1$

    private String literal;

    private int index;

    public int getIndex() {
        return index;
    }

    public String getLiteral() {
        return literal;
    }

    PatternToExcelEnum(int index, String literal) {
        this.index = index;
        this.literal = literal;
    }
}
