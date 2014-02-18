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
package org.talend.dataprofiler.core.ui.wizard.analysis.column;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.ui.PlatformUI;
import org.talend.core.model.properties.Item;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.ui.editor.analysis.AnalysisEditor;
import org.talend.dataprofiler.core.ui.editor.analysis.MatchMasterDetailsPage;
import org.talend.dataprofiler.core.ui.wizard.analysis.AnalysisMetadataWizardPage;
import org.talend.dataprofiler.core.ui.wizard.analysis.provider.MatchAnaColumnContentProvider;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.analysis.AnalysisType;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.columnset.ColumnsetFactory;
import org.talend.dataquality.indicators.columnset.RecordMatchingIndicator;
import org.talend.dataquality.rules.MatchRuleDefinition;
import org.talend.dataquality.rules.RulesFactory;
import org.talend.dq.analysis.parameters.AnalysisParameter;
import org.talend.dq.nodes.ColumnRepNode;
import org.talend.dq.nodes.ColumnSetRepNode;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.RepositoryNode;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC yyin class global comment. Detailled comment
 */
public class MatchWizard extends ColumnWizard {

    private ColumnAnalysisDOSelectionPage selectionPage;

    /**
     * MatchWizard constructor comment.
     * 
     * @param parameter
     */
    public MatchWizard(AnalysisParameter parameter) {
        super(parameter);
    }

    // make the next button available and the next page is to select columns
    @Override
    public void addPages() {
        addPage(new AnalysisMetadataWizardPage());
        AnalysisParameter parameter = (AnalysisParameter) getParameter();
        if (parameter.getConnectionRepNode() == null && parameter.getAnalysisType().equals(AnalysisType.MATCH_ANALYSIS)) {
            selectionPage = new ColumnAnalysisDOSelectionPage(new MatchAnaColumnContentProvider(true));
            addPage(selectionPage);
        }
        for (WizardPage page : getExtenalPages()) {
            addPage(page);
        }
    }

    @Override
    public void openEditor(Item item) {
        super.openEditor(item);

        if (selectionPage != null) {
            AnalysisEditor editor = (AnalysisEditor) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
                    .getActiveEditor();
            if (editor != null) {
                updateAnalysisBySelectedNode(editor);
            }
        }
    }

    private void updateAnalysisBySelectedNode(AnalysisEditor editor) {
        MatchMasterDetailsPage masterPage = (MatchMasterDetailsPage) editor.getMasterPage();
        List<IRepositoryNode> nodes = selectionPage.nodes;

        if (nodes != null && nodes.size() > 0) {
            // if some selected node is not column type
            if (isNotColumn(nodes)) {
                nodes = translateTableIntoColumn(nodes);
            }

            // update analyze data label by selected nodes names(don't cotain columnRepNode).
            if (nodes.size() > 0) {
                masterPage.updateAnalyzeDataLabel(nodes.get(0));
            }
            // give the selected columns to the master page
            masterPage.setSelectedNodes(nodes.toArray(new RepositoryNode[nodes.size()]));
            masterPage.doSave(new NullProgressMonitor());
            masterPage.updateAllColumnsToKeySection();
        }
    }

    /**
     * check if some columnset is selected. Added yyin TDQ-8481, 20140218
     * 
     * @param nodes
     * @return
     */
    private boolean isNotColumn(List<IRepositoryNode> nodes) {
        for (IRepositoryNode node : nodes) {
            if (!(node instanceof ColumnRepNode)) {
                return true;
            }
        }
        return false;
    }

    /**
     * when the selected node is a db table, or file's metadata, should get its children and add them; for other types:
     * just return empty list of node. MOD yyin 20140218 TDQ-8481
     * 
     * @param nodes
     * 
     * @param iRepositoryNode
     * @return
     */
    private List<IRepositoryNode> translateTableIntoColumn(List<IRepositoryNode> nodes) {
        for (IRepositoryNode node : nodes) {
            if (node instanceof ColumnSetRepNode) {
                return ((ColumnSetRepNode) node).getAllChildrenColumns();
            }
        }
        return new ArrayList<IRepositoryNode>();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.wizard.analysis.column.ColumnSetWizard#initCWMResourceBuilder()
     */
    @Override
    public ModelElement initCWMResourceBuilder() {
        Analysis analysis = (Analysis) super.initCWMResourceBuilder();

        // New blocking key indicator.
        Indicator blockKeyIndicator = ColumnsetFactory.eINSTANCE.createBlockKeyIndicator();
        analysis.getResults().getIndicators().add(blockKeyIndicator);

        // Match rule indicator
        RecordMatchingIndicator matchRuleIndicator = ColumnsetFactory.eINSTANCE.createRecordMatchingIndicator();
        MatchRuleDefinition matchRuleDefinition = RulesFactory.eINSTANCE.createMatchRuleDefinition();
        matchRuleIndicator.setBuiltInMatchRuleDefinition(matchRuleDefinition);
        analysis.getResults().getIndicators().add(matchRuleIndicator);

        // default loaded row count
        IPreferenceStore preferenceStore = CorePlugin.getDefault().getPreferenceStore();
        int maxRows = preferenceStore.getInt(PluginConstant.MAX_NB_ROWS_ANALYSIS_EDITOR);
        analysis.getParameters().setMaxNumberRows(maxRows);

        return analysis;
    }

}
