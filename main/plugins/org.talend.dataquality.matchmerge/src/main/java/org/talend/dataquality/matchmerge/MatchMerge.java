/*
 * Copyright (C) 2006-2015 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 9 rue Pages 92150 Suresnes, France
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.talend.dataquality.matchmerge;

import org.apache.commons.lang.StringUtils;
import org.talend.dataquality.matchmerge.mfb.MFB;
import org.talend.dataquality.record.linkage.attribute.AttributeMatcherFactory;
import org.talend.dataquality.record.linkage.attribute.IAttributeMatcher;
import org.talend.dataquality.record.linkage.attribute.SubstringAttributeMatcher;
import org.talend.dataquality.record.linkage.constant.AttributeMatcherType;
import org.talend.dataquality.record.linkage.utils.CustomAttributeMatcherClassNameConvert;
import org.talend.dataquality.record.linkage.utils.SurvivorShipAlgorithmEnum;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


public class MatchMerge {

    public static boolean equals(Record record1, Record record2) {
        List<Attribute> r1 = record1.getAttributes();
        List<Attribute> r2 = record2.getAttributes();
        int i = 0;
        boolean find = true;
        while (i < r1.size() && find) {
            find = false;
            if ((r1.get(i).getValue() == null || r2.get(i).getValue() == null)) {
                find = r1.get(i).getValue() == null && r2.get(i).getValue() == null;
            } else {
                if (r1.get(i).getValue().equals(r2.get(i).getValue())) {
                    find = true;
                }
            }
            i++;
        }
        return find;
    }

    public static Record merge(Record record1,
                               Record record2,
                               SurvivorShipAlgorithmEnum[] typeMergeTable,
                               String[] parameters,
                               String mergedRecordSource) {
        List<Attribute> r1 = record1.getAttributes();
        List<Attribute> r2 = record2.getAttributes();
        // Takes most recent as timestamp for the merged record.
        long mergedRecordTimestamp = record1.getTimestamp() > record2.getTimestamp() ? record1.getTimestamp() : record2.getTimestamp();
        Record mergedRecord = new Record(record1.getId(), mergedRecordTimestamp, mergedRecordSource);
        for (int k = 0; k < r1.size(); k++) {
            Attribute a = new Attribute(r1.get(k).getLabel(), null);
            mergedRecord.getAttributes().add(k, a);
        }
        for (int i = 0; i < r1.size(); i++) {
            if (r1.get(i).getValue() == null && r2.get(i).getValue() == null) {
                mergedRecord.getAttributes().get(i).setValue(r1.get(i).getValue());
            } else {
                if (StringUtils.equals(r1.get(i).getValue(), r2.get(i).getValue())) {
                    if (typeMergeTable[i] == SurvivorShipAlgorithmEnum.MOST_COMMON) {
                        mergedRecord.getAttributes().get(i).getValues().addAll(r1.get(i).getValues());
                    }
                    mergedRecord.getAttributes().get(i).setValue(r1.get(i).getValue());
                } else {
                    BigDecimal r1Value;
                    BigDecimal r2Value;
                    switch (typeMergeTable[i]) {
                        case CONCATENATE:
                            mergedRecord.getAttributes().get(i).setValue(r1.get(i).getValue() + r2.get(i).getValue());
                            break;
                        case LARGEST:
                            r1Value = parseNumberValue(r1, i);
                            r2Value = parseNumberValue(r2, i);
                            if (r1Value.compareTo(r2Value) >= 0) {
                                mergedRecord.getAttributes().get(i).setValue(r2.get(i).getValue());
                            } else {
                                mergedRecord.getAttributes().get(i).setValue(r1.get(i).getValue());
                            }
                            break;
                        case SMALLEST:
                            r1Value = parseNumberValue(r1, i);
                            r2Value = parseNumberValue(r2, i);
                            if (r1Value.compareTo(r2Value) <= 0) {
                                mergedRecord.getAttributes().get(i).setValue(r2.get(i).getValue());
                            } else {
                                mergedRecord.getAttributes().get(i).setValue(r1.get(i).getValue());
                            }
                            break;
                        case MOST_RECENT:
                            if (record1.getTimestamp() > record2.getTimestamp()) {
                                mergedRecord.getAttributes().get(i).setValue(r1.get(i).getValue());
                            } else if (record1.getTimestamp() < record2.getTimestamp()) {
                                mergedRecord.getAttributes().get(i).setValue(r2.get(i).getValue());
                            } else {
                                // Both r1 and r2 have same timestamp, return first value
                                mergedRecord.getAttributes().get(i).setValue(r1.get(i).getValue());
                            }
                            break;
                        case MOST_ANCIENT:
                            if (record1.getTimestamp() < record2.getTimestamp()) {
                                mergedRecord.getAttributes().get(i).setValue(r1.get(i).getValue());
                            } else if (record1.getTimestamp() > record2.getTimestamp()) {
                                mergedRecord.getAttributes().get(i).setValue(r2.get(i).getValue());
                            } else {
                                // Both r1 and r2 have same timestamp, return first value
                                mergedRecord.getAttributes().get(i).setValue(r1.get(i).getValue());
                            }
                            break;
                        case PREFER_TRUE:
                            if (!Boolean.parseBoolean(mergedRecord.getAttributes().get(i).getValue())
                                    && !Boolean.parseBoolean(mergedRecord.getAttributes().get(i).getValue())) {
                                mergedRecord.getAttributes().get(i).setValue("false"); //$NON-NLS-1$
                            } else {
                                mergedRecord.getAttributes().get(i).setValue("true"); //$NON-NLS-1$
                            }
                            break;
                        case PREFER_FALSE:
                            if (Boolean.parseBoolean(mergedRecord.getAttributes().get(i).getValue())
                                    && Boolean.parseBoolean(mergedRecord.getAttributes().get(i).getValue())) {
                                mergedRecord.getAttributes().get(i).setValue("true"); //$NON-NLS-1$
                            } else {
                                mergedRecord.getAttributes().get(i).setValue("false"); //$NON-NLS-1$
                            }
                            break;
                        case MOST_COMMON:
                            List<String> r1Values = r1.get(i).getValues();
                            List<String> r2Values = r2.get(i).getValues();
                            List<String> unionValues = new ArrayList<String>(r1Values.size() + r2Values.size());
                            unionValues.addAll(r1Values);
                            unionValues.addAll(r2Values);
                            mergedRecord.getAttributes().get(i).getValues().addAll(unionValues);
                            String mostCommonValue = MFB.getMostCommonValue(unionValues);
                            mergedRecord.getAttributes().get(i).setValue(mostCommonValue);
                            break;
                        case LONGEST:
                            if (r1.get(i).getValue().length() > r2.get(i).getValue().length()) {
                                mergedRecord.getAttributes().get(i).setValue(r1.get(i).getValue());
                            } else if (r1.get(i).getValue().length() < r2.get(i).getValue().length()) {
                                mergedRecord.getAttributes().get(i).setValue(r2.get(i).getValue());
                            } else {
                                if (r1.get(i).getValue().equals(r2.get(i).getValue())) {
                                    // Same length and equals
                                    mergedRecord.getAttributes().get(i).setValue(r1.get(i).getValue());
                                } else {
                                    // Both r1 and r2 have same length, return first value
                                    mergedRecord.getAttributes().get(i).setValue(r1.get(i).getValue());
                                }
                            }
                            break;
                        case SHORTEST:
                            if (r1.get(i).getValue().length() < r2.get(i).getValue().length()) {
                                mergedRecord.getAttributes().get(i).setValue(r1.get(i).getValue());
                            } else if (r1.get(i).getValue().length() > r2.get(i).getValue().length()) {
                                mergedRecord.getAttributes().get(i).setValue(r2.get(i).getValue());
                            } else {
                                if (r1.get(i).getValue().equals(r2.get(i).getValue())) {
                                    // Same length and equals
                                    mergedRecord.getAttributes().get(i).setValue(r1.get(i).getValue());
                                } else {
                                    // Both r1 and r2 have same length, return first value
                                    mergedRecord.getAttributes().get(i).setValue(r1.get(i).getValue());
                                }
                            }
                            break;
                        case MOST_TRUSTED_SOURCE:
                            String mostTrustedSourceName = parameters[i];
                            if (mostTrustedSourceName == null) {
                                throw new IllegalStateException("Survivorship 'most trusted source' must specify a trusted source.");
                            }
                            if (mostTrustedSourceName.equals(record1.getSource())) {
                                mergedRecord.getAttributes().get(i).setValue(r1.get(i).getValue());
                            } else if (mostTrustedSourceName.equals(record2.getSource())) {
                                mergedRecord.getAttributes().get(i).setValue(r2.get(i).getValue());
                            } else {
                                // r1 and r2 are not from a trusted source, return first value
                                mergedRecord.getAttributes().get(i).setValue(r1.get(i).getValue());
                            }
                            break;
                    }
                }
            }
        }
        mergedRecord.setRelatedIds(new HashSet<String>(record1.getRelatedIds().size() + record2.getRelatedIds().size() + 2));
        mergedRecord.getRelatedIds().add(record1.getId());
        mergedRecord.getRelatedIds().add(record2.getId());
        mergedRecord.getRelatedIds().addAll(record1.getRelatedIds());
        mergedRecord.getRelatedIds().addAll(record2.getRelatedIds());
        // Conservative strategy -> keeps the lowest confidence to avoid over-confidence in a group with many low-confidence
        // records.
        mergedRecord.setConfidence(Math.min(record1.getConfidence(), record2.getConfidence()));
        if (record1.getGroupId() != null) {
            mergedRecord.setGroupId(record1.getGroupId());
        } else if (record2.getGroupId() != null) {
            mergedRecord.setGroupId(record2.getGroupId());
        } else if (record1.getGroupId() != null && record2.getGroupId() != null) {
            throw new IllegalStateException("Trying to merge two groups together.");
        }
        return mergedRecord;
    }

    private static BigDecimal parseNumberValue(List<Attribute> record, int i) {
        String value = record.get(i).getValue();
        return value == null || value.isEmpty() ? BigDecimal.ZERO : new BigDecimal(value);
    }

    public static double matchScore(Attribute attribute0,
                                    Attribute attribute1,
                                    AttributeMatcherType algorithm,
                                    String parameter,
                                    IAttributeMatcher.NullOption nullOption,
                                    SubString subString) {
        String leftValue = attribute0.getValue();
        String rightValue = attribute1.getValue();
        IAttributeMatcher matcher;
        if (algorithm == AttributeMatcherType.CUSTOM) {
            try {
                String className=CustomAttributeMatcherClassNameConvert.getClassName(parameter);
                matcher = AttributeMatcherFactory.createMatcher(algorithm, className);
            } catch (Exception e) {
                throw new RuntimeException("Could not initialize custom matcher '" + parameter + "'.", e);
            }
            if(matcher == null) {
                throw new IllegalStateException("Could not initialize matcher '" + algorithm + "' (class '" + parameter + "' can't be found).");
            }
        } else {
            matcher = AttributeMatcherFactory.createMatcher(algorithm);
        }
        // Sanity check
        if (matcher == null) {
            throw new IllegalStateException("Could not initialize matcher '" + algorithm + "' (not recognized as valid choice).");
        }
        // Null match options
        matcher.setNullOption(nullOption);
        // Sub string
        if (subString.needSubStringOperation()) {
            matcher = SubstringAttributeMatcher.decorate(matcher, subString.getBeginIndex(), subString.getEndIndex());
        }
        return matcher.getMatchingWeight(leftValue, rightValue);
    }
}
