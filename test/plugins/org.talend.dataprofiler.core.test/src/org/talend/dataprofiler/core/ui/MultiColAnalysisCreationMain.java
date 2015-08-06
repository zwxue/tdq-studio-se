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
package org.talend.dataprofiler.core.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.metadata.builder.database.DqRepositoryViewService;
import org.talend.cwm.exception.TalendException;
import org.talend.cwm.helper.CatalogHelper;
import org.talend.cwm.management.api.ConnectionService;
import org.talend.cwm.management.api.FolderProvider;
import org.talend.cwm.relational.TdColumn;
import org.talend.cwm.relational.TdTable;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.analysis.AnalysisType;
import org.talend.dataquality.domain.Domain;
import org.talend.dataquality.domain.DomainFactory;
import org.talend.dataquality.domain.RangeRestriction;
import org.talend.dataquality.domain.sql.SqlPredicate;
import org.talend.dataquality.expressions.BooleanExpressionNode;
import org.talend.dataquality.helpers.MetadataHelper;
import org.talend.dataquality.indicators.DataminingType;
import org.talend.dataquality.indicators.columnset.ColumnSetMultiValueIndicator;
import org.talend.dataquality.indicators.columnset.ColumnsetFactory;
import org.talend.dq.analysis.AnalysisBuilder;
import org.talend.dq.analysis.IAnalysisExecutor;
import org.talend.dq.analysis.MultiColumnAnalysisExecutor;
import org.talend.dq.analysis.parameters.DBConnectionParameter;
import org.talend.dq.indicators.IndicatorEvaluator;
import org.talend.dq.indicators.definitions.DefinitionHandler;
import org.talend.dq.sql.converters.CwmZExpression;
import org.talend.dq.writer.impl.ElementWriterFactory;
import org.talend.utils.properties.PropertiesLoader;
import org.talend.utils.properties.TypedProperties;
import org.talend.utils.sql.Java2SqlType;
import org.talend.utils.sugars.ReturnCode;
import orgomg.cwm.resource.relational.Catalog;

/**
 * DOC scorreia class global comment. Detailled comment
 */
public class MultiColAnalysisCreationMain {

    /**
     * 
     */
    private static final DomainFactory DOMAIN = DomainFactory.eINSTANCE;

    private static Logger log = Logger.getLogger(MultiColAnalysisCreationMain.class);

    private AnalysisBuilder analysisBuilder;

    private static final boolean GRAPHICALTEST = true;

    private static final String[] COLUMNS = GRAPHICALTEST ? new String[] { "position_title", "gender", "management_role",
            "salary" } : new String[] { "position_title", "gender", "management_role", "salary" };

    private static final String[] NUMERICFUNC = GRAPHICALTEST ? new String[] { "SUM({0})", "COUNT({0})", "SUM(ISNULL({0}))" }
            : new String[] { "AVG({0})", "SUM(ISNULL({0}))", "COUNT({0})", "MIN({0})" };

    private static final String CATALOG = "tbi";

    private static final String TABLE = "employee";

    /**
     * DOC scorreia Comment method "main".
     * 
     * @param args
     */
    public static void main(String[] args) {
        try {
            MultiColAnalysisCreationMain myTest = new MultiColAnalysisCreationMain();
            myTest.run();
        } catch (TalendException e) {
            // TODO Auto-generated catch block
            log.error(e, e);
        }
    }

    /**
     * DOC scorreia Comment method "run".
     * 
     * @throws TalendException
     */
    public ColumnSetMultiValueIndicator run() throws TalendException {
        String outputFolder = "ANA";
        analysisBuilder = new AnalysisBuilder();
        String analysisName = "My test analysis";

        boolean analysisInitialized = analysisBuilder.initializeAnalysis(analysisName, AnalysisType.MULTIPLE_COLUMN);
        Assert.assertTrue(analysisName + " failed to initialize!", analysisInitialized);

        // get the connection
        Connection dataManager = getDataManager();
        Assert.assertNotNull("No datamanager found!", dataManager);
        analysisBuilder.setAnalysisConnection(dataManager);

        // get a column to analyze
        List<TdColumn> columns = new ArrayList<TdColumn>();
        try {
            columns.addAll(getColumns(dataManager));
        } catch (Exception e) {
            log.error(e, e);
        }
        ColumnSetMultiValueIndicator indicator = getIndicator(columns);
        for (TdColumn tdColumn : columns) {
            analysisBuilder.addElementToAnalyze(tdColumn, indicator);
        }

        // TODO scorreia save domain with analysisbuilder?
        FolderProvider folderProvider = new FolderProvider();
        folderProvider.setFolder(new File(outputFolder));

        // run analysis
        Analysis analysis = analysisBuilder.getAnalysis();

        IAnalysisExecutor exec = new MultiColumnAnalysisExecutor();
        ReturnCode executed = exec.execute(analysis);
        Assert.assertTrue("Problem executing analysis: " + analysisName + ": " + executed.getMessage(), executed.isOk());

        // save data provider
        ElementWriterFactory.getInstance().createDataProviderWriter().create(dataManager, folderProvider.getFolderResource());

        return indicator;
        // Need workspace context
        // AnalysisWriter writer = new AnalysisWriter();
        // File file = new File(outputFolder + File.separator + "analysis.ana");
        // ReturnCode saved = writer.save(analysis, file);
        // Assert.assertTrue(saved.getMessage(), saved.isOk());
        // if (saved.isOk()) {
        // log.info("Saved in " + file.getAbsolutePath());
        // }
    }

    //
    // public Analysis createAndRunAnalysis() throws TalendException {
    // analysisBuilder = new AnalysisBuilder();
    // String analysisName = "My test analysis";
    //
    // boolean analysisInitialized = analysisBuilder.initializeAnalysis(analysisName, AnalysisType.COLUMN);
    // Assert.assertTrue(analysisName + " failed to initialize!", analysisInitialized);
    //
    // // get the connection
    // TdDataProvider dataManager = getDataManager();
    // Assert.assertNotNull("No datamanager found!", dataManager);
    // analysisBuilder.setAnalysisConnection(dataManager);
    //
    // // get a column to analyze
    // List<TdColumn> columns = getColumns(dataManager);
    // Indicator indicator = getIndicator(columns);
    // for (TdColumn tdColumn : columns) {
    // analysisBuilder.addElementToAnalyze(tdColumn, indicator);
    // }
    //
    //
    // // run analysis
    // Analysis analysis = analysisBuilder.getAnalysis();
    // final boolean useSql = true;
    // IAnalysisExecutor exec = useSql ? new ColumnAnalysisSqlExecutor() : new ColumnAnalysisExecutor();
    // ReturnCode executed = exec.execute(analysis);
    // Assert.assertTrue("Problem executing analysis: " + analysisName + ": " + executed.getMessage(), executed.isOk());
    // return analysis;
    // }

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
        expre.setOperands(column, "\"sunny\"");
        return expre.generateExpressions();
    }

    /**
     * DOC scorreia Comment method "getIndicators".
     * 
     * @param column
     * @return
     */
    private ColumnSetMultiValueIndicator getIndicator(List<TdColumn> columns) {
        ColumnSetMultiValueIndicator ind = ColumnsetFactory.eINSTANCE.createColumnSetMultiValueIndicator();
        ind.getAnalyzedColumns().addAll(columns);

        boolean definitionSet = DefinitionHandler.getInstance().setDefaultIndicatorDefinition(ind);
        if (log.isDebugEnabled()) {
            log.debug("Definition set for " + ind.getName() + ": " + definitionSet);
        }

        for (int i = 0; i < NUMERICFUNC.length; i++) {
            String f = NUMERICFUNC[i];
            ind.getNumericFunctions().add(f);
        }

        return ind;
    }

    /**
     * DOC scorreia Comment method "getColumn".
     * 
     * @param dataManager
     * @return
     * @throws Exception
     */
    private List<TdColumn> getColumns(Connection dataManager) throws Exception {
        List<Catalog> tdCatalogs = CatalogHelper.getCatalogs(dataManager.getDataPackage());
        Catalog catalog = null;
        for (Catalog tdCatalog : tdCatalogs) {
            if (CATALOG.equals(tdCatalog.getName())) {
                catalog = tdCatalog;
                break;
            }
        }
        Assert.assertNotNull(catalog);
        Assert.assertFalse(tdCatalogs.isEmpty());
        System.out.println("analysed Catalog: " + catalog.getName());

        List<TdTable> tables = DqRepositoryViewService.getTables(dataManager, catalog, TABLE, true);

        // store tables in catalog
        CatalogHelper.addTables(tables, catalog);

        Assert.assertFalse(tables.isEmpty());
        TdTable tdTable = tables.get(0);
        System.out.println("analyzed Table: " + tdTable.getName());
        List<TdColumn> columns;
        columns = DqRepositoryViewService.getColumns(dataManager, tdTable, null, true);
        // MOD scorreia 2009-01-29 columns are stored in the table
        // TableHelper.addColumns(tdTable, columns);

        Assert.assertFalse(columns.isEmpty());

        List<TdColumn> usedCols = new ArrayList<TdColumn>();
        for (TdColumn tdColumn : columns) {
            for (int i = 0; i < COLUMNS.length; i++) {
                String c = COLUMNS[i];
                if (tdColumn.getName().equals(c)) {
                    usedCols.add(tdColumn);
                }
            }
        }
        // set DM type for each used column
        for (TdColumn tdColumn : usedCols) {
            final int javaType = tdColumn.getJavaType();
            if (Java2SqlType.isNumbericInSQL(javaType)) {
                MetadataHelper.setDataminingType(DataminingType.INTERVAL, tdColumn);
            } else if (Java2SqlType.isTextInSQL(javaType)) {
                MetadataHelper.setDataminingType(DataminingType.NOMINAL, tdColumn);
            }
        }
        return usedCols;
    }

    /**
     * DOC scorreia Comment method "getDataManager".
     * 
     * @return
     */
    public Connection getDataManager() {
        TypedProperties connectionParams = PropertiesLoader.getProperties(IndicatorEvaluator.class, "db.properties");
        String driverClassName = connectionParams.getProperty("driver");
        String dbUrl = connectionParams.getProperty("url");
        String sqlTypeName = connectionParams.getProperty("sqlTypeName"); //$NON-NLS-1$

        DBConnectionParameter params = new DBConnectionParameter();
        params.setName("My connection");
        params.setDriverClassName(driverClassName);
        params.setJdbcUrl(dbUrl);
        params.setSqlTypeName(sqlTypeName);
        params.setParameters(connectionParams);
        params.getParameters();

        // create connection

        Connection dataProvider = ConnectionService.createConnection(params).getObject();

        dataProvider.setName("My data provider");
        return dataProvider;

    }
}
