// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.cwm.compare.ui.views;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareViewerPane;
import org.eclipse.emf.compare.ui.export.ExportMenu;
import org.eclipse.emf.compare.ui.viewer.structure.ModelStructureMergeViewer;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.widgets.Composite;

/**
 * DOC xqliu class global comment. bug 9407 2009-10-13
 */
public class CompareModelStructureMergeViewer extends ModelStructureMergeViewer {

    /**
     * DOC xqliu CompareModelStructureMergeViewer constructor comment.
     * 
     * @param parent
     * @param compareConfiguration
     */
    public CompareModelStructureMergeViewer(Composite parent, CompareConfiguration compareConfiguration) {
        super(parent, compareConfiguration);
        final ToolBarManager tbm = CompareViewerPane.getToolBarManager(getControl().getParent());
        IContributionItem[] items = tbm.getItems();
        for (IContributionItem item : items) {
            if (item instanceof ActionContributionItem) {
                IAction action = ((ActionContributionItem) item).getAction();
                if (action instanceof ExportMenu) {
                    tbm.remove(item);
                    item.dispose();
                }
            }
        }

        tbm.update(true);
        // exportMenu.setEnabled(false);
    }

}
