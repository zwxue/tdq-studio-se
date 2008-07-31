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
import org.talend.dataquality.domain.pattern.Pattern;
import org.talend.dataquality.domain.pattern.impl.RegularExpressionImpl;
import org.talend.dataquality.indicators.schema.ConnectionIndicator;
import org.talend.dataquality.indicators.schema.SchemaIndicator;
import org.talend.utils.sugars.ReturnCode;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * @author rli
 * 
 */
public class ConnectionMasterDetailsPage extends AbstractMetadataFormPage implements PropertyChangeListener {

    private static Logger log = Logger.getLogger(ConnectionMasterDetailsPage.class);

    private static final String TABLE_PATTERN_NAME = "TABLE_PATTERN_NAME";

    // private static final String VIEW_PATTERN_NAME = "VIEW_PATTERN_NAME";

    Analysis connectionAnalysis;

    private Text tableFilterText;

    private Text viewFilterText;

    private Domain domain;

    private TdDataProvider tdDataProvider;

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
        Pattern tablePattern = null;
        Pattern viewPattern = null;
        if (this.connectionAnalysis.getParameters().getDataFilter().size() > 0) {
            domain = connectionAnalysis.getParameters().getDataFilter().get(0);
            for (Pattern patttern : domain.getPatterns()) {
                if (patttern.getName().equals(TABLE_PATTERN_NAME)) {
                    tablePattern = patttern;
                } else {
                    viewPattern = patttern;
                }
            }
        }

        sectionClient.setLayout(new GridLayout(2, false));
        Label tableFilterLabel = new Label(sectionClient, SWT.None);
        tableFilterLabel.setText("Filter on tables:");
        tableFilterLabel.setLayoutData(new GridData());
        tableFilterText = new Text(sectionClient, SWT.BORDER);
        tableFilterText.setText(tablePattern == null ? PluginConstant.EMPTY_STRING : ((RegularExpressionImpl) tablePattern
                .getComponents().get(0)).getExpression().getBody());
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
        viewFilterText.setText(viewPattern == null ? PluginConstant.EMPTY_STRING : ((RegularExpressionImpl) viewPattern
                .getComponents().get(0)).getExpression().getBody());
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

        // EList<Indicator> indicators = connectionAnalysis.getResults().getIndicators();
        // ConnectionIndicator connectionIndicator = null;
        // if (indicators.size() > 0) {
        // connectionIndicator = (ConnectionIndicator) indicators.get(0);
        // }
        // int tableCount = connectionIndicator.getTableCount();

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

        TableViewer statisticalViewer = new TableViewer(sectionClient, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER
                | SWT.FULL_SELECTION);
        Table table = statisticalViewer.getTable();
        table.setHeaderVisible(true);
        table.setBackgroundMode(SWT.INHERIT_FORCE);
        table.setLinesVisible(true);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(table);
        List<? extends SchemaIndicator> indicatorList = null;
        if (this.connectionAnalysis.getResults().getIndicators().size() > 0) {
            ConnectionIndicator conIndicator = (ConnectionIndicator) connectionAnalysis.getResults().getIndicators().get(0);
            indicatorList = conIndicator.getCatalogIndicators();
            // indicatorList = conIndicator.getSchemaIndicators();
        } else {
            indicatorList = new ArrayList<SchemaIndicator>();
        }
        List<TdCatalog> catalogs = DataProviderHelper.getTdCatalogs(tdDataProvider);
        AbstractStatisticalViewerProvider provider;
        if (catalogs.size() > 0) {
            createCatalogTableColumns(table);
            provider = new CatalogViewerProvier();
        } else {
            createSchemaTableColumns(table);
            provider = new SchemaViewerProvier();
        }
        statisticalViewer.setLabelProvider(provider);
        statisticalViewer.setContentProvider(provider);
        statisticalViewer.setInput(indicatorList);
        sectionClient.layout();
        statisticalSection.setClient(sectionClient);

    }

    private void createCatalogTableColumns(Table table) {
        TableColumn tableColumn = new TableColumn(table, SWT.CENTER);
        tableColumn.setText("catalog");
        tableColumn.setWidth(120);
        tableColumn = new TableColumn(table, SWT.CENTER);
        tableColumn.setText("#rows");
        tableColumn.setWidth(100);
        tableColumn = new TableColumn(table, SWT.CENTER);
        tableColumn.setText("#tables");
        tableColumn.setWidth(100);
        tableColumn = new TableColumn(table, SWT.CENTER);
        tableColumn.setText("#rows/table");
        tableColumn.setWidth(100);
        tableColumn = new TableColumn(table, SWT.CENTER);
        tableColumn.setText("#keys");
        tableColumn.setWidth(100);
        tableColumn = new TableColumn(table, SWT.CENTER);
        tableColumn.setText("#indexes");
        tableColumn.setWidth(100);
    }

    private void createSchemaTableColumns(Table table) {
        TableColumn tableColumn = new TableColumn(table, SWT.CENTER);
        tableColumn.setText("schema");
        tableColumn.setWidth(120);
        tableColumn = new TableColumn(table, SWT.CENTER);
        tableColumn.setText("#rows");
        tableColumn.setWidth(100);
        tableColumn = new TableColumn(table, SWT.CENTER);
        tableColumn.setText("#tables");
        tableColumn.setWidth(100);
        tableColumn = new TableColumn(table, SWT.CENTER);
        tableColumn.setText("#rows/table");
        tableColumn.setWidth(100);
        tableColumn = new TableColumn(table, SWT.CENTER);
        tableColumn.setText("#keys");
        tableColumn.setWidth(100);
        tableColumn = new TableColumn(table, SWT.CENTER);
        tableColumn.setText("#indexes");
        tableColumn.setWidth(100);
    }

    private void createCatalogSchemaColumns(Table table) {
        TableColumn tableColumn = new TableColumn(table, SWT.CENTER);
        tableColumn.setText("catalog");
        tableColumn.setWidth(120);
        tableColumn = new TableColumn(table, SWT.CENTER);
        tableColumn.setText("#rows");
        tableColumn.setWidth(100);
        tableColumn = new TableColumn(table, SWT.CENTER);
        tableColumn.setText("#schemata");
        tableColumn.setWidth(100);
        tableColumn = new TableColumn(table, SWT.CENTER);
        tableColumn.setText("#rows/schema");
        tableColumn.setWidth(100);
        tableColumn = new TableColumn(table, SWT.CENTER);
        tableColumn.setText("#tables");
        tableColumn.setWidth(100);
        tableColumn = new TableColumn(table, SWT.CENTER);
        tableColumn.setText("#rows/table");
        tableColumn.setWidth(100);
        tableColumn = new TableColumn(table, SWT.CENTER);
        tableColumn.setText("#keys");
        tableColumn.setWidth(100);
        tableColumn = new TableColumn(table, SWT.CENTER);
        tableColumn.setText("#indexes");
        tableColumn.setWidth(100);
    }

    public void saveAnalysis() throws DataprofilerCoreException {

        // Pattern tablePattern = null;
        // Pattern viewPattern = null;
        // if (this.domain == null) {
        // domain = DomainFactory.eINSTANCE.createDomain();
        // tablePattern = PatternFactory.eINSTANCE.createPattern();
        // tablePattern.setName(TABLE_PATTERN_NAME);
        // domain.getPatterns().add(tablePattern);
        // viewPattern = PatternFactory.eINSTANCE.createPattern();
        // viewPattern.setName(VIEW_PATTERN_NAME);
        // domain.getPatterns().add(viewPattern);
        // connectionAnalysis.getParameters().getDataFilter().add(domain);
        // } else {
        // for (Pattern pattern : domain.getPatterns()) {
        // if (pattern.getName().equals(TABLE_PATTERN_NAME)) {
        // tablePattern = pattern;
        // } else {
        // viewPattern = pattern;
        // }
        //
        // }
        // }
        // if (!this.tableFilterText.getText().equals(PluginConstant.EMPTY_STRING)) {
        //
        // Expression expression = CoreFactory.eINSTANCE.createExpression();
        // expression.setBody(this.tableFilterText.getText());
        // RegularExpressionImpl newRegularExpress = (RegularExpressionImpl)
        // PatternFactory.eINSTANCE.createRegularExpression();
        // newRegularExpress.setExpression(expression);
        // newRegularExpress.setExpressionType(ExpressionType.SQL_LIKE.getLiteral());
        // tablePattern.getComponents().add(newRegularExpress);
        //
        // }
        // if (!this.viewFilterText.getText().equals(PluginConstant.EMPTY_STRING)) {
        // Expression expression = CoreFactory.eINSTANCE.createExpression();
        // expression.setBody(this.viewFilterText.getText());
        // RegularExpressionImpl newRegularExpress = (RegularExpressionImpl)
        // PatternFactory.eINSTANCE.createRegularExpression();
        // newRegularExpress.setExpression(expression);
        // newRegularExpress.setExpressionType(ExpressionType.SQL_LIKE.getLiteral());
        // viewPattern.getComponents().add(newRegularExpress);
        // }
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
                text = PluginConstant.EMPTY_STRING + schemaIndicator.getTotalRowCount();
                return text;
            case 2:
                text = PluginConstant.EMPTY_STRING + schemaIndicator.getTableCount();
                return text;
            case 3:
                return text + getRowCountPerTable(schemaIndicator);
            case 4:
                text = PluginConstant.EMPTY_STRING + schemaIndicator.getKeyCount();
                return text;
            case 5:
                text = PluginConstant.EMPTY_STRING + schemaIndicator.getIndexCount();
                return text;
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
            case 1:
                text = PluginConstant.EMPTY_STRING + schemaIndicator.getTotalRowCount();
                return text;
            case 2:
                text = PluginConstant.EMPTY_STRING + schemaIndicator.getTableCount();
                return text;
            case 3:
                return text + getRowCountPerTable(schemaIndicator);
            case 4:
                text = PluginConstant.EMPTY_STRING + schemaIndicator.getKeyCount();
                return text;
            case 5:
                text = PluginConstant.EMPTY_STRING + schemaIndicator.getIndexCount();
                return text;
            default:
                break;
            }
            return text;
        }
    }

    private static String getRowCountPerTable(SchemaIndicator schemaIndicator) {
        Double frac = Double.valueOf(schemaIndicator.getTotalRowCount()) / schemaIndicator.getTableCount();
        frac = frac.isNaN() ? 0 : frac;
        String formatValue = new DecimalFormat("0.00").format(frac);
        return formatValue;
    }
}
