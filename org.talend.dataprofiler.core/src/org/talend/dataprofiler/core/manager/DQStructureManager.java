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
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.undo.CreateProjectOperation;
import org.eclipse.ui.ide.undo.WorkspaceUndoUtil;
import org.talend.commons.emf.FactoriesUtil;
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

    public static final String SQL_PATTERNS = "SQL Patterns";

    public static final String LIBRARIES = "Libraries";

    public static final String METADATA = "Metadata";

    public static final String DATA_PROFILING = "Data Profiling";

    public static final String ANALYSIS = "Analyses";

    /**
     * String for the DB connections folder.
     */
    public static final String DB_CONNECTIONS = "DB Connections";

    public static final QualifiedName FOLDER_CLASSIFY_KEY = new QualifiedName(CorePlugin.PLUGIN_ID, "FOLDER_CLASSIFY");

    public static final String ANALYSIS_FOLDER_PROPERTY = "FOLDER_ANALYSIS_PROPERTY";

    public static final String REPORT_FOLDER_PROPERTY = "FOLDER_REPORT_PROPERTY";

    public static final String PATTERNS_FOLDER_PROPERTY = "FOLDER_PATTERNS_PROPERTY";

    public static final String SQLPATTERNS_FOLDER_PROPERTY = "SQLPATTERNS_FOLDER_PROPERTY";

    public static final String SOURCEFILES_FOLDER_PROPERTY = "SOURCEFILES_FOLDER_PROPERTY";

    public static final String DBCONNECTION_FOLDER_PROPERTY = "DBCONNECTION_FOLDER_PROPERTY";

    public static final QualifiedName FOLDER_READONLY_KEY = new QualifiedName(CorePlugin.PLUGIN_ID, "FOLDER_READ_ONLY");

    public static final String FOLDER_READONLY_PROPERTY = "FOLDER_READONLY_property";

    private List<String> modleElementSuffixs = null;

    private static DQStructureManager manager = new DQStructureManager();

    public static DQStructureManager getInstance() {
        return manager;
    }

    private DQStructureManager() {
        init();
    }

    private void init() {
        modleElementSuffixs = new ArrayList<String>();
        modleElementSuffixs.add(FactoriesUtil.ANA);
        modleElementSuffixs.add(FactoriesUtil.REP);
        modleElementSuffixs.add(FactoriesUtil.PROV);
    }

    public List<String> getModelElementSuffixs() {
        return modleElementSuffixs;
    }

    public boolean createDQStructure() {

        Shell shell = PlatformUI.getWorkbench().getDisplay().getActiveShell();
        try {
            // create "Data Profiling" project
            IProject project = this.createNewProject(DATA_PROFILING, shell);
            IFolder createNewFoler = this.createNewFoler(project, ANALYSIS);
            createNewFoler.setPersistentProperty(FOLDER_CLASSIFY_KEY, ANALYSIS_FOLDER_PROPERTY);
            createNewFoler = this.createNewFoler(project, REPORTS);
            createNewFoler.setPersistentProperty(FOLDER_CLASSIFY_KEY, REPORT_FOLDER_PROPERTY);

            // create "Libraries" project
            project = this.createNewProject(LIBRARIES, shell);
            createNewFoler = this.createNewFoler(project, PATTERNS);
            createNewFoler.setPersistentProperty(FOLDER_CLASSIFY_KEY, PATTERNS_FOLDER_PROPERTY);
            // Copy the .pattern files from 'org.talend.dataprofiler.core/patterns' to folder "Libraries/Patterns".
            this.copyFilesToFolder(PATTERN_PATH, true, createNewFoler);
            createNewFoler = this.createNewFoler(project, SQL_PATTERNS);
            createNewFoler.setPersistentProperty(FOLDER_CLASSIFY_KEY, SQLPATTERNS_FOLDER_PROPERTY);
            createNewFoler = this.createNewFoler(project, SOURCE_FILES);
            createNewFoler.setPersistentProperty(FOLDER_CLASSIFY_KEY, SOURCEFILES_FOLDER_PROPERTY);
            // Copy the .sql files from 'org.talend.dataprofiler.core/demo' to folder "Libraries/Source Files".
            this.copyFilesToFolder(DEMO_PATH, true, createNewFoler);

            // create "Metadata" project
            project = this.createNewProject(METADATA, shell);
            createNewFoler = this.createNewFoler(project, DB_CONNECTIONS);
            createNewFoler.setPersistentProperty(FOLDER_CLASSIFY_KEY, DBCONNECTION_FOLDER_PROPERTY);
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
        if (!desFolder.exists()) {
            desFolder.create(false, true, null);
        }
        desFolder.setPersistentProperty(FOLDER_READONLY_KEY, FOLDER_READONLY_PROPERTY);
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
                    folder.create(true, true, null);
                }
                folder.setPersistentProperty(FOLDER_CLASSIFY_KEY, desFolder.getPersistentProperty(FOLDER_CLASSIFY_KEY));
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

    public boolean isPathValid(IPath path, String label) {
        IFolder folder = ResourcesPlugin.getWorkspace().getRoot().getFolder(path);
        IFolder newFolder = folder.getFolder(label);
        return !newFolder.exists();
    }
}
