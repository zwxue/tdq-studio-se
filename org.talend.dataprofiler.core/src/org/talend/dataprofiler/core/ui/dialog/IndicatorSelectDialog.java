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
package org.talend.dataprofiler.core.ui.dialog;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TreeEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.TypedListener;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.model.ModelElementIndicator;
import org.talend.dataprofiler.core.ui.dialog.composite.TooltipTree;
import org.talend.dataprofiler.core.ui.editor.analysis.AnalysisEditor;
import org.talend.dataprofiler.core.ui.editor.analysis.ColumnMasterDetailsPage;
import org.talend.dataprofiler.core.ui.utils.ModelElementIndicatorRule;
import org.talend.dataquality.analysis.ExecutionLanguage;
import org.talend.dataquality.helpers.MetadataHelper;
import org.talend.dataquality.indicators.DatePatternFreqIndicator;
import org.talend.dataquality.indicators.Indicator;
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
    private static final int COLI_WIDTH = 100;

    /**
     * Width of the first column.
     */
    private static final int COL0_WIDTH = 200;

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
                this.dbms = DbmsLanguageFactory.createDbmsLanguage(connection);
            }

        }
        // ~

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
                            columnIndicator.addTempIndicatorEnum(node.getIndicatorEnum());
                        }
                    } else {
                        btn.setSelection(false);
                        columnIndicator.removeTempIndicatorEnum(node.getIndicatorEnum());
                    }
                }
            }
        }

    }

    protected Control createDialogArea(Composite parent) {
        Composite comp = (Composite) super.createDialogArea(parent);
        Tree tree = new TooltipTree(comp, SWT.BORDER) {

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

        Composite buttomComp = new Composite(comp, SWT.NONE);
        buttomComp.setLayout(new GridLayout());
        buttomComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        purposeLabel = new Label(buttomComp, SWT.NULL);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(purposeLabel);
        descriptionLabel = new Label(buttomComp, SWT.NULL);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(descriptionLabel);

        return comp;
    }

    /**
     * DOC yyi 2010-05-19 select all enabled indicators on Ctrl+Shift+[A|N] is down or other case.
     * 
     * @param selected
     */
    public void selectAllIndicators(boolean selected) {
        for (Button checkButton : checkButtons) {
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
    }

    private void createTreeStructure(Tree tree) {

        TreeColumn[] treeColumns = createTreeColumns(tree);

        IIndicatorNode[] branchNodes = IndicatorTreeModelBuilder.buildIndicatorCategory();

        createChildrenNode(tree, null, treeColumns, branchNodes);
    }

    private void createChildrenNode(Tree tree, TreeItemContainer parentItem, TreeColumn[] treeColumns,
            IIndicatorNode[] branchNodes) {
        for (int i = 0; i < branchNodes.length; i++) {
            IIndicatorNode indicatorNode = branchNodes[i];
            if (!indicatorNode.getLabel().equals("")) {
                final TreeItemContainer treeItem;
                if (parentItem == null) {
                    treeItem = new TreeItemContainer(tree, SWT.NONE, treeColumns.length);
                } else {
                    treeItem = new TreeItemContainer(parentItem, SWT.NONE, treeColumns.length);
                }

                TreeEditor editor;
                Button checkButton;
                Button rowCheckButton = null;
                Button commonCheckButton;
                List<Button> rowButtonList = new ArrayList<Button>();
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

                        if (((ModelElementIndicator) treeColumns[j].getData()).contains(indicatorEnum)) {
                            checkButton.setSelection(true);
                        }
                        final ModelElementIndicator currentIndicator = (ModelElementIndicator) treeColumns[j].getData();
                        checkButton.setEnabled(ModelElementIndicatorRule.match(indicatorNode, currentIndicator, this.language));

                        // ADD yyi 2010-05-06 10494: Disable the indicator buttons which are not support MS Access
                        // Remove the language compare to support all DB type, if needed.

                        // MOD mzhao bug 10494, if there is no definition for this
                        // indicator in .talend.definition file(Neither
                        // the database type is clearly specified nor a default database
                        // type is defined), disable the indicator selection.
                        // 2010-05-06
                        if (null != indicatorNode.getIndicatorInstance()
                                && !(indicatorNode.getIndicatorInstance() instanceof DatePatternFreqIndicator)
                                && null != indicatorNode.getIndicatorInstance().getIndicatorDefinition()
                                && dbms.getSqlExpression(indicatorNode.getIndicatorInstance().getIndicatorDefinition()) == null) {
                            checkButton.setEnabled(false);
                        }
                        // ~

                        checkButton
                                .addSelectionListener(new ButtonSelectionListener(j, treeItem, indicatorEnum, currentIndicator));
                        if (indicatorEnum != null) {
                            checkButton.setToolTipText(DefaultMessagesImpl.getString(
                                    "IndicatorSelectDialog.enable", indicatorEnum.getLabel(), currentIndicator.getElementName())); //$NON-NLS-1$ //$NON-NLS-2$
                        }
                        checkButton.setData(MODELELEMENTINDICATORFLAG, currentIndicator);
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
                treeItem.setExpanded(true);
            }
        }

    }

    private TreeColumn[] createTreeColumns(Tree tree) {
        tree.setHeaderVisible(true);
        TreeColumn[] treeColumn = null;

        if (this.modelElementIndicators.length > 1) {
            treeColumn = new TreeColumn[modelElementIndicators.length + 2];
            treeColumn[0] = new TreeColumn(tree, SWT.CENTER);
            treeColumn[0].setWidth(COL0_WIDTH);
            treeColumn[0].setText(DefaultMessagesImpl.getString("IndicatorSelectDialog.indicator")); //$NON-NLS-1$
            treeColumn[1] = new TreeColumn(tree, SWT.CENTER);
            treeColumn[1].setWidth(COLI_WIDTH);
            treeColumn[1].setText(DefaultMessagesImpl.getString("IndicatorSelectDialog.allColumn")); //$NON-NLS-1$
            treeColumn[1].setToolTipText(DefaultMessagesImpl.getString("IndicatorSelectDialog.string")); //$NON-NLS-1$

            for (int i = 0; i < this.modelElementIndicators.length; i++) {
                treeColumn[i + 2] = new TreeColumn(tree, SWT.CENTER);
                treeColumn[i + 2].setWidth(COLI_WIDTH);
                treeColumn[i + 2].setText(modelElementIndicators[i].getElementName());
                treeColumn[i + 2].setData(modelElementIndicators[i]);
                treeColumn[i + 2].setToolTipText(DefaultMessagesImpl.getString("IndicatorSelectDialog.analyzeColumn")); //$NON-NLS-1$
                modelElementIndicators[i].copyOldIndicatorEnum();
            }
        } else {
            treeColumn = new TreeColumn[modelElementIndicators.length + 1];
            treeColumn[0] = new TreeColumn(tree, SWT.CENTER);
            treeColumn[0].setWidth(COL0_WIDTH);
            treeColumn[0].setText(DefaultMessagesImpl.getString("IndicatorSelectDialog.indicators")); //$NON-NLS-1$
            for (int i = 0; i < this.modelElementIndicators.length; i++) {
                treeColumn[i + 1] = new TreeColumn(tree, SWT.CENTER);
                treeColumn[i + 1].setWidth(COLI_WIDTH);
                treeColumn[i + 1].setText(modelElementIndicators[i].getElementName());
                treeColumn[i + 1].setData(modelElementIndicators[i]);
                modelElementIndicators[i].copyOldIndicatorEnum();
            }
        }

        return treeColumn;
    }

    public ModelElementIndicator[] getResult() {
        return modelElementIndicators;
    }
}
