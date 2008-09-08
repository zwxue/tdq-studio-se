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

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.resource.Resource;
import org.talend.cwm.dependencies.DependenciesHandler;
import org.talend.cwm.helper.DataProviderHelper;
import org.talend.cwm.relational.TdCatalog;
import org.talend.cwm.relational.TdSchema;
import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.exception.DataprofilerCoreException;
import org.talend.dataprofiler.core.helper.AnaResourceFileHelper;
import org.talend.dataprofiler.core.ui.views.DQRespositoryView;
import org.talend.dataprofiler.core.ui.wizard.analysis.AbstractAnalysisWizard;
import org.talend.dataprofiler.core.ui.wizard.analysis.AnalysisMetadataWizardPage;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.domain.Domain;
import org.talend.dataquality.helpers.DomainHelper;
import org.talend.dataquality.indicators.schema.ConnectionIndicator;
import org.talend.dataquality.indicators.schema.SchemaFactory;
import org.talend.dataquality.indicators.schema.SchemaIndicator;
import org.talend.dq.analysis.AnalysisBuilder;
import org.talend.dq.analysis.AnalysisWriter;
import org.talend.dq.analysis.parameters.ConnectionAnalysisParameter;
import org.talend.utils.sugars.ReturnCode;
import org.talend.utils.sugars.TypedReturnCode;

/**
 * @author zqin
 * 
 */
public class ConnectionWizard extends AbstractAnalysisWizard {

    private static Logger log = Logger.getLogger(ConnectionWizard.class);

    private ConnectionAnalysisParameter parameter;

    private ConnAnalysisPageStep0 page0;

    private ConnAnalysisPageStep1 page1;

    /**
     * 
     */
    public ConnectionWizard(ConnectionAnalysisParameter parameter) {
        super(parameter);
        this.parameter = parameter;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.wizard.Wizard#addPages()
     */
    @Override
    public void addPages() {

        addPage(new AnalysisMetadataWizardPage());

        if (parameter.getTdDataProvider() == null) {
            page0 = new ConnAnalysisPageStep0();
            addPage(page0);
        }

        page1 = new ConnAnalysisPageStep1();
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
        this.analysisName = parameter.getAnalysisName();
        this.analysisType = parameter.getAnalysisType();
        this.folderResource = parameter.getFolderProvider().getFolderResource();
    }

    @Override
    protected void fillAnalysisBuilder(AnalysisBuilder analysisBuilder) {

        TdDataProvider tdProvider = parameter.getTdDataProvider();
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

    protected IFile createEmptyAnalysisFile() throws DataprofilerCoreException {
        AnalysisBuilder analysisBuilder = new AnalysisBuilder();
        boolean analysisInitialized = analysisBuilder.initializeAnalysis(analysisName, analysisType);
        if (!analysisInitialized) {
            throw new DataprofilerCoreException(analysisName + " failed to initialize!");
        }
        Analysis analysis = analysisBuilder.getAnalysis();
        fillAnalysisBuilder(analysisBuilder);
        AnalysisWriter writer = new AnalysisWriter();
        TypedReturnCode<IFile> saved = writer.createAnalysisFile(analysis, folderResource);
        IFile file;
        if (saved.isOk()) {
            log.info("Saved in  " + folderResource.getFullPath().toString());
            file = saved.getObject();
            AnaResourceFileHelper.getInstance().clear();
            Resource anaResource = analysis.eResource();
            AnaResourceFileHelper.getInstance().register(file, anaResource);
            AnaResourceFileHelper.getInstance().setResourcesNumberChanged(true);
        } else {
            throw new DataprofilerCoreException("Problem saving file: " + folderResource.getFullPath().toString() + ": "
                    + saved.getMessage());
        }

        EList<Domain> dataFilters = analysisBuilder.getAnalysis().getParameters().getDataFilter();
        if ((parameter.getTableFilter() != null) && (!parameter.getTableFilter().equals(PluginConstant.EMPTY_STRING))) {
            DomainHelper.setDataFilterTablePattern(dataFilters, parameter.getTableFilter());
        }
        if ((parameter.getViewFilter() != null) && (!parameter.getViewFilter().equals(PluginConstant.EMPTY_STRING))) {
            DomainHelper.setDataFilterViewPattern(dataFilters, parameter.getViewFilter());
        }

        DependenciesHandler.getInstance().setDependencyOn(analysisBuilder.getAnalysis(),
                analysisBuilder.getAnalysis().getContext().getConnection());

        ReturnCode save = AnaResourceFileHelper.getInstance().save(analysisBuilder.getAnalysis());
        if (save.isOk()) {
            log.info("Success to save connection analysis:" + analysisBuilder.getAnalysis().getFileName());
        }

        CorePlugin.getDefault().refreshWorkSpace();
        ((DQRespositoryView) CorePlugin.getDefault().findView(DQRespositoryView.ID)).getCommonViewer().refresh();

        return file;

    }
}
