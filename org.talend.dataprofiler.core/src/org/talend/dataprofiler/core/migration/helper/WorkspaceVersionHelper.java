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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.manager.DQStructureManager;
import org.talend.utils.ProductVersion;

/**
 * DOC bzhou class global comment. Detailled comment
 */
public class WorkspaceVersionHelper {

    public final static String VERSION = "version"; //$NON-NLS-1$

    public static IFile getVersionFile() {
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        try {
            root.refreshLocal(IResource.DEPTH_INFINITE, null);
        } catch (CoreException e1) {
            e1.printStackTrace();
        }
        IProject project = root.getProject(DQStructureManager.LIBRARIES);

        if (project.exists() && !project.isOpen()) {
            try {
                project.open(null);
            } catch (CoreException e) {
                e.printStackTrace();
            }
        }

        return project.getFile(PluginConstant.VERSION_FILE_PATH);
    }

    public static ProductVersion getVesion() {
        IFile versionFile = getVersionFile();
        if (versionFile.exists()) {
            Properties pros = new Properties();
            try {
                pros.load(versionFile.getContents());
                String version = pros.getProperty(VERSION);
                if (version != null && !"".equals(version)) { //$NON-NLS-1$
                    return ProductVersion.fromString(version);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return new ProductVersion(0, 0, 0);
    }

    public static void storeVersion() {
        IFile versionFile = getVersionFile();
        Properties pros = new Properties();
        pros.setProperty(VERSION, CorePlugin.getDefault().getProductVersion().toString());

        try {
            pros.store(new FileOutputStream(new File(versionFile.getLocation().toOSString())), null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
