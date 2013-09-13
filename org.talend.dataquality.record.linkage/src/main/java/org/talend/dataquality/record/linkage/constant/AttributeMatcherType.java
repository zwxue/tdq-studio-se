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
package org.talend.dataquality.record.linkage.constant;

import java.util.ArrayList;
import java.util.List;

/**
 * @author scorreia
 *
 * Enumeration of all available attribute matcher algorithms.
 */
public enum AttributeMatcherType {
    exact(0, "Exact", "exact", false), //$NON-NLS-1$ //$NON-NLS-2$
    exactIgnoreCase(1, "Exact - ignore case", "exact_ignore_case", false), //$NON-NLS-1$ //$NON-NLS-2$
    soundex(2, "Soundex", "soundex", false), //$NON-NLS-1$ //$NON-NLS-2$
    soundexFR(3, "Soundex FR", "soundex_fr", false), //$NON-NLS-1$ //$NON-NLS-2$
    levenshtein(4, "Levenshtein", "levenshtein", false), //$NON-NLS-1$ //$NON-NLS-2$
    metaphone(5, "Metaphone", "metaphone", false), //$NON-NLS-1$ //$NON-NLS-2$
    doubleMetaphone(6, "Double Metaphone", "double_metaphone", false), //$NON-NLS-1$ //$NON-NLS-2$
    jaro(7, "Jaro", "JARO", false), //$NON-NLS-1$ //$NON-NLS-2$
    jaroWinkler(8, "Jaro-Winkler", "jaro_winkler", false), //$NON-NLS-1$ //$NON-NLS-2$
    qgrams(9, "q-grams", "q_grams", false), //$NON-NLS-1$ //$NON-NLS-2$
    dummy(10, "Dummy", "dummy", false), //$NON-NLS-1$ //$NON-NLS-2$
    custom(11, "Custom", "CUSTOM", true); //$NON-NLS-1$ //$NON-NLS-2$

    private final String label;

    private final String componentName;

    private final int index;

    private boolean isTakeParameter;

    AttributeMatcherType(int index, String label, String componentName, boolean isTakeParameter) {
        this.label = label;
        this.index = index;
        this.componentName = componentName;
        this.isTakeParameter = isTakeParameter;
    }

    /**
     * Getter for isTakeParameter.
     *
     * @return the isTakeParameter
     */
    public boolean isTakeParameter() {
        return this.isTakeParameter;
    }

    /**
     * Getter for componentName.
     *
     * @return the componentName
     */
    public String getComponentName() {
        return this.componentName;
    }

    /**
     * Getter for label.
     *
     * @return the label
     */
    public String getLabel() {
        return this.label;
    }

    /**
     * Getter for index.
     *
     * @return the index
     */
    public int getIndex() {
        return this.index;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return this.label;
    }

    /**
     * Method "get"
     *
     * @param label the label of the matcher
     * @return the matcher type given the label or null
     */
    public static AttributeMatcherType get(String label) {
        for (AttributeMatcherType type : AttributeMatcherType.values()) {
            if (type.getLabel().equalsIgnoreCase(label)) {
                return type;
            }
        }
        return null;
    }

    public static String[] getAllTypes() {
        List<String> list = new ArrayList<String>();
        for (AttributeMatcherType theType : AttributeMatcherType.values()) {
            list.add(theType.getLabel());
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
    public static AttributeMatcherType getTypeByValue(String value) {
        for (AttributeMatcherType element : AttributeMatcherType.values()) {
            if (element.getLabel().equalsIgnoreCase(value)) {
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
    public static AttributeMatcherType getTypeBySavedValue(String value) {
        for (AttributeMatcherType element : AttributeMatcherType.values()) {
            if (element.getComponentName().equalsIgnoreCase(value)) {
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
    public static AttributeMatcherType getTypeByIndex(int index) {
        for (AttributeMatcherType element : AttributeMatcherType.values()) {
            if (element.getIndex() == index) {
                return element;
            }
        }

        return null;
    }
}
