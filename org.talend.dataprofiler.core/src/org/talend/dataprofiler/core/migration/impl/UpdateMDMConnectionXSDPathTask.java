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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Date;

import org.apache.log4j.Logger;
import org.talend.commons.utils.io.FilesUtils;
import org.talend.dataprofiler.core.migration.AbstractWorksapceUpdateTask;
import org.talend.resource.ResourceManager;

/**
 * DOC xqliu class global comment. bug 14123
 */
public class UpdateMDMConnectionXSDPathTask extends AbstractWorksapceUpdateTask {

    private static Logger log = Logger.getLogger(UpdateMDMConnectionXSDPathTask.class);

    private static final String MIGRATION_FILE_EXT = ".mig";//$NON-NLS-1$ 

    private static final String XED = "<xsdElementDeclaration href=\"";//$NON-NLS-1$ 

    private static final String XSD = ".xsd/";//$NON-NLS-1$ 

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.migration.IWorkspaceMigrationTask#getMigrationTaskType()
     */
    public MigrationTaskType getMigrationTaskType() {
        return MigrationTaskType.FILE;
    }

    /**
     * DOC xqliu Comment method "migrateFilePath".
     * 
     * @param aString
     * @return
     */
    private static String migrateFilePath(String aString) {
        String result = aString;
        if (aString != null && !"".equals(aString)) {
            int xedPoint = aString.indexOf(XED);
            int xsdPoint = aString.indexOf(XSD);
            if (xedPoint > -1 && xsdPoint > -1 && (xsdPoint - xedPoint > 0)) {
                result = aString.substring(0, xedPoint + XED.length()) + aString.substring(xsdPoint);
            }
        }
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.migration.IMigrationTask#getOrder()
     */
    public Date getOrder() {
        // this task should be executed before MergeMetadataTask
        return createDate(2010, 6, 21);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.migration.AMigrationTask#doExecute()
     */
    @Override
    protected boolean doExecute() throws Exception {
        boolean result = true;
        boolean rename = true;

        File rawFileMetadata = new File(ResourceManager.getMDMConnectionFolder().getRawLocationURI());
        final String[] metadataFileExtentionNames = { ".prv" };//$NON-NLS-1$ 

        return doUpdate(result, rename, rawFileMetadata, metadataFileExtentionNames);
    }

    public static boolean doUpdate(boolean result, boolean rename, File rawFileMetadata, final String[] metadataFileExtentionNames) {
        ArrayList<File> fileList = new ArrayList<File>();
        FilesUtils.getAllFilesFromFolder(rawFileMetadata, fileList, new FilenameFilter() {

            public boolean accept(File dir, String name) {
                for (String extName : metadataFileExtentionNames) {
                    if (name.endsWith(extName)) {
                        return true;
                    }
                }
                return false;
            }
        });
        log.info("-------------- Migrating " + fileList.size() + " files");

        int counter = 0;
        int errorCounter = 0;
        Throwable error = null;

        for (File sample : fileList) {
            log.info("-------------- Migrating (" + counter++ + ") : " + sample.getAbsolutePath());
            try {
                BufferedReader fileReader = new BufferedReader(new FileReader(sample));
                BufferedWriter fileWriter = new BufferedWriter(new FileWriter(new File(sample.getAbsolutePath()
                        + MIGRATION_FILE_EXT)));

                while (fileReader.ready()) {
                    String line = fileReader.readLine();
                    line = migrateFilePath(line);

                    fileWriter.append(line);
                    fileWriter.newLine();
                }

                fileWriter.flush();
                fileWriter.close();
            } catch (Exception e) {
                error = e;
                errorCounter++;
                log.error("!!!!!!!!!!!  Error transforming (" + sample.getAbsolutePath() + ")\n" + e.getMessage(), e);
            }
            log.info("-------------- Migration done of " + counter + " files"
                    + (errorCounter != 0 ? (",  there are " + errorCounter + " files in error.") : "."));
        }

        if (error != null) {
            result = false;
        } else {
            if (rename) {
                // remove original files and rename new ones to old ones
                for (File sample : fileList) {
                    boolean isDeleted = sample.delete();
                    log.info(sample.getAbsolutePath() + (isDeleted ? " is deleted." : " failed to delete."));
                    boolean isrenamed = new File(sample.getAbsolutePath() + MIGRATION_FILE_EXT).renameTo(sample); //$NON-NLS-1$
                    log.info(sample.getAbsolutePath() + MIGRATION_FILE_EXT + (isrenamed ? " is renamed." : " failed to rename."));
                }
            }
        }
        return result;
    }
}
