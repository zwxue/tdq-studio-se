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
package org.talend.dataprofiler.core.ui.action;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;
import org.eclipse.ui.navigator.ICommonViewerWorkbenchSite;

/**
 * DOC xqliu class global comment. Detailled comment
 */
public abstract class AbstractPredefinedTableActionProvider extends CommonActionProvider {

    private AbstractPredefinedTableAnalysisAction action;

    @Override
    public void init(ICommonActionExtensionSite site) {

        if (site.getViewSite() instanceof ICommonViewerWorkbenchSite) {
            action = getAction();
        }
    }

    @Override
    public void fillContextMenu(IMenuManager menu) {
        TreeSelection currentSelection = ((TreeSelection) this.getContext().getSelection());
        action.setSelection(currentSelection);

        if (action.isAllowed()) {
            menu.add(action);
        }
    }

    protected abstract AbstractPredefinedTableAnalysisAction getAction();

}
