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
package org.talend.dq;

import java.io.File;

import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.BundleContext;
import org.talend.core.language.ECodeLanguage;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.metadata.builder.connection.DatabaseConnection;
import org.talend.core.model.metadata.builder.database.PluginConstant;
import org.talend.dataprofiler.service.ISqlexplorerService;
import org.talend.dq.analysis.memory.AnalysisThreadMemoryChangeNotifier;
import org.talend.dq.helper.SqlExplorerUtils;
import org.talend.librariesmanager.prefs.LibrariesManagerUtils;
import orgomg.cwm.foundation.softwaredeployment.DataProvider;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC scorreia class global comment. Detailled comment
 */
public class CWMPlugin extends Plugin {

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

    @Override
    public void stop(BundleContext context) throws Exception {
        super.stop(context);
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
        SqlExplorerUtils.getDefault().addConnetionAliasToSQLPlugin(dataproviders);
    }

    /**
     * 
     * DOC qiongli Comment method "updateConnetionAliasByName".
     * 
     * @param connection
     * @param aliasName
     */
    public void updateConnetionAliasByName(Connection connection, String aliasName) {
        SqlExplorerUtils.getDefault().updateConnetionAliasByName(connection, aliasName);
    }

    /**
     * 
     * update ManagedDriver driver jars.
     * 
     * @param connection
     */
    public void loadDriverByLibManageSystem(DatabaseConnection connection) {
        SqlExplorerUtils.getDefault().loadDriverByLibManageSystem(connection);
    }

    /**
     * DOC bZhou Comment method "removeAliasInSQLExplorer".
     * 
     * @param dataproviders
     */
    public void removeAliasInSQLExplorer(DataProvider... dataproviders) {
        SqlExplorerUtils.getDefault().removeAliasInSQLExplorer(dataproviders);
    }

    /**
     * 
     * when you start TOP ,the 'lib/java' dosen't exist,should create it.
     */
    public void createLibFolderIfNotExist() {
        String installLocation = getLibrariesPath();
        File libFile = new File(installLocation);
        if (!libFile.exists()) {
            org.talend.utils.io.FilesUtils.createFoldersIfNotExists(installLocation, false);
        }
    }

    public String getLibrariesPath() {
        String installLocation = LibrariesManagerUtils.getLibrariesPath(ECodeLanguage.JAVA);
        return installLocation;
    }

    public void bind(ISqlexplorerService service) {
        SqlExplorerUtils.getDefault().setSqlexplorerService(service);
    }

    public void unbind(ISqlexplorerService service) {
        SqlExplorerUtils.getDefault().setSqlexplorerService(null);
    }
}
