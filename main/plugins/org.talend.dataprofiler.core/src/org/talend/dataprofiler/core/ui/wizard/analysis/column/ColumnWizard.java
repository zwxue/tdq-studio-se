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
package org.talend.dataprofiler.core.ui.wizard.analysis.column;

import java.util.List;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.ui.PlatformUI;
import org.talend.core.model.properties.Item;
import org.talend.dataprofiler.core.ui.editor.analysis.AbstractAnalysisMetadataPage;
import org.talend.dataprofiler.core.ui.editor.analysis.AnalysisEditor;
import org.talend.dataprofiler.core.ui.editor.analysis.ColumnMasterDetailsPage;
import org.talend.dataprofiler.core.ui.wizard.analysis.AbstractAnalysisWizard;
import org.talend.dataprofiler.core.ui.wizard.analysis.AnalysisMetadataWizardPage;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.analysis.AnalysisType;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dq.analysis.parameters.AnalysisParameter;
import org.talend.dq.indicators.definitions.DefinitionHandler;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.RepositoryNode;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * @author zqin
 */
public class ColumnWizard extends AbstractAnalysisWizard {

    private WizardPage[] extenalPages;

    private Indicator indicator;

    private ColumnAnalysisDOSelectionPage selectionPage;

    public WizardPage[] getExtenalPages() {
        if (extenalPages == null) {
            return new WizardPage[0];
        }
        return extenalPages;
    }

    public void setExtenalPages(WizardPage[] extenalPages) {
        this.extenalPages = extenalPages;
    }

    public ColumnWizard(AnalysisParameter parameter) {
        super(parameter);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.wizard.analysis.AbstractAnalysisWizard#initCWMResourceBuilder()
     */
    @Override
    public ModelElement initCWMResourceBuilder() {
        Analysis analysis = (Analysis) super.initCWMResourceBuilder();

        if (indicator != null) {
            DefinitionHandler.getInstance().setDefaultIndicatorDefinition(indicator);
            analysis.getResults().getIndicators().add(indicator);
        }

        return analysis;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.wizard.Wizard#addPages()
     */
    @Override
    public void addPages() {
        addPage(new AnalysisMetadataWizardPage());
        AnalysisParameter parameter = (AnalysisParameter) getParameter();
        if (parameter.getConnectionRepNode() == null
                && (parameter.getAnalysisType().equals(AnalysisType.MULTIPLE_COLUMN) || parameter.getAnalysisType().equals(
                        AnalysisType.COLUMN_SET))) {
            selectionPage = new ColumnAnalysisDOSelectionPage();
            addPage(selectionPage);
        }
        for (WizardPage page : getExtenalPages()) {
            addPage(page);
        }
    }

    /**
     * Sets the indicator.
     * 
     * @param indicator the indicator to set
     */
    public void setIndicator(Indicator indicator) {
        this.indicator = indicator;
    }

    /**
     * Getter for indicator.
     * 
     * @return the indicator
     */
    public Indicator getIndicator() {
        return this.indicator;
    }

    /*
     * (non-Jsdoc)
     * 
     * @see
     * org.talend.dataprofiler.core.ui.wizard.analysis.AbstractAnalysisWizard#openEditor(org.talend.core.model.properties
     * .Item)
     */
    @Override
    public void openEditor(Item item) {
        super.openEditor(item);
        if (this.selectionPage != null) {
            AnalysisEditor editor = (AnalysisEditor) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
                    .getActiveEditor();
            if (editor != null) {
                AbstractAnalysisMetadataPage masterPage = editor.getMasterPage();
                List<IRepositoryNode> nodes = this.selectionPage.nodes;
                if (nodes != null && nodes.size() > 0) {
                    // MOD msjian TDQ-6665 2013-1-7: after the wizard, make the editor is saved status
                    if (masterPage instanceof ColumnMasterDetailsPage) {
                        ((ColumnMasterDetailsPage) masterPage).setTreeViewInput(nodes.toArray(new RepositoryNode[nodes.size()]));
                    } else {
                        masterPage.getTreeViewer().setInput(nodes.toArray(new RepositoryNode[nodes.size()]));
                    }
                    masterPage.doSave(new NullProgressMonitor());
                    // TDQ-6665~
                }
            }
        }
    }
}
