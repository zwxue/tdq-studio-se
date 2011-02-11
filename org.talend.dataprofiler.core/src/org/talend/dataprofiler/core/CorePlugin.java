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
package org.talend.dataprofiler.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import net.sourceforge.sqlexplorer.dbproduct.Alias;
import net.sourceforge.sqlexplorer.dbproduct.AliasManager;
import net.sourceforge.sqlexplorer.plugin.SQLExplorerPlugin;
import net.sourceforge.sqlexplorer.plugin.editors.SQLEditor;
import net.sourceforge.sqlexplorer.plugin.editors.SQLEditorInput;
import net.sourceforge.sqlexplorer.sqleditor.actions.ExecSQLAction;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.help.internal.base.BaseHelpSystem;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.RefreshAction;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.jfree.util.Log;
import org.osgi.framework.BundleContext;
import org.talend.commons.bridge.ReponsitoryContextBridge;
import org.talend.commons.emf.EMFUtil;
import org.talend.commons.exception.PersistenceException;
import org.talend.core.context.Context;
import org.talend.core.context.RepositoryContext;
import org.talend.core.language.ECodeLanguage;
import org.talend.core.model.general.Project;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.metadata.builder.database.JavaSqlFactory;
import org.talend.core.model.properties.ConnectionItem;
import org.talend.core.model.properties.Property;
import org.talend.core.model.properties.Status;
import org.talend.core.model.properties.User;
import org.talend.core.model.properties.helper.StatusHelper;
import org.talend.core.model.properties.impl.PropertiesFactoryImpl;
import org.talend.core.repository.constants.FileConstants;
import org.talend.core.repository.model.IRepositoryFactory;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.core.repository.model.RepositoryFactoryProvider;
import org.talend.core.repository.utils.ProjectHelper;
import org.talend.core.repository.utils.XmiResourceManager;
import org.talend.core.runtime.CoreRuntimePlugin;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.dataprofiler.core.exception.ExceptionHandler;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.editor.connection.ConnectionItemEditorInput;
import org.talend.dataprofiler.core.ui.views.DQRespositoryView;
import org.talend.dataprofiler.core.ui.views.PatternTestView;
import org.talend.dataprofiler.help.BookMarkEnum;
import org.talend.dq.CWMPlugin;
import org.talend.repository.model.IProxyRepositoryFactory;
import org.talend.repository.model.RepositoryConstants;
import org.talend.resource.ResourceManager;
import org.talend.utils.ProductVersion;

/**
 * The activator class controls the plug-in life cycle.
 */
public class CorePlugin extends AbstractUIPlugin {

    protected static Logger log = Logger.getLogger(CorePlugin.class);

    // The plug-in ID
    public static final String PLUGIN_ID = "org.talend.dataprofiler.core"; //$NON-NLS-1$

    // The shared instance
    private static CorePlugin plugin;

    private RefreshAction refreshAction;

    private boolean repositoryInitialized = false;

    /**
     * Getter for repositoryInitialized.
     * 
     * @return the repositoryInitialized
     */
    public boolean isRepositoryInitialized() {
        return this.repositoryInitialized;
    }

    /**
     * The constructor.
     */
    public CorePlugin() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext )
     */
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
        getPreferenceStore().setDefault(PluginConstant.CHEAT_SHEET_VIEW, true);
        try {
            for (BookMarkEnum bookMark : BookMarkEnum.VALUES) {
                BaseHelpSystem.getBookmarkManager().addBookmark(bookMark.getHref(), bookMark.getLabel());
            }
        } catch (Exception e) {
            log.error(e, e);
        }

        repositoryInitialized = this.initProxyRepository();
        SQLExplorerPlugin.getDefault().setRootProject(ReponsitoryContextBridge.getRootProject());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext )
     */
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
        // save();
    }

    /**
     * Returns the shared instance.
     * 
     * @return the shared instance
     */
    public static CorePlugin getDefault() {
        return plugin;
    }

    // public void save() {
    // NeedSaveDataProviderHelper.saveAllDataProvider();
    // }

    /**
     * Returns an image descriptor for the image file at the given plug-in relative path.
     * 
     * @param path the path
     * @return the image descriptor
     */
    public static ImageDescriptor getImageDescriptor(String path) {
        return imageDescriptorFromPlugin(PLUGIN_ID, path);
    }

    // public void loadExternalDriver(String driverPaths, String driverName,
    // String url) {
    // String[] driverJarPath = driverPaths.split(";");
    // LinkedList<String> driverFile = new LinkedList<String>();
    // for (String driverpath : driverJarPath) {
    // driverFile.add(driverpath);
    // }
    // ManagedDriver driver = new
    // ManagedDriver(SQLExplorerPlugin.getDefault().getDriverModel
    // ().createUniqueId());
    // driver.setJars(driverFile);
    // driver.setDriverClassName(driverName);
    // driver.setUrl(url);
    // SQLExplorerPlugin.getDefault().getDriverModel().addDriver(driver);
    // }

    /**
     * DOC Zqin Comment method "getCurrentActiveEditor".
     * 
     * @return the current active editor;
     */
    public IEditorPart getCurrentActiveEditor() {
        return getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
    }

    /**
     * DOC Zqin Comment method "runInDQViewer". this method open DQ responsitory view and run the specified query.
     * 
     * @param tdDataProvider
     * @param query
     */
    public void runInDQViewer(Connection tdDataProvider, String query, String editorName) {
        SQLEditor sqlEditor = openInSqlEditor(tdDataProvider, query, editorName);
        if (sqlEditor != null) {
            new ExecSQLAction(sqlEditor).run();
        }
    }

    /**
     * DOC bZhou Comment method "openInSqlEditor".
     * 
     * @param tdDataProvider
     * @param query
     * @param editorName
     * @return the specified sql editor.
     */
    public SQLEditor openInSqlEditor(Connection tdDataProvider, String query, String editorName) {
        if (editorName == null) {
            editorName = String.valueOf(SQLExplorerPlugin.getDefault().getEditorSerialNo());
        }

        SQLExplorerPlugin sqlPlugin = SQLExplorerPlugin.getDefault();
        AliasManager aliasManager = sqlPlugin.getAliasManager();

        Alias alias = aliasManager.getAlias(tdDataProvider.getName());

        if (alias == null) {
            Collection<Connection> allDataProviders = new ArrayList<Connection>();
            
            List<Connection> conns = new ArrayList<Connection>();
            try {
                for (ConnectionItem connItem : ProxyRepositoryFactory.getInstance().getMetadataConnectionsItem()) {
                    conns.add(connItem.getConnection());
                }
            } catch (PersistenceException e) {
                Log.error(e, e);
            }
            allDataProviders.addAll(conns);
            
            
            
            for (Connection dataProvider : allDataProviders) {
                // MOD xqliu 2010-10-13 bug 15756
                // if (dataProvider.getId().equals(tdDataProvider.getId())) {
                if (dataProvider.getName().equals(tdDataProvider.getName())) {
                    // ~ 15756
                    CWMPlugin.getDefault().addConnetionAliasToSQLPlugin(dataProvider);
                    openInSqlEditor(tdDataProvider, query, editorName);
                }
            }
        } else {
            try {
                Connection connection = SwitchHelpers.CONNECTION_SWITCH.doSwitch(tdDataProvider);
                if (connection != null) {
                    String userName = JavaSqlFactory.getUsernameDefault(connection);
                    SQLEditorInput input = new SQLEditorInput("SQL Editor (" + alias.getName() + "." + editorName + ").sql"); //$NON-NLS-1$ //$NON-NLS-2$
                    input.setUser(alias.getUser(userName));
                    IWorkbenchPage page = SQLExplorerPlugin.getDefault().getActivePage();
                    SQLEditor editorPart = (SQLEditor) page.openEditor((IEditorInput) input, SQLEditor.class.getName());
                    editorPart.setText(query);
                    return editorPart;
                }
            } catch (PartInitException e) {
                log.error(e, e);
            }
        }

        return null;
    }

    /**
     * DOC bZhou Comment method "openEditor".
     * 
     * @param file
     * @param editorId
     * @return
     */
    public IEditorPart openEditor(IFile file, String editorId) {
        FileEditorInput input = new FileEditorInput(file);
        // input.setUser(alias.getDefaultUser());
        try {

            return this.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(input, editorId);
        } catch (PartInitException e) {
            ExceptionHandler.process(e);
            return null;
        }
    }

    /**
     * 
     * DOC mzhao open editor with editor input.
     * 
     * @param editorInput
     * @param editorId
     * @return
     */
    public IEditorPart openEditor(IEditorInput editorInput, String editorId) {
        try {
            return this.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(editorInput, editorId);
        } catch (PartInitException e) {
            ExceptionHandler.process(e);
            return null;
        }
    }

    /**
     * Get viewPart with special partId. If the active page doesn't exsit, the method will return null; Else, it will
     * get the viewPart and focus it. if the viewPart closed, it will be opened.
     * 
     * @param viewId the identifier of viewPart
     * @return
     */
    private IViewPart findView(String viewId) {
        IWorkbenchWindow activeWorkbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (activeWorkbenchWindow == null) {
            return null;
        }
        IWorkbenchPage page = activeWorkbenchWindow.getActivePage();
        if (page == null) {
            return null;
        }
        IViewPart part = page.findView(viewId);
        if (part == null) {
            try {
                part = page.showView(viewId);
            } catch (Exception e) {
                ExceptionHandler.process(e, Level.ERROR);
            }
        } else {
            page.bringToTop(part);
        }
        return part;
    }

    /**
     * DOC bzhou Comment method "getRepositoryView".
     * 
     * @return
     */
    public DQRespositoryView getRepositoryView() {
        IViewPart view = findView(DQRespositoryView.ID);
        return view != null ? (DQRespositoryView) view : null;
    }

    /**
     * DOC bzhou Comment method "getPatternTestView".
     * 
     * @return
     */
    public PatternTestView getPatternTestView() {
        IViewPart view = findView(PatternTestView.ID);
        return view != null ? (PatternTestView) view : null;
    }

    public void refreshWorkSpace() {
        if (refreshAction == null) {
            refreshAction = new RefreshAction(PlatformUI.getWorkbench().getActiveWorkbenchWindow());

        }
        refreshAction.run();
    }

    public void refreshDQView() {
        getRepositoryView().getCommonViewer().refresh();
    }

    /**
     * DOC bzhou Comment method "getProductVersion".
     * 
     * @return
     */
    public ProductVersion getProductVersion() {
        Object obj = plugin.getBundle().getHeaders().get(org.osgi.framework.Constants.BUNDLE_VERSION);
        ProductVersion currentVersion = ProductVersion.fromString(obj.toString());
        return currentVersion;
    }

    /**
     * DOC qiongli close editor by file.
     * 
     * @param fileRes
     */
    public void closeEditorIfOpened(Property property) {
        IWorkbenchPage activePage = CorePlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage();
        IEditorReference[] editorReferences = activePage.getEditorReferences();
        IEditorInput editorInput = null;
        for (IEditorReference reference : editorReferences) {
            try {
                editorInput = reference.getEditorInput();
                if (editorInput instanceof FileEditorInput) {
                    FileEditorInput fileInput = (FileEditorInput) editorInput;

                    if (property.eResource() != null) {
                        IPath propPath = new Path(property.eResource().getURI().lastSegment()).removeFileExtension();
                        IPath filePath = new Path(fileInput.getFile().getName()).removeFileExtension();
                        if (filePath.equals(propPath)) {
                            activePage.closeEditor(reference.getEditor(false), false);
                            break;
                        }
                    }
                } else if (editorInput instanceof SQLEditorInput) {// bug 16349.
                    SQLEditorInput sqlEditorInput = (SQLEditorInput) editorInput;
                    // MOD qiongli 2010-11-26 bug 17009
                    if (sqlEditorInput.getUser() == null) {
                        if (sqlEditorInput.getName().equals(property.getLabel())) {
                            activePage.closeEditor(reference.getEditor(false), false);
                        }
                    } else if (sqlEditorInput.getUser().getAlias().getName().equals(property.getLabel())) {
                        activePage.closeEditor(reference.getEditor(false), false);
                    }

                } else if (editorInput instanceof ConnectionItemEditorInput) {
                    ConnectionItemEditorInput connectionInput = (ConnectionItemEditorInput) editorInput;
                    if (property.equals(connectionInput.getItem().getProperty())) {
                        activePage.closeEditor(reference.getEditor(false), false);
                        // break;
                    }
                }
            } catch (PartInitException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 
     * DOC zshen Comment method "initProxyRepository".
     * 
     */
    public boolean initProxyRepository() {

        Project project = null;
        RepositoryContext repositoryContext = (RepositoryContext) org.talend.core.runtime.CoreRuntimePlugin.getInstance()
                .getContext().getProperty(Context.REPOSITORY_CONTEXT_KEY);
        if (repositoryContext != null) {
            project = repositoryContext.getProject();
            User user = repositoryContext.getUser();

            ReponsitoryContextBridge.initialized(project.getEmfProject(), user);
        } else { // else project is null, then we are in TOP only
            ProxyRepositoryFactory proxyRepository = ProxyRepositoryFactory.getInstance();
            IRepositoryFactory repository = RepositoryFactoryProvider.getRepositoriyById("local");//$NON-NLS-1$
            if (repository == null) {
                log.fatal("No local Repository found! Probably due to a missing plugin in the product.");
                return false;
            }
            proxyRepository.setRepositoryFactoryFromProvider(repository);
            try {
                proxyRepository.checkAvailability();
                proxyRepository.initialize();

                XmiResourceManager xmiResourceManager = new XmiResourceManager();
                IProject rootProject = ResourceManager.getRootProject();
                if (rootProject.getFile(FileConstants.LOCAL_PROJECT_FILENAME).exists()) {
                    // Initialize TDQ EMF model packages.
                    new EMFUtil();
                    project = new Project(xmiResourceManager.loadProject(rootProject));
                } else {
                    User user = PropertiesFactoryImpl.eINSTANCE.createUser();
                    user.setLogin("talend@talend.com");
                    user.setPassword("talend@talend.com".getBytes());
                    String projectName = ResourceManager.getRootProjectName();
                    String projectDesc = ResourcesPlugin.getWorkspace().newProjectDescription(projectName).getComment();
                    Project projectInfor = ProjectHelper.createProject(projectName, projectDesc, ECodeLanguage.JAVA.getName(),
                            user);

                    // MOD zshen create project by proxyRepository
                    checkFileName(projectInfor.getLabel(), RepositoryConstants.PROJECT_PATTERN);

                    project = proxyRepository.getRepositoryFactoryFromProvider().createProject(projectInfor);

                }

                if (project != null) {
                    initRepositoryContext(project);

                    // add status
                    String defaultTechnicalStatusList = "DEV development;TEST testing;PROD production";
                    List<Status> statusList = StatusHelper.parse(defaultTechnicalStatusList);
                    proxyRepository.setTechnicalStatus(statusList);
                }

            } catch (PersistenceException e) {
                ExceptionHandler.process(e);
                return false;
            }
        }
        return true;
    }

    private void initRepositoryContext(Project project) {
        RepositoryContext repositoryContext = new RepositoryContext();
        repositoryContext.setUser(project.getAuthor());
        repositoryContext.setClearPassword(project.getLabel());
        repositoryContext.setProject(project);
        repositoryContext.setFields(new HashMap<String, String>());
        repositoryContext.getFields().put(IProxyRepositoryFactory.BRANCH_SELECTION + "_" + project.getTechnicalLabel(), "");
        Context ctx = CoreRuntimePlugin.getInstance().getContext();
        ctx.putProperty(Context.REPOSITORY_CONTEXT_KEY, repositoryContext);

        ReponsitoryContextBridge.initialized(project.getEmfProject(), project.getAuthor());
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
    private void checkFileName(String fileName, String pattern) {
        if (!Pattern.matches(pattern, fileName)) {
            throw new IllegalArgumentException(DefaultMessagesImpl.getString(
                    "ProxyRepositoryFactory.illegalArgumentException.labelNotMatchPattern", new Object[] { fileName, pattern })); //$NON-NLS-1$
        }
    }

}
