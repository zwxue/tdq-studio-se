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

import org.talend.dataquality.record.linkage.constant.AttributeMatcherType;

import java.util.LinkedList;
import java.util.List;

public class MatchResult {

    private final List<Score> scores = new LinkedList<Score>();

    private final List<Float> thresholds = new LinkedList<Float>();

    public static class Score {
        public AttributeMatcherType algorithm;
        public double score;
    }

    public List<Score> getScores() {
        return scores;
    }

    public List<Float> getThresholds() {
        return thresholds;
    }

    public void setScore(int index, AttributeMatcherType algorithm, double score) {
        while (index >= scores.size()) {
            scores.add(new Score());
        }
        Score currentScore = scores.get(index);
        currentScore.algorithm = algorithm;
        currentScore.score = score;
    }

    public void setThreshold(int index, float threshold) {
        while (index >= thresholds.size()) {
            thresholds.add(0f);
        }
        thresholds.set(index, threshold);
    }

    public boolean isMatch() {
        int i = 0;
        for (Score score : scores) {
            if (score.score < thresholds.get(i++)) {
                return false;
            }
        }
        return true;
    }
}
