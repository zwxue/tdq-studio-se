// ============================================================================
//
// Copyright (C) 2006-2010 Talend Inc. - www.talend.com
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

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


/**
 * DOC scorreia  class global comment. Detailled comment
 */
public class DatePatternRetrieverTest {

   
	/**
     * DOC scorreia Comment method "setUp".
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {    	
    }

    /**
     * DOC scorreia Comment method "tearDown".
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {    	
    }

    /**
     * Test method for {@link org.talend.dataquality.matching.date.pattern.DatePatternRetriever#initModel2Regex(java.lang.String[][])}.
     */
    @Test
    public void testInitModel2Regex() {
    	DatePatternRetriever dtr = new  DatePatternRetriever(); 
    	dtr.initModel2Regex(new File("PatternsNameAndRegularExpressions.txt"));
    	assertNotNull(dtr.getModelMatchers());
    }

    /**
     * Test method for {@link org.talend.dataquality.matching.date.pattern.DatePatternRetriever#handle(java.lang.String)}.
     */
    @Test
    public void testHandle() {
    	 DatePatternRetriever dtr = new  DatePatternRetriever(); 
    	 dtr.handle("21 11 1999");
    	 dtr.getModelMatchers().add(new ModelMatcher("^[0-3][0-9](-|/| )([0-0][1-9]|10|11|12)(-|/| )(19|20)[0-9]{2}$", "11 novenmber 1999"));    	    	 
    	 assertEquals(dtr.getModelMatchers(),1);
    }
 }
