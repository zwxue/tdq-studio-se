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

public class MeanValueAnalyzer implements Analyzer<SummaryStatistics> {

    private final ResizableList<SummaryStatistics> meanStatistics = new ResizableList<>(SummaryStatistics.class);

    @Override
    public void init() {
        meanStatistics.clear();
    }

    @Override
    public boolean analyze(String... record) {
        if (record == null) {
            return true;
        }
        meanStatistics.resize(record.length);
        for (int i = 0; i < record.length; i++) {
            final SummaryStatistics meanStats = meanStatistics.get(i);
            if (!TypeInferenceUtils.isNumber(record[i])) {
                // Filter out the non-number value
                continue;
            }
            double currentValue = Double.valueOf(record[i]);
            if (Double.isNaN(meanStats.getSum())) {
                meanStats.setSum(currentValue);
                meanStats.setCountOfNumber(1);
            }else{
                meanStats.setSum(currentValue+meanStats.getSum());
                meanStats.setCountOfNumber(meanStats.getCountOfNumber()+1);
            }
        }
        return true;
    }

    @Override
    public void end() {
        for(SummaryStatistics meanStas: meanStatistics){
            meanStas.setMean(meanStas.getSum()/meanStas.getCountOfNumber());
        }
    }

    @Override
    public List<SummaryStatistics> getResult() {
        return meanStatistics;
    }

}
