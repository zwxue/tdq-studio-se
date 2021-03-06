// ============================================================================
//
// Copyright (C) 2006-2019 Talend Inc. - www.talend.com
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

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFolder;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.manager.DQStructureManager;
import org.talend.dataprofiler.core.migration.AbstractWorksapceUpdateTask;
import org.talend.resource.ResourceManager;

/**
 * DOC xqliu class global comment. Detailled comment
 */
public class CreateDQRulesFolderTask extends AbstractWorksapceUpdateTask {

    protected static Logger log = Logger.getLogger(CreateDQRulesFolderTask.class);

    private static final String DQ_RULES = "DQ Rules"; //$NON-NLS-1$

    /*
     * (non-Javadoc)
     *
     * @see org.talend.dataprofiler.core.migration.AWorkspaceTask#valid()
     */
    @Override
    public boolean valid() {
        return !ResourceManager.getRulesSQLFolder().exists() && super.valid();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.talend.dataprofiler.core.migration.AMigrationTask#doExecute()
     */
    @Override
    protected boolean doExecute() throws Exception {
        DQStructureManager manager = DQStructureManager.getInstance();
        IFolder folder = manager.createNewFolder(ResourceManager.getLibrariesFolder(), DQ_RULES);
        manager.copyFilesToFolder(CorePlugin.getDefault(), DQStructureManager.RULES_PATH, true, folder, null);

        return folder != null && folder.exists();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.talend.dataprofiler.core.migration.IWorkspaceMigrationTask#getOrder()
     */
    public Date getOrder() {
        return createDate(2009, 2, 13);
    }

    /*
     * (non-Javadoc)
     *
     * @seeorg.talend.dataprofiler.core.migration.IWorkspaceMigrationTask# getMigrationTaskType()
     */
    public MigrationTaskType getMigrationTaskType() {
        return MigrationTaskType.STUCTRUE;
    }
}
