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

import java.io.File;
import java.io.FileOutputStream;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.talend.commons.bridge.ReponsitoryContextBridge;
import org.talend.utils.sugars.ReturnCode;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public final class ResourceManager {

    private static Logger log = Logger.getLogger(ResourceManager.class);

    public static final String DATA_PROFILING_FOLDER_NAME = "TDQ_Data Profiling";

    public static final String LIBRARIES_FOLDER_NAME = "TDQ_Libraries";

    public static final String METADATA_FOLDER_NAME = "TDQ_Metadata";

    public static final String REPORTING_DB_FOLDER_NAME = "TDQ_reporting_db";

    public static final String DQ_RULES = "DQ Rules";

    public static final String ANALYSIS = "Analyses";

    private static final String RESOURCE_PROPERTIES_FILE = "ResourceProperties";

    private static ResourceManager instance = null;

    private Properties properties = new Properties();

    private ResourceManager() {
    }

    public static ResourceManager getInstance() {
        if (instance == null) {
            instance = new ResourceManager();
        }
        return instance;
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
        return ResourcesPlugin.getWorkspace().getRoot().getFolder(new Path(REPORTING_DB_FOLDER_NAME));
    }

    /**
     * DOC bZhou Comment method "getDQRuleFolder".
     * 
     * @return
     */
    public static IFolder getDQRuleFolder() {
        return getLibrariesFolder().getFolder(DQ_RULES);
    }

    /**
     * DOC bZhou Comment method "addResourceProperty".
     * 
     * @param key
     * @param resourcePath
     */
    public void addResourceProperty(String key, String resourcePath) {
        properties.put(key, resourcePath);
    }

    /**
     * DOC bZhou Comment method "storeResourceProperties".
     * 
     * @return
     */
    public ReturnCode storeResourceProperties() {
        ReturnCode rc = new ReturnCode();

        IFile file = getRootProject().getFile(RESOURCE_PROPERTIES_FILE);
        String filePath = file.getLocation().toOSString();

        try {
            FileOutputStream fos = new FileOutputStream(new File(filePath));
            properties.store(fos, null);
            rc.setOk(true);
        } catch (Exception e) {
            rc.setMessage(e.getMessage());
        }

        return rc;
    }
}
