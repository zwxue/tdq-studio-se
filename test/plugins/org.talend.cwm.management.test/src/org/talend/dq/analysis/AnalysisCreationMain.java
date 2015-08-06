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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.metadata.builder.database.DqRepositoryViewService;
import org.talend.core.repository.model.IRepositoryFactory;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.core.repository.model.RepositoryFactoryProvider;
import org.talend.cwm.db.connection.ConnectionUtils;
import org.talend.cwm.exception.TalendException;
import org.talend.cwm.helper.CatalogHelper;
import org.talend.cwm.management.api.ConnectionService;
import org.talend.cwm.management.api.FolderProvider;
import org.talend.cwm.relational.RelationalFactory;
import org.talend.cwm.relational.TdColumn;
import org.talend.cwm.relational.TdExpression;
import org.talend.cwm.relational.TdTable;
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
import org.talend.dataquality.indicators.definition.DefinitionFactory;
import org.talend.dataquality.indicators.definition.IndicatorCategory;
import org.talend.dataquality.indicators.definition.IndicatorDefinition;
import org.talend.dq.analysis.parameters.DBConnectionParameter;
import org.talend.dq.indicators.IndicatorEvaluator;
import org.talend.dq.sql.converters.CwmZExpression;
import org.talend.dq.writer.impl.ElementWriterFactory;
import org.talend.utils.properties.PropertiesLoader;
import org.talend.utils.properties.TypedProperties;
import org.talend.utils.sugars.ReturnCode;
import orgomg.cwm.objectmodel.core.ModelElement;
import orgomg.cwm.resource.relational.Catalog;

/**
 * DOC scorreia class global comment. Detailled comment
 */
public class AnalysisCreationMain {

    private static final String EMPTY_STRING = ""; //$NON-NLS-1$

    /**
     * test column name
     */
    private static final String COLUMN_ANALYZED = "lname"; //$NON-NLS-1$

    /**
     * test table name
     */
    private static final String TABLE_ANALYZED = "customer"; //$NON-NLS-1$

    /**
     * test database name
     */
    private static final String DB_TO_ANALYZE = "tbi"; //$NON-NLS-1$

    /**
     * test regexp pattern
     */
    private static final String REGEXP = "'su.*'"; //$NON-NLS-1$

    /**
     * default DomainFactory
     */
    private static final DomainFactory DOMAIN = DomainFactory.eINSTANCE;

    private static final String INDICATOR_NAME_RegexpMatchingIndicator = "RegexpMatchingIndicator"; //$NON-NLS-1$

    /**
     * DOC xqliu TestAnalysisCreation constructor comment.
     */
    public AnalysisCreationMain() {
        initProxyRepository();
    }

    /**
     * DOC xqliu Comment method "initProxyRepository".
     */
    protected void initProxyRepository() {
        if (ProxyRepositoryFactory.getInstance().getRepositoryFactoryFromProvider() == null) {
            IRepositoryFactory repository = RepositoryFactoryProvider.getRepositoriyById("local"); //$NON-NLS-1$
            if (repository != null) {
                ProxyRepositoryFactory.getInstance().setRepositoryFactoryFromProvider(repository);
            }
        }
    }

    /**
     * DOC scorreia Comment method "main".
     * 
     * @param args
     */
    public static void main(String[] args) {
        try {
            AnalysisCreationMain myTest = new AnalysisCreationMain();
            myTest.run();
        } catch (TalendException e) {
            log.error(e, e);
        }
    }

    /**
     * DOC scorreia Comment method "run".
     * 
     * @throws TalendException
     */
    private void run() throws TalendException {
        String outputFolder = "ANA"; //$NON-NLS-1$
        analysisBuilder = new AnalysisBuilder();
        String analysisName = "My test analysis"; //$NON-NLS-1$

        boolean analysisInitialized = analysisBuilder.initializeAnalysis(analysisName, AnalysisType.COLUMN);
        Assert.assertTrue(analysisName + " failed to initialize!", analysisInitialized); //$NON-NLS-1$

        // get the connection
        Connection dataManager = getDataManager();
        Assert.assertNotNull("No datamanager found!", dataManager); //$NON-NLS-1$
        analysisBuilder.setAnalysisConnection(dataManager);

        // get a column to analyze
        ModelElement column = null;
        try {
            column = getColumn(dataManager);
        } catch (Exception e) {
            log.error(e, e);
        }
        Indicator[] indicators = getIndicators(column);
        analysisBuilder.addElementToAnalyze(column, indicators);

        // get the domain constraint
        Domain dataFilter = getDataFilter(dataManager, (TdColumn) column); // CAST here for test
        // analysisBuilder.addFilterOnData(dataFilter);

        // TODO scorreia save domain with analysisbuilder?
        FolderProvider folderProvider = new FolderProvider();
        folderProvider.setFolder(new File(outputFolder));

        // run analysis
        Analysis analysis = analysisBuilder.getAnalysis();
        final boolean useSql = true;
        IAnalysisExecutor exec = useSql ? new ColumnAnalysisSqlExecutor() : new ColumnAnalysisExecutor();
        ReturnCode executed = exec.execute(analysis);
        Assert.assertTrue("Problem executing analysis: " + analysisName + ": " + executed.getMessage(), executed.isOk()); //$NON-NLS-1$ //$NON-NLS-2$

        // save data provider
        ElementWriterFactory.getInstance().createDataProviderWriter().create(dataManager, folderProvider.getFolderResource());

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
        String analysisName = "My test analysis"; //$NON-NLS-1$

        boolean analysisInitialized = analysisBuilder.initializeAnalysis(analysisName, AnalysisType.COLUMN);
        Assert.assertTrue(analysisName + " failed to initialize!", analysisInitialized); //$NON-NLS-1$

        // get the connection
        Connection dataManager = getDataManager();
        Assert.assertNotNull("No datamanager found!", dataManager); //$NON-NLS-1$
        analysisBuilder.setAnalysisConnection(dataManager);

        // get a column to analyze
        ModelElement column = null;
        try {
            column = getColumn(dataManager);
        } catch (Exception e) {
            log.error(e, e);
        }
        Indicator[] indicators = getIndicators(column);
        analysisBuilder.addElementToAnalyze(column, indicators);

        // get the domain constraint
        Domain dataFilter = getDataFilter(dataManager, (TdColumn) column); // CAST here for test
        analysisBuilder.addFilterOnData(dataFilter);

        // run analysis
        Analysis analysis = analysisBuilder.getAnalysis();
        final boolean useSql = true;
        IAnalysisExecutor exec = useSql ? new ColumnAnalysisSqlExecutor() : new ColumnAnalysisExecutor();
        ReturnCode executed = exec.execute(analysis);
        Assert.assertTrue("Problem executing analysis: " + analysisName + ": " + executed.getMessage(), executed.isOk()); //$NON-NLS-1$ //$NON-NLS-2$
        return analysis;
    }

    /**
     * DOC scorreia Comment method "getDataFilter".
     * 
     * @param dataManager
     * @param column
     * @return
     */
    private Domain getDataFilter(Connection dataManager, TdColumn column) {
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
    private BooleanExpressionNode getExpression(TdColumn column) {
        CwmZExpression<String> expre = new CwmZExpression<String>(SqlPredicate.EQUAL);
        expre.setOperands(column, "\"sunny\""); //$NON-NLS-1$
        return expre.generateExpressions();
    }

    private static Logger log = Logger.getLogger(AnalysisCreationMain.class);

    private AnalysisBuilder analysisBuilder;

    /**
     * DOC scorreia Comment method "getIndicators".
     * 
     * @param column
     * @return
     */
    private Indicator[] getIndicators(ModelElement column) {
        List<Indicator> allIndicators = new ArrayList<Indicator>();
        allIndicators.add(createPatternMatchingIndicator());

        for (Indicator indicator : allIndicators) {
            indicator.setAnalyzedElement(column);
            boolean definitionSet = setDefaultIndicatorDefinition(indicator);
            if (log.isDebugEnabled()) {
                log.debug("Definition set for " + indicator.getName() + ": " + definitionSet); //$NON-NLS-1$ //$NON-NLS-2$
            }
        }

        return allIndicators.toArray(new Indicator[allIndicators.size()]);
    }

    /**
     * DOC xqliu Comment method "setDefaultIndicatorDefinition".
     * 
     * @param indicator
     * @return
     */
    private boolean setDefaultIndicatorDefinition(Indicator indicator) {
        boolean result = false;
        if (indicator != null) {
            IndicatorDefinition indDef = createIndicatorDefinitionByName(indicator.getName());
            if (indDef != null) {
                indicator.setIndicatorDefinition(indDef);
                result = true;
            }
        }
        return result;
    }

    /**
     * DOC xqliu Comment method "createIndicatorDefinitionByName".
     * 
     * @param name
     * @return
     */
    private IndicatorDefinition createIndicatorDefinitionByName(String name) {
        if (INDICATOR_NAME_RegexpMatchingIndicator.equals(name)) {
            return createIndicatorDefinitionRegexpMatchingIndicator();
        }
        return null;
    }

    /**
     * DOC xqliu Comment method "createIndicatorDefinitionRegexpMatchingIndicator".
     * 
     * @return
     */
    private IndicatorDefinition createIndicatorDefinitionRegexpMatchingIndicator() {
        IndicatorDefinition indDef = DefinitionFactory.eINSTANCE.createIndicatorDefinition();

        String defName = "Regular Expression Matching"; //$NON-NLS-1$
        indDef.setName(defName);
        indDef.setLabel(EMPTY_STRING);

        indDef.getCategories().add(createIndicatorCategoryPatternMatching());

        TdExpression regularExpressionMatchingMysql = RelationalFactory.eINSTANCE.createTdExpression();
        regularExpressionMatchingMysql
                .setBody("SELECT COUNT(CASE WHEN <%=__COLUMN_NAMES__%> REGEXP BINARY <%=__PATTERN_EXPR__%> THEN 1 END), COUNT(*) FROM <%=__TABLE_NAME__%> <%=__WHERE_CLAUSE__%>"); //$NON-NLS-1$
        regularExpressionMatchingMysql.setLanguage("MySQL"); //$NON-NLS-1$
        indDef.getSqlGenericExpression().add(regularExpressionMatchingMysql);

        return indDef;
    }

    /**
     * DOC xqliu Comment method "createIndicatorCategoryPatternMatching".
     * 
     * @return
     */
    protected IndicatorCategory createIndicatorCategoryPatternMatching() {
        IndicatorCategory indCat = DefinitionFactory.eINSTANCE.createIndicatorCategory();
        String catName = "Pattern Matching"; //$NON-NLS-1$
        indCat.setName(catName);
        indCat.setLabel(catName);
        return indCat;
    }

    /**
     * DOC scorreia Comment method "createPatternMatchingIndicator".
     * 
     * @return
     */
    private PatternMatchingIndicator createPatternMatchingIndicator() {
        Pattern pattern = PatternFactory.eINSTANCE.createPattern();
        pattern.setName("My Pattern"); //$NON-NLS-1$
        RegularExpression regularExpr = PatternFactory.eINSTANCE.createRegularExpression();
        TdExpression expression = RelationalFactory.eINSTANCE.createTdExpression();
        expression.setBody(REGEXP);
        expression.setLanguage("SQL"); //$NON-NLS-1$
        regularExpr.setExpression(expression);
        pattern.getComponents().add(regularExpr);

        PatternIndicatorFactory.createRegexpMatchingIndicator(pattern);
        PatternMatchingIndicator patternMatchingIndicator = IndicatorsFactory.eINSTANCE.createRegexpMatchingIndicator();
        IndicatorParameters indicParams = IndicatorsFactory.eINSTANCE.createIndicatorParameters();
        Domain validData = DomainFactory.eINSTANCE.createDomain();
        validData.getPatterns().add(pattern);
        indicParams.setDataValidDomain(validData);
        patternMatchingIndicator.setParameters(indicParams);

        return patternMatchingIndicator;
    }

    /**
     * DOC scorreia Comment method "getColumn".
     * 
     * @param dataManager
     * @return
     * @throws Exception
     */
    private ModelElement getColumn(Connection dataManager) throws Exception {
        List<Catalog> tdCatalogs = CatalogHelper.getCatalogs(dataManager.getDataPackage());
        System.out.println("Catalogs: " + tdCatalogs); //$NON-NLS-1$
        Assert.assertFalse(tdCatalogs.isEmpty());
        Catalog catalog = tdCatalogs.get(0);
        for (Catalog tdCatalog : tdCatalogs) {
            if (DB_TO_ANALYZE.equals(tdCatalog.getName())) {
                catalog = tdCatalog;
                break;
            }
        }
        Assert.assertNotNull(catalog);
        System.out.println("analysed Catalog: " + catalog.getName()); //$NON-NLS-1$
        List<TdTable> tables = DqRepositoryViewService.getTables(dataManager, catalog, EMPTY_STRING, true);

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
        System.out.println("analyzed Table: " + tdTable.getName()); //$NON-NLS-1$
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
        System.out.println("analyzed Column: " + col.getName()); //$NON-NLS-1$
        return col;
    }

    /**
     * DOC scorreia Comment method "getDataManager".
     * 
     * @return
     */
    public Connection getDataManager() {
        TypedProperties connectionParams = PropertiesLoader.getProperties(IndicatorEvaluator.class, "db.properties"); //$NON-NLS-1$
        String driverClassName = connectionParams.getProperty("driver"); //$NON-NLS-1$
        String dbUrl = connectionParams.getProperty("url"); //$NON-NLS-1$
        String sqlTypeName = connectionParams.getProperty("sqlTypeName"); //$NON-NLS-1$

        DBConnectionParameter params = new DBConnectionParameter();
        params.setName("My connection"); //$NON-NLS-1$
        params.setDriverClassName(driverClassName);
        params.setJdbcUrl(dbUrl);
        params.setSqlTypeName(sqlTypeName);
        params.setParameters(connectionParams);

        // create connection
        ConnectionUtils.setTimeout(false);
        Connection dataProvider = ConnectionService.createConnection(params).getObject();

        dataProvider.setName("My data provider"); //$NON-NLS-1$
        return dataProvider;
    }

    /**
     * create a postgresql connection
     * 
     * @return
     */
    public Connection getDataManagerPostgresql() {
        TypedProperties connectionParams = PropertiesLoader.getProperties(IndicatorEvaluator.class, "postgresql.properties"); //$NON-NLS-1$
        String driverClassName = connectionParams.getProperty("driver"); //$NON-NLS-1$
        String dbUrl = connectionParams.getProperty("url"); //$NON-NLS-1$
        String sqlTypeName = connectionParams.getProperty("sqlTypeName"); //$NON-NLS-1$

        DBConnectionParameter params = new DBConnectionParameter();
        params.setName("Postgresql connection"); //$NON-NLS-1$
        params.setDriverClassName(driverClassName);
        params.setJdbcUrl(dbUrl);
        params.setSqlTypeName(sqlTypeName);
        params.setParameters(connectionParams);

        // create connection
        ConnectionUtils.setTimeout(false);
        Connection dataProvider = ConnectionService.createConnection(params).getObject();

        dataProvider.setName("Postgresql data provider"); //$NON-NLS-1$
        return dataProvider;
    }
}
