// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.hadoopcluster.action;

import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.talend.dataprofiler.core.hadoopcluster.i18n.DefaultMessagesImpl;
import org.talend.repository.hdfs.ui.HDFSSchemaWizard;
import org.talend.repository.model.RepositoryNode;
import org.talend.repository.model.hdfs.HDFSConnectionItem;

/**
 * created by yyin on 2015年5月22日 Detailled comment
 *
 */
public class RetrieveSchemaOfHDFSAction extends CreateHiveTableAction {

    private static final String rslabel = DefaultMessagesImpl.getString("RetrieveSchemaOfHDFSAction.create"); //$NON-NLS-1$

    /**
     * DOC yyin RetrieveSchemaOfHDFSAction constructor comment.
     * 
     * @param node
     */
    public RetrieveSchemaOfHDFSAction(RepositoryNode node) {
        super(node);
        setText(rslabel);
        setToolTipText(rslabel);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.dataprofiler.core.ui.action.actions.CreateHiveTableAction#openHDFSSchemaWizard(org.talend.repository
     * .model.hdfs.HDFSConnectionItem)
     */
    @Override
    protected void openHDFSSchemaWizard(HDFSConnectionItem item) {
        WizardDialog wizardDialog = new WizardDialog(Display.getCurrent().getActiveShell(), new HDFSSchemaWizard(
                PlatformUI.getWorkbench(), true, repositoryNode.getObject(), null, getExistingNames(), false));
        wizardDialog.setPageSize(WIZARD_WIDTH, WIZARD_HEIGHT);
        wizardDialog.create();
        wizardDialog.open();
    }

}
