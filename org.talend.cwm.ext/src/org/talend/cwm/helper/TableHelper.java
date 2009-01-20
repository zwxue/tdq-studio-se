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
package org.talend.cwm.helper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.talend.cwm.relational.TdColumn;
import org.talend.cwm.relational.TdTable;
import orgomg.cwm.resource.relational.Column;
import orgomg.cwm.resource.relational.Table;

/**
 * @author scorreia
 * 
 * Helper for Table class.
 */
public final class TableHelper {

    private TableHelper() {
    }

    /**
     * Method "getTables" extracts the tables from the list.
     * 
     * @param elements any elements that could contain TdTables
     * @return the list of TdTables found in the given list (never null, but can be empty).
     */
    public static List<TdTable> getTables(Collection<? extends EObject> elements) {
        List<TdTable> tables = new ArrayList<TdTable>();
        for (EObject elt : elements) {
            TdTable table = SwitchHelpers.TABLE_SWITCH.doSwitch(elt);
            if (table != null) {
                tables.add(table);
            }
        }
        return tables;
    }

    /**
     * Method "getColumns" returns the columns of a table.
     * 
     * @param table a table
     * @return the list of TdColumn contained in the table
     */
    public static List<TdColumn> getColumns(TdTable table) {
        return ColumnHelper.getColumns(table.getFeature());
    }

    /**
     * Method "addColumns".
     * 
     * @param table the table in which to add the columns (must not be null)
     * @param columns the columns to add (must not be null)
     * @return true if the content of the table changed as a result of the call.
     */
    public static boolean addColumns(TdTable table, Collection<TdColumn> columns) {
        return ColumnSetHelper.addColumns(table, columns);
    }

    /**
     * Method "addColumn" adds a column to the given table.
     * 
     * @param table the table in which the column is added (must not be null)
     * @param column the column to add (must not be null)
     * @return true if the content of the table changed as a result of the call.
     */
    public static boolean addColumn(TdTable table, TdColumn column) {
        assert table != null;
        assert column != null;
        return table.getFeature().add(column);
    }

    /**
     * Method "getParentTable".
     * 
     * @param column a column
     * @return the table containing this column or null
     */
    public static Table getParentTable(Column column) {
        TdTable table = SwitchHelpers.TABLE_SWITCH.doSwitch(column.getOwner());
        return table;
    }
}
