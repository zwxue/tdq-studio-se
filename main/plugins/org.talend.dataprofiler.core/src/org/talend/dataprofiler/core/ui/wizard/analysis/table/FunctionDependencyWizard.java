// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.wizard.analysis.table;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.talend.core.model.properties.Item;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.ui.editor.analysis.AnalysisEditor;
import org.talend.dataprofiler.core.ui.editor.analysis.AnalysisItemEditorInput;
import org.talend.dataprofiler.core.ui.wizard.analysis.AbstractAnalysisWizard;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dq.analysis.parameters.AnalysisParameter;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC jet this is wizard for create new Function Dependency Analysis.
 * <p>
 * 
 */
public class FunctionDependencyWizard extends AbstractAnalysisWizard implements INewWizard {

    /**
     * DOC jet FunctionDependencyWizard constructor comment.
     * 
     * @param parameter
     */
    public FunctionDependencyWizard(AnalysisParameter parameter) {
        super(parameter);
    }

    // private IStructuredSelection initalSelection = null;
    // private TableAnalysisDPSelectionPage tableAnalysisDPSelectionPage = null;
    // private DQRuleSelectPage dqruleSelectPage = null;

    private TableAnalysisMetadataWizardPage analysisMetadataWizardPage = null;

    @Override
    public ModelElement initCWMResourceBuilder() {
        Analysis analysis = (Analysis) super.initCWMResourceBuilder();

        return analysis;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench,
     * org.eclipse.jface.viewers.IStructuredSelection)
     */
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        // initalSelection = selection;
        setHelpAvailable(true);
        setWindowTitle("Just a test"); //$NON-NLS-1$
    }

    @Override
    public void addPages() {
        analysisMetadataWizardPage = new TableAnalysisMetadataWizardPage();
        this.addPage(analysisMetadataWizardPage);
    }

    /*
     * (non-Jsdoc)
     * 
     * @see org.talend.dataprofiler.core.ui.wizard.AbstractWizard#openEditor(org.talend.core.model.properties.Item)
     */
    @Override
    public void openEditor(Item item) {
        AnalysisItemEditorInput itemEditorInput = new AnalysisItemEditorInput(item);
        CorePlugin.getDefault().openEditor(itemEditorInput, AnalysisEditor.class.getName());
    }
}
