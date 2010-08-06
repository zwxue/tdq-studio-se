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
package org.talend.dataprofiler.core.ui.action.actions.handle;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public class SimpleHandle implements IDuplicateHandle, IDeletionHandle {

    protected IFile file;

    /**
     * DOC bZhou DuplicateSimpleHandle constructor comment.
     */
    public SimpleHandle(IFile file) {
        this.file = file;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.action.actions.duplicate.IDuplicateHandle#duplicate()
     */
    public IFile duplicate() {
        IFile newFile = getNewFile(file);
        try {
            file.copy(newFile.getFullPath(), true, null);

            // create property
        } catch (CoreException e) {
            e.printStackTrace();
        }

        return newFile;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.action.actions.handle.IDeletionHandle#delete(boolean)
     */
    public boolean delete(boolean isPhysical) throws Exception {
        if (isPhysical && file.exists()) {
            file.delete(true, null);
        }
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.action.actions.handle.IDeletionHandle#getDependencies()
     */
    public List<ModelElement> getDependencies() {
        return null;
    }

    /**
     * DOC bZhou Comment method "getNewFile".
     * 
     * @param file
     * @return
     */
    private IFile getNewFile(IFile file) {
        IFile newFile = null;
        int idx = 1;
        while (true) {
            final String newFilename = "copy" + idx + "_" + file.getName(); //$NON-NLS-1$
            newFile = ((IFolder) file.getParent()).getFile(newFilename);
            if (!newFile.exists()) {
                break;
            }
            idx++;
        }
        return newFile;
    }
}
