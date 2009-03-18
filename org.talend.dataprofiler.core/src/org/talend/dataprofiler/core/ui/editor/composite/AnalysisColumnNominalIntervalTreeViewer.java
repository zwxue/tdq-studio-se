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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
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
import org.talend.cwm.relational.TdColumn;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.manager.DQStructureManager;
import org.talend.dataprofiler.core.model.ColumnIndicator;
import org.talend.dataprofiler.core.ui.editor.AbstractAnalysisActionHandler;
import org.talend.dataprofiler.core.ui.editor.AbstractMetadataFormPage;
import org.talend.dataprofiler.core.ui.editor.analysis.ColumnCorrelationNominalAndIntervalMasterPage;
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
import orgomg.cwm.resource.relational.Column;

/**
 * 
 * DOC zhaoxinyi class global comment. Detailled comment
 */
public class AnalysisColumnNominalIntervalTreeViewer extends AbstractColumnDropTree {

    private static final String DATA_PARAM = "DATA_PARAM"; //$NON-NLS-1$

    public static final String INDICATOR_UNIT_KEY = "INDICATOR_UNIT_KEY"; //$NON-NLS-1$

    public static final String COLUMN_INDICATOR_KEY = "COLUMN_INDICATOR_KEY"; //$NON-NLS-1$

    public static final String ITEM_EDITOR_KEY = "ITEM_EDITOR_KEY"; //$NON-NLS-1$

    public static final String VIEWER_KEY = "org.talend.dataprofiler.core.ui.editor.composite.AnalysisColumnNominalIntervalTreeViewer"; //$NON-NLS-1$

    private static final int WIDTH1_CELL = 75;

    private Composite parentComp;

    private Tree tree;

    private List<Column> columnSetMultiValueList;

    // private final List<String> comboTextList = new ArrayList<String>();

    private ColumnCorrelationNominalAndIntervalMasterPage masterPage;

    private Menu menu;

    private MenuItem editPatternMenuItem;

    public AnalysisColumnNominalIntervalTreeViewer(Composite parent) {
        parentComp = parent;
        this.tree = createTree(parent);
        tree.setData(this);
    }

    public AnalysisColumnNominalIntervalTreeViewer(Composite parent, ColumnCorrelationNominalAndIntervalMasterPage masterPage) {
        this(parent);
        this.masterPage = masterPage;
        // this.setElements(masterPage.getColumnSetMultiValueIndicator().getAnalyzedColumns());
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
        column2.setWidth(120);
        column2.setText(DefaultMessagesImpl.getString("AnalysisColumnTreeViewer.dataminingType")); //$NON-NLS-1$
        column2.setToolTipText(DefaultMessagesImpl.getString("AnalysisColumnTreeViewer.columnTip")); //$NON-NLS-1$
        TreeColumn column4 = new TreeColumn(newTree, SWT.CENTER);
        column4.setWidth(80);
        column4.setText(DefaultMessagesImpl.getString("AnalysisColumnTreeViewer.operation")); //$NON-NLS-1$

        parent.layout();
        createTreeMenu(newTree, false);

        AbstractAnalysisActionHandler actionHandler = new AbstractAnalysisActionHandler(parent) {

            @Override
            protected void handleRemove() {
                removeSelectedElements(newTree);
            }

        };

        parent.setData(AbstractMetadataFormPage.ACTION_HANDLER, actionHandler);
        // ColumnViewerDND.installDND(newTree);
        // TreeViewerDNDDecorator treeDND = new TreeViewerDNDDecorator();
        // treeDND.installDND(newTree, true);
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
        ColumnViewerDND.installDND(newTree);

        this.addTreeListener(newTree);
        return newTree;
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
        MenuItem deleteMenuItem = new MenuItem(menu, SWT.CASCADE);
        deleteMenuItem.setText(DefaultMessagesImpl.getString("AnalysisColumnTreeViewer.removeElement")); //$NON-NLS-1$
        deleteMenuItem.setImage(ImageLib.getImage(ImageLib.DELETE_ACTION));
        deleteMenuItem.addSelectionListener(new SelectionAdapter() {

            /*
             * (non-Javadoc)
             * 
             * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
             */
            @Override
            public void widgetSelected(SelectionEvent e) {
                removeSelectedElements(newTree);
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
             * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
             */
            @Override
            public void widgetSelected(SelectionEvent e) {
                TreeItem[] selection = newTree.getSelection();

                if (selection.length == 1) {
                    DQRespositoryView dqview = (DQRespositoryView) CorePlugin.getDefault().findView(DQRespositoryView.ID);
                    try {
                        TdColumn tdColumn = (TdColumn) selection[0].getData(COLUMN_INDICATOR_KEY);
                        dqview.showSelectedElements(tdColumn);
                    } catch (Exception ex) {
                        ex.printStackTrace();
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
                 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
                 */
                @Override
                public void widgetSelected(SelectionEvent e) {
                    TreeItem[] selection = tree.getSelection();
                    if (selection.length > 0) {
                        TreeItem treeItem = selection[0];
                        IndicatorUnit indicatorUnit = (IndicatorUnit) treeItem.getData(INDICATOR_UNIT_KEY);
                        PatternMatchingIndicator indicator = (PatternMatchingIndicator) indicatorUnit.getIndicator();
                        Pattern pattern = indicator.getParameters().getDataValidDomain().getPatterns().get(0);
                        // MOD mzhao 2009-03-13 Feature 6066 Move all folders into one project.
                        IFolder patternFolder = ResourcesPlugin.getWorkspace().getRoot().getProject(org.talend.dataquality.PluginConstant.ROOTPROJECTNAME)
                                .getFolder(DQStructureManager.LIBRARIES).getFolder(DQStructureManager.PATTERNS);
                        IFolder sqlPatternFolder = ResourcesPlugin.getWorkspace().getRoot()
                                .getProject(org.talend.dataquality.PluginConstant.ROOTPROJECTNAME).getFolder(DQStructureManager.LIBRARIES).getFolder(
                                        DQStructureManager.SQL_PATTERNS);
                        IFile file = PatternResourceFileHelper.getInstance().getPatternFile(pattern,
                                new IFolder[] { patternFolder, sqlPatternFolder });
                        IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
                        try {
                            activePage.openEditor(new FileEditorInput(file),
                                    "org.talend.dataprofiler.core.ui.editor.pattern.PatternEditor"); //$NON-NLS-1$
                        } catch (PartInitException e1) {
                            e1.printStackTrace();
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

    public void setElements(final List<Column> columns) {
        this.tree.dispose();
        this.tree = createTree(this.parentComp);
        tree.setData(this);
        this.columnSetMultiValueList = columns;
        addItemElements(columns, 0);
        // addItemElements(columns);
    }

    private void addItemElements(final List<Column> columns, int index) {
        for (int i = 0; i < columns.size(); i++) {
            final TdColumn column = (TdColumn) columns.get(i);
            final TreeItem treeItem = new TreeItem(tree, SWT.NONE, index);
            String columnName = column.getName();
            treeItem.setImage(ImageLib.getImage(ImageLib.TD_COLUMN));

            treeItem.setText(0, columnName != null ? columnName + PluginConstant.SPACE_STRING + PluginConstant.PARENTHESIS_LEFT
                    + column.getSqlDataType().getName() + PluginConstant.PARENTHESIS_RIGHT : "null"); //$NON-NLS-1$
            treeItem.setData(COLUMN_INDICATOR_KEY, column);

            TreeEditor comboEditor = new TreeEditor(tree);
            tree.setData(DefaultMessagesImpl.getString("AnalysisColumnNominalIntervalTreeViewer.TreeEditor"), comboEditor); //$NON-NLS-1$

            final CCombo combo = new CCombo(tree, SWT.BORDER);
            for (DataminingType type : DataminingType.values()) {
                combo.add(type.getLiteral()); // MODSCA 2008-04-10 use literal for presentation
            }
            DataminingType dataminingType = MetadataHelper.getDataminingType(column);
            if (dataminingType == null) {
                dataminingType = MetadataHelper.getDefaultDataminingType(column.getJavaType());
            }

            if (dataminingType == null) {
                combo.select(0);
            } else {
                combo.setText(dataminingType.getLiteral());
            }
            MetadataHelper.setDataminingType(DataminingType.get(combo.getText()), column);
            /**
             * 
             * DOC zhaoxinyi AnalysisColumnNominalIntervalTreeViewer class global comment. Detailled comment
             */
            class Selection extends SelectionAdapter {

                public void widgetSelected(SelectionEvent e) {
                    MetadataHelper.setDataminingType(DataminingType.get(combo.getText()), column);
                    setDirty(true);
                }
            }
            combo.addSelectionListener(new Selection());
            // comboTextList.add(combo.getText().trim());
            combo.setEditable(false);
            comboEditor.minimumWidth = WIDTH1_CELL;
            comboEditor.setEditor(combo, treeItem, 1);

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
                }

            });

            delLabelEditor.minimumWidth = delLabel.getImage().getBounds().width;
            delLabelEditor.horizontalAlignment = SWT.CENTER;
            delLabelEditor.setEditor(delLabel, treeItem, 2);
            treeItem.setData(ITEM_EDITOR_KEY, new TreeEditor[] { comboEditor, delLabelEditor });
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

    // private void createIndicatorItems(final TreeItem treeItem, IndicatorUnit[] indicatorUnits) {
    // for (IndicatorUnit indicatorUnit : indicatorUnits) {
    // createOneUnit(treeItem, indicatorUnit);
    // }
    // }

    /**
     * DOC xzhao Comment method "deleteIndicatorItems".
     * 
     * @param treeItem
     * @param inidicatorUnit
     */
    private void deleteIndicatorItems(ColumnIndicator columnIndicator, IndicatorUnit inidicatorUnit) {
        columnIndicator.removeIndicatorUnit(inidicatorUnit);
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
        return this.masterPage.getColumnCorrelationAnalysisHandler().getAnalysis();
    }

    // public List<String> getComboString() {
    // return comboTextList;
    // }

    public Tree getTree() {
        return tree;
    }

    @Override
    public boolean canDrop(Column column) {
        List<TdColumn> existColumns = new ArrayList<TdColumn>();
        for (Column columnFromMultiValueList : this.getColumnSetMultiValueList()) {
            existColumns.add((TdColumn) columnFromMultiValueList);
        }

        if (existColumns.contains(column)) {
            return false;
        }
        return true;
    }

    @Override
    public void dropColumns(List<Column> columns, int index) {
        this.columnSetMultiValueList.addAll(index, columns);
        this.addElements(columns, index);
        // this.setElements(columnSetMultiValueList);
    }
}
