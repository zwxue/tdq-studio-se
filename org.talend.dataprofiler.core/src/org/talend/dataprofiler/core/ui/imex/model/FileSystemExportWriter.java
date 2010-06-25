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

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.commons.utils.io.FilesUtils;
import org.talend.dataprofiler.core.PluginChecker;
import org.talend.dataprofiler.core.migration.helper.WorkspaceVersionHelper;
import org.talend.resource.ResourceManager;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public class FileSystemExportWriter implements IImexWriter {

    private static Logger log = Logger.getLogger(FileSystemExportWriter.class);

    private String basePath;

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.dataprofiler.core.ui.imex.model.IImexWriter#populate(org.talend.dataprofiler.core.ui.imex.model.ItemRecord
     * [], boolean)
     */
    public ItemRecord[] populate(ItemRecord[] elements, boolean checkExisted) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.dataprofiler.core.ui.imex.model.IImexWriter#write(org.talend.dataprofiler.core.ui.imex.model.ItemRecord
     * )
     */
    public void write(ItemRecord recored) throws IOException, CoreException {

        IPath itemDesPath = new Path(basePath).append(recored.getFullPath());
        IPath propDesPath = itemDesPath.removeFileExtension().addFileExtension(FactoriesUtil.PROPERTIES_EXTENSION);

        // export item file
        File resItemFile = recored.getFile();
        File desItemFile = itemDesPath.toFile();

        copyFile(resItemFile, desItemFile);

        // export property file
        File resPropFile = recored.getPropertyPath().toFile();
        File desPropFile = propDesPath.toFile();

        copyFile(resPropFile, desPropFile);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.dataprofiler.core.ui.imex.model.IImexWriter#finish(org.talend.dataprofiler.core.ui.imex.model.ItemRecord
     * [])
     */
    public void finish(ItemRecord[] records) throws IOException {
        ItemRecord.clear();

        if (PluginChecker.isTDCPLoaded()) {
            IFile projFile = ResourceManager.getRootProject().getFile("talend.project");
            copyFileToDest(projFile);
        }

        IFile versionFile = WorkspaceVersionHelper.getVersionFile();
        copyFileToDest(versionFile);
    }

    /**
     * DOC bZhou Comment method "copyFileToDest".
     * 
     * @param source
     * @throws IOException
     */
    private void copyFileToDest(IFile source) throws IOException {
        IPath desPath = new Path(basePath).append(source.getFullPath());
        if (source.exists()) {
            copyFile(source.getLocation().toFile(), desPath.toFile());
        }
    }

    /**
     * DOC bZhou Comment method "copyFile".
     * 
     * @param source
     * @param target
     * @throws IOException
     */
    static void copyFile(File source, File target) throws IOException {
        if (source.exists()) {
            FilesUtils.copyFile(source, target);
        } else {
            log.warn("Export failed! " + source.getAbsolutePath() + " is not existed");
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.imex.model.IImexWriter#setBasePath(java.lang.String)
     */
    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.imex.model.IImexWriter#getBasePath()
     */
    public String getBasePath() {
        return this.basePath;
    }

}
