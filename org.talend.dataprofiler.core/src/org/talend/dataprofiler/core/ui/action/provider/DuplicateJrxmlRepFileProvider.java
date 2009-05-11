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
package org.talend.dataprofiler.core.ui.action.provider;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;

/**
 * DOC mzhao 2009-05-11, Feature 6894.
 */
public class DuplicateJrxmlRepFileProvider extends CommonActionProvider {

    @Override
    public void fillContextMenu(IMenuManager menu) {
        TreeSelection selection = (TreeSelection) this.getContext().getSelection();
        if (!selection.isEmpty()) {
            Object[] objs = selection.toArray();
            IFile[] files = new IFile[objs.length];
            for (int i = 0; i < objs.length; i++) {
                IFile file = (IFile) objs[i];
                files[i] = file;
            }
            DuplicateJrxmlRepFileAction duplicate = new DuplicateJrxmlRepFileAction(files);
            menu.add(duplicate);
        }
    }

    /**
     * 
     * DOC mzhao 2009-05-05, Feature:6894.
     */
    private class DuplicateJrxmlRepFileAction extends Action {

        private IFile[] files;

        public DuplicateJrxmlRepFileAction(IFile[] files) {
            super(DefaultMessagesImpl.getString("DuplicateCWMResourceAction.Duplicate")); //$NON-NLS-1$
            setImageDescriptor(ImageLib.getImageDescriptor(ImageLib.EDIT_COPY));
            this.files = files;
        }

        @Override
        public void run() {
            if (files != null && files.length > 0) {
                for (IFile file : files) {
                    IFile newFile = getNewFile(file);
                    try {
                        file.copy(newFile.getFullPath(), true, null);
                    } catch (CoreException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        private IFile getNewFile(IFile file) {
            IFile newFile = null;
            int idx = 1;
            while (true) {
                final String newFilename = "copy" + idx + file.getName(); //$NON-NLS-1$
                newFile = ((IFolder) file.getParent()).getFile(newFilename);
                if (!newFile.exists()) {
                    break;
                }
                idx++;
            }
            return newFile;
        }
    }

}
