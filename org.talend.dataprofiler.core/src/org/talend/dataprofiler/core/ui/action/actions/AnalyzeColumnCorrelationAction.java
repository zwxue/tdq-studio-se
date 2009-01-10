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
package org.talend.dataprofiler.core.ui.action.actions;

import java.util.Iterator;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.PlatformUI;
import org.talend.cwm.relational.TdColumn;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.ui.editor.analysis.AnalysisEditor;
import org.talend.dataprofiler.core.ui.editor.analysis.ColumnCorrelationNominalAndIntervalMasterPage;
import org.talend.dataprofiler.core.ui.wizard.analysis.WizardFactory;
import org.talend.dataquality.analysis.AnalysisType;
import org.talend.dq.analysis.parameters.AnalysisLabelParameter;
import org.talend.utils.sql.Java2SqlType;

/**
 * DOC zqin class global comment. Detailled comment <br/>
 * 
 * $Id: talend.epf 1 2006-09-29 17:06:40Z zqin $
 * 
 */
public class AnalyzeColumnCorrelationAction extends Action {

    TreeSelection selection;

    public AnalyzeColumnCorrelationAction() {
        super("Analyze correlation"); //$NON-NLS-1$
        setImageDescriptor(ImageLib.getImageDescriptor(ImageLib.ACTION_NEW_ANALYSIS));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.action.Action#run()
     */
    @SuppressWarnings("unchecked")
    @Override
    public void run() {

        if (openStandardAnalysisDialog(AnalysisType.COLUMN_CORRELATION) == Window.OK) {
            AnalysisEditor editor = (AnalysisEditor) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
                    .getActiveEditor();
            if (editor != null) {
                ColumnCorrelationNominalAndIntervalMasterPage page = (ColumnCorrelationNominalAndIntervalMasterPage) editor
                        .getMasterPage();
                if (!this.selection.isEmpty()) {
                    TdColumn[] columns = new TdColumn[selection.size()];
                    Iterator it = this.selection.iterator();

                    int i = 0;
                    while (it.hasNext()) {
                        columns[i] = (TdColumn) it.next();
                        i++;
                    }
                    page.getTreeViewer().setInput(columns);
                }
            }
        }
    }

    public void setColumnSelection(TreeSelection selection) {
        this.selection = selection;
    }

    private int openStandardAnalysisDialog(AnalysisType type) {
        AnalysisLabelParameter parameter = new AnalysisLabelParameter();

        if (isContainNumber()) {
            parameter.setCategoryLabel(AnalysisLabelParameter.NUMBERIC_CORRELATION);
        } else {
            parameter.setCategoryLabel(AnalysisLabelParameter.DATE_CORRELATION);
        }

        Wizard wizard = WizardFactory.createAnalysisWizard(type, parameter);
        wizard.setForcePreviousAndNextButtons(true);
        WizardDialog dialog = new WizardDialog(null, wizard);
        dialog.setPageSize(500, 340);

        return dialog.open();
    }

    private boolean isContainNumber() {
        TdColumn column = (TdColumn) selection.getFirstElement();
        return Java2SqlType.isNumbericInSQL(column.getJavaType());
    }
}
