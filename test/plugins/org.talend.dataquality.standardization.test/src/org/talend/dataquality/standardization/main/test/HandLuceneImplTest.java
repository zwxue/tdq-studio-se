package org.talend.dataquality.standardization.main.test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.lucene.queryParser.ParseException;
import org.talend.core.utils.PluginUtil;
import org.talend.dataquality.standardization.main.HandleLucene;
import org.talend.dataquality.standardization.main.HandleLuceneImpl;

public class HandLuceneImplTest extends TestCase {

    public final static String PLUGIN_ID = "org.talend.dataquality.standardization.test"; // $NON-NLS-1$

    public final static String filename = PluginUtil.getPluginInstallPath(PLUGIN_ID).concat("data/TalendGivenNames.TXT"); // $NON-NLS-1$

    public final static String indexfolder = PluginUtil.getPluginInstallPath(PLUGIN_ID).concat("data/TalendGivenNames_index"); // $NON-NLS-1$

    private HandleLucene hl;

    public HandLuceneImplTest() {
        hl = new HandleLuceneImpl();
    }

    public void testCreateIndex() {
        boolean back = hl.createIndex(filename, indexfolder);
        assertTrue("Creating Index :", back); // $NON-NLS-1$
    }

    public void testGetSearchResultStringStringMapOfStringStringBoolean() {
        Map<String, String> information2value = new HashMap<String, String>();
        information2value.put("gender", "0"); // $NON-NLS-1$ // $NON-NLS-2$
        Map<String, String[]> hits = null;
        try {
            hits = hl.getSearchResult(indexfolder, "Edou", information2value, false); // $NON-NLS-1$
            String[] soreDocs = hits.get("Edou"); // $NON-NLS-1$
            assertNotNull(soreDocs);
            if (soreDocs != null) {
                for (String doc : soreDocs) {
                    System.out.println(doc);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

}
