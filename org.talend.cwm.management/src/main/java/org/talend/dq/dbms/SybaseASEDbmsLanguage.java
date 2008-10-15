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
package org.talend.dq.dbms;

/**
 * DOC scorreia class global comment. Detailled comment
 */
public class SybaseASEDbmsLanguage extends DbmsLanguage {

    /**
     * DOC scorreia SybaseASEDbmsLanguage constructor comment.
     */
    public SybaseASEDbmsLanguage() {
        super(DbmsLanguage.SYBASE_ASE);
    }

    /**
     * DOC scorreia SybaseASEDbmsLanguage constructor comment.
     * 
     * @param dbmsType
     * @param majorVersion
     * @param minorVersion
     */
    public SybaseASEDbmsLanguage(String dbmsType, int majorVersion, int minorVersion) {
        super(dbmsType, majorVersion, minorVersion);
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
        schema = quote("dbo");
        // Bug fixed: 5118. ZQL parser does not understand statement with full qualified name
        // catalog = "dbo";
        return super.toQualifiedName(catalog, schema, table);
    }

}
