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
package org.talend.dataprofiler.core.ui.editor.report;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.part.FileEditorInput;
import org.talend.dataprofiler.core.helper.RepResourceFileHelper;
import org.talend.dataprofiler.core.ui.editor.AbstractMetadataFormPage;
import org.talend.dataquality.reports.TdReport;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC rli class global comment. Detailled comment
 */
public class ReportMasterDetailsPage extends AbstractMetadataFormPage implements PropertyChangeListener {

    private TdReport currentReport;

    public ReportMasterDetailsPage(FormEditor editor, String id, String title) {
        super(editor, id, title);
    }

    @Override
    protected ModelElement getCurrentModelElement(FormEditor editor) {
        FileEditorInput input = (FileEditorInput) editor.getEditorInput();
        this.currentReport = RepResourceFileHelper.getInstance().findReport(input.getFile());
        return currentReport;
    }

    @Override
    public void setDirty(boolean isDirty) {
        if (this.isDirty != isDirty) {
            this.isDirty = isDirty;
            ((ReportEditror) this.getEditor()).firePropertyChange(IEditorPart.PROP_DIRTY);
        }
    }

    public void propertyChange(PropertyChangeEvent evt) {
        // TODO Auto-generated method stub

    }

}
