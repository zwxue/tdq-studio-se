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
package org.talend.dataprofiler.core.sql;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.manager.DQStructureManager;

/**
 * DOC qzhang class global comment. Detailled comment <br/>
 * 
 * $Id: talend.epf 1 2006-09-29 17:06:40Z nrousseau $
 * 
 */
public class DeleteSqlFileAction extends Action {

    private List<IFile> folder;

    /**
     * DOC qzhang AddSqlFileAction constructor comment.
     * 
     * @param selectedFiles
     */
    public DeleteSqlFileAction(List<IFile> selectedFiles) {
        setText(DefaultMessagesImpl.getString("DeleteSqlFileAction.delete")); //$NON-NLS-1$
        setImageDescriptor(ImageLib.getImageDescriptor(ImageLib.DELETE_ACTION));
        this.folder = selectedFiles;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.action.Action#run()
     */
    @Override
    public void run() {
        IFolder sourceFiles = ResourcesPlugin.getWorkspace().getRoot().getProject(DQStructureManager.LIBRARIES).getFolder(
                DQStructureManager.SOURCE_FILES);
        for (IFile file : folder) {
            if (MessageDialog.openConfirm(new Shell(), DefaultMessagesImpl.getString("DeleteSqlFileAction.deleteSqlFile"), DefaultMessagesImpl.getString("DeleteSqlFileAction.areYouDeleteSqlFile") + file.getName())) { //$NON-NLS-1$ //$NON-NLS-2$
                try {
                    if (file.exists()) {
                        file.delete(true, null);
                    }
                } catch (CoreException e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            sourceFiles.refreshLocal(IResource.DEPTH_INFINITE, null);
        } catch (CoreException e) {
            e.printStackTrace();
        }
    }

}
