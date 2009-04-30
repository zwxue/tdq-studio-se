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
package org.talend.dataprofiler.core.migration.helper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.ResourceManager;
import org.talend.utils.ProductVersion;

/**
 * DOC bzhou class global comment. Detailled comment
 */
public final class WorkspaceVersionHelper {

    protected static Logger log = Logger.getLogger(WorkspaceVersionHelper.class);

    public static final String VERSION = "version"; //$NON-NLS-1$

    private WorkspaceVersionHelper() {

    }

    /**
     * DOC bZhou Comment method "getVersionFile".
     * 
     * @return
     */
    public static IFile getVersionFile() {
        return ResourceManager.getLibrariesFolder().getFile(PluginConstant.VERSION_FILE_PATH);
    }

    /**
     * 
     * MOD mzhao Get version file by static way, not by IFile. See feature 6066
     * 
     * @return
     */
    public static ProductVersion getVesion() {
        IFile versionFile = getVersionFile();
        try {
            versionFile.refreshLocal(IResource.DEPTH_ONE, new NullProgressMonitor());
            if (versionFile.exists()) {
                Properties pros = new Properties();

                pros.load(versionFile.getContents());
                String version = pros.getProperty(VERSION);
                if (version != null && !"".equals(version)) { //$NON-NLS-1$
                    return ProductVersion.fromString(version);
                }

            }
        } catch (Exception e) {
            log.error(e, e);
        }

        return new ProductVersion(0, 0, 0);
    }

    /**
     * 
     * MOD mzhao 2009-04-03
     */
    public static void storeVersion() {
        IFile versionFile = getVersionFile();
        Properties pros = new Properties();
        pros.setProperty(VERSION, CorePlugin.getDefault().getProductVersion().toString());
        try {
            pros.store(new FileOutputStream(new File(versionFile.getLocation().toOSString())), null);
        } catch (FileNotFoundException e) {
            log.error(e, e);
        } catch (IOException e) {
            log.error(e, e);
        }
    }
}
