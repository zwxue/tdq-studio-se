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
import org.eclipse.help.HelpSystem;
import org.eclipse.help.IContext;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.CheckedTreeSelectionDialog;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.part.FileEditorInput;
import org.talend.commons.emf.EMFUtil;
import org.talend.cwm.db.connection.ConnectionUtils;
import org.talend.cwm.dependencies.DependenciesHandler;
import org.talend.cwm.helper.DataProviderHelper;
import org.talend.cwm.helper.ModelElementHelper;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.cwm.helper.XmlElementHelper;
import org.talend.cwm.relational.TdColumn;
import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.cwm.xml.TdXMLElement;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.helper.ModelElementIndicatorHelper;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.model.ColumnIndicator;
import org.talend.dataprofiler.core.model.ModelElementIndicator;
import org.talend.dataprofiler.core.model.XmlElementIndicator;
import org.talend.dataprofiler.core.pattern.PatternUtilities;
import org.talend.dataprofiler.core.ui.action.actions.TdAddTaskAction;
import org.talend.dataprofiler.core.ui.action.actions.predefined.PreviewColumnAction;
import org.talend.dataprofiler.core.ui.dialog.IndicatorSelectDialog;
import org.talend.dataprofiler.core.ui.dialog.composite.TooltipTree;
import org.talend.dataprofiler.core.ui.editor.AbstractAnalysisActionHandler;
import org.talend.dataprofiler.core.ui.editor.AbstractMetadataFormPage;
import org.talend.dataprofiler.core.ui.editor.analysis.ColumnMasterDetailsPage;
import org.talend.dataprofiler.core.ui.editor.preview.IndicatorUnit;
import org.talend.dataprofiler.core.ui.utils.MessageUI;
import org.talend.dataprofiler.core.ui.utils.OpeningHelpWizardDialog;
import org.talend.dataprofiler.core.ui.views.ColumnViewerDND;
import org.talend.dataprofiler.core.ui.views.DQRespositoryView;
import org.talend.dataprofiler.core.ui.wizard.indicator.IndicatorOptionsWizard;
import org.talend.dataprofiler.core.ui.wizard.indicator.forms.FormEnum;
import org.talend.dataprofiler.help.HelpPlugin;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.analysis.ExecutionLanguage;
import org.talend.dataquality.domain.Domain;
import org.talend.dataquality.domain.pattern.Pattern;
import org.talend.dataquality.helpers.MetadataHelper;
import org.talend.dataquality.indicators.CompositeIndicator;
import org.talend.dataquality.indicators.DataminingType;
import org.talend.dataquality.indicators.DateParameters;
import org.talend.dataquality.indicators.FrequencyIndicator;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.IndicatorParameters;
import org.talend.dataquality.indicators.PatternMatchingIndicator;
import org.talend.dataquality.indicators.TextParameters;
import org.talend.dataquality.indicators.sql.UserDefIndicator;
import org.talend.dq.dbms.DbmsLanguage;
import org.talend.dq.dbms.DbmsLanguageFactory;
import org.talend.dq.helper.UDIHelper;
import org.talend.dq.helper.resourcehelper.PatternResourceFileHelper;
import org.talend.dq.helper.resourcehelper.UDIResourceFileHelper;
import org.talend.dq.indicators.definitions.DefinitionHandler;
import org.talend.dq.nodes.indicator.type.IndicatorEnum;
import org.talend.resource.ResourceManager;
import orgomg.cwm.foundation.softwaredeployment.DataManager;
import orgomg.cwm.objectmodel.core.Expression;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * @author rli
 * 
 */
public class AnalysisColumnTreeViewer extends AbstractColumnDropTree {

    protected static Logger log = Logger.getLogger(AnalysisColumnTreeViewer.class);

    private static final String DATA_PARAM = "DATA_PARAM"; //$NON-NLS-1$

    public static final String INDICATOR_UNIT_KEY = "INDICATOR_UNIT_KEY"; //$NON-NLS-1$

    public static final String MODELELEMENT_INDICATOR_KEY = "MODELELEMENT_INDICATOR_KEY"; //$NON-NLS-1$

    public static final String ITEM_EDITOR_KEY = "ITEM_EDITOR_KEY"; //$NON-NLS-1$

    public static final String VIEWER_KEY = "org.talend.dataprofiler.core.ui.editor.composite.AnasisColumnTreeViewer"; //$NON-NLS-1$

    private static final int WIDTH1_CELL = 75;

    private Composite parentComp;

    private Tree tree;

    private ModelElementIndicator[] modelElementIndicators;

    private ColumnMasterDetailsPage masterPage;

    // ADD xqliu 2009-08-24 bug 8776
    private ExecutionLanguage language;

    public ExecutionLanguage getLanguage() {
        return language;
    }

    public void setLanguage(ExecutionLanguage language) {
        this.language = language;
    }

    // ~

    public AnalysisColumnTreeViewer(Composite parent) {
        parentComp = parent;
        this.tree = createTree(parent);
    }

    public AnalysisColumnTreeViewer(Composite parent, ColumnMasterDetailsPage masterPage) {
        this(parent);
        this.masterPage = masterPage;
        this.setElements(masterPage.getCurrentModelElementIndicators());
        this.setDirty(false);
    }

    /**
     * @param parent
     */
    private Tree createTree(Composite parent) {
        final Tree newTree = new TooltipTree(parent, SWT.MULTI | SWT.BORDER) {

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
        column4.setText(DefaultMessagesImpl.getString("AnalysisColumnTreeViewer.operation")); //$NON-NLS-1$

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

    public void setInput(Object[] objs) {
        boolean isMdm = false;
        if (objs != null && objs.length != 0) {
            isMdm = objs[0] instanceof TdXMLElement;
            if (!(objs[0] instanceof TdColumn || isMdm)) {
                return;
            }
        }

        List<ModelElement> modelElementList = new ArrayList<ModelElement>();
        for (Object obj : objs) {
            modelElementList.add((ModelElement) obj);
        }
        List<ModelElementIndicator> modelElementIndicatorList = new ArrayList<ModelElementIndicator>();
        for (ModelElementIndicator modelElementIndicator : modelElementIndicators) {
            if (modelElementList.contains(modelElementIndicator.getModelElement())) {
                modelElementIndicatorList.add(modelElementIndicator);
                modelElementList.remove(modelElementIndicator.getModelElement());
            }
        }

        for (ModelElement modelElement : modelElementList) {
            ModelElementIndicator temp = isMdm ? ModelElementIndicatorHelper
                    .createXmlElementIndicator((TdXMLElement) modelElement) : ModelElementIndicatorHelper
                    .createColumnIndicator((TdColumn) modelElement);
            modelElementIndicatorList.add(temp);
        }
        this.modelElementIndicators = modelElementIndicatorList.toArray(new ModelElementIndicator[modelElementIndicatorList
                .size()]);
        this.setElements(modelElementIndicators);
    }

    public void setElements(final ModelElementIndicator[] elements) {
        this.tree.dispose();
        this.tree = createTree(this.parentComp);
        tree.setData(VIEWER_KEY, this);
        this.modelElementIndicators = elements;
        addItemElements(elements);
        initializedConnection(elements);
        // MOD mzhao 2009-05-5, bug 6587.
        updateBindConnection(masterPage, modelElementIndicators, tree);
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
        TdDataProvider tdDataProvider = null;

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
            treeItem.setImage(ImageLib.getImage(ImageLib.TD_COLUMN));

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

            addPatternLabl.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseDown(MouseEvent e) {
                    DataManager dm = getAnalysis().getContext().getConnection();
                    if (dm == null) {
                        masterPage.doSave(null);
                    }

                    // TODO 10238
                    if (dm != null && dm instanceof TdDataProvider) {
                        TdDataProvider dp = (TdDataProvider) dm;
                        if (ConnectionUtils.isMdmConnection(DataProviderHelper.getTdProviderConnection(dp).getObject())) {
                            MessageUI.openWarning("Don't support this method yet!");
                            return;
                        }
                    }

                    IFolder libProject = ResourceManager.getLibrariesFolder();

                    CheckedTreeSelectionDialog dialog = PatternUtilities.createPatternCheckedTreeSelectionDialog(libProject);

                    // IFile[] selectedFiles =
                    // PatternUtilities.getPatternFileByIndicator(clmIndicator);
                    // dialog.setInitialSelections(selectedFiles);
                    if (dialog.open() == Window.OK) {
                        for (Object obj : dialog.getResult()) {
                            if (obj instanceof IFile) {
                                IFile file = (IFile) obj;
                                IndicatorUnit addIndicatorUnit = PatternUtilities.createIndicatorUnit(file, meIndicator,
                                        getAnalysis());
                                if (addIndicatorUnit != null) {
                                    createOneUnit(treeItem, addIndicatorUnit);
                                    setDirty(true);
                                } else {
                                    Pattern pattern = PatternResourceFileHelper.getInstance().findPattern(file);
                                    MessageUI.openError(DefaultMessagesImpl
                                            .getString("AnalysisColumnTreeViewer.IndicatorSelected") //$NON-NLS-1$
                                            + pattern.getName());
                                }
                            }
                        }
                    }
                }
            });
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
            delLabelEditor.setEditor(delLabel, treeItem, 3);
            treeItem.setData(ITEM_EDITOR_KEY, new TreeEditor[] { comboEditor, delLabelEditor, addPatternEditor });
            if (meIndicator.hasIndicators()) {
                createIndicatorItems(treeItem, meIndicator.getIndicatorUnits());
            }
            treeItem.setExpanded(true);
        }
        this.setDirty(true);
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
            typeName = ((TdXMLElement) meIndicator.getModelElement()).getJavaType();
        }
        return meName != null ? meName + PluginConstant.SPACE_STRING + PluginConstant.PARENTHESIS_LEFT + typeName
                + PluginConstant.PARENTHESIS_RIGHT : "null";
    }

    private void createIndicatorItems(final TreeItem treeItem, IndicatorUnit[] indicatorUnits) {
        for (IndicatorUnit indicatorUnit : indicatorUnits) {
            createOneUnit(treeItem, indicatorUnit);
        }
    }

    /**
     * DOC xqliu Comment method "getIndicatorIamge".
     * 
     * @param indicatorUnit
     * @return
     */
    private Image getIndicatorIamge(IndicatorUnit indicatorUnit) {
        IndicatorEnum indicatorType = indicatorUnit.getType();
        if (indicatorType == IndicatorEnum.RegexpMatchingIndicatorEnum
                || indicatorType == IndicatorEnum.SqlPatternMatchingIndicatorEnum) {
            return ImageLib.getImage(ImageLib.PATTERN_REG);
        } else if (indicatorType == IndicatorEnum.UserDefinedIndicatorEnum) {
            // TODO use different image for user defined indicator
            if (DefinitionHandler.getInstance().getUserDefinedMatchIndicatorCategory().equals(
                    UDIHelper.getUDICategory(indicatorUnit.getIndicator().getIndicatorDefinition()))) {
                return ImageLib.getImage(ImageLib.PATTERN_REG);
            } else {
                return ImageLib.getImage(ImageLib.IND_DEFINITION);
            }
            // ~
        }
        return ImageLib.getImage(ImageLib.IND_DEFINITION);
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
        IndicatorEnum indicatorType = indicatorUnit.getType();

        indicatorItem.setData(MODELELEMENT_INDICATOR_KEY, treeItem.getData(MODELELEMENT_INDICATOR_KEY));
        indicatorItem.setData(INDICATOR_UNIT_KEY, unit);
        indicatorItem.setData(VIEWER_KEY, this);

        indicatorItem.setImage(0, getIndicatorIamge(unit));

        String indicatorName = indicatorUnit.getIndicatorName();
        String label = indicatorName == null ? "unknown indicator" : indicatorName;
        indicatorItem.setText(0, label);

        TreeEditor optionEditor;
        // if (indicatorEnum.hasChildren()) {
        optionEditor = new TreeEditor(tree);
        Label optionLabel = new Label(tree, SWT.NONE);
        optionLabel.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
        optionLabel.setImage(ImageLib.getImage(ImageLib.INDICATOR_OPTION));
        optionLabel.setToolTipText(DefaultMessagesImpl.getString("AnalysisColumnTreeViewer.options")); //$NON-NLS-1$
        optionLabel.pack();
        optionLabel.setData(indicatorUnit);
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
        // }

        TreeEditor delEditor = new TreeEditor(tree);
        Label delLabel = new Label(tree, SWT.NONE);
        delLabel.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
        delLabel.setImage(ImageLib.getImage(ImageLib.DELETE_ACTION));
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
                ModelElementIndicator meIndicator = (ModelElementIndicator) treeItem.getData(MODELELEMENT_INDICATOR_KEY);
                deleteIndicatorItems(meIndicator, unit);
                if (indicatorItem.getParentItem() != null && indicatorItem.getParentItem().getData(INDICATOR_UNIT_KEY) != null) {
                    setElements(modelElementIndicators);
                } else {
                    removeItemBranch(indicatorItem);
                }
            }

        });

        delEditor.minimumWidth = delLabel.getImage().getBounds().width;
        delEditor.horizontalAlignment = SWT.CENTER;
        delEditor.setEditor(delLabel, indicatorItem, 3);
        indicatorItem.setData(ITEM_EDITOR_KEY, new TreeEditor[] { optionEditor, delEditor });
        if (indicatorType.hasChildren()) {
            indicatorItem.setData(treeItem.getData(MODELELEMENT_INDICATOR_KEY));
            createIndicatorItems(indicatorItem, indicatorUnit.getChildren());
        }
        createIndicatorParameters(indicatorItem, indicatorUnit);
    }

    /**
     * DOC xqliu Comment method "removeDependency".
     * 
     * @param analysis
     * @param unit
     */
    protected void removeDependency(Analysis analysis, IndicatorUnit unit) {
        List<ModelElement> reomveElements = new ArrayList<ModelElement>();
        Indicator indicator = unit.getIndicator();
        if (indicator instanceof UserDefIndicator) {
            reomveElements.add(indicator.getIndicatorDefinition());
            // MOD xqliu 2009-10-09 bug 9304
            // if (IndicatorCategoryHelper.isUserDefMatching(UDIHelper.getUDICategory(indicator))) {
            // reomveElements.addAll(indicator.getParameters().getDataValidDomain().getPatterns());
            // }
            // ~
        } else if (indicator instanceof PatternMatchingIndicator) {
            reomveElements.addAll(indicator.getParameters().getDataValidDomain().getPatterns());
        }
        DependenciesHandler.getInstance().removeDependenciesBetweenModels(analysis, reomveElements);
        for (ModelElement me : reomveElements) {
            EMFUtil.saveSingleResource(me.eResource());
        }
    }

    /**
     * DOC qzhang Comment method "createIndicatorParameters".
     * 
     * @param indicatorItem
     * @param parameters
     */
    private void createIndicatorParameters(TreeItem indicatorItem, IndicatorUnit indicatorUnit) {
        TreeItem[] items = indicatorItem.getItems();
        if (indicatorItem != null && !indicatorItem.isDisposed()) {
            for (TreeItem treeItem : items) {
                if (DATA_PARAM.equals(treeItem.getData(DATA_PARAM))) {
                    treeItem.dispose();
                }
            }
        }
        IndicatorParameters parameters = indicatorUnit.getIndicator().getParameters();
        if (parameters == null) {
            return;
        }
        TreeItem iParamItem;
        if (indicatorUnit.getIndicator() instanceof FrequencyIndicator) {
            // MOD hcheng bug 7377,2009-05-18,when bins is null,parameters not
            // set on tree
            if (parameters.getBins() == null) {
                return;
            }
            // ~
            iParamItem = new TreeItem(indicatorItem, SWT.NONE);
            iParamItem.setText(0, DefaultMessagesImpl.getString("AnalysisColumnTreeViewer.resultsShown") + parameters.getTopN()); //$NON-NLS-1$
            iParamItem.setData(DATA_PARAM, DATA_PARAM);
            iParamItem.setImage(0, ImageLib.getImage(ImageLib.OPTION));
        }

        TextParameters tParameter = parameters.getTextParameter();
        if (tParameter != null) {
            iParamItem = new TreeItem(indicatorItem, SWT.NONE);
            iParamItem.setText(0, DefaultMessagesImpl.getString("AnalysisColumnTreeViewer.textParameters")); //$NON-NLS-1$
            iParamItem.setData(DATA_PARAM, DATA_PARAM);
            iParamItem.setImage(0, ImageLib.getImage(ImageLib.OPTION));

            TreeItem subParamItem = new TreeItem(iParamItem, SWT.NONE);
            subParamItem.setText(DefaultMessagesImpl.getString("AnalysisColumnTreeViewer.useBlanks") + tParameter.isUseBlank()); //$NON-NLS-1$
            subParamItem.setImage(0, ImageLib.getImage(ImageLib.OPTION));
            subParamItem.setData(DATA_PARAM, DATA_PARAM);

            subParamItem = new TreeItem(iParamItem, SWT.NONE);
            subParamItem
                    .setText(DefaultMessagesImpl.getString("AnalysisColumnTreeViewer.ignoreCase") + tParameter.isIgnoreCase()); //$NON-NLS-1$
            subParamItem.setImage(0, ImageLib.getImage(ImageLib.OPTION));
            subParamItem.setData(DATA_PARAM, DATA_PARAM);

            subParamItem = new TreeItem(iParamItem, SWT.NONE);
            subParamItem.setText(DefaultMessagesImpl.getString("AnalysisColumnTreeViewer.useNulls") + tParameter.isUseNulls()); //$NON-NLS-1$
            subParamItem.setImage(0, ImageLib.getImage(ImageLib.OPTION));
            subParamItem.setData(DATA_PARAM, DATA_PARAM);
        }
        DateParameters dParameters = parameters.getDateParameters();
        if (dParameters != null) {
            iParamItem = new TreeItem(indicatorItem, SWT.NONE);
            iParamItem.setText(0, DefaultMessagesImpl.getString("AnalysisColumnTreeViewer.dateParameters")); //$NON-NLS-1$
            iParamItem.setData(DATA_PARAM, DATA_PARAM);
            iParamItem.setImage(0, ImageLib.getImage(ImageLib.OPTION));

            TreeItem subParamItem = new TreeItem(iParamItem, SWT.NONE);
            subParamItem.setText(DefaultMessagesImpl.getString(
                    "AnalysisColumnTreeViewer.aggregationType", dParameters.getDateAggregationType().getName())); //$NON-NLS-1$ //$NON-NLS-2$
            subParamItem.setImage(0, ImageLib.getImage(ImageLib.OPTION));
            subParamItem.setData(DATA_PARAM, DATA_PARAM);
        }

        Domain dataValidDomain = parameters.getDataValidDomain();
        if (dataValidDomain != null) {
            iParamItem = new TreeItem(indicatorItem, SWT.NONE);
            iParamItem.setText(0,
                    DefaultMessagesImpl.getString("AnalysisColumnTreeViewer.validDomain") + (dataValidDomain != null)); //$NON-NLS-1$
            iParamItem.setData(DATA_PARAM, DATA_PARAM);
            iParamItem.setImage(0, ImageLib.getImage(ImageLib.OPTION));
        }
        Domain indicatorValidDomain = parameters.getIndicatorValidDomain();
        if (indicatorValidDomain != null) {
            iParamItem = new TreeItem(indicatorItem, SWT.NONE);
            iParamItem.setText(0,
                    DefaultMessagesImpl.getString("AnalysisColumnTreeViewer.qualityThresholds") + (indicatorValidDomain != null)); //$NON-NLS-1$
            iParamItem.setData(DATA_PARAM, DATA_PARAM);
            iParamItem.setImage(0, ImageLib.getImage(ImageLib.OPTION));
        }
        Domain bins = parameters.getBins();
        if (bins != null) {
            iParamItem = new TreeItem(indicatorItem, SWT.NONE);
            iParamItem.setText(0, DefaultMessagesImpl.getString("AnalysisColumnTreeViewer.binsDefined") + (bins != null)); //$NON-NLS-1$
            iParamItem.setData(DATA_PARAM, DATA_PARAM);
            iParamItem.setImage(0, ImageLib.getImage(ImageLib.OPTION));
        }
    }

    /**
     * DOC rli Comment method "deleteIndicatorItems".
     * 
     * @param treeItem
     * @param inidicatorUnit
     */
    private void deleteIndicatorItems(ModelElementIndicator meIndicator, IndicatorUnit inidicatorUnit) {
        meIndicator.removeIndicatorUnit(inidicatorUnit);
        // remove dependency
        removeDependency(getAnalysis(), inidicatorUnit);
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
        dialog.getShell().addShellListener(new ShellAdapter() {

            /*
             * (non-Javadoc)
             * 
             * @see org.eclipse.swt.events.ShellAdapter#shellActivated(org.eclipse .swt.events.ShellEvent)
             */
            @Override
            public void shellActivated(ShellEvent e) {
                dialog.getShell().setFocus();
                IContext context = HelpSystem.getContext(HelpPlugin.getDefault().getIndicatorSelectorHelpContextID());
                PlatformUI.getWorkbench().getHelpSystem().displayHelp(context);
            }
        });

        if (dialog.open() == Window.OK) {
            ModelElementIndicator[] result = dialog.getResult();
            for (ModelElementIndicator modelElementIndicator : result) {
                modelElementIndicator.storeTempIndicator();
            }
            this.setElements(result);
            return;
        }
    }

    public void openIndicatorOptionDialog(Shell shell, TreeItem indicatorItem) {

        if (isDirty()) {
            masterPage.doSave(null);
        }

        IndicatorUnit indicatorUnit = (IndicatorUnit) indicatorItem.getData(INDICATOR_UNIT_KEY);
        IndicatorOptionsWizard wizard = new IndicatorOptionsWizard(indicatorUnit);

        if (FormEnum.isExsitingForm(indicatorUnit)) {
            String href = FormEnum.getFirstFormHelpHref(indicatorUnit);
            OpeningHelpWizardDialog optionDialog = new OpeningHelpWizardDialog(shell, wizard, href);

            if (Window.OK == optionDialog.open()) {
                setDirty(wizard.isDirty());
                createIndicatorParameters(indicatorItem, indicatorUnit);
            }
        } else {
            MessageDialogWithToggle.openInformation(null, DefaultMessagesImpl.getString("AnalysisColumnTreeViewer.information"), //$NON-NLS-1$
                    DefaultMessagesImpl.getString("AnalysisColumnTreeViewer.nooption")); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    public ModelElementIndicator[] getModelElementIndicator() {
        return this.modelElementIndicators;
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

    private String isExpressionNull(TreeItem item) {
        String expressContent = null;
        IndicatorUnit indicatorUnit = (IndicatorUnit) item.getData(INDICATOR_UNIT_KEY);
        ModelElementIndicator meIndicator = (ModelElementIndicator) item.getData(MODELELEMENT_INDICATOR_KEY);
        ModelElement me = meIndicator.getModelElement();
        TdDataProvider dataprovider = ModelElementHelper.getTdDataProvider(me);
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
                    new ModelElementTreeMenuProvider(tree).createTreeMenu();
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
        List<ModelElement> existModelElements = new ArrayList<ModelElement>();
        for (ModelElementIndicator modelElementIndicator : this.getModelElementIndicator()) {
            existModelElements.add(modelElementIndicator.getModelElement());
        }
        if (existModelElements.contains(modelElement)) {
            return false;
        }
        if (modelElement instanceof TdXMLElement) {
            return XmlElementHelper.isLeafNode((TdXMLElement) modelElement);
        }
        return true;
    }

    @Override
    public void dropModelElements(List<? extends ModelElement> modelElements, int index) {
        int size = modelElements.size();
        ModelElementIndicator[] meIndicators = new ModelElementIndicator[size];
        for (int i = 0; i < size; i++) {
            ModelElement modelElement = modelElements.get(i);
            TdColumn tdColumn = SwitchHelpers.COLUMN_SWITCH.doSwitch(modelElement);
            if (tdColumn != null) {
                meIndicators[i] = ModelElementIndicatorHelper.createColumnIndicator(tdColumn);
            } else {
                TdXMLElement xmlElement = SwitchHelpers.XMLELEMENT_SWITCH.doSwitch(modelElement);
                if (xmlElement != null) {
                    meIndicators[i] = ModelElementIndicatorHelper.createXmlElementIndicator(xmlElement);
                }
            }
        }
        this.addElements(meIndicators);
    }

    /**
     * DOC zqin AnalysisColumnTreeViewer class global comment. Detailled comment
     * 
     * this class provide an action to deal with the menu actions on tree viewer.
     * 
     */
    class ModelElementTreeMenuProvider {

        private Tree tree;

        public ModelElementTreeMenuProvider(Tree tree) {
            this.tree = tree;
        }

        /**
         * DOC qzhang Comment method "createTreeMenu".
         * 
         * @param newTree
         * @param containEdit
         */
        public void createTreeMenu() {
            Menu oldMenu = tree.getMenu();
            if (oldMenu != null && !oldMenu.isDisposed()) {
                oldMenu.dispose();
            }
            Menu menu = new Menu(tree);

            if (isSelectedColumn(tree.getSelection())) {
                MenuItem previewMenuItem = new MenuItem(menu, SWT.CASCADE);
                previewMenuItem.setText(DefaultMessagesImpl.getString("AnalysisColumnTreeViewer.previewDQElement")); //$NON-NLS-1$
                previewMenuItem.setImage(ImageLib.getImage(ImageLib.EXPLORE_IMAGE));
                previewMenuItem.addSelectionListener(new SelectionAdapter() {

                    /*
                     * (non-Javadoc)
                     * 
                     * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected (org.eclipse
                     * .swt.events.SelectionEvent)
                     */
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        previewSelectedElements(tree);
                    }

                });

                MenuItem showLocationMenuItem = new MenuItem(menu, SWT.CASCADE);
                showLocationMenuItem.setText(DefaultMessagesImpl.getString("AnalysisColumnTreeViewer.showDQElement")); //$NON-NLS-1$
                showLocationMenuItem.setImage(ImageLib.getImage(ImageLib.EXPLORE_IMAGE));
                showLocationMenuItem.addSelectionListener(new SelectionAdapter() {

                    /*
                     * (non-Javadoc)
                     * 
                     * @seeorg.eclipse.swt.events.SelectionAdapter# widgetSelected(org.eclipse
                     * .swt.events.SelectionEvent)
                     */
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        showSelectedElements(tree);
                    }

                });
            }

            if (isSelectedIndicator(tree.getSelection()) && !isMdmSelected(tree.getSelection())) {
                // MOD 2009-01-04 mzhao
                MenuItem showQueryMenuItem = new MenuItem(menu, SWT.CASCADE);
                showQueryMenuItem.setText(DefaultMessagesImpl.getString("AnalysisColumnTreeViewer.viewQuery")); //$NON-NLS-1$
                showQueryMenuItem.setImage(ImageLib.getImage(ImageLib.EXPLORE_IMAGE));
                showQueryMenuItem.addSelectionListener(new SelectionAdapter() {

                    /*
                     * (non-Javadoc)
                     * 
                     * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected (org.eclipse
                     * .swt.events.SelectionEvent)
                     */
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        viewQueryForSelectedElement(tree);

                    }
                });
            }

            if (isSelectedPatternIndicator(tree.getSelection())) {
                MenuItem editPatternMenuItem = new MenuItem(menu, SWT.CASCADE);
                editPatternMenuItem.setText(DefaultMessagesImpl.getString("AnalysisColumnTreeViewer.editPattern")); //$NON-NLS-1$
                editPatternMenuItem.setImage(ImageLib.getImage(ImageLib.PATTERN_REG));
                editPatternMenuItem.addSelectionListener(new SelectionAdapter() {

                    /*
                     * (non-Javadoc)
                     * 
                     * @seeorg.eclipse.swt.events.SelectionAdapter# widgetSelected(org
                     * .eclipse.swt.events.SelectionEvent)
                     */
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        editPattern(tree);
                    }

                });
            }

            if (isSelectedUDIndicator(tree.getSelection())) {
                MenuItem editPatternMenuItem = new MenuItem(menu, SWT.CASCADE);
                editPatternMenuItem.setText(DefaultMessagesImpl.getString("AnalysisColumnTreeViewer.editUDIndicator")); //$NON-NLS-1$
                editPatternMenuItem.setImage(ImageLib.getImage(ImageLib.IND_DEFINITION));
                editPatternMenuItem.addSelectionListener(new SelectionAdapter() {

                    /*
                     * (non-Javadoc)
                     * 
                     * @seeorg.eclipse.swt.events.SelectionAdapter# widgetSelected(org
                     * .eclipse.swt.events.SelectionEvent)
                     */
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        editUDIndicator(tree);
                    }

                });
            }
            // add common menu to the tree
            MenuItem addTaskItem = new MenuItem(menu, SWT.CASCADE);
            addTaskItem.setText(DefaultMessagesImpl.getString("AnalysisColumnTreeViewer.AddTask")); //$NON-NLS-1$
            addTaskItem.setImage(ImageLib.getImage(ImageLib.ADD_ACTION));
            addTaskItem.addSelectionListener(new SelectionAdapter() {

                /*
                 * (non-Javadoc)
                 * 
                 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org .eclipse .swt.events.SelectionEvent)
                 */
                @Override
                public void widgetSelected(SelectionEvent e) {
                    TreeItem[] selection = tree.getSelection();
                    if (selection.length > 0) {
                        TreeItem treeItem = selection[0];
                        ModelElementIndicator meIndicator = (ModelElementIndicator) treeItem.getData(MODELELEMENT_INDICATOR_KEY);
                        ModelElement me = meIndicator.getModelElement();
                        ModelElement ana = getAnalysis();
                        ana.setName(me.getName());
                        if (me instanceof ModelElement) {
                            (new TdAddTaskAction(tree.getShell(), ana)).run();
                        }
                    }

                }
            });

            MenuItem deleteMenuItem = new MenuItem(menu, SWT.CASCADE);
            deleteMenuItem.setText(DefaultMessagesImpl.getString("AnalysisColumnTreeViewer.removeElement")); //$NON-NLS-1$
            deleteMenuItem.setImage(ImageLib.getImage(ImageLib.DELETE_ACTION));
            deleteMenuItem.addSelectionListener(new SelectionAdapter() {

                /*
                 * (non-Javadoc)
                 * 
                 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org .eclipse .swt.events.SelectionEvent)
                 */
                @Override
                public void widgetSelected(SelectionEvent e) {
                    removeSelectedElements(tree);

                }

            });

            tree.setMenu(menu);
        }

        private void editPattern(Tree tree) {
            TreeItem[] selection = tree.getSelection();
            if (selection.length > 0) {
                TreeItem treeItem = selection[0];
                IndicatorUnit indicatorUnit = (IndicatorUnit) treeItem.getData(INDICATOR_UNIT_KEY);
                PatternMatchingIndicator indicator = (PatternMatchingIndicator) indicatorUnit.getIndicator();
                Pattern pattern = indicator.getParameters().getDataValidDomain().getPatterns().get(0);
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

        /**
         * DOC yyi Comment method "editUDIndicator" 2009-09-04.
         * 
         * @param tree
         */
        private void editUDIndicator(Tree tree) {
            TreeItem[] selection = tree.getSelection();
            if (selection.length > 0) {
                TreeItem treeItem = selection[0];
                IndicatorUnit indicatorUnit = (IndicatorUnit) treeItem.getData(INDICATOR_UNIT_KEY);
                UserDefIndicator indicator = (UserDefIndicator) indicatorUnit.getIndicator();

                IFolder userFolder = ResourceManager.getUDIFolder();
                IFile file = UDIResourceFileHelper.getInstance().getUDIFile(indicator.getIndicatorDefinition(),
                        new IFolder[] { userFolder });

                IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
                try {
                    activePage.openEditor(new FileEditorInput(file),
                            "org.talend.dataprofiler.core.ui.editor.indicator.IndicatorEditor"); //$NON-NLS-1$
                } catch (PartInitException e1) {
                    log.error(e1, e1);
                }
            }
        }

        /**
         * DOC Zqin Comment method "previewSelectedElements".
         * 
         * @param newTree
         */
        private void previewSelectedElements(Tree newTree) {
            TreeItem[] items = newTree.getSelection();
            ModelElement[] mes = new ModelElement[items.length];

            for (int i = 0; i < items.length; i++) {
                ModelElementIndicator meIndicator = (ModelElementIndicator) items[i].getData(MODELELEMENT_INDICATOR_KEY);
                ModelElement me = meIndicator.getModelElement();
                mes[i] = me;
            }

            new PreviewColumnAction(mes).run();
        }

        /**
         * 
         * DOC mzhao Comment method "viewQueryForSelectedElement".
         * 
         * @param newTree
         */
        private void viewQueryForSelectedElement(Tree newTree) {
            TreeItem[] selection = newTree.getSelection();
            for (TreeItem item : selection) {
                ModelElementIndicator meIndicator = (ModelElementIndicator) item.getData(MODELELEMENT_INDICATOR_KEY);
                ModelElement me = meIndicator.getModelElement();
                TdDataProvider dataprovider = ModelElementHelper.getTdDataProvider(me);
                IndicatorUnit indicatorUnit = (IndicatorUnit) item.getData(INDICATOR_UNIT_KEY);
                DbmsLanguage dbmsLang = DbmsLanguageFactory.createDbmsLanguage(dataprovider);
                Expression expression = dbmsLang.getInstantiatedExpression(indicatorUnit.getIndicator());
                if (expression == null) {
                    MessageDialogWithToggle
                            .openWarning(
                                    null,
                                    DefaultMessagesImpl.getString("AnalysisColumnTreeViewer.Warn"), DefaultMessagesImpl.getString("AnalysisColumnTreeViewer.NoQueryDefined")); //$NON-NLS-1$ //$NON-NLS-2$
                    return;
                }

                CorePlugin.getDefault().openInSqlEditor(dataprovider, expression.getBody(), me.getName());
            }
        }

        /**
         * DOC Zqin Comment method "showSelectedElements".MOD 2009-01-07 mzhao.
         * 
         * @param newTree
         */
        private void showSelectedElements(Tree newTree) {
            TreeItem[] selection = newTree.getSelection();

            DQRespositoryView dqview = CorePlugin.getDefault().getRepositoryView();
            if (selection.length == 1) {
                try {
                    ModelElementIndicator meIndicator = (ModelElementIndicator) selection[0].getData(MODELELEMENT_INDICATOR_KEY);
                    ModelElement me = meIndicator.getModelElement();
                    dqview.showSelectedElements(me);

                } catch (Exception e) {
                    log.error(e, e);
                }
            }
        }

        private boolean isSelectedColumn(TreeItem[] items) {
            for (TreeItem item : items) {
                if (item.getData(INDICATOR_UNIT_KEY) != null || item.getData(DATA_PARAM) != null) {
                    return false;
                }
            }

            return true;
        }

        private boolean isSelectedIndicator(TreeItem[] items) {

            if (isSelectedColumn(items)) {
                return false;
            }

            for (TreeItem item : items) {
                if (item.getData(DATA_PARAM) != null) {
                    return false;
                }
            }

            return true;
        }

        private boolean isSelectedPatternIndicator(TreeItem[] items) {
            if (!isSelectedIndicator(items)) {
                return false;
            }

            for (TreeItem item : items) {
                IndicatorUnit unit = (IndicatorUnit) item.getData(INDICATOR_UNIT_KEY);
                if (unit != null) {

                    Indicator indicator = unit.getIndicator();
                    if (!(indicator instanceof PatternMatchingIndicator)) {
                        return false;
                    }
                }
            }

            return true;
        }

        /**
         * DOC xqliu Comment method "isMdmSelected".
         * 
         * @param items
         * @return
         */
        private boolean isMdmSelected(TreeItem[] items) {
            for (TreeItem item : items) {
                Object data = item.getData(INDICATOR_UNIT_KEY);
                if (data != null) {
                    if (data instanceof IndicatorUnit) {
                        IndicatorUnit iu = (IndicatorUnit) data;
                        return iu.isXmlElement();
                    }
                }
            }
            return false;
        }

        /**
         * DOC yyi Comment method "isSelectedUDIndicator" 2009-09-04.
         * 
         * @param items
         * @return
         */
        private boolean isSelectedUDIndicator(TreeItem[] items) {
            if (!isSelectedIndicator(items)) {
                return false;
            }

            for (TreeItem item : items) {
                IndicatorUnit unit = (IndicatorUnit) item.getData(INDICATOR_UNIT_KEY);
                if (unit != null) {

                    Indicator indicator = unit.getIndicator();
                    if (!(indicator instanceof UserDefIndicator)) {
                        return false;
                    }
                }
            }

            return true;
        }
    }
}
