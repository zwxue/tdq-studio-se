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
package org.talend.dq.analysis;

import junit.framework.Assert;

import org.eclipse.ui.PlatformUI;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.metadata.builder.connection.ConnectionFactory;
import org.talend.cwm.management.i18n.Messages;
import org.talend.cwm.relational.TdColumn;
import org.talend.cwm.relational.TdTable;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.analysis.AnalysisContext;
import org.talend.dataquality.analysis.AnalysisFactory;
import org.talend.dataquality.analysis.AnalysisParameters;
import org.talend.dataquality.analysis.AnalysisResult;
import org.talend.dataquality.analysis.ExecutionInformations;
import org.talend.dataquality.indicators.CountsIndicator;
import org.talend.dataquality.indicators.IndicatorsFactory;
import org.talend.dataquality.indicators.definition.DefinitionFactory;
import org.talend.dataquality.indicators.definition.IndicatorDefinition;
import org.talend.dq.analysis.memory.AnalysisThreadMemoryChangeNotifier;
import org.talend.utils.sugars.ReturnCode;
import orgomg.cwm.resource.relational.Catalog;

/**
 * created by talend on Aug 30, 2012 Detailled comment
 * 
 */
public class AnalysisExecutorTest {

    Analysis createAnalysis = null;

    ColumnAnalysisExecutor spy = null;

    @Before
    public void setUp() throws Exception {
        // Schema
        Catalog createCatalog = orgomg.cwm.resource.relational.RelationalFactory.eINSTANCE.createCatalog();
        createCatalog.setName("testTable"); //$NON-NLS-1$
        // ~Schema

        // TdTable
        TdTable createTdTable = org.talend.cwm.relational.RelationalFactory.eINSTANCE.createTdTable();
        createTdTable.setName("generaldata"); //$NON-NLS-1$
        createCatalog.getOwnedElement().add(createTdTable);
        // ~TdTable

        // TdColumn
        TdColumn createColumn = org.talend.cwm.relational.RelationalFactory.eINSTANCE.createTdColumn();
        createColumn.setName("name"); //$NON-NLS-1$
        createTdTable.getFeature().add(createColumn);
        // ~TdColumn

        // Indicator
        CountsIndicator createCountsIndicator = IndicatorsFactory.eINSTANCE.createCountsIndicator();
        IndicatorDefinition createIndicatorDefinition = DefinitionFactory.eINSTANCE.createIndicatorDefinition();
        createCountsIndicator.setIndicatorDefinition(createIndicatorDefinition);
        createCountsIndicator.setAnalyzedElement(createColumn);
        // ~Indicator
        // Analysis
        AnalysisParameters createAnalysisParameters = AnalysisFactory.eINSTANCE.createAnalysisParameters();
        createAnalysisParameters.setStoreData(true);
        createAnalysis = AnalysisFactory.eINSTANCE.createAnalysis();
        AnalysisContext createAnalysisContext = AnalysisFactory.eINSTANCE.createAnalysisContext();
        createAnalysisContext.getAnalysedElements().add(createColumn);
        createAnalysis.setContext(createAnalysisContext);
        AnalysisResult createAnalysisResult = AnalysisFactory.eINSTANCE.createAnalysisResult();
        ExecutionInformations createExecutionInformations = AnalysisFactory.eINSTANCE.createExecutionInformations();
        createAnalysisResult.setResultMetadata(createExecutionInformations);
        createAnalysisResult.getIndicators().add(createCountsIndicator);
        createAnalysis.setResults(createAnalysisResult);
        createAnalysis.setParameters(createAnalysisParameters);
        // ~Analysis

        // connection
        Connection createConnection = ConnectionFactory.eINSTANCE.createDatabaseConnection();
        createAnalysisContext.setConnection(createConnection);
        // ~connection
        spy = Mockito.spy(new ColumnAnalysisExecutor());
        Mockito.doReturn(true).when(spy).runAnalysis(((Analysis) Mockito.anyObject()), Mockito.anyString());
        Mockito.doReturn(1000L).when(spy).getCheckContinueCount();

    }

    @After
    public void tearDown() throws Exception {
        setMemoryControl(false);
    }

    /**
     * Test method for {@link org.talend.dq.analysis.AnalysisExecutor#execute(org.talend.dataquality.analysis.Analysis)}
     * . case2 no have enough memory
     */
    @Test
    public void testExecute2() {
        setMemoryControl(true);
        setMemoryValue(0);
        ReturnCode execute2 = spy.execute(createAnalysis);

        Assert.assertFalse(execute2.isOk());
        Assert.assertTrue(execute2.getMessage().equals(Messages.getString("Evaluator.OutOfMomory", spy.getUsedMemory()))); //$NON-NLS-1$

    }

    /**
     * DOC talend Comment method "setMemoryControl".
     * 
     * @param i
     */
    private void setMemoryValue(int i) {
        PlatformUI.getPreferenceStore().setValue(AnalysisThreadMemoryChangeNotifier.ANALYSIS_MEMORY_THRESHOLD, i);

    }

    /**
     * DOC talend Comment method "openMemoryControl".
     */
    private void setMemoryControl(boolean isOpen) {
        PlatformUI.getPreferenceStore().setValue(AnalysisThreadMemoryChangeNotifier.ANALYSIS_AUTOMATIC_MEMORY_CONTROL, isOpen);

    }
}
