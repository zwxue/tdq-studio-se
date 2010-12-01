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
import org.eclipse.core.runtime.Path;
import org.talend.core.model.properties.Property;
import org.talend.core.model.properties.TDQItem;
import org.talend.resource.ResourceManager;
import org.talend.utils.sugars.ReturnCode;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public class SimpleHandle implements IDuplicateHandle, IDeletionHandle {

    protected Property property;

    protected IFile file;

    /**
     * DOC bZhou DuplicateSimpleHandle constructor comment.
     */
    SimpleHandle(Property property) {
        this.property = property;

        TDQItem item = (TDQItem) property.getItem();
        file = ResourceManager.getRoot().getFile(new Path(item.getFilename()));
    }

    /**
     * DOC bZhou SimpleHandle constructor comment.
     * 
     * @param file
     */
    SimpleHandle(IFile file) {
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
     * @see org.talend.dataprofiler.core.ui.action.actions.handle.IDeletionHandle#delete()
     */
    public boolean delete() throws Exception {
        if (file.exists() && isPhysicalDelete() && !file.isReadOnly()) {
            file.delete(true, null);
        }

        return true;
    }

    /**
     * DOC bZhou Comment method "getNewFile".
     * 
     * @param file
     * @return
     */
    public static IFile getNewFile(IFile file) {
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

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.action.actions.handle.IDeletionHandle#getDependencies()
     */
    public List<ModelElement> getDependencies() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.action.actions.handle.IActionHandle#getProperty()
     */
    public Property getProperty() {
        return this.property;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.action.actions.handle.IDeletionHandle#isPhysicalDelete()
     */
    public boolean isPhysicalDelete() {
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.action.actions.handle.IDuplicateHandle#validDuplicated()
     */
    public ReturnCode validDuplicated() {
        return new ReturnCode(true);

    }
}
