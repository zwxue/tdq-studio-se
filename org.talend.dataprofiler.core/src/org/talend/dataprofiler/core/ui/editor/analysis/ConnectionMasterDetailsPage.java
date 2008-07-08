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
import java.util.Properties;

import org.apache.log4j.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
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
import org.talend.cwm.constants.DevelopmentStatus;
import org.talend.cwm.helper.DataProviderHelper;
import org.talend.cwm.helper.TaggedValueHelper;
import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.cwm.softwaredeployment.TdProviderConnection;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.exception.DataprofilerCoreException;
import org.talend.dataprofiler.core.exception.ExceptionHandler;
import org.talend.dataprofiler.core.helper.AnaResourceFileHelper;
import org.talend.dataprofiler.core.model.dburl.SupportDBUrlStore;
import org.talend.dataprofiler.core.ui.editor.AbstractFormPage;
import org.talend.dataquality.analysis.Analysis;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * @author rli
 * 
 */
public class ConnectionMasterDetailsPage extends AbstractFormPage implements PropertyChangeListener {

    // private static Logger log = Logger.getLogger(ConnectionMasterDetailsPage.class);

    Analysis connectionAnalysis;

    public ConnectionMasterDetailsPage(FormEditor editor, String id, String title) {
        super(editor, id, title);
    }

    public void initialize(FormEditor editor) {
        super.initialize(editor);
        FileEditorInput input = (FileEditorInput) editor.getEditorInput();
        connectionAnalysis = AnaResourceFileHelper.getInstance().findAnalysis(input.getFile());
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
        sectionClient.setLayout(new GridLayout(2, false));
        Label tableFilterLabel = new Label(sectionClient, SWT.None);
        tableFilterLabel.setText("Filter on tables:");
        tableFilterLabel.setLayoutData(new GridData());
        Text tableFilterText = new Text(sectionClient, SWT.BORDER);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(tableFilterText);
        Label viewFilterLabel = new Label(sectionClient, SWT.None);
        viewFilterLabel.setText("Filter on views: ");
        viewFilterLabel.setLayoutData(new GridData());
        Text viewFilterText = new Text(sectionClient, SWT.BORDER);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(viewFilterText);
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
        TdDataProvider tdDataProvider = null;
        if (analysedElements.size() > 0) {
            tdDataProvider = (TdDataProvider) analysedElements.get(0);
        }
        TdProviderConnection providerConnection = DataProviderHelper.getTdProviderConnection(tdDataProvider).getObject();
        String connectionStr = providerConnection.getConnectionString();
        Properties pameterProperties = SupportDBUrlStore.getInstance().getDBPameterProperties(connectionStr);
        String labelContent = pameterProperties.getProperty(PluginConstant.DBTYPE_PROPERTY);
        Label leftLabel = new Label(leftComp, SWT.NONE);
        leftLabel.setText("DBMS:" + (labelContent == null ? PluginConstant.EMPTY_STRING : labelContent));
        leftLabel.setLayoutData(new GridData());
        leftLabel = new Label(leftComp, SWT.NONE);
        labelContent = pameterProperties.getProperty(PluginConstant.HOSTNAME_PROPERTY);
        leftLabel.setText("Server:" + (labelContent == null ? PluginConstant.EMPTY_STRING : labelContent));
        leftLabel.setLayoutData(new GridData());
        leftLabel = new Label(leftComp, SWT.NONE);
        labelContent = pameterProperties.getProperty(PluginConstant.PORT_PROPERTY);
        leftLabel.setText("Port:" + (labelContent == null ? PluginConstant.EMPTY_STRING : labelContent));
        leftLabel.setLayoutData(new GridData());
        leftLabel = new Label(leftComp, SWT.NONE);
        labelContent = TaggedValueHelper.getValue(PluginConstant.USER_PROPERTY, providerConnection);
        leftLabel.setText("Connected as:" + (labelContent == null ? PluginConstant.EMPTY_STRING : labelContent));
        leftLabel.setLayoutData(new GridData());
        leftLabel = new Label(leftComp, SWT.NONE);
        leftLabel.setText("Catalogs:" + (labelContent == null ? PluginConstant.EMPTY_STRING : labelContent));
        leftLabel.setLayoutData(new GridData());
        leftLabel = new Label(leftComp, SWT.NONE);
        leftLabel.setText("Schemata:" + (labelContent == null ? PluginConstant.EMPTY_STRING : labelContent));
        leftLabel.setLayoutData(new GridData());

        Label rightLabel = new Label(rightComp, SWT.NONE);
        rightLabel.setText("Creation date:");
        rightLabel.setLayoutData(new GridData());
        rightLabel = new Label(rightComp, SWT.NONE);
        rightLabel.setText("Execution date:");
        rightLabel.setLayoutData(new GridData());
        rightLabel = new Label(rightComp, SWT.NONE);
        rightLabel.setText("Execution status:");
        rightLabel.setLayoutData(new GridData());
        rightLabel = new Label(rightComp, SWT.NONE);
        rightLabel.setText("Number of executions:");
        rightLabel.setLayoutData(new GridData());
        rightLabel = new Label(rightComp, SWT.NONE);
        rightLabel.setText("Last successful execution:");
        rightLabel.setLayoutData(new GridData());
        sectionClient.layout();
        summarySection.setClient(sectionClient);

    }

    private void createStatisticalSection(ScrolledForm form, Composite topComp) {
        Section statisticalSection = this.createSection(form, topComp, "Statistical informations", false, null);
        Composite sectionClient = toolkit.createComposite(statisticalSection);
        sectionClient.setLayout(new GridLayout());
        Table table = new Table(sectionClient, SWT.BORDER);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(table);
        table.setHeaderVisible(true);
        createCatalogTableColumns(table);
        sectionClient.layout();
        statisticalSection.setClient(sectionClient);

    }

    private void createCatalogTableColumns(Table table) {
        TableColumn tableColumn = new TableColumn(table, SWT.CENTER);
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

    protected void fireTextChange() {
        connectionAnalysis.setName(nameText.getText());
        TaggedValueHelper.setPurpose(purposeText.getText(), connectionAnalysis);
        TaggedValueHelper.setDescription(descriptionText.getText(), connectionAnalysis);
        TaggedValueHelper.setAuthor(connectionAnalysis, authorText.getText());
        TaggedValueHelper.setDevStatus(connectionAnalysis, DevelopmentStatus.get(statusCombo.getText()));
    }

    public void saveAnalysis() throws DataprofilerCoreException {
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (PluginConstant.ISDIRTY_PROPERTY.equals(evt.getPropertyName())) {
            ((AnalysisEditor) this.getEditor()).firePropertyChange(IEditorPart.PROP_DIRTY);
        }

    }

    @Override
    protected void initMetaTextFied() {
        nameText.setText(connectionAnalysis.getName() == null ? PluginConstant.EMPTY_STRING : connectionAnalysis.getName());
        purposeText.setText(TaggedValueHelper.getPurpose(connectionAnalysis) == null ? PluginConstant.EMPTY_STRING
                : TaggedValueHelper.getPurpose(connectionAnalysis));
        descriptionText.setText(TaggedValueHelper.getDescription(connectionAnalysis) == null ? PluginConstant.EMPTY_STRING
                : TaggedValueHelper.getDescription(connectionAnalysis));
        authorText.setText(TaggedValueHelper.getAuthor(connectionAnalysis) == null ? PluginConstant.EMPTY_STRING
                : TaggedValueHelper.getAuthor(connectionAnalysis));
        statusCombo.setText(TaggedValueHelper.getDevStatus(connectionAnalysis) == null ? PluginConstant.EMPTY_STRING
                : TaggedValueHelper.getDevStatus(connectionAnalysis).getLiteral());
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

}
