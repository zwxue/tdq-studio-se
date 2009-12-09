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
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.talend.cwm.helper.ColumnHelper;
import org.talend.cwm.management.connection.DatabaseContentRetriever;
import org.talend.cwm.relational.TdColumn;
import org.talend.cwm.relational.TdSqlDataType;
import org.talend.dataquality.helpers.BooleanExpressionHelper;
import org.talend.utils.sql.metadata.constants.GetColumn;
import orgomg.cwm.objectmodel.core.Expression;
import orgomg.cwm.resource.relational.enumerations.NullableType;

/**
 * @author scorreia
 * 
 * This class creates TdColumn objects from a connection. The connection must be closed by the caller. It will not be
 * closed by the ColumnBuilder.
 */
public class ColumnBuilder extends CwmBuilder {

    private static Logger log = Logger.getLogger(ColumnBuilder.class);

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
            // TODO scorreia other informations for columns can be retrieved here
            // get the default value
            // MOD mzhao 2009-04-09,Bug 6840: fetch LONG or LONG RAW column first , as these kind of columns are read as
            // stream,if not read by select order, there will be "Stream has already been closed" error.
            // MOD xqliu 2009-10-29 bug 9838
            // MOD xqliu 2009-12-08 bug 9822, if use odbc connect to sqlserver the resultset is forward only!!!
            String colName = null;
            try {
                colName = columns.getString(GetColumn.COLUMN_NAME.name());
            } catch (Exception e1) {
                log.warn(e1, e1);
                if (colName == null) {
                    colName = e1.getMessage();
                }
            }
            TdColumn column = ColumnHelper.createTdColumn(colName);

            int dataType = 0;
            try {
                dataType = columns.getInt(GetColumn.DATA_TYPE.name());
                column.setJavaType(dataType);
            } catch (Exception e) {
                log.warn(e, e);
            }

            String typeName = null;
            try {
                typeName = columns.getString(GetColumn.TYPE_NAME.name());
            } catch (Exception e1) {
                log.warn(e1, e1);
            }

            try {
                column.setLength(columns.getInt(GetColumn.COLUMN_SIZE.name()));
            } catch (Exception e1) {
                log.warn(e1, e1);
            }

            int decimalDigits = 0;
            try {
                decimalDigits = columns.getInt(GetColumn.DECIMAL_DIGITS.name());
            } catch (Exception e) {
                log.warn(e);
            }

            int numPrecRadix = 0;
            try {
                numPrecRadix = columns.getInt(GetColumn.NUM_PREC_RADIX.name());
            } catch (Exception e) {
                log.warn(e);
            }

            // get column description (comment)
            try {
                String colComment = getComment(colName, columns);
                ColumnHelper.setComment(colComment, column);
            } catch (Exception e) {
                log.warn(e, e);
            }

            Object defaultvalue = null;
            try {
                defaultvalue = columns.getObject(GetColumn.COLUMN_DEF.name());
            } catch (Exception e1) {
                log.warn(e1, e1);
            }
            String defaultStr = (defaultvalue != null) ? String.valueOf(defaultvalue) : null;
            Expression defExpression = BooleanExpressionHelper.createExpression(GetColumn.COLUMN_DEF.name(), defaultStr);

            try {
                column.setIsNullable(NullableType.get(columns.getInt(GetColumn.NULLABLE.name())));
            } catch (Exception e1) {
                log.warn(e1, e1);
            }
            // --- create and set type of column
            // TODO scorreia get type of column on demand, not on creation of column
            // TdSqlDataType sqlDataType = DatabaseContentRetriever.createDataType(columns);
            TdSqlDataType sqlDataType = DatabaseContentRetriever.createDataType(dataType, typeName, decimalDigits, numPrecRadix);
            column.setSqlDataType(sqlDataType);
            // column.setType(sqlDataType); // it's only reference to previous sql data type

            column.setInitialValue(defExpression);
            tableColumns.add(column);
        }

        // release JDBC resources
        columns.close();

        return tableColumns;
    }

    /**
     * DOC scorreia Comment method "getComment".
     * 
     * @param colName
     * 
     * @param columns
     * @return
     * @throws SQLException
     */
    private String getComment(String colName, ResultSet columns) throws SQLException {
        String colComment = columns.getString(GetColumn.REMARKS.name());
        if (colComment == null) {
            String selectRemarkOnColumn = dbms.getSelectRemarkOnColumn(colName);
            if (selectRemarkOnColumn != null) {
                colComment = executeGetCommentStatement(selectRemarkOnColumn);
            }
        }
        return colComment;
    }
}
