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

import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.junit.Before;
import org.junit.Test;

/**
 * DOC scorreia class global comment. Detailled comment
 */
public class SynonymIndexBuilderTest {

    // The abosolute path will be "path/to/svn/top/org.talend.dataquality.standardization.test/data/index
    static String path = "data/index";

    /**
     * ATTENTION: Be careful when changing this list of synonyms, they are also use in SynonymIndexSearcherTest.
     */
    public static String[][] synonyms = { { "I.B.M.", "IBM|International Business Machines|Big Blue" },
            { "IRTY", "IBM|International Business Machines" },
            { "ISDF", "IBM|International Business Machines|Big Blue" },
            { "ANPE", "A.N.P.E.|Agence Nationale Pour l'Emploi|Pôle Emploi" },
            { "TEST", "A.N.P.E.|Agence Nationale Pour l'Emploi|Pôle Emploi" }, { "Sécurité Sociale", "Sécu|SS|CPAM" },
            { "IAIDQ", "International Association for Information & Data Quality|Int. Assoc. Info & DQ" }, };

    // private SynonymIndexBuilder builder;

    @Before
    public void setUp() throws Exception {
        SynonymIndexBuilder builder = new SynonymIndexBuilder();
        // clear any existing files
        assertEquals(builder.getError().getMessage(), true, builder.deleteIndexFromFS(path));

        // builder = new SynonymIndexBuilder();
        //
        // // start with a clean index: delete previous index
        // // builder.closeIndex();
        // builder.deleteIndexFromFS(path);
        //
        // builder.initIndexInFS(path);
        //
        // // insert docs
        // insertDocuments(builder);

        // no need because default is set
        // builder.setSynonymSeparator('|');

        // searcher = new SynonymIndexSearcher();
        // searcher.initIndexInFS(path);
    }

    SynonymIndexSearcher getSearcher(SynonymIndexBuilder builder) {
        SynonymIndexSearcher searcher = new SynonymIndexSearcher();
        try {
            searcher.setAnalyzer(builder.getAnalyzer());
            searcher.openIndexInFS(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        searcher.setTopDocLimit(5);
        return searcher;
    }

    public void finalize() throws Exception {
        //
        // try {
        // builder.closeIndex();
        // } catch (Exception e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
    }

    void insertDocuments(SynonymIndexBuilder build) throws IOException {
        for (String[] syns : synonyms) {
            build.insertDocument(syns[0], syns[1]);
        }
        build.commit();
    }

    private SynonymIndexBuilder createNewIndexBuilder(String p) {
        SynonymIndexBuilder builder = new SynonymIndexBuilder();
        builder.initIndexInFS(p);
        return builder;
    }

    @Test
    public void testInsertDocumentIfNotExists() throws Exception {
        System.out.println("\n---------------Test addDocument------------------");
        SynonymIndexBuilder builder = createNewIndexBuilder(path);
        insertDocuments(builder);

        // this.testInsertDocuments();// insert documents first
        builder.insertDocumentIfNotExists("ADD", "This|is|a|new|document");
        builder.commit();
        SynonymIndexSearcher searcher = getSearcher(builder);
        assertEquals(synonyms.length + 1, searcher.getNumDocs());

        builder.insertDocumentIfNotExists("ANPE", "This|is|an|existing|document");
        builder.commit();
        searcher.close();

        searcher = getSearcher(builder);
        assertEquals(synonyms.length + 1, searcher.getNumDocs());
        searcher.close();

        builder.insertDocumentIfNotExists("Irish Bar Managers", "IBM");
        builder.commit();

        searcher = getSearcher(builder);
        assertEquals(synonyms.length + 2, searcher.getNumDocs());
        searcher.close();
        builder.closeIndex();
    }

    @Test
    public void testInsertDocuments() throws Exception {
        System.out.println("---------------Test insertDocuments--------------");

        SynonymIndexBuilder builder = createNewIndexBuilder(path);
        insertDocuments(builder);

        SynonymIndexSearcher searcher = getSearcher(builder);
        assertEquals(synonyms.length, searcher.getNumDocs());
        builder.closeIndex();
        searcher.close();
    }

    @Test
    public void testUpdateSynonymDocument() throws Exception {
        System.out.println("\n---------------Test updateDocument---------------");
        int updateCount = 0;
        SynonymIndexBuilder builder = createNewIndexBuilder(path);
        insertDocuments(builder);

        SynonymIndexSearcher searcher = getSearcher(builder);
        assertEquals(0, searcher.searchDocumentBySynonym("updated").totalHits);

        updateCount += builder.updateDocument("Sécurité Sociale", "I|have|been|updated");
        builder.commit();
        // close previous searcher
        searcher.close();

        searcher = getSearcher(builder);
        assertEquals(1, searcher.searchDocumentBySynonym("updated").totalHits);

        updateCount += builder.updateDocument("INEXIST", "I|don't|exist");
        builder.commit();
        // close previous searcher
        searcher.close();

        searcher = getSearcher(builder);
        assertEquals(0, searcher.searchDocumentBySynonym("exist").totalHits);
        // close builders and searchers that are not used later
        builder.closeIndex();
        searcher.close();
    }

    @Test
    public void testUpdateSynonymDocument2() throws Exception {

        // --- create a new index with several similar documents
        SynonymIndexBuilder synIdxBuild = new SynonymIndexBuilder();
        String idxPath = "data/test_update";
        synIdxBuild.deleteIndexFromFS(idxPath);
        synIdxBuild.initIndexInFS(idxPath);
        int maxDoc = 4;
        String word = "salut";
        for (int i = 0; i < maxDoc; i++) {
            synIdxBuild.insertDocument(word, "synonym|toto");
        }
        String toupdate = "to update.";
        synIdxBuild.insertDocument(toupdate, "this document will be updated");
        int nbDocInIndex = maxDoc + 1;
        assertEquals(nbDocInIndex, synIdxBuild.getNumDocs());
        synIdxBuild.commit();

        int nbUpdatedDocuments = synIdxBuild.updateDocument("unknown", "new syn");
        assertEquals("there should be no document to update", 0, nbUpdatedDocuments);
        assertEquals("The document should not be inserted here", nbDocInIndex, synIdxBuild.getNumDocs());

        nbUpdatedDocuments = synIdxBuild.updateDocument(word, "new syn");
        assertEquals("no update should be done because several documents match the word " + word, -1, nbUpdatedDocuments);

        nbUpdatedDocuments = synIdxBuild.updateDocument(toupdate, "a new list of 3 synonyms|test|ok");
        synIdxBuild.commit();
        synIdxBuild.closeIndex();

        SynonymIndexSearcher search = new SynonymIndexSearcher();
        search.setTopDocLimit(maxDoc); // retrieve all possible documents
        search.openIndexInFS(idxPath);
        TopDocs salutDocs = search.searchDocumentByWord(word);
        assertEquals(maxDoc, salutDocs.totalHits);
        for (int i = 0; i < salutDocs.scoreDocs.length; i++) {
            ScoreDoc scoreDoc = salutDocs.scoreDocs[i];
            Document document = search.getDocument(scoreDoc.doc);
            String syn = document.get(SynonymIndexSearcher.F_SYN);
            assertEquals("the first synonym field should be the same as the word (after being analyzed)", word, syn);
            String[] values = document.getValues(SynonymIndexSearcher.F_SYN);
            // expect to see "salut" and "synonym" and "toto"
            assertEquals(3, values.length);
            assertEquals(word, values[0]);
            assertEquals("synonym", values[1]);
            assertEquals("toto", values[2]);
        }

        TopDocs updatedDocs = search.searchDocumentByWord(toupdate);
        assertEquals(1, updatedDocs.totalHits);
        for (int i = 0; i < updatedDocs.scoreDocs.length; i++) {
            ScoreDoc scoreDoc = updatedDocs.scoreDocs[i];
            Document document = search.getDocument(scoreDoc.doc);
            String syn = document.get(SynonymIndexSearcher.F_SYN);
            assertEquals("the first synonym field should be the same as the word (after being analyzed)", toupdate, syn);
            String[] values = document.getValues(SynonymIndexSearcher.F_SYN);
            // expect to see "salut" and "synonym" and "toto"
            assertEquals("there should 3 synonyms + the reference word", 4, values.length);
            assertEquals(toupdate, values[0]);
            assertEquals("a new list of 3 synonyms", values[1]);
            assertEquals("test", values[2]);
            assertEquals("ok", values[3]);
        }
        search.close();
    }

    @Test
    public void testDeleteDocumentByWord() throws IOException {
        System.out.println("\n---------------Test deleteDocument---------------");
        SynonymIndexBuilder builder = createNewIndexBuilder(path);
        insertDocuments(builder);
        SynonymIndexSearcher searcher = getSearcher(builder);
        int docCount = searcher.getNumDocs();

        assertEquals(1, searcher.searchDocumentByWord("IAIDQ").totalHits);

        // the word to delete should be precise and case sensitive.
        builder.deleteDocumentByWord("iaidq");
        builder.commit();
        // close previous searcher
        searcher.close();

        searcher = getSearcher(builder);
        assertEquals(docCount, searcher.getNumDocs());
        // close previous searcher
        searcher.close();

        builder.deleteDocumentByWord("IAIDQ");
        searcher = getSearcher(builder);
        assertEquals(docCount, searcher.getNumDocs());
        // close previous searcher
        searcher.close();

        builder.commit();
        searcher = getSearcher(builder);
        assertEquals(docCount - 1, searcher.getNumDocs());

        builder.deleteDocumentByWord("random");
        builder.commit();
        // close previous searcher
        searcher.close();

        searcher = getSearcher(builder);
        assertEquals(docCount - 1, searcher.getNumDocs());
        // close previous searcher
        searcher.close();
        builder.closeIndex();

    }

    @Test
    public void testAddSynonymToWord() throws IOException, ParseException {

        System.out.println("\n---------------Test addSynonymToWord-------------");
        SynonymIndexBuilder builder = createNewIndexBuilder(path);
        insertDocuments(builder);
        SynonymIndexSearcher searcher = getSearcher(builder);
        assertEquals(0, searcher.searchDocumentBySynonym("another").totalHits);

        int synonymCount = searcher.getSynonymCount("ANPE");
        builder.addSynonymToDocument("ANPE", "Another synonym of ANPE");
        builder.commit();
        searcher.close();

        searcher = getSearcher(builder);
        assertEquals(1, searcher.searchDocumentBySynonym("another").totalHits);

        int addedSynonymToDocument = builder.addSynonymToDocument("ANPE", "Anpe");
        assertEquals("anpe already exists as ANPE in the synonym list", 0, addedSynonymToDocument);
        builder.commit();
        searcher.close();

        searcher = getSearcher(builder);
        assertEquals(synonymCount + 1, searcher.getSynonymCount("ANPE"));

        builder.addSynonymToDocument("ANPEEEE", "A.N.P.E");
        builder.commit();
        searcher.close();

        searcher = getSearcher(builder);
        assertEquals(0, searcher.searchDocumentByWord("ANPEEEE").totalHits);
        searcher.close();
        builder.closeIndex();
    }

    @Test
    public void testRemoveSynonymFromWord() throws IOException {
        System.out.println("\n------------Test removeSynonymFromWord-----------");
        SynonymIndexBuilder builder = createNewIndexBuilder(path);
        insertDocuments(builder);

        SynonymIndexSearcher searcher = getSearcher(builder);
        int synonymCount = searcher.getSynonymCount("ANPE");
        // the synonym to delete should be precise and case sensitive
        int removed = builder.removeSynonymFromDocument("ANPE", "Agence Nationale Pour l'Emploi");
        assertEquals(1, removed);
        builder.commit();
        searcher.close();

        searcher = getSearcher(builder);
        assertEquals(--synonymCount, searcher.getSynonymCount("ANPE"));

        removed = builder.removeSynonymFromDocument("ANPE", "Anpe");
        assertEquals(0, removed);
        builder.commit();
        searcher.close();

        searcher = getSearcher(builder);
        assertEquals(synonymCount, searcher.getSynonymCount("ANPE"));

        removed = builder.removeSynonymFromDocument("ANPE", "A.N.P.E.");
        assertEquals(1, removed);
        removed = builder.removeSynonymFromDocument("ANPE", "A.N.P.E.");
        assertEquals("We did not commit, so we still should find a synonym to delete here", 1, removed);
        builder.commit();
        removed = builder.removeSynonymFromDocument("ANPE", "A.N.P.E.");
        assertEquals(0, removed);
        searcher.close();

        searcher = getSearcher(builder);
        assertEquals(--synonymCount, searcher.getSynonymCount("ANPE"));

        removed = builder.removeSynonymFromDocument("ANPE", "Pôle Emploi");
        assertEquals(1, removed);
        builder.commit();
        searcher.close();

        searcher = getSearcher(builder);
        assertEquals(--synonymCount, searcher.getSynonymCount("ANPE"));

        removed = builder.removeSynonymFromDocument("ANPEEEE", "A.N.P.E");
        assertEquals(0, removed);
        searcher.close();
        builder.closeIndex();
    }

    @Test
    public void testDeleteAllDocuments() throws IOException {
        System.out.println("\n-----------Test deleteAllDocuments----------");

        SynonymIndexBuilder builder = createNewIndexBuilder(path);
        insertDocuments(builder);
        builder.deleteAllDocuments();
        assertEquals(0, builder.getWriter().numDocs());
        SynonymIndexSearcher searcher = getSearcher(builder);
        assertEquals("A searcher should still see the documents as no commit has been done yet", false,
                searcher.getNumDocs() == 0);
        builder.commit();
        assertEquals(
                "The previous searcher should still see the documents as it still has a reader on the indexs before the commit has been done",
                false, searcher.getNumDocs() == 0);
        // builder.closeIndex();
        searcher.close();

        searcher = getSearcher(builder);

        assertEquals("A new searcher should not see the documents anymore as a commit has been done", true,
                searcher.getNumDocs() == 0);
        searcher.close();
        builder.closeIndex();
    }

    @Test
    public void deleteIndexFromFS() throws IOException {
        System.out.println("\n-----------Test deleteIndexFromFS----------");
        String indexPath = "data/index2";
        SynonymIndexBuilder synonymIndexBuilder = new SynonymIndexBuilder();
        synonymIndexBuilder.initIndexInFS(indexPath);
        File indexfile = new File(indexPath);
        assertEquals(true, indexfile.exists());

        // TODO test with lock?
        synonymIndexBuilder.insertDocument("salut", "toto");
        synonymIndexBuilder.commit();
        synonymIndexBuilder.closeIndex();

        boolean deleteIndexFromFS = synonymIndexBuilder.deleteIndexFromFS(indexPath);
        assertEquals(synonymIndexBuilder.getError().getMessage(), true, deleteIndexFromFS);
        assertEquals(false, indexfile.exists());
    }

    @Test
    public void initIndexInFS() throws IOException {
        String indexPath = "data/index3";
        SynonymIndexBuilder synonymIndexBuilder = new SynonymIndexBuilder();
        synonymIndexBuilder.initIndexInFS(indexPath);
        synonymIndexBuilder.insertDocument("salut", "toto");
        synonymIndexBuilder.commit();

        SynonymIndexSearcher searcher = new SynonymIndexSearcher(indexPath);
        int numDocs = searcher.getNumDocs();

        // check that two calls of initIndexInFS does not reset the index.
        synonymIndexBuilder.initIndexInFS(indexPath);
        synonymIndexBuilder.insertDocument("bye", "au revoir");
        synonymIndexBuilder.commit();

        // get a new searcher because the previous is open on the index when it contained only one document.
        SynonymIndexSearcher searcher2 = new SynonymIndexSearcher(indexPath);
        assertEquals(numDocs + 1, searcher2.getNumDocs());

        synonymIndexBuilder.closeIndex();
        searcher.close();
        searcher2.close();
        boolean deleted = synonymIndexBuilder.deleteIndexFromFS(indexPath);
        assertEquals(true, deleted);
    }
    /**
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
