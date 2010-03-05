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

import org.talend.dataquality.record.linkage.constant.AttributeMatcherType;

/**
 * @author scorreia
 * 
 * This class always return 1 whatever the given strings are. This class can be used when we don't care about the some
 * attributes for matching.
 */
public class DummyMatcher implements IAttributeMatcher {

    /* (non-Javadoc)
     * @see org.talend.dataquality.matching.attribute.IAttributeMatcher#getMatchingProba(java.lang.String, java.lang.String)
     */
    public double getMatchingWeight(String str1, String str2) {
        return 1;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.record.linkage.attribute.IAttributeMatcher#getMatchType()
     */
    public AttributeMatcherType getMatchType() {
        return AttributeMatcherType.dummy;
    }

}
