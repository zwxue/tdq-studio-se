// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.helper;

import java.io.File;
import java.net.URL;
import java.util.Enumeration;
import java.util.Map;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.talend.commons.utils.WorkspaceUtils;
import org.talend.core.model.properties.FolderItem;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.Folder;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.repository.ProjectManager;
import org.talend.resource.ResourceManager;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * 
 * DOC mzhao class global comment. Detailled comment
 */
public final class WorkspaceResourceHelper {

    private static final String ANALYSIS_EDITOR_ID = "org.talend.dataprofiler.core.ui.editor.analysis.AnalysisEditor"; //$NON-NLS-1$

    private WorkspaceResourceHelper() {
    }

    /**
     * 
     * DOC Administrator Comment method "getModelElementPath".
     * 
     * @param me
     * @return
     */
    public static IPath getModelElementPath(ModelElement me) {
        return WorkspaceUtils.getModelElementResource(me).getFullPath();
    }

    /**
     * DOC bZhou Comment method "getInstallPath".
     * 
     * @return the install path for the current release.
     */
    public static String getInstallPath() {
        return Platform.getInstallLocation().getURL().getPath().substring(1);
    }

    /**
     * DOC bZhou Comment method "getLocationPath".
     * 
     * @return
     */
    public static String getLocationPath() {
        return Platform.getInstanceLocation().getURL().getPath().substring(1);
    }

    /**
     * DOC bZhou Comment method "computeFileFromPlugin".
     * 
     * @param plugin
     * @param srcPath
     * @param recurse
     * @param desFolder
     * @param suffix
     * @param type
     * @param resultMap
     * @throws Exception
     */
    public static void computeFileFromPlugin(Plugin plugin, String srcPath, boolean recurse, Folder desFolder, String suffix,
            ERepositoryObjectType type, Map<File, IPath> resultMap) throws Exception {
        if (plugin == null || srcPath == null) {
            return;
        }

        IProject project = ResourceManager.getRootProject();
        Enumeration<String> paths = plugin.getBundle().getEntryPaths(srcPath);

        while (paths != null && paths.hasMoreElements()) {
            String nextElement = (String) paths.nextElement();
            String currentPath = "/" + nextElement; //$NON-NLS-1$
            URL resourceURL = plugin.getBundle().getEntry(currentPath);
            URL fileURL = null;
            File file = null;

            fileURL = FileLocator.toFileURL(resourceURL);
            file = new File(fileURL.getFile());
            if (file.isDirectory() && recurse) {
                if (file.getName().startsWith(".")) { //$NON-NLS-1$
                    continue;
                }
                Folder folder = null;
                if (!project.getFolder(file.getName()).exists()) {
                    folder = ProxyRepositoryFactory.getInstance().createFolder(type, Path.EMPTY, file.getName());
                } else {
                    IPath fullPath = new Path(file.getPath());
                    int count = fullPath.segmentCount();
                    FolderItem folderItem = ProxyRepositoryFactory.getInstance().getFolderItem(
                            ProjectManager.getInstance().getCurrentProject(), type, fullPath.removeFirstSegments(count - 1));

                    if (folderItem == null) {
                        folder = ProxyRepositoryFactory.getInstance().createFolder(type, Path.EMPTY, file.getName());
                    } else {
                        folder = new Folder(folderItem.getProperty(), type);
                    }
                }
                computeFileFromPlugin(plugin, currentPath, recurse, folder, suffix, type, resultMap);
                continue;
            }

            if (suffix != null && !file.getName().endsWith(suffix)) {
                continue;
            }

            String folderName = null;
            if (type.equals(ERepositoryObjectType.TDQ_JRAXML_ELEMENT)) {
                folderName = ERepositoryObjectType.getFolderName(type) + "/" + desFolder.getLabel(); //$NON-NLS-1$
            }
            IFolder folder = project.getFolder(folderName);
            int segmentCount = folder.getFullPath().segmentCount();
            IPath parentPath = folder.getFullPath().removeFirstSegments(segmentCount - 1);

            resultMap.put(file, parentPath);
        }
    }

}
