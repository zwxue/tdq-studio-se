/*
 * Copyright (C) 2006-2016 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 9 rue Pages 92150 Suresnes, France
 */
package org.talend.dataquality.matchmerge.mfb;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.talend.dataquality.matchmerge.*;
import org.talend.dataquality.record.linkage.attribute.IAttributeMatcher;
import org.talend.dataquality.record.linkage.constant.AttributeMatcherType;
import org.talend.dataquality.record.linkage.utils.SurvivorShipAlgorithmEnum;

import java.util.*;

public class MFB implements MatchMergeAlgorithm {

    private static final Logger LOGGER = Logger.getLogger(MFB.class);

    private AttributeMatcherType[] algorithms;

    private final String[] algorithmParameters;

    private float[] thresholds;

    private SurvivorShipAlgorithmEnum[] merges;

    private double[] weights;

    private IAttributeMatcher.NullOption[] nullOptions;

    private SubString[] subStrings;

    private int maxWeight;

    private double recordMatchThreshold;

    private String mergedRecordSource;

    private String[] mergesParameters;

    private double minConfidenceValue;

    public MFB() {
        this.algorithms = new AttributeMatcherType[0];
        this.algorithmParameters = new String[0];
        this.thresholds = new float[0];
        this.merges = new SurvivorShipAlgorithmEnum[0];
        this.mergesParameters = new String[0];
        this.weights = new double[0];
        this.nullOptions = new IAttributeMatcher.NullOption[0];
        this.subStrings = new SubString[0];
        this.mergedRecordSource = StringUtils.EMPTY;
        this.minConfidenceValue = 0;
        maxWeight = 0;
    }

    public MFB(AttributeMatcherType[] algorithms,
               String[] algorithmParameters,
               float[] thresholds,
               double minConfidenceValue,
               SurvivorShipAlgorithmEnum[] merges,
               String[] mergesParameters,
               double[] weights,
               IAttributeMatcher.NullOption[] nullOptions,
               SubString[] subStrings,
               String mergedRecordSource) {
        this.algorithms = algorithms;
        this.algorithmParameters = algorithmParameters;
        this.thresholds = thresholds;
        this.minConfidenceValue = minConfidenceValue;
        this.merges = merges;
        this.mergesParameters = mergesParameters;
        this.weights = weights;
        this.nullOptions = nullOptions;
        this.subStrings = subStrings;
        this.mergedRecordSource = mergedRecordSource;
        if (algorithms.length == 0 || thresholds.length == 0 || merges.length == 0 || weights.length == 0) {
            LOGGER.warn("Algorithm initialized with no matching algorithm/threshold/merge/weight information");
        }
        for (double weight : weights) {
            maxWeight += weight;
        }
    }

    public List<Record> execute(Iterator<Record> sourceRecords) {
        return execute(sourceRecords, DefaultCallback.INSTANCE);
    }

    @Override
    public List<Record> execute(Iterator<Record> sourceRecords, Callback callback) {
        if (callback == null) {
            throw new IllegalArgumentException("Callback cannot be null.");
        }
        if (algorithms.length == 0 || thresholds.length == 0 || merges.length == 0 || weights.length == 0) {
            List<Record> ret = new ArrayList<Record>();
            while (sourceRecords.hasNext()) {
                ret.add(sourceRecords.next());
            }
            return ret;
        }
        List<Record> mergedRecords = new ArrayList<Record>();
        int index = 0;
        // Read source record per record
        Queue<Record> queue = new ProcessQueue<Record>(sourceRecords);
        callback.onBeginProcessing();
        while (!queue.isEmpty() && !callback.isInterrupted()) {
            if (LOGGER.isDebugEnabled() && index % 10000 == 0) {
                LOGGER.debug("Current index: " + index);
            }
            Record currentRecord = queue.poll();
            callback.onBeginRecord(currentRecord);
            // Sanity checks
            if (currentRecord == null) {
                throw new IllegalArgumentException("Record cannot be null.");
            }
            if (index == 0) { // Performs this only for first record.
                performAsserts(currentRecord);
            }
            // MFB algorithm
            boolean hasCreatedNewMerge = false;
            for (Record mergedRecord : mergedRecords) {
                MatchResult matchResult = matchRecords(mergedRecord,
                        currentRecord,
                        algorithms,
                        algorithmParameters,
                        thresholds,
                        nullOptions,
                        subStrings,
                        weights,
                        minConfidenceValue);
                if (matchResult.isMatch()) {
                    callback.onMatch(mergedRecord, currentRecord, matchResult);
                    Record newMergedRecord = MatchMerge.merge(currentRecord,
                            mergedRecord,
                            merges,
                            mergesParameters,
                            mergedRecordSource);
                    queue.offer(newMergedRecord);
                    callback.onNewMerge(newMergedRecord);
                    mergedRecords.remove(mergedRecord);
                    callback.onRemoveMerge(mergedRecord);
                    hasCreatedNewMerge = true;
                    break;
                } else {
                    callback.onDifferent(mergedRecord, currentRecord, matchResult);
                }
            }
            if (!hasCreatedNewMerge) {
                currentRecord.getRelatedIds().add(currentRecord.getId());
                mergedRecords.add(currentRecord);
                callback.onNewMerge(currentRecord);
            }
            callback.onEndRecord(currentRecord);
            index++;
        }
        // Post merge processing (most common values...)
        callback.onEndProcessing();
        return mergedRecords;
    }

    // TODO This method does not belong here: should be in linkage (maybe?)
    // TODO Duplicated code with org.talend.mdm.core -> bad
    public static String getMostCommonValue(Collection<String> values) {
        if (values.isEmpty()) {
            return null;
        }
        String[] strings = values.toArray(new String[values.size()]);
        Arrays.sort(strings); // Sorts items to ensure all similar strings are grouped together
        int occurrenceCount = 1;
        int maxOccurrenceCount = 0;
        String mostCommon = strings[0];
        String previousString = strings[0];
        for (int i = 1; i < strings.length; i++) {
            String current = strings[i];
            if (!areEquals(previousString, current)) {
                if (occurrenceCount > maxOccurrenceCount) {
                    mostCommon = previousString;
                    maxOccurrenceCount = occurrenceCount;
                }
                occurrenceCount = 1;
            } else {
                occurrenceCount++;
            }
            previousString = current;
        }
        if (occurrenceCount > maxOccurrenceCount) {
            mostCommon = previousString;
        }
        return mostCommon;
    }

    private static boolean areEquals(String previousString, String current) {
        if (previousString == null) {
            return current == null;
        }
        return previousString.equals(current);
    }

    private void performAsserts(Record currentRecord) {
        if (currentRecord.getAttributes().size() != algorithms.length) {
            throw new IllegalArgumentException("All record columns should have a matching algorithm.");
        }
        if (currentRecord.getAttributes().size() != thresholds.length) {
            throw new IllegalArgumentException("All record columns should have a threshold.");
        }
        if (currentRecord.getAttributes().size() != merges.length) {
            throw new IllegalArgumentException("All record columns should have a threshold.");
        }
    }

    public static MatchResult matchRecords(Record mergedRecord,
                                           Record currentRecord,
                                           AttributeMatcherType[] algorithms,
                                           String[] algorithmParameters,
                                           float[] thresholds,
                                           IAttributeMatcher.NullOption[] nullOptions,
                                           SubString[] subStrings,
                                           double[] weights,
                                           double minConfidenceValue) {
        if (mergedRecord.getAttributes().size() != currentRecord.getAttributes().size()) {
            throw new IllegalArgumentException("Records do not share same attribute count.");
        }
        if (mergedRecord.getGroupId() != null && currentRecord.getGroupId() != null) {
            if (!mergedRecord.getGroupId().equals(currentRecord.getGroupId())) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Cannot match record: already different groups.");
                }
                return NonMatchResult.INSTANCE;
            } else { // Two records of same group
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Merging already merged records (same group id).");
                }
            }
        }
        Iterator<Attribute> mergedRecordAttributes = mergedRecord.getAttributes().iterator();
        Iterator<Attribute> currentRecordAttributes = currentRecord.getAttributes().iterator();
        double confidence = 0;
        int matchIndex = 0;
        MatchResult result = new MatchResult(mergedRecord.getAttributes().size());
        while (mergedRecordAttributes.hasNext()) {
            Attribute left = mergedRecordAttributes.next();
            Attribute right = currentRecordAttributes.next();
            double score = MatchMerge.matchScore(left,
                    right,
                    algorithms[matchIndex],
                    algorithmParameters[matchIndex],
                    nullOptions[matchIndex],
                    subStrings[matchIndex]);
            result.setScore(matchIndex, algorithms[matchIndex], score, left.getValue(), right.getValue());
            result.setThreshold(matchIndex, thresholds[matchIndex]);
            confidence += score * weights[matchIndex];
            matchIndex++;
        }
        int maxWeight = 0;
        for (double weight : weights) {
            maxWeight += weight;
        }
        double normalizedConfidence = confidence > 0 ? confidence / maxWeight : confidence; // Normalize to 0..1 value
        result.setConfidence(normalizedConfidence, minConfidenceValue);
        if (normalizedConfidence < minConfidenceValue) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Cannot match record: merged record has a too low confidence value ("
                        + normalizedConfidence
                        + " < " + minConfidenceValue + ")");
            }
            return NonMatchResult.wrap(result);
        }
        currentRecord.setConfidence(normalizedConfidence);
        return result;
    }

    @Override
    public void setRecordSize(int numberOfAttributes) {
        if (numberOfAttributes != algorithms.length) {
            algorithms = new AttributeMatcherType[numberOfAttributes];
            thresholds = new float[numberOfAttributes];
            merges = new SurvivorShipAlgorithmEnum[numberOfAttributes];
            weights = new double[numberOfAttributes];
            nullOptions = new IAttributeMatcher.NullOption[numberOfAttributes];
            subStrings = new SubString[numberOfAttributes];
        }
    }

    @Override
    public int getRecordSize() {
        return algorithms.length;
    }

    @Override
    public boolean setAttributeWeights(double[] weights) {
        if (this.weights.length > weights.length) {
            return false;
        }
        this.weights = weights;
        return true;
    }

    @Override
    public boolean setAttributeGroups(int[][] groups) {
        // All values are compared in this algorithm
        return true;
    }

    @Override
    public boolean setAttributeMatchers(IAttributeMatcher[] attributeMatchers) {
        if (attributeMatchers.length > algorithms.length) {
            return false;
        }
        int i = 0;
        for (IAttributeMatcher attributeMatcher : attributeMatchers) {
            algorithms[i] = attributeMatcher.getMatchType();
            nullOptions[i] = attributeMatcher.getNullOption();
            i++;
        }
        return true;
    }

    @Override
    public double getMatchingWeight(String[] record1, String[] record2) {
        List<String[]> records = new ArrayList<String[]>(2);
        records.add(record1);
        records.add(record2);
        final Iterator<String[]> iterator = records.iterator();
        // Performs a match&merge on these 2 records -> Only one record at the end = match.
        List<Record> list = execute(new Iterator<Record>() {
            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public Record next() {
                String[] values = iterator.next();
                ArrayList<Attribute> attributes = new ArrayList<Attribute>();
                int i = 0;
                for (String value : values) {
                    attributes.add(new Attribute(String.valueOf(i++), value));
                }
                return new Record(attributes, StringUtils.EMPTY, 0, StringUtils.EMPTY);
            }

            @Override
            public void remove() {
                iterator.remove();
            }
        });
        return list.size() == 1 ? 1 : 0;
    }

    @Override
    public double[] getCurrentAttributeMatchingWeights() {
        return new double[0];
    }

    @Override
    public String getLabeledAttributeMatchWeights() {
        return StringUtils.EMPTY;
    }

    @Override
    public boolean setBlockingAttributeMatchers(int[] attrMatcherIndices) {
        // This implementation does not perform blocking -> block construction is expected to be done by caller.
        return false;
    }

    @Override
    public boolean setblockingThreshold(double threshold) {
        // This implementation does not perform blocking -> block construction is expected to be done by caller.
        return false;
    }

    @Override
    public double getRecordMatchThreshold() {
        return recordMatchThreshold;
    }

    @Override
    public void setRecordMatchThreshold(double recordMatchThreshold) {
        this.recordMatchThreshold = recordMatchThreshold;
    }

    @Override
    public void setDisplayLabels(boolean displayLabels) {
        // TODO Nothing to do here?
    }

    public static class NonMatchResult extends MatchResult {

        public static final MatchResult INSTANCE = wrap(new MatchResult(0));

        private final MatchResult result;

        private NonMatchResult(MatchResult result) {
            super(result.getScores().size());
            this.result = result;
        }

        public static MatchResult wrap(MatchResult result) {
            return new NonMatchResult(result);
        }

        @Override
        public List<Score> getScores() {
            return result.getScores();
        }

        @Override
        public List<Float> getThresholds() {
            return result.getThresholds();
        }

        @Override
        public void setScore(int index, AttributeMatcherType algorithm, double score, String value1, String value2) {
            result.setScore(index, algorithm, score, value1, value2);
        }

        @Override
        public void setThreshold(int index, float threshold) {
            result.setThreshold(index, threshold);
        }

        @Override
        public boolean isMatch() {
            return false;
        }
    }
}

