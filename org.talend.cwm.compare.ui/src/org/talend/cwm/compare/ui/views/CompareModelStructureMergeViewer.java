// ============================================================================
//
// Copyright (C) 2006-2009 Talend Inc. - www.talend.com
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
import org.eclipse.emf.compare.ui.viewer.structure.ModelStructureMergeViewer;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.events.DisposeEvent;
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
        // TODO Auto-generated constructor stub
    }

    protected void createToolItems() {
        final ToolBarManager tbm = CompareViewerPane.getToolBarManager(getControl().getParent());
        tbm.add(new Separator("IO")); //$NON-NLS-1$
        tbm.update(true);
    }

    protected void handleDispose(DisposeEvent event) {
        super.handleDispose(event);
    }

    protected void updateToolItems() {
        CompareViewerPane.getToolBarManager(getControl().getParent()).update(true);
    }
}
