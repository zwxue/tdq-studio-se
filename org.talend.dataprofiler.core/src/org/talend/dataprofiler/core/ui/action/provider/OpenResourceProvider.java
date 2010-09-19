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
package org.talend.dataprofiler.core.ui.action.provider;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.actions.OpenFileAction;
import org.eclipse.ui.navigator.ICommonActionConstants;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;
import org.eclipse.ui.navigator.ICommonMenuConstants;
import org.eclipse.ui.navigator.ICommonViewerWorkbenchSite;

/**
 * DOC rli class global comment. Detailled comment
 */
public class OpenResourceProvider extends AbstractCommonActionProvider {

    private OpenFileAction openFileAction;

    private ICommonViewerWorkbenchSite viewSite = null;

    private boolean contribute = false;

    public void init(ICommonActionExtensionSite aConfig) {
        if (aConfig.getViewSite() instanceof ICommonViewerWorkbenchSite) {
            viewSite = (ICommonViewerWorkbenchSite) aConfig.getViewSite();
            openFileAction = new OpenFileAction(viewSite.getPage());
            contribute = true;
        }
    }

    public void fillContextMenu(IMenuManager aMenu) {

        if (!contribute || getContext().getSelection().isEmpty()) {
            return;
        }

        IStructuredSelection selection = (IStructuredSelection) getContext().getSelection();

        openFileAction.selectionChanged(selection);
        if (openFileAction.isEnabled()) {
            aMenu.insertAfter(ICommonMenuConstants.GROUP_OPEN, openFileAction);
        }
    }

    public void fillActionBars(IActionBars theActionBars) {
        if (!contribute) {
            return;
        }
        IStructuredSelection selection = (IStructuredSelection) getContext().getSelection();
        if (selection.size() == 1 && selection.getFirstElement() instanceof IFile) {
            openFileAction.selectionChanged(selection);
            theActionBars.setGlobalActionHandler(ICommonActionConstants.OPEN, openFileAction);
        }

    }

}
