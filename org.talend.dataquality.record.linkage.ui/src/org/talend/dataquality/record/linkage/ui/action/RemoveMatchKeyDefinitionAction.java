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
package org.talend.dataquality.record.linkage.ui.action;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.talend.dataquality.record.linkage.ui.composite.tableviewer.AbstractMatchAnalysisTableViewer;
import org.talend.dataquality.rules.KeyDefinition;

/**
 * created by zshen on Aug 2, 2013 Detailled comment
 * 
 */
public class RemoveMatchKeyDefinitionAction extends Action {

    private AbstractMatchAnalysisTableViewer tableViewer = null;

    public RemoveMatchKeyDefinitionAction(AbstractMatchAnalysisTableViewer tableViewer) {
        // TODO zshen externalize the string below
        setText("Delete");
        this.tableViewer = tableViewer;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.action.Action#run()
     */
    @Override
    public void run() {
        IStructuredSelection structuredSelection = (IStructuredSelection) tableViewer.getSelection();
        List<?> selections = structuredSelection.toList();
        for (Object selection : selections) {
            KeyDefinition keyDef = (KeyDefinition) selection;
            // The analysis parameter can be null when remove element from match key table, because the table viewer
            // kept
            // the reference of match rule already.
            tableViewer.removeElement(keyDef.getColumn(), null);
            // TODO zshen notify editor dirty.
        }
    }

}
