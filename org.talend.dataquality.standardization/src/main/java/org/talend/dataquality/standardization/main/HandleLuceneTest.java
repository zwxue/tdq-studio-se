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
package org.talend.dataquality.standardization.main;

import java.io.IOException;

import org.apache.lucene.queryParser.ParseException;

/**
 * DOC klliu class global comment.
 * 
 * TODO transform this class to a junit class and move it to the appropriate test project.
 */
public final class HandleLuceneTest {

    private HandleLuceneTest() {
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        String filename = "./data/TalendGivenNames.TXT";//$NON-NLS-1$
        // String indexfolder = "C:\\Documents and Settings\\Administrator\\����\\data\\TalendGivenNames_index";
        String indexfolder = "./data/TalendGivenNames_index";//$NON-NLS-1$
        HandleLucene hl = new HandleLuceneImpl();
        System.out.print(hl.createIndex(filename, indexfolder));

        try {

            String res = hl.replaceName(indexfolder, "Philippe", false);//$NON-NLS-1$
            System.out.println("replaceName:" + res);//$NON-NLS-1$
            try {
                String res1 = hl.replaceNameWithCountryInfo(indexfolder, "Philippe", "china", false);//$NON-NLS-1$ $NON-NLS-2$
                System.out.println("replaceNameWithCountryInfo:" + res1);//$NON-NLS-1$

                String res2 = hl.replaceNameWithGenderInfo(indexfolder, "Philippe", "0", false);//$NON-NLS-1$ $NON-NLS-2$

                System.out.println("replaceNameWithGenderInfo:" + res2);//$NON-NLS-1$
                String res3 = hl.replaceNameWithCountryGenderInfo(indexfolder, "Philippe", "china", "1", false);//$NON-NLS-1$ $NON-NLS-2$ $NON-NLS-3$
                System.out.println("replaceNameWithCountryGenderInfo:" + res3);//$NON-NLS-1$
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
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
