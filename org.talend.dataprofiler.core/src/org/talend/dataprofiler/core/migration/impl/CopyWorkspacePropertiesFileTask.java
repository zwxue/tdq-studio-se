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

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Plugin;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.manager.DQStructureManager;
import org.talend.dataprofiler.core.migration.AbstractMigrationTask;
import org.talend.resource.ResourceManager;
import org.talend.resource.xml.TdqPropertieManager;

/**
 * 
 * DOC mzhao class global comment. Detailled comment
 */
public class CopyWorkspacePropertiesFileTask extends AbstractMigrationTask {

    private static Logger log = Logger.getLogger(CopyWorkspacePropertiesFileTask.class);
    public static final String CONFIG_PATH = "/configure";//$NON-NLS-1$
    public CopyWorkspacePropertiesFileTask() {
    }

    public CopyWorkspacePropertiesFileTask(String id, String name, String version) {
        super(id, name, version);
    }

    public boolean execute() {
        // Copy properties configuration file.
        Plugin plugin = CorePlugin.getDefault();
        IProject rootProject = ResourceManager.getRootProject();

        // MOD scorreia - 2009-10-07
        // a file may already be created by another migration task. The changes will be lost! Either
        // this migration should be done first or it should not overwrite the other migration task
        // at this time, I have reordered the migration tasks so that this task is executed before the other that create
        // or modify the TdqProperties.xml file.

        DQStructureManager.copyConfigFiles(rootProject, plugin);
        // Reload properties.
        TdqPropertieManager.reload();
        return Boolean.TRUE;
    }


    public MigrationTaskType getMigrationTaskType() {
        return MigrationTaskType.FILE;
    }

    public Date getOrder() {
        Calendar calender = Calendar.getInstance();
        calender.set(2009, 1, 1);
        return calender.getTime();
    }

}
