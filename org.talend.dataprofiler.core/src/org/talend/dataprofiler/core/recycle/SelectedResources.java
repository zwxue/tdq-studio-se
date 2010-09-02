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
package org.talend.dataprofiler.core.recycle;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.TreeSelection;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.ui.views.DQRespositoryView;
import org.talend.dq.factory.ModelElementFileFactory;
import org.talend.dq.helper.PropertyHelper;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * @author qiongli Get all selected elements which used to delete forever
 */
public class SelectedResources {

    /**
	 * 
	 */
    public SelectedResources() {
    }

    /**
     * Return an array of the currently selected resources.
     * 
     * @return the selected resources
     */
    @SuppressWarnings("unchecked")
    public IFile[] getSelectedResourcesArrayForDelForever() {
        DQRespositoryView findView = CorePlugin.getDefault().getRepositoryView();
        TreeSelection treeSelection = (TreeSelection) findView.getCommonViewer().getSelection();
        List<IFile> fileList = new ArrayList<IFile>();
        Iterator iterator = treeSelection.iterator();
        while (iterator.hasNext()) {
            Object obj = iterator.next();
            if (obj instanceof DQRecycleBinNode) {
                DQRecycleBinNode rbn = (DQRecycleBinNode) obj;
                Object o = rbn.getObject();
                if (o instanceof IFile) {
                    Property property = PropertyHelper.getProperty((IFile) o);
                    if (property.getItem().getState().isDeleted())
                        fileList.add((IFile) o);
                } else if (o instanceof IFolder) {
                    getAllSubFilesByRecycleBinNode((IFolder) o, fileList);
                }
            } else if (obj instanceof IFile) {
                IFile file = (IFile) obj;
                fileList.add(file);
            } else if (obj instanceof IFolder) {
                IFolder folder = (IFolder) obj;
                getAllSubFiles(folder, fileList);
                // } else if (obj instanceof IRepositoryViewObject) {
                // Item theItem = ((IRepositoryViewObject) obj).getProperty().getItem();
                // fileList.add(ResourceManager.getRoot().getFile(
                // new Path(((ConnectionItem) theItem).getConnection().eResource().getURI().toPlatformString(false))));
                // // PrvResourceFileHelper.getInstance().get

            } else {
                return new IFile[0];
            }
        }
        return fileList.toArray(new IFile[fileList.size()]);
    }

    /**
     * Return an array of the currently selected object.
     * 
     * @return the selected object
     */
    @SuppressWarnings("unchecked")
    public IRepositoryViewObject[] getSelectedObjectArrayForDelForever() {
        DQRespositoryView findView = CorePlugin.getDefault().getRepositoryView();
        TreeSelection treeSelection = (TreeSelection) findView.getCommonViewer().getSelection();
        List<IRepositoryViewObject> objectList = new ArrayList<IRepositoryViewObject>();
        Iterator iterator = treeSelection.iterator();
        while (iterator.hasNext()) {
            Object obj = iterator.next();
            if (obj instanceof IRepositoryViewObject) {
                // Item theItem = ((IRepositoryViewObject) obj).getProperty().getItem();
                // fileList.add((IFile) ((ConnectionItem) theItem).getConnection().eResource());
                objectList.add((IRepositoryViewObject) obj);
                // PrvResourceFileHelper.getInstance().get
            }
        }
        return objectList.toArray(new IRepositoryViewObject[objectList.size()]);
    }

    /**
     * DOC qiongli Comment method "getAllSubFiles for ".
     * 
     * @param folder
     * @param fileList
     */
    private void getAllSubFilesByRecycleBinNode(IFolder folder, List<IFile> fileList) {
        IResource[] members = null;
        try {
            members = folder.members();
            for (Object o : members) {
                if (o instanceof IFile) {
                    IFile file = (IFile) o;
                    ModelElement mElement = ModelElementFileFactory.getModelElement(file);
                    if (mElement == null) {
                        continue;
                    }
                    Property property = PropertyHelper.getProperty(mElement);
                    if (property.getItem().getState().isDeleted())
                        fileList.add(file);
                } else if (o instanceof IFolder) {
                    getAllSubFilesByRecycleBinNode((IFolder) o, fileList);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * DOC qiongli Comment method "getAllSubFiles".
     * 
     * @param folder
     * @param fileList
     */
    private void getAllSubFiles(IFolder folder, List<IFile> fileList) {
        IResource[] members = null;
        try {
            members = folder.members();
        } catch (CoreException e) {
            e.printStackTrace();
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
