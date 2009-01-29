// ============================================================================
//
// Copyright (C) 2006-2007 Talend Inc. - www.talend.com
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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.talend.dataprofiler.core.manager.DQStructureManager;
import org.talend.dataprofiler.core.migration.AbstractMigrationTask;

/**
 * DOC bzhou class global comment. Detailled comment
 */
public class CopyDefineFileTask extends AbstractMigrationTask {

    private static final String TALEND_DEFINITION_FILENAME = ".Talend.definition";

    private static final String TALEND_DEFINITION_BAK_FILENAME = ".Talend.definition.bak";

    public boolean execute() {
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        IProject project = root.getProject(DQStructureManager.LIBRARIES);

        IFile file = project.getFile(TALEND_DEFINITION_FILENAME);
        IFile bakfile = project.getFile(TALEND_DEFINITION_BAK_FILENAME);

        if (file.exists()) {
            try {

                if (bakfile.exists()) {
                    bakfile.delete(true, null);
                }

                file.copy(bakfile.getFullPath(), true, null);

                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    public Date getOrder() {
        Calendar calender = Calendar.getInstance();
        calender.set(2009, 1, 29);
        return calender.getTime();
    }

}
