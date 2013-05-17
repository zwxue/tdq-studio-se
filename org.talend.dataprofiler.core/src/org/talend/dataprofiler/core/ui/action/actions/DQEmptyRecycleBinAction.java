// ============================================================================
//
// Copyright (C) 2006-2013 Talend Inc. - www.talend.com
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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ISelection;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.ui.utils.RepNodeUtils;
import org.talend.dataprofiler.core.ui.views.DQRespositoryView;
import org.talend.dataprofiler.core.ui.views.resources.IRepositoryObjectCRUDAction;
import org.talend.dq.helper.RepositoryNodeHelper;
import org.talend.dq.nodes.DQRepositoryNode;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.RepositoryNode;
import org.talend.repository.ui.actions.EmptyRecycleBinAction;

/**
 * DOC qiongli class global comment. Detailled comment
 */
public class DQEmptyRecycleBinAction extends EmptyRecycleBinAction {

    private IRepositoryObjectCRUDAction repositoryObjectCRUD = RepNodeUtils.getRepositoryObjectCRUD();

    public DQEmptyRecycleBinAction() {
        super();
        setText(super.getText());
        setImageDescriptor(super.getImageDescriptor());
    }

    @Override
    public ISelection getSelection() {
        DQRespositoryView findView = CorePlugin.getDefault().getRepositoryView();
        ISelection selection = findView.getCommonViewer().getSelection();
        return selection;
    }

    @Override
    protected ISelection getRepositorySelection() {

        return this.getSelection();
    }

    @Override
    public void run() {
        repositoryObjectCRUD.refreshDQViewForRemoteProject();
        // MOD gdbu 2011-11-24 TDQ-4068
        List<IRepositoryNode> findAllRecycleBinNodes = needDeleteNodes();
        if (null == findAllRecycleBinNodes) {
            return;
        }
        new DQDeleteAction(findAllRecycleBinNodes.toArray()).run();
    }

    /**
     * ADD gdbu 2011-11-24 TDQ-4068 To get all the nodes those need to be deleted when empty Recycle Bin node.
     * 
     * DOC gdbu Comment method "findAllRecycleBinNodes".
     * 
     * @return
     */
    private List<IRepositoryNode> needDeleteNodes() {
        DQRepositoryNode.setIsReturnAllNodesWhenFiltering(false);
        // shownNodesInRecycleBin list contains only the first level shown children under the Recycle Bin node.
        List<IRepositoryNode> shownNodesInRecycleBin = ((RepositoryNode) RepositoryNodeHelper.getRecycleBinRepNode())
                .getChildren();
        DQRepositoryNode.setIsReturnAllNodesWhenFiltering(true);

        // If there is no filter in the case, findAllRecycleBinNode contains of all the nodes under the Recycle Bin
        // node.
        // If the filter case, findAllRecycleBinNode contains all need to be deleted nodes under the Recycle Bin node.
        List<IRepositoryNode> findAllRecycleBinNode = null;

        if (DQRepositoryNode.isOnFilterring()) {
            // containsFilteredOutChildren list contains all of the first level children under the Recycle Bin node.
            List<IRepositoryNode> containsFilteredOutChildren = ((RepositoryNode) RepositoryNodeHelper.getRecycleBinRepNode())
                    .getChildren();

            // needToEmptyNodesWhenFiltering list contains Recycle Bin's first level children which need to be
            // delete (contains those nodes which has been filtered out).
            List<IRepositoryNode> needToEmptyNodesWhenFiltering = new ArrayList<IRepositoryNode>();

            for (IRepositoryNode iRepositoryNode : containsFilteredOutChildren) {
                if (shownNodesInRecycleBin.contains(iRepositoryNode)) {
                    needToEmptyNodesWhenFiltering.add(iRepositoryNode);
                }
            }

            findAllRecycleBinNode = RepositoryNodeHelper.findAllChildrenNodes(needToEmptyNodesWhenFiltering);

            if (!RepositoryNodeHelper.isEmptyRecycleBin(findAllRecycleBinNode,
                    RepositoryNodeHelper.findAllChildrenNodes(shownNodesInRecycleBin))) {
                return null;
            }

        } else {
            findAllRecycleBinNode = RepositoryNodeHelper.findAllChildrenNodes(shownNodesInRecycleBin);
        }

        return findAllRecycleBinNode;
    }
}
