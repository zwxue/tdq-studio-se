// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
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

import java.io.IOException;
import java.util.Arrays;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.util.Version;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * DOC scorreia class global comment. Detailled comment
 */
public class SimilarityTest {

    private static final String PATH = "data/similarity";

    /**
     * ATTENTION: Be careful when changing this list of synonyms, they are also use in SynonymIndexSearcherTest.
     */
    public static String[][] synonyms = { { "Paris 5eme", "Paris 05 Panthéon|Paris 5|75005 paris" },
            { "Paris 2eme", "Paris 02 Bourse|Paris 2" }, { "Paris", "巴黎|Paryz|Parizh|Parizs|Paras|Pariz|Parigi" },
            { "Beijing", "Peking|Pékin" }, { "Beijing", "Peking|Pékin" }, { "Beijing", "Peking|Pékin" },
            { "Beijing", "Peking|Pékin" }, { "Beijing", "Peking|Pékin" }, { "Beijing", "Peking|Pékin" },
            { "Beijing", "Peking|Pékin" }, { "Beijing", "Peking|Pékin" }, { "Beijing", "Peking|Pékin" },
            { "Beijing", "Peking|Pékin" }, { "Beijing", "Peking|Pékin" }, { "Beijing", "Peking|Pékin" },
            { "Beijing", "Peking|Pékin" }, { "Beijing", "Peking|Pékin" }, { "Beijing", "Peking|Pékin" },
            { "Beijing", "Peking|Pékin" }, { "Beijing", "Peking|Pékin" }, { "Beijing", "Peking|Pékin" },
            { "Beijing", "Peking|Pékin" }, { "Beijing", "Peking|Pékin" }, { "Beijing", "Peking|Pékin" },
            { "Beijing", "Peking|Pékin" }, { "Beijing", "Peking|Pékin" }, { "Beijing", "Peking|Pékin" },
            { "Beijing", "Peking|Pékin" }, { "Beijing", "Peking|Pékin" }, { "Beijing", "Peking|Pékin" },
            { "Beijing", "Peking|Pékin" }, { "Beijing", "Peking|Pékin" }, { "Beijing", "Peking|Pékin" },
            { "Beijing", "Peking|Pékin" }, };

    /**
     * DOC scorreia Comment method "setUp".
     * 
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUp() throws Exception {
        SynonymIndexBuilder builder = new SynonymIndexBuilder();
        builder.deleteIndexFromFS(PATH);
        // clear any existing files
        assertEquals(builder.getError().getMessage(), true, builder.deleteIndexFromFS(PATH));

        builder.initIndexInFS(PATH);
        insertDocuments(builder);
        builder.closeIndex();
    }

    static void insertDocuments(SynonymIndexBuilder build) throws IOException {
        for (String[] syns : synonyms) {
            build.insertDocument(syns[0], syns[1]);
        }
        build.commit();
    }

    @Test
    public void testSearchDocumentBySynonym() throws IOException, ParseException {
        System.out.println("\n-----------Test searchDocumentBySynonym----------");
        SynonymIndexSearcher searcher = getSearcher();
        searcher.setAnalyzer(new StandardAnalyzer(Version.LUCENE_30));
        TopDocs docs = searcher.searchDocumentBySynonym("Paris");
        System.out.println(docs.totalHits + " documents found.");

        // assertEquals(3, docs.totalHits);
        // assertEquals(true, searcher.getTopDocLimit() >= docs.scoreDocs.length);
        for (int i = 0; i < docs.scoreDocs.length; i++) {
            int docNumber = docs.scoreDocs[i].doc;
            System.out.print("\ndoc=" + docNumber + "\tscore=" + docs.scoreDocs[i].score);
            // Document doc = builder.getSearcher().doc(docs.scoreDocs[i].doc);
            System.out.println("\tword: " + searcher.getWordByDocNumber(docNumber));
            System.out.println("\tsynonyms: " + Arrays.toString(searcher.getSynonymsByDocNumber(docNumber)));
        }

        searcher.close();

        // TODO check that the best matching is the exact string.
        assertEquals("the best matching should be the exact string", 2,docs.scoreDocs[0].doc);
        

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
            searcher.openIndexInFS(PATH);
        } catch (IOException e) {
            e.printStackTrace();
        }
        searcher.setTopDocLimit(5);

        return searcher;
    }

}
