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

import org.apache.log4j.Logger;
import org.talend.dataquality.matchmerge.*;

import java.util.*;

public class MFB implements MatchMergeAlgorithm {

    private static final Logger LOGGER = Logger.getLogger(MFB.class);

    private final MatchAlgorithm[] algorithms;

    private final float[] thresholds;

    private final MergeAlgorithm[] merges;

    private final int[] weights;

    private final NullOption[] nullOptions;

    private int maxWeight;

    public MFB(MatchAlgorithm[] algorithms, float[] thresholds, MergeAlgorithm[] merges, int[] weights, NullOption[] nullOptions) {
        this.algorithms = algorithms;
        this.thresholds = thresholds;
        this.merges = merges;
        this.weights = weights;
        this.nullOptions = nullOptions;
        if (algorithms.length == 0 || thresholds.length == 0 || merges.length == 0 || weights.length == 0) {
            LOGGER.warn("Algorithm initialized with no matching algorithm/threshold/merge/weight information");
        }
        for (int weight : weights) {
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
            double score = MatchMerge.matchScore(left, right, algorithms[matchIndex], nullOptions[matchIndex]);
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
}

