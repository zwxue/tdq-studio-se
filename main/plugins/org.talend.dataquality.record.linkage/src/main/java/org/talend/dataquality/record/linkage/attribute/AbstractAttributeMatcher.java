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

/**
 * Abstract matcher class for shared operations like blank string checking.
 */
public abstract class AbstractAttributeMatcher implements IAttributeMatcher {

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.record.linkage.attribute.IAttributeMatcher#getMatchingWeight(java.lang.String,
     * java.lang.String)
     */
    public double getMatchingWeight(String str1, String str2) {
        if ("".equals(str1)) { //$NON-NLS-1$
            if ("".equals(str2)) { //$NON-NLS-1$
                return 1.0;
            } else {
                return 0.0;
            }
        } else {
            if ("".equals(str2)) { //$NON-NLS-1$
                return 0.0;
            }
        }

        return getWeight(str1, str2);
    }

    /**
     * Calculate matching weight using specified matcher.
     * 
     * @param record1 the first string
     * @param record2 the secord string
     * @return result between 0 and 1
     */
    protected abstract double getWeight(String record1, String record2);

}
