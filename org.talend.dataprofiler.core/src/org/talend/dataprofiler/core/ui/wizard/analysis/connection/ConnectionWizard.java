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
package org.talend.dataprofiler.core.ui.wizard.analysis.connection;

import java.util.List;

import org.talend.cwm.helper.DataProviderHelper;
import org.talend.cwm.relational.TdCatalog;
import org.talend.cwm.relational.TdSchema;
import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.dataprofiler.core.ui.wizard.analysis.AbstractAnalysisWizard;
import org.talend.dataprofiler.core.ui.wizard.analysis.AnalysisMetadataWizardPage;
import org.talend.dataquality.indicators.schema.ConnectionIndicator;
import org.talend.dataquality.indicators.schema.SchemaFactory;
import org.talend.dataquality.indicators.schema.SchemaIndicator;
import org.talend.dq.analysis.AnalysisBuilder;
import org.talend.dq.analysis.parameters.ConnectionAnalysisParameter;

/**
 * @author zqin
 * 
 */
public class ConnectionWizard extends AbstractAnalysisWizard {

    private AnalysisMetadataWizardPage metadataPage;

    private ConnAnalysisPageStep0 page0;

    private ConnAnalysisPageStep1 page1;

    /**
     * 
     */
    public ConnectionWizard() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.wizard.Wizard#addPages()
     */
    @Override
    public void addPages() {

        metadataPage = new AnalysisMetadataWizardPage();
        page0 = new ConnAnalysisPageStep0();
        page1 = new ConnAnalysisPageStep1();

        addPage(metadataPage);
        addPage(page0);
        addPage(page1);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.wizard.Wizard#canFinish()
     */
    @Override
    public boolean canFinish() {
        if (this.getContainer().getCurrentPage() != page1) {
            return false;
        }
        return super.canFinish();
    }

    @Override
    protected void fillAnalysisEditorParam() {
        ConnectionAnalysisParameter parameters = (ConnectionAnalysisParameter) getAnalysisParameter();
        this.analysisName = parameters.getAnalysisName();
        this.analysisType = parameters.getAnalysisType();
        this.folderResource = parameters.getFolderProvider().getFolderResource();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.wizard.analysis.AbstractAnalysisWizard#fillAnalysisBuilder(org.talend.dq.analysis.AnalysisBuilder)
     */
    protected void fillAnalysisBuilder(AnalysisBuilder analysisBuilder) {
        ConnectionAnalysisParameter parameters = (ConnectionAnalysisParameter) getAnalysisParameter();
        TdDataProvider tdProvider = parameters.getTdDataProvider();
        analysisBuilder.setAnalysisConnection(tdProvider);
        ConnectionIndicator indicator = SchemaFactory.eINSTANCE.createConnectionIndicator();
        indicator.setAnalyzedElement(tdProvider);
        List<TdSchema> tdSchemas = DataProviderHelper.getTdSchema(tdProvider);
        for (TdSchema schema : tdSchemas) {
            SchemaIndicator createSchemaIndicator = SchemaFactory.eINSTANCE.createSchemaIndicator();
            createSchemaIndicator.setAnalyzedElement(schema);
            indicator.addSchemaIndicator(createSchemaIndicator);
        }
        List<TdCatalog> tdCatalogs = DataProviderHelper.getTdCatalogs(tdProvider);
        for (TdCatalog tdCatalog : tdCatalogs) {
            SchemaIndicator createSchemaIndicator = SchemaFactory.eINSTANCE.createSchemaIndicator();
            createSchemaIndicator.setAnalyzedElement(tdCatalog);
            indicator.addSchemaIndicator(createSchemaIndicator);
        }
        analysisBuilder.addElementToAnalyze(tdProvider, indicator);
        super.fillAnalysisBuilder(analysisBuilder);
    }

}
