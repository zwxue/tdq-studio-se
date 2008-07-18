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
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.part.FileEditorInput;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.exception.ExceptionHandler;
import org.talend.dataprofiler.core.helper.AnaResourceFileHelper;
import org.talend.dataprofiler.core.ui.editor.CommonFormEditor;
import org.talend.dataprofiler.core.ui.views.DQRespositoryView;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.analysis.AnalysisType;

/**
 * @author rli
 * 
 */
public class AnalysisEditor extends CommonFormEditor {

    private IFormPage columnMasterPage;

    private IFormPage columnResultPage;

    private IFormPage connectionMasterPage;

    private AnalysisType analysisType = AnalysisType.COLUMN;

    /**
     * 
     */
    public AnalysisEditor() {
    }

    protected void addPages() {
        switch (analysisType) {
        case MULTIPLE_COLUMN:
            columnMasterPage = new ColumnMasterDetailsPage(this, "MasterPage", "Analysis Settings");
            setPartName("Column Analysis Editor");
            columnResultPage = new ColumnAnalysisResultPage(this, "secondpage", "Analysis Results");
            try {
                addPage(columnMasterPage);
                addPage(columnResultPage);
            } catch (PartInitException e) {
                ExceptionHandler.process(e, Level.ERROR);
            }
            break;
        case CONNECTION:
            connectionMasterPage = new ConnectionMasterDetailsPage(this, "MasterPage", "Analysis Settings");
            setPartName("Connection Analysis Editor");
            try {
                addPage(connectionMasterPage);
            } catch (PartInitException e) {
                ExceptionHandler.process(e, Level.ERROR);
            }
            break;
        default:

        }

    }

    public void doSave(IProgressMonitor monitor) {
        if (columnMasterPage != null && columnMasterPage.isDirty()) {
            columnMasterPage.doSave(monitor);

        }
        if (connectionMasterPage != null && connectionMasterPage.isDirty()) {
            connectionMasterPage.doSave(monitor);
        }

        IFile efile = ((FileEditorInput) getEditorInput()).getFile();
        refreshDQView(efile);

        super.doSave(monitor);
    }

    /**
     * DOC qzhang Comment method "refreshDQView".
     */
    protected void refreshDQView(Object node) {
        IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        IViewPart findView = activePage.findView("org.talend.dataprofiler.core.ui.views.DQRespositoryView");
        DQRespositoryView view = (DQRespositoryView) findView;
        view.getCommonViewer().refresh(node);
    }

    protected void firePropertyChange(final int propertyId) {
        super.firePropertyChange(propertyId);
    }

    protected void translateInput(IEditorInput input) {
        FileEditorInput fileEditorInput = (FileEditorInput) input;
        String name = fileEditorInput.getFile().getName();
        if (name.endsWith(PluginConstant.ANA_SUFFIX)) {
            Analysis findAnalysis = AnaResourceFileHelper.getInstance().findAnalysis(fileEditorInput.getFile());
            analysisType = findAnalysis.getParameters().getAnalysisType();
        }
    }

    /**
     * Getter for masterPage.
     * 
     * @return the masterPage
     */
    public IFormPage getMasterPage() {
        return this.columnMasterPage;
    }

    public void performGlobalAction(String id) {
        if (analysisType == AnalysisType.MULTIPLE_COLUMN) {
            ((ColumnMasterDetailsPage) columnMasterPage).performGlobalAction(id);
        }
    }

}
