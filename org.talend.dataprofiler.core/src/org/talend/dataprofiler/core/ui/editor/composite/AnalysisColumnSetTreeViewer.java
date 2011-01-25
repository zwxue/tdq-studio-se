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
package org.talend.dataprofiler.core.ui.editor.composite;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFolder;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.TreeEditor;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TreeAdapter;
import org.eclipse.swt.events.TreeEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.talend.core.model.metadata.MetadataColumnRepositoryObject;
import org.talend.cwm.relational.MeatadataColumn;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.model.ColumnIndicator;
import org.talend.dataprofiler.core.model.ModelElementIndicator;
import org.talend.dataprofiler.core.ui.editor.AbstractAnalysisActionHandler;
import org.talend.dataprofiler.core.ui.editor.AbstractMetadataFormPage;
import org.talend.dataprofiler.core.ui.editor.analysis.ColumnSetMasterPage;
import org.talend.dataprofiler.core.ui.editor.preview.IndicatorUnit;
import org.talend.dataprofiler.core.ui.views.ColumnViewerDND;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.helpers.MetadataHelper;
import org.talend.dataquality.indicators.DataminingType;
import org.talend.dataquality.indicators.RegexpMatchingIndicator;
import org.talend.dq.nodes.DBColumnRepNode;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.RepositoryNode;

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

    private Composite parentComp;

    private Map<String, Button> buttons = new HashMap<String, Button>();

    private List<IRepositoryNode> columnSetMultiValueList;

    // private final List<String> comboTextList = new ArrayList<String>();

    private ColumnSetMasterPage masterPage;

    // private Menu menu;

    // private MenuItem editPatternMenuItem;

    public AnalysisColumnSetTreeViewer(Composite parent, ColumnSetMasterPage masterPage) {
        absMasterPage = masterPage;
        viewKey = VIEWER_KEY;
        parentComp = parent;
        tree = createTree(parent);
        tree.setData(this);
        columnSetMultiValueList = new ArrayList<IRepositoryNode>();
        this.masterPage = masterPage;
        this.createButtonSection(parent.getParent());
        this.setElements(masterPage.getCurrentModelElementIndicators());
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

        TreeColumn column3 = new TreeColumn(newTree, SWT.CENTER);
        column3.setWidth(80);
        column3.setText(DefaultMessagesImpl.getString("AnalysisColumnTreeViewer.pattern")); //$NON-NLS-1$

        TreeColumn column4 = new TreeColumn(newTree, SWT.CENTER);
        column4.setWidth(80);
        column4.setText(DefaultMessagesImpl.getString("AnalysisColumnTreeViewer.operation")); //$NON-NLS-1$

        parent.layout();
        // createTreeMenu(newTree, false);

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

        // addSourceDND(newTree);
        addTargetDND(newTree);

        addTreeListener(newTree);
        return newTree;
    }

    /**
     * 
     * DOC zshen Comment method "createButtonSection".
     * 
     * @param topComp create the button with delButton,moveUpButton,moveDownButton
     */
    private void createButtonSection(Composite topComp) {
        Composite buttonsComp = masterPage.getEditor().getToolkit().createComposite(topComp, SWT.NONE);
        buttonsComp.setLayout(new GridLayout(3, true));
        buttonsComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        GridData buttonGridData = new GridData();
        buttonGridData.heightHint = 25;
        buttonGridData.horizontalAlignment = GridData.CENTER;
        buttonGridData.verticalAlignment = GridData.FILL;
        buttonGridData.grabExcessHorizontalSpace = true;
        buttonGridData.grabExcessVerticalSpace = true;

        final Button delButton = new Button(buttonsComp, SWT.NULL);
        delButton.setImage(ImageLib.getImage(ImageLib.DELETE_ACTION));
        delButton.setLayoutData(buttonGridData);
        final Button moveUpButton = new Button(buttonsComp, SWT.NULL);
        moveUpButton.setText(DefaultMessagesImpl.getString("ColumnsComparisonMasterDetailsPage.moveUp")); //$NON-NLS-1$
        moveUpButton.setLayoutData(buttonGridData);
        final Button moveDownButton = new Button(buttonsComp, SWT.NULL);
        moveDownButton.setText(DefaultMessagesImpl.getString("ColumnsComparisonMasterDetailsPage.moveDown")); //$NON-NLS-1$
        moveDownButton.setLayoutData(buttonGridData);
        buttons.put("delButton", delButton);
        buttons.put("moveUpButton", moveUpButton);
        buttons.put("moveDownButton", moveDownButton);
        enabledButtons(false);
        moveUpButton.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                moveElement(masterPage.getTreeViewer(), -1);
            }

        });
        moveDownButton.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                moveElement(masterPage.getTreeViewer(), 1);
            }

        });
        delButton.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                Tree currentTree = tree;
                Object[] selectItem = currentTree.getSelection();
                List<IRepositoryNode> columnList = masterPage.getTreeViewer().getColumnSetMultiValueList();
                for (int i = 0; i < selectItem.length; i++) {
                    Object removeElement = ((TreeItem) selectItem[i])
                            .getData(AnalysisColumnNominalIntervalTreeViewer.COLUMN_INDICATOR_KEY);
                    columnList.remove(removeElement);
                }
                masterPage.getTreeViewer().setInput(convertList(columnList).toArray());
                enabledButtons(false);
            }
        });
    }

    /**
     * 
     * DOC zshen Comment method "enabledButtons".
     * 
     * @param enabled the state of buttons .
     * 
     * change the state of buttons.
     */
    public void enabledButtons(boolean enabled) {

        boolean moveEnabled = enabled;
        TreeItem[] selectItems = getTree().getSelection();
        if (!isSelectedColumn(selectItems)) {
            moveEnabled = false;
        }
        buttons.get("delButton").setEnabled(enabled);
        buttons.get("moveUpButton").setEnabled(moveEnabled);
        buttons.get("moveDownButton").setEnabled(moveEnabled);
    }

    /**
     * 
     * DOC zshen Comment method "moveElement".
     * 
     * @param columnsElementViewer
     * @param step
     * 
     * move the element of the columnList to up or down.
     */
    private void moveElement(AnalysisColumnSetTreeViewer columnsElementViewer, int step) {
        Tree currentTree = columnsElementViewer.getTree();
        Object[] selectItem = currentTree.getSelection();
        List<IRepositoryNode> columnList = columnsElementViewer.getColumnSetMultiValueList();
        int index = 0;
        List<Integer> indexArray = new ArrayList<Integer>();
        for (int i = 0; i < selectItem.length; i++) {
            index = currentTree.indexOf((TreeItem) selectItem[i]);
            if (index + step > -1 && index + step < columnList.size()) {
                RepositoryNode moveElement = (RepositoryNode) ((TreeItem) selectItem[i])
                        .getData(AnalysisColumnNominalIntervalTreeViewer.COLUMN_INDICATOR_KEY);
                columnList.remove(moveElement);
                columnList.add((index + step), moveElement);
                indexArray.add(index + step);

                ModelElementIndicator tmpElement = modelElementIndicators[index + step];
                modelElementIndicators[index + step] = modelElementIndicators[index];
                modelElementIndicators[index] = tmpElement;

            } else {
                return;
            }
        }

        // columnsElementViewer.setInput(columnList.toArray());
        setElements(modelElementIndicators);
        currentTree = columnsElementViewer.getTree();
        for (int i = 0; i < indexArray.size(); i++) {
            currentTree.select(currentTree.getItem(indexArray.get(i)));
        }
    }

    private List<IRepositoryNode> convertList(List<IRepositoryNode> columnList) {
        List<IRepositoryNode> resultList = new ArrayList<IRepositoryNode>();
        for (int i = columnList.size() - 1; i >= 0; i--) {
            resultList.add(columnList.get(i));
        }
        return resultList;
    }

    /**
     * DOC bZhou Comment method "addTargetDND".
     * 
     * @param newTree
     */
    private void addTargetDND(final Tree newTree) {
        ColumnViewerDND.installDND(newTree);
    }

    public void setInput(Object[] objs) {
        if (objs != null && objs.length != 0) {
            List<DBColumnRepNode> columnList = new ArrayList<DBColumnRepNode>();
            for (Object obj : objs) {
                if (obj instanceof DBColumnRepNode) {
                    columnList.add((DBColumnRepNode) obj);
                }

            }
            // MOD yyi 2010-05-13 12828
            Collections.reverse(columnList);
        }
        // ~
        super.setInput(objs);
    }

    public void setElements(ModelElementIndicator[] elements) {
        this.tree.dispose();
        this.tree = createTree(this.parentComp);
        tree.setData(this);
        // MOD mzhao bug 8282 2009-7-31 Clear column cache.
        columnSetMultiValueList.clear();
        this.modelElementIndicators = elements;
        addItemElements(elements);
        // addItemElements(columns);
        // masterPage.getAnalysis().getContext().setConnection(null);
        // MOD mzhao 2009-05-05 bug 6587.
        updateBindConnection(masterPage, tree);
    }

    private void addItemElements(final ModelElementIndicator[] elements) {
        for (int i = 0; i < elements.length; i++) {
            final ModelElementIndicator meIndicator = (ModelElementIndicator) elements[i];

            final MeatadataColumn column = (MeatadataColumn) ((MetadataColumnRepositoryObject) meIndicator.getModelElementRepositoryNode()
                    .getObject()).getTdColumn();
            final TreeItem treeItem = new TreeItem(tree, SWT.NONE);

            MetadataHelper.setDataminingType(DataminingType.NOMINAL, column);
            columnSetMultiValueList.add(meIndicator.getModelElementRepositoryNode());
            String columnName = column.getName();
            treeItem.setImage(ImageLib.getImage(ImageLib.TD_COLUMN));

            treeItem.setText(0, columnName != null ? columnName + PluginConstant.SPACE_STRING + PluginConstant.PARENTHESIS_LEFT
                    + column.getSqlDataType().getName() + PluginConstant.PARENTHESIS_RIGHT : "null"); //$NON-NLS-1$
            treeItem.setData(COLUMN_INDICATOR_KEY, column);
            treeItem.setData(MODELELEMENT_INDICATOR_KEY, meIndicator);

            // MOD mzhao feature 13040 , 2010-05-21

            TreeEditor comboEditor = new TreeEditor(tree);
            final CCombo combo = new CCombo(tree, SWT.BORDER);
            for (DataminingType type : DataminingType.values()) {
                combo.add(type.getLiteral()); // MODSCA 2008-04-10 use literal
                // for presentation
            }
            final MeatadataColumn tdColumn = (MeatadataColumn) ((MetadataColumnRepositoryObject) meIndicator.getModelElementRepositoryNode()
                    .getObject()).getTdColumn();
            DataminingType dataminingType = MetadataHelper.getDataminingType(tdColumn);

            if (dataminingType == null) {
                combo.select(0);
            } else {
                combo.setText(dataminingType.getLiteral());
            }
            combo.addSelectionListener(new SelectionAdapter() {

                public void widgetSelected(SelectionEvent e) {
                    MetadataHelper.setDataminingType(DataminingType.get(combo.getText()), tdColumn);
                    setDirty(true);
                }

            });
            combo.setEditable(false);

            comboEditor.minimumWidth = WIDTH1_CELL;
            comboEditor.setEditor(combo, treeItem, 1);

            TreeEditor addPatternEditor = new TreeEditor(tree);
            Label addPatternLabl = new Label(tree, SWT.NONE);
            addPatternLabl.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
            addPatternLabl.setImage(ImageLib.getImage(ImageLib.ADD_PATTERN));
            addPatternLabl.setToolTipText(DefaultMessagesImpl.getString("AnalysisColumnTreeViewer.addPattern")); //$NON-NLS-1$
            addPatternLabl.pack();

            PatternMouseAdapter mouseAdapter = new PatternMouseAdapter(this, masterPage, meIndicator, treeItem);
            mouseAdapter.addFilter(new ViewerFilter() {

                @Override
                public boolean select(Viewer viewer, Object parentElement, Object element) {
                    if (element instanceof IFolder) {
                        IFolder folder = (IFolder) element;
                        return !folder.getName().endsWith("SQL");
                    }
                    return true;
                }
            });
            addPatternLabl.addMouseListener(mouseAdapter);
            addPatternEditor.minimumWidth = addPatternLabl.getImage().getBounds().width;
            addPatternEditor.setEditor(addPatternLabl, treeItem, 2);

            TreeEditor delLabelEditor = new TreeEditor(tree);
            Label delLabel = new Label(tree, SWT.NONE);
            delLabel.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
            delLabel.setImage(ImageLib.getImage(ImageLib.DELETE_ACTION));
            delLabel.setToolTipText(DefaultMessagesImpl.getString("AnalysisColumnTreeViewer.delete")); //$NON-NLS-1$
            delLabel.pack();
            delLabel.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseDown(MouseEvent e) {
                    deleteColumnItems(meIndicator.getModelElementRepositoryNode());
                    removeItemBranch(treeItem);
                    enabledButtons(false);
                    // MOD mzhao 2005-05-05 bug 6587.
                    // MOD mzhao 2009-06-8, bug 5887.
                    updateBindConnection(masterPage, tree);
                }

            });

            delLabelEditor.minimumWidth = delLabel.getImage().getBounds().width;
            delLabelEditor.horizontalAlignment = SWT.CENTER;
            delLabelEditor.setEditor(delLabel, treeItem, 3);
            treeItem.setData(ITEM_EDITOR_KEY, new TreeEditor[] { comboEditor, addPatternEditor, delLabelEditor });
            if (meIndicator.hasIndicators()) {
                createIndicatorItems(treeItem, meIndicator.getIndicatorUnits());
            }
            delLabelEditor.layout();
            treeItem.setExpanded(true);
        }

        tree.layout();
        tree.redraw();
        this.setDirty(true);
        tree.forceFocus();
    }

    private boolean isSelectedColumn(TreeItem[] items) {
        for (TreeItem item : items) {
            if (item.getData(AbstractColumnDropTree.INDICATOR_UNIT_KEY) != null
                    || item.getData(AbstractColumnDropTree.DATA_PARAM) != null) {
                return false;
            }
        }

        return true;
    }

    public void addElements(final ModelElementIndicator[] elements) {
        // this.addItemElements(elements);

        RepositoryNode[] columns = new RepositoryNode[elements.length];
        for (int i = 0; i < elements.length; i++) {
            columns[i] = (RepositoryNode) elements[i].getModelElementRepositoryNode();
        }
        List<IRepositoryNode> oriColumns = getColumnSetMultiValueList();
        for (RepositoryNode column : columns) {// FIXME_15750 Caution: the direct instance comparison might not fittable
                                               // here.
            if (!oriColumns.contains(column)) {
                oriColumns.add(column);
            }
        }
        setInput(oriColumns.toArray());
        // MOD qiongli 2010-6-4,bug 0012766,after drag and drop a column from left view,update the connection state
        updateBindConnection(masterPage, tree);
    }

    /**
     * DOC xzhao Comment method "deleteTreeElements".
     * 
     * @param columnIndicators
     * @param deleteColumnIndiciators
     */
    private void deleteColumnItems(IRepositoryNode deleteColumn) {
        List<IRepositoryNode> remainColumns = columnSetMultiValueList;
        for (int j = 0; j < columnSetMultiValueList.size(); j++) {
            RepositoryNode column = (RepositoryNode) columnSetMultiValueList.get(j);
            if (deleteColumn.getObject().getId() == column.getObject().getId()) {
                remainColumns.remove(j);
            }
        }
        this.columnSetMultiValueList = remainColumns;
    }

    public List<IRepositoryNode> getColumnSetMultiValueList() {
        return this.columnSetMultiValueList;
    }

    // private void createColumnAnalysis(Tree newTree) {
    // TreeItem[] items = newTree.getSelection();
    // if (items.length > 0) {
    // TreePath[] paths = new TreePath[items.length];
    //
    // for (int i = 0; i < items.length; i++) {
    // TdColumn tdColumn = (TdColumn) items[i].getData(COLUMN_INDICATOR_KEY);
    // paths[i] = new TreePath(new Object[] { tdColumn });
    // }
    // CreateColumnAnalysisAction analysisAction = new CreateColumnAnalysisAction();
    // analysisAction.setSelection(new TreeSelection(paths));
    // analysisAction.run();
    // }
    // }

    /**
     * Remove the selected elements(eg:TdColumn or Indicator) from tree.
     * 
     * @param newTree
     */
    private void removeSelectedElements(final Tree newTree) {
        TreeItem[] selection = newTree.getSelection();
        for (TreeItem item : selection) {
            ModelElementIndicator meIndicator = (ModelElementIndicator) item.getData(MODELELEMENT_INDICATOR_KEY);

            IndicatorUnit indicatorUnit = (IndicatorUnit) item.getData(INDICATOR_UNIT_KEY);
            if (indicatorUnit != null) {
                deleteIndicatorItems(meIndicator, indicatorUnit);
            } else {
                RepositoryNode tdColumn = (RepositoryNode) item.getData(COLUMN_INDICATOR_KEY);
                deleteColumnItems(tdColumn);
            }
            removeItemBranch(item);
        }
    }

    private void addTreeListener(final Tree tree) {

        tree.addFocusListener(new FocusListener() {

            public void focusGained(FocusEvent e) {
                if (e.getSource() instanceof Tree) {
                    Tree currentTree = (Tree) e.getSource();

                    if (currentTree.getSelection().length > 0) {
                        enabledButtons(true);
                    }
                }
            }

            public void focusLost(FocusEvent e) {
                if (e.getSource() instanceof Tree) {
                    Tree currentTree = (Tree) e.getSource();

                    if (currentTree.getSelection().length <= 0) {
                        enabledButtons(false);
                    }
                }
            }
        });
        tree.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                // boolean con = false;

                if (e.item instanceof TreeItem) {
                    enabledButtons(true);
                    TreeItem item = (TreeItem) e.item;
                    if (DATA_PARAM.equals(item.getData(DATA_PARAM))) {
                        tree.setMenu(null);
                        return;
                    } else {
                        // IndicatorUnit indicatorUnit = (IndicatorUnit) item.getData(INDICATOR_UNIT_KEY);
                        // IndicatorEnum type = indicatorUnit.getType();
                        // con = IndicatorEnum.RegexpMatchingIndicatorEnum.compareTo(type) == 0
                        // || IndicatorEnum.SqlPatternMatchingIndicatorEnum.compareTo(type) == 0;
                        new AnalysisColumnSetMenuProvider(tree).createTreeMenu(Boolean.TRUE);
                    }
                }
                // createTreeMenu(tree, con);
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
        this.setElements(masterPage.getCurrentModelElementIndicators());
    }

    @Override
    public boolean canDrop(IRepositoryNode reposNode) {
        List<IRepositoryNode> existColumns = new ArrayList<IRepositoryNode>();
        for (IRepositoryNode columnFromMultiValueList : this.getColumnSetMultiValueList()) {
            existColumns.add(columnFromMultiValueList);
        }

        if (existColumns.contains(reposNode)) {
            return false;
        }
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.dataprofiler.core.ui.editor.composite.AbstractColumnDropTree#createOneUnit(org.eclipse.swt.widgets
     * .TreeItem, org.talend.dataprofiler.core.ui.editor.preview.IndicatorUnit)
     */
    @Override
    public void createOneUnit(TreeItem treeItem, IndicatorUnit indicatorUnit) {
        super.createOneUnit(treeItem, indicatorUnit);
        treeItem.setExpanded(true);
        masterPage.getAllMatchIndicator().getCompositeRegexMatchingIndicators().add(
                (RegexpMatchingIndicator) indicatorUnit.getIndicator());
        masterPage.updateIndicatorSection();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.dataprofiler.core.ui.editor.composite.AbstractColumnDropTree#removeItemBranch(org.eclipse.swt.widgets
     * .TreeItem)
     */
    @Override
    protected void removeItemBranch(TreeItem item) {
        IndicatorUnit unit = (IndicatorUnit) item.getData(INDICATOR_UNIT_KEY);
        ModelElementIndicator meIndicator = (ModelElementIndicator) item.getData(MODELELEMENT_INDICATOR_KEY);

        super.removeItemBranch(item);
        if (null != unit) {
            meIndicator.removeIndicatorUnit(unit);
            masterPage.getAllMatchIndicator().getCompositeRegexMatchingIndicators().remove(unit.getIndicator());
            masterPage.updateIndicatorSection();
        }
    }

    /**
     * DOC yyi AnalysisColumnSetTreeViewer class global comment. Detailled comment
     */
    class AnalysisColumnSetMenuProvider extends ModelElementTreeMenuProvider {

        /**
         * DOC yyi AnalysisColumnSetMenuProvider constructor comment.
         * 
         * @param tree
         */
        public AnalysisColumnSetMenuProvider(Tree tree) {
            super(tree);
        }

        @Override
        protected void removeSelectedElements2(Tree theTree) {
            removeSelectedElements(theTree);
        }

        @Override
        protected Analysis getAnalysis2() {
            return getAnalysis();
        }

    }
}
