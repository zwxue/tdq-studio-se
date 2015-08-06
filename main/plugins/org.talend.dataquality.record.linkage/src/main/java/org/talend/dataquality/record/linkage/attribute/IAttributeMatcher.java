// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataquality.record.linkage.attribute;

import org.talend.dataquality.record.linkage.constant.AttributeMatcherType;

/**
 * DOC scorreia class global comment. Detailled comment
 */
public interface IAttributeMatcher {

    /**
     * Method "getMatchingWeight".
     * 
     * @param str1 a first string (must not be null)
     * @param str2 a second string (must not be null)
     * @return the probability that the two strings match (should return a value between 0 and 1.
     */
    // Note: this is not a mathematical probability (the sum won't yield 1)
    double getMatchingWeight(String str1, String str2);

    /**
     * Method "getMatchType".
     * 
     * @return the matching type.
     */
    AttributeMatcherType getMatchType();
    // TODO handle other types of data
}
