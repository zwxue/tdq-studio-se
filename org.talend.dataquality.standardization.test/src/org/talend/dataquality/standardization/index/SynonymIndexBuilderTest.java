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
package org.talend.dataquality.standardization.index;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * DOC scorreia class global comment. Detailled comment
 */
public class SynonymIndexBuilderTest {

    private static File path = new File("data/index");

    private IndexSearcher searcher;

    private static SynonymAnalyzer synonymAnalyzer;

    private static String[][] synonyms = { { "I.B.M.", "IBM|International Business Machines|Big Blue" },
            { "ANPE", "A.N.P.E|Agence Nationale Pour l'Emploi|Pôle Emploi" }, { "Sécurité Sociale", "Sécu|SS|CPAM" },
            { "IAIDQ", "International Association for Information & Data Quality|Int. Assoc. Info & DQ" }, };

    private static final String[] testList = { "IBM", "International Business Machines", "Big Blue", "I.B.M.", "big blue", "ibm",
            "Ibm", "International assoc", "IAIDQ", "I.A.I.D.Q.", "I.B.M." };

    private static boolean useQueryParser = true;

    private static boolean useAllDocCollector = false;

    private static boolean useMemeryForIndex = false;

    private SynonymIndexBuilder builder;

    @Before
    public void setUp() throws Exception {
        initOnceForAll();
        synonymAnalyzer = new SynonymAnalyzer(new WordNetSynonymEngine(path));
    }

    private void initOnceForAll() throws IOException {
        if (!path.exists()) {
            fail("Path does not exist, please create " + path.getPath() + " first!");
        }


        builder = new SynonymIndexBuilder();
        builder.initIndexInfile(path);

        // builder.initIndexInRAM();

        builder.setSynonymSeparator('|');
    }

    @After
    public void closeIndex() throws Exception {
        builder.closeIndex();
    }


    private void insertAllDocuments() throws IOException {
        for (String[] syns : synonyms) {
            builder.insertSynonymDocument(syns[0], syns[1]);
        }
    }

    @Test
    public void testInsertDocuments() throws Exception {
        System.out.println("---------------Test insertDocuments--------------");
        builder.deleteAllDocuments();
        insertAllDocuments();
        assertEquals(synonyms.length, builder.getWriter().numDocs());
    }

    @Test
    public void testInsertSynonymDocument() throws IOException {
        System.out.println("\n-------------Test addSynonymDocument-------------");
        builder.insertSynonymDocument("ADD", "I|am|a|new|document");
        builder.insertSynonymDocument("I.B.M.", "This|is|an|existing|document");
        assertEquals(synonyms.length + 1, builder.getWriter().numDocs());
    }

    @Test
    public void testUpdateSynonymDocument() throws IOException {
        System.out.println("\n------------Test updateSynonymDocument-----------");
        int updateCount = 0;
        updateCount += builder.updateSynonymDocument("Sécurité Sociale", "I|have|been|updated");
        updateCount += builder.updateSynonymDocument("INEXIST", "I|don't|exist");
        assertEquals(1, updateCount);
    }

    @Test
    public void testDeleteSynonymDocument() throws IOException {
        System.out.println("\n------------Test deleteSynonymDocument-----------");
        int deleteCount = 0;

        assertEquals(1, builder.searchDocumentByWord("I.B.M.").totalHits);
        deleteCount += builder.deleteSynonymDocument("ibm");
        

        deleteCount += builder.deleteSynonymDocument("random");
        assertEquals(1, deleteCount);

    }

    @Test
    public void testAddSynonymToWord() throws IOException {

        System.out.println("\n---------------Test addSynonymToWord-------------");
        int addCount = 0;

        addCount += builder.addSynonymToWord("ANPE", "Another synonym of ANPE");
        addCount += builder.addSynonymToWord("ANPE", "A.N.P.E");
        addCount += builder.addSynonymToWord("ANPEEEE", "A.N.P.E");

        assertEquals(1, addCount);
    }

    @Test
    public void testDeleteSynonymFromWord() throws IOException {
        System.out.println("\n------------Test deleteSynonymFromWord-----------");

        int deleteCount = 0;

        // the synonym to delete should be precise and case sensitive
        deleteCount += builder.deleteSynonymFromWord("ANPE", "a.n.p.e");
        deleteCount += builder.deleteSynonymFromWord("ANPE", "A.N.P.E");
        deleteCount += builder.deleteSynonymFromWord("ANPE", "Pôle Emploi");

        assertEquals(2, deleteCount);
    }

    @Test
    public void testSearchDocumentBySynonym() throws IOException {
        System.out.println("\n-----------Test searchDocumentBySynonym----------");
        TopDocs docs = builder.searchDocumentBySynonym("i");
        System.out.println(docs.totalHits + " documents found.");
        for (int i = 0; i < docs.totalHits; i++) {
            System.out.print(docs.scoreDocs[i]);
            Document doc = builder.getSearcher().doc(docs.scoreDocs[i].doc);
            System.out.println(Arrays.toString(doc.getValues("syn")));
        }

        assertEquals(2, docs.totalHits);
    }

    // @Test
    public void testDeleteAll() throws IOException {
        builder.deleteAllDocuments();
        assertEquals(0, builder.getWriter().numDocs());
    }

    /**
     * Test method for
     * {@link org.talend.dataquality.standardization.index.SynonymIndexBuilder#input(java.lang.String, java.lang.String, char)}
     * .
     */
    public void testIndexStringStringChar() {
        for (String str : testList) {
            search(str);
        }

    }

    /**
     * DOC scorreia Comment method "search".
     * 
     * @param str
     */
    private void search(String str) {
        try {
            Directory index = useMemeryForIndex ? builder.getIndexDir() : FSDirectory.open(path);

            searcher = new IndexSearcher(index);

            // Query query = new QueryParser(Version.LUCENE_30, "syn", synonymAnalyzer).parse("\"" + str + "\"");
            Query query = useQueryParser ? new QueryParser(Version.LUCENE_30, "syn", synonymAnalyzer).parse(str)
                    : new PhraseQuery();

            if (!useQueryParser) {
                ((PhraseQuery) query).add(new Term("syn", str));
            }

            List<ScoreDoc> scoreDocs = new ArrayList<ScoreDoc>();
            if (useAllDocCollector) {
                AllDocCollector collector = new AllDocCollector();
                searcher.search(query, collector);
                scoreDocs = collector.getHits();
            } else {
                TopScoreDocCollector collector = TopScoreDocCollector.create(1, false);
                searcher.search(query, collector);
                scoreDocs = Arrays.asList(collector.topDocs().scoreDocs);
            }
            if (scoreDocs.isEmpty()) {
                fail("No doc found for " + str);
            } else {
                System.out.println("Got match for " + str);
                for (ScoreDoc hits : scoreDocs) {
                    Document doc = searcher.doc(hits.doc);
                    String[] entry = doc.getValues("word");
                    for (String string : entry) {
                        System.out.println("entry=" + string);
                    }
                    String[] values = doc.getValues("syn");
                    for (String string : values) {
                        System.out.println("syn=" + string);
                    }
                    System.out.println();
                }

            }
            searcher.close();
            index.close();
        } catch (CorruptIndexException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
