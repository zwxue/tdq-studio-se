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
package org.talend.dataprofiler.core.ui.dialog;

import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.dataprofiler.core.ui.editor.analysis.AbstractAnalysisMetadataPage;
import org.talend.dq.helper.RepositoryNodeHelper;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.RepositoryNode;


/**
 * DOC yyin  class global comment. Detailled comment
 */
public class MetadataAndColumnSelectionDialog extends ColumnsSelectionDialog {
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
    }

    private void updateStatusBySelection() {
        Status fCurrStatus;
        // the table node all stored in the map as key, so when the key's number >1, means there are more than one
        // table's column selected. then make the ok status disable
        if (super.modelElementCheckedMap.keySet().size() > 1) {
            fCurrStatus = new Status(IStatus.ERROR, PlatformUI.PLUGIN_ID, IStatus.OK, "", null);
        } else {
            fCurrStatus = new Status(IStatus.OK, PlatformUI.PLUGIN_ID, IStatus.OK, "", null); //$NON-NLS-1$
        }
        updateStatus(fCurrStatus);
    }

    @Override
    protected void handleTreeElementsChecked(RepositoryNode repNode, Boolean checkedFlag) {
        super.handleTreeElementsChecked(repNode, checkedFlag);
        updateStatusBySelection();
    }

    // no need to create "select All " buttons
    @Override
    protected Composite createSelectionButtons(Composite composite) {
        Composite buttonComposite = new Composite(composite, SWT.RIGHT);
        return buttonComposite;
    }

}
