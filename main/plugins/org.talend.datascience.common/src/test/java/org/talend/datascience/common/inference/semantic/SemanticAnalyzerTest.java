package org.talend.datascience.common.inference.semantic;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.talend.dataquality.semantic.recognizer.CategoryRecognizerBuilder.Mode;
import org.talend.datascience.common.inference.AnalyzerTest;

/**
 * This test is ignored for the time being because the dictionary path and key word path is hard coded, they should be
 * replaced later by elastic search server.
 * 
 * @author zhao
 *
 */
@Ignore
public class SemanticAnalyzerTest extends AnalyzerTest {

    public static final String ddPath = "/home/zhao/Talend/codebase/GIT/tdq-siq/org.talend.dataquality.semantic/luceneIdx/dictionary";

    public static final String kwPath = "/home/zhao/Talend/codebase/GIT/tdq-siq/org.talend.dataquality.semantic/luceneIdx/keyword";

    SemanticAnalyzer semanticAnalyzer = null;

    @Before
    public void setUp() throws Exception {
        semanticAnalyzer = new SemanticAnalyzer();
        semanticAnalyzer.setDdPath(ddPath);
        semanticAnalyzer.setKwPath(kwPath);
        semanticAnalyzer.setSemanticRecognizerMode(Mode.LUCENE);
    }

    @After
    public void tearDown() throws Exception {
        semanticAnalyzer.end();
    }

    @Test
    public void testHandle() {
        final List<String[]> records = getRecords(AnalyzerTest.class.getResourceAsStream("employee_1000.csv"));
        for (String[] record : records) {
            semanticAnalyzer.analyze(record);
        }
        // TODO Asserts on semantic types
    }

}
