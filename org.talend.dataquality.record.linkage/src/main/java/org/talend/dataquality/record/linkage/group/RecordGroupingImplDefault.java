// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataquality.record.linkage.group;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.talend.dataquality.record.linkage.attribute.IAttributeMatcher;
import org.talend.dataquality.record.linkage.constant.AttributeMatcherType;
import org.talend.dataquality.record.linkage.constant.RecordMatcherType;
import org.talend.dataquality.record.linkage.record.IRecordMatcher;
import org.talend.dataquality.record.linkage.record.RecordMatcherFactory;

/**
 * 
 * mzhao Default implementation for recording matching algorithm.
 */
public class RecordGroupingImplDefault implements IRecordGrouping {

    private static Logger log = Logger.getLogger(RecordGroupingImplDefault.class);

    private List<String[]> masterRecords = new ArrayList<String[]>();

    private List<String[]> duplicatedRecords = new ArrayList<String[]>();

    public static final float DEFAULT_THRESHOLD = 0.95f;

    private float acceptableThreshold = DEFAULT_THRESHOLD;

    // Output distance details or not.
    private boolean isOutputDistDetails = Boolean.FALSE;


    public void setAcceptableThreshold(float acceptableThreshold) {
        this.acceptableThreshold = acceptableThreshold;
    }

    public void setIsOutputDistDetails(boolean isOutputDistDetails) {
        this.isOutputDistDetails = isOutputDistDetails;
    }
    private IRecordMatcher recordMatcher = RecordMatcherFactory.createMatcher(RecordMatcherType.simpleVSRMatcher);

    private List<Map<String, String>> matchingColumns = new ArrayList<Map<String, String>>();


    public RecordGroupingImplDefault() {
    }

    public void doGroup(String[] inputRow) {
        // temporary array to store attributes to match
        String[] lookupDataArray = new String[matchingColumns.size()];

        for (int idx = 0; idx < lookupDataArray.length; idx++) {
            lookupDataArray[idx] = inputRow[Integer.parseInt(matchingColumns.get(idx).get(IRecordGrouping.COLUMN_IDX))];
        }
        boolean isSimilar = false;
        int rowExtendSize = 4;
        if (isOutputDistDetails) {
            rowExtendSize++;
        }
        for (String[] masterRecord : masterRecords) {
            String[] masterMatchRecord = new String[lookupDataArray.length];
            // Find the match record from master record.
            for (int idx = 0; idx < lookupDataArray.length; idx++) {
                masterMatchRecord[idx] = masterRecord[Integer.parseInt(matchingColumns.get(idx).get(IRecordGrouping.COLUMN_IDX))];
            }
            double matchingProba = recordMatcher.getMatchingWeight(masterMatchRecord, lookupDataArray);
            String dinstanceDetails = ""; //$NON-NLS-1$
            if (isOutputDistDetails) {
                // Concatenate the matching distance details.
                double[] currentAttrWeight = recordMatcher.getCurrentAttributeMatchingWeights();
                if (currentAttrWeight != null && currentAttrWeight.length > 0) {
                    for (double wt : currentAttrWeight) {
                        if (!StringUtils.isEmpty(dinstanceDetails)) {
                            dinstanceDetails += "|"; //$NON-NLS-1$
                        }
                        dinstanceDetails += wt;
                    }
                }

            }

            // Similar
            if (matchingProba >= acceptableThreshold) {
                isSimilar = true;
                // Master GRP_SIZE ++
                masterRecord[masterRecord.length - rowExtendSize + 1] = String.valueOf(Integer
                        .parseInt(masterRecord[masterRecord.length - rowExtendSize + 1]) + 1);
                // Duplicated record
                String[] duplicateRecord = new String[masterRecord.length];
                for (int idx = 0; idx < inputRow.length; idx++) {
                    duplicateRecord[idx] = inputRow[idx];
                }
                // GID
                duplicateRecord[duplicateRecord.length - rowExtendSize] = masterRecord[masterRecord.length - rowExtendSize];
                // Group size
                duplicateRecord[duplicateRecord.length - rowExtendSize + 1] = String.valueOf(0);
                // Master
                duplicateRecord[duplicateRecord.length - rowExtendSize + 2] = String.valueOf(false);
                // Score
                duplicateRecord[duplicateRecord.length - rowExtendSize + 3] = String.valueOf(matchingProba);
                // Match distance details
                if (isOutputDistDetails) {
                    duplicateRecord[duplicateRecord.length - rowExtendSize + 4] = dinstanceDetails;
                }

                duplicatedRecords.add(duplicateRecord);
                break;

            }

        }
        if (!isSimilar) {
            // Master record
            String[] masterRow = new String[inputRow.length + rowExtendSize];
            for (int idx = 0; idx < inputRow.length; idx++) {
                masterRow[idx] = inputRow[idx];
            }
            // GID
            masterRow[masterRow.length - rowExtendSize] = String.valueOf(UUID.randomUUID());
            // Group size
            masterRow[masterRow.length - rowExtendSize + 1] = String.valueOf(1);
            // Master
            masterRow[masterRow.length - rowExtendSize + 2] = String.valueOf(true);
            // Score
            masterRow[masterRow.length - rowExtendSize + 3] = String.valueOf(1.0);
            // Match distance details in master record is null

            masterRecords.add(masterRow);
        }
    }

    public void add(Map<String, String> matchingColumn) {
        matchingColumns.add(matchingColumn);
    }

    public void initialize() {
        masterRecords.clear();
        duplicatedRecords.clear();
        final int recordSize = matchingColumns.size();
        double[] arrAttrWeights = new double[recordSize];
        String[][] algorithmName = new String[recordSize][2];
        int recordIdx = 0;
        for (Map<String, String> recordMap : matchingColumns) {
            arrAttrWeights[recordIdx] = Double.parseDouble(recordMap.get(IRecordGrouping.CONFIDENCE_WEIGHT));
            algorithmName[recordIdx][0] = recordMap.get(IRecordGrouping.MATCHING_TYPE);
            algorithmName[recordIdx][1] = recordMap.get(IRecordGrouping.CUSTOMER_MATCH_CLASS);
            recordIdx++;
        }
        IAttributeMatcher[] attributeMatcher = new IAttributeMatcher[recordSize];

        for (int indx = 0; indx < recordSize; indx++) {
            AttributeMatcherType attrMatcherType = AttributeMatcherType.get(algorithmName[indx][0]);
            try {
                attributeMatcher[indx] = org.talend.dataquality.record.linkage.attribute.AttributeMatcherFactory.createMatcher(
                        attrMatcherType, algorithmName[indx][1]);
            } catch (Throwable e) {
                log.error(e);
                // e.printStackTrace();
                // Exception occurred during the matcher class initializing.
                return;
            }
        }
        recordMatcher.setRecordSize(recordSize);
        recordMatcher.setAttributeWeights(arrAttrWeights);
        recordMatcher.setAttributeMatchers(attributeMatcher);

    }
    public void end() {
        // Add all record to the duplicated record list.
        duplicatedRecords.addAll(masterRecords);
    }

    public List<String[]> getResults() {
        return duplicatedRecords;
    }

    public boolean doMatch(String[] record1, String[] record2) {
        String[] masterMatchRecord = new String[matchingColumns.size()];
        String[] lookupDataArray = new String[matchingColumns.size()];
        for (int idx = 0; idx < lookupDataArray.length; idx++) {
            masterMatchRecord[idx] = record1[Integer.parseInt(matchingColumns.get(idx).get(IRecordGrouping.COLUMN_IDX))];
            lookupDataArray[idx] = record2[Integer.parseInt(matchingColumns.get(idx).get(IRecordGrouping.COLUMN_IDX))];
        }
        double matchingProba = recordMatcher.getMatchingWeight(masterMatchRecord, lookupDataArray);
        if (matchingProba >= acceptableThreshold) {
            return true;
        }
        return false;
    }
}
