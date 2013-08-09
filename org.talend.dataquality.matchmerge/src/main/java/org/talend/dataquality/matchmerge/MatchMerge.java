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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MatchMerge {

    public static boolean equals(Record record1, Record record2) {
        List<Attribute> r1 = record1.getAttributes();
        List<Attribute> r2 = record2.getAttributes();
        int i = 0;
        int j;
        Vector<String> l1 = new Vector<String>();
        Vector<String> l2 = new Vector<String>();
        String[] s1;
        String[] s2;
        Boolean find = true;
        while (i < r1.size() && find) {
            find = false;
            if ((r1.get(i).getValue() == null || r2.get(i).getValue() == null)) {
                find = r1.get(i).getValue() == null && r2.get(i).getValue() == null;
            } else {
                if (r1.get(i).getValue().equals(r2.get(i).getValue())) {
                    find = true;
                } else {
                    if (r1.get(i).getValue().indexOf(',') > 0 && r2.get(i).getValue().indexOf(',') > 0) {
                        StringTokenizer st1, st2;
                        st1 = new StringTokenizer(r1.get(i).getValue(), ",");
                        st2 = new StringTokenizer(r2.get(i).getValue(), ",");
                        s1 = new String[st1.countTokens()];
                        s2 = new String[st2.countTokens()];
                        j = 0;
                        while (st1.hasMoreTokens()) {
                            s1[j] = st1.nextToken();
                            j++;
                        }
                        for (j = 0; j < s1.length; j++)
                            l1.add(s1[j]);
                        j = 0;
                        while (st2.hasMoreTokens()) {
                            s2[j] = st2.nextToken();
                            j++;
                        }
                        for (j = 0; j < s2.length; j++) {
                            l2.add(s2[j]);
                        }
                        if (l1.containsAll(l2)) {
                            find = true;
                        }
                    } else {
                        find = false;
                    }
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
        String[] s1;
        String[] s2;
        String s;
        Boolean find;
        int i, j;
        for (i = 0; i < r1.size(); i++) {
            if (mergedRecord.getAttributes().get(i).getLabel().equals("Fusion")) {
                String valueFusion = addValueToFusion(r2.get(i).getValue(), r1.get(i).getValue());
                mergedRecord.getAttributes().get(i).setValue(valueFusion);
                continue;
            }
            if (r1.get(i).getValue() == null && r2.get(i).getValue() == null)
                mergedRecord.getAttributes().get(i).setValue(r1.get(i).getValue());

            else {
                if (r1.get(i).getValue() == null || r1.get(i).getValue().equals("null") || r1.get(i).getValue().length() == 0)
                    mergedRecord.getAttributes().get(i).setValue(r2.get(i).getValue());
                else {
                    if (r2.get(i).getValue() == null || r2.get(i).getValue().equals("null") || r2.get(i).getValue().length() == 0)
                        mergedRecord.getAttributes().get(i).setValue(r1.get(i).getValue());
                    else {
                        if (r1.get(i).getValue().equals(r2.get(i).getValue())) {
                            mergedRecord.getAttributes().get(i).setValue(r1.get(i).getValue());
                        } else {
                            switch (typeMergeTable[i]) {
                                case CONCAT:
                                    StringTokenizer st1, st2;
                                    if (r1.get(i).getValue().contains(",")) {
                                        st1 = new StringTokenizer(r1.get(i).getValue(), ",");
                                    } else {
                                        st1 = new StringTokenizer(r1.get(i).getValue());
                                    }
                                    if (r2.get(i).getValue().contains(",")) {
                                        st2 = new StringTokenizer(r2.get(i).getValue(), ",");
                                    } else {
                                        st2 = new StringTokenizer(r2.get(i).getValue());
                                    }
                                    s1 = new String[st1.countTokens()];
                                    s2 = new String[st2.countTokens()];

                                    j = 0;
                                    while (st1.hasMoreTokens()) {
                                        s1[j] = st1.nextToken();
                                        j++;
                                    }
                                    j = 0;
                                    while (st2.hasMoreTokens()) {
                                        s2[j] = st2.nextToken();
                                        j++;
                                    }
                                    s = r1.get(i).getValue();
                                    int m;
                                    for (m = 0; m < s2.length; m++) {
                                        find = false;
                                        for (j = 0; j < s1.length; j++) {
                                            if (s2[m].equals(s1[j])) {
                                                find = true;
                                                j = s1.length;
                                            }
                                        }
                                        if (find != null && !find)
                                            s = s + "," + s2[m];
                                    }
                                    mergedRecord.getAttributes().get(i).setValue(s);
                                    break;
                                case UNIFY:
                                    Pattern pattern = Pattern.compile("(\\D+)\\1$");
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
        return mergedRecord;
    }

    public static boolean match(Attribute get, Attribute get0, MatchAlgorithm algorithm, float threshold) {
        String leftValue = get.getValue();
        String rightValue = get0.getValue();
        if (leftValue == null || rightValue == null) {
            return false;
        }
        if (leftValue.equals("null") || rightValue.equals("null")) {
            return false;
        }
        boolean leftContainsComma = leftValue.indexOf(',') > 0;
        boolean  rightContainsComma = rightValue.indexOf(',') > 0;
        if (leftContainsComma || rightContainsComma) {
            int j = 0;
            Vector<String> list = new Vector<String>();
            Vector<String> list2 = new Vector<String>();
            if (leftContainsComma && rightContainsComma) {
                StringTokenizer st1 = new StringTokenizer(rightValue, ",");
                String[] s1 = new String[st1.countTokens()];
                StringTokenizer st2 = new StringTokenizer(leftValue, ",");
                String[] s2 = new String[st2.countTokens()];
                while (st1.hasMoreTokens()) {
                    s1[j] = st1.nextToken();
                    j++;
                }
                for (j = 0; j < s1.length; j++) {
                    list.add(s1[j]);
                }
                j = 0;
                while (st2.hasMoreTokens()) {
                    s2[j] = st2.nextToken();
                    j++;
                }
                for (j = 0; j < s2.length; j++)
                    list2.add(s2[j]);

                if (list2.size() < list.size()) {
                    if (list.containsAll(list2)) {
                        return true;
                    }
                }
                if (list.size() <= list2.size()) {
                    if (list2.containsAll(list)) {
                        return true;
                    }
                }
            }
            if (leftContainsComma) {
                StringTokenizer st1 = new StringTokenizer(leftValue, ",");
                String[] s = new String[st1.countTokens()];
                j = 0;
                while (st1.hasMoreTokens()) {
                    s[j] = st1.nextToken();
                    j++;
                }
                for (j = 0; j < s.length; j++) {
                    list.add(s[j]);
                }
                if (list.contains(rightValue)) {
                    return true;
                }
            } else {
                StringTokenizer st1 = new StringTokenizer(rightValue, ",");
                String[] s = new String[st1.countTokens()];
                j = 0;
                while (st1.hasMoreTokens()) {
                    s[j] = st1.nextToken();
                    j++;
                }
                for (j = 0; j < s.length; j++) {
                    list.add(s[j]);
                }
                return list.contains(leftValue);
            }
        }
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
                return leftValue.equals(rightValue);
            default:
                throw new RuntimeException("Not supported " + algorithm);
        }
        return matcher.getMatchingWeight(leftValue, rightValue) >= threshold;
    }

    private static String addValueToFusion(String Fusion1, String Fusion2) {
        String Plus = "\\+";
        String[] temp1, temp2;
        String resultFusion = "";
        temp1 = (Fusion1 + "+" + Fusion2).split(Plus);
        for (String aTemp1 : temp1) {
            if (resultFusion.length() == 0) {
                resultFusion = aTemp1;
                continue;
            }
            temp2 = resultFusion.split(Plus);
            if (!Arrays.asList(temp2).contains(aTemp1)) {
                resultFusion = resultFusion + "+" + aTemp1;
            }
        }
        return resultFusion;
    }

    public static String Conversion(Conversions conversion, String r) {
        java.util.Date dateToConvert;
        if (r == null) {
            return null;
        }
        try {
            switch (conversion) {
                case NONE:
                    break;
                case UPPER_CASE:
                    r = r.toUpperCase();
                    break;
                case LOWER_CASE:
                    r = r.toLowerCase();
                    break;
                case FIRST_UPPER_CASE_CHARACTER:
                    r = r.substring(0, 1).toUpperCase() + r.substring(1).toLowerCase();
                    break;
                case DATE_FORMAT:
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    dateToConvert = dateFormat.parse(r);
                    r = dateFormat.format(dateToConvert);
                    break;
                case YEAR_FORMAT:
                    dateFormat = new SimpleDateFormat("yyyy");
                    dateToConvert = dateFormat.parse(r);
                    r = dateFormat.format(dateToConvert);
                    break;
                case YEAR_MONTH_FORMAT:
                    dateFormat = new SimpleDateFormat("yyyy-MM");
                    dateToConvert = dateFormat.parse(r);
                    r = dateFormat.format(dateToConvert);
                    break;
                case EMAIL_FORMAT:
                    Pattern pattern = Pattern.compile("^[A-Za-z_0-9']+((-[A-Za-z_0-9']+)|(\\.[A-Za-z_0-9']+))*@[A-Za-z0-9]+((\\.|-)[A-Za-z0-9]+)*\\.[A-Za-z0-9]+$");
                    Matcher matcher = pattern.matcher(r);
                    if (!matcher.matches()) {
                        r = "";
                    }
            }
        } catch (ParseException e) {
            return r;
        }
        return r;
    }
}
