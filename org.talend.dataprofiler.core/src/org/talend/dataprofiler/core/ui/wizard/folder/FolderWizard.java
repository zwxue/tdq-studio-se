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
package org.talend.dataprofiler.core.ui.wizard.folder;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.Wizard;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.manager.DQStructureManager;
import org.talend.dataprofiler.core.ui.views.DQRespositoryView;
import org.talend.dataquality.exception.ExceptionHandler;

/**
 * This class is a wizard to create a folder on workspace.
 */
public class FolderWizard extends Wizard {

    /** Main page. */
    private FolderWizardPage mainPage;

    private IPath path;

    private final String defaultLabel;

    /**
     * Constructs a new NewProjectWizard.
     * 
     * @param author Project author.
     * @param server
     * @param password
     */
    public FolderWizard(IPath path, String defaultLabel) {
        super();
        this.path = path;
        this.defaultLabel = defaultLabel;
        setDefaultPageImageDescriptor(ImageLib.getImageDescriptor(ImageLib.FOLDER_WIZ_IMAGE));
    }

    /**
     * @see org.eclipse.jface.wizard.Wizard#addPages()
     */
    @Override
    public void addPages() {
        mainPage = new FolderWizardPage(defaultLabel);
        addPage(mainPage);
        setWindowTitle(DefaultMessagesImpl.getString("FolderWizard.newFolder")); //$NON-NLS-1$
    }

    /**
     * @see org.eclipse.jface.wizard.Wizard#performFinish()
     */
    @Override
    public boolean performFinish() {

        String folderName = mainPage.getName();
        if (defaultLabel == null) {
            IFolder folder = ResourcesPlugin.getWorkspace().getRoot().getFolder(path);
            IFolder newFolder = folder.getFolder(folderName);
            try {
                newFolder.create(false, true, null);
                newFolder.setPersistentProperty(DQStructureManager.FOLDER_CLASSIFY_KEY, folder
                        .getPersistentProperty(DQStructureManager.FOLDER_CLASSIFY_KEY));
                folder.refreshLocal(IResource.DEPTH_INFINITE, null);
                DQRespositoryView findView = (DQRespositoryView) CorePlugin.getDefault().findView(DQRespositoryView.ID);
                findView.getCommonViewer().refresh();
                findView.getCommonViewer().setExpandedState(newFolder, true);

            } catch (CoreException e) {
                MessageDialog.openError(getShell(), DefaultMessagesImpl.getString("FolderWizard.error"), //$NON-NLS-1$
                        DefaultMessagesImpl.getString("FolderWizard.folderCreatedError")); //$NON-NLS-1$
                ExceptionHandler.process(e);
                return false;
            }
        }
        // else {
        // folder.renameFolder(type, path, folderName);
        // }
        return true;
    }

    public boolean isValid(String folderName) {
        DQStructureManager manager = DQStructureManager.getInstance();
        return manager.isPathValid(path, folderName);
    }

}
