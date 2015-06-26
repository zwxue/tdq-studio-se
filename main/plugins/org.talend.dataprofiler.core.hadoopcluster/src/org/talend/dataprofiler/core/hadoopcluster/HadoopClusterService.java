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
package org.talend.dataprofiler.core.hadoopcluster;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.ui.IWorkbench;
import org.talend.core.database.EDatabaseTypeName;
import org.talend.core.database.conn.ConnParameterKeys;
import org.talend.core.database.conn.template.EDatabaseConnTemplate;
import org.talend.core.hadoop.version.EHadoopDistributions;
import org.talend.core.hadoop.version.EHadoopVersion4Drivers;
import org.talend.core.hadoop.version.custom.ECustomVersionGroup;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.dataprofiler.core.hadoopcluster.action.CreateHiveTableAction;
import org.talend.dataprofiler.core.hadoopcluster.action.RetrieveSchemaOfHDFSAction;
import org.talend.dataprofiler.service.IHadoopClusterService;
import org.talend.repository.hadoopcluster.node.model.HadoopClusterRepositoryNodeType;
import org.talend.repository.hadoopcluster.ui.HadoopClusterWizard;
import org.talend.repository.hadoopcluster.util.HCRepositoryUtil;
import org.talend.repository.hdfs.node.model.HDFSRepositoryNodeType;
import org.talend.repository.hdfs.ui.HDFSWizard;
import org.talend.repository.model.RepositoryNode;
import org.talend.repository.model.hadoopcluster.HadoopClusterConnection;
import org.talend.repository.model.hadoopcluster.HadoopClusterConnectionItem;
import org.talend.repository.model.hdfs.HDFSConnection;
import org.talend.repository.model.hdfs.HDFSConnectionItem;

/**
 * created by yyin on 2015年6月25日 Detailled comment
 *
 */
public class HadoopClusterService implements IHadoopClusterService {

    /**
     * getHadoopClusterID.
     * 
     * @param viewObject
     * @return
     */
    @Override
    public String getHadoopClusterID(Object object) {
        IRepositoryViewObject viewObject = (IRepositoryViewObject) object;
        HDFSConnectionItem dbItem = (HDFSConnectionItem) viewObject.getProperty().getItem();
        HDFSConnection dbConnection = (HDFSConnection) dbItem.getConnection();
        return dbConnection.getRelativeHadoopClusterId();
    }

    @Override
    public ERepositoryObjectType getHDFSType() {
        return HDFSRepositoryNodeType.HDFS;
    }

    @Override
    public ERepositoryObjectType getHadoopClusterType() {
        return HadoopClusterRepositoryNodeType.HADOOPCLUSTER;
    }

    @Override
    public void initConnectionParameters(Map<String, String> initMap, Object node) {
        HadoopClusterConnectionItem hcConnectionItem = HCRepositoryUtil
                .getHCConnectionItemFromRepositoryNode((RepositoryNode) node);
        Map<String, String> dbParameters = HCRepositoryUtil.getHadoopDbParameters(hcConnectionItem.getProperty().getId());
        Iterator<Entry<String, String>> iterator = dbParameters.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            initMap.put(entry.getKey(), entry.getValue());
        }
        HadoopClusterConnection hcConnection = (HadoopClusterConnection) hcConnectionItem.getConnection();
        initMap.put(ConnParameterKeys.CONN_PARA_KEY_HADOOP_CUSTOM_JARS,
                hcConnection.getParameters().get(ECustomVersionGroup.HIVE.getName()));
        initMap.put(ConnParameterKeys.CONN_PARA_KEY_DB_TYPE, EDatabaseConnTemplate.HIVE.getDBTypeName());
        initMap.put(ConnParameterKeys.CONN_PARA_KEY_DB_PRODUCT, EDatabaseTypeName.HIVE.getProduct());
        initMap.put(ConnParameterKeys.CONN_PARA_KEY_DB_PORT, EDatabaseConnTemplate.HIVE.getDefaultPort());
    }

    @Override
    public Object createHDFSWizard(Object workbench, boolean creation, Object node, String[] existingNames) {
        return new HDFSWizard((IWorkbench) workbench, creation, (RepositoryNode) node, existingNames);
    }

    @Override
    public Object createHadoopClusterWizard(Object workbench, boolean creation, Object node, String[] existingNames) {
        return new HadoopClusterWizard((IWorkbench) workbench, creation, (RepositoryNode) node, existingNames);
    }

    @Override
    public Map<Object, Object> createHiveTable(Object hdfsNode) {
        CreateHiveTableAction createTable = new CreateHiveTableAction((RepositoryNode) hdfsNode);
        createTable.run();
        // selectedHive = new one
        Map<Object, Object> conAndTable = new HashMap<>();
        conAndTable.put(createTable.getCreateTableName(), createTable.getHiveConnectionItem());

        return conAndTable;
    }

    @Override
    public boolean hideAction(Object node) {
        HadoopClusterConnectionItem hcConnectionItem = HCRepositoryUtil
                .getHCConnectionItemFromRepositoryNode((RepositoryNode) node);
        if (hcConnectionItem != null) {
            HadoopClusterConnection hcConnection = (HadoopClusterConnection) hcConnectionItem.getConnection();
            EHadoopDistributions distribution = EHadoopDistributions.getDistributionByName(hcConnection.getDistribution(), false);
            EHadoopVersion4Drivers version4Drivers = EHadoopVersion4Drivers.indexOfByVersion(hcConnection.getDfVersion());
            if (EHadoopVersion4Drivers.APACHE_0_20_204.equals(version4Drivers)
                    || EHadoopVersion4Drivers.APACHE_0_20_2.equals(version4Drivers)) {
                return true;
            }
            if (distribution == EHadoopDistributions.MICROSOFT_HD_INSIGHT) {
                return true;
            }
        }

        return false;
    }

    @Override
    public Object createActionOfHiveTable(Object node) {
        return new CreateHiveTableAction((RepositoryNode) node);
    }

    @Override
    public Object createActionOfRetrieveHDFS(Object node) {
        return new RetrieveSchemaOfHDFSAction((RepositoryNode) node);
    }
}
