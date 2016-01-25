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
package org.talend.dq.indicators.definitions;

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
import org.talend.dataquality.helpers.MetadataHelper;
import org.talend.dataquality.indicators.DataminingType;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.IndicatorsFactory;
import org.talend.dataquality.indicators.LowerQuartileIndicator;
import org.talend.dataquality.indicators.UpperQuartileIndicator;
import org.talend.dataquality.indicators.definition.IndicatorDefinition;
import org.talend.dq.analysis.AnalysisBuilder;
import org.talend.dq.analysis.ColumnAnalysisExecutor;
import org.talend.dq.analysis.ColumnAnalysisSqlExecutor;
import org.talend.dq.analysis.IAnalysisExecutor;
import org.talend.dq.helper.UnitTestBuildHelper;
import org.talend.utils.sql.Java2SqlType;
import org.talend.utils.sugars.ReturnCode;
import orgomg.cwm.resource.relational.Schema;

/**
 * current worked DB2: 192.168.31.135 , used table: "DB2INST1"."TESTFORLOWERQUANTITL";
 * 
 * Test for 4 scenarios:
 * 
 * 1)Used column: SCENARIO1
 * 
 * odd number of data(same as java mode) Ordered Data Set: 6, 7, 15, 36, 39, 40, 41, 42, 43, 47, 49
 * 
 * lower=15(index=3), upper = 43(index=9)
 * 
 * 2)Used column: SCENARIO2
 * 
 * even number of data(same as java mode) Ordered Data Set: 7, 15, 36, 39, 40, 41, 42, 43, 47, 49
 * 
 * lower=36(index=3), upper = 43(index=8)
 * 
 * 3)Used column: SCENARIO3
 * 
 * odd number of data(same as java mode) Ordered Data Set: 15, 36, 39, 40, 41, 42, 43, 47, 49
 * 
 * lower=39(index=3), upper = 43(index=7)
 * 
 * 4)Used column: SCENARIO4
 * 
 * even number of data(java mode : 39.5, 45) Ordered Data Set: 36, 39, 40, 41, 42, 43, 47, 49
 * 
 * lower=40(index=3), upper = 47(index=7)
 */
public class LowerUpperQuantileForDB2Test {

    private static Connection db2Connection = null;

    private static List<TdColumn> columns = null;

    private static final String CATALOG = "DB2INST1"; //$NON-NLS-1$

    private static final String TABLE = "TESTFORLOWERQUANTITL"; //$NON-NLS-1$

    private static final String COLUMN = "SCENARIO"; //$NON-NLS-1$

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
    public void testLowerUpperIndicatorWithDB2Scenario_1() {
        Indicator lowerIndicator = this.createLowerIndicator();
        Indicator upperIndicator = this.createUpperIndicator();
        String analysisName = "analysis_db2_1"; //$NON-NLS-1$

        // use column: SCENARIO1
        Analysis analysis = createAnalysis(lowerIndicator, upperIndicator, analysisName, 0);

        IAnalysisExecutor exec = new ColumnAnalysisSqlExecutor();
        ReturnCode executed = exec.execute(analysis);
        Assert.assertTrue("Executing analysis: analysis_db2_1: " + executed.getMessage(), executed.isOk()); //$NON-NLS-1$ 

        // result of lower quantile
        EList<Indicator> indicators = analysis.getResults().getIndicators();
        for (Indicator indicator : indicators) {
            if (indicator instanceof LowerQuartileIndicator) {
                Assert.assertEquals("15.0", ((LowerQuartileIndicator) indicator).getValue());
            } else if (indicator instanceof UpperQuartileIndicator) {
                Assert.assertEquals("43.0", ((UpperQuartileIndicator) indicator).getValue());
            }
        }

        exec = new ColumnAnalysisExecutor();
        executed = exec.execute(analysis);
        Assert.assertTrue("Executing analysis: analysis_db2_1: " + executed.getMessage(), executed.isOk()); //$NON-NLS-1$ 

        // result of lower quantile
        indicators = analysis.getResults().getIndicators();
        for (Indicator indicator : indicators) {
            if (indicator instanceof LowerQuartileIndicator) {
                Assert.assertEquals("15.0", ((LowerQuartileIndicator) indicator).getValue());
            } else if (indicator instanceof UpperQuartileIndicator) {
                Assert.assertEquals("43.0", ((UpperQuartileIndicator) indicator).getValue());
            }
        }
    }

    @Test
    public void testLowerUpperIndicatorWithDB2Scenario_2() {
        Indicator lowerIndicator = this.createLowerIndicator();
        Indicator upperIndicator = this.createUpperIndicator();
        String analysisName = "analysis_db2_2"; //$NON-NLS-1$

        // use column: SCENARIO2
        Analysis analysis = createAnalysis(lowerIndicator, upperIndicator, analysisName, 1);

        IAnalysisExecutor exec = new ColumnAnalysisSqlExecutor();
        ReturnCode executed = exec.execute(analysis);
        Assert.assertTrue("Executing analysis: analysis_db2_2: " + executed.getMessage(), executed.isOk()); //$NON-NLS-1$ 

        // result of lower quantile
        EList<Indicator> indicators = analysis.getResults().getIndicators();
        for (Indicator indicator : indicators) {
            if (indicator instanceof LowerQuartileIndicator) {
                Assert.assertEquals("36.0", ((LowerQuartileIndicator) indicator).getValue());
            } else if (indicator instanceof UpperQuartileIndicator) {
                Assert.assertEquals("43.0", ((UpperQuartileIndicator) indicator).getValue());
            }
        }

        exec = new ColumnAnalysisExecutor();
        executed = exec.execute(analysis);
        Assert.assertTrue("Executing analysis: analysis_db2_2: " + executed.getMessage(), executed.isOk()); //$NON-NLS-1$ 

        // result of lower quantile
        indicators = analysis.getResults().getIndicators();
        for (Indicator indicator : indicators) {
            if (indicator instanceof LowerQuartileIndicator) {
                Assert.assertEquals("36.0", ((LowerQuartileIndicator) indicator).getValue());
            } else if (indicator instanceof UpperQuartileIndicator) {
                Assert.assertEquals("43.0", ((UpperQuartileIndicator) indicator).getValue());
            }
        }
    }

    @Test
    public void testLowerUpperIndicatorWithDB2Scenario_3() {
        Indicator lowerIndicator = this.createLowerIndicator();
        Indicator upperIndicator = this.createUpperIndicator();
        String analysisName = "analysis_db2_3"; //$NON-NLS-1$

        // use column: SCENARIO3
        Analysis analysis = createAnalysis(lowerIndicator, upperIndicator, analysisName, 2);

        IAnalysisExecutor exec = new ColumnAnalysisSqlExecutor();
        ReturnCode executed = exec.execute(analysis);
        Assert.assertTrue("Executing analysis: analysis_db2_3: " + executed.getMessage(), executed.isOk()); //$NON-NLS-1$ 

        // result of lower quantile
        EList<Indicator> indicators = analysis.getResults().getIndicators();
        for (Indicator indicator : indicators) {
            if (indicator instanceof LowerQuartileIndicator) {
                Assert.assertEquals("39.0", ((LowerQuartileIndicator) indicator).getValue());
            } else if (indicator instanceof UpperQuartileIndicator) {
                Assert.assertEquals("43.0", ((UpperQuartileIndicator) indicator).getValue());
            }
        }

        exec = new ColumnAnalysisExecutor();
        executed = exec.execute(analysis);
        Assert.assertTrue("Executing analysis: analysis_db2_3: " + executed.getMessage(), executed.isOk()); //$NON-NLS-1$ 

        // result of lower quantile
        indicators = analysis.getResults().getIndicators();
        for (Indicator indicator : indicators) {
            if (indicator instanceof LowerQuartileIndicator) {
                Assert.assertEquals("39.0", ((LowerQuartileIndicator) indicator).getValue());
            } else if (indicator instanceof UpperQuartileIndicator) {
                Assert.assertEquals("43.0", ((UpperQuartileIndicator) indicator).getValue());
            }
        }
    }

    @Test
    public void testLowerUpperIndicatorWithDB2Scenario_4() {
        Indicator lowerIndicator = this.createLowerIndicator();
        Indicator upperIndicator = this.createUpperIndicator();
        String analysisName = "analysis_db2_4"; //$NON-NLS-1$

        // use column: SCENARIO4
        Analysis analysis = createAnalysis(lowerIndicator, upperIndicator, analysisName, 3);

        IAnalysisExecutor exec = new ColumnAnalysisSqlExecutor();
        ReturnCode executed = exec.execute(analysis);
        Assert.assertTrue("Executing analysis: analysis_db2_4: " + executed.getMessage(), executed.isOk()); //$NON-NLS-1$ 

        // result of lower quantile
        EList<Indicator> indicators = analysis.getResults().getIndicators();
        for (Indicator indicator : indicators) {
            if (indicator instanceof LowerQuartileIndicator) {
                Assert.assertEquals("40.0", ((LowerQuartileIndicator) indicator).getValue());
            } else if (indicator instanceof UpperQuartileIndicator) {
                Assert.assertEquals("47.0", ((UpperQuartileIndicator) indicator).getValue());
            }
        }

        exec = new ColumnAnalysisExecutor();
        executed = exec.execute(analysis);
        Assert.assertTrue("Executing analysis: analysis_db2_4: " + executed.getMessage(), executed.isOk()); //$NON-NLS-1$ 

        // result of lower quantile
        indicators = analysis.getResults().getIndicators();
        for (Indicator indicator : indicators) {
            if (indicator instanceof LowerQuartileIndicator) {
                Assert.assertEquals("39.5", ((LowerQuartileIndicator) indicator).getValue());
            } else if (indicator instanceof UpperQuartileIndicator) {
                Assert.assertEquals("45.0", ((UpperQuartileIndicator) indicator).getValue());
            }
        }
    }

    private Analysis createAnalysis(Indicator lowerIndicator, Indicator upperIndicator, String analysisName, int index) {
        AnalysisBuilder analysisBuilder = new AnalysisBuilder();

        boolean analysisInitialized = analysisBuilder.initializeAnalysis(analysisName, AnalysisType.MULTIPLE_COLUMN);
        Assert.assertTrue(analysisName + " initialize!", analysisInitialized); //$NON-NLS-1$

        analysisBuilder.setAnalysisConnection(db2Connection);

        TdColumn tdColumn = columns.get(index);
        lowerIndicator.setAnalyzedElement(tdColumn);
        upperIndicator.setAnalyzedElement(tdColumn);
        analysisBuilder.addElementToAnalyze(tdColumn, upperIndicator);
        analysisBuilder.addElementToAnalyze(tdColumn, lowerIndicator);

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
            if (tdColumn.getName().startsWith(COLUMN)) {
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
