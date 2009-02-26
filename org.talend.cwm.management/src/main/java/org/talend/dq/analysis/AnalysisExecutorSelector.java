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
package org.talend.dq.analysis;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.analysis.AnalysisType;
import org.talend.dataquality.analysis.ExecutionLanguage;
import org.talend.dataquality.helpers.AnalysisHelper;
import org.talend.i18n.Messages;
import org.talend.utils.sugars.ReturnCode;

/**
 * @author scorreia
 * 
 * A class to get the correct executor for a given analysis.
 */
public final class AnalysisExecutorSelector {

    private static Logger log = Logger.getLogger(AnalysisExecutorSelector.class);

    private AnalysisExecutorSelector() {
    }

    /**
     * Method "getAnalysisExecutor".
     * 
     * @param analysis the analysis to be run by the executor.
     * @return the appropriate executor of the analysis or null when no appropriate executor has been found.
     */
    public static IAnalysisExecutor getAnalysisExecutor(Analysis analysis) {
        assert analysis != null;
        AnalysisType analysisType = AnalysisHelper.getAnalysisType(analysis);
        if (analysisType == null) {
            log.error("Analysis type is not set for analysis" + analysis.getName()); //$NON-NLS-1$
            return null;
        }
        ExecutionLanguage executionEngine = AnalysisHelper.getExecutionEngine(analysis);
        AnalysisExecutor exec = null;
        switch (analysisType) {
        case MULTIPLE_COLUMN:
            exec = executionEngine != null ? new ColumnAnalysisSqlExecutor() : new ColumnAnalysisExecutor();
            break;
        case CONNECTION:
            exec = new ConnectionAnalysisExecutor();
            break;
        case SCHEMA:
            exec = new SchemaAnalysisExecutor();
            break;
        case CATALOG:
            exec = new CatalogAnalysisExecutor();
            break;
        case COLUMNS_COMPARISON:
            exec = new RowMatchingAnalysisExecutor();
            break;
        case COLUMN_CORRELATION:
            exec = new MultiColumnAnalysisExecutor();
            break;
        case TABLE:
            exec = ExecutionLanguage.SQL.equals(executionEngine) ? new TableAnalysisSqlExecutor() : new TableAnalysisExecutor();
            break;
        default:
            // this should not happen. This executor has not been tested for a long time.
            exec = null;
        }
        return exec;
    }

    /**
     * Method "executeAnalysis".
     * 
     * @param analysis an analysis to be run
     * @return a return code with an error message when the analysis failed to run.
     */
    public static ReturnCode executeAnalysis(Analysis analysis) {
        return executeAnalysis(analysis, null);
    }

    /**
     * 
     * DOC xqliu Comment method "executeAnalysis".
     * 
     * @param analysis
     * @param monitor
     * @return
     */
    public static ReturnCode executeAnalysis(Analysis analysis, IProgressMonitor monitor) {
        IAnalysisExecutor analysisExecutor = getAnalysisExecutor(analysis);
        if (analysisExecutor != null) {
            // MOD xqliu 2009-02-09 bug 6237
            analysisExecutor.setMonitor(monitor);
            return analysisExecutor.execute(analysis);
        }
        // else
        return new ReturnCode(Messages.getString("AnalysisExecutorSelector.NotFindHowExecute", analysis.getName()), false); //$NON-NLS-1$
    }
}
