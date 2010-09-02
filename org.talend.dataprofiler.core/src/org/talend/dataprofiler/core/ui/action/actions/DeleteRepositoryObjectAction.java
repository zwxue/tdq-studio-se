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
import org.eclipse.jface.viewers.TreeSelection;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.commons.exception.BusinessException;
import org.talend.commons.exception.PersistenceException;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.recycle.SelectedResources;
import org.talend.dataprofiler.core.ui.dialog.message.DeleteModelElementConfirmDialog;
import org.talend.dataprofiler.core.ui.views.DQRespositoryView;
import org.talend.dq.factory.ModelElementFileFactory;
import org.talend.dq.helper.DQConnectionReposViewObjDelegator;
import org.talend.dq.helper.EObjectHelper;
import org.talend.repository.model.ProxyRepositoryFactory;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * 
 * DOC mzhao Delete repository object feature:14891
 */
public class DeleteRepositoryObjectAction extends Action {

    private static Logger log = Logger.getLogger(DeleteRepositoryObjectAction.class);

    private IRepositoryViewObject reposViewObj;

    private boolean isDeleteForever;

    public DeleteRepositoryObjectAction(boolean isDeleteForever, IRepositoryViewObject reposViewObj) {
        this.reposViewObj = reposViewObj;

        if (isDeleteForever) {
            setText(DefaultMessagesImpl.getString("DeleteObjectsAction.deleteForever")); //$NON-NLS-1$
        } else {
            setText(DefaultMessagesImpl.getString("DeleteObjectsAction.logicalDelete")); //$NON-NLS-1$
        }
        setImageDescriptor(ImageLib.getImageDescriptor(ImageLib.DELETE_ACTION));
        setActionDefinitionId("org.talend.dataprofiler.core.removeAnalysis"); //$NON-NLS-1$
        this.isDeleteForever = isDeleteForever;
    }

    @Override
    public void run() {
        IRepositoryViewObject[] selectedFiles;
        if (isDeleteForever) {
            SelectedResources selectedResources = new SelectedResources();

            selectedFiles = selectedResources.getSelectedObjectArrayForDelForever();
        } else {
            selectedFiles = getSelectedResourcesArray();
        }
        try {
            for (IRepositoryViewObject repositoryObject : selectedFiles) {
                // Item theItem = repositoryObject.getProperty().getItem();
                // IFile file = (IFile) ((ConnectionItem) theItem).getConnection().eResource();
                //                
                // IDeletionHandle handle = ActionHandleFactory.createDeletionHandle(file);
                // // ProxyRepositoryFactory.getInstance().
                List<ModelElement> dependencies = EObjectHelper.getDependencyClients(repositoryObject);
                // Item theItem = ((IRepositoryViewObject) obj).getProperty().getItem();
                // fileList.add((IFile) ((ConnectionItem) theItem).getConnection().eResource());
                ModelElement mElement = ModelElementFileFactory.getModelElement(repositoryObject);
                if (dependencies != null && !dependencies.isEmpty()) {
                    showDependenciesDialog(mElement, dependencies);
                    return;
                }

                // TODO there will to be changed when have more the element to be persistening.
                if (mElement instanceof Connection) {
                    reposViewObj = DQConnectionReposViewObjDelegator.getInstance().getRepositoryViewObject((Connection) mElement);
                    if (!isDeleteForever) {
                        ProxyRepositoryFactory.getInstance().deleteObjectLogical(reposViewObj);
                    } else {
                        ProxyRepositoryFactory.getInstance().deleteObjectPhysical(reposViewObj);
                    }
                } else {
                    continue;
                }
            }

            DQConnectionReposViewObjDelegator.getInstance().fetchRepositoryViewObjects(Boolean.TRUE);
            CorePlugin.getDefault().refreshDQView();
            CorePlugin.getDefault().refreshWorkSpace();
        } catch (PersistenceException e) {
            log.error(e, e);
        } catch (BusinessException e) {
            log.error(e, e);
        }
    }

    /**
     * Return an array of the currently selected resources.
     * 
     * @return the selected resources
     */
    @SuppressWarnings("unchecked")
    private IRepositoryViewObject[] getSelectedResourcesArray() {
        DQRespositoryView findView = CorePlugin.getDefault().getRepositoryView();
        TreeSelection treeSelection = (TreeSelection) findView.getCommonViewer().getSelection();
        List<IRepositoryViewObject> objectList = new ArrayList<IRepositoryViewObject>();
        Iterator iterator = treeSelection.iterator();
        while (iterator.hasNext()) {
            Object obj = iterator.next();
            // if (obj instanceof IFile) {
            // IFile file = (IFile) obj;
            // objectList.add(file);
            // } else if (obj instanceof IFolder) {
            // IFolder folder = (IFolder) obj;
            // getAllSubFiles(folder, objectList);
            // } else
            if (obj instanceof IRepositoryViewObject) {

                // Item theItem = ((IRepositoryViewObject) obj).getProperty().getItem();
                objectList.add((IRepositoryViewObject) obj);
                // PrvResourceFileHelper.getInstance().get
            }
        }
        return objectList.toArray(new IRepositoryViewObject[objectList.size()]);
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
     * DOC zshen Comment method "showDependenciesDialog".
     * 
     * @param file
     * @param dependencies
     */
    private void showDependenciesDialog(ModelElement modelElement, List<ModelElement> dependencies) {
        ModelElement[] dependencyElements = dependencies.toArray(new ModelElement[dependencies.size()]);
        DeleteModelElementConfirmDialog.showDialog(null, modelElement, dependencyElements, DefaultMessagesImpl
                .getString("LogicalDeleteFileHandle.dependencyByOther"));
    }
}
