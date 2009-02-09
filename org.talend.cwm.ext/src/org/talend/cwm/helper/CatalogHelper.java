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
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.talend.cwm.relational.RelationalFactory;
import org.talend.cwm.relational.TdCatalog;
import org.talend.cwm.relational.TdSchema;
import org.talend.cwm.relational.TdTable;
import org.talend.cwm.relational.TdView;
import orgomg.cwm.objectmodel.core.ModelElement;
import orgomg.cwm.objectmodel.core.Namespace;
import orgomg.cwm.resource.relational.Catalog;

/**
 * @author scorreia
 * 
 * Utility class for handling Catalog and its children.
 */
public final class CatalogHelper {

    private CatalogHelper() {
    }

    /**
     * Method "createCatalog" creates a catalog with the given name.
     * 
     * @param name the name of the catalog
     * @return the new catalog
     */
    public static TdCatalog createCatalog(String name) {
        TdCatalog catalog = RelationalFactory.eINSTANCE.createTdCatalog();
        catalog.setName(name);
        return catalog;
    }

    public static List<TdCatalog> getTdCatalogs(Collection<? extends EObject> elements) {
        List<TdCatalog> catalogs = new ArrayList<TdCatalog>();
        for (EObject pack : elements) {
            TdCatalog cat = SwitchHelpers.CATALOG_SWITCH.doSwitch(pack);
            if (cat != null) {
                catalogs.add(cat);
            }
        }
        return catalogs;
    }

    /**
     * Method "getParentCatalog" returns a Catalog if the element is directly owned by a catalog.
     * 
     * @param element (can be null)
     * @return the Catalog or null
     */
    public static TdCatalog getParentCatalog(ModelElement element) {
        if (element == null) {
            return null;
        }
        Namespace namespace = element.getNamespace();
        if (namespace == null) {
            return null;
        }
        return SwitchHelpers.CATALOG_SWITCH.doSwitch(namespace);
    }

    public static boolean addSchemas(Collection<TdSchema> schemas, Catalog catalog) {
        return catalog.getOwnedElement().addAll(schemas);
    }

    public static boolean addTables(Collection<TdTable> tables, Catalog catalog) {
        return catalog.getOwnedElement().addAll(tables);
    }

    public static List<TdTable> getTables(Catalog catalog) {
        return TableHelper.getTables(catalog.getOwnedElement());
    }

    public static List<TdSchema> getSchemas(Catalog catalog) {
        return SchemaHelper.getSchemas(catalog.getOwnedElement());
    }

    // ADDED add a method addViews to add all the views to special catalog
    public static boolean addViews(Collection<TdView> views, Catalog catalog) {
        return catalog.getOwnedElement().addAll(views);
    }

    public static List<TdView> getViews(Catalog catalog) {
        return ViewHelper.getViews(catalog.getOwnedElement());
    }
}
