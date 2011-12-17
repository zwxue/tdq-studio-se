// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
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

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.wizard.WizardDialog;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.utils.WorkbenchUtils;
import org.talend.dataprofiler.core.ui.wizard.parserrule.ExportParserRuleWizard;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.RepositoryNode;

/**
 * DOC Administrator class global comment. Detailled comment
 */
public class ExportParserRuleAction extends Action {

    protected static Logger log = Logger.getLogger(ExportParserRuleAction.class);

    private IRepositoryNode parserRuleFolder;

    /**
     * DOC Administrator ExportParserRuleAction constructor comment.
     * 
     * @param node
     */
    public ExportParserRuleAction(IRepositoryNode node) {
        setText(DefaultMessagesImpl.getString("ExportParserRuleAction.ExportExchange")); //$NON-NLS-1$
        setImageDescriptor(ImageLib.getImageDescriptor(ImageLib.EXPORT));
        parserRuleFolder = node;
    }

    @Override
    public void run() {
        ExportParserRuleWizard wizard = new ExportParserRuleWizard(parserRuleFolder);
        WizardDialog dialog = new WizardDialog(null, wizard);
        wizard.setWindowTitle(getText());
        if (WizardDialog.OK == dialog.open()) {
            try {
                IFolder folder = WorkbenchUtils.getFolder((RepositoryNode) parserRuleFolder);
                folder.refreshLocal(IResource.DEPTH_INFINITE, null);
            } catch (CoreException e) {
                log.error(e, e);
            }
        }
    }

}
