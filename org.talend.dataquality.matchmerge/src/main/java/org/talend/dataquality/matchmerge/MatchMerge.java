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
import org.talend.dataquality.record.linkage.constant.AttributeMatcherType;

import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


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

    public static Record merge(Record record1, Record record2, MergeAlgorithm[] typeMergeTable) {
        List<Attribute> r1 = record1.getAttributes();
        List<Attribute> r2 = record2.getAttributes();
        Record mergedRecord = new Record(record1.getId());
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
                                case CONCAT:
                                    mergedRecord.getAttributes().get(i).setValue(r1.get(i).getValue() + r2.get(i).getValue());
                                    break;
                                case UNIFY:
                                    Pattern pattern = Pattern.compile("(\\D+)\\1$"); //$NON-NLS-1$
                                    if (r1.get(i).getValue().length() <= r2.get(i).getValue().length()) {
                                        Matcher matcher = pattern.matcher(r2.get(i).getValue());
                                        if (!matcher.find()) {
                                            mergedRecord.getAttributes().get(i).setValue(r2.get(i).getValue());
                                        } else {
                                            mergedRecord.getAttributes().get(i).setValue(r1.get(i).getValue());
                                        }
                                    } else {
                                        Matcher matcher = pattern.matcher(r1.get(i).getValue());
                                        if (!matcher.find()) {
                                            mergedRecord.getAttributes().get(i).setValue(r1.get(i).getValue());
                                        } else {
                                            mergedRecord.getAttributes().get(i).setValue(r2.get(i).getValue());
                                        }
                                    }
                                    break;
                                case MAX:
                                    if (Integer.parseInt(r1.get(i).getValue()) <= Integer.parseInt(r2.get(i).getValue())) {
                                        mergedRecord.getAttributes().get(i).setValue(r2.get(i).getValue());
                                    } else {
                                        mergedRecord.getAttributes().get(i).setValue(r1.get(i).getValue());
                                    }
                                    break;
                                case MIN:
                                    if (Integer.parseInt(r1.get(i).getValue()) <= Integer.parseInt(r2.get(i).getValue())) {
                                        mergedRecord.getAttributes().get(i).setValue(r1.get(i).getValue());
                                    } else {
                                        mergedRecord.getAttributes().get(i).setValue(r2.get(i).getValue());
                                    }
                                    break;
                                case MEAN:
                                    mergedRecord.getAttributes().get(i).setValue(Integer.toString((Integer.parseInt(r1.get(i).getValue())) + (Integer.parseInt(r2.get(i).getValue())) / 2));
                                    break;
                                case SUM:
                                    mergedRecord.getAttributes().get(i).setValue(Integer.toString((Integer.parseInt(r1.get(i).getValue())) + (Integer.parseInt(r2.get(i).getValue()))));
                                    break;
                                case MOST_RECENT:
                                    if (r1.get(i).getValue().compareTo(r2.get(i).getValue()) <= 0) {
                                        mergedRecord.getAttributes().get(i).setValue(r2.get(i).getValue());
                                    } else {
                                        mergedRecord.getAttributes().get(i).setValue(r1.get(i).getValue());
                                    }
                                    break;
                                case OLDEST_DATE:
                                    if (r1.get(i).getValue().compareTo(r2.get(i).getValue()) <= 0) {
                                        mergedRecord.getAttributes().get(i).setValue(r1.get(i).getValue());
                                    } else {
                                        mergedRecord.getAttributes().get(i).setValue(r2.get(i).getValue());
                                    }
                                case REPEATED_VALUES:
                                    mergedRecord.getAttributes().get(i).setValue(r2.get(i).getValue());
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
        mergedRecord.setConfidence(Math.min(record1.getConfidence(), record2.getConfidence()));
        return mergedRecord;
    }

    public static double matchScore(Attribute attribute0, Attribute attribute1, MatchAlgorithm algorithm) {
        String leftValue = attribute0.getValue();
        String rightValue = attribute1.getValue();
        IAttributeMatcher matcher;
        switch (algorithm) {
            case LEVENSHTEIN:
                matcher = AttributeMatcherFactory.createMatcher(AttributeMatcherType.levenshtein);
                break;
            case JAROWINKLER:
                matcher = AttributeMatcherFactory.createMatcher(AttributeMatcherType.jaroWinkler);
                break;
            case JACCARD:
                throw new RuntimeException("Not supported " + algorithm);
            case SOUNDEX:
                matcher = AttributeMatcherFactory.createMatcher(AttributeMatcherType.soundex);
                break;
            case DOUBLE_METAPHONE:
                matcher = AttributeMatcherFactory.createMatcher(AttributeMatcherType.doubleMetaphone);
                break;
            case DIFFERENCE:
                matcher = AttributeMatcherFactory.createMatcher(AttributeMatcherType.exact);
                break;
            case DATE_COMPARE_YEAR:
                throw new RuntimeException("Not supported " + algorithm);
            case DATE_COMPARE_YEAR_MONTH:
                throw new RuntimeException("Not supported " + algorithm);
            case DATE_COMPARE_YEAR_MONTH_DAY:
                throw new RuntimeException("Not supported " + algorithm);
            case COMPARE_DOUBLE:
                return leftValue.equals(rightValue) ? 1 : 0;
            default:
                throw new RuntimeException("Not supported " + algorithm);
        }
        return matcher.getMatchingWeight(leftValue, rightValue);
    }
}
