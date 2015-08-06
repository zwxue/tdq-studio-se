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
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.metadata.builder.database.DqRepositoryViewService;
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
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.IndicatorsFactory;
import org.talend.dataquality.indicators.RowCountIndicator;
import org.talend.dataquality.indicators.schema.SchemaFactory;
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
public class ConnectionAnalysisCreationMain {

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
        String outputFolder = "ANA";
        // initialized analysis
        AnalysisBuilder analysisBuilder = new AnalysisBuilder();
        String analysisName = "My Connection analysis";

        boolean analysisInitialized = analysisBuilder.initializeAnalysis(analysisName, AnalysisType.CONNECTION);
        Assert.assertTrue(analysisName + " failed to initialize!", analysisInitialized);

        // get the connection
        Connection dataManager = getDataManager();
        Assert.assertNotNull("No datamanager found!", dataManager);
        analysisBuilder.setAnalysisConnection(dataManager);

        Indicator indicator = SchemaFactory.eINSTANCE.createConnectionIndicator();
        indicator.setAnalyzedElement(dataManager);
        analysisBuilder.addElementToAnalyze(dataManager, indicator);

        // get the domain constraint
        Domain dataFilter = getDataFilter(dataManager); // CAST here for test
        analysisBuilder.addFilterOnData(dataFilter);

        // TODO scorreia save domain with analysisbuilder?
        FolderProvider folderProvider = new FolderProvider();
        folderProvider.setFolder(new File(outputFolder));

        // run analysis
        Analysis analysis = analysisBuilder.getAnalysis();
        IAnalysisExecutor exec = new ConnectionAnalysisExecutor();
        ReturnCode executed = exec.execute(analysis);
        Assert.assertTrue("Problem executing analysis: '" + analysisName + "': " + executed.getMessage(), executed.isOk());

        // save data provider
        ElementWriterFactory.getInstance().createDataProviderWriter().create(dataManager, folderProvider.getFolderResource());

        // save analysis, need workspace context
        // AnalysisWriter writer = new AnalysisWriter();
        // File file = new File(outputFolder + File.separator + "connection.ana");
        // ReturnCode saved = writer.save(analysis, file);
        // Assert.assertTrue(saved.getMessage(), saved.isOk());
        // if (saved.isOk()) {
        // log.info("Saved in " + file.getAbsolutePath());
        // }
    }

    /**
     * DOC scorreia Comment method "getDataFilter".
     * 
     * @param dataManager
     * @param column
     * @return
     */
    private static Domain getDataFilter(Connection dataManager) {
        Domain domain = DOMAIN.createDomain();
        RangeRestriction rangeRestriction = DOMAIN.createRangeRestriction();
        domain.getRanges().add(rangeRestriction);

        return domain;
    }

    /**
     * DOC scorreia Comment method "getExpression".
     * 
     * @param column
     * 
     * @return
     */
    private static BooleanExpressionNode getExpression(TdColumn column) {
        CwmZExpression<String> expre = new CwmZExpression<String>(SqlPredicate.EQUAL);
        expre.setOperands(column, "sunny");
        return expre.generateExpressions();
    }

    private static Logger log = Logger.getLogger(ConnectionAnalysisCreationMain.class);

    /**
     * DOC scorreia Comment method "getIndicators".
     * 
     * @param column
     * @return
     */
    private static Indicator[] getIndicators(ModelElement column) {
        RowCountIndicator rowCountIndicator = IndicatorsFactory.eINSTANCE.createRowCountIndicator();
        rowCountIndicator.setAnalyzedElement(column);
        return new Indicator[] { rowCountIndicator };
    }

    /**
     * DOC scorreia Comment method "getColumn".
     * 
     * @param dataManager
     * @return
     * @throws Exception
     */
    private static ModelElement getColumn(Connection dataManager) throws Exception {
        List<Catalog> tdCatalogs = CatalogHelper.getCatalogs(dataManager.getDataPackage());
        System.out.println("Catalogs: " + tdCatalogs);
        Assert.assertFalse(tdCatalogs.isEmpty());
        Catalog catalog = tdCatalogs.get(0);
        Assert.assertNotNull(catalog);
        System.out.println("analysed Catalog: " + catalog.getName());
        List<TdTable> tables = DqRepositoryViewService.getTables(dataManager, catalog, null, true);

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
        TdColumn col = columns.get(0);
        System.out.println("analyzed Column: " + col.getName());
        return col;
    }

    /**
     * DOC scorreia Comment method "getDataManager".
     * 
     * @return
     */
    public static Connection getDataManager() {
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

        // create connection

        Connection dataProvider = ConnectionService.createConnection(params).getObject();

        dataProvider.setName("My data provider");
        return dataProvider;

    }
}
