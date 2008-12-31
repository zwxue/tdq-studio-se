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
package org.talend.dq.analysis;

import java.sql.Connection;
import java.util.Date;

import org.apache.log4j.Logger;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.cwm.management.connection.JavaSqlFactory;
import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.analysis.AnalysisContext;
import org.talend.dataquality.analysis.AnalysisResult;
import org.talend.dataquality.analysis.ExecutionInformations;
import org.talend.utils.sugars.ReturnCode;
import org.talend.utils.sugars.TypedReturnCode;
import orgomg.cwm.foundation.softwaredeployment.DataManager;

/**
 * DOC scorreia class global comment. Detailled comment
 */
public abstract class AnalysisExecutor implements IAnalysisExecutor {

    private static Logger log = Logger.getLogger(AnalysisExecutor.class);

    protected String errorMessage;

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.analysis.IAnalysisExecutor#execute(org.talend.dataquality.analysis.Analysis)
     */
    public ReturnCode execute(final Analysis analysis) {
        // --- preconditions
        if (!check(analysis)) {
            return getReturnCode(false);
        }
        assert analysis != null;

        // --- creation time
        final ExecutionInformations resultMetadata = analysis.getResults().getResultMetadata();
        final long startime = System.currentTimeMillis();
        resultMetadata.setExecutionDate(new Date(startime));

        // --- create SQL statement
        String sql = createSqlStatement(analysis);
        if (sql == null) {
            return getReturnCode(false);
        }

        // --- run analysis
        boolean ok = false;
        try { // catch any exception
            ok = runAnalysis(analysis, sql);
        } catch (Exception e) {
            log.error(e, e);
        }

        // --- set metadata information of analysis
        resultMetadata.setLastRunOk(ok);
        int executionNumber = resultMetadata.getExecutionNumber() + 1;
        resultMetadata.setExecutionNumber(executionNumber);
        if (ok) {
            resultMetadata.setLastExecutionNumberOk(executionNumber);
            resultMetadata.setMessage(null); // reset error message
        } else {
            resultMetadata.setMessage(errorMessage);
        }

        // --- compute execution duration
        long endtime = System.currentTimeMillis();
        resultMetadata.setExecutionDuration((int) (endtime - startime));
        return getReturnCode(ok);
    }

    /**
     * Method "createSqlStatement".
     * 
     * @param analysis the analysis from which the SQL will be generated
     * @return the generated SQL statement or null if problem
     */
    protected abstract String createSqlStatement(Analysis analysis);

    /**
     * Method "getReturnCode".
     * 
     * @param ok
     * @return a return code with the last error message
     */
    protected ReturnCode getReturnCode(boolean ok) {
        return ok ? new ReturnCode() : new ReturnCode(this.errorMessage, false);
    }

    /**
     * Method "check" checks that the analysis can be run.
     * 
     * @param analysis the analysis to prepare
     * @return true if ok.
     */
    protected boolean check(Analysis analysis) {
        AnalysisContext context = analysis.getContext();
        if (context == null) {
            this.errorMessage = "Context is null in " + analysis.getName();
            return false;
        }
        DataManager connection = context.getConnection();
        if (connection == null) {
            this.errorMessage = "No connection found in context of " + analysis.getName();
            return false;
        }
        AnalysisResult results = analysis.getResults();
        if (results == null) {
            this.errorMessage = "Analysis " + analysis.getName() + " is not prepared correctly. No Result container.";
            return false;
        }
        return true;
    }

    /**
     * Method "runAnalysis".
     * 
     * @param analysis the analysis to be run
     * @param sqlStatement the sql statement to execute on Database
     * @return true if ok
     */
    protected abstract boolean runAnalysis(Analysis analysis, String sqlStatement);

    /**
     * DOC scorreia Comment method "getConnection".
     * 
     * @param analysis
     * @param schema
     * @return
     */
    protected TypedReturnCode<Connection> getConnection(Analysis analysis) {
        // MODSCA 2008-03-25 scorreia schema is not used. removed (was used before to select the catalog of the db)
        // now it is done elsewhere
        TypedReturnCode<Connection> rc = new TypedReturnCode<Connection>();

        DataManager datamanager = analysis.getContext().getConnection();
        if (datamanager == null) {
            rc.setReturnCode("Data manager is null for analysis " + analysis.getName(), false);
            return rc;
        }
        TdDataProvider dataprovider = SwitchHelpers.TDDATAPROVIDER_SWITCH.doSwitch(datamanager);
        if (dataprovider == null) {
            rc.setReturnCode("Data provider is null for data manager " + datamanager.getName() + " in analysis "
                    + analysis.getName(), false);
            return rc;
        }

        // else ok

        TypedReturnCode<Connection> connection = JavaSqlFactory.createConnection(dataprovider);
        if (!connection.isOk()) {
            rc.setReturnCode(connection.getMessage(), false);
            return rc;
        }
        // else ok
        rc.setObject(connection.getObject());
        return rc;

    }

}
