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
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.talend.cwm.exception.AnalysisExecutionException;
import org.talend.cwm.management.i18n.Messages;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.analysis.AnalysisResult;
import org.talend.dataquality.helpers.IndicatorHelper;
import org.talend.dataquality.indicators.Indicator;
import org.talend.utils.sugars.TypedReturnCode;
import orgomg.cwm.objectmodel.core.ModelElement;

import Zql.ParseException;

/**
 * DOC xqliu class global comment. TODO 10238
 */
public class XmlElementAnalysisSqlExecutor extends XmlElementAnalysisExecutor {

    protected boolean parallel = true;

    private static Logger log = Logger.getLogger(XmlElementAnalysisSqlExecutor.class);

    @Override
    protected String createSqlStatement(Analysis analysis) {
        this.cachedAnalysis = analysis;
        AnalysisResult results = analysis.getResults();
        assert results != null;

        try {
            // --- get data filter
            ModelElementAnalysisHandler handler = new ModelElementAnalysisHandler();
            handler.setAnalysis(analysis);
            String stringDataFilter = handler.getStringDataFilter();

            // --- get all the leaf indicators used for the sql computation
            Collection<Indicator> leafIndicators = IndicatorHelper.getIndicatorLeaves(results);
            // --- create one sql statement for each leaf indicator
            for (Indicator indicator : leafIndicators) {
                if (!createSqlQuery(stringDataFilter, indicator)) {
                    log.error("Error when creating query with indicator " + indicator.getName());
                    // return null;
                }
            }
        } catch (ParseException e) {
            log.error(e, e);
            return null;
        } catch (AnalysisExecutionException e) {
            log.error(e, e);
            return null;
        }

        return ""; //$NON-NLS-1$
    }

    /**
     * DOC xqliu Comment method "createSqlQuery".
     * 
     * @param dataFilterAsString
     * @param indicator
     * @return
     * @throws ParseException
     * @throws AnalysisExecutionException
     */
    private boolean createSqlQuery(String dataFilterAsString, Indicator indicator) throws ParseException,
            AnalysisExecutionException {
        return true;
    }

    /**
     * DOC xqliu Comment method "getCount".
     * 
     * @param analysis
     * @param colName
     * @param table
     * @param catalog
     * @param whereExpression
     * @return
     * @throws AnalysisExecutionException
     */
    protected Long getCount(Analysis analysis, String colName, String table, String catalog, List<String> whereExpression)
            throws AnalysisExecutionException {
        try {
            return getCountLow(analysis, colName, table, catalog, whereExpression);
        } catch (SQLException e) {
            throw new AnalysisExecutionException(Messages.getString("ColumnAnalysisSqlExecutor.CannotGetCount",//$NON-NLS-1$
                    analysis.getName(), colName, dbms().toQualifiedName(catalog, null, table)), e);
        }
    }

    /**
     * DOC xqliu Comment method "getCountLow".
     * 
     * @param analysis
     * @param colName
     * @param table
     * @param catalogName
     * @param whereExpression
     * @return
     * @throws SQLException
     * @throws AnalysisExecutionException
     */
    private Long getCountLow(Analysis analysis, String colName, String table, String catalogName, List<String> whereExpression)
            throws SQLException, AnalysisExecutionException {
        TypedReturnCode<Connection> trc = this.getConnection(analysis);
        if (!trc.isOk()) {
            throw new AnalysisExecutionException(Messages.getString(
                    "ColumnAnalysisSqlExecutor.CannotExecuteAnalysis", analysis.getName() //$NON-NLS-1$
                    , trc.getMessage()));
        }
        Connection connection = trc.getObject();
        String whereExp = (whereExpression == null || whereExpression.isEmpty()) ? "" : " WHERE " //$NON-NLS-1$ //$NON-NLS-2$
                + dbms().buildWhereExpression(whereExpression);
        String queryStmt = "SELECT COUNT(" + colName + ") FROM " + table + whereExp; // + dbms().eos(); //$NON-NLS-1$ //$NON-NLS-2$

        List<Object[]> myResultSet = executeQuery(catalogName, connection, queryStmt);

        if (myResultSet.isEmpty() || myResultSet.size() > 1) {
            log.error("Too many result obtained for a simple count: " + myResultSet);
            return -1L;
        }
        return Long.valueOf(String.valueOf(myResultSet.get(0)[0]));
    }

    /**
     * DOC xqliu Comment method "replaceVariablesLow".
     * 
     * @param sqlGenericString
     * @param arguments
     * @return
     */
    protected String replaceVariablesLow(String sqlGenericString, Object... arguments) {
        String toFormat = surroundSingleQuotes(sqlGenericString);
        return MessageFormat.format(toFormat, arguments);
    }

    /**
     * DOC xqliu Comment method "surroundSingleQuotes".
     * 
     * @param sqlGenericString
     * @return
     */
    private String surroundSingleQuotes(String sqlGenericString) {
        return sqlGenericString.replaceAll("'", "''"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * DOC xqliu Comment method "traceError".
     * 
     * @param error
     * @return
     */
    protected boolean traceError(String error) {
        this.errorMessage = error;
        log.error(this.errorMessage);
        return false;
    }

    @Override
    protected boolean runAnalysis(Analysis analysis, String sqlStatement) {
        return true;
    }

    /**
     * DOC xqliu Comment method "setRowCountAndNullCount".
     * 
     * @param elementToIndicator
     */
    protected void setRowCountAndNullCount(Map<ModelElement, List<Indicator>> elementToIndicator) {
    }

    /**
     * DOC xqliu Comment method "getCatalogOrSchemaName".
     * 
     * @param analyzedElement
     * @return
     */
    protected String getCatalogOrSchemaName(ModelElement analyzedElement) {
        return analyzedElement.getName();
    }

    /**
     * DOC xqliu Comment method "executeQuery".
     * 
     * @param indicator
     * @param connection
     * @param queryStmt
     * @return
     * @throws SQLException
     */
    protected boolean executeQuery(Indicator indicator, Connection connection, String queryStmt) throws SQLException {
        return true;
    }

    /**
     * DOC xqliu Comment method "executeQuery".
     * 
     * @param catalogName
     * @param connection
     * @param queryStmt
     * @return
     * @throws SQLException
     */
    protected List<Object[]> executeQuery(String catalogName, Connection connection, String queryStmt) throws SQLException {
        List<Object[]> myResultSet = new ArrayList<Object[]>();
        return myResultSet;
    }



}
