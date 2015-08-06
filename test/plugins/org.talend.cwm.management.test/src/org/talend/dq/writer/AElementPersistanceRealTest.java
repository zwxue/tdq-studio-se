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
package org.talend.dq.writer;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.talend.core.model.properties.Item;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.analysis.AnalysisFactory;
import org.talend.dataquality.domain.pattern.Pattern;
import org.talend.dataquality.domain.pattern.PatternFactory;
import org.talend.dataquality.indicators.definition.DefinitionFactory;
import org.talend.dataquality.indicators.definition.IndicatorDefinition;
import org.talend.dataquality.properties.TDQAnalysisItem;
import org.talend.dataquality.properties.TDQBusinessRuleItem;
import org.talend.dataquality.properties.TDQIndicatorDefinitionItem;
import org.talend.dataquality.properties.TDQPatternItem;
import org.talend.dataquality.properties.TDQReportItem;
import org.talend.dataquality.rules.ParserRule;
import org.talend.dataquality.rules.RulesFactory;
import org.talend.dataquality.rules.WhereRule;
import org.talend.dq.helper.UnitTestBuildHelper;
import org.talend.dq.writer.impl.AnalysisWriter;
import org.talend.dq.writer.impl.DQRuleWriter;
import org.talend.dq.writer.impl.ElementWriterFactory;
import org.talend.dq.writer.impl.IndicatorDefinitionWriter;
import org.talend.dq.writer.impl.PatternWriter;
import org.talend.dq.writer.impl.ReportWriter;
import orgomg.cwmx.analysis.informationreporting.InformationreportingFactory;
import orgomg.cwmx.analysis.informationreporting.Report;

/**
 * created by xqliu on Jul 23, 2013 Detailled comment
 * 
 */
public class AElementPersistanceRealTest {

    private static final String PROJECT_NAME = "AElementPersistanceTestProject"; //$NON-NLS-1$

    /**
     * DOC xqliu Comment method "setUp".
     * 
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        UnitTestBuildHelper.createRealProject(PROJECT_NAME);
    }

    /**
     * DOC xqliu Comment method "tearDown".
     * 
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
        UnitTestBuildHelper.deleteCurrentProject();
    }

    /**
     * Test method for
     * {@link org.talend.dq.writer.AElementPersistance#createItem(orgomg.cwm.objectmodel.core.ModelElement)}.
     */
    @Test
    public void testCreateItemAnalysis() {
        AnalysisWriter createAnalysisWrite = ElementWriterFactory.getInstance().createAnalysisWrite();
        // test normal name
        String anaName = "ana1"; //$NON-NLS-1$
        String exceptedFileName = anaName + "_0.1.ana"; //$NON-NLS-1$
        Analysis createAnalysis = AnalysisFactory.eINSTANCE.createAnalysis();
        createAnalysis.setName(anaName);
        Item createItem = createAnalysisWrite.createItem(createAnalysis);
        assertTrue(createItem instanceof TDQAnalysisItem);
        TDQAnalysisItem analysisItem = (TDQAnalysisItem) createItem;
        assertTrue(analysisItem.getFilename().equals(exceptedFileName));
        // test special name
        String anaNameWithSpecialChar = "ana1~!`"; //$NON-NLS-1$
        String exceptedFileName2 = "ana1____0.1.ana"; //$NON-NLS-1$
        Analysis createAnalysis2 = AnalysisFactory.eINSTANCE.createAnalysis();
        createAnalysis2.setName(anaNameWithSpecialChar);
        Item createItem2 = createAnalysisWrite.createItem(createAnalysis2);
        assertTrue(createItem2 instanceof TDQAnalysisItem);
        TDQAnalysisItem analysisItem2 = (TDQAnalysisItem) createItem2;
        assertTrue(analysisItem2.getFilename().equals(exceptedFileName2));
    }

    /**
     * Test method for
     * {@link org.talend.dq.writer.AElementPersistance#createItem(orgomg.cwm.objectmodel.core.ModelElement)}.
     */
    @Test
    public void testCreateItemReport() {
        ReportWriter createReportWriter = ElementWriterFactory.getInstance().createReportWriter();
        // test normal name
        String repName = "rep1"; //$NON-NLS-1$
        String exceptedFileName = repName + "_0.1.rep"; //$NON-NLS-1$
        Report createReport = InformationreportingFactory.eINSTANCE.createReport();
        createReport.setName(repName);
        Item createItem = createReportWriter.createItem(createReport);
        assertTrue(createItem instanceof TDQReportItem);
        TDQReportItem reportItem = (TDQReportItem) createItem;
        assertTrue(reportItem.getFilename().equals(exceptedFileName));
        // test special name
        String repName2 = "rep1#^&"; //$NON-NLS-1$
        String exceptedFileName2 = "rep1____0.1.rep"; //$NON-NLS-1$
        Report createReport2 = InformationreportingFactory.eINSTANCE.createReport();
        createReport2.setName(repName2);
        Item createItem2 = createReportWriter.createItem(createReport2);
        assertTrue(createItem2 instanceof TDQReportItem);
        TDQReportItem reportItem2 = (TDQReportItem) createItem2;
        assertTrue(reportItem2.getFilename().equals(exceptedFileName2));
    }

    /**
     * Test method for
     * {@link org.talend.dq.writer.AElementPersistance#createItem(orgomg.cwm.objectmodel.core.ModelElement)}.
     */
    @Test
    public void testCreateItemWhereRule() {
        DQRuleWriter createdRuleWriter = ElementWriterFactory.getInstance().createdRuleWriter();
        // test normal name
        String whereRuleName = "whereRule1"; //$NON-NLS-1$
        String exceptedFileName = whereRuleName + "_0.1.rules"; //$NON-NLS-1$
        WhereRule createWhereRule = RulesFactory.eINSTANCE.createWhereRule();
        createWhereRule.setName(whereRuleName);
        Item createItem = createdRuleWriter.createItem(createWhereRule);
        assertTrue(createItem instanceof TDQBusinessRuleItem);
        TDQBusinessRuleItem ruleItem = (TDQBusinessRuleItem) createItem;
        assertTrue(ruleItem.getFilename().equals(exceptedFileName));
        // test special name
        String whereRuleName2 = "whereRule1*\\/?"; //$NON-NLS-1$
        String exceptedFileName2 = "whereRule1_____0.1.rules"; //$NON-NLS-1$
        WhereRule createWhereRule2 = RulesFactory.eINSTANCE.createWhereRule();
        createWhereRule2.setName(whereRuleName2);
        Item createItem2 = createdRuleWriter.createItem(createWhereRule2);
        assertTrue(createItem2 instanceof TDQBusinessRuleItem);
        TDQBusinessRuleItem ruleItem2 = (TDQBusinessRuleItem) createItem2;
        assertTrue(ruleItem2.getFilename().equals(exceptedFileName2));
    }

    /**
     * Test method for
     * {@link org.talend.dq.writer.AElementPersistance#createItem(orgomg.cwm.objectmodel.core.ModelElement)}.
     */
    @Test
    public void testCreateItemParserRule() {
        DQRuleWriter createdRuleWriter = ElementWriterFactory.getInstance().createdRuleWriter();
        // test normal name
        String parserRuleName = "parserRule1"; //$NON-NLS-1$
        String exceptedFileName = parserRuleName + "_0.1.rules"; //$NON-NLS-1$
        ParserRule createParserRule = RulesFactory.eINSTANCE.createParserRule();
        createParserRule.setName(parserRuleName);
        Item createItem = createdRuleWriter.createItem(createParserRule);
        assertTrue(createItem instanceof TDQBusinessRuleItem);
        TDQBusinessRuleItem ruleItem = (TDQBusinessRuleItem) createItem;
        assertTrue(ruleItem.getFilename().equals(exceptedFileName));
        // test special name
        String parserRuleName2 = "parserRule1:;\"."; //$NON-NLS-1$
        String exceptedFileName2 = "parserRule1_____0.1.rules"; //$NON-NLS-1$
        ParserRule createParserRule2 = RulesFactory.eINSTANCE.createParserRule();
        createParserRule2.setName(parserRuleName2);
        Item createItem2 = createdRuleWriter.createItem(createParserRule2);
        assertTrue(createItem2 instanceof TDQBusinessRuleItem);
        TDQBusinessRuleItem ruleItem2 = (TDQBusinessRuleItem) createItem2;
        assertTrue(ruleItem2.getFilename().equals(exceptedFileName2));
    }

    /**
     * Test method for
     * {@link org.talend.dq.writer.AElementPersistance#createItem(orgomg.cwm.objectmodel.core.ModelElement)}.
     */
    @Test
    public void testCreateItemPattern() {
        PatternWriter createPatternWriter = ElementWriterFactory.getInstance().createPatternWriter();
        // test normal name
        String patternName = "pattern1"; //$NON-NLS-1$
        String exceptedFileName = patternName + "_0.1.pattern"; //$NON-NLS-1$
        Pattern createPattern = PatternFactory.eINSTANCE.createPattern();
        createPattern.setName(patternName);
        Item createItem = createPatternWriter.createItem(createPattern);
        assertTrue(createItem instanceof TDQPatternItem);
        TDQPatternItem patternItem = (TDQPatternItem) createItem;
        assertTrue(patternItem.getFilename().equals(exceptedFileName));
        // test special name
        String patternName2 = "pattern1()，。"; //$NON-NLS-1$
        String exceptedFileName2 = "pattern1_____0.1.pattern"; //$NON-NLS-1$
        Pattern createPattern2 = PatternFactory.eINSTANCE.createPattern();
        createPattern2.setName(patternName2);
        Item createItem2 = createPatternWriter.createItem(createPattern2);
        assertTrue(createItem2 instanceof TDQPatternItem);
        TDQPatternItem patternItem2 = (TDQPatternItem) createItem2;
        assertTrue(patternItem2.getFilename().equals(exceptedFileName2));
    }

    /**
     * Test method for
     * {@link org.talend.dq.writer.AElementPersistance#createItem(orgomg.cwm.objectmodel.core.ModelElement)}.
     */
    @Test
    public void testCreateItemIndicatorDefinition() {
        IndicatorDefinitionWriter createIndicatorDefinitionWriter = ElementWriterFactory.getInstance()
                .createIndicatorDefinitionWriter();
        // test normal name
        String indDefName = "indDef1"; //$NON-NLS-1$
        String exceptedFileName = indDefName + "_0.1.definition"; //$NON-NLS-1$
        IndicatorDefinition createIndicatorDefinition = DefinitionFactory.eINSTANCE.createIndicatorDefinition();
        createIndicatorDefinition.setName(indDefName);
        Item createItem = createIndicatorDefinitionWriter.createItem(createIndicatorDefinition);
        assertTrue(createItem instanceof TDQIndicatorDefinitionItem);
        TDQIndicatorDefinitionItem indDefItem = (TDQIndicatorDefinitionItem) createItem;
        assertTrue(indDefItem.getFilename().equals(exceptedFileName));
        // test special name
        String indDefName2 = "indDef1'￥‘”、《，》<> "; //$NON-NLS-1$
        String exceptedFileName2 = "indDef1____________0.1.definition"; //$NON-NLS-1$
        IndicatorDefinition createIndicatorDefinition2 = DefinitionFactory.eINSTANCE.createIndicatorDefinition();
        createIndicatorDefinition2.setName(indDefName2);
        Item createItem2 = createIndicatorDefinitionWriter.createItem(createIndicatorDefinition2);
        assertTrue(createItem2 instanceof TDQIndicatorDefinitionItem);
        TDQIndicatorDefinitionItem indDefItem2 = (TDQIndicatorDefinitionItem) createItem2;
        assertTrue(indDefItem2.getFilename().equals(exceptedFileName2));
    }
}
