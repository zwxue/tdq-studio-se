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

package org.talend.dataprofiler.core.ui.action.actions;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;

/**
 * DOC qzhang class global comment. Detailled comment <br/>
 * 
 * $Id: talend.epf 1 2006-09-29 17:06:40Z qzhang $
 * 
 */
public class DeleteFolderAction extends Action {

    protected static Logger log = Logger.getLogger(DeleteFolderAction.class);

    private IFolder obj;

    /**
     * DOC qzhang DeleteFolderAction constructor comment.
     */
    public DeleteFolderAction(IFolder obj) {
        this.obj = obj;
        setText(DefaultMessagesImpl.getString("DeleteFolderAction.deleteFolder")); //$NON-NLS-1$
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
            log.error(e, e);
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
        return MessageDialog.openConfirm(Display.getDefault().getActiveShell(), DefaultMessagesImpl
                .getString("DeleteFolderAction.deleteFold"), //$NON-NLS-1$
                DefaultMessagesImpl.getString("DeleteFolderAction.areYouDeleteFolder")); //$NON-NLS-1$

    }

    private void getAllSubFiles(IFolder folder, List<IFile> fileList) {
        IResource[] members = null;
        try {
            members = folder.members();
        } catch (CoreException e) {
            log.error(e, e);
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
