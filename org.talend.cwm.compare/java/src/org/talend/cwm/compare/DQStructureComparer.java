// ============================================================================
//
// Copyright (C) 2006-2007 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.cwm.compare;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.talend.commons.emf.EMFSharedResources;
import org.talend.dataprofiler.core.manager.DQStructureManager;

/**
 * DOC scorreia class global comment. Detailled comment
 */
public final class DQStructureComparer {

    private static final String NEED_RELOAD_ELEMENTS_PRV = ".needReloadElements.comp"; //$NON-NLS-1$

    private static final String RESULT_EMFDIFF_FILE = ".result.emfdiff"; //$NON-NLS-1$

    private static final String TEMP_REFRESH_FILE = ".refresh.comp"; //$NON-NLS-1$

    private static final Class<DQStructureComparer> THAT = DQStructureComparer.class;

    protected static Logger log = Logger.getLogger(THAT);

    private static DQStructureComparer comparer = new DQStructureComparer();

    public static DQStructureComparer getInstance() {
        return comparer;
    }

    private DQStructureComparer() {

    }

    /**
     * Method "getCopyedFile" copies the source file into the destination file .
     * 
     * @param sourceFile
     * @param destinationFile
     * @return
     */
    public static IFile copyedToDestinationFile(IFile sourceFile, IFile destinationFile) {
        IFile desFile = destinationFile;
        try {
            if (destinationFile.exists()) {
                IFolder parentFolder = (IFolder) destinationFile.getParent();
                String fileName = desFile.getName();
                deleteFile(destinationFile);
                desFile = parentFolder.getFile(fileName);
            }

            sourceFile.copy(desFile.getFullPath(), true, new NullProgressMonitor());
        } catch (CoreException e) {
            e.printStackTrace();
        }
        return desFile;
    }

    public static IFile getTempRefreshFile() {
        IFolder folder = ResourcesPlugin.getWorkspace().getRoot().getProject(DQStructureManager.METADATA).getFolder(
                DQStructureManager.DB_CONNECTIONS);

        String fileName = TEMP_REFRESH_FILE;
        IFile file = folder.getFile(fileName);
        return file;
    }

    public static IFile getNeedReloadElementsFile() {
        IFolder folder = ResourcesPlugin.getWorkspace().getRoot().getProject(DQStructureManager.METADATA).getFolder(
                DQStructureManager.DB_CONNECTIONS);

        String fileName = NEED_RELOAD_ELEMENTS_PRV;
        IFile file = folder.getFile(fileName);
        return file;
    }

    /**
     * Method "deleteCopiedResourceFile".
     * 
     * @return true if temporary file ".refresh.prv" has been deleted (or did not exist)
     */
    public static boolean deleteCopiedResourceFile() {

        return deleteFile(getTempRefreshFile());
    }

    public static boolean deleteNeedReloadElementFile() {

        return deleteFile(getNeedReloadElementsFile());
    }

    public static IFile getDiffResourceFile() {
        IFolder folder = ResourcesPlugin.getWorkspace().getRoot().getProject(DQStructureManager.METADATA).getFolder(
                DQStructureManager.DB_CONNECTIONS);

        String fileName = RESULT_EMFDIFF_FILE;
        IFile file = folder.getFile(fileName);
        return file;
    }

    /**
     * To delete the file of "DB Connections" folder by the specific fileName.
     * 
     * @return
     */
    private static boolean deleteFile(IFile file) {
        boolean retValue = false;
        if (file.exists()) {
            URI uri = URI.createPlatformResourceURI(file.getFullPath().toString(), false);
            EMFSharedResources.getInstance().unloadResource(uri.toString());
            try {
                file.delete(true, new NullProgressMonitor());
                retValue = true;
            } catch (CoreException e) {
                log.warn("Problem while trying to delete temp file:" + file.getFullPath().toOSString(), e);
                retValue = false;
            }
        } else {
            retValue = true;
        }
        return retValue;
    }
}
