// ============================================================================
//
// Copyright (C) 2006-2007 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.utils;

import org.eclipse.jface.preference.IPreferenceStore;
import org.talend.dataprofiler.core.CorePlugin;

/**
 * DOC qwei class global comment. Detailled comment
 */
public class PreferenceStoreUtil {

    public static IPreferenceStore getPreferenceStore() {
        return CorePlugin.getDefault().getPreferenceStore();
    }

    public static String getString(String str) {
        return getPreferenceStore().getString(str);
    }
}
