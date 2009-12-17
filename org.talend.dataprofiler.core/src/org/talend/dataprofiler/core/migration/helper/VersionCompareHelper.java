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
package org.talend.dataprofiler.core.migration.helper;

import org.talend.utils.ProductVersion;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public final class VersionCompareHelper {

    private VersionCompareHelper() {

    }

    /**
     * DOC bZhou Comment method "isLower".
     * 
     * @param v1
     * @param v2
     * @return
     */
    public static boolean isLower(ProductVersion v1, ProductVersion v2) {
        assert v1 != null;
        assert v2 != null;

        return v1.compareTo(v2) < 0;
    }

    /**
     * DOC bZhou Comment method "isHigher".
     * 
     * @param v1
     * @param v2
     * @return
     */
    public static boolean isHigher(ProductVersion v1, ProductVersion v2) {
        assert v1 != null;
        assert v2 != null;

        return v1.compareTo(v2) > 0;
    }

    /**
     * DOC bZhou Comment method "isEqual".
     * 
     * @param v1
     * @param v2
     * @return
     */
    public static boolean isEqual(ProductVersion v1, ProductVersion v2) {
        assert v1 != null;
        assert v2 != null;

        return v1.compareTo(v2) == 0;
    }
}
