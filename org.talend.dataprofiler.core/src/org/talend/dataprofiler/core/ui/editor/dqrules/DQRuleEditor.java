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
package org.talend.dataprofiler.core.ui.editor.dqrules;

import org.apache.log4j.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;
import org.talend.dataprofiler.core.exception.ExceptionHandler;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.action.actions.DefaultSaveAction;
import org.talend.dataprofiler.core.ui.editor.CommonFormEditor;
import org.talend.dataprofiler.core.ui.editor.TdEditorToolBar;

/**
 * DOC xqliu class global comment. Detailled comment
 */
public class DQRuleEditor extends CommonFormEditor {

    private IFormPage masterPage;

    private static final String ID = "DQRuleEditor.masterPage";//$NON-NLS-1$

    // MOD xqliu 2009-07-02 bug 7687
    private DefaultSaveAction saveAction;

    // ~

    protected void addPages() {
        masterPage = new DQRuleMasterDetailsPage(this, ID, DefaultMessagesImpl.getString("DQRuleEditor.dqRuleSettings")); //$NON-NLS-1$ 

        setPartName(((DQRuleMasterDetailsPage) masterPage).getIntactElemenetName()); //$NON-NLS-1$

        try {
            addPage(masterPage);
        } catch (PartInitException e) {
            ExceptionHandler.process(e, Level.ERROR);
        }

        // ADD xqliu 2009-07-02 bug 7687
        TdEditorToolBar toolbar = getToolBar();
        if (toolbar != null && masterPage != null) {
            saveAction = new DefaultSaveAction(this);
            toolbar.addActions(saveAction);
        }
        // ~
    }

    public void doSave(IProgressMonitor monitor) {
        if (masterPage.isDirty()) {
            masterPage.doSave(monitor);
            setPartName(((DQRuleMasterDetailsPage) masterPage).getIntactElemenetName()); //$NON-NLS-1$
        }
        super.doSave(monitor);

    }

    protected void firePropertyChange(final int propertyId) {
        // ADD xqliu 2009-07-02 bug 7687
        setSaveActionButtonState(isDirty());
        // ~
        super.firePropertyChange(propertyId);
    }

    /**
     * Getter for masterPage.
     * 
     * @return the masterPage
     */
    public IFormPage getMasterPage() {
        return this.masterPage;
    }

    @Override
    protected void pageChange(int newPageIndex) {
        super.pageChange(newPageIndex);
        // ADD xqliu 2009-07-02 bug 7686
        if (masterPage != null) {
            setSaveActionButtonState(false);
        }
    }

    /**
     * DOC xqliu 2009-07-02 bug 7687.
     * 
     * @param state
     */
    public void setSaveActionButtonState(boolean state) {
        if (saveAction != null) {
            saveAction.setEnabled(state);
        }
    }
}
