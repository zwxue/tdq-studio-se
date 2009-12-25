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

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.talend.cwm.db.connection.MdmConnection;
import org.talend.cwm.exception.AnalysisExecutionException;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.cwm.management.i18n.Messages;
import org.talend.cwm.xml.TdXMLElement;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.analysis.AnalysisResult;
import org.talend.dataquality.helpers.BooleanExpressionHelper;
import org.talend.dataquality.helpers.IndicatorHelper;
import org.talend.dataquality.indicators.CompositeIndicator;
import org.talend.dataquality.indicators.Indicator;
import org.talend.utils.collections.MultiMapHelper;
import org.talend.utils.sugars.TypedReturnCode;
import orgomg.cwm.objectmodel.core.Expression;
import orgomg.cwm.objectmodel.core.ModelElement;
import orgomg.cwm.objectmodel.core.Namespace;

import Zql.ParseException;

/**
 * DOC xqliu class global comment. TODO 10238
 */
public class MdmAnalysisSqlExecutor extends MdmAnalysisExecutor {

    protected boolean parallel = true;

    private static Logger log = Logger.getLogger(MdmAnalysisSqlExecutor.class);

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
        ModelElement analyzedElement = indicator.getAnalyzedElement();
        if (analyzedElement == null) {
            return traceError("Analyzed element is null for indicator " + indicator.getName());
        }
        TdXMLElement xmlElement = SwitchHelpers.XMLELEMENT_SWITCH.doSwitch(indicator.getAnalyzedElement());
        if (xmlElement == null) {
            return traceError("Analyzed element is not a XmlElement for indicator " + indicator.getName());
        }
        String elementName = getFullXmlElementName(xmlElement);

        // completedSqlString is the final query
        String finalQuery = "count(" + elementName + ")";

        String language = dbms().getDbmsName();

        if (finalQuery != null) {
            Expression instantiateSqlExpression = BooleanExpressionHelper.createExpression(language, finalQuery);
            indicator.setInstantiatedExpression(instantiateSqlExpression);
            return true;
        }

        return false;
    }

    /**
     * DOC xqliu Comment method "getFullXmlElementName".
     * 
     * @param xmlElement
     * @return
     */
    private String getFullXmlElementName(TdXMLElement xmlElement) {
        Namespace namespace = xmlElement.getNamespace();
        return "//Country/" + xmlElement.getName();
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
        TypedReturnCode<MdmConnection> trc = this.getMdmConnection(analysis);
        if (!trc.isOk()) {
            throw new AnalysisExecutionException(Messages.getString(
                    "ColumnAnalysisSqlExecutor.CannotExecuteAnalysis", analysis.getName() //$NON-NLS-1$
                    , trc.getMessage()));
        }
        MdmConnection connection = trc.getObject();
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
        boolean ok = true;
        TypedReturnCode<MdmConnection> trc = this.getMdmConnection(analysis);
        if (!trc.isOk()) {
            return traceError("Cannot execute Analysis " + analysis.getName() + ". Error: " + trc.getMessage());
        }

        MdmConnection connection = trc.getObject();
        try {
            // store map of element to each indicator used for computation (leaf indicator)
            Map<ModelElement, List<Indicator>> elementToIndicator = new HashMap<ModelElement, List<Indicator>>();

            // execute the sql statement for each indicator
            Collection<Indicator> indicators = IndicatorHelper.getIndicatorLeaves(analysis.getResults());
            ok = runAnalysisIndicators(connection, elementToIndicator, indicators);
            // connection.close();

            // --- finalize indicators by setting the row count and null when they exist.
            setRowCountAndNullCount(elementToIndicator);

        } catch (SQLException e) {
            log.error(e, e);
            this.errorMessage = e.getMessage();
            ok = false;
        } finally {
            // ConnectionUtils.closeConnection(connection);
        }
        return ok;
    }

    /**
     * DOC xqliu Comment method "runAnalysisIndicators".
     * 
     * @param connection
     * @param elementToIndicator
     * @param indicators
     * @return
     * @throws SQLException
     */
    private boolean runAnalysisIndicators(MdmConnection connection, Map<ModelElement, List<Indicator>> elementToIndicator,
            Collection<Indicator> indicators) throws SQLException {
        boolean ok = true;
        for (Indicator indicator : indicators) {
            // skip composite indicators that do not require a sql execution
            if (indicator instanceof CompositeIndicator) {
                // options of composite indicators are handled elsewhere
                continue;
            }

            Expression query = dbms().getInstantiatedExpression(indicator);
            if (query == null || !executeQuery(indicator, connection, query.getBody())) {
                ok = traceError("Query not executed for indicator: \"" + indicator.getName() + "\" "
                        + ((query == null) ? "query is null" : "SQL query: " + query.getBody()));
            } else {
                // set computation done
                indicator.setComputed(true);
            }

            // add mapping of analyzed elements to their indicators
            MultiMapHelper.addUniqueObjectToListMap(indicator.getAnalyzedElement(), indicator, elementToIndicator);
        }
        return ok;
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
    protected boolean executeQuery(Indicator indicator, MdmConnection connection, String queryStmt) throws SQLException {
        String cat = getCatalogOrSchemaName(indicator.getAnalyzedElement());
        if (log.isInfoEnabled()) {
            log.info("Computing indicator: " + indicator.getName());
        }
        List<Object[]> myResultSet = executeQuery(cat, connection, queryStmt);

        // give result to indicator so that it handles the results
        boolean ret = false;
        try {
            ret = indicator.storeSqlResults(myResultSet);
        } catch (Exception e) {
            throw new SQLException(e.toString());
        }
        return ret;
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
    protected List<Object[]> executeQuery(String catalogName, MdmConnection connection, String queryStmt) throws SQLException {
        List<Object[]> myResultSet = new ArrayList<Object[]>();
        return myResultSet;
    }
}
