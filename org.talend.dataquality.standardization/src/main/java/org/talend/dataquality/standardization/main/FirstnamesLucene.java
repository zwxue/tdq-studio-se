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
package org.talend.dataquality.standardization.main;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.talend.dataquality.standardization.index.IndexBuilder;
import org.talend.dataquality.standardization.query.FirstNameStandardize;

/**
 * DOC scorreia class global comment. Detailled comment
 */
public final class FirstnamesLucene {

    private FirstnamesLucene() {
    }

    public static void main(String[] args) throws IOException, ParseException {

        // input filename to be indexed once for all.
        String filename = "./data/TalendGivenNames.TXT";
        String indexfolder = "./data/TalendGivenNames_index";
        IndexBuilder idxBuilder = new IndexBuilder(indexfolder);

        // this file contains several columns. We don't need all columns right now.
        // The columns are the following: firstname, corrected name, count, related name
        int[] columnsToBeIndexed = new int[] { 0, 1, 2, 3 };

        // 0. Specify the analyzer for tokenizing text.
        // The same analyzer should be used for indexing and searching
        // Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_30);
        Analyzer analyzer = new SimpleAnalyzer();

        // 1. create the index
        // TODO not sure about the best directory to choose, please investigate

        idxBuilder.initializeIndex(filename, columnsToBeIndexed);

        // 2. query
        // TODO the query will be the data of an input flow in a Talend job.
        String[] firstnames = args.length > 0 ? args : new String[] { "jeants", "rémy", "jean-philippe", "sebastiao", "r*my*" };

        // 3. search
        int hitsPerPage = 10;
        Directory index = idxBuilder.getIndex();

        IndexSearcher searcher = new IndexSearcher(index, true);

        FirstNameStandardize stdname = new FirstNameStandardize(searcher, analyzer, hitsPerPage);

        for (String querystr : firstnames) {
            ScoreDoc[] hits = stdname.standardize("name",querystr);
            System.out.println("--> Matching \"" + querystr + "\"");
            // 4. display results
            displayResults(searcher, hits);
            System.out.println("--\n");
        }
        // searcher can only be closed when there
        // is no need to access the documents any more.
        searcher.close();

    }

    static boolean sortAccordingToCount = false;

    private static void displayResults(IndexSearcher searcher, ScoreDoc[] hits) throws IOException {
            System.out.println("Found " + hits.length + " hits.");
            for (int i = 0; i < hits.length; ++i) {
                int docId = hits[i].doc;

                Document d = searcher.doc(docId);
                System.out.println((i + 1) + ". " + d.get("name") + " | alias= " + d.get("alias") + " | score="
                        + hits[i].score + " | GRC count=" + d.get("count"));
            }
    }
}
