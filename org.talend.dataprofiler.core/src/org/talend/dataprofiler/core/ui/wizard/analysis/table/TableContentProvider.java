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
package org.talend.dataprofiler.core.ui.wizard.analysis.table;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.runtime.CoreException;
import org.talend.cwm.helper.CatalogHelper;
import org.talend.cwm.helper.SchemaHelper;
import org.talend.cwm.relational.TdTable;
import org.talend.dataprofiler.core.model.nodes.foldernode.NamedColumnSetFolderNode;
import org.talend.dataprofiler.core.ui.utils.ComparatorsFactory;
import org.talend.dataprofiler.core.ui.views.provider.DQRepositoryViewContentProvider;
import org.talend.dq.helper.ProxyRepositoryViewObject;
import org.talend.resource.ResourceManager;
import orgomg.cwm.resource.relational.ColumnSet;
import orgomg.cwm.resource.relational.NamedColumnSet;

/**
 * DOC xqliu class global comment. Detailled comment
 */
public class TableContentProvider extends DQRepositoryViewContentProvider {

    private static Logger log = Logger.getLogger(TableContentProvider.class);

    public TableContentProvider() {
        super();
    }

    @Override
    public Object[] getChildren(Object parentElement) {
        if (parentElement instanceof IContainer) {
            IContainer container = ((IContainer) parentElement);
            Object[] members = null;
            try {
                members = container.members();
            } catch (CoreException e) {
                log.error("Can't get the children of container:" + container.getLocation());
            }
            if (container.equals(ResourceManager.getConnectionFolder())) {

                members = ProxyRepositoryViewObject.fetchAllDBRepositoryViewObjects(Boolean.FALSE, Boolean.TRUE).toArray();
                ComparatorsFactory.sort(members, ComparatorsFactory.IREPOSITORYVIEWOBJECT_COMPARATOR_ID);
            }
            return members;
        } else if (parentElement instanceof NamedColumnSet) {
            return null;
        } else if (parentElement instanceof NamedColumnSetFolderNode) {
            NamedColumnSetFolderNode folderNode = (NamedColumnSetFolderNode) parentElement;
            // if (folderNode instanceof ViewFolderNode) {
            // return null;
            // }
            folderNode.loadChildren();
            Object[] children = folderNode.getChildren();
            if (children != null && children.length > 0) {
                if (!(children[0] instanceof ColumnSet)) {
                    return children;
                }
            }
            return ComparatorsFactory.sort(children, ComparatorsFactory.MODELELEMENT_COMPARATOR_ID);
        }
        return super.getChildren(parentElement);
    }

    @Override
    public Object[] getElements(Object object) {
        return this.getChildren(object);
    }

    @Override
    public Object getParent(Object element) {
        if (element instanceof IContainer) {
            return ((IContainer) element).getParent();
        }
        return super.getParent(element);
    }

    @Override
    public boolean hasChildren(Object element) {
        return !(element instanceof TdTable);
    }

    /**
     * This class will combine catlogName and columnSetName as a key.
     */
    class CatalogSchemaKey {

        private final String catalogName;

        private final String schemaName;

        public CatalogSchemaKey(ColumnSet columnSet) {
            this.schemaName = SchemaHelper.getParentSchema(columnSet) == null ? "" //$NON-NLS-1$
                    : SchemaHelper.getParentSchema(columnSet).getName();
            this.catalogName = CatalogHelper.getParentCatalog(columnSet) == null ? "" //$NON-NLS-1$
                    : CatalogHelper.getParentCatalog(columnSet).getName();
        }

        @Override
        public int hashCode() {
            final int prime = 22;
            int result = 1;
            result = prime * result + ((schemaName == null) ? 0 : schemaName.hashCode());
            result = prime * result + ((catalogName == null) ? 0 : catalogName.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final CatalogSchemaKey other = (CatalogSchemaKey) obj;
            if (catalogName == null) {
                if (other.catalogName != null) {
                    return false;
                }
            } else if (!catalogName.equals(other.catalogName)) {
                return false;
            }
            if (schemaName == null) {
                if (other.schemaName != null) {
                    return false;
                }
            } else if (!schemaName.equals(other.schemaName)) {
                return false;
            }
            return true;
        }
    }
}
