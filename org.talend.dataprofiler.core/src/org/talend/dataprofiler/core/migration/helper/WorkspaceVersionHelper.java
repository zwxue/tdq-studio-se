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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.manager.DQStructureManager;
import org.talend.utils.ProductVersion;

/**
 * DOC bzhou class global comment. Detailled comment
 */
public class WorkspaceVersionHelper {

	public final static String VERSION = "version"; //$NON-NLS-1$

	public static File getVersionFile() {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		try {
			root.refreshLocal(IResource.DEPTH_INFINITE, null);
		} catch (CoreException e1) {
			e1.printStackTrace();
		}
		// MOD mzhao 2009-03-13 put all folders in one project.
		IProject project = root
				.getProject(org.talend.dataquality.PluginConstant
						.getRootProjectName());

		if (project.exists() && !project.isOpen()) {
			try {
				project.open(null);
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
		IFolder librariesFolder = project.getFolder(DQStructureManager
				.getLibraries());
		// MOD mzhao try to find version in "/Libraries/.verstion.txt" location
		// if not exist in "/TOP_DEFAULT_PRJ/Libraries/.version.txt".It is only
		// necessary when migrate version from previouse to 1.2.0 (See feature
		// 6066), Moreover, the ".version.txt" file must be fetched in static
		// way, not by finding in "WorkSpace"( means not by
		// root.getProject(...))
		IFile versionFileTmp = librariesFolder
				.getFile(PluginConstant.VERSION_FILE_PATH);
		if (!versionFileTmp.exists()) {
			File vf = new File(root.getRawLocation().toOSString()
					+ IPath.SEPARATOR + DQStructureManager.getLibraries()
					+ IPath.SEPARATOR + PluginConstant.VERSION_FILE_PATH);

			return vf;

		} else {
			return new File(versionFileTmp.getRawLocation().toOSString());
		}

	}

	/**
	 * 
	 * MOD mzhao Get version file by static way, not by IFile. See feature 6066
	 * 
	 * @return
	 */
	public static ProductVersion getVesion() {
		File versionFile = getVersionFile();
		if (versionFile.exists()) {
			Properties pros = new Properties();
			try {
				pros.load(new FileInputStream(versionFile));
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

	/**
	 * 
	 * MOD mzhao 2009-03-19, Get version file as "java.io.File", and if this
	 * file dose not exist,return.
	 */
	public static void storeVersion() {
		File versionFile = getVersionFile();
		if (!versionFile.exists()) {
			return;
		}
		Properties pros = new Properties();
		pros.setProperty(VERSION, CorePlugin.getDefault().getProductVersion()
				.toString());

		try {
			pros.store(new FileOutputStream(versionFile), null);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
