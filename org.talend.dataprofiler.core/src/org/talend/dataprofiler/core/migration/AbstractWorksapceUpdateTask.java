// ============================================================================
//
// Copyright (C) 2006-2010 Talend Inc. - www.talend.com
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dq.writer.EMFSharedResources;
import org.talend.resource.EResourceConstant;
import org.talend.resource.ResourceManager;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public abstract class AbstractWorksapceUpdateTask extends AWorkspaceTask {

    private static Logger log = Logger.getLogger(AbstractWorksapceUpdateTask.class);

    public static final String OLD_MEATADATA_FOLDER_NAME = "TDQ_Metadata";

    public static final String OLD_LIBRARIES_FOLDER_NAME = "TDQ_Libraries";

    public static final String OLD_PROFILING_FOLDER_NAME = "TDQ_Data Profiling";

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

    protected Collection<Analysis> searchAllAnalysis(IFolder folder) {
        Collection<Analysis> analyses = new ArrayList<Analysis>();

        try {
            for (IResource resource : folder.members()) {
                if (resource.getType() == IResource.FOLDER) {
                    analyses.addAll(searchAllAnalysis(folder.getFolder(resource.getName())));
                    continue;
                }
                IFile file = (IFile) resource;
                if (file.getFileExtension().equals(FactoriesUtil.ANA)) {
                    URI uri = URI.createPlatformResourceURI(file.getFullPath().toString(), false);
                    Resource eResource = EMFSharedResources.getInstance().getResource(uri, true);
                    analyses.add((Analysis) eResource.getContents().get(0));
                }
            }
        } catch (Exception e) {
            log.error(e);
        }
        return analyses;
    }

}
