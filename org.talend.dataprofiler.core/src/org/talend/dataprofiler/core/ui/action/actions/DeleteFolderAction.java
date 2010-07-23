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

package org.talend.dataprofiler.core.ui.action.actions;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.recycle.LogicalDeleteFileHandle;
import org.talend.top.repository.ProxyRepositoryManager;

/**
 * DOC qzhang class global comment. Detailled comment <br/>
 * 
 * $Id: talend.epf 1 2006-09-29 17:06:40Z qzhang $
 * 
 */
public class DeleteFolderAction extends Action {

    protected static Logger log = Logger.getLogger(DeleteFolderAction.class);

    private IFolder folder;
    
	private boolean isDeleteForever = false;

    /**
     * DOC qzhang DeleteFolderAction constructor comment.
     */
    public DeleteFolderAction(IFolder obj,boolean isDeleteForever) {
        this.folder = obj;
        this.isDeleteForever = isDeleteForever;
        if(isDeleteForever){
        	setText(DefaultMessagesImpl.getString("DeleteObjectsAction.deleteForever")); //$NON-NLS-1$
        }else{
        	setText(DefaultMessagesImpl.getString("DeleteFolderAction.deleteFolder")); //$NON-NLS-1$
        }
        setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_DELETE));		
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.action.Action#run()
     */
    @Override
    public void run() {
        IFolder parent = (IFolder) folder.getParent();
        boolean isFilesDeleted = deleteFolderAndFiles();
        if (!isFilesDeleted) {
            return;
		}
		try {
			// MOD qiongli feature 9486
			if (isDeleteForever) {	
				delsubFolderForever(folder);
				if (folder.members().length==0){
					LogicalDeleteFileHandle.replaceInFile(
							LogicalDeleteFileHandle.folderType
									+ folder.getFullPath().toOSString(), "");
					folder.delete(true, null);
				}
			} else {
				for (IResource member:folder.members()){
					if (member instanceof IFolder) {
						LogicalDeleteFileHandle.saveElement(
								LogicalDeleteFileHandle.folderType,
								((IFolder) member).getFullPath().toOSString());
					}
				}
				LogicalDeleteFileHandle.saveElement(
						LogicalDeleteFileHandle.folderType, folder
								.getFullPath().toOSString());
			}
			CorePlugin.getDefault().refreshDQView();
			// ~
			ProxyRepositoryManager.getInstance().save();
			parent.refreshLocal(IResource.DEPTH_INFINITE, null);
		} catch (CoreException e) {
			log.error(e, e);
		}
    }

    private boolean deleteFolderAndFiles() {

        try {
            if (folder.members().length != 0) {
                DeleteObjectsAction action = new DeleteObjectsAction(isDeleteForever);
                action.run();
                return action.getRunStatus();
            }
        } catch (CoreException e) {
            log.error(e, e);
        }

        return MessageDialog.openConfirm(Display.getDefault().getActiveShell(), DefaultMessagesImpl
                .getString("DeleteFolderAction.deleteFold"), //$NON-NLS-1$
                DefaultMessagesImpl.getString("DeleteFolderAction.areYouDeleteFolder")); //$NON-NLS-1$

    }
    
    private void delsubFolderForever(IFolder fo) throws CoreException{
		IResource[] members = fo.members();
		for (IResource member : members) {
			if (member instanceof IFolder) {
				IFolder subFolder = (IFolder) member;
				if (subFolder.members().length == 0) {
					LogicalDeleteFileHandle.replaceInFile(
							LogicalDeleteFileHandle.folderType
									+ ((IFolder) member).getFullPath().toOSString(), "");
					subFolder.delete(true, null);
				} else {
					delsubFolderForever(subFolder);
				}
			}	
    	}
    }

}
