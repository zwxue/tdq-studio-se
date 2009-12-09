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

import java.sql.Connection;
import java.util.Date;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.talend.cwm.helper.CatalogHelper;
import org.talend.cwm.helper.DataProviderHelper;
import org.talend.cwm.helper.SchemaHelper;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.cwm.management.connection.JavaSqlFactory;
import org.talend.cwm.management.i18n.Messages;
import org.talend.cwm.relational.TdCatalog;
import org.talend.cwm.relational.TdSchema;
import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.cwm.softwaredeployment.TdProviderConnection;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.analysis.AnalysisContext;
import org.talend.dataquality.analysis.AnalysisResult;
import org.talend.dataquality.analysis.ExecutionInformations;
import org.talend.dq.dbms.DbmsLanguage;
import org.talend.dq.dbms.DbmsLanguageFactory;
import org.talend.utils.sugars.ReturnCode;
import org.talend.utils.sugars.TypedReturnCode;
import orgomg.cwm.foundation.softwaredeployment.DataManager;
import orgomg.cwm.foundation.softwaredeployment.DataProvider;
import orgomg.cwm.foundation.softwaredeployment.SoftwaredeploymentPackage;
import orgomg.cwm.objectmodel.core.ModelElement;
import orgomg.cwm.resource.relational.ColumnSet;

/**
 * DOC scorreia class global comment. Detailled comment
 */
public abstract class AnalysisExecutor implements IAnalysisExecutor {

    private static Logger log = Logger.getLogger(AnalysisExecutor.class);

    protected String errorMessage;

    /**
     * use {@link #dbms()} to access this attribute.
     */
    private DbmsLanguage dbmsLanguage;
    
    protected Analysis cachedAnalysis;

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
        return new ReturnCode(this.errorMessage, ok);
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
            this.errorMessage = Messages.getString("AnalysisExecutor.ContextNull", analysis.getName()); //$NON-NLS-1$
            return false;
        }
        DataManager connection = context.getConnection();
        if (connection == null) {
            this.errorMessage = Messages.getString("AnalysisExecutor.NoConnectionFound", analysis.getName()); //$NON-NLS-1$
            return false;
        }
        if (log.isInfoEnabled()) {
            if (SoftwaredeploymentPackage.eINSTANCE.getDataProvider().isInstance(connection)) {
                TypedReturnCode<TdProviderConnection> rc = DataProviderHelper
                        .getTdProviderConnection((DataProvider) connection);
                TdProviderConnection providerConnection = rc.getObject();
                log.info("Connection to " + providerConnection.getConnectionString());
            }
        }
        AnalysisResult results = analysis.getResults();
        if (results == null) {
            this.errorMessage = Messages.getString("AnalysisExecutor.AnalysisnotNotPrepareCorrect", analysis.getName()); //$NON-NLS-1$
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
            rc.setReturnCode(Messages.getString("AnalysisExecutor.DataManagerNull", analysis.getName()), false); //$NON-NLS-1$
            return rc;
        }
        TdDataProvider dataprovider = SwitchHelpers.TDDATAPROVIDER_SWITCH.doSwitch(datamanager);
        if (dataprovider == null) {
            rc.setReturnCode(Messages.getString("AnalysisExecutor.DataProviderNull", datamanager.getName(), //$NON-NLS-1$
                    analysis.getName()), false);
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

    private IProgressMonitor monitor;

    public IProgressMonitor getMonitor() {
        return monitor;
    }

    public void setMonitor(IProgressMonitor monitor) {
        this.monitor = monitor;
    }

    protected boolean continueRun() {
        boolean ret = true;
        if (getMonitor() != null && getMonitor().isCanceled()) {
            ret = false;
        }
        return ret;
    }

    /**
     * Method "getCatalogName".
     * 
     * @param analyzedElement
     * @return the catalog or schema quoted name
     */
    protected String getQuotedCatalogName(ModelElement analyzedElement) {
        final TdCatalog parentCatalog = CatalogHelper.getParentCatalog(analyzedElement);
        return parentCatalog == null ? null : quote(parentCatalog.getName());
    }

    /**
     * DOC scorreia Comment method "getSchemaName".
     * 
     * @param columnSetOwner
     * @return
     */
    protected String getQuotedSchemaName(ColumnSet columnSetOwner) {
        final TdSchema parentSchema = SchemaHelper.getParentSchema(columnSetOwner);
        return (parentSchema == null) ? null : quote(parentSchema.getName());
    }

    /**
     * Method "dbms".
     * 
     * @return the DBMS language (not null)
     */
    protected DbmsLanguage dbms() {
        if (this.dbmsLanguage == null) {
            this.dbmsLanguage = createDbmsLanguage();
        }
        return this.dbmsLanguage;
    }

    DbmsLanguage createDbmsLanguage() {
        DataManager connection = this.cachedAnalysis.getContext().getConnection();
        return DbmsLanguageFactory.createDbmsLanguage(connection);
    }

    /**
     * Method "quote".
     * 
     * @param input
     * @return the given string between quotes (for SQL)
     */
    protected String quote(String input) {
        return dbms().quote(input);
    }

}
