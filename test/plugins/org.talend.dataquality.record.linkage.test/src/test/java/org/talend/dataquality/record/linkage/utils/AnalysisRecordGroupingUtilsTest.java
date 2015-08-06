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
package org.talend.dataquality.record.linkage.utils;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;
import org.talend.dataquality.record.linkage.constant.AttributeMatcherType;
import org.talend.dataquality.record.linkage.grouping.IRecordGrouping;

/**
 * created by zshen on Nov 19, 2013 Detailled comment
 * 
 */
public class AnalysisRecordGroupingUtilsTest {

    /**
     * Test method for
     * {@link org.talend.dataquality.record.linkage.utils.AnalysisRecordGroupingUtils#getMatchKeyMap(java.lang.String, java.lang.String, java.lang.String, int, java.util.Map, double, java.lang.String, java.lang.String)}
     * .
     */
    @Test
    public void testGetMatchKeyMap() {
        String column = "col1"; //$NON-NLS-1$
        String algoType = AttributeMatcherType.CUSTOM.name();
        String algoParameter = "myMatcher.jar||com.matcher.MyMatcher"; //$NON-NLS-1$
        int confidentWeight = 5;
        Map<String, String> columnIndexMap = new HashMap<String, String>();
        columnIndexMap.put(column, "0"); //$NON-NLS-1$
        double matchInterval = 0.5;
        String attributeName = "attribute1"; //$NON-NLS-1$
        String handleNull = "nullMatchNone"; //$NON-NLS-1$

        Map<String, String> matchKeyMap = AnalysisRecordGroupingUtils.getMatchKeyMap(column, algoType, algoParameter,
                confidentWeight, columnIndexMap, matchInterval, attributeName, handleNull);
        judgeMatchMapResult(matchKeyMap);
    }

    /**
     * DOC zshen Comment method "judgeMatchMapResult".
     * 
     * @param matchKeyMap
     */
    private void judgeMatchMapResult(Map<String, String> matchKeyMap) {
        Assert.assertTrue(matchKeyMap.size() == 7);
        Assert.assertNotNull(matchKeyMap.get(IRecordGrouping.COLUMN_IDX));
        Assert.assertNotNull(matchKeyMap.get(IRecordGrouping.MATCHING_TYPE));
        Assert.assertNotNull(matchKeyMap.get(IRecordGrouping.CUSTOMER_MATCH_CLASS));
        Assert.assertNotNull(matchKeyMap.get(IRecordGrouping.CONFIDENCE_WEIGHT));
        Assert.assertNotNull(matchKeyMap.get(IRecordGrouping.RECORD_MATCH_THRESHOLD));
        Assert.assertNotNull(matchKeyMap.get(IRecordGrouping.ATTRIBUTE_NAME));
        Assert.assertNotNull(matchKeyMap.get(IRecordGrouping.HANDLE_NULL));
    }

}
