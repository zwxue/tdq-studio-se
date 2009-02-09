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
package org.talend.dataprofiler.core.ui.wizard.analysis.connection;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.resource.Resource;
import org.talend.cwm.dependencies.DependenciesHandler;
import org.talend.cwm.relational.TdSchema;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.exception.DataprofilerCoreException;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.views.DQRespositoryView;
import org.talend.dataprofiler.core.ui.wizard.analysis.AbstractAnalysisWizard;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.domain.Domain;
import org.talend.dataquality.helpers.DomainHelper;
import org.talend.dataquality.indicators.schema.CatalogIndicator;
import org.talend.dataquality.indicators.schema.SchemaFactory;
import org.talend.dataquality.indicators.schema.SchemaIndicator;
import org.talend.dq.analysis.AnalysisBuilder;
import org.talend.dq.analysis.AnalysisWriter;
import org.talend.dq.analysis.parameters.AnalysisFilterParameter;
import org.talend.dq.helper.resourcehelper.AnaResourceFileHelper;
import org.talend.dq.indicators.definitions.DefinitionHandler;
import org.talend.utils.sugars.ReturnCode;
import org.talend.utils.sugars.TypedReturnCode;

/**
 * The wizard contains a table and table/view filter wizard page.
 */
public class AnalysisFilterWizard extends AbstractAnalysisWizard {

    private static Logger log = Logger.getLogger(AnalysisFilterWizard.class);

    protected AnalysisFilterParameter anaFilterParameter;

    protected AnalysisFilterPage anaFilterPage;

    public AnalysisFilterWizard(AnalysisFilterParameter parameter) {
        super(parameter);
        this.anaFilterParameter = parameter;
    }

    @Override
    public boolean canFinish() {
        if (this.getContainer().getCurrentPage() != anaFilterPage) {
            return false;
        }
        return super.canFinish();
    }

    @Override
    protected void fillAnalysisEditorParam() {
        this.analysisName = anaFilterParameter.getAnalysisName();
        this.analysisType = anaFilterParameter.getAnalysisType();
        this.folderResource = anaFilterParameter.getFolderProvider().getFolderResource();
    }

    protected IFile createEmptyAnalysisFile() throws DataprofilerCoreException {
        AnalysisBuilder analysisBuilder = new AnalysisBuilder();
        boolean analysisInitialized = analysisBuilder.initializeAnalysis(analysisName, analysisType);
        if (!analysisInitialized) {
            throw new DataprofilerCoreException(analysisName + DefaultMessagesImpl.getString("ConnectionWizard.failedInitialize")); //$NON-NLS-1$
        }
        Analysis analysis = analysisBuilder.getAnalysis();
        fillAnalysisBuilder(analysisBuilder);
        AnalysisWriter writer = new AnalysisWriter();
        TypedReturnCode<IFile> saved = writer.createAnalysisFile(analysis, folderResource);
        IFile file;
        if (saved.isOk()) {
            if (log.isDebugEnabled()) {
                log.debug("Saved in  " + folderResource.getFullPath().toString()); //$NON-NLS-1$
            }
            file = saved.getObject();
            AnaResourceFileHelper.getInstance().clear();
            Resource anaResource = analysis.eResource();
            AnaResourceFileHelper.getInstance().register(file, anaResource);
            AnaResourceFileHelper.getInstance().setResourcesNumberChanged(true);
        } else {
            throw new DataprofilerCoreException(DefaultMessagesImpl.getString(
                    "ConnectionWizard.problemFile", folderResource.getFullPath().toString(), saved.getMessage())); //$NON-NLS-1$ //$NON-NLS-2$
        }

        EList<Domain> dataFilters = analysisBuilder.getAnalysis().getParameters().getDataFilter();
        if ((anaFilterParameter.getTableFilter() != null)
                && (!anaFilterParameter.getTableFilter().equals(PluginConstant.EMPTY_STRING))) {
            DomainHelper.setDataFilterTablePattern(dataFilters, anaFilterParameter.getTableFilter());
        }
        if ((anaFilterParameter.getViewFilter() != null)
                && (!anaFilterParameter.getViewFilter().equals(PluginConstant.EMPTY_STRING))) {
            DomainHelper.setDataFilterViewPattern(dataFilters, anaFilterParameter.getViewFilter());
        }

        DependenciesHandler.getInstance().setDependencyOn(analysisBuilder.getAnalysis(),
                analysisBuilder.getAnalysis().getContext().getConnection());

        ReturnCode save = AnaResourceFileHelper.getInstance().save(analysisBuilder.getAnalysis());
        if (save.isOk()) {
            log.info("Success to save the analysis:" + analysisBuilder.getAnalysis().getFileName()); //$NON-NLS-1$
        }

        CorePlugin.getDefault().refreshWorkSpace();
        ((DQRespositoryView) CorePlugin.getDefault().findView(DQRespositoryView.ID)).getCommonViewer().refresh();

        return file;

    }

    protected void addSchemaIndicator(List<TdSchema> tdSchemas, CatalogIndicator catalogIndicator) {
        for (TdSchema schema : tdSchemas) {
            SchemaIndicator createSchemaIndicator = SchemaFactory.eINSTANCE.createSchemaIndicator();
            // MOD xqliu 2009-1-21 feature 4715
            DefinitionHandler.getInstance().setDefaultIndicatorDefinition(createSchemaIndicator);
            createSchemaIndicator.setAnalyzedElement(schema);
            catalogIndicator.addSchemaIndicator(createSchemaIndicator);
        }
    }

}
