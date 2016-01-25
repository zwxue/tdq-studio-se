// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
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

/**
 * @author scorreia
 * 
 * Enumeration of all available attribute matcher algorithms.
 */
public enum AttributeMatcherType {
    exact("Exact"), //$NON-NLS-1$
    exactIgnoreCase("Exact - ignore case"), //$NON-NLS-1$
    soundex("Soundex"), //$NON-NLS-1$
    soundexFR("Soundex FR"), //$NON-NLS-1$
    levenshtein("Levenshtein"), //$NON-NLS-1$
    metaphone("Metaphone"), //$NON-NLS-1$
    doubleMetaphone("Double Metaphone"), //$NON-NLS-1$
    jaro("Jaro"), //$NON-NLS-1$
    jaroWinkler("Jaro-Winkler"), //$NON-NLS-1$
    qgrams("q-grams"), //$NON-NLS-1$
    dummy("Dummy"), //$NON-NLS-1$
    custom("Custom"); //$NON-NLS-1$

    private final String label;

    AttributeMatcherType(String label) {
        this.label = label;
    }

    /**
     * Getter for label.
     * 
     * @return the label
     */
    public String getLabel() {
        return this.label;
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
}
