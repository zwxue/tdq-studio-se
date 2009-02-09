// ============================================================================
//
// Copyright (C) 2006-2009 Talend Inc. - www.talend.com
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
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;
import org.talend.dataprofiler.core.exception.MessageBoxExceptionHandler;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.editor.CommonFormEditor;

/**
 * DOC rli class global comment. Detailled comment
 */
public class ConnectionEditor extends CommonFormEditor {

    private IFormPage masterPage;

    /**
     * 
     */
    public ConnectionEditor() {
    }

    protected void addPages() {
        masterPage = new ConnectionInfoPage(this, DefaultMessagesImpl.getString("ConnectionEditor.masterPage"), DefaultMessagesImpl.getString("ConnectionEditor.connectionSettings")); //$NON-NLS-1$ //$NON-NLS-2$
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
        super.doSave(monitor);
    }

    protected void firePropertyChange(final int propertyId) {
        super.firePropertyChange(propertyId);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.part.EditorPart#setInput(org.eclipse.ui.IEditorInput)
     */
    protected void setInput(IEditorInput input) {
        super.setInput(input);
        setPartName(DefaultMessagesImpl.getString("ConnectionEditor.connectionEditor")); //$NON-NLS-1$
    }

}
