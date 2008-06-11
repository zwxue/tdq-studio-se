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
package org.talend.dataprofiler.core.ui.editor;

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
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
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
import org.talend.dataprofiler.core.ui.editor.composite.AnasisColumnTreeViewer;
import org.talend.dataprofiler.core.ui.editor.composite.DataFilterComp;
import org.talend.dataprofiler.core.ui.editor.preview.IndicatorChartFactory;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.indicators.DataminingType;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dq.analysis.ColumnAnalysisHandler;
import org.talend.utils.sugars.ReturnCode;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * @author rli
 * 
 */
public class ColumnMasterDetailsPage extends AbstractFormPage implements PropertyChangeListener {

    private static Logger log = Logger.getLogger(ColumnMasterDetailsPage.class);

    AnasisColumnTreeViewer treeViewer;

    DataFilterComp dataFilterComp;

    ColumnAnalysisHandler analysisHandler;

    private ColumnIndicator[] currentColumnIndicators;

    private String stringDataFilter;

    private static final int TREE_MAX_LENGTH = 500;

    public ColumnMasterDetailsPage(FormEditor editor, String id, String title) {
        super(editor, id, title);
    }

    public void initialize(FormEditor editor) {
        super.initialize(editor);
        FileEditorInput input = (FileEditorInput) editor.getEditorInput();
        analysisHandler = new ColumnAnalysisHandler();
        analysisHandler.setAnalysis(AnaResourceFileHelper.getInstance().findAnalysis(input.getFile()));
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
            currentColumnIndicator.setDataminingType(dataminingType == null ? DataminingType.NOMINAL : dataminingType);
            Collection<Indicator> indicatorList = analysisHandler.getIndicators(tdColumn);
            currentColumnIndicator.setIndicators(indicatorList.toArray(new Indicator[indicatorList.size()]));
            columnIndicatorList.add(currentColumnIndicator);
        }
        currentColumnIndicators = columnIndicatorList.toArray(new ColumnIndicator[columnIndicatorList.size()]);
    }

    @Override
    protected void createFormContent(IManagedForm managedForm) {
        super.createFormContent(managedForm);
        final ScrolledForm form = managedForm.getForm();
        metadataSection.setText("Analysis metadata");
        metadataSection.setDescription("Set the properties of analysis.");
        createAnalysisColumnsSection(form, topComp);
        createDataFilterSection(form, topComp);
        Composite body = form.getBody();
        Composite previewComp = toolkit.createComposite(body);
        GridData previewData = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);

        previewComp.setLayoutData(previewData);
        previewComp.setLayout(new GridLayout());
        createPreviewSection(form, previewComp);
    }

    protected void fireTextChange() {
        analysisHandler.setName(nameText.getText());
        analysisHandler.setPurpose(purposeText.getText());
        analysisHandler.setDescription(descriptionText.getText());
        analysisHandler.setAuthor(authorText.getText());
        analysisHandler.setStatus(statusCombo.getText());
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

                treeViewer.openIndicatorSelectDialog(getSite().getShell());
            }

        });

        Composite tree = toolkit.createComposite(topComp, SWT.BORDER);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(tree);
        tree.setLayout(new GridLayout());
        ((GridData) tree.getLayoutData()).heightHint = TREE_MAX_LENGTH;

        treeViewer = new AnasisColumnTreeViewer(tree, currentColumnIndicators, analysisHandler.getAnalysis());
        treeViewer.setDirty(false);
        treeViewer.addPropertyChangeListener(this);

        section.setClient(topComp);

    }

    /**
     * 
     */
    public void openColumnsSelectionDialog() {
        ColumnIndicator[] columnIndicator = treeViewer.getColumnIndicator();
        ColumnsSelectionDialog dialog = new ColumnsSelectionDialog(getSite().getShell(), columnIndicator, "Column Selection");
        if (dialog.open() == Window.OK) {
            Object[] columns = dialog.getResult();
            treeViewer.setInput(columns);
            return;
        }
    }

    void createPreviewSection(final ScrolledForm form, Composite parent) {

        Section section = createSection(form, parent, "Preview", true, "");

        Composite sectionClient = toolkit.createComposite(section);
        sectionClient.setLayout(new GridLayout());
        sectionClient.setLayoutData(new GridData(GridData.FILL_BOTH));

        ImageHyperlink refreshBtn = toolkit.createImageHyperlink(sectionClient, SWT.NONE);
        refreshBtn.setText("Refresh the preview");
        refreshBtn.setImage(ImageLib.getImage(ImageLib.SECTION_PREVIEW));
        final Label message = toolkit.createLabel(sectionClient, "");
        message.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_RED));
        message.setVisible(false);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).applyTo(sectionClient);

        final Composite composite = toolkit.createComposite(sectionClient);
        composite.setLayout(new GridLayout());
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));

        final Analysis analysis = analysisHandler.getAnalysis();

        refreshBtn.addHyperlinkListener(new HyperlinkAdapter() {

            public void linkActivated(HyperlinkEvent e) {

                for (Control control : composite.getChildren()) {
                    control.dispose();
                }

                boolean analysisStatue = analysis.getResults().getResultMetadata() != null
                        && analysis.getResults().getResultMetadata().getExecutionDate() != null;

                if (!analysisStatue) {
                    boolean returnCode = MessageDialog.openConfirm(null, "Preview the result of analyis",
                            "Do you want to run the analysis or simply see sample data?");

                    if (returnCode) {
                        RunAnalysisAction runAction = new RunAnalysisAction();
                        runAction.run();
                        createPreviewCharts(form, composite, true);
                        message.setVisible(false);
                    } else {
                        createPreviewCharts(form, composite, false);
                        message.setText("Warning: currently displayed values are only sample values. "
                                + "Run the analysis to get the real values");
                        message.setVisible(true);
                    }
                } else {
                    createPreviewCharts(form, composite, true);
                }

                composite.layout();
                form.reflow(true);
            }

        });

        section.setClient(sectionClient);
    }

    private void createPreviewCharts(final ScrolledForm form, final Composite composite, final boolean isCreate) {

        for (ModelElement modelElement : analysisHandler.getAnalyzedColumns()) {

            final TdColumn column = SwitchHelpers.COLUMN_SWITCH.doSwitch(modelElement);
            final Collection<Indicator> indicators = analysisHandler.getIndicators(column);
            final ColumnIndicator columnIndicator = new ColumnIndicator(column);
            columnIndicator.setIndicators(indicators.toArray(new Indicator[indicators.size()]));

            ExpandableComposite exComp = toolkit.createExpandableComposite(composite, ExpandableComposite.TREE_NODE
                    | ExpandableComposite.CLIENT_INDENT);

            exComp.setText("Column: " + column.getName());
            exComp.setLayout(new GridLayout());
            final Composite comp = toolkit.createComposite(exComp);
            comp.setLayout(new GridLayout());
            comp.setLayoutData(new GridData(GridData.FILL_BOTH));

            if (columnIndicator.getIndicators().length != 0) {

                IRunnableWithProgress rwp = new IRunnableWithProgress() {

                    public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

                        monitor.beginTask("Creating preview for " + column.getName(), IProgressMonitor.UNKNOWN);

                        Display.getDefault().asyncExec(new Runnable() {

                            public void run() {

                                for (ImageDescriptor descriptor : IndicatorChartFactory.createChart(columnIndicator, isCreate)) {

                                    ImageHyperlink image = toolkit.createImageHyperlink(comp, SWT.WRAP);
                                    image.setImage(descriptor.createImage());
                                }
                            }

                        });

                        monitor.done();
                    }

                };

                try {
                    new ProgressMonitorDialog(null).run(true, false, rwp);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                exComp.setExpanded(true);
            }

            exComp.setClient(comp);

            exComp.addExpansionListener(new ExpansionAdapter() {

                public void expansionStateChanged(ExpansionEvent e) {
                    form.reflow(true);
                }

            });
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
        if (columnIndicators != null) {
            if (columnIndicators.length != 0) {
                tdProvider = EObjectHelper.getTdDataProvider(columnIndicators[0].getTdColumn());
                analysisHandler.getAnalysis().getContext().setConnection(tdProvider);
            }
            for (ColumnIndicator columnIndicator : columnIndicators) {
                analysisHandler.addIndicator(columnIndicator.getTdColumn(), columnIndicator.getIndicators());
                analysisHandler.setDatamingType(columnIndicator.getDataminingType().getLiteral(), columnIndicator.getTdColumn());
            }
        }
        // if (providerList.size() != 0) {
        //           
        // }
        analysisHandler.setStringDataFilter(dataFilterComp.getDataFilterString());
        boolean modifiedResourcesSaved = analysisHandler.saveModifiedResources();
        if (!modifiedResourcesSaved) {
            log.error("Problem when saving modified resource.");
        }
        // AnalysisWriter writer = new AnalysisWriter();

        String urlString = PluginConstant.EMPTY_STRING;
        // try {
        // urlString = editorInput.getFile();
        // analysisHandler.getAnalysis().setUrl(urlString);
        // } catch (MalformedURLException e) {
        // e.printStackTrace();
        // }

        // FIXME after i set the options of bins designer, and when saving the file, it cause a exception.

        // File file = new File(editorInput.getFile().getParent() + File.separator + fileName);
        // ReturnCode saved = writer.save(analysisHandler.getAnalysis(), file);
        ReturnCode saved = AnaResourceFileHelper.getInstance().save(analysisHandler.getAnalysis());
        if (saved.isOk()) {
            if (tdProvider != null) {
                DqRepositoryViewService.saveOpenDataProvider(tdProvider);
            }
            AnaResourceFileHelper.getInstance().setResourceChanged(true);
            log.info("Saved in  " + urlString + " successful");
        } else {
            throw new DataprofilerCoreException("Problem saving file: " + urlString + ": " + saved.getMessage());
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
    protected void initMetaTextFied() {
        nameText.setText(analysisHandler.getName() == null ? PluginConstant.EMPTY_STRING : analysisHandler.getName());
        purposeText.setText(analysisHandler.getPurpose() == null ? PluginConstant.EMPTY_STRING : analysisHandler.getPurpose());
        descriptionText.setText(analysisHandler.getDescription() == null ? PluginConstant.EMPTY_STRING : analysisHandler
                .getDescription());
        authorText.setText(analysisHandler.getAuthor() == null ? PluginConstant.EMPTY_STRING : analysisHandler.getAuthor());
        statusCombo.setText(analysisHandler.getStatus() == null ? PluginConstant.EMPTY_STRING : analysisHandler.getStatus());
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
    public AnasisColumnTreeViewer getTreeViewer() {
        return this.treeViewer;
    }

}
