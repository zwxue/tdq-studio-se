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
package org.talend.dataprofiler.core.ui.action.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.PlatformUI;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.editor.analysis.AnalysisEditor;
import org.talend.dataprofiler.core.ui.editor.analysis.ColumnMasterDetailsPage;
import org.talend.dataprofiler.core.ui.wizard.analysis.WizardFactory;
import org.talend.dataquality.analysis.AnalysisType;
import org.talend.repository.model.RepositoryNode;

/**
 * DOC zqin class global comment. Detailled comment <br/>
 * 
 * $Id: talend.epf 1 2006-09-29 17:06:40Z zqin $
 * 
 */
public class AnalyzeColumnAction extends Action {

    TreeSelection selection;

    public AnalyzeColumnAction() {
        super(DefaultMessagesImpl.getString("AnalyzeColumnAction.analyze")); //$NON-NLS-1$
        setImageDescriptor(ImageLib.getImageDescriptor(ImageLib.ACTION_NEW_ANALYSIS));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.action.Action#run()
     */
    @SuppressWarnings("unchecked")
    @Override
    public void run() {

        if (openStandardAnalysisDialog(AnalysisType.MULTIPLE_COLUMN) == Window.OK) {
            AnalysisEditor editor = (AnalysisEditor) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
                    .getActiveEditor();
            if (editor != null) {
                ColumnMasterDetailsPage page = (ColumnMasterDetailsPage) editor.getMasterPage();
                if (!this.selection.isEmpty()) {
                    List<RepositoryNode> nodeList = new ArrayList<RepositoryNode>();
                    Object[] objs = this.selection.toArray();
                    for (Object obj : objs) {
                        if (obj instanceof RepositoryNode) {
                            nodeList.add((RepositoryNode) obj);
                        }
                    }
                    page.getTreeViewer().setInput(nodeList.toArray(new RepositoryNode[nodeList.size()]));
                }
            }
        }
    }

    public void setColumnSelection(TreeSelection selection) {
        this.selection = selection;
    }

    private int openStandardAnalysisDialog(AnalysisType type) {
        Wizard wizard = WizardFactory.createAnalysisWizard(type);
        wizard.setForcePreviousAndNextButtons(true);
        WizardDialog dialog = new WizardDialog(null, wizard);
        dialog.setPageSize(500, 340);

        return dialog.open();
    }
}
