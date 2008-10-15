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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.talend.cwm.db.connection.ConnectionUtils;
import org.talend.cwm.exception.AnalysisExecutionException;
import org.talend.cwm.helper.ColumnHelper;
import org.talend.cwm.helper.ResourceHelper;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.cwm.relational.TdColumn;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.analysis.AnalysisResult;
import org.talend.dataquality.domain.Domain;
import org.talend.dataquality.domain.RangeRestriction;
import org.talend.dataquality.domain.pattern.Pattern;
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
import org.talend.dataquality.indicators.definition.IndicatorDefinition;
import org.talend.dq.dbms.DbmsLanguage;
import org.talend.dq.dbms.DbmsLanguageFactory;
import org.talend.utils.collections.MultiMapHelper;
import org.talend.utils.sql.Java2SqlType;
import org.talend.utils.sugars.TypedReturnCode;
import orgomg.cwm.foundation.softwaredeployment.DataManager;
import orgomg.cwm.objectmodel.core.CoreFactory;
import orgomg.cwm.objectmodel.core.Expression;
import orgomg.cwm.objectmodel.core.ModelElement;
import orgomg.cwm.objectmodel.core.Package;

import Zql.ParseException;

/**
 * DOC scorreia class global comment. Detailled comment
 */
public class ColumnAnalysisSqlExecutor extends ColumnAnalysisExecutor {

    /**
     * TODO scorreia this constant must be replaced by a default preference and the possibility to the user to change it
     * for each indicator.
     */
    private static final int TOP_N = 10;

    private static Logger log = Logger.getLogger(ColumnAnalysisSqlExecutor.class);

    private DbmsLanguage dbmsLanguage;

    private Analysis cachedAnalysis;

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
            ColumnAnalysisHandler handler = new ColumnAnalysisHandler();
            handler.setAnalysis(analysis);
            String stringDataFilter = handler.getStringDataFilter();

            // --- get all the leaf indicators used for the sql computation
            Collection<Indicator> leafIndicators = IndicatorHelper.getIndicatorLeaves(results);
            // --- create one sql statement for each leaf indicator
            for (Indicator indicator : leafIndicators) {
                if (!createSqlQuery(stringDataFilter, indicator)) {
                    log.error("Error when creating query with indicator " + indicator.getName());
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

        return "";
    }

    /**
     * DOC scorreia Comment method "createSqlQuery".
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
        // --- get the schema owner
        String colName = quote(tdColumn.getName());
        if (!belongToSameSchemata(tdColumn)) {
            StringBuffer buf = new StringBuffer();
            for (orgomg.cwm.objectmodel.core.Package schema : schemata.values()) {
                buf.append(schema.getName() + " ");
            }
            log.error("Column " + colName + " does not belong to an existing schema [" + buf.toString().trim() + "]");
            return false;
        }
        colName = castColumn(indicator, tdColumn, colName);

        // get correct language for current database
        String language = dbms().getDbmsName();
        Expression sqlGenericExpression = null; // SqlIndicatorHandler.getSqlCwmExpression(indicator, language);

        // --- create select statement
        // get indicator's sql columnS (generate the real SQL statement from its definition)

        IndicatorDefinition indicatorDefinition = indicator.getIndicatorDefinition();
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

            return traceError("No SQL expression found for indicator "
                    + (indicator.getName() != null ? indicator.getName() : indicatorEclass.getName()) + " (Pattern id: "
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
                rangeStrings = getBinsAsGenericString(bins.getRanges());
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
                if (!textParameter.isUseBlank()) {
                    whereExpression.add(dbms().isNotBlank(colName));
                }
            }
        }

        String table = quote(ColumnHelper.getColumnSetFullName(tdColumn));

        // --- normalize table name
        String catalogName = getQuotedCatalogName(tdColumn);
        table = dbms().toQualifiedName(catalogName, null, table);

        // ### evaluate SQL Statement depending on indicators ###
        String completedSqlString = null;

        // ZExp dataFilterExpression = null;
        // --- handle case when indicator is a quantile
        if (indicatorEclass.equals(IndicatorsPackage.eINSTANCE.getMedianIndicator())
                || indicatorEclass.equals(IndicatorsPackage.eINSTANCE.getLowerQuartileIndicator())
                || indicatorEclass.equals(IndicatorsPackage.eINSTANCE.getUpperQuartileIndicator())) {
            // TODO scorreia test type of column and cast when needed
            completedSqlString = getCompletedStringForQuantiles(indicator, sqlGenericExpression, colName, table, whereExpression);
            completedSqlString = addWhereToSqlStringStatement(whereExpression, completedSqlString);
        } else

        // --- handle case when frequency indicator
        if (indicatorEclass.equals(IndicatorsPackage.eINSTANCE.getFrequencyIndicator())
                || indicatorEclass.isSuperTypeOf(IndicatorsPackage.eINSTANCE.getFrequencyIndicator())
                || indicatorEclass.equals(IndicatorsPackage.eINSTANCE.getModeIndicator())) {
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
                    && Java2SqlType.isDateInSQL(tdColumn.getJavaType())
                    ) {
                // frequencies with date aggregation
                completedSqlString = getDateAggregatedCompletedString(sqlGenericExpression, colName, table, dateAggregationType);
                completedSqlString = addWhereToSqlStringStatement(whereExpression, completedSqlString);
                completedSqlString = dbms().getTopNQuery(completedSqlString, topN);
            } else { // usual nominal frequencies
                // wrap column name into a function for pattern finder indicator
                if (indicatorEclass.equals(IndicatorsPackage.eINSTANCE.getPatternFreqIndicator())
                        || indicatorEclass.equals(IndicatorsPackage.eINSTANCE.getPatternLowFreqIndicator())) {
                    // TODO scorreia get user defined functions for pattern finder
                    colName = dbms().getPatternFinderDefaultFunction(colName);
                }
                
                completedSqlString = replaceVariablesLow(sqlGenericExpression.getBody(), colName, table, colName);
                completedSqlString = addWhereToSqlStringStatement(whereExpression, completedSqlString);
                completedSqlString = dbms().getTopNQuery(completedSqlString, topN);
            }
        } else

        // --- handle case of unique count
        if (indicatorEclass.equals(IndicatorsPackage.eINSTANCE.getUniqueCountIndicator())) {
            completedSqlString = replaceVariables(sqlGenericExpression.getBody(), colName, table);
            completedSqlString = addWhereToSqlStringStatement(whereExpression, completedSqlString);
            completedSqlString = dbms().countRowInSubquery(completedSqlString, "myquery");
        } else

        // --- handle case of duplicate count
        if (indicatorEclass.equals(IndicatorsPackage.eINSTANCE.getDuplicateCountIndicator())) {
            completedSqlString = replaceVariables(sqlGenericExpression.getBody(), colName, table);
            completedSqlString = addWhereToSqlStringStatement(whereExpression, completedSqlString);
            // duplicate is the number of rows having more than one identical instance
            completedSqlString = dbms().countRowInSubquery(completedSqlString, "myquery");
            // duplicate (with sumRowInSubquery) means that the number of instances that are duplicated
            // completedSqlString = dbms().sumRowInSubquery("mycount", completedSqlString, "myquery");
            // scorreia hard coded "mycount" string must be the same as the alias in the TalendDefinition file!
            // PTODO scorreia avoid hard coded "mycount" string
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

            // --- default case
            completedSqlString = replaceVariables(sqlGenericExpression.getBody(), colName, table);
            completedSqlString = addWhereToSqlStringStatement(whereExpression, completedSqlString);
        }

        if (log.isDebugEnabled()) {
            log.debug("Completed SQL expression for language " + language + ": " + completedSqlString);
        }

        // completedSqlString is the final query
        String finalQuery = completedSqlString;

        Expression instantiateSqlExpression = instantiateSqlExpression(language, finalQuery);
        indicator.setInstantiatedExpression(instantiateSqlExpression);
        return true;
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
        Object[] arguments = new Object[patterns.size() + 2];
        arguments[0] = colName;
        arguments[1] = table;
        int i = 2;
        for (String string : patterns) {
            arguments[i++] = string;
        }

        String toFormat = surroundSingleQuotes(sqlGenericString);
        // No problem if pattern contains something like {1} because it is in the arguments
        return MessageFormat.format(toFormat, arguments);
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
            String regexp = this.dbmsLanguage.getRegexp(pattern);
            if (regexp != null) {
                patternStrings.add(regexp);
            }
        }
        return patternStrings;
    }

    /**
     * DOC scorreia Comment method "castColumn".
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
                return "CAST (" + colName + " AS DECIMAL)";
            }
        }
        return colName;
    }

    private static final String COMMA = " , ";

    /**
     * DOC scorreia Comment method "getDateAggregatedCompletedString".
     * 
     * @param indicator
     * @param sqlExpression
     * @param colName
     * @param table
     * @param dataFilterExpression
     * @param dateAggregationType
     * @return
     */
    @SuppressWarnings("fallthrough")
    private String getDateAggregatedCompletedString(Expression sqlExpression, String colName, String table,
            DateGrain dateAggregationType) {
        int nbExtractedColumns = 0;
        String result = "";
        String aliases = ""; // used in group by clause in MySQL
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
            aliases = colName; // bug 5336 fixed aliases must not be empty otherwise, the group by clause is empty.
            break;
        default:
            break;
        }
        String groupByAliases = dbms().supportAliasesInGroupBy() ? aliases : result;
        String sql = replaceVariablesLow(sqlExpression.getBody(), result, table, groupByAliases);
        return sql;
    }

    private String getAlias(String colName, DateGrain dateAggregationType) {
        if (dbms().supportAliasesInGroupBy()) {
            return " TDAL_" + unquote(colName) + dateAggregationType.getName() + " ";
        } else {
            return "";
        }
    }

    /**
     * DOC scorreia Comment method "unquote".
     * 
     * @param colName
     * @return
     */
    private String unquote(String colName) {
        String dbQuoteString = dbms().getDbQuoteString();
        return colName.replace(dbQuoteString, "");
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
        // return dbms().addWhereToSqlStringStatement(completedSqlString, whereExpression);
        TypedReturnCode<String> trc = dbms().prepareQuery(completedSqlString);
        String query = trc.getObject();

        String where = dbms().buildWhereExpression(whereExpressions);
        if (where != null && where.trim().length() != 0) {
            query = dbms().addWhereToStatement(query, where);
        }
        query = dbms().finalizeQuery(query);
        return query;
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
        int idxOfOrderBY = sqlGenericExpression.indexOf(" ORDER BY");
        String orderBy = (idxOfOrderBY != -1) ? sqlGenericExpression.substring(idxOfOrderBY) : "";
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
        if (idxOfOrderBY != -1) {
            buf.append(orderBy);
        }

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
        String completedRange = replaceVariables(range, colName, table);
        String rangeColumn = "'" + completedRange + "'";

        String singleQuery = removeGroupBy(sqlGenericExpression);
        String completedSqlString = replaceVariablesLow(singleQuery, rangeColumn, table);

        List<String> allWheresForSingleSelect = new ArrayList<String>(whereExpression);

        // add this range clause to the given where clause (but do not modify the given where clause)
        // allWheresForSingleSelect.add(completedRange);

        completedSqlString = addWhereToSqlStringStatement(allWheresForSingleSelect, completedSqlString);
        // replacement in order to get lines even when no data is available
        // do this replacement after having added the where clause otherwise the parsing with ZQL will fail.
        completedSqlString = replaceCountByZeroCount(completedSqlString, completedRange);
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
        return completedSqlString.replace("COUNT(*)", "COUNT( CASE WHEN " + completedRange + " THEN 1 END )");
    }

    /**
     * DOC scorreia Comment method "getBinsAsString".
     * 
     * @param ranges
     * @return
     */
    private List<String> getBinsAsGenericString(EList<RangeRestriction> ranges) {
        List<String> bins = new ArrayList<String>();
        for (RangeRestriction rangeRestriction : ranges) {
            String bin = "{0} >= " + DomainHelper.getMinValue(rangeRestriction) + " AND {0} < "
                    + DomainHelper.getMaxValue(rangeRestriction);
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
            throw new AnalysisExecutionException("Got an invalid result set when evaluating row count for column "
                    + dbms().toQualifiedName(catalogOrSchema, null, colName));
        }

        Long midleCount = getLimitFirstArg(indicator, count);
        Integer nbRow = getNbReturnedRows(indicator, count);
        return MessageFormat.format(sqlExpression.getBody(), colName, table, String.valueOf(midleCount), String.valueOf(nbRow));
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
        }
        return 1;
    }

    /**
     * DOC scorreia Comment method "getLimitFirstArg".
     * 
     * @param indicator
     * @param count
     * @return
     */
    private Long getLimitFirstArg(Indicator indicator, long count) {
        if (indicator.eClass().equals(IndicatorsPackage.eINSTANCE.getMedianIndicator())) {
            boolean isEven = count % 2 == 0;
            return isEven ? count / 2 - 1 : (count - 1) / 2;
        } else if (indicator.eClass().equals(IndicatorsPackage.eINSTANCE.getLowerQuartileIndicator())) {
            return count / 4 - 1;
        } else if (indicator.eClass().equals(IndicatorsPackage.eINSTANCE.getUpperQuartileIndicator())) {
            return (3 * count) / 4 - 1;
        }
        return null;
    }

    private Long getCount(Analysis analysis, String colName, String table, String catalog, List<String> whereExpression)
            throws AnalysisExecutionException {
        try {
            return getCountLow(analysis, colName, table, catalog, whereExpression);
        } catch (SQLException e) {
            throw new AnalysisExecutionException("Cannot get count for analysis \"" + analysis.getName() + "\" on column "
                    + colName + " in " + dbms().toQualifiedName(catalog, null, table), e);
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
            throw new AnalysisExecutionException("Cannot execute Analysis " + analysis.getName() + ". Error: " + trc.getMessage());
        }
        Connection connection = trc.getObject();
        String whereExp = (whereExpression == null || whereExpression.isEmpty()) ? "" : " WHERE "
                + dbms().buildWhereExpression(whereExpression);
        String queryStmt = "SELECT COUNT(" + colName + ") FROM " + table + whereExp; // + dbms().eos();

        List<Object[]> myResultSet = executeQuery(catalogName, connection, queryStmt);

        if (myResultSet.isEmpty() || myResultSet.size() > 1) {
            log.error("Too many result obtained for a simple count: " + myResultSet);
            return -1L;
        }
        return Long.valueOf(String.valueOf(myResultSet.get(0)[0]));
    }

    private DbmsLanguage createDbmsLanguage() {
        DataManager connection = this.cachedAnalysis.getContext().getConnection();
        return DbmsLanguageFactory.createDbmsLanguage(connection);
    }

    /**
     * Method "dbms".
     * 
     * @return the DBMS language (not null)
     */
    protected DbmsLanguage dbms() {
        if (this.dbmsLanguage == null) {
            this.dbmsLanguage = createDbmsLanguage();
            // this.dbmsLanguage.setDbQuoteString(this.getDbQuoteString(cachedAnalysis));
        }
        return this.dbmsLanguage;
    }

    // private static final String DEFAULT_SQL = "SQL";

    private Expression instantiateSqlExpression(String language, String body) {
        Expression expression = CoreFactory.eINSTANCE.createExpression();
        expression.setLanguage(language);
        expression.setBody(body);
        return expression;
    }

    /**
     * Method "replaceVariables".
     * 
     * @param sqlGenericString a string with 2 parameters {0} and {1}
     * @param column the string that replaces the {0} parameter
     * @param table the string that replaces the {1} parameter
     * @return the string with the given parameters
     */
    private String replaceVariables(String sqlGenericString, String column, String table) {
        Object[] arguments = { column, table };
        return replaceVariablesLow(sqlGenericString, arguments);
    }

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
        return sqlGenericString.replaceAll("'", "''");
    }

    /**
     * DOC scorreia Comment method "getUpperCase".
     * 
     * @param language
     * @param colName
     * @return
     */

    /**
     * Method "quote".
     * 
     * @param input
     * @return the given string between quotes
     */
    private String quote(String input) {
        return dbms().quote(input);
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
            for (Indicator indicator : indicators) {
                // skip composite indicators that do not require a sql execution
                if (indicator instanceof CompositeIndicator) {
                    // TODO scorreia we will have to handle possible options of composite indicators elsewhere?
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
                }

                // add mapping of analyzed elements to their indicators
                MultiMapHelper.addUniqueObjectToListMap(indicator.getAnalyzedElement(), indicator, elementToIndicator);

            }

            connection.close();

            // --- finalize indicators by setting the row count and null when they exist.
            Set<ModelElement> analyzedElements = elementToIndicator.keySet();
            for (ModelElement modelElement : analyzedElements) {
                // get row count indicator
                RowCountIndicator rowCount = getRowCountIndicator(modelElement, elementToIndicator);
                // get null count indicator
                NullCountIndicator nullCount = getNullCountIndicator(modelElement, elementToIndicator);

                List<Indicator> list = elementToIndicator.get(modelElement);
                for (Indicator ind : list) {
                    // set row count value to each indicator
                    if (rowCount != null) {
                        ind.setCount(rowCount.getCount());
                    }
                    // set null count value to each indicator
                    if (nullCount != null) {
                        ind.setNullCount(nullCount.getNullCount());
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
    private String getCatalogOrSchemaName(ModelElement analyzedElement) {
        Package schema = super.schemata.get(analyzedElement);
        if (schema == null) {
            log.error("No schema found for column " + analyzedElement.getName());
            return null;
        }
        // else
        return schema.getName();
    }

    /**
     * Method "getCatalogName".
     * 
     * @param analyzedElement
     * @return the catalog or schema quoted name
     */
    private String getQuotedCatalogName(ModelElement analyzedElement) {
        return quote(this.getCatalogOrSchemaName(analyzedElement));
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
    private boolean executeQuery(Indicator indicator, Connection connection, String queryStmt) throws SQLException {
        String cat = getCatalogOrSchemaName(indicator.getAnalyzedElement());
        List<Object[]> myResultSet = executeQuery(cat, connection, queryStmt);

        // give result to indicator so that it handles the results
        return indicator.storeSqlResults(myResultSet);
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
        // Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY,
        // ResultSet.CLOSE_CURSORS_AT_COMMIT);
        Statement statement = connection.createStatement();
        // statement.setFetchSize(fetchSize);
        if (log.isInfoEnabled()) {
            log.info("Executing query: " + queryStmt);
        }
        statement.execute(queryStmt);

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
            // MOD scorreia 2008-08-01 MSSQL does not support quoted catalog's name
            connection.setCatalog(catalogName);
            return true;
        } catch (RuntimeException e) {
            return traceError("Problem when changing trying to set catalog \"" + catalogName
                    + "\" on connection. RuntimeException message: " + e.getMessage());
        } catch (SQLException e) {
            return traceError("Problem when changing trying to set catalog \"" + catalogName
                    + "\" on connection. SQLException message: " + e.getMessage());
        }
    }
}
