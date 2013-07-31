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

package org.talend.dataquality.matchmerge.mfb;

import junit.framework.TestCase;
import org.talend.dataquality.matchmerge.*;

import java.util.*;

public class MFBTest extends TestCase {

    private final static String[] CONSTANTS = {"constant", "value", "tac", "different", "big", "database", "heat", "quality"};

    private final static String[] SIMILARS = {"constant", "constan", "ocnstant", "constnat", "constnta", "oncstant", "constatn", "consttan"};

    private final static MatchAlgorithm[] TESTS_MATCH = {
            MatchAlgorithm.LEVENSHTEIN,
            MatchAlgorithm.SOUNDEX,
            MatchAlgorithm.JAROWINKLER,
            MatchAlgorithm.DOUBLE_METAPHONE
    };

    public void testArguments() throws Exception {
        MatchMergeAlgorithm algorithm = new MFB(new MatchAlgorithm[0],
                new float[0],
                new MergeAlgorithm[0]);
        List<Record> list = algorithm.execute(Collections.<Record>emptyList().iterator());
        assertEquals(0, list.size());
    }

    public void testEmptyRecords() throws Exception {
        Map<String, ValueGenerator> generators = new HashMap<String, ValueGenerator>();
        Iterator<Record> iterator = new ValuesIterator(100000, generators);
        MatchMergeAlgorithm algorithm = new MFB(new MatchAlgorithm[0],
                new float[0],
                new MergeAlgorithm[0]);
        List<Record> list = algorithm.execute(iterator);
        assertEquals(100000, list.size());
    }

    public void testConstantValueRecords() throws Exception {
        for (MatchAlgorithm matchAlgorithm : TESTS_MATCH) {
            testConstant(1, 100000, matchAlgorithm);
            testConstant(2, 100000, matchAlgorithm);
            testConstant(4, 100000, matchAlgorithm);
            testConstant(8, 100000, matchAlgorithm);
        }
    }

    private static void testConstant(final int constantNumber, int totalCount, MatchAlgorithm matchAlgorithm) {
        Map<String, ValueGenerator> generators = new HashMap<String, ValueGenerator>();
        generators.put("name", new ValueGenerator() {
            int index = 0;

            @Override
            public String newValue() {
                return CONSTANTS[index++ % constantNumber];
            }
        });

        Iterator<Record> iterator = new ValuesIterator(totalCount, generators);
        MatchMergeAlgorithm algorithm = new MFB(new MatchAlgorithm[]{matchAlgorithm},
                new float[]{1},
                new MergeAlgorithm[]{MergeAlgorithm.UNIFY});
        List<Record> mergedRecords = algorithm.execute(iterator);
        assertEquals(constantNumber, mergedRecords.size());
        System.out.println("---------");
        for (Record mergedRecord : mergedRecords) {
            assertNotNull(mergedRecord.getGroupId());
            assertEquals(totalCount / constantNumber, mergedRecord.getRelatedIds().size());
            System.out.println("Group id: " + mergedRecord.getGroupId() + " (" + mergedRecord.getRelatedIds().size() + ").");
        }
        System.out.println("---------");
    }

    public void testSimilarValueRecords() throws Exception {
        testSimilar(1, 100000, MatchAlgorithm.LEVENSHTEIN);
        testSimilar(2, 100000, MatchAlgorithm.LEVENSHTEIN);
        testSimilar(4, 100000, MatchAlgorithm.LEVENSHTEIN);
        testSimilar(8, 100000, MatchAlgorithm.LEVENSHTEIN);
    }

    private static void testSimilar(final int similarNumber, int totalCount, MatchAlgorithm matchAlgorithm) {
        Map<String, ValueGenerator> generators = new HashMap<String, ValueGenerator>();
        generators.put("name", new ValueGenerator() {
            int index = 0;

            @Override
            public String newValue() {
                return SIMILARS[index++ % similarNumber];
            }
        });

        Iterator<Record> iterator = new ValuesIterator(totalCount, generators);
        MatchMergeAlgorithm algorithm = new MFB(new MatchAlgorithm[]{matchAlgorithm},
                new float[]{0.5f},
                new MergeAlgorithm[]{MergeAlgorithm.UNIFY});
        List<Record> mergedRecords = algorithm.execute(iterator);
        assertEquals(1, mergedRecords.size());
        System.out.println("---------");
        for (Record mergedRecord : mergedRecords) {
            assertNotNull(mergedRecord.getGroupId());
            assertEquals(totalCount, mergedRecord.getRelatedIds().size());
            System.out.println("Group id: " + mergedRecord.getGroupId() + " (" + mergedRecord.getRelatedIds().size() + ").");
        }
        System.out.println("---------");
    }

    interface ValueGenerator {
        String newValue();
    }

    private static class ValuesIterator implements Iterator<Record> {

        private int currentIndex = 0;

        private final int size;

        private final Map<String, ValueGenerator> generators;

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
            return new Record(record, String.valueOf(currentIndex - 1));
        }

        @Override
        public void remove() {
            throw new RuntimeException("Not supported");
        }
    }
}
