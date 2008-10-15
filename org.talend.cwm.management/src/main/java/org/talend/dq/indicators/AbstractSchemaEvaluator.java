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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.apache.log4j.Logger;
import org.talend.cwm.builders.TableBuilder;
import org.talend.cwm.builders.ViewBuilder;
import org.talend.cwm.helper.CatalogHelper;
import org.talend.cwm.helper.DataProviderHelper;
import org.talend.cwm.management.connection.JavaSqlFactory;
import org.talend.cwm.relational.TdCatalog;
import org.talend.cwm.relational.TdSchema;
import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.dataquality.helpers.DataqualitySwitchHelper;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.schema.CatalogIndicator;
import org.talend.dataquality.indicators.schema.ConnectionIndicator;
import org.talend.dataquality.indicators.schema.SchemaFactory;
import org.talend.dataquality.indicators.schema.SchemaIndicator;
import org.talend.dataquality.indicators.schema.SchemaPackage;
import org.talend.dataquality.indicators.schema.TableIndicator;
import org.talend.dataquality.indicators.schema.ViewIndicator;
import org.talend.dataquality.indicators.schema.util.SchemaSwitch;
import org.talend.dq.dbms.DbmsLanguage;
import org.talend.dq.dbms.DbmsLanguageFactory;
import org.talend.utils.sugars.ReturnCode;
import org.talend.utils.sugars.TypedReturnCode;
import orgomg.cwm.foundation.softwaredeployment.DataManager;
import orgomg.cwm.foundation.softwaredeployment.DataProvider;
import orgomg.cwm.resource.relational.NamedColumnSet;

/**
 * DOC scorreia class global comment. Detailled comment
 * 
 * @param <T> the type of analyzed element.
 */
public abstract class AbstractSchemaEvaluator<T> extends Evaluator<T> {

    private DbmsLanguage dbmsLanguage;

    private static final String SELECT_COUNT_FROM = "select count(*) from ";

    private static Logger log = Logger.getLogger(AbstractSchemaEvaluator.class);

    /**
     * The maximum number of exception before we restart the connection in order to release correctly the cursors.
     */
    private static final int MAX_EXCEPTION = 20;

    private int nbExceptions = 0;

    // TODO scorreia use catalog pattern to filter analysis
    private String catalogPattern = null;

    // TODO scorreia use schema pattern to filter analysis
    private String schemaPattern = null;

    private String tablePattern = null;

    private String viewPattern = null;

    protected DbmsLanguage dbms() {
        if (this.dbmsLanguage == null) {
            DataManager dm = this.getDataManager();
            if (dm == null) {
                throw new RuntimeException("No data manager found."); // FIXME use TalendException ?
            }
            this.dbmsLanguage = DbmsLanguageFactory.createDbmsLanguage(dm);
            // this.dbmsLanguage.setDbQuoteString(this.dbmsLanguage.getQuoteIdentifier());
        }
        return this.dbmsLanguage;
    }

    protected abstract TdDataProvider getDataManager();

    private void resetCounts(final ConnectionIndicator connectionIndicator) {
        if (connectionIndicator != null) {
            boolean reset = connectionIndicator.reset();
            if (log.isDebugEnabled()) {
                log.debug("connection indicator reset: " + reset);
            }
        }
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
    protected void evalCatalogIndic(final ConnectionIndicator connectionIndicator, final CatalogIndicator catalogIndic,
            TdCatalog tdCatalog, ReturnCode ok) throws SQLException {
        this.evalSchemaIndicLow(connectionIndicator, null, catalogIndic, tdCatalog, null, ok);
    }

    /**
     * DOC scorreia Comment method "evalSchemaIndic".
     * 
     * @param tdSchema
     * @param tableBuilder
     * @param tablePattern
     * @param ok
     * @throws SQLException
     */
    protected void evalSchemaIndic(final ConnectionIndicator connectionIndicator, TdSchema tdSchema, ReturnCode ok)
            throws SQLException {
        // --- create SchemaIndicator for each catalog
        SchemaIndicator schemaIndic = SchemaFactory.eINSTANCE.createSchemaIndicator();
        this.evalSchemaIndicLow(connectionIndicator, null, schemaIndic, null, tdSchema, ok);
    }

    protected void evalSchemaIndicLow(final ConnectionIndicator connectionIndicator, final CatalogIndicator catalogIndic,
            final SchemaIndicator schemaIndic, final TdCatalog tdCatalog, final TdSchema tdSchema, ReturnCode ok)
            throws SQLException {
        boolean hasSchema = tdSchema != null;
        boolean hasCatalog = tdCatalog != null;

        String catName = hasCatalog ? tdCatalog.getName() : null;
        String schemaName = hasSchema ? tdSchema.getName() : null;

        schemaIndic.setAnalyzedElement(hasSchema ? tdSchema : tdCatalog);

        // profile tables
        TableBuilder tableBuilder = new TableBuilder(connection);
        int tableCount = 0;
        List<? extends NamedColumnSet> tables = tableBuilder.getColumnSets(catName, schemaName, tablePattern);
        for (NamedColumnSet t : tables) {
            tableCount++;
            evalAllCounts(catName, schemaName, t, schemaIndic, true, ok);
        }
        schemaIndic.setTableCount(tableCount);

        // do the same for views
        ViewBuilder viewBuilder = new ViewBuilder(connection);
        int viewCount = 0;
        List<? extends NamedColumnSet> views = viewBuilder.getColumnSets(catName, schemaName, viewPattern);
        for (NamedColumnSet t : views) {
            viewCount++;
            evalAllCounts(catName, schemaName, t, schemaIndic, false, ok);
        }
        schemaIndic.setViewCount(viewCount);

        if (hasCatalog && hasSchema) {
            // add it to list of indicators
            this.addToConnectionIndicator(connectionIndicator, catalogIndic);

            // add it to list of indicators
            catalogIndic.addSchemaIndicator(schemaIndic);

            // --- increment values of catalog indicator
            catalogIndic.setTableCount(catalogIndic.getTableCount() + tableCount);
            catalogIndic.setTableRowCount(catalogIndic.getTableRowCount() + schemaIndic.getTableRowCount());
            catalogIndic.setViewRowCount(catalogIndic.getViewRowCount() + schemaIndic.getViewRowCount());

        } else if (!hasCatalog) { // has schema only
            // add it to list of indicators
            this.addToConnectionIndicator(connectionIndicator, schemaIndic);
        } else if (!hasSchema) { // has catalog only
            if (SchemaPackage.eINSTANCE.getCatalogIndicator().equals(schemaIndic.eClass())) {
                this.addToConnectionIndicator(connectionIndicator, schemaIndic);
            } else {
                log.error("This should not happen. No catatog and no schema.");
            }
        }
    }

    private void addToConnectionIndicator(final ConnectionIndicator connectionIndicator, Indicator indicator) {
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
     * DOC scorreia Comment method "queryOnTable".
     * 
     * @param catalog
     * @param schema
     * @param schemaIndic
     * @param tablesSet
     * @param tableCube
     * @param ok
     * @throws SQLException
     */
    private void evalAllCounts(String catalog, String schema, NamedColumnSet t, SchemaIndicator schemaIndic, boolean isTable,
            ReturnCode ok) throws SQLException {
        String quCatalog = catalog == null ? null : dbms().quote(catalog);
        String quSchema = schema == null ? null : dbms().quote(schema);
        String quTable = dbms().quote(t.getName());

        if (isTable) {
            long rowCount = getRowCounts(quCatalog, quSchema, quTable);
            schemaIndic.setTableRowCount(schemaIndic.getTableRowCount() + rowCount);

            // ---- pk
            int pkCount = getPKCount(quCatalog, quSchema, quTable);
            schemaIndic.setKeyCount(schemaIndic.getKeyCount() + pkCount);

            // indexes
            int idxCount = getIndexCount(quCatalog, quSchema, quTable);
            schemaIndic.setIndexCount(schemaIndic.getIndexCount() + idxCount);

            // create Table Indicator
            // TODO create tableindicator only if it's in top N or in bottom N (use an option?)
            createTableIndicator(t, schemaIndic, rowCount, pkCount, idxCount);
        } else { // is a view TODO probably need to handle system tables separately
            long rowCount = getRowCounts(quCatalog, quSchema, quTable);
            schemaIndic.setViewRowCount(schemaIndic.getViewRowCount() + rowCount);
            createViewIndicator(t, schemaIndic, rowCount);
        }
        // --- triggers (JDBC API cannot get triggers)

    }

    /**
     * DOC scorreia Comment method "createViewIndicator".
     * 
     * @param t
     * @param schemaIndic
     * @param rowCount
     */
    private void createViewIndicator(NamedColumnSet t, SchemaIndicator schemaIndic, long rowCount) {
        ViewIndicator viewIndicator = SchemaFactory.eINSTANCE.createViewIndicator();
        // t is not stored in xmi file. tableIndicator.setAnalyzedElement(t);
        viewIndicator.setTableName(t.getName());
        viewIndicator.setRowCount(rowCount);
        schemaIndic.addViewIndicator(viewIndicator);
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
        // --- preconditions
        DataProvider dataProvider = this.getDataManager();

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
        List<Indicator> indics = this.elementToIndicators.values().iterator().next();
        if (indics.isEmpty()) {
            String msg = "No indicator for " + dataProvider;
            log.error(msg);
            ok.setReturnCode(msg, false);
            return ok;
        }

        ConnectionIndicator connectionIndicator = getConnectionIndicator(dataProvider, indics);
        this.resetCounts(connectionIndicator); // TODO reset other indicators

        List<TdCatalog> catalogs = DataProviderHelper.getTdCatalogs(dataProvider);

        if (catalogs.isEmpty()) { // no catalog, only schemata
            List<TdSchema> schemata = DataProviderHelper.getTdSchema(dataProvider);
            for (TdSchema tdSchema : schemata) {
                evalSchemaIndic(connectionIndicator, tdSchema, ok);
            }
        } else { // catalogs exist
            for (TdCatalog tdCatalog : catalogs) {
                String catName = tdCatalog.getName();
                connection.setCatalog(catName);
                CatalogIndicator catalogIndic = SchemaFactory.eINSTANCE.createCatalogIndicator();
                this.addToConnectionIndicator(connectionIndicator, catalogIndic);
                List<TdSchema> schemas = CatalogHelper.getSchemas(tdCatalog);
                if (schemas.isEmpty()) { // no schema
                    evalCatalogIndic(connectionIndicator, catalogIndic, tdCatalog, ok);
                } else {
                    catalogIndic.setAnalyzedElement(tdCatalog);
                    // --- create SchemaIndicator for each pair of catalog schema
                    for (TdSchema tdSchema : schemas) {
                        // --- create SchemaIndicator for each catalog
                        SchemaIndicator schemaIndic = SchemaFactory.eINSTANCE.createSchemaIndicator();
                        evalSchemaIndicLow(connectionIndicator, catalogIndic, schemaIndic, tdCatalog, tdSchema, ok);
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

    /**
     * DOC scorreia Comment method "getConnectionIndicator".
     * 
     * @param dataProvider
     * @param indics
     * @param ok
     * @return
     */
    protected ConnectionIndicator getConnectionIndicator(DataProvider dataProvider, List<Indicator> indics) {
        ConnectionIndicator connectionIndicator = DataqualitySwitchHelper.CONNECTION_SWITCH.doSwitch(indics.get(0));
        if (connectionIndicator != null) {
            connectionIndicator.setAnalyzedElement(dataProvider);
        }
        return connectionIndicator;
    }

    /**
     * DOC scorreia Comment method "createTableIndicator".
     * 
     * @param t
     * @param schemaIndic
     * @param rowCount
     * @param pkCount
     * @param idxCount
     */
    private void createTableIndicator(NamedColumnSet t, SchemaIndicator schemaIndic, long rowCount, int pkCount, int idxCount) {
        TableIndicator tableIndicator = SchemaFactory.eINSTANCE.createTableIndicator();
        // t is not stored in xmi file. tableIndicator.setAnalyzedElement(t);
        tableIndicator.setTableName(t.getName());
        tableIndicator.setRowCount(rowCount);
        tableIndicator.setKeyCount(pkCount);
        tableIndicator.setIndexCount(idxCount);
        schemaIndic.addTableIndicator(tableIndicator);
    }

    /**
     * DOC scorreia Comment method "getIndexCount".
     * 
     * @param quCatalog
     * @param quSchema
     * @param quTable
     * @param idxCount
     * @return
     * @throws SQLException
     */
    private int getIndexCount(String quCatalog, String quSchema, String quTable) throws SQLException {
        int idxCount = 0;
        ResultSet idx = null;
        try {
            idx = getConnection().getMetaData().getIndexInfo(quCatalog, quSchema, quTable, false, false);
        } catch (SQLException e) {
            log.warn("Exception while getting indexes on " + this.dbms().toQualifiedName(quCatalog, quSchema, quTable) + ": "
                    + e.getLocalizedMessage(), e);
            // Oracle increments the number of cursors to close each time a new query is executed after this exception!
            reloadConnectionAfterException(quCatalog);
        }
        // TODO unicity of index could be a parameter
        if (idx != null) {
            while (idx.next()) {
                idxCount += 1;
            }
            idx.close();
        }
        return idxCount;
    }

    /**
     * DOC scorreia Comment method "getPKCount".
     * 
     * @param quCatalog
     * @param quSchema
     * @param quTable
     * @param pkCount
     * @return
     * @throws SQLException
     */
    private int getPKCount(String quCatalog, String quSchema, String quTable) throws SQLException {
        int pkCount = 0;
        ResultSet pk = null;
        try {
            pk = getConnection().getMetaData().getPrimaryKeys(quCatalog, quSchema, quTable);
        } catch (SQLException e1) {
            log.warn("Exception while getting primary keys on " + this.dbms().toQualifiedName(quCatalog, quSchema, quTable)
                    + ": " + e1.getLocalizedMessage(), e1);
            reloadConnectionAfterException(quCatalog);
        }
        if (pk != null) {
            while (pk.next()) {
                pkCount += 1;
            }
            // ResultSetUtils.printResultSet(pk, 0);
            pk.close();
        }
        return pkCount;
    }

    /**
     * DOC scorreia Comment method "reloadConnection".
     * 
     * @param catalog
     */
    protected void reloadConnectionAfterException(String catalog) {
        nbExceptions++;
        if (nbExceptions < MAX_EXCEPTION) {
            return; // not yet
        }
        ReturnCode connClosed = super.closeConnection();
        if (!connClosed.isOk()) {
            log.error("Problem reloading connection: " + connClosed.getMessage());
        }
        TdDataProvider dp = this.getDataManager();
        TypedReturnCode<Connection> conn = JavaSqlFactory.createConnection(dp);
        if (!conn.isOk()) {
            log.error(conn.getMessage());
            return;
        }
        // else ok
        this.setConnection(conn.getObject());
        this.selectCatalog(catalog);

        // reset the number of exceptions
        nbExceptions = 0;
    }

    /**
     * DOC scorreia Comment method "getRowCounts".
     * 
     * @param schemaIndic
     * @param quCatalog
     * @param quSchema
     * @param quTable
     * @return
     * @throws SQLException
     */
    private long getRowCounts(String quCatalog, String quSchema, String quTable) throws SQLException {
        String sqlStatement = SELECT_COUNT_FROM + dbms().toQualifiedName(quCatalog, quSchema, quTable);
        // Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY,
        // ResultSet.CLOSE_CURSORS_AT_COMMIT);

        Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);

        long totalRowCount = 0;
        // not needed here statement.setFetchSize(fetchSize);
        try {
            if (log.isInfoEnabled()) {
                log.info("Executing SQL statement: " + sqlStatement);
            }
            statement.execute(sqlStatement);
        } catch (SQLException e) {
            statement.close();
            log.warn(e.getMessage() + " for SQL statement: " + sqlStatement);
            if (log.isDebugEnabled()) {
                log.debug(e, e);
            }
            // some tables on Oracle give the following exception:
            // ORA-25191: cannot reference overflow table of an index-organized table
            reloadConnectionAfterException(quCatalog);
            return totalRowCount;
        }

        // get the results
        ResultSet resultSet = statement.getResultSet();
        if (resultSet == null) {
            String mess = "No result set for this statement: " + sqlStatement;
            log.warn(mess);
        } else {
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
        }

        // -- release JDBC resources
        statement.close();
        return totalRowCount;
    }

    /**
     * Getter for tablePattern.
     * 
     * @return the tablePattern
     */
    public String getTablePattern() {
        return this.tablePattern;
    }

    /**
     * Sets the tablePattern.
     * 
     * @param tablePattern the tablePattern to set
     */
    public void setTablePattern(String tablePattern) {
        this.tablePattern = tablePattern;
    }

    /**
     * Getter for viewPattern.
     * 
     * @return the viewPattern
     */
    public String getViewPattern() {
        return this.viewPattern;
    }

    /**
     * Sets the viewPattern.
     * 
     * @param viewPattern the viewPattern to set
     */
    public void setViewPattern(String viewPattern) {
        this.viewPattern = viewPattern;
    }

    /**
     * Getter for catalogPattern.
     * 
     * @return the catalogPattern
     */
    public String getCatalogPattern() {
        return this.catalogPattern;
    }

    /**
     * Sets the catalogPattern.
     * 
     * @param catalogPattern the catalogPattern to set
     */
    public void setCatalogPattern(String catalogPattern) {
        this.catalogPattern = catalogPattern;
    }

    /**
     * Getter for schemaPattern.
     * 
     * @return the schemaPattern
     */
    public String getSchemaPattern() {
        return this.schemaPattern;
    }

    /**
     * Sets the schemaPattern.
     * 
     * @param schemaPattern the schemaPattern to set
     */
    public void setSchemaPattern(String schemaPattern) {
        this.schemaPattern = schemaPattern;
    }

}
