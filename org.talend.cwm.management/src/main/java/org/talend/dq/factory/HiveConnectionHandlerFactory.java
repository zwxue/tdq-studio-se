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

import org.talend.core.database.conn.ConnParameterKeys;
import org.talend.core.database.hbase.conn.version.EHBaseDistribution4Versions;
import org.talend.core.model.metadata.IMetadataConnection;
import org.talend.dq.handler.CDH4YarnHandler;
import org.talend.dq.handler.CDH5Handler;
import org.talend.dq.handler.HDP130Handler;
import org.talend.dq.handler.HDP200Handler;
import org.talend.dq.handler.HiveConnectionHandler;
import org.talend.dq.handler.Mapr212Handler;

/**
 * created Hive Handler for excuting some paramters.
 * 
 */
public class HiveConnectionHandlerFactory {

    public static HiveConnectionHandler createHandler(IMetadataConnection metadataConnection) {
        HiveConnectionHandler handler = null;
        String version = (String) metadataConnection.getParameter(ConnParameterKeys.CONN_PARA_KEY_HIVE_VERSION);
        if (EHBaseDistribution4Versions.HDP_1_3.getVersionValue().equals(version)) {
            handler = new HDP130Handler(metadataConnection);
        } else if (EHBaseDistribution4Versions.CLOUDERA_CDH4_YARN.getVersionValue().equals(version)) {
            handler = new CDH4YarnHandler(metadataConnection);
        } else if (EHBaseDistribution4Versions.HDP_2_0.getVersionValue().equals(version)) {
            handler = new HDP200Handler(metadataConnection);
        } else if (EHBaseDistribution4Versions.MAPR_2_1_2.getVersionValue().equals(version)
                || EHBaseDistribution4Versions.MAPR_3_0_1.getVersionValue().equals(version)) {
            handler = new Mapr212Handler(metadataConnection);
        } else if (EHBaseDistribution4Versions.CLOUDERA_CDH5.getVersionValue().equals(version)) {
            handler = new CDH5Handler(metadataConnection);
        } else {
            handler = new HiveConnectionHandler(metadataConnection);
        }
        return handler;
    }

}
