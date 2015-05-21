package org.talend.datascience.common.inference.type;

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

public class SemanticAndDataTypeInferExecutorTest {
	SemanticAndDataTypeInferExecutor executor = null;

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testHandle() throws IOException {
		executor = new SemanticAndDataTypeInferExecutor();
		DataTypeInferExecutorTest printService = new DataTypeInferExecutorTest();
		InputStream in = this
				.getClass()
				.getClassLoader()
				.getResourceAsStream(
						"org/talend/datascience/common/inference/type/employee_100.csv");
		BufferedReader inBuffReader = new BufferedReader(new InputStreamReader(
				in));
		String start = "";
		String end = "";
		String line = null;
		start = TypeInferenceUtilsTest.getCurrentTimeStamp();
		printService.printline("1 00 data set infer start at " + start);
		executor.init();
		while ((line = inBuffReader.readLine()) != null) {
			String[] record = StringUtils
					.splitByWholeSeparatorPreserveAllTokens(line, ";");
			executor.handle(record);
		}
		end = TypeInferenceUtilsTest.getCurrentTimeStamp();
		printService.printline("100 data set infer end at " + end);
		double timeDiff = TypeInferenceUtilsTest.getTimeDifference(start, end);
		printService.printline("100 time difference: " + timeDiff);
		Assert.assertTrue(timeDiff < 7);

		// Get the semantic name result .
		List<ColumnTypeBean> semanticTypeList = executor.getResults();
		for (ColumnTypeBean bean : semanticTypeList) {
			if (bean.getColumnIdx() == 1) {
				Map<String, Long> semanticTypeToCount = bean
						.getSemanticNameToCountMap();
				Iterator<String> semanticTypes = semanticTypeToCount.keySet()
						.iterator();
				// Assert semantic category type result, this assert should be
				// same to the one in SemanticInferExecutorTest.java for 1000
				// records.
				while (semanticTypes.hasNext()) {
					String semanticName = semanticTypes.next();
					if ("ADDRESS".equalsIgnoreCase(semanticName)) {
						Assert.assertEquals(1l,
								semanticTypeToCount.get(semanticName), 0l);
					} else if ("ORGANISATION_CULTURE"
							.equalsIgnoreCase(semanticName)) {
						Assert.assertEquals(1l,
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
				Assert.assertEquals("DATE", bean.getSemanticNameToCountMap()
						.keySet().toArray()[0].toString());
				Assert.assertEquals(100, bean.getSemanticTypeCount("DATE"), 0);
			}

			// Assert data type result, this assert should be same to the one in
			// DataTypeInferExecutorTest.java for 1000 records.
			Iterator<String> typeKeys = bean.getTypeToCountMap().keySet()
					.iterator();
			while (typeKeys.hasNext()) {
				String key = typeKeys.next();
				Long count = bean.getDataTypeCount(key);
				printService.printline("--- " + key + " : " + count);
				if (18 == (bean.getColumnIdx() + 1)) {
					// Assert column 18, empty:100 string:900
					if (TypeInferenceUtils.TYPE_EMPTY.equals(key)) {
						Assert.assertEquals(10, count, 0.00d);
					} else if (TypeInferenceUtils.TYPE_STRING.equals(key)) {
						Assert.assertEquals(90, count, 0l);
					}
				} else if (16 == (bean.getColumnIdx() + 1)) {
					// Assert column 16, char:900 integer:100
					if (TypeInferenceUtils.TYPE_CHAR.equals(key)) {
						Assert.assertEquals(90, count, 0.00d);
					} else if (TypeInferenceUtils.TYPE_INTEGER.equals(key)) {
						Assert.assertEquals(10, count, 0l);
					}
				} else if (13 == (bean.getColumnIdx() + 1)) {
					// Assert column 13, double:1000
					if (TypeInferenceUtils.TYPE_DOUBLE.equals(key)) {
						Assert.assertEquals(100, count, 0.00d);
					}
				} else if (11 == (bean.getColumnIdx() + 1)) {
					// Assert column 11, date:1000
					if (TypeInferenceUtils.TYPE_DATE.equals(key)) {
						Assert.assertEquals(100, count, 0.00d);
					}
				}
			}
		}
	}

}
