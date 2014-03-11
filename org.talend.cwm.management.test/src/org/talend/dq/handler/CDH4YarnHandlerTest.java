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
 * created by qiongli on 2014-3-4 Detailled comment
 * 
 */
public class CDH4YarnHandlerTest {

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
     * Test method for {@link org.talend.dq.handler.CDH4YarnHandler#getDefaultHadoopParameters()}.
     */
    @Test
    public void testGetDefaultHadoopParameters() {
        IMetadataConnection metadataConnection = new MetadataConnection();
        metadataConnection.setParameter("CONN_PARA_KEY_JOB_TRACKER_URL", "hdfs://ubuntu:8020"); //$NON-NLS-1$//$NON-NLS-2$
        CDH4YarnHandler hiveYarnHandler = new CDH4YarnHandler(metadataConnection);
        Map<String, String> defaultHadoopParameters = hiveYarnHandler.getDefaultHadoopParameters();
        assertEquals(defaultHadoopParameters.get(CDH5Handler.MAP_FM_NAME), "yarn"); //$NON-NLS-1$
        assertEquals(defaultHadoopParameters.get(CDH5Handler.YARN_RM_ADDRESS), "hdfs://ubuntu:8020"); //$NON-NLS-1$ 
        assertEquals(defaultHadoopParameters.get(CDH5Handler.YARN_CLASSPATH), CDH5Handler.YARN_CLASSPATH_VALUE);
    }

}
