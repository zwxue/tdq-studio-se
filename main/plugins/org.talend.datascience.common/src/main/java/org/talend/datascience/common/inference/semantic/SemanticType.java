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

public class SemanticType {

    private Map<String, Long> semanticNameToCount = new HashMap<String, Long>();

    public void putSemanticNameToCount(String semanticName, Long count) {
        semanticNameToCount.put(semanticName, count);
    }

    public Map<String, Long> getSemanticNameToCountMap() {
        return semanticNameToCount;
    }

    public Long getSemanticTypeCount(String semanticCategoryName) {
        return semanticNameToCount.get(semanticCategoryName);
    }

}
