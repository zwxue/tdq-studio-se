// ============================================================================
//
// Copyright (C) 2006-2018 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dq.analysis;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.talend.core.model.metadata.builder.connection.ConnectionFactory;
import org.talend.core.model.metadata.builder.connection.MetadataColumn;
import org.talend.dataquality.record.linkage.constant.AttributeMatcherType;
import org.talend.dataquality.record.linkage.grouping.IRecordGrouping;

/**
 * created by zshen on Nov 19, 2013 Detailled comment
 * 
 */
public class AnalysisRecordGroupingUtilsTest {

    /**
     * Test method for
     * {@link org.talend.dq.analysis.AnalysisRecordGroupingUtils#getMatchKeyMap(java.lang.String, java.lang.String, java.lang.String, int, java.util.Map, double, java.lang.String, java.lang.String)}
     * .
     */
    @Test
    public void testGetMatchKeyMap() {
        String column = "col1"; //$NON-NLS-1$
        String algoType = AttributeMatcherType.CUSTOM.name();
        String algoParameter = "myMatcher.jar||com.matcher.MyMatcher"; //$NON-NLS-1$
        int confidentWeight = 5;
        Map<MetadataColumn, String> columnIndexMap = new HashMap<MetadataColumn, String>();
        MetadataColumn metaColumn = ConnectionFactory.eINSTANCE.createMetadataColumn();
        metaColumn.setName(column);
        columnIndexMap.put(metaColumn, "0"); //$NON-NLS-1$
        double matchInterval = 0.5;
        double attrThreshold = 1.0d;
        String attributeName = "attribute1"; //$NON-NLS-1$
        String handleNull = "nullMatchNone"; //$NON-NLS-1$

        Map<String, String> matchKeyMap = AnalysisRecordGroupingUtils.getMatchKeyMap(column, algoType, algoParameter,
                confidentWeight, attrThreshold, columnIndexMap, matchInterval, attributeName, handleNull);
        judgeMatchMapResult(matchKeyMap);
    }

    /**
     * DOC zshen Comment method "judgeMatchMapResult".
     * 
     * @param matchKeyMap
     */
    private void judgeMatchMapResult(Map<String, String> matchKeyMap) {
        // this line will check the size of return map, any change will cause fail
        assertNotNull(matchKeyMap.size() == 8);
        assertNotNull(matchKeyMap.get(IRecordGrouping.COLUMN_IDX));
        assertNotNull(matchKeyMap.get(IRecordGrouping.MATCHING_TYPE));
        assertNotNull(matchKeyMap.get(IRecordGrouping.CUSTOMER_MATCH_CLASS));
        assertNotNull(matchKeyMap.get(IRecordGrouping.CONFIDENCE_WEIGHT));
        assertNotNull(matchKeyMap.get(IRecordGrouping.RECORD_MATCH_THRESHOLD));
        assertNotNull(matchKeyMap.get(IRecordGrouping.ATTRIBUTE_NAME));
        assertNotNull(matchKeyMap.get(IRecordGrouping.HANDLE_NULL));
        assertNull(matchKeyMap.get(IRecordGrouping.JAR_PATH));
    }

}
