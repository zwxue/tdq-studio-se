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

package org.talend.dataquality.matchmerge.mfb;

import junit.framework.TestCase;
import org.talend.dataquality.matchmerge.*;
import org.talend.dataquality.record.linkage.attribute.IAttributeMatcher;
import org.talend.dataquality.record.linkage.constant.AttributeMatcherType;
import org.talend.dataquality.record.linkage.utils.SurvivorShipAlgorithmEnum;

import java.util.*;

public class MFBTest extends TestCase {

    private final static String[] CONSTANTS = {"constant", "value", "tac", "different", "big", "database", "heat", "quality"};

    private final static String[] SIMILARS = {"constant", "constan", "ocnstant", "constnat", "constnta", "oncstant", "constatn", "consttan"};

    private final static AttributeMatcherType[] TESTS_MATCH = {
            AttributeMatcherType.LEVENSHTEIN,
            AttributeMatcherType.SOUNDEX,
            AttributeMatcherType.JARO_WINKLER,
            AttributeMatcherType.DOUBLE_METAPHONE
    };

    public void testArguments() throws Exception {
        MatchMergeAlgorithm algorithm = new MFB(new AttributeMatcherType[0],
                new String[0],
                new float[0],
                0,
                new SurvivorShipAlgorithmEnum[0],
                new String[0],
                new double[0],
                new IAttributeMatcher.NullOption[0],
                new SubString[0],
                "MFB");
        List<Record> list = algorithm.execute(Collections.<Record>emptyList().iterator());
        assertEquals(0, list.size());
    }

    public void testEmptyRecords() throws Exception {
        Map<String, ValueGenerator> generators = new HashMap<String, ValueGenerator>();
        Iterator<Record> iterator = new ValuesIterator(100000, generators);
        MatchMergeAlgorithm algorithm = new MFB(new AttributeMatcherType[0],
                new String[0],
                new float[0],
                0,
                new SurvivorShipAlgorithmEnum[0],
                new String[0],
                new double[0],
                new IAttributeMatcher.NullOption[0],
                new SubString[0],
                "MFB");
        List<Record> list = algorithm.execute(iterator);
        assertEquals(100000, list.size());
    }

    public void testConstantValueRecords() throws Exception {
        for (AttributeMatcherType matchAlgorithm : TESTS_MATCH) {
            testConstant(1, 100000, matchAlgorithm);
            testConstant(2, 100000, matchAlgorithm);
            testConstant(4, 100000, matchAlgorithm);
            testConstant(8, 100000, matchAlgorithm);
        }
    }

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
        MatchMergeAlgorithm algorithm = new MFB(new AttributeMatcherType[]{matchAlgorithm},
                new String[] {""},
                new float[]{1},
                0,
                new SurvivorShipAlgorithmEnum[]{SurvivorShipAlgorithmEnum.LONGEST},
                new String[] {""},
                new double[]{1},
                new IAttributeMatcher.NullOption[]{IAttributeMatcher.NullOption.nullMatchAll},
                new SubString[]{SubString.NO_SUBSTRING},
                "MFB");
        List<Record> mergedRecords = algorithm.execute(iterator);
        assertEquals(constantNumber, mergedRecords.size());
        for (Record mergedRecord : mergedRecords) {
            assertEquals(totalCount / constantNumber, mergedRecord.getRelatedIds().size());
        }
    }

    public void testMatchWeight() throws Exception {
        for (AttributeMatcherType matchAlgorithm : TESTS_MATCH) {
            testWeight(1, 100000, matchAlgorithm);
            testWeight(2, 100000, matchAlgorithm);
            testWeight(4, 100000, matchAlgorithm);
            testWeight(8, 100000, matchAlgorithm);
        }
    }

    private static void testWeight(final int constantNumber, int totalCount, AttributeMatcherType matchAlgorithm) {
        Map<String, ValueGenerator> generators = new HashMap<String, ValueGenerator>();
        generators.put("name", new ValueGenerator() {
            int index = 0;

            @Override
            public String newValue() {
                return CONSTANTS[index++ % constantNumber];
            }
        });

        Iterator<Record> iterator = new ValuesIterator(totalCount, generators);
        MatchMergeAlgorithm algorithm = new MFB(new AttributeMatcherType[]{matchAlgorithm},
                new String[] {""},
                new float[]{1},
                0,
                new SurvivorShipAlgorithmEnum[]{SurvivorShipAlgorithmEnum.LONGEST},
                new String[] {""},
                new double[]{0}, // Mark rule with no weight (-> match record should have a 0 confidence).
                new IAttributeMatcher.NullOption[] {IAttributeMatcher.NullOption.nullMatchAll},
                new SubString[]{SubString.NO_SUBSTRING},
                "MFB");
        List<Record> mergedRecords = algorithm.execute(iterator);
        assertEquals(constantNumber, mergedRecords.size());
        for (Record mergedRecord : mergedRecords) {
            assertEquals(totalCount / constantNumber, mergedRecord.getRelatedIds().size());
            assertEquals(0.0, mergedRecord.getConfidence());
        }
    }

    public void testSimilarValueRecords() throws Exception {
        testSimilar(1, 100000, AttributeMatcherType.LEVENSHTEIN);
        testSimilar(2, 100000, AttributeMatcherType.LEVENSHTEIN);
        testSimilar(4, 100000, AttributeMatcherType.LEVENSHTEIN);
        testSimilar(8, 100000, AttributeMatcherType.LEVENSHTEIN);
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
        MatchMergeAlgorithm algorithm = new MFB(new AttributeMatcherType[]{matchAlgorithm},
                new String[] {""},
                new float[]{0.5f},
                0,
                new SurvivorShipAlgorithmEnum[]{SurvivorShipAlgorithmEnum.CONCATENATE},
                new String[] {""},
                new double[]{1},
                new IAttributeMatcher.NullOption[]{IAttributeMatcher.NullOption.nullMatchAll},
                new SubString[]{SubString.NO_SUBSTRING},
                "MFB");
        List<Record> mergedRecords = algorithm.execute(iterator);
        assertEquals(1, mergedRecords.size());
        for (Record mergedRecord : mergedRecords) {
            assertEquals(totalCount, mergedRecord.getRelatedIds().size());
        }
    }

    interface ValueGenerator {
        String newValue();
    }

    private static class ValuesIterator implements Iterator<Record> {

        private int currentIndex = 0;

        private final int size;

        private final Map<String, ValueGenerator> generators;

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
                record.add(new Attribute(generator.getKey(), generator.getValue().newValue()));
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
