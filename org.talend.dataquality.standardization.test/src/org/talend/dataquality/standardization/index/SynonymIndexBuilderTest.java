// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
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

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.lucene.search.TopDocs;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.talend.dataquality.standardization.record.SynonymRecordSearcher;
import org.talend.dataquality.standardization.record.SynonymRecordSearcher.OutputRecord;

/**
 * DOC scorreia class global comment. Detailled comment
 */
public class SynonymIndexBuilderTest {

    // The abosolute path will be "path/to/svn/top/org.talend.dataquality.standardization.test/data/index
    private static String path = "data/index";

    private static String[][] synonyms = { { "I.B.M.", "IBM|International Business Machines|Big Blue" },
            { "ISDF", "IBM|International Business Machines|Big Blue" },
 { "IRTY", "IBM|International Business Machines" },
            { "ANPE", "A.N.P.E.|Agence Nationale Pour l'Emploi|Pôle Emploi" },
            { "TEST", "A.N.P.E.|Agence Nationale Pour l'Emploi|Pôle Emploi" }, { "Sécurité Sociale", "Sécu|SS|CPAM" },
            { "IAIDQ", "International Association for Information & Data Quality|Int. Assoc. Info & DQ" }, };


    private SynonymIndexBuilder builder;

    @Before
    public void setUp() throws Exception {
        builder = new SynonymIndexBuilder();

        // start with a clean index: delete previous index
        builder.deleteIndexFromFS(path);

        builder.setUsingCreateMode(true);
        builder.initIndexInFS(path);

        // insert docs
        insertDocuments();

        // no need because default is set
        // builder.setSynonymSeparator('|');
        

        // searcher = new SynonymIndexSearcher();
        // searcher.initIndexInFS(path);
    }

    private SynonymIndexSearcher getSearcher() {
        SynonymIndexSearcher searcher = new SynonymIndexSearcher();
        try {
            searcher.initIndexInFS(path);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        searcher.setTopDocLimit(5);
        return searcher;
    }

    @After
    public void finalize() throws Exception {
        builder.closeIndex();
    }

    private void insertDocuments() throws Exception {
        for (String[] syns : synonyms) {
            builder.insertDocument(syns[0], syns[1]);
        }
    }

    @Test
    public void testInsertDocuments() throws Exception {
        System.out.println("---------------Test insertDocuments--------------");
        // builder.deleteAllDocuments();

        assertEquals(synonyms.length, getSearcher().getNumDocs());
    }

    @Test
    public void testInsertSynonymDocument() throws Exception {
        System.out.println("\n---------------Test addDocument------------------");

        // this.testInsertDocuments();// insert documents first
        builder.insertDocument("ADD", "This|is|a|new|document");
        SynonymIndexSearcher searcher = getSearcher();
        assertEquals(synonyms.length + 1, searcher.getNumDocs());
        builder.insertDocument("ANPE", "This|is|an|existing|document");
        searcher = getSearcher();
        assertEquals(synonyms.length + 1, searcher.getNumDocs());
        builder.insertDocument("Irish Bar Managers", "IBM");
        searcher = getSearcher();
        assertEquals(synonyms.length + 2, searcher.getNumDocs());

    }

    @Test
    public void testUpdateSynonymDocument() throws IOException {
        System.out.println("\n---------------Test updateDocument---------------");
        int updateCount = 0;
        SynonymIndexSearcher searcher = getSearcher();
        assertEquals(0, searcher.searchDocumentBySynonym("updated").totalHits);
        updateCount += builder.updateDocument("Sécurité Sociale", "I|have|been|updated");
        searcher = getSearcher();
        assertEquals(1, searcher.searchDocumentBySynonym("updated").totalHits);
        updateCount += builder.updateDocument("INEXIST", "I|don't|exist");
        searcher = getSearcher();
        assertEquals(0, searcher.searchDocumentBySynonym("exist").totalHits);
    }

    @Test
    public void testDeleteSynonymDocument() throws IOException {
        System.out.println("\n---------------Test deleteDocument---------------");
        SynonymIndexSearcher searcher = getSearcher();
        int docCount = searcher.getNumDocs();

        assertEquals(1, searcher.searchDocumentByWord("IAIDQ").totalHits);

        // the word to delete should be precise and case sensitive.
        builder.deleteDocumentByWord("iaidq");
        searcher = getSearcher();
        assertEquals(docCount, searcher.getNumDocs());

        builder.deleteDocumentByWord("IAIDQ");
        searcher = getSearcher();
        assertEquals(docCount - 1, searcher.getNumDocs());

        builder.deleteDocumentByWord("random");
        searcher = getSearcher();
        assertEquals(docCount - 1, searcher.getNumDocs());

    }

    @Test
    public void testAddSynonymToWord() throws IOException {

        System.out.println("\n---------------Test addSynonymToWord-------------");
        SynonymIndexSearcher searcher = getSearcher();
        assertEquals(0, searcher.searchDocumentBySynonym("another").totalHits);

        int synonymCount = searcher.getSynonymCount("anpe");
        builder.addSynonymToDocument("ANPE", "Another synonym of ANPE");
        searcher = getSearcher();
        assertEquals(1, searcher.searchDocumentBySynonym("another").totalHits);

        builder.addSynonymToDocument("ANPE", "Anpe");
        searcher = getSearcher();
        assertEquals(synonymCount + 1, searcher.getSynonymCount("anpe"));

        builder.addSynonymToDocument("ANPEEEE", "A.N.P.E");
        searcher = getSearcher();
        assertEquals(0, searcher.searchDocumentByWord("ANPEEEE").totalHits);

    }

    @Test
    public void testRemoveSynonymFromWord() throws IOException {
        System.out.println("\n------------Test removeSynonymFromWord-----------");

        int synonymCount = getSearcher().getSynonymCount("ANPE");
        // the synonym to delete should be precise and case sensitive
        builder.removeSynonymFromDocument("ANPE", "Agence Nationale Pour l'Emploi");

        assertEquals(--synonymCount, getSearcher().getSynonymCount("ANPE"));

        builder.removeSynonymFromDocument("ANPE", "Anpe");
        assertEquals(synonymCount, getSearcher().getSynonymCount("ANPE"));

        builder.removeSynonymFromDocument("ANPE", "A.N.P.E.");
        builder.removeSynonymFromDocument("ANPE", "A.N.P.E.");
        assertEquals(--synonymCount, getSearcher().getSynonymCount("ANPE"));

        builder.removeSynonymFromDocument("ANPE", "Pôle Emploi");
        assertEquals(--synonymCount, getSearcher().getSynonymCount("ANPE"));

        builder.removeSynonymFromDocument("ANPEEEE", "A.N.P.E");

    }

    @Test
    public void testSearchDocumentBySynonym() throws IOException {
        System.out.println("\n-----------Test searchDocumentBySynonym----------");
        SynonymIndexSearcher searcher = getSearcher();
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
        // FIXME add other assertions here

    }

    @Test
    public void testSearchInSeveralIndexes() throws IOException {
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

        // FIXME add assertions here (or create a junit test for SynonymRecordSearcher instead)

    }

    @Test
    public void testDeleteAllDocuments() throws IOException {
        System.out.println("\n-----------Test deleteAllDocuments----------");
        builder.deleteAllDocuments();
        assertEquals(0, builder.getWriter().numDocs());
    }

    @Test
    public void deleteIndexFromFS() throws IOException {
        System.out.println("\n-----------Test deleteIndexFromFS----------");
        // TODO not yet resolved.
        // segment files are deleted but not the entire directory.

        String indexPath = "data/index2";
        builder.initIndexInFS(indexPath);
        File indexfile = new File(indexPath);
        assertEquals(true, indexfile.exists());
        builder.deleteIndexFromFS(indexPath);
        assertEquals(false, indexfile.exists());

    }


    /**
     * DOC scorreia Comment method "search".
     * 
     * @param str
     */
    // private void search(String str) {
    // try {
    // Directory index = useMemeryForIndex ? builder.getIndexDir() : FSDirectory.open(new File(path));
    //
    // searcher = new IndexSearcher(index);
    //
    // // Query query = new QueryParser(Version.LUCENE_30, "syn", synonymAnalyzer).parse("\"" + str + "\"");
    // Query query = useQueryParser ? new QueryParser(Version.LUCENE_30, "syn", synonymAnalyzer).parse(str)
    // : new PhraseQuery();
    //
    // if (!useQueryParser) {
    // ((PhraseQuery) query).add(new Term("syn", str));
    // }
    //
    // List<ScoreDoc> scoreDocs = new ArrayList<ScoreDoc>();
    // if (useAllDocCollector) {
    // AllDocCollector collector = new AllDocCollector();
    // searcher.search(query, collector);
    // scoreDocs = collector.getHits();
    // } else {
    // TopScoreDocCollector collector = TopScoreDocCollector.create(1, false);
    // searcher.search(query, collector);
    // scoreDocs = Arrays.asList(collector.topDocs().scoreDocs);
    // }
    // if (scoreDocs.isEmpty()) {
    // fail("No doc found for " + str);
    // } else {
    // System.out.println("Got match for " + str);
    // for (ScoreDoc hits : scoreDocs) {
    // Document doc = searcher.doc(hits.doc);
    // String[] entry = doc.getValues("word");
    // for (String string : entry) {
    // System.out.println("entry=" + string);
    // }
    // String[] values = doc.getValues("syn");
    // for (String string : values) {
    // System.out.println("syn=" + string);
    // }
    // System.out.println();
    // }
    //
    // }
    // searcher.close();
    // index.close();
    // } catch (CorruptIndexException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // } catch (IOException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // } catch (ParseException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // }
    // }

}
