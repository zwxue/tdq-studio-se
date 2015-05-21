package org.talend.datascience.common.inference.semantic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.talend.datascience.common.inference.type.ColumnTypeBean;
import org.talend.datascience.common.inference.type.DataTypeInferExecutorTest;
import org.talend.datascience.common.inference.type.TypeInferenceUtilsTest;

public class SemanticInferExecutorTest {
	SemanticInferExecutor semanticInferExecutor = null;

	@Before
	public void setUp() throws Exception {
		semanticInferExecutor = new SemanticInferExecutor();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testHandle() {
		// ---------1. test given 1000 data ---------
		InputStream in = this
				.getClass()
				.getClassLoader()
				.getResourceAsStream(
						"org/talend/datascience/common/inference/type/employee_1000.csv");
		BufferedReader inBuffReader = new BufferedReader(new InputStreamReader(
				in));
		String start = "";
		String end = "";
		String line = null;
		DataTypeInferExecutorTest printService = new DataTypeInferExecutorTest();
		try {
			start = TypeInferenceUtilsTest.getCurrentTimeStamp();
			printService.printline("1 000 data set infer start at " + start);
			semanticInferExecutor.init();
			while ((line = inBuffReader.readLine()) != null) {
				String[] record = StringUtils
						.splitByWholeSeparatorPreserveAllTokens(line, ";");
				semanticInferExecutor.handle(record);
			}
			end = TypeInferenceUtilsTest.getCurrentTimeStamp();
			printService.printline("1 000 data set infer end at " + end);
			double timeDiff = TypeInferenceUtilsTest.getTimeDifference(start,
					end);
			printService.printline("1 000 time difference: " + timeDiff);
			Assert.assertTrue(timeDiff < 7);

			// Get the semantic name result .
			List<ColumnTypeBean> semanticTypeList = semanticInferExecutor
					.getResults();
			for (ColumnTypeBean bean : semanticTypeList) {
				if (bean.getColumnIdx() == 1) {
					Map<String, Long> semanticTypeToCount = bean
							.getSemanticNameToCountMap();
					Iterator<String> semanticTypes = semanticTypeToCount
							.keySet().iterator();
					while (semanticTypes.hasNext()) {
						String semanticName = semanticTypes.next();
						if ("ADDRESS".equalsIgnoreCase(semanticName)) {
							Assert.assertEquals(2l,
									semanticTypeToCount.get(semanticName), 0l);
						} else if ("ORGANISATION_CULTURE"
								.equalsIgnoreCase(semanticName)) {
							Assert.assertEquals(3l,
									semanticTypeToCount.get(semanticName), 0l);
						} else if ("ORGANISATION_BANK"
								.equalsIgnoreCase(semanticName)) {
							Assert.assertEquals(1l,
									semanticTypeToCount.get(semanticName), 0l);
						} else if ("ORGANISATION_SPORT"
								.equalsIgnoreCase(semanticName)) {
							Assert.assertEquals(1l,
									semanticTypeToCount.get(semanticName), 0l);
						}
					}
				} else if (bean.getColumnIdx() == 10) {
					Assert.assertEquals("DATE", bean
							.getSemanticNameToCountMap().keySet().toArray()[0]
							.toString());
					Assert.assertEquals(1000,
							bean.getSemanticTypeCount("DATE"), 0);
				}
			}

			// ---------2. test given 10000 data ---------
			in = this
					.getClass()
					.getClassLoader()
					.getResourceAsStream(
							"org/talend/datascience/common/inference/type/employee_10000.csv");
			inBuffReader = new BufferedReader(new InputStreamReader(in));
			start = "";
			end = "";
			line = null;
			start = TypeInferenceUtilsTest.getCurrentTimeStamp();
			printService.printline("1 0000 data set infer start at " + start);
			semanticInferExecutor.init();
			while ((line = inBuffReader.readLine()) != null) {
				String[] record = StringUtils
						.splitByWholeSeparatorPreserveAllTokens(line, ";");
				semanticInferExecutor.handle(record);
			}
			end = TypeInferenceUtilsTest.getCurrentTimeStamp();
			printService.printline("1 0000 data set infer end at " + end);
			timeDiff = TypeInferenceUtilsTest.getTimeDifference(start, end);
			printService.printline("1 0000 time difference: " + timeDiff);
			Assert.assertTrue(timeDiff < 9);

			// Get the semantic name result .
			semanticTypeList = semanticInferExecutor.getResults();
			for (ColumnTypeBean bean : semanticTypeList) {
				if (bean.getColumnIdx() == 1) {
					Map<String, Long> semanticTypeToCount = bean
							.getSemanticNameToCountMap();
					Iterator<String> semanticTypes = semanticTypeToCount
							.keySet().iterator();
					while (semanticTypes.hasNext()) {
						String semanticName = semanticTypes.next();
						if ("ADDRESS".equalsIgnoreCase(semanticName)) {
							Assert.assertEquals(20l,
									semanticTypeToCount.get(semanticName), 0l);
						} else if ("ORGANISATION_CULTURE"
								.equalsIgnoreCase(semanticName)) {
							Assert.assertEquals(30l,
									semanticTypeToCount.get(semanticName), 0l);
						} else if ("ORGANISATION_BANK"
								.equalsIgnoreCase(semanticName)) {
							Assert.assertEquals(10l,
									semanticTypeToCount.get(semanticName), 0l);
						} else if ("ORGANISATION_SPORT"
								.equalsIgnoreCase(semanticName)) {
							Assert.assertEquals(10l,
									semanticTypeToCount.get(semanticName), 0l);
						}
					}
				} else if (bean.getColumnIdx() == 10) {
					Assert.assertEquals("DATE", bean
							.getSemanticNameToCountMap().keySet().toArray()[0]
							.toString());
					Assert.assertEquals(10000,
							bean.getSemanticTypeCount("DATE"), 0);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
