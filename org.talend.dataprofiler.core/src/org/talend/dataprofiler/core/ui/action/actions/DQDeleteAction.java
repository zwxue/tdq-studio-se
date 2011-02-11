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

import java.util.List;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.actions.ActionFactory;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.dialog.message.DeleteModelElementConfirmDialog;
import org.talend.dataprofiler.core.ui.views.DQRespositoryView;
import org.talend.dq.helper.EObjectHelper;
import org.talend.dq.helper.RepositoryNodeHelper;
import org.talend.repository.model.RepositoryNode;
import org.talend.repository.ui.actions.DeleteAction;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC qiongli class global comment. Detailled comment
 */
public class DQDeleteAction extends DeleteAction {

    public DQDeleteAction() {
        super();
        setText("Delete");
        setId(ActionFactory.DELETE.getId());
        // setImageDescriptor(ImageLib.getImageDescriptor(ImageLib.DELETE_ACTION));

    }


    @Override
    public ISelection getSelection() {
        DQRespositoryView findView = CorePlugin.getDefault().getRepositoryView();
        ISelection selection = findView.getCommonViewer().getSelection();
        return selection;
    }

    // @Override
    // protected void doRun() {
    // super.doRun();
    //
    // CorePlugin.getDefault().refreshDQView();
    // CorePlugin.getDefault().refreshWorkSpace();
    // }

    @Override
    public void init(TreeViewer viewer, IStructuredSelection selection) {

    }


    @Override
    public void run() {
        boolean haveDependencies = false;
        ISelection selection = this.getSelection();
        for (Object obj : ((IStructuredSelection) selection).toArray()) {
            if (obj instanceof RepositoryNode) {
                RepositoryNode node = (RepositoryNode) obj;
                List<ModelElement> dependencies = EObjectHelper.getDependencyClients(node);
                if (dependencies != null && !dependencies.isEmpty()) {
                    haveDependencies = true;
                    ModelElement modEle = RepositoryNodeHelper.getModelElementFromRepositoryNode(node);
                    showDependenciesDialog(modEle, dependencies);
                    break;
                }
            }
        }
        if (haveDependencies) {
            return;
        }
        super.run();
        CorePlugin.getDefault().refreshDQView();
        CorePlugin.getDefault().refreshWorkSpace();
    }

    /**
     * DOC bZhou Comment method "showDependenciesDialog".
     * 
     * @param file
     * @param dependencies
     */
    private void showDependenciesDialog(ModelElement node, List<ModelElement> dependencies) {
        ModelElement[] dependencyElements = dependencies.toArray(new ModelElement[dependencies.size()]);
        DeleteModelElementConfirmDialog.showDialog(null, node, dependencyElements,
                DefaultMessagesImpl.getString("LogicalDeleteFileHandle.dependencyByOther"));
    }

}
