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
public enum PatternLanguageType {

    ALL_DATABASE_TYPE(0, "SQL", PatternToExcelEnum.AllDBRegularexpression), //$NON-NLS-1$
    MYSQL(1, "MYSQL", PatternToExcelEnum.MySQLRegexp), //$NON-NLS-1$
    ORACLE(2, "ORACLE", PatternToExcelEnum.OracleRegexp), //$NON-NLS-1$
    SQLSERVER(3, "SQL SERVER", PatternToExcelEnum.SQLServerRegexp), //$NON-NLS-1$
    DB2(4, "DB2", PatternToExcelEnum.DB2Regexp), //$NON-NLS-1$
    POSTGRESQL(5, "POSTGRESQL", PatternToExcelEnum.PostgreSQLRegexp), //$NON-NLS-1$
    SYBASE(6, "SYBASE", PatternToExcelEnum.SybaseRegexp); //$NON-NLS-1$

    private int index;

    private String literal;

    private PatternToExcelEnum excelEnum;

    public PatternToExcelEnum getExcelEnum() {
        return excelEnum;
    }

    public int getIndex() {
        return index;
    }

    public String getLiteral() {
        return literal;
    }

    PatternLanguageType(int index, String literal, PatternToExcelEnum excelEnum) {
        this.index = index;
        this.literal = literal;
        this.excelEnum = excelEnum;
    }
}
