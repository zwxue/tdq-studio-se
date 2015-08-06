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
package org.talend.dataprofiler.core.ui.views;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sourceforge.sqlexplorer.plugin.SQLExplorerPlugin;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.PreferenceContentProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TreeAdapter;
import org.eclipse.swt.events.TreeEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
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
import org.eclipse.ui.model.IContributionService;
import org.eclipse.ui.navigator.CommonNavigator;
import org.eclipse.ui.progress.UIJob;
import org.talend.commons.exception.PersistenceException;
import org.talend.commons.ui.swt.dialogs.ProgressDialog;
import org.talend.core.model.general.Project;
import org.talend.core.model.metadata.builder.database.JavaSqlFactory;
import org.talend.core.model.metadata.builder.database.PluginConstant;
import org.talend.core.model.properties.DatabaseConnectionItem;
import org.talend.core.model.properties.FolderItem;
import org.talend.core.model.properties.Item;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.manager.DQStructureManager;
import org.talend.dataprofiler.core.migration.helper.WorkspaceVersionHelper;
import org.talend.dataprofiler.core.model.nodes.foldernode.ColumnFolderNode;
import org.talend.dataprofiler.core.model.nodes.foldernode.TableFolderNode;
import org.talend.dataprofiler.core.model.nodes.foldernode.ViewFolderNode;
import org.talend.dataprofiler.core.service.GlobalServiceRegister;
import org.talend.dataprofiler.core.service.IService;
import org.talend.dataprofiler.core.service.IViewerFilterService;
import org.talend.dataprofiler.core.ui.ResoureceChangedListener;
import org.talend.dataprofiler.core.ui.action.actions.EditDFTableAction;
import org.talend.dataprofiler.core.ui.action.actions.EditFileDelimitedAction;
import org.talend.dataprofiler.core.ui.action.actions.OpenItemEditorAction;
import org.talend.dataprofiler.core.ui.editor.indicator.IndicatorEditor;
import org.talend.dataprofiler.core.ui.editor.indicator.IndicatorEditorInput;
import org.talend.dataprofiler.core.ui.filters.AbstractViewerFilter;
import org.talend.dataprofiler.core.ui.filters.EMFObjFilter;
import org.talend.dataprofiler.core.ui.filters.FolderObjFilter;
import org.talend.dataprofiler.core.ui.filters.ReportingFilter;
import org.talend.dataprofiler.core.ui.progress.ProgressUI;
import org.talend.dataprofiler.core.ui.utils.WorkbenchUtils;
import org.talend.dataprofiler.core.ui.views.layout.BorderLayout;
import org.talend.dataprofiler.core.ui.views.provider.DQRepositoryViewLabelProvider;
import org.talend.dataprofiler.migration.manager.MigrationTaskManager;
import org.talend.dataquality.indicators.definition.IndicatorDefinition;
import org.talend.dq.CWMPlugin;
import org.talend.dq.helper.ProxyRepositoryManager;
import org.talend.dq.helper.RepositoryNodeHelper;
import org.talend.dq.indicators.definitions.DefinitionHandler;
import org.talend.dq.nodes.AnalysisRepNode;
import org.talend.dq.nodes.DBTableRepNode;
import org.talend.dq.nodes.DBViewRepNode;
import org.talend.dq.nodes.DFConnectionRepNode;
import org.talend.dq.nodes.DFTableRepNode;
import org.talend.dq.nodes.DQRepositoryNode;
import org.talend.dq.nodes.PatternRepNode;
import org.talend.dq.nodes.ReportFileRepNode;
import org.talend.dq.nodes.ReportRepNode;
import org.talend.dq.nodes.RuleRepNode;
import org.talend.dq.nodes.SysIndicatorDefinitionRepNode;
import org.talend.dq.nodes.foldernode.AbstractFolderNode;
import org.talend.repository.ProjectManager;
import org.talend.repository.RepositoryWorkUnit;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.RepositoryNode;
import org.talend.resource.ResourceManager;
import org.talend.resource.ResourceService;
import org.talend.utils.ProductVersion;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * @author rli
 */
public class DQRespositoryView extends CommonNavigator {

    protected static Logger log = Logger.getLogger(DQRespositoryView.class);

    public static final String ID = "org.talend.dataprofiler.core.ui.views.DQRespositoryView"; //$NON-NLS-1$

    private Map<String, AbstractViewerFilter> filterMap = new HashMap<String, AbstractViewerFilter>();

    private static final String VIEW_CONTEXT_ID = "org.talend.dataprofiler.core.ui.views.DQRespositoryView.viewScope"; //$NON-NLS-1$

    private ITreeContentProvider contentProvider = null;

    DQStructureManager manager;

    private static boolean upDownStatus = false;// true : down ; fasle : up

    private static boolean isOnUpDownStatus = false;

    public DQRespositoryView() {
        super();

        manager = DQStructureManager.getInstance();

        if (manager.isNeedCreateStructure()) {
            ProxyRepositoryFactory.getInstance().executeRepositoryWorkUnit(
                    new RepositoryWorkUnit<Object>("Create DQ Repository structure") { //$NON-NLS-1$

                        @Override
                        protected void run() {
                            manager.createDQStructure();
                        }
                    });
        }

        if (manager.isNeedMigration()) {
            // MOD qiongli 2010-9-7,bug 14698,add 'try...catch'
            try {
                IRunnableWithProgress op = new IRunnableWithProgress() {

                    public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                        ProductVersion wVersion = WorkspaceVersionHelper.getVesion();
                        new MigrationTaskManager(wVersion).doMigrationTask(monitor);
                    }

                };

                ProgressUI.popProgressDialog(op);
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
            if (!SQLExplorerPlugin.getDefault().isInitedAllConnToSQLExpl()) {
                for (IRepositoryViewObject viewObject : ProxyRepositoryFactory.getInstance().getAll(
                        ERepositoryObjectType.METADATA_CONNECTIONS, true)) {
                    if (viewObject == null || viewObject.getProperty() == null) {
                        continue;
                    }
                    Item item = viewObject.getProperty().getItem();
                    if (item != null && (item instanceof DatabaseConnectionItem)) {
                        String username = JavaSqlFactory.getUsername(((DatabaseConnectionItem) item).getConnection());
                        if (username != null && !"".equals(username.trim())) { //$NON-NLS-1$
                            CWMPlugin.getDefault().addConnetionAliasToSQLPlugin(((DatabaseConnectionItem) item).getConnection());
                        }
                    }
                }
                SQLExplorerPlugin.getDefault().setInitedAllConnToSQLExpl(true);
            }
        } catch (PersistenceException e) {
            log.error(e, e);
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

        // toolBarManager.add((IAction) new FilterDQReponsitoryTreeAction());
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
                // MOD zshen it is duplicate with initWorkspace()
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

        GridLayout gl = new GridLayout();
        gl.horizontalSpacing = 0;
        gl.verticalSpacing = 0;
        gl.marginWidth = 0;
        gl.marginHeight = 0;
        parent.setLayout(gl);

        Composite topComp = new Composite(parent, SWT.NONE);
        topComp.setFont(parent.getFont());
        Composite bottomComp = new Composite(parent, SWT.NONE);
        bottomComp.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
        bottomComp.setFont(parent.getFont());

        GridData gridDataTop = new GridData();
        gridDataTop.horizontalAlignment = GridData.FILL;
        gridDataTop.verticalAlignment = GridData.FILL;
        gridDataTop.grabExcessHorizontalSpace = true;
        topComp.setLayoutData(gridDataTop);

        GridData gridDataBottom = new GridData();
        gridDataBottom.horizontalAlignment = GridData.FILL;
        gridDataBottom.verticalAlignment = GridData.FILL;
        gridDataBottom.grabExcessVerticalSpace = true;
        gridDataBottom.grabExcessHorizontalSpace = true;
        bottomComp.setLayoutData(gridDataBottom);

        FillLayout topLayout = new FillLayout(SWT.FILL_WINDING);
        topComp.setLayout(topLayout);

        FillLayout bottomLayout = new FillLayout(SWT.FILL_WINDING);
        bottomComp.setLayout(bottomLayout);

        createTreeFilter(topComp);
        super.createPartControl(bottomComp);

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
                    if (obj instanceof DQRepositoryNode) {
                        if (obj instanceof ReportFileRepNode) {
                            new OpenItemEditorAction((IRepositoryNode) obj).run();
                        } else if (obj instanceof DFConnectionRepNode) { // MOD gdbu 2011-4-1 bug 20051
                            new EditFileDelimitedAction((IRepositoryNode) obj).run();
                        } else if (obj instanceof DFTableRepNode) {// MOD qiongli 2011-10-21 bug TDQ-3797
                            new EditDFTableAction((IRepositoryNode) obj).run();
                        } else {
                            DQRepositoryNode repoNode = (DQRepositoryNode) obj;
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
                if (e.keyCode == SWT.CR || e.keyCode == SWT.KEYPAD_CR) {
                    Object source = e.getSource();
                    Tree tree = (Tree) e.getSource();
                    TreeItem[] selection = tree.getSelection();
                    for (TreeItem item : selection) {
                        Object data = item.getData();
                        DQRepositoryNode repoNode = (DQRepositoryNode) data;
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
                // if (selection.size() != 1) {
                // return;
                // }
                // ADD xwang 2011-08-30
                // MOD sizhaoliu 2012-09-10 TDQ-5612 remove the following part to reduce operation latency with remote
                // connection.
                // if (PluginChecker.isSVNProviderPluginLoaded()) {
                // RepositoryWorkUnit<Object> workUnit = new RepositoryWorkUnit<Object>("Open editor") {
                //
                // @Override
                // protected void run() {
                //
                // }
                // };
                // workUnit.setAvoidUnloadResources(true);
                // ProxyRepositoryFactory.getInstance().executeRepositoryWorkUnit(workUnit);
                // }
                // MOD klliu 2011-12-19 TDQ-4197
                Iterator<?> iterator = selection.iterator();
                while (iterator.hasNext()) {
                    Object selectedElement = iterator.next();
                    if (selectedElement instanceof DBTableRepNode || selectedElement instanceof DBViewRepNode) {
                        ((RepositoryNode) selectedElement).getChildren().get(0).getChildren();
                    }
                }
                // Object selectedElement = selection.getFirstElement();
                // if (selectedElement instanceof TdTable || selectedElement instanceof TdView) {
                // if (contentProvider == null) {
                // contentProvider = (ITreeContentProvider) getCommonViewer().getContentProvider();
                // }
                // for (Object child : contentProvider.getChildren(selectedElement)) {
                // if (child instanceof IFolderNode
                // && ((IFolderNode) child).getFolderNodeType() == ColumnFolderNode.COLUMNFOLDER_NODE_TYPE) {
                // ((IFolderNode) child).loadChildren();
                // break;
                // }
                // }
                // }
            }

        });

        // MOD mzhao 2011-03-10 bug 19147: filter MDM entries.
        getCommonViewer().addFilter(new ViewerFilter() {

            @Override
            public boolean select(Viewer viewer, Object parentElement, Object element) {
                if (element instanceof IProject) {
                    return false;
                }
                return true;
            }

        });
        // ~

    }

    /**
     * DOC gdbu Comment method "createTreeFilter".
     * 
     * @param parent
     * @param _catalogToolBarMgr
     */
    protected void createTreeFilter(final Composite parent) {

        parent.setLayout(new BorderLayout());

        Composite centerComp = new Composite(parent, SWT.NONE);
        // centerComp.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
        centerComp.setFont(parent.getFont());
        Composite eastComp = new Composite(parent, SWT.NONE);
        // eastComp.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
        eastComp.setFont(parent.getFont());

        centerComp.setLayoutData(BorderLayout.CENTER);
        eastComp.setLayoutData(BorderLayout.EAST);

        FillLayout centerLayout = new FillLayout(SWT.FILL_WINDING);
        centerLayout.marginHeight = 1;
        // centerLayout.marginWidth = 2;
        centerComp.setLayout(centerLayout);

        FillLayout leftLayout = new FillLayout(SWT.HORIZONTAL);
        leftLayout.marginWidth = 1;
        leftLayout.marginHeight = 2;
        // leftLayout.spacing = 1;
        eastComp.setLayout(leftLayout);

        final Text filterText = new Text(centerComp, SWT.BORDER);
        filterText.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));

        final CoolBar coolBar = new CoolBar(eastComp, SWT.FLAT);
        CoolItem coolItem1 = new CoolItem(coolBar, SWT.FLAT);
        ToolBar toolBar = new ToolBar(coolBar, SWT.FLAT);

        ToolItem runFilterItem = new ToolItem(toolBar, SWT.FLAT);
        runFilterItem.setImage(ImageLib.getImage(ImageLib.FILTER_RUN));
        runFilterItem.setToolTipText(DefaultMessagesImpl.getString("DQRespositoryView.RunFilter"));//$NON-NLS-1$

        ToolItem previousMatchItem = new ToolItem(toolBar, SWT.FLAT);
        previousMatchItem.setImage(ImageLib.getImage(ImageLib.FILTER_UP));
        previousMatchItem.setToolTipText(DefaultMessagesImpl.getString("DQRespositoryView.ShowPreviousMatch"));//$NON-NLS-1$

        ToolItem nextMatchItem = new ToolItem(toolBar, SWT.FLAT);
        nextMatchItem.setImage(ImageLib.getImage(ImageLib.FILTER_DOWN));
        nextMatchItem.setToolTipText(DefaultMessagesImpl.getString("DQRespositoryView.ShowNextMatch"));//$NON-NLS-1$

        ToolItem closeFilterItem = new ToolItem(toolBar, SWT.FLAT);
        closeFilterItem.setImage(ImageLib.getImage(ImageLib.FILTER_CLOSE));
        closeFilterItem.setToolTipText(DefaultMessagesImpl.getString("DQRespositoryView.CloseFilter"));//$NON-NLS-1$

        // MOD gdbu 2011-8-17 bug TDQ-3276 : set the filter buttons composite height
        ((GridData) parent.getLayoutData()).heightHint = nextMatchItem.getImage().getImageData().height + 8;

        toolBar.pack();
        Point size = toolBar.getSize();
        coolItem1.setControl(toolBar);
        coolItem1.setSize(coolItem1.computeSize(size.x, size.y));
        coolItem1.setMinimumSize(size);

        coolBar.setLocked(true);
        runFilterItem.addSelectionListener(new SelectionAdapter() {

            /*
             * (non-Javadoc)
             * 
             * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
             */
            @Override
            public void widgetSelected(SelectionEvent e) {
                String filterStr = filterText.getText() + PluginConstant.EMPTY_STRING;
                Shell shell = filterText.getShell();
                runFilter(filterStr, shell);
            }
        });

        previousMatchItem.addSelectionListener(new SelectionAdapter() {

            /*
             * (non-Javadoc)
             * 
             * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
             */
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (DQRepositoryNode.isOnFilterring()) {
                    TreeItem[] selectionNode = getCommonViewer().getTree().getSelection();
                    if (0 == selectionNode.length) {
                        IRepositoryNode filteredNode = RepositoryNodeHelper.getFilteredNode();
                        IRepositoryNode previousFilteredNode = RepositoryNodeHelper.getPreviouFilteredNode(filteredNode);
                        if (null != previousFilteredNode) {
                            RepositoryNodeHelper.setFilteredNode(previousFilteredNode);
                            showSelectedElements(previousFilteredNode);
                        }
                    } else {
                        TreeItem selectionTreeItem = selectionNode[(selectionNode.length - 1)];
                        IRepositoryNode repoNode = (IRepositoryNode) selectionTreeItem.getData();
                        IRepositoryNode previousFilteredNode = RepositoryNodeHelper.getPreviouFilteredNode(repoNode);
                        if (null != previousFilteredNode) {
                            RepositoryNodeHelper.setFilteredNode(previousFilteredNode);
                            // showSelectedElements((RepositoryNode) previousFilteredNode);
                            DQRepositoryNode.setOnDisplayNextOrPreviousNode(true);
                            StructuredSelection structSel = new StructuredSelection(previousFilteredNode);
                            getCommonViewer().setSelection(structSel);
                            DQRepositoryNode.setOnDisplayNextOrPreviousNode(false);
                        }
                    }
                }
            }
        });

        nextMatchItem.addSelectionListener(new SelectionAdapter() {

            /*
             * (non-Javadoc)
             * 
             * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
             */
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (DQRepositoryNode.isOnFilterring()) {
                    TreeItem[] selectionNode = getCommonViewer().getTree().getSelection();
                    if (0 == selectionNode.length) {
                        IRepositoryNode filteredNode = RepositoryNodeHelper.getFilteredNode();
                        IRepositoryNode nextFilteredNode = RepositoryNodeHelper.getNextFilteredNode(filteredNode);
                        if (null != nextFilteredNode) {
                            RepositoryNodeHelper.setFilteredNode(nextFilteredNode);
                            showSelectedElements(nextFilteredNode);
                        }
                    } else {
                        TreeItem selectionTreeItem = selectionNode[(selectionNode.length - 1)];
                        IRepositoryNode repoNode = (IRepositoryNode) selectionTreeItem.getData();
                        IRepositoryNode nextFilteredNode = RepositoryNodeHelper.getNextFilteredNode(repoNode);
                        if (null != nextFilteredNode) {
                            RepositoryNodeHelper.setFilteredNode(nextFilteredNode);
                            // showSelectedElements((RepositoryNode) nextFilteredNode);
                            DQRepositoryNode.setOnDisplayNextOrPreviousNode(true);
                            getCommonViewer().expandToLevel(nextFilteredNode, 1);
                            StructuredSelection structSel = new StructuredSelection(nextFilteredNode);
                            getCommonViewer().setSelection(structSel);
                            DQRepositoryNode.setOnDisplayNextOrPreviousNode(false);
                        }
                    }
                }
            }
        });

        closeFilterItem.addSelectionListener(new SelectionAdapter() {

            /*
             * (non-Javadoc)
             * 
             * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
             */
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (DQRepositoryNode.isOnFilterring()) {
                    closeFilterStatus(filterText);
                }
            }
        });

        filterText.addKeyListener(new KeyAdapter() {

            /*
             * (non-Javadoc)
             * 
             * @see org.eclipse.swt.events.KeyAdapter#keyPressed(org.eclipse.swt.events.KeyEvent)
             */
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.keyCode == SWT.CR || e.keyCode == SWT.KEYPAD_CR) {
                    String filterStr = filterText.getText() + PluginConstant.EMPTY_STRING;
                    Shell shell = filterText.getShell();
                    runFilter(filterStr, shell);
                }
            }

            /*
             * (non-Javadoc)
             * 
             * @see org.eclipse.swt.events.KeyAdapter#keyReleased(org.eclipse.swt.events.KeyEvent)
             */
            @Override
            public void keyReleased(KeyEvent e) {
                if (DQRepositoryNode.isOnFilterring()) {
                    if (e.keyCode == SWT.BS || e.keyCode == SWT.DEL) {
                        if (isFilterTextEmpty(filterText)) {
                            new UIJob(PluginConstant.EMPTY_STRING) {

                                @Override
                                public IStatus runInUIThread(IProgressMonitor monitor) {
                                    if (isFilterTextEmpty(filterText)) {
                                        closeFilterStatus(filterText);
                                    }
                                    return Status.OK_STATUS;
                                }
                            }.schedule(2000);
                        }
                    }
                }
            }
        });
    }

    private void closeFilterStatus(Text filterText) {
        RepositoryNodeHelper.setFilteredNode(null);
        DQRepositoryNode.setFilterStr(PluginConstant.EMPTY_STRING);
        DQRepositoryNode.setFiltering(false);
        filterText.setText(PluginConstant.EMPTY_STRING);
        getCommonViewer().collapseAll();
        refresh();
    }

    private boolean isFilterTextEmpty(Text filterText) {
        String filterString = filterText.getText().trim() + PluginConstant.EMPTY_STRING;
        if (filterString.equals(PluginConstant.EMPTY_STRING)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 
     * DOC gdbu DQRespositoryView class global comment. Detailled comment
     */
    class RunFilterThread extends Thread {

        private Shell shell = null;

        private String filterStr = null;

        public RunFilterThread(String filterStr, Shell shell) {
            this.shell = shell;
            this.filterStr = filterStr;
        }

        @Override
        public void run() {

            Display.getDefault().asyncExec(new Runnable() {

                public void run() {
                    ProgressDialog progressDialog = new ProgressDialog(shell) {

                        @Override
                        public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

                            monitor.beginTask("Filtering...", 11); //$NON-NLS-1$

                            getCommonViewer().collapseAll();
                            monitor.worked(1);
                            try {
                                DQRepositoryNode.setFilterStr(filterStr);
                                if (filterStr.trim().equals(PluginConstant.EMPTY_STRING)) {
                                    DQRepositoryNode.setFiltering(false);

                                } else {
                                    DQRepositoryNode.setFiltering(true);
                                    RepositoryNodeHelper.fillTreeList(monitor);
                                }
                            } catch (Exception exception) {
                                exception.printStackTrace();
                            } finally {
                                final IRepositoryNode firstFilteredNode = RepositoryNodeHelper.getFirstFilteredNode();
                                if (null != firstFilteredNode) {
                                    RepositoryNodeHelper.setFilteredNode(firstFilteredNode);
                                    monitor.worked(1);
                                    showSelectedElements(firstFilteredNode);
                                    monitor.worked(1);
                                }
                                monitor.done();
                            }
                        }
                    };
                    try {
                        progressDialog.executeProcess();
                    } catch (InvocationTargetException e) {
                        // do nothing ???
                    } catch (InterruptedException e) {
                        // do nothing ???
                    }
                }
            });
        }
    }

    private void runFilter(final String filterStr, final Shell shell) {
        RunFilterThread runFilterThread = new RunFilterThread(filterStr, shell);
        runFilterThread.run();
    }

    private void expandNodes(boolean expand) {
        expandTreeItems(getCommonViewer().getTree().getItems(), expand);
        packOtherColumns();
    }

    private void expandTreeItems(TreeItem[] items, boolean expandOrCollapse) {
        for (TreeItem item : items) {
            item.setExpanded(expandOrCollapse);
            TreeItem[] its = item.getItems();
            if (its != null && its.length > 0) {
                expandTreeItems(its, expandOrCollapse);
            }
        }
    }

    private void packOtherColumns() {
        TreeColumn[] columns = getCommonViewer().getTree().getColumns();
        for (TreeColumn column : columns) {
            column.pack();
        }
        getCommonViewer().getTree().pack();
        getCommonViewer().getTree().getParent().layout();
    }

    protected void setContentAndLabelProviders(TreeViewer treeViewer) {
        treeViewer.setLabelProvider(new DQRepositoryViewLabelProvider());
        IContributionService cs = (IContributionService) PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                .getService(IContributionService.class);
        treeViewer.setComparator(cs.getComparatorFor(IContributionService.TYPE_PREFERENCE));
        treeViewer.setContentProvider(new PreferenceContentProvider());
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
                StructuredSelection recursiveExpandTree = recursiveExpandTree(selectedElement);
                if (recursiveExpandTree != null) {
                    structSel = recursiveExpandTree;
                }
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
    private StructuredSelection recursiveExpandTree(Object item) {
        if (contentProvider == null) {
            contentProvider = (ITreeContentProvider) getCommonViewer().getContentProvider();
        }
        // MOD xqliu 2011-01-14 bug 15750: show in DQ Repository View
        if (item instanceof RepositoryNode) {
            // RepositoryNode node = ((RepositoryNode) item).getParent();
            // if (node == null) {
            RepositoryNode recursiveFind = findRealNode(item);
            if (recursiveFind == null) {
                return null;
            }
            RepositoryNode node = recursiveFind.getParent();
            // }
            recursiveExpandTree(node);
            getCommonViewer().expandToLevel(node, 1);
            return new StructuredSelection(recursiveFind);
        }
        return null;
    }

    /**
     * Because the exist for filter on the repositoryView we maybe use a temp node to display so we need to find out
     * real one at here.
     * 
     * @param item
     * @return
     */
    private RepositoryNode findRealNode(Object item) {
        ModelElement modelElementFromRepositoryNode = RepositoryNodeHelper
                .getModelElementFromRepositoryNode((RepositoryNode) item);
        if (modelElementFromRepositoryNode == null) {// for root node case
            return null;
        }
        RepositoryNode recursiveFind = RepositoryNodeHelper.recursiveFind(modelElementFromRepositoryNode);
        if (recursiveFind == null) {
            log.warn(DefaultMessagesImpl.getString("DQRepositoryView.nodeNotExistWarring", //$NON-NLS-1$
                    modelElementFromRepositoryNode.getName()));
            MessageDialog.openWarning(null, DefaultMessagesImpl.getString("DQRepositoryView.nodeNotExistWarring.Title"), //$NON-NLS-1$
                    DefaultMessagesImpl.getString("DQRepositoryView.nodeNotExistWarring", //$NON-NLS-1$
                            modelElementFromRepositoryNode.getName()));
            return null;
        }
        return recursiveFind;
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

    @Override
    protected void handleDoubleClick(DoubleClickEvent anEvent) {
        IStructuredSelection selection = (IStructuredSelection) anEvent.getSelection();
        Object element = selection.getFirstElement();
        RepositoryNode repoNode = (RepositoryNode) element;
        if (repoNode.canExpandForDoubleClick()) {
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
                    // MOD by zshen to avoid NullPointerException.
                    if (type != null) {
                        ProxyRepositoryFactory.getInstance().getAll(type, true);
                    }
                }
            }
        } catch (PersistenceException e) {
            log.error(e, e);
        }
    }

    private static boolean getUpDownStatus() {
        return upDownStatus;
    }

    private static void setUpDownStatus(boolean upDownStatus) {
        DQRespositoryView.upDownStatus = upDownStatus;
    }

    private static boolean isOnUpDownStatus() {
        return isOnUpDownStatus;
    }

    private static void setOnUpDownStatus(boolean isOnUpDownStatus) {
        DQRespositoryView.isOnUpDownStatus = isOnUpDownStatus;
    }

}
