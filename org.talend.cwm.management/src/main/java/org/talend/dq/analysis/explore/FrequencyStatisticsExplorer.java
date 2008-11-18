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
package org.talend.dq.analysis.explore;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.emf.common.util.EList;
import org.talend.cwm.relational.TdColumn;
import org.talend.dataquality.domain.Domain;
import org.talend.dataquality.domain.RangeRestriction;
import org.talend.dataquality.helpers.DomainHelper;
import org.talend.dataquality.indicators.DateGrain;
import org.talend.dataquality.indicators.IndicatorParameters;
import org.talend.utils.sql.Java2SqlType;
import orgomg.cwm.objectmodel.core.Expression;

/**
 * DOC Administrator class global comment. Detailled comment
 */
public class FrequencyStatisticsExplorer extends DataExplorer {

    private static final String REGEX = "SELECT (.*)\\s*, COUNT\\(\\*\\)\\s*(AS|as)?\\s*\\w*\\s* FROM";

    @SuppressWarnings("fallthrough")
    protected String getFreqRowsStatement() {

        String clause = "";

        TdColumn column = (TdColumn) indicator.getAnalyzedElement();
        int javaType = column.getJavaType();

        if (Java2SqlType.isTextInSQL(javaType)) {
            clause = getInstantiatedClause();
        } else if (Java2SqlType.isDateInSQL(javaType)) {
            IndicatorParameters parameters = indicator.getParameters();
            DateGrain dateGrain = parameters.getDateParameters().getDateAggregationType();

            switch (dateGrain) {
            case DAY:
                clause = dbmsLanguage.extractDay(this.columnName) + dbmsLanguage.equal() + getDayCharacters(entity.getLabel());
                // no break
            case WEEK:
                if (clause.length() == 0) { // needs week to identify the row
                    clause = concatWhereClause(clause, dbmsLanguage.extractWeek(this.columnName) + dbmsLanguage.equal()
                            + getWeekCharacters(entity.getLabel()));
                }
                // no break
            case MONTH:
                clause = concatWhereClause(clause, dbmsLanguage.extractMonth(this.columnName) + dbmsLanguage.equal()
                        + getMonthCharacters(dateGrain, entity.getLabel()));
                // no break
            case QUARTER:
                if (clause.length() == 0) { // need quarter to identify the row
                    clause = concatWhereClause(clause, dbmsLanguage.extractQuarter(this.columnName) + dbmsLanguage.equal()
                            + getQuarterCharacters(entity.getLabel()));
                }
                // no break
            case YEAR:
                clause = concatWhereClause(clause, dbmsLanguage.extractYear(this.columnName) + dbmsLanguage.equal()
                        + getYearCharacters(entity.getLabel()));
                break;
            case NONE:
            default:
                clause = getDefaultQuotedStatement("'");
                break;
            }

        } else if (Java2SqlType.isNumbericInSQL(javaType)) {
            IndicatorParameters parameters = indicator.getParameters();
            if (parameters != null) {
                // handle bins
                Domain bins = parameters.getBins();
                if (bins != null) {
                    // rangeStrings = getBinsAsGenericString(bins.getRanges());
                    final EList<RangeRestriction> ranges = bins.getRanges();
                    for (RangeRestriction rangeRestriction : ranges) {
                        // find the rangeLabel
                        if (entity.getLabel() != null && entity.getLabel().equals(rangeRestriction.getName())) {
                            clause = createWhereClause(rangeRestriction);
                            break;
                        }
                    }
                }
            }
        } else {
            clause = getDefaultQuotedStatement(""); // no quote here
        }

        return "SELECT * FROM " + getFullyQualifiedTableName(column) + dbmsLanguage.where() + inBrackets(clause)
                + andDataFilterClause();
    }

 
    /**
     * DOC scorreia Comment method "createWhereClause".
     * 
     * @param rangeRestriction
     * @return
     */
    private String createWhereClause(RangeRestriction rangeRestriction) {
        double max = Double.valueOf(DomainHelper.getMaxValue(rangeRestriction));
        double min = Double.valueOf(DomainHelper.getMinValue(rangeRestriction));
        String whereClause = columnName + dbmsLanguage.greaterOrEqual() + min + dbmsLanguage.and() + columnName
                + dbmsLanguage.less() + max;
        return whereClause;
    }


    private String getDefaultQuotedStatement(String quote) {
        return entity.isLabelNull() ? dbmsLanguage.quote(this.columnName) + dbmsLanguage.isNull() : dbmsLanguage
                .quote(this.columnName)
                + dbmsLanguage.equal() + quote + entity.getLabel() + quote;
    }

    /**
     * DOC scorreia Comment method "getQuarterCharacters".
     * 
     * @param label
     * @return
     */
    private String getQuarterCharacters(String label) {
        return label.substring(label.length() - 1);
    }

    /**
     * DOC scorreia Comment method "getYearCharacters".
     * 
     * @param label
     * @return
     */
    private String getYearCharacters(String label) {
        return label.substring(0, 4);
    }

    /**
     * DOC scorreia Comment method "getMonthCharacters".
     * 
     * @param dateGrain
     * 
     * @param label
     * @return
     */
    private String getMonthCharacters(DateGrain dateGrain, String label) {
        switch (dateGrain) {
        case DAY:
        case WEEK:
            // week and day are the two last digits
            return label.substring(label.length() - 4, label.length() - 2);
        case MONTH:
            return label.substring(label.length() - 2);
        default:
            break;
        }
        return null;
    }

    /**
     * DOC scorreia Comment method "getWeekCharacters".
     * 
     * @param label
     * @return
     */
    private String getWeekCharacters(String label) {
        return label.substring(label.length() - 2);
    }

    /**
     * DOC scorreia Comment method "getDayCharacters".
     * 
     * @param label
     * @return
     */
    private String getDayCharacters(String label) {
        return label.substring(label.length() - 2);
    }

    /**
     * DOC scorreia Comment method "concatWhereClause".
     * 
     * @param clause
     * @return
     */
    private String concatWhereClause(String clause, String whereclause) {
        String and = (clause.length() == 0) ? "" : dbmsLanguage.and();
        clause = clause + and + whereclause;
        return clause;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.analysis.explore.IDataExplorer#getQueryMap()
     */
    public Map<String, String> getQueryMap() {
        Map<String, String> map = new HashMap<String, String>();

        map.put(MENU_VIEW_ROWS, getFreqRowsStatement());

        return map;
    }
    
    
    /**
     * Method "getInstantiatedClause".
     * 
     * @return the where clause from the instantiated query
     */
    protected String getInstantiatedClause() {
        // get function which convert data into a pattern
        String function = getFunction();

        String clause = entity.isLabelNull() || function == null ? columnName + dbmsLanguage.isNull() : function
                + dbmsLanguage.equal() + "'" + entity.getLabel() + "'";
        return clause;
    }
    
    private String getFunction() {
        Expression instantiatedExpression = dbmsLanguage.getInstantiatedExpression(indicator);
        final String body = instantiatedExpression.getBody();
        Pattern p = Pattern.compile(REGEX, Pattern.CASE_INSENSITIVE);
        Matcher matcher = p.matcher(body);
        matcher.find();
        String group = matcher.group(1);
        return group;
    }


}
