package org.talend.dataquality.matchmerge.mfb;

import java.util.Iterator;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.talend.dataquality.matchmerge.Attribute;
import org.talend.dataquality.matchmerge.Record;
import org.talend.dataquality.record.linkage.attribute.IAttributeMatcher;
import org.talend.dataquality.record.linkage.record.AbstractRecordMatcher;

public class MFBRecordMatcher extends AbstractRecordMatcher {

    private static final Logger LOGGER = Logger.getLogger(MFBRecordMatcher.class);

    private static final double MAX_SCORE = 1;

    private final double minConfidenceValue;

    public MFBRecordMatcher(double minConfidenceValue) {
        this.minConfidenceValue = minConfidenceValue;
    }

    @Override
    public double getMatchingWeight(String[] record1, String[] record2) {
        return getMatchingWeight(buildRecord(record1), buildRecord(record2)).getNormalizedConfidence();
    }

    private static Record buildRecord(String[] values) {
        Record record = new Record(null, 0, StringUtils.EMPTY);
        int i = 0;
        for (String value : values) {
            Attribute attribute = new Attribute(String.valueOf(i++));
            attribute.setValue(value);
            record.getAttributes().add(attribute);
        }
        return record;
    }

    @Override
    public MatchResult getMatchingWeight(Record record1, Record record2) {
        Iterator<Attribute> mergedRecordAttributes = record1.getAttributes().iterator();
        Iterator<Attribute> currentRecordAttributes = record2.getAttributes().iterator();
        double confidence = 0;
        int matchIndex = 0;
        MatchResult result = new MatchResult(record1.getAttributes().size());
        int maxWeight = 0;
        while (mergedRecordAttributes.hasNext()) {
            Attribute left = mergedRecordAttributes.next();
            Attribute right = currentRecordAttributes.next();
            IAttributeMatcher matcher = attributeMatchers[matchIndex];
            // Find the first score to exceed threshold (if any).
            double score = matchScore(left, right, matcher);
            attributeMatchingWeights[matchIndex] = score;
            result.setScore(matchIndex, matcher.getMatchType(), score, record1.getId(), left.getValue(), record2.getId(),
                    right.getValue());
            result.setThreshold(matchIndex, matcher.getThreshold());
            confidence += score * matcher.getWeight();
            maxWeight += matcher.getWeight();
            matchIndex++;
        }
        double normalizedConfidence = confidence > 0 ? confidence / maxWeight : confidence; // Normalize to 0..1 value
        result.setConfidence(normalizedConfidence);
        if (normalizedConfidence < minConfidenceValue) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Cannot match record: merged record has a too low confidence value (" + normalizedConfidence + " < "
                        + minConfidenceValue + ")");
            }
            return MFB.NonMatchResult.wrap(result);
        }
        record2.setConfidence(normalizedConfidence);
        return result;
    }

    private static double matchScore(Attribute leftAttribute, Attribute rightAttribute, IAttributeMatcher matcher) {
        // Find the best score in values
        // 1- Try first values
        String left = leftAttribute.getValue();
        String right = rightAttribute.getValue();
        double score = matcher.getMatchingWeight(left, right);
        if (score >= matcher.getThreshold()) {
            return score;
        }
        // 2- Compare using values that build attribute value (if any)
        Iterator<String> leftValues = leftAttribute.getValues().iterator();
        Iterator<String> rightValues = rightAttribute.getValues().iterator();
        double maxScore = 0;
        String leftValue = left;
        while (leftValues.hasNext()) {
            leftValue = leftValues.next();
            while (rightValues.hasNext()) {
                String rightValue = rightValues.next();
                score = matcher.getMatchingWeight(leftValue, rightValue);
                if (score > maxScore) {
                    maxScore = score;
                }
                if (maxScore == MAX_SCORE) {
                    // Can't go higher, no need to perform other checks.
                    return maxScore;
                }
            }
        }
        // Process remaining values in right (if any).
        while (rightValues.hasNext()) {
            String rightValue = rightValues.next();
            score = matcher.getMatchingWeight(leftValue, rightValue);
            if (score > maxScore) {
                maxScore = score;
            }
            if (maxScore == MAX_SCORE) {
                // Can't go higher, no need to perform other checks.
                return maxScore;
            }
        }
        return maxScore;
    }

}
