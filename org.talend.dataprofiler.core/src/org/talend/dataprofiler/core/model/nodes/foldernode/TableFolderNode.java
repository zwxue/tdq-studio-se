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
package org.talend.dataprofiler.core.model.nodes.foldernode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.talend.cwm.exception.TalendException;
import org.talend.cwm.helper.CatalogHelper;
import org.talend.cwm.helper.SchemaHelper;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.cwm.management.api.DqRepositoryViewService;
import org.talend.cwm.relational.TdCatalog;
import org.talend.cwm.relational.TdSchema;
import org.talend.cwm.relational.TdTable;
import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.dataprofiler.core.exception.MessageBoxExceptionHandler;

/**
 * @author rli
 * 
 */
public class TableFolderNode extends NamedColumnSetFolderNode<TdTable> {

    /**
     * 
     */
    public TableFolderNode() {
        super("Tables");
    }

    @Override
    public void loadChildren() {
        // MODSCA 2008-03-14 load children also when no catalog is given, but a schema exists (e.g. for DB2 database)
        TdCatalog catalog = SwitchHelpers.CATALOG_SWITCH.doSwitch(this.getParent());
        if (catalog != null) {
            loadChildrenLow(catalog, catalog, null, new ArrayList<TdTable>());
        } else {
            TdSchema schema = SwitchHelpers.SCHEMA_SWITCH.doSwitch(this.getParent());
            if (schema != null) {
                loadChildrenLow(schema, null, schema, new ArrayList<TdTable>());
            }
        }
        super.loadChildren();

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.dataprofiler.core.model.nodes.AbstractFolderNode#getColumnSets(org.talend.cwm.relational.TdCatalog,
     * org.talend.cwm.relational.TdSchema)
     */
    @Override
    protected List<TdTable> getColumnSets(TdCatalog catalog, TdSchema schema) {
        if (catalog != null) {
            return CatalogHelper.getTables(catalog);
        }
        if (schema != null) {
            return SchemaHelper.getTables(schema);
        }
        return Collections.emptyList();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.dataprofiler.core.model.nodes.AbstractFolderNode#loadColumnSets(org.talend.cwm.relational.TdCatalog,
     * org.talend.cwm.relational.TdSchema, org.talend.cwm.softwaredeployment.TdDataProvider, java.util.List)
     */
    @Override
    protected <T extends List<TdTable>> boolean loadColumnSets(TdCatalog catalog, TdSchema schema, TdDataProvider provider,
            final T columnSets) {
        try {
            boolean ok = false;
            assert provider != null : "no provider given for getting views";
            assert catalog != null ^ schema != null : "either catalog or schema must exist but not both. Provider= "
                    + provider.getName();

            if (catalog != null) {
                ok = columnSets.addAll(DqRepositoryViewService.getTables(provider, catalog, null, true));
            }
            if (schema != null) {
                ok = columnSets.addAll(DqRepositoryViewService.getTables(provider, schema, null, true));
            }
            return ok;
        } catch (TalendException e) {
            MessageBoxExceptionHandler.process(e);
            return false;
        }
    }

}
