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
package org.talend.cwm.helper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.talend.cwm.relational.RelationalFactory;
import org.talend.cwm.relational.TdColumn;
import orgomg.cwm.foundation.keysindexes.UniqueKey;
import orgomg.cwm.foundation.softwaredeployment.DataProvider;
import orgomg.cwm.objectmodel.core.Classifier;
import orgomg.cwm.objectmodel.core.Expression;
import orgomg.cwm.objectmodel.core.ModelElement;
import orgomg.cwm.objectmodel.core.TaggedValue;
import orgomg.cwm.resource.relational.Column;
import orgomg.cwm.resource.relational.ColumnSet;
import orgomg.cwm.resource.relational.PrimaryKey;

/**
 * @author scorreia
 * 
 * Utility class for handling (Td)Columns.
 */
public final class ColumnHelper {

    private ColumnHelper() {
    }

    /**
     * Method "createColumn" creates a column with the given name.
     * 
     * @param name the name of the column
     * @return the created column.
     */
    public static Column createColumn(String name) {
        Column column = orgomg.cwm.resource.relational.RelationalFactory.eINSTANCE.createColumn();
        column.setName(name);

        return column;
    }

    /**
     * Method "createColumn" creates a column with the given name.
     * 
     * @param name the name of the column
     * @return the created column.
     */
    public static TdColumn createTdColumn(String name) {
        TdColumn column = RelationalFactory.eINSTANCE.createTdColumn();
        column.setName(name);
        return column;
    }

    /**
     * Method "getColumns" returns the columns that are in the list.
     * 
     * @param elements a list with various elements
     * @return the list of TdColumn contained in the given list
     */
    public static List<TdColumn> getColumns(EList<? extends EObject> elements) {
        List<TdColumn> columns = new ArrayList<TdColumn>();
        for (EObject elt : elements) {
            if (elt != null) {
                TdColumn col = SwitchHelpers.COLUMN_SWITCH.doSwitch(elt);
                if (col != null) {
                    columns.add(col);
                }
            }
        }
        return columns;
    }

    /**
     * Method "getColumnNames".
     * 
     * @param columnSet
     * @return the list of column names (with their table names specified)
     */
    public static String[] getColumnFullNames(ColumnSet columnSet) {
        List<TdColumn> columns = ColumnSetHelper.getColumns(columnSet);
        return getColumnFullNames(columns);
    }

    /**
     * Method "getColumnNames".
     * 
     * @param columnSet
     * @return the list of column names (with their table names specified)
     */
    public static String[] getColumnFullNames(Collection<? extends Column> columns) {
        Set<String> columnNames = new HashSet<String>();
        for (Column column : columns) {
            String colName = getFullName(column);
            columnNames.add(colName);
        }

        return columnNames.toArray(new String[columnNames.size()]);
    }

    /**
     * Method "getFullName" the name of the column with the table (or view) name in front of it. E.g. "Table.Column".
     * 
     * @param column
     * @return the name of the column or null
     */
    public static String getFullName(Column column) {
        String tableName = getColumnSetFullName(column);
        if (tableName != null) {
            return tableName + "." + column.getName(); //$NON-NLS-1$
        }
        return column.getName();
    }

    /**
     * Method "getColumnSetFulName" the name of the container of the column. E.g. a table or a view.
     * 
     * @param column a column
     * @return the name of the container of the column or null
     */
    public static String getColumnSetFullName(Column column) {
        ColumnSet colSet = getColumnSetOwner(column);
        if (colSet != null) {
            return colSet.getName();
        }
        // else
        return null;
    }

    /**
     * Method "getColumnSetOwner".
     * 
     * @param column
     * @return the owner of the given column or null
     */
    public static ColumnSet getColumnSetOwner(Column column) {
        Classifier owner = column.getOwner();
        if (owner == null) {
            return null;
        }
        return SwitchHelpers.COLUMN_SET_SWITCH.doSwitch(owner);
    }

    /**
     * Method "getDefaultValue".
     * 
     * @param column a column
     * @return the default value of a column or null
     */
    public static String getDefaultValue(Column column) {
        Expression initialValue = column.getInitialValue();
        return initialValue != null ? initialValue.getBody() : null;
    }

    /**
     * Method "isPrimaryKey".
     * 
     * @param column a column
     * @return true if the given column is a primary key
     */
    public static boolean isPrimaryKey(Column column) {
        return getPrimaryKey(column) != null;
    }

    /**
     * Method "getPrimaryKey".
     * 
     * @param column a column
     * @return the primary key object if this column is a primary key
     */
    public static PrimaryKey getPrimaryKey(Column column) {
        EList<UniqueKey> uniqueKeys = column.getUniqueKey();
        for (UniqueKey uniqueKey : uniqueKeys) {
            if (uniqueKey != null) {
                PrimaryKey pk = SwitchHelpers.PRIMARY_KEY_SWITCH.doSwitch(uniqueKey);
                if (pk != null) {
                    return pk;
                }
            }
        }
        return null;
    }

    /**
     * DOC bZhou Comment method "isFromSameConnection".
     * 
     * @param columns
     * @return
     */
    public static boolean isFromSameConnection(List<Column> columns) {
        assert columns != null;

        Set<DataProvider> dataProviderSets = new HashSet<DataProvider>();
        for (Column column : columns) {
            ColumnSet columnSetOwner = getColumnSetOwner(column);
            DataProvider dp = DataProviderHelper.getDataProvider(columnSetOwner);
            dataProviderSets.add(dp);
        }
        return dataProviderSets.size() == 1;
    }

    /**
     * DOC bZhou Comment method "isFromSameTable".
     * 
     * @param columns
     * @return
     */
    public static boolean isFromSameTable(List<Column> columns) {
        assert columns != null;

        Set<ColumnSet> columnSets = new HashSet<ColumnSet>();
        for (Column column : columns) {
            ColumnSet columnSetOwner = getColumnSetOwner(column);
            columnSets.add(columnSetOwner);
        }
        return columnSets.size() == 1;
    }

    /**
     * DOC bZhou Comment method "getColumnFilter".
     * 
     * @param element
     * @return
     */
    public static String getColumnFilter(ModelElement element) {
        TaggedValue taggedValue = TaggedValueHelper.getTaggedValue(TaggedValueHelper.COLUMN_FILTER, element.getTaggedValue());
        if (taggedValue == null) {
            return "";
        }
        return taggedValue.getValue();
    }

    /**
     * DOC bZhou Comment method "setColumnFilter".
     * 
     * @param filter
     * @param element
     */
    public static void setColumnFilter(String filter, ModelElement element) {
        TaggedValueHelper.setTaggedValue(element, TaggedValueHelper.COLUMN_FILTER, filter);
    }

    /**
     * DOC bZhou Comment method "getComment".
     * 
     * @param element
     * @return
     */
    public static String getComment(ModelElement element) {
        TaggedValue taggedValue = TaggedValueHelper.getTaggedValue(TaggedValueHelper.COMMENT, element.getTaggedValue());
        if (taggedValue == null) {
            return "";
        }
        return taggedValue.getValue();
    }

    /**
     * DOC bZhou Comment method "setComment".
     * 
     * @param comment
     * @param element
     */
    public static void setComment(String comment, ModelElement element) {
        TaggedValueHelper.setTaggedValue(element, TaggedValueHelper.COMMENT, comment);
    }
}
