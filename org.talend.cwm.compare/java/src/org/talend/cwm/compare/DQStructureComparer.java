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
import org.talend.dataprofiler.core.manager.DQStructureManager;

/**
 * DOC scorreia class global comment. Detailled comment
 */
public final class DQStructureComparer {

    /**
     * 
     */
    private static final String TEMP_PRV_FILE = ".refresh.prv";

    private static final Class<DQStructureComparer> THAT = DQStructureComparer.class;

    protected static Logger log = Logger.getLogger(THAT);

    private static DQStructureComparer comparer = new DQStructureComparer();

    public static DQStructureComparer getInstance() {
        return comparer;
    }

    private DQStructureComparer() {

    }

    /**
     * Method "copyCurrentResourceFile" copies the given file into the temporary file ".refresh.prv".
     * 
     * @param f the file to copy
     * @return the copy
     */
    @SuppressWarnings("restriction")
    public static IFile copyCurrentResourceFile(IFile f) {
        IFolder folder = ResourcesPlugin.getWorkspace().getRoot().getProject(DQStructureManager.METADATA).getFolder(
                DQStructureManager.DB_CONNECTIONS);

        String fileName = TEMP_PRV_FILE;
        IFile file = folder.getFile(fileName);
        try {
            f.copy(file.getFullPath(), true, new NullProgressMonitor());
        } catch (CoreException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return file;
    }

    public static IFile createTempConnectionFile() {
        IFolder folder = ResourcesPlugin.getWorkspace().getRoot().getProject(DQStructureManager.METADATA).getFolder(
                DQStructureManager.DB_CONNECTIONS);

        String fileName = TEMP_PRV_FILE;
        IFile file = folder.getFile(fileName);
        return file;
    }

    /**
     * Method "deleteCopiedResourceFile".
     * 
     * @return true if temporary file ".refresh.prv" has been deleted (or did not exist)
     */
    public static boolean deleteCopiedResourceFile() {

        boolean retValue = false;
        IFolder folder = ResourcesPlugin.getWorkspace().getRoot().getProject(DQStructureManager.METADATA).getFolder(
                DQStructureManager.DB_CONNECTIONS);

        String fileName = TEMP_PRV_FILE;
        IFile file = folder.getFile(fileName);
        if (file.exists()) {
            try {
                file.delete(true, new NullProgressMonitor());
                retValue = true;
            } catch (CoreException e) {
                log.warn("Problem while trying to delete temp file" + fileName, e);
                retValue = false;
            }
        } else {
            retValue = true;
        }
        return retValue;
    }
}
