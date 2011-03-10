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
package org.talend.dataprofiler.core.ui.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.sqlexplorer.plugin.SQLExplorerPlugin;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.TreeAdapter;
import org.eclipse.swt.events.TreeEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionContext;
import org.eclipse.ui.actions.RefreshAction;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.navigator.CommonNavigator;
import org.eclipse.ui.part.FileEditorInput;
import org.talend.commons.exception.PersistenceException;
import org.talend.core.model.general.Project;
import org.talend.core.model.properties.ConnectionItem;
import org.talend.core.model.properties.DatabaseConnectionItem;
import org.talend.core.model.properties.FolderItem;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.cwm.compare.DQStructureComparer;
import org.talend.cwm.relational.TdTable;
import org.talend.cwm.relational.TdView;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.manager.DQStructureManager;
import org.talend.dataprofiler.core.migration.MigrationTaskManager;
import org.talend.dataprofiler.core.model.nodes.foldernode.ColumnFolderNode;
import org.talend.dataprofiler.core.model.nodes.foldernode.TableFolderNode;
import org.talend.dataprofiler.core.model.nodes.foldernode.ViewFolderNode;
import org.talend.dataprofiler.core.service.GlobalServiceRegister;
import org.talend.dataprofiler.core.service.IService;
import org.talend.dataprofiler.core.service.IViewerFilterService;
import org.talend.dataprofiler.core.ui.ResoureceChangedListener;
import org.talend.dataprofiler.core.ui.action.actions.OpenItemEditorAction;
import org.talend.dataprofiler.core.ui.editor.indicator.IndicatorEditor;
import org.talend.dataprofiler.core.ui.editor.indicator.IndicatorEditorInput;
import org.talend.dataprofiler.core.ui.filters.AbstractViewerFilter;
import org.talend.dataprofiler.core.ui.filters.EMFObjFilter;
import org.talend.dataprofiler.core.ui.filters.FolderObjFilter;
import org.talend.dataprofiler.core.ui.filters.ReportingFilter;
import org.talend.dataprofiler.core.ui.utils.WorkbenchUtils;
import org.talend.dataquality.indicators.definition.IndicatorDefinition;
import org.talend.dq.CWMPlugin;
import org.talend.dq.helper.RepositoryNodeHelper;
import org.talend.dq.indicators.definitions.DefinitionHandler;
import org.talend.dq.nodes.AnalysisRepNode;
import org.talend.dq.nodes.DBConnectionRepNode;
import org.talend.dq.nodes.DFConnectionRepNode;
import org.talend.dq.nodes.MDMConnectionRepNode;
import org.talend.dq.nodes.PatternRepNode;
import org.talend.dq.nodes.ReportFileRepNode;
import org.talend.dq.nodes.ReportRepNode;
import org.talend.dq.nodes.RuleRepNode;
import org.talend.dq.nodes.SysIndicatorDefinitionRepNode;
import org.talend.dq.nodes.foldernode.AbstractFolderNode;
import org.talend.dq.nodes.foldernode.IFolderNode;
import org.talend.repository.ProjectManager;
import org.talend.repository.RepositoryWorkUnit;
import org.talend.repository.model.RepositoryNode;
import org.talend.resource.ResourceManager;
import org.talend.resource.ResourceService;
import org.talend.top.repository.ProxyRepositoryManager;

/**
 * @author rli
 * 
 */
public class DQRespositoryView extends CommonNavigator {

    protected static Logger log = Logger.getLogger(DQRespositoryView.class);

    public static final String ID = "org.talend.dataprofiler.core.ui.views.DQRespositoryView"; //$NON-NLS-1$

    private Map<String, AbstractViewerFilter> filterMap = new HashMap<String, AbstractViewerFilter>();

    private static final String VIEW_CONTEXT_ID = "org.talend.dataprofiler.core.ui.views.DQRespositoryView.viewScope"; //$NON-NLS-1$

    private ITreeContentProvider contentProvider = null;

    public DQRespositoryView() {
        super();

        final DQStructureManager manager = DQStructureManager.getInstance();

        if (manager.isNeedCreateStructure()) {
            ProxyRepositoryFactory.getInstance().executeRepositoryWorkUnit(
                    new RepositoryWorkUnit<Object>("Create DQ Repository structure") {

                        @Override
                        protected void run() {
                            manager.createDQStructure();
                        }
                    });
        }

        if (manager.isNeedMigration()) {
            // MOD qiongli 2010-9-7,bug 14698,add 'try...catch'
            try {
                MigrationTaskManager.doMigrationTask(MigrationTaskManager.findWorksapceTasks());
            } catch (Exception e) {
                log.error(e, e);
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.navigator.CommonNavigator#init(org.eclipse.ui.IViewSite, org.eclipse.ui.IMemento)
     */
    @Override
    public void init(IViewSite site, IMemento memento) throws PartInitException {
        super.init(site, memento);
        if (memento == null) {
            setLinkingEnabled(false);
        }
        // MOD qiongli 2010-9-7,bug 14698,add 'try...catch'
        try {
            addPostWindowCloseListener();

            // addResourceChangedListener();

            initToolBar();

            initWorkspace();

        } catch (Exception e) {
            log.error(e, e);
        }
    }

    /**
     * DOC bZhou Comment method "initWorkspace".
     */
    private void initWorkspace() {

        // initialized resource persistence property.
        ResourceService.initResourcePersistence();

        // initialized drivers in sql explorer.
        SQLExplorerPlugin.getDefault().initAllDrivers();
        try {
            for (ConnectionItem item : ProxyRepositoryFactory.getInstance().getMetadataConnectionsItem()) {
                if (item == null || item instanceof DatabaseConnectionItem) {
                    continue;
                }
                CWMPlugin.getDefault().addConnetionAliasToSQLPlugin(item.getConnection());
            }
        } catch (PersistenceException e) {
            log.error(e, e);
        }

        IFile defFile = ResourceManager.getLibrariesFolder().getFile(DefinitionHandler.FILENAME);
        if (!defFile.exists()) {
            DefinitionHandler.getInstance();
        }
        // MOD qiongli 2011-3-2 feature 17588.initilize all folder.
        initAllFolders();
    }

    /**
     * DOC bZhou Comment method "initToolBar".
     */
    private void initToolBar() {
        IToolBarManager toolBarManager = getViewSite().getActionBars().getToolBarManager();
        toolBarManager.add(new RefreshDQReponsitoryViewAction());
    }

    private void addResourceChangedListener() {
        IWorkspace workspace = ResourceManager.getRootProject().getWorkspace();
        workspace.addResourceChangeListener(new ResoureceChangedListener());
    }

    /**
     * MOD mzhao bug 8581 Add pre window close listener.
     */
    private void addPostWindowCloseListener() {

        PlatformUI.getWorkbench().addWorkbenchListener(new IWorkbenchListener() {

            public void postShutdown(IWorkbench workbench) {
            }

            public boolean preShutdown(IWorkbench workbench, boolean forced) {
                // Clean the copied comparison resources under folder
                // "Metadata/"
                IFolder folder = ResourceManager.getConnectionFolder();
                try {
                    IResource[] resources = folder.members(true);
                    for (IResource resource : resources) {
                        if (resource instanceof IFile) {
                            if (resource.getFileExtension().equalsIgnoreCase(DQStructureComparer.COMPARE_FILE_EXTENSION)
                                    || resource.getFileExtension().equalsIgnoreCase(
                                            DQStructureComparer.RESULT_EMFDIFF_FILE_EXTENSION)) {
                                resource.delete(true, null);
                            }
                        }
                    }
                } catch (CoreException e) {
                    log.error(e, e);
                }
                return true;
            }

        });
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.navigator.CommonNavigator#createPartControl(org.eclipse .swt.widgets.Composite)
     */
    @Override
    public void createPartControl(Composite parent) {
        super.createPartControl(parent);

        // For removing the popup menu of DQRepositoryView.
        MenuManager menuMgr = new MenuManager("org.talend.dataprofiler.core.ui.views.DQRespositoryView"); //$NON-NLS-1$
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(new IMenuListener() {

            public void menuAboutToShow(IMenuManager manager) {
                ISelection selection = getCommonViewer().getSelection();
                getNavigatorActionService().setContext(new ActionContext(selection));
                getNavigatorActionService().fillContextMenu(manager);
            }
        });

        Menu menu = menuMgr.createContextMenu(getCommonViewer().getTree());
        getCommonViewer().getTree().setMenu(menu);

        this.addViewerFilter(EMFObjFilter.FILTER_ID);
        this.addViewerFilter(ReportingFilter.FILTER_ID);
        this.addViewerFilter(FolderObjFilter.FILTER_ID);
        adjustFilter();
        activateContext();
        getCommonViewer().setSorter(null);
        getCommonViewer().getTree().addTreeListener(new TreeAdapter() {

            @Override
            public void treeExpanded(TreeEvent e) {
                TreeItem item = (TreeItem) e.item;
                if (!item.getText().endsWith(")")) { //$NON-NLS-1$
                    Object obj = item.getData();

                    if (obj instanceof TableFolderNode || obj instanceof ViewFolderNode || obj instanceof ColumnFolderNode) {
                        item.setText(item.getText() + "(" + item.getItemCount() + ")"); //$NON-NLS-1$ //$NON-NLS-2$
                    }
                }
                super.treeExpanded(e);
            }

        });

        getCommonViewer().getTree().addMouseListener(new MouseAdapter() {

            @Override
            public void mouseDoubleClick(MouseEvent e) {
                boolean superDoubleClick = true;
                Tree tree = (Tree) e.getSource();
                Point point = new Point(e.x, e.y);
                TreeItem item = tree.getItem(point);
                if (item != null) {
                    Object obj = item.getData();

                    if (obj instanceof AbstractFolderNode) {
                        AbstractFolderNode node = (AbstractFolderNode) obj;
                        node.loadChildren();
                        Object[] children = node.getChildren();
                        if (children != null) {
                            if (item.getText().indexOf("(") > 0) { //$NON-NLS-1$
                                item.setText(item.getText().substring(0, item.getText().indexOf("(")) + "(" + children.length //$NON-NLS-1$ //$NON-NLS-2$
                                        + ")"); //$NON-NLS-1$
                            } else {
                                item.setText(item.getText() + "(" + children.length + ")"); //$NON-NLS-1$ //$NON-NLS-2$
                            }

                        }

                    }

                    // all object will be RepositoryNode, so these code is unused any more
                    // if (obj instanceof Analysis) {
                    //
                    // Analysis analysis = (Analysis) obj;
                    // List<RenderedObject> tempList = new ArrayList<RenderedObject>();
                    // tempList.add(analysis);
                    //
                    // IFolder analysesFolder = ResourceManager.getAnalysisFolder();
                    // IFile file = AnaResourceFileHelper.getInstance().findCorrespondingFile(tempList,
                    // analysesFolder).get(0);
                    //
                    // CorePlugin.getDefault().openEditor(file, "org.talend.dataprofiler.core.ui.editor.analysis.AnalysisEditor"); //$NON-NLS-1$
                    // }

                    // ADD hcheng 07-28-2009,8243: open the indicator definition
                    // with a double-click.
                    if (obj instanceof IndicatorDefinition) {
                        IndicatorDefinition indicatorDefinition = (IndicatorDefinition) obj;
                        // reload object
                        indicatorDefinition = DefinitionHandler.getInstance().getIndicatorDefinition(
                                indicatorDefinition.getLabel());
                        IndicatorEditorInput input = new IndicatorEditorInput(indicatorDefinition);
                        try {
                            CorePlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage()
                                    .openEditor(input, IndicatorEditor.class.getName());
                        } catch (PartInitException e1) {
                            log.error(e1, e1);
                        }
                    }

                    if (obj instanceof IRepositoryViewObject) {
                        OpenItemEditorAction openItemEditorAction = new OpenItemEditorAction((IRepositoryViewObject) obj);
                        openItemEditorAction.run();
                    }
                    if (obj instanceof RepositoryNode) {
                        if (obj instanceof ReportFileRepNode) {
                            superDoubleClick = false;
                            ReportFileRepNode reportFileNode = (ReportFileRepNode) obj;
                            IPath location = Path.fromOSString(reportFileNode.getResource().getRawLocation().toOSString());
                            IFile latestRepIFile = ResourceManager.getRootProject().getFile(location.lastSegment());
                            try {
                                latestRepIFile.createLink(location, IResource.REPLACE, null);
                                IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
                                page.openEditor(new FileEditorInput(latestRepIFile), IEditorRegistry.SYSTEM_EXTERNAL_EDITOR_ID);
                            } catch (Throwable e1) {
                                log.error(e1, e1);
                            }
                        } else {
                            RepositoryNode repoNode = (RepositoryNode) obj;
                            if (RepositoryNodeHelper.canOpenEditor(repoNode)) {
                                OpenItemEditorAction openItemEditorAction = new OpenItemEditorAction(repoNode.getObject());
                                openItemEditorAction.run();
                            }
                            if (repoNode instanceof AnalysisRepNode || repoNode instanceof ReportRepNode
                                    || repoNode instanceof SysIndicatorDefinitionRepNode || repoNode instanceof PatternRepNode
                                    || repoNode instanceof RuleRepNode) {
                                superDoubleClick = false;
                            }
                        }

                    }
                }
                if (superDoubleClick) {
                    super.mouseDoubleClick(e);
                }
            }
        });
        getCommonViewer().getTree().addKeyListener(new KeyListener() {

            public void keyPressed(KeyEvent e) {
                if (e.keyCode == SWT.CR) {
                    Object source = e.getSource();
                    Tree tree = (Tree) e.getSource();
                    TreeItem[] selection = tree.getSelection();
                    for (TreeItem item : selection) {
                        Object data = item.getData();
                        RepositoryNode repoNode = (RepositoryNode) data;
                        if (RepositoryNodeHelper.canOpenEditor(repoNode)) {
                            OpenItemEditorAction openItemEditorAction = new OpenItemEditorAction(repoNode.getObject());
                            openItemEditorAction.run();
                        }

                    }
                }
            }

            public void keyReleased(KeyEvent e) {

            }
        });
        // ~ADD mzhao for feature 6233 Load columns when selecting a table (or
        // view) in DQ Repository view
        getCommonViewer().addSelectionChangedListener(new ISelectionChangedListener() {

            public void selectionChanged(SelectionChangedEvent event) {
                TreeSelection selection = (TreeSelection) event.getSelection();
                if (selection.size() != 1) {
                    return;
                }
                Object selectedElement = selection.getFirstElement();
                if (selectedElement instanceof TdTable || selectedElement instanceof TdView) {
                    if (contentProvider == null) {
                        contentProvider = (ITreeContentProvider) getCommonViewer().getContentProvider();
                    }
                    for (Object child : contentProvider.getChildren(selectedElement)) {
                        if (child instanceof IFolderNode
                                && ((IFolderNode) child).getFolderNodeType() == ColumnFolderNode.COLUMNFOLDER_NODE_TYPE) {
                            ((IFolderNode) child).loadChildren();
                            break;
                        }
                    }
                }
            }

        });
        // ~
    }

    /**
     * Activate a context that this view uses. It will be tied to this view activation events and will be removed when
     * the view is disposed.
     */
    private void activateContext() {
        IContextService contextService = (IContextService) getSite().getService(IContextService.class);
        contextService.activateContext(VIEW_CONTEXT_ID);

        // DQDeleteAction dqDeleteAction = new DQDeleteAction();
        // IHandlerService service = (IHandlerService) getViewSite().getService(IHandlerService.class);
        // service.activateHandler(dqDeleteAction.getActionDefinitionId(), new ActionHandler(dqDeleteAction));
    }

    private void adjustFilter() {
        List<IService> filterList = GlobalServiceRegister.getDefault().getServiceGroup(IViewerFilterService.class);
        for (IService service : filterList) {
            if (service instanceof IViewerFilterService) {
                IViewerFilterService filterService = (IViewerFilterService) service;
                if (filterService.isAddOrDel()) {
                    this.addViewerFilter(filterService.getViwerFilterId());
                } else {
                    this.removeViewerFilter(filterService.getViwerFilterId());
                }
            }
        }
    }

    public void addViewerFilter(int viewerFilterId) {
        if (filterMap.containsKey(String.valueOf(viewerFilterId))) {
            return;
        }
        AbstractViewerFilter filter = null;
        switch (viewerFilterId) {
        case EMFObjFilter.FILTER_ID:
            filter = new EMFObjFilter();
            filterMap.put(String.valueOf(EMFObjFilter.FILTER_ID), filter);
            break;
        case ReportingFilter.FILTER_ID:
            filter = new ReportingFilter();
            filterMap.put(String.valueOf(ReportingFilter.FILTER_ID), filter);
            break;
        case FolderObjFilter.FILTER_ID:
            filter = new FolderObjFilter();
            filterMap.put(String.valueOf(FolderObjFilter.FILTER_ID), filter);
            break;
        default:
            filter = new ReportingFilter();
            filterMap.put(String.valueOf(ReportingFilter.FILTER_ID), filter);
        }
        getCommonViewer().addFilter(filter);
    }

    public void removeViewerFilter(int viewerFilterId) {
        String filterKey = String.valueOf(viewerFilterId);
        if (filterMap.containsKey(filterKey)) {
            this.getCommonViewer().removeFilter(filterMap.get(filterKey));
            this.filterMap.remove(filterKey);
        }
    }

    /**
     * DOC Zqin Comment method "showSelectedElements".
     * 
     * MOD 2009-01-07 mzhao for feature:0005664
     * 
     * @param newTree
     */
    public void showSelectedElements(Object selectedElement) {
        try {
            // MOD by zshen for bug 12940 refresh the viewer to collapse all the element.
            StructuredSelection structSel = new StructuredSelection(selectedElement);
            getCommonViewer().setSelection(structSel);
            // If not select,unfold tree structure to this column.
            StructuredSelection selectionTarge = (StructuredSelection) getCommonViewer().getSelection();
            if (!selectionTarge.equals(structSel)) {
                getCommonViewer().refresh();
                recursiveExpandTree(selectedElement);
                getCommonViewer().setSelection(structSel);
            }

        } catch (Exception e) {
            log.error(e, e);
        }
    }

    /**
     * 
     * DOC mzhao Comment method "recursiveExpandTree".
     * 
     * @param commonViewer
     * @param provider
     * @param item
     */
    private void recursiveExpandTree(Object item) {
        if (contentProvider == null) {
            contentProvider = (ITreeContentProvider) getCommonViewer().getContentProvider();
        }
        // MOD xqliu 2011-01-14 bug 15750: show in DQ Repository View
        if (item instanceof RepositoryNode) {
            RepositoryNode node = ((RepositoryNode) item).getParent();
            if (node == null) {
                return;
            }
            recursiveExpandTree(node);
            getCommonViewer().expandToLevel(node, 1);
        }
    }

    /**
     * DOC bZhou DQRespositoryView class global comment. Detailled comment
     */
    class RefreshDQReponsitoryViewAction extends RefreshAction {

        public RefreshDQReponsitoryViewAction() {
            super(PlatformUI.getWorkbench().getActiveWorkbenchWindow());
            setImageDescriptor(ImageLib.getImageDescriptor(ImageLib.REFRESH_SPACE));
        }

        @Override
        public void run() {
            ProxyRepositoryManager.getInstance().refresh();
            // MOD qiongli 2010-12-7 bug 16843.
            // MOD qiongli 2011-1-20. shield for resusing TOS delete mechanism.
            // LogicalDeleteFileHandle.setManualRefresh(true);
            // LogicalDeleteFileHandle.setFinishScanAllFolders(false);
            getCommonViewer().refresh();
            super.run();
        }
    }

    public void refresh() {
        RefreshDQReponsitoryViewAction refresh = new RefreshDQReponsitoryViewAction();
        refresh.run();
    }

    /*
     * (non-Jsdoc)
     * 
     * @see org.eclipse.ui.navigator.CommonNavigator#handleDoubleClick(org.eclipse.jface.viewers.DoubleClickEvent)
     */
    @Override
    protected void handleDoubleClick(DoubleClickEvent anEvent) {
        IStructuredSelection selection = (IStructuredSelection) anEvent.getSelection();
        Object element = selection.getFirstElement();
        RepositoryNode repoNode = (RepositoryNode) element;
        if (!(repoNode instanceof AnalysisRepNode || repoNode instanceof ReportRepNode
                || repoNode instanceof SysIndicatorDefinitionRepNode || repoNode instanceof PatternRepNode
                || repoNode instanceof RuleRepNode || repoNode instanceof DBConnectionRepNode
                || repoNode instanceof DFConnectionRepNode || repoNode instanceof MDMConnectionRepNode)) {
            super.handleDoubleClick(anEvent);
        }
    }

    /**
     * 
     * DOC qiongli Comment method "initAllFolder".
     */
    private void initAllFolders() {
        Project newProject = ProjectManager.getInstance().getCurrentProject();
        List<FolderItem> folderItems = ProjectManager.getInstance().getFolders(newProject.getEmfProject());
        try {
            for (FolderItem folder : new ArrayList<FolderItem>(folderItems)) {
                if (WorkbenchUtils.isTDQOrMetadataRootFolder(folder)) {
                    ERepositoryObjectType type = WorkbenchUtils.getFolderContentType(folder);
                    ProxyRepositoryFactory.getInstance().getAll(type, true);
                }
            }
        } catch (PersistenceException e) {
            log.error(e, e);
        }
    }

}
