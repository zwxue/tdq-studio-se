// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
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

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.Test;
import org.talend.commons.utils.resource.ResourceUtil;

/**
 * Test class of DatePatternRetriever.
 */
public class DatePatternRetrieverTest {

    /**
     * Test method for
     * {@link org.talend.dataquality.matching.date.pattern.DatePatternRetriever#initModel2Regex(java.lang.String[][])}.
     * 
     * @throws URISyntaxException
     * @throws IOException
     */
    @Test
    public void testInitModel2Regex() throws IOException, URISyntaxException {
        File file = ResourceUtil.getFileFromResource(getClass(), "/data/PatternsNameAndRegularExpressions.txt"); //$NON-NLS-1$
        DatePatternRetriever dtr = new DatePatternRetriever();
        dtr.initModel2Regex(file);
        assertNotNull(dtr.getModelMatchers());
        assertEquals("Found " + dtr.getModelMatchers().size() + " modelMatchers. Was waiting for 32 patterns in file", true, dtr //$NON-NLS-1$//$NON-NLS-2$
                .getModelMatchers().size() == 32);
    }

    /**
     * Test method for
     * {@link org.talend.dataquality.matching.date.pattern.DatePatternRetriever#handle(java.lang.String)}.
     */
    @Test
    public void testHandle() {
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
    }
}
