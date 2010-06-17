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
import net.sourceforge.sqlexplorer.ExplorerException;
import net.sourceforge.sqlexplorer.dbproduct.Alias;
import net.sourceforge.sqlexplorer.dbproduct.AliasManager;
import net.sourceforge.sqlexplorer.dbproduct.ManagedDriver;
import net.sourceforge.sqlexplorer.dbproduct.User;
import net.sourceforge.sqlexplorer.plugin.SQLExplorerPlugin;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Preferences;
import org.osgi.framework.BundleContext;
import org.talend.cwm.helper.DataProviderHelper;
import org.talend.cwm.softwaredeployment.TdProviderConnection;
import org.talend.utils.sugars.TypedReturnCode;
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
                TypedReturnCode<TdProviderConnection> tdPc = DataProviderHelper.getTdProviderConnection(dataProvider);
                TdProviderConnection providerConnection = tdPc.getObject();

                Alias alias = new Alias(dataProvider.getName());

                String clearTextUser = DataProviderHelper.getUser(providerConnection);
                String user = "".equals(clearTextUser) ? "root" : clearTextUser; //$NON-NLS-1$ //$NON-NLS-2$
                String password = DataProviderHelper.getClearTextPassword(providerConnection);

                String url = providerConnection.getConnectionString();

                User previousUser = new User(user, password);
                alias.setDefaultUser(previousUser);

                alias.setAutoLogon(false);
                alias.setConnectAtStartup(true);
                alias.setUrl(url);
                ManagedDriver manDr = sqlPlugin.getDriverModel().getDriver(
                        EDriverName.getId(providerConnection.getDriverClassName()));
                if (manDr != null) {
                    alias.setDriver(manDr);
                }

                if (!aliasManager.contains(alias)) {
                    aliasManager.addAlias(alias);
                }

            } catch (ExplorerException e) {
                log.error(e, e);
            }
        }

        try {
            aliasManager.saveAliases();
        } catch (ExplorerException e) {
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
