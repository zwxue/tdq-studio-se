// ============================================================================
//
// Copyright (C) 2006-2010 Talend Inc. - www.talend.com
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

import org.eclipse.core.resources.IFolder;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.cheatsheets.ICheatSheetAction;
import org.eclipse.ui.cheatsheets.ICheatSheetManager;
import org.talend.dataprofiler.core.ui.wizard.analysis.WizardFactory;
import org.talend.resource.ResourceManager;

/**
 * DOC Administrator class global comment. Detailled comment
 */
public class CreateSqlFileAction extends Action implements ICheatSheetAction {

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.cheatsheets.ICheatSheetAction#run(java.lang.String[],
     * org.eclipse.ui.cheatsheets.ICheatSheetManager)
     */
    public void run(String[] params, ICheatSheetManager manager) {
        IFolder sqlFileFolder = ResourceManager.getSourceFileFolder();
        // FolderProvider folderProvider = new FolderProvider();
        // folderProvider.setFolderResource(sqlFileFolder);
        // folderProvider.setFolder(sqlFileFolder.getLocation().toFile());
        // SqlFileParameter sqlFileParameter = new SqlFileParameter();
        // sqlFileParameter.setFolderProvider(folderProvider);
        Wizard wizard = WizardFactory.createSqlFileWizard(sqlFileFolder);
        wizard.setForcePreviousAndNextButtons(true);
        WizardDialog dialog = new WizardDialog(null, wizard);
        dialog.setPageSize(500, 340);
        dialog.open();
    }

}
