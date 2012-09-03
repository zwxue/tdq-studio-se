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

import java.util.HashMap;
import java.util.List;

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
import org.talend.core.context.Context;
import org.talend.core.context.RepositoryContext;
import org.talend.core.language.ECodeLanguage;
import org.talend.core.model.general.Project;
import org.talend.core.model.properties.Status;
import org.talend.core.model.properties.User;
import org.talend.core.model.properties.helper.StatusHelper;
import org.talend.core.repository.constants.FileConstants;
import org.talend.core.repository.model.IRepositoryFactory;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.core.repository.model.RepositoryFactoryProvider;
import org.talend.core.repository.utils.ProjectHelper;
import org.talend.core.repository.utils.XmiResourceManager;
import org.talend.core.runtime.CoreRuntimePlugin;
import org.talend.repository.model.IProxyRepositoryFactory;
import org.talend.repository.model.RepositoryConstants;

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
     * @param project
     * @param folderName
     * @return
     */
    public static IFolder createRealFolder(IProject project, String folderName) {
        IFolder iFolder = project.getFolder(folderName);
        if (!iFolder.exists()) {
            try {
                iFolder.create(true, true, null);
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
            } catch (CoreException e) {
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
}
