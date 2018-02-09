// ============================================================================
//
// Copyright (C) 2006-2018 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.editor.parserrules;

import org.apache.log4j.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.PartInitException;
import org.talend.commons.exception.ExceptionHandler;
import org.talend.dataprofiler.core.ui.action.actions.DefaultSaveAction;
import org.talend.dataprofiler.core.ui.editor.CommonFormEditor;
import org.talend.dataprofiler.core.ui.editor.TdEditorToolBar;
import org.talend.dataprofiler.core.ui.editor.dqrules.ParserRuleMasterDetailsPage;

/**
 * DOC klliu class global comment. Detailled comment
 */
public class ParserRuleEditor extends CommonFormEditor {

    private static final String ID = "ParserRuleEditor.masterPage";//$NON-NLS-1$

    private DefaultSaveAction saveAction;

    @Override
    protected void addPages() {
        masterPage = new ParserRuleMasterDetailsPage(this, ID, "Parser Rule Settings"); //$NON-NLS-1$ 
        try {
            addPage(masterPage);
            setPartName(masterPage.getIntactElemenetName());
        } catch (PartInitException e) {
            ExceptionHandler.process(e, Level.ERROR);
        }
        TdEditorToolBar toolbar = getToolBar();
        if (toolbar != null && masterPage != null) {
            saveAction = new DefaultSaveAction(this);
            toolbar.addActions(saveAction);
        }
    }

    @Override
    public void doSave(IProgressMonitor monitor) {
        if (masterPage.isDirty()) {
            masterPage.doSave(monitor);
            setPartName(masterPage.getIntactElemenetName());
        }
        setEditorObject(((ParserRuleMasterDetailsPage) getMasterPage()).getRuleRepNode());
        super.doSave(monitor);

    }

    @Override
    protected void firePropertyChange(final int propertyId) {
        setSaveActionButtonState(isDirty());
        super.firePropertyChange(propertyId);
    }

    @Override
    protected void pageChange(int newPageIndex) {
        super.pageChange(newPageIndex);
        if (masterPage != null) {
            setSaveActionButtonState(false);
        }
    }

    public void setSaveActionButtonState(boolean state) {
        if (saveAction != null) {
            saveAction.setEnabled(state);
        }
    }

    @Override
    public void setFocus() {
        super.setFocus();
    }
}
