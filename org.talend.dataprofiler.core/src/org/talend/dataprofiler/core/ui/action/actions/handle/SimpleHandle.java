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
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
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
     * @see org.talend.dataprofiler.core.ui.action.actions.handle.IDuplicateHandle#duplicate(java.lang.String)
     */
    public IFile duplicate(String newLabel) {
        IPath newFileNamePath = new Path(newLabel).addFileExtension(file.getFileExtension());
        IFile newFile = file.getParent().getFile(newFileNamePath);

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
        if (file.exists() && isPhysicalDelete()) {
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

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.action.actions.handle.IDuplicateHandle#isExistedLabel(java.lang.String)
     */
    public boolean isExistedLabel(String label) {
        IPath path = file.getLocation();
        String fileExtension = path.getFileExtension();
        IPath newPath = path.removeLastSegments(1).append(label).addFileExtension(fileExtension);
        return newPath.toFile().exists();
    }
}
