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
import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.talend.cwm.db.connection.ConnectionUtils;
import org.talend.cwm.helper.ColumnHelper;
import org.talend.cwm.helper.PackageHelper;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.IndicatorsPackage;
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
        this.reset();

        EList<Indicator> indicators = analysis.getResults().getIndicators();
        for (Indicator indicator : indicators) {
            instantiateQuery(indicator);
        }

        // no query to return, here we only instantiate several SQL queries
        return "";
    }

    /**
     * DOC scorreia Comment method "instantiateQuery".
     * 
     * @param indicator
     */
    private void instantiateQuery(Indicator indicator) {
        if (ColumnsetPackage.eINSTANCE.getRowMatchingIndicator().equals(indicator.eClass())) {
            RowMatchingIndicator rowMatchingIndicator = (RowMatchingIndicator) indicator;
            EList<Column> columnSetA = rowMatchingIndicator.getColumnSetA();
            EList<Column> columnSetB = rowMatchingIndicator.getColumnSetB();
            if (columnSetA.size() != columnSetB.size()) {
                log.error("Cannot compare two column sets with different size");
                return; // break;
            }

            IndicatorDefinition indicatorDefinition = indicator.getIndicatorDefinition();
            Expression sqlGenericExpression = dbms().getSqlExpression(indicatorDefinition);

            Expression instantiatedSqlExpression = createInstantiatedSqlExpression(sqlGenericExpression, columnSetA, columnSetB);
            indicator.setInstantiatedExpression(instantiatedSqlExpression);
        }
    }

    /**
     * DOC scorreia Comment method "createInstantiatedSqlExpression".
     * 
     * @param sqlGenericExpression
     * @param columnSetA
     * @param columnSetB
     * @return
     */
    private Expression createInstantiatedSqlExpression(Expression sqlGenericExpression, EList<Column> columnSetA,
            EList<Column> columnSetB) {
        String tableNameA = getTableName(columnSetA);
        String tableNameB = getTableName(columnSetB);

        // Generic SQL expression is something like:
        // SELECT COUNT(*) FROM {0} LEFT JOIN {1} ON ({2} ) WHERE ({3})
        String genericSQL = sqlGenericExpression.getBody();
        String joinClause = createJoinClause(tableNameA, columnSetA, tableNameB, columnSetB);
        String whereClause = createWhereClause(tableNameB, columnSetB);

        String instantiatedSQL = replaceVariablesLow(genericSQL, tableNameA, tableNameB, joinClause, whereClause);
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
            builder.append(tableNameB).append('.').append(columnSetB.get(i).getName()).append(dbms().isNull());
            if (i != size - 1) {
                builder.append(dbms().and());
            }
        }
        return builder.toString();
    }

    /**
     * DOC scorreia Comment method "createJoinClause".
     * 
     * @param tableNameA
     * 
     * @param columnSetA
     * @param tableNameB
     * @param columnSetB
     * @return
     */
    private String createJoinClause(String tableNameA, EList<Column> columnSetA, String tableNameB, EList<Column> columnSetB) {
        StringBuilder builder = new StringBuilder();

        int size = columnSetA.size();
        for (int i = 0; i < size; i++) {
            String colA = tableNameA + '.' + columnSetA.get(i).getName();
            String colB = tableNameB + '.' + columnSetB.get(i).getName();
            builder.append(" (").append(colA).append(dbms().equal()).append(colB).append(dbms().or())
                    .append(bothNull(colA, colB)).append(") ");
            if (i != size - 1) {
                builder.append(dbms().and());
            }
        }

        return builder.toString();
    }

    /**
     * DOC scorreia Comment method "bothNull".
     * 
     * @param colA
     * @param colB
     * @return
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
                Package pack = PackageHelper.getCatalogOrSchema(columnSetOwner);
                if (pack == null) {
                    log.error("No Catalog or Schema found for column set owner: " + tableName);
                    continue; // do not break until we find the owner
                }
                this.catalogOrSchema = pack.getName();
                break; // all columns should belong to the same table
            }
        }
        return tableName;
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

        Indicator rowCountIndicator = null;
        Connection connection = trc.getObject();
        try {

            // execute the sql statement for each indicator
            EList<Indicator> indicators = analysis.getResults().getIndicators();
            for (Indicator indicator : indicators) {

                // set the connection's catalog
                if (this.catalogOrSchema != null) { // check whether null argument can be given
                    changeCatalog(this.catalogOrSchema, connection);
                }
                Expression query = dbms().getInstantiatedExpression(indicator);

                if (query == null || !executeQuery(indicator, connection, query)) {
                    ok = traceError("Query not executed for indicator: \"" + indicator.getName() + "\" "
                            + ((query == null) ? "query is null" : "SQL query: " + query.getBody()));
                }

                if (IndicatorsPackage.eINSTANCE.getRowCountIndicator().equals(indicator.eClass())) {
                    rowCountIndicator = indicator;
                }
            }

            connection.close();

            // --- finalize indicators by setting the row count when it exists.
            if (rowCountIndicator != null) {
                for (Indicator indicator : indicators) {
                    if (indicator != rowCountIndicator) {
                        Long count = rowCountIndicator.getCount();
                        indicator.setCount(count);
                        if (ColumnsetPackage.eINSTANCE.getRowMatchingIndicator().equals(indicator.eClass())) {
                            RowMatchingIndicator rowMatchingIndicator = (RowMatchingIndicator) indicator;
                            Long notMatchingValueCount = rowMatchingIndicator.getNotMatchingValueCount();
                            if (notMatchingValueCount != null) {
                                rowMatchingIndicator.setMatchingValueCount(count - notMatchingValueCount);
                            }
                        }
                    }
                }
            }

        } catch (SQLException e) {
            log.error(e, e);
            this.errorMessage = e.getMessage();
            ok = false;
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
     */
    private boolean executeQuery(Indicator indicator, Connection connection, Expression query) {
        try {
            List<Object[]> myResultSet = executeQuery(catalogOrSchema, connection, query.getBody());
            // give result to indicator so that it handles the results
            return indicator.storeSqlResults(myResultSet);
        } catch (SQLException e) {
            log.error(e, e);
            return false;
        }

    }

}
