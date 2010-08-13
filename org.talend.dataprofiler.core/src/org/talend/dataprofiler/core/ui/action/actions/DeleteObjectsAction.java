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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TreeSelection;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.recycle.SelectedResources;
import org.talend.dataprofiler.core.ui.action.actions.handle.ActionHandleFactory;
import org.talend.dataprofiler.core.ui.action.actions.handle.IDeletionHandle;
import org.talend.dataprofiler.core.ui.dialog.message.DeleteModelElementConfirmDialog;
import org.talend.dataprofiler.core.ui.views.DQRespositoryView;
import org.talend.top.repository.ProxyRepositoryManager;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC rli class global comment. Detailled comment
 */
public class DeleteObjectsAction extends Action {

    private static Logger log = Logger.getLogger(DeleteObjectsAction.class);

    private boolean runStatus;

    private boolean isDeleteForever = false;

    /**
     * DOC rli DeleteObjectAction constructor comment.
     */
    public DeleteObjectsAction(boolean isDeleteForever) {
        if (isDeleteForever) {
            setText(DefaultMessagesImpl.getString("DeleteObjectsAction.deleteForever")); //$NON-NLS-1$
        } else {
            setText(DefaultMessagesImpl.getString("DeleteObjectsAction.logicalDelete")); //$NON-NLS-1$
        }
        setImageDescriptor(ImageLib.getImageDescriptor(ImageLib.DELETE_ACTION));
        setActionDefinitionId("org.talend.dataprofiler.core.removeAnalysis"); //$NON-NLS-1$
        this.isDeleteForever = isDeleteForever;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.action.Action#run()
     */
    @Override
    public void run() {

        // MOD qiongli feature 9486
        IFile[] selectedFiles;
        if (isDeleteForever) {
            SelectedResources selectedResources = new SelectedResources();

            selectedFiles = selectedResources.getSelectedResourcesArrayForDelForever();
        } else {
            selectedFiles = getSelectedResourcesArray();
        }
        // ~ 9486

        for (IFile file : selectedFiles) {

            IDeletionHandle handle = ActionHandleFactory.createDeletionHandle(file);

            List<ModelElement> dependencies = handle.getDependencies();

            if (dependencies != null && !dependencies.isEmpty()) {
                showDependenciesDialog(file, dependencies);
                return;
            }
        }

        try {

            for (IFile file : selectedFiles) {
                IDeletionHandle handle = ActionHandleFactory.createDeletionHandle(file);

                if (isDeleteForever && !showConfirmDialog()) {
                    return;
                }
                // MOD qiongli bug 14090
                CorePlugin.getDefault().closeEditorIfOpened(file);

                runStatus = handle.delete(isDeleteForever);
            }

        } catch (Exception e) {
            log.error(e, e);
        }

        ProxyRepositoryManager.getInstance().save();

        CorePlugin.getDefault().refreshDQView();

        CorePlugin.getDefault().refreshWorkSpace();
    }

    /**
     * DOC bZhou Comment method "showDependenciesDialog".
     * 
     * @param file
     * @param dependencies
     */
    private void showDependenciesDialog(IFile file, List<ModelElement> dependencies) {
        ModelElement[] dependencyElements = dependencies.toArray(new ModelElement[dependencies.size()]);
        DeleteModelElementConfirmDialog.showDialog(null, file, dependencyElements, DefaultMessagesImpl
                .getString("LogicalDeleteFileHandle.dependencyByOther"));
    }

    /**
     * DOC bZhou Comment method "showConfirmDialog".
     * 
     * @return
     */
    private boolean showConfirmDialog() {
        return MessageDialog.openConfirm(null, DefaultMessagesImpl.getString("DeleteObjectsAction.deleteForeverTitle"),
                DefaultMessagesImpl.getString("DeleteObjectsAction.areYouDeleteForever"));
    }

    /**
     * DOC bZhou Comment method "getRunStatus".
     * 
     * @return
     */
    public boolean getRunStatus() {
        return runStatus;
    }

    /**
     * Return an array of the currently selected resources.
     * 
     * @return the selected resources
     */
    @SuppressWarnings("unchecked")
    private IFile[] getSelectedResourcesArray() {
        DQRespositoryView findView = CorePlugin.getDefault().getRepositoryView();
        TreeSelection treeSelection = (TreeSelection) findView.getCommonViewer().getSelection();
        List<IFile> fileList = new ArrayList<IFile>();
        Iterator iterator = treeSelection.iterator();
        while (iterator.hasNext()) {
            Object obj = iterator.next();
            if (obj instanceof IFile) {
                IFile file = (IFile) obj;
                fileList.add(file);
            } else if (obj instanceof IFolder) {
                IFolder folder = (IFolder) obj;
                getAllSubFiles(folder, fileList);
            } else {
                return new IFile[0];
            }
        }
        return fileList.toArray(new IFile[fileList.size()]);
    }

    /**
     * DOC bZhou Comment method "getAllSubFiles".
     * 
     * @param folder
     * @param fileList
     */
    private void getAllSubFiles(IFolder folder, List<IFile> fileList) {
        IResource[] members = null;
        try {
            members = folder.members();
        } catch (CoreException e) {
            log.error(e, e);
        }
        for (IResource res : members) {
            if (res.getType() == IResource.FILE) {
                IFile file = (IFile) res;
                if (!StringUtils.equals(file.getFileExtension(), FactoriesUtil.PROPERTIES_EXTENSION)) {
                    fileList.add(file);
                }
            } else if (res.getType() == IResource.FOLDER) {
                getAllSubFiles((IFolder) res, fileList);
            }
        }

    }
}
