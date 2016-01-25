// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataquality.standardization.index;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.util.Version;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.talend.dataquality.standardization.record.OutputRecord;
import org.talend.dataquality.standardization.record.SynonymRecordSearcher;

/**
 * DOC scorreia class global comment. Detailled comment
 */
public class SynonymIndexSearcherTest {

    /**
     * 
     */
    private SynonymIndexBuilderTest synIdxBuilderTest;

    /**
     * DOC scorreia Comment method "setUp".
     * 
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        // create the index
        this.synIdxBuilderTest = new SynonymIndexBuilderTest();
        synIdxBuilderTest.setUp();

        SynonymIndexBuilder synonymIdxBuilder = new SynonymIndexBuilder();
        synonymIdxBuilder.initIndexInFS(SynonymIndexBuilderTest.path);
        synIdxBuilderTest.insertDocuments(synonymIdxBuilder);
        synonymIdxBuilder.closeIndex();
    }

    @After
    public void finalize() throws Throwable {
        super.finalize();
        this.synIdxBuilderTest.finalize();
    }

    /**
     * Test method for
     * {@link org.talend.dataquality.standardization.index.SynonymIndexSearcher#openIndexInFS(java.lang.String)}.
     */
    @Test
    public void testInitIndexInFS() {
        SynonymIndexSearcher searcher = new SynonymIndexSearcher();
        String path = "data/tmp_indx";
        File idxFolder = new File(path);
        idxFolder.delete();
        assertEquals(false, idxFolder.exists());
        assertEquals(true, idxFolder.mkdirs());
        try {
            searcher.openIndexInFS(path);
        } catch (IOException e) {
            assertEquals(true, e.getMessage().contains("no segment"));
        }
        // use an existing index folder.
        try {
            searcher.openIndexInFS(SynonymIndexBuilderTest.path);
        } catch (IOException e) {
            fail(e.getMessage());
        }
        assertEquals(true, idxFolder.exists());
        assertEquals(true, idxFolder.delete());
        searcher.close();
    }

    /**
     * Test method for
     * {@link org.talend.dataquality.standardization.index.SynonymIndexSearcher#searchDocumentByWord(java.lang.String)}.
     */
    @Test
    public void testSearchDocumentByWord() {
        SynonymIndexSearcher searcher = getSearcher();
        String bigblue[] = { "big blue", "Big Blue" };
        for (int i = 0; i < bigblue.length; i++) {
            String toFind = bigblue[i];
            TopDocs doc = searcher.searchDocumentByWord(toFind);
            assertEquals("we should have found no document, check the code", true, doc.totalHits == 0);
        }
        String words[] = { "I.B.M.", "ANPE" };
        for (int i = 0; i < words.length; i++) {
            String w = words[i];
            TopDocs doc = searcher.searchDocumentByWord(w);
            assertEquals("we should have found at least one document, check the list of synonyms or the code", false,
                    doc.totalHits == 0);
        }

        searcher.close();
    }

    @Test
    public void testSearchDocumentBySynonym() throws IOException, ParseException {
        System.out.println("\n-----------Test searchDocumentBySynonym----------");
        SynonymIndexSearcher searcher = getSearcher();
        searcher.setAnalyzer(new StandardAnalyzer(Version.LUCENE_30));
        TopDocs docs = searcher.searchDocumentBySynonym("ibm");
        System.out.println(docs.totalHits + " documents found.");

        assertEquals(3, docs.totalHits);
        assertEquals(true, searcher.getTopDocLimit() >= docs.scoreDocs.length);
        for (int i = 0; i < docs.scoreDocs.length; i++) {
            int docNumber = docs.scoreDocs[i].doc;
            System.out.print("\ndoc=" + docNumber + "\tscore=" + docs.scoreDocs[i].score);
            // Document doc = builder.getSearcher().doc(docs.scoreDocs[i].doc);
            System.out.println("\tword: " + searcher.getWordByDocNumber(docNumber));
            System.out.println("\tsynonyms: " + Arrays.toString(searcher.getSynonymsByDocNumber(docNumber)));
        }
        String[] bigblue = { "Big Blue", "big blue", "Blue", "big" };
        for (int i = 0; i < bigblue.length; i++) {
            String toFind = bigblue[i];
            TopDocs doc = searcher.searchDocumentBySynonym(toFind);
            assertEquals("we should have found at least one document, check the list of synonyms or the code", false,
                    doc.totalHits == 0);

        }

        searcher.close();

        // TODO check that the best matching is the exact string.
        // float[] scores = new float[bigblue.length];

    }

    /**
     * DOC scorreia Comment method "getSearcher".
     * 
     * @return
     */
    private SynonymIndexSearcher getSearcher() {
        SynonymIndexSearcher searcher = new SynonymIndexSearcher();
        try {
            // searcher.setAnalyzer(builder.getAnalyzer());
            searcher.openIndexInFS(SynonymIndexBuilderTest.path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        searcher.setTopDocLimit(5);

        return searcher;
    }

    @Test
    public void testSearchInSeveralIndexes() throws IOException, ParseException {
        System.out.println("\n-----------Test SearchInSeveralIndexes----------");

        // assume we have two fields to search
        String row1Company = "ibm";
        String row1Label = "ANPE";

        SynonymIndexSearcher searcher = getSearcher();
        // search
        TopDocs docsField1 = searcher.searchDocumentBySynonym(row1Company);
        System.out.println(docsField1.totalHits + " documents found for " + row1Company);

        for (int i = 0; i < docsField1.scoreDocs.length; i++) {
            int docNumber = docsField1.scoreDocs[i].doc;
            System.out.print("\ndoc=" + docNumber + "\tscore=" + docsField1.scoreDocs[i].score);
            // Document doc = builder.getSearcher().doc(docs.scoreDocs[i].doc);
            System.out.println("\tword: " + searcher.getWordByDocNumber(docNumber));
            System.out.println("\tsynonyms: " + Arrays.toString(searcher.getSynonymsByDocNumber(docNumber)));
        }

        TopDocs docsField2 = searcher.searchDocumentBySynonym(row1Label);
        System.out.println(docsField2.totalHits + " documents found for " + row1Label);

        for (int i = 0; i < docsField2.totalHits; i++) {
            int docNumber = docsField2.scoreDocs[i].doc;
            System.out.print("\ndoc=" + docNumber + "\tscore=" + docsField2.scoreDocs[i].score);
            // Document doc = builder.getSearcher().doc(docs.scoreDocs[i].doc);
            System.out.println("\tword: " + searcher.getWordByDocNumber(docNumber));
            System.out.println("\tsynonyms: " + Arrays.toString(searcher.getSynonymsByDocNumber(docNumber)));
        }

        // build output by a cross product of matched results
        for (int i = 0; i < docsField1.totalHits; i++) {
            int docNumber = docsField1.scoreDocs[i].doc;
            String word1 = searcher.getWordByDocNumber(docNumber);
            for (int j = 0; j < docsField2.totalHits; j++) {
                int docNumber2 = docsField2.scoreDocs[j].doc;
                String word2 = searcher.getWordByDocNumber(docNumber2);
                System.out.println("output row = " + word1 + " , " + word2);
            }
        }

        System.out.println("   ----------   ");

        // record to search
        String[] record = new String[] { row1Company, row1Label };

        SynonymRecordSearcher recsearcher = new SynonymRecordSearcher(record.length);
        recsearcher.addSearcher(searcher, 0);
        recsearcher.addSearcher(searcher, 1);
        List<OutputRecord> output = recsearcher.search(3, record);
        for (OutputRecord outputRecord : output) {
            System.out.println("out= " + outputRecord);
        }

        searcher.close();

        // FIXME add assertions here (or create a junit test for SynonymRecordSearcher instead)

    }

    /**
     * Test method for
     * {@link org.talend.dataquality.standardization.index.SynonymIndexSearcher#getSynonymCount(java.lang.String)}.
     */
    @Test
    public void testGetSynonymCount() {
        SynonymIndexSearcher search = new SynonymIndexSearcher();
        try {
            search.openIndexInFS(SynonymIndexBuilderTest.path);
        } catch (IOException e) {
            fail(e.getMessage());
        }
        assertEquals(2, search.getSynonymCount("IAIDQ"));
        search.close();
    }

    /**
     * Test method for {@link org.talend.dataquality.standardization.index.SynonymIndexSearcher#getDocument(int)}.
     */
    @Test
    public void testGetDocument() {
        SynonymIndexSearcher search = new SynonymIndexSearcher();
        try {
            search.openIndexInFS(SynonymIndexBuilderTest.path);
        } catch (IOException e) {
            fail(e.getMessage());
        }
        TopDocs docs = search.searchDocumentByWord("IAIDQ");
        assertEquals(false, docs.totalHits == 0);
        Document document = search.getDocument(docs.scoreDocs[0].doc);
        Assert.assertNotNull(document);
        String[] values = document.getValues(SynonymIndexSearcher.F_WORD);
        Assert.assertNotNull(values);
        assertEquals(1, values.length);
        search.close();
    }

    /**
     * Test method for {@link org.talend.dataquality.standardization.index.SynonymIndexSearcher#getWordByDocNumber(int)}
     * .
     */
    @Test
    public void testGetWordByDocNumber() {
        SynonymIndexSearcher search = new SynonymIndexSearcher();
        try {
            search.openIndexInFS(SynonymIndexBuilderTest.path);
        } catch (IOException e) {
            fail(e.getMessage());
        }
        String word = search.getWordByDocNumber(0);
        Assert.assertNotNull(word);
        Assert.assertNotSame(0, word.length());
        // the word found should be one the input words
        boolean wordFound = false;
        for (int i = 0; i < SynonymIndexBuilderTest.synonyms.length; i++) {
            String[] input = SynonymIndexBuilderTest.synonyms[i];
            if (word.equals(input[0])) {
                wordFound = true;
                break;
            }
        }
        Assert.assertTrue(wordFound);
        search.close();
    }

    /**
     * Test method for
     * {@link org.talend.dataquality.standardization.index.SynonymIndexSearcher#getSynonymsByDocNumber(int)}.
     */
    @Test
    public void testGetSynonymsByDocNumber() {
        SynonymIndexSearcher search = new SynonymIndexSearcher();
        try {
            search.openIndexInFS(SynonymIndexBuilderTest.path);
        } catch (IOException e) {
            fail(e.getMessage());
        }
        String[] syns = search.getSynonymsByDocNumber(0);
        Assert.assertNotNull(syns);
        Assert.assertNotSame(0, syns.length);
        // the synonyms found should be one the input synonyms
        boolean synonymFound = false;
        for (int i = 0; i < SynonymIndexBuilderTest.synonyms.length; i++) {
            String[] input = SynonymIndexBuilderTest.synonyms[i];
            for (int j = 0; j < syns.length; j++) {
                String syn = syns[j];
                if (input[1].contains(syn)) {
                    synonymFound = true;
                    break;
                }
            }
        }
        Assert.assertTrue(synonymFound);
        search.close();
    }

    /**
     * Test method for {@link org.talend.dataquality.standardization.index.SynonymIndexSearcher#getNumDocs()}.
     */
    @Test
    public void testGetNumDocs() {
        SynonymIndexSearcher search = new SynonymIndexSearcher();
        try {
            search.openIndexInFS(SynonymIndexBuilderTest.path);
        } catch (IOException e) {
            fail(e.getMessage());
        }
        int numDocs = search.getNumDocs();
        assertEquals(SynonymIndexBuilderTest.synonyms.length, numDocs);
        search.close();
    }

}
