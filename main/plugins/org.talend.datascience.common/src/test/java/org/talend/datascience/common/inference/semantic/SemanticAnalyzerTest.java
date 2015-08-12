// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.datascience.common.inference.semantic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.net.URI;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.talend.datascience.common.inference.AnalyzerTest;

/**
 * This test is ignored for the time being because the dictionary path and key word path is hard coded, they should be
 * replaced later by elastic search server.
 * 
 * @author zhao
 *
 */
public class SemanticAnalyzerTest extends AnalyzerTest {

    private SemanticAnalyzer semanticAnalyzer;

    @Before
    public void setUp() throws Exception {
        final URI ddPath = this.getClass().getResource("/luceneIdx/dictionary").toURI();
        final URI kwPath = this.getClass().getResource("/luceneIdx/keyword").toURI();
        final CategoryRecognizerBuilder builder = CategoryRecognizerBuilder.newBuilder() //
                .ddPath(ddPath) //
                .kwPath(kwPath) //
                .setMode(Mode.LUCENE);
        semanticAnalyzer = new SemanticAnalyzer(builder);
        semanticAnalyzer.init();
    }

    @After
    public void tearDown() throws Exception {
        assertNotNull(semanticAnalyzer);
        semanticAnalyzer.end();
    }
    @Test
    public void testHandleCustomer100() {
        final List<String[]> records = getRecords(AnalyzerTest.class.getResourceAsStream("customers_100_bug_TDQ10380.csv"));
        for (String[] record : records) {
            semanticAnalyzer.analyze(record);
        }
        List<SemanticType> result = semanticAnalyzer.getResult();
        int columnIndex = 0;
        String[] expectedCategories = new String[] { //
                "", //
                "FIRSTNAME", //
                "CITY", //
                "STATE_CODE_US", //
                "DATE", //
                "CITY", //
                "DATE", //
                "", //
                "" //
        };
        for (SemanticType columnSemanticType : result) {
            assertEquals(expectedCategories[columnIndex++], columnSemanticType.getSuggestedCategory());
        }
    }

    @Test
    public void testHandle() {
        final List<String[]> records = getRecords(AnalyzerTest.class.getResourceAsStream("employee_1000.csv"));
        for (String[] record : records) {
            semanticAnalyzer.analyze(record);
        }
        List<SemanticType> result = semanticAnalyzer.getResult();
        int columnIndex = 0;
        String[] expectedCategories = new String[] { "", //
                "", //
                "FIRSTNAME", //
                "FIRSTNAME", //
                "", //
                "", //
                "", //
                "", //
                "", //
                "DATE", //
                "DATE", //
                "", //
                "", //
                "", //
                "", //
                "", //
                "GENDER", //
                "" //
        };
        for (SemanticType columnSemanticType : result) {
            assertEquals(expectedCategories[columnIndex++], columnSemanticType.getSuggestedCategory());
        }
    }

    @Ignore
    @Test
    public void testValidMailHandle() {
        final List<String[]> records = getRecords(AnalyzerTest.class.getResourceAsStream("employee_valid_email.csv"));
        for (String[] record : records) {
            semanticAnalyzer.analyze(record);
        }
        List<SemanticType> result = semanticAnalyzer.getResult();
        int columnIndex = 0;
        String[] expectedCategories = new String[] { "", //
                "", //
                "", //
                "", //
                "EMAIL", //
                "", //
                "", //
                "", //
                "", //
                "DATE", //
                "DATE", //
                "", //
                "", //
                "", //
                "", //
                "GENDER", //
                "GENDER", //
                "" //
        };
        for (SemanticType columnSemanticType : result) {
            assertEquals(expectedCategories[columnIndex++], columnSemanticType.getSuggestedCategory());
        }
    }

}
