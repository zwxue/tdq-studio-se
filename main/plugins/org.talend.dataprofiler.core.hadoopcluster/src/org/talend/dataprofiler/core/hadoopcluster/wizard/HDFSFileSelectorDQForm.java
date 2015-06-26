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
package org.talend.dataprofiler.core.hadoopcluster.wizard;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeItem;
import org.talend.core.model.properties.ConnectionItem;
import org.talend.designer.hdfsbrowse.model.EHadoopFileTypes;
import org.talend.designer.hdfsbrowse.model.IHDFSNode;
import org.talend.repository.hdfs.ui.HDFSFileSelectorForm;
import org.talend.repository.model.hdfs.HDFSConnection;

/**
 * Mainly Purpose: use a list to temp remember the selected nodes, and judge: if the selected nodes are files, and all
 * belongs to the same folder, the status is OK, otherwise is NOT.
 *
 */
public class HDFSFileSelectorDQForm extends HDFSFileSelectorForm {

    // Used to store selected nodes on the tree
    private List<IHDFSNode> selectedNodes;

    private final WizardPage parentPage;

    public HDFSFileSelectorDQForm(Composite parent, ConnectionItem connectionItem, HDFSConnection temConnection,
            WizardPage parentWizardPage) {
        super(parent, connectionItem, temConnection, parentWizardPage);
        parentPage = parentWizardPage;
        selectedNodes = new ArrayList<IHDFSNode>();
    }

    @Override
    public void performCancel() {
        super.performCancel();
        selectedNodes.clear();
    }

    @Override
    protected void refreshTable(TreeItem treeItem, int size) {
        super.refreshTable(treeItem, size);
        IHDFSNode node = (IHDFSNode) treeItem.getData();
        // only file type need to be remembered
        if (node.getType() == EHadoopFileTypes.FILE) {
            selectedNodes.add(node);
        }
    }

    @Override
    protected void deleteTable(IHDFSNode node) {
        super.deleteTable(node);
        selectedNodes.remove(node);
        parentPage.setPageComplete(true);
    }

    public boolean isStatusOk() {
        if (selectedNodes.size() == 0) {
            return false;
        }
        IHDFSNode parentNode = selectedNodes.get(0).getParent();
        for (IHDFSNode node : selectedNodes) {
            if (parentNode.equals(node.getParent())) {
                continue;
            } else {
                return false;
            }
        }
        return true;
    }

}
