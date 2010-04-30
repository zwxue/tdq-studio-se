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

import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.talend.dataprofiler.core.migration.AWorkspaceTask;
import org.talend.dq.indicators.definitions.DefinitionHandler;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public class UpdateDefintionFileTask extends AWorkspaceTask {

    private static Logger log = Logger.getLogger(UpdateDefintionFileTask.class);

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.migration.IMigrationTask#execute()
     */
    public boolean execute() {
        // TODO compare new file and old file to extract the udis and then add them into new file.

        IFile talendDefinitionFile = DefinitionHandler.getTalendDefinitionFile();
        if (talendDefinitionFile.exists()) {
            try {
                talendDefinitionFile.delete(true, null);

                DefinitionHandler.getInstance();
            } catch (Exception e) {
                log.error(e, e);
                return false;
            }
        }

        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.migration.IWorkspaceMigrationTask#getMigrationTaskType()
     */
    public MigrationTaskType getMigrationTaskType() {
        return MigrationTaskType.FILE;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.migration.IMigrationTask#getOrder()
     */
    public Date getOrder() {
        Calendar calender = Calendar.getInstance();
        calender.set(2009, 10, 20);
        return calender.getTime();
    }
}
