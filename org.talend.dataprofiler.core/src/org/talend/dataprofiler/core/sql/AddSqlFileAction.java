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
package org.talend.dataprofiler.core.sql;

import net.sourceforge.sqlexplorer.plugin.editors.SQLEditorInput;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.cheatsheets.ICheatSheetAction;
import org.eclipse.ui.cheatsheets.ICheatSheetManager;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.wizard.analysis.WizardFactory;
import org.talend.resource.ResourceManager;

/**
 * DOC qzhang class global comment. Detailled comment <br/>
 * 
 * $Id: talend.epf 1 2006-09-29 17:06:40Z nrousseau $
 * 
 */
public class AddSqlFileAction extends Action implements ICheatSheetAction {

    protected static Logger log = Logger.getLogger(AddSqlFileAction.class);

    private IFolder folder;

    /**
     * DOC qzhang AddSqlFileAction constructor comment.
     * 
     * @param folder
     */
    public AddSqlFileAction(IFolder folder) {
        setText(DefaultMessagesImpl.getString("AddSqlFileAction.createSQLFile")); //$NON-NLS-1$
        setImageDescriptor(ImageLib.getImageDescriptor(ImageLib.CREATE_SQL_ACTION));
        this.folder = folder;
    }

    /**
     * 
     * DOC zshen AddSqlFileAction constructor comment.
     * 
     */
    public AddSqlFileAction() {
        setText(DefaultMessagesImpl.getString("AddSqlFileAction.createSQLFile")); //$NON-NLS-1$
        setImageDescriptor(ImageLib.getImageDescriptor(ImageLib.CREATE_SQL_ACTION));
        this.folder = ResourceManager.getSourceFileFolder();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.action.Action#run()
     */
    @Override
    public void run() {
        IWorkbenchWindow aww = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        IWorkbenchPage ap = aww.getActivePage();

        CreateSqlFileWizard fileWizard = (CreateSqlFileWizard) WizardFactory.createSqlFileWizard(folder);
        WizardDialog dialog = new WizardDialog(Display.getDefault().getActiveShell(), fileWizard);
        fileWizard.setWindowTitle(getText());
        if (WizardDialog.OK == dialog.open()) {
            try {
                folder.refreshLocal(IResource.DEPTH_INFINITE, null);
            } catch (CoreException e) {
                log.error(e, e);
            }

            try {
                ap
                        .openEditor(new SQLEditorInput(fileWizard.getSqlFile()),
                                "net.sourceforge.sqlexplorer.plugin.editors.SQLEditor"); //$NON-NLS-1$
            } catch (PartInitException e) {
                log.error(e, e);
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.cheatsheets.ICheatSheetAction#run(java.lang.String[],
     * org.eclipse.ui.cheatsheets.ICheatSheetManager)
     */
    public void run(String[] params, ICheatSheetManager manager) {
        run();

    }

}
