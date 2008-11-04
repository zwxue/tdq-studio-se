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

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.talend.dq.analysis.ColumnAnalysisHandler;

/**
 * DOC rli class global comment. Detailled comment
 */
public class ColumnsComparisonAnalysisResultPage extends AbstractAnalysisResultPage {

    private ColumnsComparisonMasterDetailsPage masterPage;

    /**
     * DOC rli ColumnsComparisonAnalysisResultPage constructor comment.
     * 
     * @param editor
     * @param id
     * @param title
     */
    public ColumnsComparisonAnalysisResultPage(FormEditor editor, String id, String title) {
        super(editor, id, title);
        AnalysisEditor analysisEditor = (AnalysisEditor) editor;
        this.masterPage = (ColumnsComparisonMasterDetailsPage) analysisEditor.getMasterPage();
    }

    @Override
    protected void createFormContent(IManagedForm managedForm) {
        super.createFormContent(managedForm);

        // resultComp = toolkit.createComposite(topComposite);
        // resultComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING));
        // resultComp.setLayout(new GridLayout());
        // createResultSection(resultComp);

        form.reflow(true);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.editor.AbstractFormPage#setDirty(boolean)
     */
    @Override
    public void setDirty(boolean isDirty) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void createResultSection(Composite parent) {
        // TODO Auto-generated method stub

    }

    @Override
    protected ColumnAnalysisHandler getColumnAnalysisHandler() {
        // TODO Auto-generated method stub
        return null;
    }
}
