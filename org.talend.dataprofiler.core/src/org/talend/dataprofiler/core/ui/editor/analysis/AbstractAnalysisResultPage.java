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

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.talend.dataprofiler.core.ui.editor.AbstractFormPage;
import org.talend.dq.analysis.ColumnAnalysisHandler;

/**
 * DOC rli class global comment. Detailled comment
 */
public abstract class AbstractAnalysisResultPage extends AbstractFormPage {

    protected ScrolledForm form;

    protected Composite topComposite;

    public AbstractAnalysisResultPage(FormEditor editor, String id, String title) {
        super(editor, id, title);
    }

    protected void createFormContent(IManagedForm managedForm) {
        this.form = managedForm.getForm();
        this.form.setText("Analysis Result");
        topComposite = form.getBody();
        topComposite.setLayout(new GridLayout());

        Composite summaryComp = toolkit.createComposite(topComposite);
        summaryComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING));
        summaryComp.setLayout(new GridLayout());
        createSummarySection(form, summaryComp, getColumnAnalysisHandler());

    }

    protected abstract ColumnAnalysisHandler getColumnAnalysisHandler();

    protected void createSummarySection(ScrolledForm form, Composite parent, ColumnAnalysisHandler analysisHandler) {
        Section section = createSection(form, parent, "Analysis Summary", true, null);
        Composite sectionClient = toolkit.createComposite(section);
        sectionClient.setLayout(new GridLayout(2, false));
        sectionClient.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        Composite databaseComp = toolkit.createComposite(sectionClient);
        databaseComp.setLayout(new GridLayout(2, false));
        GridData databaseCompData = new GridData(GridData.FILL_HORIZONTAL);
        databaseCompData.verticalAlignment = GridData.BEGINNING;
        databaseComp.setLayoutData(databaseCompData);

        toolkit.createLabel(databaseComp, "Connection:");
        toolkit.createLabel(databaseComp, analysisHandler.getConnectionName());
        if (analysisHandler.isCatalogExisting()) {
            toolkit.createLabel(databaseComp, "Catalog:");
            toolkit.createLabel(databaseComp, analysisHandler.getCatalogNames());
        }

        if (analysisHandler.isSchemaExisting()) {
            toolkit.createLabel(databaseComp, "Schema:");
            toolkit.createLabel(databaseComp, analysisHandler.getSchemaNames());
        }

        toolkit.createLabel(databaseComp, "Table(s):");
        toolkit.createLabel(databaseComp, analysisHandler.getTableNames());

        Composite executionComp = toolkit.createComposite(sectionClient);
        executionComp.setLayout(new GridLayout(2, false));
        GridData executionCompData = new GridData(GridData.FILL_HORIZONTAL);
        executionCompData.verticalAlignment = GridData.BEGINNING;
        executionComp.setLayoutData(executionCompData);
        toolkit.createLabel(executionComp, "Execution Date:");
        toolkit.createLabel(executionComp, analysisHandler.getExecuteData());
        toolkit.createLabel(executionComp, "Execution Duration:");
        toolkit.createLabel(executionComp, analysisHandler.getExecuteDuration());
        toolkit.createLabel(executionComp, "Execution Status:");
        if (analysisHandler.getResultMetadata().isLastRunOk()) {
            toolkit.createLabel(executionComp, "success");
        } else {
            toolkit.createLabel(executionComp, "failure:" + analysisHandler.getResultMetadata().getMessage()).setForeground(
                    Display.getDefault().getSystemColor(SWT.COLOR_RED));
        }

        toolkit.createLabel(executionComp, "Number of Execution:");
        toolkit.createLabel(executionComp, analysisHandler.getExecuteNumber());
        toolkit.createLabel(executionComp, "Last Sucessful Execution:");
        toolkit.createLabel(executionComp, analysisHandler.getLastExecutionNumberOk());

        section.setClient(sectionClient);
    }

    protected abstract void createResultSection(Composite parent);

}
