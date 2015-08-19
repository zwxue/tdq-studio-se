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

/**
 * 
 * Data type bean hold type to frequency and type to value maps.
 */
public class DataType {

    private Map<Type, Long> typeFrequencies = new EnumMap<Type, Long>(Type.class);

    private Map<Type, List<String>> type2Values = new EnumMap<Type, List<String>>(Type.class);

    public Map<Type, Long> getTypeFrequencies() {
        return typeFrequencies;
    }

    /**
     * The default method for get suggested type with the Type.DOUBLE decision threshold equal 0.5
     * 
     * @return type suggested by system automatically given frequencies.
     */
    public Type getSuggestedType() {
        return getSuggestedType(0.5);
    }

    /**
     * 
     * DOC fji Comment method "getSuggestedType".
     * 
     * @param threshold the Type.DOUBLE decision threshold for a integer/double mixed Numeric column: if the ratio of
     * doubles and the sum of integers, doubles is greater than a given threshold, then suggest column Type.DOUBLE,
     * otherwise Type.INTEGER. So, if the threshold equal 0.5 means that we keep only the most frequent data type, and a
     * lower threshold means that a column with some double values will be considered as double type. E.g. for the input
     * column: "1.2","3.5","2","6","7", if threshold=0.5 return Type.INTEGER; if threshold=0.1 return Type.DOUBLE.
     * 
     * @return enum Type : BOOLEAN, INTEGER, DOUBLE, STRING, DATE
     * <p>
     * Note: there is no Type.EMPTY returned, even Type.EMPTY is the most frequent, the second most frequent will be
     * returned. E.g. for the input column: "","","","","1.2" return Type.DOUBLE. If a column is all empty, will return
     * Type.STRING
     */
    public Type getSuggestedType(double threshold) {
        long max = 0;
        long nbDouble = typeFrequencies.get(Type.DOUBLE) == null ? 0 : typeFrequencies.get(Type.DOUBLE);
        long nbInteger = typeFrequencies.get(Type.INTEGER) == null ? 0 : typeFrequencies.get(Type.INTEGER);
        Type electedType = Type.STRING; // String by default
        for (Map.Entry<Type, Long> entry : typeFrequencies.entrySet()) {
            if (Type.EMPTY.equals(entry.getKey())) {
                continue;
            }
            if (entry.getValue() > max) {
                max = entry.getValue();
                electedType = entry.getKey();
            }
        }
        // column contains mostly numeric values (doubles + integers) and the ratio of double values is greater than a
        // given threshold. For more informations, see https://jira.talendforge.org/browse/TDQ-10830
        if (((nbDouble + nbInteger) > max) && (((double) nbDouble) / (nbInteger + nbDouble) >= threshold)) {
            return Type.DOUBLE;
        }
        return electedType;
    }

    /**
     * Increment by 1 from frequency table.
     * @param type the type from which the frequencies incremnt.
     */
    public void increment(Type type) {
        if (!typeFrequencies.containsKey(type)) {
            typeFrequencies.put(type, 1l);
        } else {
            typeFrequencies.put(type, typeFrequencies.get(type) + 1);
        }
    }

    /**
     * Increment the frequency of given type and value simultaneously .
     * @param type the given type.
     * @param value value to be appended to map given type
     */
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
        INTEGER,
        DOUBLE,
        STRING,
        DATE,
        EMPTY
    }
}
