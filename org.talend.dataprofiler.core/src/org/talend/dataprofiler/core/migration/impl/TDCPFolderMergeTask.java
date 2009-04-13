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

import java.lang.reflect.InvocationTargetException;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
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
            IProject rootProject = DQStructureManager.getInstance().createNewProject(
                    org.talend.dataquality.PluginConstant.getRootProjectName());
            // Delete first if dq structure has already constructed.
            rootProject.delete(true, true, new NullProgressMonitor());
            // create brandly new one.
            rootProject = DQStructureManager.getInstance().createNewProject(
                    org.talend.dataquality.PluginConstant.getRootProjectName());
            // Copy "top level" folders already as projects in TOP/TDQ into this
            // project.
            IResource[] resources = ResourcesPlugin.getWorkspace().getRoot().members();
            if (resources != null && resources.length > 0) {
                for (IResource resource : resources) {
                    if (resource instanceof IProject
                            && !resource.getName().equals(org.talend.dataquality.PluginConstant.getRootProjectName())) {
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

        } catch (InvocationTargetException e) {
            logger.error(e, e);
        } catch (InterruptedException e) {
            logger.error(e, e);
        } catch (CoreException e) {
            logger.error(e, e);
        }
        return false;
    }

    public Date getOrder() {
        Calendar calender = Calendar.getInstance();
        calender.set(1949, 10, 1);
        return calender.getTime();
    }

}
