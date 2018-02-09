// ============================================================================
//
// Copyright (C) 2006-2018 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dq.dbms;

import static org.junit.Assert.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.talend.commons.utils.StringUtils;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.metadata.builder.database.JavaSqlFactory;
import org.talend.dq.helper.UnitTestBuildHelper;
import org.talend.utils.sugars.TypedReturnCode;

/**
 * created by qiongli on 2014-4-11 Detailled comment
 * 
 */
public class OracleDbmsLanguageTest {

    private OracleDbmsLanguage oracDbms = new OracleDbmsLanguage();

    Connection dataManager = null;

    java.sql.Connection sqlConn = null;

    /**
     * DOC talend2 Comment method "setUp".
     * 
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        dataManager = UnitTestBuildHelper.getDefault().getRealOracleDatabase();
        TypedReturnCode<java.sql.Connection> typeRet = JavaSqlFactory.createConnection(dataManager);
        sqlConn = typeRet.getObject();
    }

    /**
     * DOC qiongli Comment method "tearDown".
     * 
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test for:make the query of AverageLengthWithBlankRows result is same as before.
     * 
     * @throws SQLException
     */
    @Test
    public void testGetAverageLengthWithBlankRows() throws SQLException {

        // old query for varchar data type
        List<Object> oldLs = querySql("SELECT * FROM \"TDQ\".\"JUNIT_TDQ6921\" WHERE LENGTH( CASE WHEN \"ADRESS\" IS NOT NULL AND  LENGTH( TRIM(\"ADRESS\") ) IS NULL  THEN '' ELSE  \"ADRESS\" END) BETWEEN (SELECT FLOOR(SUM(LENGTH( CASE WHEN \"ADRESS\" IS NOT NULL AND  LENGTH( TRIM(\"ADRESS\") ) IS NULL  THEN '' ELSE  \"ADRESS\" END)) / COUNT(*)) FROM \"TDQ\".\"JUNIT_TDQ6921\" WHERE \"ADRESS\" IS NOT NULL ) AND (SELECT CEIL(SUM(LENGTH( CASE WHEN \"ADRESS\" IS NOT NULL AND  LENGTH( TRIM(\"ADRESS\") ) IS NULL  THEN '' ELSE  \"ADRESS\" END )) / COUNT(* )) FROM \"TDQ\".\"JUNIT_TDQ6921\" WHERE \"ADRESS\" IS NOT NULL )"); //$NON-NLS-1$

        // new query for varchar data type
        String queryStr = oracDbms.getAverageLengthWithBlankRows();
        queryStr = StringUtils.replace(queryStr, "<%=__TABLE_NAME__%>", "\"TDQ\".\"JUNIT_TDQ6921\""); //$NON-NLS-1$ //$NON-NLS-2$
        String replacedCoumnStr = StringUtils.replace(queryStr, "<%=__COLUMN_NAMES__%>", "\"ADRESS\""); //$NON-NLS-1$//$NON-NLS-2$
        List<Object> newLs = querySql(replacedCoumnStr);
        assertAndCompareResult(oldLs, newLs);

        // nvarchar data type
        replacedCoumnStr = StringUtils.replace(queryStr, "<%=__COLUMN_NAMES__%>", "\"NAME\""); //$NON-NLS-1$ //$NON-NLS-2$
        List<Object> resLs = getResultRows(replacedCoumnStr);
        assertTrue(resLs.size() == 9);
    }

    /**
     * Test for:make the query of AverageLengthWithNullBlankRows result is same as before.
     * 
     * @throws SQLException
     */
    @Test
    public void testGetAverageLengthWithNullBlankRows() throws SQLException {
        // old query for varchar data type

        List<Object> oldLs = querySql("SELECT * FROM \"TDQ\".\"JUNIT_TDQ6921\" WHERE LENGTH( CASE WHEN \"ADRESS\" IS NOT NULL AND  LENGTH( TRIM(\"ADRESS\") ) IS NULL  THEN '' ELSE  \"ADRESS\" END) BETWEEN (SELECT FLOOR(SUM(LENGTH( CASE WHEN \"ADRESS\" IS NOT NULL AND  LENGTH( TRIM(\"ADRESS\") ) IS NULL  THEN '' ELSE  \"ADRESS\" END)) / COUNT(*)) FROM \"TDQ\".\"JUNIT_TDQ6921\") AND (SELECT CEIL(SUM(LENGTH( CASE WHEN \"ADRESS\" IS NOT NULL AND  LENGTH( TRIM(\"ADRESS\") ) IS NULL  THEN '' ELSE  \"ADRESS\" END )) / COUNT(* )) FROM \"TDQ\".\"JUNIT_TDQ6921\")"); //$NON-NLS-1$

        // new query for varchar data type
        String queryStr = oracDbms.getAverageLengthWithNullBlankRows();
        queryStr = StringUtils.replace(queryStr, "<%=__TABLE_NAME__%>", "\"TDQ\".\"JUNIT_TDQ6921\""); //$NON-NLS-1$ //$NON-NLS-2$
        String replacedCoumnStr = StringUtils.replace(queryStr, "<%=__COLUMN_NAMES__%>", "\"ADRESS\""); //$NON-NLS-1$//$NON-NLS-2$
        List<Object> newLs = querySql(replacedCoumnStr);
        assertAndCompareResult(oldLs, newLs);

        // nvarchar data type

        replacedCoumnStr = StringUtils.replace(queryStr, "<%=__COLUMN_NAMES__%>", "\"NAME\""); //$NON-NLS-1$//$NON-NLS-2$
        List<Object> resLs = getResultRows(replacedCoumnStr);
        assertTrue(resLs.size() == 11);
    }

    /**
     * Test for:the query of AverageLengthWithNullRows result is same as before.
     * 
     * @throws SQLException
     */
    @Test
    public void testGetAverageLengthWithNullRows() throws SQLException {
        // old query for varchar data type
        List<Object> oldLs = querySql("SELECT * FROM \"TDQ\".\"JUNIT_TDQ6921\" WHERE(\"ADRESS\" IS NULL OR  TRIM(\"ADRESS\")  IS NOT NULL )AND LENGTH(\"ADRESS\") BETWEEN (SELECT FLOOR(SUM(LENGTH(\"ADRESS\" )) / COUNT( * )) FROM \"TDQ\".\"JUNIT_TDQ6921\" WHERE(\"ADRESS\" IS NULL OR  TRIM(\"ADRESS\")  IS NOT NULL )) AND (SELECT CEIL(SUM(LENGTH(\"ADRESS\" )) / COUNT(*)) FROM \"TDQ\".\"JUNIT_TDQ6921\"  WHERE(\"ADRESS\" IS NULL OR  TRIM(\"ADRESS\")  IS NOT NULL ))"); //$NON-NLS-1$
        // new query for varchar data type
        String queryStr = oracDbms.getAverageLengthWithNullRows();
        queryStr = StringUtils.replace(queryStr, "<%=__TABLE_NAME__%>", "\"TDQ\".\"JUNIT_TDQ6921\""); //$NON-NLS-1$ //$NON-NLS-2$
        String replacedCoumnStr = StringUtils.replace(queryStr, "<%=__COLUMN_NAMES__%>", "\"ADRESS\""); //$NON-NLS-1$ //$NON-NLS-2$
        List<Object> newLs = querySql(replacedCoumnStr);
        assertAndCompareResult(oldLs, newLs);

        // nvarchar data type
        replacedCoumnStr = StringUtils.replace(queryStr, "<%=__COLUMN_NAMES__%>", "\"NAME\""); //$NON-NLS-1$ //$NON-NLS-2$
        List<Object> resLs = getResultRows(replacedCoumnStr);
        assertTrue(resLs.size() == 9);

    }

    private List<Object> querySql(String queryStmt) throws SQLException {
        Statement stat = null;
        ResultSet resultSet = null;
        List<Object> ls = new ArrayList<Object>();

        stat = sqlConn.createStatement();
        stat.execute(queryStmt);
        resultSet = stat.getResultSet();
        int columsCount = 0;
        if (resultSet != null) {
            columsCount = resultSet.getMetaData().getColumnCount();
        }
        while (resultSet.next()) {
            for (int i = 1; i <= columsCount; i++) {
                ls.add(resultSet.getString(i));
            }
        }

        stat.close();

        return ls;
    }

    private List<Object> getResultRows(String queryStmt) throws SQLException {
        Statement stat = null;
        ResultSet resultSet = null;
        List<Object> ls = new ArrayList<Object>();

        stat = sqlConn.createStatement();
        stat.execute(queryStmt);
        resultSet = stat.getResultSet();
        while (resultSet.next()) {
            ls.add(resultSet.getString(1));
        }

        stat.close();

        return ls;
    }

    private void assertAndCompareResult(List<Object> oldLs, List<Object> newLs) {
        assertTrue(oldLs.size() == newLs.size());
        for (int i = 0; i < oldLs.size(); i++) {
            assertEquals(oldLs.get(i), newLs.get(i));
        }

    }

}
