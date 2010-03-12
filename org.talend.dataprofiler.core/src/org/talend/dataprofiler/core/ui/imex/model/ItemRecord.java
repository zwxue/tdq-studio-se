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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.talend.commons.emf.FactoriesUtil;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public class ItemRecord {

    private File file;

    private String projectName;

    private IPath fullPath;

    public ItemRecord(File file) {
        this.file = file;
    }

    public ItemRecord(IFile file) {
        this(file.getLocation().toFile());
        this.fullPath = file.getFullPath();
    }

    /**
     * Getter for file.
     * 
     * @return the file
     */
    public File getFile() {
        return this.file;
    }

    /**
     * Getter for fullPath.
     * 
     * @return the fullPath
     */
    public IPath getFullPath() {
        return this.fullPath;
    }

    /**
     * Getter for propertyFilePath.
     * 
     * @return the propertyFilePath
     */
    public String getPropertyFilePath() {
        if (file != null) {
            IPath itemResPath = new Path(file.getAbsolutePath());
            IPath propResPath = itemResPath.removeFileExtension().addFileExtension(FactoriesUtil.PROPERTIES_EXTENSION);
            return propResPath.toOSString();
        }
        return null;
    }

    /**
     * Sets the projectName.
     * 
     * @param projectName the projectName to set
     */
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    /**
     * Getter for projectName.
     * 
     * @return the projectName
     */
    public String getProjectName() {
        return this.projectName;
    }
}
