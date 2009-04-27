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
import org.talend.cwm.relational.TdSchema;
import org.talend.cwm.relational.TdTable;
import org.talend.cwm.relational.TdView;
import orgomg.cwm.objectmodel.core.ModelElement;
import orgomg.cwm.objectmodel.core.Namespace;
import orgomg.cwm.objectmodel.core.TaggedValue;
import orgomg.cwm.resource.relational.Schema;

/**
 * @author scorreia
 * 
 * Utility for handling Schema.
 */
public final class SchemaHelper {

    private SchemaHelper() {
    }

    public static List<TdSchema> getSchemas(Collection<? extends EObject> elements) {
        List<TdSchema> schemas = new ArrayList<TdSchema>();
        for (EObject modelElement : elements) {
            TdSchema schema = SwitchHelpers.SCHEMA_SWITCH.doSwitch(modelElement);
            if (schema != null) {
                schemas.add(schema);
            }
        }
        return schemas;
    }

    public static List<TdTable> getTables(Schema schema) {
	    //MOD xqliu 2009-04-27 bug 6507
        TaggedValue tv = TaggedValueHelper.getTaggedValue(TaggedValueHelper.TABLE_FILTER, schema.getTaggedValue());
        String tableFilter = tv == null ? null : tv.getValue();
        return TableHelper.getTables(schema.getOwnedElement(), tableFilter);
        // ~
    }

    public static List<TdView> getViews(Schema schema) {
    	//MOD xqliu 2009-04-27 bug 6507
        TaggedValue tv = TaggedValueHelper.getTaggedValue(TaggedValueHelper.VIEW_FILTER, schema.getTaggedValue());
        String viewFilter = tv == null ? null : tv.getValue();
        return ViewHelper.getViews(schema.getOwnedElement(), viewFilter);
        // ~
    }

    public static boolean addTables(Collection<TdTable> tables, Schema schema) {
        return schema.getOwnedElement().addAll(tables);
    }

    public static boolean addViews(Collection<TdView> views, Schema schema) {
        return schema.getOwnedElement().addAll(views);
    }

    /**
     * Method "getParentSchema" returns a schema if the element is directly owned by a schema.
     * 
     * @param element (can be null)
     * @return the Catalog or null
     */
    public static TdSchema getParentSchema(ModelElement element) {
        if (element == null) {
            return null;
        }
        final Namespace namespace = element.getNamespace();
        return (namespace != null) ? SwitchHelpers.SCHEMA_SWITCH.doSwitch(namespace) : null;
    }

}
