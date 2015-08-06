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
package org.talend.dataprofiler.core;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.osgi.framework.Bundle;
import org.talend.commons.utils.io.FilesUtils;
import org.talend.core.model.general.ModuleNeeded;
import org.talend.core.model.general.ModuleToInstall;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dq.CWMPlugin;
import org.talend.dq.helper.SqlExplorerUtils;
import org.talend.librariesmanager.utils.RemoteModulesHelper;
import org.talend.updates.runtime.engine.factory.AbstractExtraUpdatesFactory;
import org.talend.updates.runtime.model.ExtraFeature;
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
        Bundle bundle = Platform.getBundle(SqlExplorerUtils.PLUGIN_NAME);
        if (bundle == null) {// if the sql explorer bundle is not installed then propose to download it.
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
                        log.error("failed to fetch missing third parties jars information for " + moduleName, e); //$NON-NLS-1$
                        return;
                    } catch (InterruptedException e) {
                        log.error("failed to fetch missing third parties jars information" + moduleName, e); //$NON-NLS-1$
                        return;
                    }
                }// else all data already fetched or already try and failed so keep going
                if (mainSubMonitor.isCanceled()) {
                    return;
                }// else keep going.
                ArrayList<ModuleToInstall> modulesForAutomaticInstall = TalendWebServiceUpdateExtraFeature
                        .filterAllAutomaticInstallableModules(modulesRequiredToBeInstalled);
                if (modulesForAutomaticInstall.isEmpty()) {// if could not find anything to dowload log and error and
                                                           // return nothing
                    log.error("failed to fetch missing third parties jars information for " + moduleName); //$NON-NLS-1$
                    return;
                }// else something to dowload to return the Extra feature to dowload.
                addToSet(uninstalledExtraFeatures, new TalendWebServiceUpdateExtraFeature(modulesForAutomaticInstall,
                        DefaultMessagesImpl.getString("DownloadSqlexplorerPluginJarFactory.name"), DefaultMessagesImpl //$NON-NLS-1$
                                .getString("DownloadSqlexplorerPluginJarFactory.description"), true) {

                    /*
                     * (non-Javadoc)
                     * 
                     * @see
                     * org.talend.updates.runtime.model.TalendWebServiceUpdateExtraFeature#install(org.eclipse.core.
                     * runtime.IProgressMonitor, java.util.List)
                     */
                    @Override
                    public IStatus install(IProgressMonitor progress, List<URI> allRepoUris) throws Exception {
                        IStatus installStatus = super.install(progress, allRepoUris);
                        // move the jar to plugins folder
                        if (installStatus.isOK()) {
                            try {
                                File jarFile = null;
                                List<File> jarFiles = FilesUtils.getJarFilesFromFolder(new File(CWMPlugin.getDefault()
                                        .getLibrariesPath()), SqlExplorerUtils.JAR_FILE_NAME);
                                if (jarFiles.size() > 0) {
                                    jarFile = jarFiles.get(0);
                                }
                                if (jarFile != null) {
                                    String pluginPath = Platform.getInstallLocation().getURL().getFile() + "plugins"; //$NON-NLS-1$
                                    File movedfile = new File(pluginPath, SqlExplorerUtils.JAR_FILE_NAME);
                                    if (!movedfile.exists()) {
                                        File target = new File(StringUtils.trimToEmpty(pluginPath));
                                        if (!target.exists()) {
                                            target.mkdirs();
                                        }
                                        FilesUtils.copyFile(jarFile, movedfile);
                                    }
                                }
                            } catch (MalformedURLException e) {
                                MultiStatus multiStatus = new MultiStatus(CorePlugin.PLUGIN_ID, IStatus.ERROR, e.getMessage(), e);
                                multiStatus.add(installStatus);
                                return multiStatus;
                            } catch (IOException e) {
                                MultiStatus multiStatus = new MultiStatus(CorePlugin.PLUGIN_ID, IStatus.ERROR, e.getMessage(), e);
                                multiStatus.add(installStatus);
                                return multiStatus;
                            }
                        }// else install not ok so return the error status.
                        return installStatus;
                    }
                });
            }
        } else {
            // if it is not TOP loaded only, need not to do anyting
        }
    }

}
