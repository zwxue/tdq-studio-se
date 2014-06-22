/*
 * Copyright (C) 2006-2014 Talend Inc. - www.talend.com
 * 
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 * 
 * You should have received a copy of the agreement along with this program; if not, write to Talend SA 9 rue Pages
 * 92150 Suresnes, France
 */

/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package org.talend.dataquality.matchmerge;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.talend.dataquality.record.linkage.attribute.AttributeMatcherFactory;
import org.talend.dataquality.record.linkage.attribute.IAttributeMatcher;
import org.talend.dataquality.record.linkage.attribute.SubstringAttributeMatcher;
import org.talend.dataquality.record.linkage.constant.AttributeMatcherType;
import org.talend.dataquality.record.linkage.utils.CustomAttributeMatcherClassNameConvert;
import org.talend.dataquality.record.linkage.utils.SurvivorShipAlgorithmEnum;

public class MatchMerge {

    private static final double MAX_SCORE = 1.0;

    public static boolean equals(Record record1, Record record2) {
        Iterator<Attribute> r1 = record1.getAttributes().iterator();
        Iterator<Attribute> r2 = record2.getAttributes().iterator();
        while (r1.hasNext()) {
            AttributeValues<String> leftAttributeValues = r1.next().getValues();
            AttributeValues<String> rightAttributeValues = r2.next().getValues();
            if (!leftAttributeValues.equals(rightAttributeValues)) {
                return false;
            }
        }
        return true;
    }

    public static Record merge(Record record1, Record record2, SurvivorShipAlgorithmEnum[] typeMergeTable, String[] parameters,
            String mergedRecordSource) {
        List<Attribute> r1 = record1.getAttributes();
        List<Attribute> r2 = record2.getAttributes();
        // Takes most recent as timestamp for the merged record.
        long mergedRecordTimestamp = record1.getTimestamp() > record2.getTimestamp() ? record1.getTimestamp() : record2
                .getTimestamp();
        Record mergedRecord = new Record(record1.getId(), mergedRecordTimestamp, mergedRecordSource);
        for (int k = 0; k < r1.size(); k++) {
            Attribute a = new Attribute(r1.get(k).getLabel());
            mergedRecord.getAttributes().add(k, a);
        }
        for (int i = 0; i < r1.size(); i++) {
            Attribute mergedAttribute = mergedRecord.getAttributes().get(i);
            Attribute leftAttribute = r1.get(i);
            Attribute rightAttribute = r2.get(i);
            String leftValue = leftAttribute.getValue();
            String rightValue = rightAttribute.getValue();
            // Keep values from original records (if any)
            AttributeValues<String> leftValues = leftAttribute.getValues();
            if (leftValues.size() > 0) {
                mergedAttribute.getValues().merge(leftValues);
            } else {
                mergedAttribute.getValues().get(leftValue).increment();
            }
            AttributeValues<String> rightValues = rightAttribute.getValues();
            if (rightValues.size() > 0) {
                mergedAttribute.getValues().merge(rightValues);
            } else {
                mergedAttribute.getValues().get(rightValue).increment();
            }
            // Merge values
            if (leftValue == null && rightValue == null) {
                mergedAttribute.setValue(null);
            } else {
                SurvivorShipAlgorithmEnum survivorShipAlgorithmEnum = typeMergeTable[i];
                BigDecimal leftNumberValue;
                BigDecimal rightNumberValue;
                int leftValueLength = leftValue == null ? 0 : leftValue.length();
                int rightValueLength = rightValue == null ? 0 : rightValue.length();
                switch (survivorShipAlgorithmEnum) {
                case CONCATENATE:
                    mergedAttribute.setValue(leftValue + rightValue);
                    break;
                case LARGEST:
                    leftNumberValue = parseNumberValue(r1, i);
                    rightNumberValue = parseNumberValue(r2, i);
                    if (leftNumberValue.compareTo(rightNumberValue) >= 0) {
                        mergedAttribute.setValue(rightValue);
                    } else {
                        mergedAttribute.setValue(leftValue);
                    }
                    break;
                case SMALLEST:
                    leftNumberValue = parseNumberValue(r1, i);
                    rightNumberValue = parseNumberValue(r2, i);
                    if (leftNumberValue.compareTo(rightNumberValue) <= 0) {
                        mergedAttribute.setValue(rightValue);
                    } else {
                        mergedAttribute.setValue(leftValue);
                    }
                    break;
                case MOST_RECENT:
                    if (record1.getTimestamp() > record2.getTimestamp()) {
                        mergedAttribute.setValue(leftValue);
                    } else if (record1.getTimestamp() < record2.getTimestamp()) {
                        mergedAttribute.setValue(rightValue);
                    } else {
                        // Both r1 and r2 have same timestamp, return first value
                        mergedAttribute.setValue(leftValue);
                    }
                    break;
                case MOST_ANCIENT:
                    if (record1.getTimestamp() < record2.getTimestamp()) {
                        mergedAttribute.setValue(leftValue);
                    } else if (record1.getTimestamp() > record2.getTimestamp()) {
                        mergedAttribute.setValue(rightValue);
                    } else {
                        // Both r1 and r2 have same timestamp, return first value
                        mergedAttribute.setValue(leftValue);
                    }
                    break;
                case PREFER_TRUE:
                    if (!Boolean.parseBoolean(mergedAttribute.getValue()) && !Boolean.parseBoolean(mergedAttribute.getValue())) {
                        mergedAttribute.setValue("false"); //$NON-NLS-1$
                    } else {
                        mergedAttribute.setValue("true"); //$NON-NLS-1$
                    }
                    break;
                case PREFER_FALSE:
                    if (Boolean.parseBoolean(mergedAttribute.getValue()) && Boolean.parseBoolean(mergedAttribute.getValue())) {
                        mergedAttribute.setValue("true"); //$NON-NLS-1$
                    } else {
                        mergedAttribute.setValue("false"); //$NON-NLS-1$
                    }
                    break;
                case MOST_COMMON:
                    mergedAttribute.setValue(mergedAttribute.getValues().mostCommon());
                    break;
                case LONGEST:
                    if (leftValueLength > rightValueLength) {
                        mergedAttribute.setValue(leftValue);
                    } else if (leftValueLength < rightValueLength) {
                        mergedAttribute.setValue(rightValue);
                    } else {
                        // Same length and equals or same length
                        mergedAttribute.setValue(leftValue);
                    }
                    break;
                case SHORTEST:
                    if (leftValueLength < rightValueLength) {
                        mergedAttribute.setValue(leftValue);
                    } else if (leftValueLength > rightValueLength) {
                        mergedAttribute.setValue(rightValue);
                    } else {
                        // Same length and equals or same length
                        mergedAttribute.setValue(leftValue);
                    }
                    break;
                case MOST_TRUSTED_SOURCE:
                    String mostTrustedSourceName = parameters[i];
                    if (mostTrustedSourceName == null) {
                        throw new IllegalStateException("Survivorship 'most trusted source' must specify a trusted source.");
                    }
                    if (mostTrustedSourceName.equals(record1.getSource())) {
                        mergedAttribute.setValue(leftValue);
                    } else if (mostTrustedSourceName.equals(record2.getSource())) {
                        mergedAttribute.setValue(rightValue);
                    } else {
                        // r1 and r2 are not from a trusted source, return first value
                        mergedAttribute.setValue(leftValue);
                    }
                    break;
                }
            }
        }
        mergedRecord.setRelatedIds(new HashSet<String>(record1.getRelatedIds().size() + record2.getRelatedIds().size() + 2));
        mergedRecord.getRelatedIds().add(record1.getId());
        mergedRecord.getRelatedIds().add(record2.getId());
        mergedRecord.getRelatedIds().addAll(record1.getRelatedIds());
        mergedRecord.getRelatedIds().addAll(record2.getRelatedIds());
        // Conservative strategy -> keeps the lowest confidence to avoid over-confidence in a group with many
        // low-confidence records.
        mergedRecord.setConfidence(Math.min(record1.getConfidence(), record2.getConfidence()));
        if (record1.getGroupId() != null) {
            mergedRecord.setGroupId(record1.getGroupId());
        } else if (record2.getGroupId() != null) {
            mergedRecord.setGroupId(record2.getGroupId());
        }
        return mergedRecord;
    }

    private static BigDecimal parseNumberValue(List<Attribute> record, int i) {
        String value = record.get(i).getValue();
        return value == null || value.isEmpty() ? BigDecimal.ZERO : new BigDecimal(value);
    }

    public static double matchScore(Iterator<String> leftValues, Iterator<String> rightValues, AttributeMatcherType algorithm,
            String parameter, IAttributeMatcher.NullOption nullOption, SubString subString) {
        // Create matcher based on algorithm
        IAttributeMatcher matcher;
        if (algorithm == AttributeMatcherType.CUSTOM) {
            try {
                String className = CustomAttributeMatcherClassNameConvert.getClassName(parameter);
                matcher = AttributeMatcherFactory.createMatcher(algorithm, className);
            } catch (Exception e) {
                throw new RuntimeException("Could not initialize custom matcher '" + parameter + "'.", e);
            }
            if (matcher == null) {
                throw new IllegalStateException("Could not initialize matcher '" + algorithm + "' (class '" + parameter
                        + "' can't be found).");
            }
        } else {
            matcher = AttributeMatcherFactory.createMatcher(algorithm);
        }
        // Null match options
        matcher.setNullOption(nullOption);
        // Sub string
        if (subString.needSubStringOperation()) {
            matcher = SubstringAttributeMatcher.decorate(matcher, subString.getBeginIndex(), subString.getEndIndex());
        }
        // Sanity check
        if (matcher == null) {
            throw new IllegalStateException("Could not initialize matcher '" + algorithm + "' (not recognized as valid choice).");
        }
        // Find the best score in values
        double maxScore = 0;
        while (leftValues.hasNext()) {
            String leftValue = leftValues.next();
            while (rightValues.hasNext()) {
                String rightValue = rightValues.next();
                double score = matcher.getMatchingWeight(leftValue, rightValue);
                if (score > maxScore) {
                    maxScore = score;
                }
                if (maxScore == MAX_SCORE) {
                    // Can't go higher, no need to perform other checks.
                    return maxScore;
                }
            }
        }
        return maxScore;
    }
}
