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
import org.eclipse.ui.PlatformUI;
import org.talend.cwm.relational.TdColumn;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.ui.editor.AnalysisEditor;
import org.talend.dataprofiler.core.ui.editor.ColumnMasterDetailsPage;

/**
 * DOC zqin class global comment. Detailled comment <br/>
 * 
 * $Id: talend.epf 1 2006-09-29 17:06:40Z zqin $
 * 
 */
public class AnalyzeColumnAction extends Action {

    TreeSelection selection;

    public AnalyzeColumnAction() {
        super("Analyze");
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

        new CreateNewAnalysisAction().run(null, null);

        AnalysisEditor editor = (AnalysisEditor) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
                .getActiveEditor();
        if (editor != null) {
            ColumnMasterDetailsPage page = (ColumnMasterDetailsPage) editor.getMasterPage();
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

    public void setColumnSelection(TreeSelection selection) {
        this.selection = selection;
    }
}
