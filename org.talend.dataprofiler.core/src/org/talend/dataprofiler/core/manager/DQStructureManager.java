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
import java.util.Enumeration;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IContainer;
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
import org.eclipse.core.runtime.Plugin;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.undo.CreateProjectOperation;
import org.eclipse.ui.ide.undo.WorkspaceUndoUtil;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.exception.ExceptionHandler;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.migration.helper.WorkspaceVersionHelper;
import org.talend.dataprofiler.core.ui.progress.ProgressUI;
import org.talend.resource.EResourceConstant;
import org.talend.resource.ResourceManager;
import org.talend.top.repository.ProxyRepositoryManager;
import org.talend.utils.ProductVersion;

/**
 * Create the folder structure for the DQ Reponsitory view.
 * 
 */
public final class DQStructureManager {

    protected static Logger log = Logger.getLogger(DQStructureManager.class);

    private static final String DEMO_PATH = "/demo"; //$NON-NLS-1$

    private static final String RULES_PATH = "/dqrules"; //$NON-NLS-1$

    private static final String PATTERN_PATH = "/patterns"; //$NON-NLS-1$

    private static final String SQL_LIKE_PATH = "/sql_like";//$NON-NLS-1$

    public static final String PREFIX_TDQ = "TDQ_"; //$NON-NLS-1$

    private static DQStructureManager manager;

    public static DQStructureManager getInstance() {
        if (manager == null) {
            manager = new DQStructureManager();
        }

        return manager;
    }

    /**
     * DOC bZhou DQStructureManager constructor comment.
     */
    private DQStructureManager() {
        ResourceManager.refreshStructure();
    }

    /**
     * DOC bZhou Comment method "createDQStructure".
     */
    public void createDQStructure() {

        Plugin plugin = CorePlugin.getDefault();
        try {

            IProject project = ResourceManager.getRootProject();
            if (!project.exists()) {
                project = createNewProject(ResourceManager.getRootProjectName());

            }

            IFolder dataProfilingFolder = createNewFolder(project, EResourceConstant.DATA_PROFILING);
            IFolder analysisFoler = createNewFolder(dataProfilingFolder, EResourceConstant.ANALYSIS);
            IFolder reportFoler = createNewFolder(dataProfilingFolder, EResourceConstant.REPORTS);

            IFolder librariesFoler = createNewFolder(project, EResourceConstant.LIBRARIES);
            IFolder patternFoler = createNewFolder(librariesFoler, EResourceConstant.PATTERNS);
            IFolder patternRegexFoler = createNewFolder(patternFoler, EResourceConstant.PATTERN_REGEX);
            IFolder patternSQLFoler = createNewFolder(patternFoler, EResourceConstant.PATTERN_SQL);
            IFolder sourceFileFoler = createNewFolder(librariesFoler, EResourceConstant.SOURCE_FILES);
            IFolder rulesFoler = createNewFolder(librariesFoler, EResourceConstant.RULES);
            IFolder rulesSQLFoler = createNewFolder(rulesFoler, EResourceConstant.RULES_SQL);
            IFolder exchangeFoler = createNewFolder(librariesFoler, EResourceConstant.EXCHANGE);
            IFolder indicatorFoler = createNewFolder(librariesFoler, EResourceConstant.INDICATORS);
            IFolder udiFoler = createNewFolder(indicatorFoler, EResourceConstant.USER_DEFINED_INDICATORS);
            IFolder jrxmlFolder = createNewFolder(librariesFoler, EResourceConstant.JRXML_TEMPLATE);

            IFolder metadataFolder = createNewFolder(project, EResourceConstant.METADATA);
            IFolder connectionFolder = createNewFolder(metadataFolder, EResourceConstant.DB_CONNECTIONS);
            IFolder mdmConnectionFolder = createNewFolder(metadataFolder, EResourceConstant.MDM_CONNECTIONS);

            copyFilesToFolder(plugin, PATTERN_PATH, true, patternRegexFoler, null);
            copyFilesToFolder(plugin, SQL_LIKE_PATH, true, patternSQLFoler, null);
            copyFilesToFolder(plugin, DEMO_PATH, true, sourceFileFoler, null);
            copyFilesToFolder(plugin, RULES_PATH, true, rulesSQLFoler, null);

            WorkspaceVersionHelper.storeVersion();

        } catch (Exception ex) {
            ExceptionHandler.process(ex);
            ProxyRepositoryManager.getInstance().save();
        }
    }

    /**
     * Method "isNeedCreateStructure" created by bzhou@talend.com.
     * 
     * @return true if need to create new resource structure.
     */
    public boolean isNeedCreateStructure() {
        if (isSecludedVersion()) {
            return !ResourceManager.checkSecludedResource();
        }

        return !ResourceManager.checkResource();
    }

    /**
     * DOC bZhou Comment method "isNeedMigration".
     * 
     * @return
     */
    public boolean isNeedMigration() {
        if (isSecludedVersion()) {
            return true;
        }

        ProductVersion wVersion = WorkspaceVersionHelper.getVesion();
        ProductVersion cVersion = CorePlugin.getDefault().getProductVersion();
        return wVersion.compareTo(cVersion) < 0;
    }

    /**
     * Method "isSecludedVersion" created by bzhou@talend.com.
     * 
     * @return true if version is before 3.0.0
     */
    private boolean isSecludedVersion() {
        return !WorkspaceVersionHelper.getVersionFile().exists();
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
                CreateProjectOperation createProjOp = new CreateProjectOperation(description, DefaultMessagesImpl
                        .getString("DQStructureManager.createDataProfile")); //$NON-NLS-1$
                try {
                    PlatformUI.getWorkbench().getOperationSupport().getOperationHistory().execute(createProjOp, monitor,
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
        // newProjectHandle.setPersistentProperty(PROJECT_TDQ_KEY, PROJECT_TDQ_PROPERTY);
        // TdqPropertieManager.getInstance().addFolderProperties(newProjectHandle, PROJECT_TDQ_KEY,
        // PROJECT_TDQ_PROPERTY);
        return newProjectHandle;
    }

    /**
     * DOC bzhou Comment method "createNewFolder".
     * 
     * @param parent
     * @param constant
     * @return
     * @throws CoreException
     */
    public IFolder createNewFolder(IContainer parent, EResourceConstant constant) throws CoreException {
        return createNewFolder(parent, constant.getName());
    }

    /**
     * Method "createNewFolder" creates a new folder.
     * 
     * @param parent
     * @param folderName
     * @return
     * @throws CoreException
     */
    public IFolder createNewFolder(IContainer parent, String folderName) throws CoreException {
        IFolder desFolder = null;

        if (parent instanceof IProject) {
            desFolder = ((IProject) parent).getFolder(folderName);
        } else if (parent instanceof IFolder) {
            desFolder = ((IFolder) parent).getFolder(folderName);
        }
        assert desFolder != null;

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
                if (file.isDirectory() && recurse) {
                    if (file.getName().startsWith(".")) { //$NON-NLS-1$
                        continue;
                    }
                    IFolder folder = desFolder.getFolder(file.getName());
                    if (!folder.exists()) {
                        folder.create(true, true, null);
                    }

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
            } catch (IOException e) {
                log.error(e, e);
            }
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
