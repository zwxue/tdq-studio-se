// ============================================================================
//
// Copyright (C) 2006-2014 Talend Inc. - www.talend./9com
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.talend.dataquality.matchmerge.MatchMergeAlgorithm;
import org.talend.dataquality.matchmerge.Record;
import org.talend.dataquality.matchmerge.mfb.MFB;
import org.talend.dataquality.matchmerge.mfb.MFBRecordMerger;
import org.talend.dataquality.matchmerge.mfb.MatchResult;
import org.talend.dataquality.matchmerge.mfb.ValuesIterator;
import org.talend.dataquality.matchmerge.mfb.ValuesIterator.ValueGenerator;
import org.talend.dataquality.record.linkage.record.IRecordMatcher;
import org.talend.dataquality.record.linkage.utils.SurvivorShipAlgorithmEnum;

/**
 * Record grouping class with t-swoosh algorithm.
 * 
 */
public class TSwooshGrouping {

    List<Map<String, ValueGenerator>> rcdsGenerators = new ArrayList<Map<String, ValueGenerator>>();

    int totalCount = 0;

    /**
     * The row that is currently handled by t-swoosh algorithm.
     */
    String[] currentRow = null;

    /**
     * Recording matching with t-swoosh algorithm.
     * 
     * @param inputRow
     * @param matchingRule
     */
    public void addToList(final String[] inputRow, List<Map<String, String>> matchRule) {
        totalCount++;
        String attributeName = null;
        Map<String, ValueGenerator> rcdMap = new HashMap<String, ValueGenerator>();
        for (final Map<String, String> recordMap : matchRule) {
            attributeName = recordMap.get(IRecordGrouping.ATTRIBUTE_NAME);
            rcdMap.put(attributeName, new ValueGenerator() {

                @Override
                public String newValue() {
                    currentRow = inputRow;
                    return inputRow[Integer.valueOf(recordMap.get(IRecordGrouping.COLUMN_IDX))];
                }
            });
        }

        rcdsGenerators.add(rcdMap);
    }

    public void swooshMatch(IRecordMatcher combinedRecordMatcher) {
        MatchMergeAlgorithm algorithm = new MFB(combinedRecordMatcher, new MFBRecordMerger("MFB", new String[] { "" },
                new SurvivorShipAlgorithmEnum[] { SurvivorShipAlgorithmEnum.LONGEST, SurvivorShipAlgorithmEnum.MOST_COMMON }));

        Iterator<Record> iterator = new ValuesIterator(totalCount, rcdsGenerators);
        List<Record> mergedRecords = algorithm.execute(iterator, new GroupingCallBack());
    }

    class GroupingCallBack implements MatchMergeAlgorithm.Callback {

        /*
         * (non-Javadoc)
         * 
         * @see
         * org.talend.dataquality.matchmerge.MatchMergeAlgorithm.Callback#onBeginRecord(org.talend.dataquality.matchmerge
         * .Record)
         */
        @Override
        public void onBeginRecord(Record record) {
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * org.talend.dataquality.matchmerge.MatchMergeAlgorithm.Callback#onMatch(org.talend.dataquality.matchmerge.
         * Record, org.talend.dataquality.matchmerge.Record, org.talend.dataquality.matchmerge.mfb.MatchResult)
         */
        @Override
        public void onMatch(Record record1, Record record2, MatchResult matchResult) {
            System.out.println("On match");
            printRow();
            record1.getAttributes().get(0).getValue();
        }

        private void printRow() {
            for (String field : currentRow) {
                System.out.print(field + " ,");
            }
            System.out.println();
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * org.talend.dataquality.matchmerge.MatchMergeAlgorithm.Callback#onNewMerge(org.talend.dataquality.matchmerge
         * .Record)
         */
        @Override
        public void onNewMerge(Record record) {
            System.out.println("On merge");
            printRow();
            record.getAttributes();

        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * org.talend.dataquality.matchmerge.MatchMergeAlgorithm.Callback#onRemoveMerge(org.talend.dataquality.matchmerge
         * .Record)
         */
        @Override
        public void onRemoveMerge(Record record) {
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * org.talend.dataquality.matchmerge.MatchMergeAlgorithm.Callback#onDifferent(org.talend.dataquality.matchmerge
         * .Record, org.talend.dataquality.matchmerge.Record, org.talend.dataquality.matchmerge.mfb.MatchResult)
         */
        @Override
        public void onDifferent(Record record1, Record record2, MatchResult matchResult) {

        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * org.talend.dataquality.matchmerge.MatchMergeAlgorithm.Callback#onEndRecord(org.talend.dataquality.matchmerge
         * .Record)
         */
        @Override
        public void onEndRecord(Record record) {
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.talend.dataquality.matchmerge.MatchMergeAlgorithm.Callback#isInterrupted()
         */
        @Override
        public boolean isInterrupted() {
            // TODO Auto-generated method stub
            return false;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.talend.dataquality.matchmerge.MatchMergeAlgorithm.Callback#onBeginProcessing()
         */
        @Override
        public void onBeginProcessing() {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see org.talend.dataquality.matchmerge.MatchMergeAlgorithm.Callback#onEndProcessing()
         */
        @Override
        public void onEndProcessing() {
            // TODO Auto-generated method stub

        }

    }
}
