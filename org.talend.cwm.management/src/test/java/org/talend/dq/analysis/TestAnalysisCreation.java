// ============================================================================
//
// Copyright (C) 2006-2009 Talend Inc. - www.talend.com
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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.talend.cwm.db.connection.ConnectionUtils;
import org.talend.cwm.exception.TalendException;
import org.talend.cwm.helper.CatalogHelper;
import org.talend.cwm.management.api.ConnectionService;
import org.talend.cwm.management.api.DqRepositoryViewService;
import org.talend.cwm.management.api.FolderProvider;
import org.talend.cwm.relational.TdCatalog;
import org.talend.cwm.relational.TdColumn;
import org.talend.cwm.relational.TdTable;
import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.analysis.AnalysisType;
import org.talend.dataquality.domain.Domain;
import org.talend.dataquality.domain.DomainFactory;
import org.talend.dataquality.domain.RangeRestriction;
import org.talend.dataquality.domain.pattern.Pattern;
import org.talend.dataquality.domain.pattern.PatternFactory;
import org.talend.dataquality.domain.pattern.RegularExpression;
import org.talend.dataquality.domain.sql.SqlPredicate;
import org.talend.dataquality.expressions.BooleanExpressionNode;
import org.talend.dataquality.factories.PatternIndicatorFactory;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.IndicatorParameters;
import org.talend.dataquality.indicators.IndicatorsFactory;
import org.talend.dataquality.indicators.PatternMatchingIndicator;
import org.talend.dq.analysis.parameters.DBConnectionParameter;
import org.talend.dq.indicators.IndicatorEvaluator;
import org.talend.dq.indicators.definitions.DefinitionHandler;
import org.talend.dq.sql.converters.CwmZExpression;
import org.talend.utils.properties.PropertiesLoader;
import org.talend.utils.properties.TypedProperties;
import org.talend.utils.sugars.ReturnCode;
import orgomg.cwm.objectmodel.core.CoreFactory;
import orgomg.cwm.objectmodel.core.Expression;
import orgomg.cwm.objectmodel.core.ModelElement;
import orgomg.cwm.resource.relational.Column;

/**
 * DOC scorreia class global comment. Detailled comment
 */
public class TestAnalysisCreation {

    /**
     * 
     */
    private static final String COLUMN_ANALYZED = "TEST_CHAR";

    /**
     * 
     */
    private static final String TABLE_ANALYZED = "TEST_COUNT";

    /**
     * 
     */
    private static final String DB_TO_ANALYZE = "TEST_DATAPROFILER";

    /**
     * 
     */
    private static final String REGEXP = "'su.*'";

    private static final String FILENAME = ".Talend.definition";

    private static final String PLUGIN_PATH = "/org.talend.dataquality/" + FILENAME;

    /**
     * 
     */
    private static final DomainFactory DOMAIN = DomainFactory.eINSTANCE;

    /**
     * DOC scorreia Comment method "main".
     * 
     * @param args
     */
    public static void main(String[] args) {
        try {
            TestAnalysisCreation myTest = new TestAnalysisCreation();
            myTest.run();
        } catch (TalendException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * DOC scorreia Comment method "run".
     * 
     * @throws TalendException
     */
    private void run() throws TalendException {
        String outputFolder = "ANA";
        analysisBuilder = new AnalysisBuilder();
        String analysisName = "My test analysis";

        boolean analysisInitialized = analysisBuilder.initializeAnalysis(analysisName, AnalysisType.COLUMN);
        Assert.assertTrue(analysisName + " failed to initialize!", analysisInitialized);

        // get the connection
        TdDataProvider dataManager = getDataManager();
        Assert.assertNotNull("No datamanager found!", dataManager);
        analysisBuilder.setAnalysisConnection(dataManager);

        // get a column to analyze
        ModelElement column;
        column = getColumn(dataManager);
        Indicator[] indicators = getIndicators(column);
        analysisBuilder.addElementToAnalyze(column, indicators);

        // get the domain constraint
        Domain dataFilter = getDataFilter(dataManager, (Column) column); // CAST here for test
        // analysisBuilder.addFilterOnData(dataFilter);

        // TODO scorreia save domain with analysisbuilder?
        FolderProvider folderProvider = new FolderProvider();
        folderProvider.setFolder(new File(outputFolder));
        DqRepositoryViewService.saveDomain(dataFilter, folderProvider);

        // run analysis
        Analysis analysis = analysisBuilder.getAnalysis();
        final boolean useSql = true;
        IAnalysisExecutor exec = useSql ? new ColumnAnalysisSqlExecutor() : new ColumnAnalysisExecutor();
        ReturnCode executed = exec.execute(analysis);
        Assert.assertTrue("Problem executing analysis: " + analysisName + ": " + executed.getMessage(), executed.isOk());

        // save data provider
        DqRepositoryViewService.saveDataProviderAndStructure(dataManager, folderProvider);

        // Need workspace context
        // AnalysisWriter writer = new AnalysisWriter();
        // File file = new File(outputFolder + File.separator + "analysis.ana");
        // ReturnCode saved = writer.save(analysis, file);
        // Assert.assertTrue(saved.getMessage(), saved.isOk());
        // if (saved.isOk()) {
        // log.info("Saved in " + file.getAbsolutePath());
        // }
    }

    public Analysis createAndRunAnalysis() throws TalendException {
        analysisBuilder = new AnalysisBuilder();
        String analysisName = "My test analysis";

        boolean analysisInitialized = analysisBuilder.initializeAnalysis(analysisName, AnalysisType.COLUMN);
        Assert.assertTrue(analysisName + " failed to initialize!", analysisInitialized);

        // get the connection
        TdDataProvider dataManager = getDataManager();
        Assert.assertNotNull("No datamanager found!", dataManager);
        analysisBuilder.setAnalysisConnection(dataManager);

        // get a column to analyze
        ModelElement column;
        column = getColumn(dataManager);
        Indicator[] indicators = getIndicators(column);
        analysisBuilder.addElementToAnalyze(column, indicators);

        // get the domain constraint
        Domain dataFilter = getDataFilter(dataManager, (Column) column); // CAST here for test
        analysisBuilder.addFilterOnData(dataFilter);

        // run analysis
        Analysis analysis = analysisBuilder.getAnalysis();
        final boolean useSql = true;
        IAnalysisExecutor exec = useSql ? new ColumnAnalysisSqlExecutor() : new ColumnAnalysisExecutor();
        ReturnCode executed = exec.execute(analysis);
        Assert.assertTrue("Problem executing analysis: " + analysisName + ": " + executed.getMessage(), executed.isOk());
        return analysis;
    }

    /**
     * DOC scorreia Comment method "getDataFilter".
     * 
     * @param dataManager
     * @param column
     * @return
     */
    private Domain getDataFilter(TdDataProvider dataManager, Column column) {
        Domain domain = DOMAIN.createDomain();
        RangeRestriction rangeRestriction = DOMAIN.createRangeRestriction();
        domain.getRanges().add(rangeRestriction);
        BooleanExpressionNode expr = getExpression(column);
        rangeRestriction.setExpressions(expr);
        return domain;
    }

    /**
     * DOC scorreia Comment method "getExpression".
     * 
     * @param column
     * 
     * @return
     */
    private BooleanExpressionNode getExpression(Column column) {
        CwmZExpression<String> expre = new CwmZExpression<String>(SqlPredicate.EQUAL);
        expre.setOperands(column, "\"sunny\"");
        return expre.generateExpressions();
    }

    private static Logger log = Logger.getLogger(TestAnalysisCreation.class);

    private AnalysisBuilder analysisBuilder;

    /**
     * DOC scorreia Comment method "getIndicators".
     * 
     * @param column
     * @return
     */
    private Indicator[] getIndicators(ModelElement column) {
        List<Indicator> allIndicators = new ArrayList<Indicator>();
        PatternMatchingIndicator patternMatchingIndicator = createPatternMatchingIndicator();
        allIndicators.add(patternMatchingIndicator);
        allIndicators.add(IndicatorsFactory.eINSTANCE.createDefValueCountIndicator());
        allIndicators.add(IndicatorsFactory.eINSTANCE.createSoundexFreqIndicator());
        allIndicators.add(IndicatorsFactory.eINSTANCE.createSoundexLowFreqIndicator());
        // allIndicators.add(IndicatorsFactory.eINSTANCE.createRowCountIndicator());
        // allIndicators.add(IndicatorsFactory.eINSTANCE.createUniqueCountIndicator());
        // allIndicators.add(IndicatorsFactory.eINSTANCE.createDistinctCountIndicator());
        // allIndicators.add(IndicatorsFactory.eINSTANCE.createDuplicateCountIndicator());
        // allIndicators.add(IndicatorsFactory.eINSTANCE.createNullCountIndicator());
        // allIndicators.add(IndicatorsFactory.eINSTANCE.createMinLengthIndicator());
        // allIndicators.add(IndicatorsFactory.eINSTANCE.createLowerQuartileIndicator());
        // allIndicators.add(IndicatorsFactory.eINSTANCE.createUpperQuartileIndicator());
        // allIndicators.add(IndicatorsFactory.eINSTANCE.createMedianIndicator());
        // allIndicators.add(IndicatorsFactory.eINSTANCE.createAverageLengthIndicator());

        for (Indicator indicator : allIndicators) {
            indicator.setAnalyzedElement(column);
            boolean definitionSet = DefinitionHandler.getInstance().setDefaultIndicatorDefinition(indicator);
            if (log.isDebugEnabled()) {
                log.debug("Definition set for " + indicator.getName() + ": " + definitionSet);
            }
        }
        //
        // EMFUtil util = new EMFUtil();
        // Resource definitionsFile = util.getResourceSet().getResource(
        // URI.createFileURI(".." + File.separator + PLUGIN_PATH + File.separator + FILENAME), true);

        return allIndicators.toArray(new Indicator[allIndicators.size()]);
    }

    /**
     * DOC scorreia Comment method "createPatternMatchingIndicator".
     * 
     * @return
     */
    private PatternMatchingIndicator createPatternMatchingIndicator() {
        Pattern pattern = PatternFactory.eINSTANCE.createPattern();
        pattern.setName("My Pattern");
        RegularExpression regularExpr = PatternFactory.eINSTANCE.createRegularExpression();
        Expression expression = CoreFactory.eINSTANCE.createExpression();
        expression.setBody(REGEXP);
        expression.setLanguage("SQL");
        regularExpr.setExpression(expression);
        pattern.getComponents().add(regularExpr);

        PatternIndicatorFactory.createRegexpMatchingIndicator(pattern);
        PatternMatchingIndicator patternMatchingIndicator = IndicatorsFactory.eINSTANCE.createRegexpMatchingIndicator();
        IndicatorParameters indicParams = IndicatorsFactory.eINSTANCE.createIndicatorParameters();
        Domain validData = DomainFactory.eINSTANCE.createDomain();
        validData.getPatterns().add(pattern);
        indicParams.setDataValidDomain(validData);
        patternMatchingIndicator.setParameters(indicParams);

        // save pattern in a file (only for test purpose)
        // FIXME rli comment this code, it's need workspace context.
        // EMFUtil util = EMFSharedResources.getSharedEmfUtil();
        // util.addPoolToResourceSet(new File("ANA/MyPattern.pattern"), pattern);
        // util.save();

        return patternMatchingIndicator;
    }

    /**
     * DOC scorreia Comment method "getColumn".
     * 
     * @param dataManager
     * @return
     * @throws TalendException
     */
    private ModelElement getColumn(TdDataProvider dataManager) throws TalendException {
        List<TdCatalog> tdCatalogs = CatalogHelper.getTdCatalogs(dataManager.getDataPackage());
        System.out.println("Catalogs: " + tdCatalogs);
        Assert.assertFalse(tdCatalogs.isEmpty());
        TdCatalog catalog = tdCatalogs.get(0);
        for (TdCatalog tdCatalog : tdCatalogs) {
            if (DB_TO_ANALYZE.equals(tdCatalog.getName())) {
                catalog = tdCatalog;
                break;
            }
        }
        Assert.assertNotNull(catalog);
        System.out.println("analysed Catalog: " + catalog.getName());
        List<TdTable> tables = DqRepositoryViewService.getTables(dataManager, catalog, null, true);

        // store tables in catalog
        CatalogHelper.addTables(tables, catalog);

        Assert.assertFalse(tables.isEmpty());
        TdTable tdTable = tables.get(0);
        for (TdTable table : tables) {
            if (TABLE_ANALYZED.equals(table.getName())) {
                tdTable = table;
                break;
            }
        }
        System.out.println("analyzed Table: " + tdTable.getName());
        List<TdColumn> columns;
        columns = DqRepositoryViewService.getColumns(dataManager, tdTable, null, true);
        // MOD scorreia 2009-01-29 columns are stored in the table
        // TableHelper.addColumns(tdTable, columns);

        Assert.assertFalse(columns.isEmpty());
        TdColumn col = columns.get(0);
        for (TdColumn tdColumn : columns) {
            if (COLUMN_ANALYZED.equals(tdColumn.getName())) {
                col = tdColumn;
                break;
            }
        }
        System.out.println("analyzed Column: " + col.getName());
        return col;
    }

    /**
     * DOC scorreia Comment method "getDataManager".
     * 
     * @return
     */
    public TdDataProvider getDataManager() {
        TypedProperties connectionParams = PropertiesLoader.getProperties(IndicatorEvaluator.class, "db.properties");
        String driverClassName = connectionParams.getProperty("driver");
        String dbUrl = connectionParams.getProperty("url");

        DBConnectionParameter params = new DBConnectionParameter();
        params.setName("My connection");
        params.setDriverClassName(driverClassName);
        params.setJdbcUrl(dbUrl);
        params.setParameters(connectionParams);

        // create connection
        ConnectionUtils.setTimeout(false);
        TdDataProvider dataProvider = ConnectionService.createConnection(params).getObject();

        dataProvider.setName("My data provider");
        return dataProvider;

    }
}
