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

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.actions.ActionFactory;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.dialog.message.DeleteModelElementConfirmDialog;
import org.talend.dataprofiler.core.ui.views.DQRespositoryView;
import org.talend.dq.helper.DQDeleteHelper;
import org.talend.dq.helper.EObjectHelper;
import org.talend.dq.helper.PropertyHelper;
import org.talend.dq.helper.RepositoryNodeHelper;
import org.talend.dq.nodes.DQRepositoryNode;
import org.talend.dq.nodes.ReportFileRepNode;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.IRepositoryNode.ENodeType;
import org.talend.repository.model.RepositoryNode;
import org.talend.repository.ui.actions.DeleteAction;
import org.talend.resource.ResourceManager;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC qiongli class global comment. Detailled comment
 */
public class DQDeleteAction extends DeleteAction {

    private RepositoryNode currentNode = null;

    private static Logger log = Logger.getLogger(DQDeleteAction.class);

    public DQDeleteAction() {
        super();
        setText(DefaultMessagesImpl.getString("DQDeleteAction.delete"));//$NON-NLS-1$
        setId(ActionFactory.DELETE.getId());
        // setImageDescriptor(ImageLib.getImageDescriptor(ImageLib.DELETE_ACTION));

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
        ISelection selection = this.getSelection();
        // boolean onlyDeleteReportFile = true;
        // MOD gdbu 2011-11-17 TDQ-3969 : when delete elements also need delete this element in filter-list, and move it
        // to recycle bin node.
        Object[] deleteElements = ((IStructuredSelection) selection).toArray();
        if (DQRepositoryNode.isOnFilterring() && deleteElements.length != 0) {
            if (deleteElements[0] instanceof RepositoryNode) {
                setPreviousFilteredNode((RepositoryNode) deleteElements[0]);
            }
            for (Object obj : deleteElements) {
                if (obj instanceof RepositoryNode) {
                    RepositoryNode node = (RepositoryNode) obj;
                    RepositoryNodeHelper.removeChildrenNodesWhenFiltering(node);
                }
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

        // MOD gdbu 2011-12-12 TDQ-4158 This is the legacy problem when refactoring the logic of deleted.
        HashSet<RepositoryNode> deleteElementsParents = new HashSet<RepositoryNode>();
        for (Object obj : deleteElements) {
            if (obj instanceof RepositoryNode) {
                RepositoryNode parent = ((RepositoryNode) obj).getParent();
                deleteElementsParents.add(parent);
            }
        }
        // ~TDQ-4158

        for (Object obj : deleteElements) {
            if (obj instanceof RepositoryNode) {
                RepositoryNode node = (RepositoryNode) obj;
                // handle generating report file.bug 18805 .
                if (node instanceof ReportFileRepNode) {
                    deleteReportFile((ReportFileRepNode) node);
                    continue;
                }
                // onlyDeleteReportFile = false;
                boolean isStateDeleted = RepositoryNodeHelper.isStateDeleted(node);
                // logical delete
                if (!isStateDeleted) {
                    // closeEditors(selection);
                    excuteSuperRun(null);
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
                    // is there have sub nodes not be deleted: if the folder have sub node(s) not be deleted, the folder
                    // should not be deleted also
                    boolean haveSubNode = false;
                    for (IRepositoryNode subNode : newLs) {
                        hasDependency = RepositoryNodeHelper.hasDependencyClients(subNode);
                        if (!hasDependency || hasDependency && handleDependencies(subNode)) {
                            excuteSuperRun((RepositoryNode) subNode);
                        } else {
                            haveSubNode = true;
                        }
                    }
                    if (!haveSubNode) {
                        excuteSuperRun(node);
                    }
                } else {
                    hasDependency = RepositoryNodeHelper.hasDependencyClients(node);
                    if (!hasDependency || hasDependency && handleDependencies(node)) {
                        excuteSuperRun((RepositoryNode) node);
                    }
                }
            }
        }

        for (RepositoryNode repositoryNode : deleteElementsParents) {
            CorePlugin.getDefault().refreshDQView(repositoryNode);
        }

        if (DQRepositoryNode.isOnFilterring() && 0 != deleteElements.length) {
            RepositoryNodeHelper.regainRecycleBinFilteredNode();
        }

        // the deleteReportFile() mothed have refresh the workspace and dqview
        CorePlugin.getDefault().refreshWorkSpace();
        CorePlugin.getDefault().refreshDQView(RepositoryNodeHelper.getRecycleBinRepNode());
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
                if (tempNode != null && !RepositoryNodeHelper.isStateDeleted(tempNode)) {
                    // logcial delete dependcy element.
                    if (tempNode.getObject() != null) {
                        CorePlugin.getDefault().closeEditorIfOpened(tempNode.getObject().getProperty().getItem());
                    }
                    excuteSuperRun(tempNode);
                    CorePlugin.getDefault().refreshDQView();
                }
                // physical delete dependcy element.
                tempNode = RepositoryNodeHelper.recursiveFindRecycleBin(mod);
                if (tempNode != null) {
                    excuteSuperRun(tempNode);
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

    /**
     * DOC qiongli :excute super method run().
     * 
     * @param currentNode:null for logical delete a selected element by UI.none-null for physical delete or logical
     * delete dependecy.
     */
    private void excuteSuperRun(RepositoryNode currentNode) {
        this.currentNode = currentNode;
        Item item = null;
        if (currentNode != null) {
            Property property = currentNode.getObject().getProperty();
            if (property != null) {
                item = property.getItem();
            }
        }
        // MOD qiongli 2011-5-9 bug 21035,avoid to unload resource.
        super.setAvoidUnloadResources(true);
        super.run();
        // because reuse tos codes.remove current node from its parent(simple folder) for phisical delete or logical
        // delete dependency.
        if (currentNode != null) {
            RepositoryNode parent = currentNode.getParent();
            if (parent != null
                    && (parent.getType() == ENodeType.SIMPLE_FOLDER || parent.getLabel().equalsIgnoreCase(
                            ERepositoryObjectType.RECYCLE_BIN.name().replaceAll("_", PluginConstant.SPACE_STRING)))) {//$NON-NLS-1$
                parent.getChildren(true).remove(currentNode);
            }
            // delete related output folder after physical delete a report.
            DQDeleteHelper.deleteRelations(item);
        }
    }

    /**
     * physical delete generating report file.
     * 
     * @param repFileNode
     */
    private void deleteReportFile(ReportFileRepNode repFileNode) {
        try {
            RepositoryNode parentNode = repFileNode.getParent(); // get the report node
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

                // refresh the parent node
                if (parentNode != null) {
                    CorePlugin.getDefault().refreshDQView(parentNode);
                }
            }
        } catch (CoreException e) {
            log.error(e, e);
        }
    }

    private boolean showConfirmDialog(String reportFileName) {
        return MessageDialog.openConfirm(null, DefaultMessagesImpl.getString("DQDeleteAction.deleteForeverTitle"), reportFileName//$NON-NLS-1$
                + PluginConstant.SPACE_STRING + DefaultMessagesImpl.getString("DQDeleteAction.areYouDeleteForever"));//$NON-NLS-1$
    }
}
