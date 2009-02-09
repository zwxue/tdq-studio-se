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
package org.talend.dq.indicators;

import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;
import org.talend.cwm.helper.CatalogHelper;
import org.talend.cwm.helper.DataProviderHelper;
import org.talend.cwm.relational.TdSchema;
import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.dataquality.helpers.DataqualitySwitchHelper;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.schema.CatalogIndicator;
import org.talend.dataquality.indicators.schema.SchemaFactory;
import org.talend.dataquality.indicators.schema.SchemaIndicator;
import org.talend.dq.indicators.definitions.DefinitionHandler;
import org.talend.utils.sugars.ReturnCode;
import orgomg.cwm.foundation.softwaredeployment.DataProvider;
import orgomg.cwm.resource.relational.Catalog;

/**
 * DOC scorreia class global comment. Detailled comment
 */
public class CatalogEvaluator extends AbstractSchemaEvaluator<Catalog> {

    private static Logger log = Logger.getLogger(CatalogEvaluator.class);

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.indicators.AbstractSchemaEvaluator#getDataManager()
     */
    @Override
    protected TdDataProvider getDataManager() {
        Catalog catalog = this.getAnalyzedElements().iterator().next();
        return catalog != null ? DataProviderHelper.getTdDataProvider(catalog) : null;
    }

    @Override
    protected ReturnCode executeSqlQuery(String sqlStatement) throws SQLException {
        ReturnCode ok = new ReturnCode(true);
        // --- preconditions
        DataProvider dataProvider = this.getDataManager();

        if (this.elementToIndicators.values().isEmpty()) {
            String msg = "No indicator set for the connection evaluation";
            log.error(msg);
            ok.setReturnCode(msg, false);
            return ok;
        }
        Indicator[] indics = this.getAllIndicators();
        if (indics.length == 0) {
            String msg = "No indicator for " + dataProvider;
            log.error(msg);
            ok.setReturnCode(msg, false);
            return ok;
        }
        for (Indicator indicator : indics) {
            CatalogIndicator catalogIndicator = DataqualitySwitchHelper.CATALOG_SWITCH.doSwitch(indicator);
            if (catalogIndicator == null) {
                continue;
            }
            Catalog catalog = (Catalog) catalogIndicator.getAnalyzedElement();
            String catName = catalog.getName();
            connection.setCatalog(catName);
            List<TdSchema> schemas = CatalogHelper.getSchemas(catalog);
            if (schemas.isEmpty()) { // no schema
                evalCatalogIndic(catalogIndicator, catalog, ok);
            } else {
                catalogIndicator.setAnalyzedElement(catalog);
                // --- create SchemaIndicator for each pair of catalog schema
                for (TdSchema tdSchema : schemas) {
                    // --- create SchemaIndicator for each catalog
                    SchemaIndicator schemaIndic = SchemaFactory.eINSTANCE.createSchemaIndicator();
                    // MOD xqliu 2009-1-21 feature 4715
                    DefinitionHandler.getInstance().setDefaultIndicatorDefinition(schemaIndic);
                    evalSchemaIndicLow(catalogIndicator, schemaIndic, catalog, tdSchema, ok);
                }
                catalogIndicator.setSchemaCount(schemas.size());

            }
        }
        return ok;
    }

}
