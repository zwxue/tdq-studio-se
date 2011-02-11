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
package org.talend.dataprofiler.core.pattern;

import java.util.HashSet;
import java.util.Set;

import org.talend.core.model.metadata.builder.database.dburl.SupportDBUrlType;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;

/**
 * DOC zqin class global comment. Detailled comment
 */
public enum PatternLanguageType {

    Default(0, DefaultMessagesImpl.getString("PatternLanguageType.Default"), "SQL", PatternToExcelEnum.AllDBRegexp), //$NON-NLS-1$ //$NON-NLS-2$
    MYSQL(
          1,
          SupportDBUrlType.MYSQLDEFAULTURL.getLanguage(),
          SupportDBUrlType.MYSQLDEFAULTURL.getLanguage(),
          PatternToExcelEnum.MySQLRegexp), //$NON-NLS-1$
    ORACLE(
           2,
           SupportDBUrlType.ORACLEWITHSIDDEFAULTURL.getLanguage(),
           SupportDBUrlType.ORACLEWITHSIDDEFAULTURL.getLanguage(),
           PatternToExcelEnum.OracleRegexp), //$NON-NLS-1$
    SQLSERVER(
              3,
              SupportDBUrlType.MSSQLDEFAULTURL.getLanguage(),
              SupportDBUrlType.MSSQLDEFAULTURL.getLanguage(),
              PatternToExcelEnum.SQLServerRegexp), //$NON-NLS-1$
    DB2(
        4,
        SupportDBUrlType.DB2DEFAULTURL.getLanguage(),
        SupportDBUrlType.DB2DEFAULTURL.getLanguage(),
        PatternToExcelEnum.DB2Regexp), //$NON-NLS-1$
    POSTGRESQL(
               5,
               SupportDBUrlType.POSTGRESQLEFAULTURL.getLanguage(),
               SupportDBUrlType.POSTGRESQLEFAULTURL.getLanguage(),
               PatternToExcelEnum.PostgreSQLRegexp), //$NON-NLS-1$
    SYBASE(
           6,
           SupportDBUrlType.SYBASEDEFAULTURL.getLanguage(),
           SupportDBUrlType.SYBASEDEFAULTURL.getLanguage(),
           PatternToExcelEnum.SybaseRegexp), //$NON-NLS-1$
    INGRES(
           7,
           SupportDBUrlType.INGRESDEFAULTURL.getLanguage(),
           SupportDBUrlType.INGRESDEFAULTURL.getLanguage(),
           PatternToExcelEnum.IngresRegexp),
    INFORMIX(
             8,
             SupportDBUrlType.INFORMIXDEFAULTURL.getLanguage(),
             SupportDBUrlType.INFORMIXDEFAULTURL.getLanguage(),
             PatternToExcelEnum.InformixRegexp),
    MDM(12, SupportDBUrlType.MDM.getLanguage(), SupportDBUrlType.MDM.getLanguage(), PatternToExcelEnum.MDMRegexp),
    // SQLITE3(
    // 9,
    // SupportDBUrlType.SQLITE3DEFAULTURL.getLanguage(),
    // SupportDBUrlType.SQLITE3DEFAULTURL.getLanguage(),
    // PatternToExcelEnum.SQLite3Regexp),
    // MOD scorreia 2008-12-16 removed "generic jdbc" because its meaning is the same as ALL_DATABASE_TYPE
    TERADATA(
             10,
             SupportDBUrlType.TERADATADEFAULTURL.getLanguage(),
             SupportDBUrlType.TERADATADEFAULTURL.getLanguage(),
             PatternToExcelEnum.Teradata),
    JAVA(
         11,
         SupportDBUrlType.JAVADEFAULTURL.getLanguage(),
         SupportDBUrlType.JAVADEFAULTURL.getLanguage(),
         PatternToExcelEnum.JavaRegexp),
    ACCESS(12, SupportDBUrlType.ACCESS.getLanguage(), SupportDBUrlType.ACCESS.getLanguage(), PatternToExcelEnum.Access),
    AS400(
          12,
          SupportDBUrlType.AS400DEFAULTURL.getLanguage(),
          SupportDBUrlType.AS400DEFAULTURL.getLanguage(),
          PatternToExcelEnum.AS400);

    private int index;

    private String literal;

    private String name;

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

    public String getName() {
        return name;
    }

    PatternLanguageType(int index, String name, String literal, PatternToExcelEnum excelEnum) {
        this.index = index;
        this.name = name;
        this.literal = literal;
        this.excelEnum = excelEnum;
    }

    public static String findLanguageByName(String name) {

        for (PatternLanguageType oneType : values()) {
            if (name != null && name.equalsIgnoreCase(oneType.getName())) {
                return oneType.getLiteral();
            }
        }

        return null;
    }

    public static String findNameByLanguage(String language) {
        for (PatternLanguageType oneType : values()) {
            if (language != null && language.equalsIgnoreCase(oneType.getLiteral())) {
                return oneType.getName();
            }
        }

        return "Unrecognized";
    }

    public static String[] getAllLanguageTypes() {
        Set<String> set = new HashSet<String>();

        for (PatternLanguageType oneType : values()) {
            set.add(oneType.getName());
        }
        // MOD xqliu 2009-11-05 bug 9652
        // set.remove(SupportDBUrlType.JAVADEFAULTURL.getLanguage());
        // ~

        return set.toArray(new String[set.size()]);
    }

    /**
     * DOC yyi 2009-09-28 Feature: 9289.
     * 
     * @param hasJava set contains Java or not.
     * @return
     */
    public static String[] getAllLanguageTypes(boolean hasJava) {
        Set<String> set = new HashSet<String>();

        for (PatternLanguageType oneType : values()) {
            set.add(oneType.getName());
        }

        if (!hasJava)
            set.remove(SupportDBUrlType.JAVADEFAULTURL.getLanguage());

        return set.toArray(new String[set.size()]);
    }
}
