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

import java.util.Calendar;
import java.util.Date;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.talend.dataprofiler.core.exception.ExceptionHandler;
import org.talend.dataprofiler.core.migration.AbstractMigrationTask;
import org.talend.resource.ResourceManager;

/**
 * DOC xqliu class global comment. Detailled comment
 */
public class UpdateProjectPersistentPropertyTask extends AbstractMigrationTask {

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.migration.IWorkspaceMigrationTask#execute()
     */
    public boolean execute() {
        IFolder[] folders = { ResourceManager.getDataProfilingFolder(), ResourceManager.getLibrariesFolder(),
                ResourceManager.getMetadataFolder() };
        try {
            checkProjectPersistentProperty(folders);
        } catch (CoreException e) {
            ExceptionHandler.process(e);
        }
        return false;
    }

    /**
     * 
     * DOC xqliu Comment method "checkProjectPersistentProperty".
     * 
     * @param projects
     * @throws CoreException
     */
    private void checkProjectPersistentProperty(IFolder[] folders) throws CoreException {

        for (IFolder folder : folders) {

            // Object persistentProperty = TdqPropertieManager.getInstance().getFolderPropertyValue(folder,
            // DQStructureManager.PROJECT_TDQ_KEY);
            // if (folder != null
            //					&& (persistentProperty == null || persistentProperty.toString().trim().equals(""))) { //$NON-NLS-1$
            //                
            // TdqPropertieManager.getInstance().addFolderProperties(folder, DQStructureManager.PROJECT_TDQ_KEY,
            // DQStructureManager.PROJECT_TDQ_PROPERTY);
            //
            // }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.migration.IWorkspaceMigrationTask#getOrder()
     */
    public Date getOrder() {
        Calendar calender = Calendar.getInstance();
        calender.set(2009, 2, 13);
        return calender.getTime();
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.talend.dataprofiler.core.migration.IWorkspaceMigrationTask# getMigrationTaskType()
     */
    public MigrationTaskType getMigrationTaskType() {
        return MigrationTaskType.FILE;
    }
}
