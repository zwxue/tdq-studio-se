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
import org.talend.cwm.helper.CatalogHelper;
import org.talend.cwm.helper.DataProviderHelper;
import org.talend.cwm.management.api.DbmsLanguage;
import org.talend.cwm.management.api.DbmsLanguageFactory;
import org.talend.cwm.relational.TdCatalog;
import org.talend.cwm.relational.TdSchema;
import org.talend.dataquality.helpers.DataqualitySwitchHelper;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.schema.CatalogIndicator;
import org.talend.dataquality.indicators.schema.ConnectionIndicator;
import org.talend.dataquality.indicators.schema.SchemaFactory;
import org.talend.dataquality.indicators.schema.SchemaIndicator;
import org.talend.dataquality.indicators.schema.SchemaPackage;
import org.talend.utils.sugars.ReturnCode;
import orgomg.cwm.foundation.softwaredeployment.DataManager;
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

    private DbmsLanguage dbms() {
        if (this.dbmsLanguage == null) {
            DataManager dm = this.getAnalyzedElements().iterator().next();
            this.dbmsLanguage = DbmsLanguageFactory.createDbmsLanguage(dm);
            this.dbmsLanguage.setDbQuoteString(this.dbmsLanguage.getQuoteIdentifier());
        }
        return this.dbmsLanguage;
    }

    private void resetCounts() {
        if (this.connectionIndicator != null) {
            boolean reset = connectionIndicator.reset();
            if (log.isDebugEnabled()) {
                log.debug("connection indicator reset: " + reset);
            }
        }
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

        connectionIndicator.setAnalyzedElement(dataProvider);
        this.resetCounts();

        // CatalogBuilder builder = new CatalogBuilder(connection);
        // Collection<TdCatalog> catalogs = builder.getCatalogs();
        // Collection<TdSchema> schemata = builder.getSchemata();

        List<TdCatalog> catalogs = DataProviderHelper.getTdCatalogs(dataProvider);

        String tablePattern = null; // TODO scorreia get pattern from analysis parameters

        TableBuilder tableBuilder = new TableBuilder(connection); // TODO generalize

        if (catalogs.isEmpty()) { // no catalog, only schemata
            List<TdSchema> schemata = DataProviderHelper.getTdSchema(dataProvider);
            for (TdSchema tdSchema : schemata) {
                evalSchemaIndic(this.connectionIndicator, tdSchema, tableBuilder, tablePattern, ok);
            }
            this.connectionIndicator.setSchemaCount(schemata.size());
        } else { // catalogs exist
            for (TdCatalog tdCatalog : catalogs) {
                String catName = tdCatalog.getName();
                connection.setCatalog(catName);
                List<TdSchema> schemas = CatalogHelper.getSchemas(tdCatalog);
                if (schemas.isEmpty()) { // no schema
                    evalCatalogIndic(this.connectionIndicator, tdCatalog, tableBuilder, tablePattern, ok);
                } else {
                    CatalogIndicator catalogIndic = SchemaFactory.eINSTANCE.createCatalogIndicator();
                    catalogIndic.setAnalyzedElement(tdCatalog);
                    this.connectionIndicator.addCatalogIndicator(catalogIndic);
                    // --- create SchemaIndicator for each pair of catalog schema
                    for (TdSchema tdSchema : schemas) {
                        evalSchemaIndic(connectionIndicator, catalogIndic, tdCatalog, tdSchema, tableBuilder, tablePattern, ok);
                    }
                    // increment schema count
                    this.connectionIndicator.setSchemaCount(this.connectionIndicator.getSchemaCount() + schemas.size());
                }
            }
            // set nb catalogs
            this.connectionIndicator.setCatalogCount(catalogs.size());
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

    /**
     * DOC scorreia Comment method "evalSchemaIndic".
     * 
     * @param connIndicator
     * @param tdCatalog
     * @param tableBuilder
     * @param tablePattern
     * @param ok
     * @throws SQLException
     */
    private void evalCatalogIndic(ConnectionIndicator connIndicator, TdCatalog tdCatalog, TableBuilder tableBuilder,
            String tablePattern, ReturnCode ok) throws SQLException {
        String catName = tdCatalog != null ? tdCatalog.getName() : null;
        // --- create CatalogIndicator for each catalog
        CatalogIndicator catalogIndic = SchemaFactory.eINSTANCE.createCatalogIndicator();
        // add it to list of indicators
        connIndicator.addCatalogIndicator(catalogIndic);

        // this.storeIndicator(new CatalogSchema(catName, null), schemaIndic);
        // List<Indicator> indicators = getIndicators(new CatalogSchema(catName, null));
        // indicators.get(0)
        catalogIndic.setAnalyzedElement(tdCatalog);
        int tableCount = 0;
        List<? extends NamedColumnSet> tables = tableBuilder.getColumnSets(catName, null, tablePattern);
        for (NamedColumnSet t : tables) {
            String tableName = t.getName();
            tableCount++;
            getRowCount(catName, null, tableName, catalogIndic, ok);
        }
        catalogIndic.setTableCount(tableCount);
    }

    /**
     * DOC scorreia Comment method "evalSchemaIndic".
     * 
     * @param connIndicator
     * @param tdSchema
     * @param tableBuilder
     * @param tablePattern
     * @param ok
     * @throws SQLException
     */
    private void evalSchemaIndic(ConnectionIndicator connIndicator, TdSchema tdSchema,
            AbstractTableBuilder<? extends NamedColumnSet> tableBuilder, String tablePattern, ReturnCode ok) throws SQLException {
        String schemaName = tdSchema != null ? tdSchema.getName() : null;
        // --- create SchemaIndicator for each catalog
        SchemaIndicator schemaIndic = SchemaFactory.eINSTANCE.createSchemaIndicator();
        // add it to list of indicators
        connIndicator.addSchemaIndicator(schemaIndic);

        // this.storeIndicator(new CatalogSchema(catName, null), schemaIndic);
        // List<Indicator> indicators = getIndicators(new CatalogSchema(catName, null));
        // indicators.get(0)
        schemaIndic.setAnalyzedElement(tdSchema);
        int tableCount = 0;
        List<? extends NamedColumnSet> tables = tableBuilder.getColumnSets(null, schemaName, tablePattern);
        for (NamedColumnSet t : tables) {
            String tableName = t.getName();
            tableCount++;
            getRowCount(null, schemaName, tableName, schemaIndic, ok);
        }
        schemaIndic.setTableCount(tableCount);
    }

    private void evalSchemaIndic(ConnectionIndicator connIndicator, CatalogIndicator catalogIndic, TdCatalog tdCatalog,
            TdSchema tdSchema, AbstractTableBuilder<? extends NamedColumnSet> tableBuilder, String tablePattern, ReturnCode ok)
            throws SQLException {
        String catName = tdCatalog != null ? tdCatalog.getName() : null;
        String schemaName = tdSchema != null ? tdSchema.getName() : null;

        // --- create SchemaIndicator for each catalog
        SchemaIndicator schemaIndic = SchemaFactory.eINSTANCE.createSchemaIndicator();
        // add it to list of indicators
        catalogIndic.addSchemaIndicator(schemaIndic);

        // this.storeIndicator(new CatalogSchema(catName, null), schemaIndic);
        // List<Indicator> indicators = getIndicators(new CatalogSchema(catName, null));
        // indicators.get(0)
        schemaIndic.setAnalyzedElement(tdSchema != null ? tdSchema : tdCatalog);
        int tableCount = 0;
        List<? extends NamedColumnSet> tables = tableBuilder.getColumnSets(catName, schemaName, tablePattern);
        for (NamedColumnSet t : tables) {
            String tableName = t.getName();
            tableCount++;
            getRowCount(catName, schemaName, tableName, schemaIndic, ok);
        }
        schemaIndic.setTableCount(tableCount);

        // --- increment values of catalog indicator
        catalogIndic.setTableCount(catalogIndic.getTableCount() + tableCount);
        catalogIndic.setTotalRowCount(catalogIndic.getTotalRowCount() + schemaIndic.getTotalRowCount());
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
        String quCatalog = catalog == null ? null : dbms().quote(catalog);
        String quSchema = schemaPattern == null ? null : dbms().quote(schemaPattern);
        String quTable = dbms().quote(table);

        String sqlStatement = SELECT_COUNT_FROM + dbms().toQualifiedName(quCatalog, quSchema, quTable);
        // Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY,
        // ResultSet.CLOSE_CURSORS_AT_COMMIT);

        Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);

        // not needed here statement.setFetchSize(fetchSize);
        try {
            if (log.isInfoEnabled()) {
                log.info("Executing SQL statement: " + sqlStatement);
            }
            statement.execute(sqlStatement);
        } catch (SQLException e) {
            log.warn(e.getMessage() + " for SQL statement: " + sqlStatement);
            if (log.isDebugEnabled()) {
                log.debug(e, e);
            }
            // some tables on Oracle give the following exception:
            // ORA-25191: cannot reference overflow table of an index-organized table
            ok.setReturnCode(e.getLocalizedMessage(), false);
            statement.close();
            return;
        }

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
                log.debug(quCatalog + "/" + quSchema + "/" + quTable + ": " + count);

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
        ResultSet pk = null;
        try {
            pk = getConnection().getMetaData().getPrimaryKeys(quCatalog, quSchema, quTable);
        } catch (SQLException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        if (pk != null) {
            while (pk.next()) {
                pkCount += 1;
            }
            // ResultSetUtils.printResultSet(pk, 0);
            pk.close();
        }
        schemaIndic.setKeyCount(pkCount);

        int idxCount = schemaIndic.getIndexCount();
        ResultSet idx = null;
        try {
            idx = getConnection().getMetaData().getIndexInfo(quCatalog, quSchema, quTable, false, false);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // TODO unicity of index could be a parameter
        if (idx != null) {
            while (idx.next()) {
                idxCount += 1;
            }
            idx.close();
        }
        schemaIndic.setIndexCount(idxCount);

        // --- triggers (JDBC API cannot get triggers)

        // -- release JDBC resources
        statement.close();
    }

}
