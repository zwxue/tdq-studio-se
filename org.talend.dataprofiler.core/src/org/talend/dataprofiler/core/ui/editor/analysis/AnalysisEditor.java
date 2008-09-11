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
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.part.FileEditorInput;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.exception.ExceptionHandler;
import org.talend.dataprofiler.core.helper.resourcehelper.AnaResourceFileHelper;
import org.talend.dataprofiler.core.ui.editor.CommonFormEditor;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.analysis.AnalysisType;

/**
 * @author rli
 * 
 */
public class AnalysisEditor extends CommonFormEditor {

    private IFormPage masterPage;

    private IFormPage columnResultPage;

    private AnalysisType analysisType;

    private boolean isRefreshResultPage = false;

    /**
     * 
     */
    public AnalysisEditor() {
    }

    protected void addPages() {
        switch (analysisType) {
        case MULTIPLE_COLUMN:
            masterPage = new ColumnMasterDetailsPage(this, "MasterPage", "Analysis Settings");
            setPartName("Column Analysis Editor");
            columnResultPage = new ColumnAnalysisResultPage(this, "secondpage", "Analysis Results");
            try {
                addPage(masterPage);
                addPage(columnResultPage);
            } catch (PartInitException e) {
                ExceptionHandler.process(e, Level.ERROR);
            }
            break;
        case CONNECTION:
            masterPage = new ConnectionMasterDetailsPage(this, "MasterPage", "Analysis Settings");
            setPartName("Connection Analysis Editor");
            try {
                addPage(masterPage);
            } catch (PartInitException e) {
                ExceptionHandler.process(e, Level.ERROR);
            }
            break;
        default:

        }

    }

    public void doSave(IProgressMonitor monitor) {
        if (masterPage != null && masterPage.isDirty()) {
            masterPage.doSave(monitor);

        }

        super.doSave(monitor);
    }

    protected void firePropertyChange(final int propertyId) {
        super.firePropertyChange(propertyId);
        isRefreshResultPage = true;
    }

    protected void translateInput(IEditorInput input) {
        FileEditorInput fileEditorInput = (FileEditorInput) input;
        String name = fileEditorInput.getFile().getName();
        if (name.endsWith(PluginConstant.ANA_SUFFIX)) {
            Analysis findAnalysis = AnaResourceFileHelper.getInstance().findAnalysis(fileEditorInput.getFile());
            analysisType = findAnalysis.getParameters().getAnalysisType();
        }
    }

    @Override
    protected void pageChange(int newPageIndex) {
        super.pageChange(newPageIndex);

        if (isRefreshResultPage && columnResultPage != null && newPageIndex == columnResultPage.getIndex()) {
            ((ColumnAnalysisResultPage) columnResultPage).refresh((ColumnMasterDetailsPage) getMasterPage());
            isRefreshResultPage = false;
        }
    }

    /**
     * Getter for masterPage.
     * 
     * @return the masterPage
     */
    public IFormPage getMasterPage() {
        return this.masterPage;
    }

    public void performGlobalAction(String id) {
        if (analysisType == AnalysisType.MULTIPLE_COLUMN) {
            ((ColumnMasterDetailsPage) masterPage).performGlobalAction(id);
        }
    }

    public void setRefreshResultPage(boolean isRefreshResultPage) {
        this.isRefreshResultPage = isRefreshResultPage;
    }

    public AnalysisType getAnalysisType() {
        return analysisType;
    }

}
