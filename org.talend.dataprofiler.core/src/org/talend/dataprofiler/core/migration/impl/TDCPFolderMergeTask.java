// ============================================================================
//
// Copyright (C) 2006-2009 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.migration.impl;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.commons.utils.StringUtils;
import org.talend.dataprofiler.core.ResourceManager;
import org.talend.dataprofiler.core.manager.DQStructureManager;
import org.talend.dataprofiler.core.migration.AbstractMigrationTask;

/**
 * 
 * DOC mzhao 2009-03-18 Migration task for merge tdq/top system to tos. All folders existing in tdq/tos before must all
 * be moved to one project, default project name is {@link org.talend.dataquality.PluginConstant#rootProjectName}
 */
public class TDCPFolderMergeTask extends AbstractMigrationTask {

    Logger logger = Logger.getLogger(TDCPFolderMergeTask.class);

    public TDCPFolderMergeTask() {
    }

    public boolean execute() {
        try {
            // Create one project.
            IProject rootProject = ResourceManager.getRootProject();
            if (!rootProject.exists()) {
                rootProject = DQStructureManager.getInstance().createNewProject(ResourceManager.DEFAULT_PROJECT_NAME);
            }

            // Copy "top level" folders already as projects in TOP/TDQ into this
            // project.
            IResource[] resources = ResourcesPlugin.getWorkspace().getRoot().members();

            if (resources != null && resources.length > 0) {
                for (IResource resource : resources) {
                    // copy three folders:
                    if (resource.getName().equals("Data Profiling") || resource.getName().equals("Libraries")
                            || resource.getName().equals("Metadata")) {
                        IPath destination = null;
                        IFolder prefixFolder = rootProject.getFolder(DQStructureManager.PREFIX_TDQ + resource.getName());
                        prefixFolder.create(IResource.FORCE, true, new NullProgressMonitor());
                        for (IResource rs : ((IProject) resource).members()) {
                            if (rs.getName().equals(".project")) {
                                continue;
                            }
                            destination = prefixFolder.getFolder(rs.getName()).getFullPath();
                            rs.copy(destination, IResource.FORCE, new NullProgressMonitor());
                        }
                        resource.delete(true, new NullProgressMonitor());
                    }
                }
            }
            // Reporting_db
            String pathName = ResourcesPlugin.getWorkspace().getRoot().getLocation().toOSString() + "/reporting_db/";
            File repFolder = new File(pathName);
            if (repFolder.exists()) {
                FileUtils.copyDirectory(repFolder, ResourceManager.getReportingDBFolder().getLocation().toFile());
                FileUtils.forceDelete(new File(pathName));
            }
            // ~MOD mzhao 2009-04-28, upgrade .prv,.ana,rep files.
            fileContentUpgrade(rootProject);
            // ~
        } catch (InvocationTargetException e) {
            logger.error(e, e);
        } catch (InterruptedException e) {
            logger.error(e, e);
        } catch (CoreException e) {
            logger.error(e, e);
        } catch (IOException e) {
            logger.error(e, e);
        } catch (Throwable e) {
            logger.error(e);
        }
        return false;
    }

    private void fileContentUpgrade(IProject rootProject) throws CoreException {
        String[] extensions = { FactoriesUtil.ANA, FactoriesUtil.PROV, FactoriesUtil.REP, "softwaredeployment" };
        boolean recursive = true;
        Collection<?> files = FileUtils.listFiles(new File(rootProject.getLocation().toOSString()), extensions, recursive);
        for (Iterator<?> iterator = files.iterator(); iterator.hasNext();) {
            File file = (File) iterator.next();
            if (file != null) {
                try {
                    String content = FileUtils.readFileToString(file);
                    content = StringUtils.replace(content, "/Metadata/", "/" + ResourceManager.METADATA_FOLDER_NAME + "/");
                    content = StringUtils.replace(content, "/Libraries/", "/" + ResourceManager.LIBRARIES_FOLDER_NAME + "/");
                    content = StringUtils.replace(content, "/resource/" + ResourceManager.LIBRARIES_FOLDER_NAME + "/",
                            "/resource/" + ResourceManager.getRootProjectName() + "/" + ResourceManager.LIBRARIES_FOLDER_NAME
                                    + "/");
                    content = StringUtils.replace(content, "/Data Profiling/", "/" + ResourceManager.DATA_PROFILING_FOLDER_NAME
                            + "/");

                    FileUtils.writeStringToFile(file, content);
                } catch (IOException e) {
                    logger.error(e);
                } catch (Throwable e) {
                    logger.error(e);
                }
            }
        }
    }

    public Date getOrder() {
        Calendar calender = Calendar.getInstance();
        calender.set(1949, 10, 1);
        return calender.getTime();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.migration.IWorkspaceMigrationTask#getMigrationTaskType()
     */
    public MigrationTaskType getMigrationTaskType() {
        return MigrationTaskType.STUCTRUE;
    }

}
