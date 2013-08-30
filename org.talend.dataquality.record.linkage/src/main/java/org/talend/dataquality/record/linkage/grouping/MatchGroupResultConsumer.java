// ============================================================================
//
// Copyright (C) 2006-2013 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataquality.record.linkage.grouping;

import java.util.ArrayList;
import java.util.List;

/**
 * created by zhao on Aug 20, 2013 Detailled comment
 * 
 */
public abstract class MatchGroupResultConsumer {

    // save the match result
    protected List<Object[]> matchResult = null;

    /**
     * 
     * Handle a row from match grouping consumer
     * 
     * @param row
     */
    public abstract void handle(Object row);

    public void addOneRowOfResult(Object rowResult) {
        if (matchResult == null) {
            matchResult = new ArrayList<Object[]>();
        }
        if (rowResult instanceof String[]) {
            matchResult.add((String[]) rowResult);
        }
    }

    public List<Object[]> getFullMatchResult() {
        return matchResult;
    }
}
