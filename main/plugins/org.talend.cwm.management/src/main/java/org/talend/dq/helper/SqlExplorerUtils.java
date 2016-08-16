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
package org.talend.dq.helper;

import java.io.File;
import java.net.URL;
import java.sql.Driver;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.services.IServiceLocator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.talend.commons.bridge.ReponsitoryContextBridge;
import org.talend.commons.exception.PersistenceException;
import org.talend.core.model.metadata.IMetadataConnection;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.metadata.builder.connection.DatabaseConnection;
import org.talend.core.model.metadata.builder.util.MetadataConnectionUtils;
import org.talend.core.model.properties.DatabaseConnectionItem;
import org.talend.core.model.properties.Item;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.cwm.indicator.ColumnFilter;
import org.talend.cwm.management.i18n.Messages;
import org.talend.dataprofiler.service.ISqlexplorerService;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.mapdb.ColumnSetDBMap;
import org.talend.dataquality.indicators.mapdb.DBMap;
import org.talend.dataquality.indicators.mapdb.DBSet;
import org.talend.dq.CWMPlugin;
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

    public static final String PLUGIN_NAME = "net.sourceforge.sqlexplorer"; //$NON-NLS-1$

    private static final String SQL_EXPLORER_VERSION = "_5.6.1"; //$NON-NLS-1$

    public static final String JAR_FILE_NAME = PLUGIN_NAME + SQL_EXPLORER_VERSION + ".jar"; //$NON-NLS-1$ 

    public static final String COMMAND_ID = "org.talend.updates.show.wizard.command"; //$NON-NLS-1$

    public static final String TALENDDATASET_CLASS_NAME = "net.sourceforge.sqlexplorer.dataset.mapdb.TalendDataSet"; //$NON-NLS-1$

    private static SqlExplorerUtils sqlExplorerUtils;

    private ISqlexplorerService sqlexplorerService;

    private boolean initRootProject = false;

    private boolean initAllDrivers = false;

    private boolean hasShowDownloadWizard = false;

    public boolean isSqlexplorerInstalled() {
        initSqlExplorerService(false);
        return this.sqlexplorerService != null;
    }

    public ISqlexplorerService getSqlexplorerService() {
        if (this.sqlexplorerService == null) {
            initSqlExplorerService(true);
        }
        return this.sqlexplorerService;
    }

    /**
     * 
     * init the sqlexplorerService.
     * 
     * @param isNeedDownload. if the service is not found, "isNeedDownload" will pop the downlaoding dialog.
     * 
     */
    private void initSqlExplorerService(boolean isNeedDownload) {

        if (this.sqlexplorerService == null) {
            BundleContext context = CWMPlugin.getDefault().getBundleContext();
            if (context == null) {
                return;
            }

            ServiceReference serviceReference = context.getServiceReference(ISqlexplorerService.class.getName());
            if (serviceReference != null) {
                Object obj = context.getService(serviceReference);
                if (obj != null) {
                    this.sqlexplorerService = (ISqlexplorerService) obj;
                }
            } else if (isNeedDownload) {
                // check the jar file has been donwloaded or not
                String pathToStore = Platform.getInstallLocation().getURL().getFile() + "plugins"; //$NON-NLS-1$
                File movedfile = new File(pathToStore, JAR_FILE_NAME);
                if (movedfile.exists()) {
                    log.warn(Messages.getString("SqlExplorerUtils.restartToLoadSqlexplorer")); //$NON-NLS-1$
                } else if (!hasShowDownloadWizard) {
                    // show download jar dialog
                    IServiceLocator serviceLocator = PlatformUI.getWorkbench();
                    ICommandService commandService = (ICommandService) serviceLocator.getService(ICommandService.class);
                    try {
                        Command command = commandService.getCommand(COMMAND_ID);
                        command.executeWithChecks(new ExecutionEvent());
                        hasShowDownloadWizard = true;
                    } catch (Exception e) {
                        log.error(e);
                    }
                } else {
                    log.error(Messages.getString("SqlExplorerUtils.missingSqlexplorer")); //$NON-NLS-1$
                }
            }
        }

        if (!this.initRootProject && sqlexplorerService != null) {
            this.sqlexplorerService.initSqlExplorerRootProject(ReponsitoryContextBridge.getRootProject());
            this.initRootProject = true;
        }
    }

    public void setSqlexplorerService(ISqlexplorerService sqlexplorerService) {
        this.sqlexplorerService = sqlexplorerService;
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
            if (!tdqSupportDBType.contains(dbType)) {
                // the dbtype is unsupported, show warning dialog
                MessageDialogWithToggle.openWarning(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                        Messages.getString("SqlExplorerUtils.Warning"), Messages.getString("SqlExplorerUtils.cantPreview")); //$NON-NLS-1$ //$NON-NLS-2$
            } else {
                if (getSqlexplorerService() != null) {
                    getSqlexplorerService().runInDQViewer(dbConn, editorName, query);
                }
            }
        } else {
            // if the connection is not database connection, do nothing
        }
    }

    public void findSqlExplorerTableNode(Connection providerConnection, Package parentPackageElement, String tableName,
            String activeTabName) {
        if (getSqlexplorerService() != null) {
            getSqlexplorerService().findSqlExplorerTableNode(providerConnection, parentPackageElement, tableName, activeTabName);
        }
    }

    public Driver getDriver(IMetadataConnection metadataConnection) throws InstantiationException, IllegalAccessException,
            ClassNotFoundException {
        if (getSqlexplorerService() != null) {
            return getSqlexplorerService().getDriver(metadataConnection.getDriverClass(), metadataConnection.getDriverJarPath());
        }
        return null;
    }

    public void initAllConnectionsToSQLExplorer() {
        // if sqlexplorerService is null, try to init it.
        if (this.sqlexplorerService == null) {
            getSqlexplorerService();
        }
        if (this.initAllDrivers || this.sqlexplorerService == null) {
            return;
        }
        List<Connection> conns = new ArrayList<Connection>();
        try {
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
        } catch (PersistenceException e) {
            log.error(e, e);
        }
        if (!conns.isEmpty()) {
            sqlexplorerService.initAllConnectionsToSQLExplorer(conns);
        }
        initAllDrivers = true;

    }

    public void setSqlEditorEditable(Object part, boolean lock) {
        if (getSqlexplorerService() != null) {
            getSqlexplorerService().setSqlEditorEditable(part, lock);
        }
    }

    public boolean needAddDriverConnection(DatabaseConnection dbConn) {
        if (sqlexplorerService != null) {
            return sqlexplorerService.needAddDriverConnection(dbConn);
        }
        return false;
    }

    public Class[] getMyURLClassLoaderAssignableClasses(URL url) {
        if (getSqlexplorerService() != null) {
            return getSqlexplorerService().getMyURLClassLoaderAssignableClasses(url);
        }
        return new Class[] {};
    }

    public Driver getClassDriverFromSQLExplorer(String driverClassName, Properties props) throws InstantiationException,
            IllegalAccessException {
        if (getSqlexplorerService() != null) {
            return getSqlexplorerService().getClassDriverFromSQLExplorer(driverClassName, props);
        }
        return null;
    }

    public void addConnetionAliasToSQLPlugin(ModelElement... dataproviders) {
        if (getSqlexplorerService() != null) {
            getSqlexplorerService().addConnetionAliasToSQLPlugin(dataproviders);
        }
    }

    public void updateConnetionAliasByName(Connection connection, String aliasName) {
        if (getSqlexplorerService() != null) {
            getSqlexplorerService().updateConnetionAliasByName(connection, aliasName);
        }
    }

    public void loadDriverByLibManageSystem(DatabaseConnection connection) {
        if (getSqlexplorerService() != null) {
            getSqlexplorerService().loadDriverByLibManageSystem(connection);
        }
    }

    public void removeAliasInSQLExplorer(DataProvider... dataproviders) {
        if (getSqlexplorerService() != null) {
            getSqlexplorerService().removeAliasInSQLExplorer(dataproviders);
        }
    }

    public boolean aliasExist(String connectionName) {
        if (getSqlexplorerService() != null) {
            return getSqlexplorerService().aliasExist(connectionName);
        }
        return false;
    }

    public Action createExportCSVAction() {
        if (getSqlexplorerService() != null) {
            return (Action) getSqlexplorerService().createExportCSVAction();
        }
        return null;
    }

    public void setExportCSVActionTable(Object action, Object table) {
        if (getSqlexplorerService() != null) {
            getSqlexplorerService().setExportCSVActionTable(action, table);
        }
    }

    public Object createMapDBColumnSetDataSet(String[] columnHeader, ColumnSetDBMap mapDB, Long size, Indicator currIndicator,
            int pageSize) {
        if (getSqlexplorerService() != null) {
            return getSqlexplorerService().createMapDBColumnSetDataSet(columnHeader, mapDB, size, currIndicator, pageSize);
        }
        return null;
    }

    public Object createMapDBSetDataSet(String[] columnHeader, DBSet<Object> mapDB, int pageSize) {
        if (getSqlexplorerService() != null) {
            return getSqlexplorerService().createMapDBSetDataSet(columnHeader, mapDB, pageSize);
        }
        return null;
    }

    public Object createMapDBDataSet(String[] columnHeader, DBMap<Object, List<Object>> mapDB, int pageSize,
            ColumnFilter columnFilter, Long itemSize) {
        if (getSqlexplorerService() != null) {
            return getSqlexplorerService().createMapDBDataSet(columnHeader, mapDB, pageSize, columnFilter, itemSize);
        }
        return null;
    }

    public void resetTalendDataSetIndex(Object talendDataSet, long fromIndex, long toIndex) {
        if (getSqlexplorerService() != null) {
            getSqlexplorerService().resetTalendDataSetIndex(talendDataSet, fromIndex, toIndex);
        }
    }

    public boolean isInstanceofTalendDataSet(Object talendDataSet) {
        if (getSqlexplorerService() != null) {
            return getSqlexplorerService().isInstanceofTalendDataSet(talendDataSet);
        }
        return false;
    }

    public Object createDataSet(String[] columnHeader, String[][] columnValue) {
        if (getSqlexplorerService() != null) {
            return getSqlexplorerService().createDataSet(columnHeader, columnValue);
        }
        return null;
    }
}
