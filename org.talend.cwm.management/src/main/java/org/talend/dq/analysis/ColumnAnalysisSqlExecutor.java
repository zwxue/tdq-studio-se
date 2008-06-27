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
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.talend.cwm.exception.AnalysisExecutionException;
import org.talend.cwm.helper.ColumnHelper;
import org.talend.cwm.helper.ResourceHelper;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.cwm.management.api.DbmsLanguage;
import org.talend.cwm.management.api.DbmsLanguageFactory;
import org.talend.cwm.relational.TdColumn;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.analysis.AnalysisResult;
import org.talend.dataquality.domain.Domain;
import org.talend.dataquality.domain.RangeRestriction;
import org.talend.dataquality.domain.pattern.Pattern;
import org.talend.dataquality.domain.pattern.PatternComponent;
import org.talend.dataquality.helpers.DomainHelper;
import org.talend.dataquality.helpers.IndicatorHelper;
import org.talend.dataquality.indicators.CompositeIndicator;
import org.talend.dataquality.indicators.DataminingType;
import org.talend.dataquality.indicators.DateGrain;
import org.talend.dataquality.indicators.DateParameters;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.IndicatorParameters;
import org.talend.dataquality.indicators.IndicatorsPackage;
import org.talend.dataquality.indicators.TextParameters;
import org.talend.dataquality.indicators.definition.IndicatorDefinition;
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

    private static final String DEFAULT_QUOTE_STRING = "";

    private DbmsLanguage dbmsLanguage;

    private String dbQuote;

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
            return traceError("No indicator definition found for indicator " + indicator.getName());
        }
        sqlGenericExpression = getSqlExpression(indicatorDefinition, language);
        if (sqlGenericExpression == null) {
            // try with default language (ANSI SQL)
            log.warn("The indicator SQL expression has not been found for the database type " + language + " for the indicator"
                    + indicatorDefinition.getName());
            if (log.isInfoEnabled()) {
                log.info("Trying to compute the indicator with the default language " + dbms().getDefaultLanguage());
            }
            sqlGenericExpression = getSqlExpression(indicatorDefinition, dbms().getDefaultLanguage());
        }

        if (sqlGenericExpression == null || sqlGenericExpression.getBody() == null) {
            return traceError("No SQL expression found for indicator "
                    + (indicator.getName() != null ? indicator.getName() : indicator.eClass().getName()) + ". Definition: "
                    + ResourceHelper.getUUID(indicatorDefinition));
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
                if (textParameter.isIgnoreCase()) {
                    colName = dbms().toUpperCase(colName);
                }
                if (!textParameter.isUseBlank()) {
                    whereExpression.add(dbms().isNotBlank(colName));
                }
                if (textParameter.isUseNulls()) {
                    colName = dbms().replaceNullsWithString(colName, "''");
                }
            }
        }

        String table = quote(ColumnHelper.getColumnSetFullName(tdColumn));

        // --- normalize table name
        String catalogName = getCatalogName(tdColumn);
        table = dbms().toQualifiedName(catalogName, null, table);

        // ### evaluate SQL Statement depending on indicators ###
        String completedSqlString = null;

        // ZExp dataFilterExpression = null;
        // --- handle case when indicator is a quantile
        if (indicator.eClass().equals(IndicatorsPackage.eINSTANCE.getMedianIndicator())
                || indicator.eClass().equals(IndicatorsPackage.eINSTANCE.getLowerQuartileIndicator())
                || indicator.eClass().equals(IndicatorsPackage.eINSTANCE.getUpperQuartileIndicator())) {
            // TODO scorreia test type of column and cast when needed
            completedSqlString = getCompletedStringForQuantiles(indicator, sqlGenericExpression, colName, table, whereExpression);
            completedSqlString = addWhereToSqlStringStatement(whereExpression, completedSqlString);
        } else

        // --- handle case when frequency indicator
        if (indicator.eClass().equals(IndicatorsPackage.eINSTANCE.getFrequencyIndicator())
                || indicator.eClass().equals(IndicatorsPackage.eINSTANCE.getModeIndicator())) {
            // TODO scorreia test type of column and cast when needed
            // with ranges (frequencies of numerical intervals)

            int topN = indicator.eClass().equals(IndicatorsPackage.eINSTANCE.getModeIndicator()) ? 1 : getTopN(indicator);
            if (topN <= 0) {
                topN = TOP_N;
            }

            if (rangeStrings != null) {
                completedSqlString = getUnionCompletedString(indicator, sqlGenericExpression, colName, table, whereExpression,
                        rangeStrings);
                if (indicator.eClass().equals(IndicatorsPackage.eINSTANCE.getModeIndicator())) {
                    // now order by second column (get the order by clause of generic expression and replace)
                    String genericSQL = sqlGenericExpression.getBody();
                    int beginIndex = genericSQL.indexOf(dbms().orderBy());
                    if (beginIndex != -1) {
                        int lastIndex = genericSQL.lastIndexOf(dbms().desc());
                        String orderByClause = genericSQL.substring(beginIndex, lastIndex);
                        completedSqlString = completedSqlString + orderByClause + dbms().desc();
                    }
                    // and get the best row
                    completedSqlString = dbms().getTopNQuery(completedSqlString, topN);
                }
            } else if ((dateAggregationType != null)
            // MOD scorreia 2008-06-23 check column type (robustness against bug 4287)
                    && Java2SqlType.isDateInSQL(tdColumn.getJavaType())) {
                // frequencies with date aggregation
                completedSqlString = getDateAggregatedCompletedString(sqlGenericExpression, colName, table, dateAggregationType);
                completedSqlString = addWhereToSqlStringStatement(whereExpression, completedSqlString);
                completedSqlString = dbms().getTopNQuery(completedSqlString, topN);
            } else { // usual nominal frequencies
                completedSqlString = replaceVariables(sqlGenericExpression.getBody(), colName, table);
                completedSqlString = addWhereToSqlStringStatement(whereExpression, completedSqlString);
                completedSqlString = dbms().getTopNQuery(completedSqlString, topN);
            }
        } else

        // --- handle case of unique count
        if (indicator.eClass().equals(IndicatorsPackage.eINSTANCE.getUniqueCountIndicator())) {
            completedSqlString = replaceVariables(sqlGenericExpression.getBody(), colName, table);
            completedSqlString = addWhereToSqlStringStatement(whereExpression, completedSqlString);
            completedSqlString = dbms().countRowInSubquery(completedSqlString, "myquery");
        } else

        // --- handle case of duplicate count
        if (indicator.eClass().equals(IndicatorsPackage.eINSTANCE.getDuplicateCountIndicator())) {
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
        if (IndicatorsPackage.eINSTANCE.getPatternMatchingIndicator().isSuperTypeOf(indicator.eClass())) {
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
            EList<PatternComponent> components = pattern.getComponents();
            for (PatternComponent patternComponent : components) {
                Expression expression = dbms().getExpression(patternComponent);
                if (expression != null) {
                    String body = expression.getBody();
                    patternStrings.add(body);
                }
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
    private String getDateAggregatedCompletedString(Expression sqlExpression, String colName, String table,
            DateGrain dateAggregationType) {
        int nbExtractedColumns = 0;
        String result = "";
        switch (dateAggregationType) {
        case DAY:
            result = dbms().extractDay(colName) + comma(result);
            nbExtractedColumns++;
        case WEEK:
            result = dbms().extractWeek(colName) + comma(result);
            nbExtractedColumns++;
        case MONTH:
            result = dbms().extractMonth(colName) + comma(result);
            nbExtractedColumns++;
        case QUARTER:
            result = dbms().extractQuarter(colName) + comma(result);
            nbExtractedColumns++;
        case YEAR:
            result = dbms().extractYear(colName) + comma(result);
            nbExtractedColumns++;
            break;
        case NONE:
            result = colName;
            nbExtractedColumns++;
            break;
        default:
            break;
        }

        String sql = replaceVariables(sqlExpression.getBody(), result, table);
        return sql;
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
        for (int i = 0; i < last; i++) {
            String singleSelect = getCompletedSingleSelect(indicator, sqlGenericExpression, colName, table, whereExpression,
                    rangeStrings.get(i));
            buf.append('(');
            buf.append(singleSelect);
            buf.append(')');
            if (i != last - 1) {
                buf.append(dbms().unionAll());
            }
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
        String completedSqlString = replaceVariables(sqlGenericExpression, rangeColumn, table);
        // add this range clause to the given where clause (but do not modify the given where clause)
        List<String> allWheresForSingleSelect = new ArrayList<String>(whereExpression);
        allWheresForSingleSelect.add(completedRange);
        return addWhereToSqlStringStatement(allWheresForSingleSelect, completedSqlString);
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
     * DOC scorreia Comment method "getSqlExpression".
     * 
     * @param indicatorDefinition
     * @param language
     * @param sqlExpression
     * @return
     */
    private Expression getSqlExpression(IndicatorDefinition indicatorDefinition, String language) {
        EList<Expression> sqlGenericExpression = indicatorDefinition.getSqlGenericExpression();
        for (Expression sqlGenExpr : sqlGenericExpression) {
            if (StringUtils.equalsIgnoreCase(language, sqlGenExpr.getLanguage())) {
                return sqlGenExpr; // language found
            }
        }
        return null;
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
        String catalog = getCatalogName(indicator.getAnalyzedElement());
        // FIXME scorreia get schema
        long count = getCount(cachedAnalysis, colName, table, catalog, whereExpression);
        if (count == -1) {
            throw new AnalysisExecutionException("Got an invalid result set when evaluating row count for column "
                    + dbms().toQualifiedName(catalog, null, colName));
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
    private DbmsLanguage dbms() {
        if (this.dbmsLanguage == null) {
            this.dbmsLanguage = createDbmsLanguage();
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
     * Method "getDbQuoteString".
     * 
     * @param analysis
     * @return the database identifier quote string
     */
    private String getDbQuoteString(Analysis analysis) {
        if (dbQuote != null) {
            return dbQuote;
        }
        TypedReturnCode<Connection> trc = this.getConnection(analysis);
        if (!trc.isOk()) {
            traceError("Cannot execute Analysis " + analysis.getName() + ". Error: " + trc.getMessage());
            return DEFAULT_QUOTE_STRING;
        }
        try {
            dbQuote = DEFAULT_QUOTE_STRING;
            dbQuote = trc.getObject().getMetaData().getIdentifierQuoteString();
            trc.getObject().close();
            return dbQuote;
        } catch (SQLException e) {
            log.warn("Could not get identifier quote string from database for analysis " + analysis.getName());
            return DEFAULT_QUOTE_STRING;
        }
    }

    /**
     * Method "traceError".
     * 
     * @param error the message to set in errorMessage
     * @return always false
     */
    private boolean traceError(String error) {
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

        try {
            Connection connection = trc.getObject();

            // execute the sql statement for each indicator
            Collection<Indicator> indicators = IndicatorHelper.getIndicatorLeaves(analysis.getResults());
            for (Indicator indicator : indicators) {
                // skip composite indicators that do not require a sql execution
                if (indicator instanceof CompositeIndicator) {
                    // TODO scorreia we will have to handle possible options of composite indicators elsewhere?
                    continue;
                }
                // set the connection's catalog
                String catalogName = getCatalogName(indicator.getAnalyzedElement());
                if (catalogName != null) { // check whether null argument can be given
                    changeCatalog(catalogName, connection);
                }

                Expression query = indicator.getInstantiatedExpressions(dbms().getDbmsName());
                if (query == null) {
                    // try to get a default sql expression
                    query = indicator.getInstantiatedExpressions(dbms().getDefaultLanguage());
                }
                if (query == null || !executeQuery(indicator, connection, query.getBody())) {
                    ok = traceError("Query not executed for indicator: \"" + indicator.getName() + "\" "
                            + ((query == null) ? "query is null" : "SQL query: " + query.getBody()));
                }
            }
            // get the results

            // store the results in each indicator

            connection.close();
        } catch (SQLException e) {
            log.error(e, e);
            this.errorMessage = e.getMessage();
            ok = false;
        }
        return ok;
    }

    /**
     * Method "getCatalogName".
     * 
     * @param analyzedElement
     * @return the catalog or schema quoted name
     */
    private String getCatalogName(ModelElement analyzedElement) {
        Package schema = super.schemata.get(analyzedElement);
        if (schema == null) {
            log.error("No schema found for column " + analyzedElement.getName());
            return null;
        }
        // else
        return quote(schema.getName());
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
        String cat = getCatalogName(indicator.getAnalyzedElement());
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
    private List<Object[]> executeQuery(String catalogName, Connection connection, String queryStmt) throws SQLException {

        if (catalogName != null) { // check whether null argument can be given
            changeCatalog(catalogName, connection);
        }
        // create query statement
        // Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY,
        // ResultSet.CLOSE_CURSORS_AT_COMMIT);
        Statement statement = connection.createStatement();
        // statement.setFetchSize(fetchSize);
        if (log.isDebugEnabled()) {
            log.debug("Excuting query: " + queryStmt);
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
        resultSet.close();

        return myResultSet;
    }

    /**
     * DOC scorreia Comment method "changeCatalog".
     * 
     * @param catalogName
     * @param connection
     * @throws SQLException
     */
    private boolean changeCatalog(String catalogName, Connection connection) {
        try {
            connection.setCatalog(catalogName);
            return true;
        } catch (RuntimeException e) {
            traceError("Problem when changing catalog on connection " + e.getMessage());
            return false;
        } catch (SQLException e) {
            traceError("Problem when changing catalog on connection " + e.getMessage());
            return false;
        }
    }
}
