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
package org.talend.dataprofiler.core.ui.wizard.analysis.table;

import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.dataprofiler.core.ui.wizard.analysis.AnalysisMetadataWizardPage;
import org.talend.dataprofiler.core.ui.wizard.analysis.connection.AnalysisFilterWizard;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.IndicatorsFactory;
import org.talend.dataquality.indicators.RowCountIndicator;
import org.talend.dq.analysis.AnalysisBuilder;
import org.talend.dq.analysis.parameters.AnalysisFilterParameter;
import org.talend.dq.analysis.parameters.NamedColumnSetAnalysisParameter;
import org.talend.dq.indicators.definitions.DefinitionHandler;
import orgomg.cwm.resource.relational.NamedColumnSet;

/**
 * DOC xqliu class global comment. Detailled comment
 */
public class TableAnalysisWizard extends AnalysisFilterWizard {

    @Override
    public boolean canFinish() {
        return tableAnaDPSelectionPage == null ? false : tableAnaDPSelectionPage.isPageComplete();
    }

    private TableAnalysisDPSelectionPage tableAnaDPSelectionPage = null;

    /**
     * 
     * DOC xqliu TableAnalysisWizard constructor comment.
     * 
     * @param parameter
     */
    public TableAnalysisWizard(AnalysisFilterParameter parameter) {
        super(parameter);
    }

    public void addPages() {
        addPage(new AnalysisMetadataWizardPage());
        if (anaFilterParameter.getTdDataProvider() == null) {
            tableAnaDPSelectionPage = new TableAnalysisDPSelectionPage();
            tableAnaDPSelectionPage.setCanFinishEarly(true);
            addPage(tableAnaDPSelectionPage);
        }
    }

    @Override
    protected void fillAnalysisBuilder(AnalysisBuilder analysisBuilder) {

        NamedColumnSetAnalysisParameter namedColumnSetParameter = (NamedColumnSetAnalysisParameter) anaFilterParameter;
        TdDataProvider tdProvider = namedColumnSetParameter.getTdDataProvider();
        analysisBuilder.setAnalysisConnection(tdProvider);
        Indicator[] indicators = new Indicator[namedColumnSetParameter.getNamedColumnSets().length];
        int i = 0;
        for (NamedColumnSet namedColumnSet : namedColumnSetParameter.getNamedColumnSets()) {
            RowCountIndicator createIndicator = IndicatorsFactory.eINSTANCE.createRowCountIndicator();
            DefinitionHandler.getInstance().setDefaultIndicatorDefinition(createIndicator);
            createIndicator.setAnalyzedElement(namedColumnSet);
            indicators[i] = createIndicator;
            i++;
        }
        analysisBuilder.addElementsToAnalyze(namedColumnSetParameter.getNamedColumnSets(), indicators);
        super.fillAnalysisBuilder(analysisBuilder);
    }

}
