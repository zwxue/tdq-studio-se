// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import net.sourceforge.sqlexplorer.dbproduct.Alias;
import net.sourceforge.sqlexplorer.dbproduct.AliasManager;
import net.sourceforge.sqlexplorer.plugin.SQLExplorerPlugin;
import net.sourceforge.sqlexplorer.plugin.editors.SQLEditor;
import net.sourceforge.sqlexplorer.plugin.editors.SQLEditorInput;
import net.sourceforge.sqlexplorer.sqleditor.actions.ExecSQLAction;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.help.internal.base.BaseHelpSystem;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.RefreshAction;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.jfree.util.Log;
import org.osgi.framework.BundleContext;
import org.talend.commons.bridge.ReponsitoryContextBridge;
import org.talend.commons.emf.EMFUtil;
import org.talend.commons.exception.ExceptionHandler;
import org.talend.commons.exception.PersistenceException;
import org.talend.commons.utils.VersionUtils;
import org.talend.core.GlobalServiceRegister;
import org.talend.core.context.Context;
import org.talend.core.context.RepositoryContext;
import org.talend.core.database.EDatabaseTypeName;
import org.talend.core.language.ECodeLanguage;
import org.talend.core.model.general.ILibrariesService;
import org.talend.core.model.general.Project;
import org.talend.core.model.metadata.IMetadataConnection;
import org.talend.core.model.metadata.builder.ConvertionHelper;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.metadata.builder.connection.DatabaseConnection;
import org.talend.core.model.metadata.builder.database.JavaSqlFactory;
import org.talend.core.model.metadata.builder.util.MetadataConnectionUtils;
import org.talend.core.model.properties.ConnectionItem;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.Property;
import org.talend.core.model.properties.Status;
import org.talend.core.model.properties.User;
import org.talend.core.model.properties.helper.StatusHelper;
import org.talend.core.model.properties.impl.PropertiesFactoryImpl;
import org.talend.core.prefs.ITalendCorePrefConstants;
import org.talend.core.repository.constants.FileConstants;
import org.talend.core.repository.model.IRepositoryFactory;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.core.repository.model.RepositoryFactoryProvider;
import org.talend.core.repository.utils.ProjectHelper;
import org.talend.core.repository.utils.XmiResourceManager;
import org.talend.core.runtime.CoreRuntimePlugin;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.editor.AbstractItemEditorInput;
import org.talend.dataprofiler.core.ui.editor.CommonFormEditor;
import org.talend.dataprofiler.core.ui.editor.analysis.AnalysisEditor;
import org.talend.dataprofiler.core.ui.utils.MessageUI;
import org.talend.dataprofiler.core.ui.utils.WorkbenchUtils;
import org.talend.dataprofiler.core.ui.views.DQRespositoryView;
import org.talend.dataprofiler.core.ui.views.PatternTestView;
import org.talend.dataprofiler.help.BookMarkEnum;
import org.talend.dq.CWMPlugin;
import org.talend.dq.helper.PropertyHelper;
import org.talend.repository.ProjectManager;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.RepositoryConstants;
import org.talend.resource.ResourceManager;
import org.talend.utils.ProductVersion;
import org.talend.utils.sugars.ReturnCode;

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
    @SuppressWarnings("restriction")
    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
        getPreferenceStore().setDefault(PluginConstant.CHEAT_SHEET_VIEW, true);
        getPreferenceStore().setValue(ITalendCorePrefConstants.PREVIEW_LIMIT, "50");//$NON-NLS-1$
        getPreferenceStore().setValue(ITalendCorePrefConstants.LANGUAGE_SELECTOR, Locale.getDefault().getLanguage());
        try {
            for (BookMarkEnum bookMark : BookMarkEnum.VALUES) {
                BaseHelpSystem.getBookmarkManager().addBookmark(bookMark.getHref(), bookMark.getLabel());
            }
        } catch (Exception e) {
            log.error(e, e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext )
     */
    @Override
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }

    /**
     * Returns the shared instance.
     * 
     * @return the shared instance
     */
    public static CorePlugin getDefault() {
        return plugin;
    }

    /**
     * Returns an image descriptor for the image file at the given plug-in relative path.
     * 
     * @param path the path
     * @return the image descriptor
     */
    public static ImageDescriptor getImageDescriptor(String path) {
        return imageDescriptorFromPlugin(PLUGIN_ID, path);
    }

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
        String lEditorName = editorName;
        if (lEditorName == null) {
            lEditorName = String.valueOf(SQLExplorerPlugin.getDefault().getEditorSerialNo());
        }

        String dbType = PluginConstant.EMPTY_STRING;
        DatabaseConnection dbConn = SwitchHelpers.DATABASECONNECTION_SWITCH.doSwitch(tdDataProvider);
        if (dbConn != null) {
            dbType = dbConn.getDatabaseType();
        }
        // MOD qiongli 2013-12-9,TDQ-8442,if the database type is not supported on DQ side,ruturn null.
        List<String> tdqSupportDBType = MetadataConnectionUtils.getTDQSupportDBTemplate();
        String username = JavaSqlFactory.getUsername(tdDataProvider);
        boolean notSupport = (EDatabaseTypeName.HIVE.getXmlName().equalsIgnoreCase(dbType) && !"STANDALONE".equals(dbConn //$NON-NLS-1$
                .getDbVersionString())) || EDatabaseTypeName.MSSQL.getDisplayName().equalsIgnoreCase(dbType)
                && (username == null || PluginConstant.EMPTY_STRING.equals(username)) || !tdqSupportDBType.contains(dbType);
        if (notSupport) {
            MessageUI.openWarning(DefaultMessagesImpl.getString("CorePlugin.cantPreview")); //$NON-NLS-1$ 
            return null;
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
                    openInSqlEditor(tdDataProvider, query, lEditorName);
                }
            }
        } else {
            try {
                Connection connection = SwitchHelpers.CONNECTION_SWITCH.doSwitch(tdDataProvider);
                if (connection != null) {
                    String userName = JavaSqlFactory.getUsername(connection);

                    String url = JavaSqlFactory.getURL(connection);
                    SQLEditorInput input = new SQLEditorInput("SQL Editor (" + alias.getName() + "." + lEditorName + ").sql"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                    net.sourceforge.sqlexplorer.dbproduct.User user;
                    user = alias.getUser(userName);
                    if (PluginConstant.EMPTY_STRING.equals(userName)) {
                        // get the user both the dbtype and username are the same.
                        if (!alias.getUrl().equals(url)) {
                            String password = JavaSqlFactory.getPassword(connection);
                            user = new net.sourceforge.sqlexplorer.dbproduct.User(userName, password);
                            user.setAlias(alias);
                            alias.addUser(user);
                        }
                    } else {
                        if (user == null) {
                            user = alias.getDefaultUser();
                        }
                    }
                    alias.setDefaultUser(user);

                    // set IMetadataConnection into the user, if the db type is hive, should use IMetadataConnection to
                    // create the hive connection
                    DatabaseConnection databaseConnection = SwitchHelpers.DATABASECONNECTION_SWITCH.doSwitch(connection);
                    if (databaseConnection != null) {
                        IMetadataConnection metadataConnection = ConvertionHelper.convert(databaseConnection);
                        if (metadataConnection != null) {
                            user.setMetadataConnection(metadataConnection);
                        }
                    }

                    input.setUser(user);
                    IWorkbenchPage page = SQLExplorerPlugin.getDefault().getActivePage();
                    SQLEditor editorPart = (SQLEditor) page.openEditor(input, SQLEditor.class.getName());
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
     * @deprecated
     */
    @Deprecated
    public IEditorPart openEditor(IFile file, String editorId) {
        FileEditorInput input = new FileEditorInput(file);
        try {

            return this.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(input, editorId);
        } catch (PartInitException e) {
            ExceptionHandler.process(e);
            return null;
        }
    }

    /**
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
     * DOC bzhou Comment method "getRepositoryView".
     * 
     * @return
     */
    public DQRespositoryView getRepositoryView() {
        IViewPart view = WorkbenchUtils.getAndOpenView(DQRespositoryView.ID);
        return view != null ? (DQRespositoryView) view : null;
    }

    /**
     * DOC bzhou Comment method "getPatternTestView".
     * 
     * @return
     */
    public PatternTestView getPatternTestView() {
        IViewPart view = WorkbenchUtils.getAndOpenView(PatternTestView.ID);
        return view != null ? (PatternTestView) view : null;
    }

    public void refreshWorkSpace() {
        if (refreshAction == null) {
            refreshAction = new RefreshAction(PlatformUI.getWorkbench().getActiveWorkbenchWindow());
        }
        refreshAction.run();
    }

    /**
     * refresh the whole DQReposirotyView.
     */
    public void refreshDQView() {
        DQRespositoryView repositoryView = getRepositoryView();
        if (repositoryView != null && repositoryView.getCommonViewer() != null) {
            repositoryView.getCommonViewer().refresh();
        } else {
            log.error(DefaultMessagesImpl.getString("CorePlugin.nullViewWhenRefresh")); //$NON-NLS-1$
        }
    }

    public IRepositoryNode getCurrentSelectionNode() {
        TreeItem[] selectionTreeItem = getRepositoryView().getCommonViewer().getTree().getSelection();
        if (null != selectionTreeItem && selectionTreeItem.length > 0 && null != selectionTreeItem[0]) {
            IRepositoryNode repoNode = (IRepositoryNode) selectionTreeItem[0].getData();
            return repoNode;
        } else {
            return null;
        }

    }

    /**
     * refresh the object of DQReposirotyView.
     * 
     * @param object
     */
    public void refreshDQView(Object object) {
        if (object == null) {
            refreshDQView();
        } else {
            DQRespositoryView repositoryView = getRepositoryView();
            if (repositoryView != null && repositoryView.getCommonViewer() != null) {
                repositoryView.getCommonViewer().refresh(object);
            } else {
                log.error(DefaultMessagesImpl.getString("CorePlugin.nullViewWhenRefresh")); //$NON-NLS-1$
            }
        }
    }

    /**
     * DOC bzhou Comment method "getProductVersion".
     * 
     * @return
     */
    public ProductVersion getProductVersion() {
        ProductVersion currentVersion = ProductVersion.fromString(VersionUtils.getVersion());
        return currentVersion;
    }

    /**
     * DOC qiongli close editor by file.
     * 
     * @param fileRes
     */
    public void closeEditorIfOpened(Item item) {
        itemIsOpening(item, true);
    }

    /**
     * check the item's editor is opening or not.
     * 
     * @param item
     * @return
     */
    public boolean itemIsOpening(Item item) {
        return itemIsOpening(item, false);
    }

    /**
     * check the item's editor is opening or not.
     * 
     * @param item
     * @param closeEditor close the editor if it is opening
     * @return
     */
    public boolean itemIsOpening(Item item, boolean closeEditor) {
        boolean opening = false;
        IWorkbenchPage activePage = CorePlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage();
        IEditorReference[] editorReferences = activePage.getEditorReferences();
        IEditorInput editorInput = null;
        Property property = item.getProperty();
        for (IEditorReference reference : editorReferences) {
            try {
                editorInput = reference.getEditorInput();
                if (editorInput instanceof FileEditorInput) {
                    FileEditorInput fileInput = (FileEditorInput) editorInput;

                    if (property.eResource() != null) {
                        IPath itemPath = PropertyHelper.getItemPath(property);
                        if (itemPath != null && itemPath.equals(fileInput.getFile().getFullPath())) {
                            opening = true;
                            if (closeEditor) {
                                activePage.closeEditor(reference.getEditor(false), false);
                            }
                            break;
                        }
                    }
                } else if (editorInput instanceof SQLEditorInput) {// bug 16349.
                    SQLEditorInput sqlEditorInput = (SQLEditorInput) editorInput;
                    // MOD qiongli 2010-11-26 bug 17009
                    if (sqlEditorInput.getUser() == null) {
                        if (sqlEditorInput.getName().equals(property.getLabel())) {
                            opening = true;
                            if (closeEditor) {
                                activePage.closeEditor(reference.getEditor(false), false);
                            }
                            break;
                        }
                    } else if (sqlEditorInput.getUser().getAlias().getName().equals(property.getLabel())) {
                        opening = true;
                        if (closeEditor) {
                            activePage.closeEditor(reference.getEditor(false), false);
                        }
                        break;
                    }

                } else if (editorInput instanceof AbstractItemEditorInput) {
                    AbstractItemEditorInput input = (AbstractItemEditorInput) editorInput;
                    Item it = input.getItem();
                    if (it != null && property != null && it.getProperty().getId().equals(property.getId())) {
                        opening = true;
                        if (closeEditor) {
                            activePage.closeEditor(reference.getEditor(false), false);
                        }
                        break;
                    }
                }
            } catch (PartInitException e) {
                log.error(e);
                continue;
            }
        }
        return opening;
    }

    /**
     * 
     * refresh the related connection which is opened in DQ side.
     * 
     * @param item
     */
    public void refreshOpenedEditor(Item item) {

        if (item == null) {
            return;
        }
        IWorkbenchPage activePage = CorePlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage();
        IEditorReference[] editorReferences = activePage.getEditorReferences();
        Property property = item.getProperty();
        for (IEditorReference reference : editorReferences) {
            try {
                IEditorInput input = reference.getEditorInput();
                if (input instanceof AbstractItemEditorInput) {
                    AbstractItemEditorInput itemInput = (AbstractItemEditorInput) input;
                    Item it = itemInput.getItem();
                    if (it != null && property != null && it.getProperty().getId().equals(property.getId())) {
                        // make sure the item in editorInput is latest.
                        if (!item.equals(it)) {
                            itemInput.setItem(item);
                        }
                        CommonFormEditor editor = (CommonFormEditor) reference.getEditor(false);
                        editor.refreshEditor();
                        break;
                    }
                }
            } catch (PartInitException e) {
                log.error(e);
                continue;
            }
        }
    }

    public List<AnalysisEditor> getCurrentOpenAnalysisEditor() {
        IWorkbenchPage activePage = CorePlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage();
        IEditorReference[] editorReferences = activePage.getEditorReferences();
        List<AnalysisEditor> result = new ArrayList<AnalysisEditor>();

        for (IEditorReference iEditorReference : editorReferences) {
            IEditorPart editor = iEditorReference.getEditor(false);
            if (editor instanceof AnalysisEditor) {
                result.add((AnalysisEditor) editor);
            }
        }
        return result;
    }

    /**
     * DOC zshen Comment method "initProxyRepository".
     */
    public ReturnCode initProxyRepository() {
        ReturnCode rc = new ReturnCode();
        Project project = null;
        RepositoryContext repositoryContext = (RepositoryContext) org.talend.core.runtime.CoreRuntimePlugin.getInstance()
                .getContext().getProperty(Context.REPOSITORY_CONTEXT_KEY);
        if (repositoryContext != null) {
            project = repositoryContext.getProject();
            User user = repositoryContext.getUser();

            ReponsitoryContextBridge.initialized(project.getEmfProject(), user);
        } else { // else project is null, then we are in TOP only
            Location instanceLoc = Platform.getInstanceLocation();
            try {
                if (instanceLoc.isLocked()) {
                    rc.setMessage(DefaultMessagesImpl.getString("CorePlugin.workspaceInUse"));//$NON-NLS-1$
                    rc.setOk(false);
                    return rc;
                } else {
                    instanceLoc.lock();
                }
            } catch (IOException e) {
                log.error(e, e);
            }
            ProxyRepositoryFactory proxyRepository = ProxyRepositoryFactory.getInstance();
            IRepositoryFactory repository = RepositoryFactoryProvider.getRepositoriyById("local"); //$NON-NLS-1$
            if (repository == null) {
                log.fatal(DefaultMessagesImpl.getString("CorePlugin.noLocalRepositoryFound")); //$NON-NLS-1$
                rc.setMessage(DefaultMessagesImpl.getString("CorePlugin.noLocalRepositoryFound"));//$NON-NLS-1$
                rc.setOk(false);
                return rc;
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
                    user.setLogin("talend@talend.com"); //$NON-NLS-1$
                    user.setPassword("talend@talend.com".getBytes()); //$NON-NLS-1$
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
                    String defaultTechnicalStatusList = "DEV development;TEST testing;PROD production"; //$NON-NLS-1$
                    List<Status> statusList = StatusHelper.parse(defaultTechnicalStatusList);
                    proxyRepository.setTechnicalStatus(statusList);

                    // deploy libraries here
                    if (GlobalServiceRegister.getDefault().isServiceRegistered(ILibrariesService.class)) {
                        ILibrariesService librariesService = (ILibrariesService) GlobalServiceRegister.getDefault().getService(
                                ILibrariesService.class);
                        if (librariesService != null) {
                            librariesService.syncLibrariesFromApp();
                            CWMPlugin.getDefault().createLibFolderIfNotExist();
                        }
                    }
                }
            } catch (PersistenceException e) {
                ExceptionHandler.process(e);
                rc.setMessage(e.getMessage());
                rc.setOk(false);
                return rc;
            }
        }
        return rc;
    }

    private void initRepositoryContext(Project project) {
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
        // MOD zshen for bug tdq-4757 remove this init from corePlugin.start() to here because the initLocal command of
        // commandLine
        SQLExplorerPlugin.getDefault().setRootProject(ReponsitoryContextBridge.getRootProject());
    }

    /**
     * copy the method from ProxyRepositoryFactory to avoid tos migeration.
     * 
     * @param fileName
     * @param pattern
     */
    private void checkFileName(String fileName, String pattern) {
        if (!Pattern.matches(pattern, fileName)) {
            throw new IllegalArgumentException(DefaultMessagesImpl.getString(
                    "ProxyRepositoryFactory.illegalArgumentException.labelNotMatchPattern", new Object[] { fileName, pattern })); //$NON-NLS-1$
        }
    }
}
