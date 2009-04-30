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
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.talend.dataprofiler.core.ResourceManager;
import org.talend.dataprofiler.core.migration.AbstractMigrationTask;

/**
 * DOC bzhou class global comment. Detailled comment
 */
public class CopyDefineFileTask extends AbstractMigrationTask {

    protected static Logger log = Logger.getLogger(CopyDefineFileTask.class);

    private static final String TALEND_DEFINITION_FILENAME = ".Talend.definition"; //$NON-NLS-1$

    private static final String TALEND_DEFINITION_BAK_FILENAME = ".Talend.definition.bak"; //$NON-NLS-1$

    public boolean execute() {
        IFolder libFolder = ResourceManager.getLibrariesFolder();

        IFile file = libFolder.getFile(TALEND_DEFINITION_FILENAME);
        IFile bakfile = libFolder.getFile(TALEND_DEFINITION_BAK_FILENAME);

        if (file.exists()) {
            try {

                if (bakfile.exists()) {
                    bakfile.delete(true, null);
                }

                file.copy(bakfile.getFullPath(), true, null);
                file.delete(true, null);

                return true;
            } catch (Exception e) {
                log.error(e, e);
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
