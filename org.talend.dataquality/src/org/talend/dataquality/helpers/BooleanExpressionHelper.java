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

import org.talend.dataquality.domain.pattern.PatternFactory;
import org.talend.dataquality.domain.pattern.RegularExpression;
import org.talend.dataquality.expressions.BooleanExpressionNode;
import org.talend.dataquality.expressions.ExpressionsFactory;
import orgomg.cwm.foundation.expressions.ExpressionNode;
import orgomg.cwm.objectmodel.core.CoreFactory;
import orgomg.cwm.objectmodel.core.Expression;

/**
 * @author scorreia
 * 
 * Helper class for handling BooleanExpressionNode.
 */
public class BooleanExpressionHelper {

    /**
     * Method "createBooleanExpressionNode".
     * 
     * @param body the body of the Expression
     * @return a BooleanExpressionNode with the given Expression.
     */
    public static BooleanExpressionNode createBooleanExpressionNode(String body) {
        BooleanExpressionNode expr = ExpressionsFactory.eINSTANCE.createBooleanExpressionNode();
        Expression expression = createExpression("SQL", body);
        expr.setExpression(expression);
        return expr;
    }

    public static Expression createExpression(String language, String body) {
        Expression expression = CoreFactory.eINSTANCE.createExpression();
        expression.setBody(body);
        expression.setLanguage("SQL");
        return expression;
    }

    public static ExpressionNode createExpressionNode(String language, String body) {
        ExpressionNode node = orgomg.cwm.foundation.expressions.ExpressionsFactory.eINSTANCE.createExpressionNode();
        node.setExpression(createExpression(language, body));
        return node;
    }

    /**
     * Method "getBody".
     * 
     * @param node (must not be null)
     * @return the body string of the expression contained in the given node
     */
    public static String getBody(ExpressionNode node) {
        Expression expression = node.getExpression();
        return (expression == null) ? null : expression.getBody();
    }

    /**
     * Mmethod "createRegularExpression".
     * 
     * @param string
     * @param expression
     * @return
     */
    public static RegularExpression createRegularExpression(String string, String expression) {
        RegularExpression regexp = PatternFactory.eINSTANCE.createRegularExpression();
        regexp.setExpression(createExpression(string, expression));
        return regexp;
    }
}
