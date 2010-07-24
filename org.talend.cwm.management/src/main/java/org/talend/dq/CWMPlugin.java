// ============================================================================
//
// Copyright (C) 2006-2010 Talend Inc. - www.talend.com
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
import org.eclipse.core.runtime.Preferences;
import org.osgi.framework.BundleContext;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.cwm.helper.ConnectionHelper;
import org.talend.cwm.helper.SwitchHelpers;
import orgomg.cwm.foundation.softwaredeployment.DataProvider;

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
        Preferences prefs = cwm.getPluginPreferences();
        prefs.setDefault(PluginConstant.CONNECTION_TIMEOUT, false);
        prefs.setDefault(PluginConstant.FILTER_TABLE_VIEW_COLUMN, true);
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
    public void addConnetionAliasToSQLPlugin(DataProvider... dataproviders) {
        SQLExplorerPlugin sqlPlugin = SQLExplorerPlugin.getDefault();
        AliasManager aliasManager = sqlPlugin.getAliasManager();

        for (DataProvider dataProvider : dataproviders) {
            try {
                Connection connection = SwitchHelpers.CONNECTION_SWITCH.doSwitch(dataProvider);
                if (connection != null) {

                    Alias alias = new Alias(dataProvider.getName());

                    String clearTextUser = ConnectionHelper.getUsername(connection);
                    String user = "".equals(clearTextUser) ? "root" : clearTextUser; //$NON-NLS-1$ //$NON-NLS-2$
                    String password = ConnectionHelper.getPassword(connection);
                    // MOD scorreia 2010-07-24 set empty string instead of null password so that database xml file is
                    // serialized correctly.
                    assert password != null;

                    String url = ConnectionHelper.getURL(connection);

                    User previousUser = new User(user, password);
                    alias.setDefaultUser(previousUser);

                    alias.setAutoLogon(false);
                    alias.setConnectAtStartup(true);
                    alias.setUrl(url);

                    ManagedDriver manDr = sqlPlugin.getDriverModel().getDriver(
                            EDriverName.getId(ConnectionHelper.getDriverClass(connection)));

                    if (manDr != null) {
                        alias.setDriver(manDr);
                    }

                    if (!aliasManager.contains(alias)) {
                        aliasManager.addAlias(alias);
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
