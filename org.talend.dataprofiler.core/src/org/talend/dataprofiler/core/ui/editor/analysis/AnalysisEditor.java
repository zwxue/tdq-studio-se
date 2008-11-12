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
import org.eclipse.swt.widgets.Button;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.part.FileEditorInput;
import org.talend.dataprofiler.core.exception.ExceptionHandler;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.editor.CommonFormEditor;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.analysis.AnalysisType;
import org.talend.dq.helper.resourcehelper.AnaResourceFileHelper;

/**
 * @author rli
 * 
 */
public class AnalysisEditor extends CommonFormEditor {

    private static final String ANALYSIS_RESULTS = DefaultMessagesImpl.getString("AnalysisEditor.analysisResult"); //$NON-NLS-1$

    private static final String SECOND_PAGE = DefaultMessagesImpl.getString("AnalysisEditor.secondPage"); //$NON-NLS-1$

    private static final String MASTER_PAGE = DefaultMessagesImpl.getString("AnalysisEditor.masterPage"); //$NON-NLS-1$

    private static final String ANALYSIS_SETTINGS = DefaultMessagesImpl.getString("AnalysisEditor.analysisSettings"); //$NON-NLS-1$

    private static final int RESULT_PAGE_INDEX = 1;

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
            masterPage = new ColumnMasterDetailsPage(this, MASTER_PAGE, ANALYSIS_SETTINGS);
            setPartName(DefaultMessagesImpl.getString("AnalysisEditor.columnAnalysisEditor")); //$NON-NLS-1$
            columnResultPage = new ColumnAnalysisResultPage(this, SECOND_PAGE, ANALYSIS_RESULTS);
            try {
                addPage(masterPage);
                addPage(columnResultPage);
            } catch (PartInitException e) {
                ExceptionHandler.process(e, Level.ERROR);
            }
            break;
        case CONNECTION:
            masterPage = new ConnectionMasterDetailsPage(this, MASTER_PAGE, ANALYSIS_SETTINGS);
            setPartName(DefaultMessagesImpl.getString("AnalysisEditor.connectionAnalysisEditor")); //$NON-NLS-1$
            try {
                addPage(masterPage);
            } catch (PartInitException e) {
                ExceptionHandler.process(e, Level.ERROR);
            }
            break;
        case COLUMNS_COMPARISON:
            masterPage = new ColumnsComparisonMasterDetailsPage(this, MASTER_PAGE, DefaultMessagesImpl
                    .getString("AnalysisEditor.analysisSetting")); //$NON-NLS-1$
            setPartName(DefaultMessagesImpl.getString("AnalysisEditor.column")); //$NON-NLS-1$
            ColumnsComparisonAnalysisResultPage columnsComparisonAnalysisReslultPage = new ColumnsComparisonAnalysisResultPage(
                    this, SECOND_PAGE, ANALYSIS_RESULTS);
            try {
                addPage(masterPage);
                addPage(columnsComparisonAnalysisReslultPage);
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
        if (propertyId == IEditorPart.PROP_DIRTY) {
            if (isDirty() && this.getMasterPage() != null) {
                Button runButton = ((AbstractAnalysisMetadataPage) this.getMasterPage()).getRunButton();
                if (runButton != null) {
                    runButton.setEnabled(false);
                }
            }
        }
        isRefreshResultPage = true;
    }

    protected void translateInput(IEditorInput input) {
        FileEditorInput fileEditorInput = (FileEditorInput) input;
        String name = fileEditorInput.getFile().getName();
        if (name.endsWith(org.talend.dq.PluginConstant.ANA_SUFFIX)) {
            Analysis findAnalysis = AnaResourceFileHelper.getInstance().findAnalysis(fileEditorInput.getFile());
            analysisType = findAnalysis.getParameters().getAnalysisType();
        }
    }

    @Override
    protected void pageChange(int newPageIndex) {
        super.pageChange(newPageIndex);
        if (getMasterPage().isDirty() && (newPageIndex == RESULT_PAGE_INDEX)) {
            getMasterPage().doSave(null);
        }

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
