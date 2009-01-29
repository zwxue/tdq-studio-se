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

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.talend.cwm.relational.TdColumn;
import org.talend.cwm.relational.TdTable;
import orgomg.cwm.objectmodel.core.ModelElement;
import orgomg.cwm.resource.relational.Column;
import orgomg.cwm.resource.relational.ColumnSet;
import orgomg.cwm.resource.relational.ForeignKey;
import orgomg.cwm.resource.relational.PrimaryKey;
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

    /**
     * Method "addPrimaryKey".
     * 
     * @param table
     * @param pk the primary key of the table
     */
    public static boolean addPrimaryKey(Table table, PrimaryKey pk) {
        assert table != null;
        assert pk != null;
        return table.getOwnedElement().add(pk);
    }

    /**
     * Method "addAllPrimaryKeys".
     * 
     * @param table
     * @param primaryKeys the primary keys of the table.
     */
    public static boolean addPrimaryKeys(ColumnSet table, List<PrimaryKey> primaryKeys) {
        assert table != null;
        assert primaryKeys != null;
        return table.getOwnedElement().addAll(primaryKeys);
    }

    /**
     * Method "addForeignKey".
     * 
     * @param table
     * @param foreignKey the foreign key of the given table
     */
    public static boolean addForeignKey(ColumnSet table, ForeignKey foreignKey) {
        assert table != null;
        assert foreignKey != null;
        return table.getOwnedElement().add(foreignKey);
    }

    /**
     * Method "addForeignKeys".
     * 
     * @param table
     * @param foreignKeys the foreign keys of this table
     */
    public static boolean addForeignKeys(ColumnSet table, List<ForeignKey> foreignKeys) {
        assert table != null;
        assert foreignKeys != null;
        return table.getOwnedElement().addAll(foreignKeys);
    }

    /**
     * Method "getPrimaryKeys".
     * 
     * @param table a table
     * @return a list of all primary keys of the given table
     */
    public static List<PrimaryKey> getPrimaryKeys(Table table) {
        List<PrimaryKey> primarykeys = new ArrayList<PrimaryKey>();
        EList<ModelElement> ownedElements = table.getOwnedElement();
        for (ModelElement modelElement : ownedElements) {
            PrimaryKey pk = SwitchHelpers.PRIMARY_KEY_SWITCH.doSwitch(modelElement);
            if (pk != null) {
                primarykeys.add(pk);
            }
        }
        return primarykeys;
    }

    /**
     * Method "getForeignKeys".
     * 
     * @param table a table
     * @return a list of all foreign keys of the given table
     */
    public static List<ForeignKey> getForeignKeys(Table table) {
        List<ForeignKey> foreignkeys = new ArrayList<ForeignKey>();
        EList<ModelElement> ownedElements = table.getOwnedElement();
        for (ModelElement modelElement : ownedElements) {
            ForeignKey pk = SwitchHelpers.FOREIGN_KEY_SWITCH.doSwitch(modelElement);
            if (pk != null) {
                foreignkeys.add(pk);
            }
        }
        return foreignkeys;
    }
}
