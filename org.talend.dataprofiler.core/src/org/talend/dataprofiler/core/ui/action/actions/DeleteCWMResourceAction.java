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
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.DeleteResourceAction;
import org.eclipse.ui.ide.undo.DeleteResourcesOperation;
import org.eclipse.ui.ide.undo.WorkspaceUndoUtil;
import org.eclipse.ui.progress.WorkbenchJob;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.helper.AnaResourceFileHelper;
import org.talend.dataprofiler.core.helper.EObjectHelper;
import org.talend.dataprofiler.core.helper.PatternResourceFileHelper;
import org.talend.dataprofiler.core.helper.PrvResourceFileHelper;
import org.talend.dataprofiler.core.helper.RepResourceFileHelper;
import org.talend.dataprofiler.core.ui.dialog.message.DeleteModelElementConfirmDialog;
import org.talend.dataprofiler.core.ui.views.DQRespositoryView;
import org.talend.dataquality.domain.pattern.Pattern;
import org.talend.dataquality.reports.TdReport;
import org.talend.utils.sugars.TypedReturnCode;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * This action to delete the file which suffix is .rep/.ana/.prv files.
 */
public class DeleteCWMResourceAction extends DeleteResourceAction {

    private static Logger log = Logger.getLogger(DeleteCWMResourceAction.class);

    protected boolean isDeleteContent = false;

    private Shell shell;

    public DeleteCWMResourceAction() {
        super(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
        this.shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        setImageDescriptor(ImageLib.getImageDescriptor(ImageLib.DELETE_ACTION));
    }

    /*
     * (non-Javadoc) Method declared on IAction.
     */
    public void run() {
        deleteResource();
    }

    public void deleteResource() {

        final IResource[] resources = getSelectedResourcesArray();
        List<IContainer> parentList = new ArrayList<IContainer>();
        for (IResource resource : resources) {
            parentList.add(resource.getParent());
        }
        // WARNING: do not query the selected resources more than once
        // since the selection may change during the run,
        // e.g. due to window activation when the prompt dialog is dismissed.
        // For more details, see Bug 60606 [Navigator] (data loss) Navigator
        // deletes/moves the wrong file
        if (!checkDeleteContent(resources)) {
            return;
        }

        EObjectHelper.removeDependencys(resources);
        Job deletionCheckJob = new Job("Checking resources") {

            /*
             * (non-Javadoc)
             * 
             * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
             */
            protected IStatus run(IProgressMonitor monitor) {
                if (resources.length == 0) {
                    return Status.CANCEL_STATUS;
                }
                scheduleDeleteJob(resources);
                return Status.OK_STATUS;
            }

            /*
             * (non-Javadoc)
             * 
             * @see org.eclipse.core.runtime.jobs.Job#belongsTo(java.lang.Object)
             */
            public boolean belongsTo(Object family) {
                if ("Deleting resources".equals(family)) {
                    return true;
                }
                return super.belongsTo(family);
            }
        };

        deletionCheckJob.schedule();

        // refresh workspace in order to avoid unsynchronized resources
        for (IContainer container : parentList) {
            try {
                container.refreshLocal(IResource.DEPTH_INFINITE, null);
            } catch (CoreException e) {
                // Jump it
                e.printStackTrace();
            }
        }
        DQRespositoryView findView = (DQRespositoryView) CorePlugin.getDefault().findView(DQRespositoryView.ID);
        findView.getCommonViewer().refresh();
    }

    /**
     * Return an array of the currently selected resources.
     * 
     * @return the selected resources
     */
    @SuppressWarnings("unchecked")
    private IFile[] getSelectedResourcesArray() {
        DQRespositoryView findView = (DQRespositoryView) CorePlugin.getDefault().findView(DQRespositoryView.ID);
        TreeSelection treeSelection = (TreeSelection) findView.getCommonViewer().getSelection();
        List<IFile> selectedFiles = new ArrayList<IFile>();
        Iterator iterator = treeSelection.iterator();
        while (iterator.hasNext()) {
            Object obj = iterator.next();
            if (obj instanceof IFile) {
                IFile file = (IFile) obj;
                selectedFiles.add(file);
            } else {
                return new IFile[0];
            }
        }
        return selectedFiles.toArray(new IFile[selectedFiles.size()]);
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
            if (file.getFileExtension().equalsIgnoreCase(FactoriesUtil.PROV)) {
                TypedReturnCode<TdDataProvider> returnValue = PrvResourceFileHelper.getInstance().readFromFile(file);
                modelElement = returnValue.getObject();
                modelElementList.add(modelElement);
                anaMessageFlag = true;
            } else if (file.getFileExtension().equalsIgnoreCase(FactoriesUtil.ANA)) {
                modelElement = AnaResourceFileHelper.getInstance().readFromFile(file);
                modelElementList.add(modelElement);
                repMessageFlag = true;
            } else if (file.getFileExtension().equalsIgnoreCase(FactoriesUtil.PATTERN)) {
                Pattern pattern = PatternResourceFileHelper.getInstance().findPattern(file);
                modelElementList.add(pattern);
                anaMessageFlag = true;
            } else {
                otherFilesExistFlag = true;
                if (res.getFileExtension().equalsIgnoreCase(FactoriesUtil.REP)) {
                    TdReport findReport = RepResourceFileHelper.getInstance().findReport(file);
                    otherFileName = findReport.getName();
                } else {
                    otherFileName = file.getName();
                }
            }
        }
        if (modelElementList.size() > 0 && !otherFilesExistFlag) {

            if (anaMessageFlag && repMessageFlag) {
                dialogMessage = "The following analyses and reports will be unusable:";
            } else if (anaMessageFlag) {
                dialogMessage = "The following analyses will be unusable:";
            } else {
                dialogMessage = "The following reports will be unusable:";
            }
            int showDialog = DeleteModelElementConfirmDialog.showDialog(null, modelElementList
                    .toArray(new ModelElement[modelElementList.size()]), dialogMessage);
            isDeleteContent = showDialog == Window.OK;
        } else if (otherFilesExistFlag) {
            isDeleteContent = popConfirmDialog(otherFileName, selectedResources);
        }
        return isDeleteContent;
    }

    private boolean popConfirmDialog(String resourceName, IResource[] selectedResources) {
        if (selectedResources.length > 1) {
            isDeleteContent = MessageDialog.openConfirm(null, "Confirm Resource Delete", "Are you sure you want to delele these "
                    + selectedResources.length + " resources from file system?");
        } else {
            isDeleteContent = MessageDialog.openConfirm(null, "Confirm Resource Delete", "Are you sure you want to delele "
                    + "\"" + resourceName + "\" from file system?");
        }
        return isDeleteContent;
    }

    /**
     * Schedule a job to delete the resources to delete.
     * 
     * @param resourcesToDelete
     */
    private void scheduleDeleteJob(final IResource[] resourcesToDelete) {
        // use a non-workspace job with a runnable inside so we can avoid
        // periodic updates
        Job deleteJob = new Job("Deleting resources") {

            public IStatus run(final IProgressMonitor monitor) {
                try {
                    final DeleteResourcesOperation op = new DeleteResourcesOperation(resourcesToDelete, "Delete Resources",
                            isDeleteContent);
                    op.setModelProviderIds(getModelProviderIds());
                    // If we are deleting projects and their content, do not
                    // execute the operation in the undo history, since it cannot be
                    // properly restored. Just execute it directly so it won't be
                    // added to the undo history.
                    if (isDeleteContent) {
                        // We must compute the execution status first so that any user prompting
                        // or validation checking occurs. Do it in a syncExec because
                        // we are calling this from a Job.
                        WorkbenchJob statusJob = new WorkbenchJob("Status checking") { //$NON-NLS-1$

                            /*
                             * (non-Javadoc)
                             * 
                             * @see
                             * org.eclipse.ui.progress.UIJob#runInUIThread(org.eclipse.core.runtime.IProgressMonitor)
                             */
                            public IStatus runInUIThread(IProgressMonitor monitor) {
                                return op.computeExecutionStatus(monitor);
                            }

                        };

                        statusJob.setSystem(true);
                        statusJob.schedule();
                        try { // block until the status is ready
                            statusJob.join();
                        } catch (InterruptedException e) {
                            // Do nothing as status will be a cancel
                        }

                        if (statusJob.getResult().isOK()) {
                            return op.execute(monitor, WorkspaceUndoUtil.getUIInfoAdapter(shell));
                        }
                        return statusJob.getResult();
                    }
                    return PlatformUI.getWorkbench().getOperationSupport().getOperationHistory().execute(op, monitor,
                            WorkspaceUndoUtil.getUIInfoAdapter(shell));
                } catch (ExecutionException e) {
                    if (e.getCause() instanceof CoreException) {
                        return ((CoreException) e.getCause()).getStatus();
                    }
                    return new Status(IStatus.ERROR, "The IDE workbench plugin ID.", e.getMessage(), e);
                }
            }

            /*
             * (non-Javadoc)
             * 
             * @see org.eclipse.core.runtime.jobs.Job#belongsTo(java.lang.Object)
             */
            public boolean belongsTo(Object family) {
                if ("Deleting resources".equals(family)) {
                    return true;
                }
                return super.belongsTo(family);
            }

        };
        deleteJob.setUser(true);
        deleteJob.schedule();
    }

}
