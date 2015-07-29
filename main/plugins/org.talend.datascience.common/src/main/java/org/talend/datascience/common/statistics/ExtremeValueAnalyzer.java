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
package org.talend.datascience.common.statistics;

import java.util.List;

import org.talend.datascience.common.inference.Analyzer;
import org.talend.datascience.common.inference.ResizableList;
import org.talend.datascience.common.inference.type.TypeInferenceUtils;

/**
 * Analyzer to analyze the minimum and maximum value given input data stream.<br>
 * <b>Important note:</b> This class is <b>NOT</b> thread safe.
 */
public abstract class ExtremeValueAnalyzer implements Analyzer<SummaryStatistics> {

    private final ResizableList<SummaryStatistics> extremetatistics = new ResizableList<>(SummaryStatistics.class);

    @Override
    public void init() {
    }

    @Override
    public boolean analyze(String... record) {
        if (record == null) {
            return true;
        }
        extremetatistics.resize(record.length);
        for (int i = 0; i < record.length; i++) {
            final SummaryStatistics minStats = extremetatistics.get(i);
            if (!TypeInferenceUtils.isNumber(record[i])) {
                // Filter out the non-number value
                continue;
            }
            replaceExtremeValue(minStats,record[i]);
        }
        return true;
    }
    protected abstract void replaceExtremeValue(SummaryStatistics currentExtremeStas,String currentValue); 

    public void end() {
    }

    @Override
    public List<SummaryStatistics> getResult() {
        return extremetatistics;
    }

}
