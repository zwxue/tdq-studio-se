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

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.talend.commons.utils.StringUtils;
import org.talend.dataprofiler.core.migration.AbstractMigrationTask;
import org.talend.resource.ResourceManager;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public class SoundexIndicatorQueryTask extends AbstractMigrationTask {

    private static Logger log = Logger.getLogger(SoundexIndicatorQueryTask.class);

    private final String oldSoundexQuery = "ORDER BY d,c DESC";

    private final String newSoundexQuery = "ORDER BY d DESC,c DESC";

    private final String oldPSoundexQuery = "ORDER BY COUNT(DISTINCT &lt;%=__COLUMN_NAMES__%>) , COUNT(*) DESC";

    private final String newPSoundexQuery = "ORDER BY COUNT(DISTINCT &lt;%=__COLUMN_NAMES__%>) DESC , COUNT(*) DESC";

    private final String talendDefinitionFileName = ".Talend.definition";

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.migration.IWorkspaceMigrationTask#execute()
     */
    public boolean execute() {
        IFolder librariesFolder = ResourceManager.getLibrariesFolder();
        IFile definitionFile = librariesFolder.getFile(talendDefinitionFileName);

        if (definitionFile.exists()) {
            File file = new File(definitionFile.getLocationURI());
            try {
                String content = FileUtils.readFileToString(file);
                content = StringUtils.replace(content, oldSoundexQuery, newSoundexQuery);
                content = StringUtils.replace(content, oldPSoundexQuery, newPSoundexQuery);
                FileUtils.writeStringToFile(file, content);
            } catch (IOException e) {
                log.error(e.getMessage(), e);
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
     * @see org.talend.dataprofiler.core.migration.IWorkspaceMigrationTask#getOrder()
     */
    public Date getOrder() {
        Calendar calender = Calendar.getInstance();
        calender.set(2009, 10, 23);
        return calender.getTime();
    }

}
