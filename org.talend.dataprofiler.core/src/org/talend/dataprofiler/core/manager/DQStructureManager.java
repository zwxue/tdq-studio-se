// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
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
import org.talend.commons.bridge.ReponsitoryContextBridge;
import org.talend.commons.exception.PersistenceException;
import org.talend.commons.utils.VersionUtils;
import org.talend.core.context.Context;
import org.talend.core.model.properties.ByteArray;
import org.talend.core.model.properties.FolderItem;
import org.talend.core.model.properties.Project;
import org.talend.core.model.properties.PropertiesFactory;
import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.Folder;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.core.runtime.CoreRuntimePlugin;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.exception.ExceptionHandler;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.migration.helper.WorkspaceVersionHelper;
import org.talend.dataprofiler.core.ui.progress.ProgressUI;
import org.talend.dataquality.PluginConstant;
import org.talend.dataquality.properties.TDQSourceFileItem;
import org.talend.dq.factory.ModelElementFileFactory;
import org.talend.dq.writer.AElementPersistance;
import org.talend.dq.writer.impl.ElementWriterFactory;
import org.talend.repository.ProjectManager;
import org.talend.repository.model.IProxyRepositoryFactory;
import org.talend.resource.EResourceConstant;
import org.talend.resource.ResourceManager;
import org.talend.resource.ResourceService;
import org.talend.top.repository.ProxyRepositoryManager;
import org.talend.utils.ProductVersion;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * Create the folder structure for the DQ Reponsitory view.
 * 
 */
public final class DQStructureManager {

    protected static Logger log = Logger.getLogger(DQStructureManager.class);

    private static final String DEMO_PATH = "/demo"; //$NON-NLS-1$

    public static final String RULES_PATH = "/dqrules"; //$NON-NLS-1$

    private static final String PATTERN_PATH = "/patterns"; //$NON-NLS-1$

    private static final String SQL_LIKE_PATH = "/sql_like";//$NON-NLS-1$

    public static final String SYSTEM_INDICATOR_PATH = "/indicators";//$NON-NLS-1$

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
        ResourceService.refreshStructure();
    }

    /**
     * DOC bZhou Comment method "getCurrentProject".
     * 
     * @return
     */
    public Project getCurrentProject() {
        Context ctx = CoreRuntimePlugin.getInstance().getContext();
        return (Project) ctx.getProperty(Context.REPOSITORY_CONTEXT_KEY);
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

            if (!project.getFolder(EResourceConstant.DB_CONNECTIONS.getPath()).exists()) {
                ProxyRepositoryFactory.getInstance().createFolder(ERepositoryObjectType.METADATA, Path.EMPTY,
                        EResourceConstant.DB_CONNECTIONS.getName());
            }

            if (!project.getFolder(EResourceConstant.MDM_CONNECTIONS.getPath()).exists()) {
                ProxyRepositoryFactory.getInstance().createFolder(ERepositoryObjectType.METADATA, Path.EMPTY,
                        EResourceConstant.MDM_CONNECTIONS.getName());
            }

            if (!project.getFolder(EResourceConstant.FILEDELIMITED.getPath()).exists()) {
                ProxyRepositoryFactory.getInstance().createFolder(ERepositoryObjectType.METADATA, Path.EMPTY,
                        EResourceConstant.FILEDELIMITED.getName());
            }

            ProxyRepositoryFactory.getInstance().createFolder(ERepositoryObjectType.TDQ_DATA_PROFILING, Path.EMPTY,
                    EResourceConstant.ANALYSIS.getName());

            if (!ReponsitoryContextBridge.isDefautProject()) {
                Folder reportFoler = ProxyRepositoryFactory.getInstance().createFolder(ERepositoryObjectType.TDQ_DATA_PROFILING,
                        Path.EMPTY, EResourceConstant.REPORTS.getName());
            }

            Folder patternFoler = ProxyRepositoryFactory.getInstance().createFolder(ERepositoryObjectType.TDQ_LIBRARIES,
                    Path.EMPTY, EResourceConstant.PATTERNS.getName());

            Folder rulesFoler = ProxyRepositoryFactory.getInstance().createFolder(ERepositoryObjectType.TDQ_LIBRARIES,
                    Path.EMPTY, EResourceConstant.RULES.getName());
            Folder rulesSQLFoler = ProxyRepositoryFactory.getInstance().createFolder(ERepositoryObjectType.TDQ_RULES, Path.EMPTY,
                    EResourceConstant.RULES_SQL.getName());

            Folder exchangeFoler = ProxyRepositoryFactory.getInstance().createFolder(ERepositoryObjectType.TDQ_LIBRARIES,
                    Path.EMPTY, EResourceConstant.EXCHANGE.getName());

            Folder indicatorFoler = ProxyRepositoryFactory.getInstance().createFolder(ERepositoryObjectType.TDQ_LIBRARIES,
                    Path.EMPTY, EResourceConstant.INDICATORS.getName());
            Folder systemIndicatorFoler = ProxyRepositoryFactory.getInstance().createFolder(
                    ERepositoryObjectType.TDQ_INDICATOR_ELEMENT, Path.EMPTY, EResourceConstant.SYSTEM_INDICATORS.getName());

            Folder udiFoler = ProxyRepositoryFactory.getInstance().createFolder(ERepositoryObjectType.TDQ_INDICATOR_ELEMENT,
                    Path.EMPTY, EResourceConstant.USER_DEFINED_INDICATORS.getName());
            // MOD zhsne 18724: Java UDI enhancements add lib folder under UDI folder.
            Folder udiLibFoler = ProxyRepositoryFactory.getInstance().createFolder(
                    ERepositoryObjectType.TDQ_USERDEFINE_INDICATORS, Path.EMPTY,
                    EResourceConstant.USER_DEFINED_INDICATORS_LIB.getName());

            Folder jrxmlFolder = ProxyRepositoryFactory.getInstance().createFolder(ERepositoryObjectType.TDQ_LIBRARIES,
                    Path.EMPTY, EResourceConstant.JRXML_TEMPLATE.getName());

            Folder patternRegexFoler = ProxyRepositoryFactory.getInstance().createFolder(
                    ERepositoryObjectType.TDQ_PATTERN_ELEMENT, Path.EMPTY, EResourceConstant.PATTERN_REGEX.getName());

            Folder patternSQLFoler = ProxyRepositoryFactory.getInstance().createFolder(ERepositoryObjectType.TDQ_PATTERN_ELEMENT,
                    Path.EMPTY, EResourceConstant.PATTERN_SQL.getName());

            Folder sourceFileFoler = ProxyRepositoryFactory.getInstance().createFolder(ERepositoryObjectType.TDQ_LIBRARIES,
                    Path.EMPTY, EResourceConstant.SOURCE_FILES.getName());

            // use the tos create folder API
            copyFilesToFolder(plugin, SYSTEM_INDICATOR_PATH, true, systemIndicatorFoler, null,
                    ERepositoryObjectType.TDQ_SYSTEM_INDICATORS);
            copyFilesToFolder(plugin, PATTERN_PATH, true, patternRegexFoler, null, ERepositoryObjectType.TDQ_PATTERN_REGEX);
            copyFilesToFolder(plugin, SQL_LIKE_PATH, true, patternSQLFoler, null, ERepositoryObjectType.TDQ_PATTERN_SQL);
            copyFilesToFolder(plugin, DEMO_PATH, true, sourceFileFoler, null, ERepositoryObjectType.TDQ_SOURCE_FILE_ELEMENT);
            copyFilesToFolder(plugin, RULES_PATH, true, rulesSQLFoler, null, ERepositoryObjectType.TDQ_RULES_SQL);

            WorkspaceVersionHelper.storeVersion();

            ResourceService.refreshStructure();

        } catch (Exception ex) {
            ExceptionHandler.process(ex);
            ProxyRepositoryManager.getInstance().save();
        }
    }

    /**
     * 
     * DOC klliu Comment method "copyFilesToFolder".
     * 
     * @param plugin
     * @param srcPath
     * @param recurse
     * @param desFolder
     * @param suffix
     * @param type
     * @throws IOException
     * @throws CoreException
     */
    public void copyFilesToFolder(Plugin plugin, String srcPath, boolean recurse, Folder desFolder, String suffix,
            ERepositoryObjectType type) throws IOException, CoreException {
        if (plugin == null) {
            return;
        }

        IProject project = ResourceManager.getRootProject();
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
                    Folder folder = null;
                    if (!project.getFolder(file.getPath()).exists()) {
                        folder = ProxyRepositoryFactory.getInstance().createFolder(type, Path.EMPTY, file.getName());
                    } else {
                        IPath fullPath = new Path(file.getPath());
                        int count = fullPath.segmentCount();
                        FolderItem folderItem = ProxyRepositoryFactory.getInstance().getFolderItem(
                                ProjectManager.getInstance().getCurrentProject(), type, fullPath.removeFirstSegments(count - 1));

                        if (folderItem == null) {
                            folder = ProxyRepositoryFactory.getInstance().createFolder(type, Path.EMPTY, file.getName());
                        } else {
                            folder = new Folder(folderItem.getProperty(), type);
                        }
                    }
                    copyFilesToFolder(plugin, currentPath, recurse, folder, suffix, type);
                    continue;
                }

                if (suffix != null && !file.getName().endsWith(suffix)) {
                    continue;
                }

                String fileName = new Path(fileURL.getPath()).lastSegment();
                InputStream openStream = fileURL.openStream();
                String folderName = null;

                if (type.equals(ERepositoryObjectType.TDQ_PATTERN_ELEMENT)) {
                    folderName = ERepositoryObjectType.getFolderName(type);
                }
                if (type.equals(ERepositoryObjectType.TDQ_RULES_SQL)) {
                    folderName = ERepositoryObjectType.getFolderName(type);
                } else if (type.equals(ERepositoryObjectType.TDQ_SOURCE_FILE_ELEMENT)) {
                    folderName = ERepositoryObjectType.getFolderName(type);
                } else {
                    folderName = ERepositoryObjectType.getFolderName(type) + "/" + desFolder.getLabel();
                }
                if (folderName.equals("date")) {
                    continue;
                }
                IFolder folder = project.getFolder(folderName);
                if (type.equals(ERepositoryObjectType.TDQ_SOURCE_FILE_ELEMENT)) {
                    String name = file.getName();
                    int indexOf = name.indexOf(".");
                    String label = name.substring(0, indexOf);
                    String extendtion = name.substring(indexOf + 1);
                    createSourceFileItem(file, Path.EMPTY, label, extendtion);
                } else {
                    copyFileToFolder(openStream, fileName, folder);
                }

                openStream.close();
            } catch (IOException e) {
                log.error(e, e);
            } catch (PersistenceException e) {
                log.error(e, e);
            }
        }
    }

    /**
     * 
     * DOC klliu Comment method "copyFileToFolder".
     * 
     * @param inputStream
     * @param fileName
     * @param folder
     * @throws CoreException
     * @throws IOException
     */
    private void copyFileToFolder(InputStream inputStream, String fileName, IFolder folder, boolean... isImportItem)
            throws CoreException, IOException {
        if (inputStream == null) {
            return;
        }
        IFile elementFile = folder.getFile(fileName);
        if (!elementFile.exists()) {
            elementFile.create(inputStream, false, null);
            ModelElement modelElement = ModelElementFileFactory.getModelElement(elementFile);
            if (modelElement != null) {
                AElementPersistance writer = ElementWriterFactory.getInstance().create(elementFile.getFileExtension());
                if (writer != null) {
                    writer.create(modelElement, folder, isImportItem);
                    elementFile.delete(true, null);
                }
            }
        }
    }

    public TDQSourceFileItem createSourceFileItem(File initFile, IPath path, String label, String extension) {
        Property property = PropertiesFactory.eINSTANCE.createProperty();
        property.setVersion(VersionUtils.DEFAULT_VERSION);
        property.setStatusCode(PluginConstant.EMPTY_STRING); //$NON-NLS-1$
        property.setLabel(label);

        TDQSourceFileItem sourceFileItem = org.talend.dataquality.properties.PropertiesFactory.eINSTANCE
                .createTDQSourceFileItem();
        sourceFileItem.setProperty(property);
        sourceFileItem.setName(label);
        sourceFileItem.setExtension(extension);

        ByteArray byteArray = PropertiesFactory.eINSTANCE.createByteArray();
        try {
            byteArray.setInnerContentFromFile(initFile);
        } catch (IOException e) {
            ExceptionHandler.process(e);
        }
        sourceFileItem.setContent(byteArray);
        IProxyRepositoryFactory repositoryFactory = ProxyRepositoryFactory.getInstance();
        try {
            property.setId(repositoryFactory.getNextId());
            repositoryFactory.create(sourceFileItem, path);
        } catch (PersistenceException e) {
            ExceptionHandler.process(e);
        }
        return sourceFileItem;
    }

    /**
     * 
     * DOC qiongli Comment method "createSourceFileItem".
     * 
     * @param content:cotanin sql sentence
     * @param path
     * @param label:file name
     * @param extension:file extension
     * @return
     */
    public TDQSourceFileItem createSourceFileItem(String content, IPath path, String label, String extension) {
        Property property = PropertiesFactory.eINSTANCE.createProperty();
        property.setVersion(VersionUtils.DEFAULT_VERSION);
        property.setStatusCode(PluginConstant.EMPTY_STRING); //$NON-NLS-1$
        property.setLabel(label);

        TDQSourceFileItem sourceFileItem = org.talend.dataquality.properties.PropertiesFactory.eINSTANCE
                .createTDQSourceFileItem();
        sourceFileItem.setProperty(property);
        sourceFileItem.setName(label);
        sourceFileItem.setExtension(extension);

        ByteArray byteArray = PropertiesFactory.eINSTANCE.createByteArray();
        byteArray.setInnerContent(content.getBytes());
        sourceFileItem.setContent(byteArray);
        IProxyRepositoryFactory repositoryFactory = ProxyRepositoryFactory.getInstance();
        try {
            property.setId(repositoryFactory.getNextId());
            repositoryFactory.create(sourceFileItem, path);
        } catch (PersistenceException e) {
            ExceptionHandler.process(e);
        }
        return sourceFileItem;
    }

    /**
     * Method "isNeedCreateStructure" created by bzhou@talend.com.
     * 
     * @return true if need to create new resource structure.
     */
    public boolean isNeedCreateStructure() {
        if (isSecludedVersion()) {
            return !ResourceService.checkSecludedResource();
        }

        return !ResourceService.checkResource();
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

        IWorkspace workspace = ResourcesPlugin.getWorkspace();
        final IProjectDescription description = workspace.newProjectDescription(newProjectHandle.getName());

        // create the new project operation
        IRunnableWithProgress op = new IRunnableWithProgress() {

            public void run(IProgressMonitor monitor) throws InvocationTargetException {
                CreateProjectOperation createProjOp = new CreateProjectOperation(description,
                        DefaultMessagesImpl.getString("DQStructureManager.createDataProfile")); //$NON-NLS-1$
                try {
                    PlatformUI.getWorkbench().getOperationSupport().getOperationHistory()
                            .execute(createProjOp, monitor, WorkspaceUndoUtil.getUIInfoAdapter(null));
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
        return newProjectHandle;
    }

    /**
     * DOC bzhou Comment method "createNewFolder".
     * 
     * @param parent
     * @param constant
     * @return
     * @throws CoreException
     * @deprecated
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
     * @deprecated
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
    public void copyFilesToFolder(Plugin plugin, String srcPath, boolean recurse, IFolder desFolder, String suffix,
            boolean... isImportItem) throws IOException, CoreException {
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
                        // ProxyRepositoryFactory.getInstance().createFolder(ERepositoryObjectType., path, label)
                        // folder.create(true, true, null);
                        this.createDQStructure();
                    }

                    copyFilesToFolder(plugin, currentPath, recurse, folder, suffix, isImportItem);
                    continue;
                }

                if (suffix != null && !file.getName().endsWith(suffix)) {
                    continue;
                }

                String fileName = new Path(fileURL.getPath()).lastSegment();
                InputStream openStream = null;
                openStream = fileURL.openStream();
                copyFileToFolder(openStream, fileName, desFolder, isImportItem);

                openStream.close();
            } catch (IOException e) {
                log.error(e, e);
            }
        }

    }

    public boolean isPathValid(IPath path, String label) {
        IFolder folder = ResourcesPlugin.getWorkspace().getRoot().getFolder(path);
        IFolder newFolder = folder.getFolder(label);
        return !newFolder.exists();
    }

}
