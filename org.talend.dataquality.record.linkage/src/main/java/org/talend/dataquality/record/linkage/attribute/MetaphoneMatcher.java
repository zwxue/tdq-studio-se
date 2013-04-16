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
public class MetaphoneMatcher extends AbstractAttributeMatcher {

    private final Metaphone algorithm = new Metaphone();

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.record.linkage.attribute.IAttributeMatcher#getMatchType()
     */
    @Override
    public AttributeMatcherType getMatchType() {
        return AttributeMatcherType.metaphone;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.record.linkage.attribute.AbstractAttributeMatcher#getWeight(java.lang.String,
     * java.lang.String)
     */
    @Override
    public double getWeight(String str1, String str2) {
        String code1 = algorithm.encode(str1);
        String code2 = algorithm.encode(str2);
        int maxLengh = Math.max(code1.length(), code2.length());
        if (maxLengh == 0) {
            return 0d;
        }
        algorithm.setMaxCodeLen(maxLengh);
        return StringComparisonUtil.difference(code1, code2) / (double) algorithm.getMaxCodeLen();
    }
}
