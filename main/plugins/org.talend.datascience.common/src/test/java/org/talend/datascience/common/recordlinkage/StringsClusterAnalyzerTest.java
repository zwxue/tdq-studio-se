package org.talend.datascience.common.recordlinkage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.talend.datascience.common.inference.type.TypeInferenceUtilsTest;
import org.talend.datascience.common.recordlinkage.StringsCluster.CountKey;

public class StringsClusterAnalyzerTest {
	private static Logger LOGGER = Logger
			.getLogger(StringsClusterAnalyzerTest.class);
	StringsClusterAnalyzer analyser = null;

	@Before
	public void setUp() throws Exception {
		analyser = new StringsClusterAnalyzer();

	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetResult() throws IOException {
		analyser.init();
		String columnDelimiter = "|";
		InputStream in = this.getClass().getResourceAsStream(
				"incoming_customers_swoosh_fingerprintkey.txt"); //$NON-NLS-1$
		BufferedReader bfr = new BufferedReader(new InputStreamReader(in));
		List<String> listOfLines = IOUtils.readLines(bfr);
		for (String line : listOfLines) {
			String[] fields = StringUtils.splitPreserveAllTokens(line,
					columnDelimiter);
			analyser.analyze(fields[1]);
		}

		analyser.end();

		StringsCluster strCluster = analyser.getResult().get(0);

		Iterator<CountKey> keys = strCluster.getCountKeySet().iterator();
		int idx = 0;
		while (keys.hasNext()) {
			CountKey key = keys.next();
			if (idx == 0) {
				// Assert the most big group size is 5
				Assert.assertEquals(5, key.getCount(), 0);
				// Assert the most commons
				Assert.assertEquals("élément", strCluster.getSurvior(key));
			} else if (idx == 1) {
				// Assert 2nd most big group size is also 5
				Assert.assertEquals(5, key.getCount(), 0);
				// Assert the most commons
				Assert.assertEquals("scorreia", strCluster.getSurvior(key));

			} else if (idx == 2) {
				// Assert least group size is 1
				Assert.assertEquals(1, key.getCount(), 0);
				// Assert the most commons
				Assert.assertEquals("Amburgey", strCluster.getSurvior(key));

			}
			idx++;
		}
	}

	@Test
	public void testCluster10000() throws IOException {
		analyser.init();
		String columnDelimiter = "|";
		InputStream in = this.getClass()
				.getResourceAsStream("cluster10000.txt"); //$NON-NLS-1$
		BufferedReader bfr = new BufferedReader(new InputStreamReader(in));
		List<String> listOfLines = IOUtils.readLines(bfr);
		String timeStart = TypeInferenceUtilsTest.getCurrentTimeStamp();
		LOGGER.debug("clustering 10000 start at: " + timeStart);		
		for (String line : listOfLines) {
			String[] fields = StringUtils.splitPreserveAllTokens(line,
					columnDelimiter);
			analyser.analyze(fields[0]);
		}

		analyser.end();

		StringsCluster strCluster = analyser.getResult().get(0);
		
		String timeEnd = TypeInferenceUtilsTest.getCurrentTimeStamp();
		LOGGER.debug("clustering 10000 end at: " + timeEnd);		
		
		Iterator<CountKey> keys = strCluster.getCountKeySet().iterator();
		int idx = 0;
		while (keys.hasNext()) {
			CountKey key = keys.next();
			if (idx == 0) {
				Assert.assertEquals(4545, key.getCount(), 0);
				// Assert the most commons
				Assert.assertEquals("élément", strCluster.getSurvior(key));
			} else if (idx == 1) {
				Assert.assertEquals(4545, key.getCount(), 0);
				// Assert the most commons
				Assert.assertEquals("scorreia", strCluster.getSurvior(key));

			} else if (idx == 2) {
				// Assert least group size is 1
				Assert.assertEquals(909, key.getCount(), 0);
				// Assert the most commons
				Assert.assertEquals("Amburgey", strCluster.getSurvior(key));

			}
			idx++;
		}
	}
	
	@Test
	public void testTShirtsLogic()throws IOException{
		analyser.init();
		String columnDelimiter = "|";
		InputStream in = this.getClass()
				.getResourceAsStream("tshirts.txt"); //$NON-NLS-1$
		BufferedReader bfr = new BufferedReader(new InputStreamReader(in));
		List<String> listOfLines = IOUtils.readLines(bfr);
		for (String line : listOfLines) {
			String[] fields = StringUtils.splitPreserveAllTokens(line,
					columnDelimiter);
			analyser.analyze(fields[0]);
		}

		analyser.end();

		StringsCluster strCluster = analyser.getResult().get(0);
		
		Iterator<CountKey> keys = strCluster.getCountKeySet().iterator();
		int idx = 0;
		while (keys.hasNext()) {
			CountKey key = keys.next();
			if (idx == 0) {
				// Assert the most big group size is 5
				Assert.assertEquals(5, key.getCount(), 0);
				// Assert the most commons
				Assert.assertEquals("Black T-shirt", strCluster.getSurvior(key));
			} else if (idx == 1) {
				// Assert 2nd most big group size is also 1
				Assert.assertEquals(1, key.getCount(), 0);
				// Assert the most commons
				Assert.assertEquals("White T-shirt", strCluster.getSurvior(key));

			} else if (idx == 2) {
				// Assert least group size is 1
				Assert.assertEquals(1, key.getCount(), 0);
				// Assert the most commons
				Assert.assertEquals("Blck T-shirt", strCluster.getSurvior(key));

			}
			idx++;
		}
	}

}
