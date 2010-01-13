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

import java.util.Date;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.talend.dataprofiler.core.manager.DQStructureManager;
import org.talend.dataprofiler.core.migration.AWorkspaceTask;
import org.talend.resource.EResourceConstant;
import org.talend.resource.ResourceManager;
import org.talend.resource.ResourceService;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public class MDMConnectionTask extends AWorkspaceTask {

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.migration.IWorkspaceMigrationTask#getMigrationTaskType()
     */
    public MigrationTaskType getMigrationTaskType() {
        return MigrationTaskType.STUCTRUE;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.migration.IMigrationTask#execute()
     */
    public boolean execute() {
        ResourceService.refreshStructure();
        IFolder mdmConnectionFolder = ResourceManager.getMDMConnectionFolder();
        if (!mdmConnectionFolder.exists()) {
            IFolder metadataFolder = ResourceManager.getMetadataFolder();
            try {
                DQStructureManager.getInstance().createNewFolder(metadataFolder, EResourceConstant.MDM_CONNECTIONS);
            } catch (CoreException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.migration.IMigrationTask#getOrder()
     */
    public Date getOrder() {
        return createDate(2010, 1, 13);
    }

}
