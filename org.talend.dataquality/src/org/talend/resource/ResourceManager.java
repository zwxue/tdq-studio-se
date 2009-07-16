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
package org.talend.resource;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.talend.commons.bridge.ReponsitoryContextBridge;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public final class ResourceManager {

    public static final String DATA_PROFILING_FOLDER_NAME = "TDQ_Data Profiling";

    public static final String LIBRARIES_FOLDER_NAME = "TDQ_Libraries";

    public static final String METADATA_FOLDER_NAME = "TDQ_Metadata";

    public static final String REPORTING_DB_FOLDER_NAME = "TDQ_reporting_db";

    private ResourceManager() {
    }

    /**
     * DOC bZhou Comment method "getRootProject".
     * 
     * @return
     */
    public static IProject getRootProject() {

        return ReponsitoryContextBridge.getRootProject();
    }

    /**
     * DOC bZhou Comment method "getRootProjectName".
     * 
     * @return
     */
    public static String getRootProjectName() {
        return ReponsitoryContextBridge.getProjectName();
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
        // MOD xqliu 2009-07-15 bug 7820
        return ResourcesPlugin.getWorkspace().getRoot().getFolder(new Path(REPORTING_DB_FOLDER_NAME));
    }
}
