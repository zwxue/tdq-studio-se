package org.talend.datascience.common.inference.type;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.talend.datascience.common.inference.AnalyzerTest;
import org.talend.datascience.common.inference.quality.ValueQuality;
import org.talend.datascience.common.inference.quality.ValueQualityAnalyzer;
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
        ValueQualityAnalyzer qualityAnalyzer = new ValueQualityAnalyzer(Type.STRING);
        qualityAnalyzer.analyze("1");
        qualityAnalyzer.analyze("2");
        qualityAnalyzer.analyze("3");
        qualityAnalyzer.analyze("str");
        qualityAnalyzer.analyze("another str");
        // Valid and invalid
        assertEquals(0, qualityAnalyzer.getResult().get(0).getInvalidCount());
        assertEquals(5, qualityAnalyzer.getResult().get(0).getValidCount());
        // Invalid values
        List<String> invalidValues = qualityAnalyzer.getResult().get(0).getInvalidValues();
        String[] invalidValuesArray = new String[invalidValues.size()];
        invalidValues.toArray(invalidValuesArray);
        assertArrayEquals(new String[] {}, invalidValuesArray);
    }

    @Test
    public void testTwoColumnsInvalid() {
        ValueQualityAnalyzer qualityAnalyzer = new ValueQualityAnalyzer(Type.INTEGER, Type.DOUBLE);
        populateAnalyzeData(qualityAnalyzer);
        // --- Assert column 0
        ValueQuality valueQuality = qualityAnalyzer.getResult().get(0);
        // Valid and invalid
        assertEquals(2, valueQuality.getInvalidCount());
        assertEquals(3, valueQuality.getValidCount());
        // Invalid values
        List<String> invalidValues = valueQuality.getInvalidValues();
        String[] invalidValuesArray = new String[invalidValues.size()];
        invalidValues.toArray(invalidValuesArray);
        assertArrayEquals(new String[] { "str", "another str" }, invalidValuesArray);
        // ---Assert column 1
        valueQuality = qualityAnalyzer.getResult().get(1);
        // Valid , Empty, and invalid
        assertEquals(1, valueQuality.getInvalidCount());
        assertEquals(2, valueQuality.getValidCount());
        assertEquals(2, valueQuality.getEmptyCount());
        // Invalid values
        invalidValues = valueQuality.getInvalidValues();
        invalidValuesArray = new String[invalidValues.size()];
        invalidValues.toArray(invalidValuesArray);
        assertArrayEquals(new String[] { "a" }, invalidValuesArray);

        // ---Assert when user set string type
        qualityAnalyzer = new ValueQualityAnalyzer(Type.STRING, Type.STRING);
        populateAnalyzeData(qualityAnalyzer);
        valueQuality = qualityAnalyzer.getResult().get(0);
        // Valid and invalid
        assertEquals(0, valueQuality.getInvalidCount());
        assertEquals(5, valueQuality.getValidCount());
        // Invalid values
        invalidValues = valueQuality.getInvalidValues();
        invalidValuesArray = new String[invalidValues.size()];
        invalidValues.toArray(invalidValuesArray);
        assertArrayEquals(new String[] {}, invalidValuesArray);

        // --- test when user set actual type to string
        valueQuality = qualityAnalyzer.getResult().get(1);
        // Valid , Empty, and invalid
        assertEquals(0, valueQuality.getInvalidCount());
        assertEquals(3, valueQuality.getValidCount());
        assertEquals(2, valueQuality.getEmptyCount());
        // Invalid values
        invalidValues = valueQuality.getInvalidValues();
        invalidValuesArray = new String[invalidValues.size()];
        invalidValues.toArray(invalidValuesArray);
        assertArrayEquals(new String[] {}, invalidValuesArray);

    }

    private void populateAnalyzeData(ValueQualityAnalyzer qualityAnalyzer) {
        qualityAnalyzer.analyze("1", "");
        qualityAnalyzer.analyze("2", "a");
        qualityAnalyzer.analyze("3", "2.0");
        qualityAnalyzer.analyze("str", "0.1");
        qualityAnalyzer.analyze("another str", "");
    }

    @Test
    public void testValidIntegers() {
        ValueQualityAnalyzer qualityAnalyzer = new ValueQualityAnalyzer(Type.INTEGER);
        qualityAnalyzer.analyze("1");
        qualityAnalyzer.analyze("2");
        qualityAnalyzer.analyze("3");
        qualityAnalyzer.analyze("5538297118");

        // Valid and invalid
        ValueQuality valueQuality =  qualityAnalyzer.getResult().get(0);
        assertEquals(0, valueQuality.getInvalidCount());
        assertEquals(4, valueQuality.getValidCount());
        // Invalid values
        List<String> invalidValues = valueQuality.getInvalidValues();
        String[] invalidValuesArray = new String[invalidValues.size()];
        invalidValues.toArray(invalidValuesArray);
        assertArrayEquals(new String[] {}, invalidValuesArray);
    }

    @Test
    public void testNoneStrings() {
        ValueQualityAnalyzer qualityAnalyzer = new ValueQualityAnalyzer(Type.DOUBLE);
        populateAnalyzerNoneString(qualityAnalyzer);
        ValueQuality valueQuality = qualityAnalyzer.getResult().get(0);
        // Valid and invalid
        assertEquals(1, valueQuality.getInvalidCount());
        assertEquals(5, valueQuality.getValidCount());
        // Invalid values
        List<String> invalidValues = valueQuality.getInvalidValues();
        String[] invalidValuesArray = new String[invalidValues.size()];
        invalidValues.toArray(invalidValuesArray);
        assertArrayEquals(new String[] {  "str" }, invalidValuesArray);

        // ---- second pass when user set the user defined type as tring---
        // Actual type
        qualityAnalyzer = new ValueQualityAnalyzer(Type.STRING);
        populateAnalyzerNoneString(qualityAnalyzer);
        valueQuality = qualityAnalyzer.getResult().get(0);
        // Valid and invalid
        assertEquals(0, valueQuality.getInvalidCount());
        assertEquals(6, valueQuality.getValidCount());
        // Invalid values
        invalidValues = valueQuality.getInvalidValues();
        invalidValuesArray = new String[invalidValues.size()];
        invalidValues.toArray(invalidValuesArray);
        assertArrayEquals(new String[] {}, invalidValuesArray);

        // ---- third pass when user set the user defined type as Iteger---
        // Actual type
        qualityAnalyzer = new ValueQualityAnalyzer(Type.INTEGER);
        populateAnalyzerNoneString(qualityAnalyzer);
        valueQuality = qualityAnalyzer.getResult().get(0);
        // Valid and invalid
        assertEquals(4, valueQuality.getInvalidCount());
        assertEquals(2, valueQuality.getValidCount());
        // Invalid values
        invalidValues = valueQuality.getInvalidValues();
        invalidValuesArray = new String[invalidValues.size()];
        invalidValues.toArray(invalidValuesArray);
        assertArrayEquals(new String[] { "1.0", "0.02", "2.88888888888888888888888", "str" }, invalidValuesArray);

    }

    private void populateAnalyzerNoneString(ValueQualityAnalyzer qualityAnalyzer) {
        qualityAnalyzer.analyze("1.0");
        qualityAnalyzer.analyze("0.02");
        qualityAnalyzer.analyze("2.88888888888888888888888");
        qualityAnalyzer.analyze("3");
        qualityAnalyzer.analyze("5538297118");
        qualityAnalyzer.analyze("str");
    }

    @Test
    public void testNumbers() {
        ValueQualityAnalyzer qualityAnalyzer = new ValueQualityAnalyzer(Type.DOUBLE);
        populateAnalyzerWithNumers(qualityAnalyzer);
        ValueQuality valueQuality = qualityAnalyzer.getResult().get(0);
        // Valid and invalid
        assertEquals(0, valueQuality.getInvalidCount());
        assertEquals(5, valueQuality.getValidCount());
        // Invalid values
        List<String> invalidValues = valueQuality.getInvalidValues();
        String[] invalidValuesArray = new String[invalidValues.size()];
        invalidValues.toArray(invalidValuesArray);
        assertArrayEquals(new String[] {}, invalidValuesArray);

        // ---- send pass when user set the user defined type ---
        // Actual type
        qualityAnalyzer = new ValueQualityAnalyzer(Type.STRING);
        populateAnalyzerWithNumers(qualityAnalyzer);
        valueQuality = qualityAnalyzer.getResult().get(0);
        // Valid and invalid
        assertEquals(0, valueQuality.getInvalidCount());
        assertEquals(5, valueQuality.getValidCount());
        // Invalid values
        invalidValues = valueQuality.getInvalidValues();
        invalidValuesArray = new String[invalidValues.size()];
        invalidValues.toArray(invalidValuesArray);
        assertArrayEquals(new String[] {}, invalidValuesArray);
    }

    private void populateAnalyzerWithNumers(ValueQualityAnalyzer qualityAnalyzer) {
        qualityAnalyzer.analyze("1.0");
        qualityAnalyzer.analyze("0.02");
        qualityAnalyzer.analyze("2.88888888888888888888888");
        qualityAnalyzer.analyze("3.0");
        qualityAnalyzer.analyze("5538297118");
    }

    @Test
    public void testEmptyOver() {
        ValueQualityAnalyzer qualityAnalyzer = new ValueQualityAnalyzer(Type.STRING);
        qualityAnalyzer.analyze("");
        qualityAnalyzer.analyze("");
        qualityAnalyzer.analyze("");
        qualityAnalyzer.analyze("");
        qualityAnalyzer.analyze("1");
        qualityAnalyzer.analyze("a str");
        ValueQuality valueQuality = qualityAnalyzer.getResult().get(0);
        // Valid and invalid
        assertEquals(0, valueQuality.getInvalidCount());
        assertEquals(2, valueQuality.getValidCount());
        assertEquals(4, valueQuality.getEmptyCount());
        // Invalid values
        List<String> invalidValues = valueQuality.getInvalidValues();
        String[] invalidValuesArray = new String[invalidValues.size()];
        invalidValues.toArray(invalidValuesArray);
        assertArrayEquals(new String[] {  }, invalidValuesArray);
    }

    @Test
    public void testEmptyAll() {
        ValueQualityAnalyzer qualityAnalyzer = new ValueQualityAnalyzer(Type.STRING);
        qualityAnalyzer.analyze("");
        qualityAnalyzer.analyze("");
        qualityAnalyzer.analyze(null);
        qualityAnalyzer.analyze("");
        qualityAnalyzer.analyze(" ");
        qualityAnalyzer.analyze("  ");
        ValueQuality valueQuality = qualityAnalyzer.getResult().get(0);
        // Valid and invalid
        assertEquals(0, valueQuality.getInvalidCount());
        assertEquals(0, valueQuality.getValidCount());
        assertEquals(6, valueQuality.getEmptyCount());
        // Invalid values
        List<String> invalidValues = valueQuality.getInvalidValues();
        String[] invalidValuesArray = new String[invalidValues.size()];
        invalidValues.toArray(invalidValuesArray);
        assertArrayEquals(new String[] {}, invalidValuesArray);
    }

}
