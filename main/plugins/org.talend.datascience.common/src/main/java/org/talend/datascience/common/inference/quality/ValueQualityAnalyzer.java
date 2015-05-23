package org.talend.datascience.common.inference.quality;

import java.util.List;

import org.talend.datascience.common.inference.Analyzer;
import org.talend.datascience.common.inference.ResizableList;
import org.talend.datascience.common.inference.type.DataType;
import org.talend.datascience.common.inference.type.TypeInferenceUtils;

public class ValueQualityAnalyzer implements Analyzer<ValueQuality> {

    private final DataType.Type[] types;

    private final ResizableList<ValueQuality> results = new ResizableList<ValueQuality>(ValueQuality.class);

    public ValueQualityAnalyzer(DataType.Type... types) {
        this.types = types;
    }

    public void init() {
        // Nothing to do.
    }

    public boolean analyze(String... record) {
        results.resize(record.length);
        for (int i = 0; i < record.length; i++) {
            final String value = record[i];
            final ValueQuality valueQuality = results.get(i);
            if (TypeInferenceUtils.isEmpty(value)) {
                valueQuality.incrementEmpty();
            } else {
                switch (types[i]) {
                case BOOLEAN:
                    if (TypeInferenceUtils.isBoolean(value)) {
                        valueQuality.incrementValid();
                    } else {
                        valueQuality.incrementInvalid();
                    }
                    break;
                case CHAR:
                    if (TypeInferenceUtils.isChar(value)) {
                        valueQuality.incrementValid();
                    } else {
                        valueQuality.incrementInvalid();
                    }
                    break;
                case INTEGER:
                    if (TypeInferenceUtils.isInteger(value)) {
                        valueQuality.incrementValid();
                    } else {
                        valueQuality.incrementInvalid();
                    }
                    break;
                case DOUBLE:
                    if (TypeInferenceUtils.isDouble(value)) {
                        valueQuality.incrementValid();
                    } else {
                        valueQuality.incrementInvalid();
                    }
                    break;
                case STRING:
                    // Everything can be a string
                    valueQuality.incrementValid();
                    break;
                case DATE:
                    if (TypeInferenceUtils.isDate(value)) {
                        valueQuality.incrementValid();
                    } else {
                        valueQuality.incrementInvalid();
                    }
                    break;
                }
            }
        }
        return true;
    }

    public void end() {
        // Nothing to do.
    }

    public List<ValueQuality> getResult() {
        return results;
    }
}
