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
package org.talend.dataquality.record.linkage.attribute;

import org.apache.commons.codec.language.DoubleMetaphone;
import org.talend.dataquality.record.linkage.constant.AttributeMatcherType;
import org.talend.dataquality.record.linkage.utils.StringComparisonUtil;


/**
 * DOC scorreia  class global comment. Detailled comment
 */
public class DoubleMetaphoneMatcher implements IAttributeMatcher {

    private final DoubleMetaphone algorithm = new DoubleMetaphone();

    private final int MAX = algorithm.getMaxCodeLen();

    /* (non-Javadoc)
     * @see org.talend.dataquality.record.linkage.attribute.IAttributeMatcher#getMatchType()
     */
    public AttributeMatcherType getMatchType() {
        return AttributeMatcherType.doubleMetaphone;
    }

    /* (non-Javadoc)
     * @see org.talend.dataquality.record.linkage.attribute.IAttributeMatcher#getMatchingWeight(java.lang.String, java.lang.String)
     */
    public double getMatchingWeight(String str1, String str2) {
        return StringComparisonUtil.difference(algorithm.encode(str1), algorithm.encode(str2)) / MAX;
    }

}
