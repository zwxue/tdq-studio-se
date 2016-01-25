// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dq.helper;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.Path;

/**
 * DOC bZhou class global comment. Detailled comment <br/>
 * 
 * $Id: talend.epf 55206 2011-02-15 17:32:14Z bzhou $
 * 
 */
public final class FileUtils {

    /**
     * DOC bZhou Comment method "getName".
     * 
     * This method get the name of a file, without the file extension.
     * 
     * @param file
     * @return
     */
    public static String getName(File file) {
        Path fileNamePath = new Path(file.getName());
        return fileNamePath.removeFileExtension().toString();
    }

    /**
     * DOC bZhou Comment method "getExtension".
     * 
     * This method get the extension of a file.
     * 
     * @param file
     * @return
     */
    public static String getExtension(File file) {
        Path fileNamePath = new Path(file.getName());
        return fileNamePath.getFileExtension();
    }

    /**
     * DOC bZhou Comment method "getFilesByExtension".
     * 
     * @param parentFolder
     * @param extensions
     * @return
     */
    public static List<File> getFilesByExtension(File parentFolder, final String... extensions) {
        ArrayList<File> fileList = new ArrayList<File>();
        getAllFilesFromFolder(parentFolder, fileList, new FilenameFilter() {

            public boolean accept(File dir, String name) {
                if (extensions != null) {
                    for (String ext : extensions) {
                        if (name.endsWith(ext)) {
                            return true;
                        }
                    }
                    return false;
                }

                return true;
            }
        });

        return fileList;
    }

    /**
     * DOC sgandon Comment method "getAllFilesFromFolder".
     * 
     * @param sampleFolder
     * @param arrayList
     * @param filenameFilter
     */
    public static void getAllFilesFromFolder(File sampleFolder, ArrayList<File> fileList, FilenameFilter filenameFilter) {
        File[] folderFiles = sampleFolder.listFiles(filenameFilter);
        Collections.addAll(fileList, folderFiles);
        File[] allFolders = sampleFolder.listFiles(new FileFilter() {

            public boolean accept(File arg0) {
                return arg0.isDirectory();
            }
        });
        for (File folder : allFolders) {
            getAllFilesFromFolder(folder, fileList, filenameFilter);
        }
    }
}
