// ============================================================================
//
// Copyright (C) 2006-2014 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dq.helper;

import java.net.URL;
import java.sql.Driver;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.BundleContext;
import org.talend.commons.bridge.ReponsitoryContextBridge;
import org.talend.commons.exception.PersistenceException;
import org.talend.core.database.EDatabaseTypeName;
import org.talend.core.model.metadata.IMetadataConnection;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.metadata.builder.connection.DatabaseConnection;
import org.talend.core.model.metadata.builder.database.JavaSqlFactory;
import org.talend.core.model.metadata.builder.util.MetadataConnectionUtils;
import org.talend.core.model.properties.DatabaseConnectionItem;
import org.talend.core.model.properties.Item;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.cwm.management.i18n.Messages;
import org.talend.dq.CWMPlugin;
import org.talend.sqlexplorer.service.ISqlexplorerService;
import orgomg.cwm.foundation.softwaredeployment.DataProvider;
import orgomg.cwm.objectmodel.core.ModelElement;
import orgomg.cwm.objectmodel.core.Package;

/**
 * created by xqliu on 2014-9-9 Detailled comment
 * 
 */
public class SqlExplorerUtils {

    private static Logger log = Logger.getLogger(SqlExplorerUtils.class);

    public static final String SQLEDITOR_ID = "net.sourceforge.sqlexplorer.plugin.editors.SQLEditor"; //$NON-NLS-1$

    private static SqlExplorerUtils sqlExplorerUtils;

    private ISqlexplorerService sqlexplorerService;

    private boolean initRootProject = false;

    /**
     * Getter for sqlexplorerService.
     * 
     * @return the sqlexplorerService
     */
    public ISqlexplorerService getSqlexplorerService() {
        if (this.sqlexplorerService == null) {
            BundleContext context = CWMPlugin.getDefault().getContext();
            if (context != null) {
                Object obj = null;
                try {
                    obj = context.getService(context.getServiceReference(ISqlexplorerService.class.getName()));
                } catch (Exception ee) {
                    log.info(ee);
                    // show download jar dialog
                    return null;
                }
                if (obj != null && obj instanceof ISqlexplorerService) {
                    this.sqlexplorerService = (ISqlexplorerService) obj;
                }
            }
        }
        if (!initRootProject && this.sqlexplorerService != null) {
            this.sqlexplorerService.initSqlExplorerRootProject(ReponsitoryContextBridge.getRootProject());
            initRootProject = true;
        }
        return this.sqlexplorerService;
    }

    public static SqlExplorerUtils getDefault() {
        if (sqlExplorerUtils == null) {
            sqlExplorerUtils = new SqlExplorerUtils();
        }
        return sqlExplorerUtils;
    }

    /**
     * this method open DQ responsitory view and run the specified query in Sqlexplorer Editor.
     * 
     * @param tdDataProvider
     * @param query
     * @param editorName
     */
    public void runInDQViewer(Connection tdDataProvider, String query, String editorName) {
        DatabaseConnection dbConn = SwitchHelpers.DATABASECONNECTION_SWITCH.doSwitch(tdDataProvider);
        if (dbConn != null) { // open in sqlexplorer editor if it is database connection
            String dbType = dbConn.getDatabaseType();
            List<String> tdqSupportDBType = MetadataConnectionUtils.getTDQSupportDBTemplate();
            String username = JavaSqlFactory.getUsername(tdDataProvider);
            boolean isInvalidUserForMsSql = EDatabaseTypeName.MSSQL.getDisplayName().equalsIgnoreCase(dbType)
                    && StringUtils.isEmpty(username);
            if (isInvalidUserForMsSql || !tdqSupportDBType.contains(dbType)) {
                // the dbtype is unsupported, show warning dialog
                MessageDialogWithToggle.openWarning(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                        Messages.getString("SqlExplorerUtils.Warning"), Messages.getString("SqlExplorerUtils.cantPreview")); //$NON-NLS-1$ //$NON-NLS-2$
            } else {
                getSqlexplorerService().runInDQViewer(dbConn, editorName, query);
            }
        } else {
            // if the connection is not database connection, do nothing
        }
    }

    public void findSqlExplorerTableNode(Connection providerConnection, Package parentPackageElement, String tableName,
            String activeTabName) {
        getSqlexplorerService().findSqlExplorerTableNode(providerConnection, parentPackageElement, tableName, activeTabName);
    }

    public Driver getDriver(IMetadataConnection metadataConnection) throws InstantiationException, IllegalAccessException,
            ClassNotFoundException {
        return getSqlexplorerService().getDriver(metadataConnection.getDriverClass(), metadataConnection.getDriverJarPath());
    }

    public void initAllConnectionsToSQLExplorer() {
        try {
            List<Connection> conns = new ArrayList<Connection>();
            for (IRepositoryViewObject viewObject : ProxyRepositoryFactory.getInstance().getAll(
                    ERepositoryObjectType.METADATA_CONNECTIONS, true)) {
                if (viewObject == null || viewObject.getProperty() == null) {
                    continue;
                }
                Item item = viewObject.getProperty().getItem();
                if (item != null && (item instanceof DatabaseConnectionItem)) {
                    Connection connection = ((DatabaseConnectionItem) item).getConnection();
                    if (connection != null) {
                        conns.add(connection);
                    }
                }
            }
            if (!conns.isEmpty()) {
                getSqlexplorerService().initAllConnectionsToSQLExplorer(conns);
            }
        } catch (PersistenceException e) {
            log.error(e, e);
        }
    }

    public void setSqlEditorEditable(Object part, boolean lock) {
        getSqlexplorerService().setSqlEditorEditable(part, lock);
    }

    public boolean needAddDriverConnection(DatabaseConnection dbConn) {
        return getSqlexplorerService().needAddDriverConnection(dbConn);
    }

    public Class[] getMyURLClassLoaderAssignableClasses(URL url) {
        return getSqlexplorerService().getMyURLClassLoaderAssignableClasses(url);
    }

    public Driver getClassDriverFromSQLExplorer(String driverClassName, Properties props) throws InstantiationException,
            IllegalAccessException {
        return getSqlexplorerService().getClassDriverFromSQLExplorer(driverClassName, props);
    }

    // ////////////////
    public void addConnetionAliasToSQLPlugin(ModelElement... dataproviders) {
        getSqlexplorerService().addConnetionAliasToSQLPlugin(dataproviders);
    }

    public void updateConnetionAliasByName(Connection connection, String aliasName) {
        getSqlexplorerService().updateConnetionAliasByName(connection, aliasName);
    }

    public void loadDriverByLibManageSystem(DatabaseConnection connection) {
        getSqlexplorerService().loadDriverByLibManageSystem(connection);
    }

    public void removeAliasInSQLExplorer(DataProvider... dataproviders) {
        getSqlexplorerService().removeAliasInSQLExplorer(dataproviders);
    }

    public boolean aliasExist(String connectionName) {
        return getSqlexplorerService().aliasExist(connectionName);
    }
}
