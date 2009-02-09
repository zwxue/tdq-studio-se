// ============================================================================
//
// Copyright (C) 2006-2009 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.sqltools;

import java.io.ByteArrayInputStream;

import Zql.ParseException;
import Zql.ZQuery;
import Zql.ZqlParser;

/**
 * DOC scorreia class global comment. Detailled comment
 */
public class ZParserTest {

    private static final String SELECT_EXTRACT = "SELECT  EXTRACT(YEAR FROM TEST_DATE) AS Y , COUNT(*) c FROM TEST_TABLES.TEST_COUNT r GROUP BY Y ORDER BY c DESC ;";

    private static final String SELECT_LIMIT = "SELECT * from TEST_TABLES.TEST_COUNT LIMIT 10;";

    private static final String SELECT_WITH_QUOTES = "SELECT T.`INO_COUNT_NULLS` FROM TDQ_INDICATOR_OPTIONS T";

    private static final String EXTRACT_ORACLE_DATE = "SELECT * FROM (SELECT TO_NUMBER(TO_CHAR(POST_DATE, 'YYYY')) , TO_NUMBER(TO_CHAR(POST_DATE,'Q')) , TO_NUMBER(TO_CHAR(POST_DATE,'MM')) , TO_NUMBER(TO_CHAR(POST_DATE, 'IW')), COUNT(*) c FROM ROOT.DATES r GROUP BY TO_NUMBER(TO_CHAR(POST_DATE, 'YYYY')) , TO_NUMBER(TO_CHAR(POST_DATE,'Q')) , TO_NUMBER(TO_CHAR(POST_DATE,'MM')) , TO_NUMBER(TO_CHAR(POST_DATE, 'IW')) ORDER BY c DESC) AS subquery WHERE ROWNUM <= 20;";

    private static final String MEDIAN_ORACLE_INNER_QUERY = "SELECT \"TEST_DOUBLE\", COUNT(*) OVER( ) total, CAST(COUNT(*) OVER( ) AS DECIMAL)/2 mid, CEIL(CAST(COUNT(*) OVER( ) AS DECIMAL)/2) next, ROW_NUMBER() OVER ( ORDER BY \"TEST_DOUBLE\") rn FROM \"ROOT\".\"TEST_COUNT\" ";

    private static final String MEDIAN_ORACLE = "SELECT AVG(\"TEST_DOUBLE\") FROM ( " + MEDIAN_ORACLE_INNER_QUERY + ") x"
            + "WHERE ( MOD(total,2) = 0     AND rn IN ( mid, mid+1 ) ) OR ( MOD(total,2) = 1 AND rn = next );";

    /**
     * DOC scorreia Comment method "main".
     * 
     * @param args
     */
    public static void main(String[] args) {
        try {
            // String sqlString = SELECT_WITH_QUOTES;
            // String sqlString = SELECT_EXTRACT;
            // String sqlString = SELECT_LIMIT;
            // String sqlString = EXTRACT_ORACLE_DATE;
            String sqlString = MEDIAN_ORACLE_INNER_QUERY;

            ZParserTest myTest = new ZParserTest();

            if (args != null && args.length > 0) {
                StringBuffer buf = new StringBuffer();
                for (String string : args) {
                    buf.append(string);
                    buf.append(" ");
                }
                sqlString = buf.toString();
            }
            myTest.parseQuery(sqlString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void parseQuery(String safeZqlString) throws ParseException {
        System.out.println("Parsing query: " + safeZqlString);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(safeZqlString.getBytes());
        ZqlParser parser = new ZqlParser();
        parser.addCustomFunction("OVER", 0);
        parser.addCustomFunction("OVER", 1);

        parser.initParser(byteArrayInputStream);
        ZQuery zQuery = (ZQuery) parser.readStatement();
        System.out.println("Parsed Query : " + zQuery.toString());

    }
}
