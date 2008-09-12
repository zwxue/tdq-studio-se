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

package org.talend.dataprofiler.core.ui.action.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

/**
 * DOC qzhang class global comment. Detailled comment <br/>
 * 
 * $Id: talend.epf 1 2006-09-29 17:06:40Z qzhang $
 * 
 */
public class DeleteFolderAction extends Action {

    private IFolder obj;

    /**
     * DOC qzhang DeleteFolderAction constructor comment.
     */
    public DeleteFolderAction(IFolder obj) {
        this.obj = obj;
        setText("Delete Folder");
        setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_DELETE));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.action.Action#run()
     */
    @Override
    public void run() {
        IFolder parent = (IFolder) obj.getParent();
        boolean isFilesDeleted = deleteFolderAndFiles();
        if (!isFilesDeleted) {
            return;
        }
        try {
            IFolder folder = parent.getFolder(obj.getName());
            folder.delete(true, null);
            parent.refreshLocal(IResource.DEPTH_INFINITE, null);
        } catch (CoreException e) {
            e.printStackTrace();
        }
    }

    private boolean deleteFolderAndFiles() {

        List<IFile> fileList = new ArrayList<IFile>();
        getAllSubFiles(obj, fileList);
        if (fileList.size() > 0) {
            IFile[] files = fileList.toArray(new IFile[fileList.size()]);
            DeleteCWMResourceAction action = new DeleteCWMResourceAction(files);
            action.run();
            return action.isFilesDeleted();
        }
        return MessageDialog.openConfirm(Display.getDefault().getActiveShell(), "Delete Folder",
                "Are you sure delete the folder, all sub folders and files?");

    }

    private void getAllSubFiles(IFolder folder, List<IFile> fileList) {
        IResource[] members = null;
        try {
            members = folder.members();
        } catch (CoreException e) {
            e.printStackTrace();
        }
        for (IResource res : members) {
            if (res.getType() == IResource.FILE) {
                fileList.add((IFile) res);
            } else if (res.getType() == IResource.FOLDER) {
                getAllSubFiles((IFolder) res, fileList);
            }
        }

    }
}
