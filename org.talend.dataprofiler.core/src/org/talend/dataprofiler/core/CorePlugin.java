// ============================================================================
//
// Copyright (C) 2006-2010 Talend Inc. - www.talend.com
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

import java.util.Collection;

import net.sourceforge.sqlexplorer.dbproduct.Alias;
import net.sourceforge.sqlexplorer.dbproduct.AliasManager;
import net.sourceforge.sqlexplorer.plugin.SQLExplorerPlugin;
import net.sourceforge.sqlexplorer.plugin.editors.SQLEditor;
import net.sourceforge.sqlexplorer.plugin.editors.SQLEditorInput;
import net.sourceforge.sqlexplorer.sqleditor.actions.ExecSQLAction;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
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
import org.osgi.framework.BundleContext;
import org.talend.commons.bridge.ReponsitoryContextBridge;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.properties.Property;
import org.talend.cwm.db.connection.ConnectionUtils;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.dataprofiler.core.exception.ExceptionHandler;
import org.talend.dataprofiler.core.ui.views.DQRespositoryView;
import org.talend.dataprofiler.core.ui.views.PatternTestView;
import org.talend.dataprofiler.help.BookMarkEnum;
import org.talend.dq.CWMPlugin;
import org.talend.dq.helper.DQConnectionReposViewObjDelegator;
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
            Collection<Connection> allDataProviders = DQConnectionReposViewObjDelegator.getInstance().getAllElements();
            for (Connection dataProvider : allDataProviders) {
                if (dataProvider == tdDataProvider) {
                    CWMPlugin.getDefault().addConnetionAliasToSQLPlugin(dataProvider);
                    openInSqlEditor(tdDataProvider, query, editorName);
                }
            }
        } else {
            try {
                Connection connection = SwitchHelpers.CONNECTION_SWITCH.doSwitch(tdDataProvider);
                if (connection != null) {
                    String userName = ConnectionUtils.getUsernameDefault(connection);
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
                }
            } catch (PartInitException e) {
                e.printStackTrace();
            }
        }
    }
}
