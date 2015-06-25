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
package org.talend.datascience.common.inference.semantic;

import java.util.HashMap;
import java.util.Map;

import org.talend.dataquality.semantic.recognizer.CategoryFrequency;

public class SemanticType {

    private Map<CategoryFrequency, Long> categoryToCount = new HashMap<CategoryFrequency, Long>();

    public String getSuggestedCategory() {
        long max = 0;
        String electedCategory = "UNKNOWN"; // Unknown by default
        for (Map.Entry<CategoryFrequency, Long> entry : categoryToCount.entrySet()) {
            if (entry.getValue() > max) {
                max = entry.getValue();
                electedCategory = entry.getKey().getCategoryId();
            }
        }
        return electedCategory;
    }

    public void increment(CategoryFrequency category, long count) {
        if (!categoryToCount.containsKey(category)) {
            categoryToCount.put(category, count);
        } else {
            categoryToCount.put(category, categoryToCount.get(category) + count);
        }
    }

}
