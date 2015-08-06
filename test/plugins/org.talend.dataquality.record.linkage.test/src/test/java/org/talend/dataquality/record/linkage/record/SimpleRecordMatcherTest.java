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

import java.util.Arrays;

import junit.framework.TestCase;

import org.apache.commons.lang.StringUtils;
import org.talend.dataquality.record.linkage.attribute.ExactIgnoreCaseMatcher;
import org.talend.dataquality.record.linkage.attribute.IAttributeMatcher;
import org.talend.dataquality.record.linkage.attribute.JaroWinklerMatcher;
import org.talend.dataquality.record.linkage.constant.RecordMatcherType;

/**
 * DOC scorreia class global comment. Detailled comment
 */
public class SimpleRecordMatcherTest extends TestCase {

    public static final String[][] RECORDS1 = { { "seb", "talend", "suresnes" }, { "seb", "talend", "suresns" },
            { "seb", "tlend", "sursnes" }, { "sebas", "taland", "suresnes" } };

    public static final String[][] RECORDS2 = { { "seb", "tlend", "sursnes" }, { "sebas", "taland", "suresnes" }, };

    public static final double[][] ALLATTRIBUTEWEIGHTS = { { 1, 1, 1 }, { 1, 1, 0 }, { 1, 0, 1 }, { 0, 1, 1 }, { 0, 0, 1 },
            { 0, 0, 1 }, { 0, 1, 0 }, { 1, 0, 0 }, { 0, 0, 0 }, { 0.4, 0.2, 1 }, { 0, 0, 1.5 }, { 20, 100, 10 }, { 1, 2, 3 },
            { 20, 40, 60 } };

    /**
     * Test method for
     * {@link org.talend.dataquality.record.linkage.record.SimpleVSRRecordMatcher#getMatchingWeight(java.lang.String[], java.lang.String[])}
     * .
     */
    public void testGetMatchingProba() {
        for (double[] attributeWeights : ALLATTRIBUTEWEIGHTS) {
            IRecordMatcher match = RecordMatcherFactory.createMatcher(RecordMatcherType.simpleVSRMatcher);
            computeForWeights(attributeWeights, match);
        }
        // for (double[] attributeWeights : allAttributeWeights) {
        // IRecordMatcher match = new MuralTestMatcher();
        // computeForWeights(attributeWeights, match);
        // }

    }

    /**
     * DOC scorreia Comment method "computeForWeights".
     * 
     * @param attributeWeights
     */
    public static void computeForWeights(double[] attributeWeights, IRecordMatcher match) {
        // print
        System.out.println("Weights = " + printWeight(attributeWeights));

        // prepare matcher
        
        match.setRecordSize(3);
        assertEquals(true, match.setAttributeMatchers(new IAttributeMatcher[] { new ExactIgnoreCaseMatcher(),
                new JaroWinklerMatcher(), new ExactIgnoreCaseMatcher() }));
        assertEquals(true, match.setAttributeWeights(attributeWeights));

        // compute proba
        for (int rec1 = 0; rec1 < RECORDS1.length; rec1++) {
            String[] record1 = RECORDS1[rec1];
            for (int rec2 = 0; rec2 < RECORDS2.length; rec2++) {
                String[] record2 = RECORDS2[rec2];
                final double matchingProba = match.getMatchingWeight(record1, record2);
                System.out.println("P(" + printRecord(record1) + " = " + printRecord(record2) + ") =" + matchingProba);
            }
        }
    }

    private static String printRecord(Object[] record) {
        return StringUtils.join(record, '|');
    }

    private static String printWeight(double[] record) {
        return Arrays.toString(record);
    }

    /**
     * Test method for
     * {@link org.talend.dataquality.record.linkage.record.AbstractRecordMatcher#setAttributeGroups(int[][])}.
     */
    public void testSetAttributeGroups() {
        // TODO fail("Not yet implemented");
    }

    /**
     * Test method for
     * {@link org.talend.dataquality.record.linkage.record.AbstractRecordMatcher#setAttributeMatchers(org.talend.dataquality.record.linkage.attribute.IAttributeMatcher[])}
     * .
     */
    public void testSetAttributeMatchers() {
        // TODO fail("Not yet implemented");
    }

    /**
     * Test method for
     * {@link org.talend.dataquality.record.linkage.record.AbstractRecordMatcher#setAttributeWeights(double[])}.
     */
    public void testSetAttributeWeights() {
        // TODO fail("Not yet implemented");
    }

    /**
     * Test method for {@link org.talend.dataquality.record.linkage.record.AbstractRecordMatcher#setRecordSize(int)}.
     */
    public void testSetRecordSize() {
        // TODO fail("Not yet implemented");
    }

    /**
     * Test method for
     * {@link org.talend.dataquality.record.linkage.record.AbstractRecordMatcher#internalScalarProduct(double[], double[])}
     * .
     */
    public void testInternalScalarProduct() {
        // TODO fail("Not yet implemented");
    }

}
