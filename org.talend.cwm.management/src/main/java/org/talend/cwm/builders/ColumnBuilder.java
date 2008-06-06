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
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.talend.cwm.helper.ColumnHelper;
import org.talend.cwm.management.connection.DatabaseContentRetriever;
import org.talend.cwm.relational.TdColumn;
import org.talend.cwm.relational.TdSqlDataType;
import org.talend.utils.sql.metadata.constants.GetColumn;
import orgomg.cwm.resource.relational.enumerations.NullableType;

/**
 * @author scorreia
 * 
 * This class creates TdColumn objects from a connection. The connection must be closed by the caller. It will not be
 * closed by the ColumnBuilder.
 */
public class ColumnBuilder extends CwmBuilder {

    /**
     * DOC scorreia ColumnBuilder constructor comment.
     * 
     * @param conn
     * @throws SQLException
     */
    public ColumnBuilder(Connection conn) {
        super(conn);
    }

    /**
     * Method "getColumns".
     * 
     * @param catalogName a catalog name; must match the catalog name as it is stored in the database; "" retrieves
     * those without a catalog; null means that the catalog name should not be used to narrow the search
     * @param schemaPattern a schema name pattern; must match the schema name as it is stored in the database; ""
     * retrieves those without a schema; null means that the schema name should not be used to narrow the search
     * @param tablePattern a table name pattern; must match the table name as it is stored in the database
     * @param columnPattern a column name pattern; must match the column name as it is stored in the database
     * @throws SQLException
     * @see DatabaseMetaData#getColumns(String, String, String, String)
     */
    public List<TdColumn> getColumns(String catalogName, String schemaPattern, String tablePattern, String columnPattern)
            throws SQLException {

        List<TdColumn> tableColumns = new ArrayList<TdColumn>();

        // --- add columns to table
        ResultSet columns = getConnectionMetadata(connection).getColumns(catalogName, schemaPattern, tablePattern, columnPattern);
        while (columns.next()) {
            TdColumn column = ColumnHelper.createTdColumn(columns.getString(GetColumn.COLUMN_NAME.name()));
            column.setLength(columns.getInt(GetColumn.COLUMN_SIZE.name()));
            column.setIsNullable(NullableType.get(columns.getInt(GetColumn.NULLABLE.name())));
            column.setJavaType(columns.getInt(GetColumn.DATA_TYPE.name()));
            // TODO columns.getString(GetColumn.TYPE_NAME.name());

            // TODO get column description (comment)

            // TODO scorreia other informations for columns can be retrieved here

            // --- create and set type of column
            // TODO scorreia get type of column on demand, not on creation of column
            TdSqlDataType sqlDataType = DatabaseContentRetriever.createDataType(columns);
            column.setSqlDataType(sqlDataType);
            // column.setType(sqlDataType); // it's only reference to previous sql data type

            tableColumns.add(column);
        }

        // release JDBC resources
        columns.close();

        return tableColumns;

    }

}
