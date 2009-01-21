// ============================================================================
//
// Copyright (C) 2006-2007 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.wizard.analysis.schema;

import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.dataprofiler.core.ui.wizard.analysis.AnalysisMetadataWizardPage;
import org.talend.dataprofiler.core.ui.wizard.analysis.connection.AnalysisFilterWizard;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.schema.SchemaFactory;
import org.talend.dataquality.indicators.schema.SchemaIndicator;
import org.talend.dq.analysis.AnalysisBuilder;
import org.talend.dq.analysis.parameters.AnalysisFilterParameter;
import org.talend.dq.analysis.parameters.PackagesAnalyisParameter;
import org.talend.dq.indicators.definitions.DefinitionHandler;
import orgomg.cwm.objectmodel.core.Package;

/**
 * DOC rli class global comment. Detailled comment
 */
public class SchemaAnalysisWizard extends AnalysisFilterWizard {

    private SchemaAnalysisDPSelectionPage tableAnaDPSelectionPage = null;

    /**
     * DOC rli SchemaAnalysisWizard constructor comment.
     * 
     * @param parameter
     */
    public SchemaAnalysisWizard(AnalysisFilterParameter parameter) {
        super(parameter);
    }

    public void addPages() {
        addPage(new AnalysisMetadataWizardPage());
        if (anaFilterParameter.getTdDataProvider() == null) {
            tableAnaDPSelectionPage = new SchemaAnalysisDPSelectionPage();
            addPage(tableAnaDPSelectionPage);
        }

        anaFilterPage = new SchemaAnalysisFilterPage();
        addPage(anaFilterPage);
    }

    @Override
    protected void fillAnalysisBuilder(AnalysisBuilder analysisBuilder) {

        PackagesAnalyisParameter packageParameter = (PackagesAnalyisParameter) anaFilterParameter;
        TdDataProvider tdProvider = packageParameter.getTdDataProvider();
        analysisBuilder.setAnalysisConnection(tdProvider);
        Indicator[] indicators = new Indicator[packageParameter.getPackages().length];
        int i = 0;
        for (Package tdSchema : packageParameter.getPackages()) {
            SchemaIndicator createSchemaIndicator = SchemaFactory.eINSTANCE.createSchemaIndicator();
            // MOD xqliu 2009-1-21 feature 4715
            DefinitionHandler.getInstance().setDefaultIndicatorDefinition(createSchemaIndicator);
            createSchemaIndicator.setAnalyzedElement(tdSchema);
            indicators[i] = createSchemaIndicator;
            i++;
        }
        analysisBuilder.addElementsToAnalyze(packageParameter.getPackages(), indicators);
        super.fillAnalysisBuilder(analysisBuilder);
    }

}
