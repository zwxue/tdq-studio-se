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

import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;
import org.talend.cwm.helper.CatalogHelper;
import org.talend.cwm.helper.DataProviderHelper;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.cwm.relational.TdCatalog;
import org.talend.cwm.relational.TdSchema;
import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.dataquality.helpers.DataqualitySwitchHelper;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.schema.CatalogIndicator;
import org.talend.dataquality.indicators.schema.ConnectionIndicator;
import org.talend.dataquality.indicators.schema.SchemaFactory;
import org.talend.dataquality.indicators.schema.SchemaIndicator;
import org.talend.dataquality.indicators.schema.util.SchemaSwitch;
import org.talend.utils.sugars.ReturnCode;
import orgomg.cwm.foundation.softwaredeployment.DataProvider;

/**
 * DOC scorreia class global comment. Detailled comment
 */
public class ConnectionEvaluator extends AbstractSchemaEvaluator<DataProvider> {

    private static Logger log = Logger.getLogger(ConnectionEvaluator.class);

    private DataProvider dataProvider;

    private List<Indicator> elementIndics;

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.indicators.AbstractSchemaEvaluator#getDataManager()
     */
    @Override
    protected TdDataProvider getDataManager() {
        DataProvider dp = this.getAnalyzedElements().iterator().next();
        if (dp != null) {
            TdDataProvider tdp = SwitchHelpers.TDDATAPROVIDER_SWITCH.doSwitch(dp);
            return tdp;
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.indicators.Evaluator#executeSqlQuery(java.lang.String)
     * 
     * Note that the given statement is not used.
     */
    @Override
    protected ReturnCode executeSqlQuery(String sqlStatement) throws SQLException {
        // assert this.getAnalyzedElements().size() == 1 : "Invalid number of analyzed elements: "
        // + this.getAnalyzedElements().size();
        ReturnCode ok = new ReturnCode(true);
        dataProvider = this.getDataManager();

        // // --- get data provider
        // if (this.getAnalyzedElements().size() != 1) {
        // String msg = "Should have only one connection to analyze instead of " + getAnalyzedElements().size();
        // log.error(msg);
        // ok.setReturnCode(msg, false);
        // return ok;
        // }

        if (this.elementToIndicators.values().isEmpty()) {
            String msg = "No indicator set for the connection evaluation";
            log.error(msg);
            ok.setReturnCode(msg, false);
            return ok;
        }
        elementIndics = this.elementToIndicators.values().iterator().next();
        if (elementIndics.isEmpty()) {
            String msg = "No indicator for " + dataProvider;
            log.error(msg);
            ok.setReturnCode(msg, false);
            return ok;
        }

        ConnectionIndicator connectionIndicator = getConnectionIndicator();
        this.resetCounts(connectionIndicator); // TODO reset other indicators

        List<TdCatalog> catalogs = DataProviderHelper.getTdCatalogs(dataProvider);

        if (catalogs.isEmpty()) { // no catalog, only schemata
            List<TdSchema> schemata = DataProviderHelper.getTdSchema(dataProvider);
            for (TdSchema tdSchema : schemata) {
                evalSchemaIndic(tdSchema, ok);
            }
        } else { // catalogs exist
            for (TdCatalog tdCatalog : catalogs) {
                String catName = tdCatalog.getName();
                connection.setCatalog(catName);
                CatalogIndicator catalogIndic = SchemaFactory.eINSTANCE.createCatalogIndicator();
                this.addToConnectionIndicator(catalogIndic);
                List<TdSchema> schemas = CatalogHelper.getSchemas(tdCatalog);
                if (schemas.isEmpty()) { // no schema
                    evalCatalogIndic(catalogIndic, tdCatalog, ok);
                } else {
                    catalogIndic.setAnalyzedElement(tdCatalog);
                    // --- create SchemaIndicator for each pair of catalog schema
                    for (TdSchema tdSchema : schemas) {
                        // --- create SchemaIndicator for each catalog
                        SchemaIndicator schemaIndic = SchemaFactory.eINSTANCE.createSchemaIndicator();
                        evalSchemaIndicLow(catalogIndic, schemaIndic, tdCatalog, tdSchema, ok);
                    }
                    catalogIndic.setSchemaCount(schemas.size());

                }
            }
        }

        if (log.isDebugEnabled()) {
            printCounts(connectionIndicator);
        }
        return ok;
    }

    protected void printCounts(ConnectionIndicator connectionIndicator) {
        if (connectionIndicator == null) {
            return;
        }
        log.info("nb table= " + connectionIndicator.getTableCount());
        log.info("nb views= " + connectionIndicator.getViewCount());
        log.info("nb index= " + connectionIndicator.getIndexCount());
        log.info("nb PK= " + connectionIndicator.getKeyCount());
        log.info("total table row count= " + connectionIndicator.getTableRowCount());
        log.info("total view row count= " + connectionIndicator.getViewRowCount());
    }

    protected void addToConnectionIndicator(Indicator indicator) {
        final ConnectionIndicator connectionIndicator = getConnectionIndicator();
        if (connectionIndicator == null) {
            return;
        }
        SchemaSwitch<Boolean> schemaSwitch = new SchemaSwitch<Boolean>() {

            /*
             * (non-Javadoc)
             * 
             * @see
             * org.talend.dataquality.indicators.schema.util.SchemaSwitch#caseCatalogIndicator(org.talend.dataquality
             * .indicators.schema.CatalogIndicator)
             */
            @Override
            public Boolean caseCatalogIndicator(CatalogIndicator object) {
                connectionIndicator.addCatalogIndicator(object);
                connectionIndicator.setCatalogCount(connectionIndicator.getCatalogCount() + 1);
                // increment schema count
                connectionIndicator.setSchemaCount(connectionIndicator.getSchemaCount() + object.getSchemaCount());
                return true;
            }

            /*
             * (non-Javadoc)
             * 
             * @see
             * org.talend.dataquality.indicators.schema.util.SchemaSwitch#caseSchemaIndicator(org.talend.dataquality
             * .indicators.schema.SchemaIndicator)
             */
            @Override
            public Boolean caseSchemaIndicator(SchemaIndicator object) {
                connectionIndicator.addSchemaIndicator(object);
                connectionIndicator.setSchemaCount(connectionIndicator.getSchemaCount() + 1);
                return true;
            }

        };
        schemaSwitch.doSwitch(indicator);
    }

    /**
     * DOC scorreia Comment method "getConnectionIndicator".
     * 
     * @param dataProvider
     * @param indics
     * @param ok
     * @return
     */
    private ConnectionIndicator getConnectionIndicator() {
        ConnectionIndicator connectionIndicator = DataqualitySwitchHelper.CONNECTION_SWITCH.doSwitch(elementIndics.get(0));
        if (connectionIndicator != null) {
            connectionIndicator.setAnalyzedElement(dataProvider);
        }
        return connectionIndicator;
    }

}
