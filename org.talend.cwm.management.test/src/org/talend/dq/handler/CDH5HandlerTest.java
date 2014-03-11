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
package org.talend.dq.handler;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.talend.core.model.metadata.IMetadataConnection;
import org.talend.core.model.metadata.builder.MetadataConnection;

/**
 * created by qiongli on 2014-3-10 Detailled comment
 * 
 */
public class CDH5HandlerTest {

    /**
     * DOC qiongli Comment method "setUp".
     * 
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }

    /**
     * DOC qiongli Comment method "tearDown".
     * 
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test method for {@link org.talend.dq.handler.CDH5Handler#getDefaultHadoopParameters()}.
     */
    @Test
    public void testGetDefaultHadoopParameters() {
        IMetadataConnection metadataConnection = new MetadataConnection();
        CDH5Handler cdh5Handler = new CDH5Handler(metadataConnection);
        Map<String, String> defaultHadoopParameters = cdh5Handler.getDefaultHadoopParameters();
        assertEquals(defaultHadoopParameters.get(CDH5Handler.YARN_MB), CDH5Handler.YARN_MB_VALUE);
        assertEquals(defaultHadoopParameters.get(CDH5Handler.YARN_CLASSPATH), CDH5Handler.YARN_CLASSPATH_VALUE);
        assertEquals(defaultHadoopParameters.get("mapreduce.map.memory.mb"), "1000"); //$NON-NLS-1$//$NON-NLS-2$
        assertEquals(defaultHadoopParameters.get("mapreduce.reduce.memory.mb"), "1000"); //$NON-NLS-1$//$NON-NLS-2$
    }
}
