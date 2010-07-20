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

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TreeSelection;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.core.model.properties.ItemState;
import org.talend.core.model.properties.Property;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.recycle.LogicalDeleteFileHandle;
import org.talend.dataprofiler.core.recycle.SelectedResources;
import org.talend.dataprofiler.core.ui.dialog.message.DeleteModelElementConfirmDialog;
import org.talend.dataprofiler.core.ui.views.DQRespositoryView;
import org.talend.dataquality.reports.AnalysisMap;
import org.talend.dataquality.reports.TdReport;
import org.talend.dq.factory.ModelElementFileFactory;
import org.talend.dq.helper.EObjectHelper;
import org.talend.dq.helper.PropertyHelper;
import org.talend.dq.helper.resourcehelper.RepResourceFileHelper;
import org.talend.dq.writer.EMFSharedResources;
import org.talend.resource.ResourceManager;
import org.talend.top.repository.ProxyRepositoryManager;
import org.talend.utils.sugars.ReturnCode;

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
        if(isDeleteForever){
        	setText(DefaultMessagesImpl.getString("DeleteObjectsAction.deleteForever")); //$NON-NLS-1$
        }else{
        	setText("Delete"); //$NON-NLS-1$
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

        //MOD qiongli feature 9486
		IFile[] selectedFiles;
		if (isDeleteForever) {
			SelectedResources selectedResources = new SelectedResources();
			selectedFiles = selectedResources
					.getSelectedResourcesArrayForDelForever();
		} else {
			selectedFiles = getSelectedResourcesArray();
		}//~

        for (IFile file : selectedFiles) {

            List<ModelElement> dependencies = getDependencies(file);

            if (!dependencies.isEmpty()) {
                showDependenciesDialog(file, dependencies);
                return;
            }
        }

		// MOD qiongli feature 9486,show the dialog only once
		try {
			if (isDeleteForever) {
				runStatus = showConfirmDialog();
				if (!runStatus)
					return;
				for (IFile file : selectedFiles) {
					String fileExt = file.getFileExtension();
					if (FactoriesUtil.isEmfFile(fileExt)) {
						ModelElementFileFactory.getResourceFileMap(file)
								.delete(file);
						LogicalDeleteFileHandle.replaceInFile(
								LogicalDeleteFileHandle.fileType+ file.getFullPath().toOSString(), "");
					} else {
						if (file.exists()) {
							file.delete(true, null);
						}
					}
					
				}
			} else {// Logical delete
				runStatus = showConfirmDialogLogicDel();
				if (!runStatus)
					return;
				for (IFile file : selectedFiles) {
					if (FactoriesUtil.isEmfFile(file.getFileExtension())) {
//						ModelElementFileFactory.getResourceFileMap(file)
//								.deleteLogical(file);
						LogicalDeleteFileHandle.deleteLogical(file);
					}
				}
			}
		} catch (Exception e) {
			log.error(e, e);
		}
		// ~
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
        DeleteModelElementConfirmDialog.showDialog(null, file, dependencyElements,
                "This item is depended by others! it can't be deleted!");
    }

    /**
     * DOC bZhou Comment method "showConfirmDialog".
     * 
     * @return
     */
    private boolean showConfirmDialog() {
        return MessageDialog.openConfirm(null, "Confirm Resource Delete",
                "Are you sure you want to delele this resource from file system?");
    }
    
    /**
     * DOC qiong Comment method "showConfirmDialog".
     * 
     * @return
     */
    private boolean showConfirmDialogLogicDel() {
        return MessageDialog.openConfirm(null, "Confirm  Delete to RecycleBin",
                "Are you sure you want to delele this resource to recycleBin?");
    }
 
    /**
     * DOC bZhou Comment method "getDependencies".
     * 
     * @param file
     * @return
     */
    private List<ModelElement> getDependencies(IFile file) {
        List<ModelElement> elementList = new ArrayList<ModelElement>();

        String fileExt = file.getFileExtension();

        if (FactoriesUtil.isEmfFile(fileExt)) {
            elementList.addAll(EObjectHelper.getDependencyClients(file));
        } else if (StringUtils.equals(fileExt, FactoriesUtil.JRXML)) {

            Collection<TdReport> allReports = RepResourceFileHelper.getInstance().getAllReports();
            for (TdReport report : allReports) {
                for (AnalysisMap anaMap : report.getAnalysisMap()) {
                    if (StringUtils.equals(file.getLocation().toOSString(), anaMap.getJrxmlSource())) {
                        elementList.add(report);
                    }
                }
            }
        }

        return elementList;
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
