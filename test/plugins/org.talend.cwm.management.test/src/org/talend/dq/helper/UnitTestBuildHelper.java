// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dq.helper;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import junit.framework.Assert;
import net.sourceforge.sqlexplorer.plugin.SQLExplorerPlugin;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.talend.commons.bridge.ReponsitoryContextBridge;
import org.talend.commons.emf.EMFUtil;
import org.talend.commons.exception.PersistenceException;
import org.talend.commons.ui.runtime.exception.ExceptionHandler;
import org.talend.commons.utils.WorkspaceUtils;
import org.talend.core.context.Context;
import org.talend.core.context.RepositoryContext;
import org.talend.core.language.ECodeLanguage;
import org.talend.core.model.general.Project;
import org.talend.core.model.properties.FolderItem;
import org.talend.core.model.properties.FolderType;
import org.talend.core.model.properties.InformationLevel;
import org.talend.core.model.properties.ItemState;
import org.talend.core.model.properties.PropertiesFactory;
import org.talend.core.model.properties.Property;
import org.talend.core.model.properties.Status;
import org.talend.core.model.properties.User;
import org.talend.core.model.properties.helper.StatusHelper;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.Folder;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.repository.constants.FileConstants;
import org.talend.core.repository.model.IRepositoryFactory;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.core.repository.model.RepositoryFactoryProvider;
import org.talend.core.repository.utils.ProjectHelper;
import org.talend.core.repository.utils.XmiResourceManager;
import org.talend.core.runtime.CoreRuntimePlugin;
import org.talend.repository.ProjectManager;
import org.talend.repository.ProjectManager;
import org.talend.repository.model.IRepositoryNode.ENodeType;
import org.talend.repository.model.RepositoryConstants;
import org.talend.repository.model.RepositoryNode;
import org.talend.utils.string.StringUtilities;

/**
 * DOC xqliu class global comment. Detailled comment
 */
public class UnitTestBuildHelper {

    private static Logger log = Logger.getLogger(UnitTestBuildHelper.class);

    private static UnitTestBuildHelper INSTANCE;

    private boolean init = false;

    private IProject iProject;

    private Project tProject;

    private File projectFile;

    private RepositoryNode dataProfilingNode;

    private RepositoryNode librariesNode;

    private RepositoryNode metadataNode;

    public File getProjectFile() {
        return this.projectFile;
    }

    public void setProjectFile(File projectFile) {
        this.projectFile = projectFile;
    }

    public boolean isInit() {
        return this.init;
    }

    public void setInit(boolean init) {
        this.init = init;
    }

    public IProject getiProject() {
        return this.iProject;
    }

    public void setiProject(IProject iProject) {
        this.iProject = iProject;
    }

    public Project gettProject() {
        return this.tProject;
    }

    public void settProject(Project tProject) {
        this.tProject = tProject;
    }

    public RepositoryNode getDataProfilingNode() {
        return this.dataProfilingNode;
    }

    public void setDataProfilingNode(RepositoryNode dataProfilingNode) {
        this.dataProfilingNode = dataProfilingNode;
    }

    public RepositoryNode getLibrariesNode() {
        return this.librariesNode;
    }

    public void setLibrariesNode(RepositoryNode librariesNode) {
        this.librariesNode = librariesNode;
    }

    public RepositoryNode getMetadataNode() {
        return this.metadataNode;
    }

    public void setMetadataNode(RepositoryNode metadataNode) {
        this.metadataNode = metadataNode;
    }

    private UnitTestBuildHelper() {
    }

    public static UnitTestBuildHelper getDefault() {
        if (INSTANCE == null) {
            INSTANCE = new UnitTestBuildHelper();
        }
        return INSTANCE;
    }

    /**
     * create a real project, init the default structure for TDQ.
     * 
     * @param projectName
     */
    public void initTdqProject(String projectName) {
        IProject realProject = UnitTestBuildHelper.createRealProject(projectName);
        if (realProject != null) {
            this.setiProject(realProject);
            if (ProjectManager.getInstance().getCurrentProject() == null) {
                this.settProject(UnitTestBuildHelper.initProxyRepository(this.getiProject()));
            } else {
                this.settProject(ProjectManager.getInstance().getCurrentProject());
            }
            if (this.gettProject() != null && this.gettProject().getEmfProject() != null) {
                this.setDataProfilingNode(UnitTestBuildHelper.createRealDataProfilingNode(this.getiProject()));
                this.setLibrariesNode(UnitTestBuildHelper.createRealLibrariesNode(this.getiProject()));
                this.setMetadataNode(UnitTestBuildHelper.createRealMetadataNode(this.getiProject()));
                this.setProjectFile(this.getiProject().getWorkspace().getRoot().getLocation()
                        .append(this.gettProject().getEmfProject().getTechnicalLabel()).toFile());
            }
        }
        if (this.getiProject() != null && this.gettProject() != null && this.gettProject().getEmfProject() != null
                && this.getDataProfilingNode() != null && this.getLibrariesNode() != null && this.getMetadataNode() != null) {
            this.setInit(true);
        } else {
            setInit(false);
        }
    }

    /**
     * create project with a specified name.
     * 
     * @param projectName specified project name
     * @return
     */
    public static IProject createRealProject(String projectName) {
        IProject rootProject = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
        if (!rootProject.exists()) {
            initProxyRepository(rootProject);
        }

        return rootProject;
    }

    /**
     * DOC xqliu Comment method "initProxyRepository".
     * 
     * @param rootProject
     */
    private static Project initProxyRepository(IProject rootProject) {
        Project project = null;

        ProxyRepositoryFactory proxyRepository = ProxyRepositoryFactory.getInstance();
        IRepositoryFactory repository = RepositoryFactoryProvider.getRepositoriyById("local"); //$NON-NLS-1$
        if (repository == null) {
            log.fatal("No local Repository found! Probably due to a missing plugin in the product."); //$NON-NLS-1$
        }
        proxyRepository.setRepositoryFactoryFromProvider(repository);
        try {
            proxyRepository.checkAvailability();
            proxyRepository.initialize();

            XmiResourceManager xmiResourceManager = new XmiResourceManager();

            if (rootProject.getFile(FileConstants.LOCAL_PROJECT_FILENAME).exists()) {
                new EMFUtil();
                project = new Project(xmiResourceManager.loadProject(rootProject));
            } else {
                User user = org.talend.core.model.properties.impl.PropertiesFactoryImpl.eINSTANCE.createUser();
                user.setLogin("test@talend.com"); //$NON-NLS-1$
                user.setPassword("password".getBytes()); //$NON-NLS-1$
                user.setLanguage(ECodeLanguage.JAVA.getName());
                String projectName = rootProject.getName();
                String projectDesc = ResourcesPlugin.getWorkspace().newProjectDescription(projectName).getComment();
                Project projectInfor = ProjectHelper.createProject(projectName, projectDesc, ECodeLanguage.JAVA.getName(), user);

                checkFileName(projectInfor.getLabel(), RepositoryConstants.PROJECT_PATTERN);

                project = proxyRepository.getRepositoryFactoryFromProvider().createProject(projectInfor);

            }

            if (project != null) {
                initRepositoryContext(project);

                String defaultTechnicalStatusList = "DEV development;TEST testing;PROD production"; //$NON-NLS-1$
                List<Status> statusList = StatusHelper.parse(defaultTechnicalStatusList);
                proxyRepository.setTechnicalStatus(statusList);
            }
        } catch (PersistenceException e) {
            ExceptionHandler.process(e);
            log.error(e, e);
        }
        return project;
    }

    /**
     * create the subfolder under the project and named for folderName.
     * 
     * @param project
     * @param folderName
     * @return
     */
    public static IFolder createRealFolder(IProject project, String folderName) {
        IFolder iFolder = project.getFolder(folderName);
        if (!iFolder.exists()) {
            try {
                iFolder.create(true, true, null);
                File file = WorkspaceUtils.ifolderToFile(iFolder);
                if (!file.exists()) {
                    file.mkdirs();
                }
            } catch (CoreException e) {
                ExceptionHandler.process(e);
                log.error(e, e);
            }
        }
        return iFolder;
    }

    /**
     * create the subfolder under the parent folder and named for folderName.
     * 
     * @param parentFolder
     * @param folderName
     * @return
     */
    public static IFolder createRealFolder(IFolder parentFolder, String folderName) {
        IFolder iFolder = parentFolder.getFolder(folderName);
        if (!iFolder.exists()) {
            try {
                iFolder.create(true, true, null);
                File file = WorkspaceUtils.ifolderToFile(iFolder);
                if (!file.exists()) {
                    file.mkdirs();
                }
            } catch (CoreException e) {
                ExceptionHandler.process(e);
                log.error(e, e);
            }
        }
        return iFolder;
    }

    /**
     * create the file under the parentFolder and named for fileName.
     * 
     * @param parentFolder
     * @param fileName
     * @return
     */
    public static IFile createRealFile(IFolder parentFolder, String fileName) {
        IFile iFile = parentFolder.getFile(fileName);
        if (!iFile.exists()) {
            try {
                iFile.create(null, true, null);
                File file = WorkspaceUtils.ifileToFile(iFile);
                if (!file.exists()) {
                    File parentFile = file.getParentFile();
                    if (!parentFile.exists()) {
                        parentFile.mkdirs();
                    }
                    file.createNewFile();
                }
            } catch (CoreException e) {
                ExceptionHandler.process(e);
                log.error(e, e);
            } catch (IOException e) {
                ExceptionHandler.process(e);
                log.error(e, e);
            }
        }
        return iFile;
    }

    /**
     * copy the method from ProxyRepositoryFactory to avoid tos migeration.
     * 
     * @param fileName
     * @param pattern
     */
    private static void checkFileName(String fileName, String pattern) {
        if (!java.util.regex.Pattern.matches(pattern, fileName)) {
            throw new IllegalArgumentException("the file name [" + fileName + "] is invalid of the pattern [" + pattern + "]"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }
    }

    /**
     * DOC xqliu Comment method "initRepositoryContext".
     * 
     * @param project
     */
    private static void initRepositoryContext(Project project) {
        RepositoryContext repositoryContext = new RepositoryContext();
        Context ctx = CoreRuntimePlugin.getInstance().getContext();
        ctx.putProperty(Context.REPOSITORY_CONTEXT_KEY, repositoryContext);

        repositoryContext.setUser(project.getAuthor());
        repositoryContext.setClearPassword(project.getLabel());
        repositoryContext.setProject(project);
        repositoryContext.setFields(new HashMap<String, String>());
        //        repositoryContext.getFields().put(IProxyRepositoryFactory.BRANCH_SELECTION + "_" + project.getTechnicalLabel(), ""); //$NON-NLS-1$ //$NON-NLS-2$
        ProjectManager.getInstance().setMainProjectBranch(project, null);

        ReponsitoryContextBridge.initialized(project.getEmfProject(), project.getAuthor());
        SQLExplorerPlugin.getDefault().setRootProject(ReponsitoryContextBridge.getRootProject());
    }

    /**
     * create the real RepositoryNode for DataProfiling.
     * 
     * @param iProject
     * @return
     */
    public static RepositoryNode createRealDataProfilingNode(IProject iProject) {
        RepositoryNode node = null;

        if (iProject != null) {
            UnitTestBuildHelper.createRealFolder(iProject, ERepositoryObjectType.TDQ_DATA_PROFILING.getFolder());

            Project tProject = ProjectManager.getInstance().getCurrentProject();
            if (tProject != null && tProject.getEmfProject() != null && tProject.getAuthor() != null) {
                IRepositoryViewObject viewObject = buildRepositoryViewObjectSystemFolder(tProject.getEmfProject(),
                        tProject.getAuthor(), ERepositoryObjectType.TDQ_DATA_PROFILING);

                node = new RepositoryNode(viewObject, null, ENodeType.SYSTEM_FOLDER);
                viewObject.setRepositoryNode(node);
            }
        }

        return node;
    }

    /**
     * create the real RepositoryNode for Libraries.
     * 
     * @param iProject
     * @return
     */
    public static RepositoryNode createRealLibrariesNode(IProject iProject) {
        RepositoryNode node = null;

        if (iProject != null) {
            UnitTestBuildHelper.createRealFolder(iProject, ERepositoryObjectType.TDQ_LIBRARIES.getFolder());

            Project tProject = ProjectManager.getInstance().getCurrentProject();
            if (tProject != null && tProject.getEmfProject() != null) {
                IRepositoryViewObject viewObject = buildRepositoryViewObjectSystemFolder(tProject.getEmfProject(),
                        tProject.getAuthor(), ERepositoryObjectType.TDQ_LIBRARIES);

                node = new RepositoryNode(viewObject, null, ENodeType.SYSTEM_FOLDER);
                viewObject.setRepositoryNode(node);
            }
        }

        return node;
    }

    /**
     * create the real RepositoryNode for Metadata.
     * 
     * @param iProject
     * @return
     */
    public static RepositoryNode createRealMetadataNode(IProject iProject) {
        RepositoryNode node = null;

        if (iProject != null) {
            UnitTestBuildHelper.createRealFolder(iProject, ERepositoryObjectType.METADATA.getFolder());

            Project tProject = ProjectManager.getInstance().getCurrentProject();
            if (tProject != null && tProject.getEmfProject() != null && tProject.getAuthor() != null) {
                IRepositoryViewObject viewObject = buildRepositoryViewObjectSystemFolder(tProject.getEmfProject(),
                        tProject.getAuthor(), ERepositoryObjectType.METADATA);

                node = new RepositoryNode(viewObject, null, ENodeType.SYSTEM_FOLDER);
                viewObject.setRepositoryNode(node);
            }
        }

        return node;
    }

    /**
     * create a RepositoryViewObject which is a system folder.
     * 
     * @param tProject
     * @param author
     * @param type
     * @return
     */
    private static IRepositoryViewObject buildRepositoryViewObjectSystemFolder(
            org.talend.core.model.properties.Project emfProject, User author, ERepositoryObjectType type) {
        ItemState itemState = PropertiesFactory.eINSTANCE.createItemState();
        FolderItem folderItem = PropertiesFactory.eINSTANCE.createFolderItem();
        Property property = PropertiesFactory.eINSTANCE.createProperty();

        itemState.setItemRelated(folderItem);

        folderItem.setParent(emfProject);
        folderItem.setType(FolderType.STABLE_SYSTEM_FOLDER_LITERAL);
        folderItem.setProperty(property);

        property.setAuthor(author);
        property.setCreationDate(new Date());
        property.setId(StringUtilities.getRandomString(26));
        property.setItem(folderItem);
        property.setLabel(type.getFolder());
        property.setMaxInformationLevel(InformationLevel.DEBUG_LITERAL);
        property.setOldStatusCode(""); //$NON-NLS-1$
        property.setVersion("0.1"); //$NON-NLS-1$

        return new Folder(property, type);
    }

    /**
     * delete the project which has been login else will effect the result of junit.
     */
    public static void deleteCurrentProject() {
        IProject rootProject = ReponsitoryContextBridge.getRootProject();
        if (rootProject.exists()) {
            try {
                rootProject.delete(true, true, null);
            } catch (CoreException e) {
                log.error(e, e);
                Assert.fail(e.getMessage());
            }
        }
    }
}
