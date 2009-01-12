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

import org.apache.log4j.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.part.FileEditorInput;
import org.talend.dataprofiler.core.exception.DataprofilerCoreException;
import org.talend.dataprofiler.core.exception.ExceptionHandler;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.IRuningStatusListener;
import org.talend.dataprofiler.core.ui.action.actions.RunAnalysisAction;
import org.talend.dataprofiler.core.ui.editor.AbstractMetadataFormPage;
import org.talend.dataprofiler.core.ui.editor.CommonFormEditor;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dq.helper.resourcehelper.AnaResourceFileHelper;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC rli class global comment. Detailled comment
 */
public abstract class AbstractAnalysisMetadataPage extends AbstractMetadataFormPage implements IRuningStatusListener {

    protected Analysis analysis;

    private Button runBtn;

    protected CommonFormEditor currentEditor = null;

    public AbstractAnalysisMetadataPage(FormEditor editor, String id, String title) {
        super(editor, id, title);
        currentEditor = (CommonFormEditor) editor;
    }

    @Override
    protected ModelElement getCurrentModelElement(FormEditor editor) {
        FileEditorInput input = (FileEditorInput) editor.getEditorInput();
        analysis = AnaResourceFileHelper.getInstance().findAnalysis(input.getFile());
        return analysis;
    }

    protected Button createRunButton(ScrolledForm form) {
        GridData gdBtn = new GridData();
        gdBtn.horizontalAlignment = SWT.CENTER;
        gdBtn.horizontalSpan = 1;
        gdBtn.widthHint = 120;
        runBtn = toolkit.createButton(form.getBody(), DefaultMessagesImpl.getString("ColumnMasterDetailsPage.run"), SWT.NONE); //$NON-NLS-1$
        runBtn.setLayoutData(gdBtn);

        runBtn.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {

                new RunAnalysisAction(AbstractAnalysisMetadataPage.this).run();
            }
        });
        runBtn.setEnabled(this.canRun());
        return runBtn;
    }

    Button getRunButton() {
        return this.runBtn;
    }

    @Override
    public void doSave(IProgressMonitor monitor) {
        super.doSave(monitor);
        if (!canSave()) {
            return;
        }
        try {
            saveAnalysis();
            this.isDirty = false;
            this.runBtn.setEnabled(this.canRun());
        } catch (DataprofilerCoreException e) {
            ExceptionHandler.process(e, Level.ERROR);
            e.printStackTrace();
        }
    }

    protected boolean canSave() {
        return true;
    }

    protected abstract boolean canRun();

    protected abstract void saveAnalysis() throws DataprofilerCoreException;

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
}
