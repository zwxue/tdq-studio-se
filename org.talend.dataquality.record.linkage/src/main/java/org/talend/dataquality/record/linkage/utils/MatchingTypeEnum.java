// ============================================================================
//
// Copyright (C) 2006-2013 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataquality.record.linkage.utils;

import java.util.ArrayList;
import java.util.List;


/**
 * created by zshen on Aug 1, 2013
 * Detailled comment
 *
 */
public enum MatchingTypeEnum {
    EXACT(0,"Exact"),
    EXACT_IGNORE_CASE(1,"Exact - ignore case"),
    SOUNDEX(2,"Soundex"),
    SOUNDEX_FR(3,"Soundex FR"),
    LEVENSHTEIN(4,"Levenshtein"),
    METAPHONE(5,"Metaphone"),
    DOUBLE_METAPHONE(6,"Double Metaphone"),
    JARO(7, "Jaro"),
    JARO_WINKLER(8, "Jaro-Winkler"),
    Q_GRAMS(9, "q-grams"),
    CUSTOM(10, "custom");

    private int index;

    private String value;

    MatchingTypeEnum(int index, String value) {
        this.index = index;
        this.value = value;
    }

    /**
     * Getter for index.
     *
     * @return the index
     */
    public int getIndex() {
        return this.index;
    }

    /**
     * Getter for value.
     *
     * @return the value
     */
    public String getValue() {
        return this.value;
    }

    public static String[] getAllTypes() {
        List<String> list = new ArrayList<String>();
        for (MatchingTypeEnum theType : values()) {
            list.add(theType.getValue());
        }
        return list.toArray(new String[list.size()]);
    }

    /**
     *
     * get type of the value which in this Enum
     *
     * @param value
     * @return null can not find this index
     */
    public static MatchingTypeEnum getTypeByValue(String value) {
        for (MatchingTypeEnum element : MatchingTypeEnum.values()) {
            if (element.getValue().equalsIgnoreCase(value)) {
                return element;
            }
        }

        return null;
    }

    /**
     *
     *
     * @param index
     * @return null can not find this index
     */
    public static MatchingTypeEnum getTypeByIndex(int index) {
        for (MatchingTypeEnum element : MatchingTypeEnum.values()) {
            if (element.getIndex() == index) {
                return element;
            }
        }

        return null;
    }
}
