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
 * This class creates the lucene index file to be used in the component.
 */
public final class HandleLuceneTest {

    private HandleLuceneTest() {
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        // choose here to test the replace methods
        boolean testReplace = false;
        // choose here the appropriate input file.
        String filename = "./data/TalendGivenNames.TXT";//$NON-NLS-1$
        String indexfolder = "./data/TalendGivenNames_index";//$NON-NLS-1$
        HandleLucene hl = new HandleLuceneImpl();
        System.out.print(hl.createIndex(filename, indexfolder));

        if (!testReplace) {
            return;
        }
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
                e.printStackTrace();
            }

            // }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

}
