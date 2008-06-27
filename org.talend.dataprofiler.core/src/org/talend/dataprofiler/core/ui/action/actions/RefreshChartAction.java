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

import org.eclipse.jface.action.Action;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.cheatsheets.ICheatSheetAction;
import org.eclipse.ui.cheatsheets.ICheatSheetManager;
import org.talend.dataprofiler.core.ui.editor.analysis.AnalysisEditor;
import org.talend.dataprofiler.core.ui.editor.analysis.ColumnMasterDetailsPage;

/**
 * DOC zqin class global comment. Detailled comment
 */
public class RefreshChartAction extends Action implements ICheatSheetAction {

    private ColumnMasterDetailsPage page;

    public RefreshChartAction() throws Exception {
        super("Refresh Chart");

        AnalysisEditor editor = (AnalysisEditor) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
                .getActiveEditor();
        if (editor != null) {
            page = (ColumnMasterDetailsPage) editor.getMasterPage();
        } else {
            throw new Exception("Editor is null, please create it at first!");
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.cheatsheets.ICheatSheetAction#run(java.lang.String[],
     * org.eclipse.ui.cheatsheets.ICheatSheetManager)
     */
    @Override
    public void run(String[] params, ICheatSheetManager manager) {
        run();
    }

    @Override
    public void run() {
        if (page.isDirty()) {
            try {
                page.saveAnalysis();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        page.refreshChart(page.getManagedForm().getForm());
    }

}
