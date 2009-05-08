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
import org.talend.dataprofiler.core.ResourceManager;
import org.talend.dataprofiler.core.manager.DQStructureManager;
import org.talend.dataprofiler.core.migration.AbstractMigrationTask;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public class CreateExchangeFolderTask extends AbstractMigrationTask {

    private static Logger log = Logger.getLogger(CreateExchangeFolderTask.class);

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.migration.IWorkspaceMigrationTask#execute()
     */
    public boolean execute() {
        try {
            DQStructureManager manager = DQStructureManager.getInstance();
            IFolder createNewFoler = manager.createNewFoler(ResourceManager.getLibrariesFolder(), DQStructureManager.EXCHANGE);
            createNewFoler.setPersistentProperty(DQStructureManager.FOLDER_CLASSIFY_KEY,
                    DQStructureManager.EXCHANGE_FOLDER_PROPERTY);
        } catch (Exception e) {
            log.error(e, e);
            return false;
        }
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.migration.IWorkspaceMigrationTask#getOrder()
     */
    public Date getOrder() {
        Calendar calender = Calendar.getInstance();
        calender.set(2009, 5, 8);
        return calender.getTime();
    }

}
