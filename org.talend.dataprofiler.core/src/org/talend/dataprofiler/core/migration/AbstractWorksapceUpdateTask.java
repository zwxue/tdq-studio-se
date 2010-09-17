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
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.talend.resource.EResourceConstant;
import org.talend.resource.ResourceManager;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public abstract class AbstractWorksapceUpdateTask extends AWorkspaceTask {

    public static final String OLD_MEATADATA_FOLDER_NAME = "TDQ_Metadata";

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
            if (constant == EResourceConstant.METADATA && !isWorksapcePath()) {
                folderList.add(workspacePath.append(OLD_MEATADATA_FOLDER_NAME).toFile());
            } else {
                folderList.add(workspacePath.append(constant.getPath()).toFile());
            }
        }

        return folderList;
    }
}
