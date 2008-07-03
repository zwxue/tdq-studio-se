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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Enumeration;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.undo.CreateProjectOperation;
import org.eclipse.ui.ide.undo.WorkspaceUndoUtil;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.exception.ExceptionHandler;
import org.talend.dataprofiler.core.ui.progress.ProgressUI;

/**
 * Create the folder structure for the DQ Reponsitory view.
 * 
 */
public final class DQStructureManager {

    private static final String DEMO_PATH = "/demo";

    private static final String PATTERN_PATH = "/patterns";

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
            IFolder patternFolder = this.createNewFoler(project, PATTERNS);
            // Copy the .pattern files from 'org.talend.dataprofiler.core/patterns' to folder "Libraries/Patterns".
            this.copyFilesToFolder(PATTERN_PATH, true, patternFolder);
            IFolder sqlSourceFolder = this.createNewFoler(project, SOURCE_FILES);
            // Copy the .sql files from 'org.talend.dataprofiler.core/demo' to folder "Libraries/Source Files".
            this.copyFilesToFolder(DEMO_PATH, true, sqlSourceFolder);

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

        return newProjectHandle;
    }

    private IFolder createNewFoler(IProject project, String folderName) throws CoreException {
        IFolder desFolder = project.getFolder(folderName);
        // ResourceAttributes attr = new ResourceAttributes();
        // attr.setReadOnly(true);
        // desFolder.setResourceAttributes(attr);
        if (!desFolder.exists()) {
            desFolder.create(false, true, null);
        }
        return desFolder;
    }

    /**
     * Copy the files from srcPath to destination folder.
     * 
     * @param srcPath The path name in which to look. The path is always relative to the root of this bundle and may
     * begin with &quot;/&quot;. A path value of &quot;/&quot; indicates the root of this bundle.
     * @param srcPath
     * @param recurse If <code>true</code>, recurse into subdirectories(contains directories). Otherwise only return
     * entries from the specified path.
     * @param desFolder
     * @throws IOException
     * @throws CoreException
     */
    @SuppressWarnings("unchecked")
    private void copyFilesToFolder(String srcPath, boolean recurse, IFolder desFolder) throws IOException, CoreException {
        Enumeration paths = null;
        paths = CorePlugin.getDefault().getBundle().getEntryPaths(srcPath);
        if (paths == null) {
            return;
        }
        while (paths.hasMoreElements()) {
            String nextElement = (String) paths.nextElement();
            String currentPath = "/" + nextElement;
            URL resourceURL = CorePlugin.getDefault().getBundle().getEntry(currentPath);
            URL fileURL = null;
            File file = null;
            try {
                fileURL = FileLocator.toFileURL(resourceURL);
                file = new File(fileURL.getFile());
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (file.isDirectory() && recurse) {
                if (file.getName().startsWith(".")) {
                    continue;
                }
                IFolder folder = desFolder.getFolder(file.getName());
                if (!folder.exists()) {
                    folder.create(false, true, null);
                }
                copyFilesToFolder(currentPath, recurse, folder);
                continue;
            }
            String fileName = new Path(fileURL.getPath()).lastSegment();
            InputStream openStream = null;
            openStream = fileURL.openStream();
            copyFileToFolder(openStream, fileName, desFolder);
        }

    }

    private void copyFileToFolder(InputStream inputStream, String fileName, IFolder folder) throws CoreException {
        if (inputStream == null) {
            return;
        }
        IFile file = folder.getFile(fileName);
        if (file.exists()) {
            return;
        }
        file.create(inputStream, false, null);
    }
}
