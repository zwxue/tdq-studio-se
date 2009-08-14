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
package org.talend.dataquality.helpers;

import org.talend.dataquality.indicators.definition.IndicatorCategory;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC xqliu class global comment. Detailled comment
 */
public final class IndicatorCategoryHelper {

    private IndicatorCategoryHelper() {
    }

    public static final String USER_DEFINED_COUNT_CATEGORY = "_b5F7QHqTEd67hM2eKD3QgQ";

    public static final String USER_DEFINED_FREQUENCY_CATEGORY = "_Frd2gHsXEd63r-VLO3_0OQ";

    public static final String USER_DEFINED_MATCH_CATEGORY = "_Ba7OYXsXEd63r-VLO3_0OQ";
    
    public static final String USER_DEFINED_COMPARISON_CATEGORY = "_yQJQ0HsXEd63r-VLO3_0OQ";

    public static final String USER_DEFINED_NOMINAL_CORRELATION_CATEGORY = "_1mapEHsXEd63r-VLO3_0OQ";

    public static final String USER_DEFINED_INTERVAL_CORRELATION_CATEGORY = "_6giZsHsXEd63r-VLO3_0OQ";

    public static final String USER_DEFINED_TIME_CORRELATION_CATEGORY = "_-fpTkHsXEd63r-VLO3_0OQ";

    public static final boolean isCount(IndicatorCategory indicatorCategory) {
        if (indicatorCategory != null) {
            return USER_DEFINED_COUNT_CATEGORY.equals(getUuid(indicatorCategory));
        }
        return false;
    }

    public static final boolean isFrequency(IndicatorCategory indicatorCategory) {
        if (indicatorCategory != null) {
            return USER_DEFINED_FREQUENCY_CATEGORY.equals(getUuid(indicatorCategory));
        }
        return false;
    }

    public static final boolean isMatching(IndicatorCategory indicatorCategory) {
        if (indicatorCategory != null) {
            return USER_DEFINED_MATCH_CATEGORY.equals(getUuid(indicatorCategory));
        }
        return false;
    }
    
    private static String getUuid(ModelElement element) {
        if (element != null) {
            return element.eResource().getURIFragment(element);
        }
        return null;
    }
}
