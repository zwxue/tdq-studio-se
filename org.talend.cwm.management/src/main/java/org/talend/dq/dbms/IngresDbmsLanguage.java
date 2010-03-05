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
package org.talend.dq.dbms;

import org.talend.utils.ProductVersion;


/**
 * DOC xqliu  class global comment. Detailled comment
 */
public class IngresDbmsLanguage extends DbmsLanguage {

    /**
     * DOC xqliu IngresDbmsLanguage constructor comment.
     */
    IngresDbmsLanguage() {
        super(DbmsLanguage.INGRES);
    }

    /**
     * DOC xqliu IngresDbmsLanguage constructor comment.
     * 
     * @param dbmsType
     * @param dbVersion
     */
    IngresDbmsLanguage(String dbmsType, ProductVersion dbVersion) {
        super(dbmsType, dbVersion);
    }

    public String toQualifiedName(String catalog, String schema, String table) {
        return super.toQualifiedName(null, null, table);
    }
}
