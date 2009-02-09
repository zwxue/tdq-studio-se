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
package org.talend.dq.helper;

import org.talend.cwm.helper.CatalogHelper;
import org.talend.cwm.helper.ColumnSetHelper;
import org.talend.cwm.relational.RelationalPackage;
import org.talend.cwm.relational.TdCatalog;
import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.dq.dbms.DbmsLanguage;
import org.talend.dq.dbms.DbmsLanguageFactory;
import orgomg.cwm.objectmodel.core.Package;
import orgomg.cwm.resource.relational.ColumnSet;

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
    public static String getColumnSetQualifiedName(TdDataProvider tdDataProvider, ColumnSet columnset) {
        DbmsLanguage dbmsLanguage = DbmsLanguageFactory.createDbmsLanguage(tdDataProvider);
        Package catalogOrSchema = ColumnSetHelper.getParentCatalogOrSchema(columnset);
        if (catalogOrSchema == null) {
            return columnset.getName();
        }
        // else
        String catalogName = null;
        String schemaName = null;
        if (catalogOrSchema != null && RelationalPackage.eINSTANCE.getTdSchema().equals(catalogOrSchema.eClass())) {
            schemaName = catalogOrSchema.getName();
            TdCatalog parentCatalog = CatalogHelper.getParentCatalog(catalogOrSchema);
            if (parentCatalog != null) {
                catalogName = parentCatalog.getName();
            }
        } else {
            catalogName = catalogOrSchema.getName();
        }

        return dbmsLanguage.toQualifiedName(catalogName, schemaName, columnset.getName());
    }
}
