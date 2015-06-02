package org.talend.datascience.common.recordlinkage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.talend.datascience.common.recordlinkage.StringsCluster.CountKey;

public class StringsClusterAnalyzerTest {
	StringsClusterAnalyzer analyser = null;

	@Before
	public void setUp() throws Exception {
		analyser = new StringsClusterAnalyzer();
		analyser.init();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetResult() throws IOException {
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

}
