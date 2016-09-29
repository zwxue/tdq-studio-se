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
package org.talend.dq.helper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.talend.commons.emf.EMFUtil;
import org.talend.commons.emf.EmfFileResourceUtil;
import org.talend.core.model.metadata.builder.database.dburl.SupportDBUrlType;
import org.talend.core.model.properties.Property;
import org.talend.cwm.relational.RelationalFactory;
import org.talend.cwm.relational.TdColumn;
import org.talend.cwm.relational.TdTable;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.PatternMatchingIndicator;
import org.talend.dataquality.properties.TDQAnalysisItem;
import org.talend.dq.CWMPlugin;
import org.talend.dq.dbms.DbmsLanguage;
import org.talend.dq.dbms.DbmsLanguageFactory;
import org.talend.utils.sugars.ReturnCode;
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
        // Load analysis item/property model from test file.
        String anaPropertyFile = "data/builtin/pattern_with_dep/TDQ_Data_Profiling/Analyses/patternMatchAna_0.1.properties"; //$NON-NLS-1$
        Resource anaPropertyResource = getResource(anaPropertyFile);
        Analysis ana = null;
        Property anaProperty = null;
        while (anaPropertyResource.getAllContents().hasNext()) {
            EObject eobj = anaPropertyResource.getAllContents().next();
            if (eobj instanceof Property) {
                anaProperty = (Property) eobj;
                ana = ((TDQAnalysisItem) anaProperty.getItem()).getAnalysis();
                break;
            }
        }
        assertTrue(ana.getResults().getIndicators().get(0).getParameters().getDataValidDomain().getBuiltInPatterns().size() == 0);

        ReturnCode rc = AnalysisExecutorHelper.check(ana);
        assertTrue(rc.isOk());
        assertTrue(ana.getResults().getIndicators().get(0).getParameters().getDataValidDomain().getBuiltInPatterns().size() > 0);
        ana.getResults().getIndicators().get(0).getParameters().getDataValidDomain().getBuiltInPatterns().clear();
        EMFUtil.saveResource(ana.eResource());
    }

    /**
     * DOC yyin Comment method "getResource".
     * 
     * @param anaPropertyFile
     * @return
     * @throws URISyntaxException
     * @throws IOException
     */
    private Resource getResource(String anaPropertyFile) {
        URL fileUrl = CWMPlugin.getDefault().getBundle().getResource(anaPropertyFile);
        File propertyFile;
        try {
            propertyFile = new File(FileLocator.toFileURL(fileUrl).toURI().getPath().toString());
            return EmfFileResourceUtil.getInstance().getFileResource(propertyFile.getAbsolutePath());
        } catch (URISyntaxException e) {
            fail("Can not find the file: " + anaPropertyFile);
            e.printStackTrace();
        } catch (IOException e) {
            fail("Can not find the file: " + anaPropertyFile);
            e.printStackTrace();
        } catch (NullPointerException e) {
            fail("Can not find the file: " + anaPropertyFile);
            e.printStackTrace();
        }
        return null;
    }

    @Test
    public void testCheckPatternWithOutDependencyFiles() {
        // Load analysis item/property model from test file.
        String anaPropertyFile = "data/builtin/pattern_without_dep/TDQ_Data_Profiling/Analyses/patternMatchAna_0.1.properties"; //$NON-NLS-1$
        Resource anaPropertyResource = getResource(anaPropertyFile);
        Analysis ana = null;
        Property anaProperty = null;
        while (anaPropertyResource.getAllContents().hasNext()) {
            EObject eobj = anaPropertyResource.getAllContents().next();
            if (eobj instanceof Property) {
                anaProperty = (Property) eobj;
                ana = ((TDQAnalysisItem) anaProperty.getItem()).getAnalysis();
                break;
            }
        }
        assertTrue(ana.getResults().getIndicators().get(0).getParameters().getDataValidDomain().getPatterns().get(0).getName() == null);

        ReturnCode rc = AnalysisExecutorHelper.check(ana);
        assertTrue(rc.isOk());
        assertTrue(ana.getResults().getIndicators().get(0).getParameters().getDataValidDomain().getPatterns().get(0).getName() == null);
    }

    @Test
    public void testCheckPatternWithOutDependencyFilesAndBuiltIn() {
        // Load analysis item/property model from test file.
        String anaPropertyFile = "data/builtin/pattern_without_dep_builtin/TDQ_Data_Profiling/Analyses/patternMatchAna_0.1.properties"; //$NON-NLS-1$
        Resource anaPropertyResource = getResource(anaPropertyFile);
        Analysis ana = null;
        Property anaProperty = null;
        while (anaPropertyResource.getAllContents().hasNext()) {
            EObject eobj = anaPropertyResource.getAllContents().next();
            if (eobj instanceof Property) {
                anaProperty = (Property) eobj;
                ana = ((TDQAnalysisItem) anaProperty.getItem()).getAnalysis();
                break;
            }
        }
        assertTrue(ana.getResults().getIndicators().get(0).getParameters().getDataValidDomain().getPatterns().get(0).getName() == null);

        ReturnCode rc = AnalysisExecutorHelper.check(ana);
        assertFalse(rc.isOk());
        assertTrue(ana.getResults().getIndicators().get(0).getParameters().getDataValidDomain().getPatterns().get(0).getName() == null);
    }

    @Test
    public void testCheckIndicatorWithDependencyFiles() {
        // Load analysis item/property model from test file.
        String anaPropertyFile = "data/builtin/indicator_with_dep/TDQ_Data_Profiling/Analyses/patternMatchAna_0.1.properties"; //$NON-NLS-1$
        Resource anaPropertyResource = getResource(anaPropertyFile);
        Analysis ana = null;
        Property anaProperty = null;
        while (anaPropertyResource.getAllContents().hasNext()) {
            EObject eobj = anaPropertyResource.getAllContents().next();
            if (eobj instanceof Property) {
                anaProperty = (Property) eobj;
                ana = ((TDQAnalysisItem) anaProperty.getItem()).getAnalysis();
                break;
            }
        }

        for (Indicator indicator : ana.getResults().getIndicators()) {
            if (!(indicator instanceof PatternMatchingIndicator)) {
                // Check system indicator and UDI
                assertTrue(indicator.getIndicatorDefinition() != null);
                assertTrue(indicator.getBuiltInIndicatorDefinition() == null);
            }
        }
        ReturnCode rc = AnalysisExecutorHelper.check(ana);
        assertTrue(rc.isOk());

        for (Indicator indicator : ana.getResults().getIndicators()) {
            if (!(indicator instanceof PatternMatchingIndicator)) {
                assertTrue(indicator.getBuiltInIndicatorDefinition() != null);
                indicator.setBuiltInIndicatorDefinition(null);
            } else {
                indicator.getParameters().getDataValidDomain().getBuiltInPatterns().clear();
            }
        }
        EMFUtil.saveResource(ana.eResource());
    }

    @Test
    public void testCheckIndicatorWithOutDependencyFilesAndBuiltIn() {
        // Load analysis item/property model from test file.
        String anaPropertyFile = "data/builtin/indicator_without_dep_builtin/TDQ_Data_Profiling/Analyses/patternMatchAna_0.1.properties"; //$NON-NLS-1$
        Resource anaPropertyResource = getResource(anaPropertyFile);
        Analysis ana = null;
        Property anaProperty = null;
        while (anaPropertyResource.getAllContents().hasNext()) {
            EObject eobj = anaPropertyResource.getAllContents().next();
            if (eobj instanceof Property) {
                anaProperty = (Property) eobj;
                ana = ((TDQAnalysisItem) anaProperty.getItem()).getAnalysis();
                break;
            }
        }
        for (Indicator indicator : ana.getResults().getIndicators()) {
            if (!(indicator instanceof PatternMatchingIndicator)) {
                // Check system indicator and UDI
                assertTrue(indicator.getIndicatorDefinition().getName() == null);
                assertTrue(indicator.getBuiltInIndicatorDefinition() == null);
            }
        }
        ReturnCode rc = AnalysisExecutorHelper.check(ana);
        assertFalse(rc.isOk());
    }

    @Test
    public void testCheckIndicatorWithOutDependencyFiles() {
        // Load analysis item/property model from test file.
        String anaPropertyFile = "data/builtin/indicator_without_dep/TDQ_Data_Profiling/Analyses/patternMatchAna_0.1.properties"; //$NON-NLS-1$
        Resource anaPropertyResource = getResource(anaPropertyFile);
        Analysis ana = null;
        Property anaProperty = null;
        while (anaPropertyResource.getAllContents().hasNext()) {
            EObject eobj = anaPropertyResource.getAllContents().next();
            if (eobj instanceof Property) {
                anaProperty = (Property) eobj;
                ana = ((TDQAnalysisItem) anaProperty.getItem()).getAnalysis();
                break;
            }
        }
        for (Indicator indicator : ana.getResults().getIndicators()) {
            if (!(indicator instanceof PatternMatchingIndicator)) {
                // Check system indicator and UDI
                assertTrue(indicator.getIndicatorDefinition().getName() != null);
                assertTrue(indicator.getBuiltInIndicatorDefinition() != null);
                assertTrue(indicator.getBuiltInIndicatorDefinition() == indicator.getIndicatorDefinition());
            }
        }
        ReturnCode rc = AnalysisExecutorHelper.check(ana);
        assertTrue(rc.isOk());
        for (Indicator indicator : ana.getResults().getIndicators()) {
            if (!(indicator instanceof PatternMatchingIndicator)) {
                // Check system indicator and UDI
                assertTrue(indicator.getIndicatorDefinition().getName() != null);
                assertTrue(indicator.getBuiltInIndicatorDefinition() != null);
                assertTrue(indicator.getBuiltInIndicatorDefinition() == indicator.getIndicatorDefinition());
            }
        }

    }

    @Test
    public void testCheckRuleWithDependencyFiles() {
        // Load analysis item/property model from test file.
        String anaPropertyFile = "data/builtin/rule_with_dep/TDQ_Data_Profiling/Analyses/matchRuleAna_0.1.properties"; //$NON-NLS-1$
        Resource anaPropertyResource = getResource(anaPropertyFile);
        Analysis ana = null;
        Property anaProperty = null;
        while (anaPropertyResource.getAllContents().hasNext()) {
            EObject eobj = anaPropertyResource.getAllContents().next();
            if (eobj instanceof Property) {
                anaProperty = (Property) eobj;
                ana = ((TDQAnalysisItem) anaProperty.getItem()).getAnalysis();
                break;
            }
        }

        for (Indicator indicator : ana.getResults().getIndicators()) {
            if (!(indicator instanceof PatternMatchingIndicator)) {
                // Check system indicator and UDI
                assertTrue(indicator.getIndicatorDefinition() != null);
                assertTrue(indicator.getBuiltInIndicatorDefinition() == null);
            }
        }
        ReturnCode rc = AnalysisExecutorHelper.check(ana);
        assertTrue(rc.isOk());

        for (Indicator indicator : ana.getResults().getIndicators()) {
            if (!(indicator instanceof PatternMatchingIndicator)) {
                assertTrue(indicator.getBuiltInIndicatorDefinition() != null);
                indicator.setBuiltInIndicatorDefinition(null);
            } else {
                indicator.getParameters().getDataValidDomain().getBuiltInPatterns().clear();
            }
        }
        EMFUtil.saveResource(ana.eResource());
    }
}
