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
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.help.HelpSystem;
import org.eclipse.help.IContext;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.TreeEditor;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.TreeAdapter;
import org.eclipse.swt.events.TreeEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.CheckedTreeSelectionDialog;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.cwm.db.connection.ConnectionUtils;
import org.talend.cwm.helper.ColumnSetHelper;
import org.talend.cwm.helper.ModelElementHelper;
import org.talend.cwm.helper.XmlElementHelper;
import org.talend.cwm.relational.TdTable;
import org.talend.cwm.xml.TdXmlElementType;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.helper.ModelElementIndicatorHelper;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.manager.DQPreferenceManager;
import org.talend.dataprofiler.core.model.ColumnIndicator;
import org.talend.dataprofiler.core.model.ModelElementIndicator;
import org.talend.dataprofiler.core.model.XmlElementIndicator;
import org.talend.dataprofiler.core.ui.dialog.IndicatorSelectDialog;
import org.talend.dataprofiler.core.ui.dialog.composite.TooltipTree;
import org.talend.dataprofiler.core.ui.editor.AbstractAnalysisActionHandler;
import org.talend.dataprofiler.core.ui.editor.AbstractMetadataFormPage;
import org.talend.dataprofiler.core.ui.editor.analysis.ColumnMasterDetailsPage;
import org.talend.dataprofiler.core.ui.editor.preview.IndicatorUnit;
import org.talend.dataprofiler.core.ui.utils.MessageUI;
import org.talend.dataprofiler.core.ui.utils.UDIUtils;
import org.talend.dataprofiler.core.ui.views.ColumnViewerDND;
import org.talend.dataprofiler.help.HelpPlugin;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.analysis.ExecutionLanguage;
import org.talend.dataquality.helpers.MetadataHelper;
import org.talend.dataquality.indicators.CompositeIndicator;
import org.talend.dataquality.indicators.DataminingType;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dq.dbms.DbmsLanguage;
import org.talend.dq.dbms.DbmsLanguageFactory;
import org.talend.dq.helper.ProxyRepositoryViewObject;
import org.talend.resource.ResourceManager;
import org.talend.utils.sugars.TypedReturnCode;
import orgomg.cwm.foundation.softwaredeployment.DataManager;
import orgomg.cwm.objectmodel.core.Expression;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * @author rli
 * 
 */
public class AnalysisColumnTreeViewer extends AbstractColumnDropTree {

    protected static Logger log = Logger.getLogger(AnalysisColumnTreeViewer.class);

    public static final String VIEWER_KEY = "org.talend.dataprofiler.core.ui.editor.composite.AnasisColumnTreeViewer"; //$NON-NLS-1$

    private Composite parentComp;

    private ColumnMasterDetailsPage masterPage;

    // ADD xqliu 2009-08-24 bug 8776
    private ExecutionLanguage language;

    private Composite buttonsComp;

    public ExecutionLanguage getLanguage() {
        return language;
    }

    public void setLanguage(ExecutionLanguage language) {
        this.language = language;
    }

    public AnalysisColumnTreeViewer(Composite parent, ColumnMasterDetailsPage masterPage) {
        this.viewKey = VIEWER_KEY;
        absMasterPage = masterPage;
        parentComp = parent;
        this.tree = createTree(parent);
        this.masterPage = masterPage;
        this.setElements(masterPage.getCurrentModelElementIndicators());
        this.createUpDownButtons(parent);
        this.setDirty(false);
    }

    /**
     * @param parent
     */
    private Tree createTree(Composite parent) {
        final Tree newTree = new TooltipTree(parent, SWT.SINGLE | SWT.BORDER | SWT.FULL_SELECTION) {

            @Override
            protected boolean isValidItem(TreeItem item) {
                if (item == null) {
                    return false;
                }
                Object itemData = item.getData(INDICATOR_UNIT_KEY);
                if (itemData != null) {
                    IndicatorUnit indicatorUnit = (IndicatorUnit) item.getData(INDICATOR_UNIT_KEY);
                    if (indicatorUnit != null && !(indicatorUnit.getIndicator() instanceof CompositeIndicator)) {
                        return true;
                    }
                    return false;
                } else {
                    return false;
                }
            }

            protected String getItemTooltipText(TreeItem item) {
                String expCont = isExpressionNull(item);
                if (expCont == null) {
                    return DefaultMessagesImpl.getString("AnalysisColumnTreeViewer.queryNotGen"); //$NON-NLS-1$
                }
                return expCont;
            }
        };
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(newTree);

        newTree.setHeaderVisible(true);
        TreeColumn column1 = new TreeColumn(newTree, SWT.CENTER);
        column1.setWidth(190);
        column1.setText(DefaultMessagesImpl.getString("AnalysisColumnTreeViewer.analyzedColumns")); //$NON-NLS-1$
        TreeColumn column2 = new TreeColumn(newTree, SWT.CENTER);
        column2.setWidth(120);
        column2.setText(DefaultMessagesImpl.getString("AnalysisColumnTreeViewer.dataminingType")); //$NON-NLS-1$
        column2.setToolTipText(DefaultMessagesImpl.getString("AnalysisColumnTreeViewer.columnTip")); //$NON-NLS-1$
        TreeColumn column3 = new TreeColumn(newTree, SWT.CENTER);
        column3.setWidth(80);
        column3.setText(DefaultMessagesImpl.getString("AnalysisColumnTreeViewer.pattern")); //$NON-NLS-1$
        TreeColumn column4 = new TreeColumn(newTree, SWT.CENTER);
        column4.setWidth(80);
        column4.setText(DefaultMessagesImpl.getString("AnalysisColumnTreeViewer.udi")); //$NON-NLS-1$
        TreeColumn column5 = new TreeColumn(newTree, SWT.CENTER);
        column5.setWidth(80);
        column5.setText(DefaultMessagesImpl.getString("AnalysisColumnTreeViewer.operation")); //$NON-NLS-1$

        parent.layout();

        AbstractAnalysisActionHandler actionHandler = new AbstractAnalysisActionHandler(parent) {

            @Override
            protected void handleRemove() {
                removeSelectedElements(newTree);
            }

        };

        parent.setData(AbstractMetadataFormPage.ACTION_HANDLER, actionHandler);
        ColumnViewerDND.installDND(newTree);
        this.addTreeListener(newTree);
        newTree.setData(this);
        return newTree;
    }

    /**
     * DOC yyi create Up Down and Delete Buttons
     * 
     * @param parent
     */
    private void createUpDownButtons(Composite parent) {
        if (null != buttonsComp) {
            buttonsComp.dispose();
        }
        buttonsComp = masterPage.getEditor().getToolkit().createComposite(parent, SWT.NONE);
        buttonsComp.setLayout(new GridLayout(3, true));
        buttonsComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        GridData buttonGridData = new GridData(SWT.CENTER, SWT.CENTER, true, false);

        Button delButton = new Button(buttonsComp, SWT.NONE);
        delButton.setImage(ImageLib.getImage(ImageLib.DELETE_ACTION));
        delButton.setLayoutData(buttonGridData);
        Button moveUpButton = new Button(buttonsComp, SWT.NONE);
        moveUpButton.setText(DefaultMessagesImpl.getString("ColumnsComparisonMasterDetailsPage.moveUp")); //$NON-NLS-1$
        moveUpButton.setLayoutData(buttonGridData);
        Button moveDownButton = new Button(buttonsComp, SWT.NONE);
        moveDownButton.setText(DefaultMessagesImpl.getString("ColumnsComparisonMasterDetailsPage.moveDown")); //$NON-NLS-1$
        moveDownButton.setLayoutData(buttonGridData);

        delButton.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                removeSelectedElements(tree);
            }
        });

        moveUpButton.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                moveSelectedElements(tree, -1);
            }
        });

        moveDownButton.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                moveSelectedElements(tree, 1);
            }
        });

        parent.layout();
    }

    /**
     * DOC yyi to set the Up Down and Delete Buttons is visible.
     * 
     * @param isShow
     */
    public void setControlButtonsVisible(boolean isShow) {
        buttonsComp.setVisible(isShow);
    }

    /**
     * DOC yyi 7466 2010-03-22 change the order of appearence of indicators
     * 
     * @param newTree
     * @param step
     */
    protected void moveSelectedElements(Tree newTree, int step) {
        TreeItem[] selection = newTree.getSelection();
        if (selection.length > 0) {
            TreeItem item = selection[0];
            IndicatorUnit indicatorUnit = (IndicatorUnit) item.getData(INDICATOR_UNIT_KEY);
            ModelElementIndicator data = (ModelElementIndicator) item.getData(MODELELEMENT_INDICATOR_KEY);
            if (indicatorUnit != null) {
                data = (ModelElementIndicator) item.getData(MODELELEMENT_INDICATOR_KEY);
                TypedReturnCode<IndicatorUnit[]> code = sortIndicatorUnits(data.getIndicatorUnits(), indicatorUnit, step);
                if (code.isOk()) {
                    if (null != code.getObject()) {
                        Indicator[] inds = new Indicator[code.getObject().length];
                        for (int i = 0; i < code.getObject().length; i++) {
                            inds[i] = code.getObject()[i].getIndicator();
                        }
                        data.setIndicators(inds);
                    }
                    setElements(modelElementIndicators);
                    selectElement(tree.getItems(), indicatorUnit);
                }
            } else {
                data = (ModelElementIndicator) item.getData(MODELELEMENT_INDICATOR_KEY);
                int index = -1;
                for (int i = 0; i < modelElementIndicators.length; i++) {
                    if (data == modelElementIndicators[i]) {
                        index = i;
                        break;
                    }
                }
                if (index + step > -1 && index + step < modelElementIndicators.length) {
                    ModelElementIndicator tmpElement = modelElementIndicators[index + step];
                    modelElementIndicators[index + step] = modelElementIndicators[index];
                    modelElementIndicators[index] = tmpElement;
                    setElements(modelElementIndicators);
                    selectElement(tree.getItems(), data);
                }
            }
        }
    }

    /**
     * DOC yyi select element after moved.
     * 
     * @param items
     * @param data
     */
    private void selectElement(TreeItem[] items, Object data) {

        if (data instanceof ModelElementIndicator) {
            for (TreeItem item : items) {
                ModelElementIndicator model = (ModelElementIndicator) item.getData(MODELELEMENT_INDICATOR_KEY);
                if (model == data) {
                    tree.setSelection(item);
                } else {
                    selectElement(item.getItems(), data);
                }
            }
        } else if (data instanceof IndicatorUnit) {
            for (TreeItem item : items) {
                IndicatorUnit unit = (IndicatorUnit) item.getData(INDICATOR_UNIT_KEY);
                if (unit != null && ((IndicatorUnit) data).getIndicator() == unit.getIndicator()) {
                    tree.setSelection(item);
                } else
                    selectElement(item.getItems(), data);
            }
        }
    }

    /**
     * DOC yyi Sort indicators
     * 
     * @param units
     * @param targetUnit
     * @param step
     * @return
     */
    private TypedReturnCode<IndicatorUnit[]> sortIndicatorUnits(IndicatorUnit[] units, IndicatorUnit targetUnit, int step) {

        TypedReturnCode<IndicatorUnit[]> code = new TypedReturnCode<IndicatorUnit[]>();
        int index = -1;
        for (int i = 0; i < units.length; i++) {
            if (targetUnit.getIndicator() == units[i].getIndicator()) {
                index = i;
                break;
            } else if (null != units[i].getChildren()) {
                TypedReturnCode<IndicatorUnit[]> code2 = sortIndicatorUnits(units[i].getChildren(), targetUnit, step);
                if (null != code2.getObject()) {
                    units[i].setChildren(code2.getObject());
                    code.setOk(true);
                    break;
                }
            }
        }
        if (-1 != index && index + step > -1 && index + step < units.length) {
            IndicatorUnit tmpUnit = units[index + step];
            units[index + step] = units[index];
            units[index] = tmpUnit;
            code.setReturnCode("", true, units);
        }
        return code;
    }

    public void setElements(ModelElementIndicator[] elements) {
        this.tree.dispose();
        this.tree = createTree(this.parentComp);
        tree.setData(VIEWER_KEY, this);
        this.modelElementIndicators = elements;
        addItemElements((ModelElementIndicator[]) elements);
        initializedConnection((ModelElementIndicator[]) elements);
        // MOD mzhao 2009-05-5, bug 6587.
        updateBindConnection(masterPage, modelElementIndicators, tree);

        createUpDownButtons(this.parentComp);
    }

    /**
     * MOD mzhao 2009-06-16 feature 5887.
     */
    @Override
    public void updateModelViewer() {
        masterPage.recomputeIndicators();
        modelElementIndicators = masterPage.getCurrentModelElementIndicators();
        setElements(modelElementIndicators);
    }

    public void addElements(final ModelElementIndicator[] elements) {
        ModelElementIndicator[] newsArray = new ModelElementIndicator[this.modelElementIndicators.length + elements.length];
        System.arraycopy(this.modelElementIndicators, 0, newsArray, 0, this.modelElementIndicators.length);
        for (int i = 0; i < elements.length; i++) {
            newsArray[this.modelElementIndicators.length + i] = elements[i];
        }
        this.modelElementIndicators = newsArray;
        this.addItemElements(elements);
        initializedConnection(elements);
        // MOD mzhao 2009-05-5, bug 6587.
        updateBindConnection(masterPage, modelElementIndicators, tree);
    }

    /**
     * DOC Administrator Comment method "initializedConnection". for 6560
     * 
     * @param indicators
     * 
     */
    private void initializedConnection(ModelElementIndicator[] indicators) {
        Analysis analysis = masterPage.getAnalysisHandler().getAnalysis();
        DataManager connection = analysis.getContext().getConnection();
        Connection tdDataProvider = null;

        if (indicators != null && indicators.length > 0) {
            if (connection == null) {
                tdDataProvider = ModelElementIndicatorHelper.getTdDataProvider(indicators[0]);
                connection = tdDataProvider;
            }
        }
    }

    private void addItemElements(final ModelElementIndicator[] elements) {
        for (int i = 0; i < elements.length; i++) {
            final TreeItem treeItem = new TreeItem(tree, SWT.NONE);
            treeItem.setImage(getColumnElementImage(elements[i]));

            final ModelElementIndicator meIndicator = (ModelElementIndicator) elements[i];
            treeItem.setText(0, getModelElemetnDisplayName(meIndicator)); //$NON-NLS-1$
            treeItem.setData(MODELELEMENT_INDICATOR_KEY, meIndicator);

            TreeEditor comboEditor = new TreeEditor(tree);
            final CCombo combo = new CCombo(tree, SWT.BORDER);
            for (DataminingType type : DataminingType.values()) {
                combo.add(type.getLiteral()); // MODSCA 2008-04-10 use literal
                // for presentation
            }
            DataminingType dataminingType = MetadataHelper.getDataminingType(meIndicator.getModelElement());

            if (dataminingType == null) {
                combo.select(0);
            } else {
                combo.setText(dataminingType.getLiteral());
            }
            combo.addSelectionListener(new SelectionAdapter() {

                public void widgetSelected(SelectionEvent e) {
                    MetadataHelper.setDataminingType(DataminingType.get(combo.getText()), meIndicator.getModelElement());
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

            // MOD mzhao feature:13040, 2010-05-21
            addPatternLabl.addMouseListener(new PatternMouseAdapter(this, masterPage, meIndicator, treeItem));
            addPatternEditor.minimumWidth = addPatternLabl.getImage().getBounds().width;
            addPatternEditor.setEditor(addPatternLabl, treeItem, 2);

            // ADD xqliu 2010-02-23 feature 11617
            TreeEditor addUdiEditor = addColumnUdi(treeItem, meIndicator, 3);
            // ~

            TreeEditor delLabelEditor = new TreeEditor(tree);
            Label delLabel = new Label(tree, SWT.NONE);
            delLabel.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
            delLabel.setImage(ImageLib.getImage(ImageLib.DELETE_ACTION));
            delLabel.setToolTipText(DefaultMessagesImpl.getString("AnalysisColumnTreeViewer.delete")); //$NON-NLS-1$
            delLabel.pack();
            delLabel.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseDown(MouseEvent e) {
                    deleteModelElementItems(meIndicator);
                    if (treeItem.getParentItem() != null && treeItem.getParentItem().getData(INDICATOR_UNIT_KEY) != null) {
                        setElements(modelElementIndicators);
                    } else {
                        removeItemBranch(treeItem);
                    }
                    // MOD mzhao 2005-05-05 bug 6587.
                    // MOD mzhao 2009-06-8, bug 5887.
                    // updateBindConnection(masterPage, getColumnIndicator(),
                    // tree);
                }

            });

            delLabelEditor.minimumWidth = delLabel.getImage().getBounds().width;
            delLabelEditor.horizontalAlignment = SWT.CENTER;
            delLabelEditor.setEditor(delLabel, treeItem, 4);
            treeItem.setData(ITEM_EDITOR_KEY, new TreeEditor[] { comboEditor, delLabelEditor, addPatternEditor, addUdiEditor });
            if (meIndicator.hasIndicators()) {
                createIndicatorItems(treeItem, meIndicator.getIndicatorUnits());
            }
            treeItem.setExpanded(true);
        }
        this.setDirty(true);
    }

    /**
     * DOC yyi 2010-04-29 12572 for MDM elements.
     * 
     * @param element
     * @return
     */
    private Image getColumnElementImage(ModelElementIndicator element) {
        if (element instanceof XmlElementIndicator) {
            return ImageLib.getImage(ImageLib.XML_ELEMENT_DOC);
        }
        return ImageLib.getImage(ImageLib.TD_COLUMN);
    }

    /**
     * DOC xqliu Comment method "addColumnUdi". ADD xqliu 2010-02-23 feature 11617
     * 
     * @param treeItem
     * @param meIndicator
     * @param columnIndex
     * @return
     */
    private TreeEditor addColumnUdi(final TreeItem treeItem, final ModelElementIndicator meIndicator, int columnIndex) {
        TreeEditor addUdiEditor = new TreeEditor(tree);
        Label addUdiLabl = new Label(tree, SWT.NONE);
        addUdiLabl.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
        addUdiLabl.setImage(ImageLib.getImage(ImageLib.ADD_IND_DEFINITION));
        addUdiLabl.setToolTipText(DefaultMessagesImpl.getString("AnalysisColumnTreeViewer.addUdi")); //$NON-NLS-1$
        addUdiLabl.pack();

        addUdiLabl.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseDown(MouseEvent e) {
                DataManager dm = getAnalysis().getContext().getConnection();
                if (dm == null) {
                    masterPage.doSave(null);
                }

                // TODO 10238
                if (dm != null && dm instanceof Connection) {
                    Connection dp = (Connection) dm;
                    if (ConnectionUtils.isMdmConnection(dp)) {
                        MessageUI.openWarning(DefaultMessagesImpl.getString("AnalysisColumnTreeViewer.dontSupport"));
                        return;
                    }
                }

                IFolder udiProject = ResourceManager.getUDIFolder();

                CheckedTreeSelectionDialog dialog = UDIUtils.createUdiCheckedTreeSelectionDialog(udiProject);

                if (dialog.open() == Window.OK) {
                    for (Object obj : dialog.getResult()) {
                        if (obj instanceof IFile) {
                            IFile file = (IFile) obj;
                            IndicatorUnit[] addIndicatorUnits = null;
                            try {
                                addIndicatorUnits = UDIUtils.createIndicatorUnit(file, meIndicator, getAnalysis());
                            } catch (Throwable e1) {
                                log.warn(e1, e1);
                            }
                            if (addIndicatorUnits != null && addIndicatorUnits.length > 0) {
                                for (IndicatorUnit unit : addIndicatorUnits) {
                                    createOneUnit(treeItem, unit);
                                }
                                setDirty(true);
                            }
                        }
                    }
                }
            }
        });
        addUdiEditor.minimumWidth = addUdiLabl.getImage().getBounds().width;
        addUdiEditor.setEditor(addUdiLabl, treeItem, columnIndex);
        return addUdiEditor;
    }

    /**
     * DOC xqliu Comment method "getModelElemetnDisplayName".
     * 
     * @param meIndicator
     * @return
     */
    private String getModelElemetnDisplayName(ModelElementIndicator meIndicator) {
        String meName = meIndicator.getElementName();
        String typeName = "";
        if (meIndicator instanceof ColumnIndicator) {
            typeName = ((ColumnIndicator) meIndicator).getTdColumn().getSqlDataType().getName();
        } else if (meIndicator instanceof XmlElementIndicator) {
            typeName = ((TdXmlElementType) meIndicator.getModelElement()).getJavaType();
        }
        return meName != null ? meName + PluginConstant.SPACE_STRING + PluginConstant.PARENTHESIS_LEFT + typeName
                + PluginConstant.PARENTHESIS_RIGHT : "null";
    }

    /**
     * DOC rli Comment method "deleteTreeElements".
     * 
     * @param deleteModelElementIndiciator
     */
    private void deleteModelElementItems(ModelElementIndicator deleteModelElementIndiciator) {
        ModelElementIndicator[] remainIndicators = new ModelElementIndicator[modelElementIndicators.length - 1];
        int i = 0;
        for (ModelElementIndicator indicator : modelElementIndicators) {
            if (deleteModelElementIndiciator.equals(indicator)) {
                continue;
            } else {
                remainIndicators[i] = indicator;
                i++;
            }
        }
        this.modelElementIndicators = remainIndicators;
    }

    public void openIndicatorSelectDialog(Shell shell) {
        final IndicatorSelectDialog dialog = new IndicatorSelectDialog(shell, DefaultMessagesImpl
                .getString("AnalysisColumnTreeViewer.indicatorSelection"), modelElementIndicators); //$NON-NLS-1$
        dialog.create();

        if (!DQPreferenceManager.isBlockWeb()) {
            dialog.getShell().addShellListener(new ShellAdapter() {

                @Override
                public void shellActivated(ShellEvent e) {
                    dialog.getShell().setFocus();
                    IContext context = HelpSystem.getContext(HelpPlugin.getDefault().getIndicatorSelectorHelpContextID());
                    PlatformUI.getWorkbench().getHelpSystem().displayHelp(context);
                }
            });
        }

        if (dialog.open() == Window.OK) {
            ModelElementIndicator[] result = dialog.getResult();
            for (ModelElementIndicator modelElementIndicator : result) {
                modelElementIndicator.storeTempIndicator();
            }

            this.setElements(result);
            return;
        }
    }

    /**
     * Remove the selected elements(eg:TdColumn or Indicator) from tree.
     * 
     * @param newTree
     */
    private void removeSelectedElements(Tree newTree) {
        TreeItem[] selection = newTree.getSelection();
        boolean branchIndicatorExist = false;
        for (TreeItem item : selection) {
            IndicatorUnit indicatorUnit = (IndicatorUnit) item.getData(INDICATOR_UNIT_KEY);
            if (indicatorUnit != null) {
                deleteIndicatorItems((ModelElementIndicator) item.getData(MODELELEMENT_INDICATOR_KEY), indicatorUnit);
            } else {
                deleteModelElementItems((ModelElementIndicator) item.getData(MODELELEMENT_INDICATOR_KEY));
            }
            // if the item's parent item is a indicator item, when current
            // indicator item removed, it's parent item
            // should be removed and recreate the tree;else,just need remove
            // current item and it's branch.
            if (item.getParentItem() != null && item.getParentItem().getData(INDICATOR_UNIT_KEY) != null) {
                branchIndicatorExist = true;
                continue;
            } else {
                removeItemBranch(item);
            }
        }
        if (branchIndicatorExist) {
            setElements(modelElementIndicators);
        }
        // MOD mzhao 2009-05-5, bug 6587.
        // MOD mzhao 2009-06-8, bug 5887.
        // updateBindConnection(masterPage, getColumnIndicator(), tree);
    }

    private String isExpressionNull(TreeItem item) {
        String expressContent = null;
        IndicatorUnit indicatorUnit = (IndicatorUnit) item.getData(INDICATOR_UNIT_KEY);
        ModelElementIndicator meIndicator = (ModelElementIndicator) item.getData(MODELELEMENT_INDICATOR_KEY);
        ModelElement me = meIndicator.getModelElement();
        Connection dataprovider = ModelElementHelper.getTdDataProvider(me);
        DbmsLanguage dbmsLang = DbmsLanguageFactory.createDbmsLanguage(dataprovider);
        Expression expression = dbmsLang.getInstantiatedExpression(indicatorUnit.getIndicator());
        if (expression != null) {
            expressContent = expression.getBody();
        }
        return expressContent;
    }

    private void addTreeListener(final Tree tree) {
        tree.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                TreeItem item = (TreeItem) e.item;
                if (DATA_PARAM.equals(item.getData(DATA_PARAM))) {
                    tree.setMenu(null);
                } else {
                    new AnalysisColumnColumnMenuProvider(tree).createTreeMenu();
                }
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

        tree.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseDoubleClick(MouseEvent e) {
                TreeItem[] treeSelection = tree.getSelection();

                if (treeSelection.length > 0) {
                    TreeItem item = treeSelection[0];
                    Object indicatorobj = item.getData(INDICATOR_UNIT_KEY);
                    Object meobj = item.getData(MODELELEMENT_INDICATOR_KEY);
                    if (meobj != null && indicatorobj == null) {
                        // open indicator selector
                        openIndicatorSelectDialog(null);
                    } else if (meobj != null && indicatorobj != null) {
                        // open indicator option wizard
                        openIndicatorOptionDialog(null, item);
                    }
                }
            }
        });
    }

    private ExpandableComposite getTheSuitedComposite(SelectionEvent e) {
        Composite[] previewChartCompsites = masterPage.getPreviewChartCompsites();
        if (previewChartCompsites == null) {
            return null;
        }

        Object obj = e.item.getData(MODELELEMENT_INDICATOR_KEY);
        if (obj instanceof ModelElementIndicator) {
            ModelElementIndicator meIndicator = (ModelElementIndicator) obj;
            for (Composite comp : previewChartCompsites) {
                if (comp.getData() == meIndicator) {
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
        return this.masterPage.getAnalysisHandler().getAnalysis();
    }

    public Tree getTree() {
        return tree;
    }

    @Override
    public boolean canDrop(ModelElement modelElement) {
        // MOD qiongli 2010-8-19,bug 14436:could not come from diffrent connection
        Connection tdProvider = ModelElementHelper.getTdDataProvider(modelElement);
        if (tdProvider == null) {
            return false;
        } else if (this.getAnalysis().getContext().getConnection() != null
                && !ProxyRepositoryViewObject.areSame(tdProvider, this.getAnalysis().getContext().getConnection())) {
            return false;
        }
        // MOD mzhao, 2010-07-23 bug 14014: If the editor is dirty, save it firstly before drag and drop an elements.
        if (masterPage.isDirty()) {
            masterPage.doSave(new NullProgressMonitor());
            return false;
        }
        List<ModelElement> existModelElements = new ArrayList<ModelElement>();
        for (ModelElementIndicator modelElementIndicator : this.getModelElementIndicator()) {
            existModelElements.add(modelElementIndicator.getModelElement());
        }
        // MOD mzhao 9848 2010-01-14, allowing to drag and drop table.
        if (modelElement instanceof TdTable) {
            return !existModelElements.containsAll(ColumnSetHelper.getColumns((TdTable) modelElement));
        }
        if (existModelElements.contains(modelElement)) {
            return false;
        }
        if (modelElement instanceof TdXmlElementType) {
            return XmlElementHelper.isLeafNode((TdXmlElementType) modelElement);
        }
        return true;
    }

    /**
     * DOC yyi 2010-06-10 Refactor to ModelElementTreeMenuProvider
     */
    class AnalysisColumnColumnMenuProvider extends ModelElementTreeMenuProvider {

        /**
         * DOC yyi ColumnModelElementTreeMenuProvider constructor comment.
         * 
         * @param tree
         */
        public AnalysisColumnColumnMenuProvider(Tree tree) {
            super(tree);
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.talend.dataprofiler.core.ui.editor.composite.ModelElementTreeMenuProvider#getAnalysis2()
         */
        @Override
        protected Analysis getAnalysis2() {
            return getAnalysis();
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * org.talend.dataprofiler.core.ui.editor.composite.ModelElementTreeMenuProvider#removeSelectedElements2(org
         * .eclipse.swt.widgets.Tree)
         */
        @Override
        protected void removeSelectedElements2(Tree tree) {
            removeSelectedElements(tree);
        }

    }
}
