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
package org.talend.dq.analysis;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.metadata.builder.database.DqRepositoryViewService;
import org.talend.cwm.helper.ConnectionHelper;
import org.talend.cwm.relational.TdColumn;
import org.talend.cwm.relational.TdTable;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.analysis.AnalysisType;
import org.talend.dataquality.helpers.AnalysisHelper;
import org.talend.dataquality.helpers.MetadataHelper;
import org.talend.dataquality.indicators.DataminingType;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.IndicatorsFactory;
import org.talend.dataquality.indicators.LowerQuartileIndicator;
import org.talend.dataquality.indicators.UpperQuartileIndicator;
import org.talend.dataquality.indicators.definition.IndicatorDefinition;
import org.talend.dq.helper.UnitTestBuildHelper;
import org.talend.dq.indicators.definitions.DefinitionHandler;
import org.talend.utils.sql.Java2SqlType;
import org.talend.utils.sugars.ReturnCode;
import orgomg.cwm.resource.relational.Schema;

/**
 * current worked DB2: 192.168.31.135 , used table: "DB2INST1"."INVENTORY";
 * 
 * Used column: QUANTITY, its values: 44,45,null,null,null,36,39, 40,41,47,49.
 * 
 * To use this database in the junit, to test several cases:
 * 
 * 1) without where clause(sql mode): the value of lower quantile is:40, upper: 47 java mode result: 39.5,46
 * 
 * 2) with where clause: LOCATION like 'a%' : (sql mode) result is: lower :39, upper: 47 equals with java mode's result.
 */
public class AnalysisRealExecutorForDB2Test {

    private static Connection db2Connection = null;

    private static List<TdColumn> columns = null;

    private static final String CATALOG = "DB2INST1"; //$NON-NLS-1$

    private static final String TABLE = "INVENTORY"; //$NON-NLS-1$

    private static final String COLUMN = "QUANTITY"; //$NON-NLS-1$

    private static final String WHERE_CLAUSE = "LOCATION like 'a%'";

    /**
     * DOC yyin Comment method "setUpBeforeClass".
     * 
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        db2Connection = UnitTestBuildHelper.getDefault().getDB2DataManager();

        columns = getColumns(db2Connection);
    }

    /**
     * DOC yyin Comment method "tearDownAfterClass".
     * 
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {

    }

    @Test
    public void testLowerIndicatorWithDB2WithoutWhereClause() {
        Indicator lowerIndicator = this.createLowerIndicator();
        String analysisName = "analysis_db2_lower"; //$NON-NLS-1$

        Analysis analysis = createAnalysis(lowerIndicator, analysisName);

        IAnalysisExecutor exec = new ColumnAnalysisSqlExecutor();
        ReturnCode executed = exec.execute(analysis);
        Assert.assertTrue("Executing analysis: analysis_db2_lower: " + executed.getMessage(), executed.isOk()); //$NON-NLS-1$ 

        // result of lower quantile
        EList<Indicator> indicators = analysis.getResults().getIndicators();
        for (Indicator indicator : indicators) {
            Assert.assertEquals("40.0", ((LowerQuartileIndicator) indicator).getValue());
        }

        exec = new ColumnAnalysisExecutor();
        executed = exec.execute(analysis);
        Assert.assertTrue("Executing analysis: analysis_db2_lower: " + executed.getMessage(), executed.isOk()); //$NON-NLS-1$ 

        // result of lower quantile
        indicators = analysis.getResults().getIndicators();
        for (Indicator indicator : indicators) {
            Assert.assertEquals("39.5", ((LowerQuartileIndicator) indicator).getValue());
        }
    }

    @Test
    public void testUpperIndicatorWithDB2WithoutWhereClause() {
        Indicator upperIndicator = this.createUpperIndicator();
        String analysisName = "analysis_db2_upper"; //$NON-NLS-1$

        Analysis analysis = createAnalysis(upperIndicator, analysisName);

        // run with sql mode
        IAnalysisExecutor exec = new ColumnAnalysisSqlExecutor();
        ReturnCode executed = exec.execute(analysis);
        Assert.assertTrue("Executing analysis: analysis_db2_lower: " + executed.getMessage(), executed.isOk()); //$NON-NLS-1$ 

        // result of upper quantile
        EList<Indicator> indicators = analysis.getResults().getIndicators();
        for (Indicator indicator : indicators) {
            Assert.assertEquals("47.0", ((UpperQuartileIndicator) indicator).getValue());
        }

        // run with java mode
        exec = new ColumnAnalysisExecutor();
        executed = exec.execute(analysis);
        Assert.assertTrue("Executing analysis: analysis_db2_lower: " + executed.getMessage(), executed.isOk()); //$NON-NLS-1$ 

        // result of upper quantile
        indicators = analysis.getResults().getIndicators();
        for (Indicator indicator : indicators) {
            Assert.assertEquals("46.0", ((UpperQuartileIndicator) indicator).getValue());
        }
    }

    @Test
    public void testLowerIndicatorWithDB2WithWhereClause() {
        Indicator lowerIndicator = this.createLowerIndicator();
        String analysisName = "analysis_db2_lower_with_where"; //$NON-NLS-1$

        Analysis analysis = createAnalysis(lowerIndicator, analysisName);
        // add where clause
        AnalysisHelper.setStringDataFilter(analysis, WHERE_CLAUSE);

        IAnalysisExecutor exec = new ColumnAnalysisSqlExecutor();
        ReturnCode executed = exec.execute(analysis);
        Assert.assertTrue(executed.isOk());

        // result of lower quantile
        EList<Indicator> indicators = analysis.getResults().getIndicators();
        for (Indicator indicator : indicators) {
            Assert.assertEquals("39.0", ((LowerQuartileIndicator) indicator).getValue());
        }

        exec = new ColumnAnalysisExecutor();
        executed = exec.execute(analysis);
        Assert.assertTrue("Executing analysis: analysis_db2_lower: " + executed.getMessage(), executed.isOk()); //$NON-NLS-1$ 

        // result of lower quantile
        indicators = analysis.getResults().getIndicators();
        for (Indicator indicator : indicators) {
            Assert.assertEquals("39.0", ((LowerQuartileIndicator) indicator).getValue());
        }
    }

    @Test
    public void testUpperIndicatorWithDB2WithWhereClause() {
        Indicator upperIndicator = this.createUpperIndicator();
        String analysisName = "analysis_db2_upper"; //$NON-NLS-1$

        Analysis analysis = createAnalysis(upperIndicator, analysisName);
        // add where clause
        AnalysisHelper.setStringDataFilter(analysis, WHERE_CLAUSE);

        // run with sql mode
        IAnalysisExecutor exec = new ColumnAnalysisSqlExecutor();
        ReturnCode executed = exec.execute(analysis);
        Assert.assertTrue("Executing analysis: analysis_db2_lower: " + executed.getMessage(), executed.isOk()); //$NON-NLS-1$ 

        // result of upper quantile
        EList<Indicator> indicators = analysis.getResults().getIndicators();
        for (Indicator indicator : indicators) {
            Assert.assertEquals("47.0", ((UpperQuartileIndicator) indicator).getValue());
        }

        // run with java mode
        exec = new ColumnAnalysisExecutor();
        executed = exec.execute(analysis);
        Assert.assertTrue("Executing analysis: analysis_db2_lower: " + executed.getMessage(), executed.isOk()); //$NON-NLS-1$ 

        // result of upper quantile
        indicators = analysis.getResults().getIndicators();
        for (Indicator indicator : indicators) {
            Assert.assertEquals("47.0", ((UpperQuartileIndicator) indicator).getValue());
        }
    }

    private Analysis createAnalysis(Indicator upperIndicator, String analysisName) {
        AnalysisBuilder analysisBuilder = new AnalysisBuilder();

        boolean analysisInitialized = analysisBuilder.initializeAnalysis(analysisName, AnalysisType.MULTIPLE_COLUMN);
        Assert.assertTrue(analysisName + " initialize!", analysisInitialized); //$NON-NLS-1$

        analysisBuilder.setAnalysisConnection(db2Connection);

        for (TdColumn tdColumn : columns) {
            upperIndicator.setAnalyzedElement(tdColumn);
            analysisBuilder.addElementToAnalyze(tdColumn, upperIndicator);
        }

        return analysisBuilder.getAnalysis();
    }

    private static List<TdColumn> getColumns(Connection dataManager) throws Exception {
        Set<Schema> schemas = ConnectionHelper.getAllSchemas(dataManager);
        Schema schema = null;
        for (Schema tdSchema : schemas) {
            if (CATALOG.equals(tdSchema.getName())) {
                schema = tdSchema;
                break;
            }
        }
        Assert.assertNotNull(schema);
        Assert.assertFalse(schemas.isEmpty());

        List<TdTable> tables = DqRepositoryViewService.getTables(dataManager, schema, TABLE, true, false);
        Assert.assertEquals(1, tables.size());

        TdTable tdTable = tables.get(0);
        List<TdColumn> columns = DqRepositoryViewService.getColumns(dataManager, tdTable, null, true);

        Assert.assertFalse(columns.isEmpty());

        List<TdColumn> usedCols = new ArrayList<TdColumn>();
        for (TdColumn tdColumn : columns) {
            if (tdColumn.getName().equals(COLUMN)) {
                usedCols.add(tdColumn);
            }
        }
        // set DM type for each used column
        for (TdColumn tdColumn : usedCols) {
            final int javaType = tdColumn.getJavaType();
            if (Java2SqlType.isNumbericInSQL(javaType)) {
                MetadataHelper.setDataminingType(DataminingType.INTERVAL, tdColumn);
            }
        }
        return usedCols;
    }

    private Indicator createLowerIndicator() {
        LowerQuartileIndicator indicator = IndicatorsFactory.eINSTANCE.createLowerQuartileIndicator();
        IndicatorDefinition indiDefinition = DefinitionHandler.getInstance().getIndicatorDefinition("Lower Quartile");
        indicator.setIndicatorDefinition(indiDefinition);
        return indicator;
    }

    private Indicator createUpperIndicator() {
        UpperQuartileIndicator indicator = IndicatorsFactory.eINSTANCE.createUpperQuartileIndicator();
        IndicatorDefinition indiDefinition = DefinitionHandler.getInstance().getIndicatorDefinition("Upper Quartile");
        indicator.setIndicatorDefinition(indiDefinition);
        return indicator;
    }
}
