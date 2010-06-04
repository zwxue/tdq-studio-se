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
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.talend.cwm.db.connection.ConnectionUtils;
import org.talend.cwm.dburl.SupportDBUrlType;
import org.talend.cwm.exception.AnalysisExecutionException;
import org.talend.cwm.helper.CatalogHelper;
import org.talend.cwm.helper.ColumnHelper;
import org.talend.cwm.helper.ColumnSetHelper;
import org.talend.cwm.helper.ResourceHelper;
import org.talend.cwm.helper.SchemaHelper;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.cwm.management.connection.DatabaseConstant;
import org.talend.cwm.management.i18n.Messages;
import org.talend.cwm.relational.RelationalPackage;
import org.talend.cwm.relational.TdCatalog;
import org.talend.cwm.relational.TdColumn;
import org.talend.cwm.relational.TdSchema;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.analysis.AnalysisResult;
import org.talend.dataquality.domain.Domain;
import org.talend.dataquality.domain.RangeRestriction;
import org.talend.dataquality.domain.pattern.Pattern;
import org.talend.dataquality.expressions.TdExpression;
import org.talend.dataquality.helpers.BooleanExpressionHelper;
import org.talend.dataquality.helpers.DomainHelper;
import org.talend.dataquality.helpers.IndicatorHelper;
import org.talend.dataquality.indicators.CompositeIndicator;
import org.talend.dataquality.indicators.DataminingType;
import org.talend.dataquality.indicators.DateGrain;
import org.talend.dataquality.indicators.DateParameters;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.IndicatorParameters;
import org.talend.dataquality.indicators.IndicatorsPackage;
import org.talend.dataquality.indicators.NullCountIndicator;
import org.talend.dataquality.indicators.RowCountIndicator;
import org.talend.dataquality.indicators.TextParameters;
import org.talend.dataquality.indicators.definition.CharactersMapping;
import org.talend.dataquality.indicators.definition.IndicatorDefinition;
import org.talend.dq.helper.UDIHelper;
import org.talend.dq.indicators.definitions.DefinitionHandler;
import org.talend.dq.nodes.indicator.type.IndicatorEnum;
import org.talend.utils.collections.MultiMapHelper;
import org.talend.utils.sql.Java2SqlType;
import org.talend.utils.sugars.TypedReturnCode;
import orgomg.cwm.objectmodel.core.Expression;
import orgomg.cwm.objectmodel.core.ModelElement;
import orgomg.cwm.objectmodel.core.Package;
import orgomg.cwm.resource.relational.ColumnSet;

import Zql.ParseException;

/**
 * @author scorreia
 * 
 * Generates the SQL queries for each indicator and each column to be analyzed. Then executes the queries and stores the
 * results.
 */
public class ColumnAnalysisSqlExecutor extends ColumnAnalysisExecutor {

    protected boolean parallel = true;

    private static final String ALIAS = "(\\w*)\\."; //$NON-NLS-1$

    /**
     * TODO scorreia this constant must be replaced by a default preference and the possibility to the user to change it
     * for each indicator.
     */
    private static final int TOP_N = org.talend.dataquality.PluginConstant.DEFAULT_TOP_N;

    private static Logger log = Logger.getLogger(ColumnAnalysisSqlExecutor.class);

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.analysis.AnalysisExecutor#createSqlStatement(org.talend.dataquality.analysis.Analysis)
     */
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
     * Method "createSqlQuery".
     * 
     * @param dataFilterExpression
     * 
     * @param analysis
     * 
     * @param indicator
     * @throws ParseException
     * @throws AnalysisExecutionException
     */
    private boolean createSqlQuery(String dataFilterAsString, Indicator indicator) throws ParseException,
            AnalysisExecutionException {
        ModelElement analyzedElement = indicator.getAnalyzedElement();
        if (analyzedElement == null) {
            return traceError("Analyzed element is null for indicator " + indicator.getName());
        }
        TdColumn tdColumn = SwitchHelpers.COLUMN_SWITCH.doSwitch(indicator.getAnalyzedElement());
        if (tdColumn == null) {
            return traceError("Analyzed element is not a column for indicator " + indicator.getName());
        }
        String colName = getQuotedColumnName(tdColumn);
        if (!belongToSameSchemata(tdColumn)) {
            StringBuffer buf = new StringBuffer();
            for (orgomg.cwm.objectmodel.core.Package schema : schemata.values()) {
                buf.append(schema.getName() + " "); //$NON-NLS-1$
            }
            log.error("Column " + colName + " does not belong to an existing schema [" + buf.toString().trim() + "]");
            return false;
        }
        colName = castColumn(indicator, tdColumn, colName);

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
        List<String> rangeStrings = null;
        DateGrain dateAggregationType = null;
        IndicatorParameters parameters = indicator.getParameters();
        if (parameters != null) {
            // handle bins
            Domain bins = parameters.getBins();
            if (bins != null) {
                rangeStrings = getBinsAsGenericString(bins.getRanges(), colName);
            }

            DateParameters dateParameters = parameters.getDateParameters();
            if (dateParameters != null) {
                dateAggregationType = dateParameters.getDateAggregationType();
            }

            TextParameters textParameter = parameters.getTextParameter();
            if (textParameter != null) {
                if (textParameter.isUseNulls()) {
                    colName = dbms().replaceNullsWithString(colName, "''");
                }
                if (textParameter.isIgnoreCase()) {
                    colName = dbms().toUpperCase(colName);
                }
                if (!textParameter.isUseBlank()
                        && IndicatorsPackage.eINSTANCE.getLengthIndicator().isSuperTypeOf(indicatorEclass)) {
                    whereExpression.add(dbms().isNotBlank(colName));
                } else if (textParameter.isUseBlank()
                        && IndicatorsPackage.eINSTANCE.getFrequencyIndicator().isSuperTypeOf(indicatorEclass)) {
                    colName = dbms().trim(colName);
                }
            }
        }

        final ColumnSet columnSetOwner = ColumnHelper.getColumnSetOwner(tdColumn);
        String schemaName = getQuotedSchemaName(columnSetOwner);

        String table = getQuotedTableName(tdColumn);

        // --- normalize table name
        String catalogName = getQuotedCatalogName(columnSetOwner);
        if (catalogName == null && schemaName != null) {
            // try to get catalog above schema
            final TdSchema parentSchema = SchemaHelper.getParentSchema(columnSetOwner);
            final TdCatalog parentCatalog = CatalogHelper.getParentCatalog(parentSchema);
            catalogName = parentCatalog != null ? parentCatalog.getName() : null;
        }
        // MOD by zshen: change schemaName of sybase database to Table's owner.
        if (dbms().getDbmsName().equals(SupportDBUrlType.SYBASEDEFAULTURL.getLanguage())) {
            schemaName = ColumnSetHelper.getTableOwner(columnSetOwner);
        }
        // ~11934

        table = dbms().toQualifiedName(catalogName, schemaName, table);

        // ### evaluate SQL Statement depending on indicators ###
        String completedSqlString = null;

        // --- handle case when indicator is a quantile
        if (indicatorEclass.equals(IndicatorsPackage.eINSTANCE.getMedianIndicator())
                || indicatorEclass.equals(IndicatorsPackage.eINSTANCE.getLowerQuartileIndicator())
                || indicatorEclass.equals(IndicatorsPackage.eINSTANCE.getUpperQuartileIndicator())) {
            // TODO scorreia test type of column and cast when needed
            completedSqlString = getCompletedStringForQuantiles(indicator, sqlGenericExpression, colName, table, whereExpression);
            if (completedSqlString != null) {
                whereExpression = duplicateForCrossJoin(completedSqlString, whereExpression, tdColumn);
                completedSqlString = addWhereToSqlStringStatement(whereExpression, completedSqlString);
            }
        } else

        // --- handle case when frequency indicator
        if (indicatorEclass.equals(IndicatorsPackage.eINSTANCE.getFrequencyIndicator())
                || IndicatorsPackage.eINSTANCE.getFrequencyIndicator().isSuperTypeOf(indicatorEclass)
                || indicatorEclass.equals(IndicatorsPackage.eINSTANCE.getModeIndicator()) || UDIHelper.isFrequency(indicator)) {
            // TODO scorreia test type of column and cast when needed
            // with ranges (frequencies of numerical intervals)

            int topN = indicatorEclass.equals(IndicatorsPackage.eINSTANCE.getModeIndicator()) ? 1 : getTopN(indicator);
            if (topN <= 0) {
                topN = TOP_N;
            }

            if (rangeStrings != null) {
                completedSqlString = getUnionCompletedString(indicator, sqlGenericExpression, colName, table, whereExpression,
                        rangeStrings);
                if (indicatorEclass.equals(IndicatorsPackage.eINSTANCE.getModeIndicator())) {
                    // get the best row
                    completedSqlString = dbms().getTopNQuery(completedSqlString, topN);
                }
            } else if (dateAggregationType != null && !dateAggregationType.equals(DateGrain.NONE)
            // MOD scorreia 2008-06-23 check column type (robustness against bug 4287)
                    && Java2SqlType.isDateInSQL(tdColumn.getJavaType())) {
                // frequencies with date aggregation
                completedSqlString = getDateAggregatedCompletedStringWithoutAlia(sqlGenericExpression, colName, table,
                        dateAggregationType);
                completedSqlString = addWhereToSqlStringStatement(whereExpression, completedSqlString);
                completedSqlString = dbms().getTopNQuery(completedSqlString, topN);
            } else { // usual nominal frequencies
                // wrap column name into a function for pattern finder indicator
                if (indicatorEclass.equals(IndicatorsPackage.eINSTANCE.getPatternFreqIndicator())
                        || indicatorEclass.equals(IndicatorsPackage.eINSTANCE.getPatternLowFreqIndicator())) {
                    // done scorreia: get user defined functions for pattern finder
                    // MOD xqliu 2009-07-01 bug 7818
                    // MOD zshen for bug 12675 2010-05-12
                    if (Java2SqlType.isNumbericInSQL(tdColumn.getJavaType()) || Java2SqlType.isDateInSQL(tdColumn.getJavaType())) {
                        colName = addFunctionTypeConvert(colName);
                        // ~12675
                    }
                    final EList<CharactersMapping> charactersMapping = indicatorDefinition.getCharactersMapping();
                    colName = dbms().getPatternFinderFunction(colName, charactersMapping);
                    if (colName == null) { // no replacement found, try the default one
                        colName = dbms().getPatternFinderDefaultFunction(colName);
                    }
                    if (colName == null) { // no replacement found, try the default one
                        return traceError("No replacement found for database type: " + language + " for indicator "
                                + indicator.getName());
                    }
                    // ~
                } else if (indicatorEclass.equals(IndicatorsPackage.eINSTANCE.getSoundexFreqIndicator())
                        || indicatorEclass.equals(IndicatorsPackage.eINSTANCE.getSoundexLowFreqIndicator())) {
                    // change table and soundex(column)
                    table = dbms().getSoundexFunction(table, colName);
                }

                completedSqlString = dbms().fillGenericQueryWithColumnTableAndAlias(sqlGenericExpression.getBody(), colName,
                        table, colName);
                completedSqlString = addWhereToSqlStringStatement(whereExpression, completedSqlString);
                completedSqlString = dbms().getTopNQuery(completedSqlString, topN);
            }
        } else

        // --- handle case of matching pattern count
        if (IndicatorsPackage.eINSTANCE.getPatternMatchingIndicator().isSuperTypeOf(indicatorEclass)) {
            List<String> patterns = getPatterns(indicator);
            if (patterns.isEmpty()) {
                return traceError("No pattern found for database type: " + language + " for indicator " + indicator.getName());
            }
            completedSqlString = replaceVariables(sqlGenericExpression.getBody(), colName, table, patterns);
            completedSqlString = addWhereToSqlStringStatement(whereExpression, completedSqlString);
        } else {
            // --- handle case of default value count -> create the where clause
            if (IndicatorsPackage.eINSTANCE.getDefValueCountIndicator().equals(indicatorEclass)) {
                String defValue = ColumnHelper.getDefaultValue(tdColumn);
                if (defValue == null) {
                    return traceError("No default value exits for this column " + colName + ". The indicator "
                            + indicator.getName() + " cannot be evaluated.");
                }
                // need to generate different SQL where clause for each type.
                int javaType = tdColumn.getJavaType();
                if (!Java2SqlType.isNumbericInSQL(javaType)) {
                    defValue = "'" + defValue + "'"; //$NON-NLS-1$ //$NON-NLS-2$
                }
                whereExpression.add(colName + dbms().equal() + defValue);
            }

            // --- default case
            completedSqlString = dbms().fillGenericQueryWithColumnsAndTable(sqlGenericExpression.getBody(), colName, table);
            completedSqlString = addWhereToSqlStringStatement(whereExpression, completedSqlString);
        }

        // completedSqlString is the final query
        String finalQuery = completedSqlString;

        if (finalQuery != null) {
            TdExpression instantiateSqlExpression = BooleanExpressionHelper.createTdExpression(language, finalQuery);
            indicator.setInstantiatedExpression(instantiateSqlExpression);
            return true;
        }

        return false;
    }

    private String addFunctionTypeConvert(String colName) {
        if (colName == null) {
            return colName;
        }
        colName = " CAST(" + colName + " AS CHAR(20)) ";
        return colName;
    }

    /**
     * Method "duplicateForCrossJoin". For some SQL queries, auto-joins are used in subqueries. This means that the
     * table has two differents aliases and the columns must be prefixed with the alias of the table. Each where clause
     * must be duplicated. For example, the clause "AGE > 10" must be duplicated to give "a.AGE > 10" and "b.AGE" when
     * table aliases are "a" and "b".
     * 
     * @param completedSqlString the SQL query
     * 
     * @param whereExpression some where clauses
     * @param tdColumn the analyzed column
     * @return a list of new where clauses (or the one given as argument)
     */
    private List<String> duplicateForCrossJoin(String completedSqlString, List<String> whereExpression, TdColumn tdColumn) {
        if (whereExpression.isEmpty()) {
            return whereExpression;
        }
        String quotedColName = getQuotedColumnName(tdColumn);
        String[] tableAliases = getTableTableAliasA(completedSqlString, quotedColName);
        if (tableAliases == null) {
            return whereExpression;
        }
        List<String> duplicatedWhereExpressions = new ArrayList<String>();
        // get the table
        ColumnSet columnSetOwner = ColumnHelper.getColumnSetOwner(tdColumn);
        List<TdColumn> columns = ColumnHelper.getColumns(columnSetOwner.getFeature());
        for (String where : whereExpression) {
            // we expect only 2 table aliases, hence two distinct where clauses.
            String whereA = where;
            String whereB = where;
            for (TdColumn col : columns) {
                String colNameToReplace = where.contains(quotedColName) ? quotedColName : col.getName();
                if (where.contains(colNameToReplace)) {
                    whereA = whereA.replace(colNameToReplace, tableAliases[0] + "." + colNameToReplace); //$NON-NLS-1$
                    whereB = whereB.replace(colNameToReplace, tableAliases[1] + "." + colNameToReplace); //$NON-NLS-1$
                }
            }
            duplicatedWhereExpressions.add(whereA);
            duplicatedWhereExpressions.add(whereB);
        }

        return duplicatedWhereExpressions;
    }

    /**
     * DOC scorreia Comment method "getTableTableAliasA".
     * 
     * @param completedSqlString
     * @param quotedColName
     * @return
     */
    private String[] getTableTableAliasA(String completedSqlString, String quotedColName) {
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ALIAS + quotedColName);
        Matcher matcher = p.matcher(completedSqlString);
        if (!matcher.find()) {
            return null;
        }
        String g1 = matcher.group(1);
        String g2 = null;
        while (matcher.find()) {
            g2 = matcher.group(1);
            if (!g1.equals(g2)) {
                break;
            }
        }
        if (g1 == null || g2 == null) {
            return null;
        }
        return new String[] { g1, g2 };
    }

    /**
     * DOC scorreia Comment method "getTopN".
     * 
     * @param indicator
     * @return
     */
    private int getTopN(Indicator indicator) {
        IndicatorParameters parameters = indicator.getParameters();
        return (parameters == null) ? -1 : parameters.getTopN();
    }

    /**
     * DOC scorreia Comment method "replaceVariables".
     * 
     * @param sqlGenericString
     * @param colName
     * @param table
     * @param patterns
     * @return
     * 
     */
    private String replaceVariables(String sqlGenericString, String colName, String table, List<String> patterns) {
        assert (patterns.size() != 0);
        return dbms().fillGenericQueryWithColumnTablePattern(sqlGenericString, colName, table, patterns.get(0));
    }

    /**
     * DOC scorreia Comment method "getPatterns".
     * 
     * @param indicator
     * @return the patterns or null if none has been found
     */
    private List<String> getPatterns(Indicator indicator) {
        List<String> patternStrings = new ArrayList<String>();
        Domain dataValidDomain = indicator.getParameters().getDataValidDomain();
        if (dataValidDomain == null) {
            return patternStrings;
        }
        EList<Pattern> patterns = dataValidDomain.getPatterns();
        for (Pattern pattern : patterns) {
            String regexp = this.dbms().getRegexp(pattern);
            if (regexp != null) {
                patternStrings.add(regexp);
            }
        }
        return patternStrings;
    }

    /**
     * Method "castColumn".
     * 
     * @param indicator
     * @param tdColumn
     * @param colName the name of the given column (tdColumn.getName() ) (could contain quotes)
     * @return
     */
    private String castColumn(Indicator indicator, TdColumn tdColumn, String colName) {
        int javaType = tdColumn.getJavaType();
        boolean isText = Java2SqlType.isTextInSQL(javaType);

        String contentType = tdColumn.getContentType();
        DataminingType dataminingType = DataminingType.get(contentType);
        if (DataminingType.INTERVAL.equals(dataminingType) && (isText)) {
            // cast is needed
            // TODO handle date
            boolean isDate = Java2SqlType.isDateInSQL(javaType);
            if (isDate) {
                throw new UnsupportedOperationException();
            }
            boolean isNumeric = Java2SqlType.isNumbericInSQL(javaType);
            if (isNumeric) {
                // TODO scorreia user should tell the expected format
                return "CAST (" + colName + " AS DECIMAL)"; //$NON-NLS-1$ //$NON-NLS-2$
            }
        }
        return colName;
    }

    private static final String COMMA = " , "; //$NON-NLS-1$

    /**
     * DOC scorreia Comment method "getDateAggregatedCompletedString".
     * 
     * @param indicator
     * @param sqlExpression
     * @param colName
     * @param table
     * @param dataFilterExpression
     * @param dateAggregationType
     * @deprecated
     * @return
     */
    @SuppressWarnings("fallthrough")
    private String getDateAggregatedCompletedString(Expression sqlExpression, String colName, String table,
            DateGrain dateAggregationType) {
        int nbExtractedColumns = 0;
        String result = ""; //$NON-NLS-1$
        String aliases = ""; // used in group by clause in MySQL //$NON-NLS-1$
        String alias;
        switch (dateAggregationType) {
        case DAY:
            alias = getAlias(colName, DateGrain.DAY);
            result = dbms().extractDay(colName) + alias + comma(result);
            aliases = alias + comma(aliases);
            nbExtractedColumns++;
        case WEEK:
            alias = getAlias(colName, DateGrain.WEEK);
            result = dbms().extractWeek(colName) + alias + comma(result);
            aliases = alias + comma(aliases);
            nbExtractedColumns++;
            // no break
        case MONTH:
            alias = getAlias(colName, DateGrain.MONTH);
            result = dbms().extractMonth(colName) + alias + comma(result);
            aliases = alias + comma(aliases);
            nbExtractedColumns++;
            // no break
        case QUARTER:
            alias = getAlias(colName, DateGrain.QUARTER);
            result = dbms().extractQuarter(colName) + alias + comma(result);
            aliases = alias + comma(aliases);
            nbExtractedColumns++;
            // no break
        case YEAR:
            alias = getAlias(colName, DateGrain.YEAR);
            result = dbms().extractYear(colName) + alias + comma(result);
            aliases = alias + comma(aliases);
            nbExtractedColumns++;
            break;
        case NONE:
            result = colName;
            nbExtractedColumns++;
            aliases = colName; // bug 5336 fixed aliases must not be empty otherwise the group by clause is empty.
            break;
        default:
            break;
        }
        String groupByAliases = dbms().supportAliasesInGroupBy() ? aliases : result;
        String sql = dbms().fillGenericQueryWithColumnTableAndAlias(sqlExpression.getBody(), result, table, groupByAliases);
        return sql;
    }

    /**
     * 
     * DOC mzhao bug 12675: Drill down of date type not correct. Here would not use alias.
     * 
     * @param sqlExpression
     * @param colName
     * @param table
     * @param dateAggregationType
     * @return
     */
    private String getDateAggregatedCompletedStringWithoutAlia(Expression sqlExpression, String colName, String table,
            DateGrain dateAggregationType) {
        int nbExtractedColumns = 0;
        String result = ""; //$NON-NLS-1$
        //        String aliases = ""; // used in group by clause in MySQL //$NON-NLS-1$
        // String alias;
        switch (dateAggregationType) {
        case DAY:
            // alias = getAlias(colName, DateGrain.DAY);
            result = dbms().extractDay(colName) + comma(result);
            // aliases = alias + comma(aliases);
            nbExtractedColumns++;
        case WEEK:
            // alias = getAlias(colName, DateGrain.WEEK);
            result = dbms().extractWeek(colName) + comma(result);
            // aliases = alias + comma(aliases);
            nbExtractedColumns++;
            // no break
        case MONTH:
            // alias = getAlias(colName, DateGrain.MONTH);
            result = dbms().extractMonth(colName) + comma(result);
            // aliases = alias + comma(aliases);
            nbExtractedColumns++;
            // no break
        case QUARTER:
            // alias = getAlias(colName, DateGrain.QUARTER);
            result = dbms().extractQuarter(colName) + comma(result);
            // aliases = alias + comma(aliases);
            nbExtractedColumns++;
            // no break
        case YEAR:
            // alias = getAlias(colName, DateGrain.YEAR);
            result = dbms().extractYear(colName) + comma(result);
            // aliases = alias + comma(aliases);
            nbExtractedColumns++;
            break;
        case NONE:
            result = colName;
            nbExtractedColumns++;
            // aliases = colName; // bug 5336 fixed aliases must not be empty otherwise the group by clause is empty.
            break;
        default:
            break;
        }
        // String groupByAliases = dbms().supportAliasesInGroupBy() ? aliases : result;
        String sql = dbms().fillGenericQueryWithColumnTableAndAlias(sqlExpression.getBody(), result, table, result);
        return sql;
    }

    private String getAlias(String colName, DateGrain dateAggregationType) {
        if (dbms().supportAliasesInGroupBy()) {
            // return " TDAL_" + unquote(colName) + dateAggregationType.getName() + " ";
            // MOD by hcheng for 7338 SQL syntax error if the name of column has invalid char.
            return " TDAL_" + dateAggregationType.getName() + " "; //$NON-NLS-1$ //$NON-NLS-2$
        } else {
            return ""; //$NON-NLS-1$
        }
    }

    /**
     * Method "unquote" remove surrounding identifier quotes.
     * 
     * @param colName a name with quotes (or without)
     * @return the name without the quotes.
     */
    private String unquote(String colName) {
        String dbQuoteString = dbms().getDbQuoteString();
        return colName.replace(dbQuoteString, ""); //$NON-NLS-1$
    }

    /**
     * Method "comma" puts a comma before a non empty string.
     * 
     * @param previousContent
     * @return either previousContent or " , " + previousContent
     */
    private String comma(String previousContent) {
        return (previousContent.length() == 0) ? previousContent : COMMA + previousContent;
    }

    /**
     * DOC scorreia Comment method "getFinalSqlStringStatement".
     * 
     * @param dataFilterExpression
     * @param whereExpression
     * @param completedSqlString
     * @return
     * @throws ParseException
     */
    private String addWhereToSqlStringStatement(List<String> whereExpressions, String completedSqlString) throws ParseException {
        return dbms().addWhereToSqlStringStatement(completedSqlString, whereExpressions);
    }

    /**
     * DOC scorreia Comment method "getUnionCompletedString".
     * 
     * @param indicator
     * @param sqlExpression
     * @param colName
     * @param table
     * @param whereExpression
     * @param rangeStrings
     * @return
     * @throws ParseException
     */
    private String getUnionCompletedString(Indicator indicator, Expression sqlExpression, String colName, String table,
            List<String> whereExpression, List<String> rangeStrings) throws ParseException {
        StringBuffer buf = new StringBuffer();
        final int last = rangeStrings.size();

        String sqlGenericExpression = sqlExpression.getBody();

        // MOD scorreia 2008-09-03 Bug #4976 Order by clause cannot be in each single select statement (Oracle
        // constraint, but MySQL allows it)
        // hence we extract the ORDER BY clause and add it at the end.
        int idxOfOrderBY = sqlGenericExpression.indexOf(" ORDER BY"); //$NON-NLS-1$
        // MOD scorreia 2008-12-16 handle teradata case where "order by" clause needs a number to identify the column
        // by removing the unnecessary "order by" clause
        // String orderBy = (idxOfOrderBY != -1) ? sqlGenericExpression.substring(idxOfOrderBY) : "";
        String singleStatement = (idxOfOrderBY != -1) ? sqlGenericExpression.substring(0, idxOfOrderBY) : sqlGenericExpression;

        for (int i = 0; i < last; i++) {
            String singleSelect = getCompletedSingleSelect(indicator, singleStatement, colName, table, whereExpression,
                    rangeStrings.get(i));
            buf.append('('); // parenthesis necessary for MySQL
            buf.append(singleSelect);
            buf.append(')'); // parenthesis necessary for MySQL
            if (i != last - 1) {
                buf.append(dbms().unionAll());
            }
        }
        // MOD scorreia 2008-09-03 Bug #4976 append the order by clause at the end of the whole statement
        // if (idxOfOrderBY != -1) {
        // buf.append(orderBy);
        // }
        return buf.toString();
    }

    /**
     * DOC scorreia Comment method "getCompletedSingleSelect".
     * 
     * @param indicator
     * @param sqlExpression
     * @param colName
     * @param table
     * @param whereExpression
     * @param range
     * @return
     * @throws ParseException
     */
    private String getCompletedSingleSelect(Indicator indicator, String sqlGenericExpression, String colName, String table,
            List<String> whereExpression, String range) throws ParseException {
        String completedRange = this.unquote(range); // replaceVariablesLow(range, this.unquote(colName),
        // this.unquote(table));
        String rangeColumn = "'" + completedRange + "'"; //$NON-NLS-1$ //$NON-NLS-2$

        String singleQuery = removeGroupBy(sqlGenericExpression);
        String completedSqlString = dbms().fillGenericQueryWithColumnsAndTable(singleQuery, rangeColumn, table);

        List<String> allWheresForSingleSelect = new ArrayList<String>(whereExpression);

        // add this range clause to the given where clause (but do not modify the given where clause)
        // allWheresForSingleSelect.add(completedRange);

        completedSqlString = addWhereToSqlStringStatement(allWheresForSingleSelect, completedSqlString);
        // replacement in order to get lines even when no data is available
        // do this replacement after having added the where clause otherwise the parsing with ZQL will fail.
        completedSqlString = replaceCountByZeroCount(completedSqlString, range);
        return completedSqlString;
    }

    /**
     * DOC scorreia Comment method "removeGroupBy".
     * 
     * @param sqlGenericExpression
     * @return
     */
    private String removeGroupBy(String sqlGenericExpression) {
        int idxOfGroupBy = sqlGenericExpression.toUpperCase().indexOf(dbms().groupBy());
        return sqlGenericExpression.substring(0, idxOfGroupBy);
    }

    /**
     * Method "replaceCountByZeroCount" replaces "COUNT(*)" by "CASE WHEN completedRange THEN COUNT(*) ELSE 0 END" in
     * the given SQL statement completedSqlString.
     * 
     * @param completedSqlString
     * @param completedRange
     * @return the new SQL statement
     */
    private String replaceCountByZeroCount(String completedSqlString, String completedRange) {
        return completedSqlString.replace("COUNT(*)", "COUNT( CASE WHEN " + completedRange + " THEN 1 END )"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }

    /**
     * DOC scorreia Comment method "getBinsAsString".
     * 
     * @param ranges
     * @return
     */
    private List<String> getBinsAsGenericString(EList<RangeRestriction> ranges, String colName) {
        List<String> bins = new ArrayList<String>();
        for (RangeRestriction rangeRestriction : ranges) {
            String bin = colName + dbms().greaterOrEqual() + DomainHelper.getMinValue(rangeRestriction) + dbms().and() + colName
                    + dbms().less() + DomainHelper.getMaxValue(rangeRestriction);
            // set the name of the RangeRestriction here
            // TODO range name should be set at the construction (in the bins designer wizard)
            rangeRestriction.setName(this.unquote(bin));
            bins.add(bin);
        }
        return bins;
    }

    /**
     * DOC scorreia Comment method "getCompletedString".
     * 
     * @param indicator
     * @param sqlExpression
     * @param colName
     * @param table
     * @param whereExpression
     * @throws AnalysisExecutionException
     */
    private String getCompletedStringForQuantiles(Indicator indicator, Expression sqlExpression, String colName, String table,
            List<String> whereExpression) throws AnalysisExecutionException {
        // first, count nb lines
        String catalogOrSchema = getCatalogOrSchemaName(indicator.getAnalyzedElement());
        long count = getCount(cachedAnalysis, colName, table, catalogOrSchema, whereExpression);
        if (count == -1) {
            this.errorMessage = Messages.getString("ColumnAnalysisSqlExecutor.GotInvalidResultSet", //$NON-NLS-1$
                    dbms().toQualifiedName(catalogOrSchema, null, colName));
            return null;
            //            throw new AnalysisExecutionException(Messages.getString("ColumnAnalysisSqlExecutor.GotInvalidResultSet", //$NON-NLS-1$
            // dbms().toQualifiedName(catalogOrSchema, null, colName)));
        }

        if (count == 0) {
            this.errorMessage = Messages.getString("ColumnAnalysisSqlExecutor.CannotComputeQuantile",//$NON-NLS-1$
                    dbms().toQualifiedName(catalogOrSchema, null, colName));
            return null;
            // throw new AnalysisExecutionException(errorMessage);
        }

        Long midleCount = getOffsetInLimit(indicator, count);
        Integer nbRow = getNbReturnedRows(indicator, count);

        long nPlusSkip = midleCount + nbRow; // needed for MSSQL query with TOP clause
        return dbms().fillGenericQueryWithColumnTableLimitOffset(sqlExpression.getBody(), colName, table, String.valueOf(nbRow),
                String.valueOf(midleCount), String.valueOf(nPlusSkip));
    }

    /**
     * DOC scorreia Comment method "getNbReturnedRows".
     * 
     * @param indicator
     * @param count
     * @return
     */
    private Integer getNbReturnedRows(Indicator indicator, long count) {
        if (indicator.eClass().equals(IndicatorsPackage.eINSTANCE.getMedianIndicator())) {
            boolean isEven = count % 2 == 0;
            return (isEven) ? 2 : 1;
        } else if (indicator.eClass().equals(IndicatorsPackage.eINSTANCE.getLowerQuartileIndicator())
                || indicator.eClass().equals(IndicatorsPackage.eINSTANCE.getUpperQuartileIndicator())) {
            return (count % 4) == 0 ? 2 : 1;
        }
        return 1;
    }

    /**
     * See http://en.wikipedia.org/wiki/Quartile Method "getLimitFirstArg".
     * 
     * @param indicator
     * @param count
     * @return the number of rows to skip in order to compute the quartiles. In case of an odd number of rows, one row
     * is returned and the returned value is the index of the row before the quartile. In case of an even number of
     * rows, two rows are returned and the quartile is the average of the two values (for the median).
     */
    private Long getOffsetInLimit(Indicator indicator, long count) {
        // get the number of rows to be skipped by the LIMIT n,o in MySQL query.
        // Be careful, -1 is needed to get the searched index -1 because the searched index is 3 when n=4
        boolean isEven = count % 2 == 0;
        if (indicator.eClass().equals(IndicatorsPackage.eINSTANCE.getMedianIndicator())) {
            return isEven ? count / 2 - 1 : (count - 1) / 2;
        } else if (indicator.eClass().equals(IndicatorsPackage.eINSTANCE.getLowerQuartileIndicator())) {
            return /* isEven ? */Math.round(0.25 * (count + 1)) - 1 /* : Math.round(0.25 count) */;
        } else if (indicator.eClass().equals(IndicatorsPackage.eINSTANCE.getUpperQuartileIndicator())) {
            Long res = 0L; // count - (Math.round(0.25 * (count + 1))) - 1; does not work
            if (isEven) {
                if ((count / 2) % 2 == 0) {
                    res = 3 * count / 4 - 1;
                } else {
                    res = 3 * ((count / 2) + 1) / 2 - 2;
                }
            } else { // odd number of rows
                if (((count + 1) / 2) % 2 == 0) {
                    res = 3 * (count + 1) / 4 - 1;
                } else {
                    res = 3 * (((count + 1) / 2) - 1) / 2;
                }
            }
            return res;
        }
        return null;
    }

    /**
     * Method "getCount".
     * 
     * @param analysis
     * @param colName the column name should be surrounded by the SQL quotes
     * @param table the table name should be surrounded by the SQL quotes
     * @param catalog the catalog (or schema) name
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
     * DOC scorreia Comment method "getCount".
     * 
     * @param cachedAnalysis2
     * @param colName
     * @param quote
     * @param whereExpression
     * @param catalogName
     * @return -1L when sql went ok, but obtained result set is invalid.
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
        // MOD zshen exchange OracleODBC will get a double value to int.Else Have a NumberFormatException at here.
        return Long.valueOf(String.valueOf(myResultSet.get(0)[0]).split("\\.")[0]);
        // ~
    }

    /**
     * 
     * 
     * DOC scorreia Comment method "replaceVariablesLow".
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
     * Method "surroundSingleQuotes".
     * 
     * see http://java.sun.com/j2se/1.4.2/docs/api/java/text/MessageFormat.html
     * 
     * @param sqlGenericString
     * @return
     */
    private String surroundSingleQuotes(String sqlGenericString) {
        return sqlGenericString.replaceAll("'", "''"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Method "traceError".
     * 
     * @param error the message to set in errorMessage
     * @return always false
     */
    protected boolean traceError(String error) {
        this.errorMessage = error;
        log.error(this.errorMessage);
        return false;
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
            // store map of element to each indicator used for computation (leaf indicator)
            Map<ModelElement, List<Indicator>> elementToIndicator = new HashMap<ModelElement, List<Indicator>>();

            // execute the sql statement for each indicator
            Collection<Indicator> indicators = IndicatorHelper.getIndicatorLeaves(analysis.getResults());
            // MOD xqliu 2009-08-07 bug 6194
            if (canParallel()) {
                ok = runAnalysisIndicatorsParallel(connection, elementToIndicator, indicators);
            } else {
                ok = runAnalysisIndicators(connection, elementToIndicator, indicators);
            }
            // ~
            connection.close();

            // --- finalize indicators by setting the row count and null when they exist.
            setRowCountAndNullCount(elementToIndicator);

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
     * DOC xqliu Comment method "runAnalysisIndicators".
     * 
     * @param connection
     * @param elementToIndicator
     * @param indicators
     * @return
     * @throws SQLException
     */
    private boolean runAnalysisIndicators(Connection connection, Map<ModelElement, List<Indicator>> elementToIndicator,
            Collection<Indicator> indicators) throws SQLException {
        boolean ok = true;
        for (Indicator indicator : indicators) {
            // skip composite indicators that do not require a sql execution
            if (indicator instanceof CompositeIndicator) {
                // options of composite indicators are handled elsewhere
                continue;
            }
            // set the connection's catalog
            String catalogName = getCatalogOrSchemaName(indicator.getAnalyzedElement());
            if (catalogName != null) { // check whether null argument can be given
                changeCatalog(catalogName, connection);
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
     * DOC xqliu Comment method "runAnalysisIndicatorsParallel".
     * 
     * @param connection
     * @param elementToIndicator
     * @param indicators
     * @return
     * @throws SQLException
     */
    private boolean runAnalysisIndicatorsParallel(Connection connection, Map<ModelElement, List<Indicator>> elementToIndicator,
            Collection<Indicator> indicators) throws SQLException {
        boolean ok = true;
        List<Thread> runners = new ArrayList<Thread>();
        List<ColumnAnalysisSqlParallelExecutor> columnSqlExecutor = new ArrayList<ColumnAnalysisSqlParallelExecutor>();
        try {
            for (Indicator indicator : indicators) {
                ColumnAnalysisSqlParallelExecutor columnSqlParallel = ColumnAnalysisSqlParallelExecutor.createInstance(this,
                        connection, elementToIndicator, indicator);
                columnSqlExecutor.add(columnSqlParallel);
                Thread thread = new Thread(columnSqlParallel);
                runners.add(thread);
                thread.start();
            }
            while (true) {
                boolean stop = true;
                for (Thread thread : runners) {
                    if (thread.isAlive()) {
                        stop = false;
                        Thread.sleep(1000);
                    }
                }
                if (stop) {
                    break;
                }
            }

        } catch (InterruptedException e) {
            log.error(e, e);
        }
        runners = null;
        // ADD xqliu 2009-09-23 bug 9058
        for (ColumnAnalysisSqlParallelExecutor executor : columnSqlExecutor) {
            if (executor.getException() != null) {
                columnSqlExecutor = null;
                throw new SQLException(executor.getException().toString());
            }
        }
        columnSqlExecutor = null;
        // ~
        return ok;
    }

    /**
     * DOC scorreia Comment method "setRowCountAndNullCount".
     * 
     * @param elementToIndicator
     */
    protected void setRowCountAndNullCount(Map<ModelElement, List<Indicator>> elementToIndicator) {
        Set<ModelElement> analyzedElements = elementToIndicator.keySet();
        for (ModelElement modelElement : analyzedElements) {
            // get row count indicator
            RowCountIndicator rowCount = getRowCountIndicator(modelElement, elementToIndicator);
            // get null count indicator
            NullCountIndicator nullCount = getNullCountIndicator(modelElement, elementToIndicator);

            List<Indicator> list = elementToIndicator.get(modelElement);
            for (Indicator ind : list) {
                // set row count value to each indicator
                if (rowCount != null && needPercentage(ind)) {
                    ind.setCount(rowCount.getCount());
                }
                // set null count value to each indicator
                if (nullCount != null) {
                    ind.setNullCount(nullCount.getNullCount());
                }
            }
        }
    }

    /**
     * DOC bZhou Comment method "needPercentage".
     * 
     * @param ind
     * @return
     */
    private boolean needPercentage(Indicator ind) {
        IndicatorEnum indType = IndicatorEnum.findIndicatorEnum(ind.eClass());

        return indType != IndicatorEnum.ModeIndicatorEnum && !indType.isAChildOf(IndicatorEnum.TextIndicatorEnum)
                && !indType.isAChildOf(IndicatorEnum.BoxIIndicatorEnum);
    }

    /**
     * DOC scorreia Comment method "getRowCountIndicator".
     * 
     * @param modelElement
     * @param elementToIndicator
     * @return
     */
    private RowCountIndicator getRowCountIndicator(ModelElement modelElement,
            Map<ModelElement, List<Indicator>> elementToIndicator) {
        List<Indicator> list = elementToIndicator.get(modelElement);
        RowCountIndicator rowCountIndicator = null;
        if (list == null) {
            return rowCountIndicator;
        }
        for (Indicator indicator : list) {
            if (IndicatorsPackage.eINSTANCE.getRowCountIndicator().equals(indicator.eClass())) {
                rowCountIndicator = (RowCountIndicator) indicator;
            }
        }
        return rowCountIndicator;
    }

    private NullCountIndicator getNullCountIndicator(ModelElement modelElement,
            Map<ModelElement, List<Indicator>> elementToIndicator) {
        List<Indicator> list = elementToIndicator.get(modelElement);
        NullCountIndicator nullCountIndicator = null;
        if (list == null) {
            return nullCountIndicator;
        }
        for (Indicator indicator : list) {
            if (IndicatorsPackage.eINSTANCE.getNullCountIndicator().equals(indicator.eClass())) {
                nullCountIndicator = (NullCountIndicator) indicator;
            }
        }
        return nullCountIndicator;
    }

    /**
     * Method "getCatalogName".
     * 
     * @param analyzedElement
     * @return the catalog or schema quoted name
     */
    protected String getCatalogOrSchemaName(ModelElement analyzedElement) {
        Package schema = super.schemata.get(analyzedElement);
        if (schema == null) {
            log.error("No schema found for column " + analyzedElement.getName());
            return null;
        }
        // else
        if (RelationalPackage.eINSTANCE.getTdSchema().equals(schema.eClass())) {
            final TdCatalog parentCatalog = CatalogHelper.getParentCatalog(schema);
            if (parentCatalog != null) {
                return parentCatalog.getName();
            }
        }
        return schema.getName();
    }

    /**
     * DOC scorreia Comment method "executeQuery".
     * 
     * @param indicator
     * 
     * @param connection
     * 
     * @param queryStmt
     * @return
     * @throws SQLException
     */
    protected boolean executeQuery(Indicator indicator, Connection connection, String queryStmt) throws SQLException {
        String cat = getCatalogOrSchemaName(indicator.getAnalyzedElement());
        if (log.isInfoEnabled()) {
            log.info("Computing indicator: " + indicator.getName());
        }
        // give result to indicator so that it handles the results
        boolean ret = false;
        try {
            List<Object[]> myResultSet = executeQuery(cat, connection, queryStmt);

            ret = indicator.storeSqlResults(myResultSet);
        } catch (Exception e) {
            log.warn(e, e);
        }
        return ret;
    }

    /**
     * DOC scorreia Comment method "executeQuery".
     * 
     * @param catalogName (can be null)
     * @param connection
     * @param queryStmt
     * @return
     * @throws SQLException
     */
    protected List<Object[]> executeQuery(String catalogName, Connection connection, String queryStmt) throws SQLException {

        if (catalogName != null) { // check whether null argument can be given
            changeCatalog(catalogName, connection);
        }
        // create query statement
        Statement statement = connection.createStatement();
        // statement.setFetchSize(fetchSize);
        if (log.isInfoEnabled()) {
            log.info("Executing query: " + queryStmt);
        }
        // MOD xqliu 2009-02-09 bug 6237
        if (continueRun()) {
            statement.execute(queryStmt);
        }

        // get the results
        ResultSet resultSet = statement.getResultSet();
        if (resultSet == null) {
            String mess = "No result set for this statement: " + queryStmt;
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
     * Method "changeCatalog".
     * 
     * @param catalogName unquoted catalog's name
     * @param connection
     * @throws SQLException
     */
    protected boolean changeCatalog(String catalogName, Connection connection) {
        try {
            // MOD xqliu 2009-12-09 bug 9822
            if (!(ConnectionUtils.isOdbcMssql(connection) || ConnectionUtils.isOdbcOracle(connection))) {
                // MOD scorreia 2008-08-01 MSSQL does not support quoted catalog's name
                connection.setCatalog(catalogName);
            }
            // ~
            return true;
        } catch (RuntimeException e) {
            return traceError("Problem when changing trying to set catalog \"" + catalogName
                    + "\" on connection. RuntimeException message: " + e.getMessage());
        } catch (SQLException e) {
            return traceError("Problem when changing trying to set catalog \"" + catalogName
                    + "\" on connection. SQLException message: " + e.getMessage());
        }
    }

    /**
     * DOC xqliu Comment method "canParallel".
     * 
     * @return
     */
    private boolean canParallel() {
        try {
            TypedReturnCode<Connection> typedReturnCode = this.getConnection(cachedAnalysis);
            Connection connection = typedReturnCode.getObject();
            DatabaseMetaData connectionMetadata = ConnectionUtils.getConnectionMetadata(connection);
            if (connectionMetadata.getDriverName() != null
                    && connectionMetadata.getDriverName().toLowerCase().startsWith(DatabaseConstant.ODBC_DRIVER_NAME)) {
                return false;
            }
        } catch (SQLException e) {
            log.warn(e, e);
        }
        return this.parallel;
    }
}
