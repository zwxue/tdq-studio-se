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
public enum BlockingKeyPostAlgorithmEnum {
    NON_ALGO(0, "-", "NON_ALGO"), //$NON-NLS-1$//$NON-NLS-2$
    USE_DEFAULT(1, "use default value", "useDefault"), //$NON-NLS-1$ //$NON-NLS-2$
    LEFT_CHAR(2, "add left position character", "add_Left_Char"), //$NON-NLS-1$ //$NON-NLS-2$
    RIGHT_CHAR(3, "add right position character", "add_Right_Char"); //$NON-NLS-1$ //$NON-NLS-2$

    private int index;

    private String value;

    private String componentValueName;

    BlockingKeyPostAlgorithmEnum(int index, String value, String componentValueName) {
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
     * Getter for componentValueName.
     *
     * @return the componentValueName
     */
    public String getComponentValueName() {
        return this.componentValueName;
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
        for (BlockingKeyPostAlgorithmEnum theType : values()) {
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
    public static BlockingKeyPostAlgorithmEnum getTypeBySavedValue(String value) {
        for (BlockingKeyPostAlgorithmEnum element : BlockingKeyPostAlgorithmEnum.values()) {
            if (element.getComponentValueName().equalsIgnoreCase(value)) {
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
    public static BlockingKeyPostAlgorithmEnum getTypeByValue(String value) {
        for (BlockingKeyPostAlgorithmEnum element : BlockingKeyPostAlgorithmEnum.values()) {
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
    public static BlockingKeyPostAlgorithmEnum getTypeByIndex(int index) {
        for (BlockingKeyPostAlgorithmEnum element : BlockingKeyPostAlgorithmEnum.values()) {
            if (element.getIndex() == index) {
                return element;
            }
        }

        return null;
    }

}
