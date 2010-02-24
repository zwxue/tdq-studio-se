// ============================================================================
//
// Copyright (C) 2006-2009 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.editor.composite;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TreeEditor;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TreeAdapter;
import org.eclipse.swt.events.TreeEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.part.FileEditorInput;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.cwm.relational.TdColumn;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.model.ColumnIndicator;
import org.talend.dataprofiler.core.ui.action.actions.predefined.CreateColumnAnalysisAction;
import org.talend.dataprofiler.core.ui.editor.AbstractAnalysisActionHandler;
import org.talend.dataprofiler.core.ui.editor.AbstractMetadataFormPage;
import org.talend.dataprofiler.core.ui.editor.analysis.ColumnSetMasterPage;
import org.talend.dataprofiler.core.ui.editor.preview.IndicatorUnit;
import org.talend.dataprofiler.core.ui.views.ColumnViewerDND;
import org.talend.dataprofiler.core.ui.views.DQRespositoryView;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.domain.pattern.Pattern;
import org.talend.dataquality.helpers.MetadataHelper;
import org.talend.dataquality.indicators.DataminingType;
import org.talend.dataquality.indicators.PatternMatchingIndicator;
import org.talend.dq.helper.resourcehelper.PatternResourceFileHelper;
import org.talend.dq.nodes.indicator.type.IndicatorEnum;
import org.talend.resource.ResourceManager;
import orgomg.cwm.objectmodel.core.ModelElement;
import orgomg.cwm.resource.relational.Column;

/**
 * 
 * @author yyi 2009-12-16
 */
public class AnalysisColumnSetTreeViewer extends AbstractColumnDropTree {

    protected static Logger log = Logger.getLogger(AnalysisColumnSetTreeViewer.class);

    private static final String DATA_PARAM = "DATA_PARAM"; //$NON-NLS-1$

    public static final String INDICATOR_UNIT_KEY = "INDICATOR_UNIT_KEY"; //$NON-NLS-1$

    public static final String COLUMN_INDICATOR_KEY = "COLUMN_INDICATOR_KEY"; //$NON-NLS-1$

    public static final String ITEM_EDITOR_KEY = "ITEM_EDITOR_KEY"; //$NON-NLS-1$

    public static final String VIEWER_KEY = "org.talend.dataprofiler.core.ui.editor.composite.AnalysisColumnSetTreeViewer"; //$NON-NLS-1$

    private static final int WIDTH1_CELL = 75;

    private Composite parentComp;

    private Tree tree;

    private List<Column> columnSetMultiValueList;

    // private final List<String> comboTextList = new ArrayList<String>();

    private ColumnSetMasterPage masterPage;

    private Menu menu;

    private MenuItem editPatternMenuItem;

    public AnalysisColumnSetTreeViewer(Composite parent) {
        parentComp = parent;
        tree = createTree(parent);
        tree.setData(this);
        columnSetMultiValueList = new ArrayList<Column>();
    }

    public AnalysisColumnSetTreeViewer(Composite parent, ColumnSetMasterPage masterPage) {
        this(parent);
        this.masterPage = masterPage;
        // this.setElements(masterPage.getColumnSetMultiValueIndicator().
        // getAnalyzedColumns());
        this.setDirty(false);
    }

    /**
     * @param parent
     */
    private Tree createTree(Composite parent) {
        final Tree newTree = new Tree(parent, SWT.MULTI | SWT.BORDER);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(newTree);

        newTree.setHeaderVisible(true);
        TreeColumn column1 = new TreeColumn(newTree, SWT.CENTER);
        column1.setWidth(190);
        column1.setText(DefaultMessagesImpl.getString("AnalysisColumnTreeViewer.analyzedColumns")); //$NON-NLS-1$

        TreeColumn column2 = new TreeColumn(newTree, SWT.CENTER);
        column2.setWidth(80);
        column2.setText(DefaultMessagesImpl.getString("AnalysisColumnTreeViewer.operation")); //$NON-NLS-1$

        parent.layout();
        createTreeMenu(newTree, false);

        AbstractAnalysisActionHandler actionHandler = new AbstractAnalysisActionHandler(parent) {

            @Override
            protected void handleRemove() {
                removeSelectedElements(newTree);
                // MOD mzhao 2005-05-05 bug 6587.
                // MOD mzhao 2009-06-8, bug 5887.
                updateBindConnection(masterPage, tree);
            }

        };

        parent.setData(AbstractMetadataFormPage.ACTION_HANDLER, actionHandler);

        addSourceDND(newTree);
        addTargetDND(newTree);

        addTreeListener(newTree);
        return newTree;
    }

    /**
     * DOC bZhou Comment method "addTargetDND".
     * 
     * @param newTree
     */
    private void addTargetDND(final Tree newTree) {
        ColumnViewerDND.installDND(newTree);
    }

    /**
     * DOC bZhou Comment method "addSourceDND".
     * 
     * @param newTree
     */
    private void addSourceDND(final Tree newTree) {
        final LocalSelectionTransfer transfer = LocalSelectionTransfer.getTransfer();
        Transfer[] types = new Transfer[] { transfer };
        int operations = DND.DROP_COPY | DND.DROP_MOVE;
        final DragSource source = new DragSource(newTree, operations);
        source.setTransfer(types);
        final TreeItem[] dragSourceItem = new TreeItem[1];
        source.addDragListener(new DragSourceListener() {

            public void dragStart(DragSourceEvent event) {
                TreeItem[] selection = newTree.getSelection();
                dragSourceItem[0] = selection[0];
                if (selection.length > 0) {
                    event.doit = true;
                    transfer.setSelection(new StructuredSelection(selection[0].getData(COLUMN_INDICATOR_KEY)));
                    getColumnSetMultiValueList().remove(selection[0].getData(COLUMN_INDICATOR_KEY));
                } else {
                    event.doit = false;
                }
            };

            public void dragSetData(DragSourceEvent event) {
                event.data = dragSourceItem[0];
            }

            public void dragFinished(DragSourceEvent event) {
                if (event.detail == DND.DROP_MOVE) {
                    removeItemBranch(dragSourceItem[0]);
                    tree.forceFocus();
                }
                dragSourceItem[0] = null;

            }
        });
    }

    /**
     * DOC xzhao Comment method "createTreeMenu".
     * 
     * @param newTree
     * @param containEdit
     */
    private void createTreeMenu(final Tree newTree, boolean containEdit) {
        Menu oldMenu = newTree.getMenu();
        if (oldMenu != null && !oldMenu.isDisposed()) {
            oldMenu.dispose();
        }
        menu = new Menu(newTree);

        MenuItem createColumnAnalysisMenuItem = new MenuItem(menu, SWT.CASCADE);
        createColumnAnalysisMenuItem.setText(DefaultMessagesImpl.getString("CreateColumnAnalysisAction.columnAnalysis")); //$NON-NLS-1$
        createColumnAnalysisMenuItem.setImage(ImageLib.getImage(ImageLib.ACTION_NEW_ANALYSIS));
        createColumnAnalysisMenuItem.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                createColumnAnalysis(tree);
            }
        });

        MenuItem deleteMenuItem = new MenuItem(menu, SWT.CASCADE);
        deleteMenuItem.setText(DefaultMessagesImpl.getString("AnalysisColumnTreeViewer.removeElement")); //$NON-NLS-1$
        deleteMenuItem.setImage(ImageLib.getImage(ImageLib.DELETE_ACTION));
        deleteMenuItem.addSelectionListener(new SelectionAdapter() {

            /*
             * (non-Javadoc)
             * 
             * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse .swt.events.SelectionEvent)
             */
            @Override
            public void widgetSelected(SelectionEvent e) {
                removeSelectedElements(newTree);
                // MOD mzhao 2005-05-05 bug 6587.
                // MOD mzhao 2009-06-8, bug 5887.
                updateBindConnection(masterPage, tree);
            }

        });

        // ADD 2009-01-07 mzhao for feature:0005664
        MenuItem showMenuItem = new MenuItem(menu, SWT.CASCADE);
        showMenuItem.setText(DefaultMessagesImpl.getString("AnalysisColumnTreeViewer.showDQElement")); //$NON-NLS-1$
        showMenuItem.setImage(ImageLib.getImage(ImageLib.EXPLORE_IMAGE));
        showMenuItem.addSelectionListener(new SelectionAdapter() {

            /*
             * (non-Javadoc)
             * 
             * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse .swt.events.SelectionEvent)
             */
            @Override
            public void widgetSelected(SelectionEvent e) {
                TreeItem[] selection = newTree.getSelection();

                if (selection.length == 1) {
                    DQRespositoryView dqview = CorePlugin.getDefault().getRepositoryView();
                    try {
                        TdColumn tdColumn = (TdColumn) selection[0].getData(COLUMN_INDICATOR_KEY);
                        dqview.showSelectedElements(tdColumn);
                    } catch (Exception ex) {
                        log.error(ex, ex);
                    }
                }
            }

        });

        if (containEdit) {
            editPatternMenuItem = new MenuItem(menu, SWT.CASCADE);
            editPatternMenuItem.setText(DefaultMessagesImpl.getString("AnalysisColumnTreeViewer.editPattern")); //$NON-NLS-1$
            editPatternMenuItem.setImage(ImageLib.getImage(ImageLib.PATTERN_REG));
            editPatternMenuItem.addSelectionListener(new SelectionAdapter() {

                /*
                 * (non-Javadoc)
                 * 
                 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org .eclipse.swt.events.SelectionEvent)
                 */
                @Override
                public void widgetSelected(SelectionEvent e) {
                    TreeItem[] selection = tree.getSelection();
                    if (selection.length > 0) {
                        TreeItem treeItem = selection[0];
                        IndicatorUnit indicatorUnit = (IndicatorUnit) treeItem.getData(INDICATOR_UNIT_KEY);
                        PatternMatchingIndicator indicator = (PatternMatchingIndicator) indicatorUnit.getIndicator();
                        Pattern pattern = indicator.getParameters().getDataValidDomain().getPatterns().get(0);
                        // MOD mzhao 2009-03-13 Feature 6066 Move all folders
                        // into one project.
                        IFolder patternFolder = ResourceManager.getPatternFolder();
                        IFolder sqlPatternFolder = ResourceManager.getPatternSQLFolder();
                        IFile file = PatternResourceFileHelper.getInstance().getPatternFile(pattern,
                                new IFolder[] { patternFolder, sqlPatternFolder });
                        IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
                        try {
                            activePage.openEditor(new FileEditorInput(file),
                                    "org.talend.dataprofiler.core.ui.editor.pattern.PatternEditor"); //$NON-NLS-1$
                        } catch (PartInitException e1) {
                            log.error(e1, e1);
                        }
                    }
                }

            });
        }
        newTree.setMenu(menu);
    }

    public void setInput(Object[] objs) {
        if (objs != null && objs.length != 0) {
            if (!(objs[0] instanceof TdColumn)) {
                return;
            }
        }
        List<Column> columnList = new ArrayList<Column>();
        for (Object obj : objs) {
            columnList.add((TdColumn) obj);
        }
        this.setElements(columnList);
    }

    public void setElements(final Object columns) {
        this.tree.dispose();
        this.tree = createTree(this.parentComp);
        tree.setData(this);
        // MOD mzhao bug 8282 2009-7-31 Clear column cache.
        columnSetMultiValueList.clear();
        addItemElements((List<Column>) columns, 0);
        // addItemElements(columns);
        // masterPage.getAnalysis().getContext().setConnection(null);
        // MOD mzhao 2009-05-05 bug 6587.
        updateBindConnection(masterPage, tree);
    }

    private void addItemElements(final List<Column> columns, int index) {
        for (int i = 0; i < columns.size(); i++) {
            final TdColumn column = (TdColumn) columns.get(i);
            final TreeItem treeItem = new TreeItem(tree, SWT.NONE, index);

            MetadataHelper.setDataminingType(DataminingType.NOMINAL, column);
            columnSetMultiValueList.add(index, column);

            String columnName = column.getName();
            treeItem.setImage(ImageLib.getImage(ImageLib.TD_COLUMN));

            treeItem.setText(0, columnName != null ? columnName + PluginConstant.SPACE_STRING + PluginConstant.PARENTHESIS_LEFT
                    + column.getSqlDataType().getName() + PluginConstant.PARENTHESIS_RIGHT : "null"); //$NON-NLS-1$
            treeItem.setData(COLUMN_INDICATOR_KEY, column);

            TreeEditor delLabelEditor = new TreeEditor(tree);
            Label delLabel = new Label(tree, SWT.NONE);
            delLabel.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
            delLabel.setImage(ImageLib.getImage(ImageLib.DELETE_ACTION));
            delLabel.setToolTipText(DefaultMessagesImpl.getString("AnalysisColumnTreeViewer.delete")); //$NON-NLS-1$
            delLabel.pack();
            delLabel.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseDown(MouseEvent e) {
                    deleteColumnItems(column);
                    removeItemBranch(treeItem);
                    // MOD mzhao 2005-05-05 bug 6587.
                    // MOD mzhao 2009-06-8, bug 5887.
                    updateBindConnection(masterPage, tree);
                }

            });

            delLabelEditor.minimumWidth = delLabel.getImage().getBounds().width;
            delLabelEditor.horizontalAlignment = SWT.CENTER;
            delLabelEditor.setEditor(delLabel, treeItem, 1);
            treeItem.setData(ITEM_EDITOR_KEY, new TreeEditor[] { delLabelEditor });
            /*
             * if (columnIndicator.hasIndicators()) { createIndicatorItems(treeItem,
             * columnIndicator.getIndicatorUnits()); }
             */
            delLabelEditor.layout();
            treeItem.setExpanded(true);
        }

        tree.layout();
        tree.redraw();
        this.setDirty(true);
        tree.forceFocus();
    }

    public void addElements(final List<Column> columns, int index) {
        this.addItemElements(columns, index);
    }

    /**
     * DOC xzhao Comment method "deleteTreeElements".
     * 
     * @param columnIndicators
     * @param deleteColumnIndiciators
     */
    private void deleteColumnItems(TdColumn deleteColumn) {
        List<Column> remainColumns = columnSetMultiValueList;
        for (int j = 0; j < columnSetMultiValueList.size(); j++) {
            TdColumn column = (TdColumn) columnSetMultiValueList.get(j);
            if (deleteColumn.equals(column)) {
                remainColumns.remove(j);
            }
        }
        this.columnSetMultiValueList = remainColumns;
        // setElements(columnSetMultiValueList);
    }

    public List<Column> getColumnSetMultiValueList() {
        return this.columnSetMultiValueList;
    }

    private void createColumnAnalysis(Tree newTree) {
        TreeItem[] items = newTree.getSelection();
        if (items.length > 0) {
            TreePath[] paths = new TreePath[items.length];

            for (int i = 0; i < items.length; i++) {
                TdColumn tdColumn = (TdColumn) items[i].getData(COLUMN_INDICATOR_KEY);
                paths[i] = new TreePath(new Object[] { tdColumn });
            }
            CreateColumnAnalysisAction analysisAction = new CreateColumnAnalysisAction();
            analysisAction.setSelection(new TreeSelection(paths));
            analysisAction.run();
        }
    }

    /**
     * Remove the selected elements(eg:TdColumn or Indicator) from tree.
     * 
     * @param newTree
     */
    private void removeSelectedElements(final Tree newTree) {
        TreeItem[] selection = newTree.getSelection();
        for (TreeItem item : selection) {
            TdColumn tdColumn = (TdColumn) item.getData(COLUMN_INDICATOR_KEY);
            deleteColumnItems(tdColumn);
            removeItemBranch(item);
        }
    }

    private void removeItemBranch(TreeItem item) {
        TreeEditor[] editors = (TreeEditor[]) item.getData(ITEM_EDITOR_KEY);
        if (editors != null) {
            for (int j = 0; j < editors.length; j++) {
                editors[j].getEditor().dispose();
                editors[j].dispose();
            }
        }

        if (item.getItemCount() == 0) {
            item.dispose();
            this.setDirty(true);
            return;
        }
        TreeItem[] items = item.getItems();
        for (int i = 0; i < items.length; i++) {
            removeItemBranch(items[i]);
        }
        item.dispose();
        this.setDirty(true);
    }

    private void addTreeListener(final Tree tree) {
        tree.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                boolean con = false;

                if (e.item instanceof TreeItem) {
                    TreeItem item = (TreeItem) e.item;
                    if (DATA_PARAM.equals(item.getData(DATA_PARAM))) {
                        tree.setMenu(null);
                        return;
                    } else if (item.getData(INDICATOR_UNIT_KEY) != null) {
                        IndicatorUnit indicatorUnit = (IndicatorUnit) item.getData(INDICATOR_UNIT_KEY);
                        IndicatorEnum type = indicatorUnit.getType();
                        con = IndicatorEnum.RegexpMatchingIndicatorEnum.compareTo(type) == 0
                                || IndicatorEnum.SqlPatternMatchingIndicatorEnum.compareTo(type) == 0;
                    }
                }
                createTreeMenu(tree, con);
            }

        });

        tree.addTreeListener(new TreeAdapter() {

            @Override
            public void treeCollapsed(TreeEvent e) {

                ExpandableComposite theSuitedComposite = getTheSuitedComposite(e);
                ScrolledForm form = masterPage.getForm();
                Composite comp = masterPage.getChartComposite();

                if (theSuitedComposite != null && theSuitedComposite.isExpanded()) {
                    getTheSuitedComposite(e).setExpanded(false);
                }

                comp.layout();
                form.reflow(true);
            }

            @Override
            public void treeExpanded(TreeEvent e) {
                ExpandableComposite theSuitedComposite = getTheSuitedComposite(e);
                ScrolledForm form = masterPage.getForm();
                Composite comp = masterPage.getChartComposite();

                if (theSuitedComposite != null && !theSuitedComposite.isExpanded()) {
                    theSuitedComposite.setExpanded(true);
                }

                comp.layout();
                form.reflow(true);
            }

        });
    }

    private ExpandableComposite getTheSuitedComposite(SelectionEvent e) {
        Composite[] previewChartCompsites = masterPage.getPreviewChartCompsites();
        if (previewChartCompsites == null) {
            return null;
        }

        Object obj = e.item.getData(COLUMN_INDICATOR_KEY);
        if (obj instanceof ColumnIndicator) {
            ColumnIndicator columnIndicator = (ColumnIndicator) obj;
            for (Composite comp : previewChartCompsites) {
                if (comp.getData() == columnIndicator) {
                    return (ExpandableComposite) comp;
                }
            }
        }

        return null;
    }

    /**
     * Getter for analysis.
     * 
     * @return the analysis
     */
    public Analysis getAnalysis() {
        return this.masterPage.getColumnSetAnalysisHandler().getAnalysis();
    }

    // public List<String> getComboString() {
    // return comboTextList;
    // }

    public Tree getTree() {
        return tree;
    }

    @Override
    public void updateModelViewer() {
        masterPage.recomputeIndicators();
        // columnSetMultiValueList =
        // masterPage.getColumnSetMultiValueIndicator()
        // .getAnalyzedColumns().subList(
        // 0,
        // masterPage.getColumnSetMultiValueIndicator()
        // .getAnalyzedColumns().size());
        columnSetMultiValueList.clear();
        this.setElements(masterPage.getSimpleStatIndicator().getAnalyzedColumns());
    }

    @Override
    public boolean canDrop(ModelElement modelElement) {
        List<TdColumn> existColumns = new ArrayList<TdColumn>();
        for (Column columnFromMultiValueList : this.getColumnSetMultiValueList()) {
            existColumns.add((TdColumn) columnFromMultiValueList);
        }

        if (existColumns.contains(modelElement)) {
            return false;
        }
        return true;
    }

    @Override
    public void dropModelElements(List<? extends ModelElement> modelElements, int index) {
        List<Column> columns = new ArrayList<Column>();
        for (ModelElement element : modelElements) {
            TdColumn column = SwitchHelpers.COLUMN_SWITCH.doSwitch(element);
            if (column != null) {
                columns.add(column);
            }
        }
        this.addElements(columns, index);
    }
}
