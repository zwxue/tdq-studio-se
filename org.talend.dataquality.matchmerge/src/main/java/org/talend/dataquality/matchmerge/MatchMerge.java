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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.talend.dataquality.matchmerge;

import org.talend.dataquality.record.linkage.attribute.AttributeMatcherFactory;
import org.talend.dataquality.record.linkage.attribute.IAttributeMatcher;
import org.talend.dataquality.record.linkage.attribute.SubstringAttributeMatcher;
import org.talend.dataquality.record.linkage.constant.AttributeMatcherType;
import org.talend.dataquality.record.linkage.utils.SurvivorShipAlgorithmEnum;

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

    public static Record merge(Record record1, Record record2, SurvivorShipAlgorithmEnum[] typeMergeTable) {
        List<Attribute> r1 = record1.getAttributes();
        List<Attribute> r2 = record2.getAttributes();
        Record mergedRecord = new Record(record1.getId(), System.currentTimeMillis()); // TODO What's the best timestamp for a merged record?
        for (int k = 0; k < r1.size(); k++) {
            Attribute a = new Attribute(r1.get(k).getLabel(), null);
            mergedRecord.getAttributes().add(k, a);
        }
        for (int i = 0; i < r1.size(); i++) {
            if (r1.get(i).getValue() == null && r2.get(i).getValue() == null) {
                mergedRecord.getAttributes().get(i).setValue(r1.get(i).getValue());
            } else {
                if (r1.get(i).getValue() == null || "null".equals(r1.get(i).getValue()) || r1.get(i).getValue().length() == 0) { //$NON-NLS-1$
                    mergedRecord.getAttributes().get(i).setValue(r2.get(i).getValue());
                } else {
                    if (r2.get(i).getValue() == null || "null".equals(r2.get(i).getValue()) || r2.get(i).getValue().length() == 0) { //$NON-NLS-1$
                        mergedRecord.getAttributes().get(i).setValue(r1.get(i).getValue());
                    } else {
                        if (r1.get(i).getValue().equals(r2.get(i).getValue())) {
                            mergedRecord.getAttributes().get(i).setValue(r1.get(i).getValue());
                        } else {
                            switch (typeMergeTable[i]) {
                                case CONCATENATE:
                                    mergedRecord.getAttributes().get(i).setValue(r1.get(i).getValue() + r2.get(i).getValue());
                                    break;
                                case LARGEST:
                                    if (Integer.parseInt(r1.get(i).getValue()) <= Integer.parseInt(r2.get(i).getValue())) {
                                        mergedRecord.getAttributes().get(i).setValue(r2.get(i).getValue());
                                    } else {
                                        mergedRecord.getAttributes().get(i).setValue(r1.get(i).getValue());
                                    }
                                    break;
                                case SMALLEST:
                                    if (Integer.parseInt(r1.get(i).getValue()) <= Integer.parseInt(r2.get(i).getValue())) {
                                        mergedRecord.getAttributes().get(i).setValue(r1.get(i).getValue());
                                    } else {
                                        mergedRecord.getAttributes().get(i).setValue(r2.get(i).getValue());
                                    }
                                    break;
                                case MOST_RECENT:
                                    if(record1.getTimestamp() > record2.getTimestamp()) {
                                        mergedRecord.getAttributes().get(i).setValue(r1.get(i).getValue());
                                    } else if(record1.getTimestamp() < record2.getTimestamp()) {
                                        mergedRecord.getAttributes().get(i).setValue(r2.get(i).getValue());
                                    } else {
                                        // Both r1 and r2 have same timestamp, concatenate both to preserve data
                                        mergedRecord.getAttributes().get(i).setValue(r1.get(i).getValue() + r2.get(i).getValue());
                                    }
                                    break;
                                case MOST_ANCIENT:
                                    if(record1.getTimestamp() < record2.getTimestamp()) {
                                        mergedRecord.getAttributes().get(i).setValue(r1.get(i).getValue());
                                    } else if(record1.getTimestamp() > record2.getTimestamp()) {
                                        mergedRecord.getAttributes().get(i).setValue(r2.get(i).getValue());
                                    } else {
                                        // Both r1 and r2 have same timestamp, concatenate both to preserve data
                                        mergedRecord.getAttributes().get(i).setValue(r1.get(i).getValue() + r2.get(i).getValue());
                                    }
                                    break;
                                case FORCE_TRUE:
                                    mergedRecord.getAttributes().get(i).setValue("true"); //$NON-NLS-1$
                                    break;
                                case FORCE_FALSE:
                                    mergedRecord.getAttributes().get(i).setValue("false"); //$NON-NLS-1$
                                    break;
                                case MOST_COMMON:
                                    mergedRecord.getAttributes().get(i).addValue(r1.get(i).getValue());
                                    mergedRecord.getAttributes().get(i).addValue(r2.get(i).getValue());
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
                                            // Both r1 and r2 have same length, concatenate both to preserve data
                                            mergedRecord.getAttributes().get(i).setValue(r1.get(i).getValue() + r2.get(i).getValue());
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
                                            // Both r1 and r2 have same length, concatenate both to preserve data
                                            mergedRecord.getAttributes().get(i).setValue(r1.get(i).getValue() + r2.get(i).getValue());
                                        }
                                    }
                                    break;
                            }
                        }
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
        return mergedRecord;
    }

    public static double matchScore(Attribute attribute0,
                                    Attribute attribute1,
                                    AttributeMatcherType algorithm,
                                    IAttributeMatcher.NullOption nullOption,
                                    SubString subString) {
        String leftValue = attribute0.getValue();
        String rightValue = attribute1.getValue();
        IAttributeMatcher matcher = AttributeMatcherFactory.createMatcher(algorithm);
        // Null match options
        matcher.setNullOption(nullOption);
        // Sub string
        if (subString.needSubStringOperation()) {
            matcher = SubstringAttributeMatcher.decorate(matcher, subString.getBeginIndex(), subString.getEndIndex());
        }
        return matcher.getMatchingWeight(leftValue, rightValue);
    }
}
