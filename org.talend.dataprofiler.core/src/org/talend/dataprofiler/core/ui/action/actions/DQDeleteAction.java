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

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.actions.ActionFactory;
import org.talend.cwm.helper.ResourceHelper;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.recycle.impl.RecycleBinManager;
import org.talend.dataprofiler.core.service.TDQResourceChangeHandler;
import org.talend.dataprofiler.core.ui.dialog.message.DeleteModelElementConfirmDialog;
import org.talend.dataprofiler.core.ui.views.DQRespositoryView;
import org.talend.dq.helper.EObjectHelper;
import org.talend.dq.helper.PropertyHelper;
import org.talend.dq.helper.RepositoryNodeHelper;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.IRepositoryNode.ENodeType;
import org.talend.repository.model.RepositoryNode;
import org.talend.repository.ui.actions.DeleteAction;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC qiongli class global comment. Detailled comment
 */
public class DQDeleteAction extends DeleteAction {

    private RepositoryNode currentNode = null;

    private static Logger log = Logger.getLogger(TDQResourceChangeHandler.class);

    public DQDeleteAction() {
        super();
        setText("Delete");
        setId(ActionFactory.DELETE.getId());
        // setImageDescriptor(ImageLib.getImageDescriptor(ImageLib.DELETE_ACTION));

    }


    @Override
    public ISelection getSelection() {
        ISelection selection = null;
        if (currentNode == null) {
            DQRespositoryView findView = CorePlugin.getDefault().getRepositoryView();
            selection = findView.getCommonViewer().getSelection();
        } else {
            selection = new StructuredSelection(currentNode);
        }
        return selection;
    }

    @Override
    public void init(TreeViewer viewer, IStructuredSelection selection) {

    }


    @Override
    public void run() {

        ISelection selection = this.getSelection();
        Object[] objs = ((IStructuredSelection) selection).toArray();
        for (Object obj : objs) {
            if (obj instanceof RepositoryNode) {
                RepositoryNode node = (RepositoryNode) obj;
                boolean isStateDeleted = RepositoryNodeHelper.isStateDeleted(node);
                if (!isStateDeleted) {
                    closeEditors(objs);
                    currentNode = null;
                    super.run();
                    break;

                }
                // show dependency dialog and phisical delete dependencies just for phisical deleting.
                if (node.getType() == ENodeType.SIMPLE_FOLDER) {
                    List<IRepositoryNode> newLs = new ArrayList<IRepositoryNode>();
                    findRepNodesByFolderNode(node, newLs);
                    for (IRepositoryNode subNode : newLs) {
                        if (showDependenciesDialog((RepositoryNode) subNode)) {
                            currentNode = (RepositoryNode) subNode;
                            super.run();
                        }
                    }
                } else {
                    if (showDependenciesDialog(node)) {
                        EObjectHelper.removeDependencys(RepositoryNodeHelper.getModelElementFromRepositoryNode(node));
                        currentNode = node;
                        super.run();
                    }
                }
            }
        }

        CorePlugin.getDefault().refreshDQView();
        CorePlugin.getDefault().refreshWorkSpace();
    }

    /**
     * show dependency question dialog.
     * 
     * @param file
     * @param dependencies
     */
    private boolean showDependenciesDialog(RepositoryNode node) {

        List<ModelElement> dependencies = EObjectHelper.getDependencyClients(node);
        boolean flag = true;
        if (dependencies == null || dependencies.isEmpty()) {
            return flag;
        }
        ModelElement modEle = RepositoryNodeHelper.getModelElementFromRepositoryNode(node);
        String lable = node.getObject().getLabel() == null ? PluginConstant.EMPTY_STRING : node.getObject().getLabel();
        if (DeleteModelElementConfirmDialog.showDialog(null, modEle, dependencies.toArray(new ModelElement[dependencies.size()]),
                DefaultMessagesImpl.getString("DQDeleteAction.dependencyByOther", lable))) {
            if (!physicalDeleteDependencies(dependencies)) {
                flag = false;
            }
        } else {
            flag = false;
        }
        return flag;

    }

    /**
     * 
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
                }
                if (!isSucceed) {
                    return false;
                }
                currentNode = RepositoryNodeHelper.recursiveFind(mod);
                if (currentNode != null && !RepositoryNodeHelper.isStateDeleted(currentNode)) {
                    // logcial delete dependcy element.
                    if (currentNode.getObject() != null)
                        CorePlugin.getDefault().closeEditorIfOpened(currentNode.getObject().getProperty());
                    super.run();
                    CorePlugin.getDefault().refreshDQView();
                }
                // physical delete dependcy element.
                currentNode = findRepNodeInRecBin(currentNode);
                if (currentNode != null) {
                    super.run();
                    IFile propertyFile = PropertyHelper.getPropertyFile(mod);
                    if (propertyFile != null && propertyFile.exists()) {
                        isSucceed = false;
                    } else {
                        EObjectHelper.removeDependencys(mod);
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
     * 
     * find all REPOSITORY_ELEMENT by folderNode .
     * 
     * @param node
     * @param elementNodes
     */
    private void findRepNodesByFolderNode(IRepositoryNode node, List<IRepositoryNode> elementNodes) {
        List<IRepositoryNode> children = node.getChildren();
        for (IRepositoryNode child : children) {
            if (child.getType() == ENodeType.SIMPLE_FOLDER) {
                findRepNodesByFolderNode(child, elementNodes);
            } else {
                elementNodes.add(child);
            }
        }

    }

    private RepositoryNode findRepNodeInRecBin(RepositoryNode node) {
        List<IRepositoryNode> nodeLs = RecycleBinManager.getInstance().getRecycleBinChildren();
        RepositoryNode returnNode = null;
        for (IRepositoryNode recNod : nodeLs) {
            if (ResourceHelper.getUUID(RepositoryNodeHelper.getModelElementFromRepositoryNode(recNod)).equals(
                    ResourceHelper.getUUID(RepositoryNodeHelper.getModelElementFromRepositoryNode(node)))) {
                returnNode = (RepositoryNode) recNod;
                break;
            }
        }
        return returnNode;

    }

    private void closeEditors(Object[] selections) {
        for (Object obj : selections) {
            if (obj instanceof RepositoryNode) {
                RepositoryNode node = (RepositoryNode) obj;
                if (node.getObject() != null) {
                    CorePlugin.getDefault().closeEditorIfOpened(node.getObject().getProperty());
                }
            }
        }
    }

}
