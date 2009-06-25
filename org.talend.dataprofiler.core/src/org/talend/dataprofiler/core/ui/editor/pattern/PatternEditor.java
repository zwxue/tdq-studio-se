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
package org.talend.dataprofiler.core.ui.editor.pattern;

import org.apache.log4j.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.editor.CommonFormEditor;
import org.talend.dataquality.exception.ExceptionHandler;

/**
 * DOC rli class global comment. Detailled comment
 */
public class PatternEditor extends CommonFormEditor {

    private IFormPage masterPage;

    protected void addPages() {
        masterPage = new PatternMasterDetailsPage(this, DefaultMessagesImpl.getString("PatternEditor.masterPage"), DefaultMessagesImpl.getString("PatternEditor.patternSettings")); //$NON-NLS-1$ //$NON-NLS-2$
        setPartName(DefaultMessagesImpl.getString("PatternEditor.patternEditor")); //$NON-NLS-1$
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
        super.doSave(monitor);

    }

    public void firePropertyChange(final int propertyId) {
        // MOD xqliu 2009-06-25 bug 7687
        IFormPage temp = this.getMasterPage();
        if (temp instanceof PatternMasterDetailsPage) {
            ((PatternMasterDetailsPage) temp).updateSaveButtonState();
        }
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
}
