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
import org.talend.cwm.dependencies.DependenciesHandler;
import org.talend.cwm.helper.ResourceHelper;
import org.talend.dataprofiler.core.CorePlugin;
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

    private ModelElement currentDependencyModEle = null;

    private ISelection originalUISelection = null;// record UI Selection elements

    private boolean isUISelection = true;

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
        // select by UI(tree)
        if (isUISelection) {
            if (originalUISelection == null) {
                DQRespositoryView findView = CorePlugin.getDefault().getRepositoryView();
                originalUISelection = findView.getCommonViewer().getSelection();
            }
            selection = originalUISelection;

        } else if (currentDependencyModEle != null) {
            // new instance of selection for dependency modeleEelemnt.
            RepositoryNode node = RepositoryNodeHelper.recursiveFind(currentDependencyModEle);
            if (node == null) {
                node = recursiveFindFromRecycleBin(currentDependencyModEle);
            }
            selection = new StructuredSelection(node);
        }
        return selection;
    }

    @Override
    public void init(TreeViewer viewer, IStructuredSelection selection) {

    }


    @Override
    public void run() {

        ISelection selection = this.getSelection();
        for (Object obj : ((IStructuredSelection) selection).toArray()) {
            if (obj instanceof RepositoryNode) {
                RepositoryNode node = (RepositoryNode) obj;
                boolean isStateDeleted = RepositoryNodeHelper.isStateDeleted(node);
                if (!isStateDeleted) {
                    if (node.getObject() != null) {
                        CorePlugin.getDefault().closeEditorIfOpened(node.getObject().getProperty());
                    }
                    excuteSuperRun(true);
                    continue;
                }
                // show dependency dialog and phisical delete dependencies just for phisical deleting.
                if (node.getType() == ENodeType.SIMPLE_FOLDER) {
                    List<IRepositoryNode> newLs = new ArrayList<IRepositoryNode>();
                    findRepNodesByFolderNode(node, newLs);
                    for (IRepositoryNode subNode : newLs) {
                        if (showDependenciesDialog((RepositoryNode) subNode)) {
                            excuteSuperRun(true);
                        }
                    }
                } else {
                    if (showDependenciesDialog(node)) {
                        excuteSuperRun(true);
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
        if (DeleteModelElementConfirmDialog.showDialog(null, modEle, dependencies.toArray(new ModelElement[dependencies.size()]),
                DefaultMessagesImpl.getString("DQDeleteAction.dependencyByOther"))) {
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
                currentDependencyModEle = mod;
                RepositoryNode dependecyNode = RepositoryNodeHelper.recursiveFind(mod);
                if (dependecyNode != null && !RepositoryNodeHelper.isStateDeleted(dependecyNode)) {
                    // logcial delete dependcy element.
                    excuteSuperRun(false);
                    CorePlugin.getDefault().closeEditorIfOpened(dependecyNode.getObject().getProperty());
                    CorePlugin.getDefault().refreshDQView();
                }
                // physical delete dependcy element.
                excuteSuperRun(false);
                IFile propertyFile = PropertyHelper.getPropertyFile(mod);
                if (propertyFile != null && propertyFile.exists()) {
                    isSucceed = false;
                } else {
                    DependenciesHandler.getInstance().clearDependencies(mod);
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

    private void excuteSuperRun(boolean isUISelection) {
        this.isUISelection = isUISelection;
        super.run();
    }

    /**
     * 
     * find a RepositoryNode from RecycleBin.
     * 
     * @return
     */
    private RepositoryNode recursiveFindFromRecycleBin(ModelElement modelElement) {
        RepositoryNode node = null;
        List<IRepositoryNode> recylebinChildren = RecycleBinManager.getInstance().getRecycleBinChildren();
        for (IRepositoryNode recNode : recylebinChildren) {
            if (ResourceHelper.getUUID(modelElement).equals(
                    ResourceHelper.getUUID(RepositoryNodeHelper.getModelElementFromRepositoryNode(recNode)))) {
                node = (RepositoryNode) recNode;
                break;
            }
        }
        return node;
    }

}
