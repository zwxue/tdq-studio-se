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

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.talend.commons.exception.ExceptionHandler;
import org.talend.commons.exception.LoginException;
import org.talend.commons.exception.PersistenceException;
import org.talend.core.GlobalServiceRegister;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.Folder;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.helper.WorkspaceResourceHelper;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.dialog.message.DeleteModelElementConfirmDialog;
import org.talend.dataprofiler.core.ui.utils.RepNodeUtils;
import org.talend.dataprofiler.core.ui.views.DQRespositoryView;
import org.talend.dataprofiler.core.ui.views.resources.IRepositoryObjectCRUDAction;
import org.talend.dataquality.properties.TDQReportItem;
import org.talend.dataquality.record.linkage.ui.service.IMatchRuleChangeService;
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
import org.talend.repository.ui.actions.DeleteActionCache;
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

    private IRepositoryObjectCRUDAction repositoryObjectCRUD = RepNodeUtils.getRepositoryObjectCRUD();

    // key in nodeWithDependsMap is the repostiory node and value is the dependencies of this node.
    private Map<IRepositoryNode, List<ModelElement>> nodeWithDependsMap = new HashMap<IRepositoryNode, List<ModelElement>>();

    private Object[] deleteElements = null;

    public DQDeleteAction() {
        super();
        setText(DefaultMessagesImpl.getString("DQDeleteAction.delete"));//$NON-NLS-1$
        setId(ActionFactory.DELETE.getId());
        this.selectedNodes = new ArrayList<RepositoryNode>();
        this.deleteElements = ((IStructuredSelection) this.getSelection()).toArray();

    }

    public DQDeleteAction(Object[] delElements) {
        super();
        setText(DefaultMessagesImpl.getString("DQDeleteAction.delete"));//$NON-NLS-1$
        setId(ActionFactory.DELETE.getId());
        this.selectedNodes = new ArrayList<RepositoryNode>();
        this.deleteElements = delElements;
    }

    @Override
    public ISelection getSelection() {
        ISelection selection = null;
        if (currentNode == null) {
            if (deleteElements != null) {
                selection = super.getSelection();
            } else {
                // select by UI(tree)
                DQRespositoryView findView = CorePlugin.getDefault().getRepositoryView();
                selection = findView.getCommonViewer().getSelection();
            }
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

    ISelectionProvider deletionSelProv = new ISelectionProvider() {

        public void setSelection(ISelection arg0) {
        }

        public void removeSelectionChangedListener(ISelectionChangedListener arg0) {
        }

        public ISelection getSelection() {
            IStructuredSelection structruedSelection = null;
            if (deleteElements != null) {
                structruedSelection = new StructuredSelection(deleteElements);
            }
            return structruedSelection;
        }

        public void addSelectionChangedListener(ISelectionChangedListener arg0) {
        }
    };

    @Override
    public void run() {
        repositoryObjectCRUD.refreshDQViewForRemoteProject();

        // ADD msjian TDQ-7006 2013-7-24: after refresh get the selection to check.
        if (!repositoryObjectCRUD.isSelectionAvailable()) {
            repositoryObjectCRUD.showWarningDialog();
            return;
        }
        // TDQ-7006~

        // MOD qiongli 2012-4-1 TDQ-4926.fill selectedNodes,and delete opration will base on the List.
        if (deleteElements.length == 0) {
            return;
        }
        setSpecialSelection(deletionSelProv);
        // remove the source file nodes which have been opened the editor
        deleteElements = checkSourceFilesEditorOpening(deleteElements);
        // ~ TDQ-4831

        for (Object obj : deleteElements) {
            if (obj instanceof RepositoryNode) {
                RepositoryNode node = (RepositoryNode) obj;
                selectedNodes.add(node);
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

        if (selectedNodes.size() > 0) {
            RepositoryNode firstNode = selectedNodes.get(0);
            boolean isStateDeleted = RepositoryNodeHelper.isStateDeleted(firstNode);
            // logical delete
            if (!isStateDeleted) {
                logicDelete();
            } else {
                // show a confirm dialog to make sure the user want to proceed
                if (showConfirmDialog()) {
                    // sort the selected nodes with special order: first report type, then (jrxml, analysis) type,
                    // finally
                    // (connection, DQ Rule, Pattern) type.
                    sortNodesBeforePhysicalDelete();

                    physicalDelete(deleteNodes, shownNodes, findAllRecycleBinNodes);
                }
            }
        }

        if (DQRepositoryNode.isOnFilterring()) {
            RepositoryNodeHelper.regainRecycleBinFilteredNode();
        }

        // the deleteReportFile() mothed have refresh the workspace and dqview
        refreshWorkspaceAndRecycleBenNode();
    }

    /**
     * DOC zshen Comment method "refreshWorkspaceAndRecycleBenNode".
     */
    protected void refreshWorkspaceAndRecycleBenNode() {
        CorePlugin.getDefault().refreshWorkSpace();
        CorePlugin.getDefault().refreshDQView(RepositoryNodeHelper.getRecycleBinRepNode());
    }

    /**
     * Sort the selected nodes with special order: - first report type, - then (jrxml, analysis) type, - then
     * (connection, DQ Rule, Pattern) type. - finally : the type which has no dependency,
     */
    private void sortNodesBeforePhysicalDelete() {
        List<RepositoryNode> anaOrJrxml = new ArrayList<RepositoryNode>();
        // in final level, means which need to be ordered at the end of the list
        List<RepositoryNode> finalLevel = new ArrayList<RepositoryNode>();
        List<RepositoryNode> reportNodes = new ArrayList<RepositoryNode>();

        for (RepositoryNode node : selectedNodes) {
            if (isReport(node)) {
                reportNodes.add(node);
            } else if (isAna(node) || isJrxml(node)) {
                anaOrJrxml.add(node);
            } else {
                finalLevel.add(node);
            }
        }
        selectedNodes.clear();
        selectedNodes.addAll(finalLevel);
        selectedNodes.addAll(anaOrJrxml);
        selectedNodes.addAll(reportNodes);

    }

    /**
     * Judge if the node is an analysis or NOT
     * 
     * @param node
     * @return true when the node is an analysis
     */
    private boolean isAna(RepositoryNode node) {
        if (node.getObject() != null) {
            return ERepositoryObjectType.TDQ_ANALYSIS_ELEMENT.equals(node.getObject().getRepositoryObjectType());
        }
        return false;
    }

    /**
     * Judge if the node is a report or NOT
     * 
     * @param node
     * @return true when the node is a report
     */
    private boolean isReport(RepositoryNode node) {
        if (node.getObject() != null) {
            return ERepositoryObjectType.TDQ_REPORT_ELEMENT.equals(node.getObject().getRepositoryObjectType());
        }
        return false;
    }

    /**
     * physical Delete all selected nodes, if the node has dependency, will popup a confirm dialog with lists of
     * dependencies
     * 
     * @param deleteNodes
     * @param shownNodes
     * @param findAllRecycleBinNodes
     * @param isStateDeleted
     */
    private void physicalDelete(List deleteNodes, List<IRepositoryNode> shownNodes, List<IRepositoryNode> findAllRecycleBinNodes) {
        // when physical deleting object with dependencies, do not popup
        // confirm anymore. and after dealing with it, store back to its default value.
        confirmForDQ = true;
        List<IRepositoryNode> folderNodeWhichChildHadDepend = null;

        for (int i = selectedNodes.size() - 1; i >= 0; i--) {
            if (selectedNodes.size() == 0) {
                break;
            }
            RepositoryNode node = selectedNodes.get(i);
            RepositoryNode parent = node.getParent();

            // MOD gdbu 2011-11-30 TDQ-4090 : Determine whether there has some nodes not been shown when filtering.
            if (DQRepositoryNode.isOnFilterring()) {
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

            // -- When the node has no depends, delete it directly
            // -- when the node has depends, add it with depends list to the nodeWithDependsMap
            if (node.getType() == ENodeType.SIMPLE_FOLDER || node.getType() == ENodeType.SYSTEM_FOLDER) {
                List<IRepositoryNode> newLs = RepositoryNodeHelper.getRepositoryElementFromFolder(node, true);
                // if the folder have sub node(s) not be deleted, the folder should not be deleted also
                boolean haveSubNode = false;
                for (IRepositoryNode subNode : newLs) {
                    if (!hasDependencyClients(subNode)) {
                        excuteSuperRun((RepositoryNode) subNode, node);
                    } else {
                        // if the folder has some child with depends, can not delete the folder itself here
                        haveSubNode = true;
                        if (folderNodeWhichChildHadDepend == null) {
                            folderNodeWhichChildHadDepend = new ArrayList<IRepositoryNode>();
                        }
                        folderNodeWhichChildHadDepend.add(node);
                    }
                }
                if (!haveSubNode) {
                    excuteSuperRun(node, parent);
                }
            } else {
                if (!hasDependencyClients(node)) {
                    excuteSuperRun(node, parent);
                }
            }
        }
        // show all nodes with its depends in one dialog
        boolean forceDelete = false;
        if (nodeWithDependsMap.size() > 0) {
            // show all nodes in one dialog.
            forceDelete = DeleteModelElementConfirmDialog.showDialog(nodeWithDependsMap,
                    DefaultMessagesImpl.getString("DQDeleteAction.dependencyByOther"), true);//$NON-NLS-1$
            if (forceDelete) {
                Iterator iter = nodeWithDependsMap.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry<IRepositoryNode, List<ModelElement>> entry = (Map.Entry<IRepositoryNode, List<ModelElement>>) iter
                            .next();
                    IRepositoryNode node = entry.getKey();
                    List<ModelElement> dependencies = entry.getValue();

                    excuteSuperRun((RepositoryNode) node, node.getParent());
                    physicalDeleteDependencies(dependencies);
                }
            }
        }
        nodeWithDependsMap.clear();

        // if the folder has the child who has depends, can only proceeding after its child be handled
        if (folderNodeWhichChildHadDepend != null && folderNodeWhichChildHadDepend.size() > 0) {
            if (forceDelete) {
                for (IRepositoryNode folder : folderNodeWhichChildHadDepend) {
                    excuteSuperRun((RepositoryNode) folder, folder.getParent());
                }
            }
        }

        // Added 20130227 TDQ-6901 yyin, when physical deleting object with dependencies, do not popup
        // confirm anymore. and after dealing with it, store back to its default value.
        confirmForDQ = false;
    }

    /**
     * Judge if the node has dependencies. and if the node has depends, add the depends with this node into map
     * 
     * Because find the depends should only be executed once in this delete action, so if the node has some depends,
     * will store it in a map here.
     * 
     * @param subNode
     * @return
     */
    private boolean hasDependencyClients(IRepositoryNode node) {
        if (isJrxml(node)) {
            List<ModelElement> dependedReport = DQDeleteHelper.getDependedReportOfJrxml(node);
            if (dependedReport.size() > 0) {
                nodeWithDependsMap.put(node, dependedReport);
                return true;
            } else {
                return false;
            }
        } else {
            boolean hasDependencyClients = RepositoryNodeHelper.hasDependencyClients(node);
            if (hasDependencyClients) {
                List<ModelElement> dependencies = EObjectHelper.getDependencyClients(node);
                nodeWithDependsMap.put(node, dependencies);
            }
            return hasDependencyClients;
        }
    }

    /**
     * logical delete the selected nodes
     * 
     */
    private void logicDelete() {
        // TMDM-6549,handle delete event for match rule object
        if (!handleDeleteEvent()) {
            return;
        }
        //
        for (int i = selectedNodes.size() - 1; i >= 0; i--) {
            RepositoryNode node = selectedNodes.get(i);
            // handle generating report file.bug 18805 .
            if (node instanceof ReportFileRepNode) {
                try {
                    deleteReportFile((ReportFileRepNode) node);
                } catch (PersistenceException e) {
                    log.error(e, e);
                }
                continue;
            }
        }

        RepositoryNode parent = selectedNodes.get(0).getParent();
        // only need to run one time, because in the super.run() can handle all selected node.
        excuteSuperRun(null, parent);
    }

    /**
     * TMDM-6549,Check and collect match rule objects from deleted objects.
     * 
     * @param node
     * @param matchRules
     */
    private void collectSelectedMatchRuleObjs(IRepositoryNode node, List<IRepositoryViewObject> matchRules) {
        IRepositoryViewObject viewObj = node.getObject();
        if (viewObj == null) {
            return; // For TDQ-8341, the generated DQ report file has no view object.
        }
        if (viewObj instanceof Folder) {
            for (IRepositoryNode childNode : node.getChildren()) {
                collectSelectedMatchRuleObjs(childNode, matchRules);
            }
        } else if (viewObj.getRepositoryObjectType() == ERepositoryObjectType.TDQ_RULES_MATCHER) {
            matchRules.add(viewObj);
        }
    }

    /**
     * TMDM-6549,handle the logic delete event, which only works for MDM and forcus match rule object, before deleting
     * remove all references from MDM data model object
     * 
     * @return
     */
    private boolean handleDeleteEvent() {
        List<IRepositoryViewObject> matchRules = new LinkedList<IRepositoryViewObject>();
        for (RepositoryNode node : selectedNodes) {
            collectSelectedMatchRuleObjs(node, matchRules);
        }

        if (!matchRules.isEmpty()) {
            IMatchRuleChangeService changeService = getMatchRuleChangeService();
            if (changeService == null) {
                return true;
            } else {
                boolean isOk = changeService.objectChange(null, matchRules, null,
                        IMatchRuleChangeService.ChangeEvent.BEFORE_DELETE);
                return isOk;
            }
        }
        return true;
    }

    /**
     * TMDM-6549, get registerd IMatchRuleChangeService instance
     * 
     * @return IMatchRuleChangeService instance
     */
    private IMatchRuleChangeService getMatchRuleChangeService() {
        if (GlobalServiceRegister.getDefault().isServiceRegistered(IMatchRuleChangeService.class)) {
            IMatchRuleChangeService service = (IMatchRuleChangeService) GlobalServiceRegister.getDefault().getService(
                    IMatchRuleChangeService.class);
            return service;
        }
        return null;
    }

    /**
     * DOC yyin Comment method "isJrxml".
     * 
     * @param node
     * @return
     */
    private boolean isJrxml(IRepositoryNode node) {
        if (node.getObject() != null) {
            return ERepositoryObjectType.TDQ_JRAXML_ELEMENT.equals(node.getObject().getRepositoryObjectType());
        }
        return false;
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
                logicDeleteDependeny(tempNode);
                // physical delete dependcy element.
                tempNode = RepositoryNodeHelper.recursiveFindRecycleBin(mod);
                if (tempNode != null) {
                    excuteSuperRun(tempNode, tempNode.getParent());
                    IFile propertyFile = PropertyHelper.getPropertyFile(mod);
                    if (propertyFile != null && propertyFile.exists()) {
                        isSucceed = false;
                    }
                }
            }
        } catch (Exception exc) {
            log.error(exc, exc);
            return false;
        }
        return isSucceed;
    }

    private void logicDeleteDependeny(RepositoryNode tempNode) {
        if (tempNode.getObject().getProperty() == null) {
            return;
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
        refreshParentNode(parent);
    }

    /**
     * refresh Parent Node.
     * 
     * @param parent
     */
    private void refreshParentNode(RepositoryNode parent) {
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

    protected boolean showConfirmDialog() {
        return MessageDialog.openConfirm(null, DefaultMessagesImpl.getString("DQDeleteAction.deleteForeverTitle"), //$NON-NLS-1$
                PluginConstant.SPACE_STRING + DefaultMessagesImpl.getString("DQDeleteAction.areYouDeleteForever"));//$NON-NLS-1$
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
            if (obj instanceof SourceFileRepNode || obj instanceof JrxmlTempleteRepNode) {
                ReturnCode rc = WorkspaceResourceHelper.checkSourceFileNodeOpening((RepositoryNode) obj);
                if (rc.isOk()) {
                    opened = rc.isOk();
                    openSourceFileNames += rc.getMessage();
                } else {
                    list.add(obj);
                }
            } else if (obj instanceof SourceFileSubFolderNode || obj instanceof JrxmlTempSubFolderNode) {
                ReturnCode rc = WorkspaceResourceHelper.checkSourceFileSubFolderNodeOpening((RepositoryNode) obj);
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

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.repository.ui.actions.DeleteAction#synchUI(org.talend.repository.ui.actions.DeleteActionCache)
     */
    @Override
    protected void synchUI(DeleteActionCache deleteActionCache) {
        super.synchUI(deleteActionCache);
    }

}
