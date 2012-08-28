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
package org.talend.dataprofiler.core.helper;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import static org.powermock.api.support.membermodification.MemberMatcher.*;
import static org.powermock.api.support.membermodification.MemberModifier.*;

import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import net.sourceforge.sqlexplorer.plugin.SQLExplorerPlugin;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.jfree.util.Log;
import org.powermock.api.mockito.PowerMockito;
import org.talend.commons.bridge.ReponsitoryContextBridge;
import org.talend.commons.emf.EMFUtil;
import org.talend.commons.exception.PersistenceException;
import org.talend.core.context.Context;
import org.talend.core.context.RepositoryContext;
import org.talend.core.language.ECodeLanguage;
import org.talend.core.model.general.Project;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.ItemState;
import org.talend.core.model.properties.PropertiesFactory;
import org.talend.core.model.properties.Property;
import org.talend.core.model.properties.Status;
import org.talend.core.model.properties.User;
import org.talend.core.model.properties.helper.StatusHelper;
import org.talend.core.model.repository.IRepositoryObject;
import org.talend.core.repository.constants.FileConstants;
import org.talend.core.repository.i18n.Messages;
import org.talend.core.repository.model.IRepositoryFactory;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.core.repository.model.RepositoryFactoryProvider;
import org.talend.core.repository.utils.ProjectHelper;
import org.talend.core.repository.utils.XmiResourceManager;
import org.talend.core.runtime.CoreRuntimePlugin;
import org.talend.cwm.relational.RelationalFactory;
import org.talend.cwm.relational.TdExpression;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.exception.ExceptionHandler;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.manager.DQStructureManager;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.analysis.AnalysisFactory;
import org.talend.dataquality.analysis.AnalysisResult;
import org.talend.dataquality.domain.pattern.Pattern;
import org.talend.dataquality.domain.pattern.PatternFactory;
import org.talend.dataquality.domain.pattern.RegularExpression;
import org.talend.dataquality.helpers.AnalysisHelper;
import org.talend.dataquality.helpers.ReportHelper;
import org.talend.dataquality.properties.TDQAnalysisItem;
import org.talend.dataquality.properties.TDQReportItem;
import org.talend.dataquality.properties.impl.PropertiesFactoryImpl;
import org.talend.repository.model.IProxyRepositoryFactory;
import org.talend.repository.model.RepositoryConstants;
import org.talend.repository.model.RepositoryNode;
import org.talend.resource.ResourceManager;
import orgomg.cwmx.analysis.informationreporting.Report;

import common.Logger;

/**
 * created by yyin on 2012-8-22 Detailled comment: include some structure methods which can be used for any tests who
 * need related object. some are mocked object with the start "mock" on its method name; some are real created object
 * with the start "createReal" on its method name;
 * 
 */
public class UnitTestBuildHelper {

    private static Logger log = Logger.getLogger(UnitTestBuildHelper.class);

    private static final String REGEXP = "'su.*'"; //$NON-NLS-1$

    private static String[] filterExtensions = { "ana", "rep" };//$NON-NLS-1$//$NON-NLS-2$

    final private static String anaFolderName = "TDQ_Data Profiling/Analyses";//$NON-NLS-1$

    final private static String repFolderName = "TDQ_Data Profiling/Reports";//$NON-NLS-1$

    public static RepositoryNode mockRepositoryNode() {
        RepositoryNode node1 = mock(RepositoryNode.class);
        IRepositoryObject object = mock(IRepositoryObject.class);
        Property prop = mock(Property.class);
        Item item = mock(Item.class);
        ItemState state = mock(ItemState.class);
        when(prop.getItem()).thenReturn(item);
        when(node1.getObject()).thenReturn(object);
        when(object.getProperty()).thenReturn(prop);
        when(item.getState()).thenReturn(state);
        when(state.isDeleted()).thenReturn(false);
        return node1;
    }

    public static CorePlugin mockCorePlugin() {
        CorePlugin corePlugin = mock(CorePlugin.class);
        PowerMockito.mockStatic(CorePlugin.class);
        when(CorePlugin.getDefault()).thenReturn(corePlugin);
        return corePlugin;
    }

    public static ResourceBundle mockResourceBundle() {
        ResourceBundle rb = mock(ResourceBundle.class);
        stub(method(ResourceBundle.class, "getBundle", String.class)).toReturn(rb);
        return rb;
    }

    public static void mockMessages() {
        PowerMockito.mockStatic(Messages.class);
        when(Messages.getString(anyString())).thenReturn("a1");
        PowerMockito.mock(DefaultMessagesImpl.class);
        when(DefaultMessagesImpl.getString(anyString())).thenReturn("a2").thenReturn("a3").thenReturn("a4").thenReturn("a5");
    }

    public static Pattern createRealPattern() {
        Pattern pattern = PatternFactory.eINSTANCE.createPattern();
        pattern.setName("My Pattern"); //$NON-NLS-1$
        RegularExpression regularExpr = PatternFactory.eINSTANCE.createRegularExpression();
        TdExpression expression = createRealExpression();
        regularExpr.setExpression(expression);
        pattern.getComponents().add(regularExpr);
        return pattern;
    }

    public static TdExpression createRealExpression() {
        TdExpression expression = RelationalFactory.eINSTANCE.createTdExpression();
        expression.setBody(REGEXP);
        expression.setLanguage("SQL"); //$NON-NLS-1$
        return expression;
    }

    public static IProject createRealProject() {
        IProject rootProject = ReponsitoryContextBridge.getRootProject();
        if (!rootProject.exists()) {
            initProxyRepository(rootProject);
        }

        if (DQStructureManager.getInstance().isNeedCreateStructure()) {
            DQStructureManager.getInstance().createDQStructure();
        }
        return rootProject;
    }

    /**
     * DOC talend Comment method "initProxyRepository".
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
                // Initialize TDQ EMF model packages.
                new EMFUtil();
                project = new Project(xmiResourceManager.loadProject(rootProject));
            } else {
                User user = org.talend.core.model.properties.impl.PropertiesFactoryImpl.eINSTANCE.createUser();
                user.setLogin("talend@talend.com"); //$NON-NLS-1$
                user.setPassword("talend@talend.com".getBytes()); //$NON-NLS-1$
                String projectName = ResourceManager.getRootProjectName();
                String projectDesc = ResourcesPlugin.getWorkspace().newProjectDescription(projectName).getComment();
                Project projectInfor = ProjectHelper.createProject(projectName, projectDesc, ECodeLanguage.JAVA.getName(), user);

                // MOD zshen create project by proxyRepository
                checkFileName(projectInfor.getLabel(), RepositoryConstants.PROJECT_PATTERN);

                project = proxyRepository.getRepositoryFactoryFromProvider().createProject(projectInfor);

            }

            if (project != null) {
                initRepositoryContext(project);

                // add status
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
     * 
     * DOC talend Comment method "createRealReport".
     * 
     * @param name the name of report
     * @param folder the path which report location
     * @param isDelete the report whether is logic delate
     */
    public static Report createRealReport(String name, IFolder folder, Boolean isDelete) {
        IPath createPath = Path.EMPTY;
        if (folder != null) {
            createPath = new Path(folder.getFullPath().lastSegment());
        }
        Report report1 = ReportHelper.createReport(name);
        TDQReportItem item1 = PropertiesFactoryImpl.eINSTANCE.createTDQReportItem();
        org.talend.core.model.properties.Property property1 = PropertiesFactory.eINSTANCE.createProperty();
        property1.setId(EcoreUtil.generateUUID());
        property1.setItem(item1);
        property1.setLabel(report1.getName());
        item1.setProperty(property1);
        item1.setReport(report1);
        ItemState itemState = org.talend.core.model.properties.PropertiesFactory.eINSTANCE.createItemState();
        itemState.setDeleted(isDelete);
        item1.setState(itemState);

        try {
            ProxyRepositoryFactory.getInstance().create(item1, createPath, false);
        } catch (PersistenceException e) {
            Log.error(e, e);
            e.printStackTrace();
        }
        return report1;
    }

    public static Analysis createRealAnalysis(String name, IFolder folder, Boolean isDelete) {
        IPath createPath = Path.EMPTY;
        if (folder != null) {
            createPath = new Path(folder.getFullPath().lastSegment());
        }
        Analysis analysis1 = AnalysisHelper.createAnalysis(name);
        TDQAnalysisItem item1 = PropertiesFactoryImpl.eINSTANCE.createTDQAnalysisItem();
        org.talend.core.model.properties.Property property1 = PropertiesFactory.eINSTANCE.createProperty();
        property1.setId(EcoreUtil.generateUUID());
        property1.setItem(item1);
        property1.setLabel(analysis1.getName());
        item1.setProperty(property1);
        item1.setAnalysis(analysis1);
        ItemState itemState = org.talend.core.model.properties.PropertiesFactory.eINSTANCE.createItemState();
        itemState.setDeleted(isDelete);
        item1.setState(itemState);
        AnalysisResult analysisResult1 = AnalysisFactory.eINSTANCE.createAnalysisResult();
        analysis1.setResults(analysisResult1);
        try {
            ProxyRepositoryFactory.getInstance().create(item1, createPath, false);
        } catch (PersistenceException e) {
            Log.error(e, e);
            e.printStackTrace();
        }
        return analysis1;
    }

    /**
     * 
     * DOC zshen Comment method "createFolder". create the subfolder under the parentFolder and named for folderName
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
                Log.error(e, e);
                e.printStackTrace();
            }
        }
        return currFolder;
    }

    /**
     * 
     * DOC zshen Comment method "checkFileName".
     * 
     * @param fileName
     * @param pattern
     * 
     * copy the method from ProxyRepositoryFactory to avoid tos migeration.
     */
    private static void checkFileName(String fileName, String pattern) {
        if (!java.util.regex.Pattern.matches(pattern, fileName)) {
            throw new IllegalArgumentException(DefaultMessagesImpl.getString(
                    "ProxyRepositoryFactory.illegalArgumentException.labelNotMatchPattern", new Object[] { fileName, pattern })); //$NON-NLS-1$
        }
    }

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
        // MOD zshen for bug tdq-4757 remove this init from corePlugin.start() to here because the initLocal command of
        // commandLine
        SQLExplorerPlugin.getDefault().setRootProject(ReponsitoryContextBridge.getRootProject());
    }
}
