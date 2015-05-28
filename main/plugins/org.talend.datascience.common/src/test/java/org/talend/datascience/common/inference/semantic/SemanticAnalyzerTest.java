package org.talend.datascience.common.inference.semantic;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.talend.dataquality.semantic.recognizer.CategoryRecognizerBuilder.Mode;
import org.talend.datascience.common.inference.AnalyzerTest;

/**
 * This test is ignored for the time being because the dictionary path and key
 * word path is hard coded, they should be replaced later by elastic search
 * server.
 * 
 * @author zhao
 *
 */
public class SemanticAnalyzerTest extends AnalyzerTest {

	public static final String ddPath = AnalyzerTest.class.getResource(
			"luceneIdx/dictionary").getPath();
	public static final String kwPath = AnalyzerTest.class.getResource(
			"luceneIdx/keyword").getPath();

	SemanticAnalyzer semanticAnalyzer = null;

	private Mode mode = Mode.LUCENE;

	@Before
	public void setUp() throws Exception {
		semanticAnalyzer = new SemanticAnalyzer();
		semanticAnalyzer.setDdPath(ddPath);
		semanticAnalyzer.setKwPath(kwPath);
		semanticAnalyzer.setSemanticRecognizerMode(mode);
		semanticAnalyzer.init();
	}

	@After
	public void tearDown() throws Exception {
		semanticAnalyzer.end();
	}

	@Test
	public void testHandle() {
		final List<String[]> records = getRecords(AnalyzerTest.class
				.getResourceAsStream("employee_1000.csv"));
		for (String[] record : records) {
			semanticAnalyzer.analyze(record);
		}
		List<SemanticType> result = semanticAnalyzer.getResult();
		int columnIndex = 0;
		for (SemanticType semanticType : result) {
			Map<String, Long> semanticNameToCountMap = semanticType
					.getSemanticNameToCountMap();
			Iterator<String> semanticKeyIt = semanticNameToCountMap.keySet()
					.iterator();
			System.out.println("");
			while (semanticKeyIt.hasNext()) {
				String semanticName = semanticKeyIt.next();
				Long count = semanticNameToCountMap.get(semanticName);
				System.out
						.println("key: " + semanticName + ",  count:" + count);
				if (columnIndex == 1) {
					if ("ADDRESS".equalsIgnoreCase(semanticName)) {
						Assert.assertEquals(1l, count, 0l);
					} else if ("ORGANISATION_CULTURE"
							.equalsIgnoreCase(semanticName)) {
						Assert.assertEquals(1l, count, 0l);
					} else if ("ORGANISATION_SPORT"
							.equalsIgnoreCase(semanticName)) {
						Assert.assertEquals(1l, count, 0l);
					} else if ("UNKNOWN".equalsIgnoreCase(semanticName)) {
						Assert.assertEquals(99l, count, 0l);
					}
				} else if (columnIndex == 10) {
					Assert.assertEquals("DATE", semanticName);
					Assert.assertEquals(100, count, 0);
				}

			}
			columnIndex++;
		}
	}

}
