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

import java.io.File;
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
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.service.TDQResourceChangeHandler;
import org.talend.dataprofiler.core.ui.dialog.message.DeleteModelElementConfirmDialog;
import org.talend.dataprofiler.core.ui.utils.WorkbenchUtils;
import org.talend.dataprofiler.core.ui.views.DQRespositoryView;
import org.talend.dataquality.reports.TdReport;
import org.talend.dq.helper.EObjectHelper;
import org.talend.dq.helper.PropertyHelper;
import org.talend.dq.helper.RepositoryNodeHelper;
import org.talend.dq.helper.resourcehelper.RepResourceFileHelper;
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
    public void init(TreeViewer viewer, IStructuredSelection selection) {

    }


    @Override
    public void run() {

        ISelection selection = this.getSelection();
        for (Object obj : ((IStructuredSelection) selection).toArray()) {
            if (obj instanceof RepositoryNode) {
                RepositoryNode node = (RepositoryNode) obj;
                // handle generating report file.bug 18805 .
                if (node instanceof ReportFileRepNode) {
                    deleteReportFile((ReportFileRepNode) node);
                    continue;
                }

                boolean isStateDeleted = RepositoryNodeHelper.isStateDeleted(node);
                // logical delete
                if (!isStateDeleted) {
                    closeEditors(selection);
                    RepositoryNodeHelper.getModelElementFromRepositoryNode(node);
                    excuteSuperRun(null);
                    break;

                }
                // show dependency dialog and phisical delete dependencies just for phisical deleting.
                boolean hasDependency = false;
                if (node.getType() == ENodeType.SIMPLE_FOLDER) {
                    List<IRepositoryNode> newLs = RepositoryNodeHelper.getRepositoryElementFromFolder(node, true);
                    for (IRepositoryNode subNode : newLs) {
                        hasDependency = RepositoryNodeHelper.hasDependencyClients(subNode);
                        if (!hasDependency || hasDependency && handleDependencies(subNode)) {
                            excuteSuperRun((RepositoryNode) subNode);
                        }
                    }
                    excuteSuperRun(node);
                } else {
                    hasDependency = RepositoryNodeHelper.hasDependencyClients(node);
                    if (!hasDependency || hasDependency && handleDependencies(node)) {
                        ModelElement modelEle = RepositoryNodeHelper.getModelElementFromRepositoryNode(node);
                        EObjectHelper.removeDependencys(modelEle);
                        // clear the memory variable of TdReport.(bug 19179)
                        if ( modelEle instanceof TdReport) {
                            IFile file = ResourceManager.getReportsFolder().getFile(WorkbenchUtils.getFilePath(node));
                            if (file != null) {
                              RepResourceFileHelper.getInstance().remove(file);
                            }
                        }
                        excuteSuperRun((RepositoryNode) node);
                    }
                }
            }
        }

        CorePlugin.getDefault().refreshDQView();
        CorePlugin.getDefault().refreshWorkSpace();
    }

    private boolean handleDependencies(IRepositoryNode node) {
        boolean flag = false;
        List<ModelElement> dependencies = EObjectHelper.getDependencyClients(node);
        if (showDialog(node, dependencies)) {
            if (physicalDeleteDependencies(dependencies)) {
                flag = true;
            } else {
                MessageDialog.openError(null, DefaultMessagesImpl.getString("DQDeleteAction.deleteFailTitle"),
                        DefaultMessagesImpl.getString("DQDeleteAction.deleteFailMessage"));
            }
        }
        return flag;
    }

    /**
     * 
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
                DefaultMessagesImpl.getString("DQDeleteAction.dependencyByOther", lable), true);
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

    private void closeEditors(ISelection selection) {
        Object[] objs = ((IStructuredSelection) selection).toArray();
        for (Object obj : objs) {
            if (obj instanceof RepositoryNode) {
                RepositoryNode node = (RepositoryNode) obj;
                if (node.getObject() != null) {
                    CorePlugin.getDefault().closeEditorIfOpened(node.getObject().getProperty().getItem());
                }
            }
        }
    }

    private void excuteSuperRun(RepositoryNode currentNode) {
        this.currentNode = currentNode;
        super.run();
        // because reuse tos codes.remove current node from its parent(simple folder) for phisical delete or logical
        // delete dependency.
        if (currentNode != null) {
            RepositoryNode parent = currentNode.getParent();
            if (parent != null
                    && (parent.getType() == ENodeType.SIMPLE_FOLDER || parent.getLabel().equalsIgnoreCase(
                            ERepositoryObjectType.RECYCLE_BIN.name().replaceAll("_", " ")))) {
                parent.getChildren(true).remove(currentNode);
            }
        }
    }

    /**
     * 
     * physical delete generating report file.
     * 
     * @param repFileNode
     */
    private void deleteReportFile(ReportFileRepNode repFileNode) {
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
            }
        } catch (CoreException e) {
            log.error(e, e);
        }
    }

    private boolean showConfirmDialog(String reportFileName) {
        return MessageDialog.openConfirm(null, DefaultMessagesImpl.getString("DQDeleteAction.deleteForeverTitle"), reportFileName
                + PluginConstant.SPACE_STRING + DefaultMessagesImpl.getString("DQDeleteAction.areYouDeleteForever"));
    }

}
