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
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.cheatsheets.ICheatSheetAction;
import org.eclipse.ui.cheatsheets.ICheatSheetManager;
import org.talend.dataprofiler.core.ui.wizard.analysis.CreateNewAnalysisWizard;
import org.talend.dataquality.analysis.AnalysisType;

/**
 * DOC zqin class global comment. Detailled comment
 */
public class CreateConnectionAnalysisAction extends Action implements ICheatSheetAction {

    public CreateConnectionAnalysisAction() {
    }

    public void run(String[] params, ICheatSheetManager manager) {

        openStandardAnalysisDialog(true, AnalysisType.CONNECTION);
    }

    private void openStandardAnalysisDialog(boolean creation, AnalysisType type) {
        CreateNewAnalysisWizard wizard = new CreateNewAnalysisWizard(creation, type);
        wizard.setForcePreviousAndNextButtons(true);
        WizardDialog dialog = new WizardDialog(null, wizard);
        dialog.setPageSize(500, 340);

        dialog.open();
    }
}
