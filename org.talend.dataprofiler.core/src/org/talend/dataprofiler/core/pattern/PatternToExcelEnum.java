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
package org.talend.dataprofiler.core.pattern;


/**
 * DOC zqin class global comment. Detailled comment
 */
public enum PatternToExcelEnum {

    Label(0, "Label"),
    Purpose(1, "Purpose"),
    Description(2, "Description"),
    Author(3, "Author"),
    RelativePath(4, "Relative Path"),
    AllDBRegularexpression(5, "All DB Regular expression"),
    DB2Regexp(6, "DB2 Regexp"),
    MySQLRegexp(7, "MySQL Regexp"),
    OracleRegexp(8, "Oracle Regexp"),
    PostgreSQLRegexp(9, "PostgreSQL Regexp"),
    SQLServerRegexp(10, "SQL Server Regexp"),
    SybaseRegexp(11, "Sybase Regexp");

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
