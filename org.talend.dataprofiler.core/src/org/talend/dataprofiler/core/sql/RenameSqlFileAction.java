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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.views.navigator.ResourceComparator;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.manager.DQStructureManager;
import org.talend.dataprofiler.core.ui.dialog.FolderSelectionDialog;
import org.talend.dataprofiler.core.ui.dialog.filter.TypedViewerFilter;

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

    private IFolder sourceFiles;

    /**
     * DOC qzhang AddSqlFileAction constructor comment.
     * 
     * @param folder
     */
    public RenameSqlFileAction(IFile folder) {
        setText(DefaultMessagesImpl.getString("RenameSqlFileAction.renameSQLFile")); //$NON-NLS-1$
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
        RenameDialog dialog = new RenameDialog(Display.getDefault().getActiveShell());
        sourceFiles = (IFolder) folder.getParent();
        existNames = new ArrayList<String>();
        try {
            getExistNames(sourceFiles, existNames);
            if (dialog.open() == RenameDialog.OK) {
                IPath addFileExtension = sourceFiles.getLocation().append(newname).addFileExtension(folder.getFileExtension());
                String newPath = addFileExtension.toPortableString();
                new File(folder.getLocation().toPortableString()).renameTo(new File(newPath));
            }
            folder.getParent().refreshLocal(IResource.DEPTH_ONE, null);
            sourceFiles.refreshLocal(IResource.DEPTH_ONE, null);
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
                if (resource instanceof IFile && resource.getFileExtension() != null
                        && resource.getFileExtension().equals(folder.getFileExtension())) {
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
            newShell.setText(DefaultMessagesImpl.getString("RenameSqlFileAction.renameSQLFileTwo")); //$NON-NLS-1$
        }

        private Text pathText;

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
            GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
            composite.setLayoutData(gridData);
            Label label = new Label(composite, SWT.NONE);
            label.setText(DefaultMessagesImpl.getString("RenameSqlFileAction.setNewName")); //$NON-NLS-1$
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
                        setErrorMessage(DefaultMessagesImpl.getString("RenameSqlFileAction.sqlFileNameNotEmpty")); //$NON-NLS-1$
                    } else if (existNames.contains(newname)) {
                        getButton(IDialogConstants.OK_ID).setEnabled(false);
                        setErrorMessage(DefaultMessagesImpl.getString("RenameSqlFileAction.sqlFileAlwaysExist")); //$NON-NLS-1$
                    } else {
                        setErrorMessage(null);
                        getButton(IDialogConstants.OK_ID).setEnabled(true);
                    }
                }
            });
            Composite pathcomposite = new Composite(createDialogArea, SWT.NONE);
            gridLayout = new GridLayout(3, false);
            pathcomposite.setLayout(gridLayout);
            gridData = new GridData(GridData.FILL_HORIZONTAL);
            pathcomposite.setLayoutData(gridData);
            Label label2 = new Label(pathcomposite, SWT.NONE);
            label2.setText(DefaultMessagesImpl.getString("RenameSqlFileAction.path")); //$NON-NLS-1$
            pathText = new Text(pathcomposite, SWT.BORDER);
            pathText.setEnabled(false);
            pathText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            Button button = new Button(pathcomposite, SWT.PUSH);
            button.setText(DefaultMessagesImpl.getString("RenameSqlFileAction.select")); //$NON-NLS-1$

            pathText.setText(folder.getParent().getFullPath().toString());
            button.addSelectionListener(new SelectionAdapter() {

                /*
                 * (non-Javadoc)
                 * 
                 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
                 */
                @Override
                public void widgetSelected(SelectionEvent e) {
                    final Class[] acceptedClasses = new Class[] { IProject.class, IFolder.class };
                    IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
                    ArrayList rejectedElements = new ArrayList();

                    IProject theProject = root.getProject(DQStructureManager.getLibraries());
                    IProject[] allProjects = root.getProjects();
                    for (int i = 0; i < allProjects.length; i++) {
                        if (!allProjects[i].equals(theProject)) {
                            rejectedElements.add(allProjects[i]);
                        }
                    }

                    try {
                        IResource[] resourse = theProject.members();
                        for (IResource one : resourse) {
                            if (one.getType() == IResource.FOLDER && !one.getName().equals(DQStructureManager.SOURCE_FILES)) {
                                rejectedElements.add(one);
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                    ViewerFilter filter = new TypedViewerFilter(acceptedClasses, rejectedElements.toArray());

                    ILabelProvider lp = new WorkbenchLabelProvider();
                    ITreeContentProvider cp = new WorkbenchContentProvider();

                    FolderSelectionDialog dialog = new FolderSelectionDialog(getShell(), lp, cp);
                    // dialog.setValidator(validator);
                    dialog.setTitle(DefaultMessagesImpl.getString("RenameSqlFileAction.selectFolder")); //$NON-NLS-1$
                    dialog.setMessage(DefaultMessagesImpl.getString("RenameSqlFileAction.selectFolderInItem")); //$NON-NLS-1$
                    dialog.setInput(root);
                    dialog.addFilter(filter);
                    dialog.setComparator(new ResourceComparator(ResourceComparator.NAME));

                    if (dialog.open() == Window.OK) {
                        Object elements = dialog.getResult()[0];
                        IResource elem = (IResource) elements;
                        if (elem instanceof IFolder) {
                            pathText.setText(elem.getFullPath().toString());
                            sourceFiles = (IFolder) elem;
                        }
                    }
                }

            });
            return createDialogArea;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.eclipse.jface.dialogs.Dialog#okPressed()
         */
        @Override
        protected void okPressed() {
            super.okPressed();
        }
    }
}
