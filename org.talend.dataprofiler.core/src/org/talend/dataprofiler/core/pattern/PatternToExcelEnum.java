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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * DOC zqin class global comment. Detailled comment
 */
public enum PatternToExcelEnum {

    Label(0, "Laebl"),
    Purpose(1, "Purpose"),
    Description(2, "Description"),
    Author(3, "Author"),
    RelativePath(4, "Relative Path"),
    AllDBRegularexpression(5, "All DB Regular expression"),
    DB2Regexp(6, "DB2 Regexp"),
    MySQLRegexp(7, "MySQL Regexp"),
    OracleRegexp(8, "Oracle Regexp"),
    PostgreSQLRegexp(9, "PostgreSQL Regexp"),
    SQLServerRegexp(10, "SQL Server Regexp");

    private String literal;

    private int index;

    public String getLiteral() {
        return literal;
    }

    PatternToExcelEnum(int index, String literal) {
        this.index = index;
        this.literal = literal;
    }

    private static final PatternToExcelEnum[] VALUES_ARRAY = new PatternToExcelEnum[] { Label, Purpose, Description, Author,
            RelativePath, AllDBRegularexpression, DB2Regexp, MySQLRegexp, OracleRegexp, PostgreSQLRegexp, SQLServerRegexp };

    public static final List<PatternToExcelEnum> VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));
}
