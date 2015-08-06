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
package org.talend.dq.handler;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.talend.core.database.conn.ConnParameterKeys;
import org.talend.core.database.hbase.conn.version.EHBaseDistribution4Versions;
import org.talend.core.hadoop.repository.HadoopRepositoryUtil;
import org.talend.core.model.metadata.IMetadataConnection;
import org.talend.core.model.metadata.connection.hive.HiveConnVersionInfo;
import org.talend.core.utils.TalendQuoteUtils;
import org.talend.metadata.managment.connection.manager.HiveConnectionManager;
import org.talend.utils.json.JSONException;

/**
 * created by xqliu on 2013-11-1 Detailled comment
 * 
 */
public class HiveConnectionHandler {

    private static Logger log = Logger.getLogger(HiveConnectionHandler.class);

    protected static final String PROPERTY = "PROPERTY"; //$NON-NLS-1$

    protected static final String VALUE = "VALUE"; //$NON-NLS-1$

    private IMetadataConnection metadataConnection;

    private Map<String, String> hadoopPropertiesMap = new HashMap<String, String>();

    /**
     * Getter for metadataConnection.
     * 
     * @return the metadataConnection
     */
    public IMetadataConnection getMetadataConnection() {
        return this.metadataConnection;
    }

    /**
     * Sets the metadataConnection.
     * 
     * @param metadataConnection the metadataConnection to set
     */
    public void setMetadataConnection(IMetadataConnection metadataConnection) {
        this.metadataConnection = metadataConnection;
    }

    /**
     * Getter for hadoopPropertiesMap.
     * 
     * @return the hadoopPropertiesMap
     */
    public Map<String, String> getHadoopPropertiesMap() {
        return this.hadoopPropertiesMap;
    }

    /**
     * Sets the hadoopPropertiesMap.
     * 
     * @param hadoopPropertiesMap the hadoopPropertiesMap to set
     */
    public void setHadoopPropertiesMap(Map<String, String> hadoopPropertiesMap) {
        this.hadoopPropertiesMap = hadoopPropertiesMap;
    }

    public HiveConnectionHandler(IMetadataConnection metadataConnection) {
        this.metadataConnection = metadataConnection;
        initHadoopProperties();
    }

    /**
     * init the hadoop properties according to the IMetadataConnection.
     */
    protected void initHadoopProperties() {
        getHadoopPropertiesMap().clear();
        String hadoopProperties = (String) getMetadataConnection().getParameter(ConnParameterKeys.CONN_PARA_KEY_HIVE_PROPERTIES);
        try {
            List<HashMap<String, Object>> hadoopPropertiesList = HadoopRepositoryUtil.getHadoopPropertiesList(hadoopProperties);
            for (HashMap<String, Object> map : hadoopPropertiesList) {
                Object objProp = map.get(PROPERTY);
                Object objValue = map.get(VALUE);
                if (objProp != null && objValue != null) {
                    String key = TalendQuoteUtils.removeQuotes(objProp.toString());
                    String value = TalendQuoteUtils.removeQuotes(objValue.toString());
                    getHadoopPropertiesMap().put(key, value);
                }
            }
        } catch (JSONException e) {
            log.equals(e);
        }
    }

    /**
     * create the hive connection, set/execute hadoop properties.
     * 
     * @return
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws SQLException
     */
    public java.sql.Connection createHiveConnection() throws ClassNotFoundException, InstantiationException,
            IllegalAccessException, SQLException {
        java.sql.Connection createConnection = null;
        if (getMetadataConnection() != null) {
            // create the hive connection
            createConnection = HiveConnectionManager.getInstance().createConnection(getMetadataConnection());
            // execute hadoop parameters
            executeHadoopParameters(createConnection);
        }
        return createConnection;
    }

    protected void executeHadoopParameters(java.sql.Connection sqlConnection) throws SQLException {
        Map<String, String> map = new HashMap<String, String>();
        // put the hadoop preperties which user input
        map.putAll(getHadoopPropertiesMap());
        // put the default hadoop parameters which user don't input
        map.putAll(getDefaultHadoopParameters());
        Statement createStatement = sqlConnection.createStatement();
        for (String key : map.keySet()) {
            createStatement.execute("SET " + key + "=" + map.get(key)); //$NON-NLS-1$ //$NON-NLS-2$
        }
        createStatement.close();
    }

    /**
     * if user don't input the default hadoop properties, add them and the default value here.
     * 
     * @return
     */
    protected Map<String, String> getDefaultHadoopParameters() {
        return new HashMap<String, String>();
    }

    /**
     * 
     * create a Handler when run an analysis.embeded model need to execute parameters.if it is standalone model,return
     * HiveConnectionHandler
     * 
     * @param metadataConnection
     * @return
     */
    public static HiveConnectionHandler createHandler(IMetadataConnection metadataConnection) {
        HiveConnectionHandler handler = null;
        String version = (String) metadataConnection.getParameter(ConnParameterKeys.CONN_PARA_KEY_HIVE_VERSION);
        String hiveModel = (String) metadataConnection.getParameter(ConnParameterKeys.CONN_PARA_KEY_HIVE_MODE);
        if (HiveConnVersionInfo.MODE_STANDALONE.getKey().equalsIgnoreCase(hiveModel)) {
            handler = new HiveConnectionHandler(metadataConnection);
        } else {
            if (EHBaseDistribution4Versions.HDP_1_3.getVersionValue().equals(version)) {
                handler = new HDP130Handler(metadataConnection);
            } else if (EHBaseDistribution4Versions.HDP_2_0.getVersionValue().equals(version)
                    || EHBaseDistribution4Versions.CLOUDERA_CDH4_YARN.getVersionValue().equals(version)) {
                handler = new HiveYarnHandler(metadataConnection);
            } else if (EHBaseDistribution4Versions.MAPR_2_1_2.getVersionValue().equals(version)
                    || EHBaseDistribution4Versions.MAPR_3_0_1.getVersionValue().equals(version)) {
                handler = new Mapr212Handler(metadataConnection);
            } else {
                handler = new HiveConnectionHandler(metadataConnection);
            }
        }
        return handler;
    }
}
