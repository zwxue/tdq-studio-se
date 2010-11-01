package org.talend.dataquality.standardization.main.test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.queryParser.ParseException;
import org.talend.dataquality.standardization.main.HandleLucene;
import org.talend.dataquality.standardization.main.HandleLuceneImpl;

import junit.framework.TestCase;

public class HandLuceneImplTest extends TestCase {

    private String filename = "./data/TalendGivenNames.TXT";

    private String indexfolder = "./data/TalendGivenNames_index";

    private HandleLucene hl;

    public HandLuceneImplTest() {
        hl = new HandleLuceneImpl();
    }

    public void testCreateIndex() {

        boolean back = hl.createIndex(filename, indexfolder);
        assertTrue("Creating Index :", back);

    }

    public void testGetSearchResultStringStringMapOfStringStringBoolean() {
        Map<String, String> information2value = new HashMap<String, String>();
        information2value.put("gender", "0");
        Map<String, String[]> hits = null;
        try {
            hits = hl.getSearchResult(indexfolder, "Edou", information2value, false);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String[] soreDocs = hits.get("Edou");
        assertNotNull(soreDocs);
        if (soreDocs != null) {
            for (String doc : soreDocs) {
                System.out.println(doc);
            }
        }

    }

}
