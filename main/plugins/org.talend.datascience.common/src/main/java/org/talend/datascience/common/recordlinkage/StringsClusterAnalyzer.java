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
package org.talend.datascience.common.recordlinkage;

import java.util.*;

import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.lang.StringUtils;
import org.talend.dataquality.matchmerge.*;
import org.talend.dataquality.matchmerge.mfb.MFB;
import org.talend.dataquality.record.linkage.attribute.IAttributeMatcher;
import org.talend.dataquality.record.linkage.constant.AttributeMatcherType;
import org.talend.dataquality.record.linkage.genkey.BlockingKeyHandler;
import org.talend.dataquality.record.linkage.utils.BlockingKeyAlgorithmEnum;
import org.talend.dataquality.record.linkage.utils.MatchAnalysisConstant;
import org.talend.dataquality.record.linkage.utils.SurvivorShipAlgorithmEnum;
import org.talend.datascience.common.inference.Analyzer;

/**
 * Sting clustering analyzer.
 * 
 * @author zhao
 *
 */
public class StringsClusterAnalyzer implements Analyzer<StringClusters> {

    private final StringClusters stringClusters = new StringClusters();

    private final List<Object[]> inputList = new ArrayList<Object[]>();

    private BlockingKeyHandler blockKeyHandler = null;

    public void init() {
        // Blocking the data given fingerprint key
        String columnName = "NAME";
        List<Map<String, String>> blockKeySchema = new ArrayList<Map<String, String>>();
        Map<String, String> blockKeyDefMap = new HashMap<String, String>();

        blockKeyDefMap.put(MatchAnalysisConstant.PRECOLUMN, columnName);
        blockKeyDefMap.put(MatchAnalysisConstant.KEY_ALGO, BlockingKeyAlgorithmEnum.FINGERPRINTKEY.getValue());
        blockKeySchema.add(blockKeyDefMap);

        Map<String, String> colName2IndexMap = new HashMap<String, String>();
        colName2IndexMap.put(columnName, String.valueOf(0));
        blockKeyHandler = new BlockingKeyHandler(blockKeySchema, colName2IndexMap);
    }

    public boolean analyze(String... record) {
        if (record == null || record.length != 1) {
            return false;
        }
        // FIXME whether multiple fields should be considered ?
        inputList.add(new Object[] { record[0] });
        return false;
    }

    public void end() {
        // Run blocking
        blockKeyHandler.setInputData(inputList);
        blockKeyHandler.run();
        // Match & merge block values
        Map<String, List<String[]>> resultData = blockKeyHandler.getResultDatas();
        List<Record> records = new ArrayList<Record>();
        int currentBlockIndex = 0;
        for (List<String[]> blockValues : resultData.values()) {
            MatchMergeAlgorithm matchMergeAlgorithm = MFB.build(new AttributeMatcherType[] { AttributeMatcherType.DUMMY }, //
                    new String[] { StringUtils.EMPTY }, //
                    new float[] { 0.8f }, //
                    0.8d, //
                    new SurvivorShipAlgorithmEnum[] { SurvivorShipAlgorithmEnum.MOST_COMMON }, //
                    new String[] { StringUtils.EMPTY }, //
                    new double[] { 1.0 }, //
                    new IAttributeMatcher.NullOption[] { IAttributeMatcher.NullOption.nullMatchNull }, //
                    new SubString[] { SubString.NO_SUBSTRING }, //
                    StringUtils.EMPTY);
            final Iterator<String[]> iterator = blockValues.iterator();
            final List<Record> blockResults = matchMergeAlgorithm.execute(new RecordIterator(currentBlockIndex, iterator));
            records.addAll(blockResults);
            currentBlockIndex++;
        }
        // Match & merge block values together (with higher thresholds)
        PostMerge[] matchAlgorithms = new PostMerge[] {
                new PostMerge(AttributeMatcherType.LEVENSHTEIN, 0.95f), //
                new PostMerge(AttributeMatcherType.SOUNDEX, 0.95f)
        };
        for (PostMerge postMerge : matchAlgorithms) {
            records = postMerge(records, postMerge.matcher, postMerge.threshold);
        }
        // Build string cluster based on successive match & merges
        Map<String, String[]> masterToValues = new HashMap<String, String[]>();
        for (Record record : records) {
            if (record.getRelatedIds().size() > 1) { // Merged record (and not a single record)
                final Attribute attribute = record.getAttributes().get(0);
                final AttributeValues<String> values = attribute.getValues();
                final String[] originalValues = (String[]) IteratorUtils.toArray(values.iterator(), String.class);
                if (values.hasMultipleValues() && originalValues.length > 1) {
                    masterToValues.put(attribute.getValue(), originalValues);
                }
            }
        }
        for (Map.Entry<String, String[]> current : masterToValues.entrySet()) {
            final StringClusters.StringCluster cluster = new StringClusters.StringCluster();
            cluster.survivedValue = current.getKey();
            cluster.originalValues = current.getValue();
            stringClusters.addCluster(cluster);
        }
    }

    private List<Record> postMerge(List<Record> records, AttributeMatcherType matchAlgorithm, float threshold) {
        List<Record> mergeResult;
        MatchMergeAlgorithm crossBlockMatch = MFB.build(new AttributeMatcherType[] { matchAlgorithm }, //
                new String[] { StringUtils.EMPTY }, //
                new float[] { threshold }, //
                0.95d, //
                new SurvivorShipAlgorithmEnum[] { SurvivorShipAlgorithmEnum.MOST_COMMON }, //
                new String[] { StringUtils.EMPTY }, //
                new double[] { 1.0 }, //
                new IAttributeMatcher.NullOption[] { IAttributeMatcher.NullOption.nullMatchNull }, //
                new SubString[] { SubString.NO_SUBSTRING }, //
                StringUtils.EMPTY);
        mergeResult = crossBlockMatch.execute(records.iterator());
        return mergeResult;
    }

    public List<StringClusters> getResult() {
        List<StringClusters> cluster = new ArrayList<StringClusters>();
        cluster.add(stringClusters);
        return cluster;
    }

    private static class PostMerge {
        AttributeMatcherType matcher;
        float threshold;

        public PostMerge(AttributeMatcherType matcher, float threshold) {
            this.matcher = matcher;
            this.threshold = threshold;
        }
    }
    
    private static class RecordIterator implements Iterator<Record> {

        private int index;

        private final int currentBlockIndex;

        private final Iterator<String[]> iterator;

        public RecordIterator(int currentBlockIndex, Iterator<String[]> iterator) {
            this.currentBlockIndex = currentBlockIndex;
            this.iterator = iterator;
        }

        public boolean hasNext() {
            return iterator.hasNext();
        }

        public Record next() {
            final String[] values = iterator.next();
            final Attribute value = new Attribute("col0");
            value.setValue(values[0]);
            return new Record(Collections.singletonList(value), currentBlockIndex + "-" + String.valueOf(index++), 0, "");
        }
    }
}
