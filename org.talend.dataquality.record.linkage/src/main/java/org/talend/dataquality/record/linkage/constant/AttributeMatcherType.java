// ============================================================================
//
// Copyright (C) 2006-2010 Talend Inc. - www.talend.com
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
    exact("Exact"),
    exactIgnoreCase("Exact - ignore case"),
    soundex("Soundex"),
    soundexFR("Soundex FR"),
    levenshtein("Levenshtein"),
    metaphone("Metaphone"),
    doubleMetaphone("Double Metaphone"),
    jaro("Jaro"),
    jaroWinkler("Jaro-Winkler"),
    qgrams("q-grams"),
    dummy("Dummy");

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

}
