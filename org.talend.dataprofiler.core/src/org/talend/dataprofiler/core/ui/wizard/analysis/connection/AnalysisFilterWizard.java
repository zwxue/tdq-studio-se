// ============================================================================
//
// Copyright (C) 2006-2010 Talend Inc. - www.talend.com
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

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.util.EList;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.cwm.dependencies.DependenciesHandler;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.ui.wizard.analysis.AbstractAnalysisWizard;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.domain.Domain;
import org.talend.dataquality.helpers.DomainHelper;
import org.talend.dataquality.indicators.schema.CatalogIndicator;
import org.talend.dataquality.indicators.schema.SchemaFactory;
import org.talend.dataquality.indicators.schema.SchemaIndicator;
import org.talend.dq.analysis.parameters.AnalysisFilterParameter;
import org.talend.dq.helper.resourcehelper.PrvResourceFileHelper;
import org.talend.dq.indicators.definitions.DefinitionHandler;
import org.talend.utils.sugars.TypedReturnCode;
import orgomg.cwm.foundation.softwaredeployment.DataManager;
import orgomg.cwm.objectmodel.core.ModelElement;
import orgomg.cwm.resource.relational.Schema;

/**
 * The wizard contains a table and table/view filter wizard page.
 */
public class AnalysisFilterWizard extends AbstractAnalysisWizard {

    protected AnalysisFilterPage anaFilterPage;

    public AnalysisFilterWizard(AnalysisFilterParameter parameter) {
        super(parameter);
    }

    @Override
    public boolean canFinish() {
        if (this.getContainer().getCurrentPage() != anaFilterPage) {
            return false;
        }
        return super.canFinish();
    }

    @Override
    public TypedReturnCode<IFile> createAndSaveCWMFile(ModelElement cwmElement) {
        Analysis analysis = (Analysis) cwmElement;
        DataManager connection = analysis.getContext().getConnection();
        DependenciesHandler.getInstance().setDependencyOn(analysis, connection);

        TypedReturnCode<IFile> saveCWMFile = super.createAndSaveCWMFile(analysis);

        // MOD by hcheng for 7173:Broken dependency between analyses and connection
        if (saveCWMFile.isOk()) {
            PrvResourceFileHelper.getInstance().save((Connection) connection);
        }

        return saveCWMFile;
    }

    @Override
    public ModelElement initCWMResourceBuilder() {
        Analysis analysis = (Analysis) super.initCWMResourceBuilder();
        EList<Domain> dataFilters = analysis.getParameters().getDataFilter();

        if ((getParameter().getTableFilter() != null) && (!getParameter().getTableFilter().equals(PluginConstant.EMPTY_STRING))) {
            DomainHelper.setDataFilterTablePattern(dataFilters, getParameter().getTableFilter());
        }
        if ((getParameter().getViewFilter() != null) && (!getParameter().getViewFilter().equals(PluginConstant.EMPTY_STRING))) {
            DomainHelper.setDataFilterViewPattern(dataFilters, getParameter().getViewFilter());
        }

        // DependenciesHandler.getInstance().setDependencyOn(analysis, analysis.getContext().getConnection());

        return analysis;
    }

    protected void addSchemaIndicator(List<Schema> tdSchemas, CatalogIndicator catalogIndicator) {
        for (Schema schema : tdSchemas) {
            SchemaIndicator createSchemaIndicator = SchemaFactory.eINSTANCE.createSchemaIndicator();
            // MOD xqliu 2009-1-21 feature 4715
            DefinitionHandler.getInstance().setDefaultIndicatorDefinition(createSchemaIndicator);
            createSchemaIndicator.setAnalyzedElement(schema);
            catalogIndicator.addSchemaIndicator(createSchemaIndicator);
        }
    }

    @Override
    protected AnalysisFilterParameter getParameter() {
        return (AnalysisFilterParameter) super.getParameter();
    }

}
