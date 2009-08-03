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
package org.talend.dataprofiler.core.helper;

import org.eclipse.emf.common.util.EList;
import org.talend.dataquality.indicators.definition.IndicatorCategory;
import org.talend.dataquality.indicators.definition.IndicatorDefinition;

/**
 * DOC xqliu class global comment. Detailled comment
 */
public final class UDIHelper {

    private static UDIHelper instance;

    private UDIHelper() {
    }

    public static UDIHelper getInstance() {
        if (instance == null) {
            instance = new UDIHelper();
        }
        return instance;
    }

    public IndicatorCategory getUDICategory(IndicatorDefinition indicatorDefinition) {
        EList<IndicatorCategory> categories = indicatorDefinition.getCategories();
        if (categories != null && categories.size() > 0) {
            return categories.get(0);
        }
        return null;
    }

}
