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
package org.talend.dq.helper;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.talend.core.model.metadata.builder.database.dburl.SupportDBUrlType;
import org.talend.cwm.relational.RelationalFactory;
import org.talend.cwm.relational.TdColumn;
import org.talend.cwm.relational.TdTable;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.analysis.AnalysisContext;
import org.talend.dataquality.analysis.AnalysisPackage;
import org.talend.dataquality.analysis.AnalysisResult;
import org.talend.dq.dbms.DbmsLanguage;
import org.talend.dq.dbms.DbmsLanguageFactory;
import org.talend.utils.sugars.ReturnCode;
import orgomg.cwm.foundation.softwaredeployment.DataProvider;
import orgomg.cwm.foundation.softwaredeployment.SoftwaredeploymentPackage;
import orgomg.cwm.objectmodel.core.ModelElement;
import orgomg.cwm.objectmodel.core.Package;

/**
 * DOC yyin class global comment. Detailled comment
 */
public class AnalysisExecutorHelperTest {

    /**
     * DOC yyin Comment method "setUp".
     * 
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }

    /**
     * DOC yyin Comment method "tearDown".
     * 
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    /**
     * try the MSSQL db type. Test method for
     * {@link org.talend.dq.helper.AnalysisExecutorHelper#getTableName(orgomg.cwm.objectmodel.core.ModelElement, org.talend.dq.dbms.DbmsLanguage)}
     * .
     */
    @Test
    public void testGetTableName_1() {
        TdColumn tdColumn = RelationalFactory.eINSTANCE.createTdColumn();

        TdTable tdTable = RelationalFactory.eINSTANCE.createTdTable();
        tdTable.setName("tableName"); //$NON-NLS-1$
        tdColumn.setOwner(tdTable);
        tdColumn.setName("columnName"); //$NON-NLS-1$

        DbmsLanguage dbmsLanguage = DbmsLanguageFactory.createDbmsLanguage(SupportDBUrlType.MSSQLDEFAULTURL.getLanguage(), null);

        Package schema = orgomg.cwm.resource.relational.RelationalFactory.eINSTANCE.createSchema();// mock(Schema.class);
        schema.setName("schemaName"); //$NON-NLS-1$
        tdTable.setNamespace(schema);

        Package catalog = orgomg.cwm.resource.relational.RelationalFactory.eINSTANCE.createCatalog();// mock(Catalog.class);
        catalog.setName("catalogName"); //$NON-NLS-1$
        schema.setNamespace(catalog);

        assertEquals("catalogName.schemaName.tableName", AnalysisExecutorHelper.getTableName(tdColumn, dbmsLanguage)); //$NON-NLS-1$

    }

    /**
     * try the oracle db type. Test method for
     * {@link org.talend.dq.helper.AnalysisExecutorHelper#getTableName(orgomg.cwm.objectmodel.core.ModelElement, org.talend.dq.dbms.DbmsLanguage)}
     * .
     */
    @Test
    public void testGetTableName_2() {
        TdColumn tdColumn = RelationalFactory.eINSTANCE.createTdColumn();

        TdTable tdTable = RelationalFactory.eINSTANCE.createTdTable();
        tdTable.setName("tableName"); //$NON-NLS-1$
        tdColumn.setOwner(tdTable);
        tdColumn.setName("columnName"); //$NON-NLS-1$

        DbmsLanguage dbmsLanguage = DbmsLanguageFactory.createDbmsLanguage(
                SupportDBUrlType.ORACLEWITHSIDDEFAULTURL.getLanguage(), null);

        Package schema = orgomg.cwm.resource.relational.RelationalFactory.eINSTANCE.createSchema();// mock(Schema.class);
        schema.setName("schemaName"); //$NON-NLS-1$
        tdTable.setNamespace(schema);

        assertEquals("\"schemaName\".\"tableName\"", AnalysisExecutorHelper.getTableName(tdColumn, dbmsLanguage)); //$NON-NLS-1$

    }

    /**
     * try the sybase db type. Test method for
     * {@link org.talend.dq.helper.AnalysisExecutorHelper#getTableName(orgomg.cwm.objectmodel.core.ModelElement, org.talend.dq.dbms.DbmsLanguage)}
     * .
     */
    @Test
    public void testGetTableName_3() {
        TdColumn tdColumn = RelationalFactory.eINSTANCE.createTdColumn();

        TdTable tdTable = RelationalFactory.eINSTANCE.createTdTable();
        tdTable.setName("tableName"); //$NON-NLS-1$
        tdColumn.setOwner(tdTable);
        tdColumn.setName("columnName"); //$NON-NLS-1$

        DbmsLanguage dbmsLanguage = DbmsLanguageFactory.createDbmsLanguage(SupportDBUrlType.SYBASEDEFAULTURL.getLanguage(), null);

        Package schema = orgomg.cwm.resource.relational.RelationalFactory.eINSTANCE.createSchema();// mock(Schema.class);
        schema.setName("schemaName");
        tdTable.setNamespace(schema);

        Package catalog = orgomg.cwm.resource.relational.RelationalFactory.eINSTANCE.createCatalog();// mock(Catalog.class);
        catalog.setName("catalogName"); //$NON-NLS-1$
        schema.setNamespace(catalog);

        assertEquals("catalogName.schemaName.tableName", AnalysisExecutorHelper.getTableName(tdColumn, dbmsLanguage)); //$NON-NLS-1$

    }

    /**
     * try the db2 db type. Test method for
     * {@link org.talend.dq.helper.AnalysisExecutorHelper#getTableName(orgomg.cwm.objectmodel.core.ModelElement, org.talend.dq.dbms.DbmsLanguage)}
     * .
     */
    @Test
    public void testGetTableName_4() {
        TdColumn tdColumn = RelationalFactory.eINSTANCE.createTdColumn();

        TdTable tdTable = RelationalFactory.eINSTANCE.createTdTable();
        tdTable.setName("tableName"); //$NON-NLS-1$
        tdColumn.setOwner(tdTable);
        tdColumn.setName("columnName"); //$NON-NLS-1$

        DbmsLanguage dbmsLanguage = DbmsLanguageFactory.createDbmsLanguage(SupportDBUrlType.DB2DEFAULTURL.getLanguage(), null);

        Package catalog = orgomg.cwm.resource.relational.RelationalFactory.eINSTANCE.createCatalog();// mock(Catalog.class);
        catalog.setName("catalogName"); //$NON-NLS-1$
        tdTable.setNamespace(catalog);

        assertEquals("catalogName.tableName", AnalysisExecutorHelper.getTableName(tdColumn, dbmsLanguage)); //$NON-NLS-1$

    }

    /**
     * try the mysql db type. Test method for
     * {@link org.talend.dq.helper.AnalysisExecutorHelper#getTableName(orgomg.cwm.objectmodel.core.ModelElement, org.talend.dq.dbms.DbmsLanguage)}
     * .
     */
    @Test
    public void testGetTableName_5() {
        TdColumn tdColumn = RelationalFactory.eINSTANCE.createTdColumn();

        TdTable tdTable = RelationalFactory.eINSTANCE.createTdTable();
        tdTable.setName("tableName"); //$NON-NLS-1$
        tdColumn.setOwner(tdTable);
        tdColumn.setName("columnName"); //$NON-NLS-1$

        DbmsLanguage dbmsLanguage = DbmsLanguageFactory.createDbmsLanguage(SupportDBUrlType.MYSQLDEFAULTURL.getLanguage(), null);

        Package catalog = orgomg.cwm.resource.relational.RelationalFactory.eINSTANCE.createCatalog();// mock(Catalog.class);
        catalog.setName("catalogName"); //$NON-NLS-1$
        tdTable.setNamespace(catalog);

        assertEquals("`catalogName`.`tableName`", AnalysisExecutorHelper.getTableName(tdColumn, dbmsLanguage)); //$NON-NLS-1$

    }

    @Test
    public void testCheckPatternDependencyFiles() {
        Analysis ana = AnalysisPackage.eINSTANCE.getAnalysisFactory().createAnalysis();
        ReturnCode rc = AnalysisExecutorHelper.check(ana);
        assertFalse(rc.isOk());

        // Set analysis context.
        AnalysisContext context = AnalysisPackage.eINSTANCE.getAnalysisFactory().createAnalysisContext();
        ana.setContext(context);
        rc = AnalysisExecutorHelper.check(ana);
        assertFalse(rc.isOk());

        // Set analyzed elements
        List<ModelElement> analyzedElements = context.getAnalysedElements();
        TdColumn tdColumn = RelationalFactory.eINSTANCE.createTdColumn();
        TdTable tdTable = RelationalFactory.eINSTANCE.createTdTable();
        tdTable.setName("tableWithOneColumn"); //$NON-NLS-1$
        tdColumn.setOwner(tdTable);
        tdColumn.setName("strTypeColumn"); //$NON-NLS-1$
        analyzedElements.add(tdColumn);
        rc = AnalysisExecutorHelper.check(ana);
        assertFalse(rc.isOk());

        // Set connection
        DataProvider dp = SoftwaredeploymentPackage.eINSTANCE.getSoftwaredeploymentFactory().createDataProvider();
        context.setConnection(dp);
        rc = AnalysisExecutorHelper.check(ana);
        assertFalse(rc.isOk());

        // Set pattern matching indicators
        AnalysisResult results = AnalysisPackage.eINSTANCE.getAnalysisFactory().createAnalysisResult();
        ana.setResults(results);
        rc = AnalysisExecutorHelper.check(ana);
        assertFalse(rc.isOk());
    }
}
