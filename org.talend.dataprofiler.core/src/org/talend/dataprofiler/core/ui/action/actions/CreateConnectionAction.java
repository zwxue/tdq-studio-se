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

import java.util.Properties;

import org.eclipse.core.resources.IFolder;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.cheatsheets.ICheatSheetAction;
import org.eclipse.ui.cheatsheets.ICheatSheetManager;
import org.talend.cwm.management.api.FolderProvider;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.wizard.analysis.WizardFactory;
import org.talend.dq.analysis.parameters.DBConnectionParameter;
import org.talend.top.repository.ProxyRepositoryManager;

/**
 * DOC zqin class global comment. Detailled comment <br/>
 * 
 * $Id: talend.epf 1 2006-09-29 17:06:40Z zqin $
 * 
 */
public class CreateConnectionAction extends Action implements ICheatSheetAction {

    private static final int WIDTH = 400;

    private static final int HEIGHT = 350;

    private IFolder folder;

    public CreateConnectionAction() {
        super(DefaultMessagesImpl.getString("CreateConnectionAction.newConnection")); //$NON-NLS-1$
        setImageDescriptor(ImageLib.getImageDescriptor(ImageLib.NEW_CONNECTION));
    }

    public CreateConnectionAction(IFolder folder) {
        this();
        this.folder = folder;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.action.Action#run()
     */
    @Override
    public void run() {
        DBConnectionParameter connectionParam = new DBConnectionParameter();
        connectionParam.setParameters(new Properties());

        if (folder != null) {
            FolderProvider provider = new FolderProvider();
            provider.setFolderResource(folder);
            connectionParam.setFolderProvider(provider);
        }
        Wizard wizard = WizardFactory.createDatabaseConnectionWizard(connectionParam);

        WizardDialog dialog = new WizardDialog(null, wizard);
        dialog.setPageSize(WIDTH, HEIGHT);
        wizard.setContainer(dialog);
        if (WizardDialog.OK == dialog.open())
            ProxyRepositoryManager.getInstance().save();
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
