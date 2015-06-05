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

import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DataType {

    private Map<Type, Long> typeFrequencies = new EnumMap<Type, Long>(Type.class);

    private Map<Type, List<String>> type2Values = new EnumMap<Type, List<String>>(Type.class);

    private Type userDefinedType = null;

    private List<String> invalidValues = Collections.synchronizedList(new LinkedList<String>());

    public Map<Type, Long> getTypeFrequencies() {
        return typeFrequencies;
    }

    public void setUserDefinedType(Type actualDataType) {
        this.userDefinedType = actualDataType;
    }

    /**
     * Get actual data type.
     * 
     * @return
     */
    public Type getUserDefinedType() {
        if (Type.EMPTY == userDefinedType) {
            return Type.STRING;
        }
        if(userDefinedType==null){
            userDefinedType = getSuggestedType();
        }
        return userDefinedType;
    }

    public long getValidCount() {
        userDefinedType = getUserDefinedType();
        if (Type.EMPTY == userDefinedType) {
            userDefinedType = Type.STRING;
        }
        if (typeFrequencies.containsKey(userDefinedType)) {
            return typeFrequencies.get(userDefinedType);
        }
        return 0;
    }

    public long getEmptyCount() {
        if (typeFrequencies.containsKey(Type.EMPTY)) {
            return typeFrequencies.get(Type.EMPTY);
        }
        return 0;
    }

    public long getInvalidCount() {
        long count = 0;
        for (long freq : typeFrequencies.values()) {
            count += freq;
        }
        return count - getValidCount() - getEmptyCount();
    }

    public List<String> getInvalidValues() {
        invalidValues.clear();
        userDefinedType = getUserDefinedType();
        for (Map.Entry<Type, List<String>> entry : type2Values.entrySet()) {
            if (entry.getKey() != userDefinedType && Type.EMPTY != entry.getKey()) {
                // Add them to invalid values
                invalidValues.addAll(entry.getValue());
            }
        }
        return invalidValues;
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
        if (Type.EMPTY == electedType) {
            return Type.STRING;
        }
        if (userDefinedType == null) {
            userDefinedType = electedType;
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

    public void increment(Type type, String value) {
        increment(type);
        // update type to values map
        if (!type2Values.containsKey(type)) {
            List<String> values = type2Values.get(type);
            if (values == null) {
                values = Collections.synchronizedList(new LinkedList<String>());
            }
            values.add(value);
            type2Values.put(type, values);
        } else {
            List<String> values = type2Values.get(type);
            values.add(value);
        }
    }

    public enum Type {
        BOOLEAN,
        CHAR,
        INTEGER,
        DOUBLE,
        STRING,
        DATE,
        EMPTY
    }
}
