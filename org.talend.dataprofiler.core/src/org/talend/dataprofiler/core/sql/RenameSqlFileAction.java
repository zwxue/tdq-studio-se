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
package org.talend.dataprofiler.core.sql;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.manager.DQStructureManager;

/**
 * DOC qzhang class global comment. Detailled comment <br/>
 * 
 * $Id: talend.epf 1 2006-09-29 17:06:40Z nrousseau $
 * 
 */
public class RenameSqlFileAction extends Action {

    private IFile folder;

    private String newname;

    private ArrayList<String> existNames;

    /**
     * DOC qzhang AddSqlFileAction constructor comment.
     * 
     * @param folder
     */
    public RenameSqlFileAction(IFile folder) {
        setText("Rename SQL File");
        setImageDescriptor(ImageLib.getImageDescriptor(ImageLib.CREATE_SQL_ACTION));
        this.folder = folder;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.action.Action#run()
     */
    @Override
    public void run() {
        IPath location = ResourcesPlugin.getWorkspace().getRoot().getLocation();
        IPath append = location.append(DQStructureManager.LIBRARIES).append(DQStructureManager.SOURCE_FILES);
        RenameDialog dialog = new RenameDialog(Display.getDefault().getActiveShell());
        IFolder sourceFiles = ResourcesPlugin.getWorkspace().getRoot().getProject(DQStructureManager.LIBRARIES).getFolder(
                DQStructureManager.SOURCE_FILES);
        existNames = new ArrayList<String>();
        try {
            getExistNames(sourceFiles, existNames);
            if (dialog.open() == RenameDialog.OK) {
                IPath addFileExtension = append.append(newname).addFileExtension(folder.getFileExtension());
                String newPath = addFileExtension.toPortableString();
                new File(folder.getLocation().toPortableString()).renameTo(new File(newPath));
            }
            folder.getParent().refreshLocal(IResource.DEPTH_ONE, null);
        } catch (CoreException e) {
            e.printStackTrace();
        }
    }

    /**
     * DOC qzhang Comment method "getExistNames".
     * 
     * @param sourceFiles
     * @return
     * @throws CoreException
     */
    private void getExistNames(IFolder sourceFiles, List<String> existNames) throws CoreException {
        boolean exists = sourceFiles.exists();
        if (exists) {
            IResource[] members = sourceFiles.members();
            for (IResource resource : members) {
                if (resource instanceof IFile && resource.getFileExtension().equals(folder.getFileExtension())) {
                    IPath removeFileExtension = resource.getFullPath().removeFileExtension();
                    existNames.add(removeFileExtension.lastSegment());
                } else if (resource instanceof IFolder) {
                    getExistNames((IFolder) resource, existNames);
                }
            }
        }
    }

    /**
     * DOC qzhang RenameSqlFileAction class global comment. Detailled comment <br/>
     * 
     * $Id: talend.epf 1 2006-09-29 17:06:40Z qzhang $
     * 
     */
    class RenameDialog extends TitleAreaDialog {

        /**
         * DOC qzhang RenameDialog constructor comment.
         * 
         * @param parentShell
         */
        protected RenameDialog(Shell parentShell) {
            super(parentShell);
            setShellStyle(getShellStyle() | SWT.RESIZE);
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
         */
        @Override
        protected void configureShell(Shell newShell) {
            super.configureShell(newShell);
            newShell.setSize(300, 300);
            newShell.setText("Rename SQL File");
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
         */
        @Override
        protected Control createDialogArea(Composite parent) {
            Composite createDialogArea = (Composite) super.createDialogArea(parent);
            Composite composite = new Composite(createDialogArea, SWT.NONE);
            GridLayout gridLayout = new GridLayout(2, false);
            composite.setLayout(gridLayout);
            GridData gridData = new GridData(GridData.FILL_BOTH);
            composite.setLayoutData(gridData);
            Label label = new Label(composite, SWT.NONE);
            label.setText("Set New Name:");
            final Text text = new Text(composite, SWT.BORDER);
            if (folder != null) {
                IPath removeFileExtension = folder.getFullPath().removeFileExtension();
                text.setText(removeFileExtension.lastSegment());
                newname = text.getText();
                existNames.remove(newname);
            }
            text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            text.addModifyListener(new ModifyListener() {

                /*
                 * (non-Javadoc)
                 * 
                 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
                 */
                public void modifyText(ModifyEvent e) {
                    newname = text.getText();
                    if (newname.length() == 0) {
                        getButton(IDialogConstants.OK_ID).setEnabled(false);
                        setErrorMessage("The Sql File Name can't be empty.");
                    } else if (existNames.contains(newname)) {
                        getButton(IDialogConstants.OK_ID).setEnabled(false);
                        setErrorMessage("The Sql File Name always exist, please input another name.");
                    } else {
                        setErrorMessage(null);
                        getButton(IDialogConstants.OK_ID).setEnabled(true);
                    }
                }
            });
            return createDialogArea;
        }
    }
}
