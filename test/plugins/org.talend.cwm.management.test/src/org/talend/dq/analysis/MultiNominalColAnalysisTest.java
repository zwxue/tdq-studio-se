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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.talend.core.model.metadata.IMetadataConnection;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.metadata.builder.connection.DatabaseConnection;
import org.talend.core.model.metadata.builder.database.DqRepositoryViewService;
import org.talend.cwm.db.connection.ConnectionUtils;
import org.talend.cwm.helper.CatalogHelper;
import org.talend.cwm.management.api.FolderProvider;
import org.talend.cwm.relational.TdColumn;
import org.talend.cwm.relational.TdTable;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.analysis.AnalysisType;
import org.talend.dataquality.helpers.MetadataHelper;
import org.talend.dataquality.indicators.DataminingType;
import org.talend.dataquality.indicators.columnset.ColumnSetMultiValueIndicator;
import org.talend.dataquality.indicators.columnset.ColumnsetFactory;
import org.talend.dq.analysis.parameters.DBConnectionParameter;
import org.talend.dq.helper.ParameterUtil;
import org.talend.dq.indicators.IndicatorEvaluator;
import org.talend.dq.indicators.definitions.DefinitionHandler;
import org.talend.metadata.managment.model.MetadataFillFactory;
import org.talend.metadata.managment.ui.model.ProjectNodeHelper;
import org.talend.utils.properties.PropertiesLoader;
import org.talend.utils.properties.TypedProperties;
import org.talend.utils.sql.Java2SqlType;
import org.talend.utils.sugars.ReturnCode;
import orgomg.cwm.resource.relational.Catalog;

/**
 * DOC scorreia class global comment. Detailled comment
 */
@SuppressWarnings("deprecation")
public class MultiNominalColAnalysisTest {

    private static Logger log = Logger.getLogger(MultiNominalColAnalysisTest.class);

    private AnalysisBuilder analysisBuilder;

    private static final String[] COLUMNS = new String[] { "city", "houseowner", "occupation", "country", "marital_status", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
            "member_card" }; //$NON-NLS-1$

    private static final String[] NUMERICFUNC = new String[] {};

    private static final String CATALOG = "tbi"; //$NON-NLS-1$

    private static final String TABLE = "customer"; //$NON-NLS-1$

    private static final String DATA_PROVIDER_NAME = "My_data_provider" + System.currentTimeMillis(); //$NON-NLS-1$\

    /**
     * DOC scorreia Comment method "run".
     * 
     */
    @Test
    public void testRun() {
        String outputFolder = "ANA"; //$NON-NLS-1$
        analysisBuilder = new AnalysisBuilder();
        String analysisName = "My test analysis"; //$NON-NLS-1$

        boolean analysisInitialized = analysisBuilder.initializeAnalysis(analysisName, AnalysisType.MULTIPLE_COLUMN);
        Assert.assertTrue(analysisName + " failed to initialize!", analysisInitialized); //$NON-NLS-1$

        // get the connection
        Connection dataManager = getDataManager();
        Assert.assertNotNull("No datamanager found!", dataManager); //$NON-NLS-1$
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
        Assert.assertTrue("Problem executing analysis: " + analysisName + ": " + executed.getMessage(), executed.isOk()); //$NON-NLS-1$ //$NON-NLS-2$
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
            log.debug("Definition set for " + ind.getName() + ": " + definitionSet); //$NON-NLS-1$ //$NON-NLS-2$
        }

        for (String f : NUMERICFUNC) {
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
    @SuppressWarnings({ "null" })
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
        System.out.println("analysed Catalog: " + catalog.getName()); //$NON-NLS-1$

        List<TdTable> tables = DqRepositoryViewService.getTables(dataManager, catalog, TABLE, true, false);

        // store tables in catalog
        CatalogHelper.addTables(tables, catalog);

        Assert.assertFalse(tables.isEmpty());
        TdTable tdTable = tables.get(0);
        System.out.println("analyzed Table: " + tdTable.getName()); //$NON-NLS-1$
        List<TdColumn> columns;
        columns = DqRepositoryViewService.getColumns(dataManager, tdTable, null, true);
        // MOD scorreia 2009-01-29 columns are stored in the table
        // TableHelper.addColumns(tdTable, columns);

        Assert.assertFalse(columns.isEmpty());

        List<TdColumn> usedCols = new ArrayList<TdColumn>();
        for (TdColumn tdColumn : columns) {
            for (String c : COLUMNS) {
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

        // create connection
        ConnectionUtils.setTimeout(false);

        MetadataFillFactory instance = MetadataFillFactory.getDBInstance();
        IMetadataConnection metaConnection = instance.fillUIParams(ParameterUtil.toMap(params));

        ReturnCode rc = null;
        try {
            rc = instance.checkConnection(metaConnection);
        } catch (java.lang.RuntimeException e) {
            Assert.fail("connect to " + dbUrl + "failed," + e.getMessage());
        }
        Connection dataProvider = null;
        if (rc.isOk()) {
            dataProvider = instance.fillUIConnParams(metaConnection, null);
            dataProvider.setName(DATA_PROVIDER_NAME);

            // because the DI side code is changed, modify the following code.
            metaConnection.setCurrentConnection(dataProvider);
            try {
                ProjectNodeHelper.fillCatalogAndSchemas(metaConnection, (DatabaseConnection) dataProvider);
            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InstantiationException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
        Assert.assertNotNull(dataProvider);
        return dataProvider;
    }
}
