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
package org.talend.dataquality.helpers;

import org.eclipse.emf.common.util.EList;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.analysis.AnalysisFactory;
import org.talend.dataquality.analysis.AnalysisParameters;
import org.talend.dataquality.analysis.AnalysisType;
import org.talend.dataquality.domain.Domain;
import org.talend.dataquality.domain.RangeRestriction;
import org.talend.dataquality.expressions.BooleanExpressionNode;
import orgomg.cwm.objectmodel.core.Expression;

/**
 * @author scorreia
 * 
 * Helper class.
 */
public class AnalysisHelper {

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
        AnalysisParameters parameters = analysis.getParameters();
        if (parameters == null) {
            return null;
        }
        EList<Domain> dataFilters = parameters.getDataFilter();
        // remove existing filters
        if (dataFilters.isEmpty()) {
            return null;
        }

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
                return expression.getBody();
            }
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
                    expression = BooleanExpressionHelper.createExpression("SQL", datafilterString);
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

    private static Domain createDomain(Analysis analysis, String datafilterString) {
        // by default use same name as the analysis. This is ok as long as there is only one datafilter.
        Domain domain = DomainHelper.createDomain(analysis.getName());
        RangeRestriction rangeRestriction = DomainHelper.addRangeRestriction(domain);
        BooleanExpressionNode expressionNode = BooleanExpressionHelper.createBooleanExpressionNode(datafilterString);
        rangeRestriction.setExpressions(expressionNode);
        return domain;
    }
}
