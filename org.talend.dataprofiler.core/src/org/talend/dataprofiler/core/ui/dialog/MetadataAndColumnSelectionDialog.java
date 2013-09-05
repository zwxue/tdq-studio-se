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
package org.talend.dataprofiler.core.ui.dialog;

import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.ui.editor.analysis.AbstractAnalysisMetadataPage;
import org.talend.dq.helper.RepositoryNodeHelper;
import org.talend.dq.nodes.DBConnectionFolderRepNode;
import org.talend.dq.nodes.DBTableFolderRepNode;
import org.talend.dq.nodes.DBTableRepNode;
import org.talend.dq.nodes.DBViewFolderRepNode;
import org.talend.dq.nodes.DBViewRepNode;
import org.talend.dq.nodes.DFConnectionFolderRepNode;
import org.talend.dq.nodes.DFTableRepNode;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.RepositoryNode;


/**
 * DOC yyin  class global comment. Detailled comment
 */
public class MetadataAndColumnSelectionDialog extends ColumnsSelectionDialog {

    private String selectedElementNames = PluginConstant.EMPTY_STRING;

    public MetadataAndColumnSelectionDialog(AbstractAnalysisMetadataPage metadataFormPage, Shell parent, String message,
            List<? extends IRepositoryNode> checkedRepoNodes) {
        super(metadataFormPage, parent, message, checkedRepoNodes);
    }

    /**
     * MetadataAndColumnSelectionDialog constructor: the last parameter:false means no need to:addConnFilterListener, in
     * the super class.
     *
     * @param parent
     */
    public MetadataAndColumnSelectionDialog(Shell parent, String title, List<IRepositoryNode> checkedRepoNodes) {
        super(null, parent, title, checkedRepoNodes, false);
        // set the root of the tree, must use the RepositoryNode type.
        setInput(RepositoryNodeHelper.getRootNode(ERepositoryObjectType.METADATA, true));// ResourceManager.getMetadataFolder());
    }

    /**
     * when the user select the columns in more than one table. should make the OK status: not ok.
     */
    @Override
    protected void handleTableElementsChecked(RepositoryNode reposNode, Boolean checkedFlag) {
        super.handleTableElementsChecked(reposNode, checkedFlag);
        updateStatusBySelection();
        updateCheckedElementNames();
    }

    private void updateStatusBySelection() {
        Status fCurrStatus;
        // the table node all stored in the map as key, so when the key's number >1, means there are more than one
        // table's column selected. then make the ok status disable
        if (super.modelElementCheckedMap.keySet().size() > 1) {
            fCurrStatus = new Status(IStatus.ERROR, PlatformUI.PLUGIN_ID, IStatus.OK, PluginConstant.EMPTY_STRING, null);
        } else {
            fCurrStatus = new Status(IStatus.OK, PlatformUI.PLUGIN_ID, IStatus.OK, PluginConstant.EMPTY_STRING, null);
        }
        updateStatus(fCurrStatus);
    }

    @Override
    protected void handleTreeElementsChecked(RepositoryNode repNode, Boolean checkedFlag) {
        super.handleTreeElementsChecked(repNode, checkedFlag);
        updateStatusBySelection();
        updateCheckedElementNames();
    }

    // no need to create "select All " buttons
    @Override
    protected Composite createSelectionButtons(Composite composite) {
        Composite buttonComposite = new Composite(composite, SWT.RIGHT);
        return buttonComposite;
    }

    /**
     *
     * update the all selected emlements name in the left tree and split them with "/".
     *
     * @return
     */
    private void updateCheckedElementNames() {
        StringBuffer strBuf = new StringBuffer();
        Object[] checkedElements = super.getTreeViewer().getCheckedElements();
        for (Object obj : checkedElements) {
            if (obj instanceof DBTableFolderRepNode || obj instanceof DBViewFolderRepNode
                    || obj instanceof DBConnectionFolderRepNode || obj instanceof DFConnectionFolderRepNode) {
                continue;
            }
            if (obj instanceof RepositoryNode) {
                strBuf.append(((RepositoryNode) obj).getLabel());
                if (!(obj instanceof DBTableRepNode || obj instanceof DFTableRepNode || obj instanceof DBViewRepNode)) {
                    strBuf.append("/"); //$NON-NLS-1$
                }
            }
        }
        selectedElementNames = strBuf.toString();
    }

    public String getSelectedElementNames() {
        return this.selectedElementNames;
    }

}
