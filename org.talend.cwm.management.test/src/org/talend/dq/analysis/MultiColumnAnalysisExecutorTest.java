// ============================================================================
//
// Copyright (C) 2006-2013 Talend Inc. - www.talend.com
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

import static org.junit.Assert.*;

import org.junit.Rule;
import org.junit.Test;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.talend.cwm.relational.RelationalFactory;
import org.talend.cwm.relational.TdColumn;
import org.talend.cwm.relational.TdExpression;
import org.talend.cwm.relational.TdTable;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.analysis.AnalysisContext;
import org.talend.dataquality.analysis.AnalysisFactory;
import org.talend.dataquality.analysis.AnalysisParameters;
import org.talend.dataquality.analysis.AnalysisResult;
import org.talend.dataquality.analysis.AnalysisType;
import org.talend.dataquality.analysis.ExecutionInformations;
import org.talend.dataquality.indicators.DataminingType;
import org.talend.dataquality.indicators.columnset.ColumnsetFactory;
import org.talend.dataquality.indicators.columnset.SimpleStatIndicator;
import org.talend.dataquality.indicators.definition.DefinitionFactory;
import org.talend.dataquality.indicators.definition.IndicatorDefinition;
import orgomg.cwm.objectmodel.core.Package;

/**
 * DOC msjian class global comment. Detailled comment
 */
public class MultiColumnAnalysisExecutorTest {

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    /**
     * Test method for
     * {@link org.talend.dq.analysis.MultiColumnAnalysisExecutor#createSqlStatement(org.talend.dataquality.analysis.Analysis)}
     * . this is test for columnset analysis
     */
    @Test
    public void testCreateSqlStatement_1() {

        Analysis analysis = AnalysisFactory.eINSTANCE.createAnalysis();
        AnalysisContext context = AnalysisFactory.eINSTANCE.createAnalysisContext();
        analysis.setContext(context);

        Package catalog = orgomg.cwm.resource.relational.RelationalFactory.eINSTANCE.createCatalog();// mock(Catalog.class);
        catalog.setName("tbi"); //$NON-NLS-1$

        Package schema = orgomg.cwm.resource.relational.RelationalFactory.eINSTANCE.createSchema();// mock(Schema.class);
        schema.setNamespace(catalog);

        TdTable tdTable = RelationalFactory.eINSTANCE.createTdTable();
        tdTable.setName("customer"); //$NON-NLS-1$
        tdTable.setNamespace(schema);

        TdColumn tdColumn = RelationalFactory.eINSTANCE.createTdColumn();
        tdColumn.setOwner(tdTable);
        tdColumn.setName("date_accnt_opened"); //$NON-NLS-1$

        AnalysisParameters analysisPara = AnalysisFactory.eINSTANCE.createAnalysisParameters();
        analysisPara.setStoreData(false);
        // set analysis type is columnset
        analysisPara.setAnalysisType(AnalysisType.COLUMN_SET);
        analysis.setParameters(analysisPara);
        context.getAnalysedElements().add(tdColumn);

        AnalysisResult analysisResult = AnalysisFactory.eINSTANCE.createAnalysisResult();
        ExecutionInformations info = AnalysisFactory.eINSTANCE.createExecutionInformations();
        analysisResult.setResultMetadata(info);

        SimpleStatIndicator simpleStatIndicator = ColumnsetFactory.eINSTANCE.createSimpleStatIndicator();

        TdExpression expression = RelationalFactory.eINSTANCE.createTdExpression();
        expression
                .setBody("SELECT <%=__COLUMN_NAMES__%> FROM <%=__TABLE_NAME__%> <%=__WHERE_CLAUSE__%> GROUP BY <%=__GROUP_BY_ALIAS__%>"); //$NON-NLS-1$
        expression.setLanguage("SQL"); //$NON-NLS-1$

        IndicatorDefinition indicatorDefinition = DefinitionFactory.eINSTANCE.createIndicatorDefinition();
        indicatorDefinition.getSqlGenericExpression().add(expression);

        simpleStatIndicator.setIndicatorDefinition(indicatorDefinition);
        simpleStatIndicator.getDateFunctions().add(""); //$NON-NLS-1$
        simpleStatIndicator.getNumericFunctions().add(""); //$NON-NLS-1$

        simpleStatIndicator.setDataminingType(DataminingType.NOMINAL);
        simpleStatIndicator.getAnalyzedColumns().add(tdColumn);

        analysisResult.getIndicators().add(simpleStatIndicator);
        analysis.setResults(analysisResult);

        MultiColumnAnalysisExecutor multiColumnAnalysisExecutor = new MultiColumnAnalysisExecutor();
        assertEquals("", multiColumnAnalysisExecutor.createSqlStatement(analysis)); //$NON-NLS-1$
        assertEquals(1, analysis.getResults().getIndicators().size());
        assertEquals("SELECT date_accnt_opened,COUNT(*) FROM tbi.customer  GROUP BY date_accnt_opened", //$NON-NLS-1$
                analysis.getResults().getIndicators().get(0).getInstantiatedExpressions("SQL").getBody()); //$NON-NLS-1$

    }

    /**
     * Test method for
     * {@link org.talend.dq.analysis.MultiColumnAnalysisExecutor#createSqlStatement(org.talend.dataquality.analysis.Analysis)}
     * . this is test for numerical correlation analysis
     */
    @Test
    public void testCreateSqlStatement_2() {
        // TODO
    }

    /**
     * Test method for
     * {@link org.talend.dq.analysis.MultiColumnAnalysisExecutor#createSqlStatement(org.talend.dataquality.analysis.Analysis)}
     * . this is test for time correlation analysis
     */
    @Test
    public void testCreateSqlStatement_3() {
        // TODO
    }

    /**
     * Test method for
     * {@link org.talend.dq.analysis.MultiColumnAnalysisExecutor#createSqlStatement(org.talend.dataquality.analysis.Analysis)}
     * . this is test for nominal correlation analysis
     */
    @Test
    public void testCreateSqlStatement_4() {
        // TODO
    }
}
