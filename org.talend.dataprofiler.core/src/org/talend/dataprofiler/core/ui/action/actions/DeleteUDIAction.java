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

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.manager.DQStructureManager;
import org.talend.dataquality.rules.WhereRule;
import org.talend.dq.helper.resourcehelper.DQRuleResourceFileHelper;
import org.talend.resource.ResourceManager;

/**
 * DOC xqliu class global comment. Detailled comment
 */
public class DeleteUDIAction extends Action {

    protected static Logger log = Logger.getLogger(DeleteDQRuleAction.class);

    private List<IFile> folder;

    public DeleteUDIAction(List<IFile> selectedFiles) {
        setText(DefaultMessagesImpl.getString("UserDefinedIndicatorsActionProvider.delete")); //$NON-NLS-1$
        setImageDescriptor(ImageLib.getImageDescriptor(ImageLib.DELETE_ACTION));
        this.folder = selectedFiles;
    }

    @Override
    public void run() {
        IFolder sourceFiles = ResourceManager.getLibrariesFolder().getFolder(DQStructureManager.DQ_RULES);
        for (IFile file : folder) {
            // TODO xqliu 2009-07-22 change to user defined indicator
            WhereRule wr = DQRuleResourceFileHelper.getInstance().findWhereRule(file);
            String fileName = wr == null ? file.getName() : wr.getName();
            if (MessageDialog
                    .openConfirm(
                            new Shell(),
                            DefaultMessagesImpl.getString("UserDefinedIndicatorsActionProvider.deleteIndicator"), DefaultMessagesImpl.getString("UserDefinedIndicatorsActionProvider.areYouDeleteFolder", fileName))) { //$NON-NLS-1$ //$NON-NLS-2$
                try {
                    if (file.exists()) {
                        file.delete(true, null);
                    }
                } catch (CoreException e) {
                    log.error(e, e);
                }
            }
        }
        try {
            sourceFiles.refreshLocal(IResource.DEPTH_INFINITE, null);
        } catch (CoreException e) {
            log.error(e, e);
        }
    }
}
