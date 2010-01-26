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
package org.talend.dataquality.record.linkage.attribute;

import org.apache.commons.codec.language.Metaphone;
import org.talend.dataquality.record.linkage.constant.AttributeMatcherType;
import org.talend.dataquality.record.linkage.utils.StringComparisonUtil;

/**
 * @author scorreia
 * 
 * Metaphone matcher. Uses Apache codec algorithm algorithms and computes a distance by comparing the number of
 * identical characters in the two algorithm codes.
 */
public class MetaphoneMatcher implements IAttributeMatcher {

    private final Metaphone algorithm = new Metaphone();

    private final int MAX = algorithm.getMaxCodeLen();

    /* (non-Javadoc)
     * @see org.talend.dataquality.record.linkage.attribute.IAttributeMatcher#getMatchType()
     */
    public AttributeMatcherType getMatchType() {
        return AttributeMatcherType.metaphone;
    }

    /* (non-Javadoc)
     * @see org.talend.dataquality.record.linkage.attribute.IAttributeMatcher#getMatchingWeight(java.lang.String, java.lang.String)
     */
    public double getMatchingWeight(String str1, String str2) {
        return StringComparisonUtil.difference(algorithm.encode(str1), algorithm.encode(str2)) / MAX;
    }


}
