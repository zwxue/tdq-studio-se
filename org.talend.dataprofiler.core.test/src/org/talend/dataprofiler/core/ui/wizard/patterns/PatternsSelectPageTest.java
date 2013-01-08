// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.wizard.patterns;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.talend.core.model.metadata.builder.connection.ConnectionFactory;
import org.talend.core.model.metadata.builder.connection.MetadataColumn;
import org.talend.cwm.relational.TdColumn;
import org.talend.cwm.xml.TdXmlElementType;

/**
 * created by xqliu on Jan 8, 2013 Detailled comment
 * 
 */
public class PatternsSelectPageTest {

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    /**
     * DOC xqliu Comment method "setUpBeforeClass".
     * 
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        // some code here
    }

    /**
     * DOC xqliu Comment method "tearDownAfterClass".
     * 
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        // some code here
    }

    /**
     * DOC xqliu Comment method "setUp".
     * 
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        // some code here
    }

    /**
     * DOC xqliu Comment method "tearDown".
     * 
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
        // some code here
    }

    /**
     * Test method for
     * {@link org.talend.dataprofiler.core.ui.wizard.patterns.PatternsSelectPage#getElementName(orgomg.cwm.objectmodel.core.ModelElement)}
     * .
     */
    @Test
    public void testGetElementName1() {
        String name1 = "name1"; //$NON-NLS-1$
        String name2 = "name2"; //$NON-NLS-1$

        MetadataColumn metadataColumn = ConnectionFactory.eINSTANCE.createMetadataColumn();
        metadataColumn.setId(name1);

        String elementName = PatternsSelectPage.getElementName(metadataColumn);
        assertTrue(name1.equals(elementName));

        metadataColumn.setId(name2);
        elementName = PatternsSelectPage.getElementName(metadataColumn);
        assertFalse(name1.equals(elementName));
        assertTrue(name2.equals(elementName));
    }

    /**
     * Test method for
     * {@link org.talend.dataprofiler.core.ui.wizard.patterns.PatternsSelectPage#getElementName(orgomg.cwm.objectmodel.core.ModelElement)}
     * .
     */
    @Test
    public void testGetElementName2() {
        String name1 = "name1"; //$NON-NLS-1$
        String name2 = "name2"; //$NON-NLS-1$

        TdColumn tdColumn = org.talend.cwm.relational.RelationalFactory.eINSTANCE.createTdColumn();
        tdColumn.setName(name1);

        String elementName = PatternsSelectPage.getElementName(tdColumn);
        assertTrue(name1.equals(elementName));

        tdColumn.setName(name2);
        elementName = PatternsSelectPage.getElementName(tdColumn);
        assertFalse(name1.equals(elementName));
        assertTrue(name2.equals(elementName));
    }

    /**
     * Test method for
     * {@link org.talend.dataprofiler.core.ui.wizard.patterns.PatternsSelectPage#getElementName(orgomg.cwm.objectmodel.core.ModelElement)}
     * .
     */
    @Test
    public void testGetElementName3() {
        String name1 = "name1"; //$NON-NLS-1$
        String name2 = "name2"; //$NON-NLS-1$

        TdXmlElementType tdXmlElementType = org.talend.cwm.xml.XmlFactory.eINSTANCE.createTdXmlElementType();
        tdXmlElementType.setName(name1);

        String elementName = PatternsSelectPage.getElementName(tdXmlElementType);
        assertTrue(name1.equals(elementName));

        tdXmlElementType.setName(name2);
        elementName = PatternsSelectPage.getElementName(tdXmlElementType);
        assertFalse(name1.equals(elementName));
        assertTrue(name2.equals(elementName));
    }

    /**
     * Test method for
     * {@link org.talend.dataprofiler.core.ui.wizard.patterns.PatternsSelectPage#getElementName(orgomg.cwm.objectmodel.core.ModelElement)}
     * .
     */
    @Test
    public void testGetElementName4() {
        MetadataColumn metadataColumn = ConnectionFactory.eINSTANCE.createMetadataColumn();
        String elementName = PatternsSelectPage.getElementName(metadataColumn);
        assertNull(elementName);

        TdColumn tdColumn = org.talend.cwm.relational.RelationalFactory.eINSTANCE.createTdColumn();
        elementName = PatternsSelectPage.getElementName(tdColumn);
        assertNull(elementName);

        TdXmlElementType tdXmlElementType = org.talend.cwm.xml.XmlFactory.eINSTANCE.createTdXmlElementType();
        elementName = PatternsSelectPage.getElementName(tdXmlElementType);
        assertNull(elementName);
    }
}
