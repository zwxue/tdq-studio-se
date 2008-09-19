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
package org.talend.cwm.builders;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.talend.cwm.helper.ColumnSetHelper;
import org.talend.cwm.helper.TaggedValueHelper;
import org.talend.cwm.relational.TdColumn;
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
     * DOC scorreia AbstractTableBuilder constructor comment.
     * 
     * @param conn
     * @param type the type of columnSet
     */
    public AbstractTableBuilder(Connection conn, TableType type) {
        super(conn);
        if (type == null) {
            throw new IllegalArgumentException("No type given. Type must be set to either tables, views, system tables...");
        }
        this.tableType = new String[] { type.toString() };
    }

    /**
     * Method "getColumnSets" returns tables or views.
     * 
     * @param catalogName a catalog name; must match the catalog name as it is stored in the database; "" retrieves
     * those without a catalog; null means that the catalog name should not be used to narrow the search
     * @param schemaPattern a schema name pattern; must match the schema name as it is stored in the database; ""
     * retrieves those without a schema; null means that the schema name should not be used to narrow the search
     * @param tablePattern a table name pattern; must match the table name as it is stored in the database
     * @return the tables with for the given catalog, schemas, table name pattern.
     * @throws SQLException
     */
    public List<T> getColumnSets(String catalogName, String schemaPattern, String tablePattern) throws SQLException {
        List<T> tables = new ArrayList<T>();

        ResultSet tablesSet = getConnectionMetadata(connection).getTables(catalogName, schemaPattern, tablePattern,
                this.tableType);
        while (tablesSet.next()) {
            T table = createTable(catalogName, schemaPattern, tablesSet);
            tables.add(table);
        }
        // release JDBC resources
        tablesSet.close();

        return tables;
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
    private T createTable(String catalogName, String schemaPattern, ResultSet tablesSet) throws SQLException {
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
