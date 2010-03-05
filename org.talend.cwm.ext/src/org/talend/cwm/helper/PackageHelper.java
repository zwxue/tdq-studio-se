// ============================================================================
//
// Copyright (C) 2006-2010 Talend Inc. - www.talend.com
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
import org.talend.cwm.relational.TdCatalog;
import org.talend.cwm.relational.TdTable;
import org.talend.cwm.relational.TdView;
import orgomg.cwm.objectmodel.core.Package;
import orgomg.cwm.resource.relational.ColumnSet;
import orgomg.cwm.resource.relational.NamedColumnSet;

/**
 * Helper class for getting elements of Schema or Catalog indifferently.
 */
public final class PackageHelper {

    private PackageHelper() {
    }

    /**
     * Method "getTables".
     * 
     * @param catOrSchema a Catalog or Schema (must not be null)
     * @return the tables contained in the given element.
     */
    public static List<TdTable> getTables(Package catOrSchema) {
        assert catOrSchema != null;
        return TableHelper.getTables(catOrSchema.getOwnedElement());
    }

    /**
     * Method "getViews".
     * 
     * @param catOrSchema a Catalog or Schema (must not be null)
     * @return the views contained in the given element.
     */
    public static List<TdView> getViews(Package catOrSchema) {
        assert catOrSchema != null;
        return ViewHelper.getViews(catOrSchema.getOwnedElement());
    }

    /**
     * DOC bZhou Comment method "getNmaedColumnSets".
     * 
     * @param catOrSchema
     * @return
     */
    public static List<NamedColumnSet> getNmaedColumnSets(Package catOrSchema) {
        List<NamedColumnSet> setList = new ArrayList<NamedColumnSet>();
        setList.addAll(getTables(catOrSchema));
        setList.addAll(getViews(catOrSchema));
        return setList;
    }

    public static Package getCatalogOrSchema(EObject element) {
        if (element == null) {
            return null;
        }

        TdCatalog res = SwitchHelpers.CATALOG_SWITCH.doSwitch(element);
        if (res != null) {
            return res;
        }
        return SwitchHelpers.SCHEMA_SWITCH.doSwitch(element);
    }

    public static boolean addColumnSets(Collection<ColumnSet> columnSets, Package packageElement) {
        return packageElement.getOwnedElement().addAll(columnSets);
    }

    public static boolean addColumnSet(ColumnSet columnSet, Package packageElement) {
        return packageElement.getOwnedElement().add(columnSet);
    }

    public static boolean removeColumnSet(ColumnSet columnSet, Package packageElement) {
        return packageElement.getOwnedElement().remove(columnSet);
    }

    public static boolean removeColumnSets(Collection<ColumnSet> columnSets, Package packageElement) {
        return packageElement.getOwnedElement().removeAll(columnSets);
    }

}
