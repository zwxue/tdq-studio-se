// ============================================================================
//
// Copyright (C) 2006-2018 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataquality.matching.date.pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.talend.commons.utils.resource.ResourceUtil;
import org.talend.dataquality.matching.i18n.Messages;

/**
 * Test class of DatePatternRetriever.
 */
public class DatePatternRetrieverTest {

    private static Logger logger = Logger.getLogger(DatePatternRetrieverTest.class);

    /**
     * Test method for
     * {@link org.talend.dataquality.matching.date.pattern.DatePatternRetriever#initModel2Regex(java.lang.String[][])}.
     * 
     * @throws URISyntaxException
     * @throws IOException
     */
    @Test
    public void testInitModel2Regex() throws IOException, URISyntaxException {
        File file = ResourceUtil.getFileFromResource(getClass(), "/PatternsNameAndRegularExpressions.txt"); //$NON-NLS-1$
        DatePatternRetriever dtr = new DatePatternRetriever();
        dtr.initModel2Regex(file);
        assertNotNull(dtr.getModelMatchers());
        assertEquals("Found " + dtr.getModelMatchers().size() + " modelMatchers. Was waiting for 32 patterns in file", true, dtr //$NON-NLS-1$//$NON-NLS-2$
                .getModelMatchers().size() == 32);
    }

    /**
     * Test method for
     * {@link org.talend.dataquality.matching.date.pattern.DatePatternRetriever#handle(java.lang.String)}.
     * 
     * @throws URISyntaxException
     * @throws IOException
     */
    @Test
    public void testHandle() throws IOException, URISyntaxException {
        // string to match
        String expr = "21 11 1999"; //$NON-NLS-1$

        DatePatternRetriever dtr = new DatePatternRetriever();
        dtr.handle(expr);
        assertEquals(dtr.getModelMatchers().size(), 0);

        ModelMatcher mm = new ModelMatcher("11 november 1999", "^[0-3][0-9](-|/| )([0-0][1-9]|10|11|12)(-|/| )(19|20)[0-9]{2}$"); //$NON-NLS-1$//$NON-NLS-2$
        assertEquals(0, mm.getScore());
        dtr.getModelMatchers().add(mm);
        assertEquals(dtr.getModelMatchers().size(), 1);
        dtr.handle(expr);
        assertEquals(1, mm.getScore());

        // ADD sizhaoliu TDQ-8139 replace the main class by junits for org.talend.dataquality.matching
        DatePatternRetriever patt = new DatePatternRetriever();
        File file = ResourceUtil.getFileFromResource(getClass(), "/PatternsNameAndRegularExpressions.txt"); //$NON-NLS-1$
        File filedates = ResourceUtil.getFileFromResource(getClass(), "/data/dates.txt"); //$NON-NLS-1$

        patt.initModel2Regex(file);
        parseFile(filedates, patt);

        final int[] EXPECTED_SCORE = { 1, 4, 3, 1, 2, 1, 1, 40, 1 };

        for (int i = 0, j = 0; i < patt.getModelMatchers().size(); i++) {
            ModelMatcher patternMatcher = patt.getModelMatchers().get(i);
            if (patternMatcher.getScore() > 0) {
                assertEquals(EXPECTED_SCORE[j], patternMatcher.getScore());
                j++;
            }
        }
        if (logger.isInfoEnabled()) {
            patt.showResults();
        }
        // ~ TDQ-8139
    }

    private void parseFile(File fileDates, DatePatternRetriever patt) {
        try {
            FileReader fr = new FileReader(fileDates);
            BufferedReader br = new BufferedReader(fr);
            String line;
            try {
                while ((line = br.readLine()) != null) {
                    patt.handle(line.replace("\"", ""));//$NON-NLS-1$ //$NON-NLS-2$
                }
            } finally {
                br.close();
            }
        } catch (FileNotFoundException e) {
            logger.error(Messages.getString("DatePatternRetriever.warn1"));//$NON-NLS-1$
        } catch (IOException e) {
            logger.error(Messages.getString("DatePatternRetriever.warn2"));//$NON-NLS-1$
        }
    }

}
