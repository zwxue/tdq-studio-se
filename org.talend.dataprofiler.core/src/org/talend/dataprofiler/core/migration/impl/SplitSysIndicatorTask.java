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
package org.talend.dataprofiler.core.migration.impl;

import java.util.Date;

import org.eclipse.core.resources.IFolder;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.manager.DQStructureManager;
import org.talend.dataprofiler.core.migration.AWorkspaceTask;
import org.talend.dq.indicators.definitions.DefinitionHandler;
import org.talend.resource.EResourceConstant;
import org.talend.resource.ResourceManager;

/**
 * 
 * DOC mzhao Migration task for splitting system indicators. The .talend.definition file would be replaced by several
 * separate files, each indicator definition is defined in its own file. feature: 13676, 2010-07-07
 */
public class SplitSysIndicatorTask extends AWorkspaceTask {

    public SplitSysIndicatorTask() {
    }

    @Override
    protected boolean doExecute() throws Exception {

        // Copy system indicators.
        DQStructureManager manager = DQStructureManager.getInstance();
        IFolder systemIndicatorFoler = manager.createNewFolder(ResourceManager.getIndicatorFolder(),
                EResourceConstant.SYSTEM_INDICATORS);
        manager.copyFilesToFolder(CorePlugin.getDefault(), DQStructureManager.SYSTEM_INDICATOR_PATH, true, systemIndicatorFoler,
                null);

        // Copy system indicator categories.
        DefinitionHandler.getInstance();
        return true;
    }

    public MigrationTaskType getMigrationTaskType() {
        return MigrationTaskType.FILE;
    }

    public Date getOrder() {
        return createDate(2010, 07, 07);
    }

}
