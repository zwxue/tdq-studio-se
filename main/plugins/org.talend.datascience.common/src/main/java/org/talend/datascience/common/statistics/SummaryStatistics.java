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

import java.util.Map;

/**
 * Summary statistics bean
 * 
 * @author mzhao
 *
 */
public class SummaryStatistics {

    private double min = Double.NaN, max = Double.NaN, mean = Double.NaN, sum = Double.NaN, variance = Double.NaN;

    private long duplicateCount, distinctCount,countOfNumber=0;

    private Map<String, Long> frequencyTable;

    
    public double getMin() {
        return min;
    }

    
    public void setMin(double min) {
        this.min = min;
    }

    
    public double getMax() {
        return max;
    }

    
    public void setMax(double max) {
        this.max = max;
    }

    
    public double getMean() {
        return mean;
    }

    
    public void setMean(double mean) {
        this.mean = mean;
    }

    
    public double getVariance() {
        return variance;
    }

    
    public void setVariance(double variance) {
        this.variance = variance;
    }

    
    public long getDuplicateCount() {
        return duplicateCount;
    }

    
    public void setDuplicateCount(long duplicateCount) {
        this.duplicateCount = duplicateCount;
    }

    
    public long getDistinctCount() {
        return distinctCount;
    }

    
    public void setDistinctCount(long distinctCount) {
        this.distinctCount = distinctCount;
    }
    
    public double getSum() {
        return sum;
    }
    
    public void setSum(double sum) {
        this.sum = sum;
    }

    public long getCountOfNumber() {
        return countOfNumber;
    }


    
    public void setCountOfNumber(long count) {
        this.countOfNumber = count;
    }

    
    
}
