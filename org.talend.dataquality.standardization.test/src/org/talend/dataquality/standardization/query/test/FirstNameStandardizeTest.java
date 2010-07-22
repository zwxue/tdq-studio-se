package org.talend.dataquality.standardization.query.test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.talend.dataquality.standardization.query.FirstNameStandardize;

public class FirstNameStandardizeTest extends TestCase {

    private String filename = "./data/TalendGivenNames.TXT";

    private String indexfolder = "./data/TalendGivenNames_index";

    public void testStandardizeStringString() throws IOException, ParseException {
        Directory dir = FSDirectory.open(new File(indexfolder));
        IndexSearcher searcher = new IndexSearcher(dir);
        Analyzer searchAnalyzer = new SimpleAnalyzer();

        FirstNameStandardize stdname = new FirstNameStandardize(searcher, searchAnalyzer, 10);
        Map<String, String> information2value = new HashMap<String, String>();
        information2value.put("country", "mosikou");
        information2value.put("gender", "0");
        ScoreDoc[] docs = stdname.standardize("Edou", information2value, false);
        ArrayList<String> soreDoc = new ArrayList<String>();
        
        for (int i = 0; i < docs.length; ++i) {
            int docId = docs[i].doc;
            Document d = null;
            try {
                d = searcher.doc(docId);
                String name = d.get("name");
                soreDoc.add(name);
                System.out.println(name);
            } catch (CorruptIndexException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        String[] resultArray = new String[soreDoc.size()];
        Map<String, String[]> hits = new HashMap<String, String[]>();
        hits.put("Edou", soreDoc.toArray(resultArray));
        searcher.close();
    }

}
