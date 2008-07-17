// ============================================================================
//
// Copyright (C) 2006-2007 Talend Inc. - www.talend.com
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
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.help.HelpSystem;
import org.eclipse.help.IContext;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.TreeEditor;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.CheckedTreeSelectionDialog;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.cwm.relational.TdColumn;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.helper.PatternResourceFileHelper;
import org.talend.dataprofiler.core.manager.DQStructureManager;
import org.talend.dataprofiler.core.model.ColumnIndicator;
import org.talend.dataprofiler.core.model.nodes.indicator.tpye.IndicatorEnum;
import org.talend.dataprofiler.core.pattern.PatternUtilities;
import org.talend.dataprofiler.core.ui.dialog.IndicatorSelectDialog;
import org.talend.dataprofiler.core.ui.editor.AbstractAnalysisActionHandler;
import org.talend.dataprofiler.core.ui.editor.AbstractFormPage;
import org.talend.dataprofiler.core.ui.editor.preview.IndicatorUnit;
import org.talend.dataprofiler.core.ui.utils.HelpUtils;
import org.talend.dataprofiler.core.ui.views.ColumnViewerDND;
import org.talend.dataprofiler.core.ui.wizard.indicator.IndicatorOptionsWizard;
import org.talend.dataprofiler.help.HelpPlugin;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.domain.pattern.Pattern;
import org.talend.dataquality.helpers.MetadataHelper;
import org.talend.dataquality.indicators.DataminingType;
import org.talend.dataquality.indicators.DateParameters;
import org.talend.dataquality.indicators.IndicatorParameters;
import org.talend.dataquality.indicators.TextParameters;

/**
 * @author rli
 * 
 */
public class AnalysisColumnTreeViewer extends AbstractPagePart {

    public static final String INDICATOR_UNIT_KEY = "INDICATOR_UNIT_KEY";

    public static final String COLUMN_INDICATOR_KEY = "COLUMN_INDICATOR_KEY";

    public static final String ITEM_EDITOR_KEY = "ITEM_EDITOR_KEY";

    public static final String VIEWER_KEY = "org.talend.dataprofiler.core.ui.editor.composite.AnasisColumnTreeViewer";

    private static final int WIDTH1_CELL = 75;

    private Composite parentComp;

    private Tree tree;

    private ColumnIndicator[] columnIndicators;

    private Analysis analysis;

    public AnalysisColumnTreeViewer(Composite parent) {
        parentComp = parent;
        this.tree = createTree(parent);
    }

    public AnalysisColumnTreeViewer(Composite parent, ColumnIndicator[] columnIndicators, Analysis analysis) {
        this(parent);
        this.analysis = analysis;
        this.setElements(columnIndicators);
        this.setDirty(false);
    }

    /**
     * @param parent
     */
    private Tree createTree(Composite parent) {
        final Tree newTree = new Tree(parent, SWT.MULTI);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(newTree);

        newTree.setHeaderVisible(false);
        TreeColumn column1 = new TreeColumn(newTree, SWT.CENTER);
        column1.setWidth(190);
        TreeColumn column2 = new TreeColumn(newTree, SWT.CENTER);
        column2.setWidth(80);
        TreeColumn column3 = new TreeColumn(newTree, SWT.CENTER);
        column3.setWidth(120);
        TreeColumn column4 = new TreeColumn(newTree, SWT.CENTER);
        column4.setWidth(120);

        parent.layout();
        Menu menu = new Menu(newTree);
        MenuItem deleteMenuItem = new MenuItem(menu, SWT.CASCADE);
        deleteMenuItem.setText("Remove elements");
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
        newTree.setMenu(menu);

        AbstractAnalysisActionHandler actionHandler = new AbstractAnalysisActionHandler(parent) {

            @Override
            protected void handleRemove() {
                removeSelectedElements(newTree);
            }

        };

        parent.setData(AbstractFormPage.ACTION_HANDLER, actionHandler);
        ColumnViewerDND.installDND(newTree);
        return newTree;
    }

    public void setInput(Object[] objs) {
        if (objs != null && objs.length != 0) {
            if (!(objs[0] instanceof TdColumn)) {
                return;
            }
        }
        List<TdColumn> columnList = new ArrayList<TdColumn>();
        for (Object obj : objs) {
            columnList.add((TdColumn) obj);
        }
        List<ColumnIndicator> columnIndicatorList = new ArrayList<ColumnIndicator>();
        for (ColumnIndicator columnIndicator : columnIndicators) {
            if (columnList.contains(columnIndicator.getTdColumn())) {
                columnIndicatorList.add(columnIndicator);
                columnList.remove(columnIndicator.getTdColumn());
            }
        }

        for (TdColumn column : columnList) {
            columnIndicatorList.add(new ColumnIndicator(column));
        }
        this.columnIndicators = columnIndicatorList.toArray(new ColumnIndicator[columnIndicatorList.size()]);
        this.setElements(columnIndicators);
    }

    public void setElements(final ColumnIndicator[] elements) {
        this.tree.dispose();
        this.tree = createTree(this.parentComp);
        tree.setData(VIEWER_KEY, this);
        this.columnIndicators = elements;
        addItemElements(elements);
    }

    private void addItemElements(final ColumnIndicator[] elements) {
        for (int i = 0; i < elements.length; i++) {
            final TreeItem treeItem = new TreeItem(tree, SWT.NONE);

            final ColumnIndicator columnIndicator = (ColumnIndicator) elements[i];

            treeItem.setImage(ImageLib.getImage(ImageLib.TD_COLUMN));
            String columnName = columnIndicator.getTdColumn().getName();
            treeItem.setText(0, columnName != null ? columnName + PluginConstant.SPACE_STRING + PluginConstant.PARENTHESIS_LEFT
                    + columnIndicator.getTdColumn().getSqlDataType().getName() + PluginConstant.PARENTHESIS_RIGHT : "null");
            treeItem.setData(COLUMN_INDICATOR_KEY, columnIndicator);

            TreeEditor comboEditor = new TreeEditor(tree);
            final CCombo combo = new CCombo(tree, SWT.BORDER);
            for (DataminingType type : DataminingType.values()) {
                combo.add(type.getLiteral()); // MODSCA 2008-04-10 use literal for presentation
            }
            DataminingType dataminingType = MetadataHelper.getDataminingType(columnIndicator.getTdColumn());
            if (dataminingType == null) {
                dataminingType = MetadataHelper.getDefaultDataminingType(columnIndicator.getTdColumn().getJavaType());
            }

            if (dataminingType == null) {
                combo.select(0);
            } else {
                combo.setText(dataminingType.getLiteral());
            }
            combo.addSelectionListener(new SelectionAdapter() {

                public void widgetSelected(SelectionEvent e) {
                    MetadataHelper.setDataminingType(DataminingType.get(combo.getText()), columnIndicator.getTdColumn());
                    setDirty(true);
                }

            });
            combo.setEditable(false);

            comboEditor.minimumWidth = WIDTH1_CELL;
            comboEditor.setEditor(combo, treeItem, 1);

            TreeEditor addPatternEditor = new TreeEditor(tree);
            Button addPatternBtn = new Button(tree, SWT.NONE);
            addPatternBtn.setText("add pattern");
            addPatternBtn.pack();
            addPatternBtn.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(SelectionEvent e) {
                    CheckedTreeSelectionDialog dialog = new CheckedTreeSelectionDialog(null, new PatternLabelProvider(),
                            new WorkbenchContentProvider());

                    IProject defaultPatternFolder = ResourcesPlugin.getWorkspace().getRoot().getProject(
                            DQStructureManager.LIBRARIES);
                    dialog.setInput(defaultPatternFolder);
                    dialog.addFilter(new ViewerFilter() {

                        /*
                         * (non-Javadoc)
                         * 
                         * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers.Viewer,
                         * java.lang.Object, java.lang.Object)
                         */
                        @Override
                        public boolean select(Viewer viewer, Object parentElement, Object element) {
                            if (element instanceof IFile) {
                                IFile file = (IFile) element;
                                if (FactoriesUtil.PATTERN.equals(file.getFileExtension())) {
                                    return true;
                                }
                            } else if (element instanceof IFolder) {
                                IFolder folder = (IFolder) element;
                                return PatternUtilities.isLibraiesSubfolder(folder, DQStructureManager.PATTERNS,
                                        DQStructureManager.SQL_PATTERNS);
                            }
                            return false;
                        }
                    });
                    dialog.setContainerMode(true);
                    dialog.setTitle("Pattern Selector");
                    dialog.setMessage("Patterns:");
                    dialog.setSize(80, 30);
                    dialog.create();
                    if (dialog.open() == Window.OK) {
                        for (Object obj : dialog.getResult()) {
                            if (obj instanceof IFile) {
                                IFile file = (IFile) obj;
                                IndicatorUnit addIndicatorUnit = PatternUtilities.createIndicatorUnit(file, columnIndicator,
                                        analysis);
                                createOneUnit(treeItem, addIndicatorUnit);
                                setDirty(true);
                            }
                        }
                    }
                }

            });
            addPatternEditor.minimumWidth = WIDTH1_CELL;
            addPatternEditor.setEditor(addPatternBtn, treeItem, 2);

            TreeEditor delLabelEditor = new TreeEditor(tree);
            Label delLabel = new Label(tree, SWT.NONE);
            delLabel.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
            delLabel.setImage(ImageLib.getImage(ImageLib.DELETE_ACTION));
            delLabel.setToolTipText("delete");
            delLabel.pack();
            delLabel.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseDown(MouseEvent e) {
                    deleteColumnItems(columnIndicator);
                    if (treeItem.getParentItem() != null && treeItem.getParentItem().getData(INDICATOR_UNIT_KEY) != null) {
                        setElements(columnIndicators);
                    } else {
                        removeItemBranch(treeItem);
                    }
                }

            });

            delLabelEditor.minimumWidth = WIDTH1_CELL;
            delLabelEditor.horizontalAlignment = SWT.CENTER;
            delLabelEditor.setEditor(delLabel, treeItem, 3);
            treeItem.setData(ITEM_EDITOR_KEY, new TreeEditor[] { comboEditor, delLabelEditor, addPatternEditor });
            if (columnIndicator.hasIndicators()) {
                createIndicatorItems(treeItem, columnIndicator.getIndicatorUnits());
            }
            treeItem.setExpanded(true);
        }
        this.setDirty(true);
    }

    public void addElements(final ColumnIndicator[] elements) {

        ColumnIndicator[] newsArray = new ColumnIndicator[this.columnIndicators.length + elements.length];
        System.arraycopy(this.columnIndicators, 0, newsArray, 0, this.columnIndicators.length);
        for (int i = 0; i < elements.length; i++) {
            newsArray[this.columnIndicators.length + i] = elements[i];
        }
        this.columnIndicators = newsArray;
        this.addItemElements(elements);
    }

    private void createIndicatorItems(final TreeItem treeItem, IndicatorUnit[] indicatorUnits) {
        for (IndicatorUnit indicatorUnit : indicatorUnits) {
            createOneUnit(treeItem, indicatorUnit);
        }
    }

    /**
     * DOC qzhang Comment method "createOneUnit".
     * 
     * @param treeItem
     * @param indicatorUnit
     */
    public void createOneUnit(final TreeItem treeItem, IndicatorUnit indicatorUnit) {
        final TreeItem indicatorItem = new TreeItem(treeItem, SWT.NONE);
        final IndicatorUnit unit = indicatorUnit;
        IndicatorEnum type = indicatorUnit.getType();
        final IndicatorEnum indicatorEnum = type;
        indicatorItem.setData(COLUMN_INDICATOR_KEY, treeItem.getData(COLUMN_INDICATOR_KEY));
        indicatorItem.setData(INDICATOR_UNIT_KEY, unit);
        indicatorItem.setData(VIEWER_KEY, this);
        String label = indicatorUnit.getIndicatorName();
        if (IndicatorEnum.RegexpMatchingIndicatorEnum.compareTo(type) == 0
                || IndicatorEnum.SqlPatternMatchingIndicatorEnum.compareTo(type) == 0) {
            indicatorItem.setImage(0, ImageLib.getImage(ImageLib.PATTERN_REG));
        }
        indicatorItem.setText(0, label);

        TreeEditor optionEditor;
        // if (indicatorEnum.hasChildren()) {
        optionEditor = new TreeEditor(tree);
        Label optionLabel = new Label(tree, SWT.NONE);
        optionLabel.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
        optionLabel.setImage(ImageLib.getImage(ImageLib.INDICATOR_OPTION));
        optionLabel.setToolTipText("Options");
        optionLabel.pack();
        optionLabel.setData(indicatorUnit);
        optionLabel.addMouseListener(new MouseAdapter() {

            /*
             * (non-Javadoc)
             * 
             * @see org.eclipse.swt.events.MouseAdapter#mouseDown(org.eclipse.swt.events.MouseEvent)
             */
            @Override
            public void mouseDown(MouseEvent e) {

                final IndicatorUnit indicator = (IndicatorUnit) ((Label) e.getSource()).getData();
                final IndicatorOptionsWizard wizard = new IndicatorOptionsWizard(indicator, analysis);

                String helpID = HelpPlugin.PLUGIN_ID + HelpPlugin.INDICATOR_OPTION_HELP_ID;
                WizardDialog dialog = new WizardDialog(null, wizard);

                WizardDialog dialogWithHelp = HelpUtils.injectHelp(dialog, wizard, helpID, indicator);
                if (dialogWithHelp != null) {

                    if (Window.OK == dialogWithHelp.open()) {
                        setDirty(wizard.isDirty());
                    }
                }
            }

        });

        optionEditor.minimumWidth = WIDTH1_CELL;
        optionEditor.horizontalAlignment = SWT.CENTER;
        optionEditor.setEditor(optionLabel, indicatorItem, 1);
        // }

        TreeEditor delEditor = new TreeEditor(tree);
        Label delLabel = new Label(tree, SWT.NONE);
        delLabel.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
        delLabel.setImage(ImageLib.getImage(ImageLib.DELETE_ACTION));
        delLabel.setToolTipText("delete");
        delLabel.pack();
        delLabel.addMouseListener(new MouseAdapter() {

            /*
             * (non-Javadoc)
             * 
             * @see org.eclipse.swt.events.MouseAdapter#mouseDown(org.eclipse.swt.events.MouseEvent)
             */
            @Override
            public void mouseDown(MouseEvent e) {
                ColumnIndicator columnIndicator = (ColumnIndicator) treeItem.getData(COLUMN_INDICATOR_KEY);
                deleteIndicatorItems(columnIndicator, unit);
                if (indicatorItem.getParentItem() != null && indicatorItem.getParentItem().getData(INDICATOR_UNIT_KEY) != null) {
                    setElements(columnIndicators);
                } else {
                    removeItemBranch(indicatorItem);
                }
            }

        });

        delEditor.minimumWidth = WIDTH1_CELL;
        delEditor.horizontalAlignment = SWT.CENTER;
        delEditor.setEditor(delLabel, indicatorItem, 3);
        indicatorItem.setData(ITEM_EDITOR_KEY, new TreeEditor[] { optionEditor, delEditor });
        if (indicatorEnum.hasChildren()) {
            indicatorItem.setData(treeItem.getData(COLUMN_INDICATOR_KEY));
            createIndicatorItems(indicatorItem, indicatorUnit.getChildren());
        }
        IndicatorParameters parameters = indicatorUnit.getIndicator().getParameters();
        if (parameters != null) {
            indicatorItem.setData(treeItem.getData(COLUMN_INDICATOR_KEY));
            createIndicatorParameters(indicatorItem, parameters);
        }
    }

    /**
     * DOC qzhang Comment method "createIndicatorParameters".
     * 
     * @param indicatorItem
     * @param parameters
     */
    private void createIndicatorParameters(TreeItem indicatorItem, IndicatorParameters parameters) {
        TreeItem iParamItem = new TreeItem(indicatorItem, SWT.NONE);
        iParamItem.setText(0, "max results shown:" + parameters.getTopN());
        TextParameters tParameter = parameters.getTextParameter();
        if (tParameter != null) {
            final TreeItem tParamItem = new TreeItem(indicatorItem, SWT.NONE);
            tParamItem.setText(0, "Text parameters");
            TreeItem item = new TreeItem(tParamItem, SWT.NONE);
            item.setText("use blanks:" + tParameter.isUseBlank());
            item = new TreeItem(tParamItem, SWT.NONE);
            item.setText("ignore case:" + tParameter.isIgnoreCase());
            item = new TreeItem(tParamItem, SWT.NONE);
            item.setText("use nulls:" + tParameter.isUseNulls());
        }
        DateParameters dParameters = parameters.getDateParameters();
        if (dParameters != null) {
            final TreeItem tParamItem = new TreeItem(indicatorItem, SWT.NONE);
            tParamItem.setText(0, "Date parameters");
            TreeItem item = new TreeItem(tParamItem, SWT.NONE);
            item.setText("aggregation type:\"" + dParameters.getDateAggregationType().getName() + "\"");
        }

        iParamItem = new TreeItem(indicatorItem, SWT.NONE);
        iParamItem.setText(0, "has valid domain:" + (parameters.getDataValidDomain() != null));

        iParamItem = new TreeItem(indicatorItem, SWT.NONE);
        iParamItem.setText(0, "has quality thresholds:" + (parameters.getIndicatorValidDomain() != null));

        iParamItem = new TreeItem(indicatorItem, SWT.NONE);
        iParamItem.setText(0, "has bins defined:" + (parameters.getBins() != null));
        tree.getColumn(0).pack();
    }

    /**
     * DOC rli Comment method "deleteIndicatorItems".
     * 
     * @param treeItem
     * @param inidicatorUnit
     */
    private void deleteIndicatorItems(ColumnIndicator columnIndicator, IndicatorUnit inidicatorUnit) {
        columnIndicator.removeIndicatorUnit(inidicatorUnit);
    }

    /**
     * DOC rli Comment method "deleteTreeElements".
     * 
     * @param columnIndicators
     * @param deleteColumnIndiciators
     */
    private void deleteColumnItems(ColumnIndicator deleteColumnIndiciator) {
        ColumnIndicator[] remainIndicators = new ColumnIndicator[columnIndicators.length - 1];
        int i = 0;
        for (ColumnIndicator indicator : columnIndicators) {
            if (deleteColumnIndiciator.equals(indicator)) {
                continue;
            } else {
                remainIndicators[i] = indicator;
                i++;
            }
        }
        this.columnIndicators = remainIndicators;
    }

    public void openIndicatorSelectDialog(Shell shell) {
        IndicatorSelectDialog dialog = new IndicatorSelectDialog(shell, "Indicator Selection", columnIndicators);
        dialog.create();
        dialog.getShell().addShellListener(new ShellAdapter() {

            /*
             * (non-Javadoc)
             * 
             * @see org.eclipse.swt.events.ShellAdapter#shellActivated(org.eclipse.swt.events.ShellEvent)
             */
            @Override
            public void shellActivated(ShellEvent e) {
                Point point = e.widget.getDisplay().getCursorLocation();
                IContext context = HelpSystem.getContext(HelpPlugin.PLUGIN_ID + HelpPlugin.INDICATOR_SELECTOR_HELP_ID);
                PlatformUI.getWorkbench().getHelpSystem().displayContext(context, point.x + 15, point.y);
            }
        });

        if (dialog.open() == Window.OK) {
            ColumnIndicator[] result = dialog.getResult();
            for (ColumnIndicator columnIndicator : result) {
                columnIndicator.storeTempIndicator();
            }
            this.setElements(result);
            return;
        }
    }

    public ColumnIndicator[] getColumnIndicator() {
        return this.columnIndicators;
    }

    /**
     * Remove the selected elements(eg:TdColumn or Indicator) from tree.
     * 
     * @param newTree
     */
    private void removeSelectedElements(final Tree newTree) {
        TreeItem[] selection = newTree.getSelection();
        boolean branchIndicatorExist = false;
        for (TreeItem item : selection) {
            IndicatorUnit indicatorUnit = (IndicatorUnit) item.getData(INDICATOR_UNIT_KEY);
            if (indicatorUnit != null) {
                deleteIndicatorItems((ColumnIndicator) item.getData(COLUMN_INDICATOR_KEY), indicatorUnit);
            } else {
                deleteColumnItems((ColumnIndicator) item.getData(COLUMN_INDICATOR_KEY));
            }
            // if the item's parent item is a indicator item, when current indicator item removed, it's parent item
            // should be removed and recreate the tree;else,just need remove current item and it's branch.
            if (item.getParentItem() != null && item.getParentItem().getData(INDICATOR_UNIT_KEY) != null) {
                branchIndicatorExist = true;
                continue;
            } else {
                removeItemBranch(item);
            }

        }
        if (branchIndicatorExist) {
            setElements(columnIndicators);
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

    /**
     * DOC zqin AnalysisColumnTreeViewer class global comment. Detailled comment
     */
    class PatternLabelProvider extends LabelProvider {

        @Override
        public Image getImage(Object element) {

            if (element instanceof IFolder) {
                return ImageLib.getImage(ImageLib.FOLDERNODE_IMAGE);
            }

            if (element instanceof IFile) {
                return ImageLib.getImage(ImageLib.PATTERN_REG);
            }

            return null;
        }

        @Override
        public String getText(Object element) {
            if (element instanceof IFile) {
                IFile file = (IFile) element;
                Pattern pattern = PatternResourceFileHelper.getInstance().findPattern(file);
                if (pattern != null) {
                    return pattern.getName();
                }
            }

            if (element instanceof IFolder) {
                return ((IFolder) element).getName();
            }

            return "";
        }
    }

    /**
     * Getter for analysis.
     * 
     * @return the analysis
     */
    public Analysis getAnalysis() {
        return this.analysis;
    }
}
