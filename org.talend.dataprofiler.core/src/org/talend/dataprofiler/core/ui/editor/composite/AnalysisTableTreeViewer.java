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
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DecorationOverlayIcon;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TreeEditor;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.CheckedTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.part.FileEditorInput;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.cwm.helper.DataProviderHelper;
import org.talend.cwm.helper.SchemaHelper;
import org.talend.cwm.helper.TaggedValueHelper;
import org.talend.cwm.relational.TdTable;
import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.dqrule.DQRuleUtilities;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.manager.DQStructureManager;
import org.talend.dataprofiler.core.model.TableIndicator;
import org.talend.dataprofiler.core.ui.action.actions.TdAddTaskAction;
import org.talend.dataprofiler.core.ui.action.actions.predefined.PreviewTableAction;
import org.talend.dataprofiler.core.ui.dialog.composite.TooltipTree;
import org.talend.dataprofiler.core.ui.editor.AbstractAnalysisActionHandler;
import org.talend.dataprofiler.core.ui.editor.AbstractMetadataFormPage;
import org.talend.dataprofiler.core.ui.editor.analysis.TableMasterDetailsPage;
import org.talend.dataprofiler.core.ui.editor.preview.IndicatorUnit;
import org.talend.dataprofiler.core.ui.editor.preview.TableIndicatorUnit;
import org.talend.dataprofiler.core.ui.perspective.ChangePerspectiveAction;
import org.talend.dataprofiler.core.ui.utils.OpeningHelpWizardDialog;
import org.talend.dataprofiler.core.ui.views.DQRespositoryView;
import org.talend.dataprofiler.core.ui.views.TableViewerDND;
import org.talend.dataprofiler.core.ui.wizard.indicator.TableIndicatorOptionsWizard;
import org.talend.dataprofiler.core.ui.wizard.indicator.forms.FormEnum;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.domain.Domain;
import org.talend.dataquality.indicators.CompositeIndicator;
import org.talend.dataquality.indicators.DateParameters;
import org.talend.dataquality.indicators.FrequencyIndicator;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.IndicatorParameters;
import org.talend.dataquality.indicators.RowCountIndicator;
import org.talend.dataquality.indicators.TextParameters;
import org.talend.dataquality.indicators.sql.WhereRuleIndicator;
import org.talend.dataquality.rules.WhereRule;
import org.talend.dq.dbms.DbmsLanguage;
import org.talend.dq.dbms.DbmsLanguageFactory;
import org.talend.dq.helper.resourcehelper.DQRuleResourceFileHelper;
import org.talend.dq.nodes.indicator.type.IndicatorEnum;
import orgomg.cwm.objectmodel.core.Expression;
import orgomg.cwm.objectmodel.core.ModelElement;
import orgomg.cwm.resource.relational.Table;

/**
 * DOC xqliu class global comment. Detailled comment
 */
public class AnalysisTableTreeViewer extends AbstractTableDropTree {

    private static final String DATA_PARAM = "DATA_PARAM"; //$NON-NLS-1$

    public static final String INDICATOR_UNIT_KEY = "INDICATOR_UNIT_KEY"; //$NON-NLS-1$

    public static final String TABLE_INDICATOR_KEY = "TABLE_INDICATOR_KEY"; //$NON-NLS-1$

    public static final String ITEM_EDITOR_KEY = "ITEM_EDITOR_KEY"; //$NON-NLS-1$

    public static final String VIEWER_KEY = "org.talend.dataprofiler.core.ui.editor.composite.AnalysisTableTreeViewer"; //$NON-NLS-1$

    private Composite parentComp;

    private Tree tree;

    private TableMasterDetailsPage masterPage;

    private TableIndicator[] tableIndicators;

    /**
     * DOC xqliu AnalysisTableTreeViewer constructor comment.
     * 
     * @param parent
     */
    public AnalysisTableTreeViewer(Composite parent) {
        parentComp = parent;
        this.tree = createTree(parent);
    }

    /**
     * DOC xqliu AnalysisTableTreeViewer constructor comment.
     * 
     * @param tree
     * @param tableMasterDetailsPage
     */
    public AnalysisTableTreeViewer(Composite parent, TableMasterDetailsPage masterPage) {
        this(parent);
        this.masterPage = masterPage;
        this.setElements(masterPage.getCurrentTableIndicators());
        this.setDirty(false);
    }

    private Tree createTree(Composite parent) {
        final Tree newTree = new TooltipTree(parent, SWT.MULTI | SWT.BORDER) {

            @Override
            protected boolean isValidItem(TreeItem item) {
                if (item == null) {
                    return false;
                }
                Object itemData = item.getData(INDICATOR_UNIT_KEY);
                if (itemData != null) {
                    TableIndicatorUnit indicatorUnit = (TableIndicatorUnit) item.getData(INDICATOR_UNIT_KEY);
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
                    return DefaultMessagesImpl.getString("AnalysisTableTreeViewer.queryNotGen"); //$NON-NLS-1$
                }
                return expCont;
            }
        };
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(newTree);

        newTree.setHeaderVisible(true);
        TreeColumn columnTableName = new TreeColumn(newTree, SWT.CENTER);
        columnTableName.setWidth(190);
        columnTableName.setText(DefaultMessagesImpl.getString("AnalysisTableTreeViewer.analyzedTables")); //$NON-NLS-1$
        TreeColumn columnDQRule = new TreeColumn(newTree, SWT.CENTER);
        columnDQRule.setWidth(80);
        columnDQRule.setText(DefaultMessagesImpl.getString("AnalysisTableTreeViewer.dqrule")); //$NON-NLS-1$
        TreeColumn columnOperation = new TreeColumn(newTree, SWT.CENTER);
        columnOperation.setWidth(80);
        columnOperation.setText(DefaultMessagesImpl.getString("AnalysisTableTreeViewer.operation")); //$NON-NLS-1$

        parent.layout();
        AbstractAnalysisActionHandler actionHandler = new AbstractAnalysisActionHandler(parent) {

            @Override
            protected void handleRemove() {
                // removeSelectedElements(newTree);
            }

        };
        parent.setData(AbstractMetadataFormPage.ACTION_HANDLER, actionHandler);
        TableViewerDND.installDND(newTree); // TODO xqliu add code for drag&drop
        this.addTreeListener(newTree);
        newTree.setData(this);
        return newTree;
    }

    private void addTreeListener(final Tree tree) {
        tree.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                TreeItem item = (TreeItem) e.item;
                if (DATA_PARAM.equals(item.getData(DATA_PARAM))) {
                    tree.setMenu(null);
                } else {
                    new TableTreeMenuProvider(tree).createTreeMenu();
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
                TreeItem item = tree.getSelection()[0];
                if (item != null) {
                    Object indicatorobj = item.getData(INDICATOR_UNIT_KEY);
                    Object tableobj = item.getData(TABLE_INDICATOR_KEY);
                    if (tableobj != null && indicatorobj == null) {
                        // open DQ Rule selector
                        showAddDQRuleDialog(item, (TableIndicator) tableobj);
                    } else if (tableobj != null && indicatorobj != null) {
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

        Object obj = e.item.getData(TABLE_INDICATOR_KEY);
        if (obj instanceof TableIndicator) {
            TableIndicator tableIndicator = (TableIndicator) obj;
            for (Composite comp : previewChartCompsites) {
                if (comp.getData() == tableIndicator) {
                    return (ExpandableComposite) comp;
                }
            }
        }

        return null;
    }

    private void setElements(final TableIndicator[] elements) {
        this.tree.dispose();
        this.tree = createTree(this.parentComp);
        tree.setData(VIEWER_KEY, this);
        this.tableIndicators = elements;
        addItemElements(elements);
    }

    private void addItemElements(final TableIndicator[] elements) {
        for (int i = 0; i < elements.length; i++) {
            final TreeItem treeItem = new TreeItem(tree, SWT.NONE);
            final TableIndicator tableIndicator = elements[i];
            treeItem.setImage(ImageLib.getImage(ImageLib.TABLE));
            treeItem.setText(0, tableIndicator.getTdTable().getName());
            treeItem.setData(TABLE_INDICATOR_KEY, tableIndicator);

            TreeEditor addDQRuleEditor = new TreeEditor(tree);
            Label addDQRuleLabl = new Label(tree, SWT.NONE);
            addDQRuleLabl.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
            addDQRuleLabl.setImage(ImageLib.getImage(ImageLib.ADD_DQ));
            addDQRuleLabl.setToolTipText(DefaultMessagesImpl.getString("AnalysisTableTreeViewer.addDQRule")); //$NON-NLS-1$
            addDQRuleLabl.pack();
            addDQRuleLabl.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseDown(MouseEvent e) {
                    showAddDQRuleDialog(treeItem, tableIndicator);
                }

            });
            addDQRuleEditor.minimumWidth = addDQRuleLabl.getImage().getBounds().width;
            addDQRuleEditor.setEditor(addDQRuleLabl, treeItem, 1);

            TreeEditor delLabelEditor = new TreeEditor(tree);
            Label delLabel = new Label(tree, SWT.NONE);
            delLabel.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
            delLabel.setImage(ImageLib.getImage(ImageLib.DELETE_ACTION));
            delLabel.setToolTipText(DefaultMessagesImpl.getString("AnalysisTableTreeViewer.delete")); //$NON-NLS-1$
            delLabel.pack();
            delLabel.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseDown(MouseEvent e) {
                    deleteTableItems(tableIndicator);
                    if (treeItem.getParentItem() != null && treeItem.getParentItem().getData(INDICATOR_UNIT_KEY) != null) {
                        setElements(tableIndicators);
                    } else {
                        removeItemBranch(treeItem);
                    }
                }
            });
            delLabelEditor.minimumWidth = delLabel.getImage().getBounds().width;
            delLabelEditor.horizontalAlignment = SWT.CENTER;
            delLabelEditor.setEditor(delLabel, treeItem, 2);
            treeItem.setData(ITEM_EDITOR_KEY, new TreeEditor[] { addDQRuleEditor, delLabelEditor });
            if (tableIndicator.hasIndicators()) {
                createIndicatorItems(treeItem, tableIndicator.getIndicatorUnits());
            }
            treeItem.setExpanded(true);
        }
        this.setDirty(true);
    }

    /**
     * DOC xqliu Comment method "showAddDQRuleDialog".
     * 
     * @param treeItem
     * @param tableIndicator
     */
    private void showAddDQRuleDialog(final TreeItem treeItem, final TableIndicator tableIndicator) {
        // Button addDQRuleBtn = new Button(tree, SWT.NONE);
        //            addDQRuleBtn.setText(DefaultMessagesImpl.getString("AnalysisTableTreeViewer.addDQRule")); //$NON-NLS-1$
        // addDQRuleBtn.pack();
        // addDQRuleBtn.addSelectionListener(new SelectionAdapter() {

        CheckedTreeSelectionDialog dialog = new CheckedTreeSelectionDialog(null, new DQRuleLabelProvider(),
                new WorkbenchContentProvider());

        IProject defaultPatternFolder = ResourcesPlugin.getWorkspace().getRoot().getProject(DQStructureManager.LIBRARIES);
        dialog.setInput(defaultPatternFolder);
        dialog.setValidator(new ISelectionStatusValidator() {

            public IStatus validate(Object[] selection) {
                IStatus status = Status.OK_STATUS;
                for (Object whereRule : selection) {
                    if (whereRule instanceof IFile) {
                        IFile file = (IFile) whereRule;
                        if (FactoriesUtil.DQRULE.equals(file.getFileExtension())) {
                            WhereRule findWhereRule = DQRuleResourceFileHelper.getInstance().findWhereRule(file);
                            boolean validStatus = TaggedValueHelper.getValidStatus(findWhereRule);
                            if (!validStatus) {
                                status = new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, DefaultMessagesImpl
                                        .getString("AnalysisTableTreeViewer.chooseValidDQRules")); //$NON-NLS-1$
                            }
                        }
                    }
                }
                return status;
            }

        });
        dialog.addFilter(new ViewerFilter() {

            @Override
            public boolean select(Viewer viewer, Object parentElement, Object element) {
                if (element instanceof IFile) {
                    IFile file = (IFile) element;
                    if (FactoriesUtil.DQRULE.equals(file.getFileExtension())) {
                        return true;
                    }
                } else if (element instanceof IFolder) {
                    IFolder folder = (IFolder) element;
                    return DQRuleUtilities.isLibraiesSubfolder(folder, DQStructureManager.DQ_RULES);
                }
                return false;
            }
        });
        dialog.setContainerMode(true);
        dialog.setTitle(DefaultMessagesImpl.getString("AnalysisTableTreeViewer.dqruleSelector")); //$NON-NLS-1$
        dialog.setMessage(DefaultMessagesImpl.getString("AnalysisTableTreeViewer.dqrules")); //$NON-NLS-1$
        dialog.setSize(80, 30);
        dialog.create();
        if (dialog.open() == Window.OK) {
            for (Object obj : dialog.getResult()) {
                if (obj instanceof IFile) {
                    IFile file = (IFile) obj;
                    TableIndicatorUnit addIndicatorUnit = DQRuleUtilities
                            .createIndicatorUnit(file, tableIndicator, getAnalysis());
                    if (addIndicatorUnit != null) {
                        createOneUnit(treeItem, addIndicatorUnit);
                        setDirty(true);
                    }
                }
            }
        }
    }

    private void createIndicatorItems(TreeItem treeItem, TableIndicatorUnit[] indicatorUnits) {
        for (TableIndicatorUnit indicatorUnit : indicatorUnits) {
            createOneUnit(treeItem, indicatorUnit);
        }
    }

    public void createOneUnit(final TreeItem treeItem, TableIndicatorUnit indicatorUnit) {
        final TreeItem indicatorItem = new TreeItem(treeItem, SWT.NONE);
        final TableIndicatorUnit unit = indicatorUnit;
        IndicatorEnum type = indicatorUnit.getType();
        final IndicatorEnum indicatorEnum = type;
        indicatorItem.setData(TABLE_INDICATOR_KEY, treeItem.getData(TABLE_INDICATOR_KEY));
        indicatorItem.setData(INDICATOR_UNIT_KEY, unit);
        indicatorItem.setData(VIEWER_KEY, this);
        String label = indicatorUnit.getIndicatorName();
        if (IndicatorEnum.WhereRuleIndicatorEnum.compareTo(type) == 0) {
            indicatorItem.setImage(0, ImageLib.getImage(ImageLib.DQ_RULE));
        }
        indicatorItem.setText(0, label);

        TreeEditor optionEditor = new TreeEditor(tree);
        Label optionLabel = new Label(tree, SWT.NONE);
        optionLabel.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
        optionLabel.setImage(ImageLib.getImage(ImageLib.INDICATOR_OPTION));
        optionLabel.setToolTipText(DefaultMessagesImpl.getString("AnalysisTableTreeViewer.options")); //$NON-NLS-1$
        optionLabel.pack();
        optionLabel.setData(indicatorUnit);
        optionLabel.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseDown(MouseEvent e) {
                openIndicatorOptionDialog(null, indicatorItem);
            }

        });

        optionEditor.minimumWidth = optionLabel.getImage().getBounds().width;
        optionEditor.horizontalAlignment = SWT.CENTER;
        optionEditor.setEditor(optionLabel, indicatorItem, 1);

        TreeEditor delEditor = null;
        if (!(unit.getIndicator() instanceof RowCountIndicator)) {
            delEditor = new TreeEditor(tree);
            Label delLabel = new Label(tree, SWT.NONE);
            delLabel.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
            delLabel.setImage(ImageLib.getImage(ImageLib.DELETE_ACTION));
            delLabel.setToolTipText(DefaultMessagesImpl.getString("AnalysisTableTreeViewer.delete")); //$NON-NLS-1$
            delLabel.pack();
            delLabel.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseDown(MouseEvent e) {
                    deleteIndicatorItems((TableIndicator) treeItem.getData(TABLE_INDICATOR_KEY), unit);
                    if (indicatorItem.getParentItem() != null
                            && indicatorItem.getParentItem().getData(INDICATOR_UNIT_KEY) != null) {
                        setElements(tableIndicators);
                    } else {
                        removeItemBranch(indicatorItem);
                    }
                }

            });

            delEditor.minimumWidth = delLabel.getImage().getBounds().width;
            delEditor.horizontalAlignment = SWT.CENTER;
            delEditor.setEditor(delLabel, indicatorItem, 2);
        }

        if (delEditor == null) {
            indicatorItem.setData(ITEM_EDITOR_KEY, new TreeEditor[] { optionEditor });
        } else {
            indicatorItem.setData(ITEM_EDITOR_KEY, new TreeEditor[] { optionEditor, delEditor });
        }
        if (indicatorEnum != null && indicatorEnum.hasChildren()) {
            indicatorItem.setData(treeItem.getData(TABLE_INDICATOR_KEY));
            createIndicatorItems(indicatorItem, indicatorUnit.getChildren());
        }
        createIndicatorParameters(indicatorItem, indicatorUnit);
    }

    public void openIndicatorOptionDialog(Shell shell, TreeItem indicatorItem) {
        if (isDirty()) {
            masterPage.doSave(null);
        }
        TableIndicatorUnit indicatorUnit = (TableIndicatorUnit) indicatorItem.getData(INDICATOR_UNIT_KEY);
        TableIndicatorOptionsWizard wizard = new TableIndicatorOptionsWizard(indicatorUnit);
        if (FormEnum.isExsitingForm(indicatorUnit)) {
            String href = FormEnum.getFirstFormHelpHref(indicatorUnit);
            OpeningHelpWizardDialog optionDialog = new OpeningHelpWizardDialog(shell, wizard, href);
            optionDialog.create();
            if (Window.OK == optionDialog.open()) {
                setDirty(wizard.isDirty());
                createIndicatorParameters(indicatorItem, indicatorUnit);
            }
        } else {
            MessageDialogWithToggle.openInformation(null, DefaultMessagesImpl.getString("AnalysisTableTreeViewer.information"), //$NON-NLS-1$
                    DefaultMessagesImpl.getString("AnalysisTableTreeViewer.nooption")); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    /**
     * DOC xqliu Comment method "createIndicatorParameters".
     * 
     * @param indicatorItem
     * @param indicatorUnit
     */
    private void createIndicatorParameters(TreeItem indicatorItem, TableIndicatorUnit indicatorUnit) {
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

    private void deleteTableItems(TableIndicator deleteTableIndiciator) {
        TableIndicator[] remainIndicators = new TableIndicator[tableIndicators.length - 1];
        int i = 0;
        for (TableIndicator indicator : tableIndicators) {
            if (deleteTableIndiciator.equals(indicator)) {
                continue;
            } else {
                remainIndicators[i] = indicator;
                i++;
            }
        }
        this.tableIndicators = remainIndicators;
    }

    public TableIndicator[] getTableIndicator() {
        return this.tableIndicators;
    }

    /**
     * DOC xqliu Comment method "setInput".
     * 
     * @param columns
     */
    public void setInput(Object[] objs) {
        if (objs != null && objs.length != 0) {
            if (!(objs[0] instanceof TdTable)) {
                return;
            }
        }
        List<TdTable> tableList = new ArrayList<TdTable>();
        for (Object obj : objs) {
            tableList.add((TdTable) obj);
        }
        List<TableIndicator> tableIndicatorList = new ArrayList<TableIndicator>();
        for (TableIndicator tableIndicator : tableIndicators) {
            if (tableList.contains(tableIndicator.getTdTable())) {
                tableIndicatorList.add(tableIndicator);
                tableList.remove(tableIndicator.getTdTable());
            }
        }
        for (TdTable table : tableList) {
            TableIndicator tableIndicator = TableIndicator.createTableIndicatorWithRowCountIndicator(table);
            tableIndicatorList.add(tableIndicator);
        }
        this.tableIndicators = tableIndicatorList.toArray(new TableIndicator[tableIndicatorList.size()]);
        this.setElements(tableIndicators);
    }

    /**
     * DOC xqliu Comment method "getTree".
     * 
     * @return
     */
    public Tree getTree() {
        return this.tree;
    }

    private String isExpressionNull(TreeItem item) {
        String expressContent = null;
        // TODO xqliu add method code!!!
        return expressContent;
    }

    /**
     * 
     * DOC xqliu Comment method "addElements".
     * 
     * @param elements
     */
    public void addElements(final TableIndicator[] elements) {
        TableIndicator[] newsArray = new TableIndicator[this.tableIndicators.length + elements.length];
        System.arraycopy(this.tableIndicators, 0, newsArray, 0, this.tableIndicators.length);
        for (int i = 0; i < elements.length; i++) {
            newsArray[this.tableIndicators.length + i] = elements[i];
        }
        this.tableIndicators = newsArray;
        this.addItemElements(elements);
    }

    /**
     * DOC xqliu Comment method "getAnalysis".
     * 
     * @return
     */
    public Analysis getAnalysis() {
        return this.masterPage.getAnalysisHandler().getAnalysis();
    }

    /**
     * DOC xqliu AnalysisTableTreeViewer class global comment. Detailled comment
     */
    class DQRuleLabelProvider extends LabelProvider {

        @Override
        public Image getImage(Object element) {
            if (element instanceof IFolder) {
                return ImageLib.getImage(ImageLib.FOLDERNODE_IMAGE);
            }

            if (element instanceof IFile) {
                WhereRule findWhereRule = DQRuleResourceFileHelper.getInstance().findWhereRule((IFile) element);
                boolean validStatus = TaggedValueHelper.getValidStatus(findWhereRule);
                ImageDescriptor imageDescriptor = ImageLib.getImageDescriptor(ImageLib.DQ_RULE);
                if (!validStatus) {
                    ImageDescriptor warnImg = PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(
                            ISharedImages.IMG_OBJS_WARN_TSK);
                    DecorationOverlayIcon icon = new DecorationOverlayIcon(imageDescriptor.createImage(), warnImg,
                            IDecoration.BOTTOM_RIGHT);
                    imageDescriptor = icon;
                }
                return imageDescriptor.createImage();
            }

            return null;
        }

        @Override
        public String getText(Object element) {
            if (element instanceof IFile) {
                IFile file = (IFile) element;
                WhereRule whereRule = DQRuleResourceFileHelper.getInstance().findWhereRule(file);
                if (whereRule != null) {
                    return whereRule.getName();
                }
            }

            if (element instanceof IFolder) {
                return ((IFolder) element).getName();
            }

            return ""; //$NON-NLS-1$
        }
    }

    @Override
    public void dropTables(List<Table> tables, int index) {
        int size = tables.size();
        TableIndicator[] tIndicators = new TableIndicator[size];
        for (int i = 0; i < size; i++) {
            Table table = tables.get(i);
            TableIndicator tableIndicator = TableIndicator.createTableIndicatorWithRowCountIndicator((TdTable) table);
            tIndicators[i] = tableIndicator;
        }
        this.addElements(tIndicators);
    }

    @Override
    public void dropWhereRules(Object data, List<WhereRule> whereRules, int index) {
        // TODO drag&dropped the WhereRule
    }

    @Override
    public boolean canDrop(Table table) {
        List<TdTable> existTables = new ArrayList<TdTable>();

        for (TableIndicator tableIndicator : getTableIndicator()) {
            existTables.add(tableIndicator.getTdTable());
        }
        if (existTables.contains(table)) {
            return false;
        }
        return true;
    }

    @Override
    public boolean canDrop(Object data, WhereRule whereRule) {
        // TODO drag&dropped the WhereRule
        return false;
    }

    private void deleteIndicatorItems(TableIndicator tableIndicator, TableIndicatorUnit inidicatorUnit) {
        tableIndicator.removeIndicatorUnit(inidicatorUnit);
    }

    private void removeSelectedElements(Tree newTree) {
        TreeItem[] selection = newTree.getSelection();
        boolean branchIndicatorExist = false;
        for (TreeItem item : selection) {
            TableIndicatorUnit indicatorUnit = (TableIndicatorUnit) item.getData(INDICATOR_UNIT_KEY);
            if (indicatorUnit != null) {
                deleteIndicatorItems((TableIndicator) item.getData(TABLE_INDICATOR_KEY), indicatorUnit);
            } else {
                deleteTableItems((TableIndicator) item.getData(TABLE_INDICATOR_KEY));
            }
            if (item.getParentItem() != null && item.getParentItem().getData(INDICATOR_UNIT_KEY) != null) {
                branchIndicatorExist = true;
                continue;
            } else {
                removeItemBranch(item);
            }

        }
        if (branchIndicatorExist) {
            setElements(tableIndicators);
        }
    }

    /**
     * DOC xqliu AnalysisTableTreeViewer class global comment. Detailled comment
     */
    class TableTreeMenuProvider {

        private Tree tree;

        public TableTreeMenuProvider(Tree tree) {
            this.tree = tree;
        }

        /**
         * DOC xqliu Comment method "createTreeMenu".
         */
        public void createTreeMenu() {
            Menu oldMenu = tree.getMenu();
            if (oldMenu != null && !oldMenu.isDisposed()) {
                oldMenu.dispose();
            }
            Menu menu = new Menu(tree);

            if (isSelectedTable(tree.getSelection())) {
                MenuItem previewMenuItem = new MenuItem(menu, SWT.CASCADE);
                previewMenuItem.setText(DefaultMessagesImpl.getString("AnalysisTableTreeViewer.previewDQElement")); //$NON-NLS-1$
                previewMenuItem.setImage(ImageLib.getImage(ImageLib.EXPLORE_IMAGE));
                previewMenuItem.addSelectionListener(new SelectionAdapter() {

                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        previewSelectedElements(tree);
                    }

                });

                MenuItem showLocationMenuItem = new MenuItem(menu, SWT.CASCADE);
                showLocationMenuItem.setText(DefaultMessagesImpl.getString("AnalysisTableTreeViewer.showDQElement")); //$NON-NLS-1$
                showLocationMenuItem.setImage(ImageLib.getImage(ImageLib.EXPLORE_IMAGE));
                showLocationMenuItem.addSelectionListener(new SelectionAdapter() {

                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        showSelectedElements(tree);
                    }

                });
            }

            if (isSelectedIndicator(tree.getSelection())) {
                MenuItem showQueryMenuItem = new MenuItem(menu, SWT.CASCADE);
                showQueryMenuItem.setText(DefaultMessagesImpl.getString("AnalysisColumnTreeViewer.viewQuery")); //$NON-NLS-1$
                showQueryMenuItem.setImage(ImageLib.getImage(ImageLib.EXPLORE_IMAGE));
                showQueryMenuItem.addSelectionListener(new SelectionAdapter() {

                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        viewQueryForSelectedElement(tree);

                    }
                });
            }

            if (isSelectedWhereRuleIndicator(tree.getSelection())) {
                MenuItem editWhereRUleMenuItem = new MenuItem(menu, SWT.CASCADE);
                editWhereRUleMenuItem.setText(DefaultMessagesImpl.getString("AnalysisTableTreeViewer.editDQRule")); //$NON-NLS-1$
                editWhereRUleMenuItem.setImage(ImageLib.getImage(ImageLib.DQ_RULE));
                editWhereRUleMenuItem.addSelectionListener(new SelectionAdapter() {

                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        editWhereRule(tree);
                    }

                });
            }

            MenuItem addTaskItem = new MenuItem(menu, SWT.CASCADE);
            addTaskItem.setText(DefaultMessagesImpl.getString("AnalysisTableTreeViewer.AddTask")); //$NON-NLS-1$
            addTaskItem.setImage(ImageLib.getImage(ImageLib.ADD_ACTION));
            addTaskItem.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(SelectionEvent e) {
                    TreeItem[] selection = tree.getSelection();
                    if (selection.length > 0) {
                        TreeItem treeItem = selection[0];
                        TableIndicator tableIndicator = (TableIndicator) treeItem.getData(TABLE_INDICATOR_KEY);
                        TdTable table = tableIndicator.getTdTable();
                        ModelElement me = getAnalysis();
                        me.setName(table.getName());
                        if (table instanceof ModelElement) {
                            (new TdAddTaskAction(tree.getShell(), me)).run();
                        }
                    }

                }
            });

            MenuItem deleteMenuItem = new MenuItem(menu, SWT.CASCADE);
            deleteMenuItem.setText(DefaultMessagesImpl.getString("AnalysisColumnTreeViewer.removeElement")); //$NON-NLS-1$
            deleteMenuItem.setImage(ImageLib.getImage(ImageLib.DELETE_ACTION));
            deleteMenuItem.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(SelectionEvent e) {
                    removeSelectedElements(tree);
                }

            });

            tree.setMenu(menu);
        }

        private void editWhereRule(Tree tree) {
            TreeItem[] selection = tree.getSelection();
            if (selection.length > 0) {
                TreeItem treeItem = selection[0];
                TableIndicatorUnit indicatorUnit = (TableIndicatorUnit) treeItem.getData(INDICATOR_UNIT_KEY);
                WhereRuleIndicator indicator = (WhereRuleIndicator) indicatorUnit.getIndicator();
                WhereRule whereRule = (WhereRule) indicator.getIndicatorDefinition();
                IFolder whereRuleFolder = ResourcesPlugin.getWorkspace().getRoot().getProject(DQStructureManager.LIBRARIES)
                        .getFolder(DQStructureManager.DQ_RULES);
                IFile file = DQRuleResourceFileHelper.getInstance()
                        .getWhereRuleFile(whereRule, new IFolder[] { whereRuleFolder });
                IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
                try {
                    activePage.openEditor(new FileEditorInput(file),
                            "org.talend.dataprofiler.core.ui.editor.dqrules.DQRuleEditor"); //$NON-NLS-1$
                } catch (PartInitException e1) {
                    e1.printStackTrace();
                }
            }
        }

        private void previewSelectedElements(Tree newTree) {
            TreeItem[] items = newTree.getSelection();
            TdTable[] tables = new TdTable[items.length];

            for (int i = 0; i < items.length; i++) {
                TableIndicator tableIndicator = (TableIndicator) items[i].getData(TABLE_INDICATOR_KEY);
                TdTable table = tableIndicator.getTdTable();
                tables[i] = table;
            }

            new PreviewTableAction(tables[0]).run();
        }

        private void viewQueryForSelectedElement(Tree newTree) {
            TreeItem[] selection = newTree.getSelection();
            for (TreeItem item : selection) {
                TableIndicator tableIndicator = (TableIndicator) item.getData(TABLE_INDICATOR_KEY);
                TdTable table = tableIndicator.getTdTable();
                TdDataProvider dataprovider = DataProviderHelper.getTdDataProvider(SchemaHelper.getParentSchema(table));
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
                new ChangePerspectiveAction(PluginConstant.SE_ID).run();
                String query = expression.getBody();
                CorePlugin.getDefault().openInSqlEditor(dataprovider, query, table.getName());
            }
        }

        private void showSelectedElements(Tree newTree) {
            TreeItem[] selection = newTree.getSelection();

            DQRespositoryView dqview = (DQRespositoryView) CorePlugin.getDefault().findView(DQRespositoryView.ID);
            if (selection.length == 1) {
                try {
                    TableIndicator tableIndicator = (TableIndicator) selection[0].getData(TABLE_INDICATOR_KEY);
                    TdTable table = tableIndicator.getTdTable();
                    dqview.showSelectedElements(table);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        private boolean isSelectedTable(TreeItem[] items) {
            for (TreeItem item : items) {
                if (item.getData(INDICATOR_UNIT_KEY) != null || item.getData(DATA_PARAM) != null) {
                    return false;
                }
            }

            return true;
        }

        private boolean isSelectedIndicator(TreeItem[] items) {

            if (isSelectedTable(items)) {
                return false;
            }

            for (TreeItem item : items) {
                if (item.getData(DATA_PARAM) != null) {
                    return false;
                }
            }

            return true;
        }

        private boolean isSelectedWhereRuleIndicator(TreeItem[] items) {
            if (!isSelectedIndicator(items)) {
                return false;
            }

            for (TreeItem item : items) {
                TableIndicatorUnit unit = (TableIndicatorUnit) item.getData(INDICATOR_UNIT_KEY);
                if (unit != null) {

                    Indicator indicator = unit.getIndicator();
                    if (!(indicator instanceof WhereRuleIndicator)) {
                        return false;
                    }
                }
            }

            return true;
        }
    }
}
