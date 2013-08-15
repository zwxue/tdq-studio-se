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
 * created by zshen on Aug 6, 2013
 * Detailled comment
 *
 */
public enum BlockingKeyAlgorithmEnum {

    FIRST_CHAR_EW(0, "first character of each word", "first_Char_EW"),
    FIRST_N_CHAR_EW(1, "N first characters of each word", "first_N_Char_EW"),
    FIRST_N_CHAR(2, "first N characters of the string", "first_N_Char"),
    LAST_N_CHAR(3, "last N characters of the string", "last_N_Char"),
    FIRST_N_CONSONANTS(4, "first N consonants of the string", "first_N_Consonants"),
    FIRST_N_VOWELS(5, "first N vowels of the string", "first_N_Vowels"),
    PICK_CHAR(6, "pick characters", "pick_Char"),
    SUBSTR(7, "substring(a,b)", "subStr"),
    SOUNDEX(8, "soundex code", "soundex"),
    METAPHONE(9, "metaphone code", "metaphone"),
    D_METAPHONE(10, "double-metaphone code", "doublemetaphone"),
    EXACT(11, "exact", "exact"),
    FINGERPRINTKEY(12, "fingerPrintKey", "fingerPrintKey"),
    NGRAMKEY(13, "nGramKey", "nGramKey"),
    COLOGNEPHONETIC(14, "colognePhonetic", "colognePhonetic");


    private int index;

    private String value;

    private String componentValueName;

    BlockingKeyAlgorithmEnum(int index, String value, String componentValueName) {
        this.index = index;
        this.value = value;
        this.componentValueName = componentValueName;
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

    /**
     * Getter for componentValueName.
     * 
     * @return the componentValueName
     */
    public String getComponentValueName() {
        return this.componentValueName;
    }

    public static String[] getAllTypes() {
        List<String> list = new ArrayList<String>();
        for (BlockingKeyAlgorithmEnum theType : values()) {
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
    public static BlockingKeyAlgorithmEnum getTypeByValue(String value) {
        for (BlockingKeyAlgorithmEnum element : BlockingKeyAlgorithmEnum.values()) {
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
    public static BlockingKeyAlgorithmEnum getTypeByIndex(int index) {
        for (BlockingKeyAlgorithmEnum element : BlockingKeyAlgorithmEnum.values()) {
            if (element.getIndex() == index) {
                return element;
            }
        }

        return null;
    }

}
