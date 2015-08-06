// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
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
import org.jfree.util.Log;
import org.talend.commons.exception.ExceptionHandler;
import org.talend.commons.exception.PersistenceException;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.manager.DQStructureManager;
import org.talend.dataprofiler.core.ui.views.DQRespositoryView;
import org.talend.dq.helper.RepositoryNodeHelper;
import org.talend.dq.nodes.DQRepositoryNode;
import org.talend.repository.model.IRepositoryNode;
import org.talend.resource.ResourceManager;

/**
 * This class is a wizard to create a folder on workspace.
 * 
 * @deprecated use org.talend.repository.ui.wizards.folder.FolderWizard instead.
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
            IRepositoryNode currentSelectionNode = CorePlugin.getDefault().getCurrentSelectionNode();
            IFolder folder = ResourcesPlugin.getWorkspace().getRoot().getFolder(path);
            IFolder newFolder = folder.getFolder(folderName);
            try {
                if (ResourceManager.getConnectionFolder().getFullPath().isPrefixOf(folder.getFullPath())) {
                    ProxyRepositoryFactory.getInstance().createFolder(ERepositoryObjectType.METADATA_CONNECTIONS,
                            path.makeRelativeTo(ResourceManager.getConnectionFolder().getFullPath()), folderName);
                } else if (ResourceManager.getMDMConnectionFolder().getFullPath().isPrefixOf(folder.getFullPath())) {
                    ProxyRepositoryFactory.getInstance().createFolder(ERepositoryObjectType.METADATA_MDMCONNECTION,
                            path.makeRelativeTo(ResourceManager.getMDMConnectionFolder().getFullPath()), folderName);
                } else {
                    newFolder.create(false, true, null);
                }
                folder.refreshLocal(IResource.DEPTH_INFINITE, null);
                DQRespositoryView findView = CorePlugin.getDefault().getRepositoryView();
                findView.getCommonViewer().setExpandedState(newFolder, true);
                findView.getCommonViewer().refresh(currentSelectionNode);

                // MOD gdbu 2011-11-18 TDQ-3969 : after create folder re-filter the tree , to create a new list .
                if (DQRepositoryNode.isOnFilterring()) {
                    RepositoryNodeHelper.fillTreeList(null);
                    RepositoryNodeHelper.setFilteredNode(RepositoryNodeHelper.getRootNode(
                            ERepositoryObjectType.TDQ_DATA_PROFILING, true));
                }

            } catch (CoreException e) {
                MessageDialog.openError(getShell(), DefaultMessagesImpl.getString("FolderWizard.error"), //$NON-NLS-1$
                        DefaultMessagesImpl.getString("FolderWizard.folderCreatedError")); //$NON-NLS-1$
                ExceptionHandler.process(e);
                return false;
            } catch (PersistenceException e) {
                Log.error(e, e);
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
