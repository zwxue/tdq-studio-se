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
package org.talend.cwm.builders;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.talend.cwm.helper.ColumnSetHelper;
import org.talend.cwm.helper.TaggedValueHelper;
import org.talend.cwm.relational.TdColumn;
import org.talend.i18n.Messages;
import org.talend.utils.sql.metadata.constants.GetTable;
import org.talend.utils.sql.metadata.constants.TableType;
import orgomg.cwm.resource.relational.NamedColumnSet;

/**
 * By default, no column is retrieved.
 * 
 * @param <T> the type of table to create (TdTable, TdView)
 */
public abstract class AbstractTableBuilder<T extends NamedColumnSet> extends CwmBuilder {

    private String[] tableType;

    private boolean columnsRequested = false;
    
    /**
     * MOD scorreia 2009-04-27 for debug purpose only.
     */
    private static final Map<String, Integer> catalog2NumberOfCalls = new HashMap<String, Integer>();
    private static final boolean debug = true;

    /**
     * DOC scorreia AbstractTableBuilder constructor comment.
     * 
     * @param conn
     * @param type the type of columnSet
     */
    public AbstractTableBuilder(Connection conn, TableType type) {
        super(conn);
        if (type == null) {
            throw new IllegalArgumentException(Messages.getString("AbstractTableBuilder.NoTypeGiven")); //$NON-NLS-1$
        }
        this.tableType = new String[] { type.toString() };
    }

    private static void incrementCount(String catalog, String schema) {
        String key = (catalog != null) ? catalog + "." + schema : schema;
        Integer count = catalog2NumberOfCalls.get(key);
        count = (count != null) ? count + 1 : 1;
        System.err.println(key + ": " + count);
        catalog2NumberOfCalls.put(key, count);
    }

    /**
     * Method "getColumnSets" returns tables or views. MOD xqliu 2009-04-27 bug 6507
     * 
     * @param catalogName a catalog name; must match the catalog name as it is stored in the database; "" retrieves
     * those without a catalog; null means that the catalog name should not be used to narrow the search
     * @param schemaPattern a schema name pattern; must match the schema name as it is stored in the database; ""
     * retrieves those without a schema; null means that the schema name should not be used to narrow the search
     * @param tablePattern table name patterns separated by a comma; must match the table name as it is stored in the
     * database
     * @return the tables with for the given catalog, schemas, table name pattern.
     * @throws SQLException
     */
    public List<T> getColumnSets(String catalogName, String schemaPattern, String tablePattern) throws SQLException {
        // MOD scorreia 2009-04-27 the following is only for debug purpose
        if (debug)
            incrementCount(catalogName, schemaPattern);
        // FIXME xqliu: this method seems to be called recursively
        List<T> tables = new ArrayList<T>();
        if (tablePattern == null) { // no pattern
            addMatchingColumnSets(catalogName, schemaPattern, tablePattern, tables);
        } else { // multiple patterns
            String[] patterns = tablePattern.split(",");
            for (String pattern : patterns) {
                addMatchingColumnSets(catalogName, schemaPattern, pattern, tables);
            }
        }
        return tables;
    }

    /**
     * Method "addMatchingColumnSets" creates new tables and add them into the given list of tables. A limit in the
     * number of tables is set to {@value TaggedValueHelper#TABLE_VIEW_MAX}
     * 
     * @param catalogName
     * @param schemaPattern
     * @param tablePattern
     * @param tables
     * @return the number of added tables
     * @throws SQLException
     */
    private int addMatchingColumnSets(String catalogName, String schemaPattern, String tablePattern, List<T> tables)
            throws SQLException {
        // MOD scorreia 2009-04-27. Bug 6507: table pattern is an SQL like used to get the table result set.
        ResultSet tablesSet = getConnectionMetadata(connection).getTables(catalogName, schemaPattern, tablePattern,
                this.tableType);
        int size = 0;
        while (tablesSet.next()) {
            T table = createTable(catalogName, schemaPattern, tablesSet);
            tables.add(table);
            size++;
            if (size > TaggedValueHelper.TABLE_VIEW_MAX) {
                tables.clear();
                // add a special table because the table/view number is to big
                table = createTable();
                table.setName(TaggedValueHelper.TABLE_VIEW_COLUMN_OVER_FLAG);
                tables.add(table);
                break;
            }
        }
        // release JDBC resources
        tablesSet.close();
        return size;
    }

    /**
     * Method "isColumnsRequested".
     * 
     * @return true if columns loading from database has been requested.
     */
    public boolean isColumnsRequested() {
        return this.columnsRequested;
    }

    /**
     * Method "setColumnsRequested".
     * 
     * @param columnsRequested true if the columns must be loaded from the database.
     */
    public void setColumnsRequested(boolean columnsRequested) {
        this.columnsRequested = columnsRequested;
    }

    /**
     * Method "createTableWithColumns" create a Table or View.
     * 
     * @param catalogName
     * @param schemaPattern
     * @param tablesSet
     * @return
     * @throws SQLException
     */
    protected T createTable(String catalogName, String schemaPattern, ResultSet tablesSet) throws SQLException {
        String tableName = tablesSet.getString(GetTable.TABLE_NAME.name());
        String tableComment = getTableComment(tableName, tablesSet);

        // TODO scorreia get table type and display in separate folder according to the type
        // --- create a table and add columns
        T table = createTable();
        table.setName(tableName);
        TaggedValueHelper.setComment(tableComment, table);
        if (columnsRequested) {
            ColumnBuilder colBuild = new ColumnBuilder(connection);
            List<TdColumn> columns = colBuild.getColumns(catalogName, schemaPattern, tableName, null);
            ColumnSetHelper.addColumns(table, columns);
        }
        return table;
    }

    /**
     * DOC scorreia Comment method "getTableComment".
     * 
     * @param tableName
     * 
     * @param tablesSet
     * @return
     * @throws SQLException
     */
    private String getTableComment(String tableName, ResultSet tablesSet) throws SQLException {
        String tableComment = tablesSet.getString(GetTable.REMARKS.name());
        if (StringUtils.isBlank(tableComment)) {
            String selectRemarkOnTable = dbms.getSelectRemarkOnTable(tableName);
            if (selectRemarkOnTable != null) {
                tableComment = executeGetCommentStatement(selectRemarkOnTable);
            }
        }
        return tableComment;
    }

    protected abstract T createTable();
}
