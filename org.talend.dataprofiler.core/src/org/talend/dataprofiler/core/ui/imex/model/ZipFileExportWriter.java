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
package org.talend.dataprofiler.core.ui.imex.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IPath;
import org.talend.utils.io.FilesUtils;
import org.talend.utils.sugars.ReturnCode;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public class ZipFileExportWriter extends FileSystemExportWriter {

    private static Logger log = Logger.getLogger(ZipFileExportWriter.class);

    private String fileExtension;

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.dataprofiler.core.ui.imex.model.FileSystemExportWriter#setBasePath(org.eclipse.core.runtime.IPath)
     */
    @Override
    public void setBasePath(IPath path) {
        if (path.getFileExtension() != null) {
            this.fileExtension = path.getFileExtension();
            super.setBasePath(path.removeFileExtension());
        } else {
            super.setBasePath(path);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.dataprofiler.core.ui.imex.model.FileSystemExportWriter#finish(org.talend.dataprofiler.core.ui.imex
     * .model.ItemRecord[])
     */
    @Override
    public void finish(ItemRecord[] records) throws IOException {
        super.finish(records);

        // zip files
        IPath basePath = getBasePath();
        IPath zipPath = basePath.addFileExtension(fileExtension);

        try {
            FilesUtils.zipFolderRecursion(basePath.toOSString(), zipPath.toOSString());

            FilesUtils.removeFolder(basePath.toFile(), true);
        } catch (Exception e) {
            log.error(e, e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.imex.model.FileSystemExportWriter#check()
     */
    @Override
    public List<String> check() {
        List<String> errors = new ArrayList<String>();

        ReturnCode rc = new ReturnCode("The root file extension is invalid!", false);

        if (fileExtension != null) {
            String[] validExtensions = new String[] { "zip", "tar", "tar.gz" };
            for (String ext : validExtensions) {
                if (StringUtils.equalsIgnoreCase(fileExtension, ext)) {
                    rc.setOk(true);
                    break;
                }
            }
        }

        if (!rc.isOk()) {
            errors.add(rc.getMessage());
        }

        return errors;
    }
}
