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
package org.talend.dataquality.standardization.record;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.List;

import junit.framework.Assert;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.queryParser.ParseException;
import org.junit.Before;
import org.junit.Test;
import org.talend.dataquality.standardization.index.SynonymIndexBuilder;
import org.talend.dataquality.standardization.index.SynonymIndexBuilderTest;
import org.talend.dataquality.standardization.index.SynonymIndexSearcher;


/**
 * DOC scorreia  class global comment. Detailled comment
 */
public class SynonymRecordSearcherTest {

    /**
     * DOC scorreia Comment method "setUp".
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }

    /**
     * Test method for
     * {@link org.talend.dataquality.standardization.record.SynonymRecordSearcher#search(int, java.lang.String[], int[])}
     * .
     * 
     * @throws IOException
     */
    @Test
    public void testSearch() {
        // create two indexes

        // index 1
        String path1 = "data/idx1";
        SynonymIndexBuilder builder1 = new SynonymIndexBuilder();
        builder1.deleteIndexFromFS(path1);
        builder1.initIndexInFS(path1);
        for (int i = 0; i < SynonymIndexBuilderTest.synonyms.length; i++) {
            String[] synonyms = SynonymIndexBuilderTest.synonyms[i];
            try {
                builder1.insertDocument(synonyms[0], synonyms[1]);
            } catch (IOException e) {
                e.printStackTrace();
                fail("should not get an exception here");
            }
        }
        builder1.closeIndex();

        // index 2
        String path2 = "data/idx2";
        SynonymIndexBuilder builder2 = new SynonymIndexBuilder();
        builder2.deleteIndexFromFS(path2);
        builder2.initIndexInFS(path2);
        for (int i = 0; i < SynonymIndexBuilderTest.synonyms.length; i++) {
            String[] synonyms = SynonymIndexBuilderTest.synonyms[i];
            try {
                builder2.insertDocument(synonyms[0], synonyms[1]);
            } catch (IOException e) {
                e.printStackTrace();
                fail("should not get an exception here");
            }
        }
        builder2.closeIndex();

        // search in the two indexes
        SynonymIndexSearcher searcher1 = new SynonymIndexSearcher(path1);
        searcher1.setTopDocLimit(3);
        SynonymIndexSearcher searcher2 = new SynonymIndexSearcher(path2);
        searcher2.setTopDocLimit(3);
        SynonymRecordSearcher recSearcher = new SynonymRecordSearcher(2);
        recSearcher.addSearcher(searcher1, 0);
        recSearcher.addSearcher(searcher2, 1);

        String[] record = { "IBM", "ANpe" };
        try {
            List<OutputRecord> results = recSearcher.search(15, record);
            Assert.assertNotNull(results);
            Assert.assertFalse(results.isEmpty());
            for (OutputRecord outputRecord : results) {
                Assert.assertNotNull(outputRecord);
                String[] resultingRecord = outputRecord.getRecord();
                Assert.assertEquals(record.length, resultingRecord.length);

                System.out.println(StringUtils.join(resultingRecord, '|'));
            }
        } catch (ParseException e) {
            e.printStackTrace();
            fail("should not get an exception here");
        } catch (IOException e) {
            e.printStackTrace();
            fail("should not get an exception here");
        }
        try {
            searcher1.close();
            searcher2.close();
        } catch (Exception e) {
            e.printStackTrace();
            fail("should not get an exception here");
        }
    }

    /**
     * Test method for
     * {@link org.talend.dataquality.standardization.record.SynonymRecordSearcher#addSearcher(org.talend.dataquality.standardization.index.SynonymIndexSearcher, int)}
     * .
     */
    @Test
    public void testAddSearcher() {
        SynonymRecordSearcher recSearcher = new SynonymRecordSearcher(2);
        try {
            recSearcher.addSearcher(null, 0);
        } catch (Exception e) {
            Assert.assertNotNull("we should get an exception here", e);
        }
        try {
            recSearcher.addSearcher(new SynonymIndexSearcher(), 2);
        } catch (Exception e) {
            Assert.assertNotNull("we should get an exception here", e);
        }

        try {
            SynonymIndexSearcher searcher = new SynonymIndexSearcher();
            recSearcher.addSearcher(searcher, 1);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        try {
            SynonymIndexSearcher searcher = new SynonymIndexSearcher();
            recSearcher.addSearcher(searcher, -1);
        } catch (Exception e) {
            Assert.assertNotNull("we should get an exception here", e);
        }
    }

}
