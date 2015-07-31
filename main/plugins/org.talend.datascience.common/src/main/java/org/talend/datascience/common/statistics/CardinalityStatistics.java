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

import com.clearspring.analytics.stream.cardinality.HyperLogLog;

public class CardinalityStatistics {

    private long count = 0l;

    private HyperLogLog hyperLogLog = null;

    public CardinalityStatistics() {
    }

    public HyperLogLog getHyperLogLog() {
        return hyperLogLog;
    }

    public long getDuplicateCount() {
        return count - getDistinctCount();
    }

    public long getCount() {
        return count;
    }

    public long getDistinctCount() {
        return hyperLogLog.cardinality();
    }

    public void incrementCount() {
        count += 1;
    }

    public void setHyperLogLog(HyperLogLog hyperLogLog2) {
        this.hyperLogLog = hyperLogLog2;

    }

}
