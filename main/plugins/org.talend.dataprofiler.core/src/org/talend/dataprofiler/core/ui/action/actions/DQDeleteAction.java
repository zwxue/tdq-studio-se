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

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.talend.commons.exception.LoginException;
import org.talend.commons.exception.PersistenceException;
import org.talend.commons.ui.runtime.exception.ExceptionHandler;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.helper.WorkspaceResourceHelper;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.dialog.message.DeleteModelElementConfirmDialog;
import org.talend.dataprofiler.core.ui.utils.MessageUI;
import org.talend.dataprofiler.core.ui.utils.RepNodeUtils;
import org.talend.dataprofiler.core.ui.views.DQRespositoryView;
import org.talend.dataprofiler.core.ui.views.resources.IRepositoryObjectCRUD;
import org.talend.dataquality.properties.TDQReportItem;
import org.talend.dq.helper.DQDeleteHelper;
import org.talend.dq.helper.EObjectHelper;
import org.talend.dq.helper.PropertyHelper;
import org.talend.dq.helper.ReportUtils;
import org.talend.dq.helper.RepositoryNodeHelper;
import org.talend.dq.nodes.AnalysisSubFolderRepNode;
import org.talend.dq.nodes.DQRepositoryNode;
import org.talend.dq.nodes.JrxmlTempSubFolderNode;
import org.talend.dq.nodes.JrxmlTempleteRepNode;
import org.talend.dq.nodes.ReportFileRepNode;
import org.talend.dq.nodes.ReportSubFolderRepNode;
import org.talend.dq.nodes.SourceFileRepNode;
import org.talend.dq.nodes.SourceFileSubFolderNode;
import org.talend.repository.ProjectManager;
import org.talend.repository.RepositoryWorkUnit;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.IRepositoryNode.ENodeType;
import org.talend.repository.model.RepositoryNode;
import org.talend.repository.ui.actions.DeleteAction;
import org.talend.resource.ResourceManager;
import org.talend.utils.sugars.ReturnCode;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC qiongli class global comment. Detailled comment
 */
public class DQDeleteAction extends DeleteAction {

    private RepositoryNode currentNode = null;

    private static Logger log = Logger.getLogger(DQDeleteAction.class);

    private List<RepositoryNode> selectedNodes;

    private IRepositoryObjectCRUD repositoryObjectCRUD = RepNodeUtils.getRepositoryObjectCRUD();

    public DQDeleteAction() {
        super();
        setText(DefaultMessagesImpl.getString("DQDeleteAction.delete"));//$NON-NLS-1$
        setId(ActionFactory.DELETE.getId());
        selectedNodes = new ArrayList<RepositoryNode>();
    }

    @Override
    public ISelection getSelection() {
        ISelection selection = null;
        if (currentNode == null) {
            // select by UI(tree)
            DQRespositoryView findView = CorePlugin.getDefault().getRepositoryView();
            selection = findView.getCommonViewer().getSelection();
        } else {
            // new instance of selection for dependency modeleEelemnt.
            selection = new StructuredSelection(currentNode);
        }
        return selection;
    }

    @Override
    protected ISelection getRepositorySelection() {
        return getSelection();
    }

    @Override
    public void init(TreeViewer viewer, IStructuredSelection selection) {
    }

    @Override
    public void run() {
        repositoryObjectCRUD.refreshDQViewForRemoteProject();

        // ADD msjian TDQ-7006 2013-7-24: after refresh get the selection to check.
        if (!repositoryObjectCRUD.isSelectionAvailable()) {
            repositoryObjectCRUD.showWarningDialog();
            return;
        }
        // TDQ-7006~

        ISelection selection = this.getSelection();
        // boolean onlyDeleteReportFile = true;
        // MOD gdbu 2011-11-17 TDQ-3969 : when delete elements also need delete this element in filter-list, and move it
        // to recycle bin node.
        Object[] deleteElements = ((IStructuredSelection) selection).toArray();
        // MOD qiongli 2012-4-1 TDQ-4926.fill selectedNodes,and delete opration will base on the List.
        if (deleteElements.length == 0) {
            return;
        }
        // ADD xqliu 2012-05-24 TDQ-4831
        if (forbiddenDeleteJrxmlFileFolder(deleteElements)) {
            return;
        }
        // remove the source file nodes which have been opened the editor
        deleteElements = checkSourceFilesEditorOpening(deleteElements);
        // ~ TDQ-4831

        List<RepositoryNode> selectedNodeParents = new ArrayList<RepositoryNode>();
        for (Object obj : deleteElements) {
            if (obj instanceof RepositoryNode) {
                RepositoryNode node = (RepositoryNode) obj;
                selectedNodes.add(node);
                selectedNodeParents.add(node.getParent());
            }
        }
        if (DQRepositoryNode.isOnFilterring()) {
            if (deleteElements[0] instanceof RepositoryNode) {
                setPreviousFilteredNode((RepositoryNode) deleteElements[0]);
            }
            for (RepositoryNode node : selectedNodes) {
                RepositoryNodeHelper.removeChildrenNodesWhenFiltering(node);
            }
        }

        // MOD gdbu 2011-11-30 TDQ-4090 : To get all Recycle Bin nodes.
        List deleteNodes = null;
        List<IRepositoryNode> shownNodes = null;
        List<IRepositoryNode> findAllRecycleBinNodes = null;
        if (DQRepositoryNode.isOnFilterring()) {
            deleteNodes = new ArrayList();
            Collections.addAll(deleteNodes, deleteElements);
            shownNodes = RepositoryNodeHelper.findAllChildrenNodes(deleteNodes);
            List<IRepositoryNode> recycleBinNodeFirstLevelChildren = ((RepositoryNode) RepositoryNodeHelper
                    .getRecycleBinRepNode()).getChildren();
            findAllRecycleBinNodes = RepositoryNodeHelper.findAllChildrenNodes(recycleBinNodeFirstLevelChildren);
        }
        // ~TDQ-4090

        for (int i = selectedNodes.size() - 1; i >= 0; i--) {
            if (selectedNodes.size() == 0) {
                break;
            }
            RepositoryNode node = selectedNodes.get(i);
            RepositoryNode parent = node.getParent();
            // handle generating report file.bug 18805 .
            if (node instanceof ReportFileRepNode) {
                try {
                    deleteReportFile((ReportFileRepNode) node);
                } catch (PersistenceException e) {
                    log.error(e, e);
                }
                continue;
            }
            // onlyDeleteReportFile = false;
            boolean isStateDeleted = RepositoryNodeHelper.isStateDeleted(node);
            // logical delete
            if (!isStateDeleted) {
                // closeEditors(selection);
                excuteSuperRun(null, parent);
                refreshRepositoryNodes(selectedNodeParents);
                break;
            }

            // MOD gdbu 2011-11-30 TDQ-4090 : Determine whether there has some nodes not been shown when filtering.
            if (DQRepositoryNode.isOnFilterring() && isStateDeleted) {
                for (IRepositoryNode iRepositoryNode : findAllRecycleBinNodes) {
                    if (node.equals(iRepositoryNode)) {
                        node = (RepositoryNode) iRepositoryNode;
                        shownNodes = RepositoryNodeHelper.findAllChildrenNodes(deleteNodes);
                        break;
                    }
                }
                if (!RepositoryNodeHelper.isEmptyRecycleBin(findAllRecycleBinNodes, shownNodes)) {
                    break;
                }
            }
            // ~TDQ-4090

            // show dependency dialog and phisical delete dependencies just for phisical deleting.
            boolean hasDependency = false;
            if (node.getType() == ENodeType.SIMPLE_FOLDER || node.getType() == ENodeType.SYSTEM_FOLDER) {
                List<IRepositoryNode> newLs = RepositoryNodeHelper.getRepositoryElementFromFolder(node, true);
                // if the folder have sub node(s) not be deleted, the folder should not be deleted also
                boolean haveSubNode = false;
                for (IRepositoryNode subNode : newLs) {
                    hasDependency = RepositoryNodeHelper.hasDependencyClients(subNode);
                    if (!hasDependency || hasDependency && handleDependencies(subNode)) {
                        excuteSuperRun((RepositoryNode) subNode, node);
                    } else {
                        haveSubNode = true;
                    }
                }
                if (!haveSubNode) {
                    excuteSuperRun(node, parent);
                }
            } else {
                hasDependency = RepositoryNodeHelper.hasDependencyClients(node);
                if (!hasDependency || hasDependency && handleDependencies(node)) {
                    excuteSuperRun(node, parent);
                }
            }
            // }
        }

        if (DQRepositoryNode.isOnFilterring()) {
            RepositoryNodeHelper.regainRecycleBinFilteredNode();
        }

        // the deleteReportFile() mothed have refresh the workspace and dqview
        CorePlugin.getDefault().refreshWorkSpace();
        CorePlugin.getDefault().refreshDQView(RepositoryNodeHelper.getRecycleBinRepNode());
    }

    /**
     * refresh the RepositoryNodes, this method shoule be called after logic delete multiple objects.
     * 
     * @param nodes
     */
    private void refreshRepositoryNodes(List<RepositoryNode> nodes) {
        if (nodes != null) {
            boolean refreshAll = false;
            for (RepositoryNode node : nodes) {
                if (node != null) {
                    CorePlugin.getDefault().refreshDQView(node);
                } else {
                    refreshAll = true;
                    break;
                }
            }
            if (refreshAll) {
                CorePlugin.getDefault().refreshDQView();
            }
        }
    }

    /**
     * DOC gdbu Comment method "setNextFilteredNode".
     * 
     * @param node
     */
    private void setPreviousFilteredNode(RepositoryNode node) {
        if (DQRepositoryNode.isOnFilterring()) {

            List<IRepositoryNode> allFilteredNodeList = RepositoryNodeHelper.getAllFilteredNodeList();
            for (int i = 0; i < allFilteredNodeList.size(); i++) {
                if (allFilteredNodeList.get(i).equals(node)) {

                    if (i <= 1) {
                        RepositoryNodeHelper.setFilteredNode(allFilteredNodeList.get(0));
                    } else {
                        RepositoryNodeHelper.setFilteredNode(allFilteredNodeList.get(i - 1));
                    }
                    break;
                }
            }
        }
    }

    private boolean handleDependencies(IRepositoryNode node) {
        boolean flag = false;
        List<ModelElement> dependencies = EObjectHelper.getDependencyClients(node);
        if (showDialog(node, dependencies)) {
            if (physicalDeleteDependencies(dependencies)) {
                flag = true;
            } else {
                MessageDialog.openError(null, DefaultMessagesImpl.getString("DQDeleteAction.deleteFailTitle"),//$NON-NLS-1$
                        DefaultMessagesImpl.getString("DQDeleteAction.deleteFailMessage"));//$NON-NLS-1$
            }
        }
        return flag;
    }

    /**
     * DOC qiongli Comment method "showDialog".
     * 
     * @param node
     * @return
     */
    private boolean showDialog(IRepositoryNode node, List<ModelElement> dependencies) {

        ModelElement modEle = RepositoryNodeHelper.getModelElementFromRepositoryNode(node);
        String lable = node.getObject().getLabel() == null ? PluginConstant.EMPTY_STRING : node.getObject().getLabel();
        boolean flag = DeleteModelElementConfirmDialog.showDialog(null, modEle,
                dependencies.toArray(new ModelElement[dependencies.size()]),
                DefaultMessagesImpl.getString("DQDeleteAction.dependencyByOther", lable), true);//$NON-NLS-1$
        return flag;
    }

    /**
     * DOC qiongli Comment method "physicalDeleteDependencies".
     * 
     * @param dependences
     * @return
     */
    private boolean physicalDeleteDependencies(List<ModelElement> dependences) {
        boolean isSucceed = true;
        try {
            for (ModelElement mod : dependences) {
                List<ModelElement> subDependences = EObjectHelper.getDependencyClients(mod);
                if (subDependences != null && !subDependences.isEmpty()) {
                    isSucceed = physicalDeleteDependencies(subDependences);
                    if (!isSucceed) {
                        return false;
                    }
                }
                RepositoryNode tempNode = RepositoryNodeHelper.recursiveFind(mod);
                if (tempNode == null) {
                    tempNode = RepositoryNodeHelper.recursiveFindRecycleBin(mod);
                }
                boolean isStateDel = RepositoryNodeHelper.isStateDeleted(tempNode);
                if (tempNode != null && !isStateDel) {
                    // logcial delete dependcy element.
                    if (tempNode.getObject() != null) {
                        CorePlugin.getDefault().closeEditorIfOpened(tempNode.getObject().getProperty().getItem());
                    }
                    // need to pass the tempNode parameter at here for logical delete dependce.
                    excuteSuperRun(tempNode, tempNode.getParent());
                    CorePlugin.getDefault().refreshDQView(RepositoryNodeHelper.getRecycleBinRepNode());
                }
                // physical delete dependcy element.
                tempNode = RepositoryNodeHelper.recursiveFindRecycleBin(mod);
                if (tempNode != null) {
                    excuteSuperRun(tempNode, tempNode.getParent());
                    // MOD qiongli 2012-5-11 TDQ-5250,if not confirm phy-delete,shoud restore this dependence.
                    if (!confirmFromDialog) {
                        if (!isStateDel) {
                            ProxyRepositoryFactory factory = ProxyRepositoryFactory.getInstance();
                            String oldPath = tempNode.getObject().getProperty().getItem().getState().getPath();
                            IPath path = new Path(oldPath);
                            factory.restoreObject(tempNode.getObject(), path);
                            CorePlugin.getDefault().refreshDQView();
                        }
                        isSucceed = false;
                    } else {
                        IFile propertyFile = PropertyHelper.getPropertyFile(mod);
                        if (propertyFile != null && propertyFile.exists()) {
                            isSucceed = false;
                        }
                    }
                }
            }
        } catch (Exception exc) {
            log.error(exc, exc);
            return false;
        }
        return isSucceed;
    }

    /**
     * DOC qiongli :excute super method run().
     * 
     * @param currentNode:null for logical delete a selected element by UI.none-null for physical delete or logical
     * delete dependecy.
     */
    private void excuteSuperRun(RepositoryNode currentNode, RepositoryNode parent) {
        this.currentNode = currentNode;
        Item item = null;
        if (currentNode != null) {
            Property property = currentNode.getObject().getProperty();
            if (property != null) {
                item = property.getItem();
            }
        }

        // is TDQReportItem or not
        boolean isReport = item != null && item instanceof TDQReportItem;
        List<IFile> repDocLinkFiles = new ArrayList<IFile>();
        if (isReport) {
            repDocLinkFiles = ReportUtils.getRepDocLinkFiles(RepositoryNodeHelper.getIFile(currentNode));
        }

        // MOD qiongli 2011-5-9 bug 21035,avoid to unload resource.
        super.setAvoidUnloadResources(true);
        super.run();
        // because reuse tos codes.remove current node from its parent(simple folder) for phisical delete or logical
        // delete dependency.
        if (currentNode != null) {
            // MOD qiongli 2012-4-1 TDQ-4926,after physical delete this node,should remove it from that
            // selection List.avoid to delete twice.
            this.selectedNodes.remove(currentNode);
            if (parent != null
                    && (parent.getType() == ENodeType.SIMPLE_FOLDER || parent.getLabel().equalsIgnoreCase(
                            ERepositoryObjectType.RECYCLE_BIN.name().replaceAll("_", PluginConstant.SPACE_STRING)))) {//$NON-NLS-1$
                parent.getChildren(true).remove(currentNode);
            }
            // delete related output folder after physical delete a report.
            DQDeleteHelper.deleteRelations(item);
            // delete the link files which links to the Report Generated Doc File
            if (isReport && !repDocLinkFiles.isEmpty()) {
                ReportUtils.removeRepDocLinkFiles(repDocLinkFiles);
            }
        }

        // refresh parent node
        if (parent != null) {
            if (parent instanceof AnalysisSubFolderRepNode || parent instanceof ReportSubFolderRepNode) {
                CorePlugin.getDefault().refreshDQView(RepositoryNodeHelper.findNearestSystemFolderNode(parent));
            } else {
                CorePlugin.getDefault().refreshDQView(parent);
            }
        }
    }

    /**
     * physical delete generating report file.
     * 
     * @param repFileNode
     * @throws PersistenceException
     */
    private void deleteReportFile(final ReportFileRepNode repFileNode) throws PersistenceException {
        RepositoryWorkUnit repositoryWorkUnit = new RepositoryWorkUnit(ProjectManager.getInstance().getCurrentProject(),
                "deleteReportFile") { //$NON-NLS-1$

            @Override
            protected void run() throws LoginException, PersistenceException {
                final IWorkspaceRunnable op = new IWorkspaceRunnable() {

                    public void run(IProgressMonitor monitor) {
                        try {
                            IPath location = Path.fromOSString(repFileNode.getResource().getProjectRelativePath().toOSString());
                            IFile latestRepIFile = ResourceManager.getRootProject().getFile(location);
                            if (showConfirmDialog(repFileNode.getLabel())) {
                                if (latestRepIFile.isLinked()) {
                                    File file = new File(latestRepIFile.getRawLocation().toOSString());
                                    if (file.exists()) {
                                        file.delete();
                                    }
                                }
                                latestRepIFile.delete(true, null);
                                IContainer parent = latestRepIFile.getParent();
                                if (parent != null) {
                                    parent.refreshLocal(IResource.DEPTH_INFINITE, monitor);
                                }
                            }
                        } catch (CoreException e) {
                            log.error(e, e);
                        }
                    }

                };

                IRunnableWithProgress iRunnableWithProgress = new IRunnableWithProgress() {

                    public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                        IWorkspace workspace = ResourcesPlugin.getWorkspace();
                        try {
                            ISchedulingRule schedulingRule = workspace.getRoot();
                            workspace.run(op, schedulingRule, IWorkspace.AVOID_UPDATE, monitor);
                        } catch (CoreException e) {
                            throw new InvocationTargetException(e);
                        }
                    }
                };

                try {
                    PlatformUI.getWorkbench().getProgressService().run(false, false, iRunnableWithProgress);
                } catch (InterruptedException e) {
                    ExceptionHandler.process(e);
                } catch (InvocationTargetException e) {
                    ExceptionHandler.process(e);
                }
            }
        };
        repositoryWorkUnit.setAvoidUnloadResources(true);
        ProxyRepositoryFactory.getInstance().executeRepositoryWorkUnit(repositoryWorkUnit);
        repositoryWorkUnit.throwPersistenceExceptionIfAny();

        // refresh the parent
        RepositoryNode parent = repFileNode.getParent();
        if (parent != null) {
            CorePlugin.getDefault().refreshDQView(parent);
        }
    }

    private boolean showConfirmDialog(String reportFileName) {
        return MessageDialog.openConfirm(null, DefaultMessagesImpl.getString("DQDeleteAction.deleteForeverTitle"), reportFileName//$NON-NLS-1$
                + PluginConstant.SPACE_STRING + DefaultMessagesImpl.getString("DQDeleteAction.areYouDeleteForever"));//$NON-NLS-1$
    }

    public RepositoryNode getCurrentNode() {
        return this.currentNode;
    }

    public void setCurrentNode(RepositoryNode node) {
        this.currentNode = node;
    }

    /**
     * DOC xqliu Comment method "checkSourceFilesEditorOpening".
     * 
     * @param deleteElements
     * @return
     */
    private Object[] checkSourceFilesEditorOpening(Object[] deleteElements) {
        List list = new ArrayList();
        boolean opened = false;
        String openSourceFileNames = ""; //$NON-NLS-1$
        for (Object obj : deleteElements) {
            if (obj instanceof SourceFileRepNode) {
                SourceFileRepNode node = (SourceFileRepNode) obj;
                ReturnCode rc = WorkspaceResourceHelper.checkSourceFileNodeOpening(node);
                if (rc.isOk()) {
                    opened = rc.isOk();
                    openSourceFileNames += rc.getMessage();
                } else {
                    list.add(obj);
                }
            } else if (obj instanceof SourceFileSubFolderNode) {
                SourceFileSubFolderNode node = (SourceFileSubFolderNode) obj;
                ReturnCode rc = WorkspaceResourceHelper.checkSourceFileSubFolderNodeOpening(node);
                if (rc.isOk()) {
                    opened = rc.isOk();
                    openSourceFileNames += rc.getMessage();
                } else {
                    list.add(obj);
                }
            } else {
                list.add(obj);
            }
        }
        if (opened) {
            WorkspaceResourceHelper.showSourceFilesOpeningWarnMessages(openSourceFileNames);
        }
        return list.toArray();
    }

    /**
     * DOC xqliu Comment method "forbiddenDeleteJrxmlFileFolder".
     * 
     * @param deleteElements
     */
    private boolean forbiddenDeleteJrxmlFileFolder(Object[] deleteElements) {
        boolean includeJrxml = false;
        for (Object obj : deleteElements) {
            if (obj instanceof JrxmlTempleteRepNode || obj instanceof JrxmlTempSubFolderNode) {
                includeJrxml = true;
                break;
            }
        }
        if (includeJrxml) {
            MessageUI.openWarning(DefaultMessagesImpl.getString("JrxmlFileAction.forbiddenOperation")); //$NON-NLS-1$
        }
        return includeJrxml;
    }
}
