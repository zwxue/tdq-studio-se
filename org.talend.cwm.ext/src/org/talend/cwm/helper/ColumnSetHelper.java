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
import org.talend.cwm.relational.RelationalPackage;
import org.talend.cwm.relational.TdCatalog;
import org.talend.cwm.relational.TdColumn;
import org.talend.cwm.relational.TdSchema;
import org.talend.cwm.relational.TdTable;
import org.talend.cwm.relational.TdView;
import org.talend.cwm.relational.util.RelationalSwitch;
import orgomg.cwm.objectmodel.core.ModelElement;
import orgomg.cwm.objectmodel.core.Namespace;
import orgomg.cwm.objectmodel.core.Package;
import orgomg.cwm.resource.relational.Column;
import orgomg.cwm.resource.relational.ColumnSet;
import orgomg.cwm.resource.relational.QueryColumnSet;
import orgomg.cwm.resource.relational.Table;
import orgomg.cwm.resource.relational.View;

/**
 * @author scorreia
 * 
 * Utility class for handling ColumnSets.
 */
public class ColumnSetHelper {

    /**
     * Method "addColumn" adds the given column to the given column set.
     * 
     * @param column the column to add
     * @param columnSet the column set
     * @return true if the column set has changed.
     */
    public static boolean addColumn(Column column, ColumnSet columnSet) {
        return columnSet.getFeature().add(column);
    }

    public static boolean removeColumn(Column column, ColumnSet columnSet) {
        return columnSet.getFeature().remove(column);
    }

    public static List<TdColumn> getColumns(ColumnSet columnSet) {
        return ColumnHelper.getColumns(columnSet.getFeature());
    }

    /**
     * Method "addColumns".
     * 
     * @param columnSet the column set in which to add the columns (must not be null)
     * @param columns the columns to add (must not be null)
     * @return true if the content of the table changed as a result of the call.
     */
    public static boolean addColumns(ColumnSet columnSet, Collection<? extends Column> columns) {
        assert columnSet != null;
        assert columns != null;
        return columnSet.getFeature().addAll(columns);
    }

    /**
     * Method "setColumns" replaces the previous columns by the new ones.
     * 
     * @param columnSet the column set in which to add the columns (must not be null)
     * @param columns the columns to add (must not be null)
     * @return true if the content of the table changed as a result of the call.
     */
    public static boolean setColumns(ColumnSet columnSet, Collection<? extends Column> columns) {
        assert columnSet != null;
        assert columns != null;
        columnSet.getFeature().clear();
        return columnSet.getFeature().addAll(columns);
    }

    /**
     * Method "createQueryColumnSet".
     * 
     * @return a Query column set
     */
    public static QueryColumnSet createQueryColumnSet() {
        return orgomg.cwm.resource.relational.RelationalFactory.eINSTANCE.createQueryColumnSet();
    }

    /**
     * DMethod "createQueryColumnSet" creates a column set from given columns.
     * 
     * @param columns the columns
     * @return the column set
     */
    public static QueryColumnSet createQueryColumnSet(Column... columns) {
        QueryColumnSet columnSet = orgomg.cwm.resource.relational.RelationalFactory.eINSTANCE.createQueryColumnSet();
        for (Column column : columns) {
            addColumn(column, columnSet);
        }
        return columnSet;
    }

    /**
     * Method "getParentCatalogOrSchema" returns the owner of the element (Catalog or Schema).
     * 
     * @param element (can be null)
     * @return the Catalog or of Schema or null
     */
    public static Package getParentCatalogOrSchema(ModelElement element) {
        if (element == null) {
            return null;
        }
        Namespace namespace = element.getNamespace();
        return PackageHelper.getCatalogOrSchema(namespace);
    }

    /**
     * Method "fillColumnSets". TODO scorreia this method has not been tested yet!!
     * 
     * @param <T> the type of elements to find (either Table, View, ColumnSet)
     * @param catalog the catalog if the tables are stored directly in catalog (or null)
     * @param schema the schema if the tables are stored directly in schema (or null)
     * @param output the list of searched elements (Tables, TdTables....)
     * @param tClassifierId the the classifierId of the searched elements (e.g. {@link RelationalPackage#TD_VIEW})
     * @return true if elements have been found and potentially added to the output list.
     */
    public static <T extends ColumnSet> boolean fillColumnSets(TdCatalog catalog, TdSchema schema, Collection<T> output,
            final int tClassifierId) {
        // --- precondition
        if (catalog == null && schema == null) {
            return false;
        }

        RelationalSwitch<T> relationalSwitch = new RelationalSwitch<T>() {

            @Override
            protected T doSwitch(int classifierID, EObject theEObject) {
                if (theEObject.eClass().getClassifierID() != tClassifierId) {
                    return null;
                } else {
                    return super.doSwitch(classifierID, theEObject);
                }
            }

            @Override
            public T caseColumnSet(ColumnSet object) {
                return castObject(object);
            }

            @Override
            public T caseTable(Table object) {
                return castObject(object);
            }

            @Override
            public T caseTdTable(TdTable object) {
                return castObject(object);
            }

            @Override
            public T caseTdView(TdView object) {
                return castObject(object);
            }

            @Override
            public T caseView(View object) {
                return castObject(object);
            }

            @SuppressWarnings("unchecked")
            private T castObject(Object object) {
                return (T) object;
            }

        };

        EList<ModelElement> elements = (schema != null) ? schema.getOwnedElement() : catalog.getOwnedElement();
        if (elements.isEmpty()) {
            // no element found
            return false;
        }
        for (EObject elt : elements) {
            T columnSet = relationalSwitch.doSwitch(elt);
            if (columnSet != null) {
                output.add(columnSet);
            }
        }
        return true;

    }

    /**
     * Method "getTableNames".
     * 
     * @param columnSet a set of columns (that could come from several Tables or views)
     * @return the list of container names (tables, views) which the columns belong to (not null).
     */
    public static String[] getColumnSetNames(ColumnSet columnSet) {
        List<TdColumn> columns = ColumnSetHelper.getColumns(columnSet);
        return getColumnSetNames(columns);
    }

    /**
     * Method "getTableNames".
     * 
     * @param columnSet a set of columns (that could come from several Tables or views)
     * @return the list of container names (tables, views) which the columns belong to (not null).
     */
    public static String[] getColumnSetNames(Collection<? extends Column> columns) {
        List<String> tableNames = new ArrayList<String>();
        for (Column tdColumn : columns) {
            String tableName = ColumnHelper.getColumnSetFullName(tdColumn);
            if (tableName != null) {
                tableNames.add(tableName);
            }
        }
        return tableNames.toArray(new String[tableNames.size()]);
    }
}
