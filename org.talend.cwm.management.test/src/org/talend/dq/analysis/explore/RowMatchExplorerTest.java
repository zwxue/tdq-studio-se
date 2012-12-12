// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
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

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.metadata.builder.connection.ConnectionFactory;
import org.talend.core.model.metadata.builder.database.dburl.SupportDBUrlType;
import org.talend.cwm.helper.TaggedValueHelper;
import org.talend.cwm.relational.RelationalFactory;
import org.talend.cwm.relational.TdColumn;
import org.talend.cwm.relational.TdTable;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.analysis.AnalysisContext;
import org.talend.dataquality.analysis.AnalysisFactory;
import org.talend.dataquality.analysis.AnalysisResult;
import org.talend.dataquality.indicators.columnset.ColumnsetFactory;
import org.talend.dataquality.indicators.columnset.RowMatchingIndicator;
import org.talend.dq.indicators.preview.table.ChartDataEntity;
import orgomg.cwm.resource.relational.Catalog;
import orgomg.cwm.resource.relational.Schema;

public class RowMatchExplorerTest {

    private static final String COMMENTS = "-- Analysis:  ;\n" + "-- Type of Analysis:  ;\n" + "-- Purpose: Purpose ;\n"
            + "-- Description: Description ;\n" + "-- AnalyzedElement: tableA ;\n" + "-- Indicator: RowMatchingIndicator ;\n";

    private static RowMatchExplorer explorer;

    private static Analysis analysis;

    private static Connection connection;

    private static ChartDataEntity entity;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {

        // analysis
        analysis = AnalysisFactory.eINSTANCE.createAnalysis();
        AnalysisContext context = AnalysisFactory.eINSTANCE.createAnalysisContext();
        analysis.setContext(context);
        AnalysisResult mockResults = AnalysisFactory.eINSTANCE.createAnalysisResult();
        analysis.setResults(mockResults);

        // connection
        connection = ConnectionFactory.eINSTANCE.createDatabaseConnection();
        analysis.getContext().setConnection(connection);
        TaggedValueHelper.setTaggedValue(connection, TaggedValueHelper.DB_PRODUCT_NAME, TaggedValueHelper.DB_PRODUCT_NAME);

        // ------------------------- table A ------------------------------
        Catalog catalogA = orgomg.cwm.resource.relational.RelationalFactory.eINSTANCE.createCatalog();
        catalogA.setName("catalogA");

        Schema schemaA = orgomg.cwm.resource.relational.RelationalFactory.eINSTANCE.createSchema();
        schemaA.setName("schemaA");
        catalogA.getOwnedElement().add(schemaA);

        TdTable tableA = RelationalFactory.eINSTANCE.createTdTable();
        tableA.setName("tableA");
        schemaA.getOwnedElement().add(tableA);

        TdColumn tdColumnA = RelationalFactory.eINSTANCE.createTdColumn();
        tdColumnA.setName("columnA");
        tableA.getFeature().add(tdColumnA);

        // ------------------------- table B ------------------------------
        Catalog catalogB = orgomg.cwm.resource.relational.RelationalFactory.eINSTANCE.createCatalog();
        catalogB.setName("catalogB");

        Schema schemaB = orgomg.cwm.resource.relational.RelationalFactory.eINSTANCE.createSchema();
        schemaB.setName("schemaB");
        catalogB.getOwnedElement().add(schemaB);

        TdTable tableB = org.talend.cwm.relational.RelationalFactory.eINSTANCE.createTdTable();
        tableB.setName("tableB");
        schemaB.getOwnedElement().add(tableB);

        TdColumn tdColumnB = org.talend.cwm.relational.RelationalFactory.eINSTANCE.createTdColumn();
        tdColumnB.setName("columnB");
        tableB.getFeature().add(tdColumnB);

        // indicator
        RowMatchingIndicator rowMatchingIndicator = ColumnsetFactory.eINSTANCE.createRowMatchingIndicator();
        analysis.getResults().getIndicators().add(rowMatchingIndicator);
        TaggedValueHelper.setTaggedValue(analysis, TaggedValueHelper.PURPOSE, TaggedValueHelper.PURPOSE);
        TaggedValueHelper.setTaggedValue(analysis, TaggedValueHelper.DESCRIPTION, TaggedValueHelper.DESCRIPTION);

        rowMatchingIndicator.setAnalyzedElement(tableA);
        rowMatchingIndicator.getColumnSetA().add(tdColumnA);
        rowMatchingIndicator.getColumnSetB().add(tdColumnB);

        explorer = new RowMatchExplorer();
        entity = new ChartDataEntity();
        entity.setIndicator(rowMatchingIndicator);
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test method for {@link org.talend.dq.analysis.explore.RowMatchExplorer#getRowsMatchStatement()}.
     */
    @Test
    public void testGetRowsMatchStatement() {

        // test mysql
        TaggedValueHelper.setTaggedValue(connection, TaggedValueHelper.DB_PRODUCT_NAME,
                SupportDBUrlType.MYSQLDEFAULTURL.getLanguage());
        explorer.setAnalysis(analysis);
        explorer.setEnitty(entity);
        assertEquals(
                COMMENTS
                        + "-- Showing: View match rows ;\n"
                        + "SELECT * FROM `catalogA`.`schemaA`.`tableA` WHERE (`catalogA`.`schemaA`.`tableA`.`columnA` NOT IN (SELECT  A.`columnA` FROM  (SELECT * FROM `catalogA`.`schemaA`.`tableA`) A LEFT JOIN "
                        + " (SELECT * FROM `catalogB`.`schemaB`.`tableB`) B ON  (A.`columnA`= B.`columnB`)  WHERE  B.`columnB` IS NULL )) ",
                explorer.getRowsMatchStatement());

        // test oracle
        TaggedValueHelper.setTaggedValue(connection, TaggedValueHelper.DB_PRODUCT_NAME,
                SupportDBUrlType.ORACLEWITHSIDDEFAULTURL.getLanguage());
        explorer.setAnalysis(analysis);
        explorer.setEnitty(entity);
        assertEquals(
                COMMENTS
                        + "-- Showing: View match rows ;\n"
                        + "SELECT * FROM \"catalogA\".\"schemaA\".\"tableA\" WHERE (\"catalogA\".\"schemaA\".\"tableA\".\"columnA\" NOT IN (SELECT  A.\"columnA\" FROM  (SELECT * FROM \"catalogA\".\"schemaA\".\"tableA\") A LEFT JOIN "
                        + " (SELECT * FROM \"catalogB\".\"schemaB\".\"tableB\") B ON  (A.\"columnA\"= B.\"columnB\")  WHERE  B.\"columnB\" IS NULL )) ",
                explorer.getRowsMatchStatement());

        // test postgresql
        TaggedValueHelper.setTaggedValue(connection, TaggedValueHelper.DB_PRODUCT_NAME,
                SupportDBUrlType.POSTGRESQLEFAULTURL.getLanguage());
        explorer.setAnalysis(analysis);
        explorer.setEnitty(entity);
        assertEquals(
                COMMENTS
                        + "-- Showing: View match rows ;\n"
                        + "SELECT * FROM \"schemaA\".\"tableA\" WHERE (\"schemaA\".\"tableA\".\"columnA\" NOT IN (SELECT  A.\"columnA\" FROM  (SELECT * FROM \"schemaA\".\"tableA\") A LEFT JOIN "
                        + " (SELECT * FROM \"schemaB\".\"tableB\") B ON  (A.\"columnA\"= B.\"columnB\")  WHERE  B.\"columnB\" IS NULL )) ",
                explorer.getRowsMatchStatement());

        // test informix
        TaggedValueHelper.setTaggedValue(connection, TaggedValueHelper.DB_PRODUCT_NAME,
                SupportDBUrlType.INFORMIXDEFAULTURL.getLanguage());
        explorer.setAnalysis(analysis);
        explorer.setEnitty(entity);
        assertEquals(
                COMMENTS
                        + "-- Showing: View match rows ;\n"
                        + "SELECT * FROM catalogA:schemaA.tableA WHERE (catalogA:schemaA.tableA.columnA NOT IN (SELECT  A.columnA FROM  (SELECT * FROM catalogA:schemaA.tableA) A"
                        + " LEFT JOIN  (SELECT * FROM catalogB:schemaB.tableB) B ON  (A.columnA= B.columnB)  WHERE  B.columnB IS NULL )) ",
                explorer.getRowsMatchStatement());

    }

    /**
     * Test method for {@link org.talend.dq.analysis.explore.RowMatchExplorer#getRowsNotMatchStatement()}.
     */
    @Test
    public void testGetRowsNotMatchStatement() {

        // test mysql
        TaggedValueHelper.setTaggedValue(connection, TaggedValueHelper.DB_PRODUCT_NAME,
                SupportDBUrlType.MYSQLDEFAULTURL.getLanguage());
        explorer.setAnalysis(analysis);
        explorer.setEnitty(entity);
        assertEquals(
                COMMENTS
                        + "-- Showing: View not match rows ;\n"
                        + "SELECT A.* FROM  (SELECT * FROM `catalogA`.`schemaA`.`tableA`) A LEFT JOIN  (SELECT * FROM `catalogB`.`schemaB`.`tableB`) B ON  (A.`columnA`= B.`columnB`)  WHERE  B.`columnB` IS NULL ",
                explorer.getRowsNotMatchStatement());

        // test oracle
        TaggedValueHelper.setTaggedValue(connection, TaggedValueHelper.DB_PRODUCT_NAME,
                SupportDBUrlType.ORACLEWITHSIDDEFAULTURL.getLanguage());
        explorer.setAnalysis(analysis);
        explorer.setEnitty(entity);
        assertEquals(
                COMMENTS
                        + "-- Showing: View not match rows ;\n"
                        + "SELECT A.* FROM  (SELECT * FROM \"catalogA\".\"schemaA\".\"tableA\") A LEFT JOIN  (SELECT * FROM \"catalogB\".\"schemaB\".\"tableB\") B ON  (A.\"columnA\"= B.\"columnB\")  WHERE  B.\"columnB\" IS NULL ",
                explorer.getRowsNotMatchStatement());

        // test postgresql
        TaggedValueHelper.setTaggedValue(connection, TaggedValueHelper.DB_PRODUCT_NAME,
                SupportDBUrlType.POSTGRESQLEFAULTURL.getLanguage());
        explorer.setAnalysis(analysis);
        explorer.setEnitty(entity);
        assertEquals(
                COMMENTS
                        + "-- Showing: View not match rows ;\n"
                        + "SELECT A.* FROM  (SELECT * FROM \"schemaA\".\"tableA\") A "
                        + "LEFT JOIN  (SELECT * FROM \"schemaB\".\"tableB\") B ON  (A.\"columnA\"= B.\"columnB\")  WHERE  B.\"columnB\" IS NULL ",
                explorer.getRowsNotMatchStatement());

        // test informix
        TaggedValueHelper.setTaggedValue(connection, TaggedValueHelper.DB_PRODUCT_NAME,
                SupportDBUrlType.INFORMIXDEFAULTURL.getLanguage());
        explorer.setAnalysis(analysis);
        explorer.setEnitty(entity);
        assertEquals(COMMENTS + "-- Showing: View not match rows ;\n"
                + "SELECT A.* FROM  (SELECT * FROM catalogA:schemaA.tableA) A"
                + " LEFT JOIN  (SELECT * FROM catalogB:schemaB.tableB) B ON  (A.columnA= B.columnB)  WHERE  B.columnB IS NULL ",
                explorer.getRowsNotMatchStatement());

    }

}
