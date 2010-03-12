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
package org.talend.dataprofiler.core.ui.imex.model;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.commons.utils.io.FilesUtils;
import org.talend.dataprofiler.core.PluginChecker;
import org.talend.resource.ResourceManager;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public class FileSystemExportWriter implements IImexWriter {

    private static Logger log = Logger.getLogger(FileSystemExportWriter.class);

    protected Map<IPath, IPath> resMap;

    private String destination;

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.dataprofiler.core.ui.imex.model.IImexWriter#initPath(org.talend.dataprofiler.core.ui.imex.model.ItemRecord
     * , java.lang.String)
     */
    public void initPath(ItemRecord resource, String destination) {
        this.destination = destination;

        IPath itemResPath = new Path(resource.getFile().getAbsolutePath());
        IPath propResPath = new Path(resource.getPropertyFilePath());

        IPath itemDesPath = new Path(destination).append(resource.getFullPath());
        IPath propDesPath = itemDesPath.removeFileExtension().addFileExtension(FactoriesUtil.PROPERTIES_EXTENSION);

        resMap = new HashMap<IPath, IPath>();
        resMap.put(itemResPath, itemDesPath);
        resMap.put(propResPath, propDesPath);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.imex.model.IImexWriter#write()
     */
    public void write() throws IOException, CoreException {
        if (resMap != null) {
            for (IPath resPath : resMap.keySet()) {
                copyFile(resPath.toFile(), resMap.get(resPath).toFile());
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.imex.model.IExportWriter#finish()
     */
    public void finish() throws IOException {
        if (PluginChecker.isTDCPLoaded()) {
            IPath projResPath = ResourceManager.getRootProject().getLocation().append("talend.project");
            IPath projDesPath = new Path(destination).append(ResourceManager.getRootProjectName()).append("talend.project");
            File projFile = projResPath.toFile();
            if (projFile.exists()) {
                copyFile(projFile, projDesPath.toFile());
            }
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
}
