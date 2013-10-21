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
package org.talend.dataprofiler.core.ui.action.provider;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;
import org.eclipse.ui.navigator.ICommonViewerWorkbenchSite;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.action.actions.MatchAnalysisAction;
import org.talend.dq.nodes.DBTableRepNode;
import org.talend.repository.model.RepositoryNode;

public class MatchAnalysisActionProvider extends AbstractCommonActionProvider {

    private MatchAnalysisAction matchAnalysisAction;

    public MatchAnalysisActionProvider() {

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.navigator.CommonActionProvider#init(org.eclipse.ui.navigator.ICommonActionExtensionSite)
     */
    @Override
    public void init(ICommonActionExtensionSite site) {

        if (site.getViewSite() instanceof ICommonViewerWorkbenchSite) {
            matchAnalysisAction = new MatchAnalysisAction();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.actions.ActionGroup#fillContextMenu(org.eclipse.jface.action.IMenuManager)
     */
    @SuppressWarnings("unchecked")
    @Override
    public void fillContextMenu(IMenuManager menu) {
        // MOD mzhao user readonly role on svn repository mode.
        if (!isShowMenu()) {
            return;
        }

        TreeSelection currentSelection = ((TreeSelection) this.getContext().getSelection());

        matchAnalysisAction.setColumnSelection(currentSelection);
        if (isSelectedTableLevel(currentSelection)) {
            matchAnalysisAction.setText(DefaultMessagesImpl.getString("MatchAnalysisAction.matchAnalysis"));
        }else 
        if (isSelectedColumnLevel(currentSelection)) {
            matchAnalysisAction.setText(DefaultMessagesImpl.getString("MatchAnalysisAction.analyzeMatches"));
        }
        menu.add(matchAnalysisAction);
    }

    /**
     * DOC bZhou Comment method "isSelectedColumnLevel".
     * 
     * @param currentSelection
     * @return
     */
    private boolean isSelectedTableLevel(TreeSelection currentSelection) {
        if (currentSelection.size() == 1) {
            for (Object obj : currentSelection.toList()) {
                if (obj instanceof DBTableRepNode) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * DOC bZhou Comment method "isSelectedColumnLevel".
     * 
     * @param currentSelection
     * @return
     */
    private boolean isSelectedColumnLevel(TreeSelection currentSelection) {
        for (Object obj : currentSelection.toList()) {
            if (obj instanceof RepositoryNode) {
                RepositoryNode node = (RepositoryNode) obj;
                if (node.hasChildren()) {
                    return false;
                }
            } else {
                return false;
            }
        }

        return true;
    }

}
