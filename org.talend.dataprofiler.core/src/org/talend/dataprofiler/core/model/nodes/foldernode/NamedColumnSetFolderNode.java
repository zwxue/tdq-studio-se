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

import java.util.List;

import org.apache.log4j.Logger;
import org.talend.cwm.helper.DataProviderHelper;
import org.talend.cwm.relational.TdCatalog;
import org.talend.cwm.relational.TdSchema;
import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.dataprofiler.core.helper.NeedSaveDataProviderHelper;
import org.talend.dq.nodes.foldernode.AbstractDatabaseFolderNode;
import orgomg.cwm.resource.relational.NamedColumnSet;

/**
 * @author scorreia
 * @param <COLSET> either TdTable or TdView
 */
public abstract class NamedColumnSetFolderNode<COLSET extends NamedColumnSet> extends AbstractDatabaseFolderNode {

    private static Logger log = Logger.getLogger(NamedColumnSetFolderNode.class);

    /**
     * @param name
     */
    public NamedColumnSetFolderNode(String name) {
        super(name);
    }

    protected <T extends List<COLSET>> void loadChildrenLow(orgomg.cwm.objectmodel.core.Package pack, TdCatalog catalog,
            TdSchema schema, final T columnSets) {
        assert pack != null;
        columnSets.addAll(getColumnSets(catalog, schema));
        if (columnSets.size() > 0) {
            this.setChildren(columnSets.toArray());
            return;
        }

        TdDataProvider provider = DataProviderHelper.getTdDataProvider(pack);
        if (provider == null) {
            log.warn(pack.getName());
            return;
        }
        // load from database
        loadColumnSets(catalog, schema, provider, columnSets);
        // store views in catalog or schema
        pack.getOwnedElement().addAll(columnSets);
        this.setChildren(columnSets.toArray());
        NeedSaveDataProviderHelper.register(provider.eResource().getURI().path(), provider);
    }

    /**
     * @param catalog
     * @param schema
     * @return the Tables or Views in the given catalog or schema.
     */
    protected abstract List<COLSET> getColumnSets(TdCatalog catalog, TdSchema schema);

    /**
     * Loads columnsets (table or view) from database.
     * 
     * @param <T>
     * @param catalog
     * @param schema
     * @param provider
     * @param columnSets
     * @return
     */
    protected abstract <T extends List<COLSET>> boolean loadColumnSets(TdCatalog catalog, TdSchema schema,
            TdDataProvider provider, final T columnSets);
}
