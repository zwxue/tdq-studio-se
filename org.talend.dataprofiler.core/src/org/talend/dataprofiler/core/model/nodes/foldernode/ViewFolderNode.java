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
package org.talend.dataprofiler.core.model.nodes.foldernode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.talend.cwm.exception.TalendException;
import org.talend.cwm.helper.CatalogHelper;
import org.talend.cwm.helper.SchemaHelper;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.cwm.helper.TaggedValueHelper;
import org.talend.cwm.management.api.DqRepositoryViewService;
import org.talend.cwm.relational.TdCatalog;
import org.talend.cwm.relational.TdSchema;
import org.talend.cwm.relational.TdView;
import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;

/**
 * @author rli
 * 
 */
public class ViewFolderNode extends NamedColumnSetFolderNode<TdView> {

    protected static Logger log = Logger.getLogger(ViewFolderNode.class);

    /**
     * @param name
     */
    public ViewFolderNode() {
        super(DefaultMessagesImpl.getString("ViewFolderNode.views")); //$NON-NLS-1$
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.model.nodes.AbstractFolderNode#loadChildren()
     */
    @Override
    public void loadChildren() {
        // MODSCA 2008-03-14 load children also when no catalog is given, but a schema exists (e.g. for DB2 database)
        TdCatalog catalog = SwitchHelpers.CATALOG_SWITCH.doSwitch(this.getParent());
        if (catalog != null) {
            loadChildrenLow(catalog, catalog, null, new ArrayList<TdView>());
        } else {
            TdSchema schema = SwitchHelpers.SCHEMA_SWITCH.doSwitch(this.getParent());
            if (schema != null) {
                loadChildrenLow(schema, null, schema, new ArrayList<TdView>());
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
    protected List<TdView> getColumnSets(TdCatalog catalog, TdSchema schema) {
        if (catalog != null) {
            return CatalogHelper.getViews(catalog);
        }
        if (schema != null) {
            return SchemaHelper.getViews(schema);
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
    protected <T extends List<TdView>> boolean loadColumnSets(TdCatalog catalog, TdSchema schema, TdDataProvider provider,
            final T columnSets) {
        try {
            boolean ok = false;
            assert provider != null : DefaultMessagesImpl.getString("ViewFolderNode.noProviderForViews"); //$NON-NLS-1$
            assert catalog != null ^ schema != null : DefaultMessagesImpl.getString("ViewFolderNode.catalogOrSchemaExist",//$NON-NLS-1$
                    provider.getName());

            if (catalog != null) {
                ok = columnSets.addAll(DqRepositoryViewService.getViews(provider, catalog, null, true));
            }
            if (schema != null) {
                ok = columnSets.addAll(DqRepositoryViewService.getViews(provider, schema, null, true));
            }
            return ok;
        } catch (TalendException e) {
            log.error(e, e);
            return false;
        }
    }

    public int getFolderNodeType() {
        return VIEWFOLDER_NODE_TYPE;
    }

    @Override
    protected List<TdView> getColumnSetsWithFilter(TdCatalog catalog, TdSchema schema) {
        if (catalog != null) {
            String viewFilter = TaggedValueHelper.getValue(TaggedValueHelper.VIEW_FILTER, catalog.getTaggedValue());
            return filterColumnSets(CatalogHelper.getViews(catalog), viewFilter);
        }
        if (schema != null) {
            String viewFilter = TaggedValueHelper.getValue(TaggedValueHelper.VIEW_FILTER, schema.getTaggedValue());
            return filterColumnSets(SchemaHelper.getViews(schema), viewFilter);
        }
        return Collections.emptyList();
    }

}
