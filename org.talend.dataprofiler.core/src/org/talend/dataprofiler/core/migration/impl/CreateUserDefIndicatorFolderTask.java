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
import org.eclipse.core.resources.IFolder;
import org.talend.dataprofiler.core.exception.ExceptionHandler;
import org.talend.dataprofiler.core.manager.DQStructureManager;
import org.talend.dataprofiler.core.migration.AbstractMigrationTask;
import org.talend.resource.EResourceConstant;
import org.talend.resource.ResourceManager;

/**
 * DOC xqliu class global comment. Detailled comment
 */
public class CreateUserDefIndicatorFolderTask extends AbstractMigrationTask {

    protected static Logger log = Logger.getLogger(CreateUserDefIndicatorFolderTask.class);

    public boolean execute() {
        try {
            DQStructureManager manager = DQStructureManager.getInstance();

            // creator Indicators
            IFolder folder = manager.createNewReadOnlyFolder(ResourceManager.getLibrariesFolder(), EResourceConstant.INDICATORS
                    .getName());
            ResourceManager.setNoSubFolderProperty(folder);

            // create User Defined Indicators
            folder = manager.createNewReadOnlyFolder(folder, EResourceConstant.USER_DEFINED_INDICATORS.getName());

        } catch (Exception e) {
            ExceptionHandler.process(e);
            return false;
        }
        return true;
    }

    public MigrationTaskType getMigrationTaskType() {
        return MigrationTaskType.STUCTRUE;
    }

    public Date getOrder() {
        Calendar calender = Calendar.getInstance();
        calender.set(2009, 8, 13);
        return calender.getTime();
    }

}
