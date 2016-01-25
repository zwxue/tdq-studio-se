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

import org.junit.Assert;
import org.junit.Test;
import org.talend.cwm.helper.ColumnHelper;
import org.talend.cwm.relational.RelationalFactory;
import org.talend.cwm.relational.TdColumn;
import org.talend.cwm.relational.TdTable;
import org.talend.dataquality.domain.sql.SqlPredicate;

import Zql.ZExpression;

/**
 * DOC scorreia class global comment. Detailled comment
 */
public class CwmZExpressionTest {

    /**
     * Test method for
     * {@link org.talend.dq.sql.converters.CwmZExpression#CwmZExpression(org.talend.dataquality.domain.sql.SqlPredicate)}
     * .
     */
    @Test
    public void testCwmZExpression() {
        CwmZExpression<?> exp = new CwmZExpression<Integer>(SqlPredicate.EQUAL);
        Assert.assertNotNull(exp);
        exp = new CwmZExpression<String>(SqlPredicate.EQUAL);
        Assert.assertNotNull(exp);
    }

    SqlPredicate[] operators = SqlPredicate.values();

    /**
     * Test method for {@link org.talend.dq.sql.converters.CwmZExpression#getOperator()}.
     */
    @Test
    public void testGetOperator() {

        Assert.assertTrue(operators.length == 19);
        for (SqlPredicate equalOp : operators) {
            CwmZExpression<?> exp = new CwmZExpression<Integer>(equalOp);
            String operator = exp.getOperator();
            Assert.assertEquals(equalOp.getLiteral(), operator);
        }
    }

    /**
     * Test method for
     * {@link org.talend.dq.sql.converters.CwmZExpression#setOperands(orgomg.cwm.resource.relational.Column, java.lang.Object)}
     * .
     */
    @Test
    public void testSetOperandsColumnT() {
        CwmZExpression<Integer> exp = new CwmZExpression<Integer>(SqlPredicate.EQUAL);
        String name = "USER_ID"; //$NON-NLS-1$
        TdColumn column = getColumn(name);
        Integer value = new Integer(5);
        exp.setOperands(column, value);
        Assert.assertEquals(value.toString(), exp.getInstance().toString());
    }

    private TdColumn getColumn(String name) {
        return ColumnHelper.createTdColumn(name);
    }

    private TdTable getTable(String name) {
        TdTable tdTable = RelationalFactory.eINSTANCE.createTdTable();
        tdTable.setName(name);
        tdTable.setLabel(name);
        return tdTable;
    }

    /**
     * Test method for
     * {@link org.talend.dq.sql.converters.CwmZExpression#setOperands(orgomg.cwm.resource.relational.Column, orgomg.cwm.resource.relational.Column)}
     * .
     */
    @Test
    public void testSetOperandsColumnColumn() {
        CwmZExpression<Integer> exp = new CwmZExpression<Integer>(SqlPredicate.EQUAL);
        String column1_Name = "USER_ID"; //$NON-NLS-1$
        TdColumn column1 = getColumn(column1_Name);

        String column2_Name = "USER_NAME"; //$NON-NLS-1$
        TdColumn column2 = getColumn(column2_Name);
        exp.setOperands(column1, column2);
        Assert.assertNull(exp.getInstance());
    }

    /**
     * Test method for {@link org.talend.dq.sql.converters.CwmZExpression#getColumn1()}.
     */
    @Test
    public void testGetColumn1() {
        CwmZExpression<Double> exp = new CwmZExpression<Double>(SqlPredicate.EQUAL);
        String name = "USER_ID"; //$NON-NLS-1$
        TdColumn column = getColumn(name);
        TdTable table = getTable(tableName);
        column.setOwner(table);
        Double value = new Double(5.0);
        exp.setOperands(column, value);
        Assert.assertNotNull(exp.getColumn1());
    }

    /**
     * Test method for {@link org.talend.dq.sql.converters.CwmZExpression#getColumn2()}.
     */
    @Test
    public void testGetColumn2() {
        CwmZExpression<Double> exp = new CwmZExpression<Double>(SqlPredicate.EQUAL);
        String name = "USER_ID"; //$NON-NLS-1$
        TdColumn column = getColumn(name);
        TdTable table = getTable(tableName);
        column.setOwner(table);
        Double value = new Double(5.0);
        exp.setOperands(column, value);
        Assert.assertNull(exp.getColumn2());
    }

    /**
     * Test method for {@link org.talend.dq.sql.converters.CwmZExpression#getInstance()}.
     */
    @Test
    public void testGetInstance() {
        CwmZExpression<Double> exp = new CwmZExpression<Double>(SqlPredicate.EQUAL);
        String name = "USER_ID"; //$NON-NLS-1$
        TdColumn column = getColumn(name);
        TdTable table = getTable(tableName);
        column.setOwner(table);
        Double value = new Double(5.0);
        exp.setOperands(column, value);
        Assert.assertEquals(value.toString(), exp.getInstance().toString());
    }

    String[] columnNames = { "USER_ID", "USER_NAME", "USER_PHONE" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

    String tableName = "test"; //$NON-NLS-1$

    Integer[] values = { 1, 2, 3 };

    /**
     * Test method for {@link org.talend.dq.sql.converters.CwmZExpression#generateZExpression()}.
     */
    @Test
    public void testGenerateZExpression() {
        for (SqlPredicate operator : operators) {
            for (Integer value : values) {
                for (String name : columnNames) {
                    generateExpression(operator, name, value);
                }
            }
        }
        for (SqlPredicate operator : operators) {
            for (String col1 : columnNames) {
                for (String col2 : columnNames) {
                    generateExpression(operator, col1, col2);
                }
            }
        }
        String list = "1,2,3,45,4,6"; //$NON-NLS-1$
        generateExpression(SqlPredicate.IN, "USER_ID", list); //$NON-NLS-1$

        String exp = "Select id from lookup"; //$NON-NLS-1$
        generateExpression(SqlPredicate.IN, "USER_ID", exp); //$NON-NLS-1$
    }

    /**
     * DOC scorreia Comment method "generateExpression".
     * 
     * @param operator
     * @param value
     * @param name
     */
    private <T> void generateExpression(SqlPredicate operator, String name, T value) {
        CwmZExpression<T> exp = new CwmZExpression<T>(operator);
        TdColumn column = getColumn(name);
        TdTable table = getTable(tableName);
        column.setOwner(table);
        exp.setOperands(column, value);

        ZExpression zExpression = exp.generateZExpression();
        Assert.assertNotNull(zExpression);

        String nameStr = tableName + "." + column.getName(); //$NON-NLS-1$ 
        Assert.assertEquals(simpleExpectedExpression(nameStr, operator, value), zExpression.toString());
        System.out.println(zExpression.toString());
    }

    private <T> String simpleExpectedExpression(String left, SqlPredicate operator, T right) {
        String rightStr = right.toString();
        if (operator.equals(SqlPredicate.UNION) || operator.equals(SqlPredicate.ALL)) {
            return left + " " + operator.getLiteral() + " " + rightStr; //$NON-NLS-1$ //$NON-NLS-2$
        }
        if (operator.equals(SqlPredicate.IN) || operator.equals(SqlPredicate.NOT_IN)) {
            rightStr = parenthesis(rightStr);
        }
        return parenthesis(left + " " + operator.getLiteral() + " " + rightStr); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * DOC scorreia Comment method "parenthesis".
     * 
     * @param rightStr
     * @return
     */
    private String parenthesis(String rightStr) {
        return "(" + rightStr + ")"; //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Test method for {@link org.talend.dq.sql.converters.CwmZExpression#generateExpressions()}.
     */
    @Test
    public void testGenerateExpressions() {
        CwmZExpression<String> expre = new CwmZExpression<String>(SqlPredicate.EQUAL);
        TdColumn column = getColumn("USER_ID"); //$NON-NLS-1$
        TdTable table = getTable(tableName);
        column.setOwner(table);
        expre.setOperands(column, "\"sunny\""); //$NON-NLS-1$
        Assert.assertNotNull(expre.generateExpressions());

        String nameStr = tableName + "." + column.getName(); //$NON-NLS-1$
        System.out.println(expre.generateExpressions().getExpression().getBody());
        Assert.assertEquals(simpleExpectedExpression(nameStr, SqlPredicate.EQUAL, "\"sunny\""), expre.generateExpressions() //$NON-NLS-1$
                .getExpression().getBody());
    }

}
