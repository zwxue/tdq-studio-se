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
package org.talend.dataprofiler.core.ui.editor.connection;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.IFormPage;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.exception.MessageBoxExceptionHandler;

/**
 * DOC rli class global comment. Detailled comment
 */
public class ConnectionEditor extends FormEditor {

    private IFormPage masterPage;

    private boolean isDirty = false;

    /**
     * 
     */
    public ConnectionEditor() {
    }

    protected void addPages() {
        masterPage = new ConnectionInfoPage(this, "MasterPage", "Connection Settings");
        try {
            addPage(masterPage);
        } catch (PartInitException e) {
            MessageBoxExceptionHandler.process(e);
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
        setPartName("Connection Editor");
    }

}
