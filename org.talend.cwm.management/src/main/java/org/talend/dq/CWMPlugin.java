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
import net.sourceforge.sqlexplorer.dbproduct.ManagedDriver;
import net.sourceforge.sqlexplorer.dbproduct.User;
import net.sourceforge.sqlexplorer.plugin.SQLExplorerPlugin;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.osgi.framework.BundleContext;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.metadata.builder.database.JavaSqlFactory;
import org.talend.core.model.metadata.builder.database.PluginConstant;
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

        for (ModelElement dataProvider : dataproviders) {
            try {
                Connection connection = SwitchHelpers.CONNECTION_SWITCH.doSwitch(dataProvider);
                if (connection != null) {

                    Alias alias = new Alias(dataProvider.getName());

                    // MOD xqliu 2010-08-06 bug 14593
                    // String clearTextUser = ConnectionUtils.getUsername(connection);
                    // String user = "".equals(clearTextUser) ? "root" : clearTextUser; //$NON-NLS-1$ //$NON-NLS-2$
                    String user = JavaSqlFactory.getUsernameDefault(connection);
                    // MOD gdbu 2011-3-17 bug 19539
                    String password = JavaSqlFactory.getPassword(connection);
                    // ~19539
                    // ~ 14593

                    // MOD scorreia 2010-07-24 set empty string instead of null password so that database xml file is
                    // serialized correctly.
                    assert password != null;

                    String url = JavaSqlFactory.getURL(connection);

                    User previousUser = new User(user, password);
                    alias.setDefaultUser(previousUser);

                    alias.setAutoLogon(false);
                    alias.setConnectAtStartup(true);
                    alias.setUrl(url);

                    ManagedDriver manDr = sqlPlugin.getDriverModel().getDriver(
                            EDriverName.getId(JavaSqlFactory.getDriverClass(connection)));

                    if (manDr != null) {
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
        } catch (Exception e) { // MOD scorreia 2010-07-24 catch all exceptions
            log.error(e, e);
        }
        aliasManager.modelChanged();
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
                String aliasName = dataProvider.getName();
                Alias alias = aliasManager.getAlias(aliasName);

                if (alias != null) {
                    aliasManager.removeAlias(aliasName);
                }
            } catch (Exception e) {
                log.error(e, e);
            }
        }
    }
}
