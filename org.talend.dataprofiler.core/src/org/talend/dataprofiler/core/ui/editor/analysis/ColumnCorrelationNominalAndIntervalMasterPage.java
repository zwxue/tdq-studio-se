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
package org.talend.dataprofiler.core.ui.editor.analysis;

import java.awt.Frame;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
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
import org.talend.cwm.helper.DataProviderHelper;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.cwm.relational.TdColumn;
import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.exception.DataprofilerCoreException;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.IRuningStatusListener;
import org.talend.dataprofiler.core.ui.action.actions.RunAnalysisAction;
import org.talend.dataprofiler.core.ui.dialog.ColumnsSelectionDialog;
import org.talend.dataprofiler.core.ui.editor.composite.AnalysisColumnNominalIntervalTreeViewer;
import org.talend.dataprofiler.core.ui.editor.composite.DataFilterComp;
import org.talend.dataprofiler.core.ui.editor.preview.HideSeriesPanel;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.helpers.MetadataHelper;
import org.talend.dataquality.indicators.DataminingType;
import org.talend.dataquality.indicators.columnset.ColumnSetMultiValueIndicator;
import org.talend.dataquality.indicators.columnset.ColumnsetFactory;
import org.talend.dataquality.indicators.columnset.CountAvgNullIndicator;
import org.talend.dataquality.indicators.columnset.MinMaxDateIndicator;
import org.talend.dq.analysis.ColumnCorrelationAnalysisHandler;
import org.talend.dq.helper.resourcehelper.AnaResourceFileHelper;
import org.talend.dq.helper.resourcehelper.PrvResourceFileHelper;
import org.talend.utils.sql.Java2SqlType;
import org.talend.utils.sugars.ReturnCode;
import orgomg.cwm.objectmodel.core.ModelElement;
import orgomg.cwm.resource.relational.Column;

/**
 * @author xzhao
 * 
 */
public class ColumnCorrelationNominalAndIntervalMasterPage extends AbstractAnalysisMetadataPage implements IRuningStatusListener,
        PropertyChangeListener {

    private static Logger log = Logger.getLogger(ColumnCorrelationNominalAndIntervalMasterPage.class);

    AnalysisColumnNominalIntervalTreeViewer treeViewer;

    DataFilterComp dataFilterComp;

    ColumnCorrelationAnalysisHandler columnCorrelationAnalysisHandler;

    private ColumnSetMultiValueIndicator columnSetMultiIndicator;

    private boolean indicatorIsBlank;

    private String stringDataFilter;

    private Composite chartComposite;

    private ScrolledForm form;

    private static final int TREE_MAX_LENGTH = 400;

    private Composite[] previewChartCompsites;

    private Button runButton;

    private EList<ModelElement> analyzedColumns;

    private Section analysisColSection;

    private Section dataFilterSection;

    private Section previewSection;

    public ColumnCorrelationNominalAndIntervalMasterPage(FormEditor editor, String id, String title) {
        super(editor, id, title);
    }

    public void initialize(FormEditor editor) {
        super.initialize(editor);
        // columnSetMultiValueIndicator = columnCorrelationAnalysisHandler.getIndicator();
        /*
         * if (columnSetMultiValueIndicator == null) { ColumnsetFactoryImpl columnsetFactory = (ColumnsetFactoryImpl)
         * ColumnsetFactoryImpl.init(); columnSetMultiValueIndicator =
         * columnsetFactory.createColumnSetMultiValueIndicator(); }
         */
        columnCorrelationAnalysisHandler = new ColumnCorrelationAnalysisHandler();
        columnCorrelationAnalysisHandler.setAnalysis((Analysis) this.currentModelElement);
        stringDataFilter = columnCorrelationAnalysisHandler.getStringDataFilter();
        analyzedColumns = columnCorrelationAnalysisHandler.getAnalyzedColumns();
        CountAvgNullIndicator currentCountAvgNullIndicator;
        if (columnCorrelationAnalysisHandler.getIndicator() == null) {
            ColumnsetFactory columnsetFactory = ColumnsetFactory.eINSTANCE;
            currentCountAvgNullIndicator = columnsetFactory.createCountAvgNullIndicator();
            columnSetMultiIndicator = currentCountAvgNullIndicator;
            indicatorIsBlank = false;
        } else {
            indicatorIsBlank = false;
            columnSetMultiIndicator = (ColumnSetMultiValueIndicator) columnCorrelationAnalysisHandler.getIndicator();
        }
        for (ModelElement element : analyzedColumns) {
            TdColumn tdColumn = SwitchHelpers.COLUMN_SWITCH.doSwitch(element);
            if (tdColumn == null) {
                continue;
            }
            // currentColumnIndicator = new ColumnIndicator(tdColumn);
            DataminingType dataminingType = DataminingType.get(columnCorrelationAnalysisHandler.getDatamingType(tdColumn));
            MetadataHelper.setDataminingType(dataminingType == null ? DataminingType.NOMINAL : dataminingType, tdColumn);
        }
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
        if (columnSetMultiIndicator instanceof CountAvgNullIndicator) {
            form.setText("Correlation Analysis between nominal and interval columns");
        } else {
            form.setText("Correlation Analysis between nominal and date columns");
        }
        metadataSection.setText(DefaultMessagesImpl.getString("ColumnMasterDetailsPage.analysisMeta")); //$NON-NLS-1$
        metadataSection.setDescription(DefaultMessagesImpl.getString("ColumnMasterDetailsPage.setPropOfAnalysis")); //$NON-NLS-1$

        createAnalysisColumnsSection(form, topComp);

        createDataFilterSection(form, topComp);

        Composite previewComp = toolkit.createComposite(sForm);
        previewComp.setLayoutData(new GridData(GridData.FILL_BOTH));
        previewComp.setLayout(new GridLayout());

        createPreviewSection(form, previewComp);
        runButton = createRunButton(form);
        // MOD 2009-01-12 mzhao, for register sections that would be collapse or expand later.
        currentEditor.registerSections(new Section[] { analysisColSection, metadataSection, dataFilterSection, previewSection });
    }

    void createAnalysisColumnsSection(final ScrolledForm form, Composite anasisDataComp) {
        analysisColSection = createSection(form, anasisDataComp, DefaultMessagesImpl
                .getString("ColumnMasterDetailsPage.analyzeColumn"), false, null); //$NON-NLS-1$

        Composite topComp = toolkit.createComposite(analysisColSection);
        topComp.setLayout(new GridLayout());

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

        treeViewer = new AnalysisColumnNominalIntervalTreeViewer(tree, this);
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
                null,
                DefaultMessagesImpl.getString("ColumnMasterDetailsPage.columnSelection"), columnList, DefaultMessagesImpl.getString("ColumnMasterDetailsPage.columnSelections")); //$NON-NLS-1$ //$NON-NLS-2$
        if (dialog.open() == Window.OK) {
            Object[] columns = dialog.getResult();
            treeViewer.setInput(columns);
            return;
        }
    }

    void createPreviewSection(final ScrolledForm form, Composite parent) {

        previewSection = createSection(
                form,
                parent,
                DefaultMessagesImpl.getString("ColumnMasterDetailsPage.graphics"), true, DefaultMessagesImpl.getString("ColumnMasterDetailsPage.space")); //$NON-NLS-1$ //$NON-NLS-2$
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

        final Analysis analysis = columnCorrelationAnalysisHandler.getAnalysis();

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
                        new RunAnalysisAction(ColumnCorrelationNominalAndIntervalMasterPage.this).run();
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

        List<Composite> previewChartList = new ArrayList<Composite>();
        List<Column> numericOrDateList = new ArrayList<Column>();
        if (columnSetMultiIndicator instanceof CountAvgNullIndicator) {
            numericOrDateList = columnSetMultiIndicator.getNumericColumns();
        } else {
            numericOrDateList = columnSetMultiIndicator.getDateColumns();
        }
        for (Column column : numericOrDateList) {
            final TdColumn tdColumn = (TdColumn) column;

            ExpandableComposite exComp = toolkit.createExpandableComposite(composite, ExpandableComposite.TREE_NODE
                    | ExpandableComposite.CLIENT_INDENT);
            exComp.setText(DefaultMessagesImpl.getString("ColumnMasterDetailsPage.column") + tdColumn.getName()); //$NON-NLS-1$
            exComp.setLayout(new GridLayout());
            exComp.setData(columnSetMultiIndicator);
            previewChartList.add(exComp);

            final Composite comp = toolkit.createComposite(exComp);
            comp.setLayout(new GridLayout());
            comp.setLayoutData(new GridData(GridData.FILL_BOTH));

            if (tdColumn != null) {
                IRunnableWithProgress rwp = new IRunnableWithProgress() {

                    public void run(final IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                        monitor.beginTask(DefaultMessagesImpl.getString("ColumnMasterDetailsPage.createPreview")
                                + tdColumn.getName(), IProgressMonitor.UNKNOWN); //$NON-NLS-1$
                        Display.getDefault().asyncExec(new Runnable() {

                            public void run() {

                                // carete chart
                                HideSeriesPanel hideSeriesPanel = new HideSeriesPanel(columnSetMultiIndicator, tdColumn);
                                if (hideSeriesPanel != null) {
                                    Composite frameComp = toolkit.createComposite(comp, SWT.EMBEDDED);
                                    frameComp.setLayout(new GridLayout());
                                    GridData gd = new GridData();
                                    gd.heightHint = 230;
                                    gd.widthHint = 460;
                                    // if (chartData.getChartType() == EIndicatorChartType.SUMMARY_STATISTICS
                                    // && chartData.getEnity().length == 6) {
                                    // gd = new GridData();
                                    // gd.heightHint = 400;
                                    // gd.widthHint = 150;
                                    // }
                                    frameComp.setLayoutData(gd);

                                    Frame frame = SWT_AWT.new_Frame(frameComp);
                                    frame.setLayout(new java.awt.BorderLayout());

                                    frame.add(hideSeriesPanel);
                                    frame.validate();
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
                    getChartComposite().layout();
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

    /**
     * DOC xzhao Comment method "refreshChart".
     * 
     * @param form
     */
    public void refreshChart() {
        if (chartComposite != null) {
            try {
                for (Control control : chartComposite.getChildren()) {
                    control.dispose();
                }

                createPreviewCharts(form, chartComposite, true);
                chartComposite.layout();
                getForm().reflow(true);
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
     * @param outputFolder
     * @throws DataprofilerCoreException
     */

    public void saveAnalysis() throws DataprofilerCoreException {
        if (columnSetMultiIndicator.getAnalyzedColumns().size() != 0) {
            indicatorIsBlank = false;
        }
        columnCorrelationAnalysisHandler.clearAnalysis();
        columnSetMultiIndicator.getAnalyzedColumns().clear();
        List<String> comboStringList = new ArrayList<String>();
        List<String> correctString = new ArrayList<String>();
        List<Column> columnSetMultiValueList = treeViewer.getColumnSetMultiValueList();
        for (int i = 0; i < columnSetMultiValueList.size(); i++) {
            TdColumn tdColumn = (TdColumn) columnSetMultiValueList.get(i);
            if (columnSetMultiIndicator instanceof CountAvgNullIndicator && Java2SqlType.isDateInSQL(tdColumn.getJavaType())) {
                MessageDialog.openWarning(new Shell(), "Warning", "Date column cannot be used in this kind of analysis.");
                return;
            } else if (columnSetMultiIndicator instanceof MinMaxDateIndicator
                    && Java2SqlType.isNumbericInSQL(tdColumn.getJavaType())) {
                MessageDialog.openWarning(new Shell(), "Warning", "Numberic column cannot be used in this kind of analysis.");
                return;
            }
            // DataminingType type = MetadataHelper.getDataminingType(tdColumn);
            // if (columnSetMultiIndicator instanceof CountAvgNullIndicator) {
            String comboString = MetadataHelper.getDataminingType(tdColumn).getLiteral();
            comboStringList.add(comboString);
            // } else {
            // String comboString = tdColumn.getSqlDataType().getName();
            // comboStringList.add(comboString);
            // }
        }
        boolean isSave = true;
        // if (columnSetMultiIndicator instanceof CountAvgNullIndicator) {
        correctString.add(DataminingType.NOMINAL.getLiteral());
        correctString.add(DataminingType.INTERVAL.getLiteral());
        // } else {
        // correctString.add("varchar"); // DataminingType.NOMINAL.getLiteral());
        // correctString.add("date"); // DataminingType.INTERVAL.getLiteral());
        // correctString.add("datetime");
        // }
        for (String combo : comboStringList) {
            if (!correctString.contains(combo)) {
                isSave = false;
                break;
            }
        }
        if (!isSave) {
            MessageDialog
                    .openWarning(
                            new Shell(),
                            "Warning",
                            "This kind of analysis handles only  nominal columns and numeric columns. If you have selected other type of data, either remove them or change their type to \"nominal\" when appropriate");

        } else {
            TdDataProvider tdProvider = null;
            // Analysis analysis = columnCorrelationAnalysisHandler.getAnalysis();
            EList<ModelElement> tdColumns = columnCorrelationAnalysisHandler.getAnalyzedColumns();
            if (columnSetMultiValueList != null) {
                if (columnSetMultiValueList.size() != 0) {
                    tdProvider = DataProviderHelper.getTdDataProvider(SwitchHelpers.COLUMN_SWITCH
                            .doSwitch(columnSetMultiValueList.get(0)));
                    analysis.getContext().setConnection(tdProvider);
                    columnSetMultiIndicator.getAnalyzedColumns().addAll(columnSetMultiValueList);
                }

                columnCorrelationAnalysisHandler.addIndicator(columnSetMultiValueList, columnSetMultiIndicator);
            }
            // if (providerList.size() != 0) {
            //           
            // }
            columnCorrelationAnalysisHandler.setStringDataFilter(dataFilterComp.getDataFilterString());
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

            treeViewer.setDirty(false);
            dataFilterComp.setDirty(false);
        }
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (PluginConstant.ISDIRTY_PROPERTY.equals(evt.getPropertyName())) {
            ((AnalysisEditor) this.getEditor()).firePropertyChange(IEditorPart.PROP_DIRTY);
        } else if (PluginConstant.DATAFILTER_PROPERTY.equals(evt.getPropertyName())) {
            this.columnCorrelationAnalysisHandler.setStringDataFilter((String) evt.getNewValue());
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
        return super.isDirty() || (treeViewer != null && treeViewer.isDirty())
                || (dataFilterComp != null && dataFilterComp.isDirty());
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
    public AnalysisColumnNominalIntervalTreeViewer getTreeViewer() {
        return this.treeViewer;
    }

    public ScrolledForm getForm() {
        return form;
    }

    public void setForm(ScrolledForm form) {
        this.form = form;
    }

    public ColumnCorrelationAnalysisHandler getColumnCorrelationAnalysisHandler() {
        return columnCorrelationAnalysisHandler;
    }

    public ColumnSetMultiValueIndicator getColumnSetMultiValueIndicator() {
        return columnSetMultiIndicator;
    }

    public Composite[] getPreviewChartCompsites() {
        return previewChartCompsites;
    }

    public Composite getChartComposite() {
        return chartComposite;
    }

    public void fireRuningItemChanged(boolean status) {

        this.runButton.setEnabled(status);

        if (status) {
            ((AnalysisEditor) getEditor()).setRefreshResultPage(true);
            refreshChart();
        }

    }

    protected boolean canSave() {
        return true;
    }

    public boolean canRun() {
        // if (canSave()) {
        // return false;
        // }
        if (isDirty() || getTreeViewer().getTree().getItemCount() == 0) {
            return false;
        }
        return true;
    }
}
