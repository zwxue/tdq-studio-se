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
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.properties.ConnectionItem;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.Property;
import org.talend.dq.CWMPlugin;
import org.talend.dq.helper.PropertyHelper;

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
        IFile dulicatedFile = super.duplicate();

        if (dulicatedFile != null) {
            Property duplicatedProperty = PropertyHelper.getProperty(dulicatedFile);
            if (duplicatedProperty != null) {
                Item item = duplicatedProperty.getItem();
                if (item instanceof ConnectionItem) {
                    Connection connection = ((ConnectionItem) item).getConnection();
                    CWMPlugin.getDefault().addConnetionAliasToSQLPlugin(connection);
                }
            }
        }

        return dulicatedFile;
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
