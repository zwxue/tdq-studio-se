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
package org.talend.datascience.common.inference.type;

import java.util.EnumMap;
import java.util.Map;

public class DataType {

    private Map<Type, Long> typeFrequencies = new EnumMap<Type, Long>(Type.class);

    public Map<Type, Long> getTypeFrequencies() {
        return typeFrequencies;
    }

    public Type getSuggestedType() {
        long max = 0;
        Type electedType = Type.STRING; // String by default
        for (Map.Entry<Type, Long> entry : typeFrequencies.entrySet()) {
            if (entry.getValue() > max) {
                max = entry.getValue();
                electedType = entry.getKey();
            }
        }
        return electedType;
    }

    public void increment(Type type) {
        if (!typeFrequencies.containsKey(type)) {
            typeFrequencies.put(type, 1l);
        } else {
            typeFrequencies.put(type, typeFrequencies.get(type) + 1);
        }
    }

    public enum Type {
        BOOLEAN,
        CHAR,
        INTEGER,
        DOUBLE,
        STRING,
        DATE
    }
}
