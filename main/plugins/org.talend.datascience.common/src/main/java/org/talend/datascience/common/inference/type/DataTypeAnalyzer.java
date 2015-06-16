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

import java.util.List;

import org.talend.datascience.common.inference.Analyzer;
import org.talend.datascience.common.inference.ResizableList;

/**
 * Type inference executor which provide several methods computing the types.<br>
 * The suggested usage of this class is the following call sequence:<br>
 * 1. {{@link #init()}, called once.<br>
 * 2. {{@link Analyzer#analyze(String...)} , called as many iterations as required.<br>
 * 3. {{@link #getResult()} , called once.<br>
 * 
 * <b>Important note:</b> This class is <b>NOT</b> thread safe.
 *
 */
public class DataTypeAnalyzer implements Analyzer<DataType> {

    private final ResizableList<DataType> dataTypes = new ResizableList<>(DataType.class);

    private static DataType.Type execute(String value) {
        if (TypeInferenceUtils.isEmpty(value)) {
            // 1. detect empty
            return DataType.Type.EMPTY;
        } else if (TypeInferenceUtils.isBoolean(value)) {
            // 1. detect boolean
            return DataType.Type.BOOLEAN;
        } else if (TypeInferenceUtils.isChar(value)) {
            // 2. detect char
            return DataType.Type.CHAR;
        } else if (TypeInferenceUtils.isInteger(value)) {
            // 3. detect integer
            return DataType.Type.INTEGER;
        } else if (TypeInferenceUtils.isDouble(value)) {
            // 4. detect double
            return DataType.Type.DOUBLE;
        } else if (TypeInferenceUtils.isAlphString(value)) {
            // 5. detect alph string
            return DataType.Type.STRING;
        } else if (TypeInferenceUtils.isDate(value)) {
            // 6. detect date
            return DataType.Type.DATE;
        }
        // will return string when no matching
        return DataType.Type.STRING;
    }

    public void init() {
        // Nothing to do.
    }

    /**
     * Inferring types record by record.
     *
     * @param record for which the data type is guessed.
     * @return true if inferred successfully, false otherwise.
     */
    public boolean analyze(String... record) {
        if (record == null) {
            return true;
        }
        dataTypes.resize(record.length);
        for (int i = 0; i < record.length; i++) {
            final DataType dataType = dataTypes.get(i);
            dataType.increment(execute(record[i]));
        }
        return true;
    }

    public void end() {
        // Nothing to do.
    }

    /**
     * Get the inferring result, this method should be called once and only once after {
     * {@link Analyzer#analyze(String...)} method.
     * 
     * @return A map for <b>each</b> column. Each map contains the type occurrence count.
     */
    public List<DataType> getResult() {
        return dataTypes;
    }
}
