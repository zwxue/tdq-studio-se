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

import org.talend.dataprofiler.core.ui.wizard.analysis.AbstractAnalysisWizard;
import org.talend.dataprofiler.core.ui.wizard.analysis.AnalysisMetadataWizardPage;
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
public class TableAnalysisWizard extends AbstractAnalysisWizard {

    private NamedColumnSet[] namedColumnSet;

    public NamedColumnSet[] getNamedColumnSet() {
        return namedColumnSet;
    }

    public void setNamedColumnSet(NamedColumnSet[] namedColumnSet) {
        this.namedColumnSet = namedColumnSet;
    }

    @Override
    public boolean canFinish() {
        return analysisMetadataWizardPage == null ? false : analysisMetadataWizardPage.isPageComplete();
    }

    private AnalysisMetadataWizardPage analysisMetadataWizardPage = null;

    /**
     * DOC xqliu TableAnalysisWizard constructor comment.
     * 
     * @param parameter
     */
    public TableAnalysisWizard(AnalysisFilterParameter parameter) {
        super(parameter);
    }

    public void addPages() {
        analysisMetadataWizardPage = new AnalysisMetadataWizardPage();
        addPage(analysisMetadataWizardPage);
    }

    @Override
    public ModelElement initCWMResourceBuilder() {
        Analysis analysis = (Analysis) super.initCWMResourceBuilder();
        if (getNamedColumnSet() != null) {
            Indicator[] indicators = new Indicator[getNamedColumnSet().length];
            int i = 0;
            for (NamedColumnSet namedColumnSet : getNamedColumnSet()) {
                RowCountIndicator createIndicator = IndicatorsFactory.eINSTANCE.createRowCountIndicator();
                DefinitionHandler.getInstance().setDefaultIndicatorDefinition(createIndicator);
                createIndicator.setAnalyzedElement(namedColumnSet);
                indicators[i] = createIndicator;
                i++;
            }
            getAnalysisBuilder().addElementsToAnalyze(getNamedColumnSet(), indicators);
        }

        // if (getAnalysisBuilder() != null) {
        // TdDataProvider tdProvider = getParameter().getTdDataProvider();
        // getAnalysisBuilder().setAnalysisConnection(tdProvider);
        // NamedColumnSet[] ncs = getParameter().getNamedColumnSets();
        // if (ncs != null) {
        // Indicator[] indicators = new Indicator[ncs.length];
        // int i = 0;
        // for (NamedColumnSet namedColumnSet : ncs) {
        // RowCountIndicator createIndicator = IndicatorsFactory.eINSTANCE.createRowCountIndicator();
        // DefinitionHandler.getInstance().setDefaultIndicatorDefinition(createIndicator);
        // createIndicator.setAnalyzedElement(namedColumnSet);
        // indicators[i] = createIndicator;
        // i++;
        // }
        // getAnalysisBuilder().addElementsToAnalyze(ncs, indicators);
        // }
        // }
        return analysis;
    }

    @Override
    protected NamedColumnSetAnalysisParameter getParameter() {
        return (NamedColumnSetAnalysisParameter) super.getParameter();
    }
}
