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
package org.talend.dq.factory;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.talend.core.database.conn.ConnParameterKeys;
import org.talend.core.database.hbase.conn.version.EHBaseDistribution4Versions;
import org.talend.core.model.metadata.builder.MetadataConnection;
import org.talend.dq.handler.CDH4YarnHandler;
import org.talend.dq.handler.CDH5Handler;
import org.talend.dq.handler.HDP130Handler;
import org.talend.dq.handler.HDP200Handler;
import org.talend.dq.handler.HiveConnectionHandler;
import org.talend.dq.handler.Mapr212Handler;

/**
 * created by qiongli on 2014-3-10 Detailled comment
 * 
 */
public class HiveConnectionHandlerFactoryTest {

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
     * Test method for
     * {@link org.talend.dq.factory.HiveConnectionHandlerFactory#createHandler(org.talend.core.model.metadata.IMetadataConnection)}
     * .
     */
    @Test
    public void testCreateHandler() {
        MetadataConnection mc = new MetadataConnection();
        mc.setParameter(ConnParameterKeys.CONN_PARA_KEY_HIVE_VERSION, EHBaseDistribution4Versions.HDP_1_3.getVersionValue());
        HiveConnectionHandler createHandler = HiveConnectionHandlerFactory.createHandler(mc);
        assertTrue(createHandler instanceof HDP130Handler);

        mc.setParameter(ConnParameterKeys.CONN_PARA_KEY_HIVE_VERSION,
                EHBaseDistribution4Versions.CLOUDERA_CDH4_YARN.getVersionValue());
        createHandler = HiveConnectionHandlerFactory.createHandler(mc);
        assertTrue(createHandler instanceof CDH4YarnHandler);

        mc.setParameter(ConnParameterKeys.CONN_PARA_KEY_HIVE_VERSION, EHBaseDistribution4Versions.HDP_2_0.getVersionValue());
        createHandler = HiveConnectionHandlerFactory.createHandler(mc);
        assertTrue(createHandler instanceof HDP200Handler);

        mc.setParameter(ConnParameterKeys.CONN_PARA_KEY_HIVE_VERSION, EHBaseDistribution4Versions.MAPR_2_1_2.getVersionValue());
        createHandler = HiveConnectionHandlerFactory.createHandler(mc);
        assertTrue(createHandler instanceof Mapr212Handler);

        mc.setParameter(ConnParameterKeys.CONN_PARA_KEY_HIVE_VERSION, EHBaseDistribution4Versions.MAPR_3_0_1.getVersionValue());
        createHandler = HiveConnectionHandlerFactory.createHandler(mc);
        assertTrue(createHandler instanceof Mapr212Handler);

        mc.setParameter(ConnParameterKeys.CONN_PARA_KEY_HIVE_VERSION, EHBaseDistribution4Versions.CLOUDERA_CDH5.getVersionValue());
        createHandler = HiveConnectionHandlerFactory.createHandler(mc);
        assertTrue(createHandler instanceof CDH5Handler);

    }

}
