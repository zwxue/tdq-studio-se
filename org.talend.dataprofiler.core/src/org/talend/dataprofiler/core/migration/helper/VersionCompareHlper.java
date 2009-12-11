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
public final class VersionCompareHlper {

    private VersionCompareHlper() {

    }

    /**
     * DOC bZhou Comment method "isLower".
     * 
     * @param v1
     * @param v2
     * @return
     */
    public static boolean isLower(ProductVersion v1, ProductVersion v2) {
        if (v1 == null) {
            return true;
        }

        if (v2 == null) {
            return false;
        }

        return v1.compareTo(v2) < 0;
    }
}
