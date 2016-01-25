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

package org.talend.dataquality.matchmerge;

import org.talend.dataquality.matchmerge.mfb.MatchResult;
import org.talend.dataquality.record.linkage.record.IRecordMatcher;

import java.util.Iterator;
import java.util.List;

/**
 *
 */
public interface MatchMergeAlgorithm extends IRecordMatcher {

    List<Record> execute(Iterator<Record> sourceRecords);

    List<Record> execute(Iterator<Record> sourceRecords, Callback callback);

    interface Callback {

        void onBeginRecord(Record record);

        void onMatch(Record record1, Record record2, MatchResult matchResult);

        void onNewMerge(Record record);

        void onRemoveMerge(Record record);

        void onDifferent(Record record1, Record record2, MatchResult matchResult);

        void onEndRecord(Record record);

        boolean isInterrupted();

        void onBeginProcessing();

        void onEndProcessing();

        void onBeginPostMergeProcess();

        void onEndPostMergeProcess();
    }

}
