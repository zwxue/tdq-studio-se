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
package org.talend.dq;

import net.sourceforge.sqlexplorer.EDriverName;
import net.sourceforge.sqlexplorer.dbproduct.Alias;
import net.sourceforge.sqlexplorer.dbproduct.AliasManager;
import net.sourceforge.sqlexplorer.dbproduct.DriverManager;
import net.sourceforge.sqlexplorer.dbproduct.ManagedDriver;
import net.sourceforge.sqlexplorer.dbproduct.User;
import net.sourceforge.sqlexplorer.plugin.SQLExplorerPlugin;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.osgi.framework.BundleContext;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.metadata.builder.connection.DatabaseConnection;
import org.talend.core.model.metadata.builder.database.JavaSqlFactory;
import org.talend.core.model.metadata.builder.database.PluginConstant;
import org.talend.cwm.helper.ConnectionHelper;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.dq.helper.PropertyHelper;
import orgomg.cwm.foundation.softwaredeployment.DataProvider;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC scorreia class global comment. Detailled comment
 */
public class CWMPlugin extends Plugin {

    private static Logger log = Logger.getLogger(CWMPlugin.class);

    private static CWMPlugin self;

    public CWMPlugin() {
        super();
    }

    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        self = this;
        initPreferences(self);
    }

    /**
     * DOC xqliu Comment method "initPreferences".
     * 
     * @param cwm
     */
    private void initPreferences(CWMPlugin cwm) {
        IEclipsePreferences prefs = new DefaultScope().getNode(cwm.getBundle().getSymbolicName());
        prefs.putBoolean(PluginConstant.CONNECTION_TIMEOUT, false);
        prefs.putBoolean(PluginConstant.FILTER_TABLE_VIEW_COLUMN, true);

    }

    /**
     * DOC bZhou Comment method "getDefault".
     * 
     * @return
     */
    public static CWMPlugin getDefault() {
        return self;
    }

    /**
     * DOC bZhou Comment method "addConnetionAliasToSQLPlugin".
     * 
     * @param dataproviders
     */
    public void addConnetionAliasToSQLPlugin(ModelElement... dataproviders) {
        SQLExplorerPlugin sqlPlugin = SQLExplorerPlugin.getDefault();
        AliasManager aliasManager = sqlPlugin.getAliasManager();
        DriverManager driverManager = sqlPlugin.getDriverModel();

        for (ModelElement dataProvider : dataproviders) {
            try {
                Connection connection = SwitchHelpers.CONNECTION_SWITCH.doSwitch(dataProvider);
                // MOD bug mzhao filter the other connections except database connection.
                if (connection != null && connection instanceof DatabaseConnection) {

                    Alias alias = new Alias(dataProvider.getName());

                    // MOD xqliu 2010-08-06 bug 14593
                    // String clearTextUser = ConnectionUtils.getUsername(connection);
                    // String user = "".equals(clearTextUser) ? "root" : clearTextUser; //$NON-NLS-1$ //$NON-NLS-2$
                    String user = JavaSqlFactory.getUsernameDefault(connection);
                    // MOD gdbu 2011-3-17 bug 19539
                    String password = JavaSqlFactory.getPassword(connection);
                    // ~19539
                    // ~ 14593

                    // password should not be null
                    password = password == null ? "" : password; //$NON-NLS-1$

                    // MOD scorreia 2010-07-24 set empty string instead of null password so that database xml file is
                    // serialized correctly.
                    assert password != null;

                    // password should not null when serialized
                    password = password != null ? password : ""; //$NON-NLS-1$

                    String url = JavaSqlFactory.getURL(connection);

                    User previousUser = new User(user, password);
                    alias.setDefaultUser(previousUser);

                    alias.setAutoLogon(false);
                    alias.setConnectAtStartup(true);
                    alias.setUrl(url);

                    ManagedDriver manDr = driverManager.getDriver(EDriverName.getId(JavaSqlFactory.getDriverClass(connection)));

                    if (manDr != null) {
                        addJars(connection, manDr);

                        alias.setDriver(manDr);
                    }

                    if (!aliasManager.contains(alias) && alias.getName() != null) {
                        aliasManager.addAlias(alias);
                        // Add yyi 2010-09-15 14549: hide connections in SQL Explorer when a connection is moved to the
                        // trash bin
                        sqlPlugin.getPropertyFile().put(alias, PropertyHelper.getPropertyFile(dataProvider));
                    }
                }
            } catch (Exception e) { // MOD scorreia 2010-07-24 catch all exceptions
                log.error(e, e);
            }
        }

        try {
            aliasManager.saveAliases();
            driverManager.saveDrivers();
        } catch (Exception e) { // MOD scorreia 2010-07-24 catch all exceptions
            log.error(e, e);
        }
        aliasManager.modelChanged();
    }

    private void addJars(Connection connection, ManagedDriver manDr) {
        DatabaseConnection dbConnnection = (DatabaseConnection) connection;
        String driverJarPath = dbConnnection.getDriverJarPath();

        if (ConnectionHelper.isJDBC(dbConnnection) && driverJarPath != null) {
            String[] pathArray = driverJarPath.split(";");
            for (String path : pathArray) {
                path = new Path(path).toPortableString();
                if (!manDr.getJars().contains(path)) {
                    manDr.getJars().add(path);
                }
            }
        }
    }

    /**
     * DOC bZhou Comment method "removeAliasInSQLExplorer".
     * 
     * @param dataproviders
     */
    public void removeAliasInSQLExplorer(DataProvider... dataproviders) {
        SQLExplorerPlugin sqlPlugin = SQLExplorerPlugin.getDefault();
        AliasManager aliasManager = sqlPlugin.getAliasManager();

        for (DataProvider dataProvider : dataproviders) {
            try {
                aliasManager.loadAliases();
                String aliasName = dataProvider.getName();
                Alias alias = aliasManager.getAlias(aliasName);
                if (alias != null) {
                    aliasManager.removeAlias(aliasName);
                    aliasManager.saveAliases();
                }
            } catch (Exception e) {
                log.error(e, e);
            }
        }
        aliasManager.modelChanged();
    }

    /**
     * 
     * DOC klliu Comment method "updateAliasInSQLExplorer".update SQL Exploer ConnectionNode's name before saving the
     * updated name.
     * 
     * @param oldDataproviderName
     * @param newDataproviderName
     */
    public void updateAliasInSQLExplorer(String oldDataproviderName, String newDataproviderName) {
        SQLExplorerPlugin sqlPlugin = SQLExplorerPlugin.getDefault();
        AliasManager aliasManager = sqlPlugin.getAliasManager();
        try {
            aliasManager.loadAliases();
            Alias alias = aliasManager.getAlias(oldDataproviderName);
            if (alias != null) {
                alias.setName(newDataproviderName);
            }
            aliasManager.saveAliases();
        } catch (Exception e) {
            log.error(e, e);
        }
        aliasManager.modelChanged();
    }

}
