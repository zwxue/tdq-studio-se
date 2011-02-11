// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.editor;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.navigator.ILinkHelper;
import org.talend.core.model.properties.ConnectionItem;
import org.talend.core.model.properties.Item;
import org.talend.dataprofiler.core.ui.action.actions.OpenItemEditorAction;
import org.talend.dataquality.properties.TDQAnalysisItem;
import org.talend.dataquality.properties.TDQIndicatorDefinitionItem;
import org.talend.dataquality.properties.TDQPatternItem;
import org.talend.dataquality.properties.TDQReportItem;
import org.talend.dq.helper.RepositoryNodeHelper;
import org.talend.repository.model.RepositoryNode;

/**
 * DOC mzhao DQ editor link helper.
 */
public class DQEditorLinkHelper implements ILinkHelper {

    public IStructuredSelection findSelection(IEditorInput editorInput) {
        if (editorInput instanceof AbstractItemEditorInput) {
            Item item = ((AbstractItemEditorInput) editorInput).getItem();
            RepositoryNode node = null;
            if (item instanceof TDQAnalysisItem) {
                node = RepositoryNodeHelper.recursiveFind(((TDQAnalysisItem) item).getAnalysis());
            } else if (item instanceof TDQReportItem) {
                node = RepositoryNodeHelper.recursiveFind(((TDQReportItem) item).getReport());
            } else if (item instanceof ConnectionItem) {
                node = RepositoryNodeHelper.recursiveFind(((ConnectionItem) item).getConnection());
            } else if (item instanceof TDQPatternItem) {
                node = RepositoryNodeHelper.recursiveFind(((TDQPatternItem) item).getPattern());
            } else if (item instanceof TDQIndicatorDefinitionItem) {
                node = RepositoryNodeHelper.recursiveFind(((TDQIndicatorDefinitionItem) item).getIndicatorDefinition());
            }
            if (node != null) {
                return new StructuredSelection(node);
            }
        }
        return StructuredSelection.EMPTY;
    }

    public void activateEditor(IWorkbenchPage aPage, IStructuredSelection aSelection) {
        RepositoryNode repNode = (RepositoryNode) aSelection.getFirstElement();
        OpenItemEditorAction openEditorAction = new OpenItemEditorAction(repNode.getObject());
        AbstractItemEditorInput absEditorInput = openEditorAction.computeEditorInput();
        aPage.bringToTop(aPage.findEditor(absEditorInput));
    }

}
