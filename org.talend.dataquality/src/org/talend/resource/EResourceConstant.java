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
package org.talend.resource;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public enum EResourceConstant {

    DATA_PROFILING(0, "TDQ_Data Profiling"),
    LIBRARIES(1, "TDQ_Libraries"),
    METADATA(2, "TDQ_Metadata"),
    ANALYSIS(3, "Analyses"),
    REPORTS(4, "Reports"),
    EXCHANGE(5, "Exchange"),
    INDICATORS(6, "Indicators"),
    JRXML_TEMPLATE(7, "JRXML Template"),
    PATTERNS(8, "Patterns"),
    RULES(9, "Rules"),
    SOURCE_FILES(10, "Source Files"),
    USER_DEFINED_INDICATORS(11, "User Defined Indicators"),
    PATTERN_REGEX(12, "Regex"),
    PATTERN_SQL(13, "SQL"),
    RULES_SQL(14, "SQL"),
    DB_CONNECTIONS(15, "DB Connections"),
    REPORTING_DB(16, "TDQ_reporting_db");

    private int index;

    private String name;

    EResourceConstant(int index, String name) {
        this.index = index;
        this.name = name;
    }

    /**
     * Getter for index.
     * 
     * @return the index
     */
    public int getIndex() {
        return index;
    }

    /**
     * Getter for name.
     * 
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * DOC bZhou Comment method "getNameByIndex".
     * 
     * @param index
     * @return
     */
    public String getNameByIndex(int index) {
        for (EResourceConstant constant : values()) {
            if (constant.getIndex() == index) {
                return constant.getName();
            }
        }

        return null;
    }

    /**
     * DOC bZhou Comment method "getIndexByName".
     * 
     * @param name
     * @return
     */
    public Integer getIndexByName(String name) {
        for (EResourceConstant constant : values()) {
            if (constant.getName().equals(name)) {
                return constant.getIndex();
            }
        }

        return null;
    }
}
