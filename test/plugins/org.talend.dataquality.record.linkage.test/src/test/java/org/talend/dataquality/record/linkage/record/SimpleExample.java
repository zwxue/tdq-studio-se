// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataquality.record.linkage.record;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.talend.dataquality.record.linkage.attribute.AttributeMatcherFactory;
import org.talend.dataquality.record.linkage.attribute.IAttributeMatcher;
import org.talend.dataquality.record.linkage.constant.AttributeMatcherType;
import org.talend.dataquality.record.linkage.constant.RecordMatcherType;

/**
 * @author scorreia
 * 
 * This class is a simple example of this record linkage library usage. It is designed to help the component developper.
 * this example represents a tRecordMatching component with 3 columns selected in the join key parameter.
 */
public final class SimpleExample {

    // The value of the thresholds
    private static final double ACCEPTABLE_THRESHOLD = 0.95;

    private static final double UNACCEPTABLE_THRESHOLD = 0.8;

    // 2 samples of data
    private static final String[][] MAINRECORDS = { { "seb", "talend", "suresnes", "data not used in record matching" },
            { "seb", "talend", "suresns", null }, { "seb", "tlend", "sursnes", null }, { "sebas", "taland", "suresnes", null } };

    private static final String[][] LOOKUPRECORDS = { { "seb", "tlend", "sursnes", null },
            { "sebas", "taland", "suresnes", null }, };

    // the algorithms selected by the user for each of the 3 join keys
    private static final String[] ATTRIBUTEMATCHERALGORITHMS = { "Exact", "Double Metaphone", "Levenshtein" };

    // the weights given by the user to each of the 3 join key.
    private static final double[] ATTRIBUTEWEIGHTS = { 1, 1, 1 };
    
    
    private SimpleExample(){
    	
    }
    

    /**
     * DOC scorreia Comment method "main".
     * 
     * @param args
     */
    public static void main(String[] args) {
        IRecordMatcher recordMatcher = RecordMatcherFactory.createMatcher(RecordMatcherType.simpleVSRMatcher);

        // ////////////// INITIALIZATION (MUST BE DONE ONCE ONLY) /////////////////////

        // initialize matcher
        int nbRecords = 3; // this value is the number of columns used in the JOIN_KEY parameter
        recordMatcher.setRecordSize(nbRecords);

        // create attribute matchers for each of the join key
        int nbJoinKey = ATTRIBUTEMATCHERALGORITHMS.length;
        IAttributeMatcher[] attributeMatchers = new IAttributeMatcher[nbJoinKey];
        for (int i = 0; i < attributeMatchers.length; i++) {
            attributeMatchers[i] = AttributeMatcherFactory.createMatcher(ATTRIBUTEMATCHERALGORITHMS[i]);
        }
        recordMatcher.setAttributeMatchers(attributeMatchers);

        // set the weights chosen by the user
        recordMatcher.setAttributeWeights(ATTRIBUTEWEIGHTS);

        // initialize the blocking variables
        // (we use the column which are in exact match as blocking variables but we could change this in the future)
        List<Integer> listIndices = new ArrayList<Integer>();
        for (int i = 0; i < attributeMatchers.length; i++) {
            AttributeMatcherType matchType = attributeMatchers[i].getMatchType();
            if (AttributeMatcherType.exact.equals(matchType) || AttributeMatcherType.exactIgnoreCase.equals(matchType)) {
                listIndices.add(i);
            }
        }
        int[] blockedVariableIndices = new int[listIndices.size()];
        for (int i = 0; i < listIndices.size(); i++) {
            blockedVariableIndices[i] = listIndices.get(i);
        }
        recordMatcher.setBlockingAttributeMatchers(blockedVariableIndices);

        // ////////////// END OF INITIALIZATION /////////////////////

        // /////////// MAIN LOOP now /////////////// compute proba
        for (int rec1 = 0; rec1 < MAINRECORDS.length; rec1++) {
            String[] record1 = MAINRECORDS[rec1];
            for (int rec2 = 0; rec2 < LOOKUPRECORDS.length; rec2++) {
                String[] record2 = LOOKUPRECORDS[rec2];

                final double matchingProba = recordMatcher.getMatchingWeight(record1, record2);
                if (matchingProba >= ACCEPTABLE_THRESHOLD) {
                    // put this record in the "matches" flow
                    System.out.println(" MATCH P(" + printRecord(record1) + " = " + printRecord(record2) + ") =" + matchingProba
                            + " DETAILS=" + printRecord(recordMatcher.getCurrentAttributeMatchingWeights()));
                    continue;
                }
                if (UNACCEPTABLE_THRESHOLD < matchingProba && matchingProba < ACCEPTABLE_THRESHOLD) {
                    // put this record in the "possible maches" flow
                    System.out.println("  POSSIBLE MATCH P(" + printRecord(record1) + " = " + printRecord(record2) + ") ="
                            + matchingProba + " DETAILS=" + printRecord(recordMatcher.getCurrentAttributeMatchingWeights()));
                    continue;
                }
                // put this record in the "non-matches" flow
                System.out.println("!match P(" + printRecord(record1) + " = " + printRecord(record2) + ") =" + matchingProba
                        + " DETAILS=" + printRecord(recordMatcher.getCurrentAttributeMatchingWeights()));
            }
        }
    }

    // method for debug purpose only
    private static String printRecord(Object[] record) {
        return StringUtils.join(record, '|');
    }

    private static String printRecord(double[] record) {
        Double[] array = new Double[record.length];
        for (int i = 0; i < record.length; i++) {
            array[i] = record[i];
        }
        return StringUtils.join(array, '|');
    }

}
