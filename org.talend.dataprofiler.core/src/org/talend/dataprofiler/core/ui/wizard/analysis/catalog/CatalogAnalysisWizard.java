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
package org.talend.dataprofiler.core.ui.wizard.analysis.catalog;

import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.dataprofiler.core.ui.wizard.analysis.AnalysisMetadataWizardPage;
import org.talend.dataprofiler.core.ui.wizard.analysis.connection.AnalysisFilterWizard;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.schema.CatalogIndicator;
import org.talend.dataquality.indicators.schema.SchemaFactory;
import org.talend.dq.analysis.parameters.AnalysisFilterParameter;
import org.talend.dq.analysis.parameters.PackagesAnalyisParameter;
import org.talend.dq.indicators.definitions.DefinitionHandler;
import orgomg.cwm.objectmodel.core.ModelElement;
import orgomg.cwm.objectmodel.core.Package;

/**
 * DOC rli class global comment. Detailled comment
 */
public class CatalogAnalysisWizard extends AnalysisFilterWizard {

    /**
     * DOC rli CatalogAnalysisWizard constructor comment.
     * 
     * @param parameter
     */
    public CatalogAnalysisWizard(AnalysisFilterParameter parameter) {
        super(parameter);
    }

    public void addPages() {
        addPage(new AnalysisMetadataWizardPage());

        if (getParameter().getTdDataProvider() == null) {
            addPage(new CatalogAnalysisDPSelectionPage());
        }

        anaFilterPage = new CatalogAnalysisFilterPage();
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
            for (Package tdCatalog : packageParameter.getPackages()) {
                CatalogIndicator createCatalogIndicator = SchemaFactory.eINSTANCE.createCatalogIndicator();
                // MOD xqliu 2009-1-21 feature 4715
                DefinitionHandler.getInstance().setDefaultIndicatorDefinition(createCatalogIndicator);
                createCatalogIndicator.setAnalyzedElement(tdCatalog);
                indicators[i] = createCatalogIndicator;
                i++;
            }
            getAnalysisBuilder().addElementsToAnalyze(packageParameter.getPackages(), indicators);
        }
        return analysis;
    }

    @Override
    protected PackagesAnalyisParameter getParameter() {
        return (PackagesAnalyisParameter) super.getParameter();
    }
}
