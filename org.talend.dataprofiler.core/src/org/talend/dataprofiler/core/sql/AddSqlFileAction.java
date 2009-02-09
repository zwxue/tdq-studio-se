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
package org.talend.dataprofiler.core.sql;

import static org.talend.dataprofiler.core.PluginConstant.SE_ID;
import net.sourceforge.sqlexplorer.plugin.editors.SQLEditorInput;

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
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.perspective.ChangePerspectiveAction;
import org.talend.dataprofiler.core.ui.wizard.analysis.WizardFactory;

/**
 * DOC qzhang class global comment. Detailled comment <br/>
 * 
 * $Id: talend.epf 1 2006-09-29 17:06:40Z nrousseau $
 * 
 */
public class AddSqlFileAction extends Action {

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
                e.printStackTrace();
            }
            ChangePerspectiveAction action = new ChangePerspectiveAction(SE_ID);
            action.run();
            try {
                ap
                        .openEditor(new SQLEditorInput(fileWizard.getSqlFile()),
                                "net.sourceforge.sqlexplorer.plugin.editors.SQLEditor"); //$NON-NLS-1$
            } catch (PartInitException e) {
                e.printStackTrace();
            }
        }
    }

}
