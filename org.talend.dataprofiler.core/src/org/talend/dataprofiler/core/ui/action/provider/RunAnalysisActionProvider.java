// ============================================================================
//
// Copyright (C) 2006-2007 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.action.provider;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;
import org.eclipse.ui.navigator.ICommonViewerWorkbenchSite;
import org.talend.dataprofiler.core.ui.action.actions.RunAnalysisAction;

/**
 * DOC rli class global comment. Detailled comment
 */
public class RunAnalysisActionProvider extends CommonActionProvider {

    private RunAnalysisAction runAnalysisAction;

    private IFile currentSelection;

    private TreeViewer treeViewer;

    public RunAnalysisActionProvider() {
    }

    public void init(ICommonActionExtensionSite anExtensionSite) {

        if (anExtensionSite.getViewSite() instanceof ICommonViewerWorkbenchSite) {
            ISelectionProvider selectionProvider = anExtensionSite.getViewSite().getSelectionProvider();
            if (selectionProvider instanceof TreeViewer) {
                treeViewer = (TreeViewer) selectionProvider;

            }
            runAnalysisAction = new RunAnalysisAction();
            runAnalysisAction.setTreeViewer(treeViewer);
        }
    }

    /**
     * Adds a submenu to the given menu with the name "New Component".
     */
    public void fillContextMenu(IMenuManager menu) {
        Object obj = ((TreeSelection) this.getContext().getSelection()).getFirstElement();
        if (obj instanceof IFile) {
            currentSelection = (IFile) obj;
        }
        runAnalysisAction.setCurrentSelection(currentSelection);
        menu.add(runAnalysisAction);
    }

}
