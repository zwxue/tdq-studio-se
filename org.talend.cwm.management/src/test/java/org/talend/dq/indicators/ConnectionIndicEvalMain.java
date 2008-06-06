// ============================================================================
//
// Copyright (C) 2006-2007 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dq.indicators;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

import org.talend.commons.emf.EMFUtil;
import org.talend.cwm.management.api.DqRepositoryViewService;
import org.talend.cwm.management.api.FolderProvider;
import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.dataquality.indicators.IndicatorsPackage;
import org.talend.dataquality.indicators.schema.ConnectionIndicator;
import org.talend.dataquality.indicators.schema.SchemaFactory;
import org.talend.dq.analysis.TestAnalysisCreation;
import org.talend.utils.properties.PropertiesLoader;
import org.talend.utils.properties.TypedProperties;
import org.talend.utils.sql.ConnectionUtils;
import org.talend.utils.sugars.ReturnCode;

/**
 * DOC scorreia class global comment. Detailled comment
 */
public class ConnectionIndicEvalMain {

    /**
     * DOC scorreia Comment method "main".
     * 
     * @param args
     */
    public static void main(String[] args) {
        TypedProperties connectionParams = PropertiesLoader.getProperties(IndicatorEvaluator.class, "db.properties");
        String driverClassName = connectionParams.getProperty("driver");
        String dbUrl = connectionParams.getProperty("url");
        try {
            // create connection
            Connection connection = ConnectionUtils.createConnection(dbUrl, driverClassName, connectionParams);

            TdDataProvider dataProvider = TestAnalysisCreation.getDataManager();

            // --- test connection evaluator
            String catalog = "test";

            ConnectionEvaluator evaluator = new ConnectionEvaluator();
            evaluator.setConnection(connection);

            // --- create indicators
            ConnectionIndicator connectionIndicator = SchemaFactory.eINSTANCE.createConnectionIndicator();
            evaluator.storeIndicator(dataProvider, connectionIndicator);

            // SchemaIndicator schemaIndic = SchemaFactory.eINSTANCE.createSchemaIndicator();
            // evaluator.storeIndicator(new CatalogSchema(catalog, null), schemaIndic);
            String sql = createSql(catalog);
            ReturnCode rc = evaluator.evaluateIndicators(sql, false);
            if (!rc.isOk()) {
                System.err.println("Failed to evaluate indicator: " + rc.getMessage());
            }

            // store in file
            File file = new File("out/myi." + IndicatorsPackage.eNAME);
            EMFUtil util = new EMFUtil();
            if (!util.addPoolToResourceSet(file.toURI().toString(), connectionIndicator)) {
                System.err.println(util.getLastErrorMessage());
            }

            File dp = new File("out");
            FolderProvider fProv = new FolderProvider();
            fProv.setFolder(dp);
            DqRepositoryViewService.saveDataProviderAndStructure(dataProvider, fProv);
            // util.addPoolToResourceSet(dp, dataProvider);
            util.save();
            ConnectionUtils.closeConnection(connection);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * DOC scorreia Comment method "createSql".
     * 
     * @param database
     * @return
     */
    private static String createSql(String database) {
        // return "select count(*) from " + database;
        return null;
    }
}
