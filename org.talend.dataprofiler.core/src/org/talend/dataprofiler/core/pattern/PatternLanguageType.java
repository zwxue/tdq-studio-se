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

import org.talend.cwm.dburl.SupportDBUrlType;

/**
 * DOC zqin class global comment. Detailled comment
 */
public enum PatternLanguageType {

    ALL_DATABASE_TYPE(0, "SQL", PatternToExcelEnum.AllDBRegularexpression),
    MYSQL(1, SupportDBUrlType.MYSQLDEFAULTURL.getLanguage(), PatternToExcelEnum.MySQLRegexp),
    ORACLE(2, SupportDBUrlType.ORACLEWITHSIDDEFAULTURL.getLanguage(), PatternToExcelEnum.OracleRegexp),
    SQLSERVER(3, SupportDBUrlType.MSSQLDEFAULTURL.getLanguage(), PatternToExcelEnum.SQLServerRegexp),
    DB2(4, SupportDBUrlType.DB2DEFAULTURL.getLanguage(), PatternToExcelEnum.DB2Regexp),
    POSTGRESQL(5, SupportDBUrlType.POSTGRESQLEFAULTURL.getLanguage(), PatternToExcelEnum.PostgreSQLRegexp);

    private int index;

    private String literal;

    private PatternToExcelEnum excelEnum;

    public PatternToExcelEnum getExcelEnum() {
        SupportDBUrlType.DB2DEFAULTURL.getLanguage();
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

    private static final PatternLanguageType[] VALUES_ARRAY = new PatternLanguageType[] { ALL_DATABASE_TYPE, MYSQL, ORACLE,
            SQLSERVER, DB2, POSTGRESQL };

    public static final List<PatternLanguageType> VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));
}
