package org.talend.datascience.common.inference.quality;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.talend.datascience.common.inference.type.DataType;

public class ValueQualityAnalyzerTest {

    private ValueQualityAnalyzer analyzer;

    @Before
    public void setUp() {
        analyzer = new ValueQualityAnalyzer(DataType.Type.INTEGER);
        analyzer.init();
    }

    @After
    public void tearDown() {
        analyzer.end();
    }

    @Test
    public void testEmpty() throws Exception {
        // One empty record
        analyzer.analyze("");
        assertEquals(1, analyzer.getResult().size());
        assertEquals(1, analyzer.getResult().get(0).getEmptyCount());
        assertEquals(0, analyzer.getResult().get(0).getInvalidCount());
        assertEquals(0, analyzer.getResult().get(0).getValidCount());
        // One new empty record
        analyzer.analyze("");
        assertEquals(2, analyzer.getResult().get(0).getEmptyCount());
        assertEquals(0, analyzer.getResult().get(0).getInvalidCount());
        assertEquals(0, analyzer.getResult().get(0).getValidCount());
    }

    @Test
    public void testValid() throws Exception {
        // One valid record (type is integer)
        analyzer.analyze("1");
        assertEquals(1, analyzer.getResult().size());
        assertEquals(0, analyzer.getResult().get(0).getEmptyCount());
        assertEquals(0, analyzer.getResult().get(0).getInvalidCount());
        assertEquals(1, analyzer.getResult().get(0).getValidCount());
        // One new valid record (type is still integer)
        analyzer.analyze("2");
        assertEquals(0, analyzer.getResult().get(0).getEmptyCount());
        assertEquals(0, analyzer.getResult().get(0).getInvalidCount());
        assertEquals(2, analyzer.getResult().get(0).getValidCount());
    }

    @Test
    public void testInvalid() throws Exception {
        // One invalid record (type is integer)
        analyzer.analyze("aaaa");
        assertEquals(1, analyzer.getResult().size());
        assertEquals(0, analyzer.getResult().get(0).getEmptyCount());
        assertEquals(1, analyzer.getResult().get(0).getInvalidCount());
        assertEquals(0, analyzer.getResult().get(0).getValidCount());
        // One new invalid record (type is integer)
        analyzer.analyze("bbbb");
        assertEquals(0, analyzer.getResult().get(0).getEmptyCount());
        assertEquals(2, analyzer.getResult().get(0).getInvalidCount());
        assertEquals(0, analyzer.getResult().get(0).getValidCount());
    }

}
