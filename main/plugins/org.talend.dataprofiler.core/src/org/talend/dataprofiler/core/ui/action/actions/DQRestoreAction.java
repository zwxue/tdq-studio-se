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

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.ui.utils.RepNodeUtils;
import org.talend.dataprofiler.core.ui.views.DQRespositoryView;
import org.talend.dataprofiler.core.ui.views.resources.IRepositoryObjectCRUD;
import org.talend.dq.helper.RepositoryNodeHelper;
import org.talend.dq.nodes.DQRepositoryNode;
import org.talend.repository.ui.actions.RestoreAction;

/**
 * @author qiongli Restore recycle bin element
 */
public class DQRestoreAction extends RestoreAction {

    private static Logger log = Logger.getLogger(DQRestoreAction.class);

    private IRepositoryObjectCRUD repositoryObjectCRUD = RepNodeUtils.getRepositoryObjectCRUD();

    /**
	 * 
	 */
    public DQRestoreAction() {
        // setImageDescriptor(ImageLib.getImageDescriptor(ImageLib.ADD_ACTION));
        super();
        // setText(DefaultMessagesImpl.getString("DQRestoreAction.restore"));
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

        // MOD qiongli 2011-5-9 bug 21035,avoid to unload resource.
        super.setAvoidUnloadResources(true);
        super.run();

        CorePlugin.getDefault().refreshDQView();

        CorePlugin.getDefault().refreshWorkSpace();

        // MOD gdbu 2011-11-17 TDQ-3969 : after restore items re-filter the tree , to create a new list .
        if (DQRepositoryNode.isOnFilterring()) {
            RepositoryNodeHelper.fillTreeList(null);
            RepositoryNodeHelper
                    .setFilteredNode(RepositoryNodeHelper.getRootNode(ERepositoryObjectType.TDQ_DATA_PROFILING, true));
        }
    }

    @Override
    public void init(TreeViewer viewer, IStructuredSelection selection) {

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

}
