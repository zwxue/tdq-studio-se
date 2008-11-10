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
package org.talend.dataprofiler.core.ui.action.actions;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.jfree.util.Log;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.dialog.message.DeleteModelElementConfirmDialog;
import org.talend.dataprofiler.core.ui.views.DQRespositoryView;
import org.talend.dataquality.domain.pattern.Pattern;
import org.talend.dataquality.reports.TdReport;
import org.talend.dq.helper.EObjectHelper;
import org.talend.dq.helper.resourcehelper.AnaResourceFileHelper;
import org.talend.dq.helper.resourcehelper.PatternResourceFileHelper;
import org.talend.dq.helper.resourcehelper.PrvResourceFileHelper;
import org.talend.dq.helper.resourcehelper.RepResourceFileHelper;
import org.talend.utils.sugars.TypedReturnCode;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * This action to delete the file which suffix is .rep/.ana/.prv files.
 */
public class DeleteCWMResourceAction extends Action {

    private static Logger log = Logger.getLogger(DeleteCWMResourceAction.class);

    private boolean isDeleteContent = false;

    private IFile[] selectedFiles;

    public DeleteCWMResourceAction(IFile[] files) {
        super("Delete"); //$NON-NLS-1$
        selectedFiles = files;
        setImageDescriptor(ImageLib.getImageDescriptor(ImageLib.DELETE_ACTION));
    }

    /*
     * (non-Javadoc) Method declared on IAction.
     */
    public void run() {
        deleteResource();
    }

    public void deleteResource() {

        final IResource[] resources = selectedFiles;
        if (!checkDeleteContent(resources)) {
            return;
        }
        delRelatedResource(isDeleteContent, resources);
        EObjectHelper.removeDependencys(resources);

        // refresh the parent resource in order to avoid unsynchronized resources
        for (IResource res : resources) {
            try {
                res.delete(true, new NullProgressMonitor());
                res.getParent().refreshLocal(IResource.DEPTH_INFINITE, null);
            } catch (CoreException e) {
                log.error(e, e);
            }
        }
        DQRespositoryView findView = (DQRespositoryView) CorePlugin.getDefault().findView(DQRespositoryView.ID);
        findView.getCommonViewer().refresh();
    }

    public boolean isFilesDeleted() {
        return isDeleteContent;
    }

    private boolean checkDeleteContent(IResource[] selectedResources) {
        List<ModelElement> modelElementList = new ArrayList<ModelElement>();
        IFile file;
        ModelElement modelElement;
        boolean otherFilesExistFlag = false;
        String otherFileName = null;
        boolean anaMessageFlag = false, repMessageFlag = false;
        String dialogMessage;
        for (IResource res : selectedResources) {
            if (!(res instanceof IFile)) {
                continue;
            } else {
                file = (IFile) res;
            }
            if (FactoriesUtil.PROV.equalsIgnoreCase(file.getFileExtension())) {
                TypedReturnCode<TdDataProvider> returnValue = PrvResourceFileHelper.getInstance().findProvider(file);
                modelElement = returnValue.getObject();
                modelElementList.add(modelElement);
                anaMessageFlag = true;
            } else if (FactoriesUtil.ANA.equalsIgnoreCase(file.getFileExtension())) {
                modelElement = AnaResourceFileHelper.getInstance().findAnalysis(file);
                modelElementList.add(modelElement);
                repMessageFlag = true;
            } else if (FactoriesUtil.PATTERN.equalsIgnoreCase(file.getFileExtension())) {
                Pattern pattern = PatternResourceFileHelper.getInstance().findPattern(file);
                modelElementList.add(pattern);
                anaMessageFlag = true;
            } else {
                otherFilesExistFlag = true;
                if (FactoriesUtil.REP.equalsIgnoreCase(res.getFileExtension())) {
                    TdReport findReport = RepResourceFileHelper.getInstance().findReport(file);
                    otherFileName = findReport.getName();
                } else {
                    otherFileName = file.getName();
                }
            }
        }
        if (modelElementList.size() > 0 && !otherFilesExistFlag) {

            if (anaMessageFlag && repMessageFlag) {
                dialogMessage = DefaultMessagesImpl.getString("DeleteCWMResourceAction.followingAnaAndRep"); //$NON-NLS-1$
            } else if (anaMessageFlag) {
                dialogMessage = DefaultMessagesImpl.getString("DeleteCWMResourceAction.followingAna"); //$NON-NLS-1$
            } else {
                dialogMessage = DefaultMessagesImpl.getString("DeleteCWMResourceAction.followngRep"); //$NON-NLS-1$
            }
            int showDialog = DeleteModelElementConfirmDialog.showDialog(null, modelElementList
                    .toArray(new ModelElement[modelElementList.size()]), dialogMessage);
            isDeleteContent = showDialog == Window.OK;
        } else if (otherFilesExistFlag) {
            isDeleteContent = popConfirmDialog(otherFileName, selectedResources);
        }
        return isDeleteContent;
    }

    private void delRelatedResource(boolean isDeleteContent, IResource[] selectedResources) {
        if (!isDeleteContent) {
            return;
        }

        for (IResource selectedResource : selectedResources) {
            if (selectedResource.getType() != IResource.FILE) {
                continue;
            }
            String folderName = null;

            // remove the unused folder related with current selected resources.
            if (PluginConstant.HTML_SUFFIX.equalsIgnoreCase(selectedResource.getFileExtension())) {
                folderName = selectedResource.getFullPath().lastSegment() + "_files"; //$NON-NLS-1$

            } else if (FactoriesUtil.REP.equalsIgnoreCase(selectedResource.getFileExtension())) {
                folderName = "." + selectedResource.getFullPath().removeFileExtension().lastSegment(); //$NON-NLS-1$
            } else {
                continue;
            }
            IFolder folder = ((IFolder) selectedResource.getParent()).getFolder(folderName);
            if (folder.exists()) {
                try {
                    folder.delete(true, null);
                } catch (CoreException e) {
                    Log
                            .warn(
                                    DefaultMessagesImpl.getString("DeleteCWMResourceAction.deleteFolderFail") + folder.getLocationURI().toString(), e); //$NON-NLS-1$
                }
            }
        }
    }

    private boolean popConfirmDialog(String resourceName, IResource[] selectedResources) {
        if (selectedResources.length > 1) {
            isDeleteContent = MessageDialog.openConfirm(null, DefaultMessagesImpl.getString("DeleteCWMResourceAction.confirm"), //$NON-NLS-1$
                    DefaultMessagesImpl.getString("DeleteCWMResourceAction.areYouDeleteResource")); //$NON-NLS-1$
        } else {
            isDeleteContent = MessageDialog
                    .openConfirm(
                            null,
                            DefaultMessagesImpl.getString("DeleteCWMResourceAction.confirmDeleteResource"), DefaultMessagesImpl.getString("DeleteCWMResourceAction.deleteResource", resourceName)); //$NON-NLS-1$ //$NON-NLS-2$
        }
        return isDeleteContent;
    }
}
