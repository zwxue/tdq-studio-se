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

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.talend.dataprofiler.core.exception.DataprofilerCoreException;

/**
 * This page show the comparisons information of column set.
 */
public class ColumnsComparisonMasterDetailsPage extends AbstractAnalysisMetadataPage {

    public ColumnsComparisonMasterDetailsPage(FormEditor editor, String id, String title) {
        super(editor, id, title);
    }

    public void initialize(FormEditor editor) {
        super.initialize(editor);
    }

    @Override
    protected void createFormContent(IManagedForm managedForm) {
        super.createFormContent(managedForm);
        final ScrolledForm form = managedForm.getForm();
        form.setText("ColumnSet Comparision Analysis");
        this.metadataSection.setText("Analysis Metadata");
        this.metadataSection.setDescription("Set the properties of analysis.");
        createAnalyzedColumnSetsSection(form, topComp);
    }

    private void createAnalyzedColumnSetsSection(ScrolledForm form, Composite topComp) {
        Section statisticalSection = this.createSection(form, topComp, "Analyzed Column Sets", false, null);
        Composite sectionClient = toolkit.createComposite(statisticalSection);
        sectionClient.setLayout(new GridLayout(2, false));
        statisticalSection.setClient(sectionClient);

    }

    @Override
    protected void saveAnalysis() throws DataprofilerCoreException {
        // TODO Auto-generated method stub

    }

}
