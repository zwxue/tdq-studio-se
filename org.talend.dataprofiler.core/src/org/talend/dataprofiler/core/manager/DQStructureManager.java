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
package org.talend.dataprofiler.core.manager;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.undo.CreateProjectOperation;
import org.eclipse.ui.ide.undo.WorkspaceUndoUtil;
import org.talend.dataprofiler.core.exception.ExceptionHandler;
import org.talend.dataprofiler.core.ui.progress.ProgressUI;

/**
 * Create the folder structure for the DQ Reponsitory view.
 * 
 */
public final class DQStructureManager {

    public static final String REPORTS = "Reports";

    public static final String SOURCE_FILES = "Source Files";

    public static final String PATTERNS = "Patterns";

    public static final String LIBRARIES = "Libraries";

    public static final String METADATA = "Metadata";

    public static final String DATA_PROFILING = "Data Profiling";

    public static final String ANALYSIS = "Analysis";

    /**
     * String for the DB connections folder.
     */
    public static final String DB_CONNECTIONS = "DB Connections";

    private static DQStructureManager manager = new DQStructureManager();

    public static DQStructureManager getInstance() {
        return manager;
    }

    private DQStructureManager() {

    }

    public boolean createDQStructure() {

        Shell shell = PlatformUI.getWorkbench().getDisplay().getActiveShell();
        try {
            // create "Data Profiling" project
            IProject project = this.createNewProject(DATA_PROFILING, shell);
            this.createNewFoler(project, ANALYSIS);
            this.createNewFoler(project, REPORTS);
            // create "Libraries" project
            project = this.createNewProject(LIBRARIES, shell);
            this.createNewFoler(project, PATTERNS);
            this.createNewFoler(project, SOURCE_FILES);
            // create "Metadata" project
            project = this.createNewProject(METADATA, shell);
            this.createNewFoler(project, DB_CONNECTIONS);
        } catch (Exception ex) {
            ExceptionHandler.process(ex);
            return false;
        }
        return true;
    }

    /**
     * Creates a new project resource with the special name.
     * 
     * @return the created project resource, or <code>null</code> if the project was not created
     * @throws InterruptedException
     * @throws InvocationTargetException
     */
    private IProject createNewProject(String projectName, Shell shell) throws InvocationTargetException, InterruptedException {

        final Shell currentShell = shell;

        // get a project handle
        final IProject newProjectHandle = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);

        // final IJavaProject javaProjHandle = JavaCore.create(newProjectHandle);
        // get a project descriptor

        IWorkspace workspace = ResourcesPlugin.getWorkspace();
        final IProjectDescription description = workspace.newProjectDescription(newProjectHandle.getName());

        // create the new project operation
        IRunnableWithProgress op = new IRunnableWithProgress() {

            public void run(IProgressMonitor monitor) throws InvocationTargetException {
                CreateProjectOperation op = new CreateProjectOperation(description, "Create data profiling structure");
                try {
                    PlatformUI.getWorkbench().getOperationSupport().getOperationHistory().execute(op, monitor,
                            WorkspaceUndoUtil.getUIInfoAdapter(currentShell));
                } catch (ExecutionException e) {
                    throw new InvocationTargetException(e);
                }
            }
        };

        // run the new project creation o`peration
        // try {
        ProgressUI.popProgressDialog(op, shell);
        // } catch (InterruptedException e) {
        // return null;
        // } catch (InvocationTargetException e) {
        // Throwable t = e.getTargetException();
        // if (t instanceof ExecutionException
        // && t.getCause() instanceof CoreException) {
        // CoreException cause = (CoreException) t.getCause();
        // StatusAdapter status;
        // if (cause.getStatus().getCode() == IResourceStatus.CASE_VARIANT_EXISTS) {
        // status = new StatusAdapter(
        //
        // new Status(
        // IStatus.WARNING,
        // CorePlugin.PLUGIN_ID,
        // IStatus.WARNING,
        // "The underlying file system is case insensitive. There is an existing project which conflicts with"
        // + newProjectHandle.getName(), cause));
        // } else {
        // status = new StatusAdapter(new Status(cause.getStatus()
        // .getSeverity(), CorePlugin.PLUGIN_ID,
        // cause.getStatus().getSeverity(),
        // "Creation Problems", cause));
        // }
        // status.setProperty(StatusAdapter.TITLE_PROPERTY,
        // "Creation Problems");
        // StatusManager.getManager().handle(status, StatusManager.BLOCK);
        // } else {
        // StatusAdapter status = new StatusAdapter(new Status(
        // IStatus.WARNING, CorePlugin.PLUGIN_ID, 0,
        // "Internal error:" + t.getMessage(), t));
        // status.setProperty(StatusAdapter.TITLE_PROPERTY,
        // "Creation Problems");
        // StatusManager.getManager().handle(status,
        // StatusManager.LOG | StatusManager.BLOCK);
        // }
        // return null;
        // }

        return newProjectHandle;
    }

    private void createNewFoler(IProject project, String folderName) throws CoreException {
        IFolder desFolder = project.getFolder(folderName);
        // ResourceAttributes attr = new ResourceAttributes();
        // attr.setReadOnly(true);
        // desFolder.setResourceAttributes(attr);
        if (!desFolder.exists()) {
            desFolder.create(false, true, null);
        }
    }

}
