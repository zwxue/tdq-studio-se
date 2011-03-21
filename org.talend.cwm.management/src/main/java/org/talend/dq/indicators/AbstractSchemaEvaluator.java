// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EObject;
import org.talend.core.model.metadata.MetadataFillFactory;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.metadata.builder.database.JavaSqlFactory;
import org.talend.core.model.metadata.builder.database.TableBuilder;
import org.talend.core.model.metadata.builder.database.ViewBuilder;
import org.talend.cwm.db.connection.ConnectionUtils;
import org.talend.cwm.helper.ColumnSetHelper;
import org.talend.cwm.helper.ConnectionHelper;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.cwm.management.i18n.Messages;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.schema.CatalogIndicator;
import org.talend.dataquality.indicators.schema.SchemaFactory;
import org.talend.dataquality.indicators.schema.SchemaIndicator;
import org.talend.dataquality.indicators.schema.SchemaPackage;
import org.talend.dataquality.indicators.schema.TableIndicator;
import org.talend.dataquality.indicators.schema.ViewIndicator;
import org.talend.dq.dbms.DbmsLanguage;
import org.talend.dq.dbms.DbmsLanguageFactory;
import org.talend.dq.helper.ListUtils;
import org.talend.dq.indicators.definitions.DefinitionHandler;
import org.talend.utils.sugars.ReturnCode;
import org.talend.utils.sugars.TypedReturnCode;
import orgomg.cwm.foundation.softwaredeployment.DataManager;
import orgomg.cwm.foundation.softwaredeployment.DataProvider;
import orgomg.cwm.resource.relational.Catalog;
import orgomg.cwm.resource.relational.NamedColumnSet;
import orgomg.cwm.resource.relational.Schema;

/**
 * DOC scorreia class global comment. Detailled comment
 * 
 * @param <T> the type of analyzed element.
 */
public abstract class AbstractSchemaEvaluator<T> extends Evaluator<T> {

    /**
     * 
     */
    public static final char FILTER_SEP = ',';

    private DbmsLanguage dbmsLanguage;

    private static final String SELECT_COUNT_FROM = "select count(*) from "; //$NON-NLS-1$

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

    protected String tablePattern = null;

    protected String viewPattern = null;

    protected DbmsLanguage dbms() {
        if (this.dbmsLanguage == null) {
            DataManager dm = this.getDataManager();
            if (dm == null) {
                throw new RuntimeException("No data manager found."); // FIXME use TalendException ? //$NON-NLS-1$
            }
            this.dbmsLanguage = DbmsLanguageFactory.createDbmsLanguage(dm);
            // this.dbmsLanguage.setDbQuoteString(this.dbmsLanguage.getQuoteIdentifier());
        }
        return this.dbmsLanguage;
    }

    protected abstract Connection getDataManager();

    private Set<String> catalogsName = new HashSet<String>();

    private Set<String> schemasName = new HashSet<String>();

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
    protected void evalAllCounts(String catalog, String schema, NamedColumnSet t, SchemaIndicator schemaIndic, boolean isTable,
            ReturnCode ok) throws SQLException {
        // String quCatalog = catalog == null ? null : dbms().quote(catalog);
        // MOD klliu 2011-02-17 bug 18961
        EObject eContainer = schemaIndic.getAnalyzedElement().eContainer();
        String quCatalog = null;
        if (eContainer instanceof Catalog) {
            quCatalog = dbms().quote(((Catalog) eContainer).getName());
        }
        // String quCatalog = catalog == null ? null : dbms().quote(catalog);
        String quSchema = schema == null ? null : dbms().quote(schema);
        final String table = t.getName();
        String quTable = dbms().quote(table);

        if (isTable) {
            long rowCount = getRowCounts(quCatalog, quSchema, quTable);
            schemaIndic.setTableRowCount(schemaIndic.getTableRowCount() + rowCount);
            // MOD by zshen: change schemaName of sybase database to Table's owner.
            if (ConnectionUtils.isSybaseeDBProducts(dbmsLanguage.getDbmsName())) {
                schema = ColumnSetHelper.getTableOwner(t);
            }
            // ~11934
            // ---- pk
            int pkCount = getPKCount(catalog, schema, table);
            schemaIndic.setKeyCount(schemaIndic.getKeyCount() + pkCount);

            // TODO get imported/exported keys
            // getConnection().getMetaData().getImportedKeys(catalog, schema, table);
            // getConnection().getMetaData().getExportedKeys(catalog, schema, table);

            // indexes
            int idxCount = getIndexCount(catalog, schema, table);
            schemaIndic.setIndexCount(schemaIndic.getIndexCount() + idxCount);

            // create Table Indicator
            // TODO create tableindicator only if it's in top N or in bottom N (use an option?)
            createTableIndicator(t, schemaIndic, rowCount, pkCount, idxCount);
        } else { // is a view TODO probably need to handle system tables separately
            if (ConnectionUtils.isSybaseeDBProducts(dbmsLanguage.getDbmsName())) {
                schema = ColumnSetHelper.getTableOwner(t);
            }
            long rowCount = getRowCounts(quCatalog, schema, quTable);
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
        // MOD xqliu 2009-1-21 feature 4715
        DefinitionHandler.getInstance().setDefaultIndicatorDefinition(viewIndicator);
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
    protected abstract ReturnCode executeSqlQuery(String sqlStatement) throws SQLException;

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
        // MOD xqliu 2009-1-21 feature 4715
        DefinitionHandler.getInstance().setDefaultIndicatorDefinition(tableIndicator);
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
     * @param catalog
     * @param schema
     * @param table
     * @param idxCount
     * @return
     * @throws SQLException
     */
    private int getIndexCount(String catalog, String schema, String table) throws SQLException {
        int idxCount = 0;
        ResultSet idx = null;
        try {
            // MOD xqliu 2009-07-13 bug 7888
            idx = org.talend.utils.sql.ConnectionUtils.getConnectionMetadata(getConnection()).getIndexInfo(catalog, schema,
                    table, false, false);
            // ~
        } catch (SQLException e) {
            log.warn(
                    "Exception while getting indexes on " + this.dbms().toQualifiedName(catalog, schema, table) + ": "
                            + e.getLocalizedMessage(), e);
            // Oracle increments the number of cursors to close each time a new query is executed after this exception!
            reloadConnectionAfterException(catalog);
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
     * @param catalog
     * @param schema
     * @param table
     * @param pkCount
     * @return
     * @throws SQLException
     */
    private int getPKCount(String catalog, String schema, String table) throws SQLException {
        int pkCount = 0;
        ResultSet pk = null;
        try {
            // MOD xqliu 2009-07-13 bug 7888
            pk = org.talend.utils.sql.ConnectionUtils.getConnectionMetadata(getConnection()).getPrimaryKeys(catalog, schema,
                    table);
            // ~
        } catch (SQLException e1) {
            log.warn(
                    "Exception while getting primary keys on " + this.dbms().toQualifiedName(catalog, schema, table) + ": "
                            + e1.getLocalizedMessage(), e1);
            reloadConnectionAfterException(catalog);
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
        Connection dp = this.getDataManager();
        TypedReturnCode<java.sql.Connection> conn = JavaSqlFactory.createConnection(dp);
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
            // MOD xqliu 2009-02-09 bug 6237
            if (continueRun()) {
                statement.execute(sqlStatement);
            }
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
            while (resultSet != null && resultSet.next()) {
                // MOD xqliu 2009-02-09 bug 6237
                if (!continueRun()) {
                    break;
                }
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
     * DOC scorreia Comment method "evalSchemaIndic".
     * 
     * @param connIndicator
     * @param tdCatalog
     * @param tableBuilder
     * @param tablePattern
     * @param ok
     * @throws SQLException
     */
    protected void evalCatalogIndic(final CatalogIndicator catalogIndic, Catalog tdCatalog, ReturnCode ok) throws SQLException {
        this.evalSchemaIndicLow(null, catalogIndic, tdCatalog, null, ok);
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
    protected void evalSchemaIndic(Schema tdSchema, ReturnCode ok) throws SQLException {
        // --- create SchemaIndicator for each catalog
        SchemaIndicator schemaIndic = SchemaFactory.eINSTANCE.createSchemaIndicator();
        // MOD xqliu 2009-1-21 feature 4715
        DefinitionHandler.getInstance().setDefaultIndicatorDefinition(schemaIndic);
        this.evalSchemaIndicLow(null, schemaIndic, null, tdSchema, ok);
    }

    protected void evalSchemaIndicLow(final CatalogIndicator catalogIndic, final SchemaIndicator schemaIndic,
            final Catalog tdCatalog, final Schema tdSchema, ReturnCode ok) throws SQLException {
        // MOD klliu 2011-02-17 bug 18961
        boolean hasSchema = tdSchema != null;
        boolean hasCatalog = false;
        String schemaName = hasSchema ? tdSchema.getName() : null;
        String catName = null;
        if (tdCatalog == null) {
            if (tdSchema.eContainer() instanceof Catalog) {
                hasCatalog = true;
                catName = ((Catalog) tdSchema.eContainer()).getName();
            }
        } else {
            hasCatalog = true;
            catName = hasCatalog ? tdCatalog.getName() : null;
        }
        schemaIndic.setAnalyzedElement(hasSchema ? tdSchema : tdCatalog);

        // profile tables
        TableBuilder tableBuilder = new TableBuilder(this.getDataManager());
        int tableCount = 0;
        final String[] tablePatterns = tablePattern != null && tablePattern.contains(String.valueOf(FILTER_SEP)) ? StringUtils
                .split(this.tablePattern, FILTER_SEP) : new String[] { this.tablePattern };
        for (String pat : tablePatterns) {
            // MOD zshen bug 12041: the variable trimPat must be null(not a "") if it isn't a table name.
            String trimPat = pat != null && !"".equals(pat) ? pat.trim() : null;
            // ~12041
            List<? extends NamedColumnSet> tables = tableBuilder.getColumnSets(catName, schemaName, trimPat);
            for (NamedColumnSet t : tables) {
                tableCount++;
                evalAllCounts(catName, schemaName, t, schemaIndic, true, ok);
            }
        }
        schemaIndic.setTableCount(tableCount);

        // do the same for views
        ViewBuilder viewBuilder = new ViewBuilder(this.getDataManager());
        int viewCount = 0;
        final String[] viewPatterns = viewPattern != null && viewPattern.contains(String.valueOf(FILTER_SEP)) ? StringUtils
                .split(this.viewPattern, FILTER_SEP) : new String[] { this.viewPattern };
        for (String pat : viewPatterns) {
            // MOD zshen bug 12041: the variable trimPat must be null(not a "") if it isn't a view name.
            String trimPat = pat != null && !"".equals(pat) ? pat.trim() : null;
            // ~12041
            List<? extends NamedColumnSet> views = viewBuilder.getColumnSets(catName, schemaName, trimPat);
            for (NamedColumnSet t : views) {
                viewCount++;
                evalAllCounts(catName, schemaName, t, schemaIndic, false, ok);
            }
        }
        schemaIndic.setViewCount(viewCount);

        if (hasCatalog && hasSchema) {
            // add it to list of indicators
            this.addToConnectionIndicator(catalogIndic);

            // add it to list of indicators
            catalogIndic.addSchemaIndicator(schemaIndic);

            // --- increment values of catalog indicator
            catalogIndic.setTableCount(catalogIndic.getTableCount() + tableCount);
            catalogIndic.setTableRowCount(catalogIndic.getTableRowCount() + schemaIndic.getTableRowCount());
            catalogIndic.setViewRowCount(catalogIndic.getViewRowCount() + schemaIndic.getViewRowCount());

        } else if (!hasCatalog) { // has schema only
            // add it to list of indicators
            this.addToConnectionIndicator(schemaIndic);
        } else if (!hasSchema) { // has catalog only
            if (SchemaPackage.eINSTANCE.getCatalogIndicator().equals(schemaIndic.eClass())) {
                this.addToConnectionIndicator(schemaIndic);
            } else {
                log.error("This should not happen. No catatog and no schema.");
            }
        }
    }

    protected void addToConnectionIndicator(Indicator indicator) {
        // does nothing: implemented only in ConnectionEvaluator
    }

    protected void resetCounts(final Indicator indicator) {
        if (indicator != null) {
            boolean reset = indicator.reset();
            if (log.isDebugEnabled()) {
                log.debug("connection indicator reset: " + reset);
            }
        }
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

    /**
     * * yyi 2009-11-30 10187 check catalog is exist in DB
     * 
     * @param catName
     * @return
     */
    public boolean checkCatalog(String catName) {

        if (0 == catalogsName.size()) {
            Collection<Catalog> catalogs = ConnectionHelper.getAllCatalogs(getDataManager());
            for (Catalog tc : catalogs) {
                catalogsName.add(tc.getName());
            }
        }

        if (!catalogsName.contains(catName)) {
            return false;
        }

        return true;
    }

    /**
     * yyi 2009-11-30 10187 check schema is exist in DB
     * 
     * @param catName
     * @return
     */
    protected boolean checkSchemaByName(String catName) {

        if (0 == schemasName.size()) {
            Collection<Schema> schemas = new ArrayList<Schema>();
            try {
                schemas = ListUtils.castList(Schema.class,
                        MetadataFillFactory.getDBInstance().fillSchemas(null, connection.getMetaData(), null));
            } catch (SQLException e) {
                log.error(e, e);
            }
            for (Schema ts : schemas) {
                schemasName.add(ts.getName());
            }
        }

        if (!schemasName.contains(catName)) {
            return false;
        }

        return true;
    }

    /**
     * yyi 2009-11-30 10187 check schema is exist in DB
     * 
     * @param catName
     * @return
     */
    public boolean checkSchema(Schema schema) {
        EObject container = schema.eContainer();
        if (container != null) {
            Catalog catalog = SwitchHelpers.CATALOG_SWITCH.doSwitch(container);
            if (catalog != null) {
                try {
                    connection.setCatalog(catalog.getName());
                    List<Schema> schemas = MetadataFillFactory.getDBInstance().fillSchemaToCatalog(getDataManager(),
                            connection.getMetaData(), catalog, null);
                    if (schemas != null) {
                        for (Schema tdSchema : schemas) {
                            if (tdSchema.getName().equals(schema.getName()))
                                return true;
                        }
                    }
                    // ~
                    return false;
                } catch (SQLException e) {
                    log.error(e);
                }
            }
        }
        return checkSchemaByName(schema.getName());
    }

    /*
     * Check DB connection is exist in Metadata
     * 
     * @see org.talend.dq.indicators.Evaluator#checkConnection()
     */
    @Override
    protected ReturnCode checkConnection() {
        ReturnCode rc = super.checkConnection();
        if (!rc.isOk()) {
            return rc;
        }

        DataProvider dataprovider = this.getDataManager();
        // MOD qiongli 2010-9-17，bug 15525
        // MOD qiongli 2010-12-24,bug 17671,avoid NPE
        if (dataprovider == null) {
            rc.setReturnCode(Messages.getString("Evaluator.NoConnectionFoundInMetadata"), false);
            return rc;
        }
        return rc;
    }
}
