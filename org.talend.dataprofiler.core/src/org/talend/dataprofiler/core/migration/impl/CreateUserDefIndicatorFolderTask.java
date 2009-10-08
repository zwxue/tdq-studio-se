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
import org.talend.resource.ResourceManager;
import org.talend.resource.xml.TdqPropertieManager;

/**
 * DOC xqliu class global comment. Detailled comment
 */
public class CreateUserDefIndicatorFolderTask extends AbstractMigrationTask {

    protected static Logger log = Logger.getLogger(CreateUserDefIndicatorFolderTask.class);

    public boolean execute() {
        try {
            DQStructureManager manager = DQStructureManager.getInstance();
            
            // creator Indicators
            IFolder createNewFoler = manager.createNewReadOnlyFolder(ResourceManager.getLibrariesFolder(), DQStructureManager.INDICATORS);
            // createNewFoler.setPersistentProperty(DQStructureManager.FOLDER_CLASSIFY_KEY,
            // DQStructureManager.INDICATORS_FOLDER_PROPERTY);
            TdqPropertieManager.getInstance().addFolderProperties(createNewFoler, DQStructureManager.FOLDER_CLASSIFY_KEY,
                    DQStructureManager.INDICATORS_FOLDER_PROPERTY);
            TdqPropertieManager.getInstance().addFolderProperties(createNewFoler, DQStructureManager.NO_SUBFOLDER_KEY,
                    DQStructureManager.NO_SUBFOLDER_PROPERTY);
            // createNewFoler.setPersistentProperty(DQStructureManager.NO_SUBFOLDER_KEY,
            // DQStructureManager.NO_SUBFOLDER_PROPERTY);
            // create User Defined Indicators
            createNewFoler = manager.createNewReadOnlyFolder(createNewFoler, DQStructureManager.USER_DEFINED_INDICATORS);
            TdqPropertieManager.getInstance().addFolderProperties(createNewFoler, DQStructureManager.FOLDER_CLASSIFY_KEY,
                    DQStructureManager.UDI_FOLDER_PROPERTY);
            // createNewFoler.setPersistentProperty(DQStructureManager.FOLDER_CLASSIFY_KEY,
            // DQStructureManager.UDI_FOLDER_PROPERTY);

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
