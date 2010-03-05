// ============================================================================
//
// Copyright (C) 2006-2010 Talend Inc. - www.talend.com
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
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TreeSelection;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.sql.DeleteSqlFileAction;
import org.talend.dataprofiler.core.ui.action.provider.NewSourceFileActionProvider;
import org.talend.dataprofiler.core.ui.views.DQRespositoryView;
import org.talend.top.repository.ProxyRepositoryManager;

/**
 * DOC rli class global comment. Detailled comment
 */
public class DeleteObjectsAction extends Action {

    /**
     * DOC rli DeleteObjectAction constructor comment.
     */
    public DeleteObjectsAction() {
        this.setActionDefinitionId("org.talend.dataprofiler.core.removeAnalysis"); //$NON-NLS-1$
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.action.Action#run()
     */
    @Override
    public void run() {
        removeSQLFile();
        removeAnalysis();
        removeCWMResource();
        ProxyRepositoryManager.getInstance().save();
    }

    private void removeAnalysis() {
        new RemoveAnalysisAction().run();
    }

    /**
     * DOC qzhang Comment method "removeSQLFile".
     */
    private boolean removeSQLFile() {
        DQRespositoryView findView = CorePlugin.getDefault().getRepositoryView();
        TreeSelection treeSelection = (TreeSelection) findView.getCommonViewer().getSelection();
        List<IFile> selectedFiles = new ArrayList<IFile>();
        boolean isSelectFile = NewSourceFileActionProvider.computeSelectedFiles(treeSelection, selectedFiles);
        if (!isSelectFile && !selectedFiles.isEmpty()) {
            new DeleteSqlFileAction(selectedFiles).run();
            return true;
        }
        return false;
    }

    private void removeCWMResource() {
        new DeleteCWMResourceAction(getSelectedResourcesArray()).run();
    }

    /**
     * Return an array of the currently selected resources.
     * 
     * @return the selected resources
     */
    @SuppressWarnings("unchecked")
    private IFile[] getSelectedResourcesArray() {
        DQRespositoryView findView = CorePlugin.getDefault().getRepositoryView();
        TreeSelection treeSelection = (TreeSelection) findView.getCommonViewer().getSelection();
        List<IFile> selectedFiles = new ArrayList<IFile>();
        Iterator iterator = treeSelection.iterator();
        while (iterator.hasNext()) {
            Object obj = iterator.next();
            if (obj instanceof IFile) {
                IFile file = (IFile) obj;
                selectedFiles.add(file);
            } else {
                return new IFile[0];
            }
        }
        return selectedFiles.toArray(new IFile[selectedFiles.size()]);
    }
}
