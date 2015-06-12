package org.talend.datascience.common.recordlinkage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class StringsClusterAnalyzerTest {

    StringsClusterAnalyzer analyser = null;

    @Before
    public void setUp() throws Exception {
        analyser = new StringsClusterAnalyzer();
    }

    @Test
    public void testGetResult() throws IOException {
        analyser.init();
        String columnDelimiter = "|";
        InputStream in = this.getClass().getResourceAsStream("incoming_customers_swoosh_fingerprintkey.txt"); //$NON-NLS-1$
        BufferedReader bfr = new BufferedReader(new InputStreamReader(in));
        List<String> listOfLines = IOUtils.readLines(bfr);
        for (String line : listOfLines) {
            String[] fields = StringUtils.splitPreserveAllTokens(line, columnDelimiter);
            analyser.analyze(fields[1]);
        }
        analyser.end();
        List<StringClusters> results = analyser.getResult();
        Assert.assertEquals(3, results.size());
        // TODO Do asserts on cluster content (see testTShirtsLogic)
    }

    @Test
    public void testCluster10000() throws IOException {
        analyser.init();
        String columnDelimiter = "|";
        InputStream in = this.getClass().getResourceAsStream("cluster10000.txt"); //$NON-NLS-1$
        BufferedReader bfr = new BufferedReader(new InputStreamReader(in));
        List<String> listOfLines = IOUtils.readLines(bfr);
        for (String line : listOfLines) {
            String[] fields = StringUtils.splitPreserveAllTokens(line, columnDelimiter);
            analyser.analyze(fields[0]);
        }
        analyser.end();
        int size = 0;
        for (StringClusters.StringCluster cluster : analyser.getResult().get(0)) {
            size++;
        }
        Assert.assertEquals(3, size);
        // TODO Do asserts on cluster content (see testTShirtsLogic)
    }

    @Test
    public void testTShirtsLogic() throws IOException {
        analyser.init();
        String columnDelimiter = "|";
        InputStream in = this.getClass().getResourceAsStream("tshirts.txt"); //$NON-NLS-1$
        BufferedReader bfr = new BufferedReader(new InputStreamReader(in));
        List<String> listOfLines = IOUtils.readLines(bfr);
        for (String line : listOfLines) {
            String[] fields = StringUtils.splitPreserveAllTokens(line, columnDelimiter);
            analyser.analyze(fields[0]);
        }

        analyser.end();

        StringClusters strCluster = analyser.getResult().get(0);
        Map<String, List<String>> expectedClusters = new HashMap<String, List<String>>();
        expectedClusters.put("Black T-shirt",
                Arrays.asList("Black T-shirt", "Black Tshirt", "Black T-shirt", "Black T-Shirt", "Black T-shirT"));
        expectedClusters.put("Blck T-shirt", Collections.singletonList("Blck T-shirt"));
        expectedClusters.put("White T-shirt", Collections.singletonList("White T-shirt"));
        for (StringClusters.StringCluster cluster : strCluster) {
            Assert.assertTrue(expectedClusters.containsKey(cluster.survivedValue));
            for (String originalValue : cluster.originalValues) {
                Assert.assertTrue(expectedClusters.get(cluster.survivedValue).contains(originalValue));
            }
        }
    }
}
