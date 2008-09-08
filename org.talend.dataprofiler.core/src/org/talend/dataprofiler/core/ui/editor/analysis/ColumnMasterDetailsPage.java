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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
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
import org.eclipse.ui.part.FileEditorInput;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.cwm.management.api.DqRepositoryViewService;
import org.talend.cwm.relational.TdColumn;
import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.exception.DataprofilerCoreException;
import org.talend.dataprofiler.core.exception.ExceptionHandler;
import org.talend.dataprofiler.core.helper.AnaResourceFileHelper;
import org.talend.dataprofiler.core.helper.EObjectHelper;
import org.talend.dataprofiler.core.model.ColumnIndicator;
import org.talend.dataprofiler.core.ui.action.actions.RunAnalysisAction;
import org.talend.dataprofiler.core.ui.dialog.ColumnsSelectionDialog;
import org.talend.dataprofiler.core.ui.editor.AbstractMetadataFormPage;
import org.talend.dataprofiler.core.ui.editor.composite.AnalysisColumnTreeViewer;
import org.talend.dataprofiler.core.ui.editor.composite.DataFilterComp;
import org.talend.dataprofiler.core.ui.editor.preview.CompositeIndicator;
import org.talend.dataprofiler.core.ui.editor.preview.IndicatorChartFactory;
import org.talend.dataprofiler.core.ui.editor.preview.model.ChartWithData;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.helpers.MetadataHelper;
import org.talend.dataquality.indicators.DataminingType;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dq.analysis.ColumnAnalysisHandler;
import org.talend.utils.sugars.ReturnCode;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * @author rli
 * 
 */
public class ColumnMasterDetailsPage extends AbstractMetadataFormPage implements PropertyChangeListener {

    private static Logger log = Logger.getLogger(ColumnMasterDetailsPage.class);

    AnalysisColumnTreeViewer treeViewer;

    DataFilterComp dataFilterComp;

    ColumnAnalysisHandler analysisHandler;

    private ColumnIndicator[] currentColumnIndicators;

    private String stringDataFilter;

    private Composite chartComposite;

    private ScrolledForm form;

    private static final int TREE_MAX_LENGTH = 400;

    private Composite[] previewChartCompsites;

    public ColumnMasterDetailsPage(FormEditor editor, String id, String title) {
        super(editor, id, title);
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

    @Override
    protected ModelElement getCurrentModelElement(FormEditor editor) {

        FileEditorInput input = (FileEditorInput) editor.getEditorInput();
        Analysis findAnalysis = AnaResourceFileHelper.getInstance().findAnalysis(input.getFile());
        return findAnalysis;
    }

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
        form.setText("Column Analysis");
        metadataSection.setText("Analysis Metadata");
        metadataSection.setDescription("Set the properties of analysis.");

        createAnalysisColumnsSection(form, topComp);

        createDataFilterSection(form, topComp);

        Composite previewComp = toolkit.createComposite(sForm);
        previewComp.setLayoutData(new GridData(GridData.FILL_BOTH));
        previewComp.setLayout(new GridLayout());

        createPreviewSection(form, previewComp);

        GridData gdBtn = new GridData();
        gdBtn.horizontalAlignment = SWT.CENTER;
        gdBtn.horizontalSpan = 2;
        gdBtn.widthHint = 120;
        Button runBtn = toolkit.createButton(form.getBody(), " Run ", SWT.NONE);
        runBtn.setLayoutData(gdBtn);

        runBtn.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {

                new RunAnalysisAction().run();

            }

        });
    }

    void createAnalysisColumnsSection(final ScrolledForm form, Composite anasisDataComp) {
        Section section = createSection(form, anasisDataComp, "Analyzed Columns", false, null);

        Composite topComp = toolkit.createComposite(section);
        topComp.setLayout(new GridLayout());

        Hyperlink clmnBtn = toolkit.createHyperlink(topComp, "Select columns to analyze", SWT.NONE);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).applyTo(clmnBtn);
        clmnBtn.addHyperlinkListener(new HyperlinkAdapter() {

            public void linkActivated(HyperlinkEvent e) {
                openColumnsSelectionDialog();
            }

        });

        Hyperlink indcBtn = toolkit.createHyperlink(topComp, "Select indicators for each column", SWT.NONE);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).applyTo(indcBtn);
        indcBtn.addHyperlinkListener(new HyperlinkAdapter() {

            public void linkActivated(HyperlinkEvent e) {

                treeViewer.openIndicatorSelectDialog(null);
            }

        });

        Composite tree = toolkit.createComposite(topComp, SWT.NONE);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(tree);
        tree.setLayout(new GridLayout());
        ((GridData) tree.getLayoutData()).heightHint = TREE_MAX_LENGTH;

        treeViewer = new AnalysisColumnTreeViewer(tree, this);
        treeViewer.setDirty(false);
        treeViewer.addPropertyChangeListener(this);
        section.setClient(topComp);

    }

    /**
     * 
     */
    public void openColumnsSelectionDialog() {
        ColumnIndicator[] columnIndicator = treeViewer.getColumnIndicator();
        ColumnsSelectionDialog dialog = new ColumnsSelectionDialog(null, "Column Selection", columnIndicator, "Column Selection");
        if (dialog.open() == Window.OK) {
            Object[] columns = dialog.getResult();
            treeViewer.setInput(columns);
            return;
        }
    }

    void createPreviewSection(final ScrolledForm form, Composite parent) {

        Section section = createSection(form, parent, "Graphics", true, "");
        section.setLayoutData(new GridData(GridData.FILL_BOTH));

        Composite sectionClient = toolkit.createComposite(section);
        sectionClient.setLayout(new GridLayout());
        sectionClient.setLayoutData(new GridData(GridData.FILL_BOTH));

        ImageHyperlink refreshBtn = toolkit.createImageHyperlink(sectionClient, SWT.NONE);
        refreshBtn.setText("Refresh the graphics");
        refreshBtn.setImage(ImageLib.getImage(ImageLib.SECTION_PREVIEW));
        final Label message = toolkit.createLabel(sectionClient, "");
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
                    boolean returnCode = MessageDialog.openConfirm(null, "View the result of analyis",
                            "Do you want to run the analysis or simply see sample data?");

                    if (returnCode) {
                        new RunAnalysisAction().run();
                        message.setVisible(false);
                    } else {
                        createPreviewCharts(form, chartComposite, false);
                        message.setText("Warning: currently displayed values are only sample values. "
                                + "Run the analysis to get the real values");
                        message.setVisible(true);
                    }
                } else {
                    createPreviewCharts(form, chartComposite, true);
                }

                chartComposite.layout();
                form.reflow(true);
            }

        });

        section.setClient(sectionClient);
    }

    public void createPreviewCharts(final ScrolledForm form, final Composite composite, final boolean isCreate) {

        List<Composite> previewChartList = new ArrayList<Composite>();

        for (final ColumnIndicator columnIndicator : this.treeViewer.getColumnIndicator()) {

            final TdColumn column = columnIndicator.getTdColumn();

            ExpandableComposite exComp = toolkit.createExpandableComposite(composite, ExpandableComposite.TREE_NODE
                    | ExpandableComposite.CLIENT_INDENT);
            exComp.setText("Column: " + column.getName());
            exComp.setLayout(new GridLayout());
            exComp.setData(columnIndicator);
            addExpandableCompositeListener(exComp);
            previewChartList.add(exComp);

            final Composite comp = toolkit.createComposite(exComp);
            comp.setLayout(new GridLayout());
            comp.setLayoutData(new GridData(GridData.FILL_BOTH));

            if (columnIndicator.getIndicators().length != 0) {

                IRunnableWithProgress rwp = new IRunnableWithProgress() {

                    public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

                        monitor.beginTask("Creating preview for " + column.getName(), IProgressMonitor.UNKNOWN);

                        Display.getDefault().asyncExec(new Runnable() {

                            public void run() {

                                for (ChartWithData chart : IndicatorChartFactory.createChart(columnIndicator, isCreate)) {
                                    if (chart.getImageDescriptor() != null) {
                                        ImageHyperlink image = toolkit.createImageHyperlink(comp, SWT.WRAP);
                                        image.setImage(chart.getImageDescriptor().createImage());
                                        if (chart.getChartNamedType().equals(CompositeIndicator.SUMMARY_STATISTICS)) {
                                            ColumnAnalysisResultPage.addShowDefinition(image);
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

            exComp.setExpanded(true);

            exComp.setClient(comp);
        }

        if (!previewChartList.isEmpty()) {
            this.previewChartCompsites = previewChartList.toArray(new Composite[previewChartList.size()]);
        }
    }

    private void addExpandableCompositeListener(final ExpandableComposite composite) {

        composite.addExpansionListener(new ExpansionAdapter() {

            @Override
            public void expansionStateChanging(ExpansionEvent e) {
                TreeItem theSuitedTreeItem = getTheSuitedTreeItem(getTreeViewer().getTree().getItems(), composite);
                if (e.getState()) {
                    if (theSuitedTreeItem != null) {
                        theSuitedTreeItem.setExpanded(true);
                    }
                } else {
                    if (theSuitedTreeItem != null) {
                        theSuitedTreeItem.setExpanded(false);
                    }
                }

                getTreeViewer().getTree().layout();

                getTreeViewer().getTree().setSelection(theSuitedTreeItem);
            }

        });
    }

    private TreeItem getTheSuitedTreeItem(TreeItem[] itemes, Composite composite) {
        for (TreeItem item : itemes) {
            ColumnIndicator columnIndicator = (ColumnIndicator) item.getData(AnalysisColumnTreeViewer.COLUMN_INDICATOR_KEY);

            if (columnIndicator == composite.getData()) {
                return item;
            }
        }

        return null;
    }

    /**
     * DOC zqin Comment method "refreshChart".
     * 
     * @param form
     */
    public void refreshChart(ScrolledForm form) {
        if (chartComposite != null) {
            try {
                for (Control control : chartComposite.getChildren()) {
                    control.dispose();
                }

                createPreviewCharts(form, chartComposite, true);
                chartComposite.layout();
                form.reflow(true);
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
        Section section = createSection(form, anasisDataComp, "Data Filter", false, "Edit the data filter:");

        Composite sectionClient = toolkit.createComposite(section);
        dataFilterComp = new DataFilterComp(sectionClient, stringDataFilter);
        dataFilterComp.addPropertyChangeListener(this);
        section.setClient(sectionClient);
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
                DqRepositoryViewService.saveOpenDataProvider(tdProvider);
            }
            // AnaResourceFileHelper.getInstance().setResourcesNumberChanged(true);
            if (log.isDebugEnabled()) {
                log.debug("Saved in  " + urlString + " successful");
            }
        } else {
            throw new DataprofilerCoreException("Problem saving analysis " + analysis.getName() + " in file: " + urlString + ": "
                    + saved.getMessage());
        }

        // TODO get the domain constraint, we will see later.
        // Domain dataFilter = getDataFilter(dataManager, (Column) column); // CAST here for test
        // analysisBuilder.addFilterOnData(dataFilter);
        //
        // FolderProvider folderProvider = new FolderProvider();
        // folderProvider.setFolder(new File(outputFolder));
        // DqRepositoryViewService.saveDomain(dataFilter, folderProvider);
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (PluginConstant.ISDIRTY_PROPERTY.equals(evt.getPropertyName())) {
            ((AnalysisEditor) this.getEditor()).firePropertyChange(IEditorPart.PROP_DIRTY);
        } else if (PluginConstant.DATAFILTER_PROPERTY.equals(evt.getPropertyName())) {
            this.analysisHandler.setStringDataFilter((String) evt.getNewValue());
        }

    }

    @Override
    public void doSave(IProgressMonitor monitor) {
        super.doSave(monitor);
        try {
            saveAnalysis();
            this.isDirty = false;
            treeViewer.setDirty(false);
            dataFilterComp.setDirty(false);
        } catch (DataprofilerCoreException e) {
            ExceptionHandler.process(e, Level.ERROR);
            e.printStackTrace();
        }
    }

    public void setDirty(boolean isDirty) {
        if (this.isDirty != isDirty) {
            this.isDirty = isDirty;
            ((AnalysisEditor) this.getEditor()).firePropertyChange(IEditorPart.PROP_DIRTY);
        }
    }

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

}
