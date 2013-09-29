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
import org.talend.dataquality.matchmerge.MatchMergeAlgorithm;
import org.talend.dataquality.matchmerge.Record;

class LoggerCallback implements MatchMergeAlgorithm.Callback {

    private static Logger LOGGER = Logger.getLogger(MatchMergeAlgorithm.Callback.class);

    @Override
    public void onBeginRecord(Record record) {
        LOGGER.info("-> Record " + record.getId());
    }

    @Override
    public void onMatch(Record record1, Record record2, MatchResult matchResult) {
        LOGGER.info("\t(+) Positive match: " + record1.getId() + " / " + record2.getId());
    }

    @Override
    public void onNewMerge(Record record) {
        LOGGER.info("\t(+) New merge: " + record.getId() + " (groups " + record.getRelatedIds().size() + " records).");
    }

    @Override
    public void onRemoveMerge(Record record) {
        LOGGER.info("\t(-) Removed merge: " + record.getId());
    }

    @Override
    public void onDifferent(Record record1, Record record2, MatchResult matchResult) {
        LOGGER.info("\t(-) Negative match: " + record1.getId() + " / " + record2.getId());
    }

    @Override
    public void onEndRecord(Record record) {
        LOGGER.info("<- Record " + record.getId());
    }

    @Override
    public boolean isInterrupted() {
        return false;
    }

    @Override
    public void onBeginProcessing() {
        LOGGER.info("Begin match & merge.");
    }

    @Override
    public void onEndProcessing() {
        LOGGER.info("End match & merge.");
    }

    @Override
    public void onBeginPostMergeProcess() {
        LOGGER.info("Begin post processing for merge");
    }

    @Override
    public void onEndPostMergeProcess() {
        LOGGER.info("End post processing for merge");
    }
}
