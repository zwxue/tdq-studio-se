// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
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
import org.talend.core.model.properties.ConnectionItem;
import org.talend.core.model.properties.DatabaseConnectionItem;
import org.talend.dataprofiler.core.exception.MessageBoxExceptionHandler;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.editor.CommonFormEditor;
import org.talend.dq.CWMPlugin;

/**
 * DOC rli class global comment. Detailled comment
 */
public class ConnectionEditor extends CommonFormEditor {

    private ConnectionInfoPage masterPage;

    private static final String ID = "ConnectionEditor.masterPage";//$NON-NLS-1$

    public ConnectionEditor() {
    }

    protected void addPages() {
        masterPage = new ConnectionInfoPage(this, ID, DefaultMessagesImpl.getString("ConnectionEditor.connectionSettings")); //$NON-NLS-1$ 
        try {
            addPage(masterPage);
        } catch (PartInitException e) {
            MessageBoxExceptionHandler.process(e);
        }
        setPartName(masterPage.getIntactElemenetName()); //$NON-NLS-1$
    }

    public void doSave(IProgressMonitor monitor) {
        if (masterPage.isDirty()) {
            masterPage.doSave(monitor);
            setPartName(masterPage.getIntactElemenetName()); //$NON-NLS-1$
            // MOD klliu 2010-04-21 bug 20204 update SQL Exploer ConnectionNode's name before saving the updated name.
            ConnectionItem item = (ConnectionItem) ((ConnectionItemEditorInput) this.getEditorInput()).getItem();
            if (item instanceof DatabaseConnectionItem) {
                String name = ((DatabaseConnectionItem) item).getConnection().getName();
                CWMPlugin.getDefault().updateAliasInSQLExplorer(masterPage.getOldDataproviderName(), name);
                masterPage.setOldDataproviderName(name);
            }
        }
        setEditorObject(masterPage.getConnectionRepNode());
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
    }
}
