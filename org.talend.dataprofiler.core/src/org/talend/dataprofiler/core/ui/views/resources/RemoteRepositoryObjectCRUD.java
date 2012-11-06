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
package org.talend.dataprofiler.core.ui.views.resources;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.talend.commons.exception.LoginException;
import org.talend.commons.exception.PersistenceException;
import org.talend.core.runtime.CoreRuntimePlugin;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.views.DQRespositoryView;
import org.talend.dq.helper.RepositoryNodeHelper;
import org.talend.repository.RepositoryWorkUnit;
import org.talend.repository.model.IRepositoryNode;

/**
 * Remote Repository Object CRUD. only when the project is remote use this.
 * 
 */
public class RemoteRepositoryObjectCRUD extends LocalRepositoryObjectCRUD {

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.views.resources.LocalRepositoryObjectCRUD#getUISelection()
     */
    @Override
    public ISelection getUISelection() {
        IWorkbenchPart activePart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart();
        ((DQRespositoryView) activePart).refresh();
        return super.getUISelection();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.dataprofiler.core.ui.views.resources.LocalRepositoryObjectCRUD#handleDrop(org.talend.repository.model
     * .IRepositoryNode)
     */
    @Override
    public Boolean handleDrop(IRepositoryNode targetNode) {
        String[] pathBeforeRefresh = getSelectedNodePaths();

        // in remote project, refresh first.
        refreshDQView();
        String[] pathAfterRefresh = getSelectedNodePaths();

        // compare the node path value between before and after refresh
        for (int i = 0; i < getSelectedRepositoryNodes().length; i++) {
            if (!pathBeforeRefresh[i].equals(pathAfterRefresh[i])) {
                MessageDialog
                        .openInformation(
                                new Shell(),
                                DefaultMessagesImpl.getString("RepositoyNodeDropAdapterAssistant.moveHintTitle"), DefaultMessagesImpl.getString("RepositoyNodeDropAdapterAssistant.moveHintContent")); //$NON-NLS-1$ //$NON-NLS-2$  
                return Boolean.FALSE;
            }
        }
        return super.handleDrop(targetNode);
    }

    /**
     * refresh DQ View First.
     */
    public void refreshDQView() {
        RepositoryWorkUnit<Object> repositoryWorkUnit = new RepositoryWorkUnit<Object>(
                "Updating from the SVN server first, please wait...") { //$NON-NLS-1$

            @Override
            protected void run() throws LoginException, PersistenceException {
                IRepositoryNode[] selectedRepositoryNodes = getSelectedRepositoryNodes();
                for (IRepositoryNode res : selectedRepositoryNodes) {
                    CorePlugin.getDefault().refreshDQView(res.getParent());
                }
                CorePlugin.getDefault().refreshWorkSpace();
            }
        };
        repositoryWorkUnit.setAvoidUnloadResources(true);
        CoreRuntimePlugin.getInstance().getProxyRepositoryFactory().executeRepositoryWorkUnit(repositoryWorkUnit);
    }

    /**
     * get the Selected Node Paths.
     * 
     * @return
     */
    public String[] getSelectedNodePaths() {
        IRepositoryNode[] nodesBeforeRefresh = getSelectedRepositoryNodes();
        String pathBeforeRefresh[] = new String[nodesBeforeRefresh.length];
        for (int i = 0; i < nodesBeforeRefresh.length; i++) {
            IRepositoryNode node = nodesBeforeRefresh[i];
            pathBeforeRefresh[i] = RepositoryNodeHelper.getPath(node).toString();
        }
        return pathBeforeRefresh;
    }
}
