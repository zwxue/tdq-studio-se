// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dq.indicators;

import static org.junit.Assert.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.talend.commons.utils.StringUtils;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.metadata.builder.database.JavaSqlFactory;
import org.talend.cwm.relational.TdExpression;
import org.talend.dataprofiler.core.helper.UnitTestBuildHelper;
import org.talend.dataprofiler.core.manager.DQStructureManager;
import org.talend.dataquality.indicators.definition.IndicatorDefinition;
import org.talend.dq.indicators.definitions.DefinitionHandler;
import org.talend.utils.sugars.TypedReturnCode;

/**
 * TDQ-6921 Test Text statistic indicators have same behavior as before modification.
 * 
 */
public class TextStatisticIndicatorsForOracleTest {

    // indicator uuids
    private final String AVG_LENGTH_WITH_BLANK_AND_NULL_UUID = "__TbUIJSOEd-TE5ti6XNR2Q"; //$NON-NLS-1$

    private final String AVG_LENGTH_WITH_BLANK_UUID = "__gPoIJSOEd-TE5ti6XNR2Q"; //$NON-NLS-1$

    private final String AVG_LENGTH_WITH_NULL_UUID = "__vI_wJSOEd-TE5ti6XNR2Q"; //$NON-NLS-1$

    private final String MAXIMAL_LENGTH_WITH_BLANK_AND_NULL_UUID = "_-hzp8JSOEd-TE5ti6XNR2Q"; //$NON-NLS-1$

    private final String MAXIMAL_LENGTH_WITH_BLANK_UUID = "_-xmZcJSOEd-TE5ti6XNR2Q"; //$NON-NLS-1$

    private final String MAXIMAL_LENGTH_WITH_NULL_UUID = "_-_UFUJSOEd-TE5ti6XNR2Q"; //$NON-NLS-1$

    private final String MAXIMAL_LENGTH_UUID = "_ccHq1RF2Ed2PKb6nEJEvhw"; //$NON-NLS-1$

    private final String MINIMAL_LENGTH_WITH_BLANK_AND_NULL_UUID = "_9HDjMJSOEd-TE5ti6XNR2Q"; //$NON-NLS-1$

    private final String MINIMAL_LENGTH_WITH_BLANK_UUID = "_G4EzQZU9Ed-Y15ulK_jijQ"; //$NON-NLS-1$

    private final String MINIMAL_LENGTH_WITH_NULL_UUID = "_a4KsoI1qEd-xwI2imLgHRA"; //$NON-NLS-1$

    private final String MINIMAL_LENGTH_UUID = "_ccHq1BF2Ed2PKb6nEJEvhw"; //$NON-NLS-1$

    /**
     * DOC qiongli Comment method "setUp".
     * 
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        if (DQStructureManager.getInstance().isNeedCreateStructure()) {
            DQStructureManager.getInstance().createDQStructure();
        }
    }

    /**
     * DOC qiongli Comment method "tearDown".
     * 
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void test() {
    }

    @Test
    public void testTextIndicatorsForNVarchar() throws SQLException {
        Connection dbConn = UnitTestBuildHelper.getRealOracleDatabase();
        TypedReturnCode<java.sql.Connection> typeRet = JavaSqlFactory.createConnection(dbConn);
        java.sql.Connection sqlConn = typeRet.getObject();
        // min length
        String body = getIndicatorSqlExpression(MINIMAL_LENGTH_UUID, "\"NAME\""); //$NON-NLS-1$
        assertNotNull(body);
        assertTrue(getResultValue(sqlConn, body, 1).equals("1")); //$NON-NLS-1$

        // min length with null
        body = getIndicatorSqlExpression(MINIMAL_LENGTH_WITH_NULL_UUID, "\"NAME\""); //$NON-NLS-1$
        assertNotNull(body);
        assertTrue(getResultValue(sqlConn, body, 1).equals("0")); //$NON-NLS-1$

        // min length with blank
        body = getIndicatorSqlExpression(MINIMAL_LENGTH_WITH_BLANK_UUID, "\"NAME\""); //$NON-NLS-1$
        assertNotNull(body);
        assertTrue(getResultValue(sqlConn, body, 1).equals("1"));

        // minilength with blank and null
        body = getIndicatorSqlExpression(MINIMAL_LENGTH_WITH_BLANK_AND_NULL_UUID, "\"NAME\""); //$NON-NLS-1$
        assertNotNull(body);
        assertTrue(getResultValue(sqlConn, body, 1).equals("0")); //$NON-NLS-1$

        // max length
        body = getIndicatorSqlExpression(MAXIMAL_LENGTH_UUID, "\"NAME\""); //$NON-NLS-1$
        assertNotNull(body);
        assertTrue(getResultValue(sqlConn, body, 1).equals("6")); //$NON-NLS-1$

        // max length with null
        body = getIndicatorSqlExpression(MAXIMAL_LENGTH_WITH_NULL_UUID, "\"NAME\""); //$NON-NLS-1$
        assertNotNull(body);
        assertTrue(getResultValue(sqlConn, body, 1).equals("6")); //$NON-NLS-1$
        // max length with blank
        body = getIndicatorSqlExpression(MAXIMAL_LENGTH_WITH_BLANK_UUID, "\"NAME\""); //$NON-NLS-1$
        assertNotNull(body);
        assertTrue(getResultValue(sqlConn, body, 1).equals("6")); //$NON-NLS-1$
        // max length with null and blank
        body = getIndicatorSqlExpression(MAXIMAL_LENGTH_WITH_BLANK_AND_NULL_UUID, "\"NAME\""); //$NON-NLS-1$
        assertNotNull(body);
        assertTrue(getResultValue(sqlConn, body, 1).equals("6")); //$NON-NLS-1$
        // avg length with null
        body = getIndicatorSqlExpression(AVG_LENGTH_WITH_NULL_UUID, "\"NAME\""); //$NON-NLS-1$
        assertNotNull(body);
        assertTrue(getResultValue(sqlConn, body, 1).equals("38")); //$NON-NLS-1$
        assertTrue(getResultValue(sqlConn, body, 2).equals("18")); //$NON-NLS-1$
        // avg length with blank
        body = getIndicatorSqlExpression(AVG_LENGTH_WITH_BLANK_UUID, "\"NAME\""); //$NON-NLS-1$
        assertNotNull(body);
        assertTrue(getResultValue(sqlConn, body, 1).equals("38")); //$NON-NLS-1$
        assertTrue(getResultValue(sqlConn, body, 2).equals("17")); //$NON-NLS-1$
        // avg length with blank and null
        body = getIndicatorSqlExpression(AVG_LENGTH_WITH_BLANK_AND_NULL_UUID, "\"NAME\""); //$NON-NLS-1$
        assertNotNull(body);
        assertTrue(getResultValue(sqlConn, body, 1).equals("38")); //$NON-NLS-1$
        assertTrue(getResultValue(sqlConn, body, 2).equals("20")); //$NON-NLS-1$
    }

    /**
     * DOC qiongli Comment method "getIndicatorSqlExpression".
     * 
     * @param indicatorUUID
     * @return
     */
    private String getIndicatorSqlExpression(String indicatorUUID, String ReplacedColumn) {
        IndicatorDefinition indiDefinition = DefinitionHandler.getInstance().getDefinitionById(indicatorUUID);
        EList<TdExpression> sqlGenericExpression = indiDefinition.getSqlGenericExpression();
        TdExpression expression = null;
        String language = "Oracle"; //$NON-NLS-1$
        for (TdExpression e : sqlGenericExpression) {
            if (e.getLanguage().equals(language)) {
                expression = e;
                break;
            }
        }
        String body = null;
        if (expression != null) {
            body = expression.getBody();
            body = StringUtils.replace(body, "<%=__TABLE_NAME__%>", "\"TDQ\".\"JUNIT_TDQ6921\""); //$NON-NLS-1$ //$NON-NLS-2$
            body = StringUtils.replace(body, "<%=__COLUMN_NAMES__%>", ReplacedColumn); //$NON-NLS-1$
            body = StringUtils.replace(body, "<%=__AND_WHERE_CLAUSE__%>", ""); //$NON-NLS-1$//$NON-NLS-2$
            body = StringUtils.replace(body, "<%=__WHERE_CLAUSE__%>", ""); //$NON-NLS-1$//$NON-NLS-2$

        }
        return body;
    }

    @Test
    public void testTextIndicatorsForVarchar() throws SQLException {

        Connection dbConn = UnitTestBuildHelper.getRealOracleDatabase();
        TypedReturnCode<java.sql.Connection> typeRet = JavaSqlFactory.createConnection(dbConn);
        java.sql.Connection sqlConn = typeRet.getObject();
        // min length
        List<Object> oldLs = querySql(
                sqlConn,
                "SELECT MIN(LENGTH('XX' || \"ADRESS\")) - LENGTH('XX') FROM \"TDQ\".\"JUNIT_TDQ6921\" WHERE (\"ADRESS\" IS NOT NULL ) AND (TRIM(NVL(\"ADRESS\",'NULL TALEND')) IS NOT NULL) "); //$NON-NLS-1$
        String newQueryStr = getIndicatorSqlExpression(MINIMAL_LENGTH_UUID, "\"ADRESS\"");
        List<Object> newLs = querySql(sqlConn, newQueryStr);
        compareResult(newLs, oldLs);

        // min length with null
        oldLs = querySql(
                sqlConn,
                "SELECT MIN(LENGTH('XX' || CASE WHEN \"ADRESS\" IS NULL  THEN '' ELSE \"ADRESS\" END)) - LENGTH('XX') FROM \"TDQ\".\"JUNIT_TDQ6921\" WHERE (TRIM(NVL(\"ADRESS\",'NULL TALEND')) IS NOT NULL)"); //$NON-NLS-1$
        newQueryStr = getIndicatorSqlExpression(MINIMAL_LENGTH_WITH_NULL_UUID, "\"ADRESS\"");
        newLs = querySql(sqlConn, newQueryStr);
        compareResult(newLs, oldLs);

        // min length with blank
        oldLs = querySql(sqlConn,
                "SELECT MIN(LENGTH('XX' || \"ADRESS\")) - LENGTH('XX') FROM \"TDQ\".\"JUNIT_TDQ6921\" WHERE (\"ADRESS\" IS NOT NULL ) "); //$NON-NLS-1$
        newQueryStr = getIndicatorSqlExpression(MINIMAL_LENGTH_WITH_BLANK_UUID, "\"ADRESS\"");
        newLs = querySql(sqlConn, newQueryStr);
        compareResult(newLs, oldLs);

        // minilength with blank and null
        oldLs = querySql(
                sqlConn,
                "SELECT MIN(LENGTH('XX' || CASE WHEN \"ADRESS\" IS NULL  THEN '' ELSE \"ADRESS\" END)) - LENGTH('XX') FROM \"TDQ\".\"JUNIT_TDQ6921\" "); //$NON-NLS-1$
        newQueryStr = getIndicatorSqlExpression(MINIMAL_LENGTH_WITH_BLANK_AND_NULL_UUID, "\"ADRESS\"");
        newLs = querySql(sqlConn, newQueryStr);
        compareResult(newLs, oldLs);

        // max length
        oldLs = querySql(
                sqlConn,
                "SELECT MAX(LENGTH('XX' || \"ADRESS\")) - LENGTH('XX') FROM \"TDQ\".\"JUNIT_TDQ6921\" WHERE (\"ADRESS\" IS NOT NULL ) AND (TRIM(NVL(\"ADRESS\",'NULL TALEND')) IS NOT NULL)"); //$NON-NLS-1$
        newQueryStr = getIndicatorSqlExpression(MAXIMAL_LENGTH_UUID, "\"ADRESS\"");
        newLs = querySql(sqlConn, newQueryStr);
        compareResult(newLs, oldLs);

        // max length with null
        oldLs = querySql(
                sqlConn,
                "SELECT MAX(LENGTH('XX' || \"ADRESS\")) - LENGTH('XX') FROM \"TDQ\".\"JUNIT_TDQ6921\" WHERE (\"ADRESS\" IS NOT NULL ) AND (TRIM(NVL(\"ADRESS\",'NULL TALEND')) IS NOT NULL) "); //$NON-NLS-1$
        newQueryStr = getIndicatorSqlExpression(MAXIMAL_LENGTH_WITH_NULL_UUID, "\"ADRESS\"");
        newLs = querySql(sqlConn, newQueryStr);
        compareResult(newLs, oldLs);

        // max length with blank
        oldLs = querySql(sqlConn,
                "SELECT MAX(LENGTH('XX' || \"ADRESS\")) - LENGTH('XX') FROM \"TDQ\".\"JUNIT_TDQ6921\" WHERE (\"ADRESS\" IS NOT NULL ) "); //$NON-NLS-1$
        newQueryStr = getIndicatorSqlExpression(MAXIMAL_LENGTH_WITH_BLANK_UUID, "\"ADRESS\"");
        newLs = querySql(sqlConn, newQueryStr);
        compareResult(newLs, oldLs);

        // max length with null and blank
        oldLs = querySql(
                sqlConn,
                "SELECT MAX(LENGTH('XX' || CASE WHEN \"ADRESS\" IS NULL  THEN '' ELSE \"ADRESS\" END)) - LENGTH('XX') FROM \"TDQ\".\"JUNIT_TDQ6921\" "); //$NON-NLS-1$
        newQueryStr = getIndicatorSqlExpression(MAXIMAL_LENGTH_WITH_BLANK_AND_NULL_UUID, "\"ADRESS\""); //$NON-NLS-1$
        newLs = querySql(sqlConn, newQueryStr);
        compareResult(newLs, oldLs);

        // avg length with null
        oldLs = querySql(
                sqlConn,
                "SELECT SUM(LENGTH(CASE WHEN \"ADRESS\" IS NULL  THEN '' ELSE \"ADRESS\" END)), COUNT(*) FROM \"TDQ\".\"JUNIT_TDQ6921\" WHERE (TRIM(NVL(\"ADRESS\",'NULL TALEND')) IS NOT NULL) "); //$NON-NLS-1$
        newQueryStr = getIndicatorSqlExpression(AVG_LENGTH_WITH_NULL_UUID, "\"ADRESS\""); //$NON-NLS-1$
        newLs = querySql(sqlConn, newQueryStr);
        compareResult(newLs, oldLs);

        // avg length with blank
        oldLs = querySql(
                sqlConn,
                "SELECT SUM(LENGTH(CASE WHEN \"ADRESS\" IS NOT NULL AND  LENGTH( TRIM(\"ADRESS\") ) IS NULL  THEN '' ELSE  \"ADRESS\" END)), COUNT(*) FROM \"TDQ\".\"JUNIT_TDQ6921\" WHERE (\"ADRESS\" IS NOT NULL ) "); //$NON-NLS-1$
        newQueryStr = getIndicatorSqlExpression(AVG_LENGTH_WITH_BLANK_UUID, "\"ADRESS\"");
        newLs = querySql(sqlConn, newQueryStr);
        compareResult(newLs, oldLs);

        // avg length with blank and null

        oldLs = querySql(
                sqlConn,
                "SELECT SUM(LENGTH(CASE WHEN  CASE WHEN \"ADRESS\" IS NULL  THEN '' ELSE \"ADRESS\" END  IS NOT NULL AND  LENGTH( TRIM( CASE WHEN \"ADRESS\" IS NULL  THEN '' ELSE \"ADRESS\" END ) ) IS NULL  THEN '' ELSE   CASE WHEN \"ADRESS\" IS NULL  THEN '' ELSE \"ADRESS\" END  END)), COUNT(*) FROM \"TDQ\".\"JUNIT_TDQ6921\" "); //$NON-NLS-1$
        newQueryStr = getIndicatorSqlExpression(AVG_LENGTH_WITH_BLANK_AND_NULL_UUID, "\"ADRESS\""); //$NON-NLS-1$
        newLs = querySql(sqlConn, newQueryStr);
        compareResult(newLs, oldLs);

    }

    private List<Object> querySql(java.sql.Connection sqlConn, String queryStmt) throws SQLException {
        Statement stat = null;
        ResultSet resultSet = null;
        List<Object> ls = new ArrayList<Object>();

        stat = sqlConn.createStatement();
        stat.execute(queryStmt);
        resultSet = stat.getResultSet();
        int columsCount = 0;
        if (resultSet != null) {
            columsCount = resultSet.getMetaData().getColumnCount();
            while (resultSet.next()) {
                for (int i = 1; i <= columsCount; i++) {
                    ls.add(resultSet.getString(i));
                }
            }
        }

        stat.close();

        return ls;
    }

    private String getResultValue(java.sql.Connection sqlConn, String queryStmt, int columnIndex) throws SQLException {

        Statement stat = null;
        ResultSet resultSet = null;
        String value = null;
        stat = sqlConn.createStatement();
        stat.execute(queryStmt);
        resultSet = stat.getResultSet();
        while (resultSet.next()) {
            value = resultSet.getString(columnIndex);
            break;
        }

        stat.close();

        return value;

    }

    private void compareResult(List<Object> oldLs, List<Object> newLs) {
        assertTrue(oldLs.size() == newLs.size());
        for (int i = 0; i < oldLs.size(); i++) {
            assertEquals(oldLs.get(i), newLs.get(i));
        }

    }

}
