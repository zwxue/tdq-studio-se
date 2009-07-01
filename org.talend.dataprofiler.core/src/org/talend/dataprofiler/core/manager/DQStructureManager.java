// ============================================================================
//
// Copyright (C) 2006-2009 Talend Inc. - www.talend.com
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

import org.apache.log4j.Logger;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.undo.CreateProjectOperation;
import org.eclipse.ui.ide.undo.WorkspaceUndoUtil;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.migration.helper.WorkspaceVersionHelper;
import org.talend.dataprofiler.core.ui.progress.ProgressUI;
import org.talend.dataquality.PluginConstant;
import org.talend.dataquality.exception.ExceptionHandler;
import org.talend.resource.ResourceManager;

/**
 * Create the folder structure for the DQ Reponsitory view.
 * 
 */
public final class DQStructureManager {

    protected static Logger log = Logger.getLogger(DQStructureManager.class);

    private static final String DEMO_PATH = "/demo"; //$NON-NLS-1$

    // MOD xqliu 2009-02-14 bug 6015
    public static final String DQ_RULES_PATH = "/dqrules"; //$NON-NLS-1$

    private static final String PATTERN_PATH = "/patterns"; //$NON-NLS-1$

    private static final String SQL_LIKE_PATH = "/sql_like";//$NON-NLS-1$

    private static final String EXCHANGE_PATH = "/exchange";//$NON-NLS-1$

    public static final String JRXML_REPORT_FOLDER = "JRXML Reports";//$NON-NLS-1$

    public static final String JRXML_FOLDER_PROPERTY = "JRXML_FOLDER_PROPERTY"; //$NON-NLS-1$

    public static final String REPORTS = DefaultMessagesImpl.getString("DQStructureManager.reports"); //$NON-NLS-1$

    public static final String SOURCE_FILES = DefaultMessagesImpl.getString("DQStructureManager.sourceFiles"); //$NON-NLS-1$

    public static final String PATTERNS = DefaultMessagesImpl.getString("DQStructureManager.patterns"); //$NON-NLS-1$

    public static final String SQL_PATTERNS = DefaultMessagesImpl.getString("DQStructureManager.sqlPatterns"); //$NON-NLS-1$

    public static final String EXCHANGE = "Exchange"; //$NON-NLS-1$

    public static final String INDICATORS = "Indicators"; //$NON-NLS-1$

    // MOD xqliu 2009-02-14 bug 6015
    public static final String DQ_RULES = DefaultMessagesImpl.getString("DQStructureManager.dqRules"); //$NON-NLS-1$

    public static final String ANALYSIS = DefaultMessagesImpl.getString("DQStructureManager.analyses"); //$NON-NLS-1$

    public static final QualifiedName FOLDER_CLASSIFY_KEY = new QualifiedName(CorePlugin.PLUGIN_ID, "FOLDER_CLASSIFY"); //$NON-NLS-1$

    public static final String ANALYSIS_FOLDER_PROPERTY = "FOLDER_ANALYSIS_PROPERTY"; //$NON-NLS-1$

    public static final String REPORT_FOLDER_PROPERTY = "FOLDER_REPORT_PROPERTY"; //$NON-NLS-1$

    public static final String PATTERNS_FOLDER_PROPERTY = "FOLDER_PATTERNS_PROPERTY"; //$NON-NLS-1$

    public static final String EXCHANGE_FOLDER_PROPERTY = "FOLDER_EXCHANGE_PROPERTY"; //$NON-NLS-1$

    public static final String INDICATORS_FOLDER_PROPERTY = "INDICATORS_FOLDER_PROPERTY"; //$NON-NLS-1$

    public static final String SQLPATTERNS_FOLDER_PROPERTY = "SQLPATTERNS_FOLDER_PROPERTY"; //$NON-NLS-1$

    public static final String SOURCEFILES_FOLDER_PROPERTY = "SOURCEFILES_FOLDER_PROPERTY"; //$NON-NLS-1$

    // MOD xqliu 2009-02-14 bug 6015
    public static final String DQRULES_FOLDER_PROPERTY = "DQRULES_FOLDER_PROPERTY"; //$NON-NLS-1$

    public static final String DBCONNECTION_FOLDER_PROPERTY = "DBCONNECTION_FOLDER_PROPERTY"; //$NON-NLS-1$

    public static final QualifiedName FOLDER_READONLY_KEY = new QualifiedName(CorePlugin.PLUGIN_ID, "FOLDER_READ_ONLY"); //$NON-NLS-1$

    public static final QualifiedName NO_SUBFOLDER_KEY = new QualifiedName(CorePlugin.PLUGIN_ID, "NO_SUBFOLDER"); //$NON-NLS-1$

    public static final QualifiedName PROJECT_TDQ_KEY = new QualifiedName(CorePlugin.PLUGIN_ID, "TDQ_PROJECT"); //$NON-NLS-1$

    public static final String FOLDER_READONLY_PROPERTY = "FOLDER_READONLY_PROPERTY"; //$NON-NLS-1$

    public static final String NO_SUBFOLDER_PROPERTY = "NO_SUBFOLDER_PROPERTY"; //$NON-NLS-1$

    public static final String PROJECT_TDQ_PROPERTY = "PROJECT_TDQ_PROPERTY"; //$NON-NLS-1$

    public static final String PREFIX_TDQ = "TDQ_"; //$NON-NLS-1$

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

        Plugin plugin = CorePlugin.getDefault();
        try {

            IProject rootProject = ResourceManager.getRootProject();
            if (!rootProject.exists()) {
                rootProject = createNewProject(ResourceManager.getRootProjectName());
            }

            // create "Data Profiling" project
            IFolder dataProfilingFolder = this.createNewFoler(rootProject, ResourceManager.DATA_PROFILING_FOLDER_NAME);
            IFolder createNewFoler = this.createNewFoler(dataProfilingFolder, ANALYSIS);
            createNewFoler.setPersistentProperty(FOLDER_CLASSIFY_KEY, ANALYSIS_FOLDER_PROPERTY);
            createNewFoler = this.createNewFoler(dataProfilingFolder, REPORTS);
            createNewFoler.setPersistentProperty(FOLDER_CLASSIFY_KEY, REPORT_FOLDER_PROPERTY);

            // create "Libraries" project
            IFolder librariesFoler = this.createNewFoler(rootProject, ResourceManager.LIBRARIES_FOLDER_NAME);
            createNewFoler = this.createNewFoler(librariesFoler, PATTERNS);
            createNewFoler.setPersistentProperty(FOLDER_CLASSIFY_KEY, PATTERNS_FOLDER_PROPERTY);
            // check version File
            WorkspaceVersionHelper.storeVersion();
            // Copy the .pattern files from
            // 'org.talend.dataprofiler.core/patterns' to folder
            // "Libraries/Patterns".
            this.copyFilesToFolder(plugin, PATTERN_PATH, true, createNewFoler, null);
            createNewFoler = this.createNewFoler(librariesFoler, SQL_PATTERNS);
            createNewFoler.setPersistentProperty(FOLDER_CLASSIFY_KEY, SQLPATTERNS_FOLDER_PROPERTY);

            // Copy the internet folder from
            // 'org.talend.dataprofiler.core/sql_like' to folder
            // "Libraries/SQL Patterns".
            this.copyFilesToFolder(plugin, SQL_LIKE_PATH, true, createNewFoler, null);
            createNewFoler = this.createNewFoler(librariesFoler, SOURCE_FILES);
            createNewFoler.setPersistentProperty(FOLDER_CLASSIFY_KEY, SOURCEFILES_FOLDER_PROPERTY);

            // Copy the .sql files from 'org.talend.dataprofiler.core/demo' to
            // folder "Libraries/Source Files".
            this.copyFilesToFolder(plugin, DEMO_PATH, true, createNewFoler, null);
            // MOD xqliu 2009-02-14 bug 6015
            createNewFoler = this.createNewFoler(librariesFoler, DQ_RULES);
            createNewFoler.setPersistentProperty(FOLDER_CLASSIFY_KEY, DQRULES_FOLDER_PROPERTY);
            // Copy the .sql files from 'org.talend.dataprofiler.core/dqrules'
            // to folder "Libraries/DQ Rules".
            this.copyFilesToFolder(plugin, DQ_RULES_PATH, true, createNewFoler, null);
            // ~

            // create exchange folder
            createNewFoler = this.createNewFoler(librariesFoler, EXCHANGE);
            createNewFoler.setPersistentProperty(FOLDER_CLASSIFY_KEY, EXCHANGE_FOLDER_PROPERTY);
            createNewFoler.setPersistentProperty(DQStructureManager.NO_SUBFOLDER_KEY, DQStructureManager.NO_SUBFOLDER_PROPERTY);

            // create indicators folder
            createNewFoler = this.createNewFoler(librariesFoler, INDICATORS);
            createNewFoler.setPersistentProperty(FOLDER_CLASSIFY_KEY, INDICATORS_FOLDER_PROPERTY);
            createNewFoler.setPersistentProperty(DQStructureManager.NO_SUBFOLDER_KEY, DQStructureManager.NO_SUBFOLDER_PROPERTY);

            // MOD mzhao 2009-04-07, create jrxml folder.
            checkJRXMLFolderExist();
            // create "Metadata" project
            IFolder metadataFolder = this.createNewFoler(rootProject, ResourceManager.METADATA_FOLDER_NAME);
            createNewFoler = this.createNewFoler(metadataFolder, PluginConstant.DB_CONNECTIONS);
            createNewFoler.setPersistentProperty(FOLDER_CLASSIFY_KEY, DBCONNECTION_FOLDER_PROPERTY);
            // ~
        } catch (Exception ex) {
            ExceptionHandler.process(ex);
            return false;
        }

        return true;
    }

    // MOD mzhao 2009-04-07
    public static IFolder checkJRXMLFolderExist() {
        IFolder folder = ResourceManager.getLibrariesFolder().getFolder(JRXML_REPORT_FOLDER);
        try {
            if (!folder.exists()) {
                folder.create(false, true, null);
            }
            folder.getParent().refreshLocal(IResource.DEPTH_INFINITE, null);
            folder.setPersistentProperty(DQStructureManager.FOLDER_READONLY_KEY, DQStructureManager.FOLDER_READONLY_PROPERTY);
            folder.setPersistentProperty(DQStructureManager.FOLDER_CLASSIFY_KEY, JRXML_FOLDER_PROPERTY);
        } catch (CoreException coreExp) {
            ExceptionHandler.process(coreExp);
        }
        return folder;
    }

    /**
     * Creates a new project resource with the special name.MOD mzhao 2009-03-18 make this method as public.For
     * {@link org.talend.dataprofiler.core.migration.impl.TDCPFolderMergeTask} use.
     * 
     * @return the created project resource, or <code>null</code> if the project was not created
     * @throws InterruptedException
     * @throws InvocationTargetException
     * @throws CoreException
     */
    public IProject createNewProject(String projectName) throws InvocationTargetException, InterruptedException, CoreException {

        // get a project handle
        final IProject newProjectHandle = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);

        // final IJavaProject javaProjHandle =
        // JavaCore.create(newProjectHandle);
        // get a project descriptor

        IWorkspace workspace = ResourcesPlugin.getWorkspace();
        final IProjectDescription description = workspace.newProjectDescription(newProjectHandle.getName());

        // create the new project operation
        IRunnableWithProgress op = new IRunnableWithProgress() {

            public void run(IProgressMonitor monitor) throws InvocationTargetException {
                CreateProjectOperation op = new CreateProjectOperation(description, DefaultMessagesImpl
                        .getString("DQStructureManager.createDataProfile")); //$NON-NLS-1$
                try {
                    PlatformUI.getWorkbench().getOperationSupport().getOperationHistory().execute(op, monitor,
                            WorkspaceUndoUtil.getUIInfoAdapter(null));
                } catch (ExecutionException e) {
                    throw new InvocationTargetException(e);
                }
            }
        };

        // run the new project creation o`peration
        // try {
        // MOD mzhao 2009-03-16 Feature 6066 First check whether project with
        // this name already exist or not. For TDCP
        // launching,
        // project always exist.
        if (!newProjectHandle.exists()) {
            ProgressUI.popProgressDialog(op);
        }
        newProjectHandle.setPersistentProperty(PROJECT_TDQ_KEY, PROJECT_TDQ_PROPERTY);
        return newProjectHandle;
    }

    /**
     * 
     * DOC mzhao Create new folder by project or folder.
     * 
     * @param parentFolder
     * @param folderName
     * @return
     * @throws CoreException
     */
    public IFolder createNewFoler(IContainer parent, String folderName) throws CoreException {
        IFolder desFolder = null;

        if (parent instanceof IProject) {
            desFolder = ((IProject) parent).getFolder(folderName);
        } else if (parent instanceof IFolder) {
            desFolder = ((IFolder) parent).getFolder(folderName);
        }
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
    public void copyFilesToFolder(Plugin plugin, String srcPath, boolean recurse, IFolder desFolder, String suffix)
            throws IOException, CoreException {
        if (plugin == null) {
            return;
        }

        Enumeration paths = null;
        paths = plugin.getBundle().getEntryPaths(srcPath);
        if (paths == null) {
            return;
        }
        while (paths.hasMoreElements()) {
            String nextElement = (String) paths.nextElement();
            String currentPath = "/" + nextElement; //$NON-NLS-1$
            URL resourceURL = plugin.getBundle().getEntry(currentPath);
            URL fileURL = null;
            File file = null;
            try {
                fileURL = FileLocator.toFileURL(resourceURL);
                file = new File(fileURL.getFile());
            } catch (IOException e) {
                log.error(e, e);
            }
            if (file.isDirectory() && recurse) {
                if (file.getName().startsWith(".")) { //$NON-NLS-1$
                    continue;
                }
                IFolder folder = desFolder.getFolder(file.getName());
                if (!folder.exists()) {
                    folder.create(true, true, null);
                }
                folder.setPersistentProperty(FOLDER_CLASSIFY_KEY, desFolder.getPersistentProperty(FOLDER_CLASSIFY_KEY));
                copyFilesToFolder(plugin, currentPath, recurse, folder, suffix);
                continue;
            }

            if (suffix != null && !file.getName().endsWith(suffix)) {
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
