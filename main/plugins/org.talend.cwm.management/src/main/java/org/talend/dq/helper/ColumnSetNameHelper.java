// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dq.helper;

import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.cwm.db.connection.ConnectionUtils;
import org.talend.cwm.helper.CatalogHelper;
import org.talend.cwm.helper.ColumnSetHelper;
import org.talend.dq.dbms.DbmsLanguage;
import org.talend.dq.dbms.DbmsLanguageFactory;
import orgomg.cwm.objectmodel.core.Package;
import orgomg.cwm.resource.relational.Catalog;
import orgomg.cwm.resource.relational.ColumnSet;
import orgomg.cwm.resource.relational.RelationalPackage;

/**
 * DOC Zqin class global comment. Detailled comment
 */
public final class ColumnSetNameHelper {

    private ColumnSetNameHelper() {

    }

    /**
     * DOC zqin Comment method "getQualifiedName".
     * 
     * @param tdDataProvider
     * @return
     */
    public static String getColumnSetQualifiedName(Connection tdDataProvider, ColumnSet columnset) {
        DbmsLanguage dbmsLanguage = DbmsLanguageFactory.createDbmsLanguage(tdDataProvider);
        Package catalogOrSchema = ColumnSetHelper.getParentCatalogOrSchema(columnset);
        if (catalogOrSchema == null) {
            return columnset.getName();
        }
        // else
        String catalogName = null;
        String schemaName = null;
        if (catalogOrSchema != null && RelationalPackage.eINSTANCE.getSchema().equals(catalogOrSchema.eClass())) {
            schemaName = catalogOrSchema.getName();
            Catalog parentCatalog = CatalogHelper.getParentCatalog(catalogOrSchema);
            if (parentCatalog != null) {
                catalogName = parentCatalog.getName();
            }
        } else {
            catalogName = catalogOrSchema.getName();

        }

        // MOD by zshen: change schemaName of sybase database to Table's owner.
        if (ConnectionUtils.isSybaseeDBProducts(dbmsLanguage.getDbmsName())) {
            schemaName = ColumnSetHelper.getTableOwner(columnset);
        }
        // ~11934

        return dbmsLanguage.toQualifiedName(catalogName, schemaName, columnset.getName());
    }
}
