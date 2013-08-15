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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.talend.dataquality.record.linkage.attribute.IAttributeMatcher;
import org.talend.dataquality.record.linkage.constant.AttributeMatcherType;
import org.talend.dataquality.record.linkage.constant.RecordMatcherType;
import org.talend.dataquality.record.linkage.record.CombinedRecordMatcher;
import org.talend.dataquality.record.linkage.record.IRecordMatcher;
import org.talend.dataquality.record.linkage.record.RecordMatcherFactory;

/**
 * created by zhao on Jul 19, 2013 <br>
 * Abstract record grouping implementation without hadoop API included.
 * 
 */
public class AbstractRecordGrouping implements IRecordGrouping {

    private static Logger log = Logger.getLogger(AbstractRecordGrouping.class);

    private List<String[]> masterRecords = new ArrayList<String[]>();

    public static final float DEFAULT_THRESHOLD = 0.95f;

    private float acceptableThreshold = DEFAULT_THRESHOLD;

    // Output distance details or not.
    private boolean isOutputDistDetails = Boolean.FALSE;

    private CombinedRecordMatcher combinedRecordMatcher = RecordMatcherFactory.createCombinedRecordMatcher();

    /**
     * @deprecated use {{@link #multiMatchRules}
     */
    @Deprecated
    private List<Map<String, String>> matchingColumns = new ArrayList<Map<String, String>>();

    private List<List<Map<String, String>>> multiMatchRules = new ArrayList<List<Map<String, String>>>();

    private String columnDelimiter = null;

    private Boolean isLinkToPrevious = Boolean.FALSE;

    // The exthended column size.
    int extSize;

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.hadoop.group.IRecordGrouping#setColumnDelimiter(java.lang.String)
     */
    @Override
    public void setColumnDelimiter(String columnDelimiter) {
        this.columnDelimiter = columnDelimiter;

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.hadoop.group.IRecordGrouping#SetIsLinkToPrevious(java.lang.Boolean)
     */
    @Override
    public void setIsLinkToPrevious(Boolean isLinkToPrevious) {
        this.isLinkToPrevious = isLinkToPrevious;
    }

    @Override
    public void setAcceptableThreshold(float acceptableThreshold) {
        this.acceptableThreshold = acceptableThreshold;
    }

    @Override
    public void setIsOutputDistDetails(boolean isOutputDistDetails) {
        this.isOutputDistDetails = isOutputDistDetails;
    }

    @Override
    public void doGroup(String[] inputRow) throws IOException, InterruptedException {

        extSize = isOutputDistDetails ? 5 : 4;
        // In case of current component is linked to previous, and the record is NOT master, just put it to the output
        // and continue;
        if (isLinkToPrevious && !inputRow[inputRow.length - extSize + 2].equalsIgnoreCase("true")) { //$NON-NLS-1$
            outputRow(StringUtils.join(inputRow, columnDelimiter));
            return;
        }

        if (multiMatchRules.size() == 0) {
            // No rule defined.
            return;
        }
        // temporary array to store attributes to match
        List<Map<String, String>> matchingRule = multiMatchRules.get(0);
        String[] lookupDataArray = new String[matchingRule.size()];

        for (int idx = 0; idx < lookupDataArray.length; idx++) {
            lookupDataArray[idx] = inputRow[Integer.parseInt(matchingRule.get(idx).get(IRecordGrouping.COLUMN_IDX))];
        }
        boolean isSimilar = false;
        for (String[] masterRecord : masterRecords) {
            if (isLinkToPrevious) {
                int masterGRPSize = Integer.valueOf(masterRecord[inputRow.length - extSize + 1]);
                int inputGRPSize = Integer.valueOf(inputRow[inputRow.length - extSize + 1]);
                // Don't compare the records whose GRP_SIZE both > 1.
                if (masterGRPSize > 1 && inputGRPSize > 1) {
                    continue;
                }
            }

            String[] masterMatchRecord = new String[lookupDataArray.length];
            // Find the match record from master record.
            for (int idx = 0; idx < lookupDataArray.length; idx++) {
                masterMatchRecord[idx] = masterRecord[Integer.parseInt(matchingRule.get(idx).get(IRecordGrouping.COLUMN_IDX))];
            }
            double matchingProba = combinedRecordMatcher.getMatchingWeight(masterMatchRecord, lookupDataArray);
            String distanceDetails = ""; //$NON-NLS-1$
            if (isOutputDistDetails) {
                // Concatenate the matching distance details.
                double[] currentAttrWeight = combinedRecordMatcher.getCurrentAttributeMatchingWeights();
                if (currentAttrWeight != null && currentAttrWeight.length > 0) {
                    for (double wt : currentAttrWeight) {
                        if (!StringUtils.isEmpty(distanceDetails)) {
                            distanceDetails += "|"; //$NON-NLS-1$
                        }
                        distanceDetails += wt;
                    }
                }

            }

            // Similar
            if (matchingProba >= acceptableThreshold) {
                isSimilar = true;
                // Master GRP_SIZE ++
                if (isLinkToPrevious) {
                    int masterGRPSize = Integer.valueOf(masterRecord[masterRecord.length - extSize + 1]);
                    if (masterGRPSize == 1) {
                        inputRow[inputRow.length - extSize + 1] = String.valueOf(Integer.parseInt(inputRow[inputRow.length
                                - extSize + 1]) + 1);
                        updateWithExtendedColumn(masterRecord, inputRow, matchingProba, distanceDetails, columnDelimiter);
                        // Update master record from the temporary master list.
                        masterRecords.remove(masterRecord);
                        masterRecords.add(inputRow);
                        break;
                    }
                }
                masterRecord[masterRecord.length - extSize + 1] = String.valueOf(Integer
                        .parseInt(masterRecord[masterRecord.length - extSize + 1]) + 1);
                // Duplicated record
                updateWithExtendedColumn(inputRow, masterRecord, matchingProba, distanceDetails, columnDelimiter);
                break;

            }

        }
        if (!isSimilar) {
            // For the passes that linked to previous, the extension size (e.g: GID,GRP_SIZE) had already been set
            // before.

            // Master record
            String[] masterRow = new String[inputRow.length + (isLinkToPrevious ? 0 : extSize)];
            for (int idx = 0; idx < inputRow.length; idx++) {
                masterRow[idx] = inputRow[idx];
            }
            if (!isLinkToPrevious) {
                // GID
                masterRow[masterRow.length - extSize] = String.valueOf(UUID.randomUUID());
                // Group size
                masterRow[masterRow.length - extSize + 1] = String.valueOf(1);
                // Master
                masterRow[masterRow.length - extSize + 2] = String.valueOf(true);
                // Score
                masterRow[masterRow.length - extSize + 3] = String.valueOf(1.0);
                if (isOutputDistDetails) {
                    // Match distance details in master record is null
                    masterRow[masterRow.length - extSize + 4] = ""; //$NON-NLS-1$
                }
            }

            masterRecords.add(masterRow);
        }
    }

    @Override
    public void end() throws IOException, InterruptedException {
        // output the masters
        for (String[] mst : masterRecords) {
            outputRow(StringUtils.join(mst, columnDelimiter));
        }

    }

    private void updateWithExtendedColumn(String[] inputRow, String[] masterRecord, double matchingProba, String distanceDetails,
            String delimiter) throws IOException, InterruptedException {
        String[] duplicateRecord = new String[masterRecord.length];
        for (int idx = 0; idx < inputRow.length; idx++) {
            duplicateRecord[idx] = inputRow[idx];
        }
        // GID
        duplicateRecord[duplicateRecord.length - extSize] = masterRecord[masterRecord.length - extSize];
        // Group size
        duplicateRecord[duplicateRecord.length - extSize + 1] = String.valueOf(0);
        // Master
        duplicateRecord[duplicateRecord.length - extSize + 2] = String.valueOf(false);
        // Score

        duplicateRecord[duplicateRecord.length - extSize + 3] = String.valueOf(matchingProba);
        if (isOutputDistDetails) {
            // Match distance details
            duplicateRecord[duplicateRecord.length - extSize + 4] = distanceDetails;
        }
        // output the duplicate record
        outputRow(StringUtils.join(duplicateRecord, delimiter));

    }

    @SuppressWarnings("deprecation")
    @Override
    public void add(Map<String, String> matchingColumn) {
        matchingColumns.add(matchingColumn);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.hadoop.group.IRecordGrouping#addMatchRule(java.util.List)
     */
    @Override
    public void addMatchRule(List<Map<String, String>> matchRule) {
        multiMatchRules.add(matchRule);
    }

    /**
     * 
     * Output one row
     * 
     * @param row
     */
    protected void outputRow(String row) {
        // Subclass must implement the way of output.
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.hadoop.group.IRecordGrouping#initialize()
     */
    @Override
    public void initialize() {
        masterRecords.clear();
        for (List<Map<String, String>> matchRule : multiMatchRules) {
            createSimpleRecordMatcher(matchRule);
        }
    }

    private void createSimpleRecordMatcher(List<Map<String, String>> matchRule) {
        final int recordSize = matchRule.size();
        double[] arrAttrWeights = new double[recordSize];
        String[][] algorithmName = new String[recordSize][2];
        String[] arrMatchHandleNull = new String[recordSize];
        int recordIdx = 0;
        for (Map<String, String> recordMap : matchRule) {
            arrAttrWeights[recordIdx] = Double.parseDouble(recordMap.get(IRecordGrouping.CONFIDENCE_WEIGHT));
            algorithmName[recordIdx][0] = recordMap.get(IRecordGrouping.MATCHING_TYPE);
            algorithmName[recordIdx][1] = recordMap.get(IRecordGrouping.CUSTOMER_MATCH_CLASS);
            arrMatchHandleNull[recordIdx] = recordMap.get(IRecordGrouping.HANDLE_NULL);
            recordIdx++;
        }
        IAttributeMatcher[] attributeMatcher = new IAttributeMatcher[recordSize];

        for (int indx = 0; indx < recordSize; indx++) {
            AttributeMatcherType attrMatcherType = AttributeMatcherType.get(algorithmName[indx][0]);
            try {
                attributeMatcher[indx] = org.talend.dataquality.record.linkage.attribute.AttributeMatcherFactory.createMatcher(
                        attrMatcherType, algorithmName[indx][1]);
                attributeMatcher[indx].setNullOption(arrMatchHandleNull[indx]);
            } catch (Throwable e) {
                log.error(e);
                // e.printStackTrace();
                // Exception occurred during the matcher class initializing.
                return;
            }
        }
        IRecordMatcher simpleRecordMatcher = RecordMatcherFactory.createMatcher(RecordMatcherType.simpleVSRMatcher);
        simpleRecordMatcher.setRecordSize(recordSize);
        simpleRecordMatcher.setAttributeWeights(arrAttrWeights);
        simpleRecordMatcher.setAttributeMatchers(attributeMatcher);
        combinedRecordMatcher.add(simpleRecordMatcher);
    }
}
