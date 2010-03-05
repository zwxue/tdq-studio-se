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
package org.talend.dataquality.helpers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.talend.cwm.helper.TaggedValueHelper;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.analysis.AnalysisFactory;
import org.talend.dataquality.analysis.AnalysisParameters;
import org.talend.dataquality.analysis.AnalysisResult;
import org.talend.dataquality.analysis.AnalysisType;
import org.talend.dataquality.analysis.ExecutionLanguage;
import org.talend.dataquality.domain.Domain;
import org.talend.dataquality.domain.RangeRestriction;
import org.talend.dataquality.domain.pattern.Pattern;
import org.talend.dataquality.expressions.BooleanExpressionNode;
import org.talend.dataquality.indicators.CountsIndicator;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.IndicatorsPackage;
import org.talend.dataquality.indicators.PatternMatchingIndicator;
import org.talend.dataquality.indicators.columnset.ColumnsetPackage;
import org.talend.dataquality.indicators.definition.IndicatorDefinition;
import org.talend.dataquality.indicators.sql.UserDefIndicator;
import orgomg.cwm.objectmodel.core.Expression;
import orgomg.cwm.objectmodel.core.TaggedValue;

/**
 * @author scorreia
 * 
 * Helper class.
 */
public final class AnalysisHelper {

    public static final int DATA_FILTER_A = 0;

    public static final int DATA_FILTER_B = 1;

    private AnalysisHelper() {
    }

    /**
     * Method "createAnalysis".
     * 
     * @param name the name of the analysis
     * @return the new analysis with the given name
     */
    public static Analysis createAnalysis(String name) {
        Analysis analysis = AnalysisFactory.eINSTANCE.createAnalysis();
        analysis.setName(name);
        return analysis;
    }

    /**
     * Method "getAnalysisType".
     * 
     * @param analysis
     * @return the analysis type or null if not set
     */
    public static AnalysisType getAnalysisType(Analysis analysis) {
        AnalysisParameters parameters = analysis.getParameters();
        if (parameters == null) {
            return null;
        }
        // else
        return parameters.getAnalysisType();
    }

    /**
     * Method "getExecutionEngine".
     * 
     * @param analysis the analysis
     * @return the execution language (SQL by default)
     */
    public static ExecutionLanguage getExecutionEngine(Analysis analysis) {
        AnalysisParameters parameters = analysis.getParameters();
        if (parameters == null) {
            return ExecutionLanguage.SQL;
        }
        // else
        return parameters.getExecutionLanguage();
    }

    /**
     * Method "setAnalysisType".
     * 
     * @param analysis an analysis
     * @param analysisType the type to set to the analysis
     */
    public static void setAnalysisType(Analysis analysis, AnalysisType analysisType) {
        AnalysisParameters parameters = analysis.getParameters();
        if (parameters == null) {
            parameters = AnalysisFactory.eINSTANCE.createAnalysisParameters();
            analysis.setParameters(parameters);
        }
        parameters.setAnalysisType(analysisType);
    }

    /**
     * Method "getDataFilter".
     * 
     * @param analysis
     * @return the list of domains or null
     */
    public static EList<Domain> getDataFilter(Analysis analysis) {
        AnalysisParameters parameters = analysis.getParameters();
        if (parameters == null) {
            return null;
        }
        return parameters.getDataFilter();
    }

    /**
     * Method "getStringDataFilter".
     * 
     * @param analysis
     * @return the data filter as a string or null if none.
     */
    public static String getStringDataFilter(Analysis analysis) {
        return getStringDataFilter(analysis, 0);
    }

    /**
     * DOC xqliu Comment method "getStringDataFilter".
     * 
     * @param analysis
     * @param index 0 for DataFilterA, 1 for DataFilterB
     * @return
     */
    public static String getStringDataFilter(Analysis analysis, int index) {
        AnalysisParameters parameters = analysis.getParameters();
        if (parameters == null) {
            return null;
        }
        EList<Domain> dataFilters = parameters.getDataFilter();
        // remove existing filters
        if (dataFilters.isEmpty()) {
            return null;
        }

        int i = 0;
        for (Domain domain : dataFilters) {
            if (domain == null) {
                continue;
            }
            EList<RangeRestriction> ranges = domain.getRanges();
            for (RangeRestriction rangeRestriction : ranges) {
                BooleanExpressionNode expressions = rangeRestriction.getExpressions();
                if (expressions == null) {
                    continue;
                }
                Expression expression = expressions.getExpression();
                if (expression == null) {
                    continue;
                }
                if (i == index) {
                    return expression.getBody();
                } else if (i > index) {
                    return null;
                }
            }
            i++;
        }
        return null;
    }

    public static boolean setStringDataFilter(Analysis analysis, String datafilterString) {
        EList<Domain> dataFilters = analysis.getParameters().getDataFilter();
        // update existing filters
        if (!dataFilters.isEmpty()) {
            Domain domain = dataFilters.get(0);
            EList<RangeRestriction> ranges = domain.getRanges();
            RangeRestriction rangeRestriction = (ranges.isEmpty()) ? DomainHelper.addRangeRestriction(domain) : ranges.get(0);
            BooleanExpressionNode expressions = rangeRestriction.getExpressions();
            if (expressions == null) {
                expressions = BooleanExpressionHelper.createBooleanExpressionNode(datafilterString);
                rangeRestriction.setExpressions(expressions);
            } else {
                Expression expression = expressions.getExpression();
                if (expression == null) {
                    expression = BooleanExpressionHelper.createExpression(BooleanExpressionHelper.DEFAULT_LANGUAGE,
                            datafilterString);
                    expressions.setExpression(expression);
                } else {
                    expression.setBody(datafilterString);
                }
            }
            return false;
        }
        // else
        return dataFilters.add(createDomain(analysis, datafilterString));
    }

    /**
     * DOC xqliu Comment method "setStringDataFilter".
     * 
     * @param analysis
     * @param datafilterString
     * @param index 0 for DataFilterA, 1 for DataFilterB
     * @return
     */
    public static boolean setStringDataFilter(Analysis analysis, String datafilterString, int index) {
        if (index == 1) {
            EList<Domain> dataFilters = analysis.getParameters().getDataFilter();
            int size = dataFilters.size();
            if (size == 0) {
                dataFilters.add(createDomain(analysis, ""));
                return dataFilters.add(createDomain(analysis, datafilterString, "1"));
            } else if (size == 1) {
                return dataFilters.add(createDomain(analysis, datafilterString, "1"));
            } else if (size == 2) {
                Domain domain = dataFilters.get(1);
                EList<RangeRestriction> ranges = domain.getRanges();
                RangeRestriction rangeRestriction = (ranges.isEmpty()) ? DomainHelper.addRangeRestriction(domain) : ranges.get(0);
                BooleanExpressionNode expressions = rangeRestriction.getExpressions();
                if (expressions == null) {
                    expressions = BooleanExpressionHelper.createBooleanExpressionNode(datafilterString);
                    rangeRestriction.setExpressions(expressions);
                } else {
                    Expression expression = expressions.getExpression();
                    if (expression == null) {
                        expression = BooleanExpressionHelper.createExpression(BooleanExpressionHelper.DEFAULT_LANGUAGE,
                                datafilterString);
                        expressions.setExpression(expression);
                    } else {
                        expression.setBody(datafilterString);
                    }
                }
                return false;
            } else {
                return false;
            }
        } else {
            return setStringDataFilter(analysis, datafilterString);
        }

    }

    /**
     * Method "containsRowCount".
     * 
     * @param analysis
     * @return true if this analysis contains the row count indicator
     */
    public static boolean containsRowCount(Analysis analysis) {
        final AnalysisResult results = analysis.getResults();
        if (results == null) {
            return false;
        }
        final EList<Indicator> indicators = results.getIndicators();
        for (Indicator indicator : indicators) {
            if (IndicatorsPackage.eINSTANCE.getRowCountIndicator().equals(indicator.eClass())) {
                return true;
            }
            if (IndicatorsPackage.eINSTANCE.getCountsIndicator().equals(indicator.eClass())) {
                CountsIndicator cInd = (CountsIndicator) indicator;
                if (cInd.getRowCountIndicator() != null) {
                    return true;
                }
            }
            if (ColumnsetPackage.eINSTANCE.getSimpleStatIndicator().equals(indicator.eClass())) {
                return true;
            }
        }
        return false;
    }

    private static Domain createDomain(Analysis analysis, String datafilterString) {
        // by default use same name as the analysis. This is ok as long as there is only one datafilter.
        Domain domain = DomainHelper.createDomain(analysis.getName());
        RangeRestriction rangeRestriction = DomainHelper.addRangeRestriction(domain);
        BooleanExpressionNode expressionNode = BooleanExpressionHelper.createBooleanExpressionNode(datafilterString);
        rangeRestriction.setExpressions(expressionNode);
        return domain;
    }

    private static Domain createDomain(Analysis analysis, String datafilterString, String alias) {
        // by default use same name as the analysis. This is ok as long as there is only one datafilter.
        Domain domain = DomainHelper.createDomain(analysis.getName() + alias);
        RangeRestriction rangeRestriction = DomainHelper.addRangeRestriction(domain);
        BooleanExpressionNode expressionNode = BooleanExpressionHelper.createBooleanExpressionNode(datafilterString);
        rangeRestriction.setExpressions(expressionNode);
        return domain;
    }

    public static List<IndicatorDefinition> getUserDefinedIndicators(Analysis analysis) {
        List<IndicatorDefinition> rets = new ArrayList<IndicatorDefinition>();
        EList<Indicator> indicators = analysis.getResults().getIndicators();
        for (Indicator indicator : indicators) {
            if (indicator instanceof UserDefIndicator) {
                rets.add(indicator.getIndicatorDefinition());
            }
        }
        return rets;
    }

    public static List<Pattern> getPatterns(Analysis analysis) {
        List<Pattern> rets = new ArrayList<Pattern>();
        EList<Indicator> indicators = analysis.getResults().getIndicators();
        for (Indicator indicator : indicators) {
            if (indicator instanceof PatternMatchingIndicator) {
                rets.addAll(((PatternMatchingIndicator) indicator).getParameters().getDataValidDomain().getPatterns());
            }
            // MOD scorreia 2009-10-07 User defined matching indicator is NOT a pattern matching indicator.
            // else if (indicator instanceof UserDefIndicator) {
            // if (IndicatorCategoryHelper.isUserDefMatching(getIndicatorCategory(indicator.getIndicatorDefinition())))
            // {
            // rets.addAll(((UserDefIndicator) indicator).getParameters().getDataValidDomain().getPatterns());
            // }
            // }
        }
        return rets;
    }

    /**
     * DOC xqliu Comment method "getReloadDatabases".
     * 
     * @param analysis
     * @return
     */
    public static boolean getReloadDatabases(Analysis analysis) {
        TaggedValue taggedValue = TaggedValueHelper.getTaggedValue(TaggedValueHelper.RELOAD_DATABASES, analysis.getTaggedValue());
        if (taggedValue != null) {
            try {
                return Boolean.valueOf(taggedValue.getValue());
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    /**
     * DOC xqliu Comment method "setReloadDatabases".
     * 
     * @param analysis
     * @param reloadDatabases
     * @return
     */
    public static boolean setReloadDatabases(Analysis analysis, boolean reloadDatabases) {
        return TaggedValueHelper.setTaggedValue(analysis, TaggedValueHelper.RELOAD_DATABASES, String.valueOf(reloadDatabases));
    }

    // MOD scorreia 2009-10-07 User defined matching indicator is NOT a pattern matching indicator.
    // private static IndicatorCategory getIndicatorCategory(IndicatorDefinition indicatorDefinition) {
    // IndicatorCategory category = null;
    // if (indicatorDefinition != null) {
    // EList<IndicatorCategory> categories = indicatorDefinition.getCategories();
    // if (categories != null && categories.size() > 0) {
    // category = categories.get(0);
    // }
    // }
    // return category;
    // }
}
