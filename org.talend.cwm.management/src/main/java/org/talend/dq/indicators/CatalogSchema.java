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
package org.talend.dq.indicators;

/**
 * DOC scorreia SchemaEvaluator class global comment. Detailled comment
 * 
 * @deprecated do not use
 */
public class CatalogSchema {

    String catalog;

    String schema;

    /**
     * CatalogSchema constructor.
     * 
     * @param cat
     * @param schem
     */
    public CatalogSchema(String cat, String schem) {
        this.catalog = cat;
        this.schema = schem;
    }

    public String getCatalog() {
        return this.catalog;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    public String getSchema() {
        return this.schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }
}
