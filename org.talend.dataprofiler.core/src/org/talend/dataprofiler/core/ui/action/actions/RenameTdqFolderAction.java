// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
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
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.repository.i18n.Messages;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.utils.RepNodeUtils;
import org.talend.dataprofiler.core.ui.views.DQRespositoryView;
import org.talend.dataprofiler.core.ui.views.resources.IRepositoryObjectCRUD;
import org.talend.repository.model.RepositoryNode;
import org.talend.repository.ui.actions.RenameFolderAction;

/**
 * rename tdq folder action.
 */
public class RenameTdqFolderAction extends RenameFolderAction {

    protected static Logger log = Logger.getLogger(RenameTdqFolderAction.class);

    private IRepositoryObjectCRUD repositoryObjectCRUD = RepNodeUtils.getRepositoryObjectCRUD();

    /**
     * @param node a folder
     */
    public RenameTdqFolderAction(RepositoryNode node) {
        this.repositoryNode = node;
        setText(DefaultMessagesImpl.getString("RenameTdqFolderAction.renameFolder")); //$NON-NLS-1$
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.repository.ui.actions.AContextualAction#doRun()
     */
    @Override
    protected void doRun() {
        repositoryObjectCRUD.handleRenameFolder(repositoryNode);
        super.doRun();
    }

    @Override
    protected void openFolderWizard(RepositoryNode node, ERepositoryObjectType objectType, IPath path) {
        if (repositoryNode.getObject().isDeleted()) {
            MessageDialog.openWarning(new Shell(), Messages.getString("RenameFolderAction.warning.cannotFind.title"), Messages //$NON-NLS-1$
                    .getString("RenameFolderAction.warning.cannotFind.message")); //$NON-NLS-1$
            return;
        }

        if (objectType != null) {
            super.openFolderWizard(node, objectType, path);

            // refresh the dq repository view
            if (this.repositoryNode != null && this.repositoryNode.getParent() != null) {
                CorePlugin.getDefault().refreshDQView(this.repositoryNode.getParent());
            }
        }
    }

    @Override
    public ISelection getSelection() {
        CorePlugin.getDefault().refreshWorkSpace();
        IWorkbenchPart activePart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart();
        ((DQRespositoryView) activePart).refresh();
        return repositoryObjectCRUD.getUISelection();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.repository.ui.actions.AContextualAction#getRepositorySelection()
     */
    @Override
    protected ISelection getRepositorySelection() {
        DQRespositoryView repositoryViewPart = null;
        for (IViewReference viewRef : getActivePage().getViewReferences()) {
            if (viewRef.getView(false) instanceof DQRespositoryView) {
                repositoryViewPart = (DQRespositoryView) viewRef.getView(false);
                break;
            }
        }

        if (repositoryViewPart == null) {
            return null;
        }
        ISelection selection = repositoryViewPart.getCommonViewer().getSelection();
        return selection;
    }
}
