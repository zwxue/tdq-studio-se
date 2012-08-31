// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.sourceforge.sqlexplorer.plugin.SQLExplorerPlugin;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.talend.commons.bridge.ReponsitoryContextBridge;
import org.talend.commons.emf.EMFUtil;
import org.talend.commons.exception.PersistenceException;
import org.talend.core.context.Context;
import org.talend.core.context.RepositoryContext;
import org.talend.core.language.ECodeLanguage;
import org.talend.core.model.general.Project;
import org.talend.core.model.properties.FolderItem;
import org.talend.core.model.properties.ItemState;
import org.talend.core.model.properties.PropertiesFactory;
import org.talend.core.model.properties.Status;
import org.talend.core.model.properties.User;
import org.talend.core.model.properties.helper.StatusHelper;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.Folder;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.model.repository.RepositoryViewObject;
import org.talend.core.repository.constants.FileConstants;
import org.talend.core.repository.model.FolderHelper;
import org.talend.core.repository.model.IRepositoryFactory;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.core.repository.model.RepositoryFactoryProvider;
import org.talend.core.repository.utils.ProjectHelper;
import org.talend.core.repository.utils.XmiResourceManager;
import org.talend.core.runtime.CoreRuntimePlugin;
import org.talend.dataprofiler.core.exception.ExceptionHandler;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.manager.DQStructureManager;
import org.talend.dataprofiler.core.ui.views.provider.RepositoryNodeBuilder;
import org.talend.dataquality.helpers.ReportHelper;
import org.talend.dataquality.properties.TDQReportItem;
import org.talend.dataquality.properties.impl.PropertiesFactoryImpl;
import org.talend.dq.nodes.ReportFolderRepNode;
import org.talend.dq.nodes.ReportRepNode;
import org.talend.dq.nodes.ReportSubFolderRepNode;
import org.talend.repository.model.IProxyRepositoryFactory;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.IRepositoryNode.ENodeType;
import org.talend.repository.model.RepositoryConstants;
import org.talend.repository.model.RepositoryNode;
import org.talend.resource.EResourceConstant;
import orgomg.cwmx.analysis.informationreporting.Report;

/**
 * DOC xqliu class global comment. Detailled comment
 */
public class UnitTestBuildHelper {

    private static Logger log = Logger.getLogger(UnitTestBuildHelper.class);

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

        if (DQStructureManager.getInstance().isNeedCreateStructure()) {
            DQStructureManager.getInstance().createDQStructure();
        }
        return rootProject;
    }

    /**
     * DOC xqliu Comment method "initProxyRepository".
     * 
     * @param rootProject
     */
    private static void initProxyRepository(IProject rootProject) {
        Project project = null;

        ProxyRepositoryFactory proxyRepository = ProxyRepositoryFactory.getInstance();
        IRepositoryFactory repository = RepositoryFactoryProvider.getRepositoriyById("local"); //$NON-NLS-1$
        if (repository == null) {
            log.fatal(DefaultMessagesImpl
                    .getString("No local Repository found! Probably due to a missing plugin in the product.")); //$NON-NLS-1$
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
                user.setLogin("talend@talend.com"); //$NON-NLS-1$
                user.setPassword("talend@talend.com".getBytes()); //$NON-NLS-1$
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
    }

    /**
     * create the subfolder under the parentFolder and named for folderName.
     * 
     * @param parentFolder
     * @param folderName
     * @return
     */
    public static IFolder createRealFolder(IFolder parentFolder, String folderName) {
        IFolder currFolder = parentFolder.getFolder(folderName);
        if (!currFolder.exists()) {
            try {
                currFolder.create(true, true, null);
            } catch (CoreException e) {
                ExceptionHandler.process(e);
                log.error(e, e);
            }
        }
        return currFolder;
    }

    /**
     * copy the method from ProxyRepositoryFactory to avoid tos migeration.
     * 
     * @param fileName
     * @param pattern
     */
    private static void checkFileName(String fileName, String pattern) {
        if (!java.util.regex.Pattern.matches(pattern, fileName)) {
            throw new IllegalArgumentException(DefaultMessagesImpl.getString(
                    "ProxyRepositoryFactory.illegalArgumentException.labelNotMatchPattern", new Object[] { fileName, pattern })); //$NON-NLS-1$
        }
    }

    /**
     * DOC xqliu Comment method "initRepositoryContext".
     * 
     * @param project
     */
    private static void initRepositoryContext(Project project) {
        RepositoryContext repositoryContext = new RepositoryContext();
        repositoryContext.setUser(project.getAuthor());
        repositoryContext.setClearPassword(project.getLabel());
        repositoryContext.setProject(project);
        repositoryContext.setFields(new HashMap<String, String>());
        repositoryContext.getFields().put(IProxyRepositoryFactory.BRANCH_SELECTION + "_" + project.getTechnicalLabel(), ""); //$NON-NLS-1$ //$NON-NLS-2$
        Context ctx = CoreRuntimePlugin.getInstance().getContext();
        ctx.putProperty(Context.REPOSITORY_CONTEXT_KEY, repositoryContext);

        ReponsitoryContextBridge.initialized(project.getEmfProject(), project.getAuthor());
        SQLExplorerPlugin.getDefault().setRootProject(ReponsitoryContextBridge.getRootProject());
    }

    /**
     * create the real RepositoryNode for DataProfiling.
     * 
     * @return
     */
    public static RepositoryNode createRealDataProfilingNode(IProject project) {
        RepositoryNode node = null;

        RepositoryNodeBuilder instance = RepositoryNodeBuilder.getInstance();
        FolderHelper folderHelper = instance.getFolderHelper();

        IFolder iFolder = project.getFolder(Path.fromPortableString(ERepositoryObjectType.TDQ_DATA_PROFILING.getFolder()));
        IRepositoryViewObject viewObject = null;
        if (folderHelper != null) {
            IPath relativePath = iFolder.getFullPath().makeRelativeTo((project).getFullPath());
            FolderItem folder2 = folderHelper.getFolder(relativePath);
            if (folder2 != null && relativePath != null) {
                viewObject = new Folder(folder2.getProperty(), instance.retrieveRepObjectTypeByPath(relativePath.toOSString()));
            }
        } else {
            viewObject = new Folder(iFolder.getName(), iFolder.getName());
        }

        if (viewObject != null) {
            node = new RepositoryNode(viewObject, null, ENodeType.SYSTEM_FOLDER);
            viewObject.setRepositoryNode(node);
        }

        return node;
    }

    /**
     * create the real ReportFolderRepNode.
     * 
     * @param parentNode
     * @return
     */
    public static ReportFolderRepNode createRealReportFolderRepNode(RepositoryNode parentNode) {
        ReportFolderRepNode node = null;
        RepositoryNodeBuilder instance = RepositoryNodeBuilder.getInstance();
        try {
            node = (ReportFolderRepNode) instance.createRepositoryNodeSystemFolder(instance.getFolderHelper(), parentNode,
                    EResourceConstant.REPORTS);
        } catch (PersistenceException e) {
            ExceptionHandler.process(e);
            log.error(e, e);
        }
        return node;
    }

    /**
     * create a real ReportSubFolderRepNode.
     * 
     * @param parentNode the parent node
     * @param folderName the folder name
     * @return
     */
    public static ReportSubFolderRepNode createRealReportSubFolderRepNode(RepositoryNode parentNode, String folderName) {
        ReportSubFolderRepNode folderNode = null;

        // create the sub folder
        IFolder iFolder = RepositoryNodeHelper.getIFolder(parentNode);
        createRealFolder(iFolder, folderName);

        // get the ReportSubFolderRepNode
        List<IRepositoryNode> subFolders = new ArrayList<IRepositoryNode>();
        if (parentNode instanceof ReportFolderRepNode) {
            subFolders = ((ReportFolderRepNode) parentNode).getChildren(false);
        } else if (parentNode instanceof ReportSubFolderRepNode) {
            subFolders = ((ReportSubFolderRepNode) parentNode).getChildren(false);
        }

        if (!subFolders.isEmpty()) {
            for (IRepositoryNode node : subFolders) {
                if (node instanceof ReportSubFolderRepNode) {
                    ReportSubFolderRepNode subNode = (ReportSubFolderRepNode) node;
                    if (folderName.equals(subNode.getLabel())) {
                        folderNode = subNode;
                        break;
                    }
                }
            }
        }

        return folderNode;
    }

    /**
     * create a real ReportRepNode.
     * 
     * @param name report name
     * @param folder report's parent folder
     * @param isDelete delete flag
     * @return
     */
    public static ReportRepNode createRealReportNode(String name, RepositoryNode parentNode, IPath createPath, Boolean isDelete) {
        ReportRepNode reportRepNode = null;

        Report report = ReportHelper.createReport(name);
        TDQReportItem reportItem = PropertiesFactoryImpl.eINSTANCE.createTDQReportItem();
        org.talend.core.model.properties.Property reportProperty = PropertiesFactory.eINSTANCE.createProperty();

        reportProperty.setId(EcoreUtil.generateUUID());
        reportProperty.setItem(reportItem);
        reportProperty.setLabel(report.getName());

        reportItem.setProperty(reportProperty);
        reportItem.setReport(report);

        ItemState itemState = org.talend.core.model.properties.PropertiesFactory.eINSTANCE.createItemState();
        itemState.setDeleted(isDelete);
        reportItem.setState(itemState);

        try {
            ProxyRepositoryFactory.getInstance().create(reportItem, createPath, false);

            IRepositoryViewObject reportViewObject = new RepositoryViewObject(reportProperty);
            reportRepNode = new ReportRepNode(reportViewObject, parentNode, ENodeType.REPOSITORY_ELEMENT);
        } catch (PersistenceException e) {
            ExceptionHandler.process(e);
            log.error(e, e);
        }

        return reportRepNode;
    }
}
