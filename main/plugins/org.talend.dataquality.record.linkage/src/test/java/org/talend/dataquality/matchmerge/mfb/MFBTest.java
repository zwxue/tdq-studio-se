/*
 * Copyright (C) 2006-2014 Talend Inc. - www.talend.com
 * 
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 * 
 * You should have received a copy of the agreement along with this program; if not, write to Talend SA 9 rue Pages
 * 92150 Suresnes, France
 */

package org.talend.dataquality.matchmerge.mfb;

import java.util.*;

import junit.framework.TestCase;

import org.talend.dataquality.matchmerge.Attribute;
import org.talend.dataquality.matchmerge.MatchMergeAlgorithm;
import org.talend.dataquality.matchmerge.Record;
import org.talend.dataquality.matchmerge.SubString;
import org.talend.dataquality.record.linkage.attribute.IAttributeMatcher;
import org.talend.dataquality.record.linkage.constant.AttributeMatcherType;
import org.talend.dataquality.record.linkage.utils.SurvivorShipAlgorithmEnum;

public class MFBTest extends TestCase {

    protected static final int COUNT = 200;

    private final static String[] CONSTANTS = { "constant", "value", "tac", "different", "big", "database", "heat", "quality" };

    private final static String[] SIMILARS = { "constant", "constan", "ocnstant", "constnat", "constnta", "oncstant", "constatn",
            "consttan" };

    private final static AttributeMatcherType[] TESTS_MATCH = { AttributeMatcherType.LEVENSHTEIN, AttributeMatcherType.SOUNDEX,
            AttributeMatcherType.JARO_WINKLER, AttributeMatcherType.DOUBLE_METAPHONE };

    private static void testConstant(final int constantNumber, int totalCount, AttributeMatcherType matchAlgorithm) {
        Map<String, ValueGenerator> generators = new HashMap<String, ValueGenerator>();
        generators.put("name", new ValueGenerator() {

            int index = 0;

            @Override
            public String newValue() {
                return CONSTANTS[index++ % constantNumber];
            }
        });

        Iterator<Record> iterator = new ValuesIterator(totalCount, generators);
        MatchMergeAlgorithm algorithm = MFB.build(new AttributeMatcherType[] { matchAlgorithm }, new String[] { "" },
                new float[] { 1 }, 0, new SurvivorShipAlgorithmEnum[] { SurvivorShipAlgorithmEnum.LONGEST }, new String[] { "" },
                new double[] { 1 }, new IAttributeMatcher.NullOption[] { IAttributeMatcher.NullOption.nullMatchAll },
                new SubString[] { SubString.NO_SUBSTRING }, "MFB");
        List<Record> mergedRecords = algorithm.execute(iterator);
        assertEquals(constantNumber, mergedRecords.size());
        for (Record mergedRecord : mergedRecords) {
            assertEquals(Math.round(totalCount / constantNumber), mergedRecord.getRelatedIds().size());
        }
    }

    private static void testSimilar(final int similarNumber, int totalCount, AttributeMatcherType matchAlgorithm) {
        Map<String, ValueGenerator> generators = new HashMap<String, ValueGenerator>();
        generators.put("name", new ValueGenerator() {

            int index = 0;

            @Override
            public String newValue() {
                return SIMILARS[index++ % similarNumber];
            }
        });

        Iterator<Record> iterator = new ValuesIterator(totalCount, generators);
        MatchMergeAlgorithm algorithm = MFB.build(new AttributeMatcherType[] { matchAlgorithm }, new String[] { "" },
                new float[] { 0.5f }, 0, new SurvivorShipAlgorithmEnum[] { SurvivorShipAlgorithmEnum.MOST_COMMON },
                new String[] { "" }, new double[] { 1 },
                new IAttributeMatcher.NullOption[] { IAttributeMatcher.NullOption.nullMatchAll },
                new SubString[] { SubString.NO_SUBSTRING }, "MFB");
        List<Record> mergedRecords = algorithm.execute(iterator);
        assertEquals(1, mergedRecords.size());
        for (Record mergedRecord : mergedRecords) {
            assertEquals(totalCount, mergedRecord.getRelatedIds().size());
        }
    }

    public void testArguments() throws Exception {
        try {
            MFB.build(new AttributeMatcherType[0], new String[0], new float[0], 0, new SurvivorShipAlgorithmEnum[0],
                    new String[0], new double[0], new IAttributeMatcher.NullOption[0], new SubString[0], "MFB");
            fail();
        } catch (Exception e) {
            // Expected
        }
    }

    public void testConstantValueRecords() throws Exception {
        for (AttributeMatcherType matchAlgorithm : TESTS_MATCH) {
            testConstant(1, COUNT, matchAlgorithm);
            testConstant(2, COUNT, matchAlgorithm);
            testConstant(4, COUNT, matchAlgorithm);
            testConstant(8, COUNT, matchAlgorithm);
        }
    }

    public void testMatchWeight() throws Exception {
        // TODO: Test needs rewrite
    }

    public void testSimilarValueRecords() throws Exception {
        testSimilar(1, COUNT, AttributeMatcherType.LEVENSHTEIN);
        testSimilar(2, COUNT, AttributeMatcherType.LEVENSHTEIN);
        testSimilar(4, COUNT, AttributeMatcherType.LEVENSHTEIN);
        testSimilar(8, COUNT, AttributeMatcherType.LEVENSHTEIN);
    }

    interface ValueGenerator {

        String newValue();
    }

    private static class ValuesIterator implements Iterator<Record> {

        private final int size;

        private final Map<String, ValueGenerator> generators;

        private int currentIndex = 0;

        private long timestamp = 0;

        private ValuesIterator(int size, Map<String, ValueGenerator> generators) {
            this.size = size;
            this.generators = generators;
        }

        @Override
        public boolean hasNext() {
            return currentIndex < size;
        }

        @Override
        public Record next() {
            Vector<Attribute> record = new Vector<Attribute>();
            for (Map.Entry<String, ValueGenerator> generator : generators.entrySet()) {
                Attribute attribute = new Attribute(generator.getKey());
                attribute.setValue(generator.getValue().newValue());
                record.add(attribute);
            }
            currentIndex++;
            return new Record(record, String.valueOf(currentIndex - 1), timestamp++, "MFB");
        }

        @Override
        public void remove() {
            throw new RuntimeException("Not supported");
        }
    }
}
