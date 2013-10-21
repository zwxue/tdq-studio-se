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
package org.talend.dq.analysis;

import java.util.Arrays;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.talend.commons.emf.EMFUtil;
import org.talend.cwm.dependencies.DependenciesHandler;
import org.talend.dataquality.analysis.Analysis;
import org.talend.utils.sugars.TypedReturnCode;
import orgomg.cwm.foundation.softwaredeployment.DataManager;
import orgomg.cwm.objectmodel.core.Dependency;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC yyin class global comment. Detailled comment
 */
public class MatchAnalysisHandler extends AnalysisHandler {

    private static Logger log = Logger.getLogger(MatchAnalysisHandler.class);

    private ModelElement[] selectedColumns = null;

    private DataManager connection;

    @Override
    public void setAnalysis(Analysis columnAnalysis) {
        super.setAnalysis(columnAnalysis);
        initSelectedColumns();
        initConnection();
    }

    /**
     * DOC yyin Comment method "initConnection".
     */
    private void initConnection() {
        connection = analysis.getContext().getConnection();
    }

    public void SetConnection(DataManager newConnection) {
        // remove the old dependencies if any
        if (this.connection != null) {
            DependenciesHandler.getInstance().removeDependenciesBetweenModel(connection, analysis);
            EMFUtil.saveSingleResource(connection.eResource());
        }// ~
        this.connection = newConnection;
    }

    public DataManager getConnection() {
        return this.connection;
    }

    public void saveConnection() {
        assert analysis != null;
        assert analysis.getContext() != null;
        analysis.getContext().setConnection(connection);

        // Added TDQ-8183 add db dependency on match analysis
        TypedReturnCode<Dependency> rc = DependenciesHandler.getInstance().setDependencyOn(analysis, connection);
        if (!rc.isOk()) {
            log.info("fail to save dependency analysis:" + analysis.getFileName());//$NON-NLS-1$
        }// ~
    }

    /**
     * DOC yyin Comment method "initSelectedColumns".
     */
    private void initSelectedColumns() {
        EList<ModelElement> analyzedColumns = getAnalyzedColumns();
        selectedColumns = new ModelElement[analyzedColumns.size()];
        int i = 0;
        for (ModelElement element : analyzedColumns) {
            selectedColumns[i++] = element;
        }
    }

    public ModelElement[] getSelectedColumns() {
        return this.selectedColumns;
    }

    public void setSelectedColumns(ModelElement[] columns) {
        this.selectedColumns = columns;
    }

    public boolean addColumnToAnalyze(ModelElement modelElement) {
        assert analysis != null;
        assert analysis.getContext() != null;
        return analysis.getContext().getAnalysedElements().add(modelElement);
    }

    public boolean addColumnsToAnalyze(Collection<ModelElement> modelElement) {
        assert analysis != null;
        assert analysis.getContext() != null;
        return analysis.getContext().getAnalysedElements().addAll(modelElement);
    }

    // modify the analysis's analyzed elements
    public boolean saveSelectedAnalyzedElements() {
        assert analysis != null;
        assert analysis.getContext() != null;
        analysis.getContext().getAnalysedElements().clear();
        if (selectedColumns == null) {
            return true;
        }
        return analysis.getContext().getAnalysedElements().addAll(Arrays.asList(selectedColumns));
    }

    /**
     * DOC yyin Comment method "changeDefaultRowLoaded".
     * 
     * @param text
     */
    public void changeDefaultRowLoaded(String text) {
        analysis.getParameters().setMaxNumberRows(Integer.valueOf(text));
    }

    /**
     * find in analysis's parameter if has: DefaultLoadedRowCount".if has:get its value, if not, create one
     * 
     * @return
     */
    public String getDefaultLoadedRowCount() {
        return String.valueOf(analysis.getParameters().getMaxNumberRows());
    }

}
