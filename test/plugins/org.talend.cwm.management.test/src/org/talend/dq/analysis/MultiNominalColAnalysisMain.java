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
package org.talend.dq.analysis;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.metadata.builder.database.DqRepositoryViewService;
import org.talend.cwm.db.connection.ConnectionUtils;
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
import org.talend.dq.analysis.parameters.DBConnectionParameter;
import org.talend.dq.indicators.IndicatorEvaluator;
import org.talend.dq.indicators.definitions.DefinitionHandler;
import org.talend.dq.indicators.graph.GraphBuilder;
import org.talend.dq.indicators.graph.tests.MyFirstMain;
import org.talend.dq.sql.converters.CwmZExpression;
import org.talend.dq.writer.impl.ElementWriterFactory;
import org.talend.utils.exceptions.TalendException;
import org.talend.utils.properties.PropertiesLoader;
import org.talend.utils.properties.TypedProperties;
import org.talend.utils.sql.Java2SqlType;
import org.talend.utils.sugars.ReturnCode;
import orgomg.cwm.resource.relational.Catalog;

/**
 * DOC scorreia class global comment. Detailled comment
 */
public class MultiNominalColAnalysisMain {

    /**
     * 
     */
    private static final DomainFactory DOMAIN = DomainFactory.eINSTANCE;

    private static Logger log = Logger.getLogger(MultiNominalColAnalysisMain.class);

    private AnalysisBuilder analysisBuilder;

    private static final String[] COLUMNS = new String[] { "city", "houseowner", "occupation", "country", "marital_status",
            "member_card" };

    private static final String[] NUMERICFUNC = new String[] {};

    private static final String CATALOG = "tbi";

    private static final String TABLE = "customer";

    /**
     * DOC scorreia Comment method "main".
     * 
     * @param args
     */
    public static void main(String[] args) {
        try {
            MultiNominalColAnalysisMain myTest = new MultiNominalColAnalysisMain();
            final ColumnSetMultiValueIndicator indicator = myTest.run();
            final List<Object[]> listRows = indicator.getListRows();
            final MyFirstMain myFirstMain = new MyFirstMain();
            myFirstMain.setAllData(listRows);
            GraphBuilder g = new GraphBuilder();
            g.setTotalWeight(indicator.getCount());
            myFirstMain.run(g);
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
        ColumnSetMultiValueIndicator ind = ColumnsetFactory.eINSTANCE.createWeakCorrelationIndicator();
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
        params.getParameters();

        // create connection
        ConnectionUtils.setTimeout(false);
        Connection dataProvider = ConnectionService.createConnection(params).getObject();

        dataProvider.setName("My data provider"); //$NON-NLS-1$
        return dataProvider;

    }
}
