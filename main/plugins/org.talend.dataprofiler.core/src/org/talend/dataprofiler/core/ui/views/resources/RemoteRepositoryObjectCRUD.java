// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
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

import org.eclipse.jface.viewers.ISelection;
import org.talend.dq.helper.RepositoryNodeHelper;
import org.talend.repository.model.IRepositoryNode;

/**
 * Remote Repository Object CRUD. only when the project is remote use this.
 */
public class RemoteRepositoryObjectCRUD extends LocalRepositoryObjectCRUD {

    @Override
    public Boolean handleDrop(IRepositoryNode targetNode) {
        String[] pathBeforeRefresh = getSelectedNodePaths();
        if (pathBeforeRefresh.length == 0) {
            showWarningDialog();
            return Boolean.FALSE;
        }
        // in remote project, refresh first.
        refreshDQViewForRemoteProject();

        String[] pathAfterRefresh = getSelectedNodePaths();
        if (pathAfterRefresh.length == 0) {
            showWarningDialog();
            return Boolean.FALSE;
        }

        IRepositoryNode[] selectedRepositoryNodes = getSelectedRepositoryNodes();
        if (selectedRepositoryNodes.length == 0) {
            showWarningDialog();
            return Boolean.FALSE;
        } else {
            // compare the node path value between before and after refresh
            for (int i = 0; i < getSelectedRepositoryNodes().length; i++) {
                if (!pathBeforeRefresh[i].equals(pathAfterRefresh[i])) {
                    showWarningDialog();
                    return Boolean.FALSE;
                }
            }
            return super.handleDrop(targetNode);
        }
    }

    /**
     * get the Selected Node Paths.
     * 
     * @return
     */
    public String[] getSelectedNodePaths() {
        String[] pathBeforeRefresh = new String[0];
        IRepositoryNode[] nodesBeforeRefresh = getSelectedRepositoryNodes();
        if (nodesBeforeRefresh.length > 0) {
            pathBeforeRefresh = new String[nodesBeforeRefresh.length];
            for (int i = 0; i < nodesBeforeRefresh.length; i++) {
                IRepositoryNode node = nodesBeforeRefresh[i];
                pathBeforeRefresh[i] = RepositoryNodeHelper.getPath(node).toString();
            }
        }
        return pathBeforeRefresh;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.views.resources.LocalRepositoryObjectCRUD#refreshDQViewForRemoteProject()
     */
    @Override
    public void refreshDQViewForRemoteProject() {
        refreshWorkspaceDQView();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.views.resources.LocalRepositoryObjectCRUD#isSelectionAvailable()
     */
    @Override
    public Boolean isSelectionAvailable() {
        ISelection uiSelection = getUISelection();
        return uiSelection != null && !uiSelection.isEmpty();
    }

}
