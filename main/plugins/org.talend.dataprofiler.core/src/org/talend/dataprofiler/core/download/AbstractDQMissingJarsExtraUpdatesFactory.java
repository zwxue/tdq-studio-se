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
package org.talend.dataprofiler.core.download;

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
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dq.CWMPlugin;
import org.talend.librariesmanager.utils.RemoteModulesHelper;
import org.talend.updates.runtime.engine.factory.AbstractExtraUpdatesFactory;
import org.talend.updates.runtime.model.ExtraFeature;
import org.talend.updates.runtime.model.TalendWebServiceUpdateExtraFeature;

/**
 * For any jars needed to be downloaded for TOP, just extends this class, and implement the abstract methods.
 *
 */
public abstract class AbstractDQMissingJarsExtraUpdatesFactory extends AbstractExtraUpdatesFactory {

    protected static Logger log = Logger.getLogger(AbstractDQMissingJarsExtraUpdatesFactory.class);

    protected abstract String getJarFileName();

    protected abstract String getPluginName();

    @Override
    public void retrieveUninstalledExtraFeatures(IProgressMonitor monitor, Set<ExtraFeature> uninstalledExtraFeatures)
            throws Exception {
        Bundle bundle = Platform.getBundle(getPluginName());
        if (bundle == null) {// if the sql explorer bundle is not installed then propose to download it.
            String pathToStore = Platform.getInstallLocation().getURL().getFile() + "plugins"; //$NON-NLS-1$
            File sqlexplorerJarfile = new File(pathToStore, getJarFileName());
            if (sqlexplorerJarfile.exists()) {
                return;
            } else {
                SubMonitor mainSubMonitor = SubMonitor.convert(monitor, 2);

                // add all needed models into the allUninstalledModules
                List<ModuleNeeded> allUninstalledModules = getAllUninstalledModules();

                if (monitor.isCanceled()) {
                    return;
                }// else keep going fetch missing jar information from remote web site.

                ArrayList<ModuleToInstall> modulesRequiredToBeInstalled = new ArrayList<ModuleToInstall>();
                IRunnableWithProgress notInstalledModulesRunnable = RemoteModulesHelper.getInstance()
                        .getNotInstalledModulesRunnable(allUninstalledModules, modulesRequiredToBeInstalled);
                // jface because it adds graphical dependencies.
                if (!runNotInstallModule(mainSubMonitor, notInstalledModulesRunnable)) {// some data need to be fetched
                    return;
                }// else all data already fetched or already try and failed so keep going
                if (mainSubMonitor.isCanceled()) {
                    return;
                }// else keep going.

                ArrayList<ModuleToInstall> modulesForAutomaticInstall = TalendWebServiceUpdateExtraFeature
                        .filterAllAutomaticInstallableModules(modulesRequiredToBeInstalled);
                if (modulesForAutomaticInstall.isEmpty()) {// if could not find anything to download log and error and
                                                           // return nothing
                    log.error("failed to fetch missing third parties jars information for " + getJarFileName()); //$NON-NLS-1$
                    return;
                }

                // else something to dowload to return the Extra feature to dowload.
                addToSet(
                        uninstalledExtraFeatures,
                        new TalendWebServiceUpdateExtraFeature(modulesForAutomaticInstall, DefaultMessagesImpl
                                .getString(getDownloadName()), DefaultMessagesImpl
                                .getString("DownloadSqlexplorerPluginJarFactory.description"), true) { //$NON-NLS-1$

                            @Override
                            public IStatus install(IProgressMonitor progress, List<URI> allRepoUris) throws Exception {
                                IStatus installStatus = super.install(progress, allRepoUris);
                                // move the jar to plugins folder
                                if (installStatus.isOK()) {
                                    try {
                                        moveJars();
                                    } catch (MalformedURLException e) {
                                        MultiStatus multiStatus = new MultiStatus(CorePlugin.PLUGIN_ID, IStatus.ERROR, e
                                                .getMessage(), e);
                                        multiStatus.add(installStatus);
                                        return multiStatus;
                                    } catch (IOException e) {
                                        MultiStatus multiStatus = new MultiStatus(CorePlugin.PLUGIN_ID, IStatus.ERROR, e
                                                .getMessage(), e);
                                        multiStatus.add(installStatus);
                                        return multiStatus;
                                    }
                                }// else install not ok so return the error status.
                                return installStatus;
                            }

                            private void moveJars() throws MalformedURLException, IOException {
                                File jarFile = null;
                                List<File> jarFiles = FilesUtils.getJarFilesFromFolder(new File(CWMPlugin.getDefault()
                                        .getLibrariesPath()), getJarFileName());
                                if (jarFiles.size() > 0) {
                                    jarFile = jarFiles.get(0);
                                }
                                if (jarFile != null) {
                                    String pluginPath = Platform.getInstallLocation().getURL().getFile() + "plugins"; //$NON-NLS-1$
                                    File movedfile = new File(pluginPath, getJarFileName());
                                    if (!movedfile.exists()) {
                                        File target = new File(StringUtils.trimToEmpty(pluginPath));
                                        if (!target.exists()) {
                                            target.mkdirs();
                                        }
                                        FilesUtils.copyFile(jarFile, movedfile);
                                    }
                                }
                            }
                        });
            }
        } else {
            // if it is not TOP loaded only, need not to do anyting
        }
    }

    /**
     * DOC yyin Comment method "getDownloadName".
     * 
     * @return
     */
    abstract protected String getDownloadName();

    private boolean runNotInstallModule(SubMonitor mainSubMonitor, IRunnableWithProgress notInstalledModulesRunnable) {
        if (notInstalledModulesRunnable == null) {
            return false;
        }
        try {
            notInstalledModulesRunnable.run(mainSubMonitor.newChild(1));
            return true;
        } catch (InvocationTargetException e) {
            log.error("failed to fetch missing third parties jars information for " + getJarFileName(), e); //$NON-NLS-1$
            return false;
        } catch (InterruptedException e) {
            log.error("failed to fetch missing third parties jars information" + getJarFileName(), e); //$NON-NLS-1$
            return false;
        }
    }

    protected List<ModuleNeeded> getAllUninstalledModules() {
        ModuleNeeded modelNeeded = new ModuleNeeded(getContextName(), getJarFileName(), getInforMessage(), true);
        List<ModuleNeeded> allUninstalledModules = new ArrayList<ModuleNeeded>();
        allUninstalledModules.add(modelNeeded);
        return allUninstalledModules;
    }

    protected abstract String getContextName();

    protected abstract String getInforMessage();
}
