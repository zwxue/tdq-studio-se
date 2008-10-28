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
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.talend.cwm.db.connection.ConnectionUtils;
import org.talend.cwm.helper.ColumnHelper;
import org.talend.cwm.helper.ColumnSetHelper;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.cwm.relational.TdColumn;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.analysis.AnalysisContext;
import org.talend.dataquality.helpers.BooleanExpressionHelper;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.columnset.ColumnSetMultiValueIndicator;
import org.talend.dataquality.indicators.columnset.ColumnsetPackage;
import org.talend.utils.sugars.TypedReturnCode;
import orgomg.cwm.objectmodel.core.Expression;
import orgomg.cwm.objectmodel.core.ModelElement;
import orgomg.cwm.objectmodel.core.Package;
import orgomg.cwm.resource.relational.Column;
import orgomg.cwm.resource.relational.ColumnSet;

/**
 * DOC scorreia class global comment. Detailled comment
 */
public class MultiColumnAnalysisExecutor extends ColumnAnalysisSqlExecutor {

    private static Logger log = Logger.getLogger(MultiColumnAnalysisExecutor.class);

    private String catalogOrSchema = null;

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.dq.analysis.ColumnAnalysisSqlExecutor#createSqlStatement(org.talend.dataquality.analysis.Analysis)
     */
    @Override
    protected String createSqlStatement(Analysis analysis) {
        this.cachedAnalysis = analysis;
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
        if (ColumnsetPackage.eINSTANCE.getColumnSetMultiValueIndicator().isSuperTypeOf(indicator.eClass())) {
            ColumnSetMultiValueIndicator colSetMultValIndicator = (ColumnSetMultiValueIndicator) indicator;
            final EList<Column> analyzedColumns = colSetMultValIndicator.getAnalyzedColumns();
            final EList<String> numericFunctions = colSetMultValIndicator.getNumericFunctions();
            // put
            List<String> nominalColumns = new ArrayList<String>();
            for (Column column : colSetMultValIndicator.getNominalColumns()) {
                nominalColumns.add(column.getName());
            }
            List<String> computedColumns = new ArrayList<String>();
            for (Column column : colSetMultValIndicator.getNumericColumns()) {
                // call functions for each column
                for (String f : numericFunctions) {
                    computedColumns.add(replaceVariablesLow(f, column.getName()));
                }
            }
            String selectItems = createSelect(nominalColumns, computedColumns);

            // get indicator definition
            final Expression sqlGenericExpression = dbms().getSqlExpression(indicator.getIndicatorDefinition());

            String grpByClause = createGroupBy(nominalColumns);
            // all columns must belong to the same table
            String tableName = getTableName(analyzedColumns);
            // definition is SELECT &lt;%=__COLUMN_NAMES__%> FROM &lt;%=__TABLE_NAME__%> GROUP BY
            // &lt;%=__GROUP_BY_ALIAS__%>
            String slqExpr = dbms().fillGenericQueryWithColumnTableAndAlias(sqlGenericExpression.getBody(), selectItems,
                    tableName, grpByClause);

            indicator.setInstantiatedExpression(BooleanExpressionHelper.createExpression(sqlGenericExpression.getLanguage(),
                    slqExpr));
        }
    }

    /**
     * DOC scorreia Comment method "getTableName".
     * 
     * @param analyzedColumns
     * @return
     */
    private String getTableName(final EList<Column> analyzedColumns) {
        ColumnSet columnSetOwner = ColumnHelper.getColumnSetOwner(analyzedColumns.get(0));
        String tableName = columnSetOwner.getName();

        Package pack = ColumnSetHelper.getParentCatalogOrSchema(columnSetOwner);
        if (pack == null) {
            log.error("No Catalog or Schema found for column set owner: " + tableName);
        } else {
            this.catalogOrSchema = pack.getName();
        }
        return tableName;
    }

    /*
     * tableName = columnSetOwner.getName(); Package pack = PackageHelper.getCatalogOrSchema(columnSetOwner); if (pack
     * == null) { log.error("No Catalog or Schema found for column set owner: " + tableName); continue; // do not break
     * until we find the owner } this.catalogOrSchema = pack.getName(); break; // all columns should belong to the same
     * table
     */
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

            }

            connection.close();

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
     * DOC scorreia Comment method "createGroupBy".
     * 
     * @param nominalColumns
     * @return
     */
    private String createGroupBy(List<String> nominalColumns) {
        StringBuilder builder = new StringBuilder();
        final int nbNomColumns = nominalColumns.size();
        for (int i = 0; i < nbNomColumns; i++) {
            builder.append(nominalColumns.get(i));
            if (i != nbNomColumns - 1) {
                builder.append(COMMA);
            }
        }
        return builder.toString();
    }

    private static final String COMMA = ",";

    /**
     * DOC scorreia Comment method "createSelect".
     * 
     * @param nominalColumns
     * @param computedColumns
     * @return
     */
    private String createSelect(List<String> nominalColumns, List<String> computedColumns) {
        StringBuilder builder = new StringBuilder();
        final int nbNomColumns = nominalColumns.size();
        final int nbComColumns = computedColumns.size();
        for (int i = 0; i < nbNomColumns; i++) {
            builder.append(nominalColumns.get(i));
            if (i != nbNomColumns - 1 || nbComColumns != 0) {
                builder.append(COMMA);
            }
        }
        for (int i = 0; i < nbComColumns; i++) {
            builder.append(computedColumns.get(i));
            if (i != nbComColumns - 1) {
                builder.append(COMMA);
            }
        }
        return builder.toString();
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

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.dq.analysis.ColumnAnalysisExecutor#checkAnalyzedElements(org.talend.dataquality.analysis.Analysis,
     * org.talend.dataquality.analysis.AnalysisContext)
     */
    @Override
    protected boolean checkAnalyzedElements(Analysis analysis, AnalysisContext context) {
        for (ModelElement node : context.getAnalysedElements()) {
            TdColumn column = SwitchHelpers.COLUMN_SWITCH.doSwitch(node);
            // TODO scorreia
        }
        return true;
    }

}
