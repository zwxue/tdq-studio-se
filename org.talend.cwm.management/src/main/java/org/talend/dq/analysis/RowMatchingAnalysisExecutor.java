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
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.talend.cwm.db.connection.ConnectionUtils;
import org.talend.cwm.exception.AnalysisExecutionException;
import org.talend.cwm.helper.ColumnHelper;
import org.talend.cwm.helper.ColumnSetHelper;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.analysis.AnalysisContext;
import org.talend.dataquality.helpers.AnalysisHelper;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.columnset.ColumnsetPackage;
import org.talend.dataquality.indicators.columnset.RowMatchingIndicator;
import org.talend.dataquality.indicators.definition.IndicatorDefinition;
import org.talend.utils.sugars.TypedReturnCode;
import orgomg.cwm.objectmodel.core.CoreFactory;
import orgomg.cwm.objectmodel.core.Expression;
import orgomg.cwm.objectmodel.core.Package;
import orgomg.cwm.resource.relational.Column;
import orgomg.cwm.resource.relational.ColumnSet;

/**
 * @author scorreia
 * 
 * executes multicolumn comparison.
 */
public class RowMatchingAnalysisExecutor extends ColumnAnalysisSqlExecutor {

    private static Logger log = Logger.getLogger(RowMatchingAnalysisExecutor.class);

    private String catalogOrSchema = null;

    private void reset() {
        catalogOrSchema = null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.analysis.AnalysisExecutor#createSqlStatement(org.talend.dataquality.analysis.Analysis)
     */
    @Override
    protected String createSqlStatement(Analysis analysis) {
        this.cachedAnalysis = analysis;
        this.reset();

        EList<Indicator> indicators = analysis.getResults().getIndicators();
        for (Indicator indicator : indicators) {
            instantiateQuery(indicator);
        }

        // no query to return, here we only instantiate several SQL queries
        return ""; //$NON-NLS-1$
    }

    /**
     * DOC scorreia Comment method "instantiateQuery".
     * 
     * @param indicator
     */
    private boolean instantiateQuery(Indicator indicator) {
        // indicator.reset(); // rli reset will clear columnSetA and columnSetB, we will lost our analysedElement
        // information. So comment it. // scorreia -> changed the implementation of reset() so that it can now be called
        // (but is not need, hence we keep it commented)
        if (ColumnsetPackage.eINSTANCE.getRowMatchingIndicator().equals(indicator.eClass())) {
            RowMatchingIndicator rowMatchingIndicator = (RowMatchingIndicator) indicator;
            EList<Column> columnSetA = rowMatchingIndicator.getColumnSetA();
            EList<Column> columnSetB = rowMatchingIndicator.getColumnSetB();
            if (columnSetA.size() != columnSetB.size()) {
                return traceError("Cannot compare two column sets with different size"); // break;
            }

            IndicatorDefinition indicatorDefinition = indicator.getIndicatorDefinition();
            Expression sqlGenericExpression = dbms().getSqlExpression(indicatorDefinition);

            boolean useNulls = true; // // TODO scorreia allow the user to set it at false
            Expression instantiatedSqlExpression = createInstantiatedSqlExpression(sqlGenericExpression, columnSetA, columnSetB,
                    useNulls);
            indicator.setInstantiatedExpression(instantiatedSqlExpression);
            return true;
        }
        return traceError("Unhandled given indicator: " + indicator.getName());
    }

    /**
     * DOC scorreia Comment method "createInstantiatedSqlExpression".
     * 
     * @param sqlGenericExpression
     * @param columnSetA
     * @param columnSetB
     * @param useNulls
     * @return
     */
    private Expression createInstantiatedSqlExpression(Expression sqlGenericExpression, EList<Column> columnSetA,
            EList<Column> columnSetB, boolean useNulls) {
        String tableNameA = getTableName(columnSetA);
        String tableNameB = getTableName(columnSetB);

        // Generic SQL expression is something like:
        // SELECT COUNT(*) FROM <%=__COLUMN_NAMES__%> LEFT JOIN <%=__TABLE_NAME__%> ON (<%=__JOIN_CLAUSE__%>) WHERE
        // (<%=__WHERE_CLAUSE__%>)
        String genericSQL = sqlGenericExpression.getBody();        
        String joinClause = createJoinClause(tableNameA, columnSetA, tableNameB, columnSetB, useNulls);
        String whereClause = createWhereClause(tableNameB, columnSetB);

        String instantiatedSQL = dbms().fillGenericQueryWithJoin(genericSQL, tableNameA, tableNameB, joinClause, whereClause);
        Expression instantiatedExpression = CoreFactory.eINSTANCE.createExpression();
        instantiatedExpression.setLanguage(sqlGenericExpression.getLanguage());
        instantiatedExpression.setBody(instantiatedSQL);

        return instantiatedExpression;
    }

    /**
     * DOC scorreia Comment method "createWhereClause".
     * 
     * @param tableNameB
     * @param columnSetB
     * @return
     */
    private String createWhereClause(String tableNameB, EList<Column> columnSetB) {
        StringBuilder builder = new StringBuilder();
        int size = columnSetB.size();
        for (int i = 0; i < size; i++) {
            builder.append(tableNameB).append('.').append(getQuotedColumnName(columnSetB.get(i))).append(dbms().isNull());
            if (i != size - 1) {
                builder.append(dbms().and());
            }
        }
        return builder.toString();
    }

    /**
     * Method "createJoinClause".
     * 
     * @param tableNameA already quoted table name
     * @param columnSetA
     * @param tableNameB already quoted table name
     * @param columnSetB
     * @return
     */
    private String createJoinClause(String tableNameA, EList<Column> columnSetA, String tableNameB, EList<Column> columnSetB,
            final boolean useNulls) {
        StringBuilder builder = new StringBuilder();
        int size = columnSetA.size();
        for (int i = 0; i < size; i++) {
            String colA = tableNameA + '.' + getQuotedColumnName(columnSetA.get(i));
            String colB = tableNameB + '.' + getQuotedColumnName(columnSetB.get(i));
            builder.append(" (").append(colA).append(dbms().equal()).append(colB);
            if (useNulls) { // allow to identify rows like ('a', null) = ('a', null)
                builder.append(dbms().or()).append(bothNull(colA, colB));
            }
            builder.append(") ");
            if (i != size - 1) {
                builder.append(dbms().and());
            }
        }

        return builder.toString();
    }

    /**
     * Method "bothNull".
     * 
     * @param colA a column name from table A
     * @param colB a column name from table B
     * @return colA IS NULL AND colB IS NULL
     */
    private String bothNull(String colA, String colB) {
        StringBuilder builder = new StringBuilder();
        builder.append(" (").append(colA).append(dbms().isNull()).append(dbms().and()).append(colB).append(dbms().isNull())
                .append(") ");
        return builder.toString();
    }

    /**
     * DOC scorreia Comment method "getTableName".
     * 
     * @param columnSetA
     * @return
     */
    private String getTableName(EList<Column> columnSetA) {
        String tableName = null;
        for (Column column : columnSetA) {
            ColumnSet columnSetOwner = ColumnHelper.getColumnSetOwner(column);
            if (columnSetOwner == null) {
                log.error("ColumnSet Owner of column " + column.getName() + " is null");
                continue;
            } else {
                tableName = columnSetOwner.getName();
                Package pack = ColumnSetHelper.getParentCatalogOrSchema(columnSetOwner);
                if (pack == null) {
                    log.error("No Catalog or Schema found for column set owner: " + tableName);
                    continue; // do not break until we find the owner
                }
                this.catalogOrSchema = pack.getName();
                break; // all columns should belong to the same table
            }
        }
        return quote(tableName);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.analysis.AnalysisExecutor#runAnalysis(org.talend.dataquality.analysis.Analysis,
     * java.lang.String)
     */
    @Override
    protected boolean runAnalysis(Analysis analysis, String sqlStatement) {
        boolean ok = true;
        TypedReturnCode<Connection> trc = this.getConnection(analysis);
        if (!trc.isOk()) {
            return traceError("Cannot execute Analysis " + analysis.getName() + ". Error: " + trc.getMessage());
        }

        Connection connection = trc.getObject();
        try {

            // execute the sql statement for each indicator
            EList<Indicator> indicators = analysis.getResults().getIndicators();
            EList<Indicator> deactivatedIndicators = analysis.getParameters().getDeactivatedIndicators();
            for (Indicator indicator : indicators) {
                if (deactivatedIndicators.contains(indicator)) {
                    continue; // do not evaluate this indicator
                }
                // set the connection's catalog
                if (this.catalogOrSchema != null) { // check whether null argument can be given
                    changeCatalog(this.catalogOrSchema, connection);
                }
                Expression query = dbms().getInstantiatedExpression(indicator);

                if (query == null || !executeQuery(indicator, connection, query)) {
                    ok = traceError("Query not executed for indicator: \"" + indicator.getName() + "\" "
                            + ((query == null) ? "query is null" : "SQL query: " + query.getBody()));
                }
            }

            connection.close();

        } catch (SQLException e) {
            log.error(e, e);
            this.errorMessage = e.getMessage();
            ok = false;
        } catch (AnalysisExecutionException e) {
            ok = traceError(e.getMessage());
        } finally {
            ConnectionUtils.closeConnection(connection);
        }
        return ok;
    }

    /**
     * DOC scorreia Comment method "executeQuery".
     * 
     * @param indicator
     * @param connection
     * @param query
     * @return
     * @throws AnalysisExecutionException
     */
    private boolean executeQuery(Indicator indicator, Connection connection, Expression query) throws AnalysisExecutionException {
        try {
            List<Object[]> myResultSet = executeQuery(catalogOrSchema, connection, query.getBody());
            String tableName = getAnalyzedTable(indicator);
            
            // set data filter here
            final String stringDataFilter = AnalysisHelper.getStringDataFilter(this.cachedAnalysis);
            List<String> whereClauses = new ArrayList<String>();
            if (stringDataFilter != null) {
                whereClauses.add(stringDataFilter);
            }
            // give result to indicator so that it handles the results
            boolean ok = indicator.storeSqlResults(myResultSet);
            // get row count and store it in indicator
            Long count = getCount(cachedAnalysis, "*", tableName, catalogOrSchema, whereClauses); //$NON-NLS-1$
            ok = ok && count != null;
            indicator.setCount(count);
            
            // compute matching count
            if (ColumnsetPackage.eINSTANCE.getRowMatchingIndicator().equals(indicator.eClass())) {
                RowMatchingIndicator rowMatchingIndicator = (RowMatchingIndicator) indicator;
                Long notMatchingValueCount = rowMatchingIndicator.getNotMatchingValueCount();
                ok = ok && notMatchingValueCount != null;
                if (ok) {
                    rowMatchingIndicator.setMatchingValueCount(count - notMatchingValueCount);
                }
            }
            
            return ok;
        } catch (SQLException e) {
            log.error(e, e);
            return false;
        }

    }

    /**
     * Method "getAnalyzedTable".
     * 
     * @param indicator
     * @return the table name (within quotes)
     */
    private String getAnalyzedTable(Indicator indicator) {
        return quote(indicator.getAnalyzedElement().getName());
    }

    protected boolean checkAnalyzedElements(final Analysis analysis, AnalysisContext context) {
        AnalysisHandler analysisHandler = new AnalysisHandler();
        analysisHandler.setAnalysis(analysis);

        // TODO How to handle the context.getAnalysedElements()???
        // for (ModelElement node : context.getAnalysedElements()) {
        // TdColumn column = SwitchHelpers.COLUMN_SWITCH.doSwitch(node);
        //
        // // --- Check that each analyzed element has at least one indicator
        // if (analysisHandler.getIndicators(column).size() == 0) {
        // this.errorMessage = "Each column must have at least one indicator, "
        // + "please select some indicator(s) to compute on each column!";
        // return false;
        // }
        //
        // // --- get the data provider
        // TdDataProvider dp = DataProviderHelper.getTdDataProvider(column);
        // if (!isAccessWith(dp)) {
        // this.errorMessage = "All columns must belong to the same connection! Remove column " + column.getName()
        // + " from this analysis! It does not belong to \"" + dataprovider.getName() + "\"";
        // return false;
        // }
        // }
        return true;
    }

}
