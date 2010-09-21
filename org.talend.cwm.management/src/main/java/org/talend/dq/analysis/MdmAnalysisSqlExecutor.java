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
package org.talend.dq.analysis;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.rpc.ServiceException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EClass;
import org.talend.cwm.db.connection.MdmStatement;
import org.talend.cwm.db.connection.MdmWebserviceConnection;
import org.talend.cwm.exception.AnalysisExecutionException;
import org.talend.cwm.helper.ResourceHelper;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.cwm.helper.XmlElementHelper;
import org.talend.cwm.relational.TdExpression;
import org.talend.cwm.xml.TdXmlElementType;
import org.talend.cwm.xml.TdXmlSchema;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.analysis.AnalysisResult;
import org.talend.dataquality.helpers.BooleanExpressionHelper;
import org.talend.dataquality.helpers.IndicatorHelper;
import org.talend.dataquality.indicators.CompositeIndicator;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.IndicatorsPackage;
import org.talend.dataquality.indicators.definition.IndicatorDefinition;
import org.talend.dq.dbms.DbmsLanguage;
import org.talend.dq.indicators.definitions.DefinitionHandler;
import org.talend.utils.collections.MultiMapHelper;
import org.talend.utils.sugars.TypedReturnCode;
import orgomg.cwm.objectmodel.core.Expression;
import orgomg.cwm.objectmodel.core.ModelElement;

import Zql.ParseException;

/**
 * DOC xqliu class global comment. TODO 10238
 */
public class MdmAnalysisSqlExecutor extends MdmAnalysisExecutor {

    private static Logger log = Logger.getLogger(MdmAnalysisSqlExecutor.class);

    private DbmsLanguage dbmsLanguage;

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
        TdXmlElementType xmlElement = SwitchHelpers.XMLELEMENTTYPE_SWITCH.doSwitch(indicator.getAnalyzedElement());
        if (xmlElement == null) {
            return traceError("Analyzed element is not a XmlElement for indicator " + indicator.getName());
        }
        // String elementName = XmlElementHelper.getFullName(xmlElement);
        String elementName = xmlElement.getName();
        // get correct language for current database
        String language = dbms().getDbmsName();
        Expression sqlGenericExpression = null;
        // --- create select statement
        // get indicator's sql columnS (generate the real SQL statement from its definition)
        IndicatorDefinition indicatorDefinition;
        String label = indicator.getIndicatorDefinition().getLabel();
        if (label == null || "".equals(label)) { //$NON-NLS-1$
            indicatorDefinition = indicator.getIndicatorDefinition();
        } else {
            indicatorDefinition = DefinitionHandler.getInstance().getIndicatorDefinition(label);
        }
        if (indicatorDefinition == null) {
            return traceError("INTERNAL ERROR: No indicator definition found for indicator " + indicator.getName()
                    + ". Please, report a bug at http://talendforge.org/bugs/");
        }
        sqlGenericExpression = dbms().getSqlExpression(indicatorDefinition);
        final EClass indicatorEclass = indicator.eClass();
        if (sqlGenericExpression == null || sqlGenericExpression.getBody() == null) {
            // when the indicator is a pattern indicator, a possible cause is that the DB does not support regular
            // expressions.
            if (IndicatorsPackage.eINSTANCE.getRegexpMatchingIndicator().equals(indicatorEclass)) {
                return traceError("Unsupported use of regular expressions on this database (" + language
                        + "). Remove all Pattern indicators from this analysis, please.");
            }
            return traceError("Unsupported Indicator. No SQL expression found for indicator "
                    + (indicator.getName() != null ? indicator.getName() : indicatorEclass.getName()) + " (UUID: "
                    + ResourceHelper.getUUID(indicatorDefinition) + ")");
        }
        // --- get indicator parameters and convert them into sql expression
        List<String> whereExpression = new ArrayList<String>();
        if (StringUtils.isNotBlank(dataFilterAsString)) {
            whereExpression.add(dataFilterAsString);
        }
        String parentElementName = XmlElementHelper.getParentElementName(xmlElement);
        // ### evaluate SQL Statement depending on indicators ###
        String completedSqlString = null;
        // --- default case
        completedSqlString = dbms().fillGenericQueryWithColumnsAndTable(sqlGenericExpression.getBody(), elementName,
                parentElementName);
        completedSqlString = addWhereToSqlStringStatement(whereExpression, completedSqlString);
        // completedSqlString is the final query
        String finalQuery = completedSqlString;
        if (finalQuery != null) {
            TdExpression instantiateSqlExpression = BooleanExpressionHelper.createTdExpression(language, finalQuery);
            indicator.setInstantiatedExpression(instantiateSqlExpression);
            return true;
        }
        return false;
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
        TypedReturnCode<MdmWebserviceConnection> trc = this.getMdmConnection(analysis);
        if (!trc.isOk()) {
            return traceError("Cannot execute Analysis " + analysis.getName() + ". Error: " + trc.getMessage());
        }

        MdmWebserviceConnection connection = trc.getObject();
        try {
            // store map of element to each indicator used for computation (leaf indicator)
            Map<ModelElement, List<Indicator>> elementToIndicator = new HashMap<ModelElement, List<Indicator>>();

            // execute the sql statement for each indicator
            Collection<Indicator> indicators = IndicatorHelper.getIndicatorLeaves(analysis.getResults());
            ok = runAnalysisIndicators(connection, elementToIndicator, indicators);
            // connection.close();

            // --- finalize indicators by setting the row count and null when they exist.
            setRowCountAndNullCount(elementToIndicator);

        } catch (Exception e) {
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
     * @throws ServiceException
     * @throws RemoteException
     */
    private boolean runAnalysisIndicators(MdmWebserviceConnection connection,
            Map<ModelElement, List<Indicator>> elementToIndicator, Collection<Indicator> indicators) throws RemoteException,
            ServiceException {
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
     * DOC xqliu Comment method "executeQuery".
     * 
     * @param indicator
     * @param connection
     * @param queryStmt
     * @return
     * @throws ServiceException
     * @throws RemoteException
     */
    protected boolean executeQuery(Indicator indicator, MdmWebserviceConnection connection, String queryStmt)
            throws RemoteException, ServiceException {
        TdXmlElementType analyzedElement = (TdXmlElementType) indicator.getAnalyzedElement();
        TdXmlSchema xmlDocument = analyzedElement.getOwnedDocument();
        if (log.isInfoEnabled()) {
            log.info("Computing indicator: " + indicator.getName());
        }
        List<Object[]> myResultSet = executeQuery(xmlDocument, connection, queryStmt);

        // give result to indicator so that it handles the results
        boolean ret = false;
        try {
            ret = indicator.storeSqlResults(myResultSet);
        } catch (Exception e) {
            throw new RemoteException(e.toString());
        }
        return ret;
    }

    /**
     * DOC xqliu Comment method "executeQuery".
     * 
     * @param xmlDocument
     * @param connection
     * @param queryStmt
     * @return
     * @throws ServiceException
     * @throws RemoteException
     */
    protected List<Object[]> executeQuery(TdXmlSchema xmlDocument, MdmWebserviceConnection connection, String queryStmt)
            throws RemoteException, ServiceException {
        // create query statement
        MdmStatement statement = connection.createStatement();
        // statement.setFetchSize(fetchSize);
        if (log.isInfoEnabled()) {
            log.info("Executing query: " + queryStmt);
        }

        if (continueRun()) {
            // MOD xqliu 2010-04-15 bug 12568
            statement.execute(xmlDocument, queryStmt);
        }

        // TODO should support several columns result
        // get the results
        String[] resultSet = statement.getResultSet();
        if (resultSet == null) {
            String mess = "No result set for this statement: " + queryStmt;
            log.warn(mess);
            return null;
        }

        List<Object[]> myResultSet = new ArrayList<Object[]>();
        for (String rs : resultSet) {
            Object[] result = new Object[1];
            result[0] = rs;
            myResultSet.add(result);
        }

        return myResultSet;
    }

    /**
     * DOC xqliu Comment method "addWhereToSqlStringStatement".
     * 
     * @param whereExpressions
     * @param completedSqlString
     * @return
     * @throws ParseException
     */
    private String addWhereToSqlStringStatement(List<String> whereExpressions, String completedSqlString) throws ParseException {
        return dbms().addWhereToSqlStringStatement(completedSqlString, whereExpressions);
    }
}
