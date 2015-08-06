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
package org.talend.dataprofiler.core.ui.action.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.talend.commons.exception.PersistenceException;
import org.talend.commons.ui.runtime.exception.ExceptionHandler;
import org.talend.commons.ui.runtime.exception.MessageBoxExceptionHandler;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.dialog.message.DeleteModelElementConfirmDialog;
import org.talend.dataprofiler.core.ui.utils.RepNodeUtils;
import org.talend.dataprofiler.core.ui.views.DQRespositoryView;
import org.talend.dataprofiler.core.ui.views.resources.IRepositoryObjectCRUD;
import org.talend.dq.helper.DQDeleteHelper;
import org.talend.dq.helper.EObjectHelper;
import org.talend.dq.helper.ReportUtils;
import org.talend.dq.helper.RepositoryNodeHelper;
import org.talend.dq.nodes.DQRepositoryNode;
import org.talend.repository.ProjectManager;
import org.talend.repository.model.IProxyRepositoryFactory;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.IRepositoryNode.ENodeType;
import org.talend.repository.model.RepositoryNode;
import org.talend.repository.ui.actions.EmptyRecycleBinAction;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC qiongli class global comment. Detailled comment
 */
public class DQEmptyRecycleBinAction extends EmptyRecycleBinAction {

    private IRepositoryObjectCRUD repositoryObjectCRUD = RepNodeUtils.getRepositoryObjectCRUD();

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
        List<IRepositoryNode> canNotDeletedNodes = DQDeleteHelper.getCanNotDeletedNodes(findAllRecycleBinNodes, true);
        if (!canNotDeletedNodes.isEmpty()) {
            DeleteModelElementConfirmDialog.showDialog(null, canNotDeletedNodes,
                    DefaultMessagesImpl.getString("DQEmptyRecycleBinAction.allDependencies"));//$NON-NLS-1$
            return;
        }

        // get the link files which link to the Report Generated Doc File
        List<IFile> linkFiles = ReportUtils.getRepGenDocLinkFiles(findAllRecycleBinNodes);
        List<IFile> repFiles = ReportUtils.getReportFiles(findAllRecycleBinNodes);

        // MOD qiongli 2011-5-20 bug 21035,avoid to unload resource.
        super.setAvoidUnloadResources(true);
        super.run();

        if (!linkFiles.isEmpty()) {
            ReportUtils.removeRepDocLinkFiles(linkFiles);
        }
        if (!repFiles.isEmpty()) {
            ReportUtils.deleteRepOutputFolders(repFiles);
        }

        CorePlugin.getDefault().refreshDQView(findAllRecycleBinNodes.get(0).getParent());
        CorePlugin.getDefault().refreshWorkSpace();

        // MOD gdbu 2011-11-18 TDQ-3969 : after empty recycle bin re-filter the tree , to create a new list .
        if (DQRepositoryNode.isOnFilterring()) {
            RepositoryNodeHelper.fillTreeList(null);
            RepositoryNodeHelper
                    .setFilteredNode(RepositoryNodeHelper.getRootNode(ERepositoryObjectType.TDQ_DATA_PROFILING, true));
        }
    }

    @Override
    /**
     * override the method so as to removing dependencies
     */
    protected void doRun() {
        ISelection selection = getSelection();
        Object obj = ((IStructuredSelection) selection).getFirstElement();
        final RepositoryNode node = (RepositoryNode) obj;

        final String title = DefaultMessagesImpl.getString("DQEmptyRecycleBinAction.dialog.title"); //$NON-NLS-1$
        String message = null;
        DQRepositoryNode.setIsReturnAllNodesWhenFiltering(false);
        List<IRepositoryNode> children = node.getChildren();
        DQRepositoryNode.setIsReturnAllNodesWhenFiltering(true);
        if (children.size() == 0) {
            return;
        } else if (children.size() >= 1) {
            message = DefaultMessagesImpl.getString("DQEmptyRecycleBinAction.dialog.messageAllElements") + "\n" + //$NON-NLS-1$ //$NON-NLS-2$
                    DefaultMessagesImpl.getString("DQEmptyRecycleBinAction.dialog.message1"); //$NON-NLS-1$;
        }
        final Shell shell = super.getShell();
        if (!(MessageDialog.openQuestion(shell, title, message))) {
            return;
        }

        IProxyRepositoryFactory factory = ProxyRepositoryFactory.getInstance();
        for (IRepositoryNode child : children) {
            try {
                removeDependency(child);
                // MOD qiongli 2012-3-29 delete related elements after physical delete itself.
                Item item = null;
                Property property = child.getObject().getProperty();
                if (property != null) {
                    item = property.getItem();
                }
                deleteElements(factory, (RepositoryNode) child);
                DQDeleteHelper.deleteRelations(item);
            } catch (Exception e) {
                MessageBoxExceptionHandler.process(e);
            }
        }
        try {
            factory.saveProject(ProjectManager.getInstance().getCurrentProject());
        } catch (PersistenceException e) {
            ExceptionHandler.process(e);
        }
    }

    /**
     * remove the dependencies of the IRepositoryNode(include all the children).
     * 
     * @param repoNode the IRepositoryNode
     */
    private void removeDependency(IRepositoryNode repoNode) {
        if (repoNode.getType() == ENodeType.REPOSITORY_ELEMENT) {
            ModelElement modelEle = RepositoryNodeHelper.getModelElementFromRepositoryNode(repoNode);
            EObjectHelper.removeDependencys(modelEle);
        } else if (repoNode.getType() == ENodeType.SIMPLE_FOLDER) {
            if (repoNode.getChildren() != null) {
                for (IRepositoryNode childNode : repoNode.getChildren()) {
                    removeDependency(childNode);
                }
            }
        }
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
