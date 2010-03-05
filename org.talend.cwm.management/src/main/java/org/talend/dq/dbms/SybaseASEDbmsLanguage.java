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
 * DOC scorreia class global comment. Detailled comment
 */
public class SybaseASEDbmsLanguage extends DbmsLanguage {

    /**
     * DOC scorreia SybaseASEDbmsLanguage constructor comment.
     */
    SybaseASEDbmsLanguage() {
        super(DbmsLanguage.SYBASE_ASE);
    }

    /**
     * DOC scorreia SybaseASEDbmsLanguage constructor comment.
     * 
     * @param dbmsType
     * @param majorVersion
     * @param minorVersion
     */
    SybaseASEDbmsLanguage(String dbmsType, ProductVersion dbVersion) {
        super(dbmsType, dbVersion);
        // TODO Auto-generated constructor stub
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.cwm.management.api.DbmsLanguage#toQualifiedName(java.lang.String, java.lang.String,
     * java.lang.String)
     */
    @Override
    public String toQualifiedName(String catalog, String schema, String table) {
        if (schema == null) { // use default (backward compatibility with TOP 1.1.0
            schema = quote("dbo"); //$NON-NLS-1$
        }
        return super.toQualifiedName(catalog, schema, table);
    }

}
