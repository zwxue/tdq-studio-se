// ============================================================================
//
// Copyright (C) 2006-2013 Talend Inc. - www.talend.com
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

import java.util.Map;

import org.apache.log4j.Logger;
import org.talend.core.model.metadata.IMetadataConnection;

/**
 * created by xqliu on 2014-2-27 Detailled comment
 * 
 */
public class ClouderaCDH5Handler extends HiveYarnHandler {

    private static Logger log = Logger.getLogger(ClouderaCDH5Handler.class);

    private final String MAP_MB = "mapreduce.map.memory.mb"; //$NON-NLS-1$

    private final String REDUCE_MB = "mapreduce.reduce.memory.mb"; //$NON-NLS-1$

    private final String YARN_MB = "yarn.app.mapreduce.am.resource.mb"; //$NON-NLS-1$

    private final String YARN_CLASSPATH = "yarn.application.classpath"; //$NON-NLS-1$

    private final String MAP_MB_VALUE = "1000"; //$NON-NLS-1$

    private final String REDUCE_MB_VALUE = "1000"; //$NON-NLS-1$

    private final String YARN_MB_VALUE = "1000"; //$NON-NLS-1$

    private final String YARN_CLASSPATH_VALUE = "$HADOOP_CONF_DIR,$HADOOP_COMMON_HOME/*,$HADOOP_COMMON_HOME/lib/*,$HADOOP_HDFS_HOME/*,$HADOOP_HDFS_HOME/lib/*,$HADOOP_MAPRED_HOME/*,$HADOOP_MAPRED_HOME/lib/*,$YARN_HOME/*,$YARN_HOME/lib/*,$HADOOP_YARN_HOME/*,$HADOOP_YARN_HOME/lib/*,$HADOOP_COMMON_HOME/share/hadoop/common/*,$HADOOP_COMMON_HOME/share/hadoop/common/lib/*,$HADOOP_HDFS_HOME/share/hadoop/hdfs/*,$HADOOP_HDFS_HOME/share/hadoop/hdfs/lib/*,$HADOOP_YARN_HOME/share/hadoop/yarn/*,$HADOOP_YARN_HOME/share/hadoop/yarn/lib/*"; //$NON-NLS-1$

    public ClouderaCDH5Handler(IMetadataConnection metadataConnection) {
        super(metadataConnection);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.handler.HiveConnectionHandler#getDefaultHadoopParameters()
     */
    @Override
    protected Map<String, String> getDefaultHadoopParameters() {
        Map<String, String> map = super.getDefaultHadoopParameters();
        Map<String, String> hadoopPropertiesMap = getHadoopPropertiesMap();
        if (hadoopPropertiesMap.get(MAP_MB) == null) {
            map.put(MAP_MB, MAP_MB_VALUE);
        }
        if (hadoopPropertiesMap.get(REDUCE_MB) == null) {
            map.put(REDUCE_MB, REDUCE_MB_VALUE);
        }
        if (hadoopPropertiesMap.get(YARN_MB) == null) {
            map.put(YARN_MB, YARN_MB_VALUE);
        }
        if (hadoopPropertiesMap.get(YARN_CLASSPATH) == null) {
            map.put(YARN_CLASSPATH, YARN_CLASSPATH_VALUE);
        }
        log.info("YARN_CLASSPATH=" + map.get(YARN_CLASSPATH));
        log.info("MAP_FM_NAME=" + map.get("mapreduce.framework.name"));
        return map;
    }
}
