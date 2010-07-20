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
package org.talend.cwm.db.connection;

/**
 * DOC zshen class global comment. help to tidy xquery expression
 */
public final class XQueryExpressionUtil {

    public static final int ROWS_PER_PAGE = 10;

    private static StringBuffer expression = new StringBuffer();

    private static String tableNode;

    private static int startNum = 0;

    private static String columnNodeNameArraystr = null;

    private XQueryExpressionUtil() {

    }

    /**
     * 
     * DOC zshen Comment method "toParseXquery". get the key parameter for tableNode、startNum.....
     * 
     * @param xqueryStr
     */
    public static void toParseXquery(String xqueryStr) {
        int beginIndex = 0;
        int endIndex = 0;
        beginIndex = xqueryStr.indexOf("//") + 2;
        endIndex = xqueryStr.indexOf(" let $_page_ := ", beginIndex);
        tableNode = xqueryStr.substring(beginIndex, endIndex);
        beginIndex = xqueryStr.indexOf(",", endIndex) + 1;
        endIndex = xqueryStr.indexOf(",", beginIndex);
        startNum = Integer.parseInt(xqueryStr.substring(beginIndex, endIndex));
        beginIndex = xqueryStr.indexOf(" then ", endIndex) + 6;
        endIndex = xqueryStr.indexOf(" else ", beginIndex);
        columnNodeNameArraystr = xqueryStr.substring(beginIndex, endIndex);
    }

    /**
     * 
     * DOC zshen Comment method "getExpression".
     * 
     * @return return the xquery statment which use current key parameter.
     */
    public static String getExpression() {
        if (tableNode == null) {
            return null;
        }
        expression.delete(0, expression.length());
        expression.append("let $_leres0_ := //");
        expression.append(tableNode);
        expression.append(" let $_page_ := for $");
        expression.append(tableNode);
        expression.append(" in subsequence($_leres0_,");
        expression.append(startNum);
        expression.append(",");
        expression.append(ROWS_PER_PAGE);
        expression.append(") return <result>{if ($");
        expression.append(tableNode);
        expression.append(") then ");
        expression.append(columnNodeNameArraystr == null ? "$" + tableNode : columnNodeNameArraystr);
        expression.append(" else <null/>}</result> return insert-before($_page_,0,<totalCount>{count($_leres0_)}</totalCount>)");

        return expression.toString();
    }

    public static String getTableNode() {
        return tableNode;
    }

    public static void setTableNode(String tableNodePar) {
        tableNode = tableNodePar;
    }

    public static int getStartNum() {
        return startNum;
    }

    public static void setStartNum(int startNumPar) {
        startNum = startNumPar;
    }

    public static String getNodeNameArraystr() {
        return columnNodeNameArraystr;
    }

    public static void setNodeNameArraystr(String nodeNameArraystr) {
        columnNodeNameArraystr = nodeNameArraystr;
    }

    /**
     * make the value range to increase DOC zshen Comment method "increamVernier".
     */
    public static void increaseVernier() {
        startNum += XQueryExpressionUtil.ROWS_PER_PAGE;
    }

    /**
     * make the value range to decrease DOC zshen Comment method "increamVernier".
     */
    public static void decreaseVernier() {
        if (startNum >= XQueryExpressionUtil.ROWS_PER_PAGE) {
            startNum -= XQueryExpressionUtil.ROWS_PER_PAGE;
        }
    }

}
