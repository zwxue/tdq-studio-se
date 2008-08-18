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
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.part.FileEditorInput;
import org.talend.cwm.helper.CatalogHelper;
import org.talend.cwm.helper.DataProviderHelper;
import org.talend.cwm.helper.TaggedValueHelper;
import org.talend.cwm.relational.TdCatalog;
import org.talend.cwm.relational.TdSchema;
import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.cwm.softwaredeployment.TdProviderConnection;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.exception.DataprofilerCoreException;
import org.talend.dataprofiler.core.exception.ExceptionHandler;
import org.talend.dataprofiler.core.helper.AnaResourceFileHelper;
import org.talend.dataprofiler.core.model.dburl.SupportDBUrlStore;
import org.talend.dataprofiler.core.ui.editor.AbstractMetadataFormPage;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.analysis.ExecutionInformations;
import org.talend.dataquality.domain.Domain;
import org.talend.dataquality.helpers.DomainHelper;
import org.talend.dataquality.indicators.schema.CatalogIndicator;
import org.talend.dataquality.indicators.schema.ConnectionIndicator;
import org.talend.dataquality.indicators.schema.SchemaIndicator;
import org.talend.utils.sugars.ReturnCode;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * @author rli
 * 
 */
public class ConnectionMasterDetailsPage extends AbstractMetadataFormPage implements PropertyChangeListener {

    private static final String SCHEMA = "schema";

    private static final String CATALOG = "catalog";

    /**
     * Width of the first column.
     */
    private static final int COL1_WIDTH = 200;

    /**
     * Width of columns.
     */
    private static final int COL_WIDTH = 100;

    private static Logger log = Logger.getLogger(ConnectionMasterDetailsPage.class);

    Analysis connectionAnalysis;

    private Text tableFilterText;

    private Text viewFilterText;

    private TdDataProvider tdDataProvider;

    private String latestTableFilterValue;

    private String latestViewFilterValue;

    public ConnectionMasterDetailsPage(FormEditor editor, String id, String title) {
        super(editor, id, title);
    }

    @Override
    protected ModelElement getCurrentModelElement(FormEditor editor) {
        FileEditorInput input = (FileEditorInput) editor.getEditorInput();
        connectionAnalysis = AnaResourceFileHelper.getInstance().findAnalysis(input.getFile());
        return connectionAnalysis;
    }

    @Override
    protected void createFormContent(IManagedForm managedForm) {
        super.createFormContent(managedForm);
        final ScrolledForm form = managedForm.getForm();
        form.setText("Connection Analysis");
        this.metadataSection.setText("Analysis Metadata");
        this.metadataSection.setDescription("Set the properties of analysis.");
        createAnalysisParamSection(form, topComp);
        createAnalysisSummarySection(form, topComp);
        createStatisticalSection(form, topComp);
    }

    private void createAnalysisParamSection(ScrolledForm form, Composite topComp) {
        Section statisticalSection = this.createSection(form, topComp, "Analysis Parameters", false, null);
        Composite sectionClient = toolkit.createComposite(statisticalSection);
        // Pattern tablePattern = null;
        // Pattern viewPattern = null;
        // if (this.connectionAnalysis.getParameters().getDataFilter().size() > 0) {
        // domain = connectionAnalysis.getParameters().getDataFilter().get(0);
        // for (Pattern patttern : domain.getPatterns()) {
        // if (patttern.getName().equals(TABLE_PATTERN_NAME)) {
        // tablePattern = patttern;
        // } else {
        // viewPattern = patttern;
        // }
        // }
        // }

        sectionClient.setLayout(new GridLayout(2, false));
        Label tableFilterLabel = new Label(sectionClient, SWT.None);
        tableFilterLabel.setText("Filter on tables:");
        tableFilterLabel.setLayoutData(new GridData());
        tableFilterText = new Text(sectionClient, SWT.BORDER);
        EList<Domain> dataFilters = connectionAnalysis.getParameters().getDataFilter();
        String tablePattern = DomainHelper.getTablePattern(dataFilters);
        latestTableFilterValue = tablePattern == null ? PluginConstant.EMPTY_STRING : tablePattern;
        tableFilterText.setText(latestTableFilterValue);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(tableFilterText);
        tableFilterText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                setDirty(true);
            }

        });
        Label viewFilterLabel = new Label(sectionClient, SWT.None);
        viewFilterLabel.setText("Filter on views: ");
        viewFilterLabel.setLayoutData(new GridData());
        viewFilterText = new Text(sectionClient, SWT.BORDER);
        String viewPattern = DomainHelper.getViewPattern(dataFilters);
        latestViewFilterValue = viewPattern == null ? PluginConstant.EMPTY_STRING : viewPattern;
        viewFilterText.setText(latestViewFilterValue);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(viewFilterText);
        viewFilterText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                setDirty(true);
            }

        });
        statisticalSection.setClient(sectionClient);
    }

    private void createAnalysisSummarySection(ScrolledForm form, Composite topComp) {
        Section summarySection = this.createSection(form, topComp, "Analysis Summary", false, null);
        Composite sectionClient = toolkit.createComposite(summarySection);
        sectionClient.setLayout(new GridLayout(2, false));
        Composite leftComp = new Composite(sectionClient, SWT.NONE);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(leftComp);
        leftComp.setLayout(new GridLayout());
        Composite rightComp = new Composite(sectionClient, SWT.NONE);
        rightComp.setLayout(new GridLayout());
        GridDataFactory.fillDefaults().grab(true, true).applyTo(rightComp);
        EList<ModelElement> analysedElements = this.connectionAnalysis.getContext().getAnalysedElements();
        tdDataProvider = null;
        if (analysedElements.size() > 0) {
            tdDataProvider = (TdDataProvider) analysedElements.get(0);
        }
        TdProviderConnection providerConnection = DataProviderHelper.getTdProviderConnection(tdDataProvider).getObject();
        String connectionStr = providerConnection.getConnectionString();
        Properties pameterProperties = SupportDBUrlStore.getInstance().getDBPameterProperties(connectionStr);
        String labelContent = pameterProperties.getProperty(PluginConstant.DBTYPE_PROPERTY);
        Label leftLabel = new Label(leftComp, SWT.NONE);
        leftLabel.setText("DBMS: " + (labelContent == null ? PluginConstant.EMPTY_STRING : labelContent));
        leftLabel.setLayoutData(new GridData());
        leftLabel = new Label(leftComp, SWT.NONE);
        labelContent = pameterProperties.getProperty(PluginConstant.HOSTNAME_PROPERTY);
        leftLabel.setText("Server: " + (labelContent == null ? PluginConstant.EMPTY_STRING : labelContent));
        leftLabel.setLayoutData(new GridData());
        leftLabel = new Label(leftComp, SWT.NONE);
        labelContent = pameterProperties.getProperty(PluginConstant.PORT_PROPERTY);
        leftLabel.setText("Port: " + (labelContent == null ? PluginConstant.EMPTY_STRING : labelContent));
        leftLabel.setLayoutData(new GridData());
        leftLabel = new Label(leftComp, SWT.NONE);
        labelContent = TaggedValueHelper.getValue(PluginConstant.USER_PROPERTY, providerConnection);
        leftLabel.setText("Connected as: " + (labelContent == null ? PluginConstant.EMPTY_STRING : labelContent));
        leftLabel.setLayoutData(new GridData());

        List<TdCatalog> tdCatalogs = DataProviderHelper.getTdCatalogs(tdDataProvider);
        List<TdSchema> tdSchema = DataProviderHelper.getTdSchema(tdDataProvider);
        leftLabel = new Label(leftComp, SWT.NONE);
        leftLabel.setText("Catalogs: " + tdCatalogs.size());
        leftLabel.setLayoutData(new GridData());
        leftLabel = new Label(leftComp, SWT.NONE);
        leftLabel.setText("Schemata: " + tdSchema.size());
        leftLabel.setLayoutData(new GridData());

        ExecutionInformations resultMetadata = connectionAnalysis.getResults().getResultMetadata();

        Label rightLabel = new Label(rightComp, SWT.NONE);
        rightLabel.setText("Creation date: " + getFormatDateStr(connectionAnalysis.getCreationDate()));
        rightLabel.setLayoutData(new GridData());
        rightLabel = new Label(rightComp, SWT.NONE);
        rightLabel.setText("Execution date: " + getFormatDateStr(resultMetadata.getExecutionDate()));
        rightLabel.setLayoutData(new GridData());
        rightLabel = new Label(rightComp, SWT.NONE);
        rightLabel.setText("Execution Duration: " + resultMetadata.getExecutionDuration() / 1000.0d + " s");
        rightLabel.setLayoutData(new GridData());

        rightLabel = new Label(rightComp, SWT.NONE);
        String executeStatus = (resultMetadata.isLastRunOk() ? "success" : "failure");
        rightLabel.setText("Execution status: "
                + (resultMetadata.getExecutionNumber() == 0 ? PluginConstant.EMPTY_STRING : executeStatus));
        rightLabel.setLayoutData(new GridData());
        rightLabel = new Label(rightComp, SWT.NONE);
        rightLabel.setText("Number of executions: " + resultMetadata.getExecutionNumber());
        rightLabel.setLayoutData(new GridData());
        rightLabel = new Label(rightComp, SWT.NONE);
        rightLabel.setText("Last successful execution: " + getFormatDateStr(resultMetadata.getExecutionDate()));
        rightLabel.setLayoutData(new GridData());
        sectionClient.layout();
        summarySection.setClient(sectionClient);

    }

    private String getFormatDateStr(Date date) {
        if (date == null) {
            return PluginConstant.EMPTY_STRING;
        }
        String format = SimpleDateFormat.getDateTimeInstance().format(date);
        return format;
    }

    private void createStatisticalSection(ScrolledForm form, Composite topComp) {
        Section statisticalSection = this.createSection(form, topComp, "Statistical informations", false, null);
        Composite sectionClient = toolkit.createComposite(statisticalSection);
        sectionClient.setLayout(new GridLayout());

        TableViewer statisticalViewer = new TableViewer(sectionClient, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER
                | SWT.FULL_SELECTION);
        Table table = statisticalViewer.getTable();
        table.setHeaderVisible(true);
        table.setBackgroundMode(SWT.INHERIT_FORCE);
        table.setLinesVisible(true);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(table);
        List<CatalogIndicator> indicatorList = null;
        if (this.connectionAnalysis.getResults().getIndicators().size() > 0) {
            ConnectionIndicator conIndicator = (ConnectionIndicator) connectionAnalysis.getResults().getIndicators().get(0);
            indicatorList = conIndicator.getCatalogIndicators();
        } else {
            indicatorList = new ArrayList<CatalogIndicator>();
        }
        List<TdCatalog> catalogs = DataProviderHelper.getTdCatalogs(tdDataProvider);
        boolean containSchema = false;
        for (TdCatalog catalog : catalogs) {
            List<TdSchema> schemas = CatalogHelper.getSchemas(catalog);
            if (schemas.size() > 0) {
                containSchema = true;
                break;
            }
        }
        AbstractStatisticalViewerProvider provider;
        if (catalogs.size() > 0 && containSchema) {
            createCatalogSchemaColumns(table);
            provider = new CatalogSchemaViewerProvier();
            final TableViewer createSecondStatisticalTable = createSecondStatisticalTable(sectionClient);
            statisticalViewer.addSelectionChangedListener(new ISelectionChangedListener() {

                public void selectionChanged(SelectionChangedEvent event) {
                    StructuredSelection selection = (StructuredSelection) event.getSelection();
                    CatalogIndicator firstElement = (CatalogIndicator) selection.getFirstElement();
                    createSecondStatisticalTable.setInput(firstElement.getSchemaIndicators());
                    createSecondStatisticalTable.getTable().setVisible(true);
                }

            });
        } else {
            if (catalogs.size() > 0) {
                createCatalogTableColumns(table);
                provider = new CatalogViewerProvier();
            } else {
                createSchemaTableColumns(table);
                provider = new SchemaViewerProvier();
            }
        }
        statisticalViewer.setLabelProvider(provider);
        statisticalViewer.setContentProvider(provider);
        statisticalViewer.setInput(indicatorList);
        sectionClient.layout();
        statisticalSection.setClient(sectionClient);

    }

    private TableViewer createSecondStatisticalTable(Composite parent) {
        TableViewer secondTableViewer = new TableViewer(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.FULL_SELECTION);
        Table table = secondTableViewer.getTable();
        table.setHeaderVisible(true);
        table.setBackgroundMode(SWT.INHERIT_FORCE);
        table.setLinesVisible(true);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(table);
        ((GridData) table.getLayoutData()).heightHint = 60;
        createSchemaTableColumns(table);
        SchemaViewerProvier provider = new SchemaViewerProvier();
        secondTableViewer.setLabelProvider(provider);
        secondTableViewer.setContentProvider(provider);
        table.setVisible(false);
        return secondTableViewer;
    }

    private void createCatalogTableColumns(Table table) {
        TableColumn tableColumn = new TableColumn(table, SWT.LEFT | SWT.FILL);
        tableColumn.setText(CATALOG);
        tableColumn.setWidth(COL1_WIDTH);
        createNbRowsCol(table, CATALOG);
        createCommonStatisticalColumns(table);
    }

    private void createSchemaTableColumns(Table table) {
        TableColumn tableColumn = new TableColumn(table, SWT.LEFT);
        tableColumn.setText(SCHEMA);
        tableColumn.setWidth(COL1_WIDTH);
        createNbRowsCol(table, SCHEMA);
        createCommonStatisticalColumns(table);
    }

    /**
     * DOC scorreia Comment method "createNbRowsCol".
     * 
     * @param table
     * @param container
     */
    private void createNbRowsCol(Table table, String container) {
        TableColumn tableColumn;
        tableColumn = new TableColumn(table, SWT.RIGHT);
        tableColumn.setText("#rows");
        tableColumn.setToolTipText("Number of rows in " + container);
        tableColumn.setWidth(COL_WIDTH);
    }

    private void createCatalogSchemaColumns(Table table) {
        TableColumn tableColumn = new TableColumn(table, SWT.LEFT);
        tableColumn.setText(CATALOG);
        tableColumn.setWidth(COL1_WIDTH);
        createNbRowsCol(table, CATALOG);
        tableColumn = new TableColumn(table, SWT.RIGHT);
        tableColumn.setText("#schemata");
        tableColumn.setWidth(COL_WIDTH);
        tableColumn = new TableColumn(table, SWT.RIGHT);
        tableColumn.setText("#rows/schema");
        tableColumn.setToolTipText("Number of rows per schema");
        tableColumn.setWidth(COL_WIDTH);
        createCommonStatisticalColumns(table);
    }

    /**
     * DOC scorreia Comment method "createCommonStatisticalColumns".
     * 
     * @param table
     */
    private void createCommonStatisticalColumns(Table table) {
        TableColumn tableColumn;
        tableColumn = new TableColumn(table, SWT.RIGHT);
        tableColumn.setText("#tables");
        tableColumn.setToolTipText("Number of tables");
        tableColumn.setWidth(COL_WIDTH);
        tableColumn = new TableColumn(table, SWT.RIGHT);
        tableColumn.setText("#rows/table");
        tableColumn.setToolTipText("Number of rows per table");
        tableColumn.setWidth(COL_WIDTH);
        tableColumn = new TableColumn(table, SWT.RIGHT);
        tableColumn.setText("#views");
        tableColumn.setToolTipText("Number of views");
        tableColumn.setWidth(COL_WIDTH);
        tableColumn = new TableColumn(table, SWT.RIGHT);
        tableColumn.setText("#rows/view");
        tableColumn.setToolTipText("Number of rows per view");
        tableColumn.setWidth(COL_WIDTH);
        tableColumn = new TableColumn(table, SWT.RIGHT);
        tableColumn.setText("#keys");
        tableColumn.setToolTipText("Number of primary keys");
        tableColumn.setWidth(COL_WIDTH);
        tableColumn = new TableColumn(table, SWT.RIGHT);
        tableColumn.setText("#indexes");
        tableColumn.setToolTipText("Number of indexes");
        tableColumn.setWidth(COL_WIDTH);
    }

    public void saveAnalysis() throws DataprofilerCoreException {
        EList<Domain> dataFilters = connectionAnalysis.getParameters().getDataFilter();
        if (!this.tableFilterText.getText().equals(latestTableFilterValue)) {
            DomainHelper.setDataFilterTablePattern(dataFilters, tableFilterText.getText());
            latestTableFilterValue = this.tableFilterText.getText();
        }
        if (!this.viewFilterText.getText().equals(latestViewFilterValue)) {
            DomainHelper.setDataFilterViewPattern(dataFilters, viewFilterText.getText());
            latestViewFilterValue = this.viewFilterText.getText();
        }

        ReturnCode save = AnaResourceFileHelper.getInstance().save(connectionAnalysis);
        if (save.isOk()) {
            log.info("Success to save connection analysis:" + connectionAnalysis.getFileName());
        }
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (PluginConstant.ISDIRTY_PROPERTY.equals(evt.getPropertyName())) {
            ((AnalysisEditor) this.getEditor()).firePropertyChange(IEditorPart.PROP_DIRTY);
        }

    }

    @Override
    public void doSave(IProgressMonitor monitor) {
        super.doSave(monitor);
        try {
            saveAnalysis();
            this.isDirty = false;
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
        return super.isDirty();
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    /**
     * The provider for display the data of catalog table viewer.
     */
    class CatalogViewerProvier extends AbstractStatisticalViewerProvider {

        @Override
        protected String getOtherColumnText(int columnIndex, SchemaIndicator schemaIndicator) {
            String text = PluginConstant.EMPTY_STRING;
            switch (columnIndex) {
            case 1:
                return text + schemaIndicator.getTableRowCount();
            case 2:
                return text + schemaIndicator.getTableCount();
            case 3:
                return text + getRowCountPerTable(schemaIndicator);
            case 4:
                return text + schemaIndicator.getViewCount();
            case 5:
                return text + getRowCountPerView(schemaIndicator);
            case 6:
                return text + schemaIndicator.getKeyCount();
            case 7:
                return text + schemaIndicator.getIndexCount();
            default:
                break;
            }
            return text;
        }
    }

    /**
     * The provider for display the data of schema table viewer.
     */
    class SchemaViewerProvier extends AbstractStatisticalViewerProvider {

        @Override
        protected String getOtherColumnText(int columnIndex, SchemaIndicator schemaIndicator) {
            String text = PluginConstant.EMPTY_STRING;
            switch (columnIndex) {
            case 2:
                return text + schemaIndicator.getTableCount();
            case 3:
                return text + getRowCountPerTable(schemaIndicator);
            case 4:
                return text + schemaIndicator.getViewCount();
            case 5:
                return text + getRowCountPerView(schemaIndicator);
            case 6:
                return text + schemaIndicator.getKeyCount();
            case 7:
                return text + schemaIndicator.getIndexCount();
            default:
                break;
            }
            return text;
        }
    }

    /**
     * The provider for display the data of catalog(contain schemas) table viewer.
     */
    class CatalogSchemaViewerProvier extends AbstractStatisticalViewerProvider {

        @Override
        protected String getOtherColumnText(int columnIndex, SchemaIndicator schemaIndicator) {
            String text = PluginConstant.EMPTY_STRING;
            CatalogIndicator catalogIndicator = null;
            if (schemaIndicator instanceof CatalogIndicator) {
                catalogIndicator = (CatalogIndicator) schemaIndicator;
            } else {
                return PluginConstant.EMPTY_STRING;
            }
            switch (columnIndex) {
            case 2:
                return text + catalogIndicator.getSchemaCount();
            case 3:
                return text + getRowCountPerSchema(catalogIndicator);
            case 4:
                return text + catalogIndicator.getTableCount();
            case 5:
                return text + getRowCountPerTable(catalogIndicator);
            case 6:
                return text + schemaIndicator.getViewCount();
            case 7:
                return text + getRowCountPerView(schemaIndicator);
            case 8:
                return text + catalogIndicator.getKeyCount();
            case 9:
                return text + catalogIndicator.getIndexCount();
            default:
                break;
            }
            return text;
        }
    }

    private static String getRowCountPerTable(SchemaIndicator schemaIndicator) {
        Double frac = Double.valueOf(schemaIndicator.getTableRowCount()) / schemaIndicator.getTableCount();
        return formatDouble(frac);
    }

    private static String getRowCountPerView(SchemaIndicator schemaIndicator) {
        Double frac = Double.valueOf(schemaIndicator.getViewRowCount()) / schemaIndicator.getViewCount();
        return formatDouble(frac);
    }

    private static String getRowCountPerSchema(CatalogIndicator catalogIndicator) {
        Double frac = Double.valueOf(catalogIndicator.getTableRowCount()) / catalogIndicator.getSchemaCount();
        return formatDouble(frac);
    }

    private static String formatDouble(Double frac) {
        if (frac.isNaN()) {
            return String.valueOf(Double.NaN);
        }
        String formatValue = new DecimalFormat("0.00").format(frac);
        return formatValue;
    }
}
