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

package org.talend.mydistance;

import org.talend.dataquality.record.linkage.attribute.AbstractAttributeMatcher;
import org.talend.dataquality.record.linkage.constant.AttributeMatcherType;

/**
 * @author scorreia
 * 
 * Example of Matching distance.
 */
public class MyDistance extends AbstractAttributeMatcher {

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.record.linkage.attribute.IAttributeMatcher#getMatchType()
     */
    public AttributeMatcherType getMatchType() {
        // a custom implementation should return this type AttributeMatcherType.custom
        return AttributeMatcherType.custom;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.record.linkage.attribute.IAttributeMatcher#getMatchingWeight(java.lang.String,
     * java.lang.String)
     */
    public double getWeight(String arg0, String arg1) {
        // Here goes the custom implementation of the matching distance between the two given strings.
        // the algorithm should return a value between 0 and 1.

        // in this example, we consider that 2 strings match if their first 4 characters are identical
        // the arguments are not null (the check for nullity is done by the caller)
        final int max = 4;
        int nbIdenticalChar = Math.min(max, Math.min(arg0.length(), arg1.length()));
        for (int c = 0; c < max; c++) {
            if (arg0.charAt(c) != arg1.charAt(c)) {
                nbIdenticalChar = c;
                break;
            }
        }
        return (max - nbIdenticalChar) / ((double) max);
    }

}
