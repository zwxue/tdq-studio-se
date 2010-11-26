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
package org.talend.dataprofiler.core.ui.action.actions.handle;

import net.sourceforge.sqlexplorer.plugin.SQLExplorerPlugin;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.talend.core.model.properties.ConnectionItem;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.Property;
import org.talend.dq.helper.PropertyHelper;
import org.talend.repository.model.ProxyRepositoryFactory;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public class ConnectionHandle extends RepositoryViewObjectHandle {

    private static Logger log = Logger.getLogger(ConnectionHandle.class);

    /**
     * DOC bZhou ConnectionHandle constructor comment.
     * 
     * @param property
     */
    ConnectionHandle(Property property) {
        super(property);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.action.actions.handle.IDuplicateHandle#duplicate()
     */
    public IFile duplicate() {
        Property property = getProperty();
        if (property != null) {
            try {

                IPath itemStatePath = PropertyHelper.getItemStatePath(property);

                Item copyItem = ProxyRepositoryFactory.getInstance().copy(property.getItem(), itemStatePath);

                ((ConnectionItem) copyItem).getConnection().setName(copyItem.getProperty().getLabel());

                ProxyRepositoryFactory.getInstance().save(copyItem);

            } catch (Exception e) {
                if (log.isDebugEnabled()) {
                    log.debug(e, e);
                }
            }
        }
        return null;
    }

    /*
     * Add yyi 2010-09-15 14549: hide connections in SQL Explorer when a connection is moved to the trash bin
     * 
     * @see org.talend.dataprofiler.core.ui.action.actions.handle.RepositoryViewObjectHandle#delete()
     */
    @Override
    public boolean delete() throws Exception {
        boolean b = super.delete();
        SQLExplorerPlugin.getDefault().getAliasManager().modelChanged();
        return b;

    }
}
