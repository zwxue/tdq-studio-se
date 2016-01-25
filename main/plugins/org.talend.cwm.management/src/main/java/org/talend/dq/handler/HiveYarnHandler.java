// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
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

import java.util.HashMap;
import java.util.Map;

import org.talend.core.database.conn.ConnParameterKeys;
import org.talend.core.model.metadata.IMetadataConnection;
import org.talend.dataquality.PluginConstant;

/**
 * created by qiongli on 2013-11-7 Detailled comment
 * 
 */
public class HiveYarnHandler extends HortonWorksHandler {

    private final String MAP_FM_NAME = "mapreduce.framework.name"; //$NON-NLS-1$

    private final String MAP_FM_NAME_VALUE = "yarn"; //$NON-NLS-1$

    private final String YARN_RM_ADDRESS = "yarn.resourcemanager.address"; //$NON-NLS-1$

    public final String YARN_CLASSPATH = "yarn.application.classpath"; //$NON-NLS-1$

    public final String YARN_CLASSPATH_VALUE = "$HADOOP_CONF_DIR,$HADOOP_COMMON_HOME/*,$HADOOP_COMMON_HOME/lib/*,$HADOOP_HDFS_HOME/*,$HADOOP_HDFS_HOME/lib/*,$HADOOP_MAPRED_HOME/*,$HADOOP_MAPRED_HOME/lib/*,$YARN_HOME/*,$YARN_HOME/lib/*,$HADOOP_YARN_HOME/*,$HADOOP_YARN_HOME/lib/*,$HADOOP_COMMON_HOME/share/hadoop/common/*,$HADOOP_COMMON_HOME/share/hadoop/common/lib/*,$HADOOP_HDFS_HOME/share/hadoop/hdfs/*,$HADOOP_HDFS_HOME/share/hadoop/hdfs/lib/*,$HADOOP_YARN_HOME/share/hadoop/yarn/*,$HADOOP_YARN_HOME/share/hadoop/yarn/lib/*"; //$NON-NLS-1$

    /**
     * DOC qiongli HDP_2_0_0_Handler constructor comment.
     * 
     * @param metadataConnection
     */
    public HiveYarnHandler(IMetadataConnection metadataConnection) {
        super(metadataConnection);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.handler.HiveConnectionHandler#getDefaultHadoopParameters()
     */
    @Override
    protected Map<String, String> getDefaultHadoopParameters() {
        Map<String, String> map = new HashMap<String, String>();
        Map<String, String> hadoopPropertiesMap = getHadoopPropertiesMap();
        if (hadoopPropertiesMap.get(MAP_FM_NAME) == null) {
            map.put(MAP_FM_NAME, MAP_FM_NAME_VALUE);
        }
        if (hadoopPropertiesMap.get(YARN_RM_ADDRESS) == null) {
            Object jobTrackerURL = getMetadataConnection().getParameter(ConnParameterKeys.CONN_PARA_KEY_JOB_TRACKER_URL);
            if (jobTrackerURL != null && !PluginConstant.EMPTY_STRING.equals(jobTrackerURL.toString().trim())) {
                map.put(YARN_RM_ADDRESS, jobTrackerURL.toString());
            }
        }
        if (hadoopPropertiesMap.get(YARN_CLASSPATH) == null) {
            map.put(YARN_CLASSPATH, YARN_CLASSPATH_VALUE);
        }
        return map;
    }

}
