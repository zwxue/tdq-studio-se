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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.talend.cwm.db.connection.ConnectionUtils;
import org.talend.cwm.helper.CatalogHelper;
import org.talend.cwm.helper.ColumnHelper;
import org.talend.cwm.helper.ColumnSetHelper;
import org.talend.cwm.helper.SchemaHelper;
import org.talend.cwm.management.i18n.Messages;
import org.talend.cwm.relational.MeatadataColumn;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.analysis.AnalysisContext;
import org.talend.dataquality.helpers.AnalysisHelper;
import org.talend.dataquality.helpers.BooleanExpressionHelper;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.RegexpMatchingIndicator;
import org.talend.dataquality.indicators.columnset.AllMatchIndicator;
import org.talend.dataquality.indicators.columnset.ColumnSetMultiValueIndicator;
import org.talend.dataquality.indicators.columnset.ColumnsetPackage;
import org.talend.dataquality.indicators.definition.IndicatorDefinition;
import org.talend.utils.sugars.TypedReturnCode;
import orgomg.cwm.objectmodel.core.Expression;
import orgomg.cwm.objectmodel.core.Package;
import orgomg.cwm.resource.relational.Catalog;
import orgomg.cwm.resource.relational.ColumnSet;
import orgomg.cwm.resource.relational.Schema;

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
        return ""; //$NON-NLS-1$
    }

    /**
     * DOC scorreia Comment method "instantiateQuery".
     * 
     * @param indicator
     */
    private void instantiateQuery(Indicator indicator) {
        if (ColumnsetPackage.eINSTANCE.getColumnSetMultiValueIndicator().isSuperTypeOf(indicator.eClass())) {
            ColumnSetMultiValueIndicator colSetMultValIndicator = (ColumnSetMultiValueIndicator) indicator;
            final EList<MeatadataColumn> analyzedColumns = colSetMultValIndicator.getAnalyzedColumns();
            final EList<String> numericFunctions = initializeNumericFunctions(colSetMultValIndicator);
            final EList<String> dateFunctions = initializeDateFunctions(colSetMultValIndicator);

            // separate nominal from numeric columns
            List<String> nominalColumns = new ArrayList<String>();
            for (MeatadataColumn column : colSetMultValIndicator.getNominalColumns()) {
                nominalColumns.add(getQuotedColumnName(column));
            }
            List<String> computedColumns = new ArrayList<String>();
            for (MeatadataColumn column : colSetMultValIndicator.getNumericColumns()) {
                // call functions for each column
                for (String f : numericFunctions) {
                    computedColumns.add(replaceVariablesLow(f, getQuotedColumnName(column)));
                }
            }
            for (MeatadataColumn column : colSetMultValIndicator.getDateColumns()) {
                // call functions for each column
                for (String f : dateFunctions) {
                    computedColumns.add(replaceVariablesLow(f, getQuotedColumnName(column)));
                }
            }
            // add count(*)
            computedColumns.add(colSetMultValIndicator.getCountAll());
            String selectItems = createSelect(nominalColumns, computedColumns);

            // get indicator definition
            final Expression sqlGenericExpression = dbms().getSqlExpression(indicator.getIndicatorDefinition());

            String grpByClause = createGroupBy(nominalColumns);
            // all columns must belong to the same table
            String tableName = getTableName(analyzedColumns);
            // definition is SELECT &lt;%=__COLUMN_NAMES__%> FROM &lt;%=__TABLE_NAME__%> GROUP BY
            // &lt;%=__GROUP_BY_ALIAS__%>
            String sqlExpr = dbms().fillGenericQueryWithColumnTableAndAlias(sqlGenericExpression.getBody(), selectItems,
                    tableName, grpByClause);

            // handle data filter
            String stringDataFilter = AnalysisHelper.getStringDataFilter(cachedAnalysis);
            if (stringDataFilter == null) {
                stringDataFilter = ""; //$NON-NLS-1$
            }
            sqlExpr = dbms().addWhereToStatement(sqlExpr, stringDataFilter);

            indicator.setInstantiatedExpression(BooleanExpressionHelper.createTdExpression(sqlGenericExpression.getLanguage(),
                    sqlExpr));
        }
    }

    /**
     * DOC scorreia Comment method "initializeNumericFunctions".
     * 
     * @param indicator
     * @return
     */
    private EList<String> initializeNumericFunctions(ColumnSetMultiValueIndicator indicator) {
        final EList<String> numericFunctions = indicator.getNumericFunctions();
        if (!numericFunctions.isEmpty()) { // could be already set
            return numericFunctions;
        }
        final IndicatorDefinition indicatorDefinition = indicator.getIndicatorDefinition();
        final List<String> aggregate1argFunctions = dbms().getAggregate1argFunctions(indicatorDefinition);
        numericFunctions.addAll(aggregate1argFunctions);
        return numericFunctions;
    }

    /**
     * DOC scorreia Comment method "initializeNumericFunctions".
     * 
     * @param indicator
     * @return
     */
    private EList<String> initializeDateFunctions(ColumnSetMultiValueIndicator indicator) {
        final EList<String> dateFunctions = indicator.getDateFunctions();
        if (!dateFunctions.isEmpty()) { // could be already set
            return dateFunctions;
        }
        final IndicatorDefinition indicatorDefinition = indicator.getIndicatorDefinition();
        final List<String> date1argFunctions = dbms().getDate1argFunctions(indicatorDefinition);
        dateFunctions.addAll(date1argFunctions);
        return dateFunctions;
    }

    /**
     * DOC scorreia Comment method "getTableName".
     * 
     * @param analyzedColumns
     * @return the quoted table name
     */
    private String getTableName(final EList<MeatadataColumn> analyzedColumns) {
        ColumnSet columnSetOwner = ColumnHelper.getColumnOwnerAsColumnSet(analyzedColumns.get(0));
        String tableName = columnSetOwner.getName();

        Package pack = ColumnSetHelper.getParentCatalogOrSchema(columnSetOwner);
        // ~ MOD mzhao feature 10082. Here differentiate case of MS SQL Sever(Catalog/schema).
        if (pack instanceof Schema && ColumnSetHelper.getParentCatalogOrSchema(pack) instanceof Catalog) {
            pack = ColumnSetHelper.getParentCatalogOrSchema(pack);
        }
        // ~
        if (pack == null) {
            log.error("No Catalog or Schema found for column set owner: " + tableName);
        } else {
            this.catalogOrSchema = pack.getName();
        }

        String schemaName = getQuotedSchemaName(columnSetOwner);
        String catalogName = getQuotedCatalogName(columnSetOwner);
        if (catalogName == null && schemaName != null) {
            // try to get catalog above schema
            final Schema parentSchema = SchemaHelper.getParentSchema(columnSetOwner);
            final Catalog parentCatalog = CatalogHelper.getParentCatalog(parentSchema);
            catalogName = parentCatalog != null ? parentCatalog.getName() : null;
        }
        // MOD by zshen: change schemaName of sybase database to Table's owner.
        if (ConnectionUtils.isSybaseeDBProducts(dbms().getDbmsName())) {
            schemaName = ColumnSetHelper.getTableOwner(columnSetOwner);
        }
        // ~11934
        return dbms().toQualifiedName(catalogName, schemaName, tableName);
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
                indicator.prepare();
                // set the connection's catalog
                if (this.catalogOrSchema != null) { // check whether null argument can be given
                    changeCatalog(this.catalogOrSchema, connection);
                }
                Expression query = dbms().getInstantiatedExpression(indicator);

                if (query == null || !executeQuery(indicator, connection, query)) {
                    ok = traceError("Query not executed for indicator: \"" + indicator.getName() + "\" "
                            + ((query == null) ? "query is null" : "SQL query: " + query.getBody()));
                } else {
                    indicator.setComputed(true);
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

    private static final String COMMA = ","; //$NON-NLS-1$

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
        // for (ModelElement node : context.getAnalysedElements()) {
        // // TdColumn column = SwitchHelpers.COLUMN_SWITCH.doSwitch(node);
        // // TODO scorreia
        // }
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.analysis.ColumnAnalysisExecutor#check(org.talend.dataquality.analysis.Analysis)
     */
    @Override
    protected boolean check(Analysis analysis) {

        boolean check = super.check(analysis);
        if (!check) {
            return false;
        } else {
            EList<Indicator> indicators = analysis.getResults().getIndicators();
            for (Indicator indicator : indicators) {
                if (indicator instanceof AllMatchIndicator) {
                    if (!checkAllMatchIndicator((AllMatchIndicator) indicator)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    protected boolean checkAllMatchIndicator(AllMatchIndicator indicator) {
        EList<RegexpMatchingIndicator> indicators = indicator.getCompositeRegexMatchingIndicators();
        String patternNames = "";
        for (RegexpMatchingIndicator rmi : indicators) {
            if (null == rmi.getRegex()) {

                patternNames += System.getProperty("line.separator") + "\"" + rmi.getName() + "\"";
            }
            // MOD klliu bug 14527 2010-08-09
            else if (rmi.getRegex().equals(rmi.getName())) {
                patternNames += System.getProperty("line.separator") + "\"" + rmi.getName() + "\"";
            }
        }
        if ("" != patternNames) {
            this.errorMessage = Messages.getString("MultiColumnAnalysisExecutor.checkAllMatchIndicatorForDbType", patternNames);
            return false;
        }
        return true;
    }
}
