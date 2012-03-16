// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.cwm.db.connection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.talend.core.model.metadata.IMetadataConnection;
import org.talend.core.model.metadata.MetadataFillFactory;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.metadata.builder.connection.MetadataTable;
import org.talend.core.model.metadata.builder.util.MetadataConnectionUtils;
import org.talend.core.repository.model.IRepositoryFactory;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.core.repository.model.RepositoryFactoryProvider;
import org.talend.cwm.relational.TdColumn;
import org.talend.cwm.relational.TdTable;
import org.talend.utils.properties.PropertiesLoader;
import org.talend.utils.properties.TypedProperties;
import org.talend.utils.sql.metadata.constants.TableType;
import orgomg.cwm.resource.relational.Catalog;

/**
 * DOC msjian class global comment. Detailled comment
 */
public class ConnectionUtilsTest {

    protected static Logger log = Logger.getLogger(ConnectionUtilsTest.class);

    private static final Class<ConnectionUtilsTest> THAT = ConnectionUtilsTest.class;

    /**
     * Test method for
     * {@link org.talend.cwm.db.connection.ConnectionUtils#retrieveColumn(org.talend.core.model.metadata.builder.connection.MetadataTable)}
     * .
     */
    @Test
    public void testRetrieveColumn() {
        TypedProperties connectionParams = PropertiesLoader.getProperties(THAT, "db.properties"); //$NON-NLS-1$
        java.util.Iterator<Object> iter = connectionParams.keySet().iterator();
        Map<String, String> parameterMap = new HashMap<String, String>();
        while (iter.hasNext()) {
            String key = (String) iter.next();
            String value = connectionParams.get(key) == null ? null : connectionParams.get(key).toString();
            parameterMap.put(key, value);
        }

        List<String> sqlDataTypeList = new ArrayList<String>();
        IMetadataConnection metadataConn = MetadataFillFactory.getDBInstance().fillUIParams(parameterMap);
        java.sql.Connection sqlconn = MetadataConnectionUtils.checkConnection(metadataConn).getObject();
        Connection dbconn=MetadataFillFactory.getDBInstance().fillUIConnParams(metadataConn,null);
        DatabaseMetaData databaseMetaData=null;
        try {
            databaseMetaData = sqlconn.getMetaData();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        List<Catalog> catalogList=MetadataFillFactory.getDBInstance().fillCatalogs(dbconn, databaseMetaData, null);
        initProxyRepository();
        List<TdTable> tableList = MetadataFillFactory.getDBInstance().fillTables(catalogList.get(1), databaseMetaData, null,
                null,
                new String[] { TableType.TABLE.toString() });
        List<TdColumn> columnList = MetadataFillFactory.getDBInstance().fillColumns(tableList.get(0), databaseMetaData, null);

        if (tableList.size() <= 0) {
            fail("The table of db should have more than one."); //$NON-NLS-1$
        }

        for (TdColumn tdColumn : columnList) {
            sqlDataTypeList.add(tdColumn.getSqlDataType().getName());
            tdColumn.setSqlDataType(null);
        }
        ConnectionUtils.retrieveColumn((MetadataTable) tableList.get(0));
        int i = 0;
        for (TdColumn tdColumn : columnList) {
            assertNotNull(tdColumn.getSqlDataType());
            assertEquals(sqlDataTypeList.get(i), tdColumn.getSqlDataType().getName());
            i++;
        }
    }

    private static boolean showUnimplemented = false;

    /**
     * DOC msjian Comment method "initProxyRepository".
     */
    private void initProxyRepository() {
        if (ProxyRepositoryFactory.getInstance().getRepositoryFactoryFromProvider() == null) {
            IRepositoryFactory repository = RepositoryFactoryProvider.getRepositoriyById("local"); //$NON-NLS-1$
            if (repository != null) {
                ProxyRepositoryFactory.getInstance().setRepositoryFactoryFromProvider(repository);
            }
        }
    }

    private void fail(String str) {
        if (showUnimplemented) {
            Assert.fail(str);
        }
    }
}
