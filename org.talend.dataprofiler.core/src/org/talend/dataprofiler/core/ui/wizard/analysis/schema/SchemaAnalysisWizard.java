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
package org.talend.dataprofiler.core.ui.wizard.analysis.schema;

import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.dataprofiler.core.ui.wizard.analysis.AnalysisMetadataWizardPage;
import org.talend.dataprofiler.core.ui.wizard.analysis.connection.AnalysisFilterWizard;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.schema.SchemaFactory;
import org.talend.dataquality.indicators.schema.SchemaIndicator;
import org.talend.dq.analysis.parameters.AnalysisFilterParameter;
import org.talend.dq.analysis.parameters.PackagesAnalyisParameter;
import org.talend.dq.indicators.definitions.DefinitionHandler;
import orgomg.cwm.objectmodel.core.ModelElement;
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
        if (getParameter().getTdDataProvider() == null) {
            tableAnaDPSelectionPage = new SchemaAnalysisDPSelectionPage();
            addPage(tableAnaDPSelectionPage);
        }

        anaFilterPage = new SchemaAnalysisFilterPage();
        addPage(anaFilterPage);
    }

    @Override
    public ModelElement initCWMResourceBuilder() {
        Analysis analysis = (Analysis) super.initCWMResourceBuilder();

        if (getAnalysisBuilder() != null) {
            PackagesAnalyisParameter packageParameter = getParameter();
            TdDataProvider tdProvider = packageParameter.getTdDataProvider();
            getAnalysisBuilder().setAnalysisConnection(tdProvider);
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
            getAnalysisBuilder().addElementsToAnalyze(packageParameter.getPackages(), indicators);
        }

        return analysis;
    }

    @Override
    protected PackagesAnalyisParameter getParameter() {
        // TODO Auto-generated method stub
        return (PackagesAnalyisParameter) super.getParameter();
    }
}
