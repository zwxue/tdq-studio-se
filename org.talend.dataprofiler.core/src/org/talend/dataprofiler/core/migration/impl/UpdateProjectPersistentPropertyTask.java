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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.talend.dataprofiler.core.exception.ExceptionHandler;
import org.talend.dataprofiler.core.manager.DQStructureManager;
import org.talend.dataprofiler.core.migration.AbstractMigrationTask;

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
        String[] projects = { DQStructureManager.getDataProfiling(), DQStructureManager.getLibraries(), DQStructureManager.getMetaData() };
        try {
            checkProjectPersistentProperty(projects);
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
    private void checkProjectPersistentProperty(String[] projects) throws CoreException {
        IProject prjct;
        for (String project : projects) {
            prjct = ResourcesPlugin.getWorkspace().getRoot().getProject(project);
            if (prjct != null && prjct.getPersistentProperty(DQStructureManager.PROJECT_TDQ_KEY) == null) {
                prjct.setPersistentProperty(DQStructureManager.PROJECT_TDQ_KEY, DQStructureManager.PROJECT_TDQ_PROPERTY);
            }
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

}
