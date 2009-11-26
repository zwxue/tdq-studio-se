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

import org.apache.log4j.Logger;
import org.talend.dataprofiler.core.migration.AProjectTask;
import org.talend.dataprofiler.core.migration.helper.DataBaseVersionHelper;
import org.talend.dataprofiler.core.migration.helper.WorkspaceVersionHelper;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public class UpdateVersionsTask extends AProjectTask {

    private static Logger log = Logger.getLogger(UpdateVersionsTask.class);

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.migration.IMigrationTask#execute()
     */
    public boolean execute() {
        try {
            // update workspace version.
            WorkspaceVersionHelper.storeVersion();

            // update databse version.
            DataBaseVersionHelper.storeVersion();

        } catch (Exception e) {
            log.error(e, e);
            return false;
        }

        return true;
    }

}
