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
package org.talend.dataprofiler.core;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.talend.dataprofiler.core.service.GlobalServiceRegister;
import org.talend.dataprofiler.core.service.IProjectAdapterService;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public final class ResourceManager {

    public static final String DEFAULT_PROJECT_NAME = "TOP_DEFAULT_PRJ";

    public static final String DATA_PROFILING_FOLDER_NAME = "TDQ_Data Profiling";

    public static final String LIBRARIES_FOLDER_NAME = "TDQ_Libraries";

    public static final String METADATA_FOLDER_NAME = "TDQ_Metadata";

    public static final String REPORTING_DB_FOLDER_NAME = "TDQ_reporting_db";

    private ResourceManager() {
    }

    public static IProject getRootProject() {

        IProject rootProject = null;
        try {
            IProjectAdapterService projectAdapter = (IProjectAdapterService) GlobalServiceRegister.getDefault().getService(
                    IProjectAdapterService.class);
            if (projectAdapter != null) {
                rootProject = projectAdapter.getRootProject();
            }
        } catch (RuntimeException e) {
            rootProject = ResourcesPlugin.getWorkspace().getRoot().getProject(DEFAULT_PROJECT_NAME);
        }

        return rootProject;
    }

    public static String getRootProjectName() {
        return getRootProject().exists() ? getRootProject().getName() : "./";
    }

    /**
     * DOC bZhou Comment method "getDataProfilingFolder".
     * 
     * @return
     */
    public static IFolder getDataProfilingFolder() {
        return getRootProject().getFolder(DATA_PROFILING_FOLDER_NAME);
    }

    /**
     * DOC bZhou Comment method "getLibrariesFolder".
     * 
     * @return
     */
    public static IFolder getLibrariesFolder() {
        return getRootProject().getFolder(LIBRARIES_FOLDER_NAME);
    }

    /**
     * DOC bZhou Comment method "getMetadataFolder".
     * 
     * @return
     */
    public static IFolder getMetadataFolder() {
        return getRootProject().getFolder(METADATA_FOLDER_NAME);
    }

    /**
     * DOC bZhou Comment method "getReportingDBFolder".
     * 
     * @return
     */
    public static IFolder getReportingDBFolder() {
        return getRootProject().getFolder(REPORTING_DB_FOLDER_NAME);
    }
}
