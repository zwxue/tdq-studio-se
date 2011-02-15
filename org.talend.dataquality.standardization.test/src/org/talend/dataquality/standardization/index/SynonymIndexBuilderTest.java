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

import java.io.IOException;
import java.util.Arrays;

import org.apache.lucene.search.TopDocs;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * DOC scorreia class global comment. Detailled comment
 */
public class SynonymIndexBuilderTest {

    // The abosolute path will be "path/to/svn/top/org.talend.dataquality.standardization.test/data/index
    private static String path = "data/index";


    private static SynonymAnalyzer synonymAnalyzer;

    private static String[][] synonyms = { { "I.B.M.", "IBM|International Business Machines|Big Blue" },
            { "ISDF", "IBM|International Business Machines|Big Blue" },
 { "IRTY", "IBM|International Business Machines" },
            { "ANPE", "A.N.P.E.|Agence Nationale Pour l'Emploi|Pôle Emploi" }, { "Sécurité Sociale", "Sécu|SS|CPAM" },
            { "IAIDQ", "International Association for Information & Data Quality|Int. Assoc. Info & DQ" }, };

    private static boolean useQueryParser = true;

    private static boolean useAllDocCollector = false;

    private static boolean useMemeryForIndex = false;

    private SynonymIndexBuilder builder;

    private SynonymIndexSearcher searcher;

    @Before
    public void setUp() throws Exception {
        builder = new SynonymIndexBuilder();

        builder.setUsingCreateMode(true);
        builder.initIndexInFS(path);

        // builder.initIndexInRAM();
        builder.setSynonymSeparator('|');
        
        searcher = new SynonymIndexSearcher();
        searcher.initIndexInFS(path);
    }

    @After
    public void finalize() throws Exception {
        builder.closeIndex();
    }

    @Test
    public void testInsertDocuments() throws Exception {
        System.out.println("---------------Test insertDocuments--------------");
        builder.deleteAllDocuments();
        for (String[] syns : synonyms) {
            builder.insertDocument(syns[0], syns[1]);
        }
        assertEquals(synonyms.length, searcher.getNumDocs());
    }

    @Test
    public void testInsertSynonymDocument() throws IOException {
        System.out.println("\n---------------Test addDocument------------------");

        builder.insertDocument("ADD", "This|is|a|new|document");
        assertEquals(synonyms.length + 1, searcher.getNumDocs());
        builder.insertDocument("ANPE", "This|is|an|existing|document");
        assertEquals(synonyms.length + 1, searcher.getNumDocs());
        builder.insertDocument("Irish Bar Managers", "IBM");
        assertEquals(synonyms.length + 2, searcher.getNumDocs());

    }

    @Test
    public void testUpdateSynonymDocument() throws IOException {
        System.out.println("\n---------------Test updateDocument---------------");
        int updateCount = 0;
        assertEquals(0, searcher.searchDocumentBySynonym("updated").totalHits);
        updateCount += builder.updateDocument("Sécurité Sociale", "I|have|been|updated");
        assertEquals(1, searcher.searchDocumentBySynonym("updated").totalHits);
        updateCount += builder.updateDocument("INEXIST", "I|don't|exist");
        assertEquals(0, searcher.searchDocumentBySynonym("exist").totalHits);
    }

    @Test
    public void testDeleteSynonymDocument() throws IOException {
        System.out.println("\n---------------Test deleteDocument---------------");
        int docCount = searcher.getNumDocs();

        assertEquals(1, searcher.searchDocumentByWord("IAIDQ").totalHits);

        // the word to delete should be precise and case sensitive.
        builder.deleteDocumentByWord("iaidq");
        assertEquals(docCount, searcher.getNumDocs());

        builder.deleteDocumentByWord("IAIDQ");
        assertEquals(docCount - 1, searcher.getNumDocs());

        builder.deleteDocumentByWord("random");
        assertEquals(docCount - 1, searcher.getNumDocs());

    }

    @Test
    public void testAddSynonymToWord() throws IOException {

        System.out.println("\n---------------Test addSynonymToWord-------------");
        assertEquals(0, searcher.searchDocumentBySynonym("another").totalHits);

        int synonymCount = searcher.getSynonymCount("anpe");
        builder.addSynonymToDocument("ANPE", "Another synonym of ANPE");
        assertEquals(1, searcher.searchDocumentBySynonym("another").totalHits);

        builder.addSynonymToDocument("ANPE", "Anpe");
        assertEquals(synonymCount + 1, searcher.getSynonymCount("anpe"));

        builder.addSynonymToDocument("ANPEEEE", "A.N.P.E");
        assertEquals(0, searcher.searchDocumentByWord("ANPEEEE").totalHits);

    }

    @Test
    public void testRemoveSynonymFromWord() throws IOException {
        System.out.println("\n------------Test removeSynonymFromWord-----------");

        int synonymCount = searcher.getSynonymCount("ANPE");
        // the synonym to delete should be precise and case sensitive
        builder.removeSynonymFromDocument("ANPE", "Another synonym of ANPE");
        assertEquals(--synonymCount, searcher.getSynonymCount("ANPE"));

        builder.removeSynonymFromDocument("ANPE", "Anpe");
        assertEquals(synonymCount, searcher.getSynonymCount("ANPE"));

        builder.removeSynonymFromDocument("ANPE", "A.N.P.E.");
        builder.removeSynonymFromDocument("ANPE", "A.N.P.E.");
        assertEquals(--synonymCount, searcher.getSynonymCount("ANPE"));

        builder.removeSynonymFromDocument("ANPE", "Pôle Emploi");
        assertEquals(--synonymCount, searcher.getSynonymCount("ANPE"));

        builder.removeSynonymFromDocument("ANPEEEE", "A.N.P.E");

    }

    @Test
    public void testSearchDocumentBySynonym() throws IOException {
        System.out.println("\n-----------Test searchDocumentBySynonym----------");
        TopDocs docs = searcher.searchDocumentBySynonym("ibm");
        System.out.println(docs.totalHits + " documents found.");
        

        for (int i = 0; i < docs.totalHits; i++) {
            int docNumber = docs.scoreDocs[i].doc;
            System.out.print("\ndoc=" + docNumber + "\tscore=" + docs.scoreDocs[i].score);
            // Document doc = builder.getSearcher().doc(docs.scoreDocs[i].doc);
            System.out.println("\tword: " + searcher.getWordByDocNumber(docNumber));
            System.out.println("\tsynonyms: " + Arrays.toString(searcher.getSynonymsByDocNumber(docNumber)));
        }

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
        builder.commit();
        builder.deleteIndexFromFS();
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
