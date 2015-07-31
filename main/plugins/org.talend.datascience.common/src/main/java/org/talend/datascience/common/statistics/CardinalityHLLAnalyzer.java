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

import com.clearspring.analytics.stream.cardinality.HyperLogLog;

/**
 * Using hyperloglog estimating cardianlities (distinct count)
 * 
 * @author zhao
 *
 */
public class CardinalityHLLAnalyzer implements Analyzer<CardinalityStatistics> {

    private  ResizableList<CardinalityStatistics> cardinalityStatistics = null;

    int rsd = 20; //relative standard deviation
    @Override
    public void init() {
        cardinalityStatistics = new ResizableList<>(CardinalityStatistics.class);
    }
    
    public void setRelativeStandardDeviation(int rsd){
        this.rsd = rsd;
    }

    @Override
    public boolean analyze(String... record) {
        if (record == null) {
            return true;
        }
        cardinalityStatistics.resize(record.length);
        for (int i = 0; i < record.length; i++) {
            final CardinalityStatistics cardStats = cardinalityStatistics.get(i);
            if(cardStats.getHyperLogLog()==null){
                cardStats.setHyperLogLog(new HyperLogLog(rsd));
            }
            cardStats.getHyperLogLog().offer(record[i]);
            cardStats.incrementCount();
        }
        return true;
    }

    @Override
    public void end() {
    }

    @Override
    public List<CardinalityStatistics> getResult() {
        return cardinalityStatistics;
    }

}
