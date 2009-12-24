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
import org.talend.dataprofiler.core.ui.editor.analysis.ColumnSetMasterPage;
import org.talend.dataprofiler.core.ui.wizard.analysis.WizardFactory;
import org.talend.dataquality.analysis.AnalysisType;

/**
 * DOC yyi class global comment. Detailled comment
 */
public class AnalyzeColumnSetAction extends Action {

    TreeSelection selection;

    TdColumn[] columns;

    boolean needselection = true;

    public AnalyzeColumnSetAction() {
        super("Analyze Column Set"); //$NON-NLS-1$
        setImageDescriptor(ImageLib.getImageDescriptor(ImageLib.ACTION_NEW_ANALYSIS));
    }

    public AnalyzeColumnSetAction(TdColumn[] columns) {
        super("Analyze Column Set"); //$NON-NLS-1$
        setImageDescriptor(ImageLib.getImageDescriptor(ImageLib.ACTION_NEW_ANALYSIS));
        needselection = false;
        this.columns = columns;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.action.Action#run()
     */
    @SuppressWarnings("unchecked")
    @Override
    public void run() {

        if (opencolumnSetAnalysisDialog() == Window.OK) {
            AnalysisEditor editor = (AnalysisEditor) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
                    .getActiveEditor();
            if (editor != null) {
                ColumnSetMasterPage page = (ColumnSetMasterPage) editor.getMasterPage();
                if (this.needselection && !this.selection.isEmpty()) {
                    TdColumn[] columns = new TdColumn[selection.size()];
                    Iterator it = this.selection.iterator();

                    int i = 0;
                    while (it.hasNext()) {
                        columns[i] = (TdColumn) it.next();
                        i++;
                    }
                    page.getTreeViewer().setInput(columns);
                } else if (!this.needselection && null != this.columns) {
                    page.getTreeViewer().setInput(this.columns);
                    page.doSave(null);
                }
            }
        }
    }

    public void setColumnSelection(TreeSelection selection) {
        this.selection = selection;
    }

    private int opencolumnSetAnalysisDialog() {
        Wizard wizard = WizardFactory.createAnalysisWizard(AnalysisType.COLUMN_SET, null);
        wizard.setForcePreviousAndNextButtons(true);
        WizardDialog dialog = new WizardDialog(null, wizard);
        dialog.setPageSize(500, 340);

        return dialog.open();
    }

}
