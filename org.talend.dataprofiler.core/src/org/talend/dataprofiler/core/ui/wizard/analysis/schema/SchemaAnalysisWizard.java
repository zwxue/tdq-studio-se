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
import org.talend.dataquality.indicators.schema.ConnectionIndicator;
import org.talend.dataquality.indicators.schema.SchemaFactory;
import org.talend.dataquality.indicators.schema.SchemaIndicator;
import org.talend.dq.analysis.AnalysisBuilder;
import org.talend.dq.analysis.parameters.AnalysisFilterParameter;
import org.talend.dq.analysis.parameters.PackagesAnalyisParameter;
import orgomg.cwm.objectmodel.core.Package;

/**
 * DOC rli class global comment. Detailled comment
 */
public class SchemaAnalysisWizard extends AnalysisFilterWizard {

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
        anaFilterPage = new SchemaAnalysisFilterPage();
        addPage(anaFilterPage);
    }

    @Override
    protected void fillAnalysisBuilder(AnalysisBuilder analysisBuilder) {
        PackagesAnalyisParameter packageParameter = (PackagesAnalyisParameter) anaFilterParameter;
        TdDataProvider tdProvider = packageParameter.getTdDataProvider();
        analysisBuilder.setAnalysisConnection(tdProvider);
        ConnectionIndicator indicator = SchemaFactory.eINSTANCE.createConnectionIndicator();
        indicator.setAnalyzedElement(tdProvider);
        for (Package tdCatalog : packageParameter.getPackages()) {
            SchemaIndicator createSchemaIndicator = SchemaFactory.eINSTANCE.createSchemaIndicator();
            createSchemaIndicator.setAnalyzedElement(tdCatalog);
            indicator.addSchemaIndicator(createSchemaIndicator);
        }
        analysisBuilder.addElementToAnalyze(tdProvider, indicator);
        super.fillAnalysisBuilder(analysisBuilder);
    }

}
