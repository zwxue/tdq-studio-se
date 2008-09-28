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

import org.talend.dataprofiler.core.model.dburl.SupportDBUrlType;

/**
 * DOC zqin class global comment. Detailled comment
 */
public enum PatternLanguageType {

    ALL_DATABASE_TYPE(0, "ALL_DATABASE_TYPE", SupportDBUrlType.ALL, PatternToExcelEnum.AllDBRegularexpression),
    MYSQL(1, "MYSQL", SupportDBUrlType.MYSQLDEFAULTURL, PatternToExcelEnum.MySQLRegexp),
    ORACLE(2, "ORACLE", SupportDBUrlType.ORACLEWITHSIDDEFAULTURL, PatternToExcelEnum.OracleRegexp),
    SQLSERVER(3, "SQLSERVER", SupportDBUrlType.MSSQLDEFAULTURL, PatternToExcelEnum.SQLServerRegexp),
    DB2(4, "DB2", SupportDBUrlType.DB2DEFAULTURL, PatternToExcelEnum.DB2Regexp),
    POSTGRESQL(5, "POSTGRESQL", SupportDBUrlType.POSTGRESQLEFAULTURL, PatternToExcelEnum.PostgreSQLRegexp);

    private int index;

    private String literal;

    private PatternToExcelEnum excelEnum;

    private SupportDBUrlType dbType;

    public SupportDBUrlType getDbType() {
        return dbType;
    }

    public PatternToExcelEnum getExcelEnum() {
        return excelEnum;
    }

    public int getIndex() {
        return index;
    }

    public String getLiteral() {
        return literal;
    }

    PatternLanguageType(int index, String literal, SupportDBUrlType dbType, PatternToExcelEnum excelEnum) {
        this.index = index;
        this.literal = literal;
        this.dbType = dbType;
        this.excelEnum = excelEnum;
    }

    private static final PatternLanguageType[] VALUES_ARRAY = new PatternLanguageType[] { ALL_DATABASE_TYPE, MYSQL, ORACLE,
            SQLSERVER, DB2, POSTGRESQL };

    public static final List<PatternLanguageType> VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));
}
