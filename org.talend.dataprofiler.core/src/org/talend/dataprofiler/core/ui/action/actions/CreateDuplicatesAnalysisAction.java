// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.action.actions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.jface.action.Action;
import org.talend.commons.utils.VersionUtils;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.properties.Item;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.cwm.dependencies.DependenciesHandler;
import org.talend.cwm.helper.ColumnHelper;
import org.talend.cwm.relational.TdColumn;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.ui.editor.analysis.AnalysisEditor;
import org.talend.dataprofiler.core.ui.editor.analysis.AnalysisItemEditorInput;
import org.talend.dataprofiler.core.ui.utils.MessageUI;
import org.talend.dataprofiler.core.ui.utils.WorkbenchUtils;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.analysis.AnalysisType;
import org.talend.dataquality.analysis.ExecutionLanguage;
import org.talend.dataquality.domain.Domain;
import org.talend.dataquality.helpers.MetadataHelper;
import org.talend.dataquality.indicators.DuplicateCountIndicator;
import org.talend.dataquality.indicators.IndicatorsFactory;
import org.talend.dataquality.indicators.RowCountIndicator;
import org.talend.dq.analysis.AnalysisBuilder;
import org.talend.dq.analysis.parameters.AnalysisParameter;
import org.talend.dq.helper.PropertyHelper;
import org.talend.dq.helper.RepositoryNodeHelper;
import org.talend.dq.indicators.definitions.DefinitionHandler;
import org.talend.dq.writer.impl.AnalysisWriter;
import org.talend.dq.writer.impl.ElementWriterFactory;
import org.talend.repository.ProjectManager;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.RepositoryNode;
import org.talend.resource.EResourceConstant;
import org.talend.utils.sugars.ReturnCode;
import org.talend.utils.sugars.TypedReturnCode;

/**
 * Create an Analysis which analyze duplicates.
 */
public class CreateDuplicatesAnalysisAction extends Action {

    private List<TdColumn> columns;

    public List<TdColumn> getColumns() {
        return this.columns;
    }

    public void setColumns(List<TdColumn> columns) {
        this.columns = columns;
    }

    private Connection connection;

    public Connection getConnection() {
        return this.connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    /**
     * DOC xqliu CreateDuplicatesAnalysisAction constructor comment.
     * 
     * @param columns
     * @param connection
     */
    public CreateDuplicatesAnalysisAction(List<TdColumn> columns, Connection connection) {
        this.setColumns(columns);
        this.setConnection(connection);
    }

    @Override
    public void run() {
        ReturnCode success = new ReturnCode(true);

        try {
            // create the analysis
            Analysis analysis = null;
            AnalysisBuilder analysisBuilder = new AnalysisBuilder();
            boolean analysisInitialized = analysisBuilder.initializeAnalysis(getAnalysisName(), AnalysisType.MULTIPLE_COLUMN);
            if (analysisInitialized) {
                analysis = analysisBuilder.getAnalysis();
            }

            fillMetadataToAnalysis(analysis, createDefaultAnalysisParameter());

            // add Connection into the Analysis Context
            analysis.getContext().setConnection(this.getConnection());

            for (TdColumn theColumn : this.getColumns()) {
                // add TdColumn into the Analysis Context
                analysis.getContext().getAnalysedElements().add(theColumn);

                // create Row Count Indicator
                RowCountIndicator rcIndicator = IndicatorsFactory.eINSTANCE.createRowCountIndicator();
                rcIndicator.setAnalyzedElement(theColumn);
                DefinitionHandler.getInstance().setDefaultIndicatorDefinition(rcIndicator);
                analysis.getResults().getIndicators().add(rcIndicator);

                // create Duplicate Count Indicator
                DuplicateCountIndicator dcIndicator = IndicatorsFactory.eINSTANCE.createDuplicateCountIndicator();
                dcIndicator.setAnalyzedElement(theColumn);
                DefinitionHandler.getInstance().setDefaultIndicatorDefinition(dcIndicator);
                analysis.getResults().getIndicators().add(dcIndicator);
            }

            // create dependencies
            DependenciesHandler.getInstance().setDependencyOn(analysis, this.getConnection());
            // set the domain
            for (Domain domain : analysis.getParameters().getDataFilter()) {
                domain.setName(analysis.getName());
            }
            // set execution language
            analysis.getParameters().setExecutionLanguage(ExecutionLanguage.SQL);

            // save the analysis
            RepositoryNode analysisRepNode = (RepositoryNode) RepositoryNodeHelper
                    .getDataProfilingFolderNode(EResourceConstant.ANALYSIS);
            IFolder folder = WorkbenchUtils.getFolder(analysisRepNode);
            AnalysisWriter analysisWriter = ElementWriterFactory.getInstance().createAnalysisWrite();
            TypedReturnCode<Object> create = analysisWriter.create(analysis, folder);
            if (create.isOk()) {
                // refresh the RepositoryView
                CorePlugin.getDefault().refreshDQView(analysisRepNode);
                // open the editor
                AnalysisItemEditorInput analysisEditorInput = new AnalysisItemEditorInput((Item) create.getObject());
                IRepositoryNode connectionRepNode = RepositoryNodeHelper.recursiveFind(this.getConnection());
                analysisEditorInput.setConnectionNode(connectionRepNode);
                CorePlugin.getDefault().openEditor(analysisEditorInput, AnalysisEditor.class.getName());
            } else {
                success.setOk(false);
                success.setMessage(create.getMessage()); //$NON-NLS-1$
            }
        } catch (Exception e) {
            success.setOk(false);
            success.setMessage(e.getMessage());
        }

        if (!success.isOk()) {
            MessageUI.openError(success.getMessage());
        }
    }

    /**
     * DOC xqliu Comment method "getAnalysisName".
     * 
     * @return
     */
    private String getAnalysisName() {
        String anaName = "AnalyzeDuplicatesOn_"; //$NON-NLS-1$
        TdColumn tdColumn = this.getColumns().get(0);
        anaName = anaName.concat(ColumnHelper.getColumnOwnerAsColumnSet(tdColumn).getName());
        SimpleDateFormat sf = new SimpleDateFormat("_yyyyMMddHHmmss"); //$NON-NLS-1$
        anaName = anaName.concat(sf.format(new Date()));
        // check the new Analysis Name
        String finalAnaName = anaName;
        int i = 1;
        while (PropertyHelper.existDuplicateName(finalAnaName, null, ERepositoryObjectType.TDQ_ANALYSIS_ELEMENT, true)) {
            finalAnaName = anaName + i;
            i++;
        }
        return finalAnaName;
    }

    /**
     * DOC xqliu Comment method "fillMetadataToAnalysis".
     * 
     * @param analysis
     * @param anaParam
     */
    private void fillMetadataToAnalysis(Analysis analysis, AnalysisParameter anaParam) {
        MetadataHelper.setDevStatus(analysis, anaParam.getStatus());
        MetadataHelper.setAuthor(analysis, anaParam.getAuthor());
        MetadataHelper.setPurpose(anaParam.getPurpose(), analysis);
        MetadataHelper.setDescription(anaParam.getDescription(), analysis);
        MetadataHelper.setVersion(anaParam.getVersion(), analysis);
    }

    /**
     * DOC xqliu Comment method "createDefaultAnalysisParameter".
     * 
     * @return
     */
    private AnalysisParameter createDefaultAnalysisParameter() {
        AnalysisParameter result = new AnalysisParameter();
        result.setStatus("development"); //$NON-NLS-1$
        result.setAuthor(ProjectManager.getInstance().getCurrentProject().getAuthor().getLogin());
        result.setPurpose(""); //$NON-NLS-1$
        TdColumn tdColumn = this.getColumns().get(0);
        result.setDescription("Analysis the duplicated columns on the table " + ColumnHelper.getColumnOwnerAsColumnSet(tdColumn).getName()); //$NON-NLS-1$
        result.setVersion(VersionUtils.DEFAULT_VERSION);
        return result;
    }
}
