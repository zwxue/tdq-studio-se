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

    //
    public MFB(MatchAlgorithm[] algorithms, float[] thresholds, MergeAlgorithm[] merges) {
        this.algorithms = algorithms;
        this.thresholds = thresholds;
        this.merges = merges;
        if (algorithms.length == 0 || thresholds.length == 0 || merges.length == 0) {
            LOGGER.warn("Algorithm initialized with no matching algorithm/threshold/merge information");
        }
    }

    public List<Record> execute(Iterator<Record> sourceRecords) {
        if (algorithms.length == 0 || thresholds.length == 0 || merges.length == 0) {
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
            if (index % 10000 == 0) {
                LOGGER.info("Current index: " + index);
            }
            Record currentRecord = queue.poll();
            // Sanity checks
            if (currentRecord == null) {
                throw new IllegalArgumentException("Record cannot be null.");
            }
            if (index == 0) { // Performs this only for first record.
                performAsserts(currentRecord);
            }
            // Actual MFB algorithm
            boolean hasCreatedNewMerge = false;
            for (Record mergedRecord : mergedRecords) {
                if (MatchMerge.equals(currentRecord, mergedRecord) || matchRecords(mergedRecord, currentRecord)) {
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
                currentRecord.setGroupId(UUID.randomUUID().toString());
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
        Iterator<Attribute> rPrimIterator = mergedRecord.getAttributes().iterator();
        Iterator<Attribute> currentIterator = currentRecord.getAttributes().iterator();
        int matchIndex = 0;
        while (rPrimIterator.hasNext()) {
            Attribute left = rPrimIterator.next();
            Attribute right = currentIterator.next();
            if (!MatchMerge.match(left, right, algorithms[matchIndex], thresholds[matchIndex])) {
                return false;
            }
            matchIndex++;
        }
        return true;
    }
}

