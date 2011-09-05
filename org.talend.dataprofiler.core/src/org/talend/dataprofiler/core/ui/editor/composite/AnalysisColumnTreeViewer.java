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
package org.talend.dataprofiler.core.ui.editor.composite;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.help.HelpSystem;
import org.eclipse.help.IContext;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.TreeEditor;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
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
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.CheckedTreeSelectionDialog;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.talend.core.model.metadata.MetadataColumnRepositoryObject;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.metadata.builder.connection.MetadataColumn;
import org.talend.core.model.properties.ConnectionItem;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.repository.model.repositoryObject.MetadataTableRepositoryObject;
import org.talend.core.repository.model.repositoryObject.MetadataXmlElementTypeRepositoryObject;
import org.talend.cwm.db.connection.ConnectionUtils;
import org.talend.cwm.helper.ColumnSetHelper;
import org.talend.cwm.helper.ModelElementHelper;
import org.talend.cwm.helper.ResourceHelper;
import org.talend.cwm.relational.TdSqlDataType;
import org.talend.cwm.relational.TdTable;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.helper.ModelElementIndicatorHelper;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.manager.DQPreferenceManager;
import org.talend.dataprofiler.core.model.ColumnIndicator;
import org.talend.dataprofiler.core.model.DelimitedFileIndicator;
import org.talend.dataprofiler.core.model.ModelElementIndicator;
import org.talend.dataprofiler.core.model.XmlElementIndicator;
import org.talend.dataprofiler.core.model.impl.DelimitedFileIndicatorImpl;
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
import org.talend.dataquality.domain.Domain;
import org.talend.dataquality.helpers.MetadataHelper;
import org.talend.dataquality.indicators.CompositeIndicator;
import org.talend.dataquality.indicators.DataminingType;
import org.talend.dataquality.indicators.DateParameters;
import org.talend.dataquality.indicators.FrequencyIndicator;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.IndicatorParameters;
import org.talend.dataquality.indicators.TextParameters;
import org.talend.dataquality.indicators.sql.JavaUserDefIndicator;
import org.talend.dq.dbms.DbmsLanguage;
import org.talend.dq.dbms.DbmsLanguageFactory;
import org.talend.dq.helper.RepositoryNodeHelper;
import org.talend.dq.helper.UDIHelper;
import org.talend.dq.nodes.AnalysisRepNode;
import org.talend.dq.nodes.MDMXmlElementRepNode;
import org.talend.dq.nodes.indicator.type.IndicatorEnum;
import org.talend.repository.model.IRepositoryNode;
import org.talend.resource.ResourceManager;
import org.talend.utils.sql.TalendTypeConvert;
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

    public static final String TREE_ELEMENT = "item_element"; //$NON-NLS-1$

    public static final String ITEM_IMAGE = "item_image"; //$NON-NLS-1$

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
        final Tree newTree = new TooltipTree(parent, SWT.SINGLE | SWT.BORDER | SWT.FULL_SELECTION | SWT.VIRTUAL) {

            @Override
            protected boolean isValidItem(TreeItem item) {
                if (item == null) {
                    return false;
                }
                Object itemData = item.getData(INDICATOR_UNIT_KEY);
                Object columnItemData = item.getData(MODELELEMENT_INDICATOR_KEY);
                if (itemData != null) {
                    IndicatorUnit indicatorUnit = (IndicatorUnit) item.getData(INDICATOR_UNIT_KEY);
                    if (indicatorUnit != null && !(indicatorUnit.getIndicator() instanceof CompositeIndicator)) {
                        return true;
                    }
                    return false;
                } else {
                    if (columnItemData != null) {
                        ModelElementIndicator modelElementIndicator = (ModelElementIndicator) columnItemData;
                        IRepositoryNode modelElementRepositoryNode = modelElementIndicator.getModelElementRepositoryNode();
                        return modelElementRepositoryNode == null ? false : true;
                    }
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
     * DOC yyi create Up Down and Delete Buttons.
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
     * DOC yyi 7466 2010-03-22 change the order of appearence of indicators.
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
                } else {
                    selectElement(item.getItems(), data);
                }
            }
        }
    }

    /**
     * DOC yyi Sort indicators.
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
            code.setReturnCode("", true, units);//$NON-NLS-1$
        }
        return code;
    }

    public void setElements(ModelElementIndicator[] elements) {
        this.tree.dispose();
        this.tree = createTree(this.parentComp);
        this.tree.setItemCount(elements.length);
        tree.setData(VIEWER_KEY, this);
        tree.setData(TREE_ELEMENT, elements);
        this.modelElementIndicators = elements;
        // addItemElements((ModelElementIndicator[]) elements);
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
                if (tdDataProvider != null && masterPage.getExecCombo() != null) {
                    if (ConnectionUtils.isDelimitedFileConnection(tdDataProvider)) {
                        masterPage.setWhereClauseDisabled();
                        masterPage.changeExecuteLanguageToJava(true);
                    } else if (ConnectionUtils.isMdmConnection(tdDataProvider)) {
                        masterPage.setWhereClauseDisabled();
                    }
                }
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

            final ModelElement modelElement = RepositoryNodeHelper
                    .getSubModelElement(meIndicator.getModelElementRepositoryNode());
            DataminingType dataminingType = MetadataHelper.getDataminingType(modelElement);
            // MOD qiongli 2010-11-15 feature 16796
            if (meIndicator instanceof DelimitedFileIndicator) {
                dataminingType = MetadataHelper.getDefaultDataminingType(meIndicator.getJavaType());
            }

            if (dataminingType == null) {
                combo.select(0);
            } else {
                combo.setText(dataminingType.getLiteral());
            }
            combo.addSelectionListener(new SelectionAdapter() {

                public void widgetSelected(SelectionEvent e) {
                    MetadataHelper.setDataminingType(DataminingType.get(combo.getText()), modelElement);
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
        String typeName = "";//$NON-NLS-1$
        if (meIndicator instanceof ColumnIndicator) {
            // MOD scorreia 2010-10-20 bug 16403 avoid NPE here
            TdSqlDataType sqlDataType = ((ColumnIndicator) meIndicator).getTdColumn().getSqlDataType();
            typeName = sqlDataType != null ? sqlDataType.getName() : "unknown";//$NON-NLS-1$
        } else if (meIndicator instanceof XmlElementIndicator) {
            typeName = ((MDMXmlElementRepNode) meIndicator.getModelElementRepositoryNode()).getTdXmlElementType().getJavaType();
        } else if (meIndicator instanceof DelimitedFileIndicatorImpl) {
            MetadataColumn mColumn = ((DelimitedFileIndicatorImpl) meIndicator).getMetadataColumn();
            typeName = TalendTypeConvert.convertToJavaType(mColumn.getTalendType());
        }
        return meName != null ? meName + PluginConstant.SPACE_STRING + PluginConstant.PARENTHESIS_LEFT + typeName
                + PluginConstant.PARENTHESIS_RIGHT : "null";//$NON-NLS-1$
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
        final IndicatorSelectDialog dialog = new IndicatorSelectDialog(shell,
                DefaultMessagesImpl.getString("AnalysisColumnTreeViewer.indicatorSelection"), modelElementIndicators); //$NON-NLS-1$
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

    private void removeSelectedElements(TreeItem[] items) {
        boolean branchIndicatorExist = false;
        for (TreeItem item : items) {
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
        ModelElement me = null;
        IndicatorUnit indicatorUnit = (IndicatorUnit) item.getData(INDICATOR_UNIT_KEY);
        ModelElementIndicator meIndicator = (ModelElementIndicator) item.getData(MODELELEMENT_INDICATOR_KEY);
        if (indicatorUnit != null) {
            IRepositoryViewObject reposViewObj = meIndicator.getModelElementRepositoryNode().getObject();
            // MOD klliu 2010-01-30 Distinguish MetadataColumnRepositoryObject and
            // MetadataXmlElementTypeRepositoryObject
            if (reposViewObj instanceof MetadataColumnRepositoryObject) {
                me = ((MetadataColumnRepositoryObject) reposViewObj).getTdColumn();
            } else if (reposViewObj instanceof MetadataXmlElementTypeRepositoryObject) {
                me = ((MetadataXmlElementTypeRepositoryObject) reposViewObj).getTdXmlElementType();
            }
            Connection dataprovider = ModelElementHelper.getTdDataProvider(me);
            DbmsLanguage dbmsLang = DbmsLanguageFactory.createDbmsLanguage(dataprovider);
            Expression expression = dbmsLang.getInstantiatedExpression(indicatorUnit.getIndicator());
            if (expression != null) {
                expressContent = expression.getBody();
            }
        } else {
            IRepositoryNode parentNodeForColumnNode = RepositoryNodeHelper.getParentNodeForColumnNode(meIndicator
                    .getModelElementRepositoryNode());
            expressContent = DefaultMessagesImpl.getString(
                    "AnalysisColumnTreeViewer.columnParent", parentNodeForColumnNode.getObject().getLabel()); //$NON-NLS-1$ //$NON-NLS-2$;
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
                    new AnalysisColumnColumnMenuProvider(tree).createTreeMenu(Boolean.FALSE);
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
                } else {
                    propertyChangeSupport.firePropertyChange(PluginConstant.EXPAND_TREE, null, e.item);
                }

                // closeOthers(e);

                comp.layout();
                form.reflow(true);
            }

            private void closeOthers(TreeEvent e) {
                TreeItem item = (TreeItem) e.item;
                Tree tree = (Tree) e.getSource();
                for (TreeItem otherItem : tree.getItems()) {
                    if (otherItem != item && otherItem.getExpanded()) {
                        int count = otherItem.getItemCount();
                        otherItem.setExpanded(false);
                        Event event = new Event();
                        event.item = otherItem;
                        tree.notifyListeners(SWT.Collapse, event);
                        otherItem.removeAll();
                        otherItem.setItemCount(count);
                    }
                }
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

        tree.getVerticalBar().addSelectionListener(new SelectionListener() {

            private int lastItemIndex = 0;

            public void widgetSelected(SelectionEvent e) {

                Tree tree = (Tree) ((ScrollBar) e.getSource()).getParent();
                TreeItem currentItem = tree.getTopItem();
                int currentItemIndex = getRootItemIndex(tree);// tree.indexOf(currentItem);
                // decide it is a column item.
                if (currentItem.getParentItem() == null) {
                    // get pervious column item and removeall it's children.
                    currentItemIndex = tree.indexOf(currentItem);

                    if (currentItemIndex <= 0) {
                        // 0: first one . -1: can not find the item
                        // for (TreeItem theItem : tree.getItems()) {
                        // if (currentItem != theItem) {
                        // theItem.setExpanded(false);
                        // }
                        // }

                    } else if (this.lastItemIndex < currentItemIndex) {
                        // have pervious column item
                        // case for pull down
                        TreeItem perviousItem = tree.getItem(currentItemIndex - 1);
                        int previousItemCount = perviousItem.getItemCount();
                        // perviousItem.setExpanded(false);
                        perviousItem.removeAll();
                        perviousItem.setItemCount(previousItemCount);
                        // currentItem.setExpanded(true);
                        if(e.detail!=SWT.DRAG){
                        tree.setTopItem(currentItem);
                        }

                    } else {
                        // case for pull up
                        TreeItem nextItem = tree.getItem(currentItemIndex + 1);
                        int previousItemCount = nextItem.getItemCount();
                        // nextItem.setExpanded(false);
                        nextItem.removeAll();
                        nextItem.setItemCount(previousItemCount);
                        // currentItem.setExpanded(true);
                        if(e.detail!=SWT.DRAG){
                         tree.setTopItem(currentItem);
                        }
                    }

                }
                this.lastItemIndex = currentItemIndex;
            }

            private int getRootItemIndex(Tree tree) {
                TreeItem currentItem = tree.getTopItem();
                TreeItem rootItem = currentItem;
                while (rootItem.getParentItem() != null) {
                    rootItem = rootItem.getParentItem();
                }
                return tree.indexOf(rootItem);
            }

            public void widgetDefaultSelected(SelectionEvent e) {
                // TODO Auto-generated method stub

            }

        });


        tree.addListener(SWT.SetData, new Listener() {


            public void handleEvent(Event event) {
                Tree tree = (Tree) event.widget;
                final TreeItem item = (TreeItem) event.item;
                List<Widget> imageWidget = new ArrayList<Widget>();
                item.setData(ITEM_IMAGE, imageWidget);
                item.addDisposeListener(new DisposeListener() {

                    public void widgetDisposed(DisposeEvent e) {
                        Object data = ((TreeItem) e.getSource()).getData(ITEM_IMAGE);
                        if (data == null || !(data instanceof List<?>)) {
                            return;
                        }
                        for (Object theWidget : (List<?>) data) {
                            if (theWidget != null && theWidget instanceof Widget) {
                                ((Widget) theWidget).dispose();
                            }
                        }
                    }

                });
                final TreeItem parentItem = item.getParentItem();
                if (parentItem == null) {// column case
                    /* root-level item */
                    final TreeItem columnItem = item;
                    ModelElementIndicator[] elements = (ModelElementIndicator[]) tree.getData(TREE_ELEMENT);
                    columnItem.setImage(getColumnElementImage(elements[event.index]));
                    final ModelElementIndicator meIndicator = (ModelElementIndicator) elements[event.index];
                    columnItem.setText(0, getModelElemetnDisplayName(meIndicator)); //$NON-NLS-1$
                    columnItem.setData(MODELELEMENT_INDICATOR_KEY, meIndicator);
                    TreeEditor comboEditor = new TreeEditor(tree);
                    final CCombo combo = new CCombo(tree, SWT.BORDER);
                    for (DataminingType type : DataminingType.values()) {
                        combo.add(type.getLiteral()); // MODSCA 2008-04-10 use literal
                        // for presentation
                    }
                    final ModelElement modelElement = RepositoryNodeHelper.getSubModelElement(meIndicator
                            .getModelElementRepositoryNode());
                    DataminingType dataminingType = MetadataHelper.getDataminingType(modelElement);
                    // MOD qiongli 2010-11-15 feature 16796
                    if (meIndicator instanceof DelimitedFileIndicator) {
                        dataminingType = MetadataHelper.getDefaultDataminingType(meIndicator.getJavaType());
                    }

                    if (dataminingType == null) {
                        combo.select(0);
                    } else {
                        combo.setText(dataminingType.getLiteral());
                    }
                    combo.addSelectionListener(new SelectionAdapter() {

                        public void widgetSelected(SelectionEvent e) {
                            MetadataHelper.setDataminingType(DataminingType.get(combo.getText()), modelElement);
                            setDirty(true);
                        }

                    });
                    combo.setEditable(false);

                    comboEditor.minimumWidth = WIDTH1_CELL;
                    comboEditor.setEditor(combo, columnItem, 1);

                    TreeEditor addPatternEditor = new TreeEditor(tree);
                    Label addPatternLabl = new Label(tree, SWT.NONE);
                    addPatternLabl.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
                    addPatternLabl.setImage(ImageLib.getImage(ImageLib.ADD_PATTERN));
                    imageWidget.add(addPatternLabl);
                    addPatternLabl.setToolTipText(DefaultMessagesImpl.getString("AnalysisColumnTreeViewer.addPattern")); //$NON-NLS-1$
                    addPatternLabl.pack();

                    // MOD mzhao feature:13040, 2010-05-21
                    addPatternLabl.addMouseListener(new PatternMouseAdapter(AnalysisColumnTreeViewer.this, masterPage,
                            meIndicator, columnItem));
                    addPatternEditor.minimumWidth = addPatternLabl.getImage().getBounds().width;
                    addPatternEditor.setEditor(addPatternLabl, columnItem, 2);

                    // ADD xqliu 2010-02-23 feature 11617
                    TreeEditor addUdiEditor = addColumnUdi(columnItem, meIndicator, 3);
                    // ~

                    TreeEditor delLabelEditor = new TreeEditor(tree);
                    Label delLabel = new Label(tree, SWT.NONE);
                    delLabel.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
                    delLabel.setImage(ImageLib.getImage(ImageLib.DELETE_ACTION));
                    imageWidget.add(delLabel);
                    delLabel.setToolTipText(DefaultMessagesImpl.getString("AnalysisColumnTreeViewer.delete")); //$NON-NLS-1$
                    delLabel.pack();
                    delLabel.addMouseListener(new MouseAdapter() {

                        @Override
                        public void mouseDown(MouseEvent e) {
                            deleteModelElementItems(meIndicator);
                            if (columnItem.getParentItem() != null
                                    && columnItem.getParentItem().getData(INDICATOR_UNIT_KEY) != null) {
                                setElements(modelElementIndicators);
                            } else {
                                removeItemBranch(columnItem);
                            }
                            // MOD mzhao 2005-05-05 bug 6587.
                            // MOD mzhao 2009-06-8, bug 5887.
                            // updateBindConnection(masterPage, getColumnIndicator(),
                            // tree);
                        }

                    });

                    delLabelEditor.minimumWidth = delLabel.getImage().getBounds().width;
                    delLabelEditor.horizontalAlignment = SWT.CENTER;
                    delLabelEditor.setEditor(delLabel, columnItem, 4);
                    columnItem.setData(ITEM_EDITOR_KEY,
                            new TreeEditor[] { comboEditor, delLabelEditor, addPatternEditor, addUdiEditor });
                    if (meIndicator.hasIndicators()) {
                        // set the children data
                        columnItem.setData(AnalysisColumnTreeViewer.TREE_ELEMENT, meIndicator.getIndicatorUnits());
                        columnItem.setItemCount(meIndicator.getIndicatorUnits().length);
                    }
                    /* root-level item */
                    // case for single indicator or indicator enum
                } else if (parentItem.getData(AnalysisColumnTreeViewer.TREE_ELEMENT) instanceof IndicatorUnit[]) {
                    final TreeItem indicatorItem = item;
                    IndicatorUnit[] indicatorUnits = (IndicatorUnit[]) parentItem.getData(AnalysisColumnTreeViewer.TREE_ELEMENT);
                    final IndicatorUnit unit = indicatorUnits[event.index];
                    IndicatorEnum indicatorType = unit.getType();

                    indicatorItem.setData(MODELELEMENT_INDICATOR_KEY, parentItem.getData(MODELELEMENT_INDICATOR_KEY));
                    indicatorItem.setData(INDICATOR_UNIT_KEY, unit);
                    indicatorItem.setData(viewKey, AnalysisColumnTreeViewer.this);

                    indicatorItem.setImage(0, getIndicatorImage(unit));

                    String indicatorName = getIndicatorName(unit);
                    String label = indicatorName == null ? "unknown indicator" : indicatorName;//$NON-NLS-1$
                    indicatorItem.setText(0, label);

                    TreeEditor optionEditor;
                    // if (indicatorEnum.hasChildren()) {
                    optionEditor = new TreeEditor(tree);
                    Label optionLabel = new Label(tree, SWT.NONE);
                    optionLabel.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
                    optionLabel.setImage(ImageLib.getImage(ImageLib.INDICATOR_OPTION));
                    imageWidget.add(optionLabel);
                    optionLabel.setToolTipText(DefaultMessagesImpl.getString("AnalysisColumnTreeViewer.options")); //$NON-NLS-1$
                    optionLabel.pack();
                    optionLabel.setData(unit);
                    optionLabel.addMouseListener(new MouseAdapter() {

                        /*
                         * (non-Javadoc)
                         * 
                         * @see org.eclipse.swt.events.MouseAdapter#mouseDown(org.eclipse.swt .events.MouseEvent)
                         */
                        @Override
                        public void mouseDown(MouseEvent e) {
                            openIndicatorOptionDialog(null, indicatorItem);
                        }

                    });

                    optionEditor.minimumWidth = optionLabel.getImage().getBounds().width;
                    optionEditor.horizontalAlignment = SWT.CENTER;
                    optionEditor.setEditor(optionLabel, indicatorItem, 1);

                    TreeEditor delEditor = new TreeEditor(tree);
                    Label delLabel = new Label(tree, SWT.NONE);
                    delLabel.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
                    delLabel.setImage(ImageLib.getImage(ImageLib.DELETE_ACTION));
                    imageWidget.add(delLabel);
                    delLabel.setToolTipText(DefaultMessagesImpl.getString("AnalysisColumnTreeViewer.delete")); //$NON-NLS-1$
                    delLabel.pack();
                    delLabel.addMouseListener(new MouseAdapter() {

                        /*
                         * (non-Javadoc)
                         * 
                         * @see org.eclipse.swt.events.MouseAdapter#mouseDown(org.eclipse.swt .events.MouseEvent)
                         */
                        @Override
                        public void mouseDown(MouseEvent e) {
                            ModelElementIndicator meIndicator = (ModelElementIndicator) parentItem
                                    .getData(MODELELEMENT_INDICATOR_KEY);
                            deleteIndicatorItems(meIndicator, unit);
                            if (indicatorItem.getParentItem() != null
                                    && indicatorItem.getParentItem().getData(INDICATOR_UNIT_KEY) != null) {
                                setElements(modelElementIndicators);
                            } else {
                                removeItemBranch(indicatorItem);
                            }
                        }

                    });

                    delEditor.minimumWidth = delLabel.getImage().getBounds().width;
                    delEditor.horizontalAlignment = SWT.CENTER;
                    // MOD mzhao feature 13040, column analysis have 5 columns, whereas columnset have 4 columns.
                    if (AnalysisColumnTreeViewer.VIEWER_KEY.equals(viewKey)) {
                        delEditor.setEditor(delLabel, indicatorItem, 4);
                    } else if (AnalysisColumnSetTreeViewer.VIEWER_KEY.equals(viewKey)) {
                        delEditor.setEditor(delLabel, indicatorItem, 3);
                    }

                    if (indicatorType.hasChildren()) {
                        indicatorItem.setData(parentItem.getData(MODELELEMENT_INDICATOR_KEY));
                        indicatorItem.setData(AnalysisColumnTreeViewer.TREE_ELEMENT, unit.getChildren());
                        indicatorItem.setItemCount(unit.getChildren().length);
                    } else if (unit.getParameters() != null) {
                        List<Object> childList = generateChildrenList(unit);
                        indicatorItem.setData(AnalysisColumnTreeViewer.TREE_ELEMENT, childList);
                        indicatorItem.setItemCount(childList.size());
                    }

                    // MOD mzhao feature 11128, Handle Java User Defined Indicator.
                    Indicator judi = null;
                    try {
                        judi = UDIHelper.adaptToJavaUDI(unit.getIndicator());
                    } catch (Throwable e) {
                        log.error(e, e);
                        MessageDialog.openError(tree.getShell(),
                                DefaultMessagesImpl.getString("ColumnsComparisonMasterDetailsPage.error"), e.getMessage());//$NON-NLS-1$
                    }
                    if (judi != null) {
                        ((JavaUserDefIndicator) judi).setExecuteEngine(absMasterPage.getAnalysis().getParameters()
                                .getExecutionLanguage());
                    }
                    // } else if (parentItem.getData(AnalysisColumnTreeViewer.TREE_ELEMENT) instanceof IndicatorUnit)
                    // {// single
                    // // indicator
                    // // case
                    // IndicatorUnit indicatorUnit = (IndicatorUnit)
                    // parentItem.getData(AnalysisColumnTreeViewer.TREE_ELEMENT);
                    // IndicatorParameters parameters = indicatorUnit.getParameters();
                    // if (parameters == null) {
                    // return;
                    // }
                    // if (hideParameters(indicatorUnit)) {
                    // return;
                    // }
                    //
                    // TreeItem iParamItem = item;
                    // if (indicatorUnit.getIndicator() instanceof FrequencyIndicator) {
                    // // MOD hcheng bug 7377,2009-05-18,when bins is null,parameters not
                    // // set on tree
                    // if (parameters.getBins() == null) {
                    // return;
                    // }
                    // // ~
                    // iParamItem.setText(0,
                    //                                DefaultMessagesImpl.getString("AnalysisColumnTreeViewer.resultsShown") + parameters.getTopN()); //$NON-NLS-1$
                    // iParamItem.setData(DATA_PARAM, DATA_PARAM);
                    // iParamItem.setImage(0, ImageLib.getImage(ImageLib.OPTION));
                    // }
                    //
                    // TextParameters tParameter = parameters.getTextParameter();
                    // if (tParameter != null && !hideTextParameters(indicatorUnit)) {
                    // // iParamItem = new TreeItem(indicatorItem, SWT.NONE);
                    //                        iParamItem.setText(0, DefaultMessagesImpl.getString("AnalysisColumnTreeViewer.textParameters")); //$NON-NLS-1$
                    // iParamItem.setData(DATA_PARAM, DATA_PARAM);
                    // iParamItem.setImage(0, ImageLib.getImage(ImageLib.OPTION));
                    //
                    // List<Object> parameterList = changeParameterToList(tParameter);
                    // if (parameterList != null) {
                    // iParamItem.setData(AnalysisColumnTreeViewer.TREE_ELEMENT, parameterList);
                    //
                    // iParamItem.setItemCount(parameterList.size());
                    // }
                    //
                    // }
                    // case for parameter and subParameters
                } else if (parentItem.getData(AnalysisColumnTreeViewer.TREE_ELEMENT) instanceof List<?>) {
                    List<?> data = (List<?>) parentItem.getData(AnalysisColumnTreeViewer.TREE_ELEMENT);
                    TreeItem subParamItem = item;
                    if (data.get(event.index) instanceof Map<?, ?>) {
                        Iterator<?> iter = ((Map<?, ?>) data.get(event.index)).keySet().iterator();
                        assert (((Map<?, ?>) data.get(event.index)).size() == 1);
                        while (iter.hasNext()) {
                            String itemText = (String) iter.next();
                            List<?> subList = (List<?>) ((Map<?, ?>) data.get(event.index)).get(itemText);
                            subParamItem.setData(AnalysisColumnTreeViewer.TREE_ELEMENT, subList);
                            subParamItem.setItemCount(subList.size());
                            subParamItem.setText(itemText);
                        }
                    } else {
                        subParamItem.setText(data.get(event.index).toString());
                    }
                    subParamItem.setImage(0, ImageLib.getImage(ImageLib.OPTION));
                    subParamItem.setData(DATA_PARAM, DATA_PARAM);

                }
                // if (parentItem != null || tree.indexOf(item) == 0) {
                    item.setExpanded(true);
                // }
                AnalysisColumnTreeViewer.this.setDirty(true);
                // treeItem.setItemCount()
            }

            private List<Object> generateChildrenList(IndicatorUnit unit) {
                List<Object> childList = new ArrayList<Object>();
                IndicatorUnit indicatorUnit = unit;
                IndicatorParameters parameters = indicatorUnit.getParameters();
                if (parameters == null) {
                    return childList;
                }
                if (hideParameters(indicatorUnit)) {
                    return childList;
                }

                if (indicatorUnit.getIndicator() instanceof FrequencyIndicator) {
                    // MOD hcheng bug 7377,2009-05-18,when bins is null,parameters not
                    // set on tree
                    if (parameters.getBins() == null) {
                        return childList;
                    }
                    // ~
                    childList.add(DefaultMessagesImpl.getString("AnalysisColumnTreeViewer.resultsShown") + parameters.getTopN()); //$NON-NLS-1$
                }

                TextParameters tParameter = parameters.getTextParameter();
                if (tParameter != null && !hideTextParameters(indicatorUnit)) {
                    // iParamItem = new TreeItem(indicatorItem, SWT.NONE);
                    //                    childList.add(DefaultMessagesImpl.getString("AnalysisColumnTreeViewer.textParameters")); //$NON-NLS-1$
                    Map<String, List<Object>> subMap = new HashMap<String, List<Object>>();

                    List<Object> parameterList = changeParameterToList(tParameter);
                    if (parameterList != null) {
                        subMap.put(DefaultMessagesImpl.getString("AnalysisColumnTreeViewer.textParameters"), parameterList);
                        childList.add(subMap);
                    } else {
                        childList.add(DefaultMessagesImpl.getString("AnalysisColumnTreeViewer.textParameters"));
                    }

                }

                DateParameters dParameters = parameters.getDateParameters();
                if (dParameters != null) {
                    //                    childList.add(DefaultMessagesImpl.getString("AnalysisColumnTreeViewer.dateParameters")); //$NON-NLS-1$

                    Map<String, List<Object>> subMap = new HashMap<String, List<Object>>();
                    List<Object> subList = new ArrayList<Object>();
                    subList.add(DefaultMessagesImpl.getString("AnalysisColumnTreeViewer.aggregationType", dParameters
                            .getDateAggregationType().getName()));
                    subMap.put(DefaultMessagesImpl.getString("AnalysisColumnTreeViewer.dateParameters"), subList);
                    childList.add(subMap);

                }

                Domain indicatorValidDomain = parameters.getIndicatorValidDomain();
                if (indicatorValidDomain != null) {
                    childList.add(DefaultMessagesImpl.getString("AnalysisColumnTreeViewer.qualityThresholds") + (indicatorValidDomain != null)); //$NON-NLS-1$
                }

                Domain bins = parameters.getBins();
                if (bins != null) {
                    childList.add(DefaultMessagesImpl.getString("AnalysisColumnTreeViewer.binsDefined") + (bins != null)); //$NON-NLS-1$
                }

                return childList;
            }

            private List<Object> changeParameterToList(TextParameters tParameter) {
                List<Object> parameterList = new ArrayList<Object>();
                parameterList.add(DefaultMessagesImpl.getString("AnalysisColumnTreeViewer.useBlanks") + tParameter.isUseBlank());
                parameterList.add(DefaultMessagesImpl.getString("AnalysisColumnTreeViewer.ignoreCase")
                        + tParameter.isIgnoreCase());
                parameterList.add(DefaultMessagesImpl.getString("AnalysisColumnTreeViewer.useNulls") + tParameter.isUseNulls());
                parameterList.add(DefaultMessagesImpl.getString("AnalysisColumnTreeViewer.countryCode")
                        + tParameter.getCountryCode());
                return parameterList;
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

    /**
     * Getter for analysis.
     * 
     * @return the analysis
     */
    public AnalysisRepNode getAnalysisNode() {
        return this.masterPage.getAnalysisRepNode();
    }

    public Tree getTree() {
        return tree;
    }

    @Override
    public boolean canDrop(IRepositoryNode metadataRepositoryNode) {
        IRepositoryViewObject metadataRepObject = metadataRepositoryNode.getObject();
        ConnectionItem connItem = (ConnectionItem) metadataRepObject.getProperty().getItem();
        // MOD qiongli 2010-8-19,bug 14436:could not come from diffrent connection
        Connection tdProvider = connItem.getConnection();
        // FIXME text is never used.
        boolean text = metadataRepObject instanceof MetadataXmlElementTypeRepositoryObject;
        if (tdProvider == null) {
            return false;
        } else if (this.getAnalysis().getContext().getConnection() != null
                && !ResourceHelper.areSame(tdProvider, this.getAnalysis().getContext().getConnection())) {
            return false;
        }
        // MOD mzhao, 2010-07-23 bug 14014: If the editor is dirty, save it firstly before drag and drop an elements.
        if (masterPage.isDirty()) {
            masterPage.doSave(new NullProgressMonitor());
            return false;
        }
        List<ModelElement> existModelElements = new ArrayList<ModelElement>();
        for (ModelElementIndicator modelElementIndicator : this.getModelElementIndicator()) {
            ModelElement me = RepositoryNodeHelper.getModelElementFromRepositoryNode(modelElementIndicator
                    .getModelElementRepositoryNode());
            existModelElements.add(me);
        }
        // MOD mzhao 9848 2010-01-14, allowing to drag and drop table.
        if (metadataRepObject instanceof MetadataTableRepositoryObject) {
            MetadataTableRepositoryObject tableRepObject = (MetadataTableRepositoryObject) metadataRepObject;
            return !existModelElements.containsAll(ColumnSetHelper.getColumns((TdTable) tableRepObject.getTable()));
        }
        if (metadataRepObject instanceof MetadataColumnRepositoryObject) {
            if (existModelElements.contains(((MetadataColumnRepositoryObject) metadataRepObject).getTdColumn())) {
                return false;
            }
        }

        if (metadataRepObject instanceof MetadataXmlElementTypeRepositoryObject) {
            return !metadataRepositoryNode.hasChildren();
        }
        return true;
    }

    @Override
    public void setInput(Object[] objs) {
        if (objs == null || objs.length == 0) {
            this.removeSelectedElements(tree.getItems());
            return;
        }
        super.setInput(objs);
    }

    /**
     * DOC yyi 2010-06-10 Refactor to ModelElementTreeMenuProvider
     */
    class AnalysisColumnColumnMenuProvider extends ModelElementTreeMenuProvider {

        /**
         * DOC yyi ColumnModelElementTreeMenuProvider constructor comment. true *
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
        protected AnalysisRepNode getAnalysis2() {
            return getAnalysisNode();
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
