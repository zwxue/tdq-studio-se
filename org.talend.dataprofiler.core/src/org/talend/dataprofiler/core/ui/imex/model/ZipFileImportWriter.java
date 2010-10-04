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

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.talend.resource.ResourceManager;
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
        String curProjectLabel = ResourceManager.getRootProjectName();
        return super.computeInput(sourcePath.append(sourcePath.lastSegment()).append(curProjectLabel));
    }

    @Override
    public void finish(ItemRecord[] records, IProgressMonitor monitor) throws IOException, CoreException {
        super.finish(records, monitor);

        FilesUtils.removeFolder(sourcePath.toFile(), true);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.dataprofiler.core.ui.imex.model.FileSystemImportWriter#backUPWorksapce(org.eclipse.core.runtime.IPath)
     */
    @Override
    protected File backUPWorksapce(IPath workspacePath) {
        // no need to create a backup here because the folder is already created by the unzip action
        return workspacePath.toFile();
    }


}
