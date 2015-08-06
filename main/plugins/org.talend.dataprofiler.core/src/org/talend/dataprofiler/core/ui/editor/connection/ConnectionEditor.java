// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
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

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PartInitException;
import org.talend.commons.exception.ExceptionHandler;
import org.talend.core.model.properties.ConnectionItem;
import org.talend.core.model.properties.DatabaseConnectionItem;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.editor.CommonFormEditor;
import org.talend.dataprofiler.core.ui.utils.WorkbenchUtils;
import org.talend.dq.CWMPlugin;
import org.talend.dq.nodes.ConnectionRepNode;
import org.talend.dq.nodes.DBConnectionRepNode;

/**
 * DOC rli class global comment. Detailled comment
 */
public class ConnectionEditor extends CommonFormEditor {

    private ConnectionInfoPage masterPage;

    private static final String ID = "ConnectionEditor.masterPage";//$NON-NLS-1$

    public ConnectionEditor() {
    }

    @Override
    protected void addPages() {
        masterPage = new ConnectionInfoPage(this, ID, DefaultMessagesImpl.getString("ConnectionEditor.connectionSettings")); //$NON-NLS-1$ 
        try {
            addPage(masterPage);
            setPartName(masterPage.getIntactElemenetName());
        } catch (PartInitException e) {
            ExceptionHandler.process(e);
        }
    }

    @Override
    public void doSave(IProgressMonitor monitor) {
        if (masterPage.isDirty()) {
            masterPage.doSave(monitor);
            setPartName(masterPage.getIntactElemenetName());
            // MOD qiongli 2012-11-29 avoid item is proxy(get item form IRepositoryViewObject).
            ConnectionRepNode connectionRepNode = masterPage.getConnectionRepNode();
            if (connectionRepNode != null && connectionRepNode instanceof DBConnectionRepNode) {
                IRepositoryViewObject object = connectionRepNode.getObject();
                if (object != null) {
                    ConnectionItem item = (ConnectionItem) object.getProperty().getItem();
                    String name = ((DatabaseConnectionItem) item).getConnection().getName();
                    CWMPlugin.getDefault().updateConnetionAliasByName(item.getConnection(), masterPage.getOldDataproviderName());
                    masterPage.setOldDataproviderName(name);
                }
            }
            // refresh the analysis editor
            if (masterPage.isNameTextUpdate()) {
                WorkbenchUtils.refreshCurrentAnalysisEditor();
                masterPage.setModify(false);
            }
        }
        setEditorObject(masterPage.getConnectionRepNode());
        super.doSave(monitor);
    }

    @Override
    protected void firePropertyChange(final int propertyId) {
        super.firePropertyChange(propertyId);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.part.EditorPart#setInput(org.eclipse.ui.IEditorInput)
     */
    @Override
    protected void setInput(IEditorInput input) {
        super.setInput(input);
    }

    @Override
    public List<String> getPreferredPerspectiveId() {
        return null;
    }

    @Override
    public void refreshEditor() {
        masterPage.initialize(this);
        masterPage.refreshTextInfo();
        setPartName((masterPage.getIntactElemenetName()));
        firePropertyChange(org.eclipse.ui.IWorkbenchPart.PROP_TITLE);
    }
}
