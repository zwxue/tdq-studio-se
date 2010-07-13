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

import org.eclipse.core.runtime.IPath;
import org.talend.utils.io.FilesUtils;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public class ZipFileImportWriter extends FileSystemImportWriter {

    private IPath sourcePath;

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.dataprofiler.core.ui.imex.model.FileSystemImportWriter#computeInput(org.eclipse.core.runtime.IPath)
     */
    @Override
    public ItemRecord computeInput(IPath path) {
        sourcePath = path.removeFileExtension();

        try {
            FilesUtils.createFolder(sourcePath.toFile());

            FilesUtils.unzip(path.toOSString(), sourcePath.toOSString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return super.computeInput(sourcePath);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.dataprofiler.core.ui.imex.model.FileSystemImportWriter#finish(org.talend.dataprofiler.core.ui.imex
     * .model.ItemRecord[])
     */
    @Override
    public void finish(ItemRecord[] records) throws IOException {
        super.finish(records);

        FilesUtils.removeFolder(sourcePath.toFile(), true);
    }
}
