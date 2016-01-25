// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dq.sql.converters;

import org.talend.cwm.helper.ColumnHelper;
import org.talend.cwm.relational.TdColumn;
import org.talend.dataquality.domain.sql.SqlPredicate;
import org.talend.dataquality.expressions.BooleanExpressionNode;
import org.talend.dataquality.expressions.ExpressionsFactory;
import orgomg.cwm.objectmodel.core.CoreFactory;
import orgomg.cwm.objectmodel.core.Expression;

import Zql.ZConstant;
import Zql.ZExp;
import Zql.ZExpression;

/**
 * @author scorreia
 * 
 * A class for building expressions.
 * @param <T> the type of the value to be compared to an SQL element (Column name...).
 */
public class CwmZExpression<T> {

    private SqlPredicate operator;

    private TdColumn column1;

    private TdColumn column2;

    private T instance;

    public CwmZExpression(SqlPredicate op) {
        assert op != null : "null operator given"; //$NON-NLS-1$
        this.operator = op;
    }

    protected String getOperator() {
        return this.operator.getLiteral();
    }

    public void setOperands(TdColumn column, T value) {
        this.column1 = column;
        this.column2 = null;
        this.instance = value;
    }

    public void setOperands(TdColumn col1, TdColumn col2) {
        this.column1 = col1;
        this.column2 = col2;
        this.instance = null;
    }

    public void setNullOperand(TdColumn col1) {
        this.column1 = col1;
        this.column2 = null;
        this.instance = null;
    }

    public void addSubQuery(SqlPredicate operator, CwmZQuery subQuery) {
        // TODO scorreia check goodness of operator (IN, NOT IN)
        // TODO scorreia append subquery
    }

    public <T2> void addExpression(SqlPredicate operator, CwmZExpression<T2> expression) {
        // TODO scorreia check goodness of operator (AND, OR)
        // TODO scorreia append expression
    }

    protected ZExp getColumn1() {
        if (column1 != null) {
            return new ZConstant(ColumnHelper.getFullName(column1), ZConstant.COLUMNNAME);
        }
        return null;
    }

    protected ZExp getColumn2() {
        if (column2 != null) {
            return new ZConstant(ColumnHelper.getFullName(column2), ZConstant.COLUMNNAME);
        }
        return null;
    }

    protected ZExp getInstance() {
        if (instance != null) {
            int type = ZConstant.UNKNOWN; // TODO scorreia set the correct type here?
            return new ZConstant(this.instance.toString(), type);
        }
        return null;
    }

    public ZExpression generateZExpression() {
        ZExpression expr = new ZExpression(this.getOperator());
        expr.addOperand(getColumn1());
        if (instance != null) {
            expr.addOperand(getInstance());
        } else if (column2 != null) {
            expr.addOperand(getColumn2());
        }
        // else if (operator == SqlPredicate.IS_NULL) {
        // || (operator == SqlPredicate.IS_NOT_NULL)
        // nothing to do

        return expr;
    }

    public BooleanExpressionNode generateExpressions() {
        BooleanExpressionNode expr = ExpressionsFactory.eINSTANCE.createBooleanExpressionNode();
        Expression expression = CoreFactory.eINSTANCE.createExpression();
        expression.setBody(this.generateZExpression().toString());
        expression.setLanguage("SQL"); //$NON-NLS-1$
        expr.setExpression(expression);
        return expr;
    }
}
