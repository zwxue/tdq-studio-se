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
package org.talend.dataprofiler.core;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.talend.commons.utils.io.FilesUtils;
import org.talend.core.model.general.ModuleNeeded;
import org.talend.core.model.general.ModuleToInstall;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dq.CWMPlugin;
import org.talend.dq.helper.SqlExplorerUtils;
import org.talend.librariesmanager.utils.RemoteModulesHelper;
import org.talend.updates.runtime.engine.factory.AbstractExtraUpdatesFactory;
import org.talend.updates.runtime.model.ExtraFeature;
import org.talend.updates.runtime.model.StatusException;
import org.talend.updates.runtime.model.TalendWebServiceUpdateExtraFeature;

public class DQRequiredMissingJarsExtraUpdatesFactory extends AbstractExtraUpdatesFactory {

    protected static Logger log = Logger.getLogger(DQRequiredMissingJarsExtraUpdatesFactory.class);

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.updates.runtime.engine.factory.AbstractExtraUpdatesFactory#retrieveUninstalledExtraFeatures(org.eclipse
     * .core.runtime.IProgressMonitor, java.util.Set)
     */
    @Override
    public void retrieveUninstalledExtraFeatures(IProgressMonitor monitor, Set<ExtraFeature> uninstalledExtraFeatures)
            throws Exception {
        if (org.talend.commons.utils.platform.PluginChecker.isOnlyTopLoaded()) {
            String pathToStore = Platform.getInstallLocation().getURL().getFile() + "plugins"; //$NON-NLS-1$
            File sqlexplorerJarfile = new File(pathToStore, SqlExplorerUtils.JAR_FILE_NAME);
            if (sqlexplorerJarfile.exists()) {
                return;
            } else {
                SubMonitor mainSubMonitor = SubMonitor.convert(monitor, 2);

                String context = "context:net.sourceforge.sqlexplorer"; //$NON-NLS-1$
                String moduleName = SqlExplorerUtils.JAR_FILE_NAME;
                String informationMsg = "DQ need the plugin: net.sourceforge.sqlexplorer"; //$NON-NLS-1$
                boolean required = true;
                ModuleNeeded modelNeeded = new ModuleNeeded(context, moduleName, informationMsg, required);
                List<ModuleNeeded> allUninstalledModules = new ArrayList<ModuleNeeded>();
                allUninstalledModules.add(modelNeeded);
                if (monitor.isCanceled()) {
                    return;
                }// else keep going fetch missing jar information from remote web site.
                ArrayList<ModuleToInstall> modulesRequiredToBeInstalled = new ArrayList<ModuleToInstall>();
                IRunnableWithProgress notInstalledModulesRunnable = RemoteModulesHelper.getInstance()
                        .getNotInstalledModulesRunnable(allUninstalledModules, modulesRequiredToBeInstalled);
                // jface because it adds graphical dependencies.
                if (notInstalledModulesRunnable != null) {// some data need to be fetched
                    try {
                        notInstalledModulesRunnable.run(mainSubMonitor.newChild(1));
                    } catch (InvocationTargetException e) {
                        log.error("failed to fetch missing third parties jars information", e); //$NON-NLS-1$
                        return;
                    } catch (InterruptedException e) {
                        log.error("failed to fetch missing third parties jars information", e); //$NON-NLS-1$
                        return;
                    }
                }// else all data already fetched so keep going
                if (mainSubMonitor.isCanceled()) {
                    return;
                }// else keep going.
                ArrayList<ModuleToInstall> modulesForAutomaticInstall = TalendWebServiceUpdateExtraFeature
                        .filterAllAutomaticInstallableModules(modulesRequiredToBeInstalled);
                addToSet(uninstalledExtraFeatures, new TalendWebServiceUpdateExtraFeature(modulesForAutomaticInstall,
                        DefaultMessagesImpl.getString("DownloadSqlexplorerPluginJarFactory.name"), DefaultMessagesImpl //$NON-NLS-1$
                                .getString("DownloadSqlexplorerPluginJarFactory.description"), true)); //$NON-NLS-1$
            }
        } else {
            // if it is not TOP loaded only, need not to do anyting
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.updates.runtime.engine.factory.AbstractExtraUpdatesFactory#afterInstall()
     */
    @Override
    public void afterInstall() throws StatusException {
        // move the jar to plugins folder
        try {
            File jarFile = null;
            List<File> jarFiles = FilesUtils.getJarFilesFromFolder(new File(CWMPlugin.getDefault().getLibrariesPath()),
                    SqlExplorerUtils.JAR_FILE_NAME);
            if (jarFiles.size() > 0) {
                jarFile = jarFiles.get(0);
            }
            if (jarFile != null) {
                String pathToStore = Platform.getInstallLocation().getURL().getFile() + "plugins"; //$NON-NLS-1$
                File movedfile = new File(pathToStore, SqlExplorerUtils.JAR_FILE_NAME);
                if (!movedfile.exists()) {
                    File target = new File(StringUtils.trimToEmpty(pathToStore));
                    if (!target.exists()) {
                        target.mkdirs();
                    }
                    FilesUtils.copyFile(jarFile, movedfile);
                }
            }
        } catch (MalformedURLException e) {
            log.error(e);
        } catch (IOException e) {
            log.error(e);
        }
    }

}
