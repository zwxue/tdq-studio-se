package org.talend.datascience.common.inference.type;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.talend.datascience.common.inference.AnalyzerTest;

public class DataTypeAnalyzerTest extends AnalyzerTest {

    private DataTypeAnalyzer analyzer;

    @Before
    public void setUp() {
        analyzer = new DataTypeAnalyzer();
        analyzer.init();
    }

    @After
    public void tearDown() {
        analyzer.end();
    }

    @Test
    public void testEmptyRecords() throws Exception {
        analyzer.analyze();
        assertEquals(0, analyzer.getResult().size());
        analyzer.analyze(null);
        assertEquals(0, analyzer.getResult().size());
        analyzer.analyze("");
        assertEquals(1, analyzer.getResult().size());
        assertEquals(DataType.Type.STRING, analyzer.getResult().get(0).getSuggestedType());
    }

    @Test
    public void testAnalysisResize() throws Exception {
        analyzer.analyze("aaaa");
        assertEquals(1, analyzer.getResult().size());
        analyzer.analyze("aaaa", "bbbb");
        assertEquals(2, analyzer.getResult().size());
    }

    @Test
    public void testString() throws Exception {
        // One string
        analyzer.analyze("aaaa");
        assertEquals(1, analyzer.getResult().size());
        assertEquals(DataType.Type.STRING, analyzer.getResult().get(0).getSuggestedType());
        // Two strings
        analyzer.analyze("bbbb");
        assertEquals(1, analyzer.getResult().size());
        assertEquals(DataType.Type.STRING, analyzer.getResult().get(0).getSuggestedType());
        // One integer
        analyzer.analyze("2");
        assertEquals(1, analyzer.getResult().size());
        assertEquals(DataType.Type.STRING, analyzer.getResult().get(0).getSuggestedType());
    }

    @Test
    public void testInteger() throws Exception {
        // One integer
        analyzer.analyze("0");
        assertEquals(1, analyzer.getResult().size());
        assertEquals(DataType.Type.INTEGER, analyzer.getResult().get(0).getSuggestedType());
        // Two integers
        analyzer.analyze("1");
        assertEquals(1, analyzer.getResult().size());
        assertEquals(DataType.Type.INTEGER, analyzer.getResult().get(0).getSuggestedType());
        // One string
        analyzer.analyze("aaaaa");
        assertEquals(1, analyzer.getResult().size());
        assertEquals(DataType.Type.INTEGER, analyzer.getResult().get(0).getSuggestedType());
    }

    // TODO All other data types

    @Test
    public void testBoolean() throws Exception {
        // One boolean
        analyzer.analyze("true");
        assertEquals(1, analyzer.getResult().size());
        assertEquals(DataType.Type.BOOLEAN, analyzer.getResult().get(0).getSuggestedType());
        // Two booleans
        analyzer.analyze("false");
        assertEquals(1, analyzer.getResult().size());
        assertEquals(DataType.Type.BOOLEAN, analyzer.getResult().get(0).getSuggestedType());
        // One string
        analyzer.analyze("aaaaa");
        assertEquals(1, analyzer.getResult().size());
        assertEquals(DataType.Type.BOOLEAN, analyzer.getResult().get(0).getSuggestedType());
    }

    @Test
    public void testMultipleColumns() throws Exception {
        analyzer.analyze("true", "aaaa");
        analyzer.analyze("true", "bbbb");
        assertEquals(2, analyzer.getResult().size());
        assertEquals(DataType.Type.BOOLEAN, analyzer.getResult().get(0).getSuggestedType());
        assertEquals(DataType.Type.STRING, analyzer.getResult().get(1).getSuggestedType());
    }

    @Test
    public void testInferDataTypes() {
        final List<String[]> records = getRecords(AnalyzerTest.class.getResourceAsStream("employee_100.csv"));
        for (String[] record : records) {
            analyzer.analyze(record);
        }
        final List<DataType> result = analyzer.getResult();
        assertEquals(18, result.size());
        assertEquals(DataType.Type.INTEGER, result.get(0).getSuggestedType());
        assertEquals(DataType.Type.STRING, result.get(1).getSuggestedType());
        assertEquals(DataType.Type.STRING, result.get(2).getSuggestedType());
        assertEquals(DataType.Type.STRING, result.get(3).getSuggestedType());
        assertEquals(DataType.Type.STRING, result.get(4).getSuggestedType());
        assertEquals(DataType.Type.INTEGER, result.get(5).getSuggestedType());
        assertEquals(DataType.Type.STRING, result.get(6).getSuggestedType());
        assertEquals(DataType.Type.INTEGER, result.get(7).getSuggestedType());
        assertEquals(DataType.Type.INTEGER, result.get(8).getSuggestedType());
        assertEquals(DataType.Type.DATE, result.get(9).getSuggestedType());
        assertEquals(DataType.Type.DATE, result.get(10).getSuggestedType());
        assertEquals(DataType.Type.STRING, result.get(11).getSuggestedType());
        assertEquals(DataType.Type.DOUBLE, result.get(12).getSuggestedType());
        assertEquals(DataType.Type.INTEGER, result.get(13).getSuggestedType());
        assertEquals(DataType.Type.STRING, result.get(14).getSuggestedType());
        assertEquals(DataType.Type.CHAR, result.get(15).getSuggestedType());
        assertEquals(DataType.Type.CHAR, result.get(16).getSuggestedType());
        assertEquals(DataType.Type.STRING, result.get(17).getSuggestedType());
    }

    /**
     * Test the order of column index to see whether it is same after the inferring type done comparing the before or
     * not.
     */
    @Test
    public void testInferTypesColumnIndexOrder() {
        final List<String[]> records = getRecords(AnalyzerTest.class.getResourceAsStream("customers_100_bug_TDQ10380.csv"));
        for (String[] record : records) {
            analyzer.analyze(record);
        }
        final List<DataType> result = analyzer.getResult();
        assertEquals(DataType.Type.INTEGER, result.get(7).getSuggestedType());
    }

}
