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
package org.talend.dataprofiler.core.ui.editor.analysis;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.JFreeChart;
import org.jfree.experimental.chart.swt.ChartComposite;
import org.talend.cwm.helper.ColumnHelper;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.cwm.relational.TdColumn;
import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.exception.DataprofilerCoreException;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.model.ColumnIndicator;
import org.talend.dataprofiler.core.ui.action.actions.RunAnalysisAction;
import org.talend.dataprofiler.core.ui.dialog.ColumnsSelectionDialog;
import org.talend.dataprofiler.core.ui.editor.composite.AnalysisColumnTreeViewer;
import org.talend.dataprofiler.core.ui.editor.composite.DataFilterComp;
import org.talend.dataprofiler.core.ui.editor.preview.CompositeIndicator;
import org.talend.dataprofiler.core.ui.editor.preview.IndicatorUnit;
import org.talend.dataprofiler.core.ui.editor.preview.model.ChartTypeStatesOperator;
import org.talend.dataprofiler.core.ui.editor.preview.model.states.IChartTypeStates;
import org.talend.dataprofiler.core.ui.utils.ChartUtils;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.analysis.ExecutionLanguage;
import org.talend.dataquality.helpers.MetadataHelper;
import org.talend.dataquality.indicators.DataminingType;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dq.analysis.ColumnAnalysisHandler;
import org.talend.dq.helper.EObjectHelper;
import org.talend.dq.helper.resourcehelper.AnaResourceFileHelper;
import org.talend.dq.helper.resourcehelper.PrvResourceFileHelper;
import org.talend.dq.indicators.preview.EIndicatorChartType;
import org.talend.utils.sugars.ReturnCode;
import orgomg.cwm.objectmodel.core.ModelElement;
import orgomg.cwm.resource.relational.Column;

/**
 * @author rli
 * 
 */
public class ColumnMasterDetailsPage extends AbstractAnalysisMetadataPage implements PropertyChangeListener {

    private static Logger log = Logger.getLogger(ColumnMasterDetailsPage.class);

    private String execLang;

    AnalysisColumnTreeViewer treeViewer;

    DataFilterComp dataFilterComp;

    ColumnAnalysisHandler analysisHandler;

    private ColumnIndicator[] currentColumnIndicators;

    private String stringDataFilter;

    private Composite chartComposite;

    private ScrolledForm form;

    private static final int TREE_MAX_LENGTH = 400;

    private Composite[] previewChartCompsites;

    private AnalysisEditor currentEditor;

    private Section dataFilterSection = null;

    private Section analysisColumnSection = null;

    private Section analysisParamSection = null;

    private Section previewSection = null;

    private List<ExpandableComposite> previewChartList = null;

    public ColumnMasterDetailsPage(FormEditor editor, String id, String title) {
        super(editor, id, title);
        currentEditor = (AnalysisEditor) editor;
    }

    public void initialize(FormEditor editor) {
        super.initialize(editor);
        analysisHandler = new ColumnAnalysisHandler();
        analysisHandler.setAnalysis((Analysis) this.currentModelElement);
        stringDataFilter = analysisHandler.getStringDataFilter();
        EList<ModelElement> analyzedColumns = analysisHandler.getAnalyzedColumns();
        List<ColumnIndicator> columnIndicatorList = new ArrayList<ColumnIndicator>();
        ColumnIndicator currentColumnIndicator;
        for (ModelElement element : analyzedColumns) {
            TdColumn tdColumn = SwitchHelpers.COLUMN_SWITCH.doSwitch(element);
            if (tdColumn == null) {
                continue;
            }
            currentColumnIndicator = new ColumnIndicator(tdColumn);
            DataminingType dataminingType = DataminingType.get(analysisHandler.getDatamingType(tdColumn));
            MetadataHelper.setDataminingType(dataminingType == null ? DataminingType.NOMINAL : dataminingType, tdColumn);
            Collection<Indicator> indicatorList = analysisHandler.getIndicators(tdColumn);
            currentColumnIndicator.setIndicators(indicatorList.toArray(new Indicator[indicatorList.size()]));
            columnIndicatorList.add(currentColumnIndicator);
        }
        currentColumnIndicators = columnIndicatorList.toArray(new ColumnIndicator[columnIndicatorList.size()]);
    }

    // @Override
    // protected ModelElement getCurrentModelElement(FormEditor editor) {
    //
    // FileEditorInput input = (FileEditorInput) editor.getEditorInput();
    // Analysis findAnalysis = AnaResourceFileHelper.getInstance().findAnalysis(input.getFile());
    // return findAnalysis;
    // }

    @Override
    protected void createFormContent(IManagedForm managedForm) {
        this.form = managedForm.getForm();
        Composite body = form.getBody();

        body.setLayout(new GridLayout());
        SashForm sForm = new SashForm(body, SWT.NULL);
        sForm.setLayoutData(new GridData(GridData.FILL_BOTH));

        topComp = toolkit.createComposite(sForm);
        topComp.setLayoutData(new GridData(GridData.FILL_BOTH));
        topComp.setLayout(new GridLayout());
        metadataSection = creatMetadataSection(form, topComp);
        form.setText(DefaultMessagesImpl.getString("ColumnMasterDetailsPage.columnAna")); //$NON-NLS-1$
        metadataSection.setText(DefaultMessagesImpl.getString("ColumnMasterDetailsPage.analysisMeta")); //$NON-NLS-1$
        metadataSection.setDescription(DefaultMessagesImpl.getString("ColumnMasterDetailsPage.setPropOfAnalysis")); //$NON-NLS-1$

        createAnalysisColumnsSection(form, topComp);

        createDataFilterSection(form, topComp);

        createAnalysisParamSection(form, topComp);

        Composite previewComp = toolkit.createComposite(sForm);
        previewComp.setLayoutData(new GridData(GridData.FILL_BOTH));
        previewComp.setLayout(new GridLayout());

        createPreviewSection(form, previewComp);

        // MOD 2009-01-10 mzhao, for register sections that would be collapse or expand later.
        currentEditor.registerSections(new Section[] { analysisColumnSection, metadataSection, dataFilterSection,
                analysisParamSection, previewSection });
    }

    void createAnalysisColumnsSection(final ScrolledForm form, Composite anasisDataComp) {
        analysisColumnSection = createSection(form, anasisDataComp, DefaultMessagesImpl
                .getString("ColumnMasterDetailsPage.analyzeColumn"), false, null); //$NON-NLS-1$

        Composite topComp = toolkit.createComposite(analysisColumnSection);
        topComp.setLayout(new GridLayout());

        Hyperlink clmnBtn = toolkit.createHyperlink(topComp, DefaultMessagesImpl
                .getString("ColumnMasterDetailsPage.selectColumn"), SWT.NONE); //$NON-NLS-1$
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).applyTo(clmnBtn);
        clmnBtn.addHyperlinkListener(new HyperlinkAdapter() {

            public void linkActivated(HyperlinkEvent e) {
                openColumnsSelectionDialog();
            }

        });

        Hyperlink indcBtn = toolkit.createHyperlink(topComp, DefaultMessagesImpl
                .getString("ColumnMasterDetailsPage.selectIndicator"), SWT.NONE); //$NON-NLS-1$
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).applyTo(indcBtn);
        indcBtn.addHyperlinkListener(new HyperlinkAdapter() {

            public void linkActivated(HyperlinkEvent e) {
                treeViewer.openIndicatorSelectDialog(null);
            }

        });

        Composite actionBarComp = toolkit.createComposite(topComp);
        GridLayout gdLayout = new GridLayout();
        gdLayout.numColumns = 2;
        actionBarComp.setLayout(gdLayout);

        ImageHyperlink collapseAllImageLink = toolkit.createImageHyperlink(actionBarComp, SWT.NONE);
        collapseAllImageLink.setToolTipText(DefaultMessagesImpl.getString("CollapseAllColumns")); //$NON-NLS-1$
        collapseAllImageLink.setImage(ImageLib.getImage(ImageLib.COLLAPSE_ALL));
        collapseAllImageLink.addHyperlinkListener(new HyperlinkAdapter() {

            public void linkActivated(HyperlinkEvent e) {
                TreeItem[] items = treeViewer.getTree().getItems();
                expandTreeItems(items, false);
                packOtherColumns();
            }
        });

        ImageHyperlink expandAllImageLink = toolkit.createImageHyperlink(actionBarComp, SWT.NONE);
        expandAllImageLink.setToolTipText(DefaultMessagesImpl.getString("ExpandAllColumns")); //$NON-NLS-1$
        expandAllImageLink.setImage(ImageLib.getImage(ImageLib.EXPAND_ALL));
        expandAllImageLink.addHyperlinkListener(new HyperlinkAdapter() {

            public void linkActivated(HyperlinkEvent e) {
                TreeItem[] items = treeViewer.getTree().getItems();
                expandTreeItems(items, true);
                packOtherColumns();

            }
        });

        Composite tree = toolkit.createComposite(topComp, SWT.NONE);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(tree);
        tree.setLayout(new GridLayout());
        ((GridData) tree.getLayoutData()).heightHint = TREE_MAX_LENGTH;

        treeViewer = new AnalysisColumnTreeViewer(tree, this);
        treeViewer.setDirty(false);
        treeViewer.addPropertyChangeListener(this);
        analysisColumnSection.setClient(topComp);

    }

    /**
     * 
     * DOC mzhao Comment method "packOtherColumns".
     */
    private void packOtherColumns() {
        TreeColumn columns[] = treeViewer.getTree().getColumns();
        for (TreeColumn column : columns) {
            column.pack();
        }
    }

    /**
     * 
     * DOC mzhao Comment method "expandTreeItems".
     * 
     * @param items
     * @param expandOrCollapse
     */
    private void expandTreeItems(TreeItem[] items, boolean expandOrCollapse) {
        for (TreeItem item : items) {
            item.setExpanded(expandOrCollapse);
            TreeItem[] its = item.getItems();
            if (its != null && its.length > 0) {
                expandTreeItems(its, expandOrCollapse);
            }
        }
    }

    /**
     * 
     */
    public void openColumnsSelectionDialog() {
        ColumnIndicator[] columnIndicators = treeViewer.getColumnIndicator();
        List<Column> columnList = new ArrayList<Column>();
        for (ColumnIndicator columnIndicator : columnIndicators) {
            columnList.add(columnIndicator.getTdColumn());
        }
        ColumnsSelectionDialog dialog = new ColumnsSelectionDialog(null, DefaultMessagesImpl
                .getString("ColumnMasterDetailsPage.columnSelection"), columnList, DefaultMessagesImpl //$NON-NLS-1$
                .getString("ColumnMasterDetailsPage.columnSelections")); //$NON-NLS-1$
        if (dialog.open() == Window.OK) {
            Object[] columns = dialog.getResult();
            treeViewer.setInput(columns);
            return;
        }
    }

    void createPreviewSection(final ScrolledForm form, Composite parent) {

        previewSection = createSection(form, parent, DefaultMessagesImpl.getString("ColumnMasterDetailsPage.graphics"), true, //$NON-NLS-1$
                DefaultMessagesImpl.getString("ColumnMasterDetailsPage.space")); //$NON-NLS-1$
        previewSection.setLayoutData(new GridData(GridData.FILL_BOTH));
        Composite sectionClient = toolkit.createComposite(previewSection);
        sectionClient.setLayout(new GridLayout());
        sectionClient.setLayoutData(new GridData(GridData.FILL_BOTH));

        Composite actionBarComp = toolkit.createComposite(sectionClient);
        GridLayout gdLayout = new GridLayout();
        gdLayout.numColumns = 2;
        actionBarComp.setLayout(gdLayout);

        ImageHyperlink collapseAllImageLink = toolkit.createImageHyperlink(actionBarComp, SWT.NONE);
        collapseAllImageLink.setToolTipText(DefaultMessagesImpl.getString("CollapseAllColumns")); //$NON-NLS-1$
        collapseAllImageLink.setImage(ImageLib.getImage(ImageLib.COLLAPSE_ALL));
        collapseAllImageLink.addHyperlinkListener(new HyperlinkAdapter() {

            public void linkActivated(HyperlinkEvent e) {
                if (previewChartList != null && !previewChartList.isEmpty()) {
                    for (ExpandableComposite comp : previewChartList) {
                        comp.setExpanded(false);
                        comp.getParent().pack();
                        // for (Object composite : comp.getChildren()) {
                        // ((Composite) composite).pack();
                        // }
                    }
                }
            }
        });

        ImageHyperlink expandAllImageLink = toolkit.createImageHyperlink(actionBarComp, SWT.NONE);
        expandAllImageLink.setToolTipText(DefaultMessagesImpl.getString("ExpandAllColumns")); //$NON-NLS-1$
        expandAllImageLink.setImage(ImageLib.getImage(ImageLib.EXPAND_ALL));
        expandAllImageLink.addHyperlinkListener(new HyperlinkAdapter() {

            public void linkActivated(HyperlinkEvent e) {
                if (previewChartList != null && !previewChartList.isEmpty()) {
                    for (ExpandableComposite comp : previewChartList) {
                        comp.setExpanded(true);
                        comp.getParent().pack();
                        // for (Object composite : comp.getChildren()) {
                        // ((Composite) composite).pack();
                        // }
                    }
                }

            }
        });

        ImageHyperlink refreshBtn = toolkit.createImageHyperlink(sectionClient, SWT.NONE);
        refreshBtn.setText(DefaultMessagesImpl.getString("ColumnMasterDetailsPage.refreshGraphics")); //$NON-NLS-1$
        refreshBtn.setImage(ImageLib.getImage(ImageLib.SECTION_PREVIEW));
        final Label message = toolkit.createLabel(sectionClient, DefaultMessagesImpl
                .getString("ColumnMasterDetailsPage.spaceWhite")); //$NON-NLS-1$
        message.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_RED));
        message.setVisible(false);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).applyTo(sectionClient);

        chartComposite = toolkit.createComposite(sectionClient);
        chartComposite.setLayout(new GridLayout());
        chartComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

        final Analysis analysis = analysisHandler.getAnalysis();

        refreshBtn.addHyperlinkListener(new HyperlinkAdapter() {

            public void linkActivated(HyperlinkEvent e) {

                for (Control control : chartComposite.getChildren()) {
                    control.dispose();
                }

                boolean analysisStatue = analysis.getResults().getResultMetadata() != null
                        && analysis.getResults().getResultMetadata().getExecutionDate() != null;

                if (!analysisStatue) {
                    boolean returnCode = MessageDialog.openConfirm(null, DefaultMessagesImpl
                            .getString("ColumnMasterDetailsPage.string0"), //$NON-NLS-1$
                            DefaultMessagesImpl.getString("ColumnMasterDetailsPage.string1")); //$NON-NLS-1$

                    if (returnCode) {
                        new RunAnalysisAction(ColumnMasterDetailsPage.this).run();
                        message.setVisible(false);
                    } else {
                        createPreviewCharts(form, chartComposite, false);
                        message.setText(DefaultMessagesImpl.getString("ColumnMasterDetailsPage.warning")); //$NON-NLS-1$
                        message.setVisible(true);
                    }
                } else {
                    createPreviewCharts(form, chartComposite, true);
                }

                chartComposite.layout();
                form.reflow(true);
            }

        });

        previewSection.setClient(sectionClient);
    }

    public void createPreviewCharts(final ScrolledForm form, final Composite composite, final boolean isCreate) {

        previewChartList = new ArrayList<ExpandableComposite>();

        for (final ColumnIndicator columnIndicator : this.treeViewer.getColumnIndicator()) {

            final TdColumn column = columnIndicator.getTdColumn();

            ExpandableComposite exComp = toolkit.createExpandableComposite(composite, ExpandableComposite.TREE_NODE
                    | ExpandableComposite.CLIENT_INDENT);
            exComp.setText(DefaultMessagesImpl.getString("ColumnMasterDetailsPage.column") + column.getName()); //$NON-NLS-1$
            exComp.setLayout(new GridLayout());
            exComp.setData(columnIndicator);
            previewChartList.add(exComp);

            final Composite comp = toolkit.createComposite(exComp);
            comp.setLayout(new GridLayout());
            comp.setLayoutData(new GridData(GridData.FILL_BOTH));

            if (columnIndicator.getIndicators().length != 0) {

                IRunnableWithProgress rwp = new IRunnableWithProgress() {

                    public void run(final IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

                        monitor.beginTask(DefaultMessagesImpl.getString("ColumnMasterDetailsPage.createPreview") //$NON-NLS-1$
                                + column.getName(), IProgressMonitor.UNKNOWN);

                        Display.getDefault().syncExec(new Runnable() {

                            public void run() {

                                Map<EIndicatorChartType, List<IndicatorUnit>> indicatorComposite = CompositeIndicator
                                        .getInstance().getIndicatorComposite(columnIndicator);
                                for (EIndicatorChartType chartType : indicatorComposite.keySet()) {
                                    List<IndicatorUnit> units = indicatorComposite.get(chartType);
                                    if (!units.isEmpty()) {
                                        final IChartTypeStates chartTypeState = ChartTypeStatesOperator.getChartState(chartType,
                                                units);
                                        JFreeChart chart = chartTypeState.getFeatChart();

                                        if (chart != null) {
                                            final ChartComposite chartComp = new ChartComposite(comp, SWT.NONE, chart, true);

                                            GridData gd = new GridData();
                                            gd.widthHint = 550;
                                            gd.heightHint = 250;
                                            chartComp.setLayoutData(gd);

                                            addListenerToChartComp(chartComp, chartTypeState);
                                        }
                                    }
                                }
                            }
                        });

                        monitor.done();
                    }

                };

                try {
                    new ProgressMonitorDialog(getSite().getShell()).run(true, false, rwp);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            exComp.addExpansionListener(new ExpansionAdapter() {

                @Override
                public void expansionStateChanged(ExpansionEvent e) {
                    composite.layout();
                    composite.pack();
                    form.reflow(true);
                }

            });

            exComp.setExpanded(true);
            exComp.setClient(comp);
        }

        if (!previewChartList.isEmpty()) {
            this.previewChartCompsites = previewChartList.toArray(new Composite[previewChartList.size()]);
        }
    }

    @Override
    public void refresh() {
        if (chartComposite != null) {
            try {
                for (Control control : chartComposite.getChildren()) {
                    control.dispose();
                }

                createPreviewCharts(form, chartComposite, true);
                chartComposite.getParent().layout();
                chartComposite.layout();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * @param form
     * @param toolkit
     * @param anasisDataComp
     */
    void createDataFilterSection(final ScrolledForm form, Composite anasisDataComp) {
        dataFilterSection = createSection(
                form,
                anasisDataComp,
                DefaultMessagesImpl.getString("ColumnMasterDetailsPage.dataFilter"), false, DefaultMessagesImpl.getString("ColumnMasterDetailsPage.editDataFilter")); //$NON-NLS-1$ //$NON-NLS-2$

        Composite sectionClient = toolkit.createComposite(dataFilterSection);
        dataFilterComp = new DataFilterComp(sectionClient, stringDataFilter);
        dataFilterComp.addPropertyChangeListener(this);
        dataFilterSection.setClient(sectionClient);
    }

    /**
     * DOC hcheng Comment method "createAnalysisParamSection".
     * 
     * @param form
     * @param anasisDataComp
     */
    void createAnalysisParamSection(final ScrolledForm form, Composite anasisDataComp) {
        analysisParamSection = createSection(form, anasisDataComp, DefaultMessagesImpl
                .getString("ColumnMasterDetailsPage.AnalysisParameter"), false, null); //$NON-NLS-1$
        Composite sectionClient = toolkit.createComposite(analysisParamSection);
        sectionClient.setLayout(new GridLayout(2, false));
        toolkit.createLabel(sectionClient, DefaultMessagesImpl.getString("ColumnMasterDetailsPage.ExecutionEngine")); //$NON-NLS-1$
        final CCombo execCombo = new CCombo(sectionClient, SWT.BORDER);
        execCombo.setEditable(false);
        for (ExecutionLanguage language : ExecutionLanguage.VALUES) {
            String temp = language.getLiteral();
            execCombo.add(temp);
        }
        // ExecutionLanguage executionLanguage = analysis.getParameters().getExecutionLanguage();
        execCombo.setText(ExecutionLanguage.SQL.getLiteral());
        execCombo.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                setDirty(true);
                execLang = execCombo.getText();
            }

        });
        analysisParamSection.setClient(sectionClient);
    }

    /**
     * @param outputFolder
     * @throws DataprofilerCoreException
     */
    public void saveAnalysis() throws DataprofilerCoreException {
        analysisHandler.clearAnalysis();
        ColumnIndicator[] columnIndicators = treeViewer.getColumnIndicator();
        // List<TdDataProvider> providerList = new ArrayList<TdDataProvider>();
        TdDataProvider tdProvider = null;
        Analysis analysis = analysisHandler.getAnalysis();
        analysis.getParameters().setExecutionLanguage(ExecutionLanguage.get(execLang));
        if (columnIndicators != null) {
            if (columnIndicators.length != 0) {
                tdProvider = EObjectHelper.getTdDataProvider(columnIndicators[0].getTdColumn());
                analysis.getContext().setConnection(tdProvider);
            }
            for (ColumnIndicator columnIndicator : columnIndicators) {
                analysisHandler.addIndicator(columnIndicator.getTdColumn(), columnIndicator.getIndicators());
                DataminingType type = MetadataHelper.getDataminingType(columnIndicator.getTdColumn());
                if (type == null) {
                    type = MetadataHelper.getDefaultDataminingType(columnIndicator.getTdColumn().getJavaType());
                }
                analysisHandler.setDatamingType(type.getLiteral(), columnIndicator.getTdColumn());
            }
        }
        // if (providerList.size() != 0) {
        //           
        // }
        analysisHandler.setStringDataFilter(dataFilterComp.getDataFilterString());
        // boolean modifiedResourcesSaved = analysisHandler.saveModifiedResources();
        // if (!modifiedResourcesSaved) {
        // log.error("Problem when saving modified resource.");
        // }
        // AnalysisWriter writer = new AnalysisWriter();

        String urlString = analysis.eResource() != null ? analysis.eResource().getURI().toFileString()
                : PluginConstant.EMPTY_STRING;
        // try {
        // urlString = editorInput.getFile();
        // analysisHandler.getAnalysis().setUrl(urlString);
        // } catch (MalformedURLException e) {
        // e.printStackTrace();
        // }

        // FIXME after i set the options of bins designer, and when saving the file, it cause a exception.

        // File file = new File(editorInput.getFile().getParent() + File.separator + fileName);
        // ReturnCode saved = writer.save(analysisHandler.getAnalysis(), file);
        ReturnCode saved = AnaResourceFileHelper.getInstance().save(analysis);
        if (saved.isOk()) {
            if (tdProvider != null) {
                PrvResourceFileHelper.getInstance().save(tdProvider);
            }
            // AnaResourceFileHelper.getInstance().setResourcesNumberChanged(true);
            if (log.isDebugEnabled()) {
                log.debug("Saved in  " + urlString + " successful"); //$NON-NLS-1$ //$NON-NLS-2$
            }
        } else {
            throw new DataprofilerCoreException(DefaultMessagesImpl.getString(
                    "ColumnMasterDetailsPage.problem", analysis.getName(), urlString, saved.getMessage())); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }

        // TODO get the domain constraint, we will see later.
        // Domain dataFilter = getDataFilter(dataManager, (Column) column); // CAST here for test
        // analysisBuilder.addFilterOnData(dataFilter);
        //
        // FolderProvider folderProvider = new FolderProvider();
        // folderProvider.setFolder(new File(outputFolder));
        // DqRepositoryViewService.saveDomain(dataFilter, folderProvider);
        treeViewer.setDirty(false);
        dataFilterComp.setDirty(false);
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (PluginConstant.ISDIRTY_PROPERTY.equals(evt.getPropertyName())) {
            currentEditor.firePropertyChange(IEditorPart.PROP_DIRTY);
            currentEditor.setRefreshResultPage(true);
        } else if (PluginConstant.DATAFILTER_PROPERTY.equals(evt.getPropertyName())) {
            this.analysisHandler.setStringDataFilter((String) evt.getNewValue());
        }
    }

    // public void setDirty(boolean isDirty) {
    // if (this.isDirty != isDirty) {
    // this.isDirty = isDirty;
    // ((AnalysisEditor) this.getEditor()).firePropertyChange(IEditorPart.PROP_DIRTY);
    // }
    // }

    @Override
    public boolean isDirty() {
        return super.isDirty() || treeViewer.isDirty() || dataFilterComp.isDirty();
    }

    @Override
    public void dispose() {
        super.dispose();
        if (this.treeViewer != null) {
            this.treeViewer.removePropertyChangeListener(this);
        }
        if (dataFilterComp != null) {
            this.dataFilterComp.removePropertyChangeListener(this);
        }
    }

    private void addListenerToChartComp(final ChartComposite chartComp, final IChartTypeStates chartTypeState) {
        chartComp.addChartMouseListener(new ChartMouseListener() {

            public void chartMouseClicked(ChartMouseEvent event) {
                final String referenceLink = chartTypeState.getReferenceLink();
                if (event.getTrigger().getButton() == 1 && referenceLink != null) {
                    Menu menu = new Menu(chartComp.getShell(), SWT.POP_UP);
                    chartComp.setMenu(menu);

                    MenuItem item = new MenuItem(menu, SWT.PUSH);
                    item.setText(DefaultMessagesImpl.getString("ColumnMasterDetailsPage.what")); //$NON-NLS-1$
                    item.addSelectionListener(new SelectionAdapter() {

                        @Override
                        public void widgetSelected(SelectionEvent e) {
                            ChartUtils.openReferenceLink(referenceLink);
                        }
                    });

                    menu.setVisible(true);
                }
            }

            public void chartMouseMoved(ChartMouseEvent event) {

            }

        });
    }

    /**
     * Getter for treeViewer.
     * 
     * @return the treeViewer
     */
    public AnalysisColumnTreeViewer getTreeViewer() {
        return this.treeViewer;
    }

    public ScrolledForm getForm() {
        return form;
    }

    public void setForm(ScrolledForm form) {
        this.form = form;
    }

    public ColumnAnalysisHandler getAnalysisHandler() {
        return analysisHandler;
    }

    public ColumnIndicator[] getCurrentColumnIndicators() {
        return currentColumnIndicators;
    }

    public Composite[] getPreviewChartCompsites() {
        return previewChartCompsites;
    }

    public Composite getChartComposite() {
        return chartComposite;
    }

    @Override
    protected ReturnCode canRun() {
        ColumnIndicator[] columnIndicators = treeViewer.getColumnIndicator();
        if (columnIndicators == null || columnIndicators.length == 0) {
            return new ReturnCode(DefaultMessagesImpl.getString("ColumnMasterDetailsPage.NoColumnAssigned"), false); //$NON-NLS-1$
        }
        for (ColumnIndicator columnIndicator : columnIndicators) {
            if (columnIndicator.getIndicators().length == 0) {
                return new ReturnCode(DefaultMessagesImpl.getString("ColumnMasterDetailsPage.NoIndicatorAssigned"), false); //$NON-NLS-1$
            }
        }

        return new ReturnCode(true);
    }

    @Override
    protected ReturnCode canSave() {
        List<Column> analyzedColumns = new ArrayList<Column>();

        for (ColumnIndicator columnIndicator : treeViewer.getColumnIndicator()) {
            analyzedColumns.add(columnIndicator.getTdColumn());
        }

        if (!analyzedColumns.isEmpty()) {
            if (!ColumnHelper.isFromSameConnection(analyzedColumns)) {
                return new ReturnCode(DefaultMessagesImpl.getString("ColumnMasterDetailsPage.AlreadyShown"), false); //$NON-NLS-1$
            }

            if (!ColumnHelper.isFromSameTable(analyzedColumns) && !"".equals(dataFilterComp.getDataFilterString())) { //$NON-NLS-1$
                return new ReturnCode(DefaultMessagesImpl.getString("ColumnMasterDetailsPage.CannotCreatAnalysis"), false); //$NON-NLS-1$
            }
        }

        return new ReturnCode(true);
    }
}
