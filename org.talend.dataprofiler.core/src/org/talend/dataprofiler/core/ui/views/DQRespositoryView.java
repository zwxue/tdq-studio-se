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
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.commands.ActionHandler;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.TreeAdapter;
import org.eclipse.swt.events.TreeEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchListener;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionContext;
import org.eclipse.ui.actions.RefreshAction;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.navigator.CommonNavigator;
import org.talend.core.model.metadata.builder.connection.Connection;
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
import org.talend.dataprofiler.core.ui.action.actions.DeleteObjectsAction;
import org.talend.dataprofiler.core.ui.editor.indicator.IndicatorEditor;
import org.talend.dataprofiler.core.ui.editor.indicator.IndicatorEditorInput;
import org.talend.dataprofiler.core.ui.filters.AbstractViewerFilter;
import org.talend.dataprofiler.core.ui.filters.EMFObjFilter;
import org.talend.dataprofiler.core.ui.filters.FolderObjFilter;
import org.talend.dataprofiler.core.ui.filters.ReportingFilter;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.indicators.definition.IndicatorDefinition;
import org.talend.dq.CWMPlugin;
import org.talend.dq.helper.resourcehelper.AnaResourceFileHelper;
import org.talend.dq.helper.resourcehelper.PrvResourceFileHelper;
import org.talend.dq.indicators.definitions.DefinitionHandler;
import org.talend.dq.nodes.foldernode.AbstractFolderNode;
import org.talend.dq.nodes.foldernode.IFolderNode;
import org.talend.resource.ResourceManager;
import org.talend.resource.ResourceService;
import org.talend.top.repository.ProxyRepositoryManager;
import orgomg.cwm.analysis.informationvisualization.RenderedObject;
import orgomg.cwm.foundation.softwaredeployment.DataProvider;

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

        DQStructureManager manager = DQStructureManager.getInstance();

        if (manager.isNeedCreateStructure()) {
            manager.createDQStructure();
        }

        if (manager.isNeedMigration()) {
            MigrationTaskManager.doMigrationTask(MigrationTaskManager.findWorksapceTasks());
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

        addPostWindowCloseListener();

        addResourceChangedListener();

        initToolBar();

        initWorkspace();
    }

    /**
     * DOC bZhou Comment method "initWorkspace".
     */
    private void initWorkspace() {

        // initialized resource persistence property.
        ResourceService.initResourcePersistence();

        // initialized drivers in sql explorer.
        SQLExplorerPlugin.getDefault().initAllDrivers();

        // initialized connections in sql explorer.
        List<Connection> providers = PrvResourceFileHelper.getInstance().getAllDataProviders();
        for (DataProvider provider : providers) {
            CWMPlugin.getDefault().addConnetionAliasToSQLPlugin(provider);
        }
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

                    if (obj instanceof Analysis) {
                        Analysis analysis = (Analysis) obj;
                        List<RenderedObject> tempList = new ArrayList<RenderedObject>();
                        tempList.add(analysis);

                        IFolder analysesFolder = ResourceManager.getAnalysisFolder();
                        IFile file = AnaResourceFileHelper.getInstance().findCorrespondingFile(tempList, analysesFolder).get(0);

                        CorePlugin.getDefault()
                                .openEditor(file, "org.talend.dataprofiler.core.ui.editor.analysis.AnalysisEditor"); //$NON-NLS-1$
                    }

                    // ADD hcheng 07-28-2009,8243: open the indicator definition
                    // with a double-click.
                    if (obj instanceof IndicatorDefinition) {
                        IndicatorDefinition indicatorDefinition = (IndicatorDefinition) obj;
                        // reload object
                        indicatorDefinition = DefinitionHandler.getInstance().getIndicatorDefinition(
                                indicatorDefinition.getLabel());
                        IndicatorEditorInput input = new IndicatorEditorInput(indicatorDefinition);
                        try {
                            CorePlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(input,
                                    IndicatorEditor.class.getName());
                        } catch (PartInitException e1) {
                            log.error(e1, e1);
                        }
                    }
                }
                super.mouseDoubleClick(e);
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

        DeleteObjectsAction deleteObjectsAction = new DeleteObjectsAction(false);
        IHandlerService service = (IHandlerService) getViewSite().getService(IHandlerService.class);
        service.activateHandler(deleteObjectsAction.getActionDefinitionId(), new ActionHandler(deleteObjectsAction));
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
            getCommonViewer().refresh();
            StructuredSelection structSel = new StructuredSelection(selectedElement);
            getCommonViewer().setSelection(structSel);
            // If not select,unfold tree structure to this column.
            StructuredSelection selectionTarge = (StructuredSelection) getCommonViewer().getSelection();
            if (!selectionTarge.equals(structSel)) {
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
        if (item instanceof EObject) {
            Object parent = contentProvider.getParent(item);
            Object[] tbFolderNodes = contentProvider.getChildren(parent);
            boolean isFind = false;
            IFolderNode fn = null;
            for (Object folderNode : tbFolderNodes) {
                if (!(folderNode instanceof IFolderNode)) {
                    continue;
                }
                fn = (IFolderNode) folderNode;
                Object[] folderChilds = contentProvider.getChildren(fn);
                for (Object child : folderChilds) {
                    if (child == item) {
                        isFind = true;
                        break;
                    }
                }
                if (isFind) {
                    break;
                }
            }
            // If EMF node,get folder parent.
            if (fn != null) {
                recursiveExpandTree(fn);
                getCommonViewer().expandToLevel(fn, 1);
            } else {
                Object emfParent = contentProvider.getParent(item);
                // EMF XMI resources
                if (emfParent instanceof Resource) {
                    Resource cwmResource = (Resource) emfParent;
                    IFile resourceFile = null;
                    URI uri = cwmResource.getURI();
                    uri = cwmResource.getResourceSet().getURIConverter().normalize(uri);
                    String scheme = uri.scheme();
                    if ("platform".equals(scheme) && uri.segmentCount() > 1 && "resource".equals(uri.segment(0))) { //$NON-NLS-1$ //$NON-NLS-2$
                        StringBuffer platformResourcePath = new StringBuffer();
                        for (int j = 1, size = uri.segmentCount(); j < size; ++j) {
                            platformResourcePath.append('/');
                            platformResourcePath.append(uri.segment(j));
                        }
                        resourceFile = ResourcesPlugin.getWorkspace().getRoot()
                                .getFile(new Path(platformResourcePath.toString()));
                    }
                    emfParent = resourceFile;
                }

                recursiveExpandTree(emfParent);
                getCommonViewer().expandToLevel(emfParent, 1);
            }
        } else if (item instanceof IFolderNode) {
            // User provider get IFolderNode parent will be null, here must call
            // IFolderNode.getParent.
            IFolderNode folderNode = (IFolderNode) item;
            Object eo = folderNode.getParent();
            recursiveExpandTree(eo);
            getCommonViewer().expandToLevel(eo, 1);
        } else {
            // Workspace resources
            Object workspaceParent = contentProvider.getParent(item);
            if (workspaceParent == null) {
                return;
            }
            getCommonViewer().expandToLevel(workspaceParent, 1);
            recursiveExpandTree(workspaceParent);
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
            getCommonViewer().refresh();
            super.run();
        }
    }

}
