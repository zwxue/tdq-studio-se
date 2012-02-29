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
package org.talend.dataquality.standardization.query.test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.talend.dataquality.standardization.query.FirstNameStandardize;

/**
 * DOC sizhaoliu class global comment. Detailled comment
 */
public class FirstNameStandardizeTest {

    private static final String indexfolder = "./data/TalendGivenNames_index"; // $NON-NLS-1$

    private static IndexSearcher searcher = null;

    private static Analyzer searchAnalyzer = null;

    private static FirstNameStandardize fnameStandardize = null;

    private static final String inputName = "Michel"; //$NON-NLS-1$

    private static final String[][] expected = { { "Michel", "AUS", "MITCHEL", "MICHELE", "MITCHEL" },
            { "Michel", "BEL", "MICHEL", "MICHEL", "MICHEL" }, { "Michel", "DEU", "MICHEL", "MICHEL", "MICHEL" },
            { "Michel", "ESP", "MICHEL", "ADELAIDA", "MICHEL" }, { "Michel", "FRA", "MICHEL", "MICHELE", "MICHEL" },
            { "Michel", "ITA", "MICHELA", "MICHELA", "MICHELE" }, { "Michel", "RUS", "MICHEL", "MICHEL", "MICHEL" },
            { "Michel", "USA", "MICHEL", "MICHELL", "MICHEL" },

            { "Adrian", "AUS", "ADRIAN", "ADRIAN", "ADRIAN" }, { "Adrian", "BEL", "ADRIAN", "ADRIAN", "ADRIAN" },
            { "Adrian", "DEU", "MARIAN", "MARIAN", "MARIAN" }, { "Adrian", "ESP", "ADRIAN", "MIRIAN", "ADRIAN" },
            { "Adrian", "FRA", "ADRIAN", "ADRIAN", "ADRIAN" }, { "Adrian", "ITA", "ADRIANO", "ADRIANA", "ADRIANO" },
            { "Adrian", "RUS", "BRIAN", "LARISSA", "BRIAN" }, { "Adrian", "USA", "ADRIAN", "ADRIANE", "ADRIAN" }, };


    /**
     * DOC sizhaoliu Comment method "setUpBeforeClass".
     * 
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        Directory dir = FSDirectory.open(new File(indexfolder));
        searcher = new IndexSearcher(dir);
        searchAnalyzer = new SimpleAnalyzer();
        fnameStandardize = new FirstNameStandardize(searcher, searchAnalyzer, 10);
    }

    @AfterClass
    public static void tearDown() throws Exception {
        searcher.close();
    }

    /**
     * Test method for
     * {@link org.talend.dataquality.standardization.query.FirstNameStandardize#replaceName(java.lang.String, boolean)}.
     */
    @Test
    public void testReplaceName() {
        try {
            String res = fnameStandardize.replaceName(inputName, true);
            System.out.println("testReplaceName:\n" + res);
            assertEquals("MICHEL", res);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * Test method for
     * {@link org.talend.dataquality.standardization.query.FirstNameStandardize#replaceNameWithCountryGenderInfo(java.lang.String, java.lang.String, java.lang.String, boolean)}
     * .
     */
    @Test
    public void testReplaceNameWithCountryGenderInfo() {
        try {

            System.out.println("\ntestReplaceNameWithCountryGenderInfo:");
            for (String[] testCase : expected) {
                String res, resF, resM = "";
                System.out.print("{\"" + testCase[0] + "\", \"" + testCase[1] + "\", \"");

                // results for query without gender info
                res = fnameStandardize.replaceNameWithCountryInfo(testCase[0], testCase[1], true);
                System.out.print(res + "\", \"");
                assertEquals(testCase[2], res);

                // results for female first name query
                resF = fnameStandardize.replaceNameWithCountryGenderInfo(testCase[0], testCase[1], "F", true);
                System.out.print(resF + "\", \"");
                assertEquals(testCase[3], resF);

                // results for female first name query
                resM = fnameStandardize.replaceNameWithCountryGenderInfo(testCase[0], testCase[1], "M", true);
                System.out.println(resM + "\"},");
                assertEquals(testCase[4], resM);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
