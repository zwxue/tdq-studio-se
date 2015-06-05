package org.talend.datascience.common.inference.type;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.talend.datascience.common.inference.AnalyzerTest;
import org.talend.datascience.common.inference.type.DataType.Type;

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

    @Test
    @Ignore
    public void testIncorrectCharDetection() throws Exception {
        // One character
        analyzer.analyze("M");
        assertEquals(1, analyzer.getResult().size());
        assertEquals(DataType.Type.CHAR, analyzer.getResult().get(0).getSuggestedType());
        // Two characters
        analyzer.analyze("M");
        assertEquals(1, analyzer.getResult().size());
        assertEquals(DataType.Type.CHAR, analyzer.getResult().get(0).getSuggestedType());
        // The new value should invalidate previous assumptions about CHAR value
        // (no longer a CHAR).
        analyzer.analyze("Mme");
        assertEquals(1, analyzer.getResult().size());
        assertEquals(DataType.Type.STRING, analyzer.getResult().get(0).getSuggestedType());
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

    @Test
    public void testInvalidValues() {
        analyzer.analyze("1");
        analyzer.analyze("2");
        analyzer.analyze("3");
        analyzer.analyze("str");
        analyzer.analyze("another str");
        DataType dataType = analyzer.getResult().get(0);
        // Actual type
        dataType.setUserDefinedType(Type.STRING);
        assertEquals(Type.STRING, dataType.getUserDefinedType());
        // Suggested type
        assertEquals(Type.INTEGER, dataType.getSuggestedType());
        // Valid and invalid
        assertEquals(3, dataType.getInvalidCount());
        assertEquals(2, dataType.getValidCount());
        // Invalid values
        List<String> invalidValues = dataType.getInvalidValues();
        String[] invalidValuesArray = new String[invalidValues.size()];
        invalidValues.toArray(invalidValuesArray);
        assertArrayEquals(new String[] { "1", "2", "3" }, invalidValuesArray);
    }

    @Test
    public void testTwoColumnsInvalid() {
        analyzer.analyze("1", "");
        analyzer.analyze("2", "a");
        analyzer.analyze("3", "2.0");
        analyzer.analyze("str", "0.1");
        analyzer.analyze("another str", "");
        // --- Assert column 0
        DataType dataType = analyzer.getResult().get(0);
        // Actual type
        assertEquals(Type.INTEGER, dataType.getUserDefinedType());
        // Suggested type
        assertEquals(Type.INTEGER, dataType.getSuggestedType());
        // Valid and invalid
        assertEquals(2, dataType.getInvalidCount());
        assertEquals(3, dataType.getValidCount());
        // Invalid values
        List<String> invalidValues = dataType.getInvalidValues();
        String[] invalidValuesArray = new String[invalidValues.size()];
        invalidValues.toArray(invalidValuesArray);
        assertArrayEquals(new String[] { "str", "another str" }, invalidValuesArray);

        // ---Assert when user set string type
        // Actual type
        dataType.setUserDefinedType(Type.STRING);
        assertEquals(Type.STRING, dataType.getUserDefinedType());
        // Suggested type
        assertEquals(Type.INTEGER, dataType.getSuggestedType());
        // Valid and invalid
        assertEquals(3, dataType.getInvalidCount());
        assertEquals(2, dataType.getValidCount());
        // Invalid values
        invalidValues = dataType.getInvalidValues();
        invalidValuesArray = new String[invalidValues.size()];
        invalidValues.toArray(invalidValuesArray);
        assertArrayEquals(new String[] { "1", "2", "3" }, invalidValuesArray);

        // ---Assert column 1
        dataType = analyzer.getResult().get(1);
        // Actual type
        assertEquals(Type.DOUBLE, dataType.getUserDefinedType());
        // Suggested type
        assertEquals(Type.DOUBLE, dataType.getSuggestedType());
        // Valid , Empty, and invalid
        assertEquals(1, dataType.getInvalidCount());
        assertEquals(2, dataType.getValidCount());
        assertEquals(2, dataType.getEmptyCount());
        // Invalid values
        invalidValues = dataType.getInvalidValues();
        invalidValuesArray = new String[invalidValues.size()];
        invalidValues.toArray(invalidValuesArray);
        assertArrayEquals(new String[] { "a" }, invalidValuesArray);
        
        //--- test when user set actual type to string
        // Actual type
        dataType.setUserDefinedType(Type.STRING);
        assertEquals(Type.STRING, dataType.getUserDefinedType());
        // Suggested type
        assertEquals(Type.DOUBLE, dataType.getSuggestedType());
        // Valid , Empty, and invalid
        assertEquals(3, dataType.getInvalidCount());
        assertEquals(0, dataType.getValidCount());
        assertEquals(2, dataType.getEmptyCount());
        // Invalid values
        invalidValues = dataType.getInvalidValues();
        invalidValuesArray = new String[invalidValues.size()];
        invalidValues.toArray(invalidValuesArray);
        assertArrayEquals(new String[] { "a", "2.0", "0.1" }, invalidValuesArray);
        
    }

    @Test
    public void testValidIntegers() {
        analyzer.analyze("1");
        analyzer.analyze("2");
        analyzer.analyze("3");
        analyzer.analyze("5538297118");
        DataType dataType = analyzer.getResult().get(0);
        // Actual type will be the suggested type in first pass when it's not defined.
        assertEquals(Type.INTEGER, dataType.getUserDefinedType());
        // Suggested type
        assertEquals(Type.INTEGER, dataType.getSuggestedType());
        // Valid and invalid
        assertEquals(0, dataType.getInvalidCount());
        assertEquals(4, dataType.getValidCount());
        // Invalid values
        List<String> invalidValues = dataType.getInvalidValues();
        String[] invalidValuesArray = new String[invalidValues.size()];
        invalidValues.toArray(invalidValuesArray);
        assertArrayEquals(new String[] {}, invalidValuesArray);
    }

    @Test
    public void testNoneStrings() {
        analyzer.analyze("1.0");
        analyzer.analyze("0.02");
        analyzer.analyze("2.88888888888888888888888");
        analyzer.analyze("3");
        analyzer.analyze("5538297118");
        analyzer.analyze("str");
        DataType dataType = analyzer.getResult().get(0);
        // Actual type
        assertEquals(Type.DOUBLE, dataType.getUserDefinedType());
        // Suggested type
        assertEquals(Type.DOUBLE, dataType.getSuggestedType());
        // Test double count equals to 3
        assertEquals(3, dataType.getTypeFrequencies().get(Type.DOUBLE).longValue());
        // Valid and invalid
        assertEquals(3, dataType.getInvalidCount());
        assertEquals(3, dataType.getValidCount());
        // Invalid values
        List<String> invalidValues = dataType.getInvalidValues();
        String[] invalidValuesArray = new String[invalidValues.size()];
        invalidValues.toArray(invalidValuesArray);
        assertArrayEquals(new String[] { "3", "5538297118", "str" }, invalidValuesArray);

        // ---- second pass when user set the user defined type as tring---
        // Actual type
        dataType.setUserDefinedType(Type.STRING);
        assertEquals(Type.STRING, dataType.getUserDefinedType());
        // Suggested type
        assertEquals(Type.DOUBLE, dataType.getSuggestedType());
        // Test double count equals to 3
        assertEquals(3, dataType.getTypeFrequencies().get(Type.DOUBLE).longValue());
        // Valid and invalid
        assertEquals(5, dataType.getInvalidCount());
        assertEquals(1, dataType.getValidCount());
        // Invalid values
        invalidValues = dataType.getInvalidValues();
        invalidValuesArray = new String[invalidValues.size()];
        invalidValues.toArray(invalidValuesArray);
        assertArrayEquals(new String[] { "3", "5538297118", "1.0", "0.02", "2.88888888888888888888888" }, invalidValuesArray);

        // ---- third pass when user set the user defined type as Iteger---
        // Actual type
        dataType.setUserDefinedType(Type.INTEGER);
        assertEquals(Type.INTEGER, dataType.getUserDefinedType());
        // Suggested type
        assertEquals(Type.DOUBLE, dataType.getSuggestedType());
        // Test double count equals to 3
        assertEquals(3, dataType.getTypeFrequencies().get(Type.DOUBLE).longValue());
        // Valid and invalid
        assertEquals(4, dataType.getInvalidCount());
        assertEquals(2, dataType.getValidCount());
        // Invalid values
        invalidValues = dataType.getInvalidValues();
        invalidValuesArray = new String[invalidValues.size()];
        invalidValues.toArray(invalidValuesArray);
        assertArrayEquals(new String[] { "1.0", "0.02", "2.88888888888888888888888", "str" }, invalidValuesArray);

    }

    @Test
    public void testNumbers() {
        analyzer.analyze("1.0");
        analyzer.analyze("0.02");
        analyzer.analyze("2.88888888888888888888888");
        analyzer.analyze("3.0");
        analyzer.analyze("5538297118");
        DataType dataType = analyzer.getResult().get(0);
        // Actual type
        assertEquals(Type.DOUBLE, dataType.getUserDefinedType());
        // Suggested type
        assertEquals(Type.DOUBLE, dataType.getSuggestedType());
        // Test double count equals to 3
        assertEquals(4, dataType.getTypeFrequencies().get(Type.DOUBLE).longValue());
        // Valid and invalid
        assertEquals(1, dataType.getInvalidCount());
        assertEquals(4, dataType.getValidCount());
        // Invalid values
        List<String> invalidValues = dataType.getInvalidValues();
        String[] invalidValuesArray = new String[invalidValues.size()];
        invalidValues.toArray(invalidValuesArray);
        assertArrayEquals(new String[] { "5538297118" }, invalidValuesArray);

        // ---- send pass when user set the user defined type ---
        // Actual type
        dataType.setUserDefinedType(Type.STRING);
        assertEquals(Type.STRING, dataType.getUserDefinedType());
        // Suggested type
        assertEquals(Type.DOUBLE, dataType.getSuggestedType());
        // Test double count equals to 3
        assertEquals(4, dataType.getTypeFrequencies().get(Type.DOUBLE).longValue());
        // Valid and invalid
        assertEquals(5, dataType.getInvalidCount());
        assertEquals(0, dataType.getValidCount());
        // Invalid values
        invalidValues = dataType.getInvalidValues();
        invalidValuesArray = new String[invalidValues.size()];
        invalidValues.toArray(invalidValuesArray);
        assertArrayEquals(new String[] { "5538297118", "1.0", "0.02", "2.88888888888888888888888", "3.0" }, invalidValuesArray);
    }

    @Test
    public void testEmptyOver() {
        analyzer.analyze("");
        analyzer.analyze("");
        analyzer.analyze("");
        analyzer.analyze("");
        analyzer.analyze("1");
        analyzer.analyze("a str");
        DataType dataType = analyzer.getResult().get(0);
        // Actual type
        assertEquals(Type.STRING, dataType.getUserDefinedType());
        // Suggested type
        assertEquals(Type.STRING, dataType.getSuggestedType());
        // Valid and invalid
        assertEquals(1, dataType.getInvalidCount());
        assertEquals(1, dataType.getValidCount());
        assertEquals(4, dataType.getEmptyCount());
        // Invalid values
        List<String> invalidValues = dataType.getInvalidValues();
        String[] invalidValuesArray = new String[invalidValues.size()];
        invalidValues.toArray(invalidValuesArray);
        assertArrayEquals(new String[] { "1" }, invalidValuesArray);
    }

    @Test
    public void testEmptyAll() {
        analyzer.analyze("");
        analyzer.analyze("");
        analyzer.analyze(null);
        analyzer.analyze("");
        analyzer.analyze(" ");
        analyzer.analyze("  ");
        DataType dataType = analyzer.getResult().get(0);
        // Actual type
        assertEquals(Type.STRING, dataType.getUserDefinedType());
        // Suggested type
        assertEquals(Type.STRING, dataType.getSuggestedType());
        // Valid and invalid
        assertEquals(0, dataType.getInvalidCount());
        assertEquals(0, dataType.getValidCount());
        assertEquals(5, dataType.getEmptyCount());
        // Invalid values
        List<String> invalidValues = dataType.getInvalidValues();
        String[] invalidValuesArray = new String[invalidValues.size()];
        invalidValues.toArray(invalidValuesArray);
        assertArrayEquals(new String[] {}, invalidValuesArray);
    }

}
