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
package org.talend.dq;

import java.io.File;
import java.util.Collection;
import java.util.List;

import net.sourceforge.sqlexplorer.EDriverName;
import net.sourceforge.sqlexplorer.dbproduct.Alias;
import net.sourceforge.sqlexplorer.dbproduct.AliasManager;
import net.sourceforge.sqlexplorer.dbproduct.DriverManager;
import net.sourceforge.sqlexplorer.dbproduct.ManagedDriver;
import net.sourceforge.sqlexplorer.dbproduct.User;
import net.sourceforge.sqlexplorer.plugin.SQLExplorerPlugin;
import net.sourceforge.sqlexplorer.plugin.views.DatabaseStructureView;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.BundleContext;
import org.talend.core.language.ECodeLanguage;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.metadata.builder.connection.DatabaseConnection;
import org.talend.core.model.metadata.builder.database.JavaSqlFactory;
import org.talend.core.model.metadata.builder.database.PluginConstant;
import org.talend.core.model.metadata.builder.util.MetadataConnectionUtils;
import org.talend.cwm.helper.ConnectionHelper;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.dq.analysis.memory.AnalysisThreadMemoryChangeNotifier;
import org.talend.dq.helper.PropertyHelper;
import org.talend.librariesmanager.prefs.LibrariesManagerUtils;
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
        PlatformUI.getPreferenceStore().setDefault(AnalysisThreadMemoryChangeNotifier.ANALYSIS_AUTOMATIC_MEMORY_CONTROL, false);
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

        List<String> tdqSupportDBType = MetadataConnectionUtils.getTDQSupportDBTemplate();
        // if all dataproviders are not supported on DQ side,don't save files SQLAliases.xml and
        // SQLDrivers.xml.Otherwise,save it.
        Boolean isNeedSave = Boolean.FALSE;
        for (ModelElement dataProvider : dataproviders) {
            try {
                Connection connection = SwitchHelpers.CONNECTION_SWITCH.doSwitch(dataProvider);
                // MOD bug mzhao filter the other connections except database connection.
                if (connection != null && connection instanceof DatabaseConnection) {

                    // TDQ-8379 do nothing if the database type isn't supproted on DQ side.
                    String databaseType = ((DatabaseConnection) connection).getDatabaseType();
                    if (!tdqSupportDBType.contains(databaseType)) {
                        continue;
                    }
                    // only new Alias when it is not in aliasManager
                    Alias alias = aliasManager.getAlias(dataProvider.getName());
                    if (alias == null) {
                        alias = new Alias(dataProvider.getName());

                        String user = JavaSqlFactory.getUsername(connection);
                        // MOD gdbu 2011-3-17 bug 19539
                        String password = JavaSqlFactory.getPassword(connection);
                        // ~19539

                        // user should not be null
                        user = user == null ? "" : user; //$NON-NLS-1$
                        // password should not be null
                        password = password == null ? "" : password; //$NON-NLS-1$

                        // MOD scorreia 2010-07-24 set empty string instead of null password so that database xml file
                        // is serialized correctly.
                        assert user != null;
                        assert password != null;

                        String url = JavaSqlFactory.getURL(connection);

                        User previousUser = new User(user, password);
                        alias.setDefaultUser(previousUser);

                        alias.setAutoLogon(false);
                        alias.setConnectAtStartup(true);
                        alias.setUrl(url);

                        ManagedDriver manDr = driverManager
                                .getDriver(EDriverName.getId(JavaSqlFactory.getDriverClass(connection)));

                        if (manDr != null) {
                            addJars(connection, manDr);

                            alias.setDriver(manDr);
                        }
                        isNeedSave = true;
                    }
                    if (!aliasManager.contains(alias) && alias.getName() != null) {
                        aliasManager.addAlias(alias);
                    }

                    // Add yyi 2010-09-15 14549: hide connections in SQL Explorer when a connection is moved to the
                    // trash bin.
                    // MOD Qiongli TDQ-6166 just put once for every Alias
                    if (sqlPlugin.getPropertyFile().get(alias) == null) {
                        sqlPlugin.getPropertyFile().put(alias, PropertyHelper.getPropertyFile(dataProvider));
                    }

                }
            } catch (Exception e) { // MOD scorreia 2010-07-24 catch all exceptions
                log.error(e, e);
            }
        }

        if (isNeedSave) {
            try {
                aliasManager.saveAliases();
                driverManager.saveDrivers();
            } catch (Exception e) { // MOD scorreia 2010-07-24 catch all exceptions
                log.error(e, e);
            }
            aliasManager.modelChanged();
        }
    }

    /**
     * 
     * DOC qiongli Comment method "updateConnetionAliasByName".
     * 
     * @param connection
     * @param aliasName
     */
    public void updateConnetionAliasByName(Connection connection, String aliasName) {
        if (connection == null || aliasName == null) {
            return;
        }
        SQLExplorerPlugin sqlPlugin = SQLExplorerPlugin.getDefault();
        // if the ctabItem is open,close it.
        IWorkbenchPage page = sqlPlugin.getActivePage();
        if (page != null) {
            DatabaseStructureView view = (DatabaseStructureView) page.findView(DatabaseStructureView.class.getName());
            if (view != null) {
                view.closeCurrentCabItem(aliasName);
            }
        }

        AliasManager aliasManager = sqlPlugin.getAliasManager();
        Alias alias = aliasManager.getAlias(aliasName);
        if (alias != null) {
            try {
                aliasManager.removeAlias(aliasName);
                aliasManager.saveAliases();
                aliasManager.modelChanged();
                addConnetionAliasToSQLPlugin(connection);

            } catch (Exception e) {
                log.error(e, e);
            }

        }

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

        DatabaseStructureView dsView = sqlPlugin.getDatabaseStructureView();
        // MOD qiongli 2012-11-12 TDQ-6166,only load aliases from file when AliasManager'Aliases is empty.should remove
        // alias from propertyFile map at the same time.
        try {
            Collection<Alias> aliases = aliasManager.getAliases();
            if (aliases.isEmpty()) {
                aliasManager.loadAliases();
            }
            for (DataProvider dataProvider : dataproviders) {
                String aliasName = dataProvider.getName();
                if (null == aliasName) {
                    continue;
                }
                Alias alias = aliasManager.getAlias(aliasName);
                if (alias != null) {
                    sqlPlugin.getPropertyFile().remove(alias);
                    aliasManager.removeAlias(aliasName);
                    aliasManager.saveAliases();
                }
                // if the ctabItem is open,close it.
                if (dsView != null) {
                    dsView.closeCurrentCabItem(aliasName);
                }
            }
        } catch (Exception e) {
            log.error(e, e);
        }
        aliasManager.modelChanged();
    }

    /**
     * 
     * when you start TOP ,the 'lib/java' dosen't exist,should create it.
     */
    public void createLibFolderIfNotExist() {
        String installLocation = LibrariesManagerUtils.getLibrariesPath(ECodeLanguage.JAVA);
        File libFile = new File(installLocation);
        if (!libFile.exists()) {
            org.talend.utils.io.FilesUtils.createFoldersIfNotExists(installLocation, false);
        }
    }

}
