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
package org.talend.dataprofiler.core.ui.editor.pattern;

import org.apache.log4j.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.IFormPage;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.exception.ExceptionHandler;

/**
 * DOC rli class global comment. Detailled comment
 */
public class PatternEditor extends FormEditor {

    private IFormPage masterPage;

    private boolean isDirty = false;

    protected void addPages() {
        masterPage = new PatternMasterDetailsPage(this, "MasterPage", "Pattern Settings");
        setPartName("Pattern Editor");
        try {
            addPage(masterPage);
        } catch (PartInitException e) {
            ExceptionHandler.process(e, Level.ERROR);
        }
    }

    public void doSave(IProgressMonitor monitor) {
        if (masterPage.isDirty()) {
            masterPage.doSave(monitor);
        }
        this.isDirty = false;
        firePropertyChange(IEditorPart.PROP_DIRTY);
        CorePlugin.getDefault().refreshWorkSpace();
    }

    protected void firePropertyChange(final int propertyId) {
        super.firePropertyChange(propertyId);
    }

    public void doSaveAs() {
        doSave(null);
    }

    public boolean isSaveAsAllowed() {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.forms.editor.FormEditor#isDirty()
     */
    @Override
    public boolean isDirty() {
        return isDirty || super.isDirty();
    }

    public void setDirty(boolean isDirty) {
        this.isDirty = isDirty;
        firePropertyChange(IEditorPart.PROP_DIRTY);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.part.EditorPart#setInput(org.eclipse.ui.IEditorInput)
     */
    protected void setInput(IEditorInput input) {
        super.setInput(input);
        // FileEditorInput fileEditorInput = (FileEditorInput) input;
        // String name = fileEditorInput.getFile().getName();
        // if (name.endsWith(PluginConstant.ANA_SUFFIX)) {
        // Analysis findAnalysis = AnaResourceFileHelper.getInstance().findAnalysis(fileEditorInput.getFile());
        // analysisType = findAnalysis.getParameters().getAnalysisType();
        // }
    }

    /**
     * Getter for masterPage.
     * 
     * @return the masterPage
     */
    public IFormPage getMasterPage() {
        return this.masterPage;
    }
}
