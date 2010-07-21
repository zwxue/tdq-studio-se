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
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.queryParser.ParseException;
import org.talend.dataquality.standardization.constant.PluginConstant;

/**
 * DOC klliu class global comment.
 */
public class HandleLuceneTest {

    public HandleLuceneTest() {
        // TODO Auto-generated constructor stub
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        String filename = "./data/TalendGivenNames.TXT";
        // String indexfolder = "C:\\Documents and Settings\\Administrator\\×ÀÃæ\\data\\TalendGivenNames_index";
        String indexfolder = "./data/TalendGivenNames_index";
        // String[] firstnames = new String[] { "jeants", "rÃ©my", "jean-philippe",
        // "sebastiao", "r*my*" };
        HandleLucene hl = new HandleLuceneImpl();
     //   System.out.print(hl.createIndex(filename, indexfolder));

        try {
            // Map<String, String[]> hits = hl.getSearchResult(indexfolder,
            // PluginConstant.FIRST_NAME_STANDARDIZE_NAME, firstnames);
            Map<String, String> information2value = new HashMap<String, String>();
           // information2value.put("country", "mosikou");
            information2value.put("gender", "0");
            Map<String, String[]> hits = hl.getSearchResult(indexfolder, "Edou", information2value, false);
         //  for (String firstName : firstnames) {
            String[] soreDocs = hits.get("Edou");
            if (soreDocs!=null) {
                for (String doc : soreDocs) {
                    System.out.println(doc);
                }
            }

            // }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
