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
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.IndicatorsFactory;
import org.talend.dataquality.indicators.RowCountIndicator;
import org.talend.dq.analysis.parameters.AnalysisFilterParameter;
import org.talend.dq.analysis.parameters.NamedColumnSetAnalysisParameter;
import org.talend.dq.indicators.definitions.DefinitionHandler;
import orgomg.cwm.objectmodel.core.ModelElement;
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
        if (getParameter().getTdDataProvider() == null) {
            tableAnaDPSelectionPage = new TableAnalysisDPSelectionPage();
            tableAnaDPSelectionPage.setCanFinishEarly(true);
            addPage(tableAnaDPSelectionPage);
        }
    }

    @Override
    public ModelElement initCWMResourceBuilder() {
        Analysis analysis = (Analysis) super.initCWMResourceBuilder();

        if (getAnalysisBuilder() != null) {
            TdDataProvider tdProvider = getParameter().getTdDataProvider();
            getAnalysisBuilder().setAnalysisConnection(tdProvider);
            Indicator[] indicators = new Indicator[getParameter().getNamedColumnSets().length];
            int i = 0;
            for (NamedColumnSet namedColumnSet : getParameter().getNamedColumnSets()) {
                RowCountIndicator createIndicator = IndicatorsFactory.eINSTANCE.createRowCountIndicator();
                DefinitionHandler.getInstance().setDefaultIndicatorDefinition(createIndicator);
                createIndicator.setAnalyzedElement(namedColumnSet);
                indicators[i] = createIndicator;
                i++;
            }
            getAnalysisBuilder().addElementsToAnalyze(getParameter().getNamedColumnSets(), indicators);
        }

        return analysis;
    }

    @Override
    protected NamedColumnSetAnalysisParameter getParameter() {
        return (NamedColumnSetAnalysisParameter) super.getParameter();
    }
}
