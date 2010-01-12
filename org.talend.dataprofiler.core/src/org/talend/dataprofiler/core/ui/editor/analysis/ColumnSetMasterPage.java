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
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.experimental.chart.swt.ChartComposite;
import org.talend.cwm.helper.ColumnHelper;
import org.talend.cwm.helper.DataProviderHelper;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.cwm.relational.TdColumn;
import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.action.actions.RunAnalysisAction;
import org.talend.dataprofiler.core.ui.chart.ChartDecorator;
import org.talend.dataprofiler.core.ui.dialog.ColumnsSelectionDialog;
import org.talend.dataprofiler.core.ui.editor.composite.AnalysisColumnSetTreeViewer;
import org.talend.dataprofiler.core.ui.editor.composite.DataFilterComp;
import org.talend.dataprofiler.core.ui.editor.composite.IndicatorsComp;
import org.talend.dataprofiler.core.ui.editor.preview.TopChartFactory;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.analysis.ExecutionLanguage;
import org.talend.dataquality.exception.DataprofilerCoreException;
import org.talend.dataquality.helpers.MetadataHelper;
import org.talend.dataquality.indicators.DataminingType;
import org.talend.dataquality.indicators.columnset.ColumnSetMultiValueIndicator;
import org.talend.dataquality.indicators.columnset.ColumnsetFactory;
import org.talend.dataquality.indicators.columnset.SimpleStatIndicator;
import org.talend.dq.analysis.ColumnSetAnalysisHandler;
import org.talend.dq.helper.resourcehelper.AnaResourceFileHelper;
import org.talend.dq.helper.resourcehelper.PrvResourceFileHelper;
import org.talend.utils.sugars.ReturnCode;
import orgomg.cwm.objectmodel.core.ModelElement;
import orgomg.cwm.resource.relational.Column;

/**
 * @author yyi 2009-12-16
 * 
 */
public class ColumnSetMasterPage extends AbstractAnalysisMetadataPage implements PropertyChangeListener {

    private static Logger log = Logger.getLogger(ColumnSetMasterPage.class);

    AnalysisEditor currentEditor;

    AnalysisColumnSetTreeViewer treeViewer;

    IndicatorsComp indicatorsViewer;

    DataFilterComp dataFilterComp;

    ColumnSetAnalysisHandler columnSetAnalysisHandler;

    private SimpleStatIndicator simpleStatIndicator;

    protected String execLang;

    private String stringDataFilter;

    private Composite chartComposite;

    private ScrolledForm form;

    private static final int TREE_MAX_LENGTH = 300;

    private static final int INDICATORS_SECTION_HEIGHT = 300;

    protected Composite[] previewChartCompsites;

    private EList<ModelElement> analyzedColumns;

    private Section analysisColSection;

    private Section dataFilterSection;

    private Section previewSection;

    private Section analysisParamSection;

    private Section indicatorsSection;

    public ColumnSetMasterPage(FormEditor editor, String id, String title) {
        super(editor, id, title);

        currentEditor = (AnalysisEditor) editor;
    }

    public void initialize(FormEditor editor) {
        super.initialize(editor);
        recomputeIndicators();

    }

    public void recomputeIndicators() {
        columnSetAnalysisHandler = new ColumnSetAnalysisHandler();
        columnSetAnalysisHandler.setAnalysis((Analysis) this.currentModelElement);
        stringDataFilter = columnSetAnalysisHandler.getStringDataFilter();
        analyzedColumns = columnSetAnalysisHandler.getAnalyzedColumns();
        SimpleStatIndicator ssIndicator;
        if (columnSetAnalysisHandler.getIndicator() == null) {
            ColumnsetFactory columnsetFactory = ColumnsetFactory.eINSTANCE;
            ssIndicator = columnsetFactory.createSimpleStatIndicator();
            simpleStatIndicator = ssIndicator;
        } else {
            simpleStatIndicator = (SimpleStatIndicator) columnSetAnalysisHandler.getIndicator();
        }
        for (ModelElement element : analyzedColumns) {
            TdColumn tdColumn = SwitchHelpers.COLUMN_SWITCH.doSwitch(element);
            if (tdColumn == null) {
                continue;
            }
            MetadataHelper.setDataminingType(DataminingType.NOMINAL, tdColumn);
        }

    }

    @Override
    protected void createFormContent(IManagedForm managedForm) {
        this.form = managedForm.getForm();
        Composite body = form.getBody();

        body.setLayout(new GridLayout());
        final SashForm sForm = new SashForm(body, SWT.NULL);
        sForm.setLayoutData(new GridData(GridData.FILL_BOTH));

        topComp = toolkit.createComposite(sForm);
        topComp.setLayoutData(new GridData(GridData.FILL_BOTH));
        topComp.setLayout(new GridLayout());
        metadataSection = creatMetadataSection(form, topComp);
        metadataSection.setText(DefaultMessagesImpl.getString("ColumnMasterDetailsPage.analysisMeta")); //$NON-NLS-1$
        metadataSection.setDescription(DefaultMessagesImpl.getString("ColumnMasterDetailsPage.setPropOfAnalysis")); //$NON-NLS-1$

        form.setText(DefaultMessagesImpl.getString("ColumnSetMasterPage.title")); //$NON-NLS-1$

        createAnalysisColumnsSection(form, topComp);

        createIndicatorsSection(form, topComp);

        createDataFilterSection(form, topComp);

        // createAnalysisParamSection(form, topComp);

        Composite previewComp = toolkit.createComposite(sForm);
        previewComp.setLayoutData(new GridData(GridData.FILL_BOTH));
        previewComp.setLayout(new GridLayout());
        // add by hcheng for 0007290: Chart cannot auto compute it's size in
        // DQRule analsyis Editor
        previewComp.addControlListener(new ControlAdapter() {

            /*
             * (non-Javadoc)
             * 
             * @see org.eclipse.swt.events.ControlAdapter#controlResized(org.eclipse .swt.events.ControlEvent)
             */
            @Override
            public void controlResized(ControlEvent e) {
                super.controlResized(e);
                sForm.redraw();
                form.reflow(true);
            }
        });
        // ~
        createPreviewSection(form, previewComp);
    }

    /**
     * DOC yyi Comment method "createIndicatorsSection".
     * 
     * @param topComp
     * @param form
     */
    private void createIndicatorsSection(ScrolledForm form, Composite topComp) {
        indicatorsSection = createSection(form, topComp, "Indicators", null); //$NON-NLS-1$

        Composite indicatorsComp = toolkit.createComposite(indicatorsSection, SWT.NONE);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(indicatorsComp);
        indicatorsComp.setLayout(new GridLayout());
        ((GridData) indicatorsComp.getLayoutData()).heightHint = INDICATORS_SECTION_HEIGHT;

        indicatorsViewer = new IndicatorsComp(indicatorsComp, this);
        indicatorsViewer.setDirty(false);
        indicatorsViewer.addPropertyChangeListener(this);
        indicatorsViewer.setInput(simpleStatIndicator);
        indicatorsSection.setClient(indicatorsComp);
    }

    void createAnalysisColumnsSection(final ScrolledForm form, Composite anasisDataComp) {
        analysisColSection = createSection(form, anasisDataComp, DefaultMessagesImpl
                .getString("ColumnMasterDetailsPage.analyzeColumn"), null); //$NON-NLS-1$

        Composite topComp = toolkit.createComposite(analysisColSection);
        topComp.setLayout(new GridLayout());
        // ~ MOD mzhao 2009-05-05,Bug 6587.
        createConnBindWidget(topComp);
        // ~
        Hyperlink clmnBtn = toolkit.createHyperlink(topComp, DefaultMessagesImpl
                .getString("ColumnMasterDetailsPage.selectColumn"), SWT.NONE); //$NON-NLS-1$
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).applyTo(clmnBtn);
        clmnBtn.addHyperlinkListener(new HyperlinkAdapter() {

            public void linkActivated(HyperlinkEvent e) {
                openColumnsSelectionDialog();
            }

        });

        Composite tree = toolkit.createComposite(topComp, SWT.NONE);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(tree);
        tree.setLayout(new GridLayout());
        ((GridData) tree.getLayoutData()).heightHint = TREE_MAX_LENGTH;

        treeViewer = new AnalysisColumnSetTreeViewer(tree, this);
        treeViewer.addPropertyChangeListener(this);
        treeViewer.setInput(analyzedColumns.toArray());
        treeViewer.setDirty(false);
        analysisColSection.setClient(topComp);

    }

    /**
     * 
     */
    public void openColumnsSelectionDialog() {
        List<Column> columnList = treeViewer.getColumnSetMultiValueList();
        if (columnList == null) {
            columnList = new ArrayList<Column>();
        }
        ColumnsSelectionDialog dialog = new ColumnsSelectionDialog(
                this,
                null,
                DefaultMessagesImpl.getString("ColumnMasterDetailsPage.columnSelection"), columnList, DefaultMessagesImpl.getString("ColumnMasterDetailsPage.columnSelections")); //$NON-NLS-1$ //$NON-NLS-2$
        if (dialog.open() == Window.OK) {
            Object[] columns = dialog.getResult();
            treeViewer.setInput(columns);
            indicatorsViewer.setInput(simpleStatIndicator);
            return;
        }
    }

    void createPreviewSection(final ScrolledForm form, Composite parent) {

        previewSection = createSection(
                form,
                parent,
                DefaultMessagesImpl.getString("ColumnMasterDetailsPage.graphics"), DefaultMessagesImpl.getString("ColumnMasterDetailsPage.space")); //$NON-NLS-1$ //$NON-NLS-2$
        previewSection.setLayoutData(new GridData(GridData.FILL_BOTH));

        Composite sectionClient = toolkit.createComposite(previewSection);
        sectionClient.setLayout(new GridLayout());
        sectionClient.setLayoutData(new GridData(GridData.FILL_BOTH));

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

        final Analysis analysis = columnSetAnalysisHandler.getAnalysis();

        refreshBtn.addHyperlinkListener(new HyperlinkAdapter() {

            public void linkActivated(HyperlinkEvent e) {

                for (Control control : chartComposite.getChildren()) {
                    control.dispose();
                }

                boolean analysisStatue = analysis.getResults().getResultMetadata() != null
                        && analysis.getResults().getResultMetadata().getExecutionDate() != null;

                if (!analysisStatue) {
                    boolean returnCode = MessageDialog.openConfirm(null, DefaultMessagesImpl
                            .getString("ColumnMasterDetailsPage.ViewResult"), //$NON-NLS-1$
                            DefaultMessagesImpl.getString("ColumnMasterDetailsPage.RunOrSeeSampleData")); //$NON-NLS-1$

                    if (returnCode) {
                        new RunAnalysisAction().run();
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

    public void createPreviewCharts(final ScrolledForm form, final Composite parentComp, final boolean isCreate) {
        Section previewSection = createSection(
                form,
                parentComp,
                DefaultMessagesImpl.getString("ColumnSetResultPage.SimpleStatistics"), DefaultMessagesImpl.getString("ColumnMasterDetailsPage.space")); //$NON-NLS-1$
        previewSection.setLayoutData(new GridData(480, 300));

        Composite sectionClient = toolkit.createComposite(previewSection);
        sectionClient.setLayout(new GridLayout());
        sectionClient.setLayoutData(new GridData(GridData.FILL_BOTH));

        Composite simpleComposite = toolkit.createComposite(sectionClient);
        simpleComposite.setLayout(new GridLayout(1, true));
        simpleComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

        createSimpleStatistics(form, simpleComposite, getSimpleStatIndicator());
        previewSection.setClient(sectionClient);
    }

    private void createSimpleStatistics(final ScrolledForm form, final Composite composite,
            final ColumnSetMultiValueIndicator columnSetMultiValueIndicator) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(columnSetMultiValueIndicator.getRowCountIndicator().getCount(), DefaultMessagesImpl
                .getString("ColumnSetMasterPage.Row_Count"), ""); //$NON-NLS-1$ //$NON-NLS-2$
        dataset.addValue(columnSetMultiValueIndicator.getDistinctCountIndicator().getDistinctValueCount(), DefaultMessagesImpl
                .getString("ColumnSetMasterPage.Distinct_Count"), ""); //$NON-NLS-1$ //$NON-NLS-2$
        dataset.addValue(columnSetMultiValueIndicator.getDuplicateCountIndicator().getDuplicateValueCount(), DefaultMessagesImpl
                .getString("ColumnSetMasterPage.Duplicate_Count"), ""); //$NON-NLS-1$ //$NON-NLS-2$
        dataset.addValue(columnSetMultiValueIndicator.getUniqueCountIndicator().getUniqueValueCount(), DefaultMessagesImpl
                .getString("ColumnSetMasterPage.Unique_Count"), ""); //$NON-NLS-1$ //$NON-NLS-2$

        JFreeChart chart = TopChartFactory.createBarChart(
                DefaultMessagesImpl.getString("ColumnSetMasterPage.SimpleStatistics"), dataset, true); //$NON-NLS-1$

        // MOD mzhao 2009-07-28 Bind the indicator with specific color.
        if (chart != null) {
            Plot plot = chart.getPlot();
            if (plot instanceof CategoryPlot) {
                ChartDecorator.decorateCategoryPlot(chart);
                // Row Count
                ((CategoryPlot) plot).getRenderer()
                        .setSeriesPaint(0, ChartDecorator.IndiBindColor.INDICATOR_ROW_COUNT.getColor());
                // Distinct Count
                ((CategoryPlot) plot).getRenderer().setSeriesPaint(1,
                        ChartDecorator.IndiBindColor.INDICATOR_DISTINCT_COUNT.getColor());
                // Unique Count
                ((CategoryPlot) plot).getRenderer().setSeriesPaint(2,
                        ChartDecorator.IndiBindColor.INDICATOR_UNIQUE_COUNT.getColor());
                // Duplicate Count
                ((CategoryPlot) plot).getRenderer().setSeriesPaint(3,
                        ChartDecorator.IndiBindColor.INDICATOR_DUPLICATE_COUNT.getColor());

            }
        }

        ChartComposite chartComp = new ChartComposite(composite, SWT.NONE, chart);
        chartComp.setLayoutData(new GridData(GridData.FILL_BOTH));
        // ChartUtils.createAWTSWTComp(composite, new GridData(GridData.FILL_BOTH), chart);
    }

    @Override
    public void refresh() {
        if (chartComposite != null) {
            try {
                for (Control control : chartComposite.getChildren()) {
                    control.dispose();
                }

                createPreviewCharts(form, chartComposite, true);
                chartComposite.layout();
                getForm().reflow(true);
            } catch (Exception ex) {
                log.error(ex, ex);
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
                DefaultMessagesImpl.getString("ColumnMasterDetailsPage.dataFilter"), DefaultMessagesImpl.getString("ColumnMasterDetailsPage.editDataFilter")); //$NON-NLS-1$ //$NON-NLS-2$

        Composite sectionClient = toolkit.createComposite(dataFilterSection);
        dataFilterComp = new DataFilterComp(sectionClient, stringDataFilter);
        dataFilterComp.addPropertyChangeListener(this);
        dataFilterSection.setClient(sectionClient);
    }

    /**
     * @param outputFolder
     * @throws DataprofilerCoreException
     */

    public void saveAnalysis() throws DataprofilerCoreException {
        columnSetAnalysisHandler.clearAnalysis();
        simpleStatIndicator.getAnalyzedColumns().clear();

        // set execute engine
        Analysis analysis = columnSetAnalysisHandler.getAnalysis();
        analysis.getParameters().setExecutionLanguage(ExecutionLanguage.get(execLang));

        // set data filter
        columnSetAnalysisHandler.setStringDataFilter(dataFilterComp.getDataFilterString());

        // save analysis
        List<Column> columnList = treeViewer.getColumnSetMultiValueList();

        TdDataProvider tdProvider = null;
        if (columnList != null && columnList.size() != 0) {
            tdProvider = DataProviderHelper.getTdDataProvider(SwitchHelpers.COLUMN_SWITCH.doSwitch(columnList.get(0)));
            analysis.getContext().setConnection(tdProvider);
            simpleStatIndicator.getAnalyzedColumns().addAll(columnList);
            columnSetAnalysisHandler.addIndicator(columnList, simpleStatIndicator);
        } else {
            analysis.getContext().setConnection(null);
            analysis.getClientDependency().clear();
        }

        String urlString = analysis.eResource() != null ? analysis.eResource().getURI().toFileString()
                : PluginConstant.EMPTY_STRING;

        ReturnCode saved = AnaResourceFileHelper.getInstance().save(analysis);
        if (saved.isOk()) {
            if (tdProvider != null) {
                PrvResourceFileHelper.getInstance().save(tdProvider);
            }

            if (log.isDebugEnabled()) {
                log.debug("Saved in  " + urlString + " successful");
            }
        } else {
            throw new DataprofilerCoreException(DefaultMessagesImpl.getString(
                    "ColumnMasterDetailsPage.problem", analysis.getName(), urlString, saved.getMessage())); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }

        treeViewer.setDirty(false);
        dataFilterComp.setDirty(false);
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (PluginConstant.ISDIRTY_PROPERTY.equals(evt.getPropertyName())) {
            currentEditor.firePropertyChange(IEditorPart.PROP_DIRTY);
        } else if (PluginConstant.DATAFILTER_PROPERTY.equals(evt.getPropertyName())) {
            this.columnSetAnalysisHandler.setStringDataFilter((String) evt.getNewValue());
        }

    }

    @Override
    public boolean isDirty() {
        return super.isDirty() || (treeViewer != null && treeViewer.isDirty())
                || (dataFilterComp != null && dataFilterComp.isDirty())
                || (indicatorsViewer != null && indicatorsViewer.isDirty());
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

    /**
     * Getter for treeViewer.
     * 
     * @return the treeViewer
     */
    public AnalysisColumnSetTreeViewer getTreeViewer() {
        return this.treeViewer;
    }

    public ScrolledForm getForm() {
        return form;
    }

    public void setForm(ScrolledForm form) {
        this.form = form;
    }

    public ColumnSetAnalysisHandler getColumnSetAnalysisHandler() {
        return columnSetAnalysisHandler;
    }

    public SimpleStatIndicator getSimpleStatIndicator() {
        // simpleStatIndicator.getListRows();
        return simpleStatIndicator;
    }

    public Composite[] getPreviewChartCompsites() {
        return previewChartCompsites;
    }

    public Composite getChartComposite() {
        return chartComposite;
    }

    @Override
    protected ReturnCode canSave() {
        String message = null;
        List<Column> columnSetMultiValueList = getTreeViewer().getColumnSetMultiValueList();

        if (!columnSetMultiValueList.isEmpty()) {
            if (!ColumnHelper.isFromSameTable(columnSetMultiValueList)) {
                message = DefaultMessagesImpl.getString("ColumnSetMasterPage.CannotCreateAnalysis"); //$NON-NLS-1$
            }
        }
        if (message == null) {
            return new ReturnCode(true);
        }

        return new ReturnCode(message, false);
    }

    @Override
    protected ReturnCode canRun() {
        List<Column> columnSetMultiValueList = getTreeViewer().getColumnSetMultiValueList();
        if (columnSetMultiValueList.isEmpty()) {
            return new ReturnCode(DefaultMessagesImpl.getString("ColumnSetMasterPage.NoColumnsAssigned"), false); //$NON-NLS-1$
        }

        return new ReturnCode(true);

    }

}
