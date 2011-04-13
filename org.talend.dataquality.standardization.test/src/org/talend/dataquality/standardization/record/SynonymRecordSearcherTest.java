// ============================================================================
//
// Copyright (C) 2006-2010 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataquality.standardization.record;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import junit.framework.Assert;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.queryParser.ParseException;
import org.junit.Before;
import org.junit.Test;
import org.talend.dataquality.standardization.index.SynonymIndexBuilder;
import org.talend.dataquality.standardization.index.SynonymIndexBuilderTest;
import org.talend.dataquality.standardization.index.SynonymIndexSearcher;
import org.talend.dataquality.standardization.record.SynonymRecordSearcher.RecordResult;
import org.talend.dataquality.standardization.record.SynonymRecordSearcher.WordResult;

/**
 * DOC scorreia class global comment. Detailled comment
 */
public class SynonymRecordSearcherTest {

    private static final String[][] WORDRESULTS = { { "11", "12", "13", "14", "15" }, { "21", "22", "23" }, { "31", "32", "33" },
            { "43" } };

   // private static final String[][] WORDRESULTS = { { "11", "12" }, { "21", "22" }, { "31", "32" } };


    @Test
    public void testRecordResultCompute() {

        // prepare wordresults
        List<List<WordResult>> wordResults = new ArrayList<List<WordResult>>();
        for (String[] elts : WORDRESULTS) {
            List<WordResult> wrs = new ArrayList<WordResult>();
            for (String elt : elts) {
                WordResult wr = new WordResult();
                wr.input = "input " + elt;
                wr.word = "word " + elt;
                wr.score = (float) Math.random();
                wrs.add(wr);
            }
            wordResults.add(wrs);
        }

        // --- compute output results
        RecordResult recRes = new RecordResult();
        recRes.record = initializeRecordToSearch(WORDRESULTS);
        recRes.wordResults = wordResults;

        List<OutputRecord> expectedOutputRows = null;
        expectedOutputRows = new ArrayList<OutputRecord>();
        computeOutputRows(WORDRESULTS.length, new ArrayList<WordResult>(), recRes.wordResults, expectedOutputRows);
        for (OutputRecord outputRecord : expectedOutputRows) {
            System.out.println(outputRecord);
        }

        List<OutputRecord> outputRows = recRes.computeOutputRows();

        // --- check some assertions

        // verify number of results
        int expectedNbOutput = 1;
        for (String[] in : WORDRESULTS) {
            expectedNbOutput *= in.length;
        }

        Assert.assertEquals(expectedNbOutput, expectedOutputRows.size());
        Assert.assertTrue(expectedOutputRows.size() >= outputRows.size());

        for (OutputRecord outputRecord : outputRows) {
            boolean found = false;
            for (OutputRecord expectedRecord : expectedOutputRows) {
                if (expectedRecord.equals(outputRecord)) {
                    found = true;
                    break;
                }
            }
            Assert.assertTrue("Record not found: " + outputRecord, found);
        }

    }

    /**
     * DOC scorreia Comment method "initializeRecordToSearch".
     * 
     * @param wordresults
     * @return
     */
    private String[] initializeRecordToSearch(String[][] wordresults) {
        Random rnd = new Random();
        String[] init = new String[wordresults.length];
        int i = 0;
        for (String[] wr : wordresults) {
            init[i++] = wr[rnd.nextInt(wr.length)];
        }
        return init;
    }

    /**
     * Method "computeOutputRows".
     * 
     * @param recordLength the record length
     * @param wordResults the list of word result that constitute the begining of the current output record
     * @param wrs the list of remaining word results
     * @param outputRows the output records (updated each time this method is called)
     * @return
     */
    private void computeOutputRows(int recordLength, List<WordResult> wordResults, List<List<WordResult>> wrs,
            List<OutputRecord> outputRows) {
        // handle last vector of word results
        if (wrs.size() == 1) {
            List<WordResult> lastWR = wrs.get(0);
            for (WordResult wordResult : lastWR) {
                OutputRecord outputRec = new OutputRecord(recordLength);
                for (int i = 0; i < wordResults.size(); i++) {
                    updateOutputRec(outputRec, i, wordResults.get(i));
                }
                updateOutputRec(outputRec, wordResults.size(), wordResult);

                outputRows.add(outputRec);
            }
        } else { // recusive call on a sublist
            List<WordResult> firstWR = wrs.get(0);
            List<List<WordResult>> sublist = wrs.subList(1, wrs.size());
            for (WordResult wordResult : firstWR) {
                List<WordResult> wr = new ArrayList<WordResult>(wordResults);
                wr.add(wordResult);
                computeOutputRows(recordLength, wr, sublist, outputRows);
            }
        }
    }

    private void updateOutputRec(OutputRecord outputRec, int idx, WordResult wordResult) {
        outputRec.record[idx] = wordResult.word;
        outputRec.score += wordResult.score; // TODO add multiplicative weight here if needed
        outputRec.scores += "|" + wordResult.score;
    }

    /**
     * DOC scorreia Comment method "setUp".
     * 
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }

    private void initIdx(String path) {
        SynonymIndexBuilder builder1 = new SynonymIndexBuilder();
        builder1.deleteIndexFromFS(path);
        builder1.initIndexInFS(path);
        for (int i = 0; i < SynonymIndexBuilderTest.synonyms.length; i++) {
            String[] synonyms = SynonymIndexBuilderTest.synonyms[i];
            try {
                builder1.insertDocument(synonyms[0], synonyms[1]);
            } catch (IOException e) {
                e.printStackTrace();
                fail("should not get an exception here");
            }
        }
        builder1.closeIndex();
    }

    /**
     * Test method for
     * {@link org.talend.dataquality.standardization.record.SynonymRecordSearcher#search(int, java.lang.String[], int[])}
     * .
     * 
     * @throws IOException
     */
    @Test
    public void testSearch() {

        // create two indexes
        initIdx("data/idx1");
        initIdx("data/idx2");

        // search in the two indexes
        SynonymIndexSearcher searcher1 = new SynonymIndexSearcher("data/idx1");
        searcher1.setTopDocLimit(5);
        SynonymIndexSearcher searcher2 = new SynonymIndexSearcher("data/idx2");
        searcher2.setTopDocLimit(5);
        SynonymRecordSearcher recSearcher = new SynonymRecordSearcher(2);
        recSearcher.addSearcher(searcher1, 0);
        recSearcher.addSearcher(searcher2, 1);

        String[] record = { "IBM", "ANpe" };
        try {
            List<OutputRecord> results = recSearcher.search(15, record);
            Assert.assertNotNull(results);
            Assert.assertFalse(results.isEmpty());
            for (OutputRecord outputRecord : results) {
                Assert.assertNotNull(outputRecord);
                String[] resultingRecord = outputRecord.getRecord();
                Assert.assertEquals(record.length, resultingRecord.length);

                System.out.println(StringUtils.join(resultingRecord, '|'));
                System.out.println("\t" + outputRecord.getScore());
            }
        } catch (ParseException e) {
            e.printStackTrace();
            fail("should not get an exception here");
        } catch (IOException e) {
            e.printStackTrace();
            fail("should not get an exception here");
        }
        try {
            searcher1.close();
            searcher2.close();
        } catch (Exception e) {
            e.printStackTrace();
            fail("should not get an exception here");
        }
    }

    @Test
    public void testSearch2() {
        System.out.println("\n");

        // create two indexes
        initIdx("data/idx1");
        initIdx("data/idx2");

        SynonymIndexBuilder builder1 = new SynonymIndexBuilder();
        builder1.initIndexInFS("data/idx1");
        try {
            builder1.insertDocument("Institute of Business Management", "IBM");
        } catch (IOException e1) {
            fail("should not get an exception here");
            e1.printStackTrace();
        }
        builder1.closeIndex();

        // search in the two indexes
        SynonymIndexSearcher searcher1 = new SynonymIndexSearcher("data/idx1");
        searcher1.setTopDocLimit(5);
        SynonymIndexSearcher searcher2 = new SynonymIndexSearcher("data/idx2");
        searcher2.setTopDocLimit(5);
        SynonymRecordSearcher recSearcher = new SynonymRecordSearcher(2);
        recSearcher.addSearcher(searcher1, 0);
        recSearcher.addSearcher(searcher2, 1);

        String[] record = { "IBM", "ANpe" };
        try {
            List<OutputRecord> results = recSearcher.search(15, record);
            Assert.assertNotNull(results);
            Assert.assertFalse(results.isEmpty());
            for (OutputRecord outputRecord : results) {
                Assert.assertNotNull(outputRecord);
                String[] resultingRecord = outputRecord.getRecord();
                Assert.assertEquals(record.length, resultingRecord.length);

                System.out.println(StringUtils.join(resultingRecord, '|'));
                System.out.println("\t" + outputRecord.getScore());
            }
        } catch (ParseException e) {
            e.printStackTrace();
            fail("should not get an exception here");
        } catch (IOException e) {
            e.printStackTrace();
            fail("should not get an exception here");
        }
        try {
            searcher1.close();
            searcher2.close();
        } catch (Exception e) {
            e.printStackTrace();
            fail("should not get an exception here");
        }

    }

    @Test
    public void testSearch3() {
        System.out.println("\n");

        // create two indexes
        initIdx("data/idx1");
        initIdx("data/idx2");
        initIdx("data/idx3");

        SynonymIndexBuilder builder1 = new SynonymIndexBuilder();
        builder1.initIndexInFS("data/idx1");
        try {
            builder1.insertDocument("Institute of Business Management", "IBM");
        } catch (IOException e1) {
            fail("should not get an exception here");
            e1.printStackTrace();
        }
        builder1.closeIndex();

        // search in the three indexes
        SynonymIndexSearcher searcher1 = new SynonymIndexSearcher("data/idx1");
        searcher1.setTopDocLimit(5);
        SynonymIndexSearcher searcher2 = new SynonymIndexSearcher("data/idx2");
        searcher2.setTopDocLimit(5);
        SynonymIndexSearcher searcher3 = new SynonymIndexSearcher("data/idx3");
        searcher3.setTopDocLimit(5);
        SynonymRecordSearcher recSearcher = new SynonymRecordSearcher(3);
        recSearcher.addSearcher(searcher1, 0);
        recSearcher.addSearcher(searcher2, 1);
        recSearcher.addSearcher(searcher3, 2);

        String[] record = { "IBM", "ANpe", "International" };
        try {
            List<OutputRecord> results = recSearcher.search(15, record);
            Assert.assertNotNull(results);
            Assert.assertFalse(results.isEmpty());
            for (OutputRecord outputRecord : results) {
                Assert.assertNotNull(outputRecord);
                String[] resultingRecord = outputRecord.getRecord();
                Assert.assertEquals(record.length, resultingRecord.length);

                System.out.println(StringUtils.join(resultingRecord, '|'));
                System.out.println("\t" + outputRecord.getScore());
            }
        } catch (ParseException e) {
            e.printStackTrace();
            fail("should not get an exception here");
        } catch (IOException e) {
            e.printStackTrace();
            fail("should not get an exception here");
        }
        try {
            searcher1.close();
            searcher2.close();
            searcher3.close();
        } catch (Exception e) {
            e.printStackTrace();
            fail("should not get an exception here");
        }
    }

    /**
     * Test method for
     * {@link org.talend.dataquality.standardization.record.SynonymRecordSearcher#addSearcher(org.talend.dataquality.standardization.index.SynonymIndexSearcher, int)}
     * .
     */
    @Test
    public void testAddSearcher() {
        SynonymRecordSearcher recSearcher = new SynonymRecordSearcher(2);
        try {
            recSearcher.addSearcher(null, 0);
        } catch (Exception e) {
            Assert.assertNotNull("we should get an exception here", e);
        }
        try {
            recSearcher.addSearcher(new SynonymIndexSearcher(), 2);
        } catch (Exception e) {
            Assert.assertNotNull("we should get an exception here", e);
        }

        try {
            SynonymIndexSearcher searcher = new SynonymIndexSearcher();
            recSearcher.addSearcher(searcher, 1);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        try {
            SynonymIndexSearcher searcher = new SynonymIndexSearcher();
            recSearcher.addSearcher(searcher, -1);
        } catch (Exception e) {
            Assert.assertNotNull("we should get an exception here", e);
        }
    }

}
