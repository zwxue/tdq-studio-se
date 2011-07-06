// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.migration;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IPath;
import org.talend.dataprofiler.datamart.migration.AWorkspaceTask;
import org.talend.resource.EResourceConstant;
import org.talend.resource.ResourceManager;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public abstract class AbstractWorksapceUpdateTask extends AWorkspaceTask {

    private static Logger log = Logger.getLogger(AbstractWorksapceUpdateTask.class);

    public static final String OLD_MEATADATA_FOLDER_NAME = "TDQ_Metadata"; //$NON-NLS-1$

    public static final String OLD_LIBRARIES_FOLDER_NAME = "TDQ_Libraries"; //$NON-NLS-1$

    public static final String OLD_PROFILING_FOLDER_NAME = "TDQ_Data Profiling"; //$NON-NLS-1$

    private IPath workspacePath = ResourceManager.getRootProject().getLocation();

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.migration.IMigrationTask#valid()
     */
    public boolean valid() {
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.migration.IMigrationTask#persist()
     */
    public boolean persist() throws Exception {
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.migration.IMigrationTask#clear()
     */
    public boolean clear() {
        return true;
    }

    public void setWorkspacePath(IPath workspacePath) {
        this.workspacePath = workspacePath;
    }

    public IPath getWorkspacePath() {
        return workspacePath;
    }

    /**
     * DOC bZhou Comment method "isWorksapcePath".
     * 
     * @return
     */
    protected boolean isWorksapcePath() {
        return workspacePath.equals(ResourceManager.getRootProject().getLocation());
    }

    /**
     * DOC bZhou Comment method "getTopFolderList".
     * 
     * @return
     */
    protected List<File> getTopFolderList() {
        List<File> folderList = new ArrayList<File>();

        for (EResourceConstant constant : EResourceConstant.getTopConstants()) {
            folderList.add(workspacePath.append(constant.getPath()).toFile());
        }

        return folderList;
    }

    /**
     * DOC bZhou Comment method "getOldTopFolderList".
     * 
     * @return
     */
    protected List<File> getOldTopFolderList() {
        List<File> folderList = new ArrayList<File>();

        folderList.add(workspacePath.append(OLD_MEATADATA_FOLDER_NAME).toFile());
        folderList.add(workspacePath.append(OLD_LIBRARIES_FOLDER_NAME).toFile());
        folderList.add(workspacePath.append(OLD_PROFILING_FOLDER_NAME).toFile());

        return folderList;
    }

    /**
     * DOC sgandon Comment method "getAllFilesFromFolder".
     * 
     * @param sampleFolder
     * @param arrayList
     * @param filenameFilter
     */
    protected void getAllFilesFromFolder(File sampleFolder, ArrayList<File> fileList, FilenameFilter filenameFilter) {
        File[] folderFiles = sampleFolder.listFiles(filenameFilter);
        Collections.addAll(fileList, folderFiles);
        File[] allFolders = sampleFolder.listFiles(new FileFilter() {

            public boolean accept(File arg0) {
                return arg0.isDirectory();
            }
        });
        for (File folder : allFolders) {
            getAllFilesFromFolder(folder, fileList, filenameFilter);
        }
    }

}
