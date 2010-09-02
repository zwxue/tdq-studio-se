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

import java.io.File;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.talend.core.model.properties.Property;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.recycle.LogicalDeleteFileHandle;
import org.talend.resource.ResourceManager;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public class FolderHandle implements IDeletionHandle {

    private Property property;

    private String pathStr;

    /**
     * DOC bZhou FolderHandle constructor comment.
     * 
     * @param property
     */
    public FolderHandle(Property property) {
        this.property = property;
        this.pathStr = property.getItem().getState().getPath();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.action.actions.handle.IDeletionHandle#delete()
     */
    public boolean delete() throws Exception {

        IFolder folder = ResourceManager.getRoot().getFolder(new Path(pathStr));

        if (isPhysicalDelete()) {
            delsubFolderForever(folder);
            LogicalDeleteFileHandle.deleteElement(folder);
            folder.delete(true, null);
        } else {
            LogicalDeleteFileHandle.saveElement(LogicalDeleteFileHandle.folderType, folder.getFullPath().toOSString());
        }

        return true;
    }

    /**
     * DOC bZhou Comment method "delsubFolderForever".
     * 
     * @param fo
     * @throws CoreException
     */
    private void delsubFolderForever(IFolder fo) throws CoreException {
        IResource[] members = fo.members();
        for (IResource member : members) {
            if (member.getType() == IResource.FOLDER) {
                IFolder subFolder = (IFolder) member;
                // MOD qiongli 2010-8-5,bug 14697
                if (LogicalDeleteFileHandle.isStartWithDelFolder(subFolder.getFullPath().toOSString())) {
                    subFolder.delete(true, null);
                    LogicalDeleteFileHandle.replaceInFile(LogicalDeleteFileHandle.folderType
                            + subFolder.getFullPath().toOSString(), PluginConstant.EMPTY_STRING);

                } else {
                    delsubFolderForever(subFolder);
                }
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.action.actions.handle.IDeletionHandle#getDependencies()
     */
    public List<ModelElement> getDependencies() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.action.actions.handle.IDeletionHandle#isPhysicalDelete()
     */
    public boolean isPhysicalDelete() {
        return LogicalDeleteFileHandle.isStartWithDelFolder(File.separator + pathStr);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.action.actions.handle.IActionHandle#getProperty()
     */
    public Property getProperty() {
        return this.property;
    }

}
