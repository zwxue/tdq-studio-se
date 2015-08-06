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
package org.talend.dataprofiler.core.ui.dialog;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TreeEditor;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.TypedListener;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.model.DelimitedFileIndicator;
import org.talend.dataprofiler.core.model.ModelElementIndicator;
import org.talend.dataprofiler.core.ui.dialog.composite.TooltipTree;
import org.talend.dataprofiler.core.ui.editor.analysis.AnalysisEditor;
import org.talend.dataprofiler.core.ui.editor.analysis.ColumnMasterDetailsPage;
import org.talend.dataprofiler.core.ui.utils.ModelElementIndicatorRule;
import org.talend.dataquality.analysis.ExecutionLanguage;
import org.talend.dataquality.helpers.MetadataHelper;
import org.talend.dataquality.indicators.DatePatternFreqIndicator;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.PhoneNumbStatisticsIndicator;
import org.talend.dataquality.indicators.definition.IndicatorDefinition;
import org.talend.dq.dbms.DbmsLanguage;
import org.talend.dq.dbms.DbmsLanguageFactory;
import org.talend.dq.nodes.indicator.IIndicatorNode;
import org.talend.dq.nodes.indicator.IndicatorTreeModelBuilder;
import org.talend.dq.nodes.indicator.type.IndicatorEnum;
import orgomg.cwm.foundation.softwaredeployment.DataManager;

/**
 * This dialog use to select the indictor object for different columns.
 * 
 */
public class IndicatorSelectDialog extends TrayDialog {

    /**
     * Default width of the columns.
     */
    private static final int COLI_WIDTH = 90;

    /**
     * Width of the first column.
     */
    private static final int COL0_WIDTH = 288;

    private static final int ROW_MAX_LENGTH = 107;

    private static final String DESCRIPTION = DefaultMessagesImpl.getString("IndicatorSelectDialog.description"); //$NON-NLS-1$

    private static final String PURPOSE = DefaultMessagesImpl.getString("IndicatorSelectDialog.purpose"); //$NON-NLS-1$

    // private Object[] tdColumns;

    private static final String INDICATORITEM = "_INDICATORITEM"; //$NON-NLS-1$

    private static final String ROWINDICATORFLAG = "_ROWINDICATORFLAG"; //$NON-NLS-1$

    private static final String MODELELEMENTINDICATORFLAG = "_COLUMNINDICATORFLAG"; //$NON-NLS-1$

    private ModelElementIndicator[] modelElementIndicators;

    private Label purposeLabel;

    private Label descriptionLabel;

    private final String title;

    // ADD by zshen:need language to decide DatePatternFrequencyIndicator whether can be choose bu user.
    private ExecutionLanguage language;

    private DbmsLanguage dbms;

    private List<Button> checkButtons = new ArrayList<Button>();

    private int pageSize = 10;// Record how many columns will be display in every page.

    private int limitPageCount = 5;// More than 5, use the paging.

    private int allColumnsCountSize = 0;// Record the total number of Columns.

    private int currentPage = 1;// Record the current page number.

    private int totalPages = 0;// Record the total number of pages.

    private Tree tree = null;

    private Button okButton = null;

    Composite parent = null;

    Composite buttomComp = null;

    protected ImageHyperlink pageLastImgHypLnk = null;

    protected ImageHyperlink pageNextImgHypLnk = null;

    protected ImageHyperlink pagePreviouseImgHypLnk = null;

    protected ImageHyperlink pageFirstImgHypLnk = null;

    private FormToolkit toolkit;

    private Combo pageGoTo;

    private Composite pageNavComp;

    /**
     * DOC xqliu IndicatorSelectDialog constructor comment.
     * 
     * @param parentShell
     * @param title
     * @param modelElementIndicators
     */
    public IndicatorSelectDialog(Shell parentShell, String title, ModelElementIndicator[] modelElementIndicators,
            ExecutionLanguage language) {
        super(parentShell);
        this.title = title;
        this.modelElementIndicators = modelElementIndicators;
        this.language = language;
        int shellStyle = getShellStyle();
        setShellStyle(shellStyle | SWT.MAX | SWT.RESIZE);

        this.allColumnsCountSize = this.modelElementIndicators.length;
        this.totalPages = this.allColumnsCountSize / this.pageSize;
        if (this.allColumnsCountSize % this.pageSize != 0) {
            this.totalPages += 1;
        }
    }

    /**
     * DOC xqliu IndicatorSelectDialog constructor comment.
     * 
     * @param parentShell
     * @param title
     * @param modelElementIndicators
     * @deprecated
     */
    public IndicatorSelectDialog(Shell parentShell, String title, ModelElementIndicator[] modelElementIndicators) {
        this(parentShell, title, modelElementIndicators, null);
        // MOD zshen: obtain language.
        Object editorPart = CorePlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
        if (editorPart instanceof AnalysisEditor) {
            AnalysisEditor analyEditor = (AnalysisEditor) editorPart;
            if (analyEditor.getMasterPage() instanceof ColumnMasterDetailsPage) {
                this.language = ExecutionLanguage.get(((ColumnMasterDetailsPage) analyEditor.getMasterPage()).getExecCombo()
                        .getText());

                DataManager connection = ((ColumnMasterDetailsPage) analyEditor.getMasterPage()).getAnalysis().getContext()
                        .getConnection();
				// MOD qiongli 2010-11-24 bug 14579.
                if (connection == null && modelElementIndicators.length > 0) {
                    // ModelElement modleElement = modelElementIndicators[0].getModelElement();
                    // connection = ModelElementHelper.getTdDataProvider(modleElement);
                }
                this.dbms = DbmsLanguageFactory.createDbmsLanguage(connection);
            }
        }
        // ~

        for (ModelElementIndicator modelElementIndicator : getResult()) {
            modelElementIndicator.copyOldIndicatorEnum();
        }
    }

    /*
     * (non-Javadoc) Method declared in Window.
     */
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        if (title != null) {
            shell.setText(title);
        }
    }

    /**
     * @author rli
     * 
     * FIXME this inner class should be static. Confirm and fix the error.
     * 
     */
    class TreeItemContainer extends TreeItem {

        private Button[] buttons;

        private final int initialCapacity;

        public TreeItemContainer(TreeItemContainer parentItem, int style, int initialCapacity) {
            super(parentItem, style);
            setImage(ImageLib.getImage(ImageLib.IND_DEFINITION));
            this.initialCapacity = initialCapacity;
        }

        public TreeItemContainer(Tree parent, int style, int initionSize) {
            super(parent, style);
            this.initialCapacity = initionSize;
        }

        public void setButton(int index, Button button) {
            if (buttons == null) {
                buttons = new Button[initialCapacity];
            }
            buttons[index] = button;
        }

        public Button getButton(int index) {
            return buttons == null ? null : buttons[index];
        }

        public Button[] getButtons() {
            return this.buttons;
        }

        /*
         * Disable the judge of subclass.
         * 
         * @see org.eclipse.swt.widgets.TreeItem#checkSubclass()
         */
        protected void checkSubclass() {
        }
    }

    /**
     * DOC rli IndicatorSelectDialog class global comment. Detailled comment.
     */
    private class ButtonSelectionListener extends SelectionAdapter {

        protected final int index;

        protected final TreeItemContainer treeItemContainer;

        protected final IndicatorEnum indicatorEnum;

        protected final ModelElementIndicator currentIndicator;

        protected ButtonSelectionListener(int index, TreeItemContainer treeItemContainer, IndicatorEnum indicatorEnum,
                ModelElementIndicator currentIndicator) {
            this.index = index;
            this.treeItemContainer = treeItemContainer;
            this.indicatorEnum = indicatorEnum;
            this.currentIndicator = currentIndicator;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
         */
        public void widgetSelected(SelectionEvent e) {
            boolean selection = ((Button) e.getSource()).getSelection();
            Button itemButton;
            if (selection) {
                itemButton = treeItemContainer.getButton(index);
                if (itemButton.isEnabled() && indicatorEnum != null) {
                    currentIndicator.addTempIndicatorEnum(indicatorEnum);
                }

            } else {
                currentIndicator.removeTempIndicatorEnum(indicatorEnum);
            }
            processParentSelection(treeItemContainer, selection);
            processChildrenSelection(treeItemContainer, index, selection);
        }

        /**
         * handle the parent button selection..
         * 
         * @param selection
         */
        protected void processParentSelection(TreeItemContainer treeItem, boolean selection) {
            TreeItem parentItem = treeItem.getParentItem();
            if (parentItem != null && selection) {
                boolean allSelection = true;
                for (TreeItem item : parentItem.getItems()) {
                    allSelection = ((TreeItemContainer) item).getButton(index).getSelection();
                    if (!allSelection) {
                        return;
                    }
                }
                Button parentItemButton = ((TreeItemContainer) parentItem).getButton(index);
                parentItemButton.setSelection(selection);
                IndicatorEnum enumData = ((IIndicatorNode) parentItemButton.getData()).getIndicatorEnum();
                if (enumData != null) {
                    currentIndicator.addTempIndicatorEnum(enumData);
                }
                processParentSelection((TreeItemContainer) parentItem, selection);
            } else if (parentItem != null && !selection) {
                Button parentItemButton = ((TreeItemContainer) parentItem).getButton(index);
                parentItemButton.setSelection(selection);
                currentIndicator.removeTempIndicatorEnum(((IIndicatorNode) parentItemButton.getData()).getIndicatorEnum());
                processParentSelection((TreeItemContainer) parentItem, selection);
            }
        }

        /**
         * handle the children button selection.
         */
        protected void processChildrenSelection(final TreeItemContainer treeItem, final int idx, boolean selection) {
            Button itemButton;
            for (TreeItem childItem : treeItem.getItems()) {
                itemButton = ((TreeItemContainer) childItem).getButton(idx);
                if (itemButton.isEnabled()) {
                    itemButton.setSelection(selection);
                } else {
                    continue;
                }
                if (selection) {
                    currentIndicator.addTempIndicatorEnum(((IIndicatorNode) itemButton.getData()).getIndicatorEnum());

                } else {
                    currentIndicator.removeTempIndicatorEnum(((IIndicatorNode) itemButton.getData()).getIndicatorEnum());
                }

                processChildrenSelection((TreeItemContainer) childItem, idx, selection);
            }
        }
    }

    /**
     * DOC zqin IndicatorSelectDialog class global comment. Detailled comment
     */
    private class RowSelectButtonListener extends ButtonSelectionListener {

        protected RowSelectButtonListener(int index, TreeItemContainer treeItemContainer, IndicatorEnum indicatorEnum,
                ModelElementIndicator currentColumnIndicator) {
            super(index, treeItemContainer, indicatorEnum, currentColumnIndicator);
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void processChildrenSelection(TreeItemContainer treeItem, int idx, boolean selection) {
            Button itemButton;
            for (TreeItem childItem : treeItem.getItems()) {
                TreeItemContainer childItemContainer = (TreeItemContainer) childItem;
                itemButton = childItemContainer.getButton(idx);
                if (itemButton.isEnabled()) {
                    itemButton.setSelection(selection);
                } else {
                    return;
                }
                processRowButtonSelect(selection, (List<Button>) itemButton.getData(ROWINDICATORFLAG));
                processChildrenSelection((TreeItemContainer) childItem, idx, selection);
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        public void widgetSelected(SelectionEvent e) {
            boolean selection = ((Button) e.getSource()).getSelection();
            Button itemButton = treeItemContainer.getButton(index);

            processRowButtonSelect(selection, (List<Button>) itemButton.getData(ROWINDICATORFLAG));
            processChildrenSelection(treeItemContainer, index, selection);
        }

        private void processRowButtonSelect(boolean selection, List<Button> rowButtons) {
            for (Button btn : rowButtons) {
                if (btn.isEnabled()) {
                    ModelElementIndicator columnIndicator = (ModelElementIndicator) btn.getData(MODELELEMENTINDICATORFLAG);
                    IIndicatorNode node = (IIndicatorNode) btn.getData();
                    IndicatorEnum indicEnum = node.getIndicatorEnum();

                    if (selection && ModelElementIndicatorRule.match(node, columnIndicator, language)) {
                        btn.setSelection(true);
                        if (indicEnum != null) {
                            addTempIndicatorForPages(node, indicEnum);
                        }
                    } else {
                        btn.setSelection(false);
                        removeTempIndicatorForPages(indicEnum);
                    }
                }
            }
        }

    }

    private void addTempIndicatorForPages(IIndicatorNode node, IndicatorEnum indicator) {
        for (int i = 0; i < this.allColumnsCountSize; i++) {
            if (isMatchCurrentIndicator(modelElementIndicators[i], node)) {
                modelElementIndicators[i].addTempIndicatorEnum(indicator);
            }
        }
    }

    private void removeTempIndicatorForPages(IndicatorEnum indicator) {
        for (int i = 0; i < this.allColumnsCountSize; i++) {
            modelElementIndicators[i].removeTempIndicatorEnum(indicator);
        }
    }

    protected Control createDialogArea(Composite parent) {
        Composite comp = (Composite) super.createDialogArea(parent);
        this.parent = comp;
        initializationTree(comp);
        return comp;
    }

    private void initializationTree(Composite comp) {
        this.tree = new TooltipTree(comp, SWT.BORDER) {

            // protected boolean isValidItem(TreeItem item) {
            // return (item != null) && (item.getData(INDICATORITEM) != null);
            // }

            protected String getItemTooltipText(TreeItem item) {
                if (item.getData(INDICATORITEM) == null) {
                    return item.getText();
                }
                IndicatorDefinition indicatorDefinition = ((IIndicatorNode) item.getData(INDICATORITEM)).getIndicatorInstance()
                        .getIndicatorDefinition();
                String description = MetadataHelper.getDescription(indicatorDefinition);
                return description.equals(PluginConstant.EMPTY_STRING) ? item.getText() : description;
            }

        };
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(tree);
        ((GridData) tree.getLayoutData()).widthHint = 650;
        ((GridData) tree.getLayoutData()).heightHint = 380;
        createTreeStructure(tree);
        tree.setLinesVisible(true);
        tree.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                if (null == e.item) {
                    return;
                }
                IIndicatorNode indicatorNode = ((IIndicatorNode) e.item.getData(INDICATORITEM));
                if (indicatorNode == null) {
                    purposeLabel.setText(PURPOSE + ((TreeItem) e.item).getText());
                    descriptionLabel.setText(DESCRIPTION + ((TreeItem) e.item).getText());
                    return;
                }
                Indicator indicator = indicatorNode.getIndicatorInstance();
                IndicatorDefinition indicatorDefinition = indicator.getIndicatorDefinition();
                purposeLabel.setText(PURPOSE + MetadataHelper.getPurpose(indicatorDefinition));
                String description = DESCRIPTION + MetadataHelper.getDescription(indicatorDefinition);
                description = splitLongString(description);
                descriptionLabel.setText(description);
            }

            private String splitLongString(String longString) {
                if (longString.length() > ROW_MAX_LENGTH) {
                    char space = ' ';
                    for (int i = ROW_MAX_LENGTH; i > 0; i--) {
                        if (longString.charAt(i) == space) {
                            String substring1 = longString.substring(0, i);
                            String lineSeparator = System.getProperty("line.separator", "\n"); //$NON-NLS-1$ //$NON-NLS-2$
                            String substring2 = longString.substring(i, longString.length());
                            substring2 = splitLongString(substring2);
                            longString = substring1 + lineSeparator + substring2;
                            break;
                        }
                    }

                }
                return longString;
            }

        });
        tree.pack();

        this.buttomComp = new Composite(comp, SWT.NONE);
        buttomComp.setLayout(new GridLayout());
        buttomComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        purposeLabel = new Label(buttomComp, SWT.NULL);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(purposeLabel);
        descriptionLabel = new Label(buttomComp, SWT.NULL);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(descriptionLabel);

    }

    /**
     * DOC yyi 2010-05-19 select all enabled indicators on Ctrl+Shift+[A|N] is down or other case.
     * 
     * @param selected
     */
    public void selectAllIndicators(boolean selected) {
        for (Button checkButton : checkButtons) {
            if (checkButton.isDisposed()) {
                continue;
            }
            if (checkButton.isEnabled() && selected != checkButton.getSelection()) {
                checkButton.setSelection(selected);

                Listener[] listeners = checkButton.getListeners(SWT.Selection);
                if (listeners.length > 0) {
                    TypedListener typedListener = (TypedListener) checkButton.getListeners(SWT.Selection)[0];
                    if (typedListener.getEventListener() instanceof ButtonSelectionListener) {
                        ButtonSelectionListener listener = (ButtonSelectionListener) typedListener.getEventListener();
                        Event e = new Event();
                        e.widget = checkButton;
                        listener.widgetSelected(new SelectionEvent(e));
                    }
                }
            }
        }

        if (isUsePaging()) {
            selectPagesAllIndicator(selected);
        }
    }

    private void selectPagesAllIndicator(boolean selected) {
        if (selected) {
            IIndicatorNode[] branchNodes = IndicatorTreeModelBuilder.buildIndicatorCategory();
            for (ModelElementIndicator modelEleIndi : getResult()) {
                List<IndicatorEnum> tempIndicator = modelEleIndi.getTempIndicator();
                if (null != tempIndicator) {
                    tempIndicator.clear();
                }
                for (IIndicatorNode iIndicatorNode : branchNodes) {
                    addModelElementIndicator(modelEleIndi, iIndicatorNode);
                }
            }
        } else {
            for (ModelElementIndicator modelEleIndi : getResult()) {
                modelEleIndi.getTempIndicator().clear();
            }
        }
    }

    private void addModelElementIndicator(ModelElementIndicator modelEleIndi, IIndicatorNode branchNode) {
        if (isMatchCurrentIndicator(modelEleIndi, branchNode)) {
            modelEleIndi.getTempIndicator().add(branchNode.getIndicatorEnum());
        }
        if (branchNode.hasChildren()) {
            for (IIndicatorNode chilrenNode : branchNode.getChildren()) {
                addModelElementIndicator(modelEleIndi, chilrenNode);
            }
        }
    }

    private boolean isMatchCurrentIndicator(ModelElementIndicator currentIndicator, IIndicatorNode indicatorNode) {
        boolean returnCurrentIndicator = true;
        IIndicatorNode parentNode = indicatorNode.getParent();
        boolean isParentPhoneStatistics = parentNode != null && parentNode.getIndicatorInstance() != null
                && parentNode.getIndicatorInstance() instanceof PhoneNumbStatisticsIndicator;
        if (!ModelElementIndicatorRule.match(indicatorNode, currentIndicator, this.language)) {
            returnCurrentIndicator = false;
        }
        if (null != indicatorNode.getIndicatorInstance()
                && !(indicatorNode.getIndicatorInstance() instanceof DatePatternFreqIndicator)
                && null != indicatorNode.getIndicatorInstance().getIndicatorDefinition()
                && indicatorNode.getIndicatorInstance().getIndicatorDefinition().getSqlGenericExpression().size() < 1
                && !indicatorNode.hasChildren() && !(currentIndicator instanceof DelimitedFileIndicator)
                && !isParentPhoneStatistics) {
            returnCurrentIndicator = false;
        }
        return returnCurrentIndicator;
    }

    private void createTreeStructure(Tree tree) {

        TreeColumn[] treeColumns = createTreeColumns(tree);

        IIndicatorNode[] branchNodes = IndicatorTreeModelBuilder.buildIndicatorCategory();

        createChildrenNode(tree, null, treeColumns, branchNodes);
    }

    private void createChildrenNode(Tree tree, TreeItemContainer parentItem, TreeColumn[] treeColumns,
            IIndicatorNode[] branchNodes) {

        boolean expanded = false;
        for (int i = 0; i < branchNodes.length; i++) {
            IIndicatorNode indicatorNode = branchNodes[i];
            if (!indicatorNode.getLabel().equals("")) {//$NON-NLS-1$ 
                final TreeItemContainer treeItem;
                if (parentItem == null) {
                    treeItem = new TreeItemContainer(tree, SWT.NONE, treeColumns.length);
                    tree.setFocus();
                } else {
                    treeItem = new TreeItemContainer(parentItem, SWT.NONE, treeColumns.length);
                }

                TreeEditor editor;
                Button checkButton;
                Button rowCheckButton = null;
                Button commonCheckButton;
                List<Button> rowButtonList = new ArrayList<Button>();
                // MOD qiongli 2011-7-22 feature 22362
                // IIndicatorNode parentNode = indicatorNode.getParent();
                // boolean isParentPhoneStatistics = parentNode != null && parentNode.getIndicatorInstance() != null
                // && parentNode.getIndicatorInstance() instanceof PhoneNumbStatisticsIndicator;
                for (int j = 0; j < treeColumns.length; j++) {
                    IndicatorEnum indicatorEnum = indicatorNode.getIndicatorEnum();
                    if (j == 0) {
                        treeItem.setText(0, indicatorNode.getLabel());
                        if (indicatorEnum != null) {
                            treeItem.setData(INDICATORITEM, indicatorNode);
                        }
                        continue;
                    } else if (j == 1 && treeColumns.length > 2) {
                        editor = new TreeEditor(tree);
                        rowCheckButton = new Button(tree, SWT.CHECK);
                        rowCheckButton.addSelectionListener(new RowSelectButtonListener(j, treeItem, indicatorEnum, null));
                        checkButtons.add(rowCheckButton);
                        // set background color to the "All columns" column
                        Color systemColor = tree.getDisplay().getSystemColor(SWT.COLOR_YELLOW);
                        treeItem.setBackground(j, systemColor); // no need to free this color
                        rowCheckButton.setBackground(systemColor);

                        commonCheckButton = rowCheckButton;

                    } else {
                        editor = new TreeEditor(tree);
                        checkButton = new Button(tree, SWT.CHECK);
                        checkButton.setData(indicatorNode);

                        ModelElementIndicator pageIndicator = null;

                        if (isUsePaging()) {
                            int currentModelElement = this.pageSize * (this.currentPage - 1) + j - 2;
                            pageIndicator = getResult()[currentModelElement];
                        } else {
                            pageIndicator = (ModelElementIndicator) treeColumns[j].getData();
                        }
                        

                        boolean isMatch = isMatchCurrentIndicator(pageIndicator, indicatorNode);
                        if (null != pageIndicator && pageIndicator.tempContains(indicatorEnum)) {
                            if (isMatch) {
                                checkButton.setSelection(true);
                                expanded = true;
                            }
                        }

                        checkButton.setEnabled(isMatch);

                        // ADD yyi 2010-05-06 10494: Disable the indicator buttons which are not support MS Access
                        // Remove the language compare to support all DB type, if needed.

                        // MOD mzhao bug 10494, if there is no definition for this
                        // indicator in .talend.definition file(Neither
                        // the database type is clearly specified nor a default database
                        // type is defined), disable the indicator selection.
                        // 2010-05-06
                        // if (null != indicatorNode.getIndicatorInstance()
                        // && !(indicatorNode.getIndicatorInstance() instanceof DatePatternFreqIndicator)
                        // && null != indicatorNode.getIndicatorInstance().getIndicatorDefinition()
                        // // MOD zshen 2011.06.01 make indicator can be select although can not found one
                        // // expression for the database on hte definition.
                        // &&
                        // indicatorNode.getIndicatorInstance().getIndicatorDefinition().getSqlGenericExpression().size()
                        // < 1
                        // && !indicatorNode.hasChildren() && !(currentIndicator instanceof DelimitedFileIndicator)
                        // && !isParentPhoneStatistics) {
                        // checkButton.setEnabled(false);
                        // }
                        // ~

                        checkButton.addSelectionListener(new ButtonSelectionListener(j, treeItem, indicatorEnum, pageIndicator));
                        if (indicatorEnum != null) {
                            checkButton.setToolTipText(DefaultMessagesImpl.getString(
                                    "IndicatorSelectDialog.enable", indicatorEnum.getLabel(), pageIndicator.getElementName())); //$NON-NLS-1$
                        }
                        checkButton.setData(MODELELEMENTINDICATORFLAG, pageIndicator);
                        commonCheckButton = checkButton;

                        rowButtonList.add(checkButton);
                        checkButtons.add(checkButton);
                    }

                    commonCheckButton.pack();
                    editor.minimumWidth = commonCheckButton.getSize().x;
                    editor.horizontalAlignment = SWT.CENTER;
                    editor.setEditor(commonCheckButton, treeItem, j);

                    treeItem.setButton(j, commonCheckButton);
                }

                if (rowCheckButton != null) {
                    boolean allChecked = true;
                    rowCheckButton.setData(ROWINDICATORFLAG, rowButtonList);
                    for (Button btn : rowButtonList) {
                        if (!btn.getSelection()) {
                            allChecked = false;
                        }
                    }
                    rowCheckButton.setSelection(allChecked);
                }

                if (indicatorNode.hasChildren()) {
                    createChildrenNode(tree, treeItem, treeColumns, indicatorNode.getChildren());
                }
                if (expanded) {
                    TreeItem item = treeItem.getParentItem();
                    while (null != item) {
                        item.setExpanded(true);
                        item = item.getParentItem();
                    }
                }
            }
        }
    }

    private TreeColumn[] createTreeColumns(Tree tree) {
        tree.setHeaderVisible(true);
        TreeColumn[] treeColumn = null;

        if (this.allColumnsCountSize > 1 && !isUsePaging()) {
            treeColumn = new TreeColumn[allColumnsCountSize + 2];
            treeColumn[0] = new TreeColumn(tree, SWT.CENTER);
            treeColumn[0].setWidth(COL0_WIDTH);
            treeColumn[0].setText(DefaultMessagesImpl.getString("IndicatorSelectDialog.indicator")); //$NON-NLS-1$
            treeColumn[1] = new TreeColumn(tree, SWT.CENTER);
            treeColumn[1].setWidth(COLI_WIDTH);
            treeColumn[1].setText(DefaultMessagesImpl.getString("IndicatorSelectDialog.allColumn")); //$NON-NLS-1$
            treeColumn[1].setToolTipText(DefaultMessagesImpl.getString("IndicatorSelectDialog.string")); //$NON-NLS-1$

            for (int i = 0; i < this.allColumnsCountSize; i++) {
                treeColumn[i + 2] = new TreeColumn(tree, SWT.CENTER);
                treeColumn[i + 2].setWidth(COLI_WIDTH);
                treeColumn[i + 2].setText(getResult()[i].getElementName());
                treeColumn[i + 2].setData(getResult()[i]);
                treeColumn[i + 2].setToolTipText(DefaultMessagesImpl.getString("IndicatorSelectDialog.analyzeColumn")); //$NON-NLS-1$
                // getResult()[i].copyOldIndicatorEnum();
            }
        } else if (this.allColumnsCountSize > 1 && isUsePaging()) {
            int treeColumnSize = 0;
            if (this.currentPage != this.totalPages) {
                treeColumnSize = this.pageSize;
            } else {
                treeColumnSize = this.allColumnsCountSize - (this.currentPage - 1) * this.pageSize;
            }
            treeColumn = new TreeColumn[treeColumnSize + 2];
            treeColumn[0] = new TreeColumn(tree, SWT.CENTER);
            treeColumn[0].setWidth(COL0_WIDTH);
            treeColumn[0].setText(DefaultMessagesImpl.getString("IndicatorSelectDialog.indicator")); //$NON-NLS-1$
            treeColumn[1] = new TreeColumn(tree, SWT.CENTER);
            treeColumn[1].setWidth(COLI_WIDTH);
            treeColumn[1].setText(DefaultMessagesImpl.getString("IndicatorSelectDialog.allColumn")); //$NON-NLS-1$
            treeColumn[1].setToolTipText(DefaultMessagesImpl.getString("IndicatorSelectDialog.string")); //$NON-NLS-1$

            for (int i = 0; i < treeColumnSize; i++) {
                treeColumn[i + 2] = new TreeColumn(tree, SWT.CENTER);
                treeColumn[i + 2].setWidth(COLI_WIDTH);
                treeColumn[i + 2].setText(getResult()[(this.currentPage - 1) * this.pageSize + i].getElementName());
                treeColumn[i + 2].setData(getResult()[(this.currentPage - 1) * this.pageSize + i]);
                treeColumn[i + 2].setToolTipText(DefaultMessagesImpl.getString("IndicatorSelectDialog.analyzeColumn")); //$NON-NLS-1$
                ModelElementIndicator modelElementIndicator = getResult()[(this.currentPage - 1) * this.pageSize + i];
                List<IndicatorEnum> tempIndicator = modelElementIndicator.getTempIndicator();
               // if (null != tempIndicator && tempIndicator.isEmpty()) {
                    // modelElementIndicator.copyOldIndicatorEnum();
                //}
            }
        } else {
            treeColumn = new TreeColumn[allColumnsCountSize + 1];
            treeColumn[0] = new TreeColumn(tree, SWT.CENTER);
            treeColumn[0].setWidth(COL0_WIDTH);
            treeColumn[0].setText(DefaultMessagesImpl.getString("IndicatorSelectDialog.indicators")); //$NON-NLS-1$
            for (int i = 0; i < this.allColumnsCountSize; i++) {
                treeColumn[i + 1] = new TreeColumn(tree, SWT.CENTER);
                treeColumn[i + 1].setWidth(COLI_WIDTH);
                treeColumn[i + 1].setText(getResult()[i].getElementName());
                treeColumn[i + 1].setData(getResult()[i]);
                // getResult()[i].copyOldIndicatorEnum();
            }
        }
        return treeColumn;
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        parent.setLayout(new GridLayout(4, false));
        if (isUsePaging()) {
            pageGoTo = new Combo(parent, SWT.READ_ONLY);
            createNavComposite(parent);
            okButton = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
            createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
            initializationPagesStatus();
            initializationMonitor();
        } else {
            okButton = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
            createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
        }
    }

    private void initializationPagesStatus() {
        for (int i = 1; i <= this.totalPages; i++) {
            pageGoTo.add(i + " / " + this.totalPages, i - 1);//$NON-NLS-1$
        }
        pageGoTo.select(0);
        okButton.setEnabled(false);
        pageFirstImgHypLnk.setEnabled(false);
        pagePreviouseImgHypLnk.setEnabled(false);
    }

    private void initializationMonitor() {
        pageFirstImgHypLnk.addMouseListener(new org.eclipse.swt.events.MouseAdapter() {

            @Override
            public void mouseUp(MouseEvent e) {
                if (currentPage != 1) {
                    currentPage = 1;
                    tree.dispose();
                    buttomComp.dispose();
                    if (IndicatorSelectDialog.this.parent != null) {
                        initializationTree(IndicatorSelectDialog.this.parent);
                        IndicatorSelectDialog.this.parent.layout();
                    }
                    pageFirstImgHypLnk.setEnabled(false);
                    pagePreviouseImgHypLnk.setEnabled(false);
                    pageNextImgHypLnk.setEnabled(true);
                    pageLastImgHypLnk.setEnabled(true);
                    okButton.setEnabled(false);
                    pageGoTo.select(currentPage - 1);
                }
            }
        });

        pagePreviouseImgHypLnk.addMouseListener(new org.eclipse.swt.events.MouseAdapter() {

            @Override
            public void mouseUp(MouseEvent e) {
                if (currentPage > 1) {
                    currentPage--;
                    tree.dispose();
                    buttomComp.dispose();
                    if (IndicatorSelectDialog.this.parent != null) {
                        initializationTree(IndicatorSelectDialog.this.parent);
                        IndicatorSelectDialog.this.parent.layout();
                    }
                    pageNextImgHypLnk.setEnabled(true);
                    pageLastImgHypLnk.setEnabled(true);
                    okButton.setEnabled(false);
                    if (currentPage <= 1) {
                        pageFirstImgHypLnk.setEnabled(false);
                        pagePreviouseImgHypLnk.setEnabled(false);
                    }
                    pageGoTo.select(currentPage - 1);
                }
            }
        });

        pageNextImgHypLnk.addMouseListener(new org.eclipse.swt.events.MouseAdapter() {

            @Override
            public void mouseUp(MouseEvent e) {
                if (currentPage < totalPages) {
                    currentPage++;
                    tree.dispose();
                    buttomComp.dispose();
                    if (IndicatorSelectDialog.this.parent != null) {
                        initializationTree(IndicatorSelectDialog.this.parent);
                        IndicatorSelectDialog.this.parent.layout();
                    }
                    pageFirstImgHypLnk.setEnabled(true);
                    pagePreviouseImgHypLnk.setEnabled(true);
                    if (currentPage >= totalPages) {
                        okButton.setEnabled(true);
                        pageNextImgHypLnk.setEnabled(false);
                        pageLastImgHypLnk.setEnabled(false);
                    }
                    pageGoTo.select(currentPage - 1);
                }
            }
        });

        pageLastImgHypLnk.addMouseListener(new org.eclipse.swt.events.MouseAdapter() {

            @Override
            public void mouseUp(MouseEvent e) {
                if (currentPage != totalPages) {
                    currentPage = totalPages;
                    tree.dispose();
                    buttomComp.dispose();
                    if (IndicatorSelectDialog.this.parent != null) {
                        initializationTree(IndicatorSelectDialog.this.parent);
                        IndicatorSelectDialog.this.parent.layout();
                    }
                    pageFirstImgHypLnk.setEnabled(true);
                    pagePreviouseImgHypLnk.setEnabled(true);
                    pageNextImgHypLnk.setEnabled(false);
                    pageLastImgHypLnk.setEnabled(false);
                    okButton.setEnabled(true);
                    pageGoTo.select(currentPage - 1);
                }
            }
        });

        pageGoTo.addSelectionListener(new SelectionAdapter() {

            /*
             * (non-Javadoc)
             * 
             * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
             */
            @Override
            public void widgetSelected(SelectionEvent e) {
                int toPage = pageGoTo.getSelectionIndex() + 1;
                currentPage = toPage;
                tree.dispose();
                buttomComp.dispose();
                if (IndicatorSelectDialog.this.parent != null) {
                    initializationTree(IndicatorSelectDialog.this.parent);
                    IndicatorSelectDialog.this.parent.layout();
                }

                if (currentPage == 1) {
                    pageFirstImgHypLnk.setEnabled(false);
                    pagePreviouseImgHypLnk.setEnabled(false);
                    pageNextImgHypLnk.setEnabled(true);
                    pageLastImgHypLnk.setEnabled(true);
                    okButton.setEnabled(false);
                } else if (currentPage == totalPages) {
                    pageFirstImgHypLnk.setEnabled(true);
                    pagePreviouseImgHypLnk.setEnabled(true);
                    pageNextImgHypLnk.setEnabled(false);
                    pageLastImgHypLnk.setEnabled(false);
                    okButton.setEnabled(true);
                } else {
                    pageFirstImgHypLnk.setEnabled(true);
                    pagePreviouseImgHypLnk.setEnabled(true);
                    pageNextImgHypLnk.setEnabled(true);
                    pageLastImgHypLnk.setEnabled(true);
                    okButton.setEnabled(false);
                }
            }
        });
    }

    /**
     * Add frist/previous/next/last button.
     * 
     * DOC gdbu Comment method "createNavComposite".
     * 
     * @param searchMainComp
     */
    private void createNavComposite(Composite searchMainComp) {
        toolkit = new FormToolkit(searchMainComp.getDisplay());
        pageNavComp = toolkit.createComposite(searchMainComp, SWT.NONE);
        pageNavComp.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
        final GridData pageNavCompGD = new GridData(SWT.FILL, SWT.CENTER, true, false);
        pageNavCompGD.heightHint = 25;
        pageNavCompGD.minimumWidth = 0;
        pageNavComp.setLayoutData(pageNavCompGD);
        pageNavComp.setLayout(new FormLayout());
        toolkit.paintBordersFor(pageNavComp);

        pageLastImgHypLnk = toolkit.createImageHyperlink(pageNavComp, SWT.NONE);
        pageLastImgHypLnk.setImage(ImageLib.getImage(ImageLib.ICON_PAGE_LAST_LNK));
        pageLastImgHypLnk.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
        final FormData fdImageHyperlink = new FormData();
        fdImageHyperlink.bottom = new FormAttachment(100, 0);
        fdImageHyperlink.top = new FormAttachment(0, 5);
        fdImageHyperlink.right = new FormAttachment(100, -10);
        fdImageHyperlink.width = 50;
        pageLastImgHypLnk.setLayoutData(fdImageHyperlink);

        pageNextImgHypLnk = toolkit.createImageHyperlink(pageNavComp, SWT.NONE);
        pageNextImgHypLnk.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
        pageNextImgHypLnk.setImage(ImageLib.getImage(ImageLib.ICON_PAGE_NEXT_LNK));
        final FormData pgNextImgFD = new FormData();
        pgNextImgFD.right = new FormAttachment(pageLastImgHypLnk, -10, SWT.LEFT);
        pgNextImgFD.bottom = new FormAttachment(pageLastImgHypLnk, 0, SWT.BOTTOM);
        pgNextImgFD.top = new FormAttachment(pageLastImgHypLnk, 0, SWT.TOP);
        pgNextImgFD.width = 50;
        pageNextImgHypLnk.setLayoutData(pgNextImgFD);

        pagePreviouseImgHypLnk = toolkit.createImageHyperlink(pageNavComp, SWT.NONE);
        pagePreviouseImgHypLnk.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
        pagePreviouseImgHypLnk.setImage(ImageLib.getImage(ImageLib.ICON_PAGE_PREV_LNK));
        final FormData pgPreImgFD = new FormData();
        pgPreImgFD.right = new FormAttachment(pageNextImgHypLnk, -10, SWT.LEFT);
        pgPreImgFD.bottom = new FormAttachment(pageNextImgHypLnk, 0, SWT.BOTTOM);
        pgPreImgFD.top = new FormAttachment(pageNextImgHypLnk, 0, SWT.TOP);
        pgPreImgFD.width = 50;
        pagePreviouseImgHypLnk.setLayoutData(pgPreImgFD);

        pageFirstImgHypLnk = toolkit.createImageHyperlink(pageNavComp, SWT.NONE);
        pageFirstImgHypLnk.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
        pageFirstImgHypLnk.setImage(ImageLib.getImage(ImageLib.ICON_PAGE_FIRST_LNK));
        final FormData pgFirImgFD = new FormData();
        pgFirImgFD.right = new FormAttachment(pagePreviouseImgHypLnk, -10, SWT.LEFT);
        pgFirImgFD.bottom = new FormAttachment(pagePreviouseImgHypLnk, 0, SWT.BOTTOM);
        pgFirImgFD.top = new FormAttachment(pagePreviouseImgHypLnk, 0, SWT.TOP);
        pgFirImgFD.width = 50;
        pageFirstImgHypLnk.setLayoutData(pgFirImgFD);

    }

    private boolean isUsePaging() {
        if (this.totalPages >= this.limitPageCount) {
            return true;
        } else {
            return false;
        }
    }

    public ModelElementIndicator[] getResult() {
        return modelElementIndicators;
    }
}
