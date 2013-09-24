/*
 * Copyright (C) 2006-2013 Talend Inc. - www.talend.com
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

import java.util.*;

public class MFB implements MatchMergeAlgorithm {

    private static final Logger LOGGER = Logger.getLogger(MFB.class);

    private AttributeMatcherType[] algorithms;

    private float[] thresholds;

    private MergeAlgorithm[] merges;

    private double[] weights;

    private IAttributeMatcher.NullOption[] nullOptions;

    private SubString[] subStrings;

    private int maxWeight;

    private double recordMatchThreshold;

    public MFB() {
        algorithms = new AttributeMatcherType[0];
        this.thresholds = new float[0];
        this.merges = new MergeAlgorithm[0];
        this.weights = new double[0];
        this.nullOptions = new IAttributeMatcher.NullOption[0];
        this.subStrings = new SubString[0];
        maxWeight = 0;
    }

    public MFB(AttributeMatcherType[] algorithms,
               float[] thresholds,
               MergeAlgorithm[] merges,
               double[] weights,
               IAttributeMatcher.NullOption[] nullOptions,
               SubString[] subStrings) {
        this.algorithms = algorithms;
        this.thresholds = thresholds;
        this.merges = merges;
        this.weights = weights;
        this.nullOptions = nullOptions;
        this.subStrings = subStrings;
        if (algorithms.length == 0 || thresholds.length == 0 || merges.length == 0 || weights.length == 0) {
            LOGGER.warn("Algorithm initialized with no matching algorithm/threshold/merge/weight information");
        }
        for (double weight : weights) {
            maxWeight += weight;
        }
    }

    public List<Record> execute(Iterator<Record> sourceRecords) {
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
        while (!queue.isEmpty()) {
            if (LOGGER.isDebugEnabled() && index % 10000 == 0) {
                LOGGER.debug("Current index: " + index);
            }
            Record currentRecord = queue.poll();
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
                if (matchRecords(mergedRecord, currentRecord)) {
                    Record newMergedRecord = MatchMerge.merge(currentRecord, mergedRecord, merges);
                    // Keep group id
                    newMergedRecord.setGroupId(mergedRecord.getGroupId());
                    queue.offer(newMergedRecord);
                    mergedRecords.remove(mergedRecord);
                    hasCreatedNewMerge = true;
                    break;
                }
            }
            if (!hasCreatedNewMerge) {
                if (currentRecord.getGroupId() == null) {
                    // Only add a group id if the record doesn't already hold one.
                    currentRecord.setGroupId(UUID.randomUUID().toString());
                }
                currentRecord.getRelatedIds().add(currentRecord.getId());
                mergedRecords.add(currentRecord);
            }
            index++;
        }
        return mergedRecords;
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

    boolean matchRecords(Record mergedRecord, Record currentRecord) {
        if (mergedRecord.getAttributes().size() != currentRecord.getAttributes().size()) {
            throw new IllegalArgumentException("Records do not share same attribute count.");
        }
        Iterator<Attribute> mergedRecordAttributes = mergedRecord.getAttributes().iterator();
        Iterator<Attribute> currentRecordAttributes = currentRecord.getAttributes().iterator();
        double confidence = 0;
        int matchIndex = 0;
        while (mergedRecordAttributes.hasNext()) {
            Attribute left = mergedRecordAttributes.next();
            Attribute right = currentRecordAttributes.next();
            double score = MatchMerge.matchScore(left,
                    right,
                    algorithms[matchIndex],
                    nullOptions[matchIndex],
                    subStrings[matchIndex]);
            if (score < thresholds[matchIndex]) {
                return false;
            } else {
                confidence += score * weights[matchIndex];
            }
            matchIndex++;
        }
        double normalizedConfidence = confidence > 0 ? confidence / maxWeight : confidence; // Normalize to 0..1 value
        currentRecord.setConfidence(normalizedConfidence);
        return true;
    }

    @Override
    public void setRecordSize(int numberOfAttributes) {
        if (numberOfAttributes != algorithms.length) {
            algorithms = new AttributeMatcherType[numberOfAttributes];
            thresholds = new float[numberOfAttributes];
            merges = new MergeAlgorithm[numberOfAttributes];
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
                return new Record(attributes, StringUtils.EMPTY);
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
}

