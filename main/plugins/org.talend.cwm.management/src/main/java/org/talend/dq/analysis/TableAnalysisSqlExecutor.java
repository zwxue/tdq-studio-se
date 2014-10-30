// ============================================================================
//
// Copyright (C) 2006-2014 Talend Inc. - www.talend.com
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
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.swt.widgets.Display;
import org.talend.core.ITDQRepositoryService;
import org.talend.core.model.metadata.builder.database.ExtractMetaDataUtils;
import org.talend.cwm.db.connection.ConnectionUtils;
import org.talend.cwm.exception.AnalysisExecutionException;
import org.talend.cwm.helper.CatalogHelper;
import org.talend.cwm.helper.ColumnSetHelper;
import org.talend.cwm.helper.ResourceHelper;
import org.talend.cwm.helper.SchemaHelper;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.cwm.management.i18n.Messages;
import org.talend.cwm.relational.TdExpression;
import org.talend.cwm.relational.TdTable;
import org.talend.dataquality.PluginConstant;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.analysis.AnalysisResult;
import org.talend.dataquality.helpers.BooleanExpressionHelper;
import org.talend.dataquality.helpers.IndicatorHelper;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.definition.IndicatorDefinition;
import org.talend.dataquality.indicators.sql.WhereRuleAideIndicator;
import org.talend.dataquality.indicators.sql.WhereRuleIndicator;
import org.talend.dataquality.rules.JoinElement;
import org.talend.dataquality.rules.RulesPackage;
import org.talend.dataquality.rules.WhereRule;
import org.talend.dq.dbms.DbmsLanguage;
import org.talend.dq.helper.AnalysisExecutorHelper;
import org.talend.dq.helper.ContextHelper;
import org.talend.dq.indicators.IndicatorCommonUtil;
import org.talend.utils.sugars.ReturnCode;
import org.talend.utils.sugars.TypedReturnCode;
import orgomg.cwm.objectmodel.core.Expression;
import orgomg.cwm.objectmodel.core.ModelElement;
import orgomg.cwm.objectmodel.core.Package;
import orgomg.cwm.resource.relational.Catalog;
import orgomg.cwm.resource.relational.NamedColumnSet;
import orgomg.cwm.resource.relational.RelationalPackage;
import orgomg.cwm.resource.relational.Schema;

import Zql.ParseException;

/**
 * DOC xqliu class global comment. Detailled comment
 */
public class TableAnalysisSqlExecutor extends TableAnalysisExecutor {

    private static Logger log = Logger.getLogger(TableAnalysisSqlExecutor.class);

    private DbmsLanguage dbmsLanguage;

    @Override
    protected String createSqlStatement(Analysis analysis) {
        this.cachedAnalysis = analysis;
        AnalysisResult results = analysis.getResults();
        assert results != null;
        try {
            // --- get data filter
            String stringDataFilter = ContextHelper.getDataFilterWithoutContext(analysis);
            // --- get all the leaf indicators used for the sql computation
            Collection<Indicator> leafIndicators = IndicatorHelper.getIndicatorLeaves(results);
            // --- create one sql statement for each leaf indicator
            for (Indicator indicator : leafIndicators) {
                if (!createSqlQuery(stringDataFilter, indicator)) {
                    log.error(Messages.getString("ColumnAnalysisSqlExecutor.CREATEQUERYERROR") + indicator.getName()); //$NON-NLS-1$
                    return null;
                }
            }
        } catch (ParseException e) {
            log.error(e, e);
            return null;
        } catch (AnalysisExecutionException e) {
            log.error(e, e);
            return null;
        }
        return PluginConstant.EMPTY_STRING;
    }

    private boolean createSqlQuery(String dataFilterAsString, Indicator indicator) throws ParseException,
            AnalysisExecutionException {
        // TDQ-9294 if the WhereRuleAideIndicator don't contain any join condictions, it result is same with row count,
        // so just return true and get the row count from RowCount indicator
        if (isAideAndJoinEmpty(indicator)) {
            return true;
        }
        // ~ TDQ-9294
        ModelElement analyzedElement = indicator.getAnalyzedElement();
        if (analyzedElement == null) {
            traceError("Analyzed element is null for indicator " + indicator.getName());//$NON-NLS-1$
            return Boolean.FALSE;
        }
        NamedColumnSet set = SwitchHelpers.NAMED_COLUMN_SET_SWITCH.doSwitch(indicator.getAnalyzedElement());
        if (set == null) {
            traceError("Analyzed element is not a table for indicator " + indicator.getName());//$NON-NLS-1$
            return Boolean.FALSE;
        }
        // --- get the schema owner
        String setName = quote(set.getName());
        if (!belongToSameSchemata(set)) {
            StringBuffer buf = new StringBuffer();
            for (orgomg.cwm.objectmodel.core.Package schema : schemata.values()) {
                buf.append(schema.getName() + " "); //$NON-NLS-1$
            }
            log.error(Messages
                    .getString("ColumnAnalysisSqlExecutor.COLUMNNOTBELONGTOEXISTSCHEMA", setName, buf.toString().trim()));//$NON-NLS-1$
            return false;
        }

        // get correct language for current database
        String language = dbms().getDbmsName();
        Expression sqlGenericExpression = null;

        // --- create select statement
        // get indicator's sql tableS (generate the real SQL statement from its definition)

        IndicatorDefinition indicatorDefinition = indicator.getIndicatorDefinition();
        if (indicatorDefinition == null) {
            traceError(Messages.getString("ColumnAnalysisSqlExecutor.INTERNALERROR", indicator.getName()));//$NON-NLS-1$
            return Boolean.FALSE;
        }
        sqlGenericExpression = dbms().getSqlExpression(indicatorDefinition);

        final EClass indicatorEclass = indicator.eClass();
        if (sqlGenericExpression == null || sqlGenericExpression.getBody() == null) {
            traceError(Messages.getString(
                    "ColumnAnalysisSqlExecutor.UNSUPPORTEDINDICATOR",//$NON-NLS-1$
                    (indicator.getName() != null ? indicator.getName() : indicatorEclass.getName()),
                    ResourceHelper.getUUID(indicatorDefinition)));
            return Boolean.FALSE;
        }

        // --- get indicator parameters and convert them into sql expression
        List<String> whereExpressionAnalysis = new ArrayList<String>();
        if (StringUtils.isNotBlank(dataFilterAsString)) {
            whereExpressionAnalysis.add(dataFilterAsString);
        }
        List<String> whereExpressionDQRule = new ArrayList<String>();
        final EList<JoinElement> joinConditions = indicator.getJoinConditions();
        if (RulesPackage.eINSTANCE.getWhereRule().equals(indicatorDefinition.eClass())) {
            WhereRule wr = (WhereRule) indicatorDefinition;
            whereExpressionDQRule.add(wr.getWhereExpression());

            // MOD scorreia 2009-03-13 copy joins conditions into the indicator
            joinConditions.clear();
            if (!wr.getJoins().isEmpty()) {
                for (JoinElement joinelt : wr.getJoins()) {
                    JoinElement joinCopy = EcoreUtil.copy(joinelt);
                    joinConditions.add(joinCopy);
                }
            }
        }

        String schemaName = getQuotedSchemaName(set);

        // --- normalize table name
        String catalogName = getQuotedCatalogName(set);
        if (catalogName == null && schemaName != null) {
            // try to get catalog above schema
            final Schema parentSchema = SchemaHelper.getParentSchema(set);
            final Catalog parentCatalog = CatalogHelper.getParentCatalog(parentSchema);
            catalogName = parentCatalog != null ? parentCatalog.getName() : null;
        }

        setName = dbms().toQualifiedName(catalogName, schemaName, setName);

        // ### evaluate SQL Statement depending on indicators ###
        String completedSqlString = null;

        // --- default case
        // allow join
        String joinclause = (!joinConditions.isEmpty()) ? dbms().createJoinConditionAsString(set, joinConditions, catalogName,
                schemaName) : PluginConstant.EMPTY_STRING;

        completedSqlString = dbms().fillGenericQueryWithJoin(sqlGenericExpression.getBody(), setName, joinclause);
        // ~
        // ADD xqliu 2012-04-23 TDQ-5057
        if (indicator instanceof WhereRuleAideIndicator) {
            whereExpressionDQRule = new ArrayList<String>();
        }
        // ~ TDQ-5057
        completedSqlString = addWhereToSqlStringStatement(whereExpressionAnalysis, whereExpressionDQRule, completedSqlString,
                true);

        // completedSqlString is the final query
        String finalQuery = completedSqlString;

        TdExpression instantiateSqlExpression = BooleanExpressionHelper.createTdExpression(language, finalQuery);
        indicator.setInstantiatedExpression(instantiateSqlExpression);
        return true;
    }

    /**
     * DOC yyin Comment method "isAideAndJoinEmpty".
     * 
     * @param indicator
     * @return
     */
    private boolean isAideAndJoinEmpty(Indicator indicator) {
        return indicator instanceof WhereRuleAideIndicator
                && ((WhereRule) indicator.getIndicatorDefinition()).getJoins().isEmpty();
    }

    protected TypedReturnCode<Boolean> belongToSameSchemata(final TdTable tdTable) {
        TypedReturnCode<Boolean> returnCode = new TypedReturnCode<Boolean>(Boolean.TRUE);
        assert tdTable != null;
        if (schemata.get(tdTable) != null) {
            return returnCode;
        }
        // get catalog or schema
        Package schema = ColumnSetHelper.getParentCatalogOrSchema(tdTable);
        if (schema == null) {
            String errorMessage = Messages.getString("TableAnalysisSqlExecutor.NoSchemaOrCatalogFound", tdTable.getName()); //$NON-NLS-1$
            returnCode.setMessage(errorMessage);
            returnCode.setOk(Boolean.FALSE);
            return returnCode;
        }
        schemata.put(tdTable, schema);
        return returnCode;
    }

    @Override
    protected DbmsLanguage dbms() {
        if (this.dbmsLanguage == null) {
            this.dbmsLanguage = createDbmsLanguage();
        }
        return this.dbmsLanguage;
    }

    /**
     * add the where clause to the sql statement.
     * 
     * @param whereExpressionsAnalysis the list of Analysis's where expressions to concatenate (must not be null)
     * @param whereExpressionsDQRule the list of DQRule's where expressions to concatenate (must not be null)
     * @param completedSqlString a generic SQL expression in which the where clause variable will be replaced.
     * @param valid if false add ! before where clause
     * @return the SQL statement with the where clause
     * @throws ParseException
     */
    private String addWhereToSqlStringStatement(List<String> whereExpressionsAnalysis, List<String> whereExpressionsDQRule,
            String completedSqlString, boolean valid) throws ParseException {
        String query = completedSqlString;

        // add Analysis's where expression
        StringBuffer buf1 = new StringBuffer();
        for (int i = 0; i < whereExpressionsAnalysis.size(); i++) {
            String exp = whereExpressionsAnalysis.get(i);
            buf1.append(this.surroundWith('(', exp, ')'));
            if (i != whereExpressionsAnalysis.size() - 1 || whereExpressionsDQRule.size() > 0) {
                buf1.append(dbms().and());
            }
        }
        // add DQRule's where expression
        StringBuffer buf2 = new StringBuffer();
        String dqruleWhereClause = buf2.toString();
        for (int i = 0; i < whereExpressionsDQRule.size(); i++) {
            String exp = whereExpressionsDQRule.get(i);
            buf2.append(this.surroundWith('(', exp, ')'));
            if (i != whereExpressionsDQRule.size() - 1) {
                buf2.append(dbms().and());
            }
        }
        // only negate DQRule's where expression
        if (valid) {
            dqruleWhereClause = buf2.toString();
        } else {
            dqruleWhereClause = dbms().not() + "(" + buf2.toString() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
        }
        String where = buf1.toString() + dqruleWhereClause;

        if (where != null) {
            query = dbms().addWhereToStatement(query, where);
        }
        return query;
    }

    private String surroundWith(char left, String toSurround, char right) {
        return left + toSurround + right;
    }

    @Override
    protected boolean runAnalysis(Analysis analysis, String sqlStatement) {
        boolean isSuccess = true;

        TypedReturnCode<java.sql.Connection> trc = this.getConnectionBeforeRun(analysis);

        if (!trc.isOk()) {
            log.error(trc.getMessage());
            traceError(Messages.getString(
                    "FunctionalDependencyExecutor.CANNOTEXECUTEANALYSIS", analysis.getName(), trc.getMessage()));//$NON-NLS-1$
            return Boolean.FALSE;
        }

        Connection connection = trc.getObject();
        try {
            Long rowCount = 0L;

            List<Indicator> indicatorList = IndicatorHelper.getIndicatorLeaves(analysis.getResults());

            // execute the row count
            Indicator rowIndicator = indicatorList.get(0);
            isSuccess = executeIndicator(rowIndicator, connection);
            publishDynamicEvent(rowIndicator);
            // remember the row count
            rowCount = rowIndicator.getCount();

            // After execute the row count, group the rules before executing
            List<List<Indicator>> indicators = groupAideRule(indicatorList);

            // execute the sql statement for each group of aide and rule
            for (final List<Indicator> aideAndRule : indicators) {
                Long aideCount = -1L;

                Indicator aide = aideAndRule.get(0);
                final Indicator rule = aideAndRule.get(1);
                // TDQ-9294 if the WhereRuleAideIndicator don't contain any join condictions, it result is same with
                // row count, so will not generate query for it
                if (!isAideAndJoinEmpty(aide)) {
                    isSuccess = executeIndicator(aide, connection);
                    aideCount = ((WhereRuleAideIndicator) aide).getUserCount();
                }

                isSuccess = executeIndicator(rule, connection);
                // TDQ-9300-- if the join condition is not empty, use the usercount in aide indicator for rule's
                // count; otherwise give the row count to the rule,because the rule can not compute the count by itself
                if (aideCount > -1) {
                    rule.setCount(aideCount);
                } else {
                    rule.setCount(rowCount);
                }

                publishDynamicEvent(rule);
            }
        } finally {
            ReturnCode rc = closeConnection(analysis, connection);
            if (!rc.isOk()) {
                appendError(rc.getMessage());
                isSuccess = Boolean.FALSE;
            }

        }
        return isSuccess;
    }

    /**
     * Added TDQ-8787 publish the related event when one indicator is finished: to refresh the chart with new result of
     * the current indicator
     * 
     * @param rule
     */
    private void publishDynamicEvent(final Indicator rule) {
        final ITDQRepositoryService tdqRepositoryService = AnalysisExecutorHelper.getTDQService();
        if (tdqRepositoryService != null) {
            Display.getDefault().syncExec(new Runnable() {

                public void run() {
                    tdqRepositoryService.publishDynamicEvent(rule, IndicatorCommonUtil.getIndicatorValue(rule));
                }
            });
        }
    }

    private boolean executeIndicator(Indicator indicator, Connection connection) {
        // set the connection's catalog
        String catalogName = getCatalogOrSchemaName(indicator.getAnalyzedElement());
        if (catalogName != null) { // check whether null argument can be given
            changeCatalog(catalogName, connection);
        }

        Expression query = dbms().getInstantiatedExpression(indicator);
        if (query == null) {
            traceError("Query not executed for indicator: \"" + indicator.getName() + "\" "//$NON-NLS-1$//$NON-NLS-2$
                    + "query is null");//$NON-NLS-1$
            return Boolean.FALSE;
        } else {
            try {
                Boolean isExecSuccess = executeQuery(indicator, connection, query.getBody());
                if (!isExecSuccess) {
                    traceError("Query not executed for indicator: \"" + indicator.getName() + "\" "//$NON-NLS-1$//$NON-NLS-2$
                            + "SQL query: " + query.getBody());//$NON-NLS-1$
                    return Boolean.FALSE;
                }
            } catch (Exception e) {
                traceError("Query not executed for indicator: \"" + indicator.getName() + "\" "//$NON-NLS-1$//$NON-NLS-2$
                        + "SQL query: " + query.getBody() + ". Exception: " + e.getMessage());//$NON-NLS-1$ //$NON-NLS-2$
                return Boolean.FALSE;
            }
        }
        indicator.setComputed(true);
        return Boolean.TRUE;
    }

    /**
     * Added TDQ-9300 : Group the rule and its aide together as one pair in one list; and reorder to move the aide rule
     * upper the rule : old order : rule, aiderule--> new order: aiderule, rule.
     * 
     * @param indicators
     */
    private List<List<Indicator>> groupAideRule(List<Indicator> indicators) {
        List<List<Indicator>> orderedIndicators = new ArrayList<List<Indicator>>();
        // no need to consider the row count,to let the index start at 1
        // each aide and rule are put in one list, as the order aide, rule.
        for (int index = 1; (index + 1) < indicators.size(); index = index + 2) {
            List<Indicator> pairOfRule = new ArrayList<Indicator>();
            Indicator indicator = indicators.get(index);
            if (indicator instanceof WhereRuleIndicator && indicator.getName().equals(indicators.get(index + 1).getName())) {
                // position: index+1 is the Aide
                pairOfRule.add(indicators.get(index + 1));
                // Rule 's position is index
                pairOfRule.add(indicator);
                orderedIndicators.add(pairOfRule);
            }
        }
        return orderedIndicators;
    }

    private String getCatalogOrSchemaName(ModelElement analyzedElement) {
        Package schema = super.schemata.get(analyzedElement);
        if (schema == null) {
            log.error(Messages.getString("TableAnalysisSqlExecutor.NOSCHEMAFOUNDFORTABLE", analyzedElement.getName()));//$NON-NLS-1$
            return null;
        }
        // else
        if (RelationalPackage.eINSTANCE.getSchema().equals(schema.eClass())) {
            final Catalog parentCatalog = CatalogHelper.getParentCatalog(schema);
            if (parentCatalog != null) {
                return parentCatalog.getName();
            }
        }
        return schema.getName();
    }

    protected boolean changeCatalog(String catalogName, Connection connection) {
        try {
            if (!(ConnectionUtils.isOdbcMssql(connection) || ConnectionUtils.isOdbcOracle(connection)
                    || ConnectionUtils.isOdbcProgress(connection) || ConnectionUtils.isOdbcTeradata(connection) || ExtractMetaDataUtils
                    .getInstance().isHiveConnection(connection))) {
                connection.setCatalog(catalogName);
            }
            return true;
        } catch (RuntimeException e) {
            traceError(Messages.getString("ColumnAnalysisSqlExecutor.ERRORWHENSETCATALOG", catalogName, e.getMessage()));//$NON-NLS-1$
            return Boolean.FALSE;
        } catch (SQLException e) {
            traceError(Messages.getString("ColumnAnalysisSqlExecutor.ERRORWHENSETCATALOGSQL", catalogName, e.getMessage()));//$NON-NLS-1$
            return Boolean.FALSE;
        }
    }

    private boolean executeQuery(Indicator indicator, Connection connection, String queryStmt) throws SQLException {
        // TDQ-9294 if the WhereRuleAideIndicator don't contain any join condictions, it result is same with
        // row count, so needn't to execute query for it
        if (isAideAndJoinEmpty(indicator)) {
            return true;
        }
        // ~ TDQ-9294
        String cat = getCatalogOrSchemaName(indicator.getAnalyzedElement());
        if (log.isInfoEnabled()) {
            log.info(Messages.getString("ColumnAnalysisSqlExecutor.COMPUTINGINDICATOR", indicator.getName())//$NON-NLS-1$
                    + "\t" + Messages.getString("ColumnAnalysisSqlExecutor.EXECUTINGQUERY", queryStmt));//$NON-NLS-1$ //$NON-NLS-2$
        }
        List<Object[]> myResultSet = executeQuery(cat, connection, queryStmt);

        // give result to indicator so that it handles the results
        return indicator.storeSqlResults(myResultSet);
    }

    protected List<Object[]> executeQuery(String catalogName, Connection connection, String queryStmt) throws SQLException {
        if (catalogName != null) { // check whether null argument can be given
            changeCatalog(catalogName, connection);
        }
        // create query statement
        Statement statement = connection.createStatement();
        // statement.setFetchSize(fetchSize);

        if (continueRun()) {
            statement.execute(queryStmt);
        }

        // get the results
        ResultSet resultSet = statement.getResultSet();
        if (resultSet == null) {
            String mess = Messages.getString("ColumnAnalysisSqlExecutor.NORESULTSETFORTHISSTATEMENT") + queryStmt;//$NON-NLS-1$
            log.warn(mess);
            return null;
        }
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();
        List<Object[]> myResultSet = new ArrayList<Object[]>();
        while (resultSet.next()) {
            Object[] result = new Object[columnCount];
            for (int i = 0; i < columnCount; i++) {
                result[i] = resultSet.getObject(i + 1);
            }
            myResultSet.add(result);
        }
        // -- release resources
        resultSet.close();
        statement.close();

        return myResultSet;
    }

    /**
     * DOC xqliu Comment method "getValidStatement". 2009-10-29 bug 9702
     * 
     * @param dataFilterAsString
     * @param indicator
     * @param valid
     * @return
     */
    public String getValidStatement(String dataFilterAsString, Indicator indicator, boolean valid) {
        ModelElement analyzedElement = indicator.getAnalyzedElement();
        if (analyzedElement == null) {
            traceError(Messages.getString("ColumnAnalysisSqlExecutor.ANALYSISELEMENTISNULL", indicator.getName()));//$NON-NLS-1$
            return PluginConstant.EMPTY_STRING;
        }
        NamedColumnSet set = SwitchHelpers.NAMED_COLUMN_SET_SWITCH.doSwitch(indicator.getAnalyzedElement());
        if (set == null) {
            traceError(Messages.getString("TableAnalysisSqlExecutor.ANALYZEDELEMENTISNOTATABLE", indicator.getName()));//$NON-NLS-1$
            return PluginConstant.EMPTY_STRING;
        }
        // --- get the schema owner
        String setName = quote(set.getName());
        if (!belongToSameSchemata(set)) {
            StringBuffer buf = new StringBuffer();
            for (orgomg.cwm.objectmodel.core.Package schema : schemata.values()) {
                buf.append(schema.getName() + " "); //$NON-NLS-1$
            }
            log.error(Messages.getString("TableAnalysisSqlExecutor.TABLENOTBELONGTOEXISSCHEMA", setName, buf.toString().trim()));//$NON-NLS-1$
            return PluginConstant.EMPTY_STRING;
        }

        // get correct language for current database
        Expression sqlGenericExpression = null;

        // --- create select statement
        // get indicator's sql tableS (generate the real SQL statement from its definition)

        IndicatorDefinition indicatorDefinition = indicator.getIndicatorDefinition();
        if (indicatorDefinition == null) {
            traceError(Messages.getString("ColumnAnalysisSqlExecutor.INTERNALERROR", indicator.getName()));//$NON-NLS-1$
            return PluginConstant.EMPTY_STRING;
        }
        sqlGenericExpression = dbms().getSqlExpression(indicatorDefinition);

        final EClass indicatorEclass = indicator.eClass();
        if (sqlGenericExpression == null || sqlGenericExpression.getBody() == null) {
            traceError(Messages
                    .getString(
                            "ColumnAnalysisSqlExecutor.UNSUPPORTEDINDICATOR", (indicator.getName() != null ? indicator.getName() : indicatorEclass.getName()), ResourceHelper.getUUID(indicatorDefinition)));//$NON-NLS-1$
            return PluginConstant.EMPTY_STRING;
        }

        // --- get indicator parameters and convert them into sql expression
        List<String> whereExpressionAnalysis = new ArrayList<String>();
        if (StringUtils.isNotBlank(dataFilterAsString)) {
            whereExpressionAnalysis.add(dataFilterAsString);
        }
        List<String> whereExpressionDQRule = new ArrayList<String>();
        String setAliasA = PluginConstant.EMPTY_STRING;
        final EList<JoinElement> joinConditions = indicator.getJoinConditions();
        if (RulesPackage.eINSTANCE.getWhereRule().equals(indicatorDefinition.eClass())) {
            WhereRule wr = (WhereRule) indicatorDefinition;
            whereExpressionDQRule.add(wr.getWhereExpression());

            // MOD scorreia 2009-03-13 copy joins conditions into the indicator
            joinConditions.clear();
            if (!wr.getJoins().isEmpty()) {
                for (JoinElement joinelt : wr.getJoins()) {
                    JoinElement joinCopy = EcoreUtil.copy(joinelt);
                    joinConditions.add(joinCopy);
                    setAliasA = PluginConstant.EMPTY_STRING.equals(setAliasA) ? joinCopy.getTableAliasA() : setAliasA;
                }
            }
        }

        String schemaName = getQuotedSchemaName(set);

        // --- normalize table name
        String catalogName = getQuotedCatalogName(set);
        if (catalogName == null && schemaName != null) {
            // try to get catalog above schema
            final Schema parentSchema = SchemaHelper.getParentSchema(set);
            final Catalog parentCatalog = CatalogHelper.getParentCatalog(parentSchema);
            catalogName = parentCatalog != null ? parentCatalog.getName() : null;
        }
        setName = dbms().toQualifiedName(catalogName, schemaName, setName);

        // ### evaluate SQL Statement depending on indicators ###
        String completedSqlString = null;

        // --- default case
        // allow join
        String joinclause = (!joinConditions.isEmpty()) ? dbms().createJoinConditionAsString(set, joinConditions, catalogName,
                schemaName) : PluginConstant.EMPTY_STRING;

        String genericSql = sqlGenericExpression.getBody();
        setAliasA = PluginConstant.EMPTY_STRING.equals(setAliasA) ? "*" : setAliasA + ".*";//$NON-NLS-1$//$NON-NLS-2$
        genericSql = genericSql.replace("COUNT(*)", setAliasA);//$NON-NLS-1$

        completedSqlString = dbms().fillGenericQueryWithJoin(genericSql, setName, joinclause);
        // ~
        try {
            completedSqlString = addWhereToSqlStringStatement(whereExpressionAnalysis, whereExpressionDQRule, completedSqlString,
                    valid);
        } catch (ParseException e) {
            log.warn(e);
        }

        return completedSqlString;
    }

    /**
     * DOC xqliu Comment method "setCachedAnalysis". 2009-10-29 bug 9702
     * 
     * @param analysis
     */
    public void setCachedAnalysis(Analysis analysis) {
        this.cachedAnalysis = analysis;
    }
}
