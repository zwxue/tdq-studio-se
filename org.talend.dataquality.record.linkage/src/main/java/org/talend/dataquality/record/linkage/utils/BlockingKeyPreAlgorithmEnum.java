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
public enum BlockingKeyPreAlgorithmEnum {
    NON_ALGO(0, "-", "NON_ALGO",false), //$NON-NLS-1$//$NON-NLS-2$
    REMOVE_MARKS(1, "remove diacritical marks", "removeDiacriticalMarks",false), //$NON-NLS-1$ //$NON-NLS-2$
    REMOVE_MARKS_THEN_LOWER_CASE(2, "remove diacritical marks and lower case", "removeDMAndLowerCase",false), //$NON-NLS-1$ //$NON-NLS-2$
    REMOVE_MARKS_THEN_UPPER_CASE(3, "remove diacritical marks and upper case", "removeDMAndUpperCase",false), //$NON-NLS-1$ //$NON-NLS-2$
    LOWER_CASE(4, "lower case", "lowerCase",false), //$NON-NLS-1$ //$NON-NLS-2$
    UPPER_CASE(5, "upper case", "upperCase",false), //$NON-NLS-1$ //$NON-NLS-2$
    LEFT_CHAR(6, "add left position character", "add_Left_Char",true), //$NON-NLS-1$ //$NON-NLS-2$
    RIGHT_CHAR(7, "add right position character", "add_Right_Char", true); //$NON-NLS-1$ //$NON-NLS-2$

    private int index;

    private String value;

    private String componentValueName;

    private boolean isTakeParameter;

    BlockingKeyPreAlgorithmEnum(int index, String value, String componentValueName, boolean isTakeParameter) {
        this.index = index;
        this.value = value;
        this.componentValueName = componentValueName;
        this.isTakeParameter = isTakeParameter;
    }

    /**
     * Getter for componentValueName.
     *
     * @return the componentValueName
     */
    public String getComponentValueName() {
        return this.componentValueName;
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
     * Getter for isTakeParameter.
     * 
     * @return the isTakeParameter
     */
    public boolean isTakeParameter() {
        return this.isTakeParameter;
    }

    public static String[] getAllTypes() {
        List<String> list = new ArrayList<String>();
        for (BlockingKeyPreAlgorithmEnum theType : values()) {
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
    public static BlockingKeyPreAlgorithmEnum getTypeByValue(String value) {
        for (BlockingKeyPreAlgorithmEnum element : BlockingKeyPreAlgorithmEnum.values()) {
            if (element.getValue().equalsIgnoreCase(value)) {
                return element;
            }
        }

        return null;
    }

    /**
     *
     * get type of the value which in this Enum
     *
     * @param value
     * @return null can not find this index
     */
    public static BlockingKeyPreAlgorithmEnum getTypeBySavedValue(String value) {
        for (BlockingKeyPreAlgorithmEnum element : BlockingKeyPreAlgorithmEnum.values()) {
            if (element.getComponentValueName().equalsIgnoreCase(value)) {
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
    public static BlockingKeyPreAlgorithmEnum getTypeByIndex(int index) {
        for (BlockingKeyPreAlgorithmEnum element : BlockingKeyPreAlgorithmEnum.values()) {
            if (element.getIndex() == index) {
                return element;
            }
        }

        return null;
    }

}
