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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EClassifier;
import org.talend.cwm.builders.AbstractTableBuilder;
import org.talend.cwm.builders.TableBuilder;
import org.talend.cwm.helper.DataProviderHelper;
import org.talend.cwm.management.api.DbmsLanguage;
import org.talend.cwm.relational.TdCatalog;
import org.talend.cwm.relational.TdSchema;
import org.talend.dataquality.helpers.DataqualitySwitchHelper;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.schema.ConnectionIndicator;
import org.talend.dataquality.indicators.schema.SchemaFactory;
import org.talend.dataquality.indicators.schema.SchemaIndicator;
import org.talend.dataquality.indicators.schema.SchemaPackage;
import org.talend.utils.sugars.ReturnCode;
import orgomg.cwm.foundation.softwaredeployment.DataProvider;
import orgomg.cwm.resource.relational.NamedColumnSet;

/**
 * DOC scorreia class global comment. Detailled comment
 */
public class ConnectionEvaluator extends Evaluator<DataProvider> {

    private DbmsLanguage dbmsLanguage;

    private static final String SELECT_COUNT_FROM = "select count(*) from ";

    private static Logger log = Logger.getLogger(ConnectionEvaluator.class);

    private static final EClassifier CLASS_TYPE = SchemaPackage.eINSTANCE.getSchemaIndicator();

    // --- indicators for the connection (sum of all indicators)
    private ConnectionIndicator connectionIndicator;

    // private int connPkCount;
    //
    // private long connTotalRowCount;
    //
    // private int connIdxCount;
    //
    // private int connViewCount;
    //
    // private int connTableCount;

    private void resetCounts() {
        if (this.connectionIndicator != null) {
            boolean reset = connectionIndicator.reset();
            if (log.isDebugEnabled()) {
                log.debug("connection indicator reset: " + reset);
            }
        }
        // this.connPkCount = 0;
        // this.connTotalRowCount = 0L;
        // this.connIdxCount = 0;
        // this.connTableCount = 0;
        // this.connViewCount = 0;
    }

    private void printCounts() {
        log.info("nb table= " + connectionIndicator.getTableCount());
        log.info("nb views= " + connectionIndicator.getViewCount());
        log.info("nb index= " + connectionIndicator.getIndexCount());
        log.info("nb PK= " + connectionIndicator.getKeyCount());
        log.info("total row count= " + connectionIndicator.getTotalRowCount());
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
        assert this.getAnalyzedElements().size() == 1 : "Invalid number of analyzed elements: "
                + this.getAnalyzedElements().size();
        ReturnCode ok = new ReturnCode(true);
        // --- preconditions
        DataProvider dataProvider = this.getAnalyzedElements().iterator().next();

        // --- get data provider
        if (this.getAnalyzedElements().size() != 1) {
            String msg = "Should have only one connection to analyze instead of " + getAnalyzedElements().size();
            log.error(msg);
            ok.setReturnCode(msg, false);
            return ok;
        }

        if (this.elementToIndicators.values().isEmpty()) {
            String msg = "No indicator set for the connection evaluation";
            log.error(msg);
            ok.setReturnCode(msg, false);
            return ok;
        }
        List<Indicator> indics = this.elementToIndicators.values().iterator().next();
        if (indics.isEmpty()) {
            String msg = "No indicator for " + dataProvider;
            log.error(msg);
            ok.setReturnCode(msg, false);
            return ok;
        }

        connectionIndicator = DataqualitySwitchHelper.CONNECTION_SWITCH.doSwitch(indics.get(0));
        if (connectionIndicator == null) {
            String msg = "Connection indicator is null";
            ok.setReturnCode(msg, false);
            return ok;
        }

        this.resetCounts();

        // CatalogBuilder builder = new CatalogBuilder(connection);
        // Collection<TdCatalog> catalogs = builder.getCatalogs();
        // Collection<TdSchema> schemata = builder.getSchemata();

        List<TdCatalog> catalogs = DataProviderHelper.getTdCatalogs(dataProvider);
        List<TdSchema> schemata = DataProviderHelper.getTdSchema(dataProvider);

        String tablePattern = null; // TODO scorreia get pattern from analysis parameters

        TableBuilder tableBuilder = new TableBuilder(connection); // TODO generalize

        if (catalogs.isEmpty()) {
            for (TdSchema tdSchema : schemata) {
                evalSchemaIndic(null, tdSchema, tableBuilder, tablePattern, ok);
            }
        } else {
            for (TdCatalog tdCatalog : catalogs) {
                String catName = tdCatalog.getName();
                connection.setCatalog(catName);
                if (schemata.isEmpty()) {
                    evalSchemaIndic(tdCatalog, null, tableBuilder, tablePattern, ok);
                } else {
                    // --- create SchemaIndicator for each pair of catalog schema
                    for (TdSchema tdSchema : schemata) {
                        evalSchemaIndic(tdCatalog, tdSchema, tableBuilder, tablePattern, ok);
                    }
                }
            }
        }

        // get the analyzed schemata
        // Set<CatalogSchema> schemata = getAnalyzedElements();
        // TODO get the filters on tables

        // TODO get the tables of the schema

        // TODO create the statements for each table (count *,...)
        // // String tablePattern = "%";
        // TableType[] type = new TableType[] { TableType.TABLE, TableType.VIEW }; // TODO set types here
        //
        // for (CatalogSchema catalogSchema : schemata) {
        //
        // // get indicators
        // Collection<SchemaIndicator> indicators = EcoreUtil.getObjectsByType(this.getIndicators(catalogSchema),
        // CLASS_TYPE);
        //
        // String catalogName = catalogSchema.catalog;
        // String schemaPattern = catalogSchema.schema;
        // if (!executeOneQuery(catalogName, schemaPattern, tablePattern, type, indicators, ok).isOk()) {
        // ok.setReturnCode(ok.getMessage(), false); // TODO scorreia concatenate message...
        // }
        // }
        // // TODO execute statement and set result into indicators
        if (log.isInfoEnabled()) {
            printCounts();
        }
        return ok;
    }

    private void evalSchemaIndic(TdCatalog tdCatalog, TdSchema tdSchema,
            AbstractTableBuilder<? extends NamedColumnSet> tableBuilder, String tablePattern, ReturnCode ok) throws SQLException {
        String catName = tdCatalog != null ? tdCatalog.getName() : null;
        String schemaName = tdSchema != null ? tdSchema.getName() : null;
        // --- create SchemaIndicator for each catalog
        SchemaIndicator schemaIndic = SchemaFactory.eINSTANCE.createSchemaIndicator();
        // add it to list of indicators
        connectionIndicator.addSchemaIndicator(schemaIndic);

        // this.storeIndicator(new CatalogSchema(catName, null), schemaIndic);
        // List<Indicator> indicators = getIndicators(new CatalogSchema(catName, null));
        // indicators.get(0)
        schemaIndic.setAnalyzedElement(tdCatalog);
        int tableCount = 0;
        List<? extends NamedColumnSet> tables = tableBuilder.getColumnSets(catName, schemaName, tablePattern);
        for (NamedColumnSet t : tables) {
            String tableName = t.getName();
            tableCount++;
            getRowCount(catName, schemaName, tableName, schemaIndic, ok);
        }
        schemaIndic.setTableCount(tableCount);
    }

    // private ReturnCode executeOneQuery(String catalogName, String schemaPattern, String tablePattern, TableType[]
    // type,
    // Collection<SchemaIndicator> indicators, ReturnCode ok) throws SQLException {
    //
    // ResultSet tablesSet = getConnection().getMetaData().getTables(catalogName, schemaPattern, tablePattern,
    // TableType.getTableTypes(type));
    //
    // if (catalogName != null) {
    // connection.setCatalog(catalogName);
    // }
    // if (schemaPattern != null) {
    // connection.setCatalog(schemaPattern); // TODO scorreia check this?
    // }
    //
    // int columnCount = tablesSet.getMetaData().getColumnCount();
    // // loop on each table
    //
    // while (tablesSet.next()) {
    //
    // // --- test cube
    //
    // // end test cube
    //
    // // ResultSetUtils.printResultSet(tablesSet, 0);
    // // TODO scorreia change connection here
    //
    // // create query statement
    // String tableName = tablesSet.getString(GetTable.TABLE_NAME.name());
    // String sqlStatement = "select count(*) from " + tableName; // TODO scorreia other statements
    // queryOnTable(catalogName, schemaPattern, tableName, indicators, sqlStatement, ok);
    // }
    // // release JDBC resources
    // tablesSet.close();
    //
    // // ---- triggers
    //
    // return ok;
    // }

    /**
     * DOC scorreia Comment method "queryOnTable".
     * 
     * @param catalog
     * @param schemaPattern
     * @param schemaIndic
     * @param tablesSet
     * @param tableCube
     * @param ok
     * @throws SQLException
     */
    private void getRowCount(String catalog, String schemaPattern, String table, SchemaIndicator schemaIndic, ReturnCode ok)
            throws SQLException {
        String sqlStatement = SELECT_COUNT_FROM + table;
        // Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY,
        // ResultSet.CLOSE_CURSORS_AT_COMMIT);

        Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);

        // not needed here statement.setFetchSize(fetchSize);
        statement.execute(sqlStatement);

        // get the results
        ResultSet resultSet = statement.getResultSet();
        if (resultSet == null) {
            String mess = "No result set for this statement: " + sqlStatement;
            log.warn(mess);
            ok.setReturnCode(mess, false);
        }
        long totalRowCount = schemaIndic.getTotalRowCount();
        while (resultSet.next()) {
            // List<Indicator> indicators = getIndicators(col);
            // ResultSetUtils.printResultSet(resultSet, 0);
            // --- get content of column
            String str = String.valueOf(resultSet.getObject(1));
            Long count = Long.valueOf(str);
            totalRowCount += count;
            if (log.isDebugEnabled()) {
                log.debug(catalog + "/" + schemaPattern + "/" + table + ": " + count);

                // // --- give row to handle to indicators
                // for (SchemaIndicator indicator : indicators) {
                // indicator.setTotalRowCount(count);
                // }
            }

            // TODO scorreia give a full row to indicator (indicator will have to handle its data??

        }
        // --- release resultset
        resultSet.close();
        schemaIndic.setTotalRowCount(totalRowCount);

        // // ---- pk
        int pkCount = schemaIndic.getKeyCount();
        ResultSet pk = getConnection().getMetaData().getPrimaryKeys(catalog, schemaPattern, table);
        while (pk.next()) {
            pkCount += 1;
        }
        // ResultSetUtils.printResultSet(pk, 0);
        pk.close();
        schemaIndic.setKeyCount(pkCount);

        int idxCount = schemaIndic.getIndexCount();
        ResultSet idx = getConnection().getMetaData().getIndexInfo(catalog, schemaPattern, table, false, false);
        // TODO unicity of index could be a parameter
        while (idx.next()) {
            idxCount += 1;
        }
        idx.close();
        schemaIndic.setIndexCount(idxCount);

        // TODO triggers
        // connection.getMetaData().get

    }

}
